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
|
+----------------------------------------------------------------------*/
package net.project.document.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.document.Document;

/**
 * DocumentHandler defines a method that needs to be implemented by any class
 * wanting to handle clicks in the Document Vault.
 *
 * To be a handler for the document vault, first write a class that implements
 * this interface, then add an entry in the PN_DOC_HANDLER table that contains
 * the fully-qualified classname of the document handler.  
 *
 * @author Matthew Flower
 * @since Gecko Update 2 (ProductionLink)
 */
public interface DocumentHandler {
    /**
     * This method response to a click on a file in a document vault.  The default
     * action is to download the file, which is handled by the {@link net.project.document.handler.DefaultDocumentHandler}
     * class.
     *
     * @param request a <code>HttpServletRequest</code> value containing the
     * parameters received from the ViewDocumentServlet.
     * @param response a <code>HttpServletResponse</code> value which can be used
     * for a variety of purposes, such as streaming a document to the user, or
     * redirecting.
     * @param document a <code>Document</code> value which represents the document
     * clicked on in the document vault.
     */
    public abstract void handleDocument(HttpServletRequest request, HttpServletResponse response, Document document);
}
