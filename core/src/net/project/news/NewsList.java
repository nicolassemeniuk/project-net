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
package net.project.news;

import java.util.ArrayList;

import net.project.persistence.IXMLPersistence;
import net.project.xml.XMLUtils;

/**
  * NewsList provides XML facilities for an ArrayList of
  * Newss
  */
public class NewsList extends ArrayList
        implements IXMLPersistence, java.io.Serializable {

    /**
      * Returns the individual News elements
      * @return XML elements as string
      */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();
        for (int i=0; i < size(); i++) {
            xml.append( ((News)get(i)).getXMLBody() );
        }
        return xml.toString();
    }

    /*
        Implement IXMLPersistence
     */
    
    /**
      * Return the NewsList XML including the XML version tag
      * @return the XML string
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }    
    
    /**
      * Return the NewsList XML without the XML version tag
      * @return XML string
      */
    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<news_list>\n");
        xml.append("<jsp_root_url>" + XMLUtils.escape(net.project.security.SessionManager.getJSPRootURL()) + "</jsp_root_url>");
        xml.append(getXMLElements());
        xml.append("</news_list>\n");
        return xml.toString();
    }

}
