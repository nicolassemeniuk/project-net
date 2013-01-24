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
package net.project.security.group;

import java.sql.SQLException;

import net.project.persistence.PersistenceException;
import net.project.util.Conversion;

import org.apache.log4j.Logger;

/**
 * Provides data access methods for Groups.
 * 
 * @author Tim
 * @since gecko
 */
public class GroupDAO
        implements java.io.Serializable {

    /**
     * Returns a <code>SELECT</code> statement that loads all properties
     * from <code>PN_GROUP</code>. No <code>WHERE</code> clause is included.
     * @return the query
     */
    public static String getQueryLoadGroup() {
        StringBuffer query = new StringBuffer();

        query.append("select g.group_id, g.group_name, g.group_desc, g.is_principal, ");
        query.append("g.is_system_group, g.record_status, g.group_type_id, g.member_count, ");
        query.append("g.principal_owner_id, g.principal_owner_display_name ");
        query.append("from pn_group_view g ");

        return query.toString();
    }


    /**
     * Returns a <code>SELECT</code> statement that loads all properties
     * from <code>PN_GROUP</code> and <code>PN_SPACE_HAS_GROUP</code> joining on
     * <code>GROUP_ID</code>.  Includes a <code>WHERE</code> clause.
     * @return the query
     */
    public static String getQueryLoadSpaceGroups() {
        StringBuffer query = new StringBuffer();

        query.append("select shg.space_id, shg.is_owner, g.group_id, g.group_name, ");
        query.append("g.group_desc, g.is_principal, g.is_system_group, ");
        query.append("g.record_status, g.group_type_id, g.member_count, ");
        query.append("g.principal_owner_id, g.principal_owner_display_name ");
        query.append("from pn_group_view g, pn_space_has_group shg ");
        query.append("where g.group_id = shg.group_id ");
        query.append("and g.record_status = 'A' ");

        return query.toString();
    }


    /**
     * Returns a <code>SELECT</code> statement that loads all properties
     * from <code>PN_GROUP</code>, joining on <code>PN_SPACE_HAS_GROUP</code> 
     * and <code>PN_GROUP_HAS_PERSON</code>.  Includes a <code>WHERE</code> clause.
     * This query is useful for finding the groups that a person is an immediate
     * member of.
     * @return the query
     */
    public static String getQueryLoadSpaceGroupsForPerson() {
        StringBuffer query = new StringBuffer();

        query.append("select distinct shg.space_id, shg.is_owner, g.group_id, g.group_name, ");
        query.append("g.group_desc, g.is_principal, g.is_system_group, ");
        query.append("g.record_status, g.group_type_id, g.member_count, ");
        query.append("g.principal_owner_id, g.principal_owner_display_name ");
        query.append("from pn_group_view g, pn_space_has_group shg, pn_group_has_person ghp ");
        query.append("where g.group_id = shg.group_id ");
        query.append("and g.record_status = 'A' ");
        query.append("and ghp.group_id = shg.group_id ");

        return query.toString();
    }


    /**
     * Returns query whose resultset will provide all group properties
     * and joins with <code>PN_GROUP_HAS_GROUP GHG</code>.
     * Use this query to load a collection of groups that are a member of
     * another group.
     * @return the query
     */
    public static String getQueryLoadMemberGroups() {
        StringBuffer query = new StringBuffer();

        query.append("select ghg.group_id as owning_group_id, g.group_id, g.group_name, g.group_desc, g.is_principal, ");
        query.append("g.is_system_group, g.record_status, g.group_type_id, g.member_count, ");
        query.append("g.principal_owner_id, g.principal_owner_display_name ");
        query.append("from pn_group_has_group ghg, pn_group_view g ");
        query.append("where g.group_id = ghg.member_group_id ");

        return query.toString();
    }


    /**
     * Returns query whose resultset will provide all group properties
     * and joins with <code>PN_GROUP_HAS_GROUP GHG</code>.
     * Use this query to load a collection of groups that are a member of
     * another group.
     * @return the query
     */
    public static String getQueryLoadMemberGroupsWithSpace() {
        StringBuffer query = new StringBuffer();

        // This query selects group information about groups that are a member
        // of another group (ghg.owning_group_id)
        // It joins with space to allow filtering on a space (usually the
        // space that owns the group)
        // It returns the "is_owner" flag based on whether the MEMBER group
        // is owned by the space that owns the group
        query.append("select shg.space_id, shg.is_owner, ghg.group_id as owning_group_id, ");
        query.append("g.group_id, g.group_name, g.group_desc, g.is_principal, ");
        query.append("g.is_system_group, g.record_status, g.group_type_id, g.member_count, ");
        query.append("g.principal_owner_id, g.principal_owner_display_name ");
        query.append("from pn_group_has_group ghg, pn_group_view g, pn_space_has_group shg ");
        query.append("where g.group_id = ghg.member_group_id ");
        query.append("and ghg.member_group_id = shg.group_id ");

        return query.toString();
    }


    /**
     * Returns a query whose resultset will provide all group properties
     * and <code>SPACE_ID</code> for all groups that a person is a member
     * of, including groups that they are not a direct member of.  This query
     * is used for finding all the groups for which security should be checked
     * for a person.  Due to the complexity of the query, the following bind
     * variable has been defined:<br>
     * 1 - id of person<br>
     * 2 - id of person<br>
     * @return the query
     */
    public static String getQueryLoadAllSpaceGroupsForPerson() {
        StringBuffer query = new StringBuffer();

        // Select all the group information
        query.append("select shg.space_id, shg.is_owner, g.group_id, g.group_name, g.group_desc, g.is_principal, g.is_system_group, ");
        query.append("g.record_status, g.group_type_id, g.member_count, g.principal_owner_id, g.principal_owner_display_name ");
        query.append("from pn_space_has_group shg, pn_group_view g, ");
        query.append("(" + getQueryFetchAllGroupIDsForPerson() + ") all_distinct_groups ");
        query.append("where g.group_id = all_distinct_groups.group_id ");
        query.append("and shg.group_id = g.group_id ");
        query.append("and g.record_status = 'A' ");

        return query.toString();
    }


    /**
     * Returns all distinct <code>group_id</code>s that a person is a member of,
     * including membership implied by being a member of a sub-group.
     * Requires <code>person_id</code> to be bound twice to this query.
     * @return the query
     */
    public static String getQueryFetchAllGroupIDsForPerson() {
        StringBuffer query = new StringBuffer();

        // From the distinct groups from the inner query
        // Unioned with the groups that they are a direct member of (but those
        // groups are not members of other groups)
        query.append("select distinct g.group_id ");
        query.append("from pn_group g, ");
        
        // Inner query to find the groups that a person is a member of
        // This query returns child_group_id and parent_group_id which must be
        // combined and duplicates removed to find the actual set of groups
        // Explanation:
        //     Select groups and their parents starting with groups that
        //     the person is an immediate member of
        //     connecting to the parents of those groups
        query.append("    (select distinct ghg.member_group_id as child_group_id, ");
        query.append("     ghg.group_id as parent_group_id, decode(level, 1, 1, 0) as is_immediate ");
        query.append("     from pn_group_has_group ghg ");
        query.append("     start with exists (select 1 ");
        query.append("                        from pn_group_has_person ghp ");
        query.append("                        where ghp.group_id = ghg.member_group_id ");
        query.append("                        and ghp.person_id = ? ) ");
        query.append("      connect by ghg.member_group_id = prior ghg.group_id ");
        query.append("    ) all_member_groups ");
        
        query.append("where g.group_id = all_member_groups.child_group_id ");
        query.append("or g.group_id = all_member_groups.parent_group_id ");
        query.append("union ");
        query.append("select ghp.group_id from pn_group_has_person ghp where ghp.person_id = ? ");
        query.append("union ");
        query.append("select grp1.group_id from pn_group grp1 where group_type_id = 600 ");

        return query.toString();
    }


    /**
     * Retuns an <code>INSERT</code> statement to add a group to a group.
     * Statement:<code><pre>
     * insert into pn_group_has_group (group_id, member_group_id) values (?, ?)
     * </pre></code>
     * @return the insert statement
     */
    public static String getQueryAddGroup() {
        StringBuffer query = new StringBuffer();
        query.append("insert into pn_group_has_group ");
        query.append("(group_id, member_group_id) ");
        query.append("values (?, ?) ");

        return query.toString();
    }


    /**
     * Retuns an <code>INSERT</code> statement to add a person to a group.
     * Statement:<code><pre>
     * insert into pn_group_has_person (group_id, person_id) values (?, ?)
     * </pre></code>
     * @return the insert statement
     */
    public static String getQueryAddPerson() {
        StringBuffer query = new StringBuffer();
        query.append("insert into pn_group_has_person ");
        query.append("(group_id, person_id) ");
        query.append("values (?, ?) ");

        return query.toString();
    }


    /**
     * Returns a <code>DELETE</code> statement to delete a group from a group.
     * Statement:<code><pre>
     * delete from pn_group_has_group where group_id = ? and member_group_id = ?
     * </pre></code>
     * @return the query
     */
    public static String getQueryRemoveGroup() {
        StringBuffer query = new StringBuffer();
        query.append("delete from pn_group_has_group ");
        query.append("where group_id = ? and member_group_id = ? ");
        return query.toString();
    }


    /**
     * Returns a <code>DELETE</code> statement to delete a person from a group.
     * Statement:<code><pre>
     * delete from pn_group_has_person where group_id = ? and person_id = ?
     * </pre></code>
     * @return the query
     */
    public static String getQueryRemovePerson() {
        StringBuffer query = new StringBuffer();
        query.append("delete from pn_group_has_person ");
        query.append("where group_id = ? and person_id = ? ");
        return query.toString();
    }


    /**
     * Returns a <code>SELECT</code> statement to fetch the space id of
     * a space for a group.
     * @return the query
     */
    public static String getQueryFetchSpaceForGroup() {
        StringBuffer query = new StringBuffer();
        query.append("select shg.space_id, shg.is_owner, shg.group_id ");
        query.append("from pn_space_has_group shg ");
        return query.toString();
    }
    

    /**
     * Populates the specified group object with the current row in the
     * specified resultset.
     * @param result the result set that is set to a current result row
     * @param group the group to populate
     * @throws SQLException if there is a problem reading the result row
     * @throws PersistenceException if there is a problem with the data,
     * for example if the group type in the database differs from the group
     * type of the specified group object.
     */
    public static void populateGroup(java.sql.ResultSet result, Group group) 
            throws SQLException, PersistenceException {
        
        String groupTypeInternalID = null;
        GroupTypeID groupTypeID = null;
        
        try {
            group.setSpaceID(result.getString("space_id"));
            group.setSpaceOwner(Conversion.toBoolean(result.getInt("is_owner")));
        
        } catch (SQLException sqle) {
            // No column called space_id
            // Simply continue
        }


        // Get the standard properties
        group.setID(result.getString("group_id"));
        group.setName(result.getString("group_name"));
        group.setDescription(result.getString("group_desc"));
        group.setMemberCount(result.getInt("member_count"));

        // Get the group type and check that it is both known, and
        // matches that of this group's type
        groupTypeInternalID = result.getString("group_type_id");
        groupTypeID = GroupTypeID.forID(groupTypeInternalID);

        if (groupTypeID == null) {
            // Id in database corresponds to no known group type object
        	Logger.getLogger(GroupDAO.class).error("Group.load() group with id: " + group.getID() + " has unknown group type: " + groupTypeInternalID);
            throw new PersistenceException("Group load operation failed. Unknown group type.");

        } else {
            // Check group type matches this object's.
            if (!groupTypeID.equals(group.getGroupTypeID())) {
                // Group type from id in database does not match this group's
                // type.  This might occur from an attempt to load
                // a user defined group into a SpaceAdministratorGroup
                // object.
            	Logger.getLogger(GroupDAO.class).error("Group.load() group with id: " + group.getID() + 
                        " has group type mismatch.  Found: " + groupTypeInternalID + 
                        ", expected: " + groupTypeID.getID());
                throw new PersistenceException("Group load operation failed. Mismatched group type.");
            }

            // Overrides the principal group name with the display name of
            // the owner
            if (group.getGroupTypeID().equals(GroupTypeID.PRINCIPAL)) {
                group.setName(result.getString("principal_owner_display_name"));
                ((PrincipalGroup) group).setOwnerID(result.getString("principal_owner_id"));
            }
        }

        // If we got this far, the load succeeded
        group.setLoaded(true);
    
    }

}
