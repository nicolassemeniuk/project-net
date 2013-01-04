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

 package net.project.datatransform.csv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A CSVColumn class represents a single column from the CSV File.
 * @author Deepak   
 * @since emu
 */


public class CSVColumn implements Serializable{
    private String colID=null;
    private String colName=null; 
    private CSVCellCollection csvCellCol=null;
    private CSVColumnNumber csvColNum=null;

    /**
    * Constructs a new CSVColumn object 
    * 
    */

    public CSVColumn(String colID,String colName,CSVCellCollection csvCellCol ,CSVColumnNumber csvColNum) {
        this.colID=colID;
        this.colName=colName;
        this.csvCellCol=csvCellCol;
        this.csvColNum=csvColNum;
    }


    /**
     * Gets the Column ID for the Current CSVColumn Object
     * @return Column ID  
     */
    public String getColumnID() {
        return this.colID;
    }


    /**
     * Gets the Column Name for the Current CSVColumn Object 
     * @return Column Name
     */
    public String getColumnName() {
        return this.colName;
    }

    /**
     * Gets the CSVDataValues for the Current CSVColumn Object ... It can be then used to get dataValues in a Column Centric 
     * manner  
     *@return CSVDataValues Object
     */
    public ArrayList getColumnCellValues() {
		ArrayList<CSVCell> ary = new ArrayList<CSVCell>();
		for (CSVCell c : csvCellCol.getCSVCells()) {
			if (c.getCSVColumnNumber().equals(csvColNum)) {
				ary.add(c);
			}
		}
		return ary;
	}

    /**
	 * Gets the Column Number for the current CSV Column
	 * 
	 * @return CSVColumnNumber Object
	 */
    public CSVColumnNumber getCSVColumnNumber() {
        return this.csvColNum;
    }

    /**
     *Gets the Distinct CSV Columns Values for the cuurent Column Object  
     *@return Set Object
     */
    public Set getDistinctCSVColumnValues() {
		HashSet<CSVDataValue> hset = new HashSet<CSVDataValue>();
		for (CSVCell c : csvCellCol.getCSVCells()) {
			if (c.getCSVColumnNumber().equals(csvColNum)) {
				hset.add(c.getCSVDataValue());
			}
		}
		return hset;
	}

    /**
	 * Gets the Distinct CSV Columns Values for the cuurent Column Object
	 * 
	 * @return Set Object
	 */
    public CSVDataValue getCSVDataValue(String str, String csvDataValueID) {
		for (CSVCell c : csvCellCol.getCSVCellsForColumnNumber(csvColNum)) {
			if (c.getCSVDataValue().getValue().equals(str)) {
				return c.getCSVDataValue();
			}
		}
		return new CSVDataValue(str, csvDataValueID);
	}

    /**
	 * Gets the DataValue for CSV Column Number & ID number
	 * 
	 * @return CSVDataValue Object
	 * @param id
	 *            int
	 */
     public CSVDataValue getCSVDataValue(CSVColumnNumber colNum ,String id) {   
        for (CSVCell c : csvCellCol.getCSVCellsForColumnNumber(colNum)){
        	if(c.getCSVDataValue().getID().equals(id)){
                return c.getCSVDataValue();
            }
        }
        return null;   
    }


}
