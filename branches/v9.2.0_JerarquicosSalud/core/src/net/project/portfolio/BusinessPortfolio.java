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

 package net.project.portfolio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceFinder;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.ISpaceListLoadable;
import net.project.space.Space;
import net.project.space.SpaceList;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;


/**
 * A portfolio of Business Spaces. Each entry in this portfolio is a <code>BusinessSpace</code>.
 */
public class BusinessPortfolio extends Portfolio implements IJDBCPersistence, IXMLPersistence, ISpaceListLoadable, Serializable {

    /**
     * The current user context.
     */
    private User user = null;

    /**
     * Indicates whether the portfolio entries have been loaded.
     */
    private boolean entriesLoaded = false;

    /**
     * The optional SpaceList context for loading this portfolio.
     */
    private SpaceList spaceList = null;

    /**
     * The record status of business spaces to load.
     */
    private String recordStatus = "A";

    /**
     * Creates an empty BusinessPortfolio.
     */
    public BusinessPortfolio() {
        super();
        setContentType(net.project.space.ISpaceTypes.BUSINESS_SPACE);
    }

    /**
     * Specifies the current user context.
     *
     * @param user the current user context
     * @see #getUser
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the current user context.
     *
     * @return the current user context
     * @see #setUser
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Sets the space list context for loading a portfolio from a SpaceList. If a spaceList is set, it will take
     * precidence over setID() and the load() method will load the portfolio for the spaceList and not the porfolio id.
     *
     * @param spaceList the list of spaces
     * @see #load
     */
    public void setSpaceList(SpaceList spaceList) {
        this.spaceList = spaceList;
    }


    /**
     * Loads this portfolio.
     * <p/>
     * If a spaceList has been specified by {@link #setSpaceList} the portfolio entries are based on the contents of the
     * space list.  Otherwise, the businesses for the current user are loaded. Portfolio id is ignored. </p>
     * <p/>
     * If the space list is null or empty, or the user is null or its ID is null, no entries are loaded. </p>
     * <p/>
     * Note: Uses the value of record status set by {@link #setRecordStatusForPortfolio(java.lang.String)} which is
     * initially <code>A</code>. </p>
     *
     * @throws PersistenceException if there is a problem loading
     * @see #setSpaceList
     */
    public void load() throws PersistenceException {

        // Load for SpaceList context.  Overrides all if spaceList set.
        if ((this.spaceList != null) && (this.spaceList.size() > 0)) {
            loadFromSpaceList();

        } else if ((this.user != null) && (this.user.getID() != null)) {
            // Loads Businesses that the user is a member of
            // There is no stored portfolio containing their membership,
            // it is performed by lookin in PN_SPACE_HAS_PERSON
            loadForUser();

        } else {
            // Silently do nothing
            // It turns out that in some instances no id or space list is
            // provided
            // It is expected that an empty portfolio results from this
            // scenario
            // Example:  Listing related business for a business.  If a business
            // has no related businesses, it has no portfolio to load
        }
    }

    /**
     * Load all businesses that exist in the database. Loads for the current record status. Adds to the portfolio
     * entries in this portfolio without clearing.
     *
     * @throws PersistenceException if there is a problem loading
     * @since Falcon
     */
    public void loadAll() throws PersistenceException {
        addAll(new BusinessSpaceFinder().findAll(this.recordStatus));
        this.entriesLoaded = true;
    }


    /**
     * Load all businesses that exist in the database filtering by name. Loads for the current record status.
     *
     * @param filter the filter to load by; a simple string value. A case-insensitive query is performed
     * @throws PersistenceException if there is a problem loading
     * @since Falcon
     */
    public void loadFiltered(String filter) throws PersistenceException {
        addAll(new BusinessSpaceFinder().findAllByName(this.recordStatus, filter));
        this.entriesLoaded = true;
    }


    /**
     * Loads this porfolio from the spaceList filtering by record status. If the space list is empty, no entries are
     * loaded.
     *
     * @throws net.project.persistence.PersistenceException if there is a problem loading
     * @throws IllegalStateException if the space list is null
     */
    private void loadFromSpaceList() throws PersistenceException {

        if (this.spaceList == null) {
            throw new IllegalStateException("Space list is null");
        }

        // Grab the ids of the businesses from the space list
        List businessSpaceIDList = new ArrayList();
        for (Iterator it = this.spaceList.iterator(); it.hasNext();) {
            businessSpaceIDList.add(((Space) it.next()).getID());
        }

        // Load and add the business spaces only if some were found
        // We filter by record status to ignore disabled businesses
        // The spaceList isn't filtered because the method it uses can't filter
        // by record status
        if (!businessSpaceIDList.isEmpty()) {
            addAll(new BusinessSpaceFinder().findByIDs(businessSpaceIDList, this.recordStatus));

        } else {
            // Portfolio entries remain empty
        }

        this.entriesLoaded = true;
    }


    /**
     * Load the business portfolio for the current user from the database. Uses the current record status for loading.
     *
     * @throws PersistenceException if there is a problem loading
     * @throws IllegalStateException if the current user context is null
     * @see #setUser
     */
    private void loadForUser() throws PersistenceException {

        if (getUser() == null) {
            throw new IllegalStateException("User is null");
        }

        addAll(new BusinessSpaceFinder().findByUser(this.user, this.recordStatus));
        this.entriesLoaded = true;
    }

    /**
     * Load all businesses that exist in the database, except the one with specified ID. This method will be used for
     * moving the projects in the BusinessDeleteWizard
     *
     * @param excludedBusinessSpaceID the id of the business not to include in any potential set of portfolio entries
     * @throws net.project.persistence.PersistenceException if there is a problem loading
     * @since Falcon
     */
    public void loadAllExceptOne(String excludedBusinessSpaceID) throws PersistenceException {
        addAll(new BusinessSpaceFinder().findAllExceptOne(excludedBusinessSpaceID, this.recordStatus));
        this.entriesLoaded = true;
    }

    /**
     * Returns a tree-style portfolio view.
     *
     * @return a tree view view
     */
    public IPortfolioView getTreeView() {
        IPortfolioView view = null;

        try {
            view = new PersonalBusinessTreeView(this);
        } catch (PersistenceException pe) {
            // Do nothing; a null tree view is returned
        }

        return view;
    }


    /**
     * Indicates whether the portfolio properties and entries loaded.
     *
     * @return true if this portfolio and entries are loaded
     */
    public boolean isLoaded() {
        return (super.isLoaded() && this.entriesLoaded);
    }


    /**
     * Converts the object to XML representation. This method returns the object as XML text.
     *
     * @return XML representation of the object
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }


    /**
     * Converts the object to XML node representation without the xml header tag. This method returns the object as XML
     * text.
     *
     * @return XML node representation
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns the XML for this portfolio. Includes portfolio properties and all entries.
     *
     * @return the XML document
     */
    protected XMLDocument getXMLDocument() {
        XMLDocument doc = new XMLDocument();

        try {
            doc.startElement("BusinessPortfolio");
            addXMLProperties(doc);
            doc.endElement();

        } catch (XMLDocumentException e) {
            // Return partially built exception
        }

        return doc;
    }

    /**
     * Adds all XML properties to the specified document. Assumes an element is started.
     *
     * @param doc the XMLDocument to which to add properties
     * @throws XMLDocumentException if there is a problem adding elements
     */
    protected void addXMLProperties(XMLDocument doc) throws XMLDocumentException {

        // Add Portfolio properties
        doc.addElement("PortfolioID", getID());
        doc.addElement("ParentSpaceID", getParentSpaceID());
        doc.addElement("Name", getName());
        doc.addElement("Description", getDescription());
        doc.addElement("Type", getType());
        doc.addElement("ContentType", getContentType());

        // Add all entries
        for (Iterator it = iterator(); it.hasNext();) {
            doc.addXMLString(((BusinessSpace) it.next()).getXMLBody());
        }

    }

    /**
     * Clears the portfolio properties and entries.
     */
    public void clear() {
        super.clear();
        entriesLoaded = false;
        spaceList = null;
    }

    /**
     * Sets the RecordStatus for the PortFolio.
     *
     * @param recordStatus the record status
     */
    public void setRecordStatusForPortfolio(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    /**
     * Indicates whether entries have been loaded for the business portfolio. This differes from {@link #isLoaded} as it
     * doesn't require the portfolio properties (like name etc.) to be loaded. Those properties never get loaded for a
     * business portfolio.
     *
     * @return true if entries have been loaded; false otherwise
     */
    public boolean isEntriesLoaded() {
        return this.entriesLoaded;
    }
}
