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
package net.project.notification;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.mail.SendFailedException;

/**
 * Indicates a problem occurred sending an email containing correctly
 * constructed email addresses to one or more recipients.
 * <p>
 * This implies that an email was valid and potentially sent to one or more
 * recipients.  However there was a problem sending the email to at least
 * one recipient.
 * The exception contains the collection of unsent email addresses.
 * </p>
 */
class ValidEmailSendFailException extends EmailSendFailException {

    /**
     * Constructs a ValidEmailSendFailException with the specified message, indicating
     * the SendFailedException that caused this it.
     * @param message the message
     * @param cause the exception that caused this exception
     */
    public ValidEmailSendFailException(String message, SendFailedException cause) {
        super(message, cause);
    }

    /**
     * Returns the invalid addresses that caused this error.
     * @return the collection where each element is a <code>javax.mail.Address</code>
     * @see SendFailedException#getInvalidAddresses
     */
    public Collection getInvalidAddresses() {
        return makeList(((SendFailedException) getCause()).getInvalidAddresses());
    }

    /**
     * Returns the valid addresses to which a mail was sent.
     * @return the collection where each element is a <code>javax.mail.Address</code>
     * @see SendFailedException#getValidSentAddresses
     */
    public Collection getValidSentAddresses() {
        return makeList(((SendFailedException) getCause()).getValidSentAddresses());
    }

    /**
     * Returns the valid addresses but to which a mail was NOT sent.
     * @return the collection where each element is a <code>javax.mail.Address</code>
     * @see SendFailedException#getValidUnsentAddresses
     */
    public Collection getValidUnsentAddresses() {
        return makeList(((SendFailedException) getCause()).getValidUnsentAddresses());
    }

    /**
     * Constructs a list from the specified array of addresses.
     *
     * @param addresses the addresses from which to make the list
     * @return the list built from the array; the empty list is returned if
     * the array is null
     */
    private List makeList(javax.mail.Address[] addresses) {
        List result;

        if (addresses == null) {
            result = Collections.EMPTY_LIST;
        } else {
            result = Arrays.asList(addresses);
        }

        return result;
    }

}
