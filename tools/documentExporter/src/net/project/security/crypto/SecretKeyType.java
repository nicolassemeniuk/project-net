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
 /*----------------------------------------------------------------------+
|                                                                       
|    $RCSfile$
|   $Revision: 14865 $
|       $Date: 2006-03-31 06:19:17 +0200 (Fri, 31 Mar 2006) $
|     $Author: avinash $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.security.crypto;

import javax.crypto.SecretKey;
import net.project.security.EncryptionException;

/**
 * Enumeration of secret key type names and the files in which they are located.
 */
public abstract class SecretKeyType {
    /** The algorithm of to which this secret key type pertains. */
    private String algorithm = null;


    /** Creates a new secret key type. */
    protected SecretKeyType(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Returns the algorithm used to generate the secret key of this
     * type.
     * @return the algorithm
     */
    public String getAlgorithm() {
        return this.algorithm;
    }

    /**
     * Get the key for this secret key type.
     *
     * @return a <code>SecretKey</code> for this SecretKeyType.
     * @throws EncryptionException if there is an error constructing the secret
     * key.
     */
    public abstract SecretKey getKey() throws EncryptionException;

    //
    // Enumeration constants
    //

    /**
     * Default secret key type, currently Blowfish algorithm, in file key.txt.
     */
    public static final SecretKeyType DEFAULT = new FileBasedSecretKeyType("Blowfish", "key.txt");

    /**
     * License Certificate secret key type, currently Blowfish algorithm, in file licensecertificate.key.
     */
    public static final SecretKeyType LICENSE_CERTIFICATE = new FileBasedSecretKeyType("Blowfish", "licensecertificate.key");

    /**
     * Invoice secret key type, currently Blowfish algorithm, in file invoice.key.
     */
    public static final SecretKeyType INVOICE = new FileBasedSecretKeyType("Blowfish", "invoice.key");
}
