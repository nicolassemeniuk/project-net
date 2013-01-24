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
|   $Revision: 15505 $
|       $Date: 2006-10-16 10:48:43 +0530 (Mon, 16 Oct 2006) $
|     $Author: anarancio $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.directory.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.project.base.attribute.IAttribute;

/**
 * Provides view-level facilities for editing an LDAPAttributeMap.
 * <p>
 * Essentially provides a flattened list of profile attributes and
 * mapped LDAP attributes (called map elements).
 * Provides a method to fetch all the LDAP attributes from a user 
 * record.  Provides convenience methods for updating the mapped 
 * LDAP attribute values. <br>
 * The map element list is backed by the original map, such that
 * updating map elements here will update the LDAP Attribute map
 * held by the LDAP Directory Configuration.
 * </p>
 * <p>
 * Typical usage for displaying: <br>
 * <code><pre>
 * &lt;jsp:useBean name="attributeMapEditor" class="net.project.base.directory.ldap.LDAPAttributeMapEditor" scope="session" /&gt;
 * <% attributeMapEditor.initialize(directoryConfiguration); %>
 * <% attributeMapEditor.lookupForUsername(username); %>
 * <%
 *     // Iterate over attributeMapEditor.getMapElements().iterator();
 *     // Render each one editable
 *     
 *     // Render ldap dropdown box like so:
 *     <%=attributeMapEditor.getLDAPAttributeOptionList(currentElement.getLDAPAttributeName)%>
 *         
 * %>
 * </pre></code>
 * </p>
 * <p>
 * Typical usage for updating:
 * <code><pre>
 * &lt;jsp:useBean name="attributeMapEditor" class="net.project.base.directory.ldap.LDAPAttributeMapEditor" scope="session" /&gt;
 * <% attributeMapEditor.initialize(directoryConfiguration); %>
 * <% attributeMapEditor.processRequest(request); %>
 * <% directoryConfiguration.store(); %>
 * </pre></code>
 * </p>
 */
public class LDAPAttributeMapEditor {

    /**
     * The configuration which will store the attribute map editied
     * by this editor.
     */
    private LDAPDirectoryConfiguration config = null;
    
    /**
     * The list of elements used for rendering the edit screen.
     * Each element is of type <code>MapElement</code>
     */
    private List mapElements = null;
    
    /**
     * The username to use when looking up available LDAP attributes.
     */
    private String lookupUsername = null;

    /**
     * The LDAP attribute IDs provided by a user record.
     */
    private List ldapAttributeIDList = null;

    /** Indicates that attributes have been looked up. */
    private boolean isLDAPAttributeIDListAvailable = false;

    /** The map of attribute IDs to token names for description. */
    private AttributeDescriptionMap attributeDescriptionMap = new AttributeDescriptionMap();

    /**
     * Creates an empty LDAPAttributeMapEditor.
     */
    public LDAPAttributeMapEditor() {
        // Do nothing
    }


    /**
     * Initializes this editor from the specified configuration.
     * The configuration provides the existing map (if any) and will
     * store any updated map.
     * @param config the configuration that manages the map
     */
    public void initialize(LDAPDirectoryConfiguration config) {
        setConfiguration(config);
       
        // Now build collection of mapping elements
        this.mapElements = new ArrayList();
        LDAPAttributeMap map = config.getAttributeMap();
        
        // Note that we use keyList() which is a custom method returns
        // the keys in a predefined order
        for (Iterator it = map.keyList().iterator(); it.hasNext(); ) {
            IAttribute nextAttribute = (IAttribute) it.next();

            MapElement element = new MapElement(
                    nextAttribute, (LDAPAttributeMap.LDAPAttributeDefinition) map.get(nextAttribute)
                );

            this.mapElements.add(element);
        }

    }


    /**
     * Sets the current LDAP Configuration information.
     * The configuration contains the current attribute map.
     * @param config the LDAP Configuration
     */
    private void setConfiguration(LDAPDirectoryConfiguration config) {
        this.config = config;
    }


    /**
     * Specifies the username used for looking up the attributes and
     * fetches the attributes.
     * A username is necessary in order to build a full DN to a user record.
     * Without this, we don't know the DN of any user record.
     * After calling, the LDAP attributes will have been fetched and
     * the map will become modifiable.
     * @param username the username to use for looking up a user record
     * @throws net.project.base.directory.DirectoryException if there is
     * a problem looking up the attributes for the user
     */
    public void lookupForUsername(String username) 
            throws net.project.base.directory.DirectoryException {

        this.lookupUsername = username;
        this.ldapAttributeIDList = new ArrayList();
        this.ldapAttributeIDList.addAll(lookupLDAPAttributes(username));
        this.isLDAPAttributeIDListAvailable = true;
    }


    /**
     * Returns a collection of attribute IDs read from the user record
     * of the specified username.
     * @param username the user whose user record to use
     * @return the collection of String attribute IDs where each one
     * is the ID of an attribute provided by the LDAP directory for
     * the specified user; the values are sorted in text order
     * @throws net.project.base.directory.DirectoryException if there is a problem looking up
     * the attributes
     */
    private Collection lookupLDAPAttributes(String username) 
            throws net.project.base.directory.DirectoryException {

        LDAPHelper ldap = new LDAPHelper();

        // Get a context to the search base
        // By using the same context we potentially maintain
        // a single connection with the LDAP server
        // (This statement yet to be proven)
        javax.naming.directory.DirContext context = ldap.getBaseContext(this.config);
        
        // Search for the user entry that contains the username; returns an absolute and relative DN
        LDAPHelper.UserDN userDN = ldap.findUserDN(context, this.config, username);

        // Fetch the attributes for the user with anonymous connection
        javax.naming.directory.Attributes attributes = ldap.fetchAttributes(context, userDN);

        // Construct a List from the attribute IDs (which are really names)
        ArrayList attributeNameList = new ArrayList(attributes.size());
        for (javax.naming.NamingEnumeration enumeration = attributes.getAll(); enumeration.hasMoreElements(); ) {
            javax.naming.directory.Attribute nextAttribute = (javax.naming.directory.Attribute) enumeration.nextElement();
            attributeNameList.add(nextAttribute.getID());
        }

        // Now sort the LDAP Attribute names
        java.util.Collections.sort(attributeNameList);
        
        return attributeNameList;
    }

    /**
     * Indicates whether LDAP Attributes have been successfully looked-up.
     * @return true if ldap attributes are available; false otherwise
     * @see #lookupForUsername
     */
    public boolean isLDAPAttributeIDListAvailable() {
        return this.isLDAPAttributeIDListAvailable;
    }


    /**
     * Returns all the LDAP attribute IDs provided by the looked-up
     * user record.
     * @return the list of String attribute IDs
     * @see #lookupForUsername
     * @throws IllegalStateException if no attributes have been looked-up
     */
    public List getLDAPAttributeIDList() {
        if (!isLDAPAttributeIDListAvailable()) {
            throw new IllegalStateException("No LDAP attributes available yet");
        }
        return this.ldapAttributeIDList;
    }


    /**
     * Returns an HTML option list built from LDAP Attribute IDs.
     * For example: <br>
     * <code><pre>
     *     &lt;option value="cn"&gt;cn&lt;/option&gt;
     *     &lt;option value="mail" selected&gt;mail&lt;/option&gt;
     * </pre></code> <br>
     * Note: If the specified <code>selectedValue</code> is not
     * actually in the list, it is added to the top and flagged as
     * such.
     * @param selectedValue the value to flag as selected in the option
     * list
     * @return the option list
     */
    public String getLDAPAttributeIDOptionList(String selectedValue) {

        boolean isSelectedValueFound = false;
        StringBuffer result = new StringBuffer();

        // Iterate over all attribute IDs, building option list
        for (Iterator it = getLDAPAttributeIDList().iterator(); it.hasNext(); ) {
            String nextAttributeID = (String) it.next();

            result.append("<option value=\"").append(nextAttributeID).append("\"");

            if (nextAttributeID.equals(selectedValue)) {
                isSelectedValueFound = true;
                result.append(" selected");
            }

            result.append(">");
            result.append(makeDisplayValue(nextAttributeID));
            result.append("</option>");
            result.append("\n");
        }

        // If we didn't locate the selected value in the option list
        // Then we add it as the first value and mark it
        // This ensures that the option list contains all values
        // Note that we ignore empty selected values; this option list
        // doesn't specify whether there is a null-value option in the
        // list
        if (!isSelectedValueFound && selectedValue != null && selectedValue.trim().length() > 0) {
            StringBuffer modifiedResult = new StringBuffer();
            modifiedResult.append("<option value=\"").append(selectedValue).append("\"");
            modifiedResult.append(" selected").append(">*");
            // We try and get a description for the selected value; even
            // though its not in the option list, it might be an attribute
            // that we know about which is simply not in the current list of
            // attributes
            modifiedResult.append(makeDisplayValue(selectedValue));            
            modifiedResult.append("*</option>");
            modifiedResult.append("\n");
            modifiedResult.append(result.toString());
        
            // Reassign augmented option list
            result = modifiedResult;
        }

        return result.toString();
    }

    /**
     * Makes a display value for the specified attributeID by looking it
     * up in the attribute description map.
     * A display value depends on whether a description is found; without
     * a description the display value is simply the attributeID, for example
     * <code>cn</code>.
     * With a description, the description follows the attributeID, for example
     * <code>cn (Display Name)</code>.
     * @param attributeID the attribute to get the display value for
     * @return the display value
     */
    private String makeDisplayValue(String attributeID) {
        StringBuffer result = new StringBuffer();
        
        String attributeDescription = this.attributeDescriptionMap.getDescription(attributeID);
        if (attributeDescription != null) {
            // Of the form:   attributeID (Description)
            // For example:    cn (Full Name)
            result.append(attributeID).append(" (").append(attributeDescription).append(")");

        } else {
            // No description, so just use the attribute id
            result.append(attributeID);
        }

        return result.toString();
    }

    /**
     * Returns the map elements built from the attribute map.
     * Each element is of type <code>MapElement</code>
     * @return the list of map elements
     */
    public List getMapElements() {
        return this.mapElements;
    }


    /**
     * Process the request and updates the map values.
     * This modified the map elements returned by {@link #getMapElements}.
     * @param request the request containing all the parameters for
     * updating the map
     */
    public void processRequest(javax.servlet.http.HttpServletRequest request) {

        // Iterate over all the request parameters, looking for and
        // processing the relevant ones
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
            
            String nextParameter = (String) e.nextElement();

            // Process only the relevant parameters
            if (nextParameter.startsWith("ldapAttributeName") ||
                nextParameter.startsWith("valueIndex")) {

                // Names are of the form <propertyName>_<index>
                // For example, ldapAttributeName_2
                // Grab the property name and the index number
                int lastUnderScorePos = nextParameter.lastIndexOf("_");
                String propertyName = nextParameter.substring(0, lastUnderScorePos);
                int index = Integer.valueOf(nextParameter.substring(lastUnderScorePos + 1)).intValue();

                // Now fetch the map element at that index position
                // So that we can update it
                MapElement element = (MapElement) this.mapElements.get(index);

                // Set the appropriate property of the map element
                if (propertyName.equals("ldapAttributeName")) {
                    element.setLDAPAttributeName(request.getParameter(nextParameter));

                } else if (propertyName.equals("valueIndex")) {
                    element.setValueIndex(request.getParameter(nextParameter));

                }

            }

        }

        // Since updating the map elements modifies the underlying
        // map directly, no more work is performed; the map
        // has been modified
    }

    /**
     * Indicates whether the mapping is missing values for any mandatory
     * mappings.
     * @return true if one or more mandatory mappings has not been completed;
     * false otherwise
     */
    public boolean isCompleteMapping() {

        boolean isComplete = true;

        // Iterate over all map elements, checking required elements
        // for complete mappings
        for (Iterator it = getMapElements().iterator(); it.hasNext() && isComplete; ) {
            MapElement nextElement = (MapElement) it.next();

            if (nextElement.isRequired()) {
                isComplete &= nextElement.isMapped();
            }
        }

        return isComplete;
    }

    //
    // Nested top-level classes
    //


    /**
     * Defines a mapping element, proving convenient methods for
     * extracting the values needed for the presentation.
     * Note that each element is backed by the original IAttribute
     * and LDAPAttributeDefinition such that updating will modify
     * the underlying object
     */
    public class MapElement {

        private IAttribute attribute = null;
        private LDAPAttributeMap.LDAPAttributeDefinition ldapAttribute = null;
        private boolean isRequired = false;

        /**
         * Creates a new MapElement from the specified profile attribute
         * and ldap attribute definition.
         * @param attribute the profile attribute
         * @param ldapAttribute the LDAP Attribute definition
         */
        public MapElement(IAttribute attribute, LDAPAttributeMap.LDAPAttributeDefinition ldapAttribute) {
            this.attribute = attribute;
            this.ldapAttribute = ldapAttribute;
            this.isRequired = LDAPAttributeMap.getRequiredMappedAttributeIDs().contains(attribute.getID());
        }

        /**
         * Indicates whether a mapping definition is required for this profile
         * attribute.
         * @return true if this mapping is required; false otherwise
         */
        public boolean isRequired() {
            return this.isRequired;
        }

        /**
         * Indicates whether this mapping definition actual has a mapped LDAP attribute.
         * @return true if there is a mapped LDAP attribute; false if no LDAP
         * attribute has been selected.
         */
        public boolean isMapped() {
            return this.ldapAttribute.isDefined();
        }

        /**
         * Returns the profile attribute if of this element.
         * @return the profile attribute id, for example <code>person.email</code>
         */
        public String getProfileAttributeID() {
            return this.attribute.getID();
        }

        /**
         * Returns the profile attribute display name of this element.
         * @return the display name
         */
        public String getProfileAttributeDisplayName() {
            return this.attribute.getDisplayName();
        }

        /**
         * Sets the new LDAP attribute name to which the current
         * attribute is mapped; this updates the underlying LDAPAttributeDefinition
         * @param ldapAttributeName the new ldap attribute name to map to
         */
        public void setLDAPAttributeName(String ldapAttributeName) {
            this.ldapAttribute.setName(ldapAttributeName);
        }

        /**
         * Returns the LDAP attribute name that the current attribute
         * is mapped to.
         * @return the attribute name, for example <code>cn</code>
         */
        public String getLDAPAttributeName() {
            return this.ldapAttribute.getName();
        }

        /**
         * Returns the description of the ldap attribute (if any).
         * @return the description, for example <code>Full Name</code>
         */
        public String getLDAPAttributeDescription() {
            return LDAPAttributeMapEditor.this.attributeDescriptionMap.getDescription(this.ldapAttribute.getName());
        }

        /**
         * Indicates whether the backed LDAPAttributeDefinition currently
         * specifies a valueIndex number.
         * @return true if a valueIndex number is specified; false otherwise
         * @see LDAPAttributeMap.LDAPAttributeDefinition#isValueIndexSpecified
         */
        public boolean isValueIndexSpecified() {
            return this.ldapAttribute.isValueIndexSpecified();
        }

        /**
         * Sets the valueIndex for the backed LDAPAttributeDefnition.
         * @param the valueIndex number; this affects {@link #isValueIndexSpecified}
         * If valueIndex contains a number then <code>isValueIndexSpecified</code>
         * will return true; if the valueIndex is null or empty then
         * <code>isValueIndexSpecified</code> will return false
         */
        public void setValueIndex(String valueIndex) {
            
            if (valueIndex != null && valueIndex.trim().length() > 0) {
                this.ldapAttribute.setValueIndex(Integer.valueOf(valueIndex.trim()).intValue());
                this.ldapAttribute.setValueIndexSpecified(true);
            
            } else {
                this.ldapAttribute.setValueIndex(0);
                this.ldapAttribute.setValueIndexSpecified(false);
            }
        }

        /**
         * Returns the value index for the backed LDAPAttributeDefinition.
         * @return the valueIndex number or the empty string if
         * no valueIndex is specified by the LDAPAttributeDefinition
         * @see LDAPAttributeMap.LDAPAttributeDefinition#getValueIndex
         */
        public String getValueIndex() {
            String value = null;

            if (this.ldapAttribute.isValueIndexSpecified()) {
                value = String.valueOf(this.ldapAttribute.getValueIndex());
            } else {
                value = "";
            }

            return value;
        }

    }

    /**
     * Provides a lookup from LDAP attribute IDs to property names.
     * LDAP Servers don't provide descriptive names for attributes.
     * Each key is a String LDAP attribute ID (for example <code>cn</code>).
     * Each value is a String property name (for example <code>prm.global.domain.directory.ldap.attribute.cn.description</code>)
     */
    private static class AttributeDescriptionMap extends HashMap {

        /**
         * Creates a new AttributeDescriptionMap populated with the
         * attribute ID to property name values.
         */
        AttributeDescriptionMap() {

            // These are the attributes that we support descriptions for
            // There are hundreds of possible attributes, and they depend on
            // each individual LDAP server
            // Since this is just an aid for an administrator, we don't need
            // to be exhaustive, or even allow for customization (yet)
            // Perhaps these should be in the database?
            put("cn", "prm.global.domain.directory.ldap.attribute.cn.description");
            put("mail", "prm.global.domain.directory.ldap.attribute.mail.description");
            put("givenname", "prm.global.domain.directory.ldap.attribute.givenname.description");
            put("sn", "prm.global.domain.directory.ldap.attribute.sn.description");
            put("uid", "prm.global.domain.directory.ldap.attribute.uid.description");
            put("alias", "prm.global.domain.directory.ldap.attribute.alias.description");
            put("userpassword", "prm.global.domain.directory.ldap.attribute.userpassword.description");
            put("telephonenumber", "prm.global.domain.directory.ldap.attribute.telephonenumber.description");
            put("facsimiletelephonenumber", "prm.global.domain.directory.ldap.attribute.facsimiletelephonenumber.description");
            put("mobile", "prm.global.domain.directory.ldap.attribute.mobile.description");
            put("pager", "prm.global.domain.directory.ldap.attribute.pager.description");

        }

        /**
         * Puts String value in the hashmap with the specified key.
         * THe key is always converted to lowercase to ensure case-insensitive
         * key matching.
         * @param key the key
         * @param value the value
         * @return the Object at the current key location or null if there is none
         */
        Object put(String key, String value) {
            return super.put(key.toLowerCase(), value);
        }

        /**
         * Returns the description for the specified attribute by looking up its token value.
         * @param attributeID the attribute to get the description for
         * @return the description for that attribute or null if this map does
         * not contain a key matching the specified attributeID
         */
        String getDescription(String attributeID) {
            String description = null;
            String descriptionToken = null;
            
            // Grab the token with a LOWERCASE key
            descriptionToken = (String) get(attributeID.toLowerCase());

            if (descriptionToken != null) {
                // Lookup description from token
                description = net.project.base.property.PropertyProvider.get(descriptionToken);
            }

            return description;
        }
    
    }
}
