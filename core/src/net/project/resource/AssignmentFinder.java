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
|    $Revision: 20857 $
|        $Date: 2010-05-14 15:56:53 -0300 (vie, 14 may 2010) $
|      $Author: avinash $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.resource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.Finder;
import net.project.base.finder.FinderSorter;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Finds assignments using the finder architecture. This gives calling classes
 * the ability to request assignments with a certain criteria without having to
 * know details like the name of database fields.
 * 
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class AssignmentFinder extends Finder {
	
	private static Logger log = Logger.getLogger(AssignmentFinder.class);
	
	public static ColumnDefinition PERSON_ID_COLUMN = new ColumnDefinition("a.person_id", "");
    public static ColumnDefinition ASSIGNOR_ID_COLUMN = new ColumnDefinition("a.assignor_id", "");    
	public static ColumnDefinition OBJECT_ID_COLUMN = new ColumnDefinition("a.object_id", "");
	public static ColumnDefinition SPACE_ID_COLUMN = new ColumnDefinition("a.space_id", "");
	public static ColumnDefinition STATUS_ID_COLUMN = new ColumnDefinition("a.status_id", "");
	public static ColumnDefinition OBJECT_TYPE_COLUMN = new ColumnDefinition("ob.object_type", "");
	public static ColumnDefinition SPACE_TYPE_COLUMN = new ColumnDefinition("assnspace.object_type", "");
	public static ColumnDefinition PERCENT_COMPLETE_COLUMN = new ColumnDefinition("a.percent_complete", "");
	public static ColumnDefinition PERCENT_ALLOCATED_COLUMN = new ColumnDefinition("a.percent_allocated", "");
	public static ColumnDefinition ROLE_COLUMN = new ColumnDefinition("a.role", "");
	public static ColumnDefinition PRIMARY_OWNER_COLUMN = new ColumnDefinition("a.is_primary_owner", "");
	public static ColumnDefinition START_DATE_COLUMN = new ColumnDefinition("a.start_date", "");
	public static ColumnDefinition END_DATE_COLUMN = new ColumnDefinition("a.end_date", "");
	public static ColumnDefinition DATE_CREATED_COLUMN = new ColumnDefinition("a.date_created", "");
	public static ColumnDefinition DATE_MODIFIED_COLUMN = new ColumnDefinition("a.modified_date", "");
	public static ColumnDefinition ACTUAL_FINISH_COLUMN = new ColumnDefinition("a.actual_finish", "");
	public static ColumnDefinition WORK_COLUMN = new ColumnDefinition("a.work", "");
	public static ColumnDefinition WORK_UNITS_COLUMN = new ColumnDefinition("a.work_units", "");
	public static ColumnDefinition DISPLAY_NAME_COLUMN = new ColumnDefinition("p.display_name", "");
	public static ColumnDefinition TIMEZONE_CODE_COLUMN = new ColumnDefinition("p.timezone_code", "");
	public static ColumnDefinition OBJECT_NAME_COLUMN = new ColumnDefinition("oname.name", "");
	public static ColumnDefinition IS_COMPLETE_COLUMN = new ColumnDefinition("a.is_complete", "");
	public static ColumnDefinition SPACE_NAME_COLUMN = new ColumnDefinition("assnspace.name", "");
	
//    public static ColumnDefinition TASK_TYPE_COLUMN = new ColumnDefinition("tk.task_type", "");
//    public static ColumnDefinition TASK_PERCENT_COMPLETE_COLUMN = new ColumnDefinition("tk.percent_complete", "");
//    public static ColumnDefinition TASK_WORK_PERCENT_COMPLETE_COLUMN = new ColumnDefinition("tk.work_percent_complete", "");

	/**
	 * Optimizer hint to improve performance of assignment query on Oracle 8i.
	 * <p>
     * BFD-1996 states that personal dashboard new items are slow
     * This problem only occurs on Oracle 8i; it seems the Rules Based Optimizer (RBO)
     * can't figure out a decent execution plan. Oracle 9i uses the Cost Based Optimizer
     * and a different execution plan.
	 * </p>
	 * <p>
     * This optimizer hint has been chosen through experimentation to force Oracle 8i
     * to produce an execution plan that closely matches that of Oracle 9i.
	 * </p>
	 * <p>
     * This problem will go away with Version 7.7 since at that time we drop support
     * for Oracle 8i.
	 * </p>
	 */
	private static final String OPTIMIZER_HINT = "/*+ use_nl(ob oname) use_nl(assnspace assnspacetype) */";

	/**
	 * This is the base of the SQL statement that we will use to query for
	 * assignments. In the best case scenario, this will be the only SQL
	 * statement that queries for assignments. For this reason, we need to be
	 * careful with performance.
	 */
    private static String SQL =
        "select " + OPTIMIZER_HINT +
        "  a.object_id, "+
        "  a.status_id, "+
        "  a.percent_allocated, "+
        "  a.role, "+
        "  a.is_primary_owner, "+
        "  a.start_date, "+
        "  a.end_date, "+
        "  a.actual_start, " +
        "  a.actual_finish, "+
        "  a.estimated_finish, "+
        "  a.space_id, "+
        "  a.work, "+
        "  a.work_units, " +
        "  a.work_complete, " +
        "  a.work_complete_units, " +
        "  a.modified_by, " +
        "  a.modified_date, "+
        "  modname.display_name as modified_by_display_name, "+
        "  a.date_created, "+
        "  a.is_complete, "+
        "  a.person_id, "+
        "  a.assignor_id, "+
        "  a.percent_complete, "+
        "  ob.object_type, "+
        "  p.display_name, "+
        "  p.timezone_code, "+
        "  util.get_name(a.object_id) as object_name, "+
        "  assnspace.name as space_name, "+
        "  assnspacetype.object_type as space_type, "+
        "  ap.display_name "+
			// " tk.percent_complete," +
			// " tk.work as total_work," +
			// " tk.work_units as total_work_units, " +
			// " tk.work_complete as total_work_complete, " +
			// " tk.work_complete_units as total_work_complete_units "+
        "from "+
        "  pn_assignment a, "+
        "  pn_object ob, "+
        "  pn_person_view p, "+
        "  pn_person_view ap, "+
			// " pn_task tk, " +
        "  pn_person modname, "+
        	// " pn_object_name oname, "+
        "  pn_object_name assnspace, "+
        "  pn_object assnspacetype "+
        "where "+
        "  a.object_id = ob.object_id "+
        "  and ob.record_status = 'A' "+
        	// " and a.object_id = oname.object_id "+
        "  and a.modified_by = modname.person_id(+) "+
        "  and p.person_id = a.person_id "+
        "  and ap.person_id(+) = a.assignor_id "+
			// " and a.object_id = tk.task_id(+) "+
			// " and tk.record_status(+) = 'A' "+
        "  and a.space_id = assnspace.object_id "+
        "  and assnspace.object_id = assnspacetype.object_id "+
        "  and assnspacetype.record_status = 'A' "+
        "  and a.record_status = 'A' ";

	private static int currentColID = 0;
	public static int OBJECT_ID_COL_ID = ++currentColID;
	public static int STATUS_ID_COL_ID = ++currentColID;
	public static int PERCENT_ALLOCATED_COL_ID = ++currentColID;
	public static int ROLE_COL_ID = ++currentColID;
	public static int IS_PRIMARY_OWNER_COL_ID = ++currentColID;
	public static int START_DATE_COL_ID = ++currentColID;
	public static int END_DATE_COL_ID = ++currentColID;
	public static int ACTUAL_START_COL_ID = ++currentColID;
	public static int ACTUAL_FINISH_COL_ID = ++currentColID;
	public static int ESTIMATED_FINISH_COL_ID = ++currentColID;
	public static int SPACE_ID_COL_ID = ++currentColID;
	public static int WORK_COL_ID = ++currentColID;
	public static int WORK_UNITS_COL_ID = ++currentColID;
	public static int WORK_COMPLETE_COL_ID = ++currentColID;
	public static int WORK_COMPLETE_UNITS_COL_ID = ++currentColID;
	public static int MODIFIED_BY_COL_ID = ++currentColID;
	public static int MODIFIED_DATE_COL_ID = ++currentColID;
	public static int MODIFIED_BY_DISPLAY_NAME = ++currentColID;
	public static int DATE_CREATED_COL_ID = ++currentColID;
	public static int IS_COMPLETE_COL_ID = ++currentColID;
	public static int PERSON_ID_COL_ID = ++currentColID;
    public static int ASSIGNOR_ID_COL_ID = ++currentColID;    
	public static int PERCENT_COMPLETE_COL_ID = ++currentColID;
	public static int OBJECT_TYPE_COL_ID = ++currentColID;
	public static int DISPLAY_NAME_COL_ID = ++currentColID;
	public static int TIMEZONE_CODE_COL_ID = ++currentColID;
	public static int OBJECT_NAME_COL_ID = ++currentColID;
	public static int SPACE_NAME_COL_ID = ++currentColID;
	public static int SPACE_TYPE_COL_ID = ++currentColID;
	public static int ASSIGNOR_NAME_COL_ID = ++currentColID;
	public static int TASK_TYPE_COLUMN = ++currentColID;
	
	// public static int TASK_PERCENT_COMPLETE_COL_ID = ++currentColID;
	// public static int TOTAL_WORK_COL_ID = ++currentColID;
	// public static int TOTAL_WORK_UNITS_COL_ID = ++currentColID;
	// public static int TOTAL_WORK_COMPLETE_COL_ID = ++currentColID;
	// public static int TOTAL_WORK_COMPLETE_UNITS_COL_ID = ++currentColID;

	protected String getBaseSQLStatement() {
		return SQL;
	}

	protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException {
		Assignment assignment = null;
		AssignmentType assignmentType = AssignmentType.forObjectType(databaseResults.getString("object_type"));
        if (assignmentType == null) {
			if (log.isDebugEnabled()){
				log.debug(" AssignmentType.forObjectType returned null !");
			}
		} else {	
    		assignment = assignmentType.newAssignment();
    		assignment.populate(databaseResults);
        }
		return assignment;
	}

	/**
	 * Find all assignments which correspond to the finder filters that have
	 * already been applied. (Or return them all if none have been applied).
	 * 
     * @return a <code>Collection</code> of {@link net.project.resource.Assignment}
     * objects.
     * @throws PersistenceException if there is a problem loading the
     * assignments from the database.
	 */
	public Collection findAll() throws PersistenceException {
		FinderSorter finderSorter = new FinderSorter(new ColumnDefinition("p.display_name","AssignedPersonName"),false);
		finderSorters.add(finderSorter);
		return super.loadFromDB();
	}
}
