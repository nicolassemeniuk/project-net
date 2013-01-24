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
|     $RCSfile$
|    PersonListField.java
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.form;

import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletRequest;

import net.project.base.ObjectType;
import net.project.database.DBFormat;
import net.project.database.ObjectManager;
import net.project.space.Space;
import net.project.util.Conversion;
import net.project.util.ErrorReporter;
import net.project.util.Validator;
import net.project.xml.XMLFormatter;

import org.apache.log4j.Logger;

/**
 * Creates and modifies the design of a FormList.
 *
 * @author Roger Bly
 * @since 03/2000
 */
public class FormListDesigner extends FormList {
    /** Contains XML formatting information and utilities specific to this object **/
    protected XMLFormatter m_formatter;
    /** is this a newly created field that has not been added to the form yet */
    protected boolean m_isNewList = false;

    /**
     * Sole Constructor.
     */
    public FormListDesigner() {
        super();
        m_formatter = new XMLFormatter();
    }


    /**
     * Is this a newly created list that has not been added to the form yet.
     */
    public boolean isNewList() {
        return m_isNewList;
    }


    /**
     * Sets whether this is a newly created list that has not been added to the
     * form yet.
     *
     * @param status true if is a new list not yet on the form, false if the
     * list already exists on the form.
     */
    public void setIsNewList(boolean status) {
        m_isNewList = status;
    }


    /**
     * Set the properties of this FormListDesigner from an instance of it's base
     * class, FormList.
     *
     * @param list the FormList to initialize this FormListDesigner from.
     */
    public void setFormList(FormList list) {
        m_fields = list.m_fields;
        m_fieldProperties = list.m_fieldProperties;

        m_list_id = list.m_list_id;
        m_is_default = list.m_is_default;
        m_list_name = list.m_list_name;
        m_list_description = list.m_list_description;
        m_field_cnt = list.m_field_cnt;
        m_page_bg_color = list.m_page_bg_color;
        m_table_color = list.m_table_color;
        m_header_color = list.m_header_color;
        m_row_color1 = list.m_row_color1;
        m_row_color2 = list.m_row_color2;
        m_crc = list.m_crc;
        m_isLoaded = list.m_isLoaded;
    }


    /**
     * Sets this list as a default for the user.
     *
     * @param value true if this list is shared with all Space users, false
     * otherwise.
     */
    public void setIsDefault(boolean value) {
        m_is_default = value;
    }


    /**
     * Sets this list as an administrator list that is available to all users.
     *
     * @param value true if this list is shared with all Space users, false
     * otherwise.
     */
    public void setIsShared(boolean value) {
        m_is_shared = value;
    }


    /**
     * Sets this list as an administrator list that is available to all users.
     * Admin lists are always shared to all users.
     *
     * @param value true if this is an admin list, false otherwise.
     */
    public void setIsAdmin(boolean value) {
        m_is_admin = value;
        setIsShared(true);
    }


    /**
     * Sets the person or space ID that owns (created) this list.  If a
     * non-personal space owns the list, the list will be shared by all people
     * as an 'admin list'.  <code>setIsAdmin(true)</code> must also be called.
     *
     * @param spaceID the Space that owns (created) this list.
     */
    public void setOwnerSpaceID(String spaceID) {
        m_owner_space_id = spaceID;
    }


    /**
     * Add this formList to a project.net Space (personal space, project space,
     * business space, work space, methodology).
     *
     * @param space the space the FormList will added to.
     */
    public void addToSpace(Space space) {
        String query;

        // ADD LIST TO SPACE
        // update in database for this Space context.  If it is a the new defualt list, set as new default in DB.

        try {
            // Check to see if the list already exists
            query = "select list_id from pn_space_has_class_list where space_id= " + space.getID() + " and list_id=" + m_list_id + " and class_id=" + m_class_id;
            m_form.db.setQuery(query);
            m_form.db.executeQuery();

            // new list, must insert
            if (!m_form.db.result.next()) {
                // If this is a new default list, clear all the default lists for this space, before setting.
                if (m_is_default) {
                    query = "update pn_space_has_class_list set is_default=0 where space_id= " + space.getID() + "  and class_id=" + m_class_id;
                    m_form.db.release();
                    m_form.db.setQuery(query);
                    m_form.db.executeQuery();
                }

                query = "insert into pn_space_has_class_list (space_id, class_id, list_id, is_default) " +
                    "values (" + space.getID() + ", " + m_class_id + ", " + m_list_id + " , " + DBFormat.bool(m_is_default) + ")";
                m_form.db.release();
                m_form.db.setQuery(query);
                m_form.db.executeQuery();
            }
            // list exists, must update
            else {
                // If this is a new default list, clear all the default lists for this space, before setting.
                if (m_is_default) {
                    query = "update pn_space_has_class_list set is_default=0 where space_id= " + space.getID() + "  and class_id=" + m_class_id;
                    m_form.db.release();
                    m_form.db.setQuery(query);
                    m_form.db.executeQuery();
                }

                query = "update pn_space_has_class_list set is_default=" + DBFormat.bool(m_is_default) +
                    " where space_id= " + space.getID() + " and list_id=" + m_list_id + " and class_id=" + m_class_id;
                m_form.db.release();
                m_form.db.setQuery(query);
                m_form.db.executeQuery();
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(FormListDesigner.class).error("FormListDesigner.addToSpace threw an SQL Exception: " + sqle);
        } finally {
            m_form.db.release();
        }

    }

    /********************************************************************************************************************
     *   Designer specific Presentation of XML
     ********************************************************************************************************************/

    /**
     * Sets the stylesheet file name used to render an XML document.  This
     * stylesheet will be used by the next call to getListPresentation,
     * getFieldsPresentation, This method accepts the name of the stylesheet
     * used to convert the XML representation of an object to a presentation
     * form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML
     * representation of the object.
     */
    public void setStylesheet(String styleSheetFileName) {
        m_formatter.setStylesheet(styleSheetFileName);
    }

    /**
     * Gets the presentation of the FormList.  This method will apply the
     * stylesheet to the XML representation of the FormList and return the
     * resulting text.
     *
     * @return presetation of the FormList
     */
    public String getPresentation() {
        return m_formatter.getPresentation(getXML());
    }

    public void verifyHttpPost(ServletRequest request, ErrorReporter errorReporter) {
        Enumeration enumeration = request.getParameterNames();

        while (enumeration.hasMoreElements()) {
            String paramName = (String) enumeration.nextElement();
            String paramValue = request.getParameter(paramName);

            if (paramName.startsWith("column_num__")) {
                //Check to make sure the field is checked, if it isn't, we'll ignore it
                if (Conversion.toBool(request.getParameter("field_checked__" + paramName.substring(12)))) {
                    if (!Validator.isBlankOrNull(paramValue) && !Validator.isNumeric(paramValue)) {
                        errorReporter.addError(paramValue + " is not a valid column number.");
                    }
                }
            }

            if (paramName.startsWith("field_width__")) {
                if (Conversion.toBool(request.getParameter("field_checked__" + paramName.substring(13)))) {
                    if (!Validator.isBlankOrNull(paramValue) && !Validator.isNumeric(paramValue)) {
                        errorReporter.addError(paramValue + " is not a valid width.");
                    }
                }
            }
        }
    }

    /**
     * Get FormList definition from an HTTP post.
     */
    public void processHttpPost(ServletRequest request) throws FormException {
        FormField field = null;
        String fieldID;
        FieldFilter fieldFilter = null;
        ListFieldProperties fieldProperties = null;
        boolean sortAscending = true;
        int num_fields = 0;
        int i = 0;


        // new list coming in... clear
        clear();
        initProperties();

        // The form is expected to always return the following Hidded fields:
        m_class_id = request.getParameter("class_id");
        m_list_id = request.getParameter("list_id");

        // New List being submitted
        if ((m_list_id == null) || (m_list_id.equals(""))) {
            m_list_id = ObjectManager.dbCreateObjectWithPermissions(ObjectType.FORM_LIST, "P", m_form.getSpace().getID(), m_form.getUser().getID());
        }

        if (m_class_id == null || m_list_id == null || m_class_id.equals("") || m_list_id.equals(""))
            throw new FormException("No class_id or list_id returned by form post.");

        // Get class_list field from the form.
        m_list_name = request.getParameter("name");
        m_list_description = request.getParameter("description");
        m_is_default = Conversion.toBool(request.getParameter("isDefault"));


        // SELECTED FIELDS
        // for each field in the parent Form, get the user's  "is_selected" fields and save on the FormList m_fields array.
        if (m_form == null || m_form.m_fields == null || ((num_fields = m_form.m_fields.size()) < 1)) {
            throw new FormException("No fields defined for this form.");
        }

        m_field_cnt = 0;

        for (i = 0; i < num_fields; i++) {
            // get the matching field from the parent Form object.
            field = (FormField)m_form.m_fields.get(i);
            fieldProperties = (ListFieldProperties)m_fieldProperties.get(field.getID());

            m_fields.add(field);

            if (fieldProperties == null) {
                fieldProperties = new ListFieldProperties();
                m_fieldProperties.put(field.getID(), fieldProperties);
            }

            fieldProperties.m_is_list_field = Conversion.toBool(request.getParameter("field_checked__" + field.m_field_id));
            if (Validator.isNumeric(request.getParameter("column_num__" + field.m_field_id))) {
                fieldProperties.m_field_order = request.getParameter("column_num__" + field.m_field_id);
            }
            if (Validator.isNumeric(request.getParameter("field_width__" + field.m_field_id))) {
                fieldProperties.m_field_width = request.getParameter("field_width__" + field.m_field_id);
            }
            fieldProperties.m_wrap_mode = Conversion.toBool(request.getParameter("wrap_mode__" + field.m_field_id));
            fieldProperties.m_is_subfield = Conversion.toBool(request.getParameter("indented_subfield__" + field.m_field_id));
            fieldProperties.m_is_calculateTotal = Conversion.toBool(request.getParameter("calculate_total__" + field.m_field_id));

            if (fieldProperties.m_is_list_field) {
                m_field_cnt++;
            }

            /* Modified By: Vishwajeet Date :09/04/2001

               Moved this code out of the for loop to fix a bug which would not allow you to submit a list if
               the form has Horizontal Section Title or Instruction field as the first field  .

               if (m_field_cnt < 1)
                   throw new FormException( "You must select at least one field for the list.");
            */
        }

        if (m_field_cnt < 1)
            throw new FormException("You must select at least one field for the list.");

        //
        // Now process all filtering fields
        //
        for (i = 0; i < num_fields; i++) {
            // get the matching field from the parent Form object.
            field = (FormField)m_form.m_fields.get(i);

            if (field.isFilterable()) {
                fieldFilter = field.processFilterHttpPost(request);
                if (fieldFilter != null) {
                    fieldFilter.setList(this);
                    addFieldFilter(fieldFilter);
                }
            }
        }

        // SORT FIELDS
        for (i = 0; i < 3; i++) {
            // set the FormSort properties
            fieldID = request.getParameter("sortFieldID" + i);

            // if "None" was not selected.
            if ((fieldID != null) && !fieldID.equals("")) {
                sortAscending = Conversion.toBool(request.getParameter("sortAscending" + i));
                setSortField(fieldID, i, sortAscending);

                // set the ListFieldProperties.
                fieldProperties = this.getListFieldProperties(fieldID);
                fieldProperties.setSortOrder(i);
                fieldProperties.setSortAscending(Conversion.toBool(request.getParameter("sortAscending" + i)));
                fieldProperties.setIsSortField(true);
            }

        }

    }
}

