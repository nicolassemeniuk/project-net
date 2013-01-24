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

/**
 * A CSVDataValue class represents a single cell value from the CSV File.
 * @author Deepak   
 * @since emu
 */

public class CSVDataValue implements Serializable{
   
    /**
     * Value corresponding to eac CSV Cell 
     */
    private String value=null;
    /**
     * ID of the Object
     */
    private String id=null;
   
    // Represents Data for a single cell from the CSV File

    /**
    * Construct a new CSVDataValue object.
    * @param String 
   */

   public CSVDataValue(String dataValue ,String id){
      this.value = dataValue;
      this.id = id;  
   }

   /**
   * Get the CSV DataValue.
   * @return CSV DataValue .
   */
   public String getValue(){
      return value;
   }
	
   /**
    * Return XML representation for the CSVDataValue object
    * @return String XML Representation
    */
   
   public String getXML(){
      return value;
   }

   /**
    * Gets the ID for the DataValue.
    * @return String
    */

   public String getID(){
      return id;
   }


}