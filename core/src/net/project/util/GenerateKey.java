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
| Provides File utility methods
+----------------------------------------------------------------------*/
package net.project.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Provides convenient way to generate Blowfish key material.
 */
public class GenerateKey {

    /**
     * <pre>
     * Usage:
     *    GenerateKey fileName  
     *        fileName  -  the name of the file to create.  For example "mykey.key"
     * </pre>
     * @param args the command line parameters
     */
    public static void main(String[] args) {
        
        String filePath = null;

        // Grab all parameters
        for (int i = 0; i < args.length; i++) {

            switch (i) {
            case 0:
                filePath = args[i];
                break;
            }
        }

        if (filePath == null) {
            
            printUsage();

        } else {
            
            try {
                createKey(filePath);
            
            } catch (Exception e) {
                e.printStackTrace();
            
            }

        }

    }
    
    /**
     * Create a secret key file.
     * @param filePath the path to the file to create
     * @throws IOException if the file at the path already exists or
     * there is a problem writing to the file
     * @throws NoSuchAlgorithmException if the Blowfish algorithm
     * is not supported
     */
    private static void createKey(String filePath) 
            throws IOException, java.security.NoSuchAlgorithmException {
        
        File f = new File(filePath);

        if (f.exists()) {
            throw new IOException("File already exists: " + f.getAbsolutePath());
        }
            
        // Generate a secret key for the Blowfish algorithm
        KeyGenerator kg = KeyGenerator.getInstance("Blowfish");
        SecretKey mykey = kg.generateKey();

        // Write key to file
        OutputStream out = new FileOutputStream(f);
        out.write(mykey.getEncoded());
        out.close();

        System.out.println("Wrote key to file: " + f.getAbsoluteFile());
    }

    /**
     * Print usage information to standard out.
     */
    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("    GenerateKey fileName");
        System.out.println("        fileName  -  the name of the file to create.  For example \"mykey.key\"");

    }

}
