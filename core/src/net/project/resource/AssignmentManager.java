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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.FinderFilter;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.NumberComparator;
import net.project.base.finder.NumberFilter;
import net.project.base.finder.StringDomainFilter;
import net.project.base.finder.TextComparator;
import net.project.base.finder.TextFilter;
import net.project.base.finder.WhereClauseFilter;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.filters.assignments.AssignmentTypesFilter;
import net.project.util.DateRange;

import org.apache.log4j.Logger;

/**
 * Provides a mechanism for loading and filtering assignments.
 *
 * @author AdamKlatzkin
 * @author Tim Morrow
 * @author Matthew Flower
 * @since Version 1.0
 */
public class AssignmentManager implements IXMLPersistence, Serializable {

    private String personID = null;
    private String objectID = null;
    private String primaryOwner = null;
    private String spaceID = null;

    /**
     * The loaded assignments.
     * Each element is an <code>Assignment</code>.
     */
    private final List assignments = new ArrayList();

    /** The assignment statuses on which to filter. */
    private List assignmentStatusFilter = new ArrayList();
    /** The assignment types on which to filter. */
    private final List assignmentTypesFilter = new ArrayList();
    /** The dates in which to show assignments. */
    private DateRange assignmentDateRange;
    /** Maximum assignments to load. 0 = all */
    private int maxAssignments = 0;
    /** Column to order by */
    private ColumnDefinition orderColumn = null;
    /** Direction to order on */
    private boolean orderDescending = false;
    /** Finder filters to add to the query. */
    private FinderFilterList filters = new FinderFilterList();

    /**
     * Indicates whether to limit task assignments to incomplete tasks only.
     * Otherwise, task assignments are loaded irrespective of the percent complete.
     * 10/22/03 - Tim.  Admitted kludge.
     * This is polluting AssignmentManager with object-type specific filtering
     * mechanism where currently it is purely assignment based, with no knowledge
     * of specific object types.
     */
    //sjmittal: yes this is removed as assignments can be for other object types also
//    private boolean isLimitTaskAssignmentsToIncompleteTasks = false;

    /**
     * Creates an empty AssignmentManager.
     */
    public AssignmentManager() {
        // Do nothing
    }

    /**
     * reset internal variables to default state.
     */
    public void reset() {
        // 07/02/03 - Tim
        // Why no spaceID or assignmentTypesFilter reset?
        // Is there code relying on this behavior?

        personID = null;
        objectID = null;
        primaryOwner = null;
        assignments.clear();
        assignmentStatusFilter.clear();
        assignmentDateRange = null;
        this.maxAssignments = 0;
        this.orderColumn = null;
        orderDescending = false;
//        this.isLimitTaskAssignmentsToIncompleteTasks = false;
        filters = new FinderFilterList();
    }


    /**
     * Specifies the ID of the person to use when filtering assignments.
     * @param personID the ID of the person
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /**
     * Specifies the ID of the object to use when filtering assignments.
     * @param objectID the ID of the object
     */
    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    /**
     * Specifies the primary owner flag to use when filtering assignments
     * Default is all.
     * Example: to locate all of the assignments person X has is the primary owner of you
     * would call setPrimaryOwner(true)
     *
     * @param po     true - only load assignments flaged as 'primary owner'
     *               false - only load assignments flaged as 'not primary owner'
     * @deprecated as of 7.6.2; No replacement.  Never used.
     * The Primary Owner flag is rarely set correctly.
     */
    public void setPrimaryOwner(boolean po) {
        if (po == true)
            primaryOwner = "1";
        else
            primaryOwner = "0";
    }

    /**
     * Specifies the ID of the space to use when filtering assignments.
     * @param spaceID the ID of the space
     */
    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }


    /**
     * Specifies status values to use when filtering assignments.
     * @param statuses an array where each element is an assignment status;
     * when null, any existing status values are cleared.  Only valid statuses
     * as specified by {@link Assignment#VALID_STATUSES} are added.
     * @deprecated as of Version 7.6.3.  Please use the overloaded version of
     * this method that passes in a List of <code>AssignmentStatus</code>
     * objects.
     */
    public void setStatusFilter(String[] statuses) {
        this.assignmentStatusFilter.clear();

        if (statuses != null) {
            // We only want to set the assignmentStatusFilter if they are valid
            for (int i = 0; i < statuses.length; i++) {
                AssignmentStatus status = AssignmentStatus.getForID(statuses[i]);
                if (status != null) {
                    assignmentStatusFilter.add(status);
                }
            }
        }
    }

    /**
     * Specifies status values to use when filtering assignments.
     *
     * @param statuses a <code>List</code> containing zero or more
     * <code>AssignmentStatus</code> objects.  When empty, the existing status
     * values are cleared.
     */
    public void setStatusFilter(List statuses) {
        this.assignmentStatusFilter = statuses;
    }

    /**
     * Specifies the only <code>AssignmentStatus</code> type to show when
     * loading assignments.
     *
     * @param status a <code>AssignmentStatus</code> which represents the type
     * which we will be able to load.  If this is null, all status types will
     * be loaded.
     */
    public void setStatusFilter(AssignmentStatus status) {
        this.assignmentStatusFilter.clear();

        if (status != null) {
            assignmentStatusFilter.add(status);
        }
    }

    /**
     * Sets the filter on AssignmentType to include only the specified
     * assignment type.
     *
     * @param assignmentType the type of assignment to filter on
     */
    public void setAssignmentTypeFilter(AssignmentType assignmentType) {
        setAssignmentTypesFilter(new AssignmentType[]{assignmentType});
    }

    /**
     * Get the date range in which you want to display assignments.  Any
     * assignment that occurs during this time frame will be displayed.
     *
     * @return a <code>DateRange</code> in which assignments to be loaded will
     * have work being done.
     */
    public DateRange getAssignmentDateRange() {
        return assignmentDateRange;
    }

    /**
     * Set the date range in which you want to display assignments.  Any
     * assignment that occurs during this time frame will be displayed.
     *
     * @param assignmentDateRange a <code>DateRange</code> object which indicates
     * the dates in which assignments that are going to be loaded will occur.
     */
    public void setAssignmentDateRange(DateRange assignmentDateRange) {
        this.assignmentDateRange = assignmentDateRange;
    }

    /**
     * Sets the filter on AssignmentType to include only those assignment types
     * specified.
     *
     * @param assignmentTypes the types of assignments to filter on
     */
    public void setAssignmentTypesFilter(AssignmentType[] assignmentTypes) {
        // Add all assignmentTypes in array to filter list
        assignmentTypesFilter.clear();
        assignmentTypesFilter.addAll(java.util.Arrays.asList(assignmentTypes));
    }

    /**
     * Sets the maximum number of assignments to load.
     * A value of 0 will cause all assignments to be loaded.
     *
     * @param maxAssignments the maximum number of assignments to load
     */
    public void setMaxAssignments(int maxAssignments) {
        this.maxAssignments = maxAssignments;
    }

    /**
     * Specifies which column to order by.
     *
     * @param column the column to order by
     */
    public void setOrderBy(ColumnDefinition column) {
        this.orderColumn = column;
    }

    /**
     * Indicates if the order by column indicated should be ordering the data in
     * ascending or descending order.
     *
     * @param isDescending
     */
    public void setOrderDescending(boolean isDescending) {
        this.orderDescending = isDescending;
    }

    /**
     * Indicates whether to limit task assignments (when task assignments are
     * included in the load) to tasks that are incomplete.
     * @param isLimit true means if task assignments are loaded, only assignments
     * to tasks that are not complete will be included; false means work percent
     * complete will be ignored
     */
//    public void setLimitTaskAssignmentsToIncompleteTasks(boolean isLimit) {
//        this.isLimitTaskAssignmentsToIncompleteTasks = isLimit;
//    }

    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<assignment_list>\n");
        for (int i = 0; i < assignments.size(); i++)
            xml.append(((Assignment) assignments.get(i)).getXMLBody());
        xml.append("</assignment_list>\n");
        return xml.toString();
    }


    /**
     * Converts the object to XML representation.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Load all assignments which match the parameters already indicated to this
     * class.
     * <p>
     * This method understands parameters set by calling the following
     * methods:
     *
     * <ul>
     * <li>{@link #setPersonID}</li>
     * <li>{@link #setPrimaryOwner}</li>
     * <li>{@link #setSpaceID}</li>
     * <li>{@link #setStatusFilter}</li>
     * <li>{@link #setAssignmentTypesFilter}</li>
     * </ul>
     * </p>
     * <p>
     * Includes only Active Tasks and Meetings that belong to Active spaces
     * and space invitations to Active spaces.
     * </p>
     * @throws PersistenceException if there is a problem loading
     */
    public void loadAssignments() throws PersistenceException {
        assignments.clear();

        AssignmentFinder finder = new AssignmentFinder();

        FinderFilterList filters = new FinderFilterList();
        filters.add(this.filters);

        // Construct the where clause
        // This is common to all assignment types
        try {
            if (objectID != null) {
                TextFilter objectIDFilter = new TextFilter("objectIDFilter", AssignmentFinder.OBJECT_ID_COLUMN, false);
                objectIDFilter.setSelected(true);
                objectIDFilter.setComparator((TextComparator)TextComparator.EQUALS);
                objectIDFilter.setValue(objectID);
                filters.add(objectIDFilter);                
            }
            
            if (personID != null) {
                TextFilter personFilter = new TextFilter("person_id", AssignmentFinder.PERSON_ID_COLUMN, false);
                personFilter.setSelected(true);
                personFilter.setComparator((TextComparator)TextComparator.EQUALS);
                personFilter.setValue(personID);
                filters.add(personFilter);
            }

            if (primaryOwner != null) {
                NumberFilter primaryOwnerFilter = new NumberFilter("primary_owner", AssignmentFinder.PRIMARY_OWNER_COLUMN, false);
                primaryOwnerFilter.setSelected(true);
                primaryOwnerFilter.setComparator(NumberComparator.EQUALS);
                primaryOwnerFilter.setNumber(Integer.parseInt(primaryOwner));
                filters.add(primaryOwnerFilter);
            }

            if (spaceID != null) {
                TextFilter spaceIDFilter = new TextFilter("space_id", AssignmentFinder.SPACE_ID_COLUMN, false);
                spaceIDFilter.setSelected(true);
                spaceIDFilter.setComparator((TextComparator)TextComparator.EQUALS);
                spaceIDFilter.setValue(spaceID);
                filters.add(spaceIDFilter);
            }
            if (assignmentStatusFilter != null && !assignmentStatusFilter.isEmpty()) {
                StringDomainFilter statusFilter = new StringDomainFilter("assignment_status", "", AssignmentFinder.STATUS_ID_COLUMN, (TextComparator)TextComparator.EQUALS);
                statusFilter.setSelected(true);

                String[] assignmentStatusStrings = new String[assignmentStatusFilter.size()];
                int i = 0;
                for (Iterator it = assignmentStatusFilter.iterator(); it.hasNext();) {
                    AssignmentStatus assignmentStatus = (AssignmentStatus) it.next();
                    assignmentStatusStrings[i++] = assignmentStatus.getID();
                }

                statusFilter.setSelectedValues(assignmentStatusStrings);
                filters.add(statusFilter);
            }
            if (assignmentDateRange != null) {
                filters.add(assignmentDateRange.getElapsedTimeFilter("assignment_date", AssignmentFinder.START_DATE_COLUMN, AssignmentFinder.END_DATE_COLUMN));
            }
//            if (isLimitTaskAssignmentsToIncompleteTasks) {
//                // To include only incomplete tasks we include
//                // milestones (which don't have work complete) or tasks with
//                // work percent complete < 100
//                WhereClauseFilter incompleteTasks = new WhereClauseFilter(
//                    "((tk.work = 0 and tk.is_milestone = 1 and" +
//                    " tk.percent_complete < 100) or (tk.work_percent_complete < 100))"
//                );
//                incompleteTasks.setSelected(true);
//                filters.add(incompleteTasks);
//            }

            if (assignmentTypesFilter != null && assignmentTypesFilter.size() > 0) {
                AssignmentTypesFilter types = new AssignmentTypesFilter("assignment_types");
                types.setAssignmentTypes(assignmentTypesFilter);
                filters.add(types);
            }

        } catch (DuplicateFilterIDException e) {
            throw new RuntimeException("Unexpected DuplicateFilterIDException");
        } catch (NumberFormatException e) {
            throw new RuntimeException("Unexpected NumberFormatException");
        }

        if (orderColumn != null) {
            FinderSorter sorter = new FinderSorter(orderColumn, orderDescending);
            finder.addFinderSorter(sorter);
        }

        finder.addFinderFilterList(filters);
        assignments.addAll(finder.findAll());
    }

    /**
     * Loads the assignments for the set object id.
     * setObjectID must be called before invoking this method.
     */
    public void loadAssigneesForObject() throws PersistenceException {
        assignments.clear();


        TextFilter objectIDFilter = new TextFilter("objectIDFilter", AssignmentFinder.OBJECT_ID_COLUMN, false);
        objectIDFilter.setSelected(true);
        objectIDFilter.setComparator((TextComparator)TextComparator.EQUALS);
        objectIDFilter.setValue(objectID);

        filters.add(objectIDFilter);
        loadAssignments();
        filters.remove("objectIDFilter");
    }

    /**
     * soft delete assignments for an object
     * must set object id before calling
     */
    public void deleteAssignmentsForObject() throws PersistenceException {
        StringBuffer sb = new StringBuffer();
        sb.append("update pn_assignment set record_status='D' where object_id=" + objectID);

        DBBean db = new DBBean();

        try {

            db.executeQuery(sb.toString());
            db.release();
        } catch (SQLException sqle) {
        	Logger.getLogger(AssignmentManager.class).error("AssignmentManager: Could not delete assignments for object: " + objectID);
            throw new PersistenceException("Could not delete assignments for object", sqle);
        } finally {
            db.release();
        }

    }

    /**
     * hard delete assignments for an object
     * must set object id before calling
     */
    public void hardDeleteAssignmentsForObject() throws PersistenceException {
        StringBuffer sb = new StringBuffer();
        sb.append("delete from pn_assignment where object_id=" + objectID);

        DBBean db = new DBBean();

        try {
            db.executeQuery(sb.toString());
            db.release();
        } catch (SQLException sqle) {
        	Logger.getLogger(AssignmentManager.class).error("AssignmentManager: Could not delete assignments for object: " + objectID);
            throw new PersistenceException("Could not delete assignments for object", sqle);
        } finally {
            db.release();
        }

    }

    public static void deleteAssignments(List assignmentsToDelete) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            deleteAssignments(db, assignmentsToDelete);
        } catch (SQLException sqle) {
            throw new PersistenceException(sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Delete one or more assignments that appear in the list of assignments to
     * delete.
     *
     * @param assignmentsToDelete a <code>List</code> of zero or more assignments
     * that need to be deleted.
     */
    public static void deleteAssignments(DBBean db, List assignmentsToDelete) throws SQLException {
        if (assignmentsToDelete.isEmpty()) {
            return;
        }

        StringBuffer identifierList = new StringBuffer();
        for (ListIterator it = assignmentsToDelete.listIterator(); it.hasNext();) {
            if (it.hasPrevious()) {
                identifierList.append(" or ");
            }

            Assignment assignment = (Assignment)it.next();
            identifierList.append(" (person_id = ").append(assignment.getPersonID()).append(" and ");
            identifierList.append("  object_id = ").append(assignment.getObjectID()).append(") ");
        }

        db.executeQuery(
            "update pn_assignment " +
            "set record_status = 'D' " +
            "where " +
            identifierList.toString()
        );
    }

    /**
     * Retrieve the loaded list of assignments
     *
     * @return ArrayList    contains loaded Assignment objects
     */
    public List getAssignments() {
        return assignments;
    }

    /**
     * Return a map of the assignments which maps person id to the assignment.
     *
     * @return a <code>Map</code> object which maps person id to assignments.
     */
    public Map getAssignmentMap() {
        Map assignmentsMap = new HashMap();

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            Assignment assignment = (Assignment)it.next();
            assignmentsMap.put(assignment.getPersonID(), assignment);
        }

        return assignmentsMap;
    }

    /**
     * Retrieve the number of assigments that have been loaded
     *
     * @return int  The number of assignments that have been loaded
     */
    public int getNumberOfAssignments() {
        return assignments.size();
    }

    /**
     * Return the assignment status constants in XML
     *
     * @return String of XML representation of the status constants
     */
    public String getStatusConstantsXML() throws PersistenceException {
        StringBuffer xml = new StringBuffer();
        StringBuffer query = new StringBuffer();
        query.append("select code_name, code from pn_global_domain where table_name = 'pn_assignment' and column_name='status_id'");

        DBBean db = new DBBean();

        try {
            db.executeQuery(query.toString());

            xml.append("<assignments_status_constants>\n");

            while (db.result.next()) {

                xml.append("<constant>\n");
                xml.append("<name>" + PropertyProvider.get(db.result.getString(1)) + "</name>\n");
                xml.append("<value>" + db.result.getString(2) + "</value>\n");
                xml.append("</constant>\n");
            }

            xml.append("</assignments_status_constants>\n");

        } catch (SQLException sqle) {
        	Logger.getLogger(AssignmentManager.class).error("AssignmentManager: Could not load status constants from the DB");
            throw new PersistenceException("Could not load Status Constants from the DB", sqle);
        } finally {
            db.release();
        }

        return xml.toString();
    }

    /**
     * Determine if a user is in the assignment list
     *
     * @return boolean     true if user is in list
     */
    public boolean isUserInAssignmentList(String userid) {
        for (int i = 0; i < assignments.size(); i++) {
            if (((Assignment) assignments.get(i)).getPersonID().equals(userid))
                return true;
        }

        return false;
    }

    /**
     * Add a list of filters to this query.
     *
     * @param filterList a <code>FinderFilterList</code> that we are going to use
     * to filter.
     */
    public void addFilters(FinderFilterList filterList) {
        filters.add(filterList);
    }

    /**
     * Add a single filter to the list of filters that will applied when
     * loadAssignments() is called.
     *
     * @param filter a <code>FinderFilter</code> that will applied when the
     * <code>loadAssignments()</code> is called.
     */
    public void addFilter(FinderFilter filter) {
        filters.add(filter);
    }
}
