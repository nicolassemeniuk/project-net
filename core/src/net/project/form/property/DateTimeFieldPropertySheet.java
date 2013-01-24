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
import net.project.form.DateField;

/**
 * Property sheet for a date & time field.
 */
public class DateTimeFieldPropertySheet extends CustomPropertySheet {
    
  
    /**
     * Adds all CustomProperty objects for this sheet.
     */
    protected void initializeCustomProperties() {
        // Field Label
        TextProperty fieldLabelProp = new TextProperty(SpecialPropertyID.FIELD_LABEL.toString(), PropertyProvider.get("prm.form.designer.fields.create.type.datetime.fieldlabel.label"));
        fieldLabelProp.setValueRequired(true);
        addCustomProperty(fieldLabelProp, new PropertyLayout.Position(1));

        //Required field
        CheckBoxProperty requiredProperty = new CheckBoxProperty(SpecialPropertyID.IS_REQUIRED.toString(), PropertyProvider.get("prm.form.designer.fields.create.type.required.fieldlabel.label"));
        requiredProperty.setColspan(2);
        addCustomProperty(requiredProperty, new PropertyLayout.Position(1));

        //Hide in EAF
        if (getForm().getSupportsExternalAccess()){
        	CheckBoxProperty hideInEafProp = new CheckBoxProperty(SpecialPropertyID.HIDDEN_FOR_EAF.toString(), PropertyProvider.get("prm.global.form.elementproperty.hiddenforeaf.label"));
        	hideInEafProp.setValueRequired(false);
        	addCustomProperty(hideInEafProp, new PropertyLayout.Position(1));
        }        
        
        // Row number
        NumberProperty rowNumProp = new NumberProperty(SpecialPropertyID.ROW_NUM.toString(), PropertyProvider.get("prm.form.designer.fields.create.type.datetime.row.label"));
        rowNumProp.setDisplayWidth(5);
        rowNumProp.setMaxlength(3);
        addCustomProperty(rowNumProp, new PropertyLayout.Position(2));

        // Column
        addCustomProperty(new ColumnListProperty(PropertyProvider.get("prm.form.designer.fields.create.type.datetime.column.label")), new PropertyLayout.Position(2));

        // Radio Group: Display Mode
        RadioGroupProperty dateModeRadioProp = new RadioGroupProperty(DateField.DISPLAY_MODE_ID, PropertyProvider.get("prm.form.designer.fields.create.type.datetime.displaymode.label"));
        dateModeRadioProp.addRadioButton(DateField.MODE_DATE_ONLY, PropertyProvider.get("prm.form.designer.fields.create.type.datetime.dateonly.label"));
        dateModeRadioProp.addRadioButton(DateField.MODE_DATE_TIME, PropertyProvider.get("prm.form.designer.fields.create.type.datetime.dateandtime.label"));
        dateModeRadioProp.addRadioButton(DateField.MODE_TIME_ONLY, PropertyProvider.get("prm.form.designer.fields.create.type.datetime.timeonly.label"));
        addCustomProperty(dateModeRadioProp, new PropertyLayout.Position(3));

        // Checkbox:  Default to creation date
        CheckBoxProperty defaultToCreationDate = new CheckBoxProperty(DateField.DEFAULT_DATE_ID, PropertyProvider.get("prm.form.designer.fields.create.type.datetime.default.label"));
        defaultToCreationDate.setColspan(2);
        addCustomProperty(defaultToCreationDate, new PropertyLayout.Position(3));
    }

    public int getDesignerFieldCount() {
        return getPropertyRows().size();
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
        CustomPropertyCollection rowProperties;
        ICustomProperty property;
        StringBuffer s = new StringBuffer();
       
        
        // Start of property table
        s.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");

        Iterator rowIterator = super.getPropertyRows().iterator();
        while (rowIterator.hasNext()) {
            rowProperties = (CustomPropertyCollection) rowIterator.next();

            s.append("<tr>");
            
            // Write all CustomProperties
            Iterator it = rowProperties.iterator();
            
            if (!it.hasNext()) {
                // No properties in this row
                s.append("<td>&nbsp;</td>");
            
            } else {
                // Add all properties in this row
                while (it.hasNext()) {
                    property = (ICustomProperty) it.next();

                    s.append(property.getPresentationHTML());
                }
            }
        
            s.append("</tr>");

        }
        
        // End of property table
        s.append("</table>");

        return s.toString();
    }



   
}
