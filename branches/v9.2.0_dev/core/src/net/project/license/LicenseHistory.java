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
package net.project.license;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;

/**
 * Provides a history of licenses for a single person, which outlines which
 * licenses they have been associated with.
 *
 * @author Tim Morrow
 * @since Gecko Update 2
 */
public class LicenseHistory implements net.project.persistence.IXMLPersistence {

    //
    // Static members
    //

    /**
     * Indicates whether the specified person has had a trial license.
     * @param personID the id of the person who's license are to be checked
     * @return true if the user's current license is a trial license or
     * if a previous license has been a trial licene; false if the user has
     * never had a trial license
     */
    public static boolean hasHadTrialLicense(String personID) throws LicenseException {

        boolean hasHadTrialLicense = false;

        // Build query to locate trial license
        // Joins license history to license for person and trial license flag
        StringBuffer selectQuery = new StringBuffer();
        selectQuery.append("select 1 ");
        selectQuery.append("from pn_license_person_history hist, pn_license lic ");
        selectQuery.append("where lic.license_id = hist.license_id ");
        selectQuery.append("and hist.person_id = ? ");
        selectQuery.append("and lic.is_trial = ? ");

        DBBean db = new DBBean();
        try {
            int index = 0;
            db.prepareStatement(selectQuery.toString());
            db.pstmt.setString(++index, personID);
            db.pstmt.setString(++index, String.valueOf(net.project.util.Conversion.booleanToInt(true)));
            db.executePrepared();

            if (db.result.next()) {
                // Found row, user has had trial license
                hasHadTrialLicense = true;
            
            } else {
                hasHadTrialLicense = false;
            
            }

        } catch (SQLException sqle) {
            throw new LicenseException("Check trial license operation faild: " + sqle, sqle);
        } finally {
            db.release();
        }

        return hasHadTrialLicense;
    }

    /**
     * Creates an entry in license history.
     * @param license the license being assigned to a person
     * @param person the person to whom the license is assigned
     * @throws PersistenceException if there is a problem creating the entry
     */
    public static void createEntry(DBBean db, License license, Person person) 
            throws PersistenceException {
        
        try {
            // Create history entry for NEW license
            StringBuffer insertHistoryQuery = new StringBuffer();
            insertHistoryQuery.append("insert into pn_license_person_history ");
            insertHistoryQuery.append("(history_id, license_id, person_id, created_datetime) ");
            insertHistoryQuery.append("values (?, ?, ?, ?) ");
            
            // Generate unique id
            String historyID = new net.project.database.ObjectManager().getNewObjectID();
            
            int index = 0;
            db.prepareStatement(insertHistoryQuery.toString());
            db.pstmt.setString(++index, historyID);
            db.pstmt.setString(++index, license.getID());
            db.pstmt.setString(++index, person.getID());
            db.pstmt.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
            db.executePrepared();
        
        } catch (SQLException sqle) {
            throw new PersistenceException("Create license history entry operation faild: " + sqle, sqle);

        }

    }

    //
    // Instance members
    //

    /** The person to whom this license history pertains. */
    private Person person = null;

    /**
     * List of {@link LicenseHistory.HistoryEntry} entries.
     */
    private List historyList = new ArrayList();

    /** 
     * Indiactes whether to exclude the user's current license from the history
     * list.
     */
    private boolean isCurrentLicenseExcluded = false;

    /**
     * Creates an empty LicenseHistory.
     */
    public LicenseHistory() {
        // Do nothing
    }

    /**
     * Sets the person to whom this history belongs and loads the history.
     * @param person the person for the history
     * @throws PersistenceException if there is a problem loading the
     * history for the person
     */
    public void setPerson(Person person) throws PersistenceException {
        this.person = person;
        load();
    }

    /**
     * Indicates whether the person has any history entries.
     * Note that since the history includes even the current license,
     * a user with a license should have at lease one history entry.
     * Assumes that the license history has been loaded
     * @return true if the user has one history entry
     * @see #setPerson
     */
    public boolean hasHistory() {
        return (this.historyList.size() > 0);
    }

    /**
     * Indicates whether to exclude the user's current license from this
     * collection.
     * Normally the current license is included in the history.
     * @param isCurrentLicenseExcluded true if the current license should
     * be excluded
     */
    public void setCurrentLicenseExcluded(boolean isCurrentLicenseExcluded) {
        this.isCurrentLicenseExcluded = isCurrentLicenseExcluded;
    }

    /**
     * Loads the license history for the current person.
     * Assumed {@link #setPerson} has been called prior to this method.
     * @throws PersistenceException if there is a problem loading; when this
     * occurs, no history entries are available
     * @throws IllegalStateException if there is no current person available
     */
    private void load() throws PersistenceException {
        
        if (this.person == null) {
            throw new IllegalStateException("Person is null");
        }

        DBBean db = new DBBean();

        // Load the history list
        try {
            // Query to load all history rows for a person
            StringBuffer loadQuery = new StringBuffer();
            loadQuery.append("select lh.history_id, lh.license_id, lh.person_id, lh.created_datetime ");
            loadQuery.append("from pn_license_person_history lh ");
            loadQuery.append("where lh.person_id = ? ");

            // Append license exclusion filter, if necessary
            if (this.isCurrentLicenseExcluded) {
                loadQuery.append("and not exists (select 1 from pn_person_has_license phl ");
                loadQuery.append("where phl.person_id = lh.person_id and phl.license_id = lh.license_id) ");
            }

            // Perform the query
            int index = 0;
            db.prepareStatement(loadQuery.toString());
            db.pstmt.setString(++index, this.person.getID());
            db.executePrepared();

            // Iterate over result set, building a list of history entries
            List entryList = new ArrayList();
            while (db.result.next()) {
                HistoryEntry entry = new HistoryEntry();
                entry.setHistoryID(db.result.getString("history_id"));
                entry.setLicenseID(db.result.getString("license_id"));
                entry.setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                entryList.add(entry);
            }

            // Now populate all the license objects in the history entries
            populateWithLicense(entryList);

            // Now that everything is successful, add to the history list
            this.historyList.addAll(entryList);
        
        } catch (SQLException sqle) {
            throw new PersistenceException("License History load operation failed: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    private net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("LicenseHistory");

            // Add person xml
            doc.startElement("Person");
            doc.addXMLString(this.person.getXMLBody());
            doc.endElement();

            // Add all history entries
            doc.startElement("HistoryEntryCollection");
            for (Iterator it = this.historyList.iterator(); it.hasNext(); ) {
                HistoryEntry nextEntry = (HistoryEntry) it.next();
                doc.startElement("HistoryEntry");
                doc.addElement(nextEntry.getLicense().getXMLDocument());
                doc.addElement("CreatedDatetime", nextEntry.getCreatedDatetime());
                doc.endElement();
            }
            doc.endElement();
        
            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Problem building document
            // Return empty document
        
        }

        return doc;
    }

    /**
     * Populates all the <code>HistoryEntry</code> elements with the license
     * that each entry represents.
     * Note: It is possible that some licenses in the history are hidden, if
     * some problem occurs loading the license (other than a persistence problem)
     * @param entryList the collection of history entries to populate; this
     * is modified by this routine.  After calling, each HistoryEntry in the
     * collection will have a license available through {@link HistoryEntry#getLicense}
     * @throws PersistenceException if there is a problem loading any license
     */
    private void populateWithLicense(Collection entryList) throws PersistenceException {

        // 02/15/2002 - Tim
        // Extremely poor implementation that does an individual load
        // Mitigated by the fact that a user should have reasonably few
        // licenses in their history.  If a user switches licenses 20 times
        // (what are the chances of that?) then 20 loads will be performed
        
        for (Iterator it = entryList.iterator(); it.hasNext(); ) {
            HistoryEntry nextEntry = (HistoryEntry) it.next();
            
            try {
                License license = new License();
                license.setID(nextEntry.getLicenseID());
                license.load();
                nextEntry.setLicense(license);

            } catch (InvalidLicenseCertificateException e) {
                // License certificate is corrupt
                // Drop the license so we can still display the history
                it.remove();

            } catch (LicenseKeyMismatchException e) {
                // Means that someone has tweaked with the License definition
                // We should simply drop this history entry;
                // If we threw an exception, it would become impossible to see
                // the history
                it.remove();
            }

        }

    }

    //
    // Nested top-level classes
    //

    /**
     * Provides simple structure for maintaining license history entries.
     */
    private static class HistoryEntry {

        private String historyID = null;
        private String licenseID = null;
        private License license = null;
        private Date createdDatetime = null;

        HistoryEntry() {
            // Do nothing
        }

        void setHistoryID(String historyID) {
            this.historyID = historyID;
        }

        String getHistoryID() {
            return this.historyID;
        }

        void setLicenseID(String licenseID) {
            this.licenseID = licenseID;
        }

        String getLicenseID() {
            return this.licenseID;
        }

        void setCreatedDatetime(Date date) {
            this.createdDatetime = date;
        }

        Date getCreatedDatetime() {
            return this.createdDatetime;
        }

        void setLicense(License license) {
            this.license = license;
        }

        License getLicense() {
            return this.license;
        }

    }

}
