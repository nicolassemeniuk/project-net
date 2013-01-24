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
package net.project.base.finder;

import java.sql.SQLException;

import net.project.base.PnetException;
import net.project.database.Clob;
import net.project.database.DBBean;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.NotSupportedException;
import net.project.persistence.PersistenceException;
import net.project.util.VisitException;

/**
 * Provides the aggregation of all the things necessary to produce results
 * from a finder.
 * Includes such things as selected columns, filtering criteria, ordering
 * and grouping information.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public abstract class FinderIngredients implements IJDBCPersistence {

    /**
     * The persistent id of this FinderIngredients.
     */
    private String ingredientsID = null;

    /**
     * The columns selected to be displayed.
     */
    private FinderColumnList finderColumnList = new FinderColumnList();

    /**
     * The filters selected to filter the data in a finder.
     */
    private FinderFilterList finderFilterList = new FinderFilterList();

    /**
     * The Grouping clauses by which to group data returned from a finder.
     */
    private FinderGroupingList finderGroupingList = new FinderGroupingList();

    /**
     * The Sorting clauses by which to sort data in a finder.
     */
    private FinderSorterList finderSorterList = new FinderSorterList();

    /**
     * Specifies the data columns required in the results from a finder.
     * @param finderColumnList the columns
     */
    protected void setFinderColumnList(FinderColumnList finderColumnList) {
        this.finderColumnList = finderColumnList;
    }

    /**
     * Returns the columns required in the results from a finder.
     * @return the columns
     */
    public FinderColumnList getFinderColumnList() {
        return this.finderColumnList;
    }

    /**
     * Specifies the filters to limit the data returned by a finder.
     * @param finderFilterList the filters
     */
    protected void setFinderFilterList(FinderFilterList finderFilterList) {
        this.finderFilterList = finderFilterList;
    }

    /**
     * Returns the filters used to limit the data returned by a finder.
     * @return the filters
     */
    public FinderFilterList getFinderFilterList() {
        return this.finderFilterList;
    }

    /**
     * Specifies the groupings by which data is to be grouped.
     * @param finderGroupingList the groupings
     */
    protected void setFinderGroupingList(FinderGroupingList finderGroupingList) {
        this.finderGroupingList = finderGroupingList;
    }

    /**
     * Returns the groupings by which data from a finder is to be grouped.
     * @return the groupings
     */
    public FinderGroupingList getFinderGroupingList() {
        return this.finderGroupingList;
    }

    /**
     * Specifies the sorters by which data is to be sorted.
     * @param finderSorterList the sorters
     */
    protected void setFinderSorterList(FinderSorterList finderSorterList) {
        this.finderSorterList = finderSorterList;
    }

    /**
     * Returns the sorters by which data is to be sorted.
     * @return the sorters
     */
    public FinderSorterList getFinderSorterList() {
        return this.finderSorterList;
    }

    /**
     * Returns the XML of these FinderIngredients suitable for persistence
     * to the database.
     * @return the XML
     * @throws PnetException if there is a problem producing the XML
     */
    protected String producePersistentXML() throws PnetException {
        FinderIngredientPersistentXMLProducer producer = new FinderIngredientPersistentXMLProducer();
        this.accept(producer);
        return producer.getXMLDocument().getXMLBodyString();
    }

    /**
     * Consumes persistent XML and updates these FinderIngredients appropriately.
     * @param xml the xml to consume
     * @throws PnetException if there is a problem consuming the XML
     */
    protected void consumePersistentXML(String xml) throws PnetException {
        FinderIngredientPersistentXMLConsumer consumer = new FinderIngredientPersistentXMLConsumer(xml);
        this.accept(consumer);
    }

    /**
     * Returns the id of the ingredients.
     * @return the id
     */
    public String getID() {
        return this.ingredientsID;
    }

    /**
     * Specifies the ingredients id used to load and store ingredients.
     * @param id the id
     */
    public void setID(String id) {
        this.ingredientsID = id;
    }

    /**
     * Loads the FinderIngredients for the current id from the database.
     * The FinderIngredients is assumed be fully populated with all available
     * columns, filters, groupings and sorters.  The purpose of this method
     * is to set all the modified settings, including selected items and values.
     *
     * @throws PersistenceException if there is a problem loading or processing
     * the XML
     * @throws IllegalStateException if the current ID is null
     */
    public void load() throws PersistenceException {

        if (getID() == null) {
            throw new IllegalStateException("ID is required");
        }

        String xml = null;
        DBBean db = new DBBean();

        // First load the XML from the Clob
        try {

            Clob clob = new Clob(db, "pn_finder_ingredients", "ingredients_id", "ingredients_data");
            clob.setID(getID());
            clob.loadReadOnly();
            xml = clob.getData();

        } finally {
            db.release();

        }

        // Now parse the XML and update this structure
        try {
            consumePersistentXML(xml);
        } catch (PnetException e) {
            throw new PersistenceException("Unable to process XML: " + e, e);
        }
    }

    /**
     * Stores these FinderIngredients.
     * If the current id is null, a new record is created; otherwise the
     * existing record is updated.
     * @throws PersistenceException if there is a problem storing
     */
    public void store() throws PersistenceException {

        DBBean db = new DBBean();

        try {

            db.setAutoCommit(false);
            store(db);
            db.commit();

        } catch (SQLException e) {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
                // Simply release
            }

            throw new PersistenceException("FinderIngredients.store() operation failed: " + e, e);

        } finally {
            db.release();

        }
    }

    /**
     * Stores these FinderIngredients.
     * No commit or rollback is performed.
     * If the current id is null, a new record is created; otherwise the
     * existing record is updated.
     * @throws PersistenceException if there is a problem storing
     */
    public void store(DBBean db) throws PersistenceException {
        if (getID() == null) {
            insert(db);
        } else {
            update(db);
        }
    }

    /**
     * Updates these finder ingredients in the database.
     * No commit or rollback is performed.
     * @param db the DBBean in which to perform the transaction.
     * @throws PersistenceException if there is a problem updating in the
     * database or a problem producing the XML for storage
     */
    private void update(DBBean db) throws PersistenceException {

        String xml = null;

        try {
            xml = producePersistentXML();
        } catch (PnetException e) {
            throw new PersistenceException("Unable to generate XML for storage: " + e, e);
        }

        Clob clob = new Clob(db, "pn_finder_ingredients", "ingredients_id", "ingredients_data");
        clob.setID(getID());
        clob.loadReadWrite();
        clob.setData(xml);
        clob.store();

    }

    /**
     * Inserts these finder ingredients in the database.
     * No commit or rollback is performed.
     * @param db the DBBean in which to perform the transaction.
     * @throws PersistenceException if there is a problem inserting into the
     * database or or a problem producing the XML for storage
     */
    private void insert(DBBean db) throws PersistenceException {

        String xml = null;

        try {
            xml = producePersistentXML();
        } catch (PnetException e) {
            throw new PersistenceException("Unable to generate XML for storage: " + e, e);
        }

        // Generate the id for the new view
        String ingredientsID = net.project.database.ObjectManager.getNewObjectID();

        Clob clob = new Clob(db, "pn_finder_ingredients", "ingredients_id", "ingredients_data");
        clob.setID(ingredientsID);
        clob.setData(xml);
        clob.store();

        // Since everything was successful, set the new id
        setID(ingredientsID);
    }

    /**
     * Not supported.
     * @throws PersistenceException never
     * @throws NotSupportedException always
     */
    public void remove() throws PersistenceException {
        throw new NotSupportedException("Remove not supported");
    }

    /**
     * Accepts the specified visitor and visits this object.
     * @param visitor the <code>IFinderIngredientVisitor</code> implementation to
     * use for visiting
     * @throws net.project.util.VisitException if there is a problem visiting this object
     */
    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitFinderIngredients(this);
    }
}
