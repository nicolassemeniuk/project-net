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
import java.util.HashSet;
import java.util.Iterator;

public class CSVErrorCellCollection  implements Serializable{
    private ArrayList csvErrorCells= new ArrayList();

    public  void add(CSVErrorCell csvErrorCell){
        csvErrorCells.add(csvErrorCell);
    }

   /**
    * Get the total number of DataValue in the ArrayList.
    *@return the total number of DataValue in the list
    */
    public int getLength(){
      return csvErrorCells.size();
    }
  
    /**
     *Get the XML representation of DataValues.
     *@return the XML representation of dataValues in the String format
     */
    public ArrayList getCSVErrorCells(){
      return csvErrorCells;
    }
    
    /**
     *Get the Error Cells based on the Column Number.
     *@return Arraylist of CSVCell for the Column
     *@param CSVColumnNumber
     */
    public ArrayList getCSVErrorCellsForColumnNumber(CSVColumnNumber csvColNum){
        ArrayList ary = new ArrayList();
        Iterator itr = csvErrorCells.iterator();
        while(itr.hasNext()) {
            CSVErrorCell csvErrorCell =(CSVErrorCell) itr.next();

            if(csvErrorCell.getCSVColumnNumber().equals(csvColNum)){
                ary.add(csvErrorCell);
            }
        }
        return ary;
    }

    /**
    *Get the Error Cells based on the Row Number.
    *@return Arraylist of CSVCell for the Row
    *@param CSVColumnNumber
    */

    public ArrayList getCSVErrorCellsForRowNumber(CSVRowNumber csvRowNum) {
        ArrayList ary = new ArrayList();
        Iterator itr = csvErrorCells.iterator();

        while(itr.hasNext()) {
            CSVErrorCell csvErrorCell =(CSVErrorCell) itr.next();

            if(csvErrorCell.getCSVRowNumber().equals(csvRowNum)){
                ary.add(csvErrorCell);
            }
        }
       return ary;
    }
    
    /**
    *Get the Row Count for the Erroneous Rows
    *@return int
    */
    public int getErrorRowsCount() {
        HashSet hset = new HashSet();
        Iterator itr = csvErrorCells.iterator();
        
        while(itr.hasNext()){
            CSVErrorCell csvErrorCell = (CSVErrorCell) itr.next();
            hset.add(csvErrorCell.getCSVRowNumber());
        }
        return hset.size();
      }
    
    /**
    *Gets the Set of Rows Numbers with an Erroneous Cells
    *@return HashSet 
    */
    public HashSet getErrorRowNumbers() {
        HashSet hset = new HashSet();
        Iterator itr = csvErrorCells.iterator();

        while(itr.hasNext()){
            CSVErrorCell csvErrorCell = (CSVErrorCell) itr.next();
            hset.add(csvErrorCell.getCSVRowNumber());
        }
         return hset;
    }

}

