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

package net.project.document;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.persistence.PersistenceException;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.util.FileUtils;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Provides handling for Document Uploads using the Spring Framework.  Places an
 * uploaded document in the vault.
 *
 * @author Matthew Flower
 * @since 22 December 2005
 */
public class DocumentUpload extends SimpleFormController {
	
    private void verifySecurity(DocumentManagerBean docManager, SecurityProvider securityProvider) throws PersistenceException {
        //Security can either be create or modify, allow either of these to be expected
        int action = securityProvider.getCheckedActionID() == Action.MODIFY ?
            Action.MODIFY :
            Action.CREATE;

        String containerID = docManager.getCurrentContainerID();
        AccessVerifier.verifyAccess(
            docManager.getModuleFromContainerID(containerID),
            action,
            containerID,
            securityProvider
        );
    }

    /**
     * Handles the parameters posted to this servlet.
     * Creates a Document or Bookmark depending on the "documentType" parameter.
     */
    protected ModelAndView onSubmit(HttpServletRequest request,
        HttpServletResponse response, Object command,
        BindException bindException) throws Exception {
    	
        try {
            DocumentUploadCommand duCommand = (DocumentUploadCommand)command;
            MultipartFile uploadedZipFile = duCommand.getFile();
            String temporaryZipFileLocation = null;
            
            if(verifyFileExists(uploadedZipFile)){
            	temporaryZipFileLocation = FileUtils.commitUploadedFileToFileSystem(uploadedZipFile);
            	logger.debug("Temporary upload zip file written to " + temporaryZipFileLocation);
            	request.getSession().removeAttribute("invalidFile");
            } else {
            	if (request.getParameter("radioSelected").equals("document")) {
	            	Map model = new HashMap();
	    			model.put("module", String.valueOf(Module.DOCUMENT));
	    			model.put("action", String.valueOf(Action.CREATE));
	    			model.put("invalidFile", "true");
	    			model.put("duCommand", duCommand);
	    			return new ModelAndView("/tiles_document_upload", model);
            	}
            }
			
            DocumentManagerBean docManager = (DocumentManagerBean) request.getSession().getAttribute("docManager");
            SecurityProvider securityProvider = (SecurityProvider) request.getSession().getAttribute(SecurityProvider.SECURITY_PROVIDER_SESSION_OBJECT_NAME);
            verifySecurity(docManager, securityProvider);

            String tempDir = System.getProperty("java.io.tmpdir");
            String contID = docManager.getCurrentContainerID();
            String contIDbeforeUpload = contID;
            String zipval = request.getParameter("zipexpand")+"val";
            boolean zipexpand = zipval.equals("nullval")? false: true; 
            String tempLoc = null;
            String tempFilename = null;
        	Enumeration entries;
        	ZipFile zipFile = null;

            if (zipexpand) {
            	zipFile = new ZipFile(temporaryZipFileLocation);
            	entries = zipFile.entries();

            	while(entries.hasMoreElements()) {
            		docManager.setCurrentContainer(contIDbeforeUpload);
            		contID = contIDbeforeUpload;

            		ZipEntry entry = (ZipEntry)entries.nextElement();

            		if(entry.isDirectory()) {
            			// Assume directories are stored parents first then children.
            			String [] str1 = entry.getName().split("/");
            			if (str1.length > 1) {
            				for (int i = 0; i < str1.length; i++) {
            					if (i <= str1.length - 1) {
            						ContainerBean cb = new ContainerBean();
            						if (!cb.getContainerInfo(contID, str1[i])) {
            							cb.setContainerID ( docManager.getCurrentContainerID() );
            							cb.setUser ( docManager.getUser() );
            							cb.setDescription("");
            							cb.setName(str1[i]);
            							//cb.setContainerID(cb.create());
            							contID = cb.create();
            							docManager.setCurrentContainer(contID);
            						}else {
            							contID = cb.getContainerID(contID, str1[i]);
            							docManager.setCurrentContainer(contID);
            						}
            					}
            				}
            			}
            			continue;
            		} else {
            			String [] str = entry.getName().split("/");
            			if (str.length > 1) {
            				for (int i = 0; i < str.length; i++) {
            					if (i != str.length - 1) {
            						ContainerBean cb = new ContainerBean();
            						if (!cb.getContainerInfo(contID, str[i])) {
            							cb.setContainerID ( docManager.getCurrentContainerID() );
            							cb.setUser ( docManager.getUser() );
            							cb.setDescription("");
            							cb.setName(str[i]);
            							contID = cb.create();
            							docManager.setCurrentContainer(contID);
            						}else {
            							contID = cb.getContainerID(contID, str[i]);
            							docManager.setCurrentContainer(contID);
            							cb.setContainerID (contID);
            						}
            					}
            				}
            			}
            			tempLoc = tempDir + File.separator + str[str.length - 1];
            			tempFilename = str[str.length - 1];
            			copyInputStream(zipFile.getInputStream(entry),
            					new BufferedOutputStream(new FileOutputStream(tempLoc)));
            		}
		        
            		// Determine type of object to create
            		if (duCommand.getDocumentType() != null &&
            				duCommand.getDocumentType().equals(ContainerObjectType.DOCUMENT_OBJECT_TYPE)) {

            			// Handle document upload
            			createDocument(duCommand, docManager, tempFilename, tempLoc, zipexpand, temporaryZipFileLocation);
		        
            		} else if (duCommand.getDocumentType() != null &&
            				duCommand.getDocumentType().equals(ContainerObjectType.BOOKMARK_OBJECT_TYPE)) {


            			// Handle bookmark creation
            			createBookmark(duCommand, docManager);

            		}
		        }
		      } else {
          		// Determine type of object to create
          		if (duCommand.getDocumentType() != null &&
          				duCommand.getDocumentType().equals(ContainerObjectType.DOCUMENT_OBJECT_TYPE)) {

          			// Handle document upload
          			createDocument(duCommand, docManager, tempFilename, tempLoc, zipexpand, temporaryZipFileLocation);
		        
          		} else if (duCommand.getDocumentType() != null &&
          				duCommand.getDocumentType().equals(ContainerObjectType.BOOKMARK_OBJECT_TYPE)) {


          			// Handle bookmark creation
          			createBookmark(duCommand, docManager);

          		}
		      }
              if (zipexpand)
            	  zipFile.close();
              
            // Now return the user to the Main Container List
              docManager.setCurrentContainer(contIDbeforeUpload);
            String forwardingPage = SessionManager.getJSPRootURL() + (String) docManager.getNavigator().get("TopContainer");
            if ((forwardingPage != null) && (!forwardingPage.equals(""))) {
            	return new ModelAndView(new RedirectView(forwardingPage+"&module="+request.getParameter("module")));
            } else {
                Map model = new HashMap();
                model.put("action", String.valueOf(Action.VIEW));
                model.put("id", "");
                return new ModelAndView(SessionManager.getJSPRootURL() +"/tiles_document_main", model);
            }

        } catch (Exception e) {
            // Save the exception for the errors page to get
            Map model = new HashMap();
            model.put(PageContext.EXCEPTION, e);
            if(StringUtils.isNotEmpty(request.getParameter("refLink"))){
            	 model.put("refLink",  request.getParameter("refLink"));
        	}
            return new ModelAndView("/tiles_error", model);
        }

    }

    /**
     * Creates and stores a Document object based on the parameters specified.
     * Assumes there is a parameter called <code>File</code> that represents
     * an uploaded file.
     * @param command the fields in the form that were sent to this servlet.
     * @param docManager the document manager used to move the uploaded
     * file to the document vault
     * @param tempLoc 
     * @param string 
     * @throws net.project.base.PnetException if the File parameter cannot be found in the
     * specified parameters or there is a problem storing the uploaded
     * document
     */
    private void createDocument(DocumentUploadCommand command, DocumentManagerBean docManager, String tempFilename, String tempLoc, boolean zipexpand, String temporaryFileLocation)
        throws PnetException {

    	MultipartFile file = command.getFile();
    	
        Document document = null;
        // upload the document and return an instantiated document object
        try {
            if (zipexpand){
                File unzipFile = new File(tempLoc);
                document = docManager.upload (unzipFile.length(), tempFilename,
                        tempLoc, "");   
            }
        	else
        		document = docManager.upload(file.getSize(), file.getOriginalFilename(),
        				temporaryFileLocation, file.getContentType());

        } catch (DocumentException e) {
        	Logger.getLogger(DocumentUpload.class).debug("Unable to upload document.  Deleting " +
                "temp file " + temporaryFileLocation);
            File tempFile = new File(temporaryFileLocation);
            tempFile.delete();

            throw e;
        }

        // set user and container context
        document.setUser(docManager.getUser());
        document.setContainerID(docManager.getCurrentContainerID());

        // Set the incoming parameters
        if (zipexpand)
            document.setName(tempFilename);
        else
        	document.setName(command.getDocName());
        document.setAuthorID(command.getAuthorID());
        document.setDescription(command.getDescription());
        document.setStatusID(command.getStatusID());
        document.setNotes(command.getNotes());

        // store the temporary record in the database (just in case)
        document.tmpStore(document.getID());
        // add the document to the system, in the current container
        // if it was successfully saved to the server's storage
        document.add();

    }


    /**
     * Creates and stores a Bookmark object based on the parameters specified.
     * @param command the form parameters posted to this servlet.
     * @param docManager the document manager bean that specifies the
     * current user and container
     * @throws PnetException if there is a problem storing the bookmark
     */
    private void createBookmark(DocumentUploadCommand command, DocumentManagerBean docManager)
        throws PnetException {

        Bookmark bookmark = new Bookmark();

        // set user and container context
        bookmark.setUser(docManager.getUser());
        bookmark.setContainerID(docManager.getCurrentContainerID());

        // Set the incoming parameters
        bookmark.setURL(command.getUrl());
        bookmark.setName(command.getDocName());
        bookmark.setOwnerID(command.getAuthorID());
        bookmark.setDescription(command.getDescription());
        bookmark.setStatusID(command.getStatusID());
        bookmark.setNotes(command.getNotes());

        bookmark.store();
    }
    
    public static final void copyInputStream(InputStream in, OutputStream out)
    throws IOException
    {
      byte[] buffer = new byte[1024];
      int len;

      while((len = in.read(buffer)) >= 0)
        out.write(buffer, 0, len);

      in.close();
      out.close();
    }

	private boolean verifyFileExists(MultipartFile filePath) {
		if (filePath == null || filePath.isEmpty()) {
			return false;
		}
		return true;
	}
	
	private String getUploadDestinationPath() {
		return SessionManager.getDefaultUploadTempDirectory();
	}
}
