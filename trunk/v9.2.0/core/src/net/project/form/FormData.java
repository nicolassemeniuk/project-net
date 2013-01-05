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
|   $Revision: 20854 $
|       $Date: 2010-05-14 10:47:54 -0300 (vie, 14 may 2010) $
|     $Author: ritesh $
|
+----------------------------------------------------------------------*/
package net.project.form;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.project.base.DefaultDirectory;
import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.ObjectType;
import net.project.base.RecordStatus;
import net.project.base.URLFactory;
import net.project.base.attribute.AttributeCollection;
import net.project.base.attribute.AttributeStoreException;
import net.project.base.attribute.BooleanAttribute;
import net.project.base.attribute.DateAttribute;
import net.project.base.attribute.DateAttributeValue;
import net.project.base.attribute.DomainListAttribute;
import net.project.base.attribute.DomainValue;
import net.project.base.attribute.IAttribute;
import net.project.base.attribute.IAttributeReadable;
import net.project.base.attribute.IAttributeValue;
import net.project.base.attribute.IAttributeWriteable;
import net.project.base.attribute.IllegalAttributeValueException;
import net.project.base.attribute.NumberAttribute;
import net.project.base.attribute.NumberAttributeValue;
import net.project.base.attribute.PersonListAttribute;
import net.project.base.attribute.TextAttribute;
import net.project.base.attribute.TextAttributeValue;
import net.project.base.attribute.UnimportableAttribute;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.database.DatabaseUtils;
import net.project.events.EventType;
import net.project.link.ILinkableObject;
import net.project.notification.DeliveryException;
import net.project.notification.EventCodes;
import net.project.notification.NotificationException;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.schedule.ScheduleEntry;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.Conversion;
import net.project.util.DateFormat;
import net.project.util.NumberFormat;
import net.project.util.Validator;
import net.project.workflow.EnvelopeException;
import net.project.workflow.EnvelopeManager;
import net.project.workflow.EnvelopePriority;
import net.project.workflow.IWorkflowable;
import net.project.workflow.Strictness;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Stores data for all fields of a single Form instance (FieldData) in a HashMap
 * keyed by each field's database storage name (FormField.getSQLName()).
 *
 * @author Roger
 * @author Tim
 */
public class FormData extends HashMap implements IJDBCPersistence, IXMLPersistence, IWorkflowable, ILinkableObject, IAttributeWriteable, IAttributeReadable {

    /** the Form this data belongs to. */
    protected Form m_form = null;

    /** the object_id of the data we will store. */
    protected String m_data_object_id = null;

    /** the version_id of the data */
    protected String m_version_id = null;

    /** the user-readable sequence number of this data object. */
    protected int m_sequence_num = 0;

    protected String m_previous_version_id = null;
    protected String m_create_person_id = null;
    protected String m_create_person_display_name = null;
    protected String m_creator_email = null;
    protected String m_modify_person_id = null;
    protected String m_modify_person_display_name = null;
    protected Date m_date_created = null;
    protected Date m_date_modified = null;

    /** the last data record change date; used for update conflict detection. */
    protected Date m_crc = null;

    private RecordStatus recordStatus = null;

    /** is the data the most current data record */
    private boolean m_is_current = false;

    /** the number of rows in this FieldData object which is equal to the max number of data items for any one field */
    private int m_numberRows = 0;


    // db access bean
    private DBBean db = new DBBean();
    private boolean m_isLoaded = false;

    // The current space context
    private Space currentSpace = null;

    //Hashmap for Attributes to FormField Mapping
    private HashMap hashAtrField = new HashMap();

    private boolean isNewFormData = false;

    private boolean hasActiveEnvelope = false;

    /**
     * flag that defines if recored is added through external form access or not 
     */
    private boolean externalRecord = false;
    
    /** space name where form data created */
    protected String m_form_data_space_name;
    
    /**
     * Constructor.
     */
    public FormData() {
        super();
    }

    /**
     * Create an empty FormData object.
     *
     * @param form the form context.
     */
    public FormData(Form form) {
        super();
        m_form = form;
    }

    /**
     * Create a new FormData object.
     *
     * @param form the form context.
     * @param data_object_id the id of the FormData object for database storage.
     */
    public FormData(Form form, String data_object_id) {
        super();
        m_form = form;
        m_data_object_id = data_object_id;
    }
    
    
    
    public boolean isExternalRecord() {
		return externalRecord;
	}

	public void setExternalRecord(boolean externalRecord) {
		this.externalRecord = externalRecord;
	}

	public boolean isHasActiveEnvelope() {
		return hasActiveEnvelope;
	}

	public void setHasActiveEnvelope(boolean hasActiveEnvelope) {
		this.hasActiveEnvelope = hasActiveEnvelope;
	}

	/**
     * Sets the current space context.
     * This is required before data can be stored.
     * @param space the current space
     */
    public void setCurrentSpace(Space space) {
        this.currentSpace = space;
    }

    /**
     * Returns the current space context.
     * @return the current space or null if no space has been set
     */
    private Space getCurrentSpace() {
        return this.currentSpace;
    }


    /** set the object_id of the FormData item */
    public void setID(String id) {
        m_data_object_id = id;
    }


    /** get the object_id of the FormData item */
    public String getID() {
        return m_data_object_id;
    }


    /*  Get the number of data rows this object contains */
    public void setNumDataRows(int rows) {
        m_numberRows = rows;
    }

    /*  Set the number of data rows this object contains */
    public int getNumDataRows() {
        return m_numberRows;
    }


    /**
     Set the Form context for this data.
     The form that owns and presents this data.
     */
    public void setForm(Form form) {
        m_form = form;
    }

    /**
     * Get the form context for this data.
     */
    public Form getForm() {
        return m_form;
    }


    public void setVersionID(String versionID) {
        m_version_id = versionID;
    }

    public String getVersionID() {
        return m_version_id;
    }


    public void setSeqNum(int seqNum) {
        m_sequence_num = seqNum;
    }

    public int getSeqNum() {
        return m_sequence_num;
    }


    public void setCRC(Date crc) {
        m_crc = crc;
    }

    public Date getCRC() {
        return m_crc;
    }

    public RecordStatus getRecordStatus() {
        return this.recordStatus;
    }

    public void setIsCurrent(boolean isCurrent) {
        m_is_current = isCurrent;
    }

    public boolean getIsCurrent() {
        return m_is_current;
    }

    
    
    /**
	 * @return the m_creator_email
	 */
	public String getCreatorEmail() {
		return m_creator_email;
	}

	/**
	 * @param m_creator_email the m_creator_email to set
	 */
	public void setCreatorEmail(String creatorEmail) {
		this.m_creator_email = creatorEmail;
	}

	/** Clear the data */
    public void clear() {
        super.clear();
        m_numberRows = 0;

    }


    /**
     Get the field data for the specified field.
     */
    public FieldData getFieldData(FormField field) {
        if (field != null)
            return (FieldData)this.get(field.getSQLName());
        else
            return null;
    }

    /**
     Get the modification string for this form data item containing persons and dates created and last modified.
     */
    public String getModificationString() {
        StringBuffer sb = new StringBuffer();
        DefaultDirectory directory = new DefaultDirectory();
        net.project.util.DateFormat formatter = m_form.getUser().getDateFormatter();

        if ((m_create_person_id != null) && (m_modify_person_id != null) && (m_date_created != null) && (m_date_modified != null)) {
        	String creator = m_create_person_id.equals("-1") ? m_creator_email : directory.getDisplayNameByID(m_create_person_id);   
            sb.append(PropertyProvider.get("prm.form.listview.modify.created.label") + creator);
            sb.append(", " + formatter.formatDate(m_date_created));
            sb.append("&nbsp;&nbsp;"); 
            sb.append(PropertyProvider.get("prm.form.listview.modify.modified.label") + (m_modify_person_id.equals("-1") ? m_creator_email : directory.getDisplayNameByID(m_modify_person_id)));
            sb.append(", " + formatter.formatDate(m_date_modified));
        }
        return sb.toString();
    }
    
    /**
    Get the modification string for this form data item containing persons and dates created and last modified.
    */
   public String getModificationTDString() {
       StringBuffer sb = new StringBuffer();
       DefaultDirectory directory = new DefaultDirectory();
       net.project.util.DateFormat formatter = m_form.getUser().getDateFormatter();

       if ((m_create_person_id != null) && (m_modify_person_id != null) && (m_date_created != null) && (m_date_modified != null)) {
       		String creator = m_create_person_id.equals("-1") ? m_creator_email : directory.getDisplayNameByID(m_create_person_id);
       		sb.append("<td align=\"left\" class=\"tableHeader\">"+PropertyProvider.get("prm.form.listview.modify.created.label") + "</td>");
       		sb.append("<td>" + creator+ ", " + formatter.formatDate(m_date_created) + "</td>");
       		sb.append("<td align=\"left\" class=\"tableHeader\">"+PropertyProvider.get("prm.form.listview.modify.modified.label") + "</td>");
       		sb.append("<td>" + (m_modify_person_id.equals("-1") ? m_creator_email : directory.getDisplayNameByID(m_modify_person_id))+ ", " + formatter.formatDate(m_date_modified) + "</td>");
       }
       return sb.toString();
   }


    /**
     * Perform an check of the physical database to see if this FormData object
     * already exists in the data.  If this data object has multi-table data,
     * only the first table is checked.
     *
     * @return true if the object already exists in the database.
     */
    public boolean dbDataExists() {
        // TODO -- make sure the "system" data_table is first or we might be checking a user table that has no data and get a erronous result
        String query_string;

        // Just check the first field's table name.  There may be multiple tables that the data is stored in,
        // but since all data transactions should be atomic through this class's methods, we will be OK checking only one table.

        if (m_form.m_fields.size() < 1)
            return false;

        query_string = "select object_id from " + ((FormField)m_form.m_fields.get(0)).m_data_table_name + " where object_id = " + DBFormat.number(m_data_object_id);

        try {
            db.setQuery(query_string);
            db.executeQuery();

            if (!db.result.next())
                return true;
            else
                return false;
        } catch (SQLException sqle) {
        	Logger.getLogger(FormData.class).error("FormData.dbDataExists failed " + sqle);
        } finally {
            db.release();
        }

        return false;
    }


    /** @return a String representation of the FormData */
    public String toString() {
        StringBuffer sb = new StringBuffer(150);

        sb.append("\n++++++ FormData dump ++++++");
        sb.append("\nm_class_id = " + m_form.m_class_id);
        sb.append("\nm_data_object_id= " + m_data_object_id);
        sb.append("\nm_sequence_num  = " + m_sequence_num);
        sb.append("\nForm's m_class_id = " + m_form.m_class_id);
        sb.append("\nvalues ---------------------------------");
        sb.append(super.toString());
        sb.append("\n++++++ End FormData dump ++++++");
        return sb.toString();
    }

    /*==========================================================================
        Implementing ILinkableObject
        Only those methods not defined due to other Interfaces
    ======================================================================*/
    /**
     * Return this form data's object type
     * @return the object type
     * @see net.project.form.FormData#getObjectType
     */
    public String getType() {
        return getObjectType();
    }


    public String getURL() {
        return URLFactory.makeURL(getID(), getObjectType());
    }

    /*==========================================================================
        Implementing IWorkflowable
        Only those methods not defined due to other Interfaces
        ======================================================================*/

    /**
     * Return this form data's "name" - which is constructed from its abbreviation.
     * Also used for ILinkable
     * @return the name
     * @see net.project.link.ILinkableObject#getName
     * @see net.project.workflow.IWorkflowable#getName
     */
    public String getName() {
        // Saw no reason why FormData derived its name from First FormField value
        // So commenting it out
        // -- deepak -- (overriding this comment) -- sjmittal

        FormField titleField = m_form.getFirstEditableField();

        if (titleField != null) {
            
            String titleFieldValue = null;
            if (this.get(titleField.getSQLName()) != null && ((FieldData) this.get(titleField.getSQLName())) != null && ((FieldData) this.get(titleField.getSQLName())).size() > 0) {
                titleFieldValue = (String) ((FieldData) this.get(titleField.getSQLName())).get(0);
            }
            if (titleFieldValue != null && !"".equals(titleFieldValue.trim())) {
            	if (titleField instanceof SelectionListField){
            		try {
	                    FieldDomainValue fieldDomainValue = new FieldDomainValue();
	                    fieldDomainValue.setID((String)((FieldData) this.get(titleField.getSQLName())).get(0));
	                    fieldDomainValue.load();
	                    titleFieldValue = fieldDomainValue.getName();
            		  } catch (PersistenceException pe) {
            			  //	
                      }                    
            	} else if (titleField instanceof PersonListField){
            		titleFieldValue = Person.getDisplayNameForID((String)((FieldData) this.get(titleField.getSQLName())).get(0));
            	}
            	//fix for bug-1846: this value has to be added in object name table and object name is limited to only 500 characters   
            	if (titleFieldValue.length() > 500){
            		titleFieldValue = titleFieldValue.substring(0, 500);
            	}
                return titleFieldValue;
            } else {
                return m_form.getAbbreviation() + "-" + this.getSeqNum();
            }
        }
        //return  m_form.getAbbreviation() + this.getSeqNum() + " -- " + titleFieldValue;
        return m_form.getAbbreviation() + "-" + this.getSeqNum();

    }
    
    /**
     * Return this form data's "name" - which is constructed from its abbreviation.
     * @return short name
     */
    public String getShortName(){
    	return m_form.getAbbreviation() + "-" + this.getSeqNum();
    }

    /**
     * Return this form data's sub type - which is actually the form class's ID
     * @return the form class id
     */
    public String getSubType() {
        return m_form.getID();
    }

    /**
     * Return this form data's object type
     * @return the object type
     * @see net.project.base.ObjectType#FORM_DATA
     */
    public String getObjectType() {
        return net.project.base.ObjectType.FORM_DATA;
    }


    /**
     * Indicates that FormData provides its own presentation.
     * @return true means the getPresentation() method will return HTML presentation
     * @see net.project.form.FormData#getPresentation()
     */
    public boolean isSpecialPresentation() {
        return true;
    }

    /**
     * Returns HTML presentation of FormData
     * @return the HTML presentation
     * @see net.project.workflow.IWorkflowable#isSpecialPresentation()
     */
    public String getPresentation() {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);

        try {
            m_form.writeHtml(pw);
        } catch (java.io.IOException ioe) {
        }
        return sw.toString();
    }

    /***************************************************************************
     *   Implementing IXMLPersistence Interface
     ***************************************************************************

     /**
      * Converts the FormData to XML representation without the XML version tag.
      * This method returns the From as XML text.
      *
      * @return XML representation
      */
    public String getXMLBody() {
        FormField field = null;
        FieldData field_data = null;
        String value = null;
        int num_fields = 0;

        StringBuffer sb = new StringBuffer();
        sb.append("<FormData>\n");
        sb.append("<id>" + XMLUtils.escape(m_data_object_id) + "</id>\n");
        sb.append("<versionID>" + XMLUtils.escape(m_version_id) + "</versionID>\n");
        sb.append("<previousVersionID>" + XMLUtils.escape(m_previous_version_id) + "</previousVersionID>\n");
        sb.append("<createPersonID>" + XMLUtils.escape(m_create_person_id) + "</createPersonID>\n");
        sb.append("<creator_name>" + XMLUtils.escape(
            (m_create_person_display_name != null ? m_create_person_display_name :
            Person.getDisplayNameForID(m_create_person_id))) + "</creator_name>\n");
        sb.append("<creator_email>" + XMLUtils.escape(m_creator_email) + "</creator_email>\n");
        sb.append("<modifyPersonID>" + XMLUtils.escape(m_modify_person_id) + "</modifyPersonID>\n");
        sb.append("<modifier_name>" + XMLUtils.escape(
            (m_modify_person_display_name != null ? m_modify_person_display_name :
            Person.getDisplayNameForID(m_modify_person_id))) + "</modifier_name>\n");
        sb.append("<dateCreated>" + DateFormat.getInstance().formatDateMedium(m_date_created) + "</dateCreated>\n");
        sb.append("<dateModified>" + DateFormat.getInstance().formatDateMedium(m_date_modified) + "</dateModified>\n");
        sb.append("<seqNum>" + m_sequence_num + "</seqNum>\n");
        sb.append("<url>" + getURL()+ "</url>\n");
        sb.append("<workSpaceName>" + XMLUtils.escape(getFormDataSpaceName()) + "</workSpaceName>\n");

        if (m_form != null) {
            sb.append("<FormName>" + XMLUtils.escape(m_form.getName()) + "</FormName>\n");
            sb.append("<rowID>" + XMLUtils.escape(m_form.m_class_abbreviation) + "-" + getSeqNum() + "</rowID>\n");
        }

        sb.append("<crc>" + XMLUtils.formatISODateTime(m_crc) + "</crc>\n");
        sb.append("<versionCnt>" + this.size() + "</versionCnt>\n");

        if (m_form != null && m_form.m_fields != null) {
            num_fields = m_form.m_fields.size();

            // Generate XML for all fields in one data row.
            for (int i = 0; i < num_fields; i++) {
                field = (FormField)m_form.m_fields.get(i);

                // ignore static fields, not stored in data table.
                if (field.isStorable()) {
                    field_data = (FieldData)this.get(field.getSQLName());
                }

                if ((field_data != null) && (field_data.size() > 0)) {
                    value = field.formatFieldData(field_data);
                    // value = (String) field_data.get(0);
                }


                sb.append("<FormFieldData id=\"" + field.getID() + "\">\n");
                sb.append(field.getXMLBody());
                sb.append("<FieldData>");
                sb.append(XMLUtils.escape(value));
                sb.append("</FieldData>\n");
                sb.append("</FormFieldData>\n");

            }
        }

        if (getCurrentSpace() != null) {
            // Attempt to add space information only if it is available
            sb.append("<Space>\n");
            sb.append("<name>" + XMLUtils.escape(getCurrentSpace().getName()) + "</name>\n");
            sb.append("<SpaceType>" + XMLUtils.escape(getCurrentSpace().getSpaceType().getName()) + "</SpaceType>\n");
            sb.append("</Space>\n");
        }

        sb.append("</FormData>\n");
        return sb.toString();
    }


    /**
     * Converts the object to XML representation of the FormData This method
     * returns the Form as XML text.
     *
     * @return XML representation of the form
     */
    public String getXML() {
        return ("<?xml version=\"1.0\" ?>\n" + getXMLBody());
    }


    /************************************************************************************************
     *   Implementing IJDBCPersistence Interface
     *************************************************************************************************

     /**
      * Is this object loaded from persistence store?
      */
    public boolean isLoaded() {
        return m_isLoaded;
    }


    /**
     * Get the data from the database for this data_object_id.
     *
     * @param data_object_id The object_id to retreive data for.
     */
    public void load(String data_object_id) throws PersistenceException {
        m_data_object_id = data_object_id;
        load();
    }


    /**
     * Get the data from the database for this data_object_id.
     *
     * @throws PersistenceException if there is a problem loading the data
     */
    public void load() throws PersistenceException {
        FormField field = null;
        FieldData fieldData = null;
        int i, c;
        int numFields;
        int tableCount = 0;
        int resultIndex;

        String queryString;
        String selectString;
        String fromString;
        String whereString;
        String joinString;
        String currentTableName;

        boolean foundData = false;

        // new data comming in, clear out the old.
        clear();

        // If we do not yet know this Form Data's form class, load it
        if (m_form == null) {
            loadForm();
        }

        if ((numFields = m_form.m_fields.size()) < 1) {
            throw new PersistenceException("No fields defined for this form.");
        }

        // build a query joining all the data tables in the users scope that are needed for the form.
        // the fields are ordered by table.

        // The select string
        selectString = "select " +
                m_form.m_master_table_name + ".crc, " +
                m_form.m_master_table_name + ".version_id, " +
                m_form.m_master_table_name + ".seq_num, " +
                m_form.m_master_table_name + ".previous_version_id, " +
                m_form.m_master_table_name + ".modify_person_id, " +
                m_form.m_master_table_name + ".create_person_id, " +
                m_form.m_master_table_name + ".creator_email, " +
                m_form.m_master_table_name + ".date_created, " +
                m_form.m_master_table_name + ".date_modified, " +
                "pn_class_instance.record_status ";

        // The from string
        fromString = " from " +
                m_form.m_master_table_name + ", " +
                "pn_class_instance ";

        // The where string
        // where PK = current object ID
        // joins with PN_CLASS_INSTANCE
        whereString = " where " +
                m_form.m_master_table_name + "." + m_form.getDataTableKey() + "=" + m_data_object_id + " " +
                "and is_current=1 " +
                "and pn_class_instance.class_instance_id = " + m_form.m_master_table_name + "." + m_form.getDataTableKey() + " " +
                "order by " + m_form.m_master_table_name + ".multi_data_seq ";

        joinString = "";
        boolean isFirstField = true;
        // The master table is always included in the join
        currentTableName = m_form.m_master_table_name;

        for (i = 0; i < numFields; i++) {
            field = (FormField)m_form.m_fields.get(i);

            // ignore static fields, not stored in data table.
            if (field.dbStorageType() != null) {

                // If the table name changes then we joing against the next table
                // 02/06/2003 - Tim
                // It is hard to see how any of this actually works
                if ((field.m_data_table_name != null) && (!field.m_data_table_name.equals(currentTableName))) {
                    if (isFirstField) {
                        isFirstField = false;
                    } else {
                        fromString = fromString + ", ";
                        joinString = whereString + " and ";
                    }

                    tableCount++;
                    fromString = fromString + field.m_data_table_name;
                    joinString = whereString + currentTableName + "." + m_form.getDataTableKey() + "=" + field.m_data_table_name + "." + m_form.getDataTableKey();
                    currentTableName = field.m_data_table_name;
                }

                selectString += ", " + field.getSQLSelectColumn();
            }
        }

        queryString = selectString + fromString + whereString;

        // add where clause for multi-table joins and filters.
        if (tableCount > 1)
            queryString = queryString + joinString;

        // LOAD DATA
        // Save the data in memory. Data for the fields of one form.
        try {
            db.setQuery(queryString);
            db.executeQuery();

            // FOR EACH ROW
            // for each row in the result set.
            // There may be multiple rows if the form has multi-selection fields.
            foundData = false;
            while (db.result.next()) {
                // we always get the sequence_num and crc for the data record
                // used for update conflict checks on load() later.
                m_crc = db.result.getTimestamp(1);
                m_version_id = db.result.getString(2);
                m_sequence_num = Conversion.toInt(db.result.getString(3));
                m_previous_version_id = db.result.getString(4);
                m_modify_person_id = db.result.getString(5);
                m_create_person_id = db.result.getString(6);
                m_creator_email = db.result.getString(7);
                m_date_created = db.result.getTimestamp(8);
                m_date_modified = db.result.getTimestamp(9);
                this.recordStatus = RecordStatus.findByID(db.result.getString(10));

                // FOR EACH FIELD IN ROW.
                // get the data for each field.
                // Only the non-static fields will be in the JDBC result set. Start at resultIndex=9 to skip the crc, version, seq, etc. system fields.
                resultIndex = 11;
                for (c = 0; c < numFields; c++) {
                    field = (FormField)m_form.m_fields.get(c);

                    // ignore static fields, not stored in data table.
                    if (field.dbStorageType() != null) {
                        if ((fieldData = (FieldData)this.get(field.getSQLName())) == null) {
                            fieldData = new FieldData();
                            foundData = true;
                        }
                        fieldData.add(db.result.getString(resultIndex++));
                        this.put(field.getSQLName(), fieldData);
                    } else {
                        // Do nothing

                    }

                }
            }

            if (!foundData)
                throw new PersistenceException("No data found");

            m_isLoaded = true;

        } catch (SQLException sqle) {
        	Logger.getLogger(FormData.class).error("FormData.load failed " + sqle);
            throw new PersistenceException("Error occured loading form data", sqle);
        } finally {
            db.release();
        }
    }


    /**
     * Store the data for a form by updating an existing data object in each of
     * the field's data table. Also adds the form to an workflow envelope if
     * there is a default workflow.
     *
     * @throws PersistenceException if there is a problem storing the form data
     * @throws SQLException 
     */
    public void store() throws PersistenceException, SQLException {
        isNewFormData = false;      // Indicates that this was brand new

        // Store existing properties for assignment back later
        // These copies may be modified by the create / modify methods and will
        // only be assigned based on successful completion of the transaciton
        String dataObjectID = m_data_object_id;
        String versionID = m_version_id;
        int sequenceNum = m_sequence_num;
        String previousVersionID = m_previous_version_id;


        //System.out.println("DEBUG [FormData.store] Starts, m_data_object_id = " + m_data_object_id + ", m_crc = " + m_crc);

        if (m_form.m_fields.size() < 1)
            throw new PersistenceException("No fields have been defined for this form. The form is not ready to be used.");

        if (getNumDataRows() < 1)
            throw new PersistenceException("Empty form not saved.  You must enter value for at least one field.");

        /*
            ATOMIC TRANSACTION STARTS HERE
         */
        try {
            // Create a crc to be stamped on new records
            java.sql.Timestamp newCrc = new java.sql.Timestamp(new Date().getTime());
            //String personID = m_form.getUser() != null ? m_form.getUser().getID() : "-1";
            //String personID = m_creator_email == null ? m_form.getUser().getID() : "-1";
            String personID = isExternalRecord() ?  "-1" : m_form.getUser().getID();

            db.openConnection();
            db.connection.setAutoCommit(false);
//Avinash :------------------------------------------------------------------------------------------------------
            if ((m_data_object_id != null && !m_data_object_id.equals("") && !m_data_object_id.equals("null"))) {
//Avinash :------------------------------------------------------------------------------------------------------            	
                // NEW VERSION of existing form data record.
                // if this data_object_id has been stored to the database before.
                // we will insert a new version of the class data record with the user's updates in order to keep version history.
                modifyFormDataVersion(newCrc);

                // the previous version for this data item, so we can track change deltas.
                previousVersionID = m_version_id;
                // Now create new version object and store that id as this form data's new version id
                versionID = m_form.objectManager.dbCreateObject(ObjectType.FORM_DATA_VERSION, "A");

                //add the hook to update form name in pn_object_name
                int index = 0;
                db.prepareStatement("UPDATE PN_OBJECT_NAME SET name = ? WHERE object_id = ?");
                db.pstmt.setString(++index, m_form.getFirstEditableField() == null ? m_form.getAbbreviation() + "-" + sequenceNum : getName());
                DatabaseUtils.setInteger(db.pstmt, ++index, dataObjectID);                
                int updateCount = db.executePreparedUpdate();
                //sjmittal: if this is form created prior to the additon of assignable for or hook then insert new row
                if(updateCount == 0) {
                    index = 0;
                    db.prepareStatement("INSERT INTO PN_OBJECT_NAME (object_id, name) VALUES (?, ?)");
                    DatabaseUtils.setInteger(db.pstmt, ++index, dataObjectID);
                    String formName = m_form.getFirstEditableField() == null ? m_form.getAbbreviation() + "-" + sequenceNum : getName();
                    db.pstmt.setString(++index, formName);
                    db.executePreparedUpdate();
                }
            } else {

                // the system unique object_id for this new FormData object
                dataObjectID = m_form.objectManager.dbCreateObjectWithPermissions(ObjectType.FORM_DATA, "A", SessionManager.getUser().getCurrentSpace().getID(), SessionManager.getUser().getID());
                // version_id for this new form data item.
                versionID = m_form.objectManager.dbCreateObject(ObjectType.FORM_DATA_VERSION, "A");
                // the user-readable sequence number 1..N for a FormData object belonging to the Form.
                // Only the first version of a form instance gets a sequence number,  future version carry the same sequence.
                sequenceNum = m_form.dbGetNextDataSequence(db);
                setSeqNum(sequenceNum);
                if (sequenceNum == -1) {
                    throw new PersistenceException("Error occurred storing form.");
                }
                m_create_person_id = personID;
                m_date_created = new Date();

                // Create the form instance record
                createFormInstance(dataObjectID, newCrc, sequenceNum);
                isNewFormData = true;
                
                //add the hook to store form name in pn_object_name
                int index = 0;
                db.prepareStatement("INSERT INTO PN_OBJECT_NAME (object_id, name) VALUES (?, ?)");
                DatabaseUtils.setInteger(db.pstmt, ++index, dataObjectID);
                String formName = m_form.getFirstEditableField() == null ? m_form.getAbbreviation() + "-" + sequenceNum : getName();
                db.pstmt.setString(++index, formName);
                db.executePreparedUpdate();
            }

            // INSERT THE DATA RECORD
            storeData(dataObjectID, versionID, previousVersionID, sequenceNum, m_create_person_id, personID, m_creator_email, m_date_created, newCrc);

            db.connection.commit();


            // Assign potentially modified properties back.
            m_data_object_id = dataObjectID;
            m_version_id = versionID;
            m_sequence_num = sequenceNum;
            m_previous_version_id = previousVersionID;

            m_crc = newCrc;

            storeEvents(); // Finally generate event for that
            /*
                END OF ATOMIC TRANSACTION
             */

        } catch (SQLException sqle) {
        	Logger.getLogger(FormData.class).error("FormData.store failed " + sqle);
            throw new PersistenceException("Error occured storing form data", sqle);

        } finally {
            if (db.connection != null) {
                try {
                    db.connection.rollback();
                } catch (SQLException sqle2) {
                    // Can do nothing except release()
                }
            }
            db.release();

        } // end try

        // Now check to see if we created new data and form has a default workflow
        // associated with it and create the envelope if it does.
        if (isNewFormData && m_form.hasDefaultWorkflow()) {
            String envelopeTitle = null;
            EnvelopeManager envelopeManager = new EnvelopeManager();
            envelopeManager.setUser(m_form.getUser());

            try {
                envelopeTitle = "Form " + this.getName();
                envelopeManager.createEnvelope(getID(), m_form.getDefaultWorkflow().workflowID, envelopeTitle, "Envelope created for form " + m_form.getName() + " " + new java.util.GregorianCalendar().getTime().toString(), Strictness.RELAXED, EnvelopePriority.NORMAL, null, "comments");

            } catch (EnvelopeException ee) {
                throw new PersistenceException("There was a problem adding the form to a workflow.  However, the form has been stored succesfully.", ee);

            }
        }

    }


    /**
     * Deletes this FormData. A soft delete is performed.
     *
     * @throws PersistenceException if there is a problem removing this FormData
     */
    public void remove() throws PersistenceException {
        StringBuffer query = new StringBuffer();
        int index = 0;

        // Query to soft delete from pn_class_instance table
        // Note that each form table denoted by the master table name does not
        // actually include a record_status field; hence this delete is sufficient
        query.append("update pn_class_instance set record_status = 'D' where class_instance_id = ? ");

        try {
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, m_data_object_id);
            db.executePrepared();

            FormEvent eventDataInstance = makeFormEventInstance(m_data_object_id, EventCodes.REMOVE_FORM_DATA, "Remove Form Data : \"" + getName() + "\"");
            eventDataInstance.store();

            FormEvent eventClassInstance = makeFormEventInstance(m_form.getID(), EventCodes.REMOVE_FORM_DATA, "Remove Form Data : \"" + getName() + "\"");
            eventClassInstance.store();

            //Seting assignment related to this form as deleted if exist	
            String sql = "update pn_assignment set record_status = 'D' where object_id = ?" ;
            db.prepareStatement(sql);
            db.pstmt.setString(1, m_data_object_id);
            db.executePrepared();
            
            try {
	        	net.project.events.FormEvent formEvent = (net.project.events.FormEvent) EventFactory.getEvent(ObjectType.FORM_DATA, EventType.DELETED);
	        	formEvent.setObjectID(m_data_object_id);
	        	formEvent.setObjectType(ObjectType.FORM_DATA);
	        	formEvent.setName(getShortName());
	        	formEvent.setParentObjectId(m_form.getID());
	        	formEvent.setObjectRecordStatus("D");
	        	formEvent.setFormNameWithSequenceNumber(this.getForm().getName()+"-"+this.getSeqNum());
	        	formEvent.publish();
			} catch (EventException e) {
				Logger.getLogger(FormData.class).error("Formdata.storeEvents() :: FormData Remove Event Publishing Failed! ", e);
			}
            
        } catch (SQLException sqle) {
        	Logger.getLogger(FormData.class).error("FormData.remove failed " + sqle);
            throw new PersistenceException("Error occured removing form data", sqle);

        } finally {
            db.release();

        } // end try
    }


    /**
     * Loads the form object for this form data.  The form can be determined
     * by looking in PN_CLASS_INSTANCE.<br>
     *
     * <b>Preconditions:</b> m_object_id is set<br>
     * <b>Postconditions:</b> m_form contains a form object<br>
     * @throws PersistenceException if there is a problem loading the form object
     */
    private void loadForm() throws PersistenceException {
        String class_id = null;         // class_id of form to load
        Form form = null;               // Form loaded from database

        try {
            // Locate the class_id of this form data's form
            db.executeQuery("select class_id from pn_class_instance where class_instance_id = " + m_data_object_id);
            if (db.result.next()) {
                class_id = db.result.getString("class_id");

                // Construct a new form object and assign to member variable
                form = new Form();
                form.setID(class_id);
                form.load();
                m_form = form;

                // Point form's data to ourself.  There is now a somewhat cyclic relationship.
                // This is necessary however to permit the access to the Form from form data
                // and vice-versa.
                try {
                    m_form.setData(this);
                } catch (FormException fe) {
                    throw new PersistenceException("Unable to locate correct form for form data.", fe);
                }

            } else {
                // No class_id found for this form data - problem!
            	Logger.getLogger(FormData.class).error("FormData.loadForm failed - unable to locate form for data with id " +
                    m_data_object_id);
                throw new PersistenceException("Unable to locate form for form data.");
            } // end if

        } catch (SQLException sqle) {
        	Logger.getLogger(FormData.class).error("FormData.loadForm failed " + sqle);
            throw new PersistenceException("Error loading form for form data", sqle);

        } finally {
            db.release();

        } // end try

    }


    /**
     * Modifies Form Data and creates a new version<br>
     * If an exception is thrown, a rollback must be performed by the calling method.<br>
     * No instance variables should be modified here, since the transaction might fail elsewhere<br>
     * <p><b>Preconditions:</b>
     * <li>There is an open connection in DBBean db and autocommit is false</li>
     * </p>
     * <p><b>Postconditions:</b>
     * <li>There are OPEN TRANSACTIONS.  NO commits or rollbacks have been performed.</i>
     * <li>Form Data record for this version updated, <code>is_current = 0</code>, NOT COMMITTED</li>
     * <li>pn_object record created for new version id, NOT COMMITTED</li>
     * </p>
     *
     * @param newCrc the crc to stamp on new records - this is modified to have its milliseconds truncated
     * @throws SQLException if there is a problem creating the new version
     * @throws PersistenceException if there is a problem locating or locking the current version
     */
    private void modifyFormDataVersion(Date newCrc) throws SQLException, PersistenceException {
        StringBuffer lockQuery = new StringBuffer();
        Date crc = null;

        lockQuery.append("select crc from " + m_form.getMasterTableName() + " ");
        lockQuery.append("where object_id = " + m_data_object_id + " ");
        lockQuery.append("and version_id = " + m_version_id + " ");
        lockQuery.append("for update nowait ");

        try {
            db.executeQuery(lockQuery.toString());

            // Now get and check crc of locked record.  It must match the crc of
            // this form data object in order to continue.
            if (db.result.next()) {
                // We got a row from the query
                crc = db.result.getTimestamp("crc");
                if (!crc.equals(m_crc)) {
                    //System.out.println("DEBUG [FormData.modifyFormDataVersion] My crc is " + m_crc + ", does not match database crc of " + crc);
                    throw new PersistenceException("Form data has been modified by another user.");
                }

            } else {
                // There is a problem if we got no rows from the query
            	Logger.getLogger(FormData.class).error("FormData.store failed to lock form data. No rows found with query " + lockQuery.toString());
                throw new PersistenceException("Error storing form data");
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(FormData.class).error("FormData.store error locking form data with query " + lockQuery.toString() + sqle);
            throw new PersistenceException("Error storing form data", sqle);

        } finally {
            db.closeStatement();
            db.closeResultSet();
        } //end try

        // At this point, we have locked the record
        //System.out.println("DEBUG [FormData.modifyFormDataVersion] UPDATE existing form data... record locked.  Updating current version.");
        // First, set this version to be non-current, updating the crc so everyone else will see it has changed.
        db.setQuery("update " + m_form.getMasterTableName() + " set is_current = 0, crc = " + DBFormat.crc(newCrc) + " where object_id = " + m_data_object_id + " and version_id=" + m_version_id);
        db.executeQuery();
        db.closeStatement();
    }

    /**
     * Creates a new Form instance for new form data.
     * The form data instance is attached to the current space.<br>
     * If an exception is thrown, a rollback must be performed by the calling method.
     * <p><b>Preconditions:</b>
     * <li>There is an open connection in DBBean db and autocommit is false</li>
     * </p>
     * <p><b>Postconditions:</b>
     * <li>There are OPEN TRANSACTIONS.  NO commits or rollbacks have been performed.</i>
     * <li><code>pn_class_instance</code> record created, NOT committed</li>
     * </p>
     *
     * @param dataObjectID the data object id to store in the database
     * @param newCrc the crc to stamp on new records - this is modified to have its milliseconds truncated
     * @throws SQLException if there is a problem creating form instance
     */
    private void createFormInstance(String dataObjectID, Date newCrc, int sequenceNum) throws SQLException {
        StringBuffer insertQuery = new StringBuffer("");

        // Since this is a new set for form data, create the PN_CLASS_INSTANCE record
        // This is a central place to locate the form class of some form data
        insertQuery.append("insert into pn_class_instance ");
        insertQuery.append("(space_id, class_instance_id, class_id, crc, record_status, seq_num) ");
        insertQuery.append("values ");
        insertQuery.append("( " + getCurrentSpace().getID() + ", " + dataObjectID + ", " + m_form.getID()
        		+ ", " + DBFormat.crc(newCrc) + ", 'A', "+ sequenceNum+" )" );
        try {
            db.executeQuery(insertQuery.toString());

        } finally {
            db.closeStatement();

        } //end try

    }


    /**
     * Store the data to the database.  Handles multiple scope tables and multiple data rows.
     * If an exception is thrown, a rollback must be performed by the calling method.<br>
     * No instance variables should be modified here, since the transaction might fail elsewhere<br>
     * <p><b>Preconditions:</b>
     * <li>There is an open connection in DBBean db and autocommit is false</li>
     * </p>
     * <p><b>Postconditions:</b>
     * <li>There are <b>OPEN TRANSACTIONS</b>.  NO commits or rollbacks have been performed.</i>
     * </p>
     *
     * @param dataObjectID the id (potentially new id) of form data object
     * @param versionID the new version id of form data object
     * @param sequenceNum the new sequenceNum of form data object
     * @throws SQLException if there is a problem storing the data
     */
    private void storeData(String dataObjectID, String versionID, String previousVersionID, int sequenceNum, String createPersonID, String modifyPersonID, String creatorEmail, Date dateCreated, Date newCrc) throws SQLException {

        // For logging batched statements
        StringBuffer logInfo = new StringBuffer();

        try {
            // Create the statement to be used for batching
            db.createStatement();

            // Add the batch statements, updating the logInfo
            addStoreStatements(dataObjectID, versionID, previousVersionID, sequenceNum, createPersonID, modifyPersonID, creatorEmail, dateCreated, newCrc, this.db, logInfo);

            // We now have a number of batched statements.  Execute them.
            db.executeBatch();
            db.closeStatement();

        } catch (SQLException sqle) {
            // If a batch update exception occurred, log the statements that were
            // executed.  The DBBean will have logged the statement numbers which
            // failed.
            if (sqle instanceof java.sql.BatchUpdateException) {
            	Logger.getLogger(FormData.class).error("FormData.storeData threw a BatchUpdateException while " +
                    "executing the following statements:\n" +
                    logInfo.toString());
            }

            throw sqle;
        }

    }

    /**
     * Adds the statements required for storing to an existing statement
     * object as batch statements in the specified DBBean.
     *
     * @param db the DBBean in which the statements exists
     * @param logInfo a buffer into which log messages are inserted to facilitate
     * logging in the event of an exception upon execution of the statements
     * @throws SQLException if there is a problem adding the store statements
     * as batch statements
     */
    private void addStoreStatements(String dataObjectID, String versionID, String previousVersionID, int sequenceNum, String createPersonID, String modifyPersonID, String creatorEmail, Date dateCreated, Date newCrc, DBBean db, StringBuffer logInfo) throws SQLException {

        FormField field = null;
        String lastTableName = null;
        StringBuffer queryString = new StringBuffer();
        StringBuffer valuesString = new StringBuffer();

        int logCount = 0;

        // Iterate over each data row and add a statement for inserting
        // form data into that row
        for (int row = 0; row < getNumDataRows(); row++) {
            lastTableName = null;
            queryString.setLength(0);
            valuesString.setLength(0);

            // Iterate over each field in the form, appending to the
            // insert statement a value for that field for the current row
            for (int i = 0; i < m_form.m_fields.size(); i++) {
                field = (FormField)m_form.m_fields.get(i);

                // ignore static fields, not stored in data table.
                if (field.isStorable()) {

                    if (!field.m_data_table_name.equals(lastTableName)) {
                        // New table, new update

                        // If this is the second or subsequent table
                        // Add the query to the batch statement
                        if (lastTableName != null) {
                            queryString.append(") ");
                            queryString.append(valuesString);
                            queryString.append(") ");

                            // Query is completed, add to batch
                            db.stmt.addBatch(queryString.toString());
                            logCount++;
                            logInfo.append("[Statement " + logCount + "]\n" + queryString + "\n");
                        }

                        // Start a new statement
                        queryString = new StringBuffer("insert into ");
                        queryString.append(field.m_data_table_name);
                        queryString.append(" (");
                        queryString.append(m_form.getDataTableKey() + ", version_id, previous_version_id, is_current, multi_data_seq, create_person_id, modify_person_id, creator_email, date_created, date_modified, crc, ");

                        valuesString = new StringBuffer("values (");
                        valuesString.append(dataObjectID + "," + versionID + "," + previousVersionID + ", 1, " + row + ", " +
                            createPersonID + "," + modifyPersonID + ",'" + creatorEmail + "'," + DBFormat.crc(dateCreated) + ", SYSDATE," + DBFormat.crc(newCrc) + ", ");

                        if (m_form.isSequenced()) {
                            queryString.append(" seq_num,");
                            valuesString.append(sequenceNum + ", ");
                        }
                        // End of new statement

                        // Remember this table as the "last" table
                        lastTableName = field.m_data_table_name;

                    } else {
                        // Same table
                        // comma before the next column in the insert
                        queryString.append(", ");
                        valuesString.append(", ");

                    }

                    // Add this field's column name to the column string
                    queryString.append(field.m_data_column_name);

                    // Add the current value to the value string
                    // Format value for storage in database based on
                    // field's database datatype
                    valuesString.append(field.processDataValueForInsert(getFieldDataValue(field, row)));

                }  // if (field.isStorable())

            }  // for each form field

            // Finish up last portion of last statement
            queryString.append(") ");
            queryString.append(valuesString);
            queryString.append(")");

            db.stmt.addBatch(queryString.toString());
            logCount++;
            logInfo.append("[Statement " + logCount + "]\n" + queryString + "\n");

        }  // for row

    }

    /**
     * Return the field data value for the specified row from the specified
     * field.
     *
     * @param field the field for which to get the data value
     * @param row the current row of data to get
     * @return the value or <code>null</code> if there is no value for the
     * specified field at the specified row
     */
    private String getFieldDataValue(FormField field, int row) {
        FieldData fieldData = null;
        String value = null;

        // Get field data
        // TODO -- need to handle multiple element fields
        fieldData = (FieldData)this.get(field.getSQLName());

        // If the field data has a value for the current row
        // then get the value for the current row
        if (fieldData != null && fieldData.size() > row) {
            value = (String)fieldData.get(row);
        } else {
            value = null;
        }

        return value;
    }

    /**
     * Returns the Attributes supported by this FormData
     *
     * @return Attributes the collection of attributes supported by FormData
     */
    public AttributeCollection getAttributes() {
        Iterator itr = this.m_form.getFields().iterator();
        AttributeCollection attributes = new AttributeCollection();

        while (itr.hasNext()) {
            FormField formField = (FormField)itr.next();

            // Get the attribute for the field
            // If the attribute is not null, add it to the list of attributes
            // Currently Meta field is not imported
            // so ignored these fields from field list
            if(!(formField instanceof MetaDataDateField) && !(formField instanceof MetaDataSpaceField) && !(formField instanceof MetaDataUserField)){
	            IAttribute iAttribute = getAttributeForField(formField);
	            attributes.add(iAttribute);
	            hashAtrField.put(formField, iAttribute);
            }
        }

        return attributes;
    }

    /**
     * Sets the value for the specified attribute.
     *
     * @param iattr the attribute to set the value for
     * @param iattrval the value for the attribute
     * @throws IllegalAttributeValueException if there is a problem setting
     * the value of the attribute
     */
    public void setAttributeValue(IAttribute iattr, IAttributeValue iattrval)
        throws IllegalAttributeValueException {

        Set set = hashAtrField.entrySet();
        Iterator itr = set.iterator();

        while (itr.hasNext()) {

            Map.Entry me = (Map.Entry)itr.next();
            FormField formField = (FormField)me.getKey();
            IAttribute iAttribute = (IAttribute)me.getValue();

            if (iAttribute != null && iAttribute.getID().equals(iattr.getID())) {

                FieldData fieldData = createFieldData(iattrval);
                formField.processFieldData(fieldData);

                if (fieldData.size() > getNumDataRows()) {
                    setNumDataRows(fieldData.size());
                }

                this.put(formField.getSQLName(), fieldData);
            }
        }
    }

    /**
     * Stores the various attributes to the Database
     *
     * @throws AttributeStoreException is thrown if anything goes wrong
     * @throws SQLException 
     */
    public void storeAttributes()
        throws AttributeStoreException, SQLException {

        try {
            store();
        } catch (PersistenceException e) {
            throw new AttributeStoreException("Cannot store the attribute for the Form Fields" + e.getMessage());
        }

    }

    /**
     * Resets the values of different attributes of the object
     */
    public void clearAttributeValues() {
        clear();
        m_data_object_id = null;
        m_version_id = null;

        /** the user-readable sequence number of this data object. */
        m_sequence_num = 0;

        m_previous_version_id = null;
        m_create_person_id = null;
        m_modify_person_id = null;
        m_date_created = null;
        m_date_modified = null;
        m_creator_email = null;

        m_crc = null;

        /** is the data the most current data record */
        m_is_current = false;

        /** the number of rows in this FieldData object which is equal to the max number of data items for any one field */
        m_numberRows = 0;

    }

    /**
     * A factory method for creating different FieldData instances
     *
     * @param iAttributeValue
     * @return <code>FieldData</code> instance
     */
    private FieldData createFieldData(IAttributeValue iAttributeValue) {

        if (iAttributeValue instanceof DateAttributeValue) {

            DateAttributeValue dateAttributeValue = (DateAttributeValue)iAttributeValue;
            Date date = dateAttributeValue.getDateValue();
            FieldData fieldData = new FieldData();

            //We always process each part of the date data individually, hence we have to add each
            //part of the date data as a separate element to fieldData
            DateFormat dateFormatter = DateFormat.getInstance();
            String dateFieldString = dateFormatter.formatDate(date);  // We must always parse the date portion based on
                                                                      //the user's chosen date pattern
            if (!Validator.isBlankOrNull(dateFieldString)) {
                String hourFieldString = dateFormatter.formatTime(date, "h");
                String minuteFieldString = dateFormatter.formatTime(date, "mm");
                net.project.calendar.PnCalendar tmpCalendar = new net.project.calendar.PnCalendar(SessionManager.getUser());
                tmpCalendar.setTime(date);
                String ampmFieldString = Integer.toString(tmpCalendar.get(Calendar.AM_PM));

                fieldData.add(dateFieldString);
                fieldData.add((Integer.valueOf(hourFieldString).intValue() == 12 ? "0" : hourFieldString));
                fieldData.add(minuteFieldString);
                fieldData.add(ampmFieldString);
            } else {
                fieldData.add(null);
                fieldData.add(null);
                fieldData.add(null);
                fieldData.add(null);
            }

            return fieldData;

        } else if (iAttributeValue instanceof DomainValue) {

            DomainValue iDomainValue = (DomainValue)iAttributeValue;
            FieldData fieldData = new FieldData();
            fieldData.add(iDomainValue.getID());
            return fieldData;

        } else if (iAttributeValue instanceof TextAttributeValue) {

            TextAttributeValue textAttributeValue = (TextAttributeValue)iAttributeValue;
            String fieldString = textAttributeValue.getTextValue();
            FieldData fieldData = new FieldData();
            fieldData.add(fieldString);
            return fieldData;

        } else if (iAttributeValue instanceof NumberAttributeValue) {

            NumberAttributeValue numberAttributeValue = (NumberAttributeValue)iAttributeValue;
            Number number = numberAttributeValue.getNumberValue();

            FieldData fieldData = new FieldData();
            if (number != null) {
                fieldData.add(NumberFormat.getInstance().formatNumber(number.doubleValue()));
            } else {
                fieldData.add(null);
            }
            return fieldData;

        } else {
            return null;
        }
    }

    /**
     * Returns an attribute for the specified field.  The attribute's type will
     * be appropriate for the field type.
     *
     * @param formField the field to get the attribute for
     * @return the attribute or <code>null</code> if the field type is not
     * supported
     */
    private IAttribute getAttributeForField(FormField formField) {
        IAttribute attribute = null;

        if (formField instanceof CheckboxField) {
            BooleanAttribute booleanAttribute = new BooleanAttribute(formField.getID(), formField.getFieldLabel());

            DomainValue dtrue = new DomainValue(String.valueOf("1"),
                PropertyProvider.get("prm.global.boolean.true.name"), booleanAttribute);
            DomainValue dfalse = new DomainValue(String.valueOf("0"),
                PropertyProvider.get("prm.global.boolean.false.name"), booleanAttribute);

            booleanAttribute.add(dtrue);
            booleanAttribute.add(dfalse);

            attribute = booleanAttribute;
        } else if (formField instanceof DateField) {
            DateAttribute dateAttribute = new DateAttribute(formField.getID(), formField.getFieldLabel());
            attribute = dateAttribute;
        } else if (formField instanceof TextAreaField) {
            TextAttribute textAttribute = new TextAttribute(formField.getID(), formField.getFieldLabel());
            attribute = textAttribute;
        } else if (formField instanceof TextField) {
            TextAttribute textAttribute = new TextAttribute(formField.getID(),
                formField.getFieldLabel(), Integer.parseInt(formField.getDataColumnSize()));
            attribute = textAttribute;
        } else if (formField instanceof SelectionListField) {
            SelectionListField selectionListField = (SelectionListField)formField;
            DomainListAttribute domainListAttribute = new DomainListAttribute(formField.getID(), formField.getFieldLabel());

            Iterator itr = selectionListField.getDomain().getValues().iterator();

            while (itr.hasNext()) {
                FieldDomainValue fieldDomainValue = (FieldDomainValue)itr.next();
                DomainValue domainValue = new DomainValue(fieldDomainValue.getID(), fieldDomainValue.getName(), domainListAttribute);
                domainListAttribute.add(domainValue);
            }

            attribute = domainListAttribute;
        } else if (formField instanceof PersonListField) {
            PersonListField personListField = (PersonListField)formField;
            PersonListAttribute personListAttribute = new PersonListAttribute(formField.getID(), formField.getFieldLabel());

            Iterator itr = personListField.getPersonList().iterator();

            DomainValue noneDomainValue = new DomainValue(null, PropertyProvider.get("prm.form.designer.lists.create.filters.personlist.option.none.name"), personListAttribute);
            personListAttribute.add(noneDomainValue);

            while (itr.hasNext()) {
                Person fieldDomainValue = (Person)itr.next();
                DomainValue domainValue = new DomainValue(fieldDomainValue.getID(), fieldDomainValue.getDisplayName(), personListAttribute);
                personListAttribute.add(domainValue);
            }

            attribute = personListAttribute;
        } else if (formField instanceof MilestoneListField) {
            MilestoneListField milestoneListField = (MilestoneListField)formField;
            DomainListAttribute milestoneListAttribute = new DomainListAttribute(formField.getID(), formField.getFieldLabel());

            DomainValue noneDomainValue = new DomainValue(null, PropertyProvider.get("prm.form.csvimport.columnmapping.milestone.unassigned"), milestoneListAttribute);
            milestoneListAttribute.add(noneDomainValue);

            List milestoneList = milestoneListField.getMilestoneList();
            if (milestoneList != null) {
            	for (Iterator itr = milestoneList.iterator(); itr.hasNext(); ) {
                    ScheduleEntry fieldDomainValue = (ScheduleEntry)itr.next();
                    DomainValue domainValue = new DomainValue(fieldDomainValue.getID(), fieldDomainValue.getName(), milestoneListAttribute);
                    milestoneListAttribute.add(domainValue);

                }	
            }
            
            attribute = milestoneListAttribute;
        } else if (formField instanceof NumberField || formField instanceof CurrencyField) {
            NumberAttribute numberAttribute = new NumberAttribute(formField.getID(), formField.getFieldLabel());
            attribute = numberAttribute;
        } else if (formField instanceof CalculationField || formField instanceof HorizontalSeparatorField || 
        			formField instanceof InstructionField || formField instanceof TextField || 
        			formField instanceof FormID) {
            UnimportableAttribute unimportableAttribute = new UnimportableAttribute(formField.getID(), formField.getFieldLabel());
            attribute = unimportableAttribute;
        }

        return attribute;
    }

    /**
     * generates and store events related to form data
     *
     * @throws PersistenceException if anything goes wrong
     */
    private void storeEvents() throws PersistenceException {

        if (this.isNewFormData) {

            FormEvent eventDataInstance = makeFormEventInstance(m_data_object_id, EventCodes.CREATE_FORM_DATA, " Create Form Data : \"" + getShortName() + "\"");
            eventDataInstance.store();
			
            // publishing event to asynchronous queue
	        try {
	        	net.project.events.FormEvent formEvent = (net.project.events.FormEvent) EventFactory.getEvent(ObjectType.FORM_DATA, EventType.NEW);
	        	formEvent.setObjectID(m_data_object_id);
	        	formEvent.setObjectType(ObjectType.FORM_DATA);
	        	formEvent.setName(getShortName());
	        	formEvent.setParentObjectId(m_form.getID());
	        	formEvent.setObjectRecordStatus("A");
	        	formEvent.setFormNameWithSequenceNumber(this.getForm().getName()+"-"+this.getSeqNum());
	        	// If form data created by accessing externally
	        	if(this.getForm().getSupportsExternalAccess()) {
	        		formEvent.setEafForm(true);
	        		formEvent.setCreatorEmailAddress(this.getCreatorEmail());
	        	}
	        	formEvent.publish();
			} catch (EventException e) {
				Logger.getLogger(FormData.class).error("Formdata.storeEvents() :: FormData Create Event Publishing Failed!"+ e.getMessage());
			}

            FormEvent eventClassInstance = makeFormEventInstance(m_form.getID(), EventCodes.CREATE_FORM_DATA, " Create Form Data : \"" + getShortName() + "\"");
            eventClassInstance.store();
            
			
            //if (m_creator_email != null && m_creator_email.trim().length() > 0){
            if (isExternalRecord()){
            	FormDataNotification notification = new FormDataNotification();            	
                try {
                	notification.initialize(this);
                    notification.post();
                } catch (DeliveryException e) {
                    // Problem delivering email, possibly due to bad email configuration                	
                	Logger.getLogger(FormData.class).error("FormData notification delivery error  " + e);	
                } catch (NotificationException e) {                	
                	Logger.getLogger(FormData.class).error("FormData notification error " + e);
				}            	
            }            
            
            
        } else {

            FormEvent eventDataInstance = makeFormEventInstance(m_data_object_id, EventCodes.MODIFY_FORM_DATA, "Modify Form Data : \"" + getShortName() + "\"");
            eventDataInstance.store();
            
			// publishing event to asynchronous queue
	        try {
	        	net.project.events.FormEvent formEvent = (net.project.events.FormEvent) EventFactory.getEvent(ObjectType.FORM_DATA, EventType.EDITED);
	        	formEvent.setObjectID(m_data_object_id);
	        	formEvent.setObjectType(ObjectType.FORM_DATA);
	        	formEvent.setName(getShortName());
	        	formEvent.setParentObjectId(m_form.getID());
	        	formEvent.setObjectRecordStatus("A");
	        	formEvent.setFormNameWithSequenceNumber(this.getForm().getName()+"-"+this.getSeqNum());
	        	formEvent.publish();
			} catch (EventException e) {
				Logger.getLogger(FormData.class).error("Formdata.storeEvents() :: FormData Modify Event Publishing Failed! ", e);
			}

            FormEvent eventClassInstance = makeFormEventInstance(m_form.getID(), EventCodes.MODIFY_FORM_DATA, "Modify Form Data : \"" + getShortName() + "\"");
            eventClassInstance.store();
        }
    }

    /**
     * A sort of "Factory" method for making instance of the same "FormEvent" type
     *
     * @param objectID  The object ID for the object on which the event has occured
     * @param eventCode The Event Code
     * @param notes     The notes for the message to send
     * @return <CODE>FormEvent</CODE> instances
     */
    private FormEvent makeFormEventInstance(String objectID, String eventCode, String notes) {

        FormEvent event = new FormEvent();
        event.setTargetObjectID(objectID);
        event.setTargetObjectType(eventCode);
        event.setTargetObjectXML(this.getXMLBody());
        event.setEventType(eventCode);
        event.setName(EventCodes.getName(eventCode));
        event.setUser(SessionManager.getUser());
        event.setDescription(notes);

        // Eliminates the Null Pointer Exception
        // Short term solution

        if (this.currentSpace != null) {
            event.setSpaceID(getCurrentSpace().getID());
        } else {
            event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
        }

        return event;
    }

	/**
	 * @return the m_form_data_space_name
	 */
	public String getFormDataSpaceName() {
		return m_form_data_space_name;
	}

	/**
	 * @param m_form_data_space_name the m_form_data_space_name to set
	 */
	public void setFormDataSpaceName(String form_data_space_name) {
		this.m_form_data_space_name = form_data_space_name;
	}

}

