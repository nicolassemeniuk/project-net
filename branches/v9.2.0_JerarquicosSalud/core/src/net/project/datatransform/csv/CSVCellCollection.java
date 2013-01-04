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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.datatransform.csv;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A CSVCellCollection class represents entire Cells in the CSVFile.
 * @author Deepak
 * @since emu
 */

public class CSVCellCollection implements Serializable{
    /**
     * Collection of all CSVCells in the System
     */
    private ArrayList<CSVCell> csvCells= new ArrayList<CSVCell>();

    /**
     * Adds a new Cell to the Collection
     * @param csvCell CSVCell object
     */
    public  void add(CSVCell csvCell){
        csvCells.add(csvCell);
    }

    /**
     * Get the total number of DataValue in the ArrayList.
     * @return int the total number of DataValue in the list
     */
    public int getLength(){
        return csvCells.size();
    }
  
    /**
     * Get the XML representation of DataValues.
     * @return String the XML representation of dataValues in the String format
     */
    //@SuppressWarnings("unchecked")
    public ArrayList<CSVCell> getCSVCells(){
        return csvCells;
    }
    
    /**
     * Get the CSV Cell based on the Column Number.
     * return Arraylist of CSVCell for the Column
     * param CSVColumnNumber
     * @param csvColNum CSVColumnNumber
     * @return ArrayList of CSVCells for the Coumn Number
     */
    public ArrayList<CSVCell> getCSVCellsForColumnNumber(CSVColumnNumber csvColNum) {
		ArrayList<CSVCell> ary = new ArrayList<CSVCell>();
		for (CSVCell c : csvCells) {
			if (c.getCSVColumnNumber().equals(csvColNum)) {
				ary.add(c);
			}
		}
		return ary;
	}

    /**
	 * Gets the no. of rows in the CSV Cell Collection
	 * 
	 * @return int Rows cout for the CSVCell Collection
	 */
    public int getRowsCount() {
    	// if unique result is needed next line should be used
		// return new HashSet(csvCells).size();
    	return csvCells.size(); 

		// original code, if need for some reasone

		// HashSet hset = new HashSet();
		// Iterator itr = csvCells.iterator();
		// while(itr.hasNext()) {
		// CSVCell csvCell = (CSVCell) itr.next();
		// hset.add(csvCell.getCSVRowNumber());
		//
		// }
		// return hset.size();
    }
}
