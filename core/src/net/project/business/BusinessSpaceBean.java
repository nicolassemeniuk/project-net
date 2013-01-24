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

 package net.project.business;

import java.io.Serializable;

import net.project.xml.XMLFormatter;



/**  
    Provides properties methods for a Business Workspace.
    Adds presentation-specific methods to BusinessSpace.
*/
public class BusinessSpaceBean 
extends BusinessSpace 
implements Serializable
{
    protected XMLFormatter formatter;
    
    public static final String SPACE_ID = "15";


    /** bean constructor */
    public BusinessSpaceBean()
    {
    	super(SPACE_ID);
        formatter = new XMLFormatter();
    }



    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation of the component
     * to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet (String styleSheetFileName)
    {
        formatter.setStylesheet(styleSheetFileName);
    }

    /**
    * Gets the presentation of the component
    * This method will apply the stylesheet to the XML representation of the component and
    * return the resulting text
    * 
    * @return presetation of the component
    */
    public String getPresentation () 
    {
        return( formatter.getPresentation( getXML()) );
    } 


}



