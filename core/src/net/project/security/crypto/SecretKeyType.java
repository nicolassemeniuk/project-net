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
