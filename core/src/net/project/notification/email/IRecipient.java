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
package net.project.notification.email;

/**
 * Represents a single email recipient.
 * An Email recipient has a name used for display purposes and one or more email addresses.
 * Note that most recipients will have a single email address; in that case
 * <code>{@link #getRecipientEmailAddresses}</code> should return a one-element
 * collection where that element is the email adress.
 * However, some recipients may represent an aggregation of other recipients; in that case
 * <code>{@link #getRecipientEmailAddresses}</code> should return a collection
 * of email addresses.
 */
public interface IRecipient {

    /**
     * Returns the id of this recipient.
     * This must be globally unique.
     * @return the id
     */
    public String getID();


    /**
     * Returns the name of this recipient.
     * @return the name
     */
    public String getRecipientName();


    /**
     * Returns the email addresses of this recipient where each address is a
     * string.  The email address may be any valid address as per RFC-822.<br>
     * For example <code>"user@domain.com"</code> or <br>
     * <code>"Firstname Lastname &lt;user@domain.com&gt;"</code>.<br>
     * This should return all email addresses for this recipient.
     * @return the email addresses
     */
    public java.util.Collection getRecipientEmailAddresses();

}
