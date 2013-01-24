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
|   $Revision: 15475 $
|       $Date: 2006-09-26 10:55:36 +0200 (Tue, 26 Sep 2006) $
|     $Author: avinash $
|
|
+----------------------------------------------------------------------*/
package net.project.wiki;

import javax.servlet.http.HttpServletRequest;

import net.project.document.DocumentManagerBean;
import net.project.document.servlet.ViewDocumentServlet;
import net.project.hibernate.model.PnWikiAttachment;
import net.project.hibernate.service.IPnWikiAttachmentService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;

import org.apache.log4j.Logger;

/**
 * 
 * @author Uros Lates 
 */
public class ViewWikiImageServlet extends ViewDocumentServlet {

	/**
     * Get the document id of the document that the user has request to view.
     * First try to get it by use of request parameters: <b>wikiPageName</b> and
     * <b>imageName</b> from pn_wiki_attachment table.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param documentManager a <code>DocumentManagerBean</code> value
     * @return a <code>String</code> value
     */
    public String getCurrentDocumentID(HttpServletRequest request, DocumentManagerBean documentManager) {
        String currentDocumentID = null;
        try {
	        // added for viewing wiki images
	        if( request.getParameter("wikiPageName") != null && request.getParameter("imageName") != null ) {
	        	String imageName = request.getParameter("imageName");
				imageName = new String(request.getParameter("imageName").getBytes(), SessionManager.getCharacterEncoding()).toString();
	        	
	        	String ownerObjectId = request.getParameter("ownerObjectId");
	        	IPnWikiAttachmentService wikiAttachmentService = ServiceFactory.getInstance().getPnWikiAttachmentService();
				PnWikiAttachment wikiAttachment = wikiAttachmentService.getFileIdWithWikiPageAndFileName(Integer.valueOf(ownerObjectId), imageName);
				currentDocumentID = wikiAttachment != null ? wikiAttachment.getFileId().toString() : (String.valueOf(-1));
	        } else if (request.getParameter("id") != null) {
	            currentDocumentID = request.getParameter("id");
	        } else {
	            // Get the current object id from the document id.
	            currentDocumentID = documentManager.getCurrentObjectID();
	
	            // Check to make sure that the document id actually exist.  If it doesn't,
	            // raise an error.
	            if (currentDocumentID == null) {
	            	Logger.getLogger(ViewWikiImageServlet.class).error("ViewDocumentServlet was unable to find "+
	                                           "a document id to download.  (Tried "+
	                                           "parameters and the document manager)");
	                throw new NullPointerException("Unable to find a document ID to download!");
	            }
	        }
	    
	        // Save the document ID in the session due to a bug that sometimes causes a URL to be
	        // called multiple times.
	        documentManager.setCurrentObjectID(currentDocumentID);
        } catch (Exception pnetEx) {
    		Logger.getLogger(ViewWikiImageServlet.class).error("Error occurred while getting document id :"+ pnetEx.getMessage());
    	}
 
        // Return the document id.
        return currentDocumentID;
    }

}
