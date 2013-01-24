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
|   $Revision: 19714 $
|       $Date: 2009-08-11 15:09:52 -0300 (mar, 11 ago 2009) $
|     $Author: vivana $
|
+----------------------------------------------------------------------*/
package net.project.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.util.HTMLUtils;
import net.project.util.NumberFormat;
import net.project.util.Validator;

/**
 * A NumberField is used for storing numbers in a Form.
 *
 * @author Tim
 * @since 08/2001
 */
public class NumberField extends FormField {

    /**
     * Creates a new NumberField belonging to the specified form with the
     * specified id.
     * @param form the form to which this NumberField belongs
     * @param fieldID the id of the NumberField
     */
    public NumberField(Form form, String fieldID) {
        super(form, fieldID);
    }

    /**
     * Can this field be used for filtering.
     * @return true if the field can be used to filter form data.
     */
    public boolean isFilterable() {
        return false;
    }

    /**
     * Can this field be used for searching.
     * @return true if the field can be used to filter form data.
     */
    public boolean isSearchable() {
        return false;
    }

    /**
     * Returns the database storage type for the field.
     * For example <code>NUMBER(10)</code> or <code>NUMBER(10,5)</code>.
     * The first number is the precision from {@link #getDataColumnSize}.
     * The second number is the scale from {@link #getDataColumnScale} and is
     * only added if greater than 0.
     * @return the database storage type for the field
     */
    public String dbStorageType() {
        StringBuffer storageType = new StringBuffer();
        storageType.append(getDatatype());
        storageType.append("(");
        storageType.append(getDataColumnSize());

        // If we have a non-zero scale, add it to the storage type
        if (Integer.parseInt(getDataColumnScale()) > 0) {
            storageType.append("," + getDataColumnScale());
        }

        storageType.append(")");
        return storageType.toString();
    }

    /**
     * Outputs an HTML representation of the field's filter to the specified stream.
     */
    public void writeFilterHtml(FieldFilter filter, java.io.PrintWriter out) throws java.io.IOException {
        throw new java.io.IOException("NumberField.writeFilterHtml not implemented");
    }

    /**
     * Process the HTTP request to extract the filter selections for this field.
     * @return the FieldFilter containing the filter information for the field
     */
    public FieldFilter processFilterHttpPost(javax.servlet.ServletRequest request) {
        FieldFilter filter = null;
        FieldFilterConstraint constraint = null;

        String value = request.getParameter("filter__" + m_field_id);
        if ((value != null) && value.equals("")) {
            value = null;
        }

        // Create a new FieldFilter for this number field
        filter = new FieldFilter(this);

        // Create a new "=" constraint
        constraint = new FieldFilterConstraint();
        constraint.setOperator("=");
        constraint.add(value);

        // Add the contraint to the filter
        filter.addConstraint(constraint);

        return filter;
    }

    /**
     * Can this field be used for sorting.
     * @return true if the field can be used to sort form data.
     */
    public boolean isSortable() {
        return true; // Modified to true : Vishwajeet : 09/20/2001
    }

    /**
     * Validates that the specified FieldData contains numbers.
     * The data is assumed to contain numbers in the current user's locale
     * format.
     * @param data the field data to validate containing the locale-specific numbers
     * @param errorMessagePatternBuffer the buffer into which the error message
     * pattern is written
     * @return true if all values in the field data are numbers; false otherwise
     */
    public boolean isValidFieldData(FieldData data, StringBuffer errorValue, StringBuffer errorMessagePatternBuffer) {
        boolean isValid = false;
        String value = null;
        double numberValue = 0.0;
        int scale = this.m_data_column_scale < 0 ? 0 : this.m_data_column_scale;
        int size = this.m_data_column_size;

        try {
            Number number = null;

            Iterator it = data.iterator();
            while (it.hasNext()) {
                value = (String)it.next();

                // Skip null or empty values; they are valid
                if (value != null && value.length() > 0) {
                    number = NumberFormat.getInstance().parseNumber(value);
                    BigDecimal bigDecimal = new BigDecimal(number.doubleValue());
                    numberValue = bigDecimal.abs().doubleValue();
                }

                if (numberValue >= Math.pow(10, size - scale)) {
                    throw new InvalidNumberFieldException(" Invalid Number for the NumberField ", this.m_data_column_size);
                }

            }

            // data is valid if all values successfully parsed
            isValid = true;

        } catch (InvalidNumberFieldException infe) {
            errorValue.append(value);
            errorMessagePatternBuffer.append(PropertyProvider.get("prm.form.number.validate.toomanydigits.message", new Object[]{value, getNumberPattern()}));

        } catch (java.text.ParseException pe) {
            errorValue.append(value);
            errorMessagePatternBuffer.append(PropertyProvider.get("prm.form.number.validate.notnumber.message", new Object[]{value, getNumberPattern()}));
        }

        return isValid;
    }

    /**
     * Turns the field data into numbers and back into a string.  This is necessary because
     * the raw number may look different than the entered string number, but
     * still be parsed correctly.
     * For example, the number "444xxx" is valid (according to Java) and is stored as
     * "444".
     * This also converts the data into a non-localized form; it should never
     * be displayed to the user without first being formatted
     */
    public void processFieldData(FieldData data) {
        ArrayList newData = new ArrayList(data.size());

        String value = null;
        Number number = null;
        double numberValue = 0.0;
        BigDecimal tempBigDecimal = null;
        int scale = this.m_data_column_scale < 0 ? 0 : this.m_data_column_scale;
        int size = this.m_data_column_size;

        // Add each parsed and re-formatted number to the new data
        // structure.
        Iterator it = data.iterator();
        while (it.hasNext()) {
            value = (String)it.next();

            try {
                if (!Validator.isBlankOrNull(value)) {
                    number = NumberFormat.getInstance().parseNumber(value);
                    BigDecimal bigDecimal = new BigDecimal(number.doubleValue());

                    tempBigDecimal = bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
                    numberValue = tempBigDecimal.abs().doubleValue();

                    if (numberValue >= Math.pow(10, size - scale)) {
                        bigDecimal = bigDecimal.setScale(scale, BigDecimal.ROUND_DOWN);
                    } else {
                        bigDecimal = tempBigDecimal;
                    }

                    //We need to use DBFormat to format the number instead of
                    //using the NumberFormat.formatNumber() method.  That method
                    //always writes out in the localized form.  The database
                    //won't understand that form.
                    newData.add(bigDecimal.toString());

                } else {
                    newData.add(null);
                }

            } catch (java.text.ParseException pe) {
                // This should not occur if the data has been validated
                // successfully.  If it does, the number is ignored
            }

        }

        // Now replace the elements in existing data with
        // new data elements
        data.clear();
        data.addAll(newData);
    }

    /**
     * Returns the number stored within the specified fieldData.
     * Uses only the first item from the fieldData.
     * @return the number contained within the fieldData or null if the
     * fieldData is empty or does not contain a number.
     */
    Number getNumber(FieldData fieldData) {

        Number number = null;

        if (fieldData != null && fieldData.size() > 0) {
            String firstItem = (String) fieldData.get(0);

            if (firstItem != null && firstItem.trim().length() > 0) {
                // We have a value

                try {
                    number = new BigDecimal(firstItem);

                } catch (NumberFormatException e) {
                    // Do nothing; number remains null

                }
            }
        }

        return number;
    }

    /**
     * Returns display pattern to indicate the pattern required for entering
     * a number based on the precision and scale of this field.
     * The pattern is not a valid Java pattern; Java patterns are too technical
     * to display to the user.  Instead, the pattern indicates the number of
     * integer and decimal digits required using the locale-specific digit
     * grouping and decimal separator characters.
     * @return the number pattern, for example <code>#,###.##</code> indicating
     * 4 integer digits and 2 decimal digits in the US locale.
     */
    private String getNumberPattern() {
        int scale = this.m_data_column_scale < 0 ? 0 : this.m_data_column_scale;
        int size = this.m_data_column_size;

        StringBuffer unlocalizedNumberBuffer = new StringBuffer();
        int maximumIntegerDigits = size - scale;

        if (maximumIntegerDigits > 0) {
            for (int i = 0; i < maximumIntegerDigits; unlocalizedNumberBuffer.append("9"), i++) ;
        } else {
            unlocalizedNumberBuffer.append("0");
        }


        // Add decimal point and decimal digits
        // If there is a scale
        if (scale > 0) {
            unlocalizedNumberBuffer.append(".");
            // Add "scale" number of "#" characters
            for (int i = 0; i < scale; unlocalizedNumberBuffer.append("9"), i++) ;
        }

        // Now construct a number and format it for the locale
        BigDecimal unlocalizedNumber = new BigDecimal(unlocalizedNumberBuffer.toString());

        // Now return the pattern converted to a localized pattern
        return NumberFormat.getInstance().formatNumber(unlocalizedNumber.doubleValue());
    }

    /**
     * Formats the specified data for presentation.
     * @param fieldData the data to format; this is assumed to contain one
     * item and to be a number and in a non-localized form
     * @return the number formatted for the user's locale or the empty string
     * if it could not be formatted
     */
    public String formatFieldData(FieldData fieldData) {

        String formattedValue = null;

        Number number = getNumber(fieldData);
        if (number != null) {
            formattedValue = NumberFormat.getInstance().formatNumber(number.doubleValue());
        }

        if (formattedValue == null) {
            formattedValue = "";
        }

        return formattedValue;
    }


    /**
     * Outputs an HTML representation of this object to the specified stream.
     * A Number field's representation is
     */
    public void writeHtml(FieldData fieldData, java.io.PrintWriter out)
        throws java.io.IOException {

        FormFieldProperty property;

        // Insert field label, flagging as an error if there is an error
        // for this field
        out.print("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + m_form.getFlagError(getDataColumnName(), HTMLUtils.escape(getFieldLabel())) + ":&nbsp;&nbsp;</td>\n");

        // Start table division for field tag
        out.print("<td align=\"left\" ");
        if (this.m_column_span > 1) {
            out.print("colspan=\"" + ((this.m_column_span * 2) - 1) + "\"");
        }
        out.print(">\n");

        // Insert the field tag
        // This originally came from a property of type "tag"
        out.print("<" + getTag() + " ");
        out.print("name=\"" + getDataColumnName() + "\" ");

        if (isValueRequired) {
            out.print(" required=\"true\" ");
        }

        // Insert this field's in_tag properties (i.e. HTML attributes)
        Iterator propertyIt = getProperties().iterator();
        while (propertyIt.hasNext()) {
            property = (FormFieldProperty)propertyIt.next();

            if (property.getType().equals("in_tag")) {

                // Property name
                if (property.getName() != null && !property.getName().equals("")) {
                    out.print(" " + property.m_name);
                }

                // Property value
                if (property.m_value != null && !property.m_value.equals("")) {
                    out.print("=\"" + property.getValue() + "\" ");
                }
            }
        }

        // write the data value
        out.print(" value=\"");
        if (fieldData != null && fieldData.size() > 0) {
            out.print(formatFieldData(fieldData));
        }
        out.print("\">");

        out.print("&nbsp;");

        // Now include any error message, if present
        out.print(m_form.getErrorMessage(getDataColumnName()));

        // Add appropriate script for indicating field is required
        if (isValueRequired()) {
            out.println(getRequiredValueJavascript());
        }
        // Close the table division for field tag
        out.println("</td>");
    }


    /**
     * Outputs an Read Only HTML representation of this field to the specified stream.
     * This will typically include table divisions spanning up to 4 columns.
     */
    public void writeHtmlReadOnly(FieldData fieldData, java.io.PrintWriter out)
        throws java.io.IOException {

        FormFieldProperty property;

        // Insert field label, flagging as an error if there is an error
        // for this field
        out.print("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + m_form.getFlagError(getDataColumnName(), HTMLUtils.escape(getFieldLabel())) + ":&nbsp;&nbsp;</td>\n");

        // Start table division for field tag
        out.print("<td align=\"left\" ");
        if (this.m_column_span > 1) {
            out.print("colspan=\"" + ((this.m_column_span * 2) - 1) + "\"");
        }
        out.print(">\n");

        // Insert the field tag
        // This originally came from a property of type "tag"
        out.print("<" + getTag() + " ");
        out.print("readonly" + " ");
        out.print("name=\"" + getDataColumnName() + "\" ");

        // Insert this field's in_tag properties (i.e. HTML attributes)
        Iterator propertyIt = getProperties().iterator();
        while (propertyIt.hasNext()) {
            property = (FormFieldProperty)propertyIt.next();

            if (property.getType().equals("in_tag")) {

                // Property name
                if (property.getName() != null && !property.getName().equals("")) {
                    out.print(" " + property.m_name);
                }

                // Property value
                if (property.m_value != null && !property.m_value.equals("")) {
                    out.print("=\"" + property.getValue() + "\" ");
                }

            }

        }

        // write the data value
        out.print("value=\"");
        if (fieldData != null && fieldData.size() > 0) {
            out.print(formatFieldData(fieldData));
        }
        out.print("\">");

        out.print("&nbsp;");

        // Now include any error message, if present
        out.print(m_form.getErrorMessage(getDataColumnName()));

        // Close the table division for field tag
        out.println("</td>");
    }

    public String writeHtmlReadOnly(FieldData fieldData){
    
    StringBuffer fieldHtml = new StringBuffer();
    

    // Insert field label, flagging as an error if there is an error
    // for this field
    fieldHtml.append("<td align=\"left\" width=\"1%\" class=\"tableHeader\">" + m_form.getFlagError(getDataColumnName(), HTMLUtils.escape(getFieldLabel())) + ": </td>");

    // Start table division for field tag
    fieldHtml.append("<td align=\"left\" class=\"tableContent\"");
    if (this.m_column_span > 1) {
    	fieldHtml.append("colspan=\"" + ((this.m_column_span * 2) - 1) + "\"");
    }
    fieldHtml.append(">");

    // write the data value
    if (fieldData != null && fieldData.size() > 0) {
    	fieldHtml.append(formatFieldData(fieldData));
    }
    // Close the table division for field tag
    fieldHtml.append("</td>");    
    return fieldHtml.toString();
}    
    

    /**
     * @return true/false depending upon whether it is selectable for display in
     * the list.
     */
    public boolean isSelectable() {
        return true;
    }

    public int compareData(FieldData data1, FieldData data2) {
        String value1 = (String)(data1.size() > 0 ? data1.get(0) : null);
        String value2 = (String)(data2.size() > 0 ? data2.get(0) : null);

        //First, handle null values
        if (value1 == null && value2 == null) {
            return 0;
        } else if (value1 == null) {
            return -1;
        } else if (value2 == null) {
            return 1;
        }

        //Now parse and compare as necessary
        double value1D = Double.parseDouble(value1);
        double value2D = Double.parseDouble(value2);

        if (value1D == value2D) {
            return 0;
        } else if (value1D < value2D) {
            return -1;
        } else {
            return 1;
        }
    }

}
