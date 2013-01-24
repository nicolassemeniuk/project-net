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
| TransitionList is a simple subclass of ArrayList which provides XML
| facilities.                                                         
+----------------------------------------------------------------------*/
package net.project.workflow;

import java.util.ArrayList;
import java.util.Iterator;

import net.project.persistence.IXMLPersistence;

/**
  * TransitionList provides XML facilities for an ArrayList of
  * Transitions
  */
public class TransitionList extends ArrayList
        implements IXMLPersistence, java.io.Serializable {

    /**
      * Get a transition from this list based on a transitionID<br />
      * Returns null if there is no transition for the specified id
      * @param id the transitionID
      * @return the transition
      */
    public Transition get(String id) {
        Transition tran = null;
        Iterator it = this.iterator();
        while (it.hasNext()) {
            tran = (Transition) it.next();
            if (tran.getID().equals(id)) {
                return tran;
            }
        }
        return null;
    }

    /**
      * Returns the individual Transition elements
      * @return XML elements as string
      */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();
        // Add each transition XML element to string
        for (int i=0; i < size(); i++) {
            xml.append( ((Transition)get(i)).getXMLBody() );
        }
        return xml.toString();
    }

    /*
        Implement IXMLPersistence
     */
    
    /**
      * Return the TransitionList XML including the XML version tag
      * @return the XML string
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }    
    
    /**
      * Return the TransitionList XML without the XML version tag
      * @return XML string
      */
    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<transition_list>\n");
        xml.append(getXMLElements());
        xml.append("</transition_list>\n");
        return xml.toString();
    }

}
