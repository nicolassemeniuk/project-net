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

import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.form.CurrencyField;
import net.project.form.FormException;
import net.project.form.FormField;
import net.project.form.Formula;
import net.project.form.IFormulaElement;
import net.project.form.NumberField;
import net.project.form.Operand;
import net.project.form.Operator;
import net.project.gui.toolbar.Band;
import net.project.gui.toolbar.Button;
import net.project.gui.toolbar.Toolbar;
import net.project.gui.toolbar.ToolbarException;
import net.project.persistence.PersistenceException;

/**
 * Property sheet for Calculation field.
 * Includes selectable fields and mathematic operator selection.
 * @author Vishwajeet
 */
public class CalculationFieldPropertySheet extends CustomPropertySheet {

    //
    // Property IDs for custom properties
    //

    public static final String OPERATOR_ID_PREFIX = "101";
    public static final String PRECISION_ID = "99";

    /**
     * The total number of mathematical operators in this property sheet.
     */
    private int elementCount = 0;

    /**
     * The formula created by the operators and operands on this property sheet.
     */
    private final Formula formula = new Formula();

    /**
     * Records the row number that the formula section starts at so that
     * we can change the presentation layout somewhat.
     */
    private int formulaRowStartPosition = 0;

    /**
     * Adds all CustomProperty objects for this sheet.
     */
    protected void initializeCustomProperties() {

        emptyCustomProperties();
        formulaRowStartPosition = 0;
        int rowIndex = 0;

        // Field Label
        TextProperty fieldLabelProp = new TextProperty(SpecialPropertyID.FIELD_LABEL.toString(), PropertyProvider.get("prm.form.designer.fields.create.type.calculated.fieldlabel.label"));
        fieldLabelProp.setValueRequired(true);
        fieldLabelProp.setMaxlength(80);
        addCustomProperty(fieldLabelProp, new PropertyLayout.Position(++rowIndex));

        //Hide in EAF
        if (getForm().getSupportsExternalAccess()){
        	CheckBoxProperty hideInEafProp = new CheckBoxProperty(SpecialPropertyID.HIDDEN_FOR_EAF.toString(), PropertyProvider.get("prm.global.form.elementproperty.hiddenforeaf.label"));
        	hideInEafProp.setValueRequired(false);
        	addCustomProperty(hideInEafProp, new PropertyLayout.Position(rowIndex));
        }
        
        // Row number
        NumberProperty rowNumProp = new NumberProperty(SpecialPropertyID.ROW_NUM.toString(), PropertyProvider.get("prm.form.designer.fields.create.type.calculated.row.label"));
        rowNumProp.setDisplayWidth(5);
        rowNumProp.setMaxlength(3);
        rowNumProp.setNumValidationRequired(true);
        addCustomProperty(rowNumProp, new PropertyLayout.Position(++rowIndex));
    
        // Column
        addCustomProperty(new ColumnListProperty(PropertyProvider.get("prm.form.designer.fields.create.type.calculated.column.label")), new PropertyLayout.Position(rowIndex));

        // Precision number
        NumberProperty precisionNumProp = new NumberProperty(PRECISION_ID, PropertyProvider.get("prm.form.designer.fields.create.type.calculated.precision.label"));
        precisionNumProp.setDisplayWidth(2);
        //bfd-4247
        precisionNumProp.setMaxlength(80);
        precisionNumProp.setNumValidationRequired(true);
        addCustomProperty(precisionNumProp, new PropertyLayout.Position(++rowIndex));

        // Blank line
        addCustomProperty(new EmptyProperty(), new PropertyLayout.Position(++rowIndex));

        // Add an action bar containing an Add and Remove button
        String toolbarHTML = null;
        try {
            Button button;
            Toolbar actionBar = new Toolbar();
            actionBar.setStyle("action");
            Band band = actionBar.addBand("action");
            band.setShowLabels(true);

            button = band.addButton("add");
            button.setLabelToken("prm.form.designer.fields.create.type.calculated.add.button.label");
            button.setAltToken("prm.form.designer.fields.create.type.calculated.add.alttext");
            button.setFunction("javascript:addopPair();");
            button.setUserOrder(0);

            button = band.addButton("delete");
            button.setLabelToken("prm.form.designer.fields.create.type.calculated.remove.button.label");
            button.setAltToken("prm.form.designer.fields.create.type.calculated.remove.alttext");
            button.setFunction("javascript:removeopPair();");
            button.setUserOrder(1);

            toolbarHTML = "<td colspan=\"5\" >" +  actionBar.getPresentation() + "</td>\n";

        } catch (ToolbarException e) {
            // No toolbar

        }
        addCustomProperty(new HTMLProperty(toolbarHTML), new PropertyLayout.Position(++rowIndex));
        formulaRowStartPosition = rowIndex;

        // Blank line
        addCustomProperty(new EmptyProperty(), new PropertyLayout.Position(++rowIndex));

        // Formula instructions
        addCustomProperty(new InstructionProperty("prm.form.designer.fields.create.type.calculated.instruction.text"), new PropertyLayout.Position(++rowIndex));

        // Blank line
        addCustomProperty(new EmptyProperty(), new PropertyLayout.Position(++rowIndex));

        initializeDynamicCustomProperties();
    }

    /**
     * Adds operands and corresponding operators as per the predefined formula.
     * If this is field is being newly defined then adds the default operands pair
     * and an operator.
     */
    private void initializeDynamicCustomProperties() {

        elementCount = 0;
        this.formula.clear();
        formula.load(this.getForm().getID(), this.getManagedField().getID());
        int nextRow = getPropertyRows().size() + 1;

        if (formula.getLength() != 0) {
            // We have formula with at least one element
            // Add each formula element to this property sheet

            for (Iterator it = formula.iterator(); it.hasNext(); ) {
                IFormulaElement nextElement = (IFormulaElement) it.next();

                if (nextElement.getElementType().equals(Formula.OPERAND)) {
                    addOperand(nextRow++);

                } else if (nextElement.getElementType().equals(Formula.OPERATOR)) {
                    addOperator(nextRow++);
                }

            }

        } else {
            // We have no formula yet
            // Add the default elements to this property sheet

            // Numeric field
            String fieldID = Integer.toString(100 + this.elementCount);
            addCustomProperty(new FieldListProperty(fieldID, PropertyProvider.get("prm.form.designer.fields.create.type.calculated.field1.label"), getForm(), new NumericFieldChecker()), new PropertyLayout.Position(nextRow++));
            elementCount++;

            // Mathematical operator
            fieldID = Integer.toString(100 + this.elementCount);
            addCustomProperty(new OperatorProperty(fieldID, PropertyProvider.get("prm.form.designer.fields.create.type.calculated.operator1.label")), new PropertyLayout.Position(nextRow++));
            elementCount++;

            // Numeric field
            fieldID = Integer.toString(100 + this.elementCount);
            addCustomProperty(new FieldListProperty(fieldID, PropertyProvider.get("prm.form.designer.fields.create.type.calculated.field2.label"), getForm(), new NumericFieldChecker()), new PropertyLayout.Position(nextRow++));
            elementCount++;

        }
    }

    /**
     * Adds an operator on the specified row.
     * @param nextRow the row on which to place the operator
     */
    private void addOperator(int nextRow) {
        String fieldID = Integer.toString((100 + elementCount));
        String fieldName = PropertyProvider.get("prm.form.designer.fields.create.type.calculated.operator.label") + " " + nextOperatorNumber(elementCount);
        addCustomProperty(new OperatorProperty(fieldID, fieldName), new PropertyLayout.Position(nextRow));
        elementCount++;
    }

    /**
     * Adds an operand on the specified row.
     * @param nextRow the row on which to place the operand
     */
    private void addOperand(int nextRow) {
        String fieldID = Integer.toString((100 + elementCount));
        String fieldName = PropertyProvider.get("prm.form.designer.fields.create.type.calculated.field.label") + " " + nextOperandNumber(elementCount);
        addCustomProperty(new FieldListProperty(fieldID, fieldName, getForm(), new NumericFieldChecker()), new PropertyLayout.Position(nextRow));
        elementCount++;
    }

    /**
     * Returns the next operand number based on the specified element count.
     * This is for assigning a presentable sequential number to each operand,
     * starting at "1" in the case of no elements.
     * <b>Note:</b> This only really works if the currentElementCount is zero
     * or an even number; so it this method must be called in the correct sequence.
     *
     * @param currentElementCount the current number of elements
     * @return the next operand number
     */
    private int nextOperandNumber(int currentElementCount) {
        return (((currentElementCount + 1) / 2) + 1);
    }

    /**
     * Returns the next operator number based on the specified element count.
     * This is for assigning a presentable sequential number to each operator,
     * starting at "1" in the case of one element.
     * <b>Note:</b> This only really works if the currentElementCount is an
     * odd number; so it this method must be called in the correct sequence.
     *
     * @param currentElementCount the current number of elements
     * @return the next operand number
     */
    private int nextOperatorNumber(int currentElementCount) {
        return ((currentElementCount + 1) / 2);
    }

    /**
     * Adds an operator-operand pair to the propertysheet.
     */
    public void addOpPair() {
        int nextRow = getPropertyRows().size() + 1;

        addOperator(nextRow++);
        addOperand(nextRow++);
    }

    /**
     * Removes a operand-operator pair from the formula based on its orderID.
     * Stores then reloads the formula.
     */
    public void removeOpPair(String orderIDString) throws PersistenceException {

        // Load the formula if not loaded
        if (this.formula.getLength() == 0) {
            formula.load(this.getForm().getID(), this.getManagedField().getID());
        }

        int orderID = Integer.parseInt(orderIDString);

        this.formula.removeElementAt(orderID); // remove the operator
        this.formula.removeElementAt(orderID);	// remove the corresponding field (stack size has reduced by 1 earlier)

        this.formula.store(this.getForm().getID(), this.getManagedField().getID());
        this.formula.clear();
        this.formula.load(this.getForm().getID(), this.getManagedField().getID());

    }

    /**
     * Adds a single operand to the property sheet.
     */
    public void addOperand() {
        int nextRow = getPropertyRows().size() + 1;
        addOperand(nextRow++);
    }

    /**
     * Adds a single operator to the property sheet.
     */
    public void addOperator() {
        int nextRow = getPropertyRows().size() + 1;
        addOperator(nextRow++);
    }

    public int getDesignerFieldCount() {
        //This number is a bit of a guess.
        return 2*getPropertyRows().size();
    }

    /**
     * Writes the property sheet as HTML.
     * This should return the property sheet that is suitable for insertion
     * in a table division.
     * @param out the PrintWriter to write to
     * @throws java.io.IOException if there is a problem writing
     */
    public void writeHtml(java.io.PrintWriter out) throws java.io.IOException {
        out.println(getPresentationHTML());
    }

    /**
     * Returns this property sheet presentation as HTML.
     * This is an HTML table including the properties as HTML form fields
     * populated with their current values.
     * @return HTML presentation of property sheet
     */
    private String getPresentationHTML() {
        StringBuffer s = new StringBuffer();

        // Start of property table
        s.append("<!-- Begin rendered form -->\n");
        s.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");

        int rowCount = 0;
        int formulaElementCount = 0;
        boolean addRadioButton = false;

        for (Iterator it = getPropertyRows().iterator(); it.hasNext(); rowCount++) {
            CustomPropertyCollection rowProperties = (CustomPropertyCollection) it.next();

            // If we have started on the formula section, begin a new table
            // It requires a different layout
            if (rowCount == formulaRowStartPosition) {
               // s.append("\n</table>\n");
                s.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");
            }

            //
            // Start the row
            //

            s.append("<tr>\n");

            // If we are drawing the formula section, change the layout slightly
            if (rowCount >= formulaRowStartPosition) {

                if (rowCount == formulaRowStartPosition) {
                    // Start the toolbar
                    s.append("<td class=\"tableContent\" colspan=\"3\">");

                } else {

                    if (addRadioButton && formulaElementCount >= 3) {
                        // Make op-field pairs after the second field removable
                        // The second field's element number will be 2 since numbering
                        // starts at zero
                        s.append("<td class=\"tableContent\">");
                        s.append("<input type=\"radio\" name=\"selected\" value=" + formulaElementCount + ">");
                        s.append("</td>");

                    } else {
                        // Pad with an empty column
                        s.append("<td>&nbsp;</td>");
                    }

                }


            }

            //
            // Now draw a complete row
            //

            // Write all CustomProperties
            boolean hasProperty = false;
            for (Iterator rowPropIt = rowProperties.iterator(); rowPropIt.hasNext(); ) {
                ICustomProperty property = (ICustomProperty) rowPropIt.next();
                s.append(property.getPresentationHTML());

                if (property instanceof OperatorProperty) {
                    formulaElementCount++;
                    addRadioButton = false;

                } else if (property instanceof FieldListProperty) {
                    formulaElementCount++;
                    addRadioButton = true;
                }
            }

            //
            // Done drawing a complete row
            //

            if (rowCount == formulaRowStartPosition) {
                // End the toolbar
            	s.append("\n<!-- End the toolbar -->\n");
                s.append("</td>");
            }

            if (!hasProperty) {
                s.append("<td colspan=\"2\">&nbsp;</td>");
            }

            s.append("</tr>\n");

        }


        // End of property table
        s.append("</table>\n");
        s.append("<!-- End rendered form -->\n");
        return s.toString();
    }

    /**
     * Processes request parameters, including special parameters and
     * property parameters.
     * The special parameters cause values in the managed field to be
     * changed.
     * @param request the request containing the parameters
     * @see IPropertySheet#writeHtml for details of the special parameters
     */
    public void processHttpPost(javax.servlet.ServletRequest request)
            throws FormException {
        // Clear the old formula as new should completely replace it.
        this.formula.clear();
        super.processHttpPost(request);
    }


    /**
     * Processes the request parameter to see if it is a special parameter.
     * First processes the parameter in the superclass.
     * This class builds a formula from the parameters in the request.
     *
     * @param name the request parameter name
     * @param values the request parameter's values
     * @param field the managed field
     */
    protected void processParameter(String name, String[] values, FormField field)
            throws FormException {

        super.processParameter(name, values, field);

        // Get the Property value
        PersistentCustomProperty prop = getPropertyForHTMLName(name);
        String value = values[0];

        // Capture the formula
        if (prop instanceof FieldListProperty) {
            formula.push(new Operand(value, Integer.valueOf(name)-100));

        } else if (prop instanceof OperatorProperty) {

            // Given the selected operator, add a new operator
            if (value.equals(OperatorProperty.DOMAIN_ID_ADD)) {
                formula.push(new Operator(Operator.PLUS, Integer.valueOf(name)-100));

            } else if (value.equals(OperatorProperty.DOMAIN_ID_SUB)) {
                formula.push(new Operator(Operator.MINUS, Integer.valueOf(name)-100));

            } else if (value.equals(OperatorProperty.DOMAIN_ID_MUL)) {
                formula.push(new Operator(Operator.MULTIPLY, Integer.valueOf(name)-100));

            } else if (value.equals(OperatorProperty.DOMAIN_ID_DIV)) {
                formula.push(new Operator(Operator.DIVIDE, Integer.valueOf(name)-100));
            }
        }
    }

    /**
     * Store the property sheet data.
     * Stores the managed field and the formula.
     */
    public void store() throws PersistenceException {
        this.getManagedField().store();
        if (formula != null) {
            formula.store(this.getForm().getID(), this.getManagedField().getID());
        }
    }

    /**
     * OperatorProperty represents second field property.
     */
    private static class OperatorProperty extends DomainListProperty {

        private static final String DOMAIN_ID_ADD = CalculationFieldPropertySheet.OPERATOR_ID_PREFIX + "_1";
        private static final String DOMAIN_ID_SUB = CalculationFieldPropertySheet.OPERATOR_ID_PREFIX + "_2";
        private static final String DOMAIN_ID_MUL = CalculationFieldPropertySheet.OPERATOR_ID_PREFIX + "_3";
        private static final String DOMAIN_ID_DIV = CalculationFieldPropertySheet.OPERATOR_ID_PREFIX + "_4";

        private OperatorProperty(String id, String displayName) {
            super(id, displayName);
            addDomainValue(DOMAIN_ID_ADD, "+");
            addDomainValue(DOMAIN_ID_SUB, "-");
            addDomainValue(DOMAIN_ID_MUL, "*");
            addDomainValue(DOMAIN_ID_DIV, "/");
        }

    }

    /**
     * A FieldChecker which ensures only number-type fields are added to
     * the field lists.
     * This provides a call-back mechanism used by {@link FieldListProperty}
     * to allow property sheets to decide what kinds of form fields
     * are added to a FieldListProperty
     */
    private static class NumericFieldChecker implements FieldListProperty.IFieldChecker {

        /**
         * Indicates whether the form field should be added.
         * Only number of currency form fields are added to this property sheet
         * @param formField the form field
         * @return true if the form field should be added; false otherwise
         */
        public boolean doAdd(FormField formField) {

            if (formField instanceof NumberField) {
                // NumberFields can be used in a calculation
                return true;

            } else if (formField instanceof CurrencyField) {
                // Currency fields can be used in a calculation
                // This is a little redundant since CurrencyFields are NumberFields
                return true;

            } else {
                return false;

            }
        }
    }


}
