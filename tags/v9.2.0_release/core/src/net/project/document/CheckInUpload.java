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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import net.project.base.PnetException;
import net.project.persistence.PersistenceException;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.util.FileUtils;

import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Respond to an upload of a document by checking the document in.
 *
 * @author Matthew Flower
 * @since 28 Dec 2005
 */
public class CheckInUpload extends SimpleFormController {
    private void verifySecurity(DocumentManagerBean docManager, SecurityProvider securityProvider) throws PersistenceException {
        
        String containerID = docManager.getCurrentContainerID();
        AccessVerifier.verifyAccess(
            docManager.getModuleFromContainerID(containerID),
            Action.MODIFY,
            containerID
        );
    }
    
    protected ModelAndView onSubmit(HttpServletRequest request,
        HttpServletResponse response, Object command,
        BindException bindException) throws Exception {

        CheckInUploadCommand ciCommand = (CheckInUploadCommand)command;

        try {
            // Create Beans
            SecurityProvider securityProvider = (SecurityProvider) request.getSession().getAttribute(SecurityProvider.SECURITY_PROVIDER_SESSION_OBJECT_NAME);
            DocumentManagerBean docManager = (DocumentManagerBean) request.getSession().getAttribute("docManager");
            
            /*
             * bug bfd-2884 fix -- reporting if user has entered wrong path to file
             */
            if (ciCommand.getFile() == null || ciCommand.getFile().isEmpty()) {
            	Map model = new HashMap();
                model.put("error.nofile", "nofile");

                return new ModelAndView("/tiles_check_in_upload", model);
            }

            // Upload and checkin the document
            // docManager checks access permissions first
            checkInDocument(ciCommand, docManager);

            // Determine the page to forward to after completing checkin
            String forwardingPage = (String)docManager.getNavigator().get("TopContainer");
            if (forwardingPage == null || forwardingPage.equals("")) {
                forwardingPage = SessionManager.getJSPRootURL() + "/document/Main.jsp?module=" + net.project.base.Module.DOCUMENT;
            }

            Map model = new HashMap();
            model.put("reloadURL", forwardingPage);
            model.put("checkIn", true);
            model.put("module", String.valueOf(securityProvider.getCheckedModuleID()));
            model.put("action", String.valueOf(securityProvider.getCheckedActionID()));

            return new ModelAndView("/tiles_logo_upload", model);

        } catch (Exception e) {
            Map model = new HashMap();
            model.put(PageContext.EXCEPTION, e);
            return new ModelAndView("/tiles_error", model);
        }
    }

    /**
     * Checks-in a Document object based on the parameters specified.
     * Assumes there is a parameter called <code>File</code> that represents
     * an uploaded file.
     * @param docManager the document manager that adds the document to the vault
     * @throws net.project.base.PnetException if no File parameters is found in the specified parameters
     * or there is a problem storing the document
     */
    private void checkInDocument(CheckInUploadCommand command, DocumentManagerBean docManager)
        throws PnetException {

        //Move the file to somewhere on the disk.
        MultipartFile file = command.getFile();
        String tempFileLocation = FileUtils.commitUploadedFileToFileSystem(file);

        // Grab the file that was uploaded
        if (command.getFile().isEmpty() || command.getFile() == null) {
            throw new PnetException("Missing request parameter File");
        }

        if(command.getId() == null) {
            command.setId(docManager.getCurrentObjectID());
        }
        if(command.getContainerID() == null) {
            command.setContainerID(docManager.getCurrentContainerID());
        }

        Document document = new Document();
        document.setID(command.getId());
        document.load();
        document = docManager.upload(document, file.getSize(),
            file.getOriginalFilename(), tempFileLocation, file.getContentType());
        document.setUser(docManager.getUser());
        document.setContainerID(command.getContainerID());
        document.setStatusID(command.getStatusID());
        document.setNotes(command.getNotes());

        // store the temporary record in the database (just in case)
        document.tmpStore();

        // add the document to the system, in the current container
        // if it was successfully saved to the server's storage
        docManager.checkInDocument(document);

    }

}
