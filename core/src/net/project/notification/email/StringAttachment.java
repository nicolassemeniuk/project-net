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


/**
 * A StringAttachment is an attachment based on an arbitrary String.
 * @author Tim
 * @since emu
 */
public class StringAttachment extends AbstractAttachment {

    /**
     * Creates a new StringAttachment assuming the string contains ASCII
     * characters only.
     * @param stringData the string to attach
     * @param name the name to use for the attachment
     * @param contentType the content type to specify for the attachment
     */
    public StringAttachment(String stringData, String name, String contentType) {
        this(stringData, name, contentType, null);
    }

    /**
     * Creates a new StringAttachment specifying the character set of the string.
     * @param stringData the string to attach
     * @param name the name to use for the attachment
     * @param contentType the content type to specify for the attachment
     * @param charset the character set of the string data
     */
    public StringAttachment(String stringData, String name, String contentType, String charset) {
        super(name);
        if (charset == null) {
            setDataSource(new ByteArrayDataSource(stringData, contentType));
        } else {
            setDataSource(new ByteArrayDataSource(stringData, contentType, charset));
        }
    }
}
