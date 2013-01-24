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

import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.attribute.AttributeCollection;
import net.project.base.attribute.BooleanAttribute;
import net.project.base.attribute.DomainListAttribute;
import net.project.base.attribute.IAttribute;
import net.project.base.attribute.PersonListAttribute;
import net.project.datatransform.csv.map.ColumnMap;
import net.project.datatransform.csv.map.ColumnMaps;
import net.project.datatransform.csv.transformer.IDataTransformer;
import net.project.security.Action;

/**
 * Processses the Column Map Page, extracting the mappings that the user has
 * selected.
 *
 * @author deepak
 * @since emu
 */
public class ColumnMapPageProcessor {
    /** Constant for Attribute */
    private final String ATTRIBUTE = "attribute";
    /** Constant for Column */
    private final String COLUMN = "column";
    /** Stores & generates the query string for the Next Page. */
    private String queryString = "/datatransform/csv/DataResolution.jsp?module="+Module.FORM+"&action="+Action.MODIFY;
    /** Stores the Data Transformer ID */
    private long transformerID = 0;
    private final String UNASSIGNED_FIELD = "unassigned";

    /**
     * Processes the request passed by the Column Map Page & constructs ColumnMaps
     *
     * @param request a <code>HttpServletRequest</code> object from which
     * submitted form values will be fetched.
     * @param csv a <code>CSV</code> object in the Session which was originally
     * used to parse the CSV file.
     * @param attributes Attributes of IAttributeWriteable or IAttributeReadable
     * @param columnMaps Collection of Column Maps
     * @return a <code>String</code> value indicating where to redirect after the
     * column mapping is completed.
     */
    public String extractColumnMappings(HttpServletRequest request, CSV csv,
        AttributeCollection attributes, ColumnMaps columnMaps) {

        StringBuffer redirectLocation = new StringBuffer();
        Enumeration enumeration = request.getParameterNames();
        columnMaps.clear();

        //Add the base redirect location
        redirectLocation.append(queryString);

        //Iterate through all the parameters passed to this method to search for
        //mappings.
        while (enumeration.hasMoreElements()) {
            String name = (String)enumeration.nextElement();

            //Check to see if the current request parameter is an attribute
            //mapping.  If it is, process it.
            if ((name.indexOf(ATTRIBUTE) >= 0) && (name.indexOf("_") > -1)) {
                String attributeID = name.substring(name.indexOf("_")+1);
                String[] values = request.getParameterValues(name);

                //Iterate through all the of the field values looking for a
                //column that this attribute was mapped to.
                for (int i = 0; i < values.length; i++) {
                    if ((values[i].indexOf(COLUMN) >= 0) && (values[i].indexOf("_") > -1)) {
                        //Extract the column id of the column that this value is
                        //mapping to.
                        String colID = values[i].substring(values[i].indexOf("_")+1);

                        if (!colID.equalsIgnoreCase(UNASSIGNED_FIELD)) {
                            IAttribute iattr = attributes.getAttributeByID(attributeID);
                            CSVColumn csvColumn = csv.getCSVColumns().getCSVColumnForID(colID);
                            ColumnMap columnMap = getColumnMap(csvColumn, columnMaps);
                            columnMap.setCSVColumn(csvColumn);
                            columnMaps.add(columnMap);
                            IDataTransformer iDataTransformer = getDataTransformer(columnMap, iattr, String.valueOf(transformerID++));
                            columnMap.addDataTransformer(iDataTransformer);
                        }
                    }
                }
            }
        }
        return queryString;
    }

    /**
     * Gets the Column Map for the CSV Column & if not presents constructs that
     * for it.
     *
     * @param csvColumn The CSV Column Object
     * @param columnMaps Collection of Column Maps
     * @return
     */
    private ColumnMap getColumnMap(CSVColumn csvColumn, ColumnMaps columnMaps) {
        Iterator itr = columnMaps.iterator();
        boolean noneFound = true;

        while (itr.hasNext()) {
            ColumnMap columnMap = (ColumnMap)itr.next();

            if (columnMap.getCSVColumn().equals(csvColumn)) {
                noneFound = false;
                return columnMap;
            }
        }

        if (noneFound) {
            ColumnMap newColumnMap = new ColumnMap(csvColumn.getColumnID());
            newColumnMap.setCSVColumn(csvColumn);
            return newColumnMap;
        }
        return null;
    }

    /**
     * Returns the Data Transformer for the Attribute based on the ID
     *
     * @param columnMap Column Map for the DataTransformer
     * @param iAttribute Attribute of IAttributeReadable Object
     * @param transformerID ID of the Data Transformer
     * @return IDataTransformer
     */
    private IDataTransformer getDataTransformer(ColumnMap columnMap, IAttribute iAttribute, String transformerID) {
        Iterator itr = columnMap.getDataTransformerList().iterator();
        boolean noneFound = true;

        while (itr.hasNext()) {
            IDataTransformer iDataTransformer = (IDataTransformer)itr.next();
            IAttribute iattr = iDataTransformer.getAttribute();

            if ((iattr instanceof PersonListAttribute) ||
                (iattr instanceof DomainListAttribute) ||
                (iattr instanceof BooleanAttribute)) {
                if (iattr.equals(iAttribute)) {
                    noneFound = false;
                    return iDataTransformer;
                }
            }
        }

        if (noneFound) {
            IDataTransformer dataTransformer = iAttribute.getDataTransformer(transformerID, iAttribute);
            if (dataTransformer != null) {
                columnMap.addDataTransformer(dataTransformer);
            }
            return dataTransformer;
        }

        return null; // Return Null if nothing is there , denotes an error condition
    }
}
