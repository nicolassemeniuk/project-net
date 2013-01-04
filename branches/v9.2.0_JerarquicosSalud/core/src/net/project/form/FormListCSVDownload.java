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
package net.project.form;

import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.log4j.Logger;

/**
 * Provides an implementation of an <code>{@link net.project.base.IDownloadable}</code>
 * for downloading form list CSV data.
 */
public class FormListCSVDownload implements net.project.base.IDownloadable {
    
    /** The bytes representing the form list data. */
    private byte[] data = null;

    /** The character set that the CSV file will be streamed in. */
    private String characterSetID = "ISO-8859-1";

    /**
     * Constructs an empty FormListCSVDownload.
     * <code>{@link #setFormList}</code> must be called to specify the data to download.
     */
    public FormListCSVDownload() {
        // Do nothing
    }


    /**
     * Constructs a new FormListCSVDownload based on the specified form list.
     * @param formList the form list to download
     */
    public FormListCSVDownload(FormList formList) {
        this();
        setFormList(formList);
    }


    /**
     * Sets the form list whose data will be downloaded.
     * @param formList the form list to download
     */
    public void setFormList(FormList formList) {
        String csv = formList.getCSV();

        try {
            CharsetEncoder ce = Charset.forName(characterSetID).newEncoder();
            this.data = ce.encode(CharBuffer.wrap(csv)).array();
        } catch (CharacterCodingException e) {
        	Logger.getLogger(FormListCSVDownload.class).debug("Unable to convert CSV file to character " +
                "set \"" + characterSetID + "\".  Defaulting to \"ISO-8859-1\".");
            characterSetID = "ISO-8859-1";
            this.data = csv.getBytes();
        }
    }


    /**
     * Returns the content type of the form list data.
     * @return <code>application/x-excel</code>
     */
    public String getContentType() {
        return "application/x-excel;charset=" +characterSetID;
    }


    /**
     * Returns the input stream for this form list csv data.
     * @return the stream
     */
    public java.io.InputStream getInputStream() throws java.io.IOException {
        return new java.io.ByteArrayInputStream(this.data);
    }


    /**
     * Returns the length of this data.
     * @return always <code>-1</code> to indicate that the length is unknown
     */
    public long getLength() {
        return -1;
    }
    

    /**
     * Returns the default filename for the form list data.
     * @return the default filename <code>FormList.csv</code>
     */
    public String getFileName() {
        return "FormList.csv";
    }

    /**
     * This is the character set id that corresponds to the character set in
     * which we are going to stream the CSV file.
     *
     * We need to do this because some programs (like Microsoft Excel) don't
     * process CSV files in UTF-8 format.  If this is set as the default
     * character set for an installation of Project.net before we had this
     * setting, CSV wouldn't work.
     *
     * @return a <code>String</code> containing the unique id for the character
     * set in which we are going to stream the CSV file.
     */
    public String getCharacterSetID() {
        return characterSetID;
    }

    /**
     * Set the id of the character set that we stream the form list in when we
     * download it.
     *
     * @param characterSetID a <code>String</code> containing the unique id for
     * the character set in which we are going to stream the CSV file.
     */
    public void setCharacterSetID(String characterSetID) {
        this.characterSetID = characterSetID;
    }
}
