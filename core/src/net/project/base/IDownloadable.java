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
package net.project.base;

/* 
 * Any implementing object may be downloaded to a file by implementing this
 * interface.
 * @see net.project.base.servlet#DownloadServlet
 * @author Deepak
 * @author Tim
 * @since emu
 */
public interface IDownloadable {
    

    /**
     * Returns the file name that the downloaded object will be saved to.
     * This is typically used as the default file name.  Most browsers will
     * allow the user to override it.
     * The filename should have an extension appropriate to the content type.
     * It should not contain any path information.
     * @return the filename, for example <code>MyFile.txt</code>
     */
    public String getFileName();
    

    /**
     * Returns the content type of the data stream from this object.
     * This is used for telling the web browser the type of data produced.
     * The content type is passed as-is to the browser; it must therefore 
     * any appropriate <code>charset</code> information.
     * @return the content type, for example <code>application/x-excel</code>
     */
    public String getContentType();
    

    /**
     * Returns the input stream for reading the content of this downloadable
     * object.
     * @return the input stream
     * @throws java.io.IOException if there is a problem reading the data
     */
    public java.io.InputStream getInputStream() throws java.io.IOException;
    

    /**
     * Indicates the length of data in this object.
     * @return the length of data
     */
    public long getLength();

}
