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
|   $Revision: 14743 $
|       $Date: 2006-02-06 22:26:39 +0530 (Mon, 06 Feb 2006) $
|     $Author: andrewr $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.directory.ldap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.project.base.attribute.AttributeCollection;
import net.project.base.attribute.IAttribute;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.IPersonAttributes;
import net.project.resource.Person;

/**
 * Defines a map of Person Attributes to LDAP attributes to enable
 * population of a Person Profile from an LDAP user record.
 * <p>
 * Keys are of type <code>IAttribute</code>.  
 * Values are of type <code>LDAPAttributeDefinition</code>.
 * The map always contains all supported <code>IAttribute</code>s as keys;
 * those values which have not been mapped are mapped to <code>LDAPAttributeDefinition</code>
 * values where {@link LDAPAttributeDefinition#isDefined} is fales.
 * </p>
 */
public class LDAPAttributeMap extends HashMap {

    //
    // Static members
    //

    /**
     * An arraylist of profile attribute IDs supported for mapping.
     * We don't want to display all the attributes available; some
     * do not make sense.
     * This list is consulted when populating the map during a load.
     */
    private static final List SUPPORTED_ATTRIBUTE_IDS = 
        Arrays.asList(
            new String[] {
                IPersonAttributes.PREFIX_ATTRIBUTE,
                IPersonAttributes.FIRSTNAME_ATTRIBUTE,
                IPersonAttributes.MIDDLENAME_ATTRIBUTE,
                IPersonAttributes.LASTNAME_ATTRIBUTE,
                IPersonAttributes.SUFFIX_ATTRIBUTE,
                IPersonAttributes.DISPLAYNAME_ATTRIBUTE,
                IPersonAttributes.EMAIL_ATTRIBUTE,
                IPersonAttributes.ADDRESS1_ATTRIBUTE,
                IPersonAttributes.ADDRESS2_ATTRIBUTE,
                IPersonAttributes.CITY_ATTRIBUTE,
                IPersonAttributes.ZIPCODE_ATTRIBUTE,
                IPersonAttributes.OFFICE_PHONE_ATTRIBUTE,
                IPersonAttributes.FAX_PHONE_ATTRIBUTE,
                IPersonAttributes.MOBILE_PHONE_ATTRIBUTE,
                IPersonAttributes.PAGER_PHONE_ATTRIBUTE,
                IPersonAttributes.PAGER_EMAIL_ATTRIBUTE
            }
        );


    /** 
     * The list of required profile mappings.
     * Each element is a string profile attribute id; each one must be listed
     * in the <code>SUPPORTED_ATTRIBUTE_IDs</code> list.
     */
    private static final List REQUIRED_MAPPINGS_BY_ATTRIBUTE_ID = 
        java.util.Arrays.asList(
            new String[] {
                net.project.resource.IPersonAttributes.EMAIL_ATTRIBUTE
            }
        );

    /**
     * Returns the list of attributeIDs supported by this map.
     * @return the list of attributeIDs where each element is a String
     * representing a profile attribute ID.
     */
    public static List getSupportedAttributeIDs() {
        return LDAPAttributeMap.SUPPORTED_ATTRIBUTE_IDS;
    }

    /**
     * Returns the list of attributeIDs that must be mapped.
     * @return the list of attributeIDs where each element is a String
     * representing a profile attribute ID.
     */
    public static List getRequiredMappedAttributeIDs() {
        return LDAPAttributeMap.REQUIRED_MAPPINGS_BY_ATTRIBUTE_ID;
    }


    //
    // Instance members
    //


    /**
     * The id of the Domain against which this LDAPAttributeMap
     * is stored.
     */
    private String domainID = null;

    /**
     * The supported attribute collection.
     * Since these values are ordered, they may be considered an
     * index into this hashmap.
     */
    private AttributeCollection supportedAttributes = null;

    /**
     * Creates an empty LDAPAttributeMap.
     */
    public LDAPAttributeMap() {
        super();
        this.supportedAttributes = getSupportedAttributes();
    }

    /**
     * Sets the domainID of the domain to which this attribute
     * map belongs.
     * @param domainID the id of the Domain; this may be any value
     * that conforms the the standard persistent store object id
     * @see #getID
     */
    public void setDomainID(String domainID ) {
        this.domainID  = domainID ;
    }

    /**
     * Returns the domainID of the domain to which this attribute
     * map belongs.
     * @return the the domainID of the domain
     * @see #setID
     */
    protected String getDomainID() {
        return this.domainID;
    }

    
    /**
     * Equivalent to calling <code>put((IAttribute) key, (LDAPAttributeDefinition) value)</code>.
     * @throws ClassCastException if key is not an IAttribute or
     * value is not an LDAPAttribute
     * @see java.util.HashMap#put
     */
    public Object put(Object key, Object value) {
        return put((IAttribute) key, (LDAPAttributeDefinition) value);

    }

    /**
     * Maps the specified attribute to the specified LDAP Attribute.
     * @param attribute the profile attribute to which the LDAP attribute pertains
     * @param ldapAttributeDefinition the LDAP attribute definition 
     * that corresponds to the specified profile attribute
     * @return the LDAPAttributeDefinition replaced by the specified ldapAttributeDefinition
     */
    public LDAPAttributeDefinition put(IAttribute attribute, LDAPAttributeDefinition ldapAttributeDefinition) {
        return (LDAPAttributeDefinition) super.put(attribute, ldapAttributeDefinition);
    }

    /**
     * Convenience method to get the mapped LDAP attribute name for
     * the specified profile attribute id.
     * @param profileAttributeID the id of the profile attribute to
     * get
     * @return the name of the LDAP attribute mapped to the specified
     * profile attribute id; or null if no mapping was found
     */
    public String getLDAPAttributeName(String profileAttributeID) {
        
        String name = null;

        LDAPAttributeDefinition ldapAttribute = (LDAPAttributeDefinition) get(new Person().getAttributes().getAttributeByID(profileAttributeID));
        
        if (ldapAttribute != null) {
            name = ldapAttribute.getName();
        }
        
        return name;
    }

    /**
     * Returns all the keys in this map in a defined order.
     * This is much like <code>keySet()</code> but the elements
     * are ordered.
     * @return the ordered keys
     */
    public List keyList() {
        // Currently the supported attributes are synonymous with
        // the keys in this hashMap
        return getSupportedAttributes();
    }

    /**
     * Returns the collection attributes supported by this map.
     * @return the attributes supported by this map
     */
    private AttributeCollection getSupportedAttributes() {

        if (this.supportedAttributes == null) {
            this.supportedAttributes = buildSupportedAttributes();
        }

        return this.supportedAttributes;
    }


    /**
     * Builds a collection of supported Attributes.
     * @return the attributes supported by this map
     */
    private AttributeCollection buildSupportedAttributes() {
        
        AttributeCollection supportedAttributes = new AttributeCollection(LDAPAttributeMap.getSupportedAttributeIDs().size());

        // Iterate over all the attributes in a person, adding
        // supported attributes to the appropriate collection
        AttributeCollection personAttributes = new Person().getAttributes();
        for (Iterator it = personAttributes.iterator(); it.hasNext(); ) {
            IAttribute nextAttribute = (IAttribute) it.next();

            if (LDAPAttributeMap.getSupportedAttributeIDs().contains(nextAttribute.getID())) {
                supportedAttributes.add(nextAttribute);
            }

        }

        return supportedAttributes;
    }


    /**
     * Loads the attribute map for the current contextID.
     * This map is cleared before attempting to load.
     * <p>
     * After loading, this map will contain the same number of keys
     * as attributes supported by the mapping process.  If no
     * map had been stored in the database, then all values will represent
     * empty LDAPAttributeDefinitions (but will never be null).
     * </p>
     * @throws PersistenceException if there is a problem loading
     * @throws NullPointerException if the current contextID is null
     */
    public void load() throws PersistenceException {

        if (getDomainID() == null) {
            throw new NullPointerException("contextID is null");
        }

        StringBuffer query = new StringBuffer();
        query.append("select am.context_id, am.attribute_id, am.ldap_attribute_name, ");
        query.append("am.ldap_attribute_value_index ");
        query.append("from pn_ldap_directory_attr_map am ");
        query.append("where context_id = ? ");


        DBBean db = new DBBean();
        
        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, getDomainID());
            db.executePrepared();

            // Grab all attributes in results
            // We store them in a temporary loadedAttributeMap
            // This is because the stored mappings may be incomplete
            // (perhaps no mapping has been stored yet)
            // or there are erroneous mappings (perhaps we have desupported
            // a particular profile attribute)
            HashMap loadedAttributeMap = new HashMap();

            while (db.result.next()) {
                
                String attributeID = db.result.getString("attribute_id");
                IAttribute attribute = getSupportedAttributes().getAttributeByID(attributeID);
                String attributeName = db.result.getString("ldap_attribute_name");
                String valueIndex = db.result.getString("ldap_attribute_value_index");
                
                // If we support the attribute, we add it to the loaded map
                if (attribute != null) {
                    
                    LDAPAttributeDefinition ldapAttributeDefinition = null;

                    // Appropriate constructor depends on whether a value index
                    // is stored in database
                    if (valueIndex == null || valueIndex.trim().length() == 0) {
                        ldapAttributeDefinition = new LDAPAttributeDefinition(attributeName);

                    } else {
                        ldapAttributeDefinition = new LDAPAttributeDefinition(attributeName, Integer.valueOf(valueIndex).intValue());

                    }
                    
                    loadedAttributeMap.put(attribute, ldapAttributeDefinition);

                }

            }

            // Now construct this map which consists of all keys
            // and whichever LDAP attributes that have been mapped
            
            // Clear out existing keys and entries
            clear();

            // Iterate over all supported attributes, building this
            // map with the previous loaded mappings
            for (Iterator it = getSupportedAttributes().iterator(); it.hasNext(); ) {
                
                IAttribute nextSupportedAttribute = (IAttribute) it.next();
                LDAPAttributeDefinition ldapAttributeDefinition = 
                    (LDAPAttributeDefinition) loadedAttributeMap.get(nextSupportedAttribute);

                // If we don't have a mapping for one of the supported
                // attributes, then populate the map with an empty
                // LDAP attribute definition
                if (ldapAttributeDefinition == null) {
                    ldapAttributeDefinition = new LDAPAttributeDefinition("");
                }

                put(nextSupportedAttribute, ldapAttributeDefinition);

            }

            // At this point, the current map is defined as follows:
            // Each key is a supported profile attribute
            // each value is an LDAPAttributeDefinition, some of
            // which may be empty

        
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("Attribute map load operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Stores this LDAPAttributeMap.
     * @throws net.project.persistence.PersistenceException if there
     * is a problem storing
     * @see #store(DBBean)
     */
    public void store() throws net.project.persistence.PersistenceException {

        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);
            store(db);
            db.commit();
        
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("LDAP Attribute Map store operation failed: " + sqle, sqle);

        } finally {
            
            try {
                db.rollback();
            } catch (java.sql.SQLException sqle) {
                // Simply release
            }

            db.release();
        }

    }

    /**
     * Stores this attribute map using the specified DBBean.
     * Does NOT commit or rollback.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem storing
     * @throws NullPointerException if the current contextID is null
     */
    public void store(DBBean db) throws PersistenceException {

        if (getDomainID() == null) {
            throw new NullPointerException("DomainID is null");
        }

        StringBuffer deleteQuery = new StringBuffer();
        deleteQuery.append("delete from pn_ldap_directory_attr_map where context_id = ? ");

        StringBuffer insertQuery = new StringBuffer();
        insertQuery.append("insert into pn_ldap_directory_attr_map ");
        insertQuery.append("(context_id, attribute_id, ldap_attribute_name, ldap_attribute_value_index) ");
        insertQuery.append("values (?, ?, ?, ?) ");

        try {

            // First delete all existing mappings for current context id
            int index = 0;
            db.prepareStatement(deleteQuery.toString());
            db.pstmt.setString(++index, getDomainID());
            db.executePrepared();

            // Now insert all map entries
            db.prepareStatement(insertQuery.toString());
            
            for (Iterator it = keySet().iterator(); it.hasNext(); ) {
                IAttribute nextAttribute = (IAttribute) it.next();
                LDAPAttributeDefinition ldapAttributeDefinition = (LDAPAttributeDefinition) get(nextAttribute);

                // Only add information if the definition has been defined
                // This means we store only a subset of definitions
                if (ldapAttributeDefinition.isDefined()) {
                    index = 0;
                    db.pstmt.setString(++index, getDomainID());
                    db.pstmt.setString(++index, nextAttribute.getID());
                    db.pstmt.setString(++index, ldapAttributeDefinition.getName());
                    if (ldapAttributeDefinition.isValueIndexSpecified()) {
                        db.pstmt.setInt(++index, ldapAttributeDefinition.getValueIndex());
                    } else {
                        db.pstmt.setNull(++index, java.sql.Types.INTEGER);
                    }
                    db.pstmt.addBatch();
                
                }

            }

            db.executePreparedBatch();

        
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("Attribute map store operation failed: " + sqle, sqle);

        }

    }

    public void remove() throws net.project.persistence.PersistenceException {
        throw new net.project.persistence.PersistenceException("Remove operation not supported");
    }



    //
    // Nested top-level classes
    //

    /**
     * Represents an LDAP attribute name whose value to get.
     * An LDAPAttributeDefinition has a name and a value index where
     * that index describes which value from a multi-value
     * attribute to use
     */
    public static class LDAPAttributeDefinition {
    
        private String name = null;

        private int valueIndex = 0;

        private boolean isValueIndexSpecified = false;
    
        /**
         * Creates a new LDAPAttributeDefinition with the specified name.
         * Assumes the attribute is not multi-valued; if it is then
         * a random value is used
         * @param name the name
         */
        public LDAPAttributeDefinition(String name) {
            this.name = name;
            this.isValueIndexSpecified = false;
        }
    
        /**
         * Creates a new LDAPAttributeDefinition with the specified name and
         * value index.
         * @param name the name; for example <code>cn</code>
         * @param valueIndex the index number of the value to get
         * from this multi-valued attribute; values start at <code>0</code>
         */
        public LDAPAttributeDefinition(String name, int valueIndex) {
            this.name = name;
            this.valueIndex = valueIndex;
            this.isValueIndexSpecified = true;
        }

        /**
         * Indicates whether this ldap attribute definition is actually
         * specified.
         * An ldap attribute definition is defined only if its name
         * is specified.
         * @return true if this ldap attribute definition has a name;
         * false otherwise
         */
        public boolean isDefined() {
            return (getName() != null && getName().trim().length() > 0);
        }

        public void setName(String name) {
            this.name = name;
        }

        /**
         * Returns the name of this ldap attribute definition.
         * @return name
         */
        public String getName() {
            return this.name;
        }

        public void setValueIndexSpecified(boolean isValueIndexSpecified) {
            this.isValueIndexSpecified = isValueIndexSpecified;
        }

        /**
         * Indicates whether a value index has been specified
         * for this attribute.
         * @return true if a value index has been specified; false
         * otherwise
         */
        public boolean isValueIndexSpecified() {
            return this.isValueIndexSpecified;
        }

        public void setValueIndex(int valueIndex) {
            this.valueIndex = valueIndex;
        }

        /**
         * Returns the index of the value to use for a multi-valued
         * attribute.
         * @return the value index or <code>0</code> if none is
         * specified for this LDAP attribute
         */
        public int getValueIndex() {
            return this.valueIndex;
        }
    
    }

}
