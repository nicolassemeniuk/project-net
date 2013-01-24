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
import java.util.ArrayList;

import net.project.base.attribute.IAttributeValue;

/**
 * A CSVCell class represents a single Cell from a CSV File.
 * @author Deepak
 * @since emu
 */
public class CSVCell implements  Serializable {
    /**
     * Column Number Object for the CSV Cell
     */
    private CSVColumnNumber colNum=null;
    /**
     * Row Number Object for the CSV Cell
     */
    private CSVRowNumber rowNum=null;
    /**
     * String containing Cell or Data Value
     */
    private CSVDataValue csvDataValue=null;
    private ArrayList iAttributeValueCols;
    /**
     * Indicates whether the Cell has been modified or not during the transformation
     */
    private boolean modified=false;

    /**
     * Constructs a new CSVCell object
     * @param colNum Column Number Object
     * @param rowNum Row Number Object
     * @param csvDataValue 
     */
    public CSVCell(CSVColumnNumber colNum,CSVRowNumber rowNum,CSVDataValue csvDataValue) {
        this.colNum = colNum;
        this.rowNum = rowNum;
        this.csvDataValue = csvDataValue;
        iAttributeValueCols = new ArrayList();
    }

    /**
     * Gets the DataValue for the Current CSVCell Object
     * @return CSVDataValue
     */
    public CSVDataValue getCSVDataValue() {
        return this.csvDataValue;
    }

    /**
     * Gets the Row Number for the Current CSVCell Object 
     * @return CSVRowNumber
     */
    public CSVRowNumber getCSVRowNumber() {
        return this.rowNum;
    }

    /**
    * Gets the CSVColumnNumber for the Current CSVColumn Object . 
    *@return CSVColumnNumber Object
    */
    public CSVColumnNumber getCSVColumnNumber() {
        return this.colNum;
    }
    
    /**
     *  Queries whether the CSVCell has been modified or not .
     * @return boolean
     */
    public boolean isModified(){
        if(iAttributeValueCols.size()>0){
            return true;
        }
        else{
            return false;
        }
    }

    /**
    * Adds the AttributeValue for the Current CSVCell Object.
     *
    * @param iAttributeValue
    */
    public void addAttributeValue(IAttributeValue iAttributeValue){
        this.iAttributeValueCols.add(iAttributeValue);
    }

    /**
     * Gets the collection of AttributeValues for the Cell Object .
     * @return return ArrayList Object
     */
    public ArrayList getAttributeValueCollection(){
         return this.iAttributeValueCols;
    }

}

