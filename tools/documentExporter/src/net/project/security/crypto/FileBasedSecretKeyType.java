/* 
 * Copyright 2000-2006 Project.net Inc.
 *
 * Licensed under the Project.net Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://dev.project.net/licenses/PPL1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 15404 $
|       $Date: 2006-08-28 16:50:09 +0200 (Mon, 28 Aug 2006) $
|     $Author: deepak $
|
+-----------------------------------------------------------------------------*/
package net.project.security.crypto;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
//        if (secretKey == null) {
//            synchronized(this) {
//                secretKey = new SecretKeySpec(readKeyFile(filename), getAlgorithm());
//            }
//        }

        return secretKey;
    }

//    /**
//     * Reads the key file.
//     * @param filename the file name of the key file to read; it is assumed
//     * to be loacated in the default application file folder
//     * @return the read bytes from the key file
//     * @throws net.project.security.EncryptionException if there is a problem reading the file
//     */
//    private static byte[] readKeyFile(String filename) throws EncryptionException {
//
//        // Now read the file and get its data
//        byte[] keyData;
//        try {
//            InputStream in = Compatibility.getResourceProvider().getResourceAsStream(filename);
//            keyData = new byte[in.available()];
//            in.read(keyData);
//
//        } catch (IOException e) {
//            throw new EncryptionException("Unable to read secret key from file " + filename + ": " + e, e);
//
//        }
//
//        return keyData;
//    }
}
