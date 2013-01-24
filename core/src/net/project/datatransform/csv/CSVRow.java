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
import java.util.Iterator;

/**
 * A CSVRow class represents a single row from the CSV File.
 *
 * @author Deepak
 * @since emu
 */
public class CSVRow implements Serializable {
    private String rowID = null;
    private CSVCellCollection csvCellCol = null;
    private CSVRowNumber csvRowNum = null;
    private boolean errorStatus = false;

    /**
     * Constructs a new CSVColumn object.
     */
    public CSVRow(String rowID, CSVCellCollection csvCellCol, CSVRowNumber csvRowNum) {
        this.rowID = rowID;
        this.csvCellCol = csvCellCol;
        this.csvRowNum = csvRowNum;
    }


    /**
     * Gets the Row ID for the Current CSVRow Object.
     *
     * @return int
     */
    public String getRowID() {
        return this.rowID;
    }

    /**
     * Gets the Row Number for the Current CSVRow Object.
     *
     * @return int
     */
    public CSVRowNumber getCSVRowNumber() {
        return this.csvRowNum;
    }

    /**
     * Gets the CSVCells for the Current CSVRow Object.
     *
     * @return ArrayList Object
     */
    public ArrayList getRowCellValues() {
        ArrayList ary = new ArrayList();
        Iterator itr = csvCellCol.getCSVCells().iterator();
        while (itr.hasNext()) {
            CSVCell csvCell = (CSVCell)itr.next();
            if (csvCell.getCSVRowNumber().equals(csvRowNum)) {
                ary.add(csvCell);
            }
        }
        return ary;
    }

    /**
     * Returns the status of the Current CSVRow Object.
     *
     * @return boolean
     */
    public boolean isErroneous() {
        return errorStatus;
    }

    /**
     * Sets the status of the Current CSVRow Object.
     *
     * @param status
     */
    public void setErroneous(boolean status) {
        this.errorStatus = status;
    }

}
