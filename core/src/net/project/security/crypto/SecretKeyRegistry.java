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
 * Provides a registry of secret keys by type.  Allows various parts of the
 * application to use a different secret key for encryption.
 *
 * @deprecated as of Version 7.5.  Use
 * {@link net.project.security.crypto.SecretKeyType#getKey} instead.
 * @author Matthew Flower
 * @since Version 7.5
 */
public class SecretKeyRegistry implements java.io.Serializable {
    /**
     * Returns a secret key registry.
     * @return a secret key registry
     */
    public static SecretKeyRegistry getInstance() {
        return new SecretKeyRegistry();
    }

    /**
     * Creates an empty secret key registry.
     */
    private SecretKeyRegistry() {
        // Do nothing
    }

    /**
     * Returns the secret key for the specified type.
     * @param keyType the type of key to get the secret key for
     * @throws EncryptionException if there is a problem getting the secret key
     */
    public SecretKey getSecretKey(SecretKeyType keyType) throws EncryptionException {
        return keyType.getKey();
    }
}
