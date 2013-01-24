/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.hibernate.service.impl;



import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.swing.ImageIcon;

import net.project.base.Module;
import net.project.document.DocumentManagerBean;
import net.project.hibernate.dao.IPnPersonProfileDAO;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnPersonProfile;
import net.project.hibernate.service.IPnPersonProfileService;
import net.project.hibernate.service.IPnPersonService;
import net.project.security.User;
import net.project.util.FileUtils;

import org.apache.log4j.Logger;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

@Service(value="pnPersonProfileService")
public class PnPersonProfileServiceImpl implements IPnPersonProfileService {
	
	private static Logger log = Logger.getLogger(PnPersonProfileServiceImpl.class);
	
	@Autowired
	private IPnPersonProfileDAO pnPersonProfileDAO; 

	@Autowired
	private IPnPersonService personService;
	
	public void setPersonService(IPnPersonService personService) {
		this.personService = personService;
	}

	public void setPnPersonProfileDAO(IPnPersonProfileDAO pnPersonProfileDAO) {
		this.pnPersonProfileDAO = pnPersonProfileDAO;
	}

	public PnPersonProfile getPersonProfile(Integer personProfileId) {
		return pnPersonProfileDAO.findByPimaryKey(personProfileId);
	}

	public Integer savePersonProfile(PnPersonProfile pnPersonProfile) {
		return pnPersonProfileDAO.create(pnPersonProfile);
	}

	public void deletePersonProfile(PnPersonProfile pnPersonProfile) {
		pnPersonProfileDAO.delete(pnPersonProfile);
	}

	public void updatePersonProfile(PnPersonProfile pnPersonProfile) {
		pnPersonProfileDAO.update(pnPersonProfile);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonProfileService#uploadImage(org.apache.tapestry.upload.services.UploadedFile, net.project.security.User, javax.servlet.http.HttpSession)
	 */
	public String uploadDocument(UploadedFile file, User user, HttpSession session , int module) {
		String imageId = null;
		String tempFilePath;
		try {
			DocumentManagerBean docManager = (DocumentManagerBean) session.getAttribute("docManager");
			String contID = docManager.getCurrentContainerID();
			docManager.setUser(user);
						
			File imageFile = File.createTempFile("tmp", FileUtils.getFileExt(file.getFileName()));
            file.write(imageFile);
            
            if(module == Module.DIRECTORY){
            	File yetAnOtherFile = File.createTempFile("tmp", "Scaled"+file.getFileName().replace(" ", "_"));

                // Changing the size of the uploaded image file
                yetAnOtherFile = createResizedImage(imageFile, yetAnOtherFile);
    	        			        
    	        tempFilePath = FileUtils.commitUploadedFileToFileSystem(yetAnOtherFile);
            } else {
            	tempFilePath = FileUtils.commitUploadedFileToFileSystem(imageFile);
            }
                   
			imageId = docManager.addFileToSpace(file.getSize(), file.getFileName(), tempFilePath, file.getContentType(), user.getCurrentSpace().getID(), module);
			PnPerson person = personService.getPerson(Integer.valueOf(user.getID()));
			person.setImageId(Integer.valueOf(imageId));
			personService.updatePerson(person);			
		} catch (Exception e) {
			log.error("Error occured while uploading the person image : "+e.getMessage());
		}		
		return imageId;
    }
	
	/**
	 * Creating scaled image with specified width and height
	 * @param original image file to scale with specified width, height
	 * @param resized image file after scaling
	 * @param calcWidth specified width
	 * @param calcHeight specified height
	 * @return resized file instance
	 */
	public File createResizedImage(File original, File resized){
		Image rawImage = new ImageIcon(original.getPath()).getImage();
		
		int imageWidth = rawImage.getWidth(null);
		int imageHeight = rawImage.getHeight(null);
	            	
		int calcWidth = 110;
        int calcHeight = 140;        
        
        if (imageWidth >= 110) {
        	float widthFactor = (float) calcWidth / (float) imageWidth;
        	calcHeight = (int) (imageHeight * widthFactor);
        }
        if (imageWidth < 110) {
        	float heightFactor = (float) calcHeight / (float) imageHeight;
        	calcWidth = (int) (imageWidth * heightFactor);
        }

		BufferedImage thumbImage = new BufferedImage(calcWidth, calcHeight, BufferedImage.SCALE_SMOOTH);

		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(rawImage, 0, 0, calcWidth, calcHeight, null);
		graphics2D.dispose();

        FileOutputStream imageOut = null;
		try {
			imageOut = new FileOutputStream(resized);
		} catch (FileNotFoundException pnetEx) {
			log.error("Specified file not found : "+ pnetEx.getMessage());
		}
        JPEGImageEncoder  encoder = JPEGCodec.createJPEGEncoder(imageOut);
        JPEGEncodeParam  enparam = encoder.getDefaultJPEGEncodeParam(thumbImage);
	    int quality = 100;
	    enparam.setXDensity(100);
	    enparam.setYDensity(100);

        enparam.setQuality( (float) quality / 100.0f, false);
        encoder.setJPEGEncodeParam(enparam);
        try {
			encoder.encode(thumbImage);
		} catch (ImageFormatException pnetEx) {
			log.error("Error occured while encoding image : "+pnetEx.getMessage());
		}
		// Unreachable catch
		/*
		catch (IOException pnetEx) {
			log.error("Error occured while encoding image : "+pnetEx.getMessage());
		}
		*/
		return resized;
	}
	
}
