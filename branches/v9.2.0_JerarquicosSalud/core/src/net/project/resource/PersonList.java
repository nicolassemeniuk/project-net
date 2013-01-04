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

 package net.project.resource;

import java.util.ArrayList;

import net.project.base.DirectoryFilter;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLFormatter;

/**  
 * A list of People.  
 * @see java.util.ArrayList    
 */
public class PersonList extends ArrayList implements IXMLPersistence {
    /** Contains XML formatting information and utilities specific to this object **/
    protected XMLFormatter m_formatter = new XMLFormatter();
    /** DBBean is the encapsulation of database access **/
    protected DBBean m_db = new DBBean();
    /** Check to see if Person information has already been loaded from the database **/
    protected boolean m_isLoaded = false;
    /** DirectoryFilter object */
    private DirectoryFilter filter = new DirectoryFilter();
    /** Stores sort order (actually stored the ORACLE order query string) */
    private String sortOrder = null;

    /** 
     * Construct an empty roster  Automaticly grows as needed.
     */
    public PersonList(){
       super();
    }

    /** 
     * Construct a PersonList of specified size.  Automaticly grows as needed.
     */
    public PersonList(int size){
        super(size);
    }

    /**
     * Returns whether the person information from the database been loaded.
     */
     public boolean isLoaded() {
         return this.m_isLoaded;
     }

     /** 
      * Set's the isLoaded flag
      *
      */
     public void setIsLoaded (boolean loaded) {
         this.m_isLoaded = loaded;
     }

    /**
     * Get the filter being applied to this directory.  A directory filter can
     * filter a list of users in a variety of ways, including license status and
     * by keyword.
     *
     * @return a <code>DirectoryFilter</code> object being applied to this
     * person list.
     */
    public DirectoryFilter getDirectoryFilter() {
        return this.filter;
    }

     /**
      * Set an filter into the object for a list of user statuses
      * 
      * @param userStatusFilter
      */
     public void setUserStatusFilter (String[] userStatusFilter) {
         this.filter.setUserStatusFilter(userStatusFilter);
     }
     
     /**
      * Set a filter into the object for a list of user statuses
      * 
      * @return a <code>String[]</code> value containing a list of license
      * filters that are being applied to this person list.
      */
    public String[] getUserStatusFilter() {
        return this.filter.getUserStatusFilter();
    }

    /**
     * Set a filter into the object for a list of userDomains to load the user by
     * 
     * @param userDomainFilter
     */
     public void setUserDomainFilter (String[] userDomainFilter) {
         this.filter.setUserDomainFilter(userDomainFilter);
     }

    /**
     * Get the list of userDomains to filter by
     * 
     */
     public String[] getUserDomainFilter () {
         return this.filter.getUserDomainFilter();
     }

    /**
     * Set the type of licenses that we'd like to see in a person list.  
     */
    public void setLicenseFilter(String[] licenseFilter) {
        this.filter.setLicenseTypeFilter(licenseFilter);
    }

    /**
     *
     */
    public String[] getLicenseFilter() {
        return this.filter.getLicenseTypeFilter();
    }

    /**
     * Set the loading filter
     * 
     * @param filter
     */
    public void setFilter (String filter) {
        this.filter.setKeywordFilter(filter);;
    }

    /**
     * Return the filter
     * 
     * @return 
     */
    public String getFilter() {
        return this.filter.getKeywordFilter();
    }


    public void setSortOrder (String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortOrder() {
        return this.sortOrder;
    }

     /**
      * Load all persons in the database into this data structure.
      */
     public void load() throws PersistenceException {

         // first clear any values in the array list
         super.clear();

         try {
             net.project.base.DefaultDirectory.loadAllPeople (this, m_db);
         }

         catch (PersistenceException pe) {
             throw new PersistenceException ("PersonList.load() threw a persistence exception: ", pe);
         }

         finally {
             m_db.release();
         }
     }

     /**
      * Load a filtered set of all people in the directory
      * Delegates processing to the Directory
      */
     public void loadFiltered() throws PersistenceException {
         loadFiltered (getDirectoryFilter(), getSortOrder());
     }

     /**
      * Load a filtered set of all people in the directory
      * Delegates processing to the Directory
      *
      * @param filter the populated directoryFilter to filter with.
      */
     public void loadFiltered (DirectoryFilter filter, String sortOrder) throws PersistenceException {

         // first clear any values in the array list
         super.clear();

         try {
             net.project.base.DefaultDirectory.loadFilteredPeople (this, filter, sortOrder, this.m_db);
         }

         catch (PersistenceException pe) {
             throw new PersistenceException ("PersonList.load() threw a persistence exception: ", pe);
         }

         finally {
             m_db.release();
         }
     }





     /**
      * Converts the object to XML representation.  This method returns the
      * object as XML text.
      *
      * @return XML representation of this object
      */
     public String getXML() {
         return IXMLPersistence.XML_VERSION + getXMLBody();
     }


     /**
      * Returns the XML representation of the PersonList object.
      *
      * @return the XML body
      */
     public String getXMLBody() {
         StringBuffer xml = new StringBuffer();

         xml.append("<person_list>\n");

         for (int i=0; i<this.size(); i++)
             xml.append( ((Person)this.get(i)).getXMLBody() );

         xml.append("</person_list>\n");

         return xml.toString();
     }


     /**
      * Gets the presentation of the component.  This method will apply the
      * stylesheet to the XML representation of the component and return the
      * resulting text.
      *
      * @return presetation of the component
      */
     public String getPresentation(){
         return m_formatter.getPresentation(getXML());
     }


     /**
      * Sets the stylesheet file name used to render this component.
      * This method accepts the name of the stylesheet used to convert the XML representation of the component
      * to a presentation form.
      *
      * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
      */
     public void setStylesheet(String styleSheetFileName){
         m_formatter.setStylesheet(styleSheetFileName);
     }

     /**
      * Clean out this object
      */
     public void clear() {
         super.clear();
         this.filter.clear();
         this.sortOrder = null;
     }
}

