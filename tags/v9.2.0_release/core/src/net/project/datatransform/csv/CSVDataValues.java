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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A CSVDataValues class represents collection of cell values from the CSV File.
 * @author Deepak   
 * @since emu
 */

public class CSVDataValues{
    
   private  ArrayList values=new ArrayList();

   public  void add(CSVDataValue newVal)
   {
      values.add(newVal);
   }

  /**
   *Get the total number of DataValue in the ArrayList.
   *@return the total number of DataValue in the list
   */
   public int getLength()
   {
      return values.size();
   }
  
   /**
    *Get the XML representation of DataValues.
    *@return the XML representation of dataValues in the String format
    */
   public String getXML(){
      return values.toString();
   }

   /** 
   *   Returns the a Sorted Set of DataValues 
   *
   *  @return SortedSet 
   */

    public Set getDistinctDataValuesSet(){

    Set tset = new TreeSet();
    Iterator itr = values.iterator();

        while(itr.hasNext()) {
            CSVDataValue csvDataValue = (CSVDataValue)itr.next();
            tset.add((String)csvDataValue.getValue());    
        }
        return tset;		   	
   }
	
   /**
   * Returns the List of DataValue (which represent a Data at individual cell in a CSV File)
   *@return ArrayList 
   */ 
   public ArrayList getDataValueList(){
        return this.values;     	       
   }
   	   
	
}
