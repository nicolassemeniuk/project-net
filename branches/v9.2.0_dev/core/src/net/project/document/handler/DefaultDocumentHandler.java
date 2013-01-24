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
import net.project.document.DocumentException;

import org.apache.log4j.Logger;

/**
 * When a file is clicked on in the document vault, this class will be in
 * control of what happens next if another DocumentHandler has not been
 * specified for that MIME Type.
 *
 * This handler forces the file to be downloaded.
 *
 * @author Matthew Flower
 * @since Gecko Update 2 (ProductionLink)
 */
public class DefaultDocumentHandler implements DocumentHandler {
    /**
     * handleDocument forces the current docoument to be downloaded to
     * the user.
     *
     * @param request a <code>HttpServletRequest</code> value containing
     * any parameters received by the calling class.
     * @param response a <code>HttpServletResponse</code> value 
     * @param document the <code>Document</code> value that is going to
     * be sent to the user.
     */
	public void handleDocument(HttpServletRequest request, HttpServletResponse response, Document document) {
		try {
			if (!"true".equals(request.getParameter("image"))) {
				document.streamToHttpResponse(response);
			} else {
				document.imageStreamToHttpResponse(response);
			}
		} catch (DocumentException de) {
			Logger.getLogger(DefaultDocumentHandler.class).error("Unable to send document " + document.getID() + " to HTTP client.  Error: " + de);
		}
	}
}
