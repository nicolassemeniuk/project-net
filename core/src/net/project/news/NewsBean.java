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

import net.project.space.Space;
import net.project.xml.XMLFormatter;

public class NewsBean extends News {

    private Space space;
    private XMLFormatter xmlFormatter;

    /**
      * Creates new NewsBean
      */
    public NewsBean() {
        this.xmlFormatter = new XMLFormatter();
    }
    
   /**
      * Set the current space
      * @param space the current space
      */
    public void setSpace(Space space) {
        this.space = space;
    }

    /**
      * Set the stylesheet to use
      * @param stylesheet the stylesheet path
      */
    public void setStylesheet(String stylesheetFileName) {
        xmlFormatter.setStylesheet(stylesheetFileName);
    }

    public String getPropertiesPresentation() {
        return xmlFormatter.getPresentation(getXML());
    }

    /**
      * Return the results of a previous prepareRemove()
      * @return the HTML results
      */
    public String getPrepareRemovePresentation() {
        return getPrepareRemoveErrorsPresentation();
    }

    public String getRemoveResultPresentation() {
        return getRemoveResultPresentation();
    }

}
