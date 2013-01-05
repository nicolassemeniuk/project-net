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

import javax.activation.DataSource;

/**
 * Provides some concrete implementations of attachment methods.
 * @author Tim
 * @since emu
 */
public abstract class AbstractAttachment implements java.io.Serializable, IEmailAttachment {

    /** The name of the attachment */
    private String name = null;

    /** The data source representing the attached data */
    private DataSource dataSource = null;

    /**
     * Creates a new Attachment.
     */
    public AbstractAttachment() {
    }

    /**
     * Creates a new Attachment with the specified name.
     * @param name the attachment name; used when listing the attachments
     * for an email
     */
    public AbstractAttachment(String name) {
        this.name = name;
    }

    /**
     * Sets this Attachment's name.
     * @param name the attachment's name
     * @see #getName
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the datas source representing the data for this attachment.
     * @param dataSource the data source
     * @see #getDataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //
    // Implementing IEmailAttachment
    //

    /**
     * Returns this Attachment's name.
     * @return the name
     * @see #setName
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the data source representing the data for this attachment.
     * @return the data source
     * @see #setDataSource
     */
    public DataSource getDataSource() {
        return this.dataSource;
    }

}
