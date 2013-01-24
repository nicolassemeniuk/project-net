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

 package net.project.code;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

/**
    A single Code item.
*/
public class Code implements IHTMLOption {

    public String objectID = null;

    /**
     * The display name of this code.
     * Note: This is only used when nameToken is not present.
     */
    private String name = null;

    /**
     * The token representing the name of this code.
     */
    private String nameToken = null;

    public String description = null;
    public String uri = null;
    public int presentationSequence = 0;
    public boolean isDefault = false;



    public Code () {
	// do nothing
    }

    /**
        Get the code number.
    */
     public String getCode()
     {
         return this.objectID;
     }


     public String getObjectID() {
	 return this.objectID;
     }

    void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this code.
     * This is gotten from the nameToken (if present), otherwise the
     * current name value is returned
     * @return the name
     */
     public String getName() {
         if (getNameToken() != null) {
             return PropertyProvider.get(getNameToken());
         } else {
             return this.name;
         }
     }

    void setNameToken(String nameToken) {
        this.nameToken = nameToken;
    }

    /**
     * Returns the name token used for getting the name of this Code.
     * @return the property for the code name
     */
    public String getNameToken() {
        return this.nameToken;
    }

      /**
            Get the code Description (human readable long form)
     */
     public String getDescription()
     {
         return this.description;
     }


      /**
            Get the order that this code should appear in the domain list.
     */
     public int getSequence()
     {
         return this.presentationSequence;
     }



      /**
            Get the URI of the resource associated with this code.
     */
     public String getURI()
     {
         return uri;
     }



     public String getXML () {

	 StringBuffer xml = new StringBuffer();
	 String tab = null;

	 tab = "\t\t";
	 xml.append(tab + "<object_id>" + XMLUtils.escape ( this.objectID ) + "</object_id>\n");
	 xml.append(tab + "<name>" + XMLUtils.escape ( getName()) + "</name>\n");
	 xml.append(tab + "<description>" + XMLUtils.escape ( this.description ) + "</description>\n");
	 xml.append(tab + "<url>" + XMLUtils.escape ( this.uri ) + "</url>\n");
	 xml.append(tab + "<presentation_sequence>" + XMLUtils.escape ( new Integer(this.presentationSequence).toString() ) + "</presentation_sequence>\n");
	 xml.append(tab + "<is_default>" + XMLUtils.escape ( Conversion.booleanToString (this.isDefault) ) + "</is_default>\n");

	return xml.toString();

    } // end getXML()


      /**
            Get String representation of the Code.
     */
     public String toString()
     {
         return getName() + "  (" + description + ")";
     }

     public boolean equals(Object obj) {
         // Codes are equal if object identity holds true
         if (this == obj) {
             return true;
         }

         // Codes are equal if objectIDs match
         if (obj instanceof Code &&
             obj != null &&
             ((Code) obj).getObjectID() != null &&
             ((Code) obj).getObjectID().equals(this.getObjectID()) ) {

             return true;
         }

         return false;
     }

    //
    // Implementing IHTMLOption
    //

    public String getHtmlOptionValue() {
        return getObjectID();
    }

    public String getHtmlOptionDisplay() {
        return getName();
    }
}
