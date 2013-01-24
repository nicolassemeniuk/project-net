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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 19034 $
|       $Date: 2009-03-24 13:14:25 -0300 (mar, 24 mar 2009) $
|     $Author: avinash $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.report;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.project.base.PnetRuntimeException;
import net.project.base.finder.FinderFilter;
import net.project.base.finder.IFinderIngredientVisitor;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.resource.PersonFinder;
import net.project.space.Space;
import net.project.util.VisitException;

/**
 * Filter which displays a list of users in the current space.
 *
 * @author Matthew Flower
 */
public class UserFilter extends FinderFilter {
    private static String SHOW_TASKS_FOR_THESE_USERS = "prm.schedule.report.common.showtasksfortheseusers.name"; //"Show tasks for these users";
    /** The list of users to display in the filter. */
    private final List userList = new LinkedList();
    /** Load all users in the system versus just ones for the current space. */
    private boolean loadSpaceUsersOnly = true;

    /**
     * Default constructor.
     *
     * @param id a <code>String</code> value which uniquely identifies this
     * filter.
     * @param userSource a <code>Space</code> value that we will use to load
     * users for the list of users to filter on.
     */
    public UserFilter(String id, Space userSource) {
        super(id, SHOW_TASKS_FOR_THESE_USERS);

        //Load all people from the space into a list so we can filter on them.
        loadUsers(userSource);
    }

    /**
     * Default constructor.
     *
     * @param id a <code>String</code> value which uniquely identifies this
     * filter.
     * @param userSource a <code>Space</code> value that we will use to load
     * users for the list of users to filter on.
     * @param loadSpaceUsersOnly a <code>boolean</code> indicating that we should
     * only load users in the given space.
     */
    public UserFilter(String id, Space userSource, boolean loadSpaceUsersOnly) {
        super(id, SHOW_TASKS_FOR_THESE_USERS);
        this.loadSpaceUsersOnly = loadSpaceUsersOnly;

        //Load all people from the space into a list so we can filter on them.
        loadUsers(userSource);
    }

    /**
     * Default constructor.
     *
     * @param id a <code>String</code> value which uniquely identifies this
     * filter.
     * @param nameToken - The token (property) that will allow this filter to
     * look up a human-readable representation of this token.
     * @param userSource a <code>Space</code> value that we will use to load
     * users for the list of users to filter on.
     */
    public UserFilter(String id, String nameToken, Space userSource) {
        super(id, nameToken);

        //Load all people from the space into a list so we can filter on them.
        loadUsers(userSource);
    }

    /**
     * Load all users in a space into the internal list of users.
     *
     * @param space a <code>Space</code> value that will be the source for
     * users.
     */
    private void loadUsers(Space space) {
        PersonFinder finder = new PersonFinder();
        List persons;

        try {
            if (loadSpaceUsersOnly) {
                persons = finder.findForSpace(space);
            } else {
                persons = finder.find();
            }
        } catch (PersistenceException e) {
            throw new PnetRuntimeException("Unexpected error loading users: " + e, e);
        }

        for (Iterator it = persons.iterator(); it.hasNext();) {
            Person person = (Person)it.next();
            userList.add(new FilterUser(person.getID(), person.getDisplayName()));
        }
    }

    /**
     * Get a where clause for this filter that can be applied to a finder.  This
     * where clause will be empty (not null) if the filter isn't selected.
     *
     * @return a <code>String</code> containing a where clause, or an empty
     * string if the filter isn't selected.
     */
    public String getWhereClause() {
        StringBuffer whereClause = new StringBuffer();

        if (isSelected()) {
            String idList = formatSelectedUsersIDCsv();

            //Construct the where clause given the list of ids we just produced
            if (idList.length() > 0) {
                whereClause.append("t.task_id in (select a.object_id from pn_assignment a where (");
                whereClause.append(idList);
                whereClause.append(") and a.record_status = 'A')");
            }
        }

        //Return our newly constructed where clause to the caller
        return whereClause.toString();
    }

    /**
     * Get the list of users that we are going to display in the list box.
     *
     * @return a <code>List</code> value containing zero or more
     * <code>FilterUser</code> objects.
     */
    public final List getUserList() {
        return userList;
    }

    /**
     * Get a description of what this filter does.  For example, if it filters
     * tasks assigned to me, this string would say "Tasks Assigned To Me".  If
     * it filtered on date range, this method should return "Tasks with finish
     * dates between 01/02/2003 and 01/02/2005"
     *
     * @return a <code>String</code> containing a human-readable description of
     * what the filter does.
     */
    public String getFilterDescription() {
        StringBuffer nameList = new StringBuffer();

        nameList.append(formatSelectedUsersDisplayNames());
        if (nameList.length() == 0) {
            nameList.append(PropertyProvider.get("prm.schedule.report.common.filter.user.anyuser"));
        }

        return PropertyProvider.get("prm.schedule.report.common.filter.user.description"
            , nameList.toString());
    }

    /**
     * Clears the modifiable properties of this FinderFilter.  Invokes when
     * {@link #clear} is invoked.
     */
    protected void clearProperties() {
        //Iterate through the enclosed users and deselect them all.
        for (Iterator it = userList.iterator(); it.hasNext();) {
            FilterUser filterUser = (FilterUser)it.next();
            filterUser.setSelected(false);
        }
    }

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitUserFilter(this);
    }

    /**
     * Returns a list of users who are selected.
     * @return a list where each element is a <code>FilterUser</code> whose
     * <code>isSelected</code> property is true
     */
    protected final List getSelectedUsers() {
        //Create list of selected users
        List selectedUsers = new LinkedList();
        for (Iterator it = getUserList().iterator(); it.hasNext();) {
            FilterUser filterUser = (FilterUser)it.next();
            if (filterUser.isSelected()) {
                selectedUsers.add(filterUser);
            }
        }
        return selectedUsers;
    }

    /**
     * Helper method that formats the user list IDs as a comma separated list
     * of values, suitable for inclusion in a SQL <code>in</code> clause.
     * @return a comma separated list of IDs of the selected users
     * or empty string if there are no users or no selected users
     */
    protected final String formatSelectedUsersIDCsv() {
        StringBuffer idList = new StringBuffer();

        //Iterate through the userlist to find the selected ID's
        for (Iterator it = getSelectedUsers().iterator(); it.hasNext();) {
            FilterUser user = (FilterUser)it.next();
            if (idList.length() > 0) {
                idList.append(" or ");
            }
            idList.append("("+getColumnName()+" = " + user.getID() + ")");
        }
        return idList.toString();
    }

    protected String getColumnName() {
        return "a.person_id";
    }

    /**
     * Formats the selected users' display names, suitable for describing this filter.
     * @return the formatted display names; this is a list of names separated by
     * the token <code>prm.global.listseparator.symbol</code>, with the final name
     * preceded by <code>prm.global.listseparator.or</code>, for example:
     * <code>User1, User2 or User2</code>
     */
    protected final String formatSelectedUsersDisplayNames() {
        StringBuffer nameList = new StringBuffer();
        for (Iterator it = getSelectedUsers().iterator(); it.hasNext();) {
            FilterUser user = (FilterUser)it.next();

            if ((!it.hasNext()) && (nameList.length() > 0)) {
                nameList.append(" ").append(PropertyProvider.get("prm.global.listseparator.or"));
            } else if (nameList.length() > 0) {
                nameList.append(PropertyProvider.get("prm.global.listseparator.symbol"));
            }
            nameList.append(user.getDisplayName());
        }
        return nameList.toString();
    }
}
