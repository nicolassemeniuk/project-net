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
  |       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
  |     $Author: umesha $
  |                                                                       
  +----------------------------------------------------------------------*/
package net.project.document.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.document.Document;

/**
 * <code>DocumentDownloadServlet</code> builds on ViewDocumentServlet by logging
 * any viewing of a document.  (This allows for notification of view events.)
 *
 * @author Matthew Flower
 * @version Gecko
 */
public class DocumentDownloadServlet extends ViewDocumentServlet {

    /**
     * Log this document access then call the superclass to figure out what to
     * do with this class.
     *
     * @param document a <code>Document</code> value containing information about
     * the document to be sent.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     */
    public void handleDocument(Document document, HttpServletRequest request, HttpServletResponse response) {
        //Log this download in the database
        document.logDownloadEvent();

        //Let the superclass decide what to do with this document.
        super.handleDocument(document, request, response);
    }
}





