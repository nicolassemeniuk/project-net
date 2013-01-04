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

 package net.project.base.attribute;
import net.project.datatransform.csv.CSVDataValue;

/**
 * Represents the Attribute Value of Domain Type ie having Muliple Values
 */
public  class DomainAttributeValue extends AbstractAttributeValue {
   private DomainListHashMap domainValues = new DomainListHashMap();
   /**
    * ID for the Object
    */
   private int id;
   /**
    * DisplayName for the Object
    */
   private String displayName=null;
   /**
    * Attribute of the object
    */
   private IAttribute iAttribute;

   public DomainAttributeValue(IAttribute iAttribute){
       this.iAttribute=iAttribute;
   }

   /**
    * Adds Mapped values from CSV to Domain Value from the
    * Entity which is importing the CSV File 
    * @param csvDataValue 
    * @param iDomainValue 
    */
   public void addMapValues(CSVDataValue csvDataValue, DomainValue iDomainValue){
        this.domainValues.put(csvDataValue ,iDomainValue);
    }

  
   public DomainListHashMap getDomainListMap(){
        return this.domainValues;
    }

    /**
     * 
     * @return 
     */
    public IAttribute getAttribute(){
        return  this.iAttribute;
    }

}


