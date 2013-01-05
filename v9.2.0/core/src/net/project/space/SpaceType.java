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

 /*------------------------------------------------------------------------------------------+
|                                                                       
|    $RCSfile$
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|    
|                                                                
+------------------------------------------------------------------------------------------*/
package net.project.space;

import java.io.Serializable;

import net.project.base.property.PropertyProvider;
import net.project.xml.XMLUtils;

/**
 * Represent a Single Type of Space
 *
 * @author deepak
 */

public class SpaceType implements Serializable {
    /**
     * ID of the Space Type
     */
    private String id=null;
    /**
     * Display Name of the Space Type
     */
    private String name=null;

    /* -------------------------------  Constructors  ------------------------------- */

    public SpaceType(String id ,String name) {
        this.id=id;
        this.name=name;
        SpaceTypes.add(this);
    }

    /**
    * Returns the Displayname for the current Object
    * @return String  
    */
    public String getName() {
        return PropertyProvider.get(this.name);
    }
         
   /**
    * Returns the ID for the current Object
    * @return String  
    */
    public String getID() {
        return this.id;
    }

    /**
     *  Converts the object to XML representation of the Space Type.
     *  This method returns the Space  Type as XML text. 
     *  @return XML representation of the Space Type
     */ 
    public String getXML() {
        return( "<?xml version=\"1.0\" ?>\n" + getXMLBody() );
    }

    /**
     *  Converts the Space Type to XML representation without the XML version tag.
     *  This method returns the Space Type as XML text. 
     *  @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer(100);
        xml.append("<SpaceType>\n");
        xml.append("<id>"+XMLUtils.escape(getID())+"</id>\n");
        xml.append("<name>"+XMLUtils.escape(getName())+"</name>\n");
        xml.append("</SpaceType>\n");
        return xml.toString();
    }
}
