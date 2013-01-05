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
import java.util.Iterator;

/**
 * A CSVRows class represents collection of rows from the CSV File.
 * @author Deepak   
 * @since emu
 */


public class CSVRows  extends ArrayList implements Serializable{

    /**
     * Get the XML representation of DataValues.
     *@return the XML representation of dataValues in the String format
     */
    public String getXML(){
       StringBuffer xml =new StringBuffer();
	String tab=null;
	tab="\t\t";
	String nl="\n";
        Iterator itr = this.iterator();
    	    while(itr.hasNext()){
		CSVRow csvRow=(CSVRow)itr.next();
		xml.append(nl);
		xml.append("\t\t");  
		xml.append("<Row num=\"");
		xml.append(csvRow.getCSVRowNumber().getCSVRowNumberValue());
		xml.append("\">");
		xml.append(nl);
		xml.append("\t\t\t");
		xml.append("<DataValues>");
		
                Iterator itrRow=csvRow.getRowCellValues().iterator();
                while(itrRow.hasNext()){
                    CSVCell csvCell =(CSVCell)itrRow.next();
	            xml.append(nl);
		    xml.append("\t\t\t\t");
		    xml.append("<Value>");
		    xml.append(csvCell.getCSVDataValue().getValue());
	            xml.append("</Value>");
		}
		xml.append(nl);
		xml.append("\t\t\t");
		xml.append("</DataValues>");
		xml.append(nl);
		xml.append("\t\t");  
		xml.append("</Row>");
		xml.append(nl);
                
	    }
	 return xml.toString();
   }
    
   /**
    *Gets the CSVRow for ID
    *@param String
    *@return CSVRow 
    */
    public CSVRow getCSVRowForID(String num){
        CSVRow csvRow=null;
        Iterator itr =this.iterator();
            while(itr.hasNext()){
                csvRow=(CSVRow)itr.next();
                if(csvRow.getCSVRowNumber().getCSVRowNumberValue().equals( num)){
                    return csvRow;
                }
            }
        return csvRow;
    }

   /**
    *Gets the CSVRow for Row Number
    *@param CSVRowNumber
    *@return CSVRow 
    */
    public CSVRow getCSVRowForRowNumber(CSVRowNumber csvRowNumber){
        CSVRow csvRow=null;
        Iterator itr = this.iterator();
        while(itr.hasNext()){
            csvRow=(CSVRow)itr.next();
            if(csvRow.getCSVRowNumber().equals(csvRowNumber)){
                return csvRow;
            }
        }
        return csvRow;
   }

}
