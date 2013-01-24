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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.security.crypto;

import java.io.IOException;
import java.io.InputStream;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.project.base.compatibility.Compatibility;
import net.project.security.EncryptionException;

/**
 * Secret keys whose key is stored in a file.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
public class FileBasedSecretKeyType extends SecretKeyType {

    /** The filename containing the secret key material. */
    private String filename = null;
    private SecretKeySpec secretKey = null;

    protected FileBasedSecretKeyType(String algorithm, String keyFilename) {
        super(algorithm);
        filename = keyFilename;
    }

    public SecretKey getKey() throws EncryptionException {
        if (secretKey == null) {
            synchronized(this) {
                secretKey = new SecretKeySpec(readKeyFile(filename), getAlgorithm());
            }
        }

        return secretKey;
    }

    /**
     * Reads the key file.
     * @param filename the file name of the key file to read; it is assumed
     * to be loacated in the default application file folder
     * @return the read bytes from the key file
     * @throws net.project.security.EncryptionException if there is a problem reading the file
     */
    private static byte[] readKeyFile(String filename) throws EncryptionException {

        // Now read the file and get its data
        byte[] keyData;
        try {
            InputStream in = Compatibility.getResourceProvider().getResourceAsStream(filename);
            keyData = new byte[in.available()];
            in.read(keyData);

        } catch (IOException e) {
            throw new EncryptionException("Unable to read secret key from file " + filename + ": " + e, e);

        }

        return keyData;
    }
}
