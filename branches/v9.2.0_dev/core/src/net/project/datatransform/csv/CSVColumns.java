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
 * A CSVColumns class represents collections  of columns from the CSV File.
 * @author Deepak   
 * @since emu
 */

public class CSVColumns extends ArrayList implements Serializable{
 /**
   *Get the XML representation of Columns.
   *@return the XML representation of Columns in the String format
   */
  public String getXML(){
	StringBuffer xml =new StringBuffer();
	String tab=null;
	tab="\t\t";
	String nl="\n";
        Iterator itr = this.iterator();
    	    while(itr.hasNext()){
		CSVColumn csvCol=(CSVColumn)itr.next();
		xml.append(nl);
		xml.append(tab);
                xml.append("<Column id=\"");
		xml.append(csvCol.getColumnID());
		xml.append("\">");
		xml.append(nl);
		xml.append("\t\t\t");
		xml.append("<name>");
		xml.append(csvCol.getColumnName());
		xml.append("</name>");
		xml.append(nl);
		xml.append(tab);
		xml.append("</Column>");
	    }
	 return xml.toString();
   }

   /**
   *Get the CSVColumn based on the ID.
   *@return the CSVColumn
   */
    public CSVColumn getCSVColumnForID(String num) {

        Iterator itr = this.iterator();
        while(itr.hasNext()) {

            CSVColumn csvColumn = (CSVColumn)itr.next();

            if(csvColumn.getCSVColumnNumber().getCSVColumnNumberValue().equals(num)){
                return csvColumn;
            }

        }
        return null; // Return null if nothing is found 
   }

  /**
   *Get the CSVColumn based on the CSVColumnNumber.
   *@return the CSVColumn
   */
    public CSVColumn getCSVColumnByColumnNumber(CSVColumnNumber csvColumnNumber) {

        Iterator itr = this.iterator();
        while(itr.hasNext()) {

            CSVColumn csvColumn = (CSVColumn)itr.next();

            if(csvColumn.getCSVColumnNumber().equals(csvColumnNumber)){
                return csvColumn;
            }
        }
       return null; // Return null if nothing is found 
   }
}
