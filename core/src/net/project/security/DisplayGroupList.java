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
package net.project.security;

import net.project.persistence.IXMLPersistence;
import net.project.security.group.GroupCollection;
import net.project.space.Space;
import net.project.xml.XMLFormatter;


/**
 * A list of security Groups.  setSpace() must be called before calling load.
 * If the User context is set using setUser(), only groups for the User in the specified space will be loaded.
 * If the User context is not set, all active groups for the Space will be loaded.
 */
public class DisplayGroupList 
        extends GroupCollection 
        implements java.io.Serializable, IXMLPersistence {
    
    /** the space context */
    protected Space m_space = null;

    /** Contains XML formatting information and utilities specific to this object **/
    protected XMLFormatter m_formatter = null;

    protected boolean m_isLoaded = false;


    /**
     * Construct a group list of unknown size.  Grows as needed
     */
    public DisplayGroupList() {
        m_formatter = new XMLFormatter();
    }

    /**
     * Get the XML for the groups which apply to the User and Space context
     * @return XML representing the group list.
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }


    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<groupList>\n");
        for (int i=0; i<this.size(); i++) {
            if (((DisplayGroup) this.get(i)).isDisplay()) {
                xml.append( ((DisplayGroup)this.get(i)).getXMLBody() );
            }
        }
        xml.append("</groupList>\n");

        return xml.toString();
    }
    /**
    * Gets the presentation of the component
    * This method will apply the stylesheet to the XML representation of the component and
    * return the resulting text
    * 
    * @return presetation of the component
    */
    public String getPresentation() {
        return m_formatter.getPresentation(getXML());
    }


    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation of the component
     * to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        m_formatter.setStylesheet(styleSheetFileName);
    }  
}

