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

import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.database.ObjectManager;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Stores a single value for a field's domain.
 */
public class FieldDomainValue implements java.io.Serializable,
    java.lang.Comparable {
    // from pn_class_domain_values table
    protected String m_domain_value_id;            // the domain that this value is part of.
    protected String m_domain_value_name;      // the value.
    protected String m_domain_value_desc;       // the description of the value (long name).
    protected int m_domain_value_seq;              // the sequence that this value apprears in the domain list.
    protected boolean m_is_default;                   // if true, this value will be initally selected on a domain list when no data is present.

    protected Form m_form;                              // the form context 
    protected FieldDomain m_domain;               // the FieldDomain context of this domain value.

    protected DBBean db = new DBBean();
    protected boolean m_isLoaded = false;


    /**
     * Bean constructor
     */
    public FieldDomainValue() {
    }


    /**
     * Constructor used for creating a new FieldDomainValue object.
     *
     * @param form the form context.
     * @param domain_id the domain that this value belongs to.
     * @param domain_value_id the object_id of this domain value
     * @param value_name the value.
     * @param seq the sequence that this value should appear on the domain list
     * when presented to the use.  0 first.
     * @param is_default true if the value should be initally selected as a
     * default when the form is newly created.
     */
    public FieldDomainValue(Form form, FieldDomain domain, String domain_value_id, String value_name, String desc, int seq, boolean is_default) {
        m_form = form;
        m_domain = domain;
        m_domain_value_id = domain_value_id;
        m_domain_value_name = value_name;
        m_domain_value_desc = desc;
        m_domain_value_seq = seq;
        m_is_default = is_default;
    }


    public String getID() {
        return m_domain_value_id;
    }

    public void setID(String value) {
        m_domain_value_id = value;
    }


    public String getDomainID() {
        return m_domain.getID();
    }


    public void setDomain(FieldDomain domain) {
        m_domain = domain;
    }


    public String getName() {
        return m_domain_value_name;
    }

    public void setName(String value) {
        m_domain_value_name = value;
    }


    public String getDescription() {
        return m_domain_value_desc;
    }

    public void setDescription(String value) {
        m_domain_value_desc = value;
    }


    public int getSequence() {
        return m_domain_value_seq;
    }

    public void setSequence(int value) {
        m_domain_value_seq = value;
    }


    public boolean isDefault() {
        return m_is_default;
    }

    public void setIsDefault(boolean value) {
        m_is_default = value;
    }


    public String defaultChecked() {
        if (m_is_default) {
            return "checked";
        } else {
            return "";
        }
    }


    /**
     * <code>compareTo</code> implements "natural sorting" for this
     * FieldDomainValue, it allows Collections.sort() to know what order these
     * values should be in under normal circumstances.
     *
     * @param fieldDomainValue an <code>Object</code> value that is going to be
     * compared to.
     * @return an <code>int</code> value.  This is defined by the Comparable
     *         interface, 0 means that the two are equal, >0 Means this value is
     *         greater, <0 means the other is greater.
     */
    public int compareTo(Object fieldDomainValue) {
        int otherSeq = ((FieldDomainValue) fieldDomainValue).getSequence();

        if (m_domain_value_seq == otherSeq) {
            return 0;
        } else if (m_domain_value_seq > otherSeq) {
            return 1;
        } else {
            return -1;
        }
    }


    /**
     * Clear all the data from this object.
     */
    public void clear() {
        m_domain = null;
        m_domain_value_id = null;
        m_domain_value_name = null;
        m_domain_value_desc = null;
        m_domain_value_seq = 0;
        m_is_default = false;
    }


    /**
     * Store the domain value and it's properties.
     */
    public void store()
        throws PersistenceException {
        String query_string = null;

        // do an SQL UPDATE.
        // if this domain_id has been stored to the database before.
        if ((m_domain_value_id != null && !m_domain_value_id.equals(""))) {
            // update the FieldDomain properties
            query_string = "update pn_class_domain_values set domain_value_name=" + DBFormat.varchar2(m_domain_value_name) +
                ", domain_value_desc=" + DBFormat.varchar2(m_domain_value_desc) + ", domain_value_seq=" + m_domain_value_seq +
                ", is_default=" + DBFormat.bool(m_is_default) + " where domain_id=" + m_domain.getID() + " and domain_value_id=" + m_domain_value_id;

            try {
                db.setQuery(query_string);
                db.executeQuery();
            } catch (SQLException sqle) {
            	Logger.getLogger(FieldDomainValue.class).error("FieldDomainValue.store failed " + sqle);
                throw new PersistenceException("failed to store field domain value", sqle);
            } finally {
                db.release();
            }
        }

        // Do SQL INSERT
        // new domain, get a new object_id and insert it.
        else {
            // the system unique object_id for this new FormData object
            ObjectManager objectManager = new ObjectManager();
            m_domain_value_id = ObjectManager.dbCreateObject(ObjectType.FORM_DOMAIN_VALUE, "A");

            try {
                // If the field doesn't have a valid sequence value, assign it one automatically
                if (getSequence() < 1) {
                    db.executeQuery("select max(domain_value_seq)+1 domain_value_seq " +
                        "from pn_class_domain_values where domain_id = " +
                        getDomainID());

                    if ((db.result.next()) && (db.result.getInt(1) != 0)) {
                        setSequence(db.result.getInt(1));
                    } else {
                        setSequence(1);
                    }
                }

                // update the FieldDomain properties
                query_string = "insert into pn_class_domain_values (domain_id, domain_value_id, domain_value_name, domain_value_desc, domain_value_seq, is_default, record_status) values (" +
                    m_domain.getID() + ", " + m_domain_value_id + ", " + DBFormat.varchar2(m_domain_value_name) + ", " + DBFormat.varchar2(m_domain_value_desc) + ", " +
                    m_domain_value_seq + ", " + DBFormat.bool(m_is_default) + ", 'A' )";

                db.setQuery(query_string);
                db.executeQuery();
            } catch (SQLException sqle) {
            	Logger.getLogger(FieldDomainValue.class).error("FieldDomainValue.store failed " + sqle);
                throw new PersistenceException("failed to store field domain value", sqle);
            } finally {
                db.release();
            }

        }

    }


    /**
     * <code>remove</code> deletes the current domain value from the database.
     * It also cleans up the sequence numbers of the remaining FieldDomainValues
     * to ensure that there aren't any gaps in the order.  (Gaps wouldn't really
     * be too much of a problem, but they are a bit untidy.
     */
    public void remove()
        // throws PersistenceException
    {
        try {
            //First, remove this value
            db.setQuery("delete from pn_class_domain_values where domain_value_id=" + m_domain_value_id);
            db.executeQuery();

            //Second, update the sequence value for the other values to remove any gaps we might have created
            m_domain.reorderSequenceNumbers();
        } catch (SQLException sqle) {
        	Logger.getLogger(FieldDomainValue.class).error("FieldDomainValue.remove failed " + sqle);
        } catch (NullPointerException npe) {
        	Logger.getLogger(FieldDomainValue.class).error("FieldDomainValue.remove failed " + npe);        	
        } finally {
            db.release();
    	}
    }

    /**
     * <code>promote</code> makes this FieldDomainValue appear higher in the
     * list of options.  It does this by increasing the "sequence" value of this
     * field while decreasing the sequence value of all the other
     * FieldDomainValues for this domain id.
     *
     * @throws PersistenceException if a database error occurs while trying to
     * update the sequence of domain values.
     */
    public void promote() throws PersistenceException {
        /* First, check to make sure that this value isn't already the highest sequence allowed (1), if it
           the highest, we'll just exit without doing any update. */
        if (getSequence() <= 1) {
            return;
        }

        DBBean db = new DBBean();

        try {
            /* Second, we need to increase the domain sequence number of the field domain value that is
               higher than we are.  Eventually, it will have this FieldDomainValues' sequence and we will
               have its sequence. */
            StringBuffer query = new StringBuffer();
            query.append("update pn_class_domain_values set domain_value_seq=");
            query.append(getSequence());
            query.append(" where domain_value_seq=");
            query.append(getSequence() - 1);
            query.append("  and domain_id=");
            query.append(this.getDomainID());

            //Update the domain value with the sequence value higher than ours.
            db.executeQuery(query.toString());

            /* Finally, we update this FieldDomainValue to have a higher sequence value */
                        
            //Now we need to construct the query to make this domain value have a lower precedence
            query = new StringBuffer();
            query.append("update pn_class_domain_values set domain_value_seq=");
            query.append(getSequence() - 1);
            query.append(" where domain_value_id=");
            query.append(this.getID());

            db.executeQuery(query.toString());

            //Update our sequence to reflect that it is now lower.
            setSequence(getSequence() - 1);
        } catch (SQLException sqle) {
            //Try to rollback the update
            try {
                db.rollback();
            } catch (SQLException sqle2) {  /*We can't do anything about this*/
            }

            Logger.getLogger(FieldDomainValue.class).error("FieldDomainValue.promote() failed due to a database error: "
                + sqle);
            throw new PersistenceException("An unexpected database error occurred while trying to " +
                "promote this menu option.", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * <code>demote()</code> makes this field domain value appear higher in the
     * selection list when viewed anywhere in the application.
     *
     * @throws PersistenceException if an error occurs
     */
    public void demote() throws PersistenceException {
        DBBean db = new DBBean();

        try {
            /* First, check to make sure that we aren't trying to demote something that is already at
               the lowest level */
            StringBuffer query = new StringBuffer();
            query.append("select max(domain_value_seq) max_sequence from pn_class_domain_values ");
            query.append("where domain_id = ");
            query.append(this.getDomainID());
            db.executeQuery(query.toString());

            //Check to make sure that we got return values.  (If not, this one probably hasn't been saved yet.)
            if (!db.result.next()) {
                throw new PersistenceException("Cannot demote a field value that has not yet been committed to the database;");
            }

            /* Checking to make sure we aren't demoting something at the lowest level.  If we are, just
               return.  (No need to raise an error, just don't do anything.) */
            if (db.result.getInt("max_sequence") == getSequence()) {
                return;
            }

            /* Second, we need to increase the domain sequence number of the field domain value that is
               lower than we are.  Eventually, it will have this FieldDomainValues' sequence and we will
               have its sequence. */
            query = new StringBuffer();
            query.append("update pn_class_domain_values set domain_value_seq=");
            query.append(getSequence());
            query.append(" where domain_value_seq=");
            query.append(getSequence() + 1);
            query.append("  and domain_id=");
            query.append(this.getDomainID());

            //Update the domain value with the sequence value higher than ours.
            db.executeQuery(query.toString());

            /* Finally, we update this FieldDomainValue to have a lower sequence value */
                        
            //Now we need to construct the query to make this domain value have a lower precedence
            query = new StringBuffer();
            query.append("update pn_class_domain_values set domain_value_seq=");
            query.append(getSequence() + 1);
            query.append(" where domain_value_id=");
            query.append(this.getID());

            db.executeQuery(query.toString());

            //Update our sequence to reflect that it is now lower.
            setSequence(getSequence() - 1);
        } catch (SQLException sqle) {
            //Try to rollback the update
            try {
                db.rollback();
            } catch (SQLException sqle2) {  /*We can't do anything about this*/
            }

            Logger.getLogger(FieldDomainValue.class).error("FieldDomainValue.demote() failed due to a database error: "
                + sqle);
            throw new PersistenceException("An unexpected database error occurred while trying to " +
                "demote this menu option.", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Get the properties and values for this domain from the database.
     */
    public void load()
        throws PersistenceException {

        String query_string = null;

        try {

            if (m_domain_value_id == null) {
                throw new PersistenceException("Domain  Value could not be loaded");
            }

            // Get the domain's values.  Return the domain properties even if there are no values defined.
            query_string = "select domain_value_name, domain_value_desc, domain_value_seq, is_default from pn_class_domain_values where domain_value_id=" +
                m_domain_value_id + " and record_status='A' order by domain_value_seq";

            db.setQuery(query_string);
            db.executeQuery();

            if (db.result.next()) {

                if (PropertyProvider.isToken(db.result.getString(1))) {
                    this.m_domain_value_name = PropertyProvider.get(db.result.getString(1));
                } else {
                    this.m_domain_value_name = db.result.getString(1);
                }

                this.m_domain_value_desc = db.result.getString(2);
                this.m_domain_value_seq = db.result.getInt(3);
                this.m_is_default = db.result.getBoolean(4);

                this.m_isLoaded = true;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(FieldDomainValue.class).error("FieldDomainValue.load failed " + sqle);
            throw new PersistenceException("failed to load field domain", sqle);
        } finally {
            db.release();
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

        xml.append("<FieldDomainValue>\n");
        xml.append("<id>" + m_domain_value_id + "</id>\n");
        xml.append("<name>" + XMLUtils.escape(m_domain_value_name) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(m_domain_value_desc) + "</description>\n");
        xml.append("<sequence>" + m_domain_value_seq + "</sequence>\n");
        if (m_is_default) {
            xml.append("<default>true</default>");
        } else {
            xml.append("<default>false</default>");
        }
        xml.append("</FieldDomainValue>\n");

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


}

