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

import java.io.Serializable;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.database.ObjectManager;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.Conversion;
import net.project.util.HTMLUtils;
import net.project.workflow.IWorkflowable;
import net.project.xml.XMLUtils;

import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;

/**
 * Represents a form. A form may be a traditional form, a checklist, a property
 * sheet, etc. This class and it's support classes: FormField, FormList,
 * FormData, FormFieldProperty, FieldFilter, FormSort, etc. provide provide a
 * complete interface between the client (ie. HTML) and the relational database
 * storage of the form data.
 * <p>
 * Currently, Form supports HTML and XML renderings. There are several
 * specialized methods for dealing with HTML, but the getXML() method returns
 * all the properties needed to provide alternative interfaces using XSLT
 * transforms.
 */
public class Form implements IJDBCPersistence, IXMLPersistence, Serializable, IWorkflowable {
    // The client presentation mode.
    // only HTML renderings supported currently.
    public static final String HTML_CLIENT = "100";
    public static final String JAVA_CLIENT = "200";
    public static final String XML_CLIENT = "300";

    // Class types
    public static final String ELEMENT_FORM = "1";
    public static final String FORM = "100";
    public static final String CHECKLIST = "200";
    public static final String SURVEY = "300";
    public static final String PROPERTY_SHEET = "400";

    // Form status
    public static final int ACTIVE = 1;
    public static final int READ_ONLY = 2;
    public static final int PENDING = 3;

    // Form lists types
    public static final int ALL_AVAILABLE = 1;
    public static final int USER_LISTS = 2;
    public static final int SHARED_LISTS = 3;
    public static final int ADMIN_LISTS = 4;

    // These column values are defined in the system data population scripts
    // for table pn_class_domain_values

    /** Field is in the left column */
    public static final String LEFT_COLUMN = "400";
    /** Field is in the right column */
    public static final String RIGHT_COLUMN = "401";
    /** Field spans both columns */
    public static final String BOTH_COLUMNS = "402";


    /**
     * The maximum number of characters a database table name is allowed to
     * have.  Currently corresponds to 30, the Oracle 8 maximum table name
     * length.
     */
    protected static final int MAX_TABLE_NAME_SIZE = 30;

    protected String m_class_id = null;
    protected String m_class_type_id = null;
    protected String m_class_name = null;
    protected String m_class_description = null;
    protected String m_class_abbreviation = null;
    protected String m_master_table_name = null;
    protected int m_max_row = 0;
    protected int m_max_column = 0;
    /**  the record_status of the Form's database record. */
    protected String m_record_status = null;
    protected java.util.Date m_crc = null;

    /** the column in the data table that is the key field **/
    protected String m_dataTableKey = null;
    /** is this form class sequenced (multiple data records stored sequentially) */
    protected boolean m_isSequenced = true;
    protected boolean m_showInstanceNum = true;
    protected boolean m_isSystemForm = false;

    /** Does each form instance support having a document vault or discussion group? */
    protected boolean m_supports_discussion_group = false;
    protected boolean m_supports_document_vault = false;
    
    /** Is this form assignable */
    protected boolean m_supports_assignment = false;

    /** Does this form support external access */
    protected boolean m_supports_external_access = false;  
    
    /** are assignments fields hidden in EAF forms */
    protected boolean m_hide_assignment_fields_in_eaf = true;    
    
    /**
     * is form shared (for hierarchical structure)
     */
    protected boolean shared = false;
    
    /**
     * is shared form visible in child space
     */
    protected boolean sharedFormVisible = false;
    
    protected HashMap<String, HashMap<String,String>> sharedFormSpaceIds = new HashMap<String, HashMap<String,String>>();
    
    /* Randomly generated external class id  used for form access without logging in  */
    protected String m_external_class_id = null;
    
    /** all the FormFields in the user's scope that are part of this form. */
    protected ArrayList m_fields; //
    /**
     * all the FormLists in the user's scope that are part of this form.
     * (FormList contains FormSorts.)
     */
    protected ArrayList m_lists;

    /**
     * Workflow class allows storage of workflow ids selected for this form
     * and specified which is the default workflow.
     */
    protected class Workflow implements java.io.Serializable {
        protected String workflowID = null;
        protected boolean isDefault;

        Workflow(String workflowID, boolean isDefault) {
            this.workflowID = workflowID;
            this.isDefault = isDefault;
        }
    }

    /** All the workflows that this form may be placed in. */
    protected ArrayList m_workflows = new ArrayList();
    private boolean isWorkflowsLoaded = false;

    // TODO -- make this an ArrayList of FormData (FieldData?), one element for each FormField.
    /** The data for all fields. */
    protected FormData m_data;
    /**
     * The position in the m_lists ArrayList that contains the user's current
     * FormList display selection.
     */
    protected int m_display_list_idx = -1;
    /**
     * The position in the m_lists ArrayList that contains the user's default
     * list (user or system level default).
     */
    protected int m_default_list_idx = -1;
    protected String m_error_message;

    // The context that this Form is currently being used in\
    /**
     * The User context for this form (rendering and defaults need context of a
     * User)
     */
    protected net.project.security.User m_user = null;
    /**
     * The space (project, personal, business, workspace, methodology) context
     * for this form.
     */
    protected Space m_space = null;
    /**
     * The comma separated list of space_id that are in the current user's scope
     * for this form.
     */
    protected String m_spaces_in_scope = null;

    // The spaces that "own" this class.
    // m_methodology may be null if this class is not part of a methodology
    protected String m_methodology_id = null;
    protected String m_owner_space_id = null;

    // db access bean
    protected DBBean db = new DBBean();
    protected boolean m_isLoaded = false;

    protected ObjectManager objectManager = new ObjectManager();

    protected Space ownerSpace = null;


    /**
     * Create a new Form. If the class_id passed is null or blank, assign a new
     * class_id to this form for storage in the database. The constructor get's
     * database login information from the [pnet] in the sajava.ini file.
     *
     * @param user the User context.
     * @param space the Space context.
     * @param class_id the class_id of this form.
     */
    public Form(net.project.security.User user, Space space, String class_id) {
        m_user = user;
        m_space = space;
        m_fields = new ArrayList(20);
        m_lists = new ArrayList(10);
        m_data = new FormData(this);

        if (class_id == null || class_id.equals(""))
            m_class_id = ObjectManager.getNewObjectID();
        else
            m_class_id = class_id;
    }

    /**
     * Bean constructor
     */
    public Form() {
        m_class_id = null;
        m_user = net.project.security.SessionManager.getUser();
        m_space = m_user.getCurrentSpace();
        m_fields = new ArrayList(20);
        m_lists = new ArrayList(10);
        m_data = new FormData(this);
    }

    /**
     * Bean constructor
     */
    public Form(String exteranalFormId) {
        m_class_id = null;
        m_external_class_id = exteranalFormId;
        m_user = null;
        m_space = null;
        m_fields = new ArrayList(20);
        m_lists = new ArrayList(10);
        m_data = new FormData(this);
    }    
    

    /**
     * Set the class_id of the form.
     */
    public void setID(String id) {
        m_class_id = id;
    }


    /**
     * Get the class_id of the form.
     */
    public String getID() {
        return m_class_id;
    }


    public void setClassTypeID(String id) {
        m_class_type_id = id;
    }

    public String getClassTypeID() {
        return m_class_type_id;
    }

    /**
     * get value of shared property for selected form
     * 
     * @return value of shared property
     */
    public boolean isShared() {
		return shared;
	}

    /**
     * set shared property
     * 
     * @param shared boolean value defining if form is shared or not
     */
	public void setShared(boolean shared) {
		this.shared = shared;
	}

	
	public boolean isSharedFormVisible() {
		return sharedFormVisible;
	}

	public void setSharedFormVisible(boolean sharedFormVisible) {
		this.sharedFormVisible = sharedFormVisible;
	}

	
	public HashMap<String, HashMap<String,String>> getSharedFormSpaceIds() {
		return sharedFormSpaceIds;
	}

	public void setSharedFormSpaceIds(HashMap<String, HashMap<String,String>> sharedFormSpaceIds) {
		this.sharedFormSpaceIds = sharedFormSpaceIds;
	}

	/**
     * Get the field used to best identify instances of the form.
     * <p>
     * The title field is the first _selectable_ field defined on the form
     * where a selectable field is one that can be displayed on a form
     * list.
     * </p>
     * @return the FormField or null if no selectable fields were found
     */
    public FormField getTitleField() {

        FormField foundField = null;

        if (getFields() != null) {
            for (Iterator it = getFields().iterator(); it.hasNext();) {
                FormField nextFormField = (FormField) it.next();
                if (nextFormField.isSelectable() &&  nextFormField.dataColumnExists()) {
                    foundField = nextFormField;
                    break;
                }
            }

        }

        return foundField;
    }


    /**
     * Get the form's master data table name.
     */
    public String getMasterTableName() {
        return m_master_table_name;
    }

    /**
     * Set the form's master data table name
     */
    public void setMasterTableName(String mtn) {
        m_master_table_name = mtn;
    }

    public void setName(String value) {
        m_class_name = value;
    }

    public String getName() {
        return m_class_name;
    }


    public void setDescription(String value) {
        m_class_description = value;
    }

    public String getDescription() {
        return m_class_description;
    }


    public void setAbbreviation(String value) {
        m_class_abbreviation = value;
    }

    public String getAbbreviation() {
        return m_class_abbreviation;
    }


    /**
     * Get the key column for this database data table
     */
    public String getDataTableKey() {
        return m_dataTableKey;
    }

    /**
     * Set the key column for this database data table
     */
    public void setDataTableKey(String key) {
        m_dataTableKey = key;
    }

    /**
     * Is this form class sequenced (multiple data records stored sequentially).
     *
     * @return true if the form stores multiple sequenced data records, false if
     * the form stores only a single record.
     */
    public boolean isSequenced() {
        return m_isSequenced;
    }

    /**
     * Set the property of this form class sequenced (multiple data records
     * stored sequentially). Set to true if the form stores multiple sequenced
     * data records, false if the form stores only a single record.
     */
    public void setSequenced(boolean b) {
        m_isSequenced = b;
    }

    /**
     * Set to display the form instance number on form lists and form instance
     * view.
     *
     * @param value true to display instance numbers.
     */
    public void setShowInstanceNum(boolean value) {
        m_showInstanceNum = value;
    }


    /**
     * Does the form display the form instance number on form lists and form
     * instance view.
     *
     * @return true if the form displays form instance numbers.
     */
    public boolean showInstanceNum() {
        return m_showInstanceNum;
    }


    /**
     * Set the user context for this form.
     */
    public void setUser(net.project.security.User user) {
        m_user = user;
        // new User context means, possible new fields in scope.
        m_isLoaded = false;
    }


    /**
     * Get the user context for this form.
     */
    public net.project.security.User getUser() {
        return m_user;
    }


    /**
     * Set the space context for this form.
     */
    public void setSpace(Space space) {
        m_space = space;
        // new Space context means, possible new fields in scope.
        m_isLoaded = false;
    }


    /**
     * Get the space context for this form.
     */
    public Space getSpace() {
        return m_space;
    }

    /**
     * Return the spaceID of the space that owns this form.
     *
     * @return the spaceID
     * @throws PersistenceException if there is a problem fetching the spaceID
     */
    public String getOwningSpaceID() throws PersistenceException {
        StringBuffer query = new StringBuffer();
        String spaceID = null;
        int index = 0;

        // Locate the space id of the space that is the owner
        query.append("select space_id from pn_space_has_class where class_id = ? and is_owner = 1 ");

        try {
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, this.getID());
            db.executePrepared();

            if (db.result.next()) {
                spaceID = db.result.getString("space_id");
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Form.class).error("Form.getDefaultListID failed " + sqle);
            throw new PersistenceException("Form get owning space operation failed.", sqle);

        } finally {
            db.release();

        }

        return spaceID;
    }

    /**
     * Return the spaceID of the space that owns this form.
     */
    public void setOwnerSpaceID(String osid) {
        this.m_owner_space_id = osid;
        loadOwnerSpace();
    }

    /**
     * Sets the maximum row property of this form
     */
    public void setMaxRow(int mrow) {
        this.m_max_row = mrow;
    }

    /**
     * Gets the maximum row property of this form
     */
    public int getMaxRow() {
        return this.m_max_row;
    }


    /**
     * Sets the maximum row property of this form
     */
    public void setMaxColumn(int mcol) {
        this.m_max_column = mcol;
    }

    /**
     * Gets the maximum row property of this form
     */
    public int getMaxColumn() {

        return this.m_max_column;
    }

    /**
     * Sets the methodologyID of this form
     */
    public void setMethodologyID(String mid) {

        this.m_methodology_id = mid;
    }

    /**
     * Gets the methodologyID of this form
     */
    public String getMethodologyID() {

        return this.m_methodology_id;
    }

    public void setSupportsDiscussionGroup(boolean supportsDiscussionGroup) {
        this.m_supports_discussion_group = supportsDiscussionGroup;
    }

    public boolean getSupportsDiscussionGroup() {
        return this.m_supports_discussion_group;
    }

    public void setSupportsDocumentVault(boolean supportsDocumentVault) {
        this.m_supports_document_vault = supportsDocumentVault;
    }

    public boolean getSupportsDocumentVault() {
        return this.m_supports_document_vault;
    }

    
    public boolean getSupportsAssignment() {
        return m_supports_assignment;
    }

    public void setSupportsAssignment(boolean supportsAssignment) {
        this.m_supports_assignment = supportsAssignment;
    }

    
    /**
	 * @return the m_supports_external_access
	 */
	public boolean getSupportsExternalAccess() {
		return m_supports_external_access;
	}

	/**
	 * @param m_supports_external_access the m_supports_external_access to set
	 */
	public void setSupportsExternalAccess(boolean supportsExternalAccess) {
		this.m_supports_external_access = supportsExternalAccess;
	}

    /**
	 * @return the m_hide_assignment_fields_in_eaf
	 */
	public boolean isAssignmentFieldHiddenInEaf() {
		return m_hide_assignment_fields_in_eaf;
	}

	/**
	 * @param m_hide_assignment_fields_in_eaf the m_hide_assignment_fields_in_eaf to set
	 */
	public void setAssignmentFieldHiddenInEaf(boolean assignmentFieldHiddenInEaf) {
		this.m_hide_assignment_fields_in_eaf = assignmentFieldHiddenInEaf;
	}	
	
	
	/**
	 * @return the m_external_class_id
	 */
	public String getExternalClassId() {
		return m_external_class_id;
	}

	/**
	 * @param m_external_class_id the m_external_class_id to set
	 */
	public void setExternalClassId(String externalClassId) {
		this.m_external_class_id = externalClassId;
	}

	/**
     * Removes all the properties and data from the form. only the ID of the
     * form is not cleared. Use setID(null) to clear ID.
     */
    public void clear() {

        m_class_type_id = null;
        m_class_name = null;
        m_class_description = null;
        m_class_abbreviation = null;
        m_master_table_name = null;
        m_max_row = 0;
        m_max_column = 0;
        m_record_status = null;
        m_crc = null;

        m_fields.clear();
        m_lists.clear();
        clearWorkflows();
        m_data.clear();
        m_display_list_idx = -1;
        m_default_list_idx = -1;
        m_max_row = 0;
        m_max_column = 0;
        m_isLoaded = false;
        m_spaces_in_scope = null;

        m_supports_document_vault = false;
        m_supports_discussion_group = false;
        
        m_supports_assignment = false;
        m_supports_external_access = false;
        m_external_class_id = null;
    }


    /**
     * Clear the data object of this form.
     */
    public void clearData() {
        if (m_data != null)
            m_data.clear();
    }


    /**
     * The number of fields on the form.
     */
    public int size() {
        return m_fields.size();
    }


    /**
     * The number of fields on the form.
     */
    public int getListSize() {
        return m_lists.size();
    }


    /**
     * Does this form contain FormData?
     *
     * @return true if this Form contains a FormData object, false otherwise.
     */
    public boolean hasData() {
        if (m_data != null)
            return (m_data.size() > 0);
        else
            return false;
    }


    /**
     * Does this form contain FormFields?
     *
     * @return true if this Form contains as least one FormField, false otherwise.
     */
    public boolean hasFields() {
        return ((m_fields != null) && (m_fields.size() > 0));
    }


    /**
     * Does this form contain FormLists?
     *
     * @return true if this Form contains as least one FormField, false otherwise.
     */
    public boolean hasLists() {
        return (m_lists != null && m_lists.size() > 0);
    }


    /**
     * Add a FormList to this Form.
     *
     * @param list the FormList to add.
     */
    public void addList(FormList list) {
        if (list == null)
            throw new NullPointerException("list is null");

        // the Form already has this FormList, nothing to add.  (contains by reference, so it knows the updates)
        if (!m_lists.contains(list))
            m_lists.add(list);

        // keep track of the user's default list.
        if (list.m_is_default)
            m_default_list_idx = m_lists.indexOf(list);
    }


    /**
     * Remove the FormList with the specified list_id from the Forms lists
     * stored in memory. Does remove the list form the database.
     */
    public void removeList(String list_id) {
        int num_lists;
        int i;
        FormList list;

        if (list_id == null || list_id.equals(""))
            return;

        if ((num_lists = m_lists.size()) < 1)
            return;

        // look for the list by list_id in the Form's in-memory ArrayList.
        for (i = 0; i < num_lists; i++) {
            list = (FormList)m_lists.get(i);
            if (list.m_list_id.equals(list_id)) {
                m_lists.remove(i);
                return;
            }
        }
    }


    /**
     * Add a FormField to this Form. The field will only be added if a field
     * with the same fieldID is not already a member of the form.
     *
     * @param field the field to add to the form.
     */
    public void addField(FormField field) {
        boolean foundField = false;

        if (field == null)
            return;

        for (int i = 0; i < m_fields.size(); i++) {
            if (((FormField)m_fields.get(i)).getID().equals(field.getID())) {
                foundField = true;
                break;
            }
        }
        if (!foundField)
            m_fields.add(field);

        // the Form already has this FormField, nothing to add.  (contains by reference, so it knows the updates)
        //if (!m_fields.contains(field))
        //    m_fields.add(field);

        // fields must stay sorted in row, colum order
        FormFieldComparator compare = new FormFieldComparator();
        Collections.sort(m_fields, compare);

        return;
    }


    /**
     * Returns the workflows for this form, loading if necessary.
     *
     * @return the collection of workflows or an empty collection if there are
     * none.
     * @throws FormException if there is a problem returning the workflows, for
     * example the workflows cannot be loaded from persistent store
     */
    public List getWorkflows() throws FormException {
        try {
            if (!this.isWorkflowsLoaded) {
                loadWorkflows();
            }

        } catch (PersistenceException pe) {
            throw new FormException("Unable to get Workflows for Form");

        }

        return this.m_workflows;
    }


    /**
     * Indicates whether this Form has a default workflow associated with it
     *
     * @return true if this form has a default workflow, false otherwise
     */
    protected boolean hasDefaultWorkflow() {
        // Return true if this Form has a default workflow
        return (getDefaultWorkflow() != null ? true : false);
    }

    /**
     * Returns the default workflow.
     * <p>
     * Currently, since a form only stores one workflow selection,
     * there default workflow is the single workflow.
     * </p>
     *
     * @return the default workflow
     */
    protected Form.Workflow getDefaultWorkflow() {
        Form.Workflow workflow = null;

        try {
            Iterator it = getWorkflows().iterator();
            if (it.hasNext()) {
                workflow = (Form.Workflow)it.next();
            }

        } catch (FormException fe) {
            // A problem getting the workflows
            // Simply return last one

        }

        return workflow;
    }

    /**
     * Get the first FormField with the matching field_id.
     *
     * @return the first FormField on the form with the matching field_id. If no
     * match is found, null is returned.
     */
    public FormField getField(String field_id) {
        FormField field;

        if (field_id == null)
            return null;

        for (int i = 0; i < m_fields.size(); i++) {
            field = (FormField)m_fields.get(i);
            if ((field != null) && (field.getID()).equals(field_id))
                return field;
        }
        return null;
    }

    /**
     * Get the fieldNum'th field on the form.
     *
     * @return the FormField on the form specified by the fieldNum position.
     */
    public FormField getField(int fieldNum) {
        if ((m_fields == null) || (fieldNum > m_fields.size()))
            return null;

        return (FormField)m_fields.get(fieldNum);
    }

    /**
     * Get all the FormFields for this form.
     *
     * @return an ArrayList of all the {@link net.project.form.FormField}
     * objects for this form that are in the user's scope.
     */
    public ArrayList getFields() {
        return m_fields;
    }

    /**
     * Returns the first editable field on a form.  This is used when deciding
     * which field to focus on first when editing.
     *
     * @return the first editiable field
     */
    public FormField getFirstEditableField() {
        FormField field = null;

        if (m_fields != null) {
            Iterator it = m_fields.iterator();
            while (it.hasNext()) {
                field = (FormField)it.next();

                // If field is stored in database, then is editable
                if (field.dbStorageType() != null) {
                    break;
                }
            }

        }

        return field;
    }

    /**
     * Get and XML representation of all of the fields defined in this form.
     * Form Object *must* be loaded before calling this method
     *
     * @return an XML String
     */
    public String getFieldMapXML() {

        StringBuffer xml = new StringBuffer();
        Iterator fieldList = getFields().iterator();
        FormField field;

        xml.append("<FormFieldList>");

        // if the fields are defined
        while (fieldList.hasNext()) {

            field = (FormField)fieldList.next();
            xml.append(field.getXMLBody());

        }

        xml.append("</FormFieldList>");

        return xml.toString();
    }


    /**
     * Get all the FormLists for this form.
     *
     * @return an ArrayList of all the FormLists defined for this form
     */
    public ArrayList getLists() {
        return m_lists;
    }


    /**
     * Get the FormList with the specified list_id. The list will be looked for
     * in memory fist, then retrieved from the database.
     *
     * @return the FormList that matches list_id. Returns null if no FormList
     * for list_id is found.
     */
    public FormList getList(String list_id) {
        int num_lists;
        int i;
        FormList list;

        if (list_id == null || list_id.equals(""))
            return null;

        if ((num_lists = m_lists.size()) < 1)
            return null;

        // look for the list by list_id in the Form's in-memory ArrayList.
        for (i = 0; i < num_lists; i++) {
            list = (FormList)m_lists.get(i);
            if (list.m_list_id.equals(list_id))
                return list;
        }

        return null;

        /*
        // list was not found in the Form's memory, get it from the database.
        list = new FormList(this);
        list.setID(list_id);

        //  add it to the Form's lists and return.
        try
        {
        list.load();
        this.addList(list);
        return list;
        }
        catch (PersistenceException pe)
        {
        System.out.println(pe);
        list = null;
        return null;
        }
        */
    }


    /**
     * Get the FormLists as an HTML option list for a select statement.
     *
     * @return HTML options list of lists in the form.
     */
    public String getFormListOptionList() {
        StringBuffer sb = new StringBuffer(100);
        FormList list;
        int num_lists;

        if ((num_lists = m_lists.size()) < 1)
            return "<option>No lists defined</option>";

        for (int i = 0; i < num_lists; i++) {
            list = (FormList)m_lists.get(i);
            sb.append("<option value=\"" + list.getID() + "\"");

            if (list.getID().equals(this.getDisplayList().getID()))
                sb.append(" selected");

            sb.append(">" + HTMLUtils.escape(list.getName()) + "</option>\n");
        }
        return sb.toString();
    }


    /**
     * Get the FormData object for this form.
     *
     * @return an ArrayList of all the FormData defined for this form
     */
    public FormData getData() {
        return m_data;
    }


    /**
     * Sets the FormData object for this form. The FormData will not be added to
     * the Form if it's class_id does not match the Forms class_id.
     *
     * @param data the FormData object
     */
    public void setData(FormData data)
            throws FormException {
        if (data.m_form.m_class_id != m_class_id)
            throw new FormException("FormData's class_id does not match Form's class_id");

        //System.out.println("Form.setData():  setting FormData object with data_object_id=" + data.m_data_object_id);
        m_data = data;
        m_data.setCurrentSpace(this.m_space);
    }

    /**
     * Gets the user's default list.  Looks first for a user-defined default, then a space default, then the methodology default.
     * Finally, if none found, the first list in memory is returned.
     *
     * @return the FormList the default form list
     */
    public FormList getDefaultList() {
        String default_list_id = null;


        // if we already have the default list in memory, return it.
        if (m_default_list_idx != -1) {
            return (FormList)m_lists.get(m_default_list_idx);
        }

        try {
            // Look for user's default listID
            default_list_id = getDefaultListID(m_user.getID());

            if (default_list_id == null) {
                // Look for current space's default listID
                default_list_id = getDefaultListID(m_space.getID());

                if (default_list_id == null) {
                    // Look for owning space's default listID
                    default_list_id = getDefaultListID(getOwningSpaceID());

                }
            }

        } catch (PersistenceException pe) {
            // Problem getting default_list_id
            // No action

        }
        if (default_list_id != null) {
            // we found a default list for the user
            // so return that list
            return this.getList(default_list_id);
        }
        // Return the first list in memory
        return (FormList)this.getLists().get(0);
    }


    /**
     * Fetches the listID of the default list for the space with the specified
     * spaceID.
     *
     * @param spaceID for whom to get default listID
     * @return the listID or null if none was found
     * @throws PersistenceException if there is a problem getting the default listID
     */
    private String getDefaultListID(String spaceID) throws PersistenceException {
        StringBuffer query = new StringBuffer();
        String listID = null;

        query.append("select list_id from pn_space_has_class_list ");
        query.append("where class_id = " + m_class_id + " and space_id = " + spaceID + " and is_default = 1");

        try {
            db.executeQuery(query.toString());
            if (db.result.next()) {
                listID = db.result.getString("list_id");
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Form.class).error("Form.getDefaultListID failed " + sqle);
            throw new PersistenceException("Form get default list ID operation failed.", sqle);

        } finally {
            db.release();

        }

        return listID;
    }

    /**
     * Set the default list by it's ID.
     *
     * @param listID the database ID of the FormList that is the default list.
     */
    public void setDefaultListID(String listID) {
        setDefaultList(getList(listID));
    }


    /**
     * Sets a FormList to the the user's defualt list stored in the database.
     * The FormList (list) must exist in the database before this method is
     * called.
     *
     * @param list the <code>FormList</code> that is the user's currently
     * displayed list.
     */
    public void setDefaultList(FormList list) {
        int index;

        if (list == null)
            throw new NullPointerException("list is null, can't set as default list.");
        ;

        //System.out.println("setDefaultList: is calling:  list.setAsUserDefault(m_user.getID()");

        list.setAsUserDefault(m_user.getID());

        // if the FormList is already in this object
        if ((index = m_lists.indexOf(list)) != -1) {
            m_display_list_idx = index;
        }
        // otherwise add it and set as display list.
        else {
            addList(list);
            m_display_list_idx = m_lists.size() - 1;
        }
    }


    /**
     * @return the list_id of the FormList that is currently set to be
     * displayed.
     */
    public String getDisplayListId()
            throws FormException {
        FormList list;

        list = getDisplayList();

        if (list != null) {
            //System.out.println("getDisplayListId() returning [" + list.m_list_id + "]");
            return list.m_list_id;
        }

        return null;
    }


    /**
     * @return the FormList that is currently set to be displayed.
     */
    public FormList getDisplayList()
            //throws FormException
    {
        //System.out.println("***********In getDisplayList()");
        FormList list = null;

        // look for the display list in memory
        if (m_lists != null && (m_display_list_idx != -1)) {
            list = (FormList)m_lists.get(m_display_list_idx);
        }
        // not in memory, get the lists from the database
        if (list == null) {
            //System.out.println("getDisplayList() no display list found in memory... doing loadLists()");
            try {
                this.loadLists(false, ALL_AVAILABLE);
                list = getDefaultList();
                m_display_list_idx = m_lists.indexOf(list);
            } catch (PersistenceException pe) {
                //throw new FormException("Could not get display list");
            }
        }
        return list;
    }


    /**
     * Set the display list by it's ID.
     *
     * @param listID the database ID of the FormList that is the user's
     * currently displayed list.
     */
    public void setDisplayListID(String listID) {
        setDisplayList(getList(listID));
    }


    /**
     * @param list the <code>FormList</code> that is the user's currently
     * displayed list.
     */
    public void setDisplayList(FormList list) {
        int index;

        // if the FormList is already in this object
        if ((index = m_lists.indexOf(list)) != -1) {
            m_display_list_idx = index;
        }
        // otherwise add it and set as display list.
        else {
            addList(list);
            m_display_list_idx = m_lists.size() - 1;
        }

        // the data is dependent on the FormList being presented.  We don't extract data for all possible fields of the Form for performance reasons.
        m_data.clear();
    }

    /**
     * Get the next data sequence number for this form (class).<br>
     * This method uses the DBBean object in the Form instance. It will RELEASE
     * the DBBean upon completion.
     *
     * @return the sequence number.  -1 returned if there is an error getting
     * from the database.
     */
    public int dbGetNextDataSequence() {
        int dataSequence = 0;

        try {
            dataSequence = dbGetNextDataSequence(db);
            if (dataSequence == -1) {
                db.connection.rollback();
            } else {
                db.connection.commit();
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Form.class).error("Form.dbGetNextDataSequence failed " + sqle);
            dataSequence = -1;
        } finally {
            db.release();
        }

        return dataSequence;
    }

    /**
     * Get the next data sequence number for this form (class).<br>
     * This method accepts a DBBean parameter.  It will use the existing connection
     * in that DBBean (or open a new one if there is not already one).  It will generate
     * its own statements. It will NOT perform any commit or rollback statements.<br>
     * <b>Preconditions:</b><br>
     * <li>none</li>
     * <br>
     * <b>Postconditions:</b><br>
     * <li>db has an open connection, auto commit is FALSE</li>
     * <li>there are uncommitted transactions in that connection</li>
     *
     * @param db the DBBean object to use for the transaction.
     * @return the sequence number.  -1 returned if there is an error getting from the database.
     * In this case, the calling method should perform a rollback on the connection.
     */
    protected int dbGetNextDataSequence(DBBean db) {
        int data_sequence = 0;
        String query_string;
        java.sql.Statement stmt = null;
        java.sql.ResultSet result = null;


        try {
            // Open a connection if there is not already one
            db.openConnection();
            db.connection.setAutoCommit(false);

            // get the next data object sequence number for this form.
            query_string = "select next_data_seq from pn_class where class_id =" + m_class_id;
            stmt = db.connection.createStatement();
            result = stmt.executeQuery(query_string);

            if (!result.next() || (data_sequence = result.getInt(1)) == -1) {
                m_error_message = "Cound not get new data sequence number from the database.<br>Contact Administrator.";
                return -1;
            }
            result.close();
            stmt.close();

            // Increment the sequence number in the database.
            query_string = "update pn_class set next_data_seq=" + (data_sequence + 1) + " where class_id=" + m_class_id;
            stmt = db.connection.createStatement();
            result = stmt.executeQuery(query_string);

            if (!result.next()) {
                m_error_message = "Cound not get new data sequence number from the database.<br>Contact Administrator.";
                return -1;
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(Form.class).error("Form.dbGetNextDataSequence failed " + sqle);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException sqle2) {
                // Can do nothing in this situation
            }
        }

        return data_sequence;
    }


    /**
     * Add this form to a project.net Space (personal space, project space,
     * business space, work space, methodology). This will make the form
     * visible, available and usable by people who have the correct ACLs in the
     * new Space.
     *
     * @param space the Space to be given access to the Form.
     */
    public void addToSpace(Space space) {
        String query;

        // is the form (class_id) already available to the Space?
        query = "select class_id from pn_space_has_class where class_id=" + m_class_id + " and space_id=" + space.getID();
        try {
            db.setQuery(query);
            db.executeQuery();

            // If it's not already there add it.  If it is already there, there is nothing to update.
            // When adding, space does not become owner.  The owner will have been
            // determined when the form was created
            if (!db.result.next()) {
                query = "insert into pn_space_has_class (space_id, class_id, is_owner) " +
                        "values (" + space.getID() + ", " + m_class_id + ", " + Conversion.booleanToInt(false) + ") ";
                db.release();
                db.setQuery(query);
                db.executeQuery();
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(Form.class).error("Form.addToSpace failed " + sqle);
        } finally {
            db.release();
        }
    }


    /**
     * Get the human-readable status of the form.
     * <pre>
     * Record Status:
     * A = In Use (active)
     * P = Hidden (pending)
     * R = Read-only
     * </pre>
     */
    public String getStatusName() {
        if (m_record_status == null)
            return PropertyProvider.get("prm.form.designer.activate.form.status.unknown.value");
        else if (m_record_status.equals("A"))
            return PropertyProvider.get("prm.form.designer.activate.form.status.inuse.value");
        else if (m_record_status.equals("P"))
            return PropertyProvider.get("prm.form.designer.activate.form.status.hidden.value");
        else if (m_record_status.equals("R"))
            return PropertyProvider.get("prm.form.designer.activate.form.status.readonly.value");
        else
            return PropertyProvider.get("prm.form.designer.activate.form.status.unknown.value");
    }

    public String getSharingStatusName() {
        if (sharedFormVisible)
            return PropertyProvider.get("prm.form.designer.activate.form.status.inuse.value");
        else 
            return PropertyProvider.get("prm.form.designer.activate.form.status.hidden.value");
    }    
    

    /**
     * Gets all the FormLists from the database that are in the user's current
     * scope. User-defined, current space-defined and methodology-defined lists
     * will be retrieved from the database.
     *
     * @param allLists if true loads all lists regardless of record_status,
     * otherwise loads only lists with record_status='A' (active).
     */
    public void loadLists(boolean allLists)
            throws PersistenceException {
        loadLists(allLists, ALL_AVAILABLE);
    }


    /**
     * Gets all the FormLists from the database that are in the specified scope.
     * User-defined, current space-defined, owning space-defined and
     * methodology-defined lists will be retrieved from the database.
     *
     * @param allLists if true loads all lists regardless of record_status, otherwise
     * loads only lists with record_status='A' (active).
     * @param listScope the scope of the lists to load. One of ALL_AVAILABLE,
     * USER_LISTS, SHARED_LISTS, ADMIN_LISTS
     */
    public void loadLists(boolean allLists, int listScope)
            throws PersistenceException {
        FormList list = null;
        FormField field;
        FormList personal_default_list = null;
        FormList space_default_list = null;
        String query_string;
        String current_list_id = null;
        String list_id;
        String field_order;
        String field_width;
        boolean is_subfield;
        boolean is_list_field;
        boolean is_calculate_total;
        boolean no_wrap;
        boolean is_sort_field;
        int sort_order;
        boolean sort_ascending;
        String space_id;
        boolean is_default;

        //System.out.println("In Form.loadLists()");

        if (m_class_id == null)
            throw new NullPointerException("m_class_id is null");

        // Clear the old lists in memory.
        m_lists.clear();

        try {
            if (m_space == null)
                m_space = SessionManager.getUser().getCurrentSpace();
            if (m_user == null)
                m_user = SessionManager.getUser();
        } catch (NullPointerException nulle) {
            throw new PersistenceException("Internal error:  Space or User not defined.  Please report this error", nulle);
        }

        // Select all list details for lists belonging to this form class
        // Where the list belongs to the current space or the form's owning space
        query_string = "select cl.list_id, cl.list_name, cl.list_desc, cl.field_cnt, clf.field_id, clf.field_order, clf.field_width, clf.is_subfield, " +
                "clf.wrap_mode, clf.is_sort_field, clf.sort_order, clf.sort_ascending, cl.owner_space_id, shcl.is_default, cl.crc, clf.is_list_field, cl.is_shared, cl.is_admin, clf.is_calculate_total " +
                "from pn_class_list cl, pn_space_has_class_list shcl, pn_class_list_field clf " +
                "where cl.class_id=" + m_class_id + " " +
                " and (shcl.space_id = " + m_space.getID() + " or shcl.space_id = " + getOwningSpaceID() + ") " +
                "and shcl.class_id = cl.class_id and shcl.list_id = cl.list_id ";

        // User has access to his own lists, admin-defined lists, and other user's shared lists..
        if (listScope == ALL_AVAILABLE) {
            query_string += "and ((cl.owner_space_id=" + m_user.getID() + ") or (cl.owner_space_id=" + m_space.getID() + ") or (cl.is_shared=1)) ";
        }
        // Just the user's personal lists
        else if (listScope == USER_LISTS) {
            query_string += "and cl.owner_space_id=" + m_user.getID() + " ";
        }
        // Just the other user's shared lists.
        else if (listScope == SHARED_LISTS) {
            query_string += "and cl.owner_space_id <>" + m_user.getID() + " and cl.is_shared=1 ";
        }
        // Space Administrator defined lists.
        else if (listScope == ADMIN_LISTS) {
            query_string += "and cl.is_admin=1 ";
        }

        query_string += "and clf.class_id = cl.class_id and clf.list_id = cl.list_id ";

        if (allLists)
            query_string += "and cl.record_status in ('A', 'P') ";
        else
            query_string += "and cl.record_status='A' ";

        // query_string += "order by cl.owner_space_id, cl.list_id asc, clf.field_order asc";
        query_string += "order by cl.list_name asc, clf.field_order asc";

        try {
            db.setQuery(query_string);
            db.executeQuery();

            // some forms (element forms and propery sheets have no lists.
            // Don't throw, just return.  Check for null lists later and throw to the user if needed.
            if (!db.result.next())
                return;

            do {
                //System.out.println("LoadLists() Got field:  field_id: " +   db.result.getString(1)  + " element_name: " +  db.result.getString(3));
                list_id = db.result.getString(1);

                // new list
                if (!list_id.equals(current_list_id)) {
                    current_list_id = list_id;
                    list = new FormList(this);
                    list.setID(list_id);
                    list.m_list_name = db.result.getString(2);
                    list.m_list_description = db.result.getString(3);
                    list.m_field_cnt = db.result.getInt(4);
                    list.m_owner_space_id = db.result.getString(13);
                    list.m_crc = db.result.getTimestamp(15);
                    list.m_is_default = Conversion.toBool(db.result.getString(14));
                    list.m_is_shared = Conversion.toBool(db.result.getString(17));
                    list.m_is_admin = Conversion.toBool(db.result.getString(18));
                    list.loadFilters();

                    this.addList(list);
                }

                field = this.getField(db.result.getString(5));       // are we sure the Form has the fields?


                if (field == null) {

                }

                field_order = db.result.getString(6);
                field_width = db.result.getString(7);
                is_subfield = db.result.getBoolean(8);
                no_wrap = Conversion.toBool(db.result.getString(9));
                is_sort_field = Conversion.toBool(db.result.getString(10));
                sort_order = db.result.getInt(11);
                sort_ascending = Conversion.toBool(db.result.getString(12));
                space_id = db.result.getString(13);
                is_default = Conversion.toBool(db.result.getString(14));
                is_list_field = Conversion.toBool(db.result.getString(16));
                is_calculate_total = Conversion.toBool(db.result.getString(19));

                // TODO -- need to keep track of cascading list defaults in order to set to correct initial display list.
                // For now, we use the person's defualt list if they have one, otherwise take the default list from the last scope we see.
                if (is_default) {
                    if (space_id.equals(m_user.getID()))
                        personal_default_list = list;
                    else
                        space_default_list = list;
                }

                // add the field to the FormList
                list.addField(field, field_order, field_width, is_list_field, is_calculate_total, is_subfield, no_wrap, is_sort_field, sort_order, sort_ascending);

            } while (db.result.next());
        } catch (SQLException sqle) {
        	Logger.getLogger(Form.class).error("Form.loadLists failed " + sqle);
            throw new PersistenceException("Failed to load form lists", sqle);
        } finally {
            db.release();
        }

        // Set the initial display list, now that we have all the lists.
        if (personal_default_list != null) {
            setDisplayList(personal_default_list);
        } else if (space_default_list != null) {
            setDisplayList(space_default_list);
        }
        // worst case, set the last list as default, so things will not break
        else {
            setDisplayList(list);
        }
    }


    /**
     * Get all fields for this from from the database. Gets all the fields in
     * the user's scope.
     *
     * @param allFields if true loads all field regardless of record_status,
     * otherwise loads only fields with record_status='A' (active).
     */
    public void loadFields(boolean allFields) throws PersistenceException {

        String query_string;
        FormField field;

        m_error_message = null;
        m_fields.clear();

        if (m_class_id == null)
            throw new NullPointerException("m_class_id is null");

        // Preload some field for form fields.  This will prevent us from doing
        // a round trip for each row later.
        Map fieldDomainCache = FieldDomain.loadFormFieldDomains(this, m_class_id);

        // Get the FormFields
        query_string = "select cf.field_id, e.element_id, e.element_name, e.db_field_datatype, cf.field_label, cf.data_table_name, cf.data_column_name, " +
                "cf.data_column_size, cf.data_column_scale, cf.data_column_exists, cf.row_num, cf.row_span, cf.column_num, cf.column_span, cf.field_group, cf.domain_id," +
                "cf.max_value, cf.min_value, cf.default_value, ep.default_value as tag, e.element_label, edc.class_id, cf.instructions_clob, cf.record_status, " +
                "cf.is_multi_select, cf.use_default, cf.column_id, cf.crc, cf.is_value_required, cf.hidden_for_eaf " + 
                "from pn_class_field cf , pn_element e, pn_element_property ep, pn_element_display_class edc " +
                "where cf.class_id=" + m_class_id + " and e.element_id = cf.element_id and edc.element_id (+)= cf.element_id and ep.element_id(+)= cf.element_id and ep.property_type(+)='tag' ";
        if (allFields)
            query_string += "and cf.record_status in ('A', 'P') ";
        else
            query_string += "and cf.record_status='A' ";

        query_string += "order by cf.row_num asc, cf.column_num asc";
        try {
            db.setQuery(query_string);
            db.executeQuery();

            while (db.result.next()) {
                // use the FieldFactory to create the proper types of fields based on the field's type (element_id).
                // create a IFormField object of the proper type for each field type (element_name).
                field = FieldFactory.makeField(
                        DBFormat.toInt(db.result.getString(2)), // element_id (field type)
                        this, // Form
                        db.result.getString(1)                                // field_id
                );
                //System.out.println("LoadField() Got field label: " + db.result.getString(5) + ":  field_id: " +   db.result.getString(1)  + " element_name: " +  db.result.getString(3));

                if (field != null) {
                    field.m_element_id = db.result.getString("element_id");
                    field.m_element_name = db.result.getString("element_name");
                    field.m_db_datatype = db.result.getString("db_field_datatype");
                    field.m_field_label = db.result.getString("field_label");
                    field.m_data_table_name = db.result.getString("data_table_name");
                    field.m_data_column_name = db.result.getString("data_column_name");
                    // some fields have static column sizes that should not be replaced.
                    if (field.m_data_column_size == -1)
                        field.m_data_column_size = db.result.getInt("data_column_size");
                    field.m_data_column_scale = db.result.getInt("data_column_scale");
                    field.m_data_column_exists = db.result.getBoolean("data_column_exists");
                    field.m_row_num = db.result.getInt("row_num");
                    field.m_row_span = db.result.getInt("row_span");
                    field.m_column_num = db.result.getInt("column_num");
                    field.m_column_span = db.result.getInt("column_span");
                    field.m_field_group = db.result.getString("field_group");

                    //Get the field domain for this field
                    FieldDomain fd = (FieldDomain)fieldDomainCache.get(db.result.getString("domain_id"));
                    if (fd == null) {
                        fd = new FieldDomain();
                        fd.setForm(this);
                        fd.setField(field);
                        fd.setID(db.result.getString("domain_id"));
                    } else {
                        fd.setField(field);
                    }
                    field.setDomainNoStore(fd);
                    field.m_max_value = db.result.getString("max_value");
                    field.m_min_value = db.result.getString("min_value");
                    field.m_default_value = db.result.getString("default_value");
                    field.m_tag = db.result.getString("tag");
                    field.m_element_label = db.result.getString("element_label");
                    if (db.result.getString("class_id") != null) {
                        // Only assign the loaded display class id if we got one
                        // some fields do not have a display class id in the database
                        field.m_elementDisplayClassID = db.result.getString("class_id");
                    }
                    field.m_instructions = ClobHelper.read(db.result.getClob("instructions_clob"));
                    field.m_record_status = db.result.getString("record_status");
                    field.m_isMultiSelect = Conversion.toBool(db.result.getInt("is_multi_select"));
                    field.m_useDefault = Conversion.toBool(db.result.getInt("use_default"));
                    field.m_column_id = db.result.getString("column_id");
                    field.m_crc = db.result.getTimestamp("crc");

                    // Resolve any token values for attributes which may
                    // have tokens in the database
                    if (PropertyProvider.isToken(field.m_field_label)) {
                        field.m_field_label = PropertyProvider.get(field.m_field_label);
                    }
                    if (PropertyProvider.isToken(field.m_element_label)) {
                        field.m_element_label = PropertyProvider.get(field.m_element_label);
                    }

                    field.isValueRequired = Conversion.toBool(db.result.getInt("is_value_required"));
                    field.hiddenForEaf = Conversion.toBool(db.result.getInt("hidden_for_eaf"));

                    // Get properties and domain values for this field
                    field.loadProperties();

                    // save this field in the ArrayList.
                    m_fields.add(field);
                }
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(Form.class).error("Form.loadFields failed " + sqle);
            throw new PersistenceException("Failed to load form fields", sqle);
        } finally {
            db.release();
        }
    }

    protected void clearWorkflows() {
        m_workflows.clear();
        isWorkflowsLoaded = false;
    }

    /**
     * Load the workflows for this form.  If none are defined for this form,
     * an empty collection is loaded.<br>
     *
     * <b>Postconditions:</b><br>
     * <li>Workflows are available through {@link #getWorkflows}</li>
     * @throws PersistenceException if there is a problem loading the workflows
     */
    public void loadWorkflows() throws PersistenceException {
        Form.Workflow workflow;
        String workflowID;
        boolean isDefault;
        StringBuffer selectQuery = new StringBuffer();

        clearWorkflows();

        selectQuery.append("select cwf.class_id, cwf.workflow_id, cwf.is_default ");
        selectQuery.append("from pn_class_has_workflow cwf, pn_workflow wf ");
        selectQuery.append("where cwf.workflow_id = wf.workflow_id ");
        selectQuery.append("and wf.is_published = 1 ");
        selectQuery.append("and class_id = " + m_class_id + " ");

        try {
            db.executeQuery(selectQuery.toString());
            while (db.result.next()) {
                workflowID = db.result.getString("workflow_id");
                isDefault = Conversion.toBoolean(db.result.getString("is_default"));
                workflow = new Form.Workflow(workflowID, isDefault);
                m_workflows.add(workflow);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Form.class).error("Form.loadWorkflows failed " + sqle);
            throw new PersistenceException("Failed to load workflows for form.", sqle);
        } finally {
            db.release();
        }

        this.isWorkflowsLoaded = true;

    }


    /**
     * Has the Form been loaded from database persistence?
     */
    public boolean isLoaded() {
        return m_isLoaded;
    }

    /**
     * Set whether the form has been loaded from database persistence?
     */
    public void setLoaded(boolean b) {
        m_isLoaded = b;
    }

    /**
     * Retrieve all data for this Form from the the database. All the FormLists
     * and FormFields in the user's (m_person_id) scope will be restored from
     * the database also.
     */
    public void load(String class_id) throws PersistenceException {
        m_class_id = class_id;
        this.load();
    }


    /**
     * Retrieve this Form (class_id) from the the database. All the FormLists
     * and FormFields in the user's (m_person_id) scope will be restored from
     * the database also.
     */
    public void load() throws PersistenceException {
        String query_string;

        // New form coming in from the database, clear everything from the old one.
        //clear();

        //System.out.println("Form.load()  Cleared Form, getting new form data from database. \n");
        // get form (class) info
        query_string = "select class_name, class_type_id, class_desc, class_abbreviation, max_row, max_column, owner_space_id, " +
                "methodology_id, master_table_name, data_table_key, is_sequenced, record_status, is_system_class, crc, " +
                "supports_discussion_group, supports_document_vault, supports_assignment, " +
                "supports_external_access, external_class_id, hide_assignment_fields_in_eaf, shared " +
                "from pn_class where class_id=" + m_class_id;
        try {
            db.setQuery(query_string);
            db.executeQuery();

            if (!db.result.next())
                throw new PersistenceException("Form could not be loaded from database.  Contact Administrator.");

            m_class_name = db.result.getString("class_name");
            m_class_type_id = db.result.getString("class_type_id");
            m_class_description = db.result.getString("class_desc");
            m_class_abbreviation = db.result.getString("class_abbreviation");
            m_max_row = db.result.getInt("max_row");
            m_max_column = db.result.getInt("max_column");
            m_owner_space_id = db.result.getString("owner_space_id");
            m_methodology_id = db.result.getString("methodology_id");
            m_master_table_name = db.result.getString("master_table_name");
            m_dataTableKey = db.result.getString("data_table_key");
            m_isSequenced = db.result.getBoolean("is_sequenced");
            m_record_status = db.result.getString("record_status");
            m_isSystemForm = db.result.getBoolean("is_system_class");
            m_crc = db.result.getTimestamp("crc");
            m_supports_discussion_group = db.result.getBoolean("supports_discussion_group");
            m_supports_document_vault = db.result.getBoolean("supports_document_vault");
            m_supports_assignment = db.result.getBoolean("supports_assignment");
            m_supports_external_access = db.result.getBoolean("supports_external_access");
            m_external_class_id = db.result.getString("external_class_id");
            m_hide_assignment_fields_in_eaf = db.result.getBoolean("hide_assignment_fields_in_eaf");
            shared = db.result.getBoolean("shared"); 

            // get FormFields and FormLists in the users's scope for this Form.
            // fields must be retrieved first.  loadLists() set's the display list and default list.
            this.loadFields(false);
            //if (shared){
            	this.loadSharedFormSpaceIds();
            //}
            m_isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(Form.class).error("Form.load failed " + sqle);
            throw new PersistenceException("Failed to load form", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Retrieve this Form (class_id) from the the database. All the FormLists
     * and FormFields in the user's (m_person_id) scope will be restored from
     * the database also.
     */
    public void loadByExternalClassId(String currentSpaceID) throws PersistenceException {
        String query_string;

        // New form coming in from the database, clear everything from the old one.
        //clear();

        //System.out.println("Form.load()  Cleared Form, getting new form data from database. \n");
        // get form (class) info
        query_string = "select c.class_id, c.class_name, c.class_type_id, c.class_desc, c.class_abbreviation, c.max_row, c.max_column, " +
        		"c.owner_space_id, " +
                "c.methodology_id, c.master_table_name, c.data_table_key, c.is_sequenced, c.record_status, c.is_system_class, c.crc, " +
                "c.supports_discussion_group, c.supports_document_vault, c.supports_assignment, " +
                "c.supports_external_access, c.external_class_id, c.hide_assignment_fields_in_eaf, c.shared " +
                "from pn_class c" ;
        if (currentSpaceID != null && currentSpaceID.trim().length() > 0){
        	query_string = query_string + ", pn_space_has_class shc";	
        }
        	
        query_string = query_string + "  where supports_external_access = 1 and record_status = 'A' and external_class_id=" + m_external_class_id;
        if (currentSpaceID != null && currentSpaceID.trim().length() > 0){
        	query_string = query_string + "  and c.class_id = shc.class_id and shc.visible = '1' and shc.space_id = " + currentSpaceID ;
        }
        try {
            db.setQuery(query_string);
            db.executeQuery();

            if (!db.result.next())
                throw new PersistenceException("Form could not be loaded from database.  Contact Administrator.");

            m_class_name = db.result.getString("class_name");
            m_class_type_id = db.result.getString("class_type_id");
            m_class_description = db.result.getString("class_desc");
            m_class_abbreviation = db.result.getString("class_abbreviation");
            m_max_row = db.result.getInt("max_row");
            m_max_column = db.result.getInt("max_column");
            m_owner_space_id = db.result.getString("owner_space_id");
            m_methodology_id = db.result.getString("methodology_id");
            m_master_table_name = db.result.getString("master_table_name");
            m_dataTableKey = db.result.getString("data_table_key");
            m_isSequenced = db.result.getBoolean("is_sequenced");
            m_record_status = db.result.getString("record_status");
            m_isSystemForm = db.result.getBoolean("is_system_class");
            m_crc = db.result.getTimestamp("crc");
            m_supports_discussion_group = db.result.getBoolean("supports_discussion_group");
            m_supports_document_vault = db.result.getBoolean("supports_document_vault");
            m_supports_assignment = db.result.getBoolean("supports_assignment");
            m_supports_external_access = db.result.getBoolean("supports_external_access");
            m_class_id = db.result.getString("class_id");
            m_hide_assignment_fields_in_eaf = db.result.getBoolean("hide_assignment_fields_in_eaf");
            shared = db.result.getBoolean("shared");

            // get FormFields and FormLists in the users's scope for this Form.
            // fields must be retrieved first.  loadLists() set's the display list and default list.
            this.loadFields(false);
            if (this.getSpace() == null){
            	loadOwnerSpace();
            	setSpace(ownerSpace);
            }
            	
            m_isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(Form.class).error("Form.load failed " + sqle);
            throw new PersistenceException("Failed to load form", sqle);
        } finally {
            db.release();
        }
    }    
    

    /**
     * Soft delete this form.
     */
    public void remove()
            throws PersistenceException {
        try {
            db.setQuery("update pn_class set record_status='D' where class_id=" + m_class_id);
            db.executeQuery();
        } catch (SQLException sqle) {
        	Logger.getLogger(Form.class).error("Form.remove failed " + sqle);
        } finally {
            db.release();
        }
    }


    /**
     * Stores only the data stored in the form. Same as calling
     * Form.getData().store().
     * @throws SQLException 
     */
    public void store() throws PersistenceException, SQLException {
        storeData();
    }


    public void loadSharedFormSpaceIds() throws SQLException{
    	String query = "select fv.space_id, fv.child_ids as child_ids, o.name from pn_shared_forms_visiblity fv, pn_object_name o where o.object_id = fv.space_id and fv.class_id = " + m_class_id;
        db.setQuery(query);        
        db.executeQuery();
        HashMap<String, HashMap<String,String>> spaceIds = new HashMap<String, HashMap<String,String>>(); 
        while (db.result.next()){
        	Clob childIdsStr = db.result.getClob("child_ids");
        	String spaceId = db.result.getString("space_id");
        	String spaceName = db.result.getString("name");
        	HashMap<String,String> extras = new HashMap<String, String>();
        	extras.put("spaceName", spaceName);
        	String childIds = "";
        	if(childIdsStr != null){
        		long len = childIdsStr.length();
        		childIds = childIdsStr.getSubString(1l, (int)len);
        	}
        	
        	extras.put("childIds", childIds);
        	spaceIds.put(spaceId, extras);
        }
        if (spaceIds.size() == 0){
        	HashMap<String,String> extras = new HashMap<String, String>();
        	extras.put("spaceName", this.getSpace().getName());
        	spaceIds.put(this.getSpace().getID(), extras);
        }
        setSharedFormSpaceIds(spaceIds);
    }
    
    /**
     * Retrieve the data for this form from the datase.
     */
    public void loadData(String data_object_id) throws PersistenceException {
        m_data.load(data_object_id);
    }


    /**
     * @return true if this Form object exists in the database. false if it does
     *         not exist. Check is done using the class_id only.
     */
    public boolean dbExists() {
        String query_string;

        query_string = "select class_id from pn_class where class_id=" + m_class_id;

        try {
            db.setQuery(query_string);
            db.executeQuery();

            if (!db.result.next())
                return false;
            else
                return true;
        } catch (SQLException sqle) {
        	Logger.getLogger(Form.class).error("Form.dbExists failed " + sqle);
        } finally {
            db.release();
        }
        return false;
    }


    /**
     * Store the data for this form to the datase.
     * @throws SQLException 
     */
    public void storeData() throws PersistenceException, SQLException {
        m_data.store();
    }


    /**
     * Get the data from a HTTP post and save in the FormData object.
     */
    public void processHttpPost(javax.servlet.ServletRequest request) throws FormException {
        FormField field;
        FieldData fieldData;
        String class_id;
        String data_object_id;

        // The form is expected to always return the following Hidded fields:
        // class_id  -- the form who's data we are storing
        // object_id -- the instance of the form (data tables & columns) where the data is to be stored.
        class_id = request.getParameter("class_id");
        data_object_id = request.getParameter("data_object_id");

        if (!class_id.equals(m_class_id)) {
            // The form type being posted is different than the form type we have in memory,
            // must get the right Form object from the database  (damn browser back buttons).
            // NOTE: this should never happen if Forms are put into Session Cache by "class_id".
            try {
                this.load(class_id);
            } catch (PersistenceException pe) {
                throw new FormException("Could not store form.");
            }
        }

        if(request.getParameter("email") != null){
        	EmailValidator emailValidator = EmailValidator.getInstance();
        	String email = request.getParameter("email");
        	if (!emailValidator.isValid(email)){
        		errors.put("email", email, PropertyProvider.get("prm.form.email.validate.wrongformat.message"));
        	}
        }
        // GET DATA
        // Get the posted HTTP parameters (data) for each field on the form.
        m_data.clear();
        m_data.setID(data_object_id);
        m_data.setCurrentSpace(this.m_space);
        if (request.getParameter("externalFormId") != null){
        	m_data.setCreatorEmail(request.getParameter("email"));
        	m_data.setExternalRecord(true);
        }
        if(m_data.m_create_person_id != null && !m_data.m_create_person_id.equals("-1")){
        	m_data.setCreatorEmail(null);
        }

        for (int i = 0; i < m_fields.size(); i++) {
            // Delagate parameter processing to each field.
            field = (FormField)m_fields.get(i);
            fieldData = new FieldData(1);
            field.processHttpPost(request, fieldData);

            // Validate, process field data and add to form data

            addValidFieldData(this.m_data, field, fieldData);
        }
    }


    /**
     * Adds the specified fieldData to the specified formData.
     * Validates and process data before adding. After calling, some errors may
     * have been generated if any validation errors occurred.  This may be
     * determined by calling {@link #hasErrors}.
     * The field data is only added to the form data if it is valid; this is
     * to prevent subsequent attempted reformatting of invalid data when
     * the error messages are displayed; it is generally impossible to correctly
     * format invalid data and there is no mechanism available to indicate that
     * the data should not be reformatted when display the error messages.
     * @param formData the form data to add the field data to
     * @param field the field for which data is being added
     * @param fieldData the field data to add to the form data
     */
    private void addValidFieldData(FormData formData, FormField field, FieldData fieldData) {
        StringBuffer errorValue = new StringBuffer();
        StringBuffer fieldValidationErrorMessage = new StringBuffer();
        boolean isValid;

        // Validate that the field data is suitable for this field
        // Returning an appropriate message
        isValid = field.isValidFieldData(fieldData, errorValue, fieldValidationErrorMessage);

        if (isValid) {
            // Now let the specific form field process the data
            // This may modify fieldData
            field.processFieldData(fieldData);

            // most form fields have only one value, but multi-selection lists have multiple values.
            // text areas may now be broken in multiple values if over a max size.
            // some fields (checkboxes) may return null if not checked.
            if (fieldData.size() > formData.getNumDataRows()) {
                formData.setNumDataRows(fieldData.size());
            }

            // Add the field data to the current form data
            formData.put(field.getSQLName(), fieldData);

        } else {
            // Add the error to an errors structure
            errors.put(field.getDataColumnName(), errorValue.toString(), fieldValidationErrorMessage.toString());
        }

    }


    /**
     * Generates an HTML form described by the class_id.
     * Fills in the form with data if the data_object_id exists.
     * The ID of the form will be displayed.
     *
     * @param out the print writer to write the generated HTML to.
     * @throws java.io.IOException if there is a problem writing to the writer
     */
    public void writeHtml(java.io.PrintWriter out) throws java.io.IOException {
        writeHtml(out, true, false, false);
    }

    public void writeHtml(java.io.PrintWriter out, boolean displayID, boolean isEafForm) throws java.io.IOException {
        writeHtml(out, displayID, false, isEafForm);
    }

    /**
     * Generates an HTML form described by the class_id.
     * Fills in the form with data if the data_object_id exists.
     *
     * @param out the print writer to write the generated HTML to.
     * @param displayID if true the ID of the form will be displayed, if false, it will not be displayed.
     * @throws java.io.IOException if there is a problem writing to the writer
     */
    public void writeHtml(java.io.PrintWriter out, boolean displayID, boolean readOnly, boolean isEafForm)
            throws java.io.IOException {
        FormField field;
        FieldData field_data;
        int r, c;
        int field_cnt;
        int current_row;
        int current_column;
        int num_fields;

        //System.out.println("Form.writeHtml():  class= " + m_class_id + "   data_object_id= " + m_data.m_data_object_id + "  num_fields:  " + m_fields.size());

        if (m_class_id == null)
            throw new NullPointerException("m_class_id is null");

        if (m_fields == null || (num_fields = m_fields.size()) < 1)
            throw new NullPointerException("m_fields is null or empty list.");

        // Don't display ID when a new form instance is being created, not assigned until store().
        if ((m_data == null) || (m_data.m_data_object_id == null))
            displayID = false;

        // Render the form's HTML.
        out.println("<!-- Begin rendered form -->");
        out.println("<table class=\"tableContent\" border=\"0\" width=\"100%\">");

        current_row = 0;
        current_column = 0;

        // Don't display the ID on the form if requested by the caller.
        // Write the form ID on the first row/column
        if (displayID) {
            out.println("<tr>");
            out.println(PropertyProvider.get("prm.form.listview.modify.number.description", new Object[] { m_class_abbreviation, String.valueOf(m_data.m_sequence_num) } ));

            out.println(this.m_data.getModificationString() + "</td>");
            out.println("</tr>");
        }

        // For each field on the form.  Fields are sorted by row, column
        for (field_cnt = 0; field_cnt < num_fields; field_cnt++) {
            field = (FormField)m_fields.get(field_cnt);

            if(field.isDesignable() && (!isEafForm || (isEafForm && !field.isHiddenForEaf()))){
	            if (m_data != null)
	                field_data = (FieldData)m_data.get(field.getSQLName());
	            else
	                field_data = null;
	
	            // Assumes four HTML table columns (2 user columns) max allowed.
	            // User column = field label in column i plus input element in column i+1
	
	            // NEW TABLE ROW
	            if (field.m_row_num > current_row) {
	                // if not the first row.
	                if (field_cnt != 0) {
	                    // finish padding out current row with empy columns
	                    for (c = current_column; c < field.m_column_num; c++)
	                        out.print("<td>&nbsp;</td>\n<td>&nbsp;</td>\n");
	                    out.print("</tr>\n");
	                }
	
	                // leave blank rows if needed.  Fields can skip rows for spacing.
	                for (r = current_row; r < field.m_row_num - 1; r++) {
	                    out.print("<tr><td colspan=\"" + m_max_column * 2 + "\">&nbsp;</td></tr>\n");
	                }
	
	                out.print("<tr>\n");
	                current_row = field.m_row_num;
	                current_column = 1;
	            }
	
	            // indent to the desired column
	            for (c = current_column; c < field.m_column_num; c++) {
	                out.println("<td>&nbsp;</td>\n<td>&nbsp;</td>");
	            }
	
	            // next available col on this row
	            current_column = current_column + field.m_column_span;
	
	            // RENDER FIELD
	            // Outputs HTML for field label,  field, field data wapped in <td> element
	
	            if (readOnly) {
	                System.out.println("About to write the value...");
	                field.writeHtmlReadOnly(field_data, out);
	            } else {
	                field.writeHtml(field_data, out);
	            }
            }
        }  // for fields

        out.println("</tr>");
        out.println("</table>");
        out.println("<!-- End rendered form -->");
    }
    
    public String getDetails() {
		FormField field;
		FieldData field_data;
		int r, c;
		int field_cnt;
		int current_row;
		int current_column;
		int num_fields;
		boolean displayID = true;
		boolean isEafForm = false;

		// System.out.println("Form.writeHtml(): class= " + m_class_id + " data_object_id= " + m_data.m_data_object_id +
		// " num_fields: " + m_fields.size());

		if (m_class_id == null)
			throw new NullPointerException("m_class_id is null");

		if (m_fields == null || (num_fields = m_fields.size()) < 1)
			throw new NullPointerException("m_fields is null or empty list.");

		// Don't display ID when a new form instance is being created, not assigned until store().
		if ((m_data == null) || (m_data.m_data_object_id == null))
			displayID = false;

		StringBuffer out = new StringBuffer();

		// Render the form's HTML.
		out.append("<!-- Begin rendered form -->");
		out.append("<table class=\"tableContent\" border=\"0\" width=\"100%\">");

		current_row = 0;
		current_column = 0;

		// Don't display the ID on the form if requested by the caller.
		// Write the form ID on the first row/column
		if (displayID) {
			out.append("<tr>");
			out.append("<td align=\"left\"  class=\"tableHeader\" >Number:</td><td colspan=\"3\">");
			out.append( m_class_abbreviation + "-" + String.valueOf(m_data.m_sequence_num));
			out.append("</td></tr><tr>");
			out.append(this.m_data.getModificationTDString());
			out.append("</tr>");
		}

		// For each field on the form. Fields are sorted by row, column
		for (field_cnt = 0; field_cnt < num_fields; field_cnt++) {
			field = (FormField) m_fields.get(field_cnt);

			if (field.isDesignable() && (!isEafForm || (isEafForm && !field.isHiddenForEaf()))) {
				if (m_data != null)
					field_data = (FieldData) m_data.get(field.getSQLName());
				else
					field_data = null;

				// Assumes four HTML table columns (2 user columns) max allowed.
				// User column = field label in column i plus input element in column i+1

				// NEW TABLE ROW
				if (field.m_row_num > current_row) {
					// if not the first row.
					if (field_cnt != 0) {
						// finish padding out current row with empy columns
						for (c = current_column; c < field.m_column_num; c++)
							out.append("<td>&nbsp;</td>\n<td>&nbsp;</td>\n");
						out.append("</tr>\n");
					}

					// leave blank rows if needed. Fields can skip rows for spacing.
					for (r = current_row; r < field.m_row_num - 1; r++) {
						out.append("<tr><td colspan=\"" + m_max_column * 2 + "\">&nbsp;</td></tr>\n");
					}

					out.append("<tr>\n");
					current_row = field.m_row_num;
					current_column = 1;
				}

				// indent to the desired column
				for (c = current_column; c < field.m_column_num; c++) {
					out.append("<td>&nbsp;</td>\n<td>&nbsp;</td>");
				}

				// next available col on this row
				current_column = current_column + field.m_column_span;

				// RENDER FIELD
				// Outputs HTML for field label, field, field data wapped in <td> element
				out.append(field.writeHtmlReadOnly(field_data));

			}
		} // for fields

		out.append("</tr>");
		out.append("</table>");
		out.append("<!-- End rendered form -->");
		return out.toString();
	}

    
    public String writeHtml(){
        FormField field;
        FieldData field_data;
        int field_cnt;

        int num_fields;

        StringBuffer formHtml = new StringBuffer();
        
        if (m_class_id == null)
            throw new NullPointerException("m_class_id is null");

        if (m_fields == null || (num_fields = m_fields.size()) < 1)
            throw new NullPointerException("m_fields is null or empty list.");


        // Render the form's HTML.
        formHtml.append("\n<table  border=\"0\" width=\"100%\">");

        // For each field on the form.  Fields are sorted by row, column
        boolean firstRow = false;
        for (field_cnt = 0; field_cnt < num_fields; field_cnt++) {
            field = (FormField)m_fields.get(field_cnt);
            if(field.isDesignable() && !field.hiddenForEaf && !(field instanceof FormID)){
            	
	            if (m_data != null)
	                field_data = (FieldData)m_data.get(field.getSQLName());
	            else
	                field_data = null;	
	                // if not the first row.
	                if (field_cnt != 0 && firstRow) {
	                    formHtml.append("</tr>\n");
	                }		
	                formHtml.append("<tr>");
	                firstRow = true;
	            // Outputs HTML for field label,  field, field data wrapped in <td> element
	             formHtml.append(field.writeHtmlReadOnly(field_data));
            }
        }  
        formHtml.append("</tr>");
        formHtml.append("</table>");
        
        return formHtml.toString();
    }
    
    /**
     * Converts the Form to XML representation without the XML version tag. This
     * method returns the From as XML text.
     *
     * @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer(300);

        try {
            if (!isLoaded())
                this.load();
        } catch (PersistenceException pe) {
            System.out.println("Could not load() form. ********** ");
            return "";
        }

        // form list properties
        xml.append("<Form>\n");
        xml.append(getXMLElements());

        if (ownerSpace != null) {
            xml.append("<Space>\n");
            xml.append("<name>" + XMLUtils.escape(ownerSpace.getName()) + "</name>\n");
            xml.append("<SpaceType>" + XMLUtils.escape(ownerSpace.getSpaceType().getName()) + "</SpaceType>\n");
            xml.append("</Space>\n");
        }
        xml.append("</Form>\n");
        return xml.toString();
    }
    
    /**
     * @return record status of form.
     */
    public String getRecordStatus(){
    	return this.m_record_status;
    }
    /**
     * @return the elements of this form's XML.
     */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();

        xml.append("<id>" + m_class_id + "</id>\n");
        xml.append("<class_type_id>" + m_class_type_id + "</class_type_id>\n");
        xml.append("<name>" + XMLUtils.escape(m_class_name) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(m_class_description) + "</description>\n");
        xml.append("<abbreviation>" + XMLUtils.escape(m_class_abbreviation) + "</abbreviation>\n");
        xml.append("<master_table_name>" + XMLUtils.escape(m_master_table_name) + "</master_table_name>\n");
        xml.append("<max_row>" + m_max_row + "</max_row>\n");
        xml.append("<max_column>" + m_max_column + "</max_column>\n");
        xml.append("<url>" + getURL() + "</url>\n");
        xml.append("<support_external_access>" + getSupportsExternalAccess() + "</support_external_access>\n");
        xml.append("<hide_assignment_field_in_eaf>" + isAssignmentFieldHiddenInEaf() + "</hide_assignment_field_in_eaf>\n");

        // the Space context
        if (m_space != null)
            xml.append(m_space.getXMLBody());

        // the User context
        if (m_user != null)
            xml.append(m_user.getXMLBody());

        // FormFields
        for (int i = 0; i < m_fields.size(); i++)
            xml.append(((FormField)m_fields.get(i)).getXMLBody());

        // FormLists
        for (int i = 0; i < m_lists.size(); i++)
            xml.append(((FormList)m_lists.get(i)).getXMLBody());

        return xml.toString();
    }

    /**
     * Converts the object to XML representation of the Form including XML
     * subnodes for all FormFields and FormLists. This method returns the Form
     * as XML text.
     *
     * @return XML representation of the form
     */
    public String getXML() {
        return ("<?xml version=\"1.0\" ?>\n" + getXMLBody());
    }

    /**
     * @return a <code>String</code> representation of the form
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(150);
        String dataOk;
        int i;

        sb.append("++++++ Form dump ++++++");
        sb.append("m_class_id = " + m_class_id);
        sb.append("m_class_type_id= " + m_class_type_id);
        sb.append("m_methodology_id = " + m_methodology_id);
        sb.append("m_class_name  = " + m_class_name);
        sb.append("m_class_description " + m_class_description);
        sb.append("m_class_abbreviation = " + m_class_abbreviation);
        sb.append("m_user.getID() = " + m_user.getID());
        sb.append("m_max_row = " + m_max_row);
        sb.append("m_max_column= " + m_max_column);
        sb.append("m_fields= " + m_fields.size());
        sb.append("m_lists= " + m_lists.size());
        if (m_data == null)
            dataOk = "null";
        else
            dataOk = "ok";
        sb.append("m_data: " + dataOk);
        sb.append("m_display_list_idx = " + m_display_list_idx);
        sb.append("m_default_list_idx = " + m_default_list_idx);
        sb.append("m_error_message = " + m_error_message);
        sb.append("++++++ End Form dump ++++++");

        // dump this form's fields also
        if (m_fields.size() > 0) {
            for (i = 0; i < m_fields.size(); i++)
                sb.append((m_fields.get(i)).toString());
        }
        return sb.toString();
    }

    /*
    * TBC - 07/01/01 - Tim
    * FormData XML is the minimum amount of XML required to render form
    * data for iMode telephone device;  it is sufficient there for read
    * only view.  These methods may be required to be re-engineered.
    * In that case, salesmode.imode.form.include.FormEdit.jsp needs fixed.
    */

    /**
     * Returns XML including this form and its loaded data.
     * Used for rendering entire form instance from XML
     *
     * @since emu
     */
    public String getFormDataXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION +
                getFormDataXMLBody();
    }

    /**
     * Returns XML including this form and its loaded data.
     * Used for rendering entire form instance from XML
     *
     * @since emu
     */
    public String getFormDataXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<Form>\n");
        xml.append(getXMLElements());
        xml.append(m_data.getXMLBody());

        if (ownerSpace != null) {
            xml.append("<Space>\n");
            xml.append("<name>" + XMLUtils.escape(ownerSpace.getName()) + "</name>\n");
            xml.append("<SpaceType>" + XMLUtils.escape(ownerSpace.getSpaceType().getName()) + "</SpaceType>\n");
            xml.append("</Space>\n");
        }

        xml.append("</Form>\n");

        return xml.toString();
    }

    /* End of FormData XML methods */
    private void loadOwnerSpace() {
        try {
            // Temporary thing ---- getting owner space
            ownerSpace = SpaceFactory.constructSpaceFromID(m_owner_space_id);
            ownerSpace.load();

        } catch (PersistenceException pe) {
        	Logger.getLogger(Form.class).error("Form.loadOwnerSpace() failed " + pe);
        }
    }

    private ValidationErrors errors = new ValidationErrors();

    public void clearErrors() {
        errors.clearErrors();
    }

    /**
     * Indicate whether there are any errors
     * @return true if there are errors, false otherwise
     */
    public boolean hasErrors() {
        return errors.hasErrors();
    }

    public String getFlagError(String fieldID, String label) {
        return errors.getFlagErrorHTML(fieldID, label);
    }

    public String getErrorMessage(String fieldID) {
        return errors.getErrorMessageHTML(fieldID);
    }

    public String getAllErrorMessages() {
        return errors.getAllErrorMessagesHTML();
    }

    public void setErrors(ValidationErrors errors) {
        this.errors = errors;
    }    
    
    public ValidationErrors getErrors() {
        return this.errors;
    }    
    
	public String getObjectType() {
		return null;
	}

	public String getPresentation() {
		return null;
	}

	public String getSubType() {
		return null;
	}

	public String getVersionID() {
		return null;
	}

	public boolean isSpecialPresentation() {
		return false;
	}
	
	public String getURL() {
        return URLFactory.makeURL(this.m_class_id, ObjectType.FORM);
    }
    
}
