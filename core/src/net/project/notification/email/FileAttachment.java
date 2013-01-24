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
package net.project.notification.email;

import java.io.File;

import javax.activation.FileDataSource;

/**
 * A FileAttachment is an attachment based on a File object.
 * @author Tim
 * @since emu
 */
public class FileAttachment extends AbstractAttachment {

    /**
     * Creates a new FileAttachment using the file's name.
     * @param file the file to attach
     */
    public FileAttachment(File file) {
        this(file, file.getName());
    }

    /**
     * Creates a new FileAttachment with the specified name.
     * @param file the file to attach
     * @param name the name to use on the attachment
     */
    public FileAttachment(File file, String name) {
        super(name);
        setDataSource(new FileDataSource(file));
    }
}


