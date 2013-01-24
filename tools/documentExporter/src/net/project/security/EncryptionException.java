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
package net.project.security;

/**
 * Indicates a problem occurred encrypting or decrypting.
 *
 * @author Tim Morrow
 * @since Version 7.6
 */
public class EncryptionException extends Exception {

    /**
     * Creates an empty EncryptionException.
     */
    public EncryptionException() {
        super();
    }

    /**
     * Creates an EncryptionException with the specified message.
     * @param message the message
     */
    public EncryptionException(String message) {
        super(message);
    }

    /**
     * Creates an EncryptionException with the specified message
     * indicating the throwable that caused this exception.
     * @param message the message
     * @param cause the cause of the exception
     */
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an EncryptionException with no message
     * indicating the throwable that caused this exception.
     * @param cause
     */
    public EncryptionException(Throwable cause) {
        super(cause);
    }

}
