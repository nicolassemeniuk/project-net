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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Provides a collection of <code>{@link IRecipient}s</code>.<br>
 * Usage: <code>recipeints.getEmailAddresses()</code> returns a collection of string
 * email addresses that may be used for addressing an email.<br>
 * <br>
 * For example:<br>
 * (group and person are a {@link net.project.security.group.Group} and
 * {@link net.project.resource.Person} respectively).
 * <code><pre>
 * RecipientCollection recipients = new RecipientCollection();
 * recipients.add(person);
 * recipients.add(group);
 * Email email = new Email();
 * email.setTo(new ArrayList(recipients.getEmailAddresses();
 * email.setSubject("Some Subject");
 * email.setBody("Some body");
 * email.send();
 * </pre></code>
 */
public class RecipientCollection
        extends ArrayList
        implements java.io.Serializable {

    /**
     * Returns a formatted display of each recipient's name separated by ";".
     * The number of names in the formatted display is equal to the number of
     * elements in this collection.  That is, it does not return the individual
     * names of members of a compount recipient.
     * @return the formatted names
     */
    public String getNamesFormatted() {
        return getNamesFormatted(";");
    }


    /**
     * Returns the names of all the recipients in this collection formatted
     * with the specified delimeter.
     * @param delimeter the delimeter to use to separate recipient names
     * @return the formatted names
     */
    public String getNamesFormatted(String delimeter) {
        StringBuffer formattedNames = new StringBuffer();
        Iterator it = null;

        it = iterator();
        while (it.hasNext()) {

            if (formattedNames.length() > 0) {
                formattedNames.append(delimeter);
            }

            formattedNames.append(((IRecipient) it.next()).getRecipientName());
        }

        return formattedNames.toString();
    }


    /**
     * Returns all email addresses of all recipients separated by ";".
     * Any compound recipients will have had their email addresses expanded.
     * @return all email addresses of all recipients, including members of
     * compound recipients.
     */
    public String getEmailAddressesFormatted() {
        return getEmailAddressesFormatted(";");
    }


    /**
     * Returns all email addresses of all recipients separated by the specified
     * delimeter.
     * Any compound recipients will have had their email addresses expanded.
     * @param delimeter the delimeter to use to separate addresses
     * @return all email addresses of all recipients, including members of
     * compound recipients.
     */
    public String getEmailAddressesFormatted(String delimeter) {
        StringBuffer formattedAddresses = new StringBuffer();
        Iterator it = null;

        it = getEmailAddresses().iterator();
        while (it.hasNext()) {

            if (formattedAddresses.length() > 0) {
                formattedAddresses.append(delimeter);
            }

            formattedAddresses.append((String) it.next());
        }

        return formattedAddresses.toString();
    }


    /**
     * Returns all email addresses from this recipient collection, with
     * duplicates removed.
     * Each email address is a string.  Note that the email address is not
     * necessarily as simple as "user@domain.com".  It might look like
     * "Firstname Lastname &lt;user@domain.com&gt;".
     * @return collection of unique email addresses, including members of
     * compound recipients.
     * @see IRecipient#getRecipientEmailAddresses
     */
    public Collection getEmailAddresses() {
        ArrayList emailAddresses = new ArrayList();
        Iterator it = null;

        it = iterator();
        while (it.hasNext()) {
            emailAddresses.addAll(((IRecipient) it.next()).getRecipientEmailAddresses());
        }

        return removeDuplicates(emailAddresses);
    }


    /**
     * Returns a copy of the specified collection with all duplicate items
     * removed.
     * @param c the collection from which to remove duplicates
     * @return the new collection
     */
    private Collection removeDuplicates(Collection c) {
        ArrayList newList = new ArrayList();
        Iterator it = null;

        it = c.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (!newList.contains(o)) {
                newList.add(o);
            }
        }

        return newList;
    }
}
