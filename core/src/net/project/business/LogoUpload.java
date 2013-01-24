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

 package net.project.business;

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
import net.project.security.User;
import net.project.util.FileUtils;

import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Spring controller which provides handling for uploading logos to the business
 * space.  Copies the uploaded logo to the document vault and adds it to the
 * business space.
 *
 * @author Matthew Flower
 * @since 22 December 2005
 */
public class LogoUpload extends SimpleFormController {
    private void verifySecurity() {
        AccessVerifier.verifyAccess(Module.BUSINESS_SPACE, Action.MODIFY);
    }

    protected ModelAndView onSubmit(HttpServletRequest request,
        HttpServletResponse response, Object command, BindException bindException) throws Exception {

        verifySecurity();
        LogoUploadCommand luCommand = (LogoUploadCommand)command;

        // Add the logo to the document vault and business space
        String docid = uploadLogo(luCommand.getFile(), request.getSession());

        // Determine the page to reload after completing checkin
        String forwardingPage = "/business/ModifyBusiness.jsp?module=" +
            Module.BUSINESS_SPACE + "&action=" + Action.MODIFY;

        //Construct the list of parameters to send to the next page
        Map model = new HashMap();
        model.put("reloadURL", forwardingPage);
        model.put("module", String.valueOf(Module.BUSINESS_SPACE));
        model.put("action", String.valueOf(Action.MODIFY));
        model.put("newLogoId", docid); // add uploaded logo to request to show on parent window 
        // Forward to CloseWindow page
        return new ModelAndView("/tiles_logo_upload", model);
    }

    /**
     * Uploads a logo, storing in the document repository.
     * Then adds it to the specified business.
     * Assumes there is a parameter called <code>File</code> that represents
     * an uploaded file.
     *
     * @param file a <code>MultipartFile</code> object that has information
     * about the uploaded file.
     * @throws PnetException if the file could not be found in the specified parameters
     */
    private String uploadLogo(MultipartFile file, HttpSession session)
        throws PnetException {

        // Create Beans
        User user = (User)session.getAttribute("user");
        DocumentManagerBean docManager = (DocumentManagerBean) session.getAttribute("docManager");
        BusinessSpaceBean businessSpace = (BusinessSpaceBean) session.getAttribute("businessSpace");

        // Grab the file that was uploaded
        if (file == null || file.isEmpty()) {
            throw new PnetException("Missing request parameter File");
        }

        docManager.setUser(user);

        String temporaryFileName;
        temporaryFileName = FileUtils.commitUploadedFileToFileSystem(file);

        String newLogoID = docManager.addFileToSpace(
            file.getSize(),
            file.getOriginalFilename(),
            temporaryFileName,
            file.getContentType(),
            user.getCurrentSpace().getID());
        businessSpace.addLogoToBusiness(newLogoID);
        return newLogoID;
    }


}
