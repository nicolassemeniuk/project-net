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
package net.project.base.directory.ldap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.project.base.attribute.IAttribute;

/**
 * Provides properties of an entry in an LDAP directory.
 */
public class LDAPDirectoryEntry implements net.project.base.directory.IDirectoryEntry {

    /** The map of profile attributes to actual values. */
    private final Map attributeValueMap = new HashMap();

    /**
     * Returns the email address.
     * This is a convenience method for fetching the email address
     * from an LDAP directory entry.
     * @return the email address
     * @see #getEmail
     */
    public String getEmail() {
        return getAttributeValue(net.project.resource.IPersonAttributes.EMAIL_ATTRIBUTE);
    }

    /**
     * Returns the content of this directory entry.
     * The precise format is undefined; it contains all the attributes
     * and their values supported by this directory entry.
     * @return the string format of this directory entry
     */
    public String toString() {
        StringBuffer str = new StringBuffer(super.toString()).append("\n");
        
        for (Iterator it = this.attributeValueMap.keySet().iterator(); it.hasNext(); ) {
            IAttribute nextAttribute = (IAttribute) it.next();
            str.append(nextAttribute.getID()).append(": ")
                .append((String) this.attributeValueMap.get(nextAttribute))
                .append("\n");
        }
        return str.toString();
    }

    /**
     * Populates this directory entry from the map of profile
     * attributes to object values.
     * @param attributeValueMap the map where keys are IAttributes
     */
    public void populate(Map attributeValueMap) {
        // Save the mappings
        this.attributeValueMap.clear();
        this.attributeValueMap.putAll(attributeValueMap);
    }


    /**
     * Indicates whether the profile attribute with the specified
     * ID is provided by this directory entry.
     * @param attributeID the attribute to check for
     * @return true if this directory entry provides a value for
     * the specified attribute; false otherwise
     * @throws NullPointerException if attributeID is null
     */
    public boolean isAttributeProvided(String attributeID) {

        if (attributeID == null) {
            throw new NullPointerException("Missing required parameter attributeID");
        }

        boolean isProvided = false;

        // Iterate over IAttribute keys, looking for first that
        // has matching ID
        for (Iterator it = this.attributeValueMap.keySet().iterator(); it.hasNext() && !isProvided; ) {
            String nextAttributeID = ((IAttribute) it.next()).getID();
            
            if (nextAttributeID.equals(attributeID)) {
                isProvided = true;
            }
        
        }
        
        return isProvided;
    }


    /**
     * Returns this directory entry's value for the specified
     * attribute.
     * @param attributeID the attribute value to get
     * @return the value for that attribute; returns the empty
     * string if this directory entry does not provide the
     * specified attribute
     * @throws NullPointerException if attributeID is null
     */
    public String getAttributeValue(String attributeID) {

        if (attributeID == null) {
            throw new NullPointerException("Missing required parameter attributeID");
        }

        String value = null;
        boolean isFound = false;

        // Iterate over IAttribute keys, looking for first that
        // has matchind ID, then get value for that attribute
        for (Iterator it = this.attributeValueMap.keySet().iterator(); it.hasNext() && !isFound; ) {
            IAttribute nextAttribute = (IAttribute) it.next();

            if (nextAttribute.getID().equals(attributeID)) {
                value = (String) this.attributeValueMap.get(nextAttribute);
                if (value == null) {
                    value = "";
                }
                isFound = true;
            }
        
        }

        return value;
    }

}
