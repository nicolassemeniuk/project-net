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

import java.util.HashMap;
import java.util.Iterator;

import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.form.property.CalculationFieldPropertySheet;
import net.project.form.property.CustomPropertySheet;
import net.project.form.property.PropertySheetFactory;
import net.project.persistence.PersistenceException;
import net.project.util.HTMLUtils;
import net.project.util.NumberFormat;

import org.apache.log4j.Logger;

/**
 * A CalculationField provides a result calculated from other fields.
 * It is never stored.
 */
public class CalculationField extends FormField {

    /**
     * Creates a new CalculationField for the specified form, with the
     * specified id
     *
     * @param form the form to which this field is being added
     * @param fieldID the id of this field
     */
    public CalculationField(Form form, String fieldID) {
        super(form, fieldID);
        // CalculationField Property Sheet ID
        super.setElementDisplayClassID(FormField.CALCULATION_FIELD);
    }

    /**
     * Formats the field data.
     * <b>Note:</b> This method igonres the specified fieldData.
     * Instead, it uses the data from the form.
     *
     * @param fieldData the fieldData to format
     * @return the formatted field data
     */
    public String formatFieldData(FieldData fieldData) {
        return formatCalculation(getForm().getData());
    }

    /**
     * Can this field be used for filtering.
     *
     * @return true if the field can be used to filter form data.
     */
    public boolean isFilterable() {
        return false;
    }

    /**
     * Can this field be used for searching.
     *
     * @return true if the field can be used to filter form data.
     */
    public boolean isSearchable() {
        return false;
    }

    /**
     * @return the database storage type for the field. i.e. NUMBER(20)
     */
    public String dbStorageType() {
        return null;

    }


    /**
     * @return true/false depending upon whether it is selectable for display in
     * the list.
     */
    public boolean isSelectable() {
        return true;
    }


    /**
     * Outputs an HTML representation of the field's filter to the specified
     * stream.
     */
    public void writeFilterHtml(FieldFilter filter, java.io.PrintWriter out) throws java.io.IOException {
        // Nothing
    }

    /**
     * Process the HTTP request to extract the filter selections for this field.
     *
     * @return the FieldFilter containing the filter information for the field,
     * null if their was no filter information for the field in the request.
     */
    public FieldFilter processFilterHttpPost(javax.servlet.ServletRequest request) {
        return new FieldFilter();
    }

    /**
     * Can this field be used for sorting.
     *
     * @return true if the field can be used to sort form data.
     */
    public boolean isSortable() {
        return false;
    }

    /**
     * Outputs an HTML representation of this field to the specified stream.
     * This will typically include table divisions spanning up to 4 columns.
     */
    public void writeHtml(FieldData field_data, java.io.PrintWriter out) throws java.io.IOException {
        // Insert field label, flagging as an error if there is an error
        // for this field
        out.print("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + m_form.getFlagError(getDataColumnName(), HTMLUtils.escape(getFieldLabel())) + ":&nbsp;&nbsp;</td>\n");

        // Start table division for field tag
        out.print("<td align=\"left\" ");
        if (this.m_column_span > 1) {
            out.print("colspan=\"" + ((this.m_column_span * 2) - 1) + "\"");
        }
        out.print(">\n");
        String calculatedValue = formatCalculation(this.m_form.getData());

        if (calculatedValue != null) {

            out.print("<input type=\"text\" readonly maxlength=\"20\" size=\"10\" value=\"" + calculatedValue + "\">");
        } else {
            out.print("<input type=\"text\"  readonly maxlength=\"20\" size=\"10\" value=\"\" >");
        }


        // Print this field's value
        //out.print("&lt;NULL&gt;");
        //out.print("&lt;Some calculated field value&gt;");
        out.print("&nbsp;");

        // Now include any error message, if present
        out.print(m_form.getErrorMessage(getDataColumnName()));

        // Close the table division for field tag
        out.println("</td>");

    }

    public void writeHtmlReadOnly(FieldData field_data, java.io.PrintWriter out)
            throws java.io.IOException {
        writeHtml(field_data, out);
    }

    public String writeHtmlReadOnly(FieldData field_data){
    	StringBuffer fieldHtml = new StringBuffer();
    	fieldHtml.append("<td align=\"left\" width=\"1%\" class=\"tableHeader\">" + HTMLUtils.escape(m_field_label) + ": </td>");
        // Start table division for field tag
    	fieldHtml.append("<td align=\"left\" class=\"tableContent\" >");
        String calculatedValue = formatCalculation(this.m_form.getData());

        if (calculatedValue != null) {
        	fieldHtml.append(calculatedValue);
        } else {
        	fieldHtml.append("");
        }

        // Close the table division for field tag
        fieldHtml.append("</td>");
        return fieldHtml.toString();
    }
    
    
    /**
     * Loads this field's properties. The loaded properties are accessible by
     * {@link #getProperties}.
     */
    public void loadProperties() {

        try {
            // Get this field's property sheet; this is assumed to be
            // a CustomPropertySheet
            CustomPropertySheet propertySheet = (CustomPropertySheet) PropertySheetFactory.getPropertySheetForDisplayClass(getElementDisplayClassID());
            // not needed anymore CustomPropertySheet propertySheet = (CustomPropertySheet) PropertySheetFactory.getCalculationFieldPropertySheet();
            propertySheet.setID(getElementDisplayClassID());
            propertySheet.setManagedField(this);
            propertySheet.setForm(getForm());

            // Load properties
            propertySheet.load();

            // Add custom properties to form field properties
            this.m_properties.addAll(propertySheet.getFormFieldProperties());

        } catch (PersistenceException pe) {
        	Logger.getLogger(CalculationField.class).debug("CalculationField.loadProperties(), PersistenceException: " + pe);

        }

    }

    /**
     * Returns the formula for this calculation field.
     * @return the Formula loaded from the database
     */
    private Formula getFormula() {
        Formula formula = new Formula();
        String class_id = this.getForm().getID();
        String field_id = this.getID();
        formula.load(class_id, field_id);
        return formula;
    }

    /**
     * Calculates the result based on the form data.
     * FormData provides all the data for all fields in a form instance.
     *
     * @param formData the form data from which to find fields that are
     * defined in this field's formula
     * @return the calculated value
     * @throws FormulaException if the formula is empty
     * @throws DivideByZeroException if an attempt was made to divide by zero
     * @throws CalculationException if some other problem occurs calculating
     */
    Number getResult(FormData formData) throws CalculationException {
        Calculator calculator = new Calculator(getFormula(), getForm(), formData);
        return calculator.calculateResult();
    }

    //
    // Formatting methods
    //

    /**
     * Calculates and formats the calculation based on the form data.
     * FormData provides all the data for all fields in a form instance.
     *
     * @param formData the form data from which to find fields that are
     * defined in this field's formula
     * @return the calculated value, formatted or a default error message
     * if there is a problem calculating
     */
    private String formatCalculation(FormData formData) {

        String result = null;

        try {

            // First calculate the result based on the form data
            Number resultNumber = getResult(formData);

            // Now format the result
            result = formatResult(resultNumber);

        } catch (FormulaException e) {
            // There was a problem with the formula
            result = PropertyProvider.get("prm.form.calculated.result.error.checkformula.message");

        } catch (DivideByZeroException e) {
            // Attempt to divide by zero
            result = PropertyProvider.get("prm.form.calculated.result.error.cannotcalculate.message");

        } catch (CalculationException e) {
            // There was a problem formatting
            result = PropertyProvider.get("prm.form.calculated.result.error.checkvalues.message");
            //bfd 2739 and 3218 : EmptyStackException thrown in preview tab
        } catch (java.util.EmptyStackException e){
        	//There was operand's stack empty error 
        	result = PropertyProvider.get("prm.form.calculated.result.error.nooperands.message");
        }

		return result;
    }

    /**
     * Gets the field's value to display in the form list.
     * The value is calculated from the formula, based on the assumption
     * that there are even number of operands and
     * odd number of operators in the formula.
     * @param formData the form's data to which this field belongs to.
     */
    public String formatCalculationListView(FormData formData) {
        // Same as regular view
        return formatCalculation(formData);
    }

    /**
     * Formats the calculation result.
     *
     * @param result the calculation result to format
     * @return the formatted result
     * @throws CalculationException if there is a problem formatting the result
     */
    private String formatResult(Number result) throws CalculationException {

        String formattedResult = null;
        
        try {
	        // Constructs a map of property names to property values
	        // This is used to easily lookup properties
	        HashMap propertyMap = new HashMap();
	        for (Iterator it = getProperties().iterator(); it.hasNext(); ) {
	            FormFieldProperty nextProperty = (FormFieldProperty) it.next();
	            propertyMap.put(nextProperty.getName(), nextProperty.getValue());
	        }
	
	        // Get the user specified precision value
	        int precision = 5;
	        String precisionValue = (String) propertyMap.get(CalculationFieldPropertySheet.PRECISION_ID);
	        if (precisionValue != null && !precisionValue.equals("")) {
	            precision = Integer.parseInt(precisionValue);
	        }
	
	        // Create the precision pattern according to the user's  specification.
	        String pattern = "#.";
	        for (int i = 0; i < precision; i++) {
	            pattern = pattern + "#";
	        }
	
	        // 05/01/2003 - Tim
	        // Note: By constructing a pattern like #.### we are able to constrain
	        // the formatted result to the specified number of decimal places, but we
	        // also end up destroying the localized format.  For example, a pattern
	        // of #.### will not include the digit grouping symbol ("," for western)
	        // since we didn't specify it in the pattern.  Also, negative numbers
	        // will simply be formatted with a "-"
	        // Note that it will use the locale's decimal separator

        
            formattedResult = NumberFormat.getInstance().formatNumber(result.doubleValue(), pattern);
            if (formattedResult.endsWith(".0")) {
                formattedResult = formattedResult.substring(0, formattedResult.indexOf("."));
            }

        } catch (NumberFormatException nfe) {
            throw new CalculationException("Error formatting result: " + nfe, nfe);

        }

        return formattedResult;
    }

    //
    // Nested top-level classes
    //

    /**
     * Indicates some problem occurred calculating a result.
     */
    static class CalculationException extends PnetException {

        /**
         * Creates an CalculationException with the specified message.
         * @param message the message
         */
        CalculationException(String message) {
            super(message);
        }

        /**
         * Creates an CalculationException with the specified message
         * indicating the throwable that caused this exception.
         * @param message the message
         * @param cause the cause of the exception
         */
        CalculationException(String message, Throwable cause) {
            super(message, cause);
        }

    }

    /**
     * Indicates some problem occurred parsing the formula.
     */
    static class FormulaException extends CalculationException {

        /**
         * Creates an FormulaException with the specified message.
         * @param message the message
         */
        FormulaException(String message) {
            super(message);
        }

    }

    /**
     * Indicates an attempt to divide by zero.
     */
    static class DivideByZeroException extends CalculationException {

        /**
         * Creates an DivideByZeroException with the specified message.
         * @param message the message
         */
        DivideByZeroException(String message) {
            super(message);
        }

    }

}
