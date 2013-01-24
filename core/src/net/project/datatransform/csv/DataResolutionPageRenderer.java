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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.datatransform.csv;

import java.io.Serializable;
import java.util.Iterator;

import net.project.base.attribute.AttributeCollection;
import net.project.base.attribute.BooleanAttribute;
import net.project.base.attribute.DateAttribute;
import net.project.base.attribute.DomainListAttribute;
import net.project.base.attribute.DomainValue;
import net.project.base.attribute.IAttribute;
import net.project.base.attribute.NumberAttribute;
import net.project.base.attribute.PersonListAttribute;
import net.project.base.attribute.TextAttribute;
import net.project.base.property.PropertyProvider;
import net.project.datatransform.csv.map.ColumnMap;
import net.project.datatransform.csv.map.ColumnMaps;
import net.project.datatransform.csv.transformer.IDataTransformer;
import net.project.security.SessionManager;

/**
 * This class renders the DataResolution JSP Page.  This page contains a list of
 * fields in the CSV file that require additional information in order to have
 * its data transferred to the form.
 *
 * @author Deepak
 * @author Matthew Flower
 * @since emu
 */
public class DataResolutionPageRenderer implements Serializable {

    /**
     * This method renders the DataResolution JSP Page, which contains a list of
     * fields in the CSV file that require additional information in order to be
     * transferred into a Form.
     *
     * @return String a <code>String</code> value containing the HTML required
     * to render the html which will capture additional data.
     * @param attributes an <code>AttributeCollection</code> object which
     * contains the attributes (form fields.)
     * @param csv a <code>CSV</code> object which originally parsed the CSV file.
     * @param colMaps a <code>ColumnMaps</code> object which maps the Columns in
     * the CSV to the Form fields.
     */
    public String render(AttributeCollection attributes, CSV csv, ColumnMaps colMaps) {
        CSVColumn column = null;
        IAttribute iattr = null;
        StringBuffer renderString = new StringBuffer();
        Iterator itrColumnMaps = colMaps.iterator();

        while (itrColumnMaps.hasNext()) {
            ColumnMap columnMap = (ColumnMap)itrColumnMaps.next();
            Iterator itrDataTransformer = columnMap.getDataTransformerList().iterator();
            column = columnMap.getCSVColumn();

            while (itrDataTransformer.hasNext()) {
                IDataTransformer iDataTransformer = (IDataTransformer)itrDataTransformer.next();
                iattr = iDataTransformer.getAttribute();

                if (iattr instanceof PersonListAttribute || iattr instanceof DomainListAttribute ||
                    iattr instanceof BooleanAttribute || iattr instanceof DateAttribute) {
                    processDomainAttributes(renderString, columnMap.getID(),
                        iDataTransformer.getID(), iattr, column);
                } else if (iattr instanceof NumberAttribute) {
                    renderString.append("<INPUT TYPE=\"hidden\" NAME=\"" + "ColumnMap_" + columnMap.getID() + "_transformerID_" + iDataTransformer.getID() + "\">");
                } else if (iattr instanceof TextAttribute) {
                    renderString.append("<INPUT TYPE=\"hidden\" NAME=\"" + "ColumnMap_" + columnMap.getID() + "_transformerID_" + iDataTransformer.getID() + "\">");
                }
            }
        } // end while
        return renderString.toString();
    }

    /**
     * For each attribute/CSVColumn pair, render HTML that will allow the user
     * to enter information needed to complete the mapping from the CSV values
     * to the form values.  As an example, if there is a date value being
     * mapped, the mapper needs to know the format of the date field.
     *
     * @param columnMapID a <code>String</code> value which contains the id of
     * the column mapper.  This is used to construction the HTML option names
     * so we can properly parse them later in the process.
     * @param dataTransformerID a <code>String</code> value containing the id of
     * the data transformer.  This is also used to construct the HTML option
     * names.
     * @param iattr a <code>IAttribute</code> value which is the attribute that
     * we are rendering.
     * @param column a <code>CSVColumn</code> value which is the column in the
     * CSV file that we are rendering a mapping for.
     */
    private void processDomainAttributes(StringBuffer renderString, String columnMapID,
        String dataTransformerID, IAttribute iattr, CSVColumn column) {

        //Draw a channel handler which tells the user which column mapping they
        //are providing information for.
        renderString.append(getChannelHeader(column, iattr.getDisplayName()));

        //Create an iterator object to get all of the column values.
        Iterator it = column.getDistinctCSVColumnValues().iterator();

        //Implement column headers if there are really going to be any column
        //mappings
        if (it.hasNext() && !(iattr instanceof DateAttribute)) {
            renderString.append("<tr>");
            renderString.append("<td></td>");
            renderString.append("<td class=\"tableHeader\">"+PropertyProvider.get("prm.form.csvimport.dataresolution.csvfieldvalue.column")+"</td>");
            renderString.append("<td class=\"tableHeader\">"+PropertyProvider.get("prm.form.csvimport.dataresolution.csvfieldvalue.formfieldvalue.column")+"</td>");
            renderString.append("<td></td>");
            renderString.append("</tr>");
            renderString.append("<tr>");
            renderString.append("<td colspan=\"4\" class=\"tableLine\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/spacers/trans.gif\" height=\"2\" width=\"1\" border=\"0\"></td>");
            renderString.append("</tr>");
        }

        while (it.hasNext() && !(iattr instanceof DateAttribute)) {
            CSVDataValue csvDataValue = (CSVDataValue)it.next();

            //Draw the row for the column mappings.
            renderString.append("<tr>").append("\n");
            renderString.append("<td></td>");
            renderString.append("<td width=\"20%\" class=\"tableContent\" align=\"left\">").append("\n");
            renderString.append(csvDataValue.getValue()).append("\n");
            renderString.append("</td>").append("\n");
            renderString.append("<td align=\"left\">").append("\n");

            if (iattr instanceof PersonListAttribute) {
                PersonListAttribute patr = (PersonListAttribute)iattr;
                renderString.append("<select name=\"ColumnMap_").append(columnMapID)
                    .append("_transformerID_").append(dataTransformerID)
                    .append("\" SIZE=\"1\">").append("\n");

                for (Iterator itrp = patr.getDomainValues().iterator(); itrp.hasNext(); ) {
                    DomainValue domainValue = (DomainValue)itrp.next();
                    renderString.append("<option value=\"").append("datavalue_")
                        .append(csvDataValue.getID()).append("_domainvalue_")
                        .append(domainValue.getID()).append("\">")
                        .append(domainValue.getDisplayName()).append("</option>")
                        .append("\n");
                }

                renderString.append("</select>").append("\n");
                renderString.append("</td>").append("\n");
                renderString.append("<td></td>");
                renderString.append("</tr>").append("\n");
            } else if (iattr instanceof DomainListAttribute) {
                DomainListAttribute datr = (DomainListAttribute)iattr;
                renderString.append("<select name=\"" + "ColumnMap_" + columnMapID + "_transformerID_" + dataTransformerID + "\" SIZE=\"1\">").append("\n");

                for (Iterator itrs = datr.getDomainValues().iterator(); itrs.hasNext(); ) {
                    DomainValue domainValue = (DomainValue)itrs.next();
                    renderString.append("<option value=\"" + "datavalue_" + csvDataValue.getID() + "_domainvalue_" + domainValue.getID() + "\">" + domainValue.getDisplayName()).append("</option>").append("\n");
                }

                renderString.append("</select>").append("\n");
                renderString.append("</td>").append("\n");
                renderString.append("<td></td>");
                renderString.append("</tr>").append("\n");
            } else if (iattr instanceof BooleanAttribute) {
                BooleanAttribute batr = (BooleanAttribute)iattr;
                renderString.append("<select name=\"" + "ColumnMap_" + columnMapID + "_transformerID_" + dataTransformerID + "\" SIZE=\"1\">").append("\n");

                for (Iterator itrs = batr.getDomainValues().iterator(); itrs.hasNext(); ) {
                    DomainValue domainValue = (DomainValue)itrs.next();
                    renderString.append("<option value=\"" + "datavalue_" + csvDataValue.getID() + "_domainvalue_" + domainValue.getID() + "\">" + domainValue.getDisplayName()).append("</option>").append("\n");
                }

                renderString.append("</select>").append("\n");
                renderString.append("</td>").append("\n");
                renderString.append("<td></td>");
                renderString.append("</tr>").append("\n");
            }
        }

        if (iattr instanceof DateAttribute) {
            renderString.append("<tr>").append("\n");
            renderString.append("<td></td>");
            renderString.append("<td align=\"left\"  colspan=\"1\" class=\"tableContent\">").append("\n");
            renderString.append(PropertyProvider.get("prm.form.csvimport.enterdateformat.fieldlabel") + "&nbsp;&nbsp;&nbsp;").append("\n");
            renderString.append("</td>").append("\n");
            renderString.append("<td width=\"20%\" align=\"left\" NOWRAP class=\"tableContent\" >").append("\n");
            renderString.append("<INPUT TYPE=\"Text\" onblur=\"handleDateFormat(this);\" NAME=\"" + "ColumnMap_" + columnMapID + "_transformerID_" + dataTransformerID + "\">").append("\n");
            renderString.append("&nbsp;&nbsp;&nbsp;" + PropertyProvider.get("prm.form.csvimport.enterdateformat.format.example")).append("\n");
            renderString.append("</td>").append("\n");
            renderString.append("</tr>").append("\n");
            renderString.append("<tr><td>&nbsp;</td></tr>").append("\n");
            renderString.append("</tr>").append("\n");
        }

        renderString.append("<tr><td>&nbsp;</td></tr>").append("\n");
    }

    /**
     * Draw a header for a given field in the CSV which serves as a separator
     * for the different field values that the user needs to map.
     *
     * @param column a <code>CSVColumn</code> value that the user needs to map
     * values for.
     * @param attributeName a <code>String</code> value which gives the name of
     * form field that is being mapped to.
     * @return a <code>String</code> which contains the HTML needed to render
     * this column header.
     */
    private String getChannelHeader(CSVColumn column, String attributeName) {
        StringBuffer sbuffer = new StringBuffer();

        sbuffer.append("<tr class=\"channelHeader\"><td width=\"1%\">");
        sbuffer.append("<IMG height=15 alt=\"\" src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-left_end.gif\" width=8 border=0></td>");
        sbuffer.append("<td align=\"left\" nowrap class=\"channelHeader\" colspan=\"2\">");
        sbuffer.append(PropertyProvider.get("prm.form.csvimport.enterdateformat.additionalinforequired.message", new Object[]{column.getColumnName(), column.getColumnID(), attributeName}));
        sbuffer.append("</td>");
        sbuffer.append("<td align=right width=\"1%\">");
        sbuffer.append("<IMG height=15 alt=\"\" src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-right_end.gif\" width=8 border=0>");
        sbuffer.append("</td>");
        sbuffer.append("</tr>");

        return sbuffer.toString();
    }
}
