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

package net.project.document;

import java.io.Serializable;

import net.project.xml.XMLFormatter;

public class DocumentBean extends Document implements IContainerObject, Serializable {

    protected XMLFormatter xml;

    public DocumentBean() {
        xml = new XMLFormatter();
    }

    /**
     * Sets the stylesheet file name used to render this component. This method
     * accepts the name of the stylesheet used to convert the XML representation
     * of the component to a presentation form.
     * 
     * @param styleSheetFileName
     *            name of the stylesheet used to render the XML representation
     *            of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        xml.setStylesheet(styleSheetFileName);
    }

    /**
     * Gets the presentation of the component This method will apply the
     * stylesheet to the XML representation of the component and return the
     * resulting text
     * 
     * @return presetation of the component
     */
    public String getPresentation() {

        // return the XML representation of the Document's contents
        return (xml.getPresentation(getXML()));

    } // end getPresentation

    public String getPropertiesPresentation() {

        // return the XML representation of the Document's contents
        return (xml.getPresentation(getXMLProperties()));

    } // end getPresentation

    public String getVersionsPresentation() {

        // return the XML representation of the Document's contents
        return (xml.getPresentation(getXMLVersions()));

    } // end getPresentation

    public String getEventsPresentation() {

        return (xml.getPresentation(getXMLEvents()));

    } // end getPresentation

} // end class DocumentBean
