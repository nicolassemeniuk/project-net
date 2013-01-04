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
package net.project.brand;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.persistence.PersistenceException;
import net.project.resource.Language;
import net.project.util.ParseString;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Provides a brand which identifies a unique brand used by the branding services.
 * <p>
 * A brand is generally managed by a configuration.  Each configuration has
 * one brand and a brand is (usually) managed by a single configuration.
 * </p>
 * <p>
 * The Brand provides methods for loading and storing a brand and its properties,
 * including name, supported hostnames, supported languages etc.
 * </p>
 * @since Version 2.0
 */
public class Brand implements java.io.Serializable {

    /** the system default brand */
    private static final String SYSTEM_DEFAULT_BRAND = "pnet";

    /** if the default brand does not have a supported language specified, the default is english */
    public static final String SYSTEM_DEFAULT_LANGUAGE = "en";

    /** the database ID of the brand */
    private String brandID = null;

    /** the database ID of the configuration associated with this brand */
    private String configurationID = null;

    /** the hostname for the brand URL */
    private ArrayList supportedHostnames = new ArrayList();

    /** the abbreviation of the brand name. */
    private String abbreviation = null;

    /** the full name of the brand */
    private String name = null;

    /** long description of the brand */
    private String description = null;

    /** The default language for the brand */
    private String defaultLanguage = null;

    /** List of languages supported by this brand */
    private ArrayList supportedLanguages = new ArrayList();


    /** The default system brand */
    private Brand systemDefaultBrand = null;

    /** Language the user requests */
    private String requestedLanguage = null;

    /** Language the brand will actually deliver based on availabilty.  May equal reqested language */
    private String activeLanguage = null;

    private boolean isLoaded = false;

    /**
     * Creates an empty brand.
     */
    public Brand() {
        // Do nothing
    }

    /**
     * Copies this brand and returns the new copy.  This does NOT copy
     * the brand tokens.
     * @return the new brand
     */
    public Brand copy() {
        Brand newBrand = new Brand();

        newBrand.setName(getName());
        newBrand.setDescription(getDescription());
        newBrand.setAbbreviation(getAbbreviation());
        newBrand.setDefaultLanguage(getDefaultLanguage());
        newBrand.setSupportedLanguages(getSupportedLanguages());
        newBrand.setSupportedHostnames(getSupportedHostnamesCSV());

        return newBrand;
    }

    /**
     * Sets the brand description.  If this is set to null, then description
     * reverts to brand name.
     */
    public void setDescription(String description) {
        this.description = description;
        // Ensure mandatory description field has value.
        // Description is now mandatory in database to avoid side effects
        // of it being null (when brand is loaded).  However, since description
        // is no longer entered in interface it must be populated here.
        if (this.description == null || this.description.length() == 0) {
            this.description = getName();
        }
    }

    /**
     * Returns the description of this brand.
     *
     * @return the description
     */
    private String getDescription() {
        return this.description;
    }

    /**
     * Specifies the default language for this brand.
     * The default language is used when looking up properties for a brand.
     * @param lang the default language
     * @see #getDefaultLanguage
     */
    public void setDefaultLanguage(String lang) {
        this.defaultLanguage = lang;
    }

    /**
     * Returns this brand's default language.
     * @return the default language for the brand.
     * @see #setDefaultLanguage
     */
    public String getDefaultLanguage() {
        return this.defaultLanguage;
    }

    /**
     * Returns the display name for this brand's default language.
     * @return the display name or null if the default language is null
     */
    public String getDefaultLanguageName() {
        if (this.defaultLanguage != null) {
            return Language.getLanguages().getLanguageForCode(this.defaultLanguage).getLanguageName();
        } else {
            return null;
        }
    }

    /**
     * Sets the brand's database ID before loading.
     * @param brandID the ID of the brand
     */
    public void setID(String brandID) {
        this.brandID = brandID;
    }

    /**
     * Get the brand's database ID.
     */
    public String getID() {
        return this.brandID;
    }

    /** Get the brand's configuration ID */
    public String getConfigurationID() {
        return this.configurationID;
    }

    /** Set the brand's abbreviation.
     @param abbreviation a unique short identifier of a brand.
     */
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     Get the brand's abbreviation.
     @return a unique short identifier of a brand.
     */
    public String getAbbreviation() {
        return this.abbreviation;
    }

    /**
     * Sets the language codes supported by this brand. Clears out any existing
     * language codes.  Therefore only the language codes specified here
     * will be stored.  All others will be removed.
     * @param languages the array of lanugage codes
     */
    public void setSupportedLanguagesArray(String[] languages) {
        setSupportedLanguages(Arrays.asList(languages));
    }

    /**
     * Adds a supported language to this brand.
     *
     * @param language the language code to add
     */
    public void addLanguage(String language) {
        this.supportedLanguages.add(language);
    }

    /**
     * Replaces the current supported languages with the specified list of
     * supported languages.
     *
     * @param supportedLanguages the collection of supported languages where
     * each element is a String representing the language code
     */
    private void setSupportedLanguages(Collection supportedLanguages) {
        this.supportedLanguages.clear();
        this.supportedLanguages.addAll(supportedLanguages);
    }

    /**
     * Get the language code for the brand property names.
     * @return a valid ISO Language Code. These codes are the lower-case two-letter codes as defined by ISO-639.
     * You can find a full list of these codes at a number of sites, such as: http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt
     */
    public ArrayList getSupportedLanguages() {
        return this.supportedLanguages;
    }

    /**
     * Sets the supported hostnames from the specified collection.
     * Replaces all supported hostnames with those in the collection.
     * @param hosts the collection where each element is a String hostname
     */
    private void setSupportedHostnames(Collection hosts) {
        supportedHostnames.clear();
        supportedHostnames.addAll(hosts);
    }

    /**
     * Sets the supported hostname from a comma-separated list of hostnames.
     * Replaces all supported hostnames with those in the comma-separated list.
     * @param hosts the comma separated list of hostnames
     */
    public void setSupportedHostnames(String hosts) {
        String[] list = ParseString.makeArrayFromCommaDelimitedString(hosts);
        setSupportedHostnames(Arrays.asList(list));
    }

    /**
     * Returns a comma separated list of hostnames supported by this brand.
     * @return the comma-separated list of hostnames or the empty string if
     * the supported hostnames are empty
     */
    public String getSupportedHostnamesCSV() {
        StringBuffer sb = new StringBuffer();
        String hostname = null;

        if (supportedHostnames != null) {

            Iterator it = supportedHostnames.iterator();
            while (it.hasNext()) {
                hostname = (String) it.next();
                sb.append(hostname);
                if (it.hasNext()) {
                    sb.append(",");
                }
            }
        }

        return sb.toString();
    }

    /**
     * Returns XML for supported languages including version tag.
     * @return XML string
     * @see #getSupportedLanguagesXMLBody()
     */
    public String getSupportedLanguagesXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION +
                getSupportedLanguagesXMLBody();
    }

    /**
     * Returns XML data of language indicating which are supported.
     * Returns all languages, indicating which are supported by context.<br>
     * For example:
     * <pre>
     * <SupportedLanguages>
     *     <Language>
     *         <LanguageCode>en</LanguageCode>
     *         <LanguageName>English (American)</LanguageName>
     *         <CharacterSet>ISO8859_1</CharacterSet>
     *         <IsSupported>1</IsSupported>
     *     </Language>
     * </SupportedLanguages>
     * </pre>
     * @return XML string
     */
    private String getSupportedLanguagesXMLBody() {
        Language language = null;
        StringBuffer xml = new StringBuffer();

        xml.append("<SupportedLanguages>");

        Iterator it = Language.getLanguages().iterator();
        while (it.hasNext()) {
            language = (Language) it.next();

            xml.append("<Language>");
            xml.append(language.getXMLElements());
            xml.append("<IsSupported>" + XMLUtils.escape((supportsLanguage(language.getLanguageCode()) ? "1" : "0")) + "</IsSupported>");
            xml.append("</Language>");

        }

        xml.append("</SupportedLanguages>");

        return xml.toString();
    }

    /**
     * Indicates whether this brand is the system default brand.
     * A brand is the system default brand if its abbreviation matches that
     * of {@link #SYSTEM_DEFAULT_BRAND}.
     * @return true if this brand is the system default brand; false otherwise
     */
    public boolean isDefaultBrand() {
        return (SYSTEM_DEFAULT_BRAND.equals(getAbbreviation())) ? true : false;
    }

    /**
     * Indicates whether this brand has been loaded.
     * @return true if this brand has been loaded; false otherwise
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    /**
     * Sets the language code for the brand property names.
     * If the language is not supported by this brand, then the active language is
     * set to the default language and the parameter is ignored.
     * After calling, the active language is available by {@link #getActiveLanguage}.
     *
     * @param language a valid ISO Language Code;
     * These codes are the lower-case two-letter codes as defined by ISO-639.
     * You can find a full list of these codes at a number of sites,
     * such as: http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt
     * @see #getRequestedLanguage
     */
    public void setRequestedLanguage(String language) {
        this.requestedLanguage = language;

        // if the brand is loaded, set the active language to the requested languge, if it is supported
        // otherwise the load languages method will set this.

        if (this.isLoaded) {

            if (this.supportsLanguage(language))
                this.activeLanguage = language;
            else
                this.activeLanguage = getDefaultLanguage();

        } // end if loaded

    }


    /**
     * Returns the requested language.
     * This is always the value specified by {@link #setRequestedLanguage}
     * even if the requested language is not supported.
     * @return the requested language
     * @see #setRequestedLanguage
     */
    public String getRequestedLanguage() {
        return this.requestedLanguage;
    }


    /**
     * Returns the active brand language.
     * This method guarantees that the active language returned is supported by the brand.
     * It will be the requested language if the requested language is supported
     * by this brand.  Otherwise it is the default language.
     * The guarantee is enforced in setRequestedLanguage() and loadSupportedLanguages();
     * @return the active language
     */
    public String getActiveLanguage() {
        return this.activeLanguage;
    }


    /**
     * Set the brand's name.
     * If the brand currently has no description, sets description to be
     * same as name.
     * @param name the brand's name.
     */
    public void setName(String name) {
        this.name = name;
        // Description has been made mandatory in Salesmode, hence we must
        // be sure to always have a description.
        // Brand description is effectively deprecated - the configuration space
        // or company description is displayed instead.
        if (getDescription() == null || getDescription().length() == 0) {
            setDescription(this.name);
        }
    }

    /**
     * Get the brand's name.
     * @return the display name of the brand
     */
    public String getName() {
        return this.name;
    }


    /**
     * Returns the system default brand, which may be this brand if this
     * is the system default brand.
     *
     * @return the system default brand
     */
    public Brand getSystemDefaultBrand() {
        Brand brand = null;

        if (this.isLoaded && this.systemDefaultBrand == null)
            brand = this;
        else
            brand = this.systemDefaultBrand;

        return brand;
    }

    /**
     * Returns the ID of the system default brand.
     *
     * @return the system default brand ID
     */
    public String getSystemDefaultBrandID() {
        return getSystemDefaultBrand().getID();
    }


    public void setDefaultBrand() {
        setAbbreviation(SYSTEM_DEFAULT_BRAND);
    }

    /**
     * Loads this brand which sets the system default brand.
     *
     * @throws PersistenceException
     * @throws IllegalStateException if both brandID and abbreviation are null
     */
    public void load() throws PersistenceException {

        loadProperties();

        // We always need the system default brand also.
        // Load the default brand also if it was not just loaded above.
        // TODO: this.abbreviation should not be null after loadProperties() is called above, but we have seen a NPE here after session expirations.
        if (this.abbreviation == null || !this.abbreviation.equals(SYSTEM_DEFAULT_BRAND))
            loadSystemDefaultBrand();

        this.isLoaded = true;
    }

    /**
     * Loads the properties of this brand from persistence.
     * Either the brandID or the abbreviation must be set.
     * The brandID takes precedence over the abbreviation.
     * @throws IllegalStateException if both brandID and abbreviation are null
     */
    private void loadProperties()
            throws PersistenceException {

        if (this.brandID == null && this.abbreviation == null) {
            throw new IllegalStateException("brandID and abbreviation are null");
        }

        String queryString = "select b.brand_id, b.brand_abbrv, b.brand_name, b.brand_desc, " +
                "b.default_language, c.configuration_id from pn_brand b, pn_configuration_space c ";

        if (this.brandID != null)
            queryString += "where b.brand_id=" + DBFormat.varchar2(this.brandID);
        else
            queryString += "where b.brand_abbrv=" + DBFormat.varchar2(this.abbreviation);

        queryString += " and c.brand_id = b.brand_id and b.record_status='A'";

        DBBean db = new DBBean();
        try {
            db.setQuery(queryString);

            if (db.stmt == null) {
                db.createStatement();
            }
            db.stmt.setFetchSize(2000);
            db.executeQuery();

            if (!db.result.next())
                throw new PersistenceException("Brand.loadProperties(): Brand not found in database.");

            this.brandID = db.result.getString("brand_id");
            this.abbreviation = db.result.getString("brand_abbrv");
            this.name = db.result.getString("brand_name");
            this.description = db.result.getString("brand_desc");
            this.defaultLanguage = db.result.getString("default_language");
            this.configurationID = db.result.getString("configuration_id");

            loadSupportedLanguages(this.brandID, db);
            loadSupportedHosts(this.brandID, db);

        } catch (SQLException sqle) {
        	Logger.getLogger(Brand.class).error("Brand.loadProperties() Unable to load brand from database.  This error often indicates the database could not be accessed.  Please check your database configuration or contract your system administrator. " + sqle);
            throw new PersistenceException("Brand.loadProperties(): Unable to load brand from database.  This error often indicates the database could not be accessed.  Please check your database configuration or contract your system administrator:  " + sqle, sqle);
        } finally {
            db.release();
        }

    }

    /**
     * Loads the system default brand.
     *
     * @throws PersistenceException
     */
    private void loadSystemDefaultBrand() throws PersistenceException {

        Brand brand = new Brand();

        brand.setAbbreviation(SYSTEM_DEFAULT_BRAND);
        brand.loadProperties();

        this.systemDefaultBrand = brand;

    }

    /**
     * Loads the supported languages for the specified brand ID.
     * The supported language are available by calling {@link #getSupportedLanguages}.
     * After calling, the active language is set to one of the following:
     * <ul>
     * <li>This brand's default language if a requested language has not been specified
     * <li>The requested language if this brand supports the requested language
     * <li>The system default language if this brand doesn't support the requested language
     * @param brandID
     * @throws SQLException
     */
    private void loadSupportedLanguages(String brandID, DBBean db) throws SQLException {

        String qstrLoadSupportedLanguages = "select language_code from pn_brand_supports_language " +
                "where brand_id = " + brandID;

        db.executeQuery(qstrLoadSupportedLanguages);

        while (db.result.next())
            this.supportedLanguages.add(db.result.getString("language_code"));

        if (this.requestedLanguage == null) {
            // No language specified so assume default language for brand
            this.activeLanguage = this.defaultLanguage;

        } else if (supportsLanguage(this.requestedLanguage)) {
            // supports requested language so use that
            this.activeLanguage = this.requestedLanguage;

        } else {
            // Specified requested language is not supported so we use the
            // system default language
            this.activeLanguage = SYSTEM_DEFAULT_LANGUAGE;
        }

    }


    private void loadSupportedHosts(String brandID, DBBean db) throws SQLException {

        String qstrLoadSupportedHosts = "select host_name from pn_brand_has_host " +
                "where brand_id = " + brandID;

        db.executeQuery(qstrLoadSupportedHosts);

        while (db.result.next())
            this.supportedHostnames.add(db.result.getString("host_name"));

    }

    private boolean supportsLanguage(String language) {

        Iterator list = this.supportedLanguages.iterator();
        boolean found = false;
        String lang = null;

        while (list.hasNext()) {

            lang = (String) list.next();

            if (lang.equals(language)) {
                found = true;
                break;
            }

        }
        return found;
    }


    private void storeSupportedLanguages(DBBean db) throws SQLException {

        Iterator list = this.supportedLanguages.iterator();

        String qstrDeleteExisting = "delete from pn_brand_supports_language where brand_id = " + this.brandID;
        String qstrStoreLanguages = "insert into pn_brand_supports_language (brand_id, language_code) " +
                "values (?,?)";

        // first delete existing
        db.executeQuery(qstrDeleteExisting);

        while (list.hasNext()) {

            String lang = (String) list.next();

            db.prepareStatement(qstrStoreLanguages);

            db.pstmt.setString(1, this.brandID);
            db.pstmt.setString(2, lang);

            db.executePrepared();
        }
    }

    private void storeSupportedHostnames(DBBean db) throws SQLException {

        Iterator list = this.supportedHostnames.iterator();

        String qstrDeleteExisting = "delete from pn_brand_has_host where brand_id = " + this.brandID;
        String qstrStoreLanguages = "insert into pn_brand_has_host (brand_id, host_name, record_status) " +
                "values (?,?,'A')";

        // first delete existing
        db.executeQuery(qstrDeleteExisting);

        while (list.hasNext()) {

            String host = (String) list.next();

            db.prepareStatement(qstrStoreLanguages);

            db.pstmt.setString(1, this.brandID);
            db.pstmt.setString(2, host);

            db.executePrepared();
        }

    }


    private void createBrand(DBBean db) throws SQLException {

        String qstrCreateBrand = "insert into pn_brand (brand_id, brand_abbrv, brand_name, " +
                "brand_desc, default_language, is_system_default, record_status) values (?,?,?,?,?,0,'A')";

        db.prepareStatement(qstrCreateBrand);

        String id = net.project.database.DatabaseUtils.getNextSequenceValue();

        db.pstmt.setString(1, id);
        db.pstmt.setString(2, this.abbreviation);
        db.pstmt.setString(3, this.name);
        db.pstmt.setString(4, this.description);
        db.pstmt.setString(5, this.defaultLanguage);

        db.executePrepared();

        this.setID(id);
    }


    private void updateBrand(DBBean db) throws SQLException {

        String qstrUpdateBrand = "update pn_brand set brand_abbrv=?, brand_name=?, " +
                "brand_desc=?, default_language=? where brand_id = ?";

        db.prepareStatement(qstrUpdateBrand);

        db.pstmt.setString(1, this.abbreviation);
        db.pstmt.setString(2, this.name);
        db.pstmt.setString(3, this.description);
        db.pstmt.setString(4, this.defaultLanguage);
        db.pstmt.setString(5, this.brandID);

        db.executePrepared();

    }

    /**
     * Stores the brand using the specified DBBean.<br>
     * <b>No COMMIT or ROLLBACK is performed</b><br>
     * Use this method for storing a brand as part of another transaction.
     * @param db the DBBean to user for the transaction
     * @throws SQLException if there is a problem storing.
     */
    public void store(DBBean db) throws SQLException {
        String qstrGetBrand = "select brand_id from pn_brand where brand_id = " + this.brandID;

        db.executeQuery(qstrGetBrand);

        // is this a new brand?
        if (!db.result.next())
            createBrand(db);
        else
            updateBrand(db);

        storeSupportedLanguages(db);
        storeSupportedHostnames(db);

    }

    /**
     * Store the brand to persistence.
     *
     * @throws PersistenceException if there is a problem storing the token in
     * the database.
     */
    public void store() throws PersistenceException {

        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            store(db);
            db.commit();
        } catch (SQLException sqle) {
        	Logger.getLogger(Brand.class).error("Brand.store failed " + sqle);
            throw new PersistenceException("Brand.store(), Error occured " +
                "storing brand: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
        xml.append(getXMLBody());

        return xml.toString();
    }

    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append("<brand>");
        xml.append("<name>" + this.name + "</name>");
        xml.append("<brand_id>" + this.brandID + "</brand_id>");
        xml.append("<description>" + this.description + "</description>");
        xml.append("<default_language>" + this.defaultLanguage + "</default_language>");
        xml.append("<active_language>" + this.getActiveLanguage() + "</active_language>");
        xml.append("<abbreviation>" + this.abbreviation + "</abbreviation>");
        xml.append("</brand>");

        return xml.toString();

    }


    public void clear() {

        brandID = null;
        supportedHostnames = new ArrayList();
        abbreviation = null;
        name = null;
        description = null;
        defaultLanguage = null;
        supportedLanguages = new ArrayList();
        systemDefaultBrand = null;
        requestedLanguage = null;
        activeLanguage = null;
        isLoaded = false;

    }

}
