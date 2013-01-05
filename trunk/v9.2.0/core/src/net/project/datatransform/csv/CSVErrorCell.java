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

/**
 * A CSV class represents a CSV File.
 * @author Deepak
 * @since emu
 */

public class CSVErrorCell implements  Serializable {
    private CSVColumnNumber colNum;
    private CSVRowNumber rowNum;
    private CSVDataValue csvDataValue;
    private String description;    

   /** Constructs a new CSVCell object 
    *@param CSVColumnNumber CSVRowNumber,CSVDataValue,String 
    */
    public CSVErrorCell(CSVColumnNumber colNum,CSVRowNumber rowNum,CSVDataValue csvDataValue,String description) {
        this.colNum = colNum;
        this.rowNum = rowNum;
        this.csvDataValue = csvDataValue;
        this.description = description;
    }

    /**
     * Gets the DataValue for the Error Cell
     *@return CSVDataValue 
     */
    public CSVDataValue getCSVDataValue() {
        return this.csvDataValue;
    }

    /**
     * Gets the Column Name for the Current CSVColumn Object 
     * @return Column Name
     */
    public CSVRowNumber getCSVRowNumber() {
        return this.rowNum;
    }

   /**
    * Gets the ColumnNumber for the Error Cell 
    *@return CSVColumnNumber Object
    */
    public CSVColumnNumber getCSVColumnNumber() {
        return this.colNum;
    }

    /**
    * Gets the Description for the Error 
    *@return String
    */
    public String getErrorDescription() {
        return this.description;
    }

}


