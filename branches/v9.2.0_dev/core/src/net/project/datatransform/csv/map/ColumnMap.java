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
package net.project.datatransform.csv.map;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;

import net.project.datatransform.csv.CSVColumn;
import net.project.datatransform.csv.transformer.IDataTransformer;

/**
 * The ColumnMap class is responsible for maintaining the mappings from a column
 * in a CSV file to a form field.
 *
 * @author Deepak
 * @since emu
 */
public class ColumnMap implements Serializable {
    private CSVColumn csvColumn = null;
    private String id = null;
    private HashSet dataTransformerList = new HashSet();

    public ColumnMap(String id) {
        this.id = id;
    }

    /**
     * Gets the CSVColumn for the current ColumnMap
     * @return CSVColumn
     */
    public CSVColumn getCSVColumn() {
        return this.csvColumn;
    }

    /**
     * Returns the ID for the current Object
     * @return int
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the List of Data Tranformer Object for the Column map
     * @return int
     */
    public HashSet getDataTransformerList() {
        return this.dataTransformerList;
    }

    /**
     * Sets the column for the Column map
     * @param csvColumn
     */
    public void setCSVColumn(CSVColumn csvColumn) {
        this.csvColumn = csvColumn;
    }

    /**
     * Adds a new Data Tranformer to the Column map
     * @param iDataTransformer
     */
    public void addDataTransformer(IDataTransformer iDataTransformer) {
        this.dataTransformerList.add(iDataTransformer);
    }

    /**
     * Gets the Data Tranformer based on the ID
     * @return IDataTranformer
     * @param id
     */
    public IDataTransformer getDataTransformerByID(String id) {
        Iterator itr = dataTransformerList.iterator();
        IDataTransformer iDataTransformer = null;
        while (itr.hasNext()) {
            iDataTransformer = (IDataTransformer)itr.next();
            if (iDataTransformer.getID().equals(id)) {
                return iDataTransformer;
            }
        }
        return iDataTransformer;
    }
}



