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
|     $RCSfile$
|    $Revision: 20826 $
|        $Date: 2010-05-10 11:42:15 -0300 (lun, 10 may 2010) $
|      $Author: dpatil $
|
+----------------------------------------------------------------------*/
package net.project.portfolio;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.database.DBBean;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.project.ProjectSpaceFinder;
import net.project.space.ISpaceListLoadable;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.space.SpaceList;
import net.project.space.SpaceManager;
import net.project.space.SpaceMember;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import org.apache.log4j.Logger;


/**
 * A portfolio of projects.
 * This class provides a collection of <code>ProjectSpace</code>s.
 * The original meaning of "Portfolio" has changed somewhat.
 * Some portfolios are stored in the database (Personal Portfolio, Business
 * owned-Projects portfolio).  Most are "on-the-fly" collections of ProjectSpaces.
 */
public class ProjectPortfolio extends Portfolio implements IJDBCPersistence, IXMLPersistence, ISpaceListLoadable {

    //
    // Static members
    //

    /**
     * Returns the user's "membership" portfolio ID.
     * This is the pn_portfolio.portfolio_id of the project portfolio containing all the projects the user is a member of.
     * @param userID the id of the user for which to get the portfolio
     * @return the id of their membership portfolio
     */
    public static String getUserPortfolioID(String userID) {
        String portfolioID = null;
        String qstrGetUsersPortfolioID = "select membership_portfolio_id from pn_person where person_id = " + userID;
        DBBean ldb = new DBBean();

        try {
            ldb.executeQuery(qstrGetUsersPortfolioID);

            if (ldb.result.next())
                portfolioID = ldb.result.getString("membership_portfolio_id");
        } // end try
        catch (SQLException sqle) {
        	Logger.getLogger(ProjectPortfolio.class).error("ProjectPortfolio.getUserPortfolioID threw an SQL exception: " + sqle);
        } // end catch
        finally {
            ldb.release();
        }

        return portfolioID;
    }


    //
    // Instance members
    //

    /** are the portfolio entries loaded. Used in overloaded isLoaded() method */
    private boolean entriesLoaded = false;

    /** the optional SpaceList context for loading this portfolio */
    private SpaceList spaceList = null;

    /**
     * The record status of entries in this portfolio.
     * Defaults to <code>A</code>.
     */
    private String recordStatus = "A";

    /** Do not show the projects with visibility project members only in Business Workspace*/
    private boolean projectMembersOnly = false;

    /**
     * Creates an empty ProjectPortfolio.
     * By default, only <code>ProjectSpace</code>s with a record status
     * of <code>A</code> are loaded.
     */
    public ProjectPortfolio() {
        super();
        setContentType(ISpaceTypes.PROJECT_SPACE);
    }


    /**
     * Set the space list context for loading a portfolio from a SpaceList.
     * If a spaceList is set, it will take precidence over setID() and the load() method will
     * load the portfolio for the spaceList and not the porfolio id.
     * @param spaceList the list of spaces
     */
    public void setSpaceList(SpaceList spaceList) {
        this.spaceList = spaceList;
    }


    /**
     * Returns a tree-style portfolio view.
     * @return a tree view
     */
     public IPortfolioView getTreeView() {
        IPortfolioView view = null;

        try {
            view = new PersonalProjectTreeView(this);
        } catch (PersistenceException pe) {
            // Do nothing; a null tree view is returned
        }

        return view;
     }


    /**
     * Returns the XML for this portfolio, including entries and XML versiont tag.
     * @return a complete XML document representation of this object.
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }


    /**
     * Returns the XML for this portfolio, including entries with no XML
     * version tag.
     * @return an XML node representation of this object.
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns an XMLDocument representing this portfolio.
     * @return the xml document
     */
    protected XMLDocument getXMLDocument() {

        XMLDocument doc = new XMLDocument();

        try {
            doc.startElement("ProjectPortfolio");
            addXMLProperties(doc);
            doc.endElement();

        } catch (XMLDocumentException e) {
            // Do nothing
            // Returns incomplete document

        }

        return doc;
    }

    /**
     * Adds all XML properties to the specified document.
     * Assumes an element is started.
     * @param doc the XMLDocument to which to add properties
     */
    protected void addXMLProperties(XMLDocument doc) throws XMLDocumentException {

        // Add Portfolio properties
        doc.addElement("portfolioID", getID());
        doc.addElement("parentSpaceID", getParentSpaceID());
        doc.addElement("name", getName());
        doc.addElement("description", getDescription());
        doc.addElement("type", getType());
        doc.addElement("contentType", getContentType());

        // Add all entries
        for (Iterator it = iterator(); it.hasNext(); ) {
            doc.addXMLString(((ProjectSpace) it.next()).getXMLBody());
        }

    }

    /* -------------------------------  Implementing IJDBCPersistence  ------------------------------- */


    /**
     * Loads this ProjectPortfolio.
     * If a space list was specified by {@link #setSpaceList} then the
     * entries loaded correspond to the spaces in that list; otherwise
     * the portfolio for the current is is loaded.
     * When entries are loaded for an id, the entries are actually
     * <code>ProjectPortfolioEntry</code>s.
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws PersistenceException {

        if ((this.spaceList != null) && (this.spaceList.size() > 0)) {
            // Load for SpaceList context.  Overrides all if spaceList set.
            loadFromSpaceList();

        } else if (getID() != null) {
            // Load static portfolio if we have an id
            loadStatic();

        } else {
            // Silently do nothing
            // It turns out that in some instances no id or space list is
            // provided
            // It is expected that an empty portfolio results from this
            // scenario
            // Example:  Listing sub-projects for a project.  If a project
            // has no sub-projects, it has no portfolio to load
        }
    }

    /**
     * Loads this project portfolio with projects which are not dependant on the specified project ID.
     * @param projectIDFilter the projectID for which dependent projects will be filtered out.
     */
    public void loadNonDependants (String projectIDFilter) throws PersistenceException {

        if (getID() == null) {
            throw new IllegalStateException("portfolioID is null. Must be set before calling load()");
        }

      // load the porfolio properties.
        super.load();

        List projectSpaceList = new ProjectSpaceFinder().findNonDependentsByPorfolioID(getID(), projectIDFilter);
        this.addAll(buildPortfolioEntries(projectSpaceList));

        this.entriesLoaded = true;

    }


    /**
     * Loads a porfolio from the spaceList.
     * Assumes spaceList has been specified and that spaceList contains
     * non-loaded <code>Space</code> objects.
     * If the Space list elements were loaded, this method will still work
     * except that it be performing redundant loads of ProjectSapces.
     * @throws net.project.persistence.PersistenceException if there is a problem loading
     */
    private void loadFromSpaceList() throws PersistenceException {

        // Convert the Spaces to space IDs
        List projectSpaceIDList = new ArrayList();
        for (Iterator it = this.spaceList.iterator(); it.hasNext(); ) {
            projectSpaceIDList.add(((Space) it.next()).getID());
        }

        addAll(new ProjectSpaceFinder().findByIDs(projectSpaceIDList));
        this.entriesLoaded = true;
    }


    /**
     * Load a static portfolio from the database.  setID must be call first.
     * A static portfolio must have an entries in the pn_portfolio and pn_portfolio_has_space database tables.
     * @throws net.project.persistence.PersistenceException if there is a problem loading
     * @throws IllegalStateException if no id or user is available.
     */
    private void loadStatic() throws PersistenceException {

        if (getID() == null) {
            throw new IllegalStateException("portfolioID is null. Must be set before calling loadStatic()");
        }

        // load the porfolio properties.
        super.load();

        List projectSpaceList = new ProjectSpaceFinder().findByPortfolioID(getID(),projectMembersOnly);
        this.addAll(buildPortfolioEntries(projectSpaceList));

        this.entriesLoaded = true;
    }

    /**
     * Builds a collection of <code>ProjectPortfolioEntry</code>s based on the specified
     * spaces.
     * @param projectSpaceCollection the collection of <code>ProjectSpace</code>s
     * from which to build the portfolio entries
     * @return the collection of <code>ProjectPortfolioEntry</code>s built
     * from the project spaces
     * @throws PersistenceException if there is a problem loading
     */
    public Collection buildPortfolioEntries(Collection projectSpaceCollection) throws PersistenceException {

        // Get SpaceMember records for the current portfolio and user
        // We turn it into a map for easy lookup
        Map spaceMemberMap = new HashMap();

        //If a user has been set in the portfolio, we are going to add that user's
        //relationship (SpaceMember) to the portfolio entry.
        boolean loadSpaceMembers = getUser() != null;
        if (loadSpaceMembers) {
            for (Iterator it = SpaceManager.getSpaceMemberCollectionForPortfolio(getID(), getUser().getID()).iterator(); it.hasNext(); ) {
                SpaceMember nextMember = (SpaceMember) it.next();
                spaceMemberMap.put(nextMember.getSpaceID(), nextMember);
            }
        }

        // Build portfolio entries
        // A portfolio entry is constructed from a ProjectSpace and SpaceMember
        List portfolioList = new ArrayList();

        for (Iterator it = projectSpaceCollection.iterator(); it.hasNext(); ) {
            ProjectSpace nextProjectSpace = (ProjectSpace) it.next();

            //Find the SpaceMember if the portfolio entry is going to include
            //this information.
            SpaceMember spaceMember = null;
            if (loadSpaceMembers) {
                spaceMember = (SpaceMember) spaceMemberMap.get(nextProjectSpace.getID());
            }

            if (spaceMember == null) {
                portfolioList.add(new ProjectPortfolioEntry(nextProjectSpace));
            } else {
                portfolioList.add(new ProjectPortfolioEntry(nextProjectSpace, spaceMember));
            }
        }

        return portfolioList;
    }

    /**
     * Load all projects that exist in the database regardless of space or user
     * for the current record status.
     * This function is specifically intended for use in the Application Space,
     * hence the lack of restrictions.
     * @throws net.project.persistence.PersistenceException if there is a problem loading
     * @since Falcon
     */
    public void loadAll() throws PersistenceException {
        addAll(new ProjectSpaceFinder().findAllForRecordStatus(this.recordStatus));
    }

    /**
     * Load all projects that exist in the database regardless of space or user.  This function is
     * specifically intended for use in the Application Space, hence the lack of restrictions.
     * @param filter the filter which is simply a string
     * @throws net.project.persistence.PersistenceException if there is a problem loading
     * @since Falcon
     */
    public void loadFiltered(String filter) throws PersistenceException {
        addAll(new ProjectSpaceFinder().findAllByName(this.recordStatus, filter));
    }

    /**
     * Load the project portfolio for a Business Space.
     * Assumes the current portfolio id is the id of the business's project
     * portfolio.
     * @throws net.project.persistence.PersistenceException if there is a problem loading
     * @deprecated as of 7.4; use {@link #load} instead.
     * There is no difference in behavior.
     */
    public void loadForBusiness() throws PersistenceException {
        loadStatic();
    }

    /**
     * Indicates whether the portfolio properties and entries are loaded.
     * @return true if the properties and entries are loaded; false otherwise
     */
    public boolean isLoaded() {
        return (super.isLoaded() && this.entriesLoaded);
    }


    /**
     * Clear the portfolio properties and entries.
     */
    public void clear() {
        super.clear();
        entriesLoaded = false;
        spaceList = null;
    }

    /**
     * Sets the RecordStatus for the PortFolio.
     * @param recordStatus the record Status
     */
    public void setRecordStatusForPortfolio(String recordStatus) {
        this.recordStatus = recordStatus;
    }


	/**
	 * @param projectMembersOnly the projectMembersOnly to set
	 */
	public void setProjectMembersOnly(boolean projectMembersOnly) {
		this.projectMembersOnly = projectMembersOnly;
	}


	/**
	 * @return the projectMembersOnly
	 */
	public boolean isProjectMembersOnly() {
		return projectMembersOnly;
	}

}