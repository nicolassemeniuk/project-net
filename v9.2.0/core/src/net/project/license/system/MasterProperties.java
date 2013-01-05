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
package net.project.license.system;

import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.database.Clob;
import net.project.database.DBBean;
import net.project.license.LicenseException;
import net.project.persistence.NoDataFoundException;
import net.project.persistence.PersistenceException;
import net.project.security.EncryptionException;

/**
 * Provides the master set of License properties.  These are persisted in an
 * encrypted form to prevent modification.
 *
 * @author Tim Morrow
 * @since Gecko Update 2
 */
public class MasterProperties implements net.project.persistence.IXMLPersistence {

    //
    // Static members
    //

    /**
     * The ID of the CLOB row that contains the properties.
     * Currently we have only one row of properties and our Clob object requires
     * an ID column.
     */
    private static final String MASTER_PROPERTIES_ID = "100";

    public static boolean masterPropertiesExist() {
        boolean exist = false;
        try {
            MasterProperties.getInstance();
            exist = true;
        } catch(Exception e) {}

        return exist;
    }

    /**
     * Returns the loaded master properties.
     * @return the master properties (which may be empty if none are found)
     * @throws PersistenceException if there is a problem loading the master
     * properties (but not if there are none)
     * @throws LicenseException if there are the license master properties
     */
    public static MasterProperties getInstance() throws
        MasterPropertiesNotFoundException, LicenseException, PersistenceException {

        MasterProperties props = new MasterProperties();

        try {
            props.load();
        } catch (NoDataFoundException e) {
            // No license master properties found, say it aloud

            //"No License Master Properties were found installed on this system.\n
            //Please contact your Application Administrator or to Project.net to get
            //a new set of Master License Properties.\n"
            throw new MasterPropertiesNotFoundException(PropertyProvider.get("prm.license.masterproperties.getinstance.load.notfoundexception.message"));
        } catch (PersistenceException pe) {
            // Problem loading license master properties, say it aloud
            throw new PersistenceException(pe.getMessage(),pe);
        }

        return props;
    }

    //
    // Instance members
    //

    /**
     * The master properties; defaults to {@link PropertyCollection#EMPTY_COLLECTION}.
     */
    private PropertyCollection properties = PropertyCollection.EMPTY_COLLECTION;

    /**
     * Creates a new MasterProperties containing an empty collection of properties.
     */
    public MasterProperties() {
        // Do nothing
    }

    /**
     * Get an individual property from the master properties file.
     *
     * @param name a <code>PropertyName</code> object which points to the type
     * of property we wish to find in the property file.
     * @return a <code>Property</code> which corresponds to the {@see #name}
     * parameter.
     */
    public Property get(PropertyName name) {
        return this.properties.get(name);
    }

    /**
     * Updates the master properties from the specified properties.
     * New properties are added; existing properties are replaced by the new
     * property; properties missing from the specified properties are deleted
     */
    void update(PropertyCollection newProperties) {
        // Make defensive copy of mutable parameter to ensure properties cannot
        // be updated from outside this class
        this.properties = new PropertyCollection(newProperties);
    }

    /**
     * Returns a read-only summary of the currently loaded or specified master
     * properties.
     * @return a text summary
     */
    String getSummary() {

        StringBuffer summary = new StringBuffer();

        for (Iterator it = this.properties.iterator(); it.hasNext();) {
            Property nextProperty = (Property)it.next();

            summary.append(nextProperty.getName().toString())
                .append(" = ")
                .append(nextProperty.getValue().toString())
                .append("\n");

        }

        return summary.toString();
    }

    /**
     * Loads the master properties.
     * @throws NoDataFoundException if no properties data is found
     * @throws PersistenceException if there is a problem loading; this
     * includes problems decrypting the master properties or parsing the XML
     */
    private void load() throws NoDataFoundException, PersistenceException {

        DBBean db = new DBBean();
        try {
            Clob clob = new Clob(db);
            clob.setTableName("pn_license_master_prop_clob");
            clob.setIDColumnName("master_prop_id");
            clob.setDataColumnName("master_prop_lob_data");
            clob.setID(MASTER_PROPERTIES_ID);
            clob.loadReadOnly();

            // Decrypt XML
            String encryptedPropertiesXML = clob.getData();
            String propertiesXML = decrypt(encryptedPropertiesXML);

            // Create properties collection from XML
            this.properties = PropertyCollection.unmarshal(propertiesXML);

        } catch (net.project.security.EncryptionException e) {
        	//passing exception during decrypt operation
            throw new PersistenceException(e.getMessage(), e);

        } catch (net.project.xml.XMLException e) {
            throw new PersistenceException("Master properties load operation failed: " + e, e);

        } finally {
            db.release();
        }
    }

    /**
     * Stores the properties.
     * Deletes existing properties then inserts new properties.
     * Properties are stored in an encrypted format in a single row.
     */
    void store() throws PersistenceException {

        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);

            Clob clob = new Clob(db);
            clob.setTableName("pn_license_master_prop_clob");
            clob.setIDColumnName("master_prop_id");
            clob.setDataColumnName("master_prop_lob_data");

            // First delete the existing properties row
            clob.setID(MASTER_PROPERTIES_ID);
            clob.remove();

            // Now insert the new properties
            // Generate the encrypted XML
            StringBuffer propertiesXML = new StringBuffer();
            this.properties.marshal(propertiesXML);
            String encryptedPropertiesXML = encrypt(propertiesXML.toString());

            // Store it
            clob.clear();
            clob.setID(MASTER_PROPERTIES_ID);
            clob.setData(encryptedPropertiesXML);
            clob.store();

            db.commit();

        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("License properties store operation failed: " + sqle, sqle);

        } catch (net.project.security.EncryptionException e) {
            // Problem encrypting properties
            throw new PersistenceException("License properties store operation failed: " + e, e);

        } finally {
            try {
                db.rollback();
            } catch (java.sql.SQLException sqle) {
                // Continue to release
            }
            db.release();

        }

    }

    public String getXML() {
        return getXMLDocument().getXMLBodyString();
    }

    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        try {
            doc.startElement("MasterProperties");
            doc.addElement("ID", MASTER_PROPERTIES_ID);
            doc.addElement(this.properties.getXMLDocument());
            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Empty document

        } catch (Exception e) {
            System.out.println("MasterProperties.java : Exception thrown : " + e.getMessage());
        }

        return doc;
    }

    /**
     * Encrypts the source using the license security key.
     * @param source the source to encrypt
     * @throws EncryptionException if there is a problem encrypting
     * @see net.project.security.EncryptionManager#encryptBlowfish
     */
    private static String encrypt(String source) throws EncryptionException {
        return net.project.security.EncryptionManager.encryptBlowfish(
            source, net.project.security.crypto.SecretKeyType.LICENSE_CERTIFICATE);
    }

    /**
     * Decrypts the source using the license security key.
     * @param source the encrypted source to decrypt
     * @throws EncryptionException if there is a problem decrypting
     * @see net.project.security.EncryptionManager#decryptBlowfish
     */
    private static String decrypt(String source) throws EncryptionException {
        return net.project.security.EncryptionManager.decryptBlowfish(
            source, net.project.security.crypto.SecretKeyType.LICENSE_CERTIFICATE);
    }

}
