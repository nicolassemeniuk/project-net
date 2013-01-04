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
|   $Revision: 20697 $
|       $Date: 2010-04-14 11:32:45 -0300 (mié, 14 abr 2010) $
|     $Author: dpatil $
|
+----------------------------------------------------------------------*/
package net.project.business;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;

import org.apache.log4j.Logger;

/**
 * Provides methods for loading BusinessSpace objects from persistence store.
 */
public class BusinessSpaceFinder {

    /**
     * The columns to select when loading a BusinessSpace.
     * Business table/view should be aliased as "b"
     */
    private static final String BUSINESS_SPACE_SELECT_COLUMNS =
            "b.business_space_id, b.business_id, b.space_type, " +
            "b.complete_portfolio_id, b.record_status, b.is_master, " +
            "b.business_category_id, b.brand_id, b.billing_account_id, b.address_id, " +
            "b.business_name, b.business_desc, b.business_type, b.logo_image_id, " +
            "b.is_local, b.remote_host_id, b.remote_business_id, b.num_projects, " +
            "b.num_people ";

    /**
     * Creates an empty BusinessSpaceFinder.
     */
    public BusinessSpaceFinder() {
        // Do nothing
    }

    /**
     * Finds <code>BusinessSpace</code>s for the specified ID.
     * @param businessSpaceID the id of the business space to find
     * @return a List where each element is a <code>BusinessSpace</code>.
     * @throws PersistenceException if there is a problem loading the BusinessSpace
     * @throws NullPointerException if the specified businessSpaceID is null
     */
    public final List findByID(String businessSpaceID) throws PersistenceException {

        if (businessSpaceID == null) {
            throw new NullPointerException("businessSpaceID cannot be null");
        }

        List businessSpaceList = new ArrayList();
        DBBean db = new DBBean();

        try {
            // Execute the select statement
            ResultSet result = fetchResultSetByID(db, businessSpaceID);

            while (result.next()) {
                // Populate a BusinessSpace
                BusinessSpace businessSpace = new BusinessSpace();
                populate(db.result, businessSpace);
                businessSpaceList.add(businessSpace);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(BusinessSpaceFinder.class).error("Error loading business space by id: " + sqle);
            throw new PersistenceException("BusinessSpace find by id operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return businessSpaceList;
    }


    /**
     * Finds and loads a business space for the specified id.
     * Populates the specified business space.
     * @param businessSpaceID the id of the business space to load
     * @param businessSpace the business space to populate
     * @return true if the business space was found; false otherwise
     * @throws PersistenceException if there is a problem loading the BusinessSpace
     * @throws NullPointerException if the specified businessSpaceID or
     * businessSpace is null
     */
    final boolean findByID(String businessSpaceID, BusinessSpace businessSpace) throws PersistenceException {

        if (businessSpaceID == null || businessSpace == null) {
            throw new NullPointerException("businessSpaceID or businessSpace cannot be null");
        }

        boolean isFound = false;
        DBBean db = new DBBean();

        try {

            // Execute the select statement
            ResultSet result = fetchResultSetByID(db, businessSpaceID);

            if (result.next()) {
                // We found a row, so populate BusinessSpace
                populate(result, businessSpace);
                isFound = true;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(BusinessSpaceFinder.class).error("Error loading business space by id: " + sqle);
            throw new PersistenceException("BusinessSpace find by id operation failed: " + sqle, sqle);

        } finally {
            db.release();
        }

        return isFound;
    }

    /**
     * Finds <code>BusinessSpace</code>s for the specified collection of IDs.
     * @param businessSpaceIDCollection the colllection of <code>String</code>
     * ids of the business spaces to find
     * @return a List where each element is a <code>BusinessSpace</code>.
     * The number of entries in the list will match the number of entries in
     * the specified collection, assuming each ID in the collection represents
     * a business ID. If any IDs are not found, no exception is thrown;
     * rather, the returned list will have fewer entries.
     * @throws PersistenceException if there is a problem loading the BusinessSpaces
     * @throws NullPointerException if the specified businessSpaceIDCollection is null
     * or empty
     */
    public final List findByIDs(Collection businessSpaceIDCollection) throws PersistenceException {
        return findByIDs(businessSpaceIDCollection, null);
    }

    /**
     * Finds <code>BusinessSpace</code>s for the specified collection of IDs.
     * @param businessSpaceIDCollection the colllection of <code>String</code>
     * ids of the business spaces to find
     * @param recordStatus the record status of businesses to find; if null,
     * no filter on record status will be applied
     * @return a List where each element is a <code>BusinessSpace</code>.
     * The number of entries in the list will match the number of entries in
     * the specified collection, assuming each ID in the collection represents
     * a business ID and has a matching record status.  If any IDs are not found,
     * no exception is thrown; rather, the returned list will have fewer entries.
     * @throws PersistenceException if there is a problem loading the BusinessSpaces
     * @throws NullPointerException if the specified businessSpaceIDCollection is null
     * or empty
     */
    public final List findByIDs(Collection businessSpaceIDCollection, String recordStatus) throws PersistenceException {

        if (businessSpaceIDCollection == null || businessSpaceIDCollection.isEmpty()) {
            throw new NullPointerException("businessSpaceIDCollection cannot be null or empty");
        }

        // Construct the query
        StringBuffer query = new StringBuffer();
        query.append("select ").append(BUSINESS_SPACE_SELECT_COLUMNS);
        query.append("from pn_business_space_view b ");

        query.append("where b.business_id in ( ");
        int counter = 1;
        for (Iterator it = businessSpaceIDCollection.iterator(); it.hasNext(); counter++) {
            query.append("'").append(it.next()).append("' ");
            if (counter < businessSpaceIDCollection.size()) {
                query.append(", ");
            }
        }
        query.append(") ");

        if (recordStatus != null) {
            query.append("and b.record_status = '").append(recordStatus).append("' ");
        }
        // Sorting businesses in alphabetical order
        query.append(" order by b.business_name ");
        
        List businessSpaceList = new ArrayList();
        DBBean db = new DBBean();

        try {

            // Execute the select statement
            db.prepareStatement(query.toString());
            db.executePrepared();

            while (db.result.next()) {
                // Populate a BusinessSpace
                BusinessSpace businessSpace = new BusinessSpace();
                populate(db.result, businessSpace);
                businessSpaceList.add(businessSpace);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(BusinessSpaceFinder.class).error("Error loading business space by id: " + sqle);
            throw new PersistenceException("BusinessSpace find by id operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return businessSpaceList;
    }

    /**
     * Finds all <code>BusinessSpace</code>s in the database.
     * @param recordStatus the record status of BusinessSpaces to find
     * @return a list where each element is a <code>BusinessSpace</code>; the
     * list is currently orderd by name, ordering performed by database
     * @throws PersistenceException if there is a problem loading the BusinessSpaces
     * @throws NullPointerException if the record status is null
     */
    public final List findAll(String recordStatus) throws PersistenceException {

        if (recordStatus == null) {
            throw new NullPointerException("Record status is required");
        }

        // Construct the query
        StringBuffer query = new StringBuffer();
        query.append("select ").append(BUSINESS_SPACE_SELECT_COLUMNS);
        query.append("from pn_business_space_view b ");
        query.append("where b.record_status = ? ");
        query.append("order by b.business_name asc ");

        List businessSpaceList = new ArrayList();
        DBBean db = new DBBean();

        try {
            db.prepareStatement(query.toString());
            int index = 0;
            db.pstmt.setString(++index, recordStatus);
            db.executePrepared();

            while (db.result.next()) {
                BusinessSpace businessSpace = new BusinessSpace();
                populate(db.result, businessSpace);
                businessSpaceList.add(businessSpace);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(BusinessSpaceFinder.class).error("Error loading all business spaces: " + sqle);
            throw new PersistenceException("BusinessSpace find all operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return businessSpaceList;
    }

    /**
     * Finds all <code>BusinessSpace</code>s in the database except the
     * space with the specified id.
     * @param excludedBusinessSpaceID the id of the business space to exclude
     * from the results
     * @param recordStatus the record status of BusinessSpaces to find
     * @return a list where each element is a <code>BusinessSpace</code>; the
     * list is currently orderd by name, ordering performed by database
     * @throws PersistenceException if there is a problem loading the BusinessSpaces
     * @throws NullPointerException if the id or record status is null
     */
    public final List findAllExceptOne(String excludedBusinessSpaceID, String recordStatus)
            throws PersistenceException {

        if (excludedBusinessSpaceID == null || recordStatus == null) {
            throw new NullPointerException("id and record status are required");
        }

        // Construct the query
        StringBuffer query = new StringBuffer();
        query.append("select ").append(BUSINESS_SPACE_SELECT_COLUMNS);
        query.append("from pn_business_space_view b ");
        query.append("where b.record_status = ? ");
        query.append("and b.business_space_id <> ? ");
        query.append("order by b.business_name asc ");

        List businessSpaceList = new ArrayList();
        DBBean db = new DBBean();

        try {
            db.prepareStatement(query.toString());
            int index = 0;
            db.pstmt.setString(++index, recordStatus);
            db.pstmt.setString(++index, excludedBusinessSpaceID);
            db.executePrepared();

            while (db.result.next()) {
                BusinessSpace businessSpace = new BusinessSpace();
                populate(db.result, businessSpace);
                businessSpaceList.add(businessSpace);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(BusinessSpaceFinder.class).error("Error loading all business spaces: " + sqle);
            throw new PersistenceException("BusinessSpace find all operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return businessSpaceList;
    }

    /**
     * Finds all <code>BusinessSpace</code>s in the database based on part
     * of a business name.
     * @param recordStatus the record status of BusinessSpaces to find
     * @param nameFilter part of the business name; A case insensitive
     * search is performed.
     * @return a list where each element is a <code>BusinessSpace</code>; the
     * list is currently orderd by name, ordering performed by database
     * @throws PersistenceException if there is a problem loading the BusinessSpaces
     * @throws NullPointerException if the record status is null
     */
    public final List findAllByName(String recordStatus, String nameFilter) throws PersistenceException {

        if (recordStatus == null || nameFilter == null) {
            throw new NullPointerException("Record status and filter are required");
        }

        String filter = nameFilter;

        if (filter != null) {
            if (filter.length() <= 1) {
                filter = filter + "%";
            } else {
                filter = "%" + filter + "%" ;
            }
            
        }

        // Construct the query
        StringBuffer query = new StringBuffer();
        query.append("select ").append(BUSINESS_SPACE_SELECT_COLUMNS);
        query.append("from pn_business_space_view b ");
        query.append("where b.record_status = ? ");
        query.append("and upper(b.business_name) like UPPER(?) ");
        query.append("order by b.business_name asc ");

        List businessSpaceList = new ArrayList();
        DBBean db = new DBBean();

        try {
            db.prepareStatement(query.toString());
            int index = 0;
            db.pstmt.setString(++index, recordStatus);
            db.pstmt.setString(++index, filter);
            db.executePrepared();

            while (db.result.next()) {
                BusinessSpace businessSpace = new BusinessSpace();
                populate(db.result, businessSpace);
                businessSpaceList.add(businessSpace);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(BusinessSpaceFinder.class).error("Error loading all business spaces: " + sqle);
            throw new PersistenceException("BusinessSpace find all for record status operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return businessSpaceList;
    }

    /**
     * Finds all <code>BusinessSpace</code>s that the specified user is a member.
     * @param user the user who is a member of the business spaces
     * @param recordStatus the record status of BusinessSpaces to find
     * @return a list where each element is a <code>BusinessSpace</code>; the
     * list is currently orderd by name, ordering performed by database
     * @throws PersistenceException if there is a problem loading the BusinessSpaces
     * @throws NullPointerException if the user or record status is null
     */
    public final List findByUser(User user, String recordStatus) throws PersistenceException {

        if (user == null || recordStatus == null) {
            throw new NullPointerException("User and record status are required");
        }

        // Construct the query
        StringBuffer query = new StringBuffer();
        query.append("select distinct ").append(BUSINESS_SPACE_SELECT_COLUMNS);
        query.append("from pn_business_space_view b, ");
        query.append("pn_space_has_person shp ");
        query.append("where b.record_status = ? ");
        query.append("and shp.space_id = b.business_space_id ");
        query.append("and (shp.person_id = ? or b.includes_everyone = 1) ");
        query.append("and shp.record_status = ? ");
        query.append("order by b.business_name asc ");

        List businessSpaceList = new ArrayList();
        DBBean db = new DBBean();

        try {
            db.prepareStatement(query.toString());
            int index = 0;
            db.pstmt.setString(++index, recordStatus);
            db.pstmt.setString(++index, user.getID());
            db.pstmt.setString(++index, recordStatus);
            db.executePrepared();

            while (db.result.next()) {
                BusinessSpace businessSpace = new BusinessSpace();
                populate(db.result, businessSpace);
                businessSpaceList.add(businessSpace);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(BusinessSpaceFinder.class).error("Error loading all business spaces: " + sqle);
            throw new PersistenceException("BusinessSpace find for user operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return businessSpaceList;
    }

    /**
     * Executes a select statement to find BusinessSpace for the specified id.
     * @param db the DBBean in which to perform the transaction
     * @param businessSpaceID the id of the business space to load
     * @return the result set
     * @throws SQLException if there is a problem accessign the database
     */
    private ResultSet fetchResultSetByID(DBBean db, String businessSpaceID) throws SQLException {

        StringBuffer query = new StringBuffer();
        query.append("select ").append(BUSINESS_SPACE_SELECT_COLUMNS);
        query.append("from pn_business_space_view b ");
        query.append("where b.business_id = ? ");

        int index = 0;
        db.prepareStatement(query.toString());
        db.pstmt.setString(++index, businessSpaceID);
        db.executePrepared();

        return db.result;
    }


    /**
     * Populates the specified business space from a result set row.
     * Assumes result contains a current row.
     * @param result the result set from which to populate
     * @param businessSpace the business space to populate
     * @throws SQLException if there is a problem getting a value from the
     * result set
     */
    private static void populate(ResultSet result, BusinessSpace businessSpace) throws SQLException {

        businessSpace.setID(result.getString("business_id"));
        businessSpace.setName(result.getString("business_name"));
        businessSpace.setDescription(result.getString("business_desc"));
        businessSpace.m_complete_portfolio_id = result.getString("complete_portfolio_id");
        businessSpace.setFlavor(result.getString("business_type"));
        businessSpace.setLogoID(result.getString("logo_image_id"));
        businessSpace.setNumProjects(result.getString("num_projects"));
        businessSpace.setNumMembers(result.getString("num_people"));
        businessSpace.setRecordStatus(result.getString("record_status"));
        businessSpace.m_address.setID(result.getString("address_id"));
        businessSpace.setLoaded(true);

    }

}