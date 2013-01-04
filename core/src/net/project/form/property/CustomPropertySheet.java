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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------*/
package net.project.form.property;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.form.FieldDomain;
import net.project.form.Form;
import net.project.form.FormException;
import net.project.form.FormField;
import net.project.form.FormFieldProperty;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.Conversion;

import org.apache.log4j.Logger;

/**
 * Provides default implementation of some IPropertySheet methods for Custom
 * Property Sheets. A CustomPropertySheet is a property sheet that contains a
 * collection of custom properties. This property sheet does not have to be
 * defined in the database as a Form; it is constructed on the fly. Its
 * properties HTML fields are not limited to those available form field types.
 */
public abstract class CustomPropertySheet implements IPropertySheet {
    private Space space = null;
    private User user = null;

    /** Current form context. */
    private Form currentForm = null;
    /** Current form field being managed by property sheet. */
    private FormField managedField = null;
    /** Property sheet id. */
    private String id = null;
    /** This property sheet's custom properties. */
    private CustomPropertyCollection props = new CustomPropertyCollection();
    /** Maintians row, column layout of properties. */
    private PropertyLayout propertyLayout = new PropertyLayout();

    /**
     * Creates a new, empty CustomPropertySheet.
     */
    public CustomPropertySheet() {
        // Do nothign
    }

    /**
     * Creates a new CustomPropertySheet for the specified form context,
     * initializing all custom properties.
     *
     * @param form the current form
     */
    public CustomPropertySheet(Form form) {
        setForm(form);
    }

    /**
     * Returns the custom properties associated with this property sheet.
     * No particular order is guaranteed.
     *
     * @return the collection where each element is an <code>ICusomProperty</code>
     */
    public Collection getCustomProperties() {
        return Collections.unmodifiableCollection(this.props);
    }

    /**
     * Returns the persistent custom properties associated with this property sheet.
     * No particular order is guaranteed.
     *
     * @return the collection where each element is a <code>PersistentCustomProperty</code>
     */
    public Collection getPersistentCustomProperties() {

        Collection persistentCustomProperties = new ArrayList();

        for (Iterator it = getCustomProperties().iterator(); it.hasNext(); ) {
            ICustomProperty nextProperty = (ICustomProperty) it.next();

            if (nextProperty instanceof PersistentCustomProperty) {
                persistentCustomProperties.add(nextProperty);
            }
        }

        return Collections.unmodifiableCollection(persistentCustomProperties);
    }


    /**
     * Returns a collection containing a number of elements equal to the number
     * of rows in the property sheet. Each element is itself a
     * CustomPropertyCollection of the properties on that row. If no properties
     * were added for a row, then there will be an empty collection for that
     * row.
     *
     * @return the collection of CustomPropertyCollection objects where each
     *         element represents the properties on a row.
     */
    public Collection getPropertyRows() {
        return this.propertyLayout.getRowsCollection();
    }


    /**
     * Adds a property to this property sheet at the specified position.
     *
     * @param prop the property to add
     * @param position the position to add at
     */
    public void addCustomProperty(ICustomProperty prop, PropertyLayout.Position position) {
        // Add to simple collection of properties
        this.props.add(prop);

        // Add to property layout
        this.propertyLayout.addProperty(position, prop);
    }

    /**
     * Clears all properties of this property sheet.
     */
    public void emptyCustomProperties() {
        this.props.clear();
    }

    /**
     * Builds collection of custom properties.
     * Called from constructor. Sub-classes should add CustomProperty
     * objects to the collection
     * returned by {@link #getCustomProperties}
     */
    protected abstract void initializeCustomProperties();

    /**
     * Returns the Custom Property belonging to this sheet that matches
     * the specified HTML name.
     *
     * @param name the HTML form field name
     * @return the custom property or <code>null</code> if none matches
     */
    protected PersistentCustomProperty getPropertyForHTMLName(String name) {
        return this.props.getForHTMLName(name);
    }


    /**
     * Sets the form that the property sheet is for.
     *
     * @param form the form
     */
    public void setForm(Form form) {
        this.currentForm = form;
        initializeCustomProperties();
    }

    /**
     * Returns the form that this property sheet is for.
     *
     * @return the form
     */
    protected Form getForm() {
        return this.currentForm;
    }


    /**
     * Sets the field that the property sheet is for.
     *
     * @param field the field
     */
    public void setManagedField(FormField field) {
        this.managedField = field;
    }

    /**
     * Returns the field that this property sheet is for.
     *
     * @return the field
     */
    protected FormField getManagedField() {
        return this.managedField;
    }


    /**
     * Sets the current space context.
     *
     * @param space the current space context
     */
    public void setSpace(net.project.space.Space space) {
        this.space = space;
    }


    protected Space getSpace() {
        return this.space;
    }


    /**
     * Sets the current user context
     *
     * @param user the current user context
     */
    public void setUser(net.project.security.User user) {
        this.user = user;
    }


    protected User getUser() {
        return this.user;
    }


    /**
     * Indicates whether this Property Sheet has a domain list of values.
     * If <code>true</code> is returned, then the managed form field
     * should return the domain via its {@link FormField#getDomain} method.
     *
     * @return true if this property sheet has a domain list of values; false
     * otherwise
     */
    public boolean hasDomain() {
        return false;
    }


    /**
     * Set the id of the property sheet data (display class id)
     */
    public void setID(String id) {
        this.id = id;
    }


    public String getID() {
        return this.id;
    }


    /**
     * Processes request parameters, including special parameters and
     * property parameters.
     * The special parameters cause values in the managed field to be
     * changed.
     *
     * @param request the request containing the parameters
     * @see IPropertySheet#writeHtml for details of the special parameters
     */
    public void processHttpPost(javax.servlet.ServletRequest request)
        throws FormException {

        String name = null;
        String[] values = null;

        //Turn off checkboxes unless they are specifically turned on
        managedField.setValueRequired(false);
        managedField.setHiddenForEaf(false);

        // Process each request parameter
        // If the request parameter represents a specific custom property
        // the values are passed to that custom property

        Enumeration requestNames = request.getParameterNames();
        while (requestNames.hasMoreElements()) {
            name = (String)requestNames.nextElement();
            values = request.getParameterValues(name);

            // Process parameter
            processParameter(name, values, this.managedField);
        }

        // Now add each property to the managed form field
        for (Iterator it = getFormFieldProperties().iterator(); it.hasNext(); ) {
            FormFieldProperty nextProperty = (FormFieldProperty) it.next();
            managedField.addProperty(nextProperty);
        }
    }


    /**
     * Processes the request parameter to see if it is a special parameter
     * and updates the field or custom property.
     *
     * @param name the parameter name
     * @param values the parameter's values
     * @param field the managed field
     */
    protected void processParameter(String name, String[] values, FormField field)
        throws FormException {

        // This code copied from elsewhere expects a parameter to have only
        // one value
        String value = values[0];

        if (name.equals("classID")) {
            field.setClassID(value);

        } else if (name.equals("fieldID")) {
            // Capture and reset isLoaded to ensure value is preserved
            boolean isLoaded = field.isLoaded();
            field.setID(value);
            field.setLoaded(isLoaded);

        } else if (name.equals("clientTypeID")) {
            field.setClientTypeID(value);

        } else if (name.equals("elementID")) {
            field.setElementID(value);

        } else if (name.equals("elementName")) {
            field.setElementName(value);

        } else if (name.equals("elementLabel")) {
            field.setElementLabel(value);

        } else if (name.equals(SpecialPropertyID.FIELD_LABEL.toString())) {
            field.setFieldLabel(value);

        } else if (name.equals(SpecialPropertyID.ROW_NUM.toString())) {
            field.setRowNum(Integer.toString(Conversion.toInt(value)));

        } else if (name.equals("row_span")) {
            field.setRowSpan(Integer.toString(Conversion.toInt(value)));

        } else if (name.equals(SpecialPropertyID.COLUMN.toString())) {
            if (value.equals(Form.LEFT_COLUMN)) {
                field.setColumnNum("1");
                field.setColumnSpan("1");
                field.setColumnID(Form.LEFT_COLUMN);
            } else if (value.equals(Form.RIGHT_COLUMN)) {
                field.setColumnNum("2");
                field.setColumnSpan("1");
                field.setColumnID(Form.RIGHT_COLUMN);
            } else if (value.equals(Form.BOTH_COLUMNS)) {
                field.setColumnNum("1");
                field.setColumnSpan("2");
                field.setColumnID(Form.BOTH_COLUMNS);
            }

        } else if (name.equals("field_group")) {
            field.setFieldGroup(value);

        } else if (name.equals("domain_id")) {
            if ((field.getDomain() != null) && (field.getDomain().getID() != null) && !field.getDomain().getID().equals("")) {
                field.setDomain(new FieldDomain(field.getForm(), value));
            } else {
                field.setDomain(null);
            }

        } else if (name.equals("data_column_size")) {
            // the # characters of storage the user requested.
            field.setDataColumnSize(Integer.toString(Conversion.toInt(value)));

        } else if (name.equals("data_column_scale")) {
            // the scale for number fields
            field.setDataColumnScale(Integer.toString(Conversion.toInt(value)));

        } else if (name.equals("instructions_clob")) {
            field.setInstructions(value);

        } else if (name.equals("is_multi_select")) {
            field.setMultiSelect(Conversion.toBoolean(value));

        } else if (name.equals("use_default")) {
            field.setUseDefault(Conversion.toBoolean(value));

        } else if (name.equals("datatype")) {
            FormFieldProperty property = new FormFieldProperty(name, value, "system");
            field.addProperty(property);

        } else if (name.equals("is_value_required")) {
            field.setValueRequired(Conversion.toBoolean(value));

        } else if (name.equals("hidden_for_eaf")) {
            field.setHiddenForEaf(Conversion.toBoolean(value));                        
        }

        // IGNORE FORM TRASH
        // The following are known name-value pairs returned by the form
        // that we want to ignore so they won't be stored in the properties table.
        else if (name.equals("theAction") ||
            name.equals("ElementID") ||
            name.equals("id") ||
            name.equals("module") ||
            name.equals("action")) {

            // Do Nothing


        } else {
            // Handle Property value
            // Check to see if the request name is the name of a specific property
            PersistentCustomProperty prop = getPropertyForHTMLName(name);

            if (prop != null) {
                prop.setValue(value);

            } else {
                // The name is not handled at all
                // Simply ignore it
                // There are fields on a the FieldEdit screen which are only
                // needed by some field types
            }

        }

        // CHECK CRITICAL fields
        if (field.getClassID() == null || field.getClassID().equals("")) {
            throw new FormException("Custom Property Sheet missing value class ID");
        }

    }


    /**
     * Load the property sheet data.
     */
    public void load() throws PersistenceException {

        if (this.managedField == null || this.managedField.getID() == null
            || this.getForm() == null || this.getForm().getID() == null) {

            return;
        }

        PersistentCustomProperty prop = null;

        String field_id = managedField.getID();
        String class_id = currentForm.getID();

        Iterator iter1 = getPersistentCustomProperties().iterator();
        String query_string = "SELECT";
        int cnt = 0;
        while (iter1.hasNext()) {
            prop = (PersistentCustomProperty)iter1.next();

            String propID = prop.getID();

            if (this.isStaticProperty(propID)) {
                if (cnt == 0) {
                    query_string = query_string + " " + propID;
                    cnt++;
                } else {

                    query_string = query_string + ", " + propID;
                    cnt++;
                }
            }

        }

        String from_string = "FROM pn_class_field" + " ";
        String where_string = "WHERE class_id = " + "'" + class_id + "'" + " and " +
            "field_id = " + "'" + field_id + "'";

        query_string = query_string + " " + from_string + " " + where_string;

        DBBean db = new DBBean();

        try {
            db.setQuery(query_string);
            db.executeQuery();
            Iterator iter2 = getPersistentCustomProperties().iterator();
            while (db.result.next()) {  //Set the property value for the static properties
                while (iter2.hasNext()) {
                    prop = (PersistentCustomProperty)iter2.next();

                    if (this.isStaticProperty(prop.getID())) {

                        if (isStaticPropertyClob(prop.getID())) {
                            prop.setValue(ClobHelper.read(db.result.getClob(prop.getID())));

                        } else {
                            prop.setValue(db.result.getString(prop.getID()));

                        }
                    }

                }
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(CustomPropertySheet.class).error("Form.loadFields failed " + sqle);
            throw new PersistenceException("Failed to load form fields", sqle);
        } finally {

            db.release();
        }


        String select_string = "SELECT property, value FROM pn_class_field_property WHERE " + " " +
            "class_id = " + "'" + class_id + "'" + " " +
            "and field_id = " + "'" + field_id + "'";

        try {
            db.setQuery(select_string);
            db.executeQuery();

            HashMap hm = new HashMap(); // store the retrieved property value pairs
            while (db.result.next()) {
                String property = db.result.getString(1);
                String value = db.result.getString(2);
                hm.put(property, value);
            }

            Iterator iter3 = getPersistentCustomProperties().iterator();
            while (iter3.hasNext()) {
                prop = (PersistentCustomProperty)iter3.next();
                String propID = prop.getID();
                if (!this.isStaticProperty(propID)) {
                    prop.setValue((String)hm.get(propID));
                }
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(CustomPropertySheet.class).error("Form.loadFields failed " + sqle);
            throw new PersistenceException("Failed to load form fields", sqle);
        } finally {

            db.release();
        }

    }


    /**
     * Store the property sheet data.
     */
    public void store() throws PersistenceException {
        this.managedField.store();

    }


    /**
     * Unimplemented.
     *
     * @throws PersistenceException always
     */
    public void remove() throws PersistenceException {
        throw new PersistenceException("CustomPropertySheet.remove() not implemented.");
    }


    /**
     * Returns this property sheet's custom properties as FormFieldProperty
     * objects.
     *
     * @return the collection of FormFieldProperty objects
     */
    public Collection getFormFieldProperties() {
        PersistentCustomProperty prop = null;
        FormFieldProperty formFieldProp = null;
        ArrayList formFieldProperties = new ArrayList(getPersistentCustomProperties().size());

        // For each PersistentCustomProperty, construct as FormFieldProperty
        // and add to list
        Iterator it = getPersistentCustomProperties().iterator();
        while (it.hasNext()) {
            prop = (PersistentCustomProperty)it.next();

            formFieldProp = new FormFieldProperty(prop.getID(), prop.getValue(), "custom");
            formFieldProperties.add(formFieldProp);
        }

        // Return list of FormFieldProperty objects
        return formFieldProperties;
    }

    /**
     * Is the passed property stored in the pn_class_field table as a static
     * column?
     */
    public boolean isStaticProperty(String name) {
        if (name.equals("class_id"))
            return true;
        else if (name.equals("field_id"))
            return true;
        else if (name.equals("element_id"))
            return true;
        else if (name.equals("client_type_id"))
            return true;
        else if (name.equals("field_label"))
            return true;
        else if (name.equals("row_num"))
            return true;
        else if (name.equals("row_span"))
            return true;
        else if (name.equals("column_id"))
            return true;
        else if (name.equals("column_span"))
            return true;
        else if (name.equals("field_group"))
            return true;
        else if (name.equals("domain_id"))
            return true;
        else if (name.equals("data_column_size"))
            return true;
        else if (name.equals("data_column_scale"))
            return true;
        else if (name.equals("instructions_clob"))
            return true;
        else if (name.equals("use_default"))
            return true;
        else if (name.equals("is_multi_select"))
            return true;
        else if (name.equals("is_value_required"))
            return true;
        else if (name.equals("hidden_for_eaf"))
            return true;        
        else
            return false;
    }

    /**
     * Indicates whether the specified static property is actually stored
     * as a clob.
     * @param name the name of the static property
     * @return true if the property is a clob; false otherwise
     */
    private boolean isStaticPropertyClob(String name) {
        if (name.equals("instructions_clob")) {
            return true;
        }
        return false;
    }

}
