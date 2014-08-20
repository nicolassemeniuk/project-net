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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.space;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;

import org.apache.log4j.Logger;


/**
    Provides static methods to manage the relationships between Spaces.  
    Creates and queries relationships between spaces.
    Enforces business rules about space relationships.
    <br>
    Relationships between Spaces are two-directional, to and from parent and child space.
    The types of relationships are determined by the SpaceRelationship class.
*/
public class SpaceManager {

    /** constructor */
    public SpaceManager() {
    }


    /*------------------------------------ Methods for creating, updating, and deleting relationships ------------------------------*/

    /**
     * Create the specified relationship between the parent and child Spaces.
     * 
     * @param parent
     *            the parent Space that the relationship is "from".
     * @param child
     *            the child Space that the relationship is "to".
     * @param relationship
     *            the type of relationship between the parent and child Space.
     */
    public static void addRelationship(Space parent, Space child, SpaceRelationship relationship) {

        DBBean  db = new DBBean();

        try {
            db.setAutoCommit(false);
            addRelationship(db, parent, child, relationship);
            db.commit();

        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException ignored) {
                // Log original error
            }
            Logger.getLogger(SpaceManager.class).error("SpaceManager.addRelationship() threw an SQL Exception: " + sqle);

        } finally {
            db.release();
        }

    }

    /**
     * Create the specified relationship between the parent and child Spaces.
     * @param db the DBBean in which to perform the transaction
     * @param parent the parent Space that the relationship is "from".
     * @param child the child Space that the relationship is "to".
     * @param relationship the type of relationship between the parent and child Space.
     * @throws SQLException if there is a database problem
     */
    public static void addRelationship(DBBean db, Space parent, Space child, SpaceRelationship relationship)
            throws SQLException {

        StringBuffer query = new StringBuffer();
        query.append("insert into pn_space_has_space");
        query.append(" (parent_space_id, parent_space_type, child_space_id, child_space_type, relationship_child_to_parent, relationship_parent_to_child, date_created, created_by, record_status)");
        query.append(" values(?,?,?,?,?,?,SYSDATE,?,'A')");

        db.prepareStatement(query.toString());
        db.pstmt.setString(1, parent.getID());
        db.pstmt.setString(2, parent.getType());
        db.pstmt.setString(3, child.getID());
        db.pstmt.setString(4, child.getType());
        db.pstmt.setString(5, relationship.getNameChildToParent());
        db.pstmt.setString(6, relationship.getNameParentToChild());
        db.pstmt.setString(7, SessionManager.getUser().getID());
        db.executePrepared();

    }


    /** 
        Remove only the specified relationship between the parent and child Spaces.
        @param parent the parent Space that the relationship is "from".
        @param child the child Space that the relationship is "to".
        @param relationship the type of relationship between the parent and child Space.
    */
    public static void removeRelationship(Space parent, Space child, SpaceRelationship relationship)
    {
        DBBean  db = new DBBean();
        StringBuffer query = new StringBuffer();

        query.append("delete from pn_space_has_space");
        query.append(" where parent_space_id=?");
        query.append(" and child_space_id=?");
        query.append(" and relationship_parent_to_child=?");

        try
        {
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, parent.getID());
            db.pstmt.setString(2, child.getID());
            db.pstmt.setString(3, relationship.getNameParentToChild());
            db.executePrepared(); 
        }
        catch (SQLException sqle)
        {
        	Logger.getLogger(SpaceManager.class).error("SpaceManager.removeRelationship() threw an SQL Exception: " + sqle);
        }
        finally
        {
            db.release();
        }

    }


    /*
     *  Create a subproject-superproject relationship.
     */
    public static void addSuperProjectRelationship(Space superProject, Space subproject)
    {
        addRelationship(superProject, subproject, SpaceRelationship.SUBSPACE);
    }


    /*
    *  Create a subbusiness-superbusiness relationship.
    */
    public static void addSuperBusinessRelationship(Space superBusiness, Space subbusiness)
    {
        addRelationship(superBusiness, subbusiness, SpaceRelationship.SUBSPACE);
    }

    /*
     * Create a subbusiness-superbusiness relationship.
     */
    public static void addSuperBusinessRelationship(DBBean db, Space superBusiness, Space subbusiness) throws SQLException {
        addRelationship(db, superBusiness, subbusiness, SpaceRelationship.SUBSPACE);
    }
    
    public static void addSuperFinancialRelationship(DBBean db, Space superFinancial, Space subFinancial) throws SQLException {
    	addRelationship(db, superFinancial, subFinancial, SpaceRelationship.SUBSPACE);
    }

    /*
     *  Create a ownership relationship.  The type of the ownership is determined by the space types passed.
     */
    public static void addOwnerRelationship(Space owner, Space ownedBy)
    {
        addRelationship(owner, ownedBy, SpaceRelationship.OWNERSHIP);
    }


    /*
     *  Create a master relationship.  The type of the ownership is determined by the space types passed.
     */
    public static void addMasterRelationship(Space master, Space child)
    {
        addRelationship(master, child, SpaceRelationship.MASTER);
    }




    /* 
     *  Remove all relationships of the specified type to children of the specified parent space.
     */
    public static void removeChildRelationships(Space parent, SpaceRelationship relationship, String childSpaceType)
    {
        DBBean  db = new DBBean();
        StringBuffer query = new StringBuffer();

        query.append("delete from pn_space_has_space");
        query.append(" where parent_space_id=?");
        query.append(" and relationship_parent_to_child=?");
        query.append(" and child_space_type=?");

        try
        {
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, parent.getID());
            db.pstmt.setString(2, relationship.getNameParentToChild());
            db.pstmt.setString(3, childSpaceType);
            db.executePrepared(); 
        }
        catch (SQLException sqle)
        {
        	Logger.getLogger(SpaceManager.class).error("SpaceManager.removeChildRelationships() threw an SQL Exception: " + sqle);
        }
        finally
        {
            db.release();
        }
    }



    /* 
     *  Remove all relationships of the specified type to parents of the specified child space 
     */
    public static void removeParentRelationships(Space child, SpaceRelationship relationship, String parentSpaceType)
    {
        DBBean  db = new DBBean();
        StringBuffer query = new StringBuffer();

        query.append("delete from pn_space_has_space");
        query.append(" where child_space_id=?");
        query.append(" and relationship_child_to_parent=?");
        query.append(" and parent_space_type=?");

        try
        {
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, child.getID());
            db.pstmt.setString(2, relationship.getNameChildToParent() );
            db.pstmt.setString(3, parentSpaceType);
            db.executePrepared(); 
        }
        catch (SQLException sqle)
        {
        	Logger.getLogger(SpaceManager.class).error("SpaceManager.removeParentRelationships() threw an SQL Exception: " + sqle);
        }
        finally
        {
            db.release();
        }

    }





    /* 
     *  Remove owner relationships of the specified type for specified Space.  
     */
    public static void removeOwnerRelationships(Space child, String ownerSpaceType)
    {
        removeParentRelationships(child, SpaceRelationship.OWNERSHIP, ownerSpaceType);
    }



    /* 
     *  Remove all relationships to a superproject for this project space.  
     *  Makes this project a top-level (parentless) project. 
    */
    public static void removeSuperProjectRelationships(Space child)
    {
        removeParentRelationships(child, SpaceRelationship.SUBSPACE, ISpaceTypes.PROJECT_SPACE);
    }




    /* 
     *  Remove all relationships to a super-business for this business space.  
     *  Makes this business space a top-level (parentless) business space. 
    */
    public static void removeSuperBusinessRelationships(Space child)
    {
        removeParentRelationships(child, SpaceRelationship.SUBSPACE, ISpaceTypes.BUSINESS_SPACE);
    }

    /* 
     *  Remove all relationships to a super-financial for this financial space.  
     *  Makes this financial space a top-level (parentless) financial space. 
    */
    public static void removeSuperFinancialRelationships(Space child)
    {
        removeParentRelationships(child, SpaceRelationship.SUBSPACE, ISpaceTypes.FINANCIAL_SPACE);
    }




    /*------------------------------------ Methods for querying relationships ------------------------------*/

    /**
        Get the child Spaces of the specified type that are related to the specified Space  
        and with the specified relationship depth.
        @param parentSpace the space to get the child spaces of.
        @param relationship the relationship the children spaces have to the parent.
        @param childSpaceType the Space type of the Children spaces to be returned.
        @param depth the number of levels of children to return, MAX_DEPTH for all levels.
    */
    public static SpaceList getRelatedChildSpaces(Space parentSpace, SpaceRelationship relationship, String childSpaceType, int depth)
    {

        DBBean  db = new DBBean();
        StringBuffer query = new StringBuffer();
        SpaceList spaceList = new SpaceList();
        Space space = null;

        // set the type of the space list if specified, set to mixed otherwise.
        if (childSpaceType != null)
            spaceList.setType(childSpaceType);

        query.append("select child_space_id, child_space_type from pn_space_has_space");
        query.append(" where parent_space_id=" + parentSpace.getID());
        query.append(" and relationship_parent_to_child='" + relationship.getNameParentToChild() + "' ");

        if (childSpaceType != null)
            query.append(" and child_space_type='" + childSpaceType + "' ");
        else
            query.append(" order by child_space_type");

        try
        {
            db.executeQuery(query.toString());

            while (db.result.next())
            {
                space = SpaceFactory.constructSpaceFromType(db.result.getString("child_space_type"));
                space.setID(db.result.getString("child_space_id"));
                space.setRelationshipToParent(relationship);
                spaceList.add(space);
                //System.out.println("spaceList: get space:  " + db.result.getString("child_space_id"));
            }
        }
        catch (SQLException sqle)
        {
        	Logger.getLogger(SpaceManager.class).error("SpaceManager.getRelatedChildSpaces() threw an SQL Exception: " + sqle);
        }
        catch (SpaceTypeException ste)
        {
        	Logger.getLogger(SpaceManager.class).error("SpaceManager.getRelatedParentSpaces() passed an unknown space type = " + childSpaceType);
        }
        finally
        {
            db.release();
        }

        return spaceList;
    }
                

    /**
     * Gets all the parent spaces for all relationship types for the specified space.
     * @param space the space for which to get the parents.
     * @return the spaces that are ancestors of specified space
     */
    public static SpaceList getRelatedParentSpaces(Space space) {
        return getRelatedParentSpaces(space, null);
    }


    /**
     * Gets all the parent spaces for the specified space, limiting to parent spaces
     * of the specified type.
     * @param space the space for which to get the parent spaces
     * @param parentSpaceType the type of space that each parent will be
     * @return the spaces that are ancestors of the specified space
     */
    public static SpaceList getRelatedParentSpaces(Space space, String parentSpaceType) {
        return getRelatedParentSpaces(space, parentSpaceType, null);
    }


    /**
     * Gets all the parent spaces for the specified space, limiting to parent spaces
     * of the specified type based on the specified relationship.
     * @param space the space for which to get parent spaces
     * @param parentSpaceType the type of space that each parent must be
     * @param relationship the relationship to limit parent spaces to
     * @return the space that are ancestors of the specified space
     */
    public static SpaceList getRelatedParentSpaces(Space space, String parentSpaceType, SpaceRelationship relationship) {
        return getRelatedParentSpaces(space, relationship, parentSpaceType, 0);
    }


    /**
     * Get the parent Spaces of the specified type that are related to the specified Space
     * and with the specified relationship depth.
     * @param childSpace the space for which to get parent spaces
     * @param relationship the relationship by which a parent is realted to a child;
     * specify <code>null</code> for all types
     * @param parentSpaceType the type of parent spaces to get
     * @param depth the depth for which to traverse the parents; always specify
     * <code>1</code> since that is the current depth that is returned
     * @return 
     */
    public static SpaceList getRelatedParentSpaces(Space childSpace, SpaceRelationship relationship, String parentSpaceType, int depth)
    {
        DBBean  db = new DBBean();
        StringBuffer query = new StringBuffer();
        SpaceList spaceList = new SpaceList();
        Space space = null;

        // set the type of the space list if specified, set to mixed otherwise.
        if (parentSpaceType != null)
            spaceList.setType(parentSpaceType);

        query.append("select parent_space_id, parent_space_type, relationship_parent_to_child, relationship_child_to_parent ");
        query.append("from pn_space_has_space ");
        query.append(" where child_space_id=" + childSpace.getID());
        
        if (relationship != null) {
            query.append(" and relationship_child_to_parent='" + relationship.getNameChildToParent() + "' ");
        }

        if (parentSpaceType != null)
            query.append(" and parent_space_type='" + parentSpaceType + "' ");
        else
            query.append(" order by parent_space_type");

        try
        {
            db.executeQuery(query.toString());

            while (db.result.next())
            {
                space = SpaceFactory.constructSpaceFromType(db.result.getString("parent_space_type"));
                space.setID(db.result.getString("parent_space_id"));
                
                if (relationship != null) {
                    // Set the relationship based on the passed in relationship
                    space.setRelationshipToChild(relationship);
                } else {
                    // Construct the relationship from its names
                    space.setRelationshipToChild(SpaceRelationship.forNames(db.result.getString("relationship_parent_to_child"), db.result.getString("relationship_child_to_parent")));
                }
                
                spaceList.add(space);
            }
        }
        catch (SQLException sqle)
        {
        	Logger.getLogger(SpaceManager.class).error("SpaceManager.getRelatedParentSpaces() threw an SQL Exception: " + sqle);
        }
        catch (SpaceTypeException ste)
        {
        	Logger.getLogger(SpaceManager.class).error("SpaceManager.getRelatedParentSpaces() passed an unknown space type = " + parentSpaceType);
        }
        finally
        {
            db.release();
        }

        return spaceList;
    }

    /**
     * Returns a collection of spaces where each space has a related space.
     * The spaces will include spaces with ids from the idCollection (only those
     * that have related spaces), but will also include other spaces which are
     * related of the related spaces.
     * In all cases, a space will have a related space.
     * @param idCollection the space ids on which to base the hierarchical view
     * @param relationship the relationship by which to link spaces
     * @param spaceType the type of the parent spaces
     * @return the collection of related spaces, each element is a <code>RelatedSpace</code>
     * @throws PersistenceException if there is a problem loading
     * @throws IllegalArgumentException if the specified idCollection is null
     * or is empty
     */
    public static Collection getHierarchicalRelatedSpaces(Set idCollection, SpaceRelationship relationship, String spaceType) throws PersistenceException {

        if (idCollection == null || idCollection.isEmpty()) {
            throw new IllegalArgumentException("idCollection must contain at least one element");
        }

        SpaceList spaceList = new SpaceList();

        // Construct the SQL query for locating the spaces and parent spaces
        StringBuffer query = new StringBuffer();
        query.append("select distinct shs.child_space_id, shs.parent_space_id ");
        query.append("from pn_space_has_space shs ");
        query.append("where (1=1) ");

        query.append("and relationship_child_to_parent='" + relationship.getNameChildToParent() + "' ");
        query.append(" and parent_space_type='" + spaceType + "' ");

        // Add the hierarchical query portions
        // This limits the child spaces to the spaces with ids specified
        // in idCollection
        query.append("start with shs.child_space_id in ( ");
        int counter = 1;
        for (Iterator it = idCollection.iterator(); it.hasNext(); counter++) {
            query.append("'").append(it.next()).append("'");
            if (counter < idCollection.size()) {
                query.append(", ");
            }
        }
        query.append(") ");

        // Traverse the structure finding parents of parents
        query.append("connect by shs.child_space_id = prior shs.parent_space_id");


        DBBean db = new DBBean();

        try {
            db.prepareStatement(query.toString());
            db.executePrepared();

            while (db.result.next()) {

                RelatedSpace relatedSpace = new RelatedSpace(db.result.getString("child_space_id"), db.result.getString("parent_space_id"), relationship);

                spaceList.add(relatedSpace);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(SpaceManager.class).error("SpaceManager.getHierarchicalRelatedParentSpaces() threw an SQLException: " + sqle);
            throw new PersistenceException("Space load operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return spaceList;
    }

    /**
     * Defines two related spaces by space IDs.
     */
    public static class RelatedSpace {

        private String spaceID = null;
        private String parentSpaceID = null;
        private SpaceRelationship relationShip = null;

        public RelatedSpace(String spaceID, String parentSpaceID, SpaceRelationship relationship) {
            this.spaceID = spaceID;
            this.parentSpaceID = parentSpaceID;
            this.relationShip = relationship;
        }

        public String getSpaceID() {
            return this.spaceID;
        }

        public String getParentSpaceID() {
            return this.parentSpaceID;
        }

        public SpaceRelationship getRelationship() {
            return this.relationShip;
        }

    }

    /**
     * Get the Spaces that are related to the specified Space with the specified
     * relationship depth.
     */
    public static SpaceList getRelatedChildSpaces(Space space, SpaceRelationship relationship, int depth) {
        return getRelatedChildSpaces(space, relationship, null, depth);
    }

    /**
     * Get the Spaces that are directly related to the specified Space
     * (relationship depth of 1).
     */
    public static SpaceList getRelatedChildSpaces(Space space, SpaceRelationship relationship) {
        return getRelatedChildSpaces(space, relationship, 1);
    }


    /**
     * Get the Space (parent) that ownes the specified Space. A Space can be
     * owned by only one Space.
     * 
     * @param childSpace
     *            the Space to get the owner of.
     * @return the Space that owns the specified child Space, null if no owning
     *         space is found.
     */
    public static Space getOwnerSpace(Space childSpace) {
        SpaceList spaceList = getRelatedParentSpaces(childSpace, SpaceRelationship.OWNERSHIP, null, 1);

        if ((spaceList != null) && (spaceList.size() > 0))
            return (Space) spaceList.get(0);
        else
            return null;
    }

    /**
     * Get the child Spaces of the speficified type that are owned by the
     * specified Space.
     */
    public static SpaceList getOwnedSpaces(Space parentSpace, String childSpaceType) {
        return getRelatedChildSpaces(parentSpace, SpaceRelationship.OWNERSHIP, childSpaceType, 1);
    }

    /**
     * Get the child Spaces that are owned by the specified Space.
     */
    public static SpaceList getOwnedSpaces(Space parentSpace) {
        return getRelatedChildSpaces(parentSpace, SpaceRelationship.OWNERSHIP, null, 1);
    }

    /**
     * Get the subspaces of the specified type in the heirarchy below the
     * specified Space.
     */
    public static SpaceList getSubspaces(Space space, String childSpaceType, int depth) {
        return getRelatedChildSpaces(space, SpaceRelationship.SUBSPACE, childSpaceType, depth);
    }


    /**
     * Get the subspaces of the all types in the heirarchy below the specified
     * Space.
     */
    public static SpaceList getSubspaces(Space space, int depth) {
        return getRelatedChildSpaces(space, SpaceRelationship.SUBSPACE, null, depth);
    }


    /**
        Get the all direct subspaces of the specified Space.
    */
    public static SpaceList getSubspaces(Space space) {
        return getSubspaces(space, 1);
    }


    /* ------------------------------------------ Space type specific convience methods ----------------------------- */

    /**
     *  Get the list of subprojects of the specified space.
     */
    public static SpaceList getSubprojects(Space space, int depth) {
        return getRelatedChildSpaces(space, SpaceRelationship.SUBSPACE, ISpaceTypes.PROJECT_SPACE, depth);
    }


    /**
     *  Get the list of direct subprojects of the specified space.
     */
    public static SpaceList getSubprojects(Space space) {
        return getSubprojects(space, 1);
    }



    /**
     *  Get the ProjectSpace that is the super-project of this space.
     *  It is possbible for a ProjectSpace to be a subproject to only one ProjectSpace.
     */
    public static Space getSuperProject(Space subspace) {
        SpaceList spaceList = getRelatedParentSpaces(subspace, SpaceRelationship.SUBSPACE, ISpaceTypes.PROJECT_SPACE, 1);
        if ((spaceList != null) && (spaceList.size() > 0))
            return (Space) spaceList.get(0);
        else
            return null;
    }


    /**
     *  Get the list of sub-businesses of the specified space.
     */
    public static SpaceList getSubbusinesses(Space space, int depth) {
        return getRelatedChildSpaces(space, SpaceRelationship.SUBSPACE, ISpaceTypes.BUSINESS_SPACE, depth);
    }


    /**
     * Get the list of sub-businesses of the specified space.
     */
    public static SpaceList getSubbusinesses(Space space) {
        return getSubbusinesses(space, 1);
    }



    /**
     *  Get the BusinessSpace that is the super-businesses of this space.
     *  It is possbible for a BusinessSpace to be a sub-business of only one BusinessSpace.
     */
    public static Space getSuperBusiness(Space subspace) {
        SpaceList spaceList = getRelatedParentSpaces(subspace, SpaceRelationship.SUBSPACE, ISpaceTypes.BUSINESS_SPACE, 1);
        if ((spaceList != null) && (spaceList.size() > 0))
            return (Space) spaceList.get(0);
        else
            return null;
    }
    
    /**
     *  Get the BusinessSpace that is the owner-businesses of this (project) space.
     *  It is possbible for a ProjectSpace to be owned-by only one BusinessSpace.
     */
    public static Space getOwnerBusiness(Space subspace) {
        SpaceList spaceList = getRelatedParentSpaces(subspace, SpaceRelationship.OWNERSHIP, ISpaceTypes.BUSINESS_SPACE, 1);
        if ((spaceList != null) && (spaceList.size() > 0))
            return (Space) spaceList.get(0);
        else
            return null;
    }

    /**
     * Returns a collection of <code>SpaceMember</code>s for the project spaces
     * in a portfolio.
     * @param portfolioID an id of a portfolio where there will be one SpaceMember
     * element for each project in the portfolio
     * @param personID the id of the person for which to get membership
     * information
     * @return the collection where each element is a <code>SpaceMember</code>
     * @throws NullPointerException if portfolioID or personID is null
     * @throws IllegalArgumentException if portfolioID or personID
     * contains only whitespace
     */
    public static Collection getSpaceMemberCollectionForPortfolio(String portfolioID, String personID) throws PersistenceException {

        if (portfolioID == null || personID == null) {
            throw new NullPointerException("Portfolio ID and person id are required");
        } else if (portfolioID.trim().length() == 0 ||personID.trim().length() == 0) {
            throw new IllegalArgumentException("Portfolio ID and person id must be non-empty");
        }

        // Construct query
        StringBuffer query = new StringBuffer();
        query.append("select shp.space_id, shp.person_id, shp.relationship_person_to_space, ");
        query.append("shp.responsibilities, shp.member_title ");
        query.append("from pn_space_has_person shp, pn_portfolio_view p ");
        query.append("where p.portfolio_id = ? ");
        query.append("and shp.space_id = p.project_id ");
        query.append("and shp.person_id = ? ");

        ArrayList memberList = new ArrayList();
        DBBean db = new DBBean();

        try {

            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, portfolioID);
            db.pstmt.setString(++index, personID);
            db.executePrepared();
            while (db.result.next()) {
                SpaceMember spaceMember = new SpaceMember();
                populate(db.result, spaceMember);
                memberList.add(spaceMember);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(SpaceManager.class).error("SpaceManager.getSpaceMemberCollection threw an SQLException: " + sqle);
            throw new PersistenceException("Space Member load operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return memberList;
    }

    /**
     * Populates a SpaceMember from a result set.
     * @param result the ResultSet from which to populate the object; assumes
     * the result set is on a row
     * @param spaceMember the object to populate
     * @throws SQLException if there is a problem reading data from the
     * result set
     */
    private static void populate(ResultSet result, SpaceMember spaceMember) throws SQLException {
        spaceMember.setSpaceID(result.getString("space_id"));
        spaceMember.setPersonID(result.getString("person_id"));
        spaceMember.setRelationship(result.getString("relationship_person_to_space"));
        spaceMember.setResponsibilities(result.getString("responsibilities"));
        spaceMember.setTitle(result.getString("member_title"));
    }
    
    /* 
     *  Remove all relationships of the specified type to parents of the specified child space 
     */
    public static void removeSharedRelationships(String id) {
        String sharequery = "DELETE from pn_shared where export_space_id=" + id + " or import_space_id = " + id;
        String taskquery = "UPDATE pn_task set record_status='D' where task_id in (select imported_object_id from pn_shared where export_space_id=" + id + ")";

        DBBean db = new DBBean();
        try {
            db.createStatement();
            db.stmt.addBatch(taskquery);
            db.stmt.addBatch(sharequery);
            db.stmt.executeBatch();
            db.connection.commit();
        } catch (SQLException sqle) {
            Logger.getLogger(SpaceManager.class).error("SpaceManager.removeSharedRelationships() threw an SQL Exception: " + sqle);
        } finally {
            db.release();
        }

    }


}
