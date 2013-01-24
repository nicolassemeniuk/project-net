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
|   $Revision: 20899 $
|       $Date: 2010-06-02 13:57:19 -0300 (mié, 02 jun 2010) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.form;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.project.base.ObjectType;
import net.project.base.RecordStatus;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.database.ObjectManager;
import net.project.link.ILinkableObject;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentStatus;
import net.project.resource.Person;
import net.project.resource.Roster;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.Conversion;
import net.project.util.HTMLUtils;
import net.project.util.NumberFormat;
import net.project.xml.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * A listing of form instances.  Represents a list of form instances of the same
 * class_id that are displyed in a table.  Determines the order and format of
 * the fields that will be displayed in each column of the form list table.  The
 * FormFields in the FormList will usually be a subset of the total fields on
 * the Form.
 *
 * @author Bern McCarty
 * @since 01/2000
 */
public class FormList implements IJDBCPersistence, IXMLPersistence, Cloneable, ILinkableObject, java.io.Serializable {

    /** the FormFields that define this FormList.  These fields will always be a subset of the fields on the form. */
    protected ArrayList m_fields = new ArrayList();

    /**
     * The ListFieldProperties hashmap.  FormList-specific properties for each
     * form field on the list.  We don't extend the FormField to a FormListField
     * subclass for of memory efficiency.  Form fields are a superset of the
     * fields on any one list.  A Form may have several FormLists that present a
     * subset of the FormFields.  The field properties are only used when a
     * field is displayed on a FormList and  the these properties differ for
     * each FormList.
     */
    protected HashMap m_fieldProperties = null;

    /** an arrayList of FieldFilters to filter the data on the list. */
    protected ArrayList m_filters = null;

    /** form list sort */
    protected FormSort m_formSort = null;

    /**
     * list of FormData that was extracted for this list view.   Kept for
     * "next 25" functionality.  Maintains reference to the form list for
     * providing ability to extract all data XML
     */
    protected FormListResult m_data = new FormListResult(this);

    /** the Form that this list belongs to. */
    protected Form m_form = null;

    protected String m_class_id = null;
    protected String m_list_id = null;

    /**
     * Whether the list is the user or system default list.  user default has
     * precedence.
     */
    protected boolean m_is_default = false;
    protected boolean m_is_shared = false;
    protected boolean m_is_admin = false;
    protected String m_owner_space_id = null;
    protected String m_list_name = null;
    protected String m_list_description = null;
    protected int m_field_cnt = -1;

    // list decorations -- not implemented yet (from user or system definitions.
    protected String m_page_bg_color = null;      // color of the page background
    protected String m_table_color = null;        // color of the table that contains the list
    protected String m_header_color = "#CCCCCC";  // color of the list table header row
    protected String m_row_color1 = null;         // color for list table row
    protected String m_row_color2 = null;         // color for alternating row colors

    protected java.util.Date m_crc = null;

    private RecordStatus recordStatus = null;

    /* The special field for the sequence number.  Used for sorting. */
    TextField m_seqField = null;

    /**
     * Indicates whether to include a select field (i.e. a radio group) when
     * outputting the HTML for this formlist.
     */
    private boolean isIncludeHtmlSelect = false;

    // db access bean
    protected boolean m_isLoaded = false;

    /** The current space context. */
    private Space currentSpace = null;

    /** Indicates whether to load data for all spaces or just the current space. */
    private boolean isLoadForAllSpaces = false;
    
    /** Indicates whether form data has envelope or not. */
    private boolean isFormDataHasEnvelope = false;

    /**
     * Maintains the total of calculationfields.
     * Each key is a String representing the fieldLable.
     * Each value is a <code>ColumnTotal</code>.
     */
    private Map fieldTotalMap = new HashMap();

    /**
     * Construct a FormList for the specified Form.
     */
    public FormList() {
        m_form = null;
    }


    /**
     * Construct a FormList for the specified Form.
     *
     * @param form a <code>Form</code> object that this FormList belongs to.
     */
    public FormList(Form form) {
        m_form = form;
        m_class_id = form.m_class_id;
        initProperties();
    }


    /**
     * Fills the properties hashmap with empty ListFieldProperies objects.
     */
    protected void initProperties() {
        ListFieldProperties properties;
        Iterator fields;
        FormField field;

        if (m_form == null)
            return;

        // new HashMap, destroying the old.
        m_fieldProperties = new HashMap();
        fields = m_form.getFields().iterator();
        // add a dummy field for the sequence number, used for sorting.
        m_fieldProperties.put("0", new ListFieldProperties());
        m_seqField = new TextField(m_form, "0");

        while (fields.hasNext()) {
            field = (FormField)fields.next();
            properties = new ListFieldProperties();
            m_fieldProperties.put(field.getID(), properties);
        }

        // setup the form sort
        m_formSort = new FormSort();
        m_formSort.setFormList(this);
    }


    /**
     Clones the FormList.  Makes a shallow copy of the list fields and list field properties.
     */
    public Object clone() {
        FormList newList = new FormList(m_form);
        newList.m_fields = m_fields;
        newList.m_fieldProperties = m_fieldProperties;
        newList.m_filters = m_filters != null ? (ArrayList)this.m_filters.clone() : null;
        newList.m_formSort = m_formSort;
        newList.m_list_id = m_list_id;
        newList.m_is_default = m_is_default;
        newList.m_list_name = m_list_name;
        newList.m_list_description = m_list_description;
        newList.m_field_cnt = m_field_cnt;
        newList.m_seqField = m_seqField;
        newList.isIncludeHtmlSelect = isIncludeHtmlSelect;
        return newList;
    }


    /** set the form context for this form list. */
    public void setForm(Form form) {
        m_form = form;
        m_class_id = form.m_class_id;
        m_isLoaded = false;
        initProperties();
    }

    /**
     * Return the form for this form list
     * @return the form
     */
    public Form getForm() {
        return m_form;
    }

    /**
     * Sets the current space context.
     * This is required before loading data.
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

    /**
     * Specifies whether to load form data for all spaces.
     * @param isLoadForAllSpaces true means form data should be loaded for
     * all spaces; false means form data should be loaded for current space (default)
     */
    public void setLoadForAllSpaces(boolean isLoadForAllSpaces) {
        this.isLoadForAllSpaces = isLoadForAllSpaces;
    }

    /**
     * Indicates whether data should be loaded for all spaces.
     * @return true if data should be loaded for all spaces
     */
    private boolean isLoadForAllSpaces() {
        return this.isLoadForAllSpaces;
    }

    /**
     set the id of this form list.
     @deprecated use setID()
     */
    public void setId(String list_id) {
        m_list_id = list_id;
        m_isLoaded = false;
    }


    /** set the id of this form list. */
    public void setID(String list_id) {
        m_list_id = list_id;
        m_isLoaded = false;
    }


    /** set the id of this form list. */
    public String getID() {
        return m_list_id;
    }

    /* Set the space ID  that owns this list. (project, personal, etc.) */
    public void setOwnerSpaceID(String ownerSpaceID) {
        m_owner_space_id = ownerSpaceID;
    }

    /* Get the ID of the space that owns this list. (project, personal, etc.) */
    public String getOwnerSpaceID() {
        return m_owner_space_id;
    }


    /* Set the space that owns this list. (project, personal, etc.) */
    public void setOwnerSpace(Space ownerSpace) {
        m_owner_space_id = ownerSpace.getID();
    }

    public RecordStatus getRecordStatus() {
        return this.recordStatus;
    }

    /**
     Does this form list have field filters set.
     @return true if the form list has filters, false if no filters are defined.
     */
    public boolean hasFilters() {
        if ((m_filters == null) || (m_filters.size() < 1))
            return false;
        else
            return true;
    }


    /**
     Does this form list have field sorts set.
     @return true if the form list has sort fields set, false otherwise.
     */

    public boolean hasSort() {
        return (m_formSort.size() > 0);
    }


    /**
     * Set the Field filters used to filter the data for this list. This will
     * overide the filters that are saved in the database for this list, but
     * these filters will not be saved in the database unless the store() method
     * is called.
     *
     * @param filters an ArrayList of FieldFilters.
     */
    public void setFieldFilters(ArrayList filters) {
        m_filters = filters;
    }


    /**
     * Get the Field filters used to filter the data for this list.
     *
     * @return an <code>ArrayList</code> of FieldFilters.
     */
    public ArrayList getFieldFilters(ArrayList filters) {
        return m_filters;
    }


    /**
     * Add the FieldFilter to the filter list
     */
    public void addFieldFilter(FieldFilter filter) {
        if (m_filters == null)
            m_filters = new ArrayList();

        m_filters.add(filter);
    }


    /**
     Return the FieldFilter at position i in the filter list.
     @param i the position of the FieldFilter in filter list.
     @return the FieldFilter at position i in the filter list or null if there is no filter at that position in the list or the list is empty.
     */
    public FieldFilter getFieldFilter(int i) {
        if ((m_filters == null) || (m_filters.size() < 1))
            return null;
        else
            return (FieldFilter)m_filters.get(i);
    }


    /**
     Return the FieldFilter at position i in the filter list.
     @param field the FormField to get the FieldFilter for.
     @return the FieldFilter at position i in the filter list or null if there is no filter at that position in the list or the list is empty.
     */
    public FieldFilter getFieldFilter(FormField field) {
        FieldFilter fieldFilter;

        if ((m_filters == null) || (m_filters.size() < 1) || (field == null))
            return null;
        for (int i = 0; i < m_filters.size(); i++) {
            fieldFilter = (FieldFilter)m_filters.get(i);
            if (fieldFilter.getField().getID().equals(field.getID()))
                return fieldFilter;
        }

        // no FieldFilter found.
        return null;
    }


    /**
     Adds the field to the FormList sort, sort direction toggles.
     @param fieldID the database ID of the field to add to the sort.
     @param n the position of this field in the sort.
     */
    public void setSortField(String fieldID, int n)
        throws FormSortException {
        ListFieldProperties fieldProperties = this.getListFieldProperties(fieldID);

        // deal with special case of sort by sequence number
        if (fieldID.equals("0")) {
            fieldProperties.toggleSortDirection();
            m_formSort.setSortField(m_seqField, n, fieldProperties.isSortAscending());
        } else {
            fieldProperties.toggleSortDirection();
            m_formSort.setSortField(m_form.getField(fieldID), n, fieldProperties.isSortAscending());
        }
    }


    /**
     Adds the field to the FormList sort, explictly setting the sort direction.
     @param fieldID the database ID of the field to add to the sort.
     @param n the position of this field in the sort.
     @param isAscending true if to sort this field's values in ascending order, false to sort descending.
     */
    public void setSortField(String fieldID, int n, boolean isAscending)
        throws FormSortException {
        ListFieldProperties fieldProperties = this.getListFieldProperties(fieldID);

        // deal with special case of sort by sequence number
        if (fieldID.equals("0")) {
            m_formSort.setSortField(m_seqField, n, isAscending);
            fieldProperties.setSortAscending(isAscending);
        } else {
            m_formSort.setSortField(m_form.getField(fieldID), n, isAscending);
            fieldProperties.setSortAscending(isAscending);
        }
    }


    /** clear the sort on this list */
    public void clearSort() {
        m_formSort.clear();
    }


    /**
     Returns an HTML select-option list of Fields on this list.
     Option names are the user-defined field labels, option values are the FieldIDs.
     The caller must generate the HTML select tag before calling this method.
     @param selectedFieldID the field in the list that should be marked selected.
     @return the html for the option list.
     */
    public String getHtmlOptionList(String selectedFieldID) {
        StringBuffer sb = new StringBuffer();
        FormField field;
        Iterator fields = m_fields.iterator();

        sb.append("<option value=\"\">" + PropertyProvider.get("prm.form.designer.fieldedit.type.option.none.name") + "</option>\n");

        while (fields.hasNext()) {
            field = (FormField)fields.next();
            sb.append("<option value=\"" + field.getID() + "\"");

            if (field.getID().equals(selectedFieldID))
                sb.append(" selected>\n");
            else
                sb.append(">\n");

            sb.append(HTMLUtils.escape(field.getFieldLabel()));
            sb.append("</option>\n");
        }

        //sb.append("</select>\n");
        return (sb.toString());
    }


    /**
     Returns an HTML select-option list of sortable fields on this form.
     All sortable form fields are returned, not just fields on the list.
     Option names are the user-defined field labels, option values are the FieldIDs.
     The caller must generate the HTML select tag before calling this method.
     @param selectedFieldID the field in the list that should be marked selected.
     @return the html for the option list.
     */
    public String getHtmlSortOptionList(String selectedFieldID) {
        StringBuffer sb = new StringBuffer();
        FormField field;
        Iterator fields = m_form.m_fields.iterator();

        sb.append("<option value=\"\">" + PropertyProvider.get("prm.form.designer.lists.edit.sorting.option.none.name") + "</option>\n");

        while (fields.hasNext()) {
            field = (FormField)fields.next();
            if (field.isSortable()) {
                sb.append("<option value=\"" + field.getID() + "\"");

                if (field.getID().equals(selectedFieldID))
                    sb.append(" selected>\n");
                else
                    sb.append(">\n");

                sb.append(HTMLUtils.escape(field.getFieldLabel()));
                sb.append("</option>\n");
            }

        }
        return (sb.toString());
    }


    /**
     * Clear all data in this FormList.
     */
    public void clearData() {
        m_data.clear();
    }


    /**
     Clear the filters in this FormList.
     */
    public void clearFilters() {
        if (m_filters != null)
            m_filters.clear();
    }


    /**
     Clear the sorts in this FormList.
     */
    public void clearSorts() {
        if (m_formSort != null)
            m_formSort.clear();
    }


    /**
     Clear all fields, data, properties of this FormList.
     */
    public void clear() {
        //m_class_id = null;
        m_list_id = null;
        m_is_default = false;
        m_list_name = null;
        m_list_description = null;
        m_fieldProperties = null;
        m_crc = null;
        m_isLoaded = false;
        this.isIncludeHtmlSelect = false;

        if (m_fields != null)
            m_fields.clear();

        clearFilters();
        clearSorts();
        clearData();
    }


    /**
     * Returns result of loading FormData for this FormList (from the last data query).
     * @return all the FormData from the last data query.
     */
    public FormListResult getData() {
        return m_data;
    }


    /**
     Return an ArrayList of FormData containing the N (range) FormData objects begining at index.
     @return an ArrayList of FormData containing the N (range) FormData objects begining at index.
     Useful for "next 25" display of FormLists.
     */
    public FormListResult getData(int index, int range) {
        int i;
        int data_size;

        if (m_data == null || (data_size = m_data.size()) <= 0)
            return null;

        // check bounds
        if ((index < 0) || (index > data_size - 1) || (range < 1))
            return null;

        // check for range over runs.
        if (index + range > data_size)
            range = data_size - index;

        // create new ArrayList, copy the range of objects, return subArray
        FormListResult sub_array = new FormListResult(this, range);

        for (i = index; i < range; i++) {
            sub_array.add(m_data.get(i));
        }

        return sub_array;
    }


    /**
     Add data item for this list.
     */
    public void addData(FormData data) {
        m_data.add(data);
    }


    /**
     Relace the previous data stored in this list with a new data ArrayList.
     */
    public void replaceData(ArrayList data_array) {
        m_data.clear();
        m_data.addAll(data_array);
    }


    /**
     Is this list shared with all Space users who have access to the form?
     */
    public boolean isShared() {
        return m_is_shared;
    }

    /**
     Is this list an administrator-defined (form designer) list?
     */
    public boolean isAdminList() {
        return m_is_admin;
    }


    /**
     @return true if this is the system default list for the form.
     */
    public boolean isSystemDefault() {
        if ((m_form != null) && (m_class_id != null) && (m_list_id != null)) {
            String query = "select is_default from pn_space_has_class_list where space_id=" + m_form.getSpace().getID() + " and class_id=" + m_class_id + " and list_id=" + m_list_id;

            DBBean db = new DBBean();
            try {
                db.setQuery(query);
                db.executeQuery();
                if (db.result.next()) {
                    if ((db.result.getString(1) != null) && db.result.getString(1).equals("1"))
                        return true;
                }
            } catch (SQLException sqle) {
            	Logger.getLogger(FormList.class).error("FormList.isSystemDefault failed " + sqle);
            } finally {
                db.release();
            }
        }
        return false;
    }


    /**
     Retuns "checked" if the list is the system default, "" otherwise
     For use in HTML display beans for setting checked attribute.
     */
    public String getSystemDefaultChecked() {
        if (isSystemDefault())
            return "checked";
        else
            return "";
    }


    /**
     * @return true if this is the user's default list for the form.
     */
    public boolean isUserDefault() {
        if ((m_form != null) && (m_class_id != null) && (m_list_id != null)) {
            String query = "select is_default from pn_space_has_class_list where space_id=" + m_form.getUser().getID() + " and class_id=" + m_class_id + " and list_id=" + m_list_id;

            DBBean db = new DBBean();
            try {
                db.setQuery(query);
                db.executeQuery();
                if (db.result.next()) {
                    String is_default = db.result.getString(1);

                    if ((is_default != null) && is_default.equals("1")) {
                        return true;
                    }
                }
            } catch (SQLException sqle) {
            	Logger.getLogger(FormList.class).error("FormList.isUserDefault failed " + sqle);
            } finally {
                db.release();
            }
        }
        return false;
    }


    /**
     Retuns "checked" if the list is the user default, "" otherwise
     For use in HTML display beans for setting checked attribute.
     */
    public String getUserDefaultChecked() {
        if (isUserDefault())
            return "checked";
        else
            return "";
    }


    /**
     Set the FormList as the system default list for the form.
     */
    public void setAsSystemDefault() {
        if (m_class_id != null && m_list_id != null)
            throw new NullPointerException("m_class_id or m_list_id are null");

        // clear all is_default fields for this class_id, form_id, space_id
        String query = "update pn_space_has_class_list set is_default=0  where class_id=" + m_class_id + " and is_admin=1 and is_default=1";

        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();
            db.release();
            // set this list to be the space default
            query = "update pn_space_has_class_list set is_default=1  where class_id =" + m_class_id + " and list_id=" + m_list_id + " and space_id=" + m_form.getSpace().getID();
            db.setQuery(query);
            db.executeQuery();
        } catch (SQLException sqle) {
        	Logger.getLogger(FormList.class).error("FormList.setAsSystemDefault failed " + sqle);
        } finally {
            db.release();
        }
    }


    /**
     * Set the FormList as the user's default list for the form.
     */
    public void setAsUserDefault(String person_id) {
        boolean userListExists = false;

        if (person_id != null && m_class_id != null && m_list_id != null) {

            // clear all is_default fields for this class_id, form_id, user_id
            DBBean db = new DBBean();
            try {

                // Does a pn_space_has_class_list record exist for the user?.
                db.setQuery("select list_id from pn_space_has_class_list where class_id=" + m_class_id + " and space_id=" + person_id + " and list_id=" + m_list_id);
                db.executeQuery();
                if (db.result.next())
                    userListExists = true;
                db.release();

                // clear the old default list.
                db.setQuery("update pn_space_has_class_list set is_default=0  where class_id=" + m_class_id + " and space_id=" + person_id + " and is_default=1");
                db.executeQuery();
                db.release();

                if (userListExists) {
                    // UPDATE: set this list to be the user's default.
                    db.setQuery("update pn_space_has_class_list set is_default=1  where class_id =" + m_class_id + " and list_id=" + m_list_id + " and space_id=" + person_id);
                    db.executeQuery();
                } else {
                    // INSERT: set this list to be the user's default.
                    db.setQuery("insert into pn_space_has_class_list (is_default, class_id, list_id, space_id) values (1," + m_class_id + "," + m_list_id + "," + person_id + ")");
                    db.executeQuery();
                }
            } catch (SQLException sqle) {
            	Logger.getLogger(FormList.class).error("FormList.setAsUserDefault failed " + sqle);
            } finally {
                db.release();
            }
        }
    }


    /**
     * Add a FormField to the list view.
     *
     * @param field the field to add to the list.
     */
    public void addField(FormField field, String field_order, String field_width,
        boolean is_list_field, boolean is_calculate_total, boolean is_subfield,
        boolean no_wrap, boolean is_sort_field, int sort_order,
        boolean sort_ascending) {

        ListFieldProperties listFieldProperties;
        boolean foundField = false;

        if (field == null)
            return;

        for (int i = 0; i < m_fields.size(); i++) {
            if (((FormField)m_fields.get(i)).getID().equals(field.getID())) {
                foundField = true;
                break;
            }
        }

        // if the field is not already on the list
        if (!foundField) {
            if (m_fieldProperties == null)
                initProperties();

            listFieldProperties = (ListFieldProperties)m_fieldProperties.get(field.getID());
            listFieldProperties.m_field_order = field_order;
            listFieldProperties.m_field_width = field_width;
            listFieldProperties.m_is_subfield = is_subfield;
            listFieldProperties.m_wrap_mode = no_wrap;
            listFieldProperties.m_is_list_field = true;
            listFieldProperties.m_is_sort_field = is_sort_field;
            listFieldProperties.m_sort_order = sort_order;
            listFieldProperties.m_sort_ascending = sort_ascending;
            listFieldProperties.m_is_list_field = is_list_field;
            listFieldProperties.m_is_calculateTotal = is_calculate_total;

            // Field sort info
            try {
                if (is_sort_field)
                    m_formSort.setSortField(field, sort_order, sort_ascending);
            }
                // sort_order is out of range.
            catch (FormSortException fse) {
                // ignore the sort fields with illegal sort_order
            }

            m_fields.add(field);
            m_fields.indexOf(field);
        }
    }


    /**
     * @return an ArrayList of all the FormFields on the form.
     */
    public ArrayList getFields() {
        return m_fields;
    }

    /**
     * @return a FormField for given field_id
     */
    public FormField getFormField(String fieldID) {
        FormField ff;
        FormField fField = null;
        Iterator iter = m_fields.iterator();
        while (iter.hasNext()) {
            ff = (FormField)iter.next();
            if (ff.getID().equals(fieldID)) {
                fField = ff;
            }
        }
        return fField;
    }


    /**
     * @return a FieldData for given FormField
     */
    public FieldData getFormFieldData(FormField ff) {
        FieldData fieldData = null;
        Iterator iter = this.getData().iterator();
        while (iter.hasNext()) {
            FormData fd = (FormData)iter.next();
            if (fd.m_form.equals(ff.m_form)) {
                fieldData = fd.getFieldData(ff);
                return fieldData;
            }
        }
        return fieldData;
    }


    /**
     * Get the list field properties for the specified field
     *
     * @return the ListFieldProperties object for the field or null if now
     * properties are found for the field.
     */
    public ListFieldProperties getListFieldProperties(FormField field) {
        if (field == null)
            return null;
        else
            return getListFieldProperties(field.getID());
    }


    /**
     * Get the list field properties for the field specified by the it's
     * database field ID.
     *
     * @return the ListFieldProperties object for the field or null if now
     * properties are found for the field.
     */
    public ListFieldProperties getListFieldProperties(String fieldID) {
        if ((fieldID == null) || (m_fieldProperties == null))
            return null;
        else
            return ((ListFieldProperties)m_fieldProperties.get(fieldID));
    }


    /**
     * Get the Nth FormField to sort the list by where N = fieldSortOrder
     *
     * @param fieldSortOrder the Nth field to sort by.
     */
    public String getSortFieldID(int fieldSortOrder)
        throws FormSortException {
        FormField field;

        if ((field = m_formSort.getSortField(fieldSortOrder)) != null)
            return field.getID();
        else
            return null;
    }


    /**
     * If the sort field is set to ascending, return "checked", else return empty string.
     * @param fieldSortOrder the Nth field to sort by.
     */
    public String getSortFieldAscendingChecked(int fieldSortOrder) {
        if (m_formSort.isSortFieldAscending(m_formSort.getSortField(fieldSortOrder)))
            return "checked";
        else
            return "";
    }


    /**
     * If the sort field is set to descending, return "checked", else return
     * empty string.
     *
     * @param fieldSortOrder the Nth field to sort by.
     */
    public String getSortFieldDescendingChecked(int fieldSortOrder) {
        if (!m_formSort.isSortFieldAscending(m_formSort.getSortField(fieldSortOrder)))
            return "checked";
        else
            return "";
    }


    /**
     * @return an Hashmap of all the ListFieldProperties.
     */
    public HashMap getListFieldProperties() {
        return m_fieldProperties;
    }


    /**
     * @return The name of the FormList.
     */
    public String getName() {
        return m_list_name;
    }


    /**
     * @return The description of the FormList.
     */
    public String getDescription() {
        return m_list_description;
    }


    /**
     * Set the name of the FormList
     *
     * @param list_name The name of the FormList.
     */
    public void setName(String list_name) {
        m_list_name = list_name;
    }


    /**
     * Set the description of the FormList
     *
     * @param list_description The description of the FormList.
     */
    public void setDescription(String list_description) {
        m_list_description = list_description;
    }


    /**
     * Get a comma separated data file for this form list.  The CSV String
     * returned is compatable with microsoft excel and other tools that can
     * import CSV files.
     */
    public String getCSV() {
        FormField field;
        FormData form_data;
        FieldData field_data;
        int num_fields;
        int num_data;
        StringBuffer csv = new StringBuffer(200);

        num_fields = m_fields.size();

        // First write the header row
        csv.append("\""+ PropertyProvider.get("prm.form.csvexport.itemid.name")+"\"");
        for (int i = 0; i < num_fields; i++) {
            if (((FormField)m_fields.get(i)).isExportable())
                csv.append(",\"" + ((FormField)m_fields.get(i)).getFieldLabel() + "\"");
        }
        csv.append("\r\n");

        // Write CSV data for each row.
        // for each list item (row)
        num_data = m_data.size();
        for (int r = 0; r < num_data; r++) {
            form_data = (FormData)m_data.get(r);

            //First column: write the item id for this row
            csv.append("\"" + m_form.getAbbreviation() + form_data.getSeqNum() + "\"");   // the ID

            for (int c = 0; c < num_fields; c++) {
                field = (FormField)m_fields.get(c);
                // include all exportable fields
                if (field.isExportable()) {
                    field_data = (FieldData)form_data.get(field.getSQLName());
                    csv.append("," + field.formatFieldDataCSV(field_data));

                }
            }
            csv.append("\r\n");
        }
        return csv.toString();
    }


    /**
     * Set the isIncludeHtmlSelect property.  This indicates that we must
     * include an html select facility (radio group) when writing html.
     *
     * @param isIncludeHtmlSelect true means include an html select facility
     * @see net.project.form.FormList#writeHtml
     */
    public void setIncludeHtmlSelect(boolean isIncludeHtmlSelect) {
        this.isIncludeHtmlSelect = isIncludeHtmlSelect;
    }

    /**
     * Return isIncludeHtmlSelect property value.
     *
     * @return true indicates that an HTML select facility (radio group) will
     * be included when writeHtml is called.
     * @see net.project.form.FormList#writeHtml
     */
    public boolean isIncludeHtmlSelect() {
        return this.isIncludeHtmlSelect;
    }


    /**
     * Generate HTML for the entire FormList.
     *
     * @param out the output stream for the generated HTML
     */
    public void writeHtml(java.io.PrintWriter out) {
        writeHtml(out, 0, m_data.size());
    }


    /**
     * Generate HTML for a specified range of the FormList.
     *
     * @param out the output stream for the generated HTML
     * @param index the FormData item to begin the list with.
     * @param range the number of FormData items to display.
     */
    public void writeHtml(java.io.PrintWriter out, int index, int range) {
        PresentationColumnCount columnCount = new PresentationColumnCount();

        // Start table
        out.println("\n<!-- Begin generated list -->");
        out.print("<table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"");
        //if (m_table_color != null)
        //    out.print("<bgcolor=\"" + m_table_color + "\"");
        out.println(">");

        // Write the header row, updating column counts
        writeHeaderHtml(out, columnCount, m_fields);

        if (m_data.size() < 1) {
        	 // If the data is empty, let the user know.
        	writeNoDataHtml(out, PropertyProvider.get("prm.global.form.list.noitemsfound"));
        } else if (m_fields.size() < 1) {
            // If there are no fields in this list, then there is no possiblity for displaying data
            // Print the appropriate message
            writeNoDataHtml(out, PropertyProvider.get("prm.global.form.list.nofieldsdefined"));
        } else {
            // We have data; display it
            writeBodyHtml(out, columnCount, m_fields, index, range);

            //If there is atleast one listed CalculationField then display the Total footer
            writeFooterHtml(out, columnCount, m_fields);

            out.println("</table>");
            out.println("<table border=0 width=\"100%\" cellpadding=0 cellspacing=0>");
            out.println("<tr><td class=tableContent>" +
                PropertyProvider.get("prm.global.form.list.itemsfoundcount", new Object[]{new Integer(range - index).toString()}) +
                "</td></tr>");
            out.println("</table>");
        }

        out.println("<!-- End generated list -->");
    }


    /**
     * Writes the Header HTML for a FormList.
     *
     * @param out the PrintWriter to write to
     * @param columnCount the column count numbers updated to aid
     * the presentation of the body of the form list
     * @param fields the fields to write
     */
    private void writeHeaderHtml(java.io.PrintWriter out, PresentationColumnCount columnCount, Collection fields) {
        FormField field;
        Iterator fieldIt;
        ListFieldProperties listFieldProperties;

        out.print("<tr class=\"tableHeader\">\n");

        // Insert cell padding for radio group if necessary
        if (this.isIncludeHtmlSelect()) {
            out.println("<th class=\"tableHeader\" align=\"left\">&nbsp;</th>");
            columnCount.additionalColumns++;
        }

        // Insert form number column heading
        out.print("<th class=\"tableHeader\" align=\"left\">");
        out.print("<a href=\"javascript:sort('0');\">" + PropertyProvider.get("prm.global.form.list.formnumber") + "</a>");
        out.println("</th>");
        columnCount.additionalColumns++;

        // For each field, print the list table header.  Fields are in display order.
        fieldIt = fields.iterator();
        while (fieldIt.hasNext()) {
            field = (FormField)fieldIt.next();

            listFieldProperties = (ListFieldProperties)m_fieldProperties.get(field.getID());

            // Only display listFields (not sort or filter fields) in the list.
            if (listFieldProperties.isListField()) {

                // don't write column for subfields, they will be below on their own line.
                if (!listFieldProperties.isSubfield()) {

                    // Insert coumn heading
                    if (field instanceof CalculationField || field instanceof NumberField) {
                        out.print("<th class=\"tableHeader\" align=\"right\"");
                    } else {
                        out.print("<th class=\"tableHeader\" align=\"left\"");
                    }

                    if (!listFieldProperties.isNoWrap()) {
                        out.print(" nowrap=\"nowrap\"");
                    }

                    if (DBFormat.toInt(listFieldProperties.getFieldWidth()) != -1) {
                        out.print(" width=\"" + listFieldProperties.getFieldWidth() + "%\"");
                    }

                    out.print(">\n");

                    // The field colum is a href for column sort
                    //Check if the field is sortable?  Modified : Vishwajeet : 09/19/2001 // Calculation Field is not currently sortable.
                    if (field.isSortable()) {
                        out.println("<a href=\"javascript:sort('" + field.getID() + "');\">");
                        out.print(HTMLUtils.escape(field.m_field_label));
                        out.print("\n</a>&nbsp;&nbsp;&nbsp;</th>\n");
                    } else {
                        out.print(HTMLUtils.escape(field.m_field_label));
                        out.print("&nbsp;&nbsp;&nbsp;</th>\n");
                    }
                    columnCount.listColumns++;
                }
            }
        }
        // for link indicator
        out.println("<th class=\"tableHeader\" align=\"left\">&nbsp;</th>");
        columnCount.additionalColumns++;
        
        out.print("</tr>\n");
        out.print("<tr class=\"tableLine\">\n");
        out.print("<td colspan=\"" + (columnCount.getTotalColumnCount()) + "\" class=\"tableLine\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/spacers/trans.gif\" width=\"1\" height=\"2\" border=\"0\" alt=\"\"/></td>");
        out.print("</tr>\n");

        out.flush();

        //
        // End of Table Header
        //
    }


    /**
     * Writes the body for a form list.
     * This is actual data in the form list.
     * @param out the PrintWriter to write to
     * @param columnCount the column count numbers updated to aid
     * the presentation of the body of the form list
     * @param fields the fields to write
     * @param index the FormData item to begin the list with.
     * @param range the number of FormData items to display.
     */
    private void writeBodyHtml(java.io.PrintWriter out, PresentationColumnCount columnCount, Collection fields, int index, int range) {
        FormData formData;
        Iterator dataIt;
        StringBuffer subfieldRows;
       
        // Get the subset of data rows
        // Iterate over each row, displaying it
        dataIt = m_data.subList(index, index + range).iterator();
        while (dataIt.hasNext()) {
        
            formData = (FormData)dataIt.next();
            subfieldRows = new StringBuffer();

            out.print("<tr id=\"tableContentHighlight\" class=\"tableContentHighlight\">\n");

            // Insert radio group if necessary
            if (this.isIncludeHtmlSelect()) {
                out.print("<td class=\"tableContentFontOnly\" align=\"left\">");
                out.print("<input type=\"radio\" name=\"selected\" value=\"" + formData.getID() + "\"/>");
                out.print("</td>");
                out.println("");
            }

            // write the abbreviation and sequence number on every list row.  This is the hotlist activator.
            out.print("<td class=\"tableContentFontOnly\" align=\"left\" nowrap=\"nowrap\"><a id=\"listElement\" href=\"javascript:modify('" + formData.getID() + "');\">");
            out.print(PropertyProvider.get("prm.form.listview.number.link", new Object[] { m_form.m_class_abbreviation, String.valueOf(formData.getSeqNum()) } ));
            out.print("</td>");
            out.println("");
            
            // Write all data columns, capturing subfield information
            writeDataColumnsHtml(out, columnCount, fields, formData, subfieldRows);

            // workflow indicator
            if (formData.isHasActiveEnvelope()) {
                out.print("<td class=\"tableContentFontOnly\" align=\"right\">");
                out.print("<img src='" + SessionManager.getJSPRootURL() + "/images/document/workflow-button.gif' title='Workflow enabled' /> ");
                out.print("</td>");
                out.println("");
            } else {
            	out.println("<td class=\"tableContentFontOnly\" align=\"right\">&nbsp;</td>");
            }
            
            out.print("</tr>\n");

            if (subfieldRows.length() > 0) {
                // Write the subfield rows.
                out.print(subfieldRows);
            }

            out.print("<tr class=\"tableLine\">\n");
            out.print("<td colspan=\"" + columnCount.getTotalColumnCount() + "\" class=\"tableLine\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/spacers/trans.gif\" width=\"1\" height=\"1\" border=\"0\" alt=\"\"/></td>");
            out.print("</tr>\n");

            out.flush();

        }  // for rows

    }


    /**
     * Write a message when no data is available for display.
     * @param out the PrintWriter to write to
     * @param message the message to display
     */
    private void writeNoDataHtml(java.io.PrintWriter out, String message) {
        out.println("<tr class=\"tableContent\"><td class=\"tableContent\" colspan=\"" + m_fields.size() + "\" align=\"center\">" + message + "</td></tr>");
        out.println("</table>");
    }


    /**
     * Write the data for each field in the list view.
     * @param out the PrintWriter to write to
     * @param columnCount the column count numbers used for display formatting
     * @param data the form data to write
     * @param subfieldRows the string containing subfield information to be
     * displayed below this row
     */
    private void writeDataColumnsHtml(java.io.PrintWriter out, PresentationColumnCount columnCount, Collection fields, FormData data, StringBuffer subfieldRows) {
        FormField field;
        ListFieldProperties listFieldProperties;

        // Iterate over each field and print its data
        Iterator columnIt = fields.iterator();
        while (columnIt.hasNext()) {
            // Grab the next field
            field = (FormField)columnIt.next();

            listFieldProperties = (ListFieldProperties)m_fieldProperties.get(field.getID());

            // only get the data for the field in the list view.
            if (listFieldProperties.isListField()) {

                FieldData fieldData = (FieldData) data.get(field.getSQLName());
                
                String value;

                // Special Handling for certain fields
                // Number and Calculation fields must have their values added
                // to field totals; we need the raw numeric value, not the
                // formatted value
                if (field == null){
                	System.out.println("\n\n\n jeste null \n\n\n");
                }
                if (field instanceof CalculationField) {
                    // A calculation field is based on the entire form's data

                    Number result = null;
                    try {
                        result = ((CalculationField) field).getResult(data);
                    } catch (CalculationField.CalculationException e) {
                        // Do nothing; result remains null
                    }
                    addToFieldTotal(field.getFieldLabel(), result);

                    // Now format the value; we actually recalculate here so
                    // we can uncover any error messages that would have
                    // resulted in a null number in the caclulation above
                    value = ((CalculationField) field).formatCalculationListView(data);

                } else if (field instanceof NumberField) {
                    // Get the actual number represented by the field data
                    addToFieldTotal(field.getFieldLabel(), ((NumberField) field).getNumber(fieldData));
                    value = field.formatFieldDataListView(fieldData);

                } else {
                    // All other fields simply format the data
					if (field.getSQLName().indexOf("create_person_id") != -1) {
						if (field.formatFieldDataListView(fieldData) == null || "".equals(field.formatFieldDataListView(fieldData))) {
							String creatorEmail = (String) data.get("creator_email");
							value = creatorEmail;
						} else {
							value = field.formatFieldDataListView(fieldData);
						}
					} else if (field.getSQLName().indexOf("modify_person_id") != -1) {
						if (field.formatFieldDataListView(fieldData) == null || "".equals(field.formatFieldDataListView(fieldData))) {
							String creatorEmail = (String) data.get("creator_email");
							value = creatorEmail;
						} else {
							value = field.formatFieldDataListView(fieldData);
						}
					} else {
						value = field.formatFieldDataListView(fieldData);
					}
                }

                if (!listFieldProperties.isSubfield()) {
                    // If it's not a subfield, write it in the row.
                    if (value == null) {
                        out.print("<td class=\"tableContentFontOnly\">");
                        out.print("&nbsp;");
                    } else {
                        if (field instanceof CalculationField || field instanceof NumberField) {
                            out.print("<td class=\"tableContentFontOnly\" align=\"" +
                            PropertyProvider.get("prm.form.view.list.numberfield.align") +
                            		"\" nowrap=\"nowrap\">");
                            out.print(value);
                        } else {
                            out.print("<td class=\"tableContentFontOnly\">");
                            out.print(value);
                        }
                    }

                    out.print("&nbsp;</td>\n");

                } else {
                    // Save the subfield row, we will write it below this row.
                    subfieldRows.append("<tr class=\"tableContent\">");
                    subfieldRows.append("<td class=\"tableContent\">&nbsp;</td>");
                    subfieldRows.append("<td  class=\"tableContent\" colspan=\"" + (columnCount.listColumns - 1) + "\"><b>" + PropertyProvider.get("prm.form.subfield.name", new Object[] { HTMLUtils.escape(field.m_field_label) } ) + "</b> ");

                    if (field instanceof CheckboxField) {
                        subfieldRows.append("&nbsp;&nbsp;" + value + "</td></tr>\n");
                    } else {
                        subfieldRows.append("&nbsp;&nbsp;" + HTMLUtils.escape(value) + "</td></tr>\n");
                    }

                }
            }

        } // end while


    }

    /**
     * Adds the CalculatedFields as they are calculated
     * and puts them in a HashMap
     * @param fieldLabel the field for which the total is being calculated
     * @param value to be added to the total for the field
     */
    private void addToFieldTotal(String fieldLabel, Number value) {

        ColumnTotal total = (ColumnTotal) this.fieldTotalMap.get(fieldLabel);
        if (total == null) {
            // First value to add for this column
            // Create a new column total and add it to the map for this column
            total = new ColumnTotal();
            this.fieldTotalMap.put(fieldLabel, total);
        }

        // Now add the value to the total
        total.add(value);
    }

    /**
     * Writes the Footer HTML for a FormList.
     * @param out the PrintWriter to write to
     * @param columnCount the column count numbers updated to aid
     * the presentation of the body of the form list
     * @param fields the fields to write
     */
    private void writeFooterHtml(java.io.PrintWriter out, PresentationColumnCount columnCount, Collection fields) {
        FormField field;
        Iterator fieldIt;
        ListFieldProperties listFieldProperties;

        boolean isWriteTotal = false;
        StringBuffer totalBuf = new StringBuffer();
        totalBuf.append("<tr class=\"tableContentHighlight\">\n");
        totalBuf.append("<tr class=\"tableContent\">\n");

        // Insert cell padding for radio group if necessary
        if (this.isIncludeHtmlSelect()) {
            totalBuf.append("<th class=\"tableContent\" align=\"left\">&nbsp;</th>");
            columnCount.additionalColumns++;
        }

        // Insert Total row heading in the first column
        totalBuf.append("<th class=\"tableContent\" align=\"left\">");
        totalBuf.append(PropertyProvider.get("prm.form.calculated.total.description"));
        totalBuf.append("</th>");
        columnCount.additionalColumns++;

        // Write the footer only if there is atleast one calculation field  or Number Field
        //for which to calculate total.
        fieldIt = fields.iterator();
        while (fieldIt.hasNext()) {
            field = (FormField)fieldIt.next();

            listFieldProperties = (ListFieldProperties)m_fieldProperties.get(field.getID());

            if (listFieldProperties.isListField()) {

                // Only display total for the calculation field if checked by the user.
                // if (listFieldProperties.isCalculateTotal()) {
                if ((field instanceof CalculationField || field instanceof NumberField)
                        && listFieldProperties.isCalculateTotal()) {

                    ColumnTotal total = (ColumnTotal) this.fieldTotalMap.get(field.getFieldLabel());

                    if (!total.isUndefined()) {
                        // We have a defined total; we'll want to write the total
                        // line
                        isWriteTotal = true;

                        totalBuf.append("<td class=\"tableContentFontOnly\" align=\"right\">");
                        if (total == null || total.isUndefined()) {
                            // No values in total
                            totalBuf.append("&nbsp;&nbsp;&nbsp");

                        } else {
                            // Format the total
                            totalBuf.append(NumberFormat.getInstance().formatNumber(total.getCurrentTotal().doubleValue()));
                        }

                        columnCount.listColumns++;
                        totalBuf.append("&nbsp;</td>\n");

                    }


                } else {
                    totalBuf.append("\n<td class=\"tableContentFontOnly\">&nbsp;&nbsp;&nbsp;</td>\n");
                    columnCount.listColumns++;
                }
            }
        }//end while


        totalBuf.append("</tr>\n");

        // Only write the total if we found a total to write
        if (isWriteTotal) {
            out.print(totalBuf.toString());
            out.flush();
        }

        out.print("<tr class=\"tableLine\">\n");
        out.print("<td colspan=\"" + (columnCount.getTotalColumnCount()) + "\" class=\"tableLine\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/spacers/trans.gif\" width=\"1\" height=\"2\" border=\"0\" alt=\"\"/></td>");
        out.print("</tr>\n");
        // Reset the fieldTotalMap.
        this.fieldTotalMap.clear();
    }


    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     * @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer(200);
        Roster roster = this.getRoster();
        Person person;

        try {
            if (!isLoaded())
                this.load();
        } catch (PersistenceException pe) {
            return "";
        }

        person = roster.getPerson(m_owner_space_id);

        // form list properties
        xml.append("<FormList>\n");
        xml.append("<id>" + m_list_id + "</id>\n");
        xml.append("<is_default>" + m_is_default + "</is_default>\n");
        xml.append("<is_shared>" + m_is_shared + "</is_shared>\n");
        xml.append("<is_admin>" + m_is_admin + "</is_admin>\n");
        xml.append("<owner_space_id>" + m_owner_space_id + "</owner_space_id>\n");
        if (person != null)
            xml.append("<owner_name>" + XMLUtils.escape(person.getDisplayName()) + "</owner_name>\n");
        xml.append("<name>" + XMLUtils.escape(m_list_name) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(m_list_description) + "</description>\n");
        xml.append("<field_count>" + m_field_cnt + "</field_count>\n");
        for (int i = 0; i < m_fields.size(); i++) {
            if (m_fields.get(i) != null) {
                xml.append("<ListField>\n");
                xml.append(((FormField)m_fields.get(i)).getXMLProperties());
                xml.append(((ListFieldProperties)m_fieldProperties.get(((FormField)m_fields.get(i)).getID())).getXMLProperties());          
                xml.append("</ListField>\n");
            }
        }
        xml.append("</FormList>\n");
        return xml.toString();
    }


    /**
     Converts the object to XML representation.
     This method returns the object as XML text.
     @return XML representation
     */
    public String getXML() {
        return ("<?xml version=\"1.0\" ?>\n" + getXMLBody());
    }


    /**
     Return String representation of this FormList.
     */
    public String toString() {
        return (
            "\n+++++ FormList +++++" +
            "\nm_class_id = " + m_class_id +
            "\nm_list_id= " + m_list_id +
            "\nm_is_default= " + m_is_default +
            "\nm_list_name  = " + m_list_name +
            "\nm_list_description = " + m_list_description +
            "\nm_field_cnt = " + m_field_cnt +
            "\nm_fields= " + m_fields.size() +
            "\nm_data= " + m_data.size() +
            "\n+++++ FormList +++++"
            );
    }


    /**
     * Loads all form data for this list filtered and sorted.
     * Must be called prior to display().
     */
    public void loadData() throws PersistenceException {
        if (m_data != null) {
            m_data.clear();
        }

        m_data = loadFilteredData();

        // Sort the data in memory.
        if (hasSort()) {
            sortData(m_data);
        }
    }


    /************************************************************************************************
     *   Implementing IJDBCPersistence Interface
     *************************************************************************************************/

    /** Has this FormList been loaded from database persistence. */
    public boolean isLoaded() {
        return m_isLoaded;
    }


    /**  Sets the list persistence flag. */
    public void setIsLoaded(boolean isLoaded) {
        m_isLoaded = isLoaded;
    }


    /**
     * Store the FormList.  This method is used for creating or updating a
     * form's list view.
     */
    public void store()
        throws PersistenceException {
        FormField field;
        ListFieldProperties listFieldProperties;
        int num_fields;
        int i;
        String query;
        boolean isExistingList = false;
        java.sql.Timestamp newCrc = new java.sql.Timestamp(new java.util.Date().getTime());

        if (m_class_id == null || m_class_id.equals(""))
            throw new NullPointerException("m_class_id is null, list cannot be stored");

        DBBean db = new DBBean();
        try {
            db.openConnection();
            db.connection.setAutoCommit(false);

            // See if the list already exists.  Also lock it if it does.
            if (m_list_id != null && !m_list_id.equals("")) {
                query = "select crc from pn_class_list where class_id=" + m_class_id + " and list_id=" + m_list_id + " for update nowait";
                db.setQuery(query);
                db.executeQuery();
                if (db.result.next()) {
                    isExistingList = true;
                }

            }

            // New list
            if (m_list_id == null || m_list_id.equals("") || (!isExistingList)) {
                // get new id and register in the pn_object table.
                m_list_id = ObjectManager.dbCreateObjectWithPermissions(ObjectType.FORM_LIST, "A", m_form.getSpace().getID(), m_form.getUser().getID());

                query = "insert into pn_class_list (class_id, list_id, owner_space_id, list_name, list_desc, field_cnt, record_status, crc, is_shared, is_admin) values (" +
                    m_class_id + ", " + m_list_id + ", " + m_owner_space_id + ", " + DBFormat.varchar2(m_list_name) + ", " + DBFormat.varchar2(m_list_description) + ", " +
                    m_field_cnt + ",'A', " + DBFormat.crc(newCrc) + ", " + DBFormat.bool(m_is_shared) + ", " + DBFormat.bool(m_is_admin) + ")";

                db.setQuery(query);
                db.executeQuery();

            }
            // UPDATE existing class_list
            else {

                /* TIMM - Not currently possible.
                   11/28/2000.  The check for the list in the database above exists because the FormListDesigner
                   object is cleared before a store is attempted.  Thus, the member variable m_crc is not cached.
                   We cannot compare it to the database then: No conflict checking can be performed.

                   Possible remedy:  Hidden "crc" field on html form that passes the crc in the request
                        OR
                   Don't clear list when existing list

                // Before updating, check crc matches
                if (!crc.equals(m_crc)) {
                    throw new PersistenceException("Form list has been modified by another user.");
                }
                */

                query = "update pn_class_list set list_name=" + DBFormat.varchar2(m_list_name) + ", list_desc=" + DBFormat.varchar2(m_list_description) + ", field_cnt=" + m_field_cnt +
                    ", crc = " + DBFormat.crc(newCrc) + ", is_shared=" + DBFormat.bool(m_is_shared) + ", is_admin=" + DBFormat.bool(m_is_admin) +
                    " where class_id=" + m_class_id + " and list_id=" + m_list_id;

                db.setQuery(query);
                db.executeQuery();
            }

            m_crc = newCrc;

            // FORM LIST FIELDS
            // Delete the old entries from the class_list_field table.
            query = "delete from pn_class_list_field where class_id=" + m_class_id + " and list_id=" + m_list_id;
            db.setQuery(query);
            db.executeQuery();

            num_fields = m_form.m_fields.size();
            for (i = 0; i < num_fields; i++) {
                field = (FormField)m_form.m_fields.get(i);  // look at all the fields
                listFieldProperties = (ListFieldProperties)m_fieldProperties.get(field.getID());

                // only store fields that are storable
                if ((field.m_field_id != null) && !field.m_field_id.equals("") && (field.isStorable() || field.isSelectable()) || field instanceof FormID ) {
                    // INSERT list field
                    query = "insert into pn_class_list_field (class_id, list_id, field_id, field_width, field_order, wrap_mode, is_list_field, is_subfield, is_sort_field, sort_order, sort_ascending, is_calculate_total) " +
                        "values (" + m_class_id + "," + m_list_id + "," + field.m_field_id + "," +
                        DBFormat.number(listFieldProperties.m_field_width) + "," +
                        DBFormat.number(listFieldProperties.m_field_order) + "," +
                        DBFormat.bool(listFieldProperties.m_wrap_mode) + "," +
                        DBFormat.bool(listFieldProperties.m_is_list_field) + "," +
                        DBFormat.bool(listFieldProperties.m_is_subfield) + "," +
                        DBFormat.bool(listFieldProperties.m_is_sort_field) + "," +
                        listFieldProperties.m_sort_order + "," +
                        DBFormat.bool(listFieldProperties.m_sort_ascending) + "," +
                        DBFormat.bool(listFieldProperties.m_is_calculateTotal) + ")";

                    db.setQuery(query);
                    db.executeQuery();
                }

            }   // for


            //
            // Now store filters for this form list
            //
            storeFilters(db);

            // Finally commit everything

            db.commit();

        } catch (SQLException sqle) {
        	Logger.getLogger(FormList.class).error("FormList.store failed " + sqle);
            throw new PersistenceException("Form list store opertaion failed: " + sqle, sqle);
        } finally {
            if (db.connection != null) {
                try {
                    db.connection.rollback();
                } catch (SQLException sqle2) {
                    // Can do nothing except release()
                }
            }
            db.release();
        }
    }


    /**
     * Stores all filters for this form list.  All existing filters are deleted
     * first such that filters absent from m_filters (like "All" filters)
     * are removed.
     */
    private void storeFilters(DBBean db) throws SQLException, PersistenceException {
        FieldFilter filter;
        int num_filters;

        // Delete all filters
        removeFilters(db);

        // Now store each field filter
        if ((m_filters != null) && ((num_filters = m_filters.size()) > 0)) {
            for (int i = 0; i < num_filters; i++) {
                filter = (FieldFilter)m_filters.get(i);
                if (filter != null)
                    filter.store(db);
            }
        }

    }

    /**
     * Deletes all field filters for this class and list
     * @throws SQLException if there is a problem deleting the field filters
     */
    private void removeFilters(DBBean db) throws SQLException {
        StringBuffer query = new StringBuffer();

        query.append("delete from pn_class_list_filter where class_id = ? and list_id = ? ");
        db.prepareStatement(query.toString());
        int index = 0;
        db.pstmt.setString(++index, this.m_class_id);
        db.pstmt.setString(++index, this.getID());

        db.executePrepared();
    }

    /**
     * Get the FormList from the database.
     */
    public void load()
        throws PersistenceException {
        FormField field;
        ListFieldProperties listFieldProperties;
        String query;

        if (m_list_id == null)
            throw new NullPointerException("FormList.load():  m_list_id is null");

        // If we do not yet know this Form List's form class, load it
        if (m_class_id == null) {
            loadForm();
        }
        try{
        	getForm().loadSharedFormSpaceIds();
        } catch (SQLException e) {
        	e.printStackTrace();
		}
        // new list coming in.  just clear the fields, filters and properties
        if (m_fields != null)
            m_fields.clear();

        if (m_filters != null)
            m_filters.clear();

        initProperties();

        // LIST INFO
        //Get the FormList information
        query = "select list_name, field_cnt, list_desc, owner_space_id, crc, is_shared, is_admin, record_status " +
                "from pn_class_list " +
                "where class_id=" + m_class_id + " and list_id=" + m_list_id;

        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();

            if (!db.result.next())
                throw new PersistenceException("The requested form list was not found in the database.  Contact Administrator.");

            m_list_name = db.result.getString(1);
            m_field_cnt = db.result.getInt(2);
            m_list_description = db.result.getString(3);
            m_owner_space_id = db.result.getString(4);
            m_crc = db.result.getTimestamp(5);
            m_is_shared = db.result.getBoolean(6);
            m_is_admin = db.result.getBoolean(7);
            this.recordStatus = RecordStatus.findByID(db.result.getString("record_status"));

            // LIST FIELDS
            // Get the FormFields for this FormList
            query = "select field_id, field_width, field_order, wrap_mode, is_list_field, is_subfield, is_sort_field, sort_order, sort_ascending, is_calculate_total from pn_class_list_field" +
                " where class_id=" + m_class_id + " and list_id=" + m_list_id +
                " order by field_order";

            db.setQuery(query);
            db.executeQuery();

            if (!db.result.next())
                throw new PersistenceException("No fields were found for this form list.  Contact Administrator.");

            // we need to get the Form's field for each FormList field so they are matching objects.
            // if the Form does not have a FormField with the matching field_id (it has been deleted by someone)
            // we must ignore it.
            do {
                if ((field = m_form.getField(db.result.getString(1))) != null) {
                    m_fields.add(field);

                    listFieldProperties = (ListFieldProperties)m_fieldProperties.get(field.getID());
                    listFieldProperties.m_field_width = db.result.getString(2);
                    listFieldProperties.m_field_order = db.result.getString(3);
                    listFieldProperties.m_wrap_mode = Conversion.toBoolean(db.result.getString(4));
                    listFieldProperties.m_is_list_field = Conversion.toBoolean(db.result.getString(5));
                    listFieldProperties.m_is_subfield = Conversion.toBoolean(db.result.getString(6));
                    listFieldProperties.m_is_sort_field = Conversion.toBoolean(db.result.getString(7));
                    listFieldProperties.m_sort_order = db.result.getInt(8);
                    listFieldProperties.m_sort_ascending = Conversion.toBoolean(db.result.getString(9));
                    listFieldProperties.m_is_calculateTotal = Conversion.toBoolean(db.result.getString(10));
                }
            } while (db.result.next());

            m_isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(FormList.class).error("FormList.load failed " + sqle);
            throw new PersistenceException("Form list load operation failed: " + sqle, sqle);
        } finally {
            db.release();
        }

        // load the filter information for this list from the database.
        loadFilters();

        // load the list sort options
        loadSort();

    }


    /**
     * Loads the form object for this form list.  The form can be determined
     * by looking in PN_CLASS_LIST.
     * <br><b>Preconditions:</b><br>
     * <li>m_list_id is set</li>
     * <br><b>Postconditions:</b><br>
     * <li>setForm() has been called to set the form</li>
     * @throws PersistenceException if there is a problem loading the form object
     * @see #setForm
     */
    private void loadForm() throws PersistenceException {
        String class_id;         // class_id of form to load
        Form form;               // Form loaded from database

        DBBean db = new DBBean();
        try {
            // Locate the class_id of this form data's form
            db.executeQuery("select class_id from pn_class_list where list_id = " + m_list_id);
            if (db.result.next()) {
                class_id = db.result.getString("class_id");

                // Construct a new form object and assign to member variable
                form = new Form();
                form.setID(class_id);
                form.load();

                setForm(form);
            } else {
                // No class_id found for this form list - problem!
            	Logger.getLogger(FormList.class).error("FormList.loadForm failed - unable to locate form for list with id " +
                    m_list_id);
                throw new PersistenceException("Unable to locate form for form list.");
            } // end if

        } catch (SQLException sqle) {
        	Logger.getLogger(FormList.class).error("FormList.loadForm failed " + sqle);
            throw new PersistenceException("Error loading form for form list.", sqle);

        } finally {
            db.release();

        } // end try

    }


    /**
     Hard delete this list from the database.
     */
    public void remove() throws PersistenceException {

        DBBean db = new DBBean();
        try {
            //db.setQuery("update pn_class_list set record_status='D' where class_id=" + m_class_id + " and list_id=" + m_list_id);
            db.setQuery("delete from pn_space_has_class_list where class_id=" + m_class_id + " and list_id=" + m_list_id);
            db.executeQuery();

            db.setQuery("delete from pn_class_list_field where class_id=" + m_class_id + " and list_id=" + m_list_id);
            db.executeQuery();

            db.setQuery("delete from pn_class_list where class_id=" + m_class_id + " and list_id=" + m_list_id);
            db.executeQuery();

            m_isLoaded = false;
        } catch (SQLException sqle) {
        	Logger.getLogger(FormList.class).error("FormList.remove failed " + sqle);
            throw new PersistenceException("Remove operation failed: " + sqle, sqle);
        } finally {
            db.release();
        }
    }



    /*******************************************************************************************************************
     *  Private methods
     ******************************************************************************************************************/

    /**
     * Concatenate the where clauses of all of the filters we are applying to
     * the list.
     *
     * @return a <code>String</code> value which contains a where clause for all
     * of the filters we are going to concatenate to the SQL statement.
     */
    private String getFilterWhereClauses() {
        StringBuffer whereClause = new StringBuffer();

        whereClause.append("(1=1 ");
        for (int i = 0; i < m_filters.size(); i++) {
            whereClause.append(((FieldFilter)m_filters.get(i)).getSQL(this.getForm().getMasterTableName()));
        }
        whereClause.append(")");

        return whereClause.toString();
    }

    /**
     * Load only the data for the given versionID list.
     * @return Arraylist of FormData objects.
     */
    private FormListResult loadFilteredData() throws PersistenceException {
        FormData form_data = null;
        FormField field;
        FieldData field_data;
        FormListResult dataArray = new FormListResult(this);
        int num_fields = m_fields.size();

        if (!isSelectableFieldFound()) {
            // This list has no fields that may be selected;
            // this situation may arise when all fields in a FormList are deleted.
            // Since no "Activation" is/ required after deleting a field,
            // this cannot be trapped in the FormDesigner
            // Also, if a form consists exclusively of section titles, user instructions etc.
            // We return the empty data array
            return dataArray;
        }

        // GET DATA
        // Save the data in memory. Data for the fields of one form on the list
        DBBean db = new DBBean();
        try {
            db.setQuery(buildLoadFilteredDataQuery());
            db.executeQuery();
            while (db.result.next()) {
                // New form instance data group.
                // Each form instance may have multiple data rows in database for multi selection lists.
                // Each form instance must have at least one DB row with multi_data_seq=0.
                if (db.result.getInt(4) == 0)     // multi_data_seq
                {
                    form_data = new FormData(m_form, db.result.getString(1));
                    dataArray.add(form_data);
                    form_data.setVersionID(db.result.getString(2));
                    form_data.setSeqNum(db.result.getInt(3));
                    
                    if(isFormDataHasEnvelope()) {
                    	form_data.setHasActiveEnvelope(db.result.getInt("env") != 0);
                    }
                }

                // For each field in the form data record.
                // only put the field data into the field_data array.
                // j+5 skips the four columns returned by the query above which are not fields.
                for (int c = 0, j = 0; c < num_fields; c++) {
                    field = (FormField)m_fields.get(c);
                    if (field instanceof CalculationField || field instanceof FormID) { // Vishwajeet : 09/19/2001 : Ignore if it is a CalculationField
                        continue;			   // as  it's datavalue would be calculated on the fly.
                    }

                    if(form_data != null) {
                        if ((field_data = (FieldData)form_data.get(field.getSQLName())) == null)
                            field_data = new FieldData();
                        field_data.add(db.result.getString(j + 5));
                        form_data.put(field.getSQLName(), field_data);
                        j++;
                    }
                }
				if(form_data != null)
	                form_data.put("creator_email", db.result.getString("creator_email"));
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(FormList.class).error("FormList.loadFilteredData failed " + sqle);
            throw new PersistenceException("Load data operation failed: " + sqle, sqle);
        } finally {
            db.release();
        }

        return dataArray;
    }

    /**
     * Indicates whether there is at least one field that can be
     * selected from the database.
     * @return true if there is a selectable field found; false if
     * no fields can be selected
     */
    private boolean isSelectableFieldFound() {
        boolean isFound = false;
        for (Iterator it = m_fields.iterator(); it.hasNext() && !isFound;) {
            FormField nextField = (FormField) it.next();
            isFound = nextField.isStorable();
        }
        return isFound;
    }


    /**
     * Builds the query for loading filtered data.
     * The query is a select on the specific form data table (<code>"CD" + class_id</code>)
     * joined with <code>PN_CLASS_INSTANCE</code>.
     * If {@link #isLoadForAllSpaces} is not true then the query filters
     * based on the current space.
     * The query includes only Active form data.
     *
     * @return the query
     * @see #getCurrentSpace
     */
    private String buildLoadFilteredDataQuery() {

        StringBuffer query = new StringBuffer();
        String selectString = "";
        String fromString = " from pn_class_instance ";
        String whereString = " where pn_class_instance.class_id = " + this.m_form.getID() + " and pn_class_instance.record_status = 'A' ";

        boolean isFirstTable = true;
        boolean isNewTable = true;
        String previousTableName = null;

        boolean hasAsssignemtColumn = false;
        FormField field = null;

        // build a query joining all the data tables in the users scope that are needed for the field in the list.
        // the fields are ordered by table.
        String creatorEmail = "";
        // Loop over all fields in this list's form
        // Build the select part and begin building the from and where clauses
        for (Iterator it = m_fields.iterator(); it.hasNext();) {
            field = (FormField) it.next();

            if (field instanceof MetaDataUserField){
            	hasAsssignemtColumn = true;
            }
            
            // Vishwajeet : 09/19/2001 : Ignore if calculation field as we have not stored
            // data value for it in database. It would be calculated on the fly.
            if (!field.isStorable() && !(field instanceof MetaDataUserField) && !(field instanceof MetaDataDateField) && !(field instanceof MetaDataSpaceField)) {			
                continue;
            }

            // We are processing a new table if the tablename has changed
            if (field.m_data_table_name != null && !field.m_data_table_name.equals(previousTableName) && field.isStorable()) {
                isNewTable = true;
            }

            if (isNewTable) {
                // New table
                // We must add the table to the from clause and potentially
                // build the first select portion
                // Additionally we must join the new table to the object_id
                // of the previous table
                if (isFirstTable) {
                    // First time round
                    // build select portion
                    selectString = "select distinct " + field.m_data_table_name + ".object_id, " + field.m_data_table_name + ".version_id, " +
                        field.m_data_table_name + ".seq_num, " + field.m_data_table_name + ".multi_data_seq ";

                    // Join table to pn_class_instance
                    whereString += "and pn_class_instance.class_instance_id = " + field.m_data_table_name + ".object_id ";

                }

                // Append this table name to the from clause
                fromString += ", " + field.m_data_table_name;
                // If this is a new table but is the second or subsequent table
                // Join it to the previous table
                if (isNewTable && !isFirstTable) {
                    whereString += "and " + previousTableName + ".object_id=" + field.m_data_table_name + ".object_id";
                }

                // The new table now becomes the previous table
                previousTableName = field.m_data_table_name;

                isFirstTable = false;
                isNewTable = false;
            }

            // Add the current field name to the select clause
            selectString += ", " + field.getSQLSelectColumn();
            if(StringUtils.isNotEmpty(field.m_data_table_name)){
                creatorEmail = ", "+ field.m_data_table_name + ".creator_email as creator_email ";
            }
        }

        if (hasAsssignemtColumn){
        	//selectString = selectString + " , PN_ASSIGNMENT.PERSON_ID  ";
        	fromString = fromString + " , PN_ASSIGNMENT ";
        	whereString = whereString +  " AND PN_ASSIGNMENT.OBJECT_ID(+) = " + 
        	           m_form.m_master_table_name + ".object_id AND PN_ASSIGNMENT.RECORD_STATUS(+) = 'A' AND PN_ASSIGNMENT.STATUS_ID(+) <> " 
        	           + AssignmentStatus.REJECTED.getID();
        }
        
        if(field != null && StringUtils.isNotEmpty(field.m_data_table_name)){
        	query.append(selectString + " , (select count(PN_ENVELOPE_HAS_OBJECT.ENVELOPE_ID) from PN_ENVELOPE_HAS_OBJECT " +
	        		" where PN_ENVELOPE_HAS_OBJECT.OBJECT_ID = " + field.m_data_table_name + ".object_id ) env " + 
	        		creatorEmail);
        	setFormDataHasEnvelope(true);
        } else {
        	query.append(selectString + creatorEmail);
        }
        
        query.append(fromString + " , PN_ENVELOPE_HAS_OBJECT ");        
        
        // Update the WHERE clause

        // for multi-table joins and filters.
        query.append(whereString);
        query.append(" and is_current=1 ");

        // Add space filtering to query if we are not loading data for all
        // spaces
/*        if (!isLoadForAllSpaces()) {
            if (getCurrentSpace() == null) {
                throw new NullPointerException("Current space context is null.");
            }
            query.append("and pn_class_instance.space_id = " + getCurrentSpace().getID() + " "); 
        }*/
        if (getForm().isShared() && getForm().getSharedFormSpaceIds().get(getCurrentSpace().getID()) != null && (getForm().getSharedFormSpaceIds().get(getCurrentSpace().getID()).get("childIds") != null )){
        	// Condition for avoiding the oracle error [ORA-01795: maximum number of expressions in a list is 1000]
        	String[] sharedFormChildIdsArray = getForm().getSharedFormSpaceIds().get(getCurrentSpace().getID()).get("childIds").split(",");
        	StringBuilder buff = new StringBuilder();
        	buff.append(" and (pn_class_instance.space_id IN (");
        	for(int index = 0; index < sharedFormChildIdsArray.length; index++) {
                if(index % 900 == 0 && index != 0) {
                	buff.append(") OR pn_class_instance.space_id IN (");
        } 
                if(index == 0 || index % 900 == 0){
                	buff.append(sharedFormChildIdsArray[index]);
                } else {
                	buff.append("," +sharedFormChildIdsArray[index]);
                }
        	}
            buff.append("))");
            query.append(buff.toString());
        } 

        // Apply the filters to build the Where Clause
        if (this.hasFilters()) {
            query.append("and " + m_form.m_master_table_name + ".is_current = 1 ");
            query.append("and (" + getFilterWhereClauses() + ") ");
        }
        
        query.append(" and PN_ENVELOPE_HAS_OBJECT.OBJECT_ID(+) = " + m_form.m_master_table_name + ".object_id " );
        query.append(" and PN_ENVELOPE_HAS_OBJECT.RECORD_STATUS(+) = 'A' ");
        
        // ORDER needed for the while loop below to work.
        query.append("order by " + m_form.m_master_table_name + ".version_id, " + m_form.m_master_table_name + ".multi_data_seq asc");

        return query.toString();
    }


    /**
     Sort the list's data according to the sort fields previously set.
     @param dataArray an ArrayList of FormData.
     */
    private void sortData(ArrayList dataArray) {
        m_formSort.sortData(dataArray);
    }


    /**
     * Load the field filter information for this list.
     * Any fields which have been deleted from form are dropped from filters.
     * @throws PersistenceException if there is a problem loading a filter
     */
    protected void loadFilters() throws PersistenceException {
        String lastOperator = null;
        String lastFieldID = null;
        FieldFilterConstraint constraint = null;
        FieldFilter filter = null;
        String query;
        boolean haveFilter = false;

        String filterFieldID;        // Current row filter field id
        String filterOperator;       // Current row operator
        String filterValue;          // Current row filter value4
        FormField filterFormField;   // Current filter field as a FormField object
        boolean skipField = false;

        m_isLoaded = false;

        DBBean db = new DBBean();
        try {
            // get all the constraints for all fields for this list in one query for speed.
            query = "select field_id, operator, filter_value from pn_class_list_filter where class_id=" + m_class_id + " and  list_id=" + getID() + " order by field_id, operator";
            db.setQuery(query);
            db.executeQuery();

            while (db.result.next()) {

                filterFieldID = db.result.getString("field_id");
                filterOperator = db.result.getString("operator");
                filterValue = db.result.getString("filter_value");

                if (!filterFieldID.equals(lastFieldID)) {
                    // If filter field id not same as last one and this is not the first field id
                    // add the remaining constraint to the current filter and add the filter to this list

                    if (lastFieldID != null) {
                        filter.addConstraint(constraint);
                        addFieldFilter(filter);
                    }

                    // Get the form field for this field id
                    filterFormField = m_form.getField(filterFieldID);

                    if (filterFormField == null) {
                        // Possible reasons include field is now deleted
                        // filter operator / value is skipped
                        skipField = true;

                    } else {
                        // The filter field id is an actual field
                        skipField = false;

                        // Contruct a new filter and add this operator as a
                        // constraint
                        filter = new FieldFilter(filterFormField);
                        constraint = new FieldFilterConstraint();
                        constraint.setOperator(filterOperator);
                        constraint.add(filterValue);

                        haveFilter = true;

                    }

                } else {
                    // Filter field is same as last field
                    // Only process the operator and value if the field
                    // is not being skipped

                    if (!skipField) {

                        if (!filterOperator.equals(lastOperator)) {
                            // Same field, new operator.  Add last operator as a contraint
                            if (lastOperator != null) {
                                filter.addConstraint(constraint);
                            }

                            // Construct a new constraint for this operator
                            constraint = new FieldFilterConstraint();
                            constraint.setOperator(filterOperator);
                            constraint.add(filterValue);

                        } else {
                            // Same field, same operator, new value
                            constraint.add(filterValue);

                        }

                    }

                }

                // Only remember current field as last field
                // if the current filter field was not skipped
                if (!skipField) {
                    lastFieldID = filterFieldID;
                    lastOperator = filterOperator;
                }

            } //end while

            // store the last filter, but only if we found at least one.
            if (haveFilter) {
                filter.addConstraint(constraint);
                addFieldFilter(filter);
                m_isLoaded = true;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(FormList.class).error("FormList.loadFilters() failed " + sqle);
            throw new PersistenceException("Form list load filters operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }


    /**
     Load the field sort information for this list.
     */
    protected void loadSort() {
        ListFieldProperties listFieldProperties;
        FormField field;
        int num_fields;

        m_formSort.setFormList(this);
        num_fields = m_fields.size();

        for (int i = 0; i < num_fields; i++) {
            field = (FormField)m_fields.get(i);
            listFieldProperties = (ListFieldProperties)m_fieldProperties.get(field.getID());

            if ((listFieldProperties != null) && listFieldProperties.isSortField()) {
                try {
                    m_formSort.setSortField(field, listFieldProperties.getSortOrder(), listFieldProperties.isSortAscending());
                } catch (FormSortException fse) {
                    // sort order was out of range.
                }
            }
        }
    }

    /*==========================================================================
        Implementing ILinkableObject
        Only those methods not defined due to other Interfaces
    ======================================================================*/

    /**
     * Return this form data's object type
     * @return the object type
     */
    public String getType() {
        return ObjectType.FORM_LIST;
    }


    /**
     * Return URL which points to this form list
     */
    public String getURL() {
        return URLFactory.makeURL(getID(), ObjectType.FORM_LIST);
    }


    /**
     Get the roster of people for the current space.
     Roster is keep in memory of the Space bean, only need to query once.
     */
    private Roster getRoster() {
        Roster roster;

        if ((roster = m_form.getSpace().getRoster()) == null) {
            roster = new Roster();
            roster.setSpace(m_form.getSpace());
            m_form.getSpace().setRoster(roster);
        }

        if (!roster.isLoaded())
            roster.load();

        return roster;
    }


    //
    // Nested top-level classes
    //

    /**
     * PresentationColumnCount the count of columns displayed when
     * rendering the form list.
     */
    private static class PresentationColumnCount {

        int additionalColumns = 0;
        int listColumns = 0;

        int getTotalColumnCount() {
            return additionalColumns + listColumns;
        }

    }

    /**
     * Provides the total sum of a column.
     * The total is initially undefined (meaning no values were added to the
     * total). A total becomes defined only when a non-null value is added.
     */
    private static class ColumnTotal {

        private boolean isUndefined = true;
        private BigDecimal currentTotal = new BigDecimal("0");

        /**
         * Adds the specified value to the total.
         * @param value the value to add; null values are not added to the
         * total
         */
        private void add(Number value) {
            if (value != null) {
                currentTotal = currentTotal.add(new BigDecimal(value.doubleValue()));
                isUndefined = false;
            }
        }

        /**
         * Indicates whether the total is undefined.
         * A total is undefined if no non-null values were added to it.
         * @return true if the total is undefined; false otherwise
         */
        private boolean isUndefined() {
            return this.isUndefined;
        }

        /**
         * Returns the current total value for this column total.
         * @return the current total value
         */
        private BigDecimal getCurrentTotal() {
            return this.currentTotal;
        }
    }

	/**
	 * @return the isFormDataHasEnvelope
	 */
	public boolean isFormDataHasEnvelope() {
		return isFormDataHasEnvelope;
	}


	/**
	 * @param isFormDataHasEnvelope the isFormDataHasEnvelope to set
	 */
	public void setFormDataHasEnvelope(boolean isFormDataHasEnvelope) {
		this.isFormDataHasEnvelope = isFormDataHasEnvelope;
	}

}




