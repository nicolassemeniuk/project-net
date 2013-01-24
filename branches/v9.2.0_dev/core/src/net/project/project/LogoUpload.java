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

 package net.project.project;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.document.DocumentManagerBean;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.FileUtils;

import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Created by IntelliJ IDEA.
 * User: Matthew Flower
 * Date: Dec 22, 2005
 * Time: 1:40:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogoUpload extends SimpleFormController {
    private void verifySecurity() {
        AccessVerifier.verifyAccess(Module.PROJECT_SPACE, Action.MODIFY);
    }

    protected ModelAndView onSubmit(HttpServletRequest request,
                                    HttpServletResponse response, Object command,
                                    BindException bindException) throws Exception {

        verifySecurity();
        LogoUploadCommand luCommand = (LogoUploadCommand)command;

        // Upload the logo  -- Old Function calling
        //uploadLogo(luCommand.getFile(), request.getSession());

        if (luCommand.getFile() == null || luCommand.getFile().isEmpty()) {
        	final Map<String,String> model = new HashMap<String,String>();
            model.put("error.nofile", "nofile");

            return new ModelAndView("/tiles_logo_upload_validation", model);
        }
        
//bfd-2945 Start
//Project information not maintained when image is uploaded after changes are made
        String newLogoId = uploadLogoImage(luCommand.getFile(), request.getSession());
//bfd-2945 End....
        
        
        // Determine the page to reload after completing checkin
        String forwardingPage = SessionManager.getJSPRootURL() +
            "/project/PropertiesEdit.jsp?module=" + Module.PROJECT_SPACE +
            "&action=" + Action.MODIFY;

        Map model = new HashMap();
        model.put("reloadURL", forwardingPage);
        model.put("module", String.valueOf(Module.PROJECT_SPACE));
        model.put("action", String.valueOf(Action.MODIFY));
        model.put("newLogoId", newLogoId);

        // Forward to CloseWindow page
        //return new ModelAndView("/document/CloseWindow.jsp, model);

        //New Code for returning ImageId
        // Forward to CloseWindow page
        return new ModelAndView("/tiles_logo_upload", model);

    }

    /**
     * Uploads a logo, storing in the document repository.
     * Then adds it to the specified project.
     * Assumes there is a parameter called <code>File</code> that represents
     * an uploaded file.
     * @throws net.project.base.PnetException if no File parameter can be found in the specified
     * parameters
     * // Use  uploadLogoImage
     * @deprecated 
     */
    private void uploadLogo(MultipartFile file, HttpSession session)
        throws PnetException {

        DocumentManagerBean docManager = (DocumentManagerBean) session.getAttribute("docManager");
        ProjectSpaceBean projectSpace = (ProjectSpaceBean) session.getAttribute("projectSpace");
        User user = (User) session.getAttribute("user");

        docManager.setUser(user);
        String tempFilePath = FileUtils.commitUploadedFileToFileSystem(file);
        String newLogoID = docManager.addFileToSpace(file.getSize(),
            file.getOriginalFilename(), tempFilePath, file.getContentType(),
            user.getCurrentSpace().getID());
        projectSpace.addLogoToProject(newLogoID);

    }

//  bfd-2945  Start
//  Project information not maintained when image is uploaded after changes are made
    
    /**
     * Uploads a logo, storing in the document repository.
     * Then adds it to the specified project.
     * Assumes there is a parameter called <code>File</code> that represents
     * an uploaded file.
     * @throws net.project.base.PnetException if no File parameter can be found in the specified
     * parameters
     */
    private String uploadLogoImage(MultipartFile file, HttpSession session)
        throws PnetException {

        DocumentManagerBean docManager = (DocumentManagerBean) session.getAttribute("docManager");
        ProjectSpaceBean projectSpace = (ProjectSpaceBean) session.getAttribute("projectSpace");
        User user = (User) session.getAttribute("user");

        docManager.setUser(user);
        String tempFilePath = FileUtils.commitUploadedFileToFileSystem(file);
        String newLogoID = docManager.addFileToSpace(file.getSize(),
            file.getOriginalFilename(), tempFilePath, file.getContentType(),
            user.getCurrentSpace().getID());
        projectSpace.addLogoToProject(newLogoID);
        return newLogoID;
    }
//  bfd-2945 End....
    
}
