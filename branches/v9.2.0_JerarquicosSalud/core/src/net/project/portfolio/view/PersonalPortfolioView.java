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
|   $Revision: 20372 $
|       $Date: 2010-02-05 03:52:52 -0300 (vie, 05 feb 2010) $
|     $Author: ritesh $
|
+----------------------------------------------------------------------*/
package net.project.portfolio.view;

import java.beans.PropertyDescriptor;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;

import net.project.base.finder.FinderIngredients;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.portfolio.ProjectPortfolio;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * Provides a saved view of a user's personal portfolio.
 *
 * @author Tim Morrow
 * @since version 7.4
 */
public class PersonalPortfolioView
        implements net.project.gui.html.IHTMLOption, IView {

    private String id = null;
    private String name = null;
    private String description = null;
    private String createdByID = null;
    private Date createdDate = null;
    private String modifiedByID = null;
    private Date modifiedDate = null;
    private String recordStatus = null;
    private boolean viewShared = false;
    private boolean visibleToAll = false;

    private final ProjectPortfolio personalPortfolio;
    private FinderIngredients finderIngredients;
    private String finderIngredientsID = null;
    private boolean isFinderIngredientsLoaded = false;

    /**
     * The current view context, used for storing a view.
     */
    private PersonalPortfolioViewContext viewContext = null;

    private SortParameters sortParameters;

    /**
     * Creates a PersonalPortfolioView for the specified context.
     * @param viewContext the context to which this view applies
     */
    PersonalPortfolioView(PersonalPortfolioViewContext viewContext) {
        this.viewContext = viewContext;
        this.personalPortfolio = viewContext.getPortfolio();
        this.finderIngredients = new PersonalPortfolioFinderIngredients(this.personalPortfolio);
    }

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedByID(String createdByID) {
        this.createdByID = createdByID;
    }

    public String getCreatedByID() {
        return this.createdByID;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setModifiedByID(String modifiedByID) {
        this.modifiedByID = modifiedByID;
    }

    public String getModifiedByID() {
        return this.modifiedByID;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setFinderIngredientsID(String ingredientsID) {
        this.finderIngredientsID = ingredientsID;
    }

    public FinderIngredients getFinderIngredients() {
        return this.finderIngredients;
    }

    public SortParameters getSortParameters() {
        return sortParameters;
    }

    public void setSortParameters(SortParameters sortParameters) {
        this.sortParameters = sortParameters;
    }

    /**
     * Returns the results of executing this view.
     * @return the results
     * @throws PersistenceException if there is a problem getting the results
     */
    public IViewResults getResults() throws PersistenceException {

        if (!this.isFinderIngredientsLoaded) {
            loadFinderIngredients();
        }
        PersonalPortfolioViewResults results = new PersonalPortfolioViewResults(this.personalPortfolio, getFinderIngredients(), ResultType.DEFAULT, getSortParameters());
        results.load();
        return results;
    }

    private void loadFinderIngredients() throws PersistenceException {
        this.finderIngredients.setID(this.finderIngredientsID);
        this.finderIngredients.load();
        this.isFinderIngredientsLoaded = true;
    }

    
    //
    // Implementing IJDBCPersistence
    //

    /**
     * Loads the view for the current id.
     * @throws PersistenceException if there is a problem loading
     * @throws IllegalStateException if the current id is null
     * @see #setID
     */
    public void load() throws PersistenceException {

        if (getID() == null) {
            throw new IllegalStateException("ID must be specified for loading");
        }

        ViewFinder viewFinder = new ViewFinder();
        viewFinder.findByID(getID(), this);
        loadFinderIngredients();
    }

    /**
     * Stores this view.
     * Inserts a new view if the current id is null, otherwise updates the
     * current view.
     * @throws PersistenceException if there is a problem storing
     * @throws IllegalStateException if the current user is null
     */
    public void store() throws PersistenceException {

        if (this.viewContext.getCurrentUser() == null) {
            throw new IllegalStateException("Current user must be specified for storing");
        }

        DBBean db = new DBBean();

        try {

            db.setAutoCommit(false);

            // First store the finder ingredients
            // We need to do this first so that if they are new, a new ID
            // will be generated.  The finder ingredients id is a foreign key
            // in the view table
            this.finderIngredients.store(db);

            // Insert or update depending on whether we have a view id
            // We're assuming that the presence of an id means the view was loaded
            if (getID() != null) {
                update(db);
                this.viewContext.update(db, this);

            } else {
                insert(db);
                this.viewContext.insert(db, this);

            }

            // Commit all modifications
            db.commit();

        } catch (SQLException sqle) {

            try {
                db.rollback();
            } catch (SQLException sqle2) {
                // Simply throw exception
            }

            throw new PersistenceException("View Builder store operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Updates the view being managed by this view builder.
     * No commit or rollback is performed.
     * @param db the database bean in which to perform the transaction
     * @throws PersistenceException if there is a problem updating
     */
    private void update(DBBean db) throws PersistenceException {

        StringBuffer query = new StringBuffer();
        query.append("update pn_view v ");
        query.append("set v.name = ?, v.description = ?, v.modified_by_id = ?, v.modified_datetime = ?, ");
        query.append("v.ingredients_id = ?, ");
        query.append("v.is_view_shared = ?, ");
        query.append("v.is_visible_to_all = ? ");
        query.append("where v.view_id = ? ");

        try {
            // Update view values before storing
            setModifiedByID(this.viewContext.getCurrentUser().getID());
            setModifiedDate(new Date());

            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, getName());
            db.pstmt.setString(++index, getDescription());
            db.pstmt.setString(++index, getModifiedByID());
            db.pstmt.setTimestamp(++index, new java.sql.Timestamp(getModifiedDate().getTime()));
            db.pstmt.setString(++index, this.finderIngredients.getID());
            db.pstmt.setBoolean(++index, isViewShared());
            db.pstmt.setBoolean(++index, isVisibleToAll());
            db.pstmt.setString(++index, getID());
            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("View Builder update operation failed: " + sqle, sqle);

        }

    }

    /**
     * Inserts the view being built.
     * No commit or rollback is performed.
     * Sets the id after inserting.
     * @param db the database bean in which to perform the transaction
     * @throws PersistenceException if there is a problem inserting
     */
    private void insert(DBBean db) throws PersistenceException {

        StringBuffer query = new StringBuffer();
        query.append("insert into pn_view (view_id, name, description, created_by_id, ");
        query.append("created_datetime, modified_by_id, modified_datetime, record_status, ingredients_id, is_view_shared, is_visible_to_all) ");
        query.append("values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ");

        try {
            // Generate the id for the new view
            String viewID = net.project.database.ObjectManager.getNewObjectID();

            // Initialize the values for storing
            setCreatedByID(this.viewContext.getCurrentUser().getID());
            setCreatedDate(new Date());
            setModifiedByID(this.viewContext.getCurrentUser().getID());
            setModifiedDate(getCreatedDate());
            setRecordStatus("A");

            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, viewID);
            db.pstmt.setString(++index, getName());
            db.pstmt.setString(++index, getDescription());
            db.pstmt.setString(++index, getCreatedByID());
            db.pstmt.setTimestamp(++index, new java.sql.Timestamp(getCreatedDate().getTime()));
            db.pstmt.setString(++index, getModifiedByID());
            db.pstmt.setTimestamp(++index, new java.sql.Timestamp(getModifiedDate().getTime()));
            db.pstmt.setString(++index, getRecordStatus());
            db.pstmt.setString(++index, this.finderIngredients.getID());
            db.pstmt.setBoolean(++index, isViewShared());
            db.pstmt.setBoolean(++index, isVisibleToAll());
            db.executePrepared();

            // Save the id now
            setID(viewID);

        } catch (SQLException sqle) {
            throw new PersistenceException("View Builder update operation failed: " + sqle, sqle);

        }

    }

    /**
     * Removes the current view.
     * @throws PersistenceException if there is a problem removing
     * @throws IllegalStateException if the current user is null
     */
    public void remove() throws PersistenceException {

        if (this.viewContext.getCurrentUser() == null) {
            throw new IllegalStateException("Current user must be specified for removing");
        }

        StringBuffer query = new StringBuffer();
        query.append("update pn_view v ");
        query.append("set v.modified_by_id = ?, v.modified_datetime = ?, v.record_status = ?, v.is_view_shared = ?, v.is_visible_to_all = ?");
        query.append("where v.view_id = ? ");

        DBBean db = new DBBean();

        try {

            db.setAutoCommit(false);

            // Update view values before storing
            setModifiedByID(this.viewContext.getCurrentUser().getID());
            setModifiedDate(new Date());
            setRecordStatus("D");

            // Soft-delete
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, getModifiedByID());
            db.pstmt.setTimestamp(++index, new java.sql.Timestamp(getModifiedDate().getTime()));
            db.pstmt.setString(++index, getRecordStatus());
            db.pstmt.setBoolean(++index, false); // for column is_view_shared
            db.pstmt.setBoolean(++index, false); // for column is_visible_to_all
            db.pstmt.setString(++index, getID());
            db.executePrepared();

            // Remove from context
            this.viewContext.remove(db, this);

            db.commit();

        } catch (SQLException sqle) {

            try {
                db.rollback();
            } catch (SQLException sqle2) {
                // Simply throw exception
            }

            throw new PersistenceException("View Builder remove operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    //
    // Implementing IXMLPersistence
    //

    /**
     * Returns the XML representation of this view.
     * @return the XML representation of this view including the XML version tag
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    /**
     * Returns the XML representation of this view.
     * @return the XML representation of this view excluding the XML version tag
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLString();
    }

    /**
     * Returns an XMLDocument representing this view.
     * @return the XMLDocument
     */
    XMLDocument getXMLDocument() {

        XMLDocument doc = new XMLDocument();

        try {
            doc.startElement("PersonalPortfolioView");
            doc.addElement("ID", getID());
            doc.addElement("Name", getName());
            doc.addElement("Description", getDescription());
            doc.addElement("CreatedByID", getCreatedByID());
            doc.addElement("CreatedDatetime", getCreatedDate());
            doc.addElement("ModifiedByID", getModifiedByID());
            doc.addElement("ModifiedDatetime", getModifiedDate());
            doc.addElement("RecordStatus", getRecordStatus());
            doc.endElement();

        } catch (XMLDocumentException e) {
            // Return partially built document

        }

        return doc;
    }

    //
    // Implementing IHTMLOption
    //

    public String getHtmlOptionValue() {
        return getID();
    }

    public String getHtmlOptionDisplay() {
        return getName();
    }


    public static class SortParameters {
        public static final int STRING_TO_STRING = 0;
        public static final int STRING_TO_DOUBLE = 1;
        public static final int NULL_IS_LESSER = 0;
        public static final int NULL_IS_GREATER = 1;

        private boolean ascending = true;
        private String sortProperty;
        private int propertyConversion = STRING_TO_STRING;
        private int nullHandling = NULL_IS_GREATER;


        public boolean isAscending() {
            return ascending;
        }

        public void setAscending(boolean ascending) {
            this.ascending = ascending;
        }

        public String getSortProperty() {
            return sortProperty;
        }

        public void setSortProperty(String sortProperty) {
            if (sortProperty != null && sortProperty.length() > 0) {
                this.sortProperty = sortProperty.substring(0, 1).toLowerCase() + sortProperty.substring(1);
            } else {
                this.sortProperty = sortProperty;
            }
        }

        public int getPropertyConversion() {
            return propertyConversion;
        }

        public void setPropertyConversion(int propertyConversion) {
            this.propertyConversion = propertyConversion;
        }


        public int getNullHandling() {
            return nullHandling;
        }

        public void setNullHandling(int nullHandling) {
            this.nullHandling = nullHandling;
        }

        public Comparator createComparator() {
            return new Comparator() {
                public int compare(Object o1, Object o2) {
                    int result = 0;
                    try {
                        do {
                            Object p1;
                            Object p2;
                            if (sortProperty.startsWith("meta")) {
                                p1 = ((net.project.project.ProjectSpace) o1).getMetaData().getProperty(sortProperty.substring(4));
                                p2 = ((net.project.project.ProjectSpace) o2).getMetaData().getProperty(sortProperty.substring(4));
                            } else {
                                PropertyDescriptor pd1 = new PropertyDescriptor(sortProperty, o1.getClass());
                                PropertyDescriptor pd2 = new PropertyDescriptor(sortProperty, o2.getClass());
                                p1 = pd1.getReadMethod().invoke(o1, new Object[]{});
                                p2 = pd2.getReadMethod().invoke(o2, new Object[]{});
                            }
                            if (p1 == null && p2 == null) {
                                result = 0;
                                break;
                            }
                            if (p1 == null || p2 == null) {
                                if (nullHandling == NULL_IS_GREATER)
                                    result = (p1 == null ? 1 : -1);
                                else
                                    result = (p1 == null ? -1 : 1);
                                break;
                            }
                            if (!p1.getClass().equals(p2.getClass())) {
                                result = 0;
                                break;
                            }
                            if (String.class.equals(p1.getClass())) {
                                if (propertyConversion == STRING_TO_STRING) {
                                    if ("".equals(p1) && "".equals(p2)) {
                                        result = 0;
                                    } else if ("".equals(p1) || "".equals(p2)) {
                                        if (nullHandling == NULL_IS_GREATER)
                                            result = ("".equals(p1) ? 1 : -1);
                                        else
                                            result = ("".equals(p1) ? -1 : 1);
                                        if (!ascending) result *= -1;
                                    } else {
                                        result = ((String) p1).compareTo((String) p2);
                                    }
                                } else if (propertyConversion == STRING_TO_DOUBLE) {
                                    Double d1 = Double.parseDouble((String) p1);
                                    Double d2 = Double.parseDouble((String) p2);
                                    result = d1.compareTo(d2);
                                }
                            } else if (p1 instanceof Comparable && p2 instanceof Comparable) {
                                result = ((Comparable) p1).compareTo(p2);
                            }
                        } while (false);
                    } catch (Exception e) {
                    }
                    if (!ascending)
                        result *= -1;
                    return result;
                }
            };
        }
    }


	/**
	 * @return the viewShared
	 */
	public boolean isViewShared() {
		return viewShared;
	}

	/**
	 * @param viewShared the viewShared to set
	 */
	public void setViewShared(boolean viewShared) {
		this.viewShared = viewShared;
	}

	/**
	 * @return the visibleToAll
	 */
	public boolean isVisibleToAll() {
		return visibleToAll;
	}

	/**
	 * @param visibleToAll the visibleToAll to set
	 */
	public void setVisibleToAll(boolean visibleToAll) {
		this.visibleToAll = visibleToAll;
	}

	/**
	 * To get view results after applying filters, without saving the view.
	 * These filter parameters will be saved when a view is saved with new settings. 
     * @param finderIngredients
     * @return results
     * @throws PersistenceException
     */
    public IViewResults getFilteredResults(FinderIngredients finderIngredients) throws PersistenceException {
       PersonalPortfolioViewResults results = new PersonalPortfolioViewResults(this.personalPortfolio, finderIngredients, ResultType.DEFAULT, getSortParameters());
       results.load();
       return results;
    }

	 
	/**
	 * To set finder ingredients to this view when filters are applied on default tree view.
     * @param finderIngredients
     * @see net.project.portfolio.view.IView#setFinderIngredients(net.project.base.finder.FinderIngredients)
     */
	public void setFinderIngredients(FinderIngredients finderIngredients) {
		this.finderIngredients = finderIngredients;
	}
}

