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

 package net.project.form;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.database.ObjectManager;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Stores and manages a domain of possible values for a FormField.
 */
public class FieldDomain implements java.io.Serializable, IXMLPersistence {
    /** the object_id in the database for this domain. */
    protected String m_domain_id;
    /** an array of FieldDomainValue objects belonging to this domain. */
    private Map domainValues;
    /** the form context */
    protected Form m_form;
    /** the field context */
    protected FormField m_field;

    /** The Name of this domain. */
    protected String m_domain_name;
    /** The type of domain this field represents. */
    protected String m_domain_type;
    /** The user-readable description of this domain. */
    protected String m_domain_description;

    // WARNING  : db access bean is needed here.  Can use Form's or will corrupt stored data in DBBean during nexting queries.
    protected DBBean db = new DBBean();
    protected boolean m_isLoaded = false;

    /**
     * Bean constructor
     */
    public FieldDomain() {
    }


    /**
     * Constructor used for a new object that does not exist in the database
     * yet.
     * 
     * @param form the application context
     */
    public FieldDomain(Form form) {
        m_form = form;
    }


    /**
     * Constructor used for an existing object in the database..
     * 
     * @param form the application context
     * @param domain_id the domain's object_id in the database.
     */
    public FieldDomain(Form form, String domain_id) {
        m_form = form;
        m_domain_id = domain_id;
    }


    public String getID() {
        return m_domain_id;
    }

    public void setID(String value) {
        m_domain_id = value;
    }

    /**
     * set the Form context for this domain
     */
    public void setForm(Form form) {
        m_form = form;
    }

    /**
     * set the FormField context for this domain
     */
    public void setField(FormField field) {
        m_field = field;
    }


    public String getName() {
        return m_domain_name;
    }

    public void setName(String value) {
        m_domain_name = value;
    }


    public String getDescription() {
        return m_domain_description;
    }

    public void setDescription(String value) {
        m_domain_description = value;
    }


    public String getType() {
        return m_domain_type;
    }

    public void setType(String value) {
        m_domain_type = value;
    }

    public List getValues() {
        return getDomainValues();
    }

    public void setValues(List values) {
        domainValues = new LinkedHashMap();

        for (Iterator it = values.iterator(); it.hasNext();) {
            FieldDomainValue fieldDomainValue = (FieldDomainValue) it.next();
            domainValues.put(fieldDomainValue.getID(), fieldDomainValue);
        }
    }


    /**
     * Add a domain value to this domain.
     */
    public void addValue(FieldDomainValue value) {
        if (domainValues == null) {
            domainValues = new LinkedHashMap();
        }

        domainValues.put(value.getID(), value);
    }


    /**
     * Clear all the data from this object.
     */
    public void clear() {
        domainValues = null;
    }


    /**
     * Get the properties and values for this domain from the database.
     * 
     * @param domain_id The domain to retreive properties and values for.
     */
    public void load(String domain_id)
        throws PersistenceException {
        m_domain_id = domain_id;
        load();
    }


    public static Map loadFormFieldDomains(Form form, String classID) throws PersistenceException {
        HashMap fieldDomainCache = new HashMap();
        DBBean db = new DBBean();
        String query_string;
        try {
            query_string =
                "select " +
                "  cdv.domain_id, cdv.domain_value_id, cdv.domain_value_name, " +
                "  cdv.domain_value_desc, cdv.domain_value_seq, cdv.is_default " +
                "from " +
                "  pn_class_domain_values cdv, pn_class_field cf " +
                "where " +
                "  cf.domain_id = cdv.domain_id " +
                "  and cf.class_id = ? " +
                "  and cdv.record_status='A' " +
                "order by " +
                "  domain_id, domain_value_seq ";
            db.prepareStatement(query_string);
            db.pstmt.setString(1, classID);
            db.executePrepared();

            FieldDomain currentDomain = null;
            while (db.result.next()) {

                //Make sure we haven't switched to a new domain value.  If we
                //have set one up.
                String currentDomainID = db.result.getString("domain_id");
                if (currentDomain == null || !currentDomainID.equals(currentDomain.getID())) {
                    currentDomain = new FieldDomain();
                    currentDomain.setID(currentDomainID);
                    currentDomain.setForm(form);

                    fieldDomainCache.put(currentDomainID, currentDomain);
                }


                FieldDomainValue domain_value = new FieldDomainValue(
                    form, // the form context
                    currentDomain, // this FieldDomain
                    db.result.getString("domain_value_id"),
                    db.result.getString("domain_value_name"),
                    db.result.getString("domain_value_desc"),
                    db.result.getInt("domain_value_seq"),
                    db.result.getBoolean("is_default")
                );

                // Resolve those values which are permitted to be tokens in
                // database
                if (PropertyProvider.isToken(domain_value.m_domain_value_name)) {
                    domain_value.m_domain_value_name = PropertyProvider.get(domain_value.m_domain_value_name);
                }

                currentDomain.addValue(domain_value);
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to load domain values.  An " +
                "unexpected error occurred while loading: " + sqle, sqle);
        } finally {
            db.release();
        }

        return fieldDomainCache;
    }

    /**
     * Get the properties and values for this domain from the database.
     */
    public void load() throws PersistenceException {
        FieldDomainValue domain_value = null;
        String query_string;

        if (m_domain_id == null) {
            return;
        }

        // new data comming in, clear out the old domain list.
        this.clear();

        // Get the domain's properties
        query_string = "select domain_name, domain_type, domain_desc from pn_class_domain where domain_id=" + m_domain_id;
        try {
            db.setQuery(query_string);
            db.executeQuery();


            if (!db.result.next()) {
                throw new PersistenceException("Domain could not be loaded");
            }

            // Get the domain's values.  Return the domain properties even if there are no values defined.
            query_string = "select domain_value_id, domain_value_name, domain_value_desc, domain_value_seq, is_default from pn_class_domain_values where domain_id=" +
                m_domain_id + " and record_status='A' order by domain_value_seq";

            db.release();
            db.setQuery(query_string);
            db.executeQuery();

            if (db.result.next()) {
                domainValues = new LinkedHashMap();

                do {
                    domain_value = new FieldDomainValue(
                        m_form, // the form context
                        this, // this FieldDomain
                        db.result.getString(1), // domain_value_id
                        db.result.getString(2), // domain_value_name
                        db.result.getString(3), // domain_value_desc
                        db.result.getInt(4), // domain_value_seq
                        db.result.getBoolean(5)    // is_default
                    );

                    // Resolve those values which are permitted to be tokens in
                    // database
                    if (PropertyProvider.isToken(domain_value.m_domain_value_name)) {
                        domain_value.m_domain_value_name = PropertyProvider.get(domain_value.m_domain_value_name);
                    }

                    domainValues.put(domain_value.getID(), domain_value);

                } while (db.result.next());
            }
            m_isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(FieldDomain.class).error("FieldDomain.load failed " + sqle);
            throw new PersistenceException("failed to load field domain", sqle);
        } finally {
            db.release();
        }

    }


    /**
     * has the FieldDomain been loaded from database persistence?
     */
    public boolean isLoaded() {
        return m_isLoaded;
    }


    /**
     * Store the data for the specified in the database for the specified
     * Domain. The the FieldDomain properties and FieldDomainValue objects will
     * be updated if they exist, otherwise an insert will be done..
     */
    public void store() throws PersistenceException {
        FieldDomainValue domain_value = null;
        int num_values;
        int i;
        String query_string = null;

        try {
            // do an SQL UPDATE.
            // if this domain_id has been stored to the database before.
            if ((m_domain_id != null && !m_domain_id.equals(""))) {
                // update the FieldDomain properties
                query_string = "update pn_class_domain set domain_name=" + DBFormat.varchar2(m_domain_name) + ", domain_type=" + DBFormat.varchar2(m_domain_type) +
                    ", domain_desc=" + DBFormat.varchar2(m_domain_description) + " where domain_id=" + m_domain_id;

                db.setQuery(query_string);
                db.executeQuery();

                // for each domain value.
                if ((domainValues != null) && ((num_values = domainValues.size()) > 0)) {
                    for (i = 0; i < num_values; i++) {
                        domain_value = (FieldDomainValue) getDomainValues().get(i);
                        domain_value.store();
                    }
                }
            }

            // Do SQL INSERT
            // new domain, get a new object_id and insert it.
            else {
                // the system unique object_id for this new FormData object
                m_domain_id = ObjectManager.dbCreateObjectWithPermissions(ObjectType.FORM_DOMAIN, "A", m_form.getSpace().getID(), m_form.getUser().getID());

                // update the FieldDomain properties
                query_string = "insert into pn_class_domain (domain_id, domain_name, domain_type, domain_desc, record_status) values (" +
                    m_domain_id + ", " + DBFormat.varchar2(m_domain_name) + ", " + DBFormat.varchar2(m_domain_type) + ", " + DBFormat.varchar2(m_domain_description) + ", 'A')";

                db.setQuery(query_string);
                db.executeQuery();

                db.release();
                db.setQuery("update pn_class_field set domain_id=" + m_domain_id + " where class_id=" + m_form.getID() + " and space_id=" + m_form.getSpace().getID() + " and field_id=" + m_field.getID());
                db.executeQuery();

                // for each domain value.
                if ((domainValues != null) && ((num_values = domainValues.size()) > 0)) {
                    List domainValues = getDomainValues();
                    for (Iterator it = domainValues.iterator(); it.hasNext();) {
                        FieldDomainValue fieldDomainValue = (FieldDomainValue) it.next();
                        fieldDomainValue.store();
                    }
                }
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(FieldDomain.class).error("FieldDomain.store failed " + sqle);
            throw new PersistenceException("failed to store field domain value", sqle);
        } finally {
            db.release();
        }


    }


    /**
     * <code>alphabetizeDomainValues</code> reorders the domain values in the
     * database according the the alphabetic value of the "domain_value_name"
     * field.
     * 
     * @throws PersistenceException if an error occurs
     */
    public void alphabetizeDomainValues() throws PersistenceException {
        StringBuffer query = new StringBuffer();

        try {
            // Get a list of the domain values in proper order
            query.append("select domain_value_id, domain_value_seq from pn_class_domain_values where domain_id=");
            query.append(getID());
            query.append(" order by domain_value_name");
            db.executeQuery(query.toString());

            /* Do updates to each domain value sequence using a batch update */
            DBBean updateDB = new DBBean();
            try {
                //In order for the batch to work, we need to commit everything in a single transaction
                updateDB.setAutoCommit(false);

                updateDB.createStatement();
                int i = 1;

                // Iterate through the previous resultset, creating an update statement for each.
                while (db.result.next()) {
                    updateDB.stmt.addBatch("update pn_class_domain_values set domain_value_seq = " +
                        i++ + " where domain_value_id = " + db.result.getString("domain_value_id"));
                }

                //We are done, execute and commit
                updateDB.executeBatch();
                updateDB.commit();
            } finally {
                updateDB.release();
                updateDB = null;
            }
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
                //We can't do anything in the database, but let the user know that something went wrong.
            	Logger.getLogger(FieldDomain.class).error("Rollback in FieldDomain.alphabetizeFieldValues() failed.  " + sqle2);
            }

            Logger.getLogger(FieldDomain.class).error("FieldDomain.alphabetizeFieldValues() failed with a sql exception: " + sqle);
            throw new PersistenceException("Unable to alphabetizeFieldValues, an unexpected database error has occurred.", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * <code>reorderSequenceNumbers</code> ensures that the sequence numbers for
     * all of the field domain values are in sequential order (no gaps.)
     * 
     * @throws SQLException if a sql occurs while updating the sequence numbers
     */
    protected void reorderSequenceNumbers() throws SQLException {
        StringBuffer query = new StringBuffer();

        // Get a list of the domain values in proper order
        query.append("select domain_value_id, domain_value_seq from pn_class_domain_values where domain_id=");
        query.append(getID());
        query.append(" order by domain_value_seq");
        db.executeQuery(query.toString());

        /* Do updates to each domain value sequence using a batch update */
        DBBean updateDB = new DBBean();
        try {
            //In order for the batch to work, we need to commit everything in a single transaction
            updateDB.setAutoCommit(false);

            updateDB.createStatement();
            int i = 1;

            // Iterate through the previous resultset, creating an update statement for each.
            while (db.result.next()) {
                updateDB.stmt.addBatch("update pn_class_domain_values set domain_value_seq = " +
                    i++ + " where domain_value_id = " + db.result.getString("domain_value_id"));
            }

            //We are done, execute and commit
            updateDB.executeBatch();
            updateDB.commit();
        } finally {
            updateDB.release();
            updateDB = null;
        }
    }

    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     * 
     * @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<FieldDomain>\n");
        xml.append("<id>" + m_domain_id + "</id>\n");
        xml.append("<name>" + XMLUtils.escape(m_domain_name) + "</name>\n");
        xml.append("<type>" + XMLUtils.escape(m_domain_type) + "</type>\n");
        xml.append("<description>" + XMLUtils.escape(m_domain_description) + "</description>\n");		
        boolean noDefault = true;
        if (domainValues != null) {
            for (Iterator it = domainValues.values().iterator(); it.hasNext();) {
                FieldDomainValue fieldDomainValue = (FieldDomainValue) it.next();
                if (fieldDomainValue.isDefault()){
                	noDefault = false;
                }
                xml.append(fieldDomainValue.getXMLBody());
            }
        }        
        xml.append("<noDefault>" + noDefault + "</noDefault>\n");
        xml.append("</FieldDomain>\n");

        return xml.toString();
    }

    /**
     * Converts the object to XML representation. This method returns the object
     * as XML text.
     * 
     * @return XML representation
     */
    public String getXML() {
        return ("<?xml version=\"1.0\" ?>\n" + getXMLBody());
    }

    protected List getDomainValues() {
        if (domainValues != null) {
            return new ArrayList(domainValues.values());
        } else {
            return null;
        }
    }

    public Map getDomainValueMap() {
        return Collections.unmodifiableMap(domainValues);
    }

    public FieldDomainValue getValue(String id) {
        return (FieldDomainValue)domainValues.get(id);
    }
}
