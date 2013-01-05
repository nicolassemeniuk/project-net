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

/**
 * A ColumnMaps class stores all ColumnMaps.
 * @author Deepak
 * @since emu
 */

public class ColumnMaps extends HashSet implements Serializable {
   /**
    * Gets the Column Map corresponding to the CSVColumn passed
    *@return Columnmap 
    *@param CSVColumn   
    */
    public ColumnMap getColumnMapByCSVColumn(CSVColumn csvColumn){
        Iterator itr = this.iterator();
         ColumnMap colMap=null;
         while(itr.hasNext()){
            colMap=(ColumnMap)itr.next();
             if(colMap.getCSVColumn().equals(csvColumn)){
                 return colMap;
             }
         }
         return colMap;
    }
    
    /**
    * Gets the Column Map corresponding to the ID passed
    *@return ColumnMap
    *@param int  
    */
    public ColumnMap getColumnMapByID(String id){
        Iterator itr = this.iterator();
        ColumnMap colMap=null;
         while(itr.hasNext()){
            colMap=(ColumnMap)itr.next();
             if(colMap.getID().equals(id)){
                 return colMap;
             }
         }
         return colMap;
    }

   /**
    * Returns whether the Column Map has been Mapped or not
    *@return boolean
    *@param CSVColumn  
    */
   public boolean isCSVColumnMapped(CSVColumn csvColumn){
        Iterator itr = this.iterator();
         while(itr.hasNext()){
            ColumnMap colMap=(ColumnMap)itr.next();
             if(colMap.getCSVColumn().equals(csvColumn)){
                 return true;
               }
         }
         return false;
    }
}

