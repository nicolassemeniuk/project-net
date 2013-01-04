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
 * Indicates that Password Based Encryption was attempted with a password
 * containing invalid characters.
 *
 * @author Tim Morrow
 * @since Version 7.6.0
 */
public class InvalidPasswordForEncryptionException extends EncryptionException {

    /**
     * Creates an empty InvalidPasswordForEncryptionException.
     */
    public InvalidPasswordForEncryptionException() {
        super();
    }

    /**
     * Creates an InvalidPasswordForEncryptionException with the specified message.
     * @param message the message
     */
    public InvalidPasswordForEncryptionException(String message) {
        super(message);
    }

    /**
     * Creates an InvalidPasswordForEncryptionException with the specified message
     * indicating the throwable that caused this exception.
     * @param message the message
     * @param cause the cause of the exception
     */
    public InvalidPasswordForEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an InvalidPasswordForEncryptionException with no message
     * indicating the throwable that caused this exception.
     * @param cause
     */
    public InvalidPasswordForEncryptionException(Throwable cause) {
        super(cause);
    }

}