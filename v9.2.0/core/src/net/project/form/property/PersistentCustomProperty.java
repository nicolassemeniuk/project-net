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

 package net.project.form.property;


/**
 * A PersistentCustomProperty represents the property of a designed form field
 * that is persisted to the database. PersistentCustomProperty objects are
 * stored differently to FieldProperty objects. They allow CustomPropertySheets
 * to implement any arbitrary property sheet without having to define that
 * PropertySheet as a Form itself.
 * 
 * @since falcon
 * @author Tim
 */
public abstract class PersistentCustomProperty implements ICustomProperty,
		java.io.Serializable {

	private String propertyID = null;

	private String displayName = null;

	private String value = null;

	private boolean isValueRequired = false;

	/**
	 * Creates a new custom property with specified id and display name.
	 * 
	 * @param id
	 *            the id of this property
	 * @param displayName
	 *            the display name (that is, label) of this property
	 */
	protected PersistentCustomProperty(String id, String displayName) {
		setID(id);
		setDisplayName(displayName);
	}

	/**
	 * Sets this property's id. The id must be unique among the properties in a
	 * property sheet.
	 * 
	 * @param id
	 *            the property id
	 */
	protected void setID(String id) {
		this.propertyID = id;
	}

	/**
	 * Returns this property's id.
	 * 
	 * @return the unique id of this property
	 */
	public String getID() {
		return this.propertyID;
	}

	/**
	 * Sets the display name of this property.
	 * 
	 * @param displayName
	 *            the property's display name
	 */
	protected void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Returns this property's display name.
	 * 
	 * @return the display name of this property
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Sets this property's value.
	 * 
	 * @param value
	 *            the property value
	 */
	protected void setValue(String value) {
		this.value = value;
	}

	/**
	 * Sets this property's value as the first value from the array.
	 * 
	 * @param values
	 *            the property values
	 */
	protected void setValues(String[] values) {
		this.value = values[0];
	}

	/**
	 * Returns this property's value.
	 * 
	 * @return the property value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Returns this property's value for display.
	 * 
	 * @return the value or <code>""</code> if the value is null
	 */
	public String getDisplayValue() {
		if (getValue() == null) {
			return "";
		} else {
			return getValue();
		}
	}

	/**
	 * Returns the HTML name of this property. This is used when rendering data
	 * entry fields for this property.
	 * 
	 * @return the HTML presentation name for this property
	 */
	protected String getPresentationHTMLName() {
		return "prop_" + getID();
	}

	/**
	 * Specifies whether this property requires a value.
	 * 
	 * @param isValueRequired
	 *            true if a value must be specified for this property when
	 *            editing a property sheet; false if the value is optional
	 */
	protected void setValueRequired(boolean isValueRequired) {
		this.isValueRequired = isValueRequired;
	}

	public boolean isValueRequired() {
		return this.isValueRequired;
	}

	/**
	 * Helper method to return Javascipt code for indicating a field is
	 * required. <p/> Sub-classes may call this from <code>writeHtml</code> to
	 * generate the function call to indicate a field is required. The view page
	 * to which the HTML is written is expected to implement the function.
	 * 
	 * @return Javascript code of the form
	 *         <code>&lt;script&gt;addRequiredField('<i>columnname</i>', '<i>message</i>');&lt;/script&gt;</code>
	 */
	protected String getRequiredValueJavascript() {
		return "<script language=\"JavaScript\">fieldNames[\"" + getID()
				+ "\"]=" + "\"" + getDisplayName() + "\"</script>";
	}

}
