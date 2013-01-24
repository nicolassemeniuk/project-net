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
| Enhanced by Project.net
+----------------------------------------------------------------------*/
/*
 * @(#)ByteArrayDataSource.java	1.2 00/05/24
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */
package net.project.notification.email;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.activation.DataSource;

/**
 * A DataSource used for representing any stream of bytes.
 * This class implements a DataSource from:
 * 	an InputStream
 *	a byte array
 * 	a String
 *
 * @author Sun Microsystems, Inc
 * @author Tim
 * @since emu
 */
public class ByteArrayDataSource implements DataSource {

    /** The data which is the data source */
    private byte[] data = null;

    /** The content type of the data source */
    private String contentType = null;

    /** The name of the data source */
    private String name = "ByteArrayDataSource";

    /**
     * Creates a DataSource from an input stream.
     * @param is the InputStream to use as the data source
     * @param contentType the data source content type
     */
    public ByteArrayDataSource(InputStream is, String contentType) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                // XXX - must be made more efficient by
                // doing buffered reads, rather than one byte reads
                os.write(ch);
            }
            data = os.toByteArray();

        } catch (IOException ioex) {
            // No data
        }
        this.contentType = contentType;
    }

    /**
     * Creates a DataSource from a byte array.
     * @param data the byte array to use as the data source
     * @param contentType the data source content type
     */
    public ByteArrayDataSource(byte[] data, String contentType) {
        this.data = data;
        this.contentType = contentType;
    }

    /**
     * Creates a DataSource from a String assuming the string contains
     * ASCII (<code>iso-8859-1</code> actually) characters.
     * @param data the string data to use as the data source
     * @param contentType the data source content type
     */
    public ByteArrayDataSource(String data, String contentType) {
        try {
            // Assumption that the string contains only ASCII
            // characters!  Otherwise just pass a charset into this
            // constructor and use it in getBytes()
            this.data = data.getBytes("iso-8859-1");

        } catch (UnsupportedEncodingException uex) {
            // No data

        }
        this.contentType = contentType;
    }

    /**
     * Creates a DataSource from a String for the specified character set.
     * @param data the string data to use as the data source
     * @param contentType the data source content type
     * @param charset the character set to use for converting string to bytes
     */
    public ByteArrayDataSource(String data, String contentType, String charset) {
        try {
            this.data = data.getBytes(charset);

        } catch (UnsupportedEncodingException uex) {
            // No data

        }
        this.contentType = contentType;
    }

    /**
     * Returns an InputStream for the data.
     * Note - a new stream must be returned each time.
     * @return the InputStream for the data
     * @throws IOException if there is no data from which to construct the
     * input stream
     */
    public InputStream getInputStream() throws IOException {
        if (data == null) {
            throw new IOException("No data available from which to construct InputStream");
        }
        return new ByteArrayInputStream(data);
    }

    /**
     * Not supported.
     * @throws IOException because this method is not supported
     */
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("ByteArrayDataSource does not supported getOutputStream()");
    }

    /**
     * Returns the content type of this data source.
     * @return the content type
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * Returns the name of this data source.
     * @return the name
     */
    public String getName() {
        return this.name;
    }
}
