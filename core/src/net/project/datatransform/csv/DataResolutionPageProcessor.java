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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.attribute.AttributeCollection;
import net.project.base.attribute.BooleanAttribute;
import net.project.base.attribute.DomainListAttribute;
import net.project.base.attribute.DomainValue;
import net.project.base.attribute.IAttribute;
import net.project.base.attribute.PersonListAttribute;
import net.project.datatransform.csv.map.ColumnMap;
import net.project.datatransform.csv.map.ColumnMaps;
import net.project.datatransform.csv.transformer.DateDataTransformer;
import net.project.datatransform.csv.transformer.DomainListDataTransformer;
import net.project.datatransform.csv.transformer.IDataTransformer;
import net.project.datatransform.csv.transformer.NumberDataTransformer;
import net.project.datatransform.csv.transformer.TextDataTransformer;
import net.project.security.Action;

/**
 * A DataReolutionPageProcessor class processes the parameters passed to the
 * DataResolution Page
 * @author Deepak
 * @since emu
 */
public class DataResolutionPageProcessor implements Serializable {
    /** Constant for Column Map */
    private static final String COLUMNMAP = "ColumnMap";
    /** Constant for DataValue */
    private static final String DATAVALUE = "datavalue";
    /** Constant for DomainValue */
    private static final String DOMAINVALUE = "domainvalue";
    /** Constant for TransformerID */
    private static final String TRANSFORMERID = "transformerID";
    /** Constant for Module */
    private final String MODULE = "module";
    /** Constant for theAction */
    private final String THE_ACTION = "theAction";
    /** String for constructing the querystring for the next page */
    private String queryString = "/datatransform/csv/ValidationResults.jsp?module="+
        Module.FORM+"&action="+Action.MODIFY;

    /**
     * Processes the request & constructs DataResolution page
     *
     * @param request    <CODE>HttpServletRequest</CODE> sent to the Page
     * @param csv        <CODE>CSV</CODE> object in the Session
     *
     * @param attributes <CODE>AttributeCollection</CODE> holds the collection of Attributes
     *                   for the object which implements the <CODE> IAttributeReadable </CODE> interface
     *
     * @param columnMaps <CODE>ColumnMaps</CODE> holds the collection of Column Map
     * @return The constructed queryString for the next page
     */
    public String process(HttpServletRequest request, CSV csv, AttributeCollection attributes, ColumnMaps columnMaps) {
        Enumeration enumeration = request.getParameterNames();
        validateRows(csv);

        while (enumeration.hasMoreElements()) {
            String name = (String)enumeration.nextElement();
            String[] values = request.getParameterValues(name);

            if ((name.indexOf(MODULE)>-1) ||
                (name.indexOf("action")>-1) ||
                (name.indexOf(THE_ACTION)>-1)) {
                continue;
            }

            for (int i = 0; i < values.length; i++) {
                processDataTransformer(name, values[i], columnMaps);
            }
        }
        return queryString;
    }

    /**
     * Processes the DataTransformer which means that it sets the Date format , etc ,
     * if any
     *
     * @param colStr The ID of the <CODE>CSVColumn</CODE> object which is being
     * passed as <CODE>String</CODE> format
     * @param attrStr The value of the Attribute which is being passed as
     * <CODE>String</CODE> object
     * @param colMaps <CODE>ColumnMaps</CODE> holds the collection of Column Map
     */
    private void processDataTransformer(String colStr, String attrStr, ColumnMaps colMaps) {
        if (stringValidator(colStr) || stringValidator(attrStr)) {
            ColumnMap columnMap = null;
            CSVDataValue csvDataValue = null;
            DomainValue idomainValue = null;
            IDataTransformer iDataTransformer = null;
            CSVColumn csvColumn = null;
            IAttribute iAttribute = null;

            StringTokenizer colStrk = new StringTokenizer(colStr, "_");
            StringTokenizer attrStrk = new StringTokenizer(attrStr, "_");

            while (colStrk.hasMoreTokens()) {

                if (!stringValidator(colStr)) {
                    return;
                }

                String col = colStrk.nextToken(); // Check for presnce of next token

                if (col.equalsIgnoreCase(COLUMNMAP)) {
                    String colID = colStrk.nextToken();
                    columnMap = colMaps.getColumnMapByID(colID);
                }

                String dataValue = colStrk.nextToken();  // Check for presnce of next token

                if (dataValue.equalsIgnoreCase(TRANSFORMERID)) {
                    String transformerID = colStrk.nextToken();
                    iDataTransformer = columnMap.getDataTransformerByID(transformerID);
                }

            } // end while

            if (iDataTransformer instanceof DateDataTransformer) {
                DateDataTransformer dateDataTransformer = (DateDataTransformer)iDataTransformer;
                dateDataTransformer.setDateFormat(attrStr);
                return;
            } else if (iDataTransformer instanceof NumberDataTransformer) {
                return;
            } else if (iDataTransformer instanceof TextDataTransformer) {
                return;
            }

            iAttribute = iDataTransformer.getAttributeValue().getAttribute();

            if (stringValidator(attrStr)) {

                csvColumn = columnMap.getCSVColumn();
                while (attrStrk.hasMoreTokens()) {
                    String attr = attrStrk.nextToken();

                    if (attr.equalsIgnoreCase(DATAVALUE)) {
                        String dataValueID = attrStrk.nextToken();
                        csvDataValue = csvColumn.getCSVDataValue(csvColumn.getCSVColumnNumber(), dataValueID);
                    }
                    String domainValue = attrStrk.nextToken(); // Check for presnce of next token

                    if (domainValue.equalsIgnoreCase(DOMAINVALUE)) {

                        String domainValueID = attrStrk.nextToken();

                        if (iAttribute instanceof PersonListAttribute) {
                            PersonListAttribute patr = (PersonListAttribute)iAttribute;
                            idomainValue = patr.getDomainValues().getDomainValueByID(domainValueID);
                        } else if (iAttribute instanceof DomainListAttribute) {
                            DomainListAttribute datr = (DomainListAttribute)iAttribute;
                            idomainValue = datr.getDomainValues().getDomainValueByID(domainValueID);
                        } else if (iAttribute instanceof BooleanAttribute) {
                            BooleanAttribute batr = (BooleanAttribute)iAttribute;
                            idomainValue = batr.getDomainValues().getDomainValueByID(domainValueID);
                        }

                    }

                }

                if (iDataTransformer instanceof DomainListDataTransformer) {
                    DomainListDataTransformer domainDataTransformer = (DomainListDataTransformer)iDataTransformer;
                    domainDataTransformer.addMapValues(csvDataValue, idomainValue);
                    return;
                }
            }
        }
        return;
    }

    /**
     * Checks to see if the parameter name being tested contains either column map
     * and transformer id or the domain value and the data value.
     *
     * @param str a <code>String</code> value containing the name of a parameter
     * passed through the request object to this class.
     * @return a <code>boolean</code> value containing true if this parameter
     * name contains either (columnMap and transformerID) or
     * (datavalue and domainvalue).
     */
    private boolean stringValidator(String str) {
        int checkColumnMap = str.indexOf(COLUMNMAP);
        int checkDomainValue = str.indexOf(DOMAINVALUE);
        int checkTransformerID = str.indexOf(TRANSFORMERID);
        int checkDataValue = str.indexOf(DATAVALUE);

        if (checkColumnMap >= 0 && checkTransformerID >= 0) {
            return true;
        } else if (checkDomainValue >= 0 && checkDataValue >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param csv    <CODE>CSV</CODE> object in the Session
     */
    private void validateRows(CSV csv) {
        Iterator itr = csv.getCSVRows().iterator();
        while (itr.hasNext()) {
            CSVRow csvRow = (CSVRow)itr.next();
            csvRow.setErroneous(false);
        }
    }
}

