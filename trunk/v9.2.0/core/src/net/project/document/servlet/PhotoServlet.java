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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 18:17:28 +0530 (Fri, 21 Nov 2008) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
package net.project.document.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.business.BusinessSpaceBean;
import net.project.document.Document;
import net.project.document.DocumentManagerBean;
import net.project.document.handler.DocumentHandler;
import net.project.document.handler.DocumentHandlerFactory;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.security.User;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Photo servlet displays a person's image in the document vault to the caller.
 * This class implements most of what used to be in the DocumentDownloadServlet.
 * The functionality was abstracted to allow person's image in the vault to be viewed
 * without that document view showing up in the notifications for that document.
 */
public class PhotoServlet extends HttpServlet {
    
    // Actions to view user's image and logo images of business and project
    public enum ImageViewActions {
        BLOGO, PLOGO;
        
        public static ImageViewActions get(String v) {
            try {
                return ImageViewActions.valueOf( v.toUpperCase() );
            } catch( Exception ex ) { }
            return null;
         }
    }
    
    /**
     * Get a valid DocumentManagerBean for the current user.  There is a possibility
     * (actually a good one) that the DocumentManager hasn't been created when this
     * servlet is called.  For that reason, we will also create the DocumentManager
     * if it doesn't already exist.
     *
     * @param request a <code>HttpServletRequest</code> value for the current
     * servlet call.  This will be used to grab the Document manager out of the
     * session.
     * @param user an <code>User</code> value.  This is the user that the
     * DocumentManager will use to report events.
     * @return a <code>DocumentManagerBean</code> instance that is properly set
     * up and ready for use.
     */
    protected DocumentManagerBean getDocumentManager(HttpServletRequest request, User user) {
        //Assume that the Document manager has already been created and exists
        //in the HttpSession
        DocumentManagerBean docManager = (DocumentManagerBean)request.getSession().getAttribute("docManager");

        //Check to make sure that the document manager actually existed.
        if (docManager == null) {
            //The document manager hasn't been created yet, create one from scratch
            docManager = new DocumentManagerBean();
            // Add the bean to the session
            request.getSession().setAttribute("docManager", docManager);
        } 

        //Make sure that the user that the Document manager thinks is current agrees
        //with the user that submitted this request.
        docManager.setUser(user);

        //Return our newly created document manager to the user.
        return docManager;
    }

    /**
     * Get the id of the image that the user has requested to view.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param documentManager a <code>DocumentManagerBean</code> value
     * @return a <code>String</code> value
     */
    public String getImageID(HttpServletRequest request) {
        String imageID = null;
        IPnPersonService pnPersonService = ServiceFactory.getInstance().getPnPersonService();
        String id = request.getParameter("id");
        String logoType = request.getParameter("logoType");
        ImageViewActions imageViewActions = ImageViewActions.get( logoType );
        if (StringUtils.isNotEmpty(id)) {
            try {
                if (imageViewActions == ImageViewActions.BLOGO) { // get business's logo id from business id
                    BusinessSpaceBean businessSpace = (BusinessSpaceBean)request.getSession().getAttribute("businessSpace");
                    if (businessSpace != null && id.equals(businessSpace.getID()))
                        imageID = businessSpace.getLogoID();
                } else if (imageViewActions == ImageViewActions.PLOGO) { // get project's logo id from project id
                    ProjectSpaceBean projectSpace = (ProjectSpaceBean)request.getSession().getAttribute("projectSpace");
                    if (projectSpace != null && id.equals(projectSpace.getID()))
                        imageID = projectSpace.getProjectLogoID();
                } else if (StringUtils.isEmpty(logoType)) {
                    // get the person's image id from user's id 
                    imageID = pnPersonService.getImageIdByPersonId(Integer.valueOf(id)).toString();
                }
            } catch (Exception e) {
                Logger.getLogger(PhotoServlet.class).error("PhotoServlet was unable to find a image id by person id : " + e.getMessage());
            }
        } 
        
        // return image id.
        return imageID;
    }

    /**
     * For the specified document, figure the correct handler to deal with the
     * response to the user.  When the correct class has been found, let it deal
     * with generating the response.
     *
     * @param document a <code>Document</code> that is going to be sent to the calling
     * HTTP client.
     * @param response a <code>HttpServletResponse</code> that we will use to send the
     * document to the HTTP client.
     */
    protected void handleDocument(Document document, HttpServletRequest request, HttpServletResponse response) {
        DocumentHandler documentHandler = DocumentHandlerFactory.getHandler(document, DocumentHandlerFactory.DEFAULT_HANDLER);
        documentHandler.handleDocument(request, response, document);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }

    /**
     * This method responds to the form post method by performing the
     * appropriate action.
     *
     * @param request a <code>HttpServletRequest</code> value containing
     * request parameters.
     * @param response a <code>HttpServletResponse</code> value representing
     * the results that are going to be sent to the user.
     * @exception IOException if an error occurs
     * @exception ServletException if an error occurs.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException {
        try {
            //Get the DocumentManagerBean for the current user.  This involves some bluestone-specific
            //API calls.
            User user = (User)request.getSession().getAttribute("user");
            DocumentManagerBean docManager = getDocumentManager(request, user);
            String imageId = getImageID(request);
            docManager.setCurrentObjectID(imageId);
            
            //Prepare the image that we are going to send to the caller
            Document document = new Document();
            document.setID(imageId);
            document.setUser(docManager.getUser());
            try {
                document.load();
            } catch (PersistenceException pe) {
            	Logger.getLogger(PhotoServlet.class).debug("DocumentDownloadServlet was unable to load document: " + pe);
            }
            handleDocument(document, request, response);
        } catch (Exception e) {
            Logger.getLogger(PhotoServlet.class).debug("PhotoServlet was unable to load image : " + e);
        }
    }

}
