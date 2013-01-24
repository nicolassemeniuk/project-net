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
|   $Revision: 20658 $
|       $Date: 2010-04-01 01:53:03 -0300 (jue, 01 abr 2010) $
|     $Author: dpatil $
|
+-----------------------------------------------------------------------------*/
package net.project.resource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.ObjectType;
import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.Finder;
import net.project.base.finder.FinderListenerAdapter;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskFinder;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.DateRange;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

public class AssignmentWorkLogFinder extends Finder {
    
    private String SELECT = "select " +    
	    " aw.assignment_work_id, aw. object_id, oname.name,  aw.person_id, personname.name , " +
	    " aw.work_start, aw.work_end, aw.comments, aw.work, aw.work_units , " +
	    " aw.work_remaining, aw. work_remaining_units, " +
	    " aw.percent_complete,  aw.log_date, aw.modified_by AS modified_by_id, " +
	    " modname.name AS  modified_by, aw.scheduled_work AS  total_work, " +
	    " aw.scheduled_work_units AS total_work_units , a.space_id , obj.object_type  ";
    	
    private String FROM =  " from " +
	    " pn_assignment a , " +
	    " pn_assignment_work aw, " +
	    " pn_object_name oname, " +
	    " pn_object_name modname , " +
	    " pn_object_name personname, " +
	    " pn_object obj ";
    	
    private String WHERE = " where " +
	    " a.object_id(+) = aw.object_id AND " +
	    " a.person_id(+) = aw.person_id AND " +
	    " aw. object_id = oname.object_id AND " +
	    " aw .modified_by = modname.object_id AND " +
	    " aw.person_id = personname.object_id AND " +
	    " aw.object_id = obj.object_id AND " +
	    " obj.record_status =  'A' ";
    
    private String SQL =  SELECT + FROM + WHERE ;

    private static int colNo = 1; 

    private static final int ASSIGNMENT_WORK_ID_COL_NO = colNo++;
    private static final int OBJECT_ID_COL_NO = colNo++;
    public static final ColumnDefinition OBJECT_ID_COL = new ColumnDefinition("object_id", "");
    private static final int ASSIGNMENT_NAME_COL_NO = colNo++;
    public static final ColumnDefinition ASSIGNMENT_NAME_COL = new ColumnDefinition("oname.name", "Assignment");
    private static final int PERSON_ID_COL_NO = colNo++;
    public static final ColumnDefinition PERSON_ID_COL = new ColumnDefinition("person_id", "");
    private static final int PERSON_NAME_COL_NO = colNo++;
    private static final int WORK_START_COL_NO = colNo++;
    public static final ColumnDefinition WORK_START_COL = new ColumnDefinition("work_start", "Work Start");
    private static final int WORK_END_COL_NO = colNo++;
    public static final ColumnDefinition WORK_END_COL = new ColumnDefinition("work_end", "Work End");
    private static final int COMMENTS_COL_NO = colNo++;
    private static final int WORK_COL_NO = colNo++;
    private static final int WORK_UNITS_COL_NO = colNo++;
    private static final int WORK_REMAINING_COL_NO = colNo++;
    private static final int WORK_REMAINING_UNITS_COL_NO = colNo++;
    private static final int PERCENT_COMPLETE_COL_NO = colNo++;
    public static final ColumnDefinition PERCENT_COMPLETE_COL = new ColumnDefinition("percent_complete", "Percent Complete");
    private static final int LOG_DATE_COL_NO = colNo++;
    public static final ColumnDefinition LOG_DATE_COL = new ColumnDefinition("log_date", "Log Date");
    private static final int MODIFIED_BY_ID_COL_NO = colNo++;
    private static final int MODIFIED_BY_NAME_COL_NO = colNo++;
    private static final int TOTAL_WORK_COL_NO = colNo++;
    private static final int TOTAL_WORK_UNITS_COL_NO = colNo++;
    public static final ColumnDefinition SPACE_ID_COL = new ColumnDefinition("a.space_id", "Workspace");
    
    /**
     * Get the SQL statement which without any additional where clauses, group
     * by, or order by statements. <p> The SQL statement will include a
     * <code>SELECT</code> part, a <code>FROM</code> part and the
     * <code>WHERE</code> keyword.  It will include any conditional expressions
     * required to perform joins. All additional conditions will be appended
     * with an <code>AND</code> operator. </p>
     *
     * @return a <code>String</code> value containing the default sql statement
     *         without any additional adornments.
     */
    protected String getBaseSQLStatement() {
        return SQL;
    }

    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link net.project.schedule.Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param results a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed to be
     * on a current row.
     * @return a <code>Object</code> subclass specific to your finder that has
     *         been populated with data.
     * @throws java.sql.SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet results) throws SQLException {
        AssignmentWorkLogEntry e = new AssignmentWorkLogEntry();
        e.setAssignmentWorkID(results.getString(ASSIGNMENT_WORK_ID_COL_NO));
        e.setObjectID(results.getString(OBJECT_ID_COL_NO));
        e.setObjectName(results.getString(ASSIGNMENT_NAME_COL_NO));
        e.setAssigneeID(results.getString(PERSON_ID_COL_NO));
        e.setAssigneeName(results.getString(PERSON_NAME_COL_NO));
        e.setHoursWorked(DatabaseUtils.getTimeQuantity(results, WORK_COL_NO, WORK_UNITS_COL_NO));
        e.setPercentComplete(results.getBigDecimal(PERCENT_COMPLETE_COL_NO));
        e.setLogDate(DatabaseUtils.getTimestamp(results, LOG_DATE_COL_NO));
        e.setModifiedByID(results.getString(MODIFIED_BY_ID_COL_NO));
        e.setModifiedBy(results.getString(MODIFIED_BY_NAME_COL_NO));
        e.setObjectType(results.getString("object_type"));
       	if(SQL.contains("seq_num")){
    		e.setSequenceNo(results.getInt("seq_num"));
    		e.setClassId(results.getString("class_id"));
       	}
        e.setTotalAssignmentWork(DatabaseUtils.getTimeQuantity(results, TOTAL_WORK_COL_NO, TOTAL_WORK_UNITS_COL_NO));
        e.setTotalRemainingWork(DatabaseUtils.getTimeQuantity(results, WORK_REMAINING_COL_NO, WORK_REMAINING_UNITS_COL_NO));

        DateRange workRange = new DateRange(
            DatabaseUtils.getTimestamp(results, WORK_START_COL_NO),
            DatabaseUtils.getTimestamp(results, WORK_END_COL_NO));
        e.setDatesWorked(workRange);
        e.setComment(results.getString(COMMENTS_COL_NO));
        return e;
    }

    
    /**
     * Load all data objects from the database that correspond to the {@link
     * #getSQLStatement} query into domain objects.  Return these values in a
     * <code>List</code> object.
     *
     * @return a <code>List</code> object containing one or more data objects
     *         constructed in {@link #createObjectForResultSetRow}
     * @throws net.project.persistence.PersistenceException if a database error
     * occurs while querying for data or while populating objects.
     */
    protected List loadFromDB() throws PersistenceException {
        List workLogEntries = super.loadFromDB();
        
        //comented after discussion with Roger.
        //This needs to be comented for fix of 3300
        //further bfd dependent on this 3275 is as designed
        //further bfd 3108 still remains fixed
        Space space = SessionManager.getUser().getCurrentSpace();
        
        TaskFinder tf = new TaskFinder();
        List tasks = tf.findBySpaceID(space.getID());
        
        for (Iterator it = tasks.iterator(); it.hasNext();) {
        	ScheduleEntry entry = (ScheduleEntry) it.next();
        	
        	if (!entry.getUnallocatedWorkComplete().isZero()) {
//       		if (!entry.getWorkCompleteTQ().isZero()) {
        		AssignmentWorkLogEntry e = new AssignmentWorkLogEntry();
        		e.setObjectID(entry.getID());
//        		e.setObjectName(SessionManager.getUser().getDisplayName());
        		e.setObjectName(entry.getName());
        		e.setAssigneeName(PropertyProvider.get("prm.resource.report.workcompleted.unallocatedworkcomplete.name"));
        		e.setModifiedBy(PropertyProvider.get("prm.resource.report.workcompleted.unallocatedworkcomplete.name"));
//        		e.setAssigneeName(entry.getName());
//        		e.setModifiedBy(SessionManager.getUser().getUserName());
        		e.setHoursWorked(entry.getUnallocatedWorkComplete());
        		e.setPercentComplete(entry.getPercentCompleteDecimal());
        		e.setLogDate(entry.getLastModifiedDate());
        		e.setTotalAssignmentWork(entry.getWorkTQ());
        		e.setTotalRemainingWork(entry.getWorkRemaining());
//        		DateRange workRange = new DateRange(entry.getActualStartTime(), entry.getActualEndTime());
//        		e.setDatesWorked(workRange);
        		workLogEntries.add(e);
        	}
        }

        return workLogEntries;
    }

    public List loadTaskWorkCompFromDB() throws PersistenceException {
    	SQL = SELECT + FROM + WHERE + " and obj.object_type = '" + ObjectType.TASK + "' ";
        return super.loadFromDB();
    }
    
    public List loadTaskWorkComp100PercentFromDB() throws PersistenceException {
    	SQL = SELECT + FROM + WHERE + " and obj.object_type = '" + ObjectType.TASK + "' and aw.work_remaining = 0 ";
        return super.loadFromDB();
    }

    public List loadFormsWorkCompFromDB() throws PersistenceException, SQLException {    	
    	SQL = SELECT + " , ci.seq_num, ci.class_id " +
    	FROM + " , pn_class_instance ci" +
    	WHERE + " and obj.object_type = '" + ObjectType.FORM_DATA + "' and ci.class_instance_id(+) = oname.object_id ";
    	return super.loadFromDB();
    }
    
    protected List loadWorkCompFromDB() throws PersistenceException, SQLException {
        List workLogEntries = new ArrayList();
        //comented after discussion with Roger.
        //This needs to be comented for fix of 3300
        //further bfd dependent on this 3275 is as designed
        //further bfd 3108 still remains fixed
        Space space = SessionManager.getUser().getCurrentSpace();
        
        TaskFinder tf = new TaskFinder();
        
        List tasks = tf.findBySpaceID(space.getID());
        
//        workLogEntries.clear(); 
        
        for (Iterator it = tasks.iterator(); it.hasNext();) {
        	ScheduleEntry entry = (ScheduleEntry) it.next();
            boolean sch = false;
      		AssignmentWorkLogEntry e1 = new AssignmentWorkLogEntry();
      		if (!entry.getWorkCompleteTQ().isZero()) {
      			e1.setObjectID(entry.getID());
      			e1.setAssigneeID(String.valueOf(entry.getModifiedByID()));
      			e1.setObjectName(entry.getName());
//      		e1.setObjectName(entry.getName());
//      		e1.setAssigneeName(PropertyProvider.get("prm.resource.report.workcompleted.unallocatedworkcomplete.name"));
//      		e1.setModifiedBy(PropertyProvider.get("prm.resource.report.workcompleted.unallocatedworkcomplete.name"));
	            e1.setAssigneeName(getUsername(entry.getModifiedByID()));
//      		e1.setAssigneeName(SessionManager.getUser().getDisplayName());
	            e1.setModifiedBy(entry.getName());
	            e1.setHoursWorked(entry.getUnallocatedWorkComplete());
	            e1.setPercentComplete(entry.getPercentCompleteDecimal());
	            e1.setLogDate(entry.getLastModifiedDate());
	            e1.setTotalAssignmentWork(entry.getWorkTQ());
	            e1.setTotalRemainingWork(entry.getWorkRemaining());
	            DateRange workRange = new DateRange(entry.getActualStartTime(), entry.getActualEndTime());
	            e1.setDatesWorked(workRange); 
//      		workLogEntries.add(e1);
                if(e1.getWork().compareTo(TimeQuantity.O_HOURS) > 0) {
                    sch = true;
                }
      		}
       	 
        	DBBean db = new DBBean();
            try {
            	db.executeQuery(
                     "select "+
                     "  a.modified_by, "+
                     "  a.person_id, "+
                     "  a.work, "+
                     "  a.work_units, "+
                     "  a.log_date "+
                     "from "+
                     "  pn_assignment_work a "+
                     "where "+
                     "  a.object_id in ("+entry.getID()+") "
                 );

             while (db.result.next()) {
             
            	 if (!entry.getWorkCompleteTQ().isZero()) {
                 	AssignmentWorkLogEntry e = new AssignmentWorkLogEntry();
                 	e.setObjectID(entry.getID());
                 	e.setAssigneeID(db.result.getString("person_id"));
                 	e.setObjectName(entry.getName());
//                  e.setObjectName(entry.getName());
//                  e.setAssigneeName(PropertyProvider.get("prm.resource.report.workcompleted.unallocatedworkcomplete.name"));
//                  e.setModifiedBy(PropertyProvider.get("prm.resource.report.workcompleted.unallocatedworkcomplete.name"));
            	    e.setAssigneeName(getUsername(Integer.parseInt(db.result.getString("person_id"))));
//                  e.setAssigneeName(SessionManager.getUser().getDisplayName());
                    e.setModifiedBy(entry.getName());
                    TimeQuantity tq = new TimeQuantity(db.result.getString("work"), TimeQuantityUnit.getForID(db.result.getString("work_units")));
                    e.setHoursWorked(tq);
                    e.setPercentComplete(entry.getPercentCompleteDecimal());
                    //e.setLogDate(entry.getLastModifiedDate());
                    e.setLogDate(DatabaseUtils.makeDate(db.result.getTimestamp("log_date")));
                    e.setTotalAssignmentWork(entry.getWorkTQ());
                    e.setTotalRemainingWork(entry.getWorkRemaining());
                    DateRange workRange = new DateRange(entry.getActualStartTime(), entry.getActualEndTime());
                    e.setDatesWorked(workRange);
                    workLogEntries.add(e);
//                    sch = false;
                  }
               }
             } catch (SQLException sqle) {
                 throw new PersistenceException("Unable to query pn_assignment_work for tasks", sqle);
             } finally {
                 db.release();
             }
             if (sch) {
                 //sjmittal: if some un allocated work complete for that task is found then add it!!
                 //note*: pn_assignment_work contains the "actual" work logged by a resource for 
                 //a task, whereas pn_task work complete can also contain the work updated directly 
                 //by modifying the task work which is called "un allocated" work complete 
                 //so if someone has edited the task's work complete irectly then a  
                 //dummy work log entry is created (e1) and added to the list.
                 workLogEntries.add(e1);
             }
        }

        return workLogEntries;
    }
    
    public List findWorkComplete() throws PersistenceException, SQLException {
        return loadWorkCompFromDB();
        //return super.loadFromDB();
    }
    /**
     * Finds assignment work log entries for work logged against the specified objectID
     * by the specified personID.
     * @param objectID the ID of the object against which logged work is required
     * @param personID the ID of the person for which logged work is required
     * @return the work logged against the given object by the given person where each element
     * is a <code>AssignmentWorkLogEntry</code>
     * @throws PersistenceException if there is a problem loading
     * @throws NullPointerException if objectID or personID is null
     */
    public List findByObjectIDPersonID(final String objectID, final String personID) throws PersistenceException {

        if (objectID == null || personID == null) {
            throw new NullPointerException("Missing required parameter objectID or personID");
        }

        addWhereClause("aw.object_id = ? and aw.person_id = ? ");
        addFinderListener(new FinderListenerAdapter() {
            public void preExecute(DBBean db) throws SQLException {
                super.preExecute(db);
                db.pstmt.setString(1, objectID);
                db.pstmt.setString(2, personID);
            }
        });
        return loadFromDB();
    }
    
    /**
     * Finds assignment work log entries for work logged against the specified personID
     * @param personID the ID of the object against which logged work is required
     * @return the work logged against the given person where each element
     * is a <code>AssignmentWorkLogEntry</code>
     * @throws PersistenceException if there is a problem loading
     * @throws NullPointerException if personID is null
     */
    public List findByPersonID(final String personID) throws PersistenceException {
        if (personID == null) {
            throw new NullPointerException("Missing required parameter personID");
        }

        addWhereClause("aw.person_id = ?");
        addFinderListener(new FinderListenerAdapter() {
            public void preExecute(DBBean db) throws SQLException {
                super.preExecute(db);
                db.pstmt.setString(1, personID);
            }
        });

        return loadFromDB();
    }

    /**
     * Finds assignment work log entries for work logged against the specified objectID
     * @param objectID the ID of the object against which logged work is required
     * @return the work logged against the given object where each element
     * is a <code>AssignmentWorkLogEntry</code>
     * @throws PersistenceException if there is a problem loading
     * @throws NullPointerException if objectID is null
     */
    public List findByObjectID(final String objectID) throws PersistenceException {
        if (objectID == null) {
            throw new NullPointerException("Missing required parameter objectID");
        }

        addWhereClause("aw.object_id = ?");
        addFinderListener(new FinderListenerAdapter() {
            public void preExecute(DBBean db) throws SQLException {
                super.preExecute(db);
                db.pstmt.setString(1, objectID);
            }
        });

        return loadFromDB();
    }
}
