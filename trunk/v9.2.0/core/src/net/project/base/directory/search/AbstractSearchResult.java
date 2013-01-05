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

 /*-------------------------------------------------------------------+
|
|   $RCSfile$
|   $Revision: 20994 $
|   $Date: 2010-06-25 12:35:41 -0300 (vie, 25 jun 2010) $
|   $Author: umesha $
|
+-------------------------------------------------------------------*/
package net.project.base.directory.search;

import java.io.Serializable;

/**
 * Provides a basic implementation of an ISearchResult.
 */
public abstract class AbstractSearchResult implements ISearchResult,Serializable {

    /**
     * Returns the ID of this result.
     * The ID is used to uniquely identify this result in a list
     * of <code>ISearchResults</code>.
     * @return the result id
     */
    public abstract String getID();

    /**
     * Returns the firstName value of this search result.
     * @return the first name value
     */
    public abstract String getFirstName();

    /**
     * Returns the lastName value of this search result.
     * @return the last name value
     */
    public abstract String getLastName();

    /**
     * Returns the displayName value of this search result.
     * @return the display name value
     */
    public abstract String getDisplayName();    
    
    /**
     * Set the displayName value of this search result.
     * @param the displayName string value
     */
    public abstract void setDisplayName(String displayName);
    
    /**
     * Returns searched display name from 
     * Directory Search results
     * @return Searched display name
     */
    public abstract String getSearchedDisplayName();

    /**
     * Returns the email value of this search result.
     * @return the email address value
     */
    public abstract String getEmail();
    
    /**
     * Set the email value of this search result.
     * @param email string value to set
     */
    public abstract void setEmail(String email);

    /**
     * Set user disabled those are already invited.
     * @param isDisable boolean value to set
     */
    public abstract void setDisable(boolean isDisable);
    
    /**
     * Returns the user's disable or enable status
     * for current working space.
     * @return
     */
    public abstract boolean isDisable();
    
    /**
     * Returns the user's disable or enable status
     * for current working space.
     * @return
     */
    public abstract String getPersonId();
    
    /**
     * Returns members online status
     * @return members online status
     */
    public abstract boolean getOnline();
    
    /**
     * Method to set online members
     * @param isOnline set memeber online status
     */
	public abstract void setOnline(boolean isOnline);
    
    /**
     * Returns the XML representation of this search result.
     * Includes the xml Version tag.
     * Constructed by calling <code>getXMLDocument().getXMLString()</code>.
     * @return the xml representation
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    /**
     * Returns the XML representation of this search result.
     * Does not include the xml Version tag.
     * Constructed by calling <code>getXMLDocument().getXMLBodyString()</code>.
     * @return the xml representation
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns an XMLDocument containing the Search Result elements
     * and any subclass specific elements.
     * @return the XMLDocument.
     */
    protected abstract net.project.xml.document.XMLDocument getXMLDocument();

    
    /**
     * Adds the comment elements of a Search Result to the specified
     * XML document.
     */
    protected void addElements(net.project.xml.document.XMLDocument xml) 
            throws net.project.xml.document.XMLDocumentException {

        xml.addElement("ID", getID());
        xml.addElement("FirstName", getFirstName());
        xml.addElement("LastName", getLastName());
        xml.addElement("DisplayName", getDisplayName());
        xml.addElement("Email", getEmail());
    }

}
