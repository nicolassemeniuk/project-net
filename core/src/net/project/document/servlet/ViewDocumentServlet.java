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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
|
+----------------------------------------------------------------------*/
package net.project.document.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.document.Document;
import net.project.document.DocumentManagerBean;
import net.project.document.handler.DocumentHandler;
import net.project.document.handler.DocumentHandlerFactory;
import net.project.persistence.PersistenceException;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.SpaceFactory;

import org.apache.log4j.Logger;

/**
 * View document servlet displays a document in the document vault to the caller.
 * This class implements most of what used to be in the DocumentDownloadServlet.
 * The functionality was abstracted to allow documents in the vault to be viewed
 * without that document view showing up in the notifications for that document.
 * This is important as the document vault has some other purposes, such as
 * holding the logo for a project.
 *
 * @author Matthew Flower 
 * @since Gecko
 */
public class ViewDocumentServlet extends net.project.base.servlet.BaseSecurityServlet {

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
     * Get the document id of the document that the user has request to view.
     * It is possible that it hasn't been passed as a parameter to this method,
     * if that is true, try to load it from the document manager.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param documentManager a <code>DocumentManagerBean</code> value
     * @return a <code>String</code> value
     */
    public String getCurrentDocumentID(HttpServletRequest request, DocumentManagerBean documentManager) {
        String currentDocumentID;

        //First, try to see if the id was passed to us through the request
        if (request.getParameter("id") != null) {
            currentDocumentID = request.getParameter("id");
        } else {
            //Get the current object id from the document id.
            currentDocumentID = documentManager.getCurrentObjectID();

            //Check to make sure that the document id actually exist.  If it doesn't,
            //raise an error.
            if (currentDocumentID == null) {
            	Logger.getLogger(ViewDocumentServlet.class).error("ViewDocumentServlet was unable to find "+
                                           "a document id to download.  (Tried "+
                                           "parameters and the document manager)");
                throw new NullPointerException("Unable to find a document ID to download!");
            }
        }
    
        //Save the document ID in the session due to a bug that sometimes causes a URL to be
        //called multiple times.
        documentManager.setCurrentObjectID(currentDocumentID);

        //Return the document id.
        return currentDocumentID;
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
     * appropriate action for the current document.
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
            String currentDocumentID = getCurrentDocumentID(request, docManager);
            String status = request.getParameter("status");

            //Prepare the document that we are going to send to the caller
            Document document = new Document();
            document.setID(currentDocumentID);
            document.setUser(docManager.getUser());
            if("D".equals(status)) {
                document.setListDeleted();//need to check from deleted documents
            }

            try {
                document.load();
            } catch (PersistenceException pe) {
            	Logger.getLogger(ViewDocumentServlet.class).debug("DocumentDownloadServlet was unable to load document: " + pe);
            }

            //Run a custom security check to make sure that the user has permission to view
            //this document.
            try {
                SessionManager.getSecurityProvider().securityCheck(currentDocumentID, Integer.toString(Module.DOCUMENT), Action.VIEW, false);
            } catch (AuthorizationFailedException e) {
            	String spaceID = DocumentManagerBean.getSpaceIDForContainerObject(currentDocumentID);
            	e.setSpace(SpaceFactory.constructSpaceFromID(spaceID));
                request.setAttribute(javax.servlet.jsp.PageContext.EXCEPTION, e);
                request.getRequestDispatcher("/errors.jsp").forward(request, response);
                return;
            }

            //If we have gotten to this point, run the appropriate document handler
            handleDocument(document, request, response);

        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

}
