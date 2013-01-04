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
|   $Revision: 20463 $
|       $Date: 2010-02-24 10:41:41 -0300 (mié, 24 feb 2010) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.security.group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * A collection of <code>Group</code>s.
 */
public class GroupCollection 
        extends ArrayList
        implements net.project.persistence.IXMLPersistence  {

    /** the space context */
    private Space currentSpace = null;
    
    /** the User context */
    private User currentUser = null;

    private boolean isLoaded = false;


    /**
     * Creates an empty GroupCollection.
     */
    public GroupCollection() {
        super();
    }
    
    /**
     * Set the Space context.
     * This also clears the collection.
     * @param space the current space context
     * @see #getSpace
     */
    public void setSpace(Space space) {
        // new context; we need to clear the list.
        this.clear();
        this.currentSpace = space;
        this.isLoaded = false;
    }


    /**
     * Returns the current space context.
     * @return the current space context
     * @see #setSpace
     */
    protected Space getSpace() {
        return this.currentSpace;
    }

    /**
     * Set the User context.
     * This also clears the collection.
     * @param user the current user
     */
    public void setUser(User user) {
        this.clear();
        this.currentUser = user;
        this.isLoaded = false;
    }


    /**
     * Returns an HTML option list of the groups in this collection exlcuding
     * the any Space Administrator groups.
     * @param selectedGroupID the id of the group to be selected in the option list
     * @return the HTML option list of groups
     */
    public String getOptionList(String selectedGroupID) throws GroupException  {
        Group group = null;
        StringBuffer options = new StringBuffer();
        String selectedAttribute = "";

        // Update the collection with owning space 
        this.updateWithOwningSpace();

        Iterator it = iterator();
        while (it.hasNext()) {
            group = (Group) it.next();
            selectedAttribute = "";
            
            // Only process non-Space Administrator groups
            if (!group.getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR)) {

                // Make the option selected if it matches the passed in group id
                if (selectedGroupID != null && group.getID().equals(selectedGroupID)) {
                    selectedAttribute = "selected ";
                }
                if (SessionManager.getUser().getCurrentSpace().getID().equals(group.getOwningSpace().getID())) {
                    options.append ("<option " + selectedAttribute + "value=\"" + group.getID() + "\">" + group.getName() + "</option>");
                } else {
                    options.append ("<option " + selectedAttribute + "value=\"" + group.getID() + "\">" + group.getOwningSpace().getName() +" : "+group.getName() + "</option>");
                }
                
            }
        }

        return options.toString();
    }


    /**
     * Returns the XML for the groups in this collection, including the
     * version tag.
     * @return the XML representing these groups
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }


    /**
     * Returns the XML for the groups in this collection, exlcuding the
     * version tag.
     * @return XML representing these groups
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<groupList>");
        xml.append("<size>" + this.size() + "</size>\n");  

        Iterator it = iterator();
        while (it.hasNext()) {
            xml.append( ((Group)it.next()).getXMLBody() );
        }

        xml.append("</groupList>\n");
        
        return xml.toString();
    }


    /**
     * Is the list loaded from persistence.
     * @return true if load, false otherwise.
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }


    /**
     * Loads groups owned by the current space into this collection.
     * Clears out collection before loading.
     * @throws PersistenceException if there is a problem loading the
     * groups
     */
    public void loadOwned() throws PersistenceException {
        StringBuffer query = new StringBuffer();
        ArrayList bindVariables = new ArrayList();

        clear();
        
        // Query to load groups by space for a specific space where
        // that space owns the group
        query.append(GroupDAO.getQueryLoadSpaceGroups());
        query.append("and shg.space_id = ? and shg.is_owner = 1 ");

        bindVariables.add(this.currentSpace.getID());
        
        try {
            // Load and populate this collection
            loadGroups(query.toString(), bindVariables);
        
        } catch (GroupException ge) {
            throw new PersistenceException("Group load operation failed", ge);

        }
    
    }


    /**
     * Loads groups not owned by the current space (but in the current space)
     * into this collection.  (That is, inherited groups).
     * Clears out collection before loading.
     * @throws PersistenceException if there is a problem loading the
     * groups
     */
    public void loadNonOwned() throws PersistenceException {
        StringBuffer query = new StringBuffer();
        ArrayList bindVariables = new ArrayList();

        clear();
        
        // Query to load groups by space for a specific space where
        // that space owns the group
        query.append(GroupDAO.getQueryLoadSpaceGroups());
        query.append("and shg.space_id = ? and shg.is_owner = 0 ");

        bindVariables.add(this.currentSpace.getID());
        
        try {
            // Load and populate this collection
            loadGroups(query.toString(), bindVariables);
        
        } catch (GroupException ge) {
            throw new PersistenceException("Group load operation failed", ge);

        }

    }


    /**
     * Loads all groups available in the current space into this collection.
     * This includes owned groups and groups inherited from other spaces.
     * Clears out collection before loading.
     * @throws PersistenceException if there is a problem loading the
     * groups
     */
    public void loadAll() throws PersistenceException {
        StringBuffer query = new StringBuffer();
        ArrayList bindVariables = new ArrayList();
        
        clear();

        // Query to load groups by space for a specific space
        query.append(GroupDAO.getQueryLoadSpaceGroups());
        query.append("and shg.space_id = ? ");
        bindVariables.add(this.currentSpace.getID());

        try {
            // Load and populate this collection
            loadGroups(query.toString(), bindVariables);
        
        } catch (GroupException ge) {
            throw new PersistenceException("Group load operation failed", ge);

        }

    }


    /**
     * Loads all groups available in the current space that the specified
     * person is a direct member of.
     * This includes owned groups and groups inherited from other spaces.
     * Clears out collection before loading.
     * @param personID the id of the person for whom to get groups
     * @throws PersistenceException if there is a problem loading the
     * groups
     */
    public void loadAll(String personID) throws PersistenceException {
        StringBuffer query = new StringBuffer();
        ArrayList bindVariables = new ArrayList();
        
        clear();

        // Query to load groups by space where a person is a member of
        // those groups and space is current space.
        query.append(GroupDAO.getQueryLoadSpaceGroupsForPerson());
        query.append("and shg.space_id = ? ");
        query.append("and ghp.person_id = ? ");
        bindVariables.add(this.currentSpace.getID());
        bindVariables.add(personID);

        try {
            // Load and populate this collection
            loadGroups(query.toString(), bindVariables);

        } catch (GroupException ge) {
            throw new PersistenceException("Group load operation failed", ge);

        }

    }


    /**
     * Loads all groups available in the current space that the specified
     * person is a member of, including those groups they are indirectly a
     * member of.
     * This includes owned groups and groups inherited from other spaces.
     * Clears out collection before loading.
     * @param personID the id of the person for whom to get groups
     * @throws PersistenceException if there is a problem loading the
     * groups
     */
    public void loadAllIncludeIndirect(String personID) throws PersistenceException {

        StringBuffer query = new StringBuffer();
        ArrayList bindVariables = new ArrayList();
        
        clear();

        // Query to load groups by space where a person is a member of
        // those groups and space is current space.
        query.append(GroupDAO.getQueryLoadAllSpaceGroupsForPerson());
        query.append("and shg.space_id = ? ");
        // Due to complexity of query, two bind variables already defined
        bindVariables.add(personID);
        bindVariables.add(personID);
        bindVariables.add(this.currentSpace.getID());

        try {
            // Load and populate this collection
            loadGroups(query.toString(), bindVariables);

        } catch (GroupException ge) {
            throw new PersistenceException("Group load operation failed", ge);

        }

    }


    /**
     * Loads all groups across all spaces that the specified person is a member
     * of.
     * @param personID the id of the person to get their groups for
     * @throws PersistenceException if there is a problem loading
     */
    public void loadAllSpaces(String personID) throws PersistenceException {
        StringBuffer query = new StringBuffer();
        ArrayList bindVariables = new ArrayList();
        
        clear();

        // Query to load groups by space where a person is a member of
        // those groups.  Loads for all spaces.
        query.append(GroupDAO.getQueryLoadSpaceGroupsForPerson());
        query.append("and ghp.person_id = ? ");
        bindVariables.add(personID);

        try {
            // Load and populate this collection
            loadGroups(query.toString(), bindVariables);

        } catch (GroupException ge) {
            throw new PersistenceException("Group load operation failed", ge);

        }

    }


    /**
     * Loads all groups for all spaces that specified person is a member of
     * including groups that they are a member of due to being a member of
     * a sub-group.
     * @param personID the id of the person to get their groups for
     * @throws PersistenceException if there is a problem loading
     */
    public void loadAllSpacesAllGroups(String personID) throws PersistenceException {
        StringBuffer query = new StringBuffer();
        ArrayList bindVariables = new ArrayList();
        
        clear();

        // Query to load groups by space where a person is a member of
        // those groups, including all groups (both immediate membership
        // and membership through sub-group membership
        query.append(GroupDAO.getQueryLoadAllSpaceGroupsForPerson());
        bindVariables.add(personID);
        bindVariables.add(personID);

        try {
            // Load and populate this collection
            loadGroups(query.toString(), bindVariables);

        } catch (GroupException ge) {
            throw new PersistenceException("Group load operation failed", ge);

        }
        
    }


    /**
     * Loads groups based on the specified query and bind variables.
     * @param query the query to execute to perform the load, with one or
     * more parameters
     * @param bindVariables the bind variables to bind to the parameters
     * in the query
     * @throws PersistenceException if there is a problem reading the groups
     * from persistence store
     * @throws GroupException if there is a problem instantiating a group
     */
    private void loadGroups(String query, Collection bindVariables) 
            throws PersistenceException, GroupException {
        
        DBBean db = new DBBean();
        GroupProvider provider = new GroupProvider();

        try {
            Iterator it = null;
            int index = 0;

            // Prepare query and bind all variables
            db.prepareStatement(query);
            it = bindVariables.iterator();
            while (it.hasNext()) {
                db.pstmt.setString(++index, (String) it.next());
            }

            // Execute the query
            db.executePrepared();

            // Populate group objects based on the results of the query
            // and add to this collection
            while (db.result.next()) {
                // Construct group based on the group type
                Group group = provider.newGroup(GroupTypeID.forID(db.result.getString("group_type_id")));
                GroupDAO.populateGroup(db.result, group);
                add(group);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(GroupCollection.class).error("GroupCollection.loadGroups() failed " + sqle);
            throw new PersistenceException("Group list load operation failed", sqle);
        
        } finally {
            db.release();
        
        }

    }


    /**
     * Returns an iterator over the principal groups in this collection.
     * @return an iterator for the principal groups in this collection
     */
    public Iterator iteratorPrincipalGroups() {
        GroupCollection principalGroups = new GroupCollection();
        Group group = null;
        
        Iterator it = iterator();
        while (it.hasNext()) {
            group = (Group) it.next();

            if (group.isPrincipal()) {
                principalGroups.add(group);
            }
        }

        return principalGroups.iterator();
    }


    /**
     * Indicates whether there is group in this collection with the specified
     * name. Ignores case.
     * @param name the name of the goup to look for
     * @return true if this collection contains a group with the specified name
     */
    public boolean containsGroupWithName(String name) {
        return containsGroupWithName(name, null);
    }

    /**
     * Indicates whether there is a group in this collection with the specified
     * name, ignoring the group with the specified id.  Ignores case.
     * @param name the name of the goup to look for
     * @param ignoreID the id of a group to ignore;  this is useful for checking
     * whether two groups have the same name;  the first group can be ignored
     * @return true if this collection contains a group with the specified name
     */
    public boolean containsGroupWithName(String name, String ignoreID) {
        Group group = null;
        boolean isMatchFound = false;
        
        // Iterate over all groups, stopping only when a match is found
        Iterator it = iterator();
        while (it.hasNext() & !isMatchFound) {
            group = (Group) it.next();

            if (!group.getID().equals(ignoreID) && group.getName().equalsIgnoreCase(name.trim())) {
                isMatchFound = true;
            }
        }

        return isMatchFound;

    }


    /**
     * Updates this collection of groups with each group's owning space.
     * After calling, <code>{@link Group#getOwningSpace}</code> for each group will
     * return a <code>Space</code>
     * @throws GroupException if there is a problem updating any groups
     */
    public void updateWithOwningSpace() throws GroupException {
        GroupProvider groupProvider = new GroupProvider();

        groupProvider.updateWithOwningSpace(this);

    }


    /**
     * Removes principal groups from this collection.
     */
    public void removePrincipalGroups() {

        // Constructs a new filter that matches PrincipalGroups
        // Removes groups based on that match
        removeGroups(
            new IFilter() {        
                public boolean isMatch(Object obj) {
                    if (obj instanceof PrincipalGroup) {
                        return true;
                    }
                    return false;
                }
            }
        );
    }


    /**
     * Removes groups from this collection based on the specified filter.
     * Those groups that MATCH in the filter are removed.
     * @param remvoeFilter the filter used to decided which groups to remove
     */
    private void removeGroups(IFilter removeFilter) {
        Iterator it = iterator();
        while (it.hasNext()) {
            Group group = (Group) it.next();

            if (removeFilter.isMatch(group)) {
                it.remove();
            }
        }
        
    }

    /**
     * Simple filtering mechanism.
     */
    private static interface IFilter {

        /**
         * Indicates whether the specified object matches the filter.
         * @return true if the object matches the filter; false otherwise
         */
        public boolean isMatch(Object obj);

    }
    
    /**
     * Loads all groups order by group name available in the current space into this collection.
     * This includes owned groups and groups inherited from other spaces.
     * Clears out collection before loading.
     * @throws PersistenceException if there is a problem loading the groups
     */
    public void loadAll(boolean isOrderBySort) throws PersistenceException {
    	StringBuffer query = new StringBuffer();
    	ArrayList bindVariables = new ArrayList();
    	clear();
    	// Query to load groups by space for a specific space and order by display name
    	query.append(GroupDAO.getQueryLoadSpaceGroups());
    	query.append(" and is_owner = 1 and shg.space_id = ? ");
    	bindVariables.add(this.currentSpace.getID());
    	if(isOrderBySort){
    		String orderBy = " g.principal_owner_display_name ";
    		query.append(" order by ? ");
    		bindVariables.add(orderBy);
    	} 

    	try {
    		// Load and populate this collection
    		loadGroups(query.toString(), bindVariables);
    		Collections.sort(this);
    	} catch (GroupException ge) {
    		throw new PersistenceException("Group load operation failed", ge);
    	}
    }
}
