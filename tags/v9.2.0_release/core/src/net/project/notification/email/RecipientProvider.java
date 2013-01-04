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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Provides methods for manipulating recipients for sending an email.
 */
public class RecipientProvider implements java.io.Serializable {

    /**
     * The available recipients.
     * The key is a recipient ID, the value is a RecipientEntry.
     */
    private HashMap available = new HashMap();

    /**
     * Indicates whether to sort the available recipients.
     */
    private boolean isSorted = false;

    /**
     * Indicates the number of available recipients.
     * @return the number of available recipients.
     */
    public int availableCount() {
        return available.size();
    }

    /**
     * Adds all the recipients in the specified collection to this provider.
     * @param recipients the recipients to add; each element must be an
     * <code>{@link IRecipient}</code>.
     */
    public void addAvailable(Collection recipients) {

        Iterator it = recipients.iterator();
        while (it.hasNext()) {
            IRecipient recipient = (IRecipient) it.next();
            addAvailable(recipient);
        }

    }


    /**
     * Adds the recipient to this provider.
     * @param recipient the recipient to add
     */
    public void addAvailable(IRecipient recipient) {
        RecipientEntry entry = new RecipientEntry();

        entry.recipient = recipient;
        entry.isSelected = false;

        // Update available map
        // key is recipient ID, value is RecipientEntry
        this.available.put(entry.recipient.getID(), entry);
    }


    /**
     * Sets the recipient to be selected.
     * @param recipient the recipient to be selected
     */
    public void setSelected(IRecipient recipient) {
        setSelected(recipient.getID());
    }


    /**
     * Sets the recipient with the selected id to be selected.
     * @param recipientID the id of the recipient to be selected
     */
    public void setSelected(String recipientID) {
        RecipientEntry entry = (RecipientEntry) this.available.get(recipientID);
        if (entry != null) {
            entry.isSelected = true;
        }
    }


    /**
     * Sets the recipients whose IDs are specified to be selected.
     * @param recipientIDs the ids of the recipients to select
     */
    public void setSelectedRecipientsArray(String[] recipientIDs) {

        // For each id, locate its entry and set it to be selected
        for (int i = 0; i < recipientIDs.length; i++) {
            setSelected(recipientIDs[i]);
        }

    }


    /**
     * Returns the <code>{@link IRecipient}</code>s that are currently selected.
     * @return the collection of selected recipients
     */
    public RecipientCollection getSelectedRecipients() {
        RecipientCollection recipients = new RecipientCollection();

        // Iterate over all values in the available map, adding the recipient
        // to the new collection
        Iterator it = this.available.values().iterator();
        while (it.hasNext()) {
            RecipientEntry entry = (RecipientEntry) it.next();
            if (entry.isSelected) {
                recipients.add(entry.recipient);
            }
        }

        return recipients;
    }


    /**
     * Returns the presentation for selecting recipients.
     * This consists of a series of html &lt;option&gt; elements.
     * @return the HTML option elements
     */
    public String getSelectionOptionPresentation() {
        StringBuffer pres = new StringBuffer();
        ArrayList options = new ArrayList();

        // Sort the options if necessary
        options.addAll(this.available.values());
        if (isSorted()) {
            Collections.sort(options, new RecipientNameComparator());
        }

        Iterator it = options.iterator();
        while (it.hasNext()) {
            RecipientEntry entry = (RecipientEntry) it.next();

            pres.append("<option value=\"" + entry.recipient.getID() + "\" ");
            if (entry.isSelected) {
                pres.append("selected ");
            }
            pres.append(">");
            pres.append(entry.recipient.getRecipientName());
            pres.append("</option>\n");
        }

        return pres.toString();
    }


    /**
     * Indicates whether to sort available recipients (by name).
     */
    public void setSorted() {
        this.isSorted = true;
    }


    /**
     * Indicates whether the available recipient list is sorted.
     * @return true if it is sorted; false otherwise
     * @see #setSorted
     */
    public boolean isSorted() {
        return this.isSorted;
    }


    /**
     * RecipientEntry used for selecting / deselecting recipients.
     */
    private static class RecipientEntry {
        /** The recipient in this entry. */
        public IRecipient recipient = null;
        /** Indicates whether the entry is selected. */
        public boolean isSelected = false;
    }

    /**
     * Provides implementation for comparing two recipient entries based
     * on their recipient names.
     */
    private static class RecipientNameComparator implements java.util.Comparator {

        /**
         * Compares two RecipientEntry objects.
         *
         * @param o1 the first recipient entry to be compared.
         * @param o2 the second recipient entry to be compared.
         * @return result of comparing the recipient names
         * @throws ClassCastException if the arguments' types prevent them from
         * 	       being compared by this Comparator.
         * @see java.lang.String#compareTo
         */
        public int compare(Object o1, Object o2) {
            RecipientEntry entry1 = (RecipientEntry) o1;
            RecipientEntry entry2 = (RecipientEntry) o2;

            return entry1.recipient.getRecipientName().compareTo(entry2.recipient.getRecipientName());
        }

        public boolean equals(Object obj) {
            if (obj instanceof RecipientNameComparator) {
                return true;
            }
            return false;
        }

    }
}
