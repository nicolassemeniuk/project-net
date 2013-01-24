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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.document;

import net.project.notification.email.FileAttachment;
import net.project.util.FileUtils;

/**
 * A DocumentAttachment is an attachment representing a Project.net document
 */
public class DocumentAttachment extends FileAttachment {

    /**
     * Creates a new DocumentAttachment using the document's name as the attachment
     * name.
     * The original file extension (if any) is added to the document name to
     * facilitate mail client understanding of the document type.
     * @param document the document to attach
     * @throws DocumentException if there is a problem getting the file object
     * for the document
     */
    public DocumentAttachment(Document document) throws DocumentException {
        super(document.getFileObject());

        String name = null;
        String extension = null;

        // Attachment name is the document name concatenated with the original
        // file extenstion (if there was one)
        name = document.getName();
        extension = FileUtils.getFileExt(document.getShortFileName());
        if (extension != null && extension.length() > 0) {
            name += "." + extension;
        }

        setName(name);
    }

    /**
     * Creates a new DocumentAttachment using the specified name as the attachment
     * name.
     * @param document the document to attach
     * @param name the name for the attachment
     * @throws DocumentException if there is a problem getting the file object
     * for the document
     */
    public DocumentAttachment(Document document, String name) throws DocumentException {
        super(document.getFileObject(), name);
    }

}

