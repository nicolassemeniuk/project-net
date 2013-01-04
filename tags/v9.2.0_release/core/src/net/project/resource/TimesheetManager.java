package net.project.resource;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.form.assignment.FormAssignment;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.util.Logger;

public class TimesheetManager {
	
	Logger log = Logger.getLogger(TimesheetManager.class);
	
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
     * The loaded assignments.
     * Each element is an <code>Assignment</code>.
     */
    private final List filteredAssignments = new ArrayList();
    
	/**
	 *  The sql to select the assignments details
	 */
	private static final String SQL = 
			" select "+ OPTIMIZER_HINT + 
			"	DISTINCT a.object_id, " +
			"	a.status_id, " +
			"	a.percent_allocated, " +
			"	a.role, " +
			"	a.is_primary_owner, " +
			"	a.start_date, " +
			"	a.end_date, " +
			"	a.actual_start, " +
			"	a.actual_finish, " +
			"	a.estimated_finish, " +
			"	a.space_id, " +
			"	a.work, " +
			"	a.work_units, " +
			"	a.work_complete, " +
			"	a.work_complete_units, " +
			"	a.modified_by, " +
			"	a.modified_date, " +
			"	modname.name as modified_by_display_name, " +
			"	a.date_created, " +
			"	a.is_complete, " +
			"	a.person_id, " +
			"	a.assignor_id, " +
			"	a.percent_complete, " +
			"	ob.object_type, " +
			"	p.display_name, " +
			"	p.timezone_code, " +
			"	oname.name as object_name, " +
			"	assnspace.name as space_name,   " +
			"	assnspacetype.object_type as space_type,   " +
			"	ap.display_name, " +
			"	t.task_type ";
		
	/**
	 * Set the column count to set so that we doesn't need to care about the 
	 * column index 
	 */
	private int colCount;
	
	public TimesheetManager(){
		colCount = 0;
	}
	
	/**
	 * This method returns the base SQL statment
	 * 
	 * @return SQL
	 */
	public String getBaseSQLStatment(){
		return SQL;
	}
	
	/** 
	 * This method will return the custom where clause 
	 * 
	 * @param personID 
	 * @param startDate
	 * @param endDate
	 * @param isPercentComplete
	 * @return the custom where conditions
	 */
	private String getWhereClause(String personID, Date startDate, Date endDate, Boolean isPercentComplete, 
									List<String> assignmentStatus, List<String> assignmentType, boolean isAllWorkCaptured){
		String status = "";
		String type = "";
		String whereClause = 
			" where " +
			"	a.object_id = ob.object_id" +
			"	and a.object_id = t.task_id(+)" +
			"	and a.object_id = ci.class_instance_id(+)" +
			"	and ci.class_id = c.class_id(+)"+
			"	and NVL(c.record_status,'A') = 'A'" +
			"	and ob.record_status = 'A'" +
			"	and a.object_id = oname.object_id" +
			"	and a.modified_by = modname.object_id(+)" +
			"	and p.person_id = a.person_id" +
			"	and ap.person_id(+) = a.assignor_id" +
			"	and a.space_id = assnspace.object_id" +
			"	and assnspace.object_id = assnspacetype.object_id" +
			"	and assnspacetype.record_status = 'A'" +
			"	and a.record_status = 'A'";
			
		//set pn_assignment_work table for getting the work captured assignments detail
		if(isAllWorkCaptured){
			whereClause += " and a.object_id = paw.object_id ";
		}
		
		if(isAllWorkCaptured) {
			whereClause += " and paw.person_id = "+ personID ;
		} 
		whereClause += " and a.person_id = "+ personID + " and a.status_id IN( ";
		
		//Set Assignment status for nested query
		for(Iterator objectStatus = assignmentStatus.iterator(); objectStatus.hasNext();) {
			status +=  objectStatus.next() + ",";
		}
		
		if(StringUtils.isNotEmpty(status)){
			whereClause += status.substring(0, status.length()-1);
		}
		
		whereClause += " ) and ob.object_type IN (";
		
		//set Assignemnt type for nested query
		for(Iterator objectType = assignmentType.iterator(); objectType.hasNext();) {
			type +=  "'"+ objectType.next() + "',";
		}
		
		if(StringUtils.isNotEmpty(type)){
			whereClause += type.substring(0, type.length()-1) + ")";
		}
		
		//set end date for start date of pn_assignment
		if( endDate != null && !isAllWorkCaptured) {
			whereClause +="  and a.start_date <= ?" ;
		}
		
		//set end date for end date of pn_assignment
		if(startDate != null && !isAllWorkCaptured) {
			whereClause +=" and a.end_date >= ? " ;
		}
		
		//set end date for start date of pn_assignment_work
		if( endDate != null && isAllWorkCaptured) {
			whereClause +="  and paw.work_start <= ?" ;
		}
		
		//set end date for end date of pn_assignment_work
		if(startDate != null && isAllWorkCaptured) {
			whereClause +=" and paw.work_end >= ? " ;
		}
		
		//set percent complete status 
		if(isPercentComplete != null){
			if(isPercentComplete) {
				whereClause += " and a.percent_complete < 1 ";
			} else {
				whereClause += " and a.percent_complete = 1 ";
			}
		}
		//Order by space id's
		whereClause += "order by a.space_id, a.object_id";
		
		return whereClause;
	}
	
	/**
	 * Method for getting the filtered assignments 
	 *  according to the condition
	 *  
	 * @param personID Assignments for the selected person
	 * @param startDate assignment start date
	 * @param endDate  assignment end date
	 * @param isPercentComplete Assignment percent complete is required or not 
	 * @param assignmentStatus Assignment Status id to set
	 * @param assignmentType Assignment type to set
	 * @param isAllWorkCaptured does query requires to access the work captured assignments
	 *  
	 */
	public void findByDate(String personID, Date startDate, Date endDate, Boolean isPercentComplete, 
			List<String> assignmentStatus, List<String> assignmentType, boolean isAllWorkCaptured) {
		
		filteredAssignments.clear();
		if(colCount != 0){ colCount = 0; }
		
		DBBean db = new DBBean();
		try{
			db.prepareStatement(getSQLStatement(personID, startDate, endDate, isPercentComplete, 
				assignmentStatus, assignmentType, isAllWorkCaptured));
			if(endDate != null) {
				DatabaseUtils.setTimestamp(db.pstmt, ++colCount, endDate);
			}
			if(startDate != null){
				DatabaseUtils.setTimestamp(db.pstmt, ++colCount, startDate);
			}
			db.executePrepared();
			if(!isAllWorkCaptured) {
				filteredAssignments.addAll(postExecute(db.result));
			} else {
				filteredAssignments.addAll(postWorkCapturedExecute(db.result));
			}
		} catch (SQLException sqle) {
			log.error("TimesheetManager.findByDate() failed.."+sqle.getMessage());			
		} finally {
			db.release();
		}
	}
	
	/**
	 * Method for getting the complete SQL statement
	 * 
	 * @param personID
	 * @param startDate
	 * @param endDate
	 * @param isPercentComplete
	 * @param assignmentStatus
	 * @param assignmentType
	 * @param isAllWorkCaptured
	 * @return complete SQL statement
	 */
	private String getSQLStatement(String personID, Date startDate, Date endDate, Boolean isPercentComplete, 
			List<String> assignmentStatus, List<String> assignmentType, boolean isAllWorkCaptured){
		String sqlStatment;
		
		/**
		 * To display the unassigned task workcapture and assigned task work capture
		 * we need to handle through different query call.
		 * Handles only one query call at a time depending on the condition
		 */
		if(!isAllWorkCaptured) {
			sqlStatment = getBaseSQLStatment().concat(getFromClause(isAllWorkCaptured))
						.concat(getWhereClause(personID, startDate, endDate, isPercentComplete, 
						assignmentStatus, assignmentType, isAllWorkCaptured));
		} else {
			sqlStatment = getAllWorkCapturedSQL(personID);
		}
		return sqlStatment;
	}
	
	/**
	 * Method for getting the custom where clause
	 * 
	 * @param isAllWorkCaptured
	 * @return custom from clause
	 */
	private String getFromClause(boolean isAllWorkCaptured){
		String fromClause =
			"from   " +
			"	pn_assignment a," +
			"	pn_object ob," +
			"	pn_person_view p," +
			"	pn_person_view ap," +
			"	pn_object_name modname," +
			"	pn_object_name oname," +
			"	pn_object_name assnspace," +
			"	pn_task t," +
			"	pn_class_instance ci," +
			"	pn_object assnspacetype," +
			"	pn_class c ";
			if(isAllWorkCaptured){
				fromClause += ", pn_assignment_work paw ";
			}
		return fromClause;
	}
	
	/** 
	 * Method to call after execution of query
	 * to find the assignment type according to the object type
	 * 
	 * @param resultSet the elapsed Result set by the query to set 
	 * @return Assignments list of type <code>Assignment</code>
	 */
	private List postExecute(ResultSet resultSet) {
		List assignments = new ArrayList();
		try{
			while(resultSet.next()){
				Assignment assignment = null;
				AssignmentType assignmentType = AssignmentType.forObjectType(resultSet.getString("object_type"));
		        if (assignmentType == null) {
					if (log.isDebugEnabled()){
						log.debug(" AssignmentType.forObjectType returned null !");
					}
				} else {	
		    		assignment = assignmentType.newAssignment();
		    		if(assignment instanceof ScheduleEntryAssignment){
		    			ScheduleEntryAssignment entryAssignment = (ScheduleEntryAssignment) assignment;
		    			entryAssignment.setIncludeTaskType(true);
		    		}
		    		assignment.populate(resultSet);
		    		assignments.add(assignment);
		        }
			}
		}catch (SQLException sqle) {
			log.error("TimesheetManager.postExecute() failed.."+sqle.getMessage());
		}
		return assignments;
	}
	
	/**
	 *  Method to display the all the workcaptured 
	 * 	assignments, tasks and form assignment
	 * 
	 *  @param personID the person's ID
	 *  @return complete SQL Statement for the workcaptured filter
	 */
	private String getAllWorkCapturedSQL(String personID) {
		String workCapturedSQL;
		
		workCapturedSQL = 
			" select " +
			"	DISTINCT obj.object_id, " +
			"	pos.space_id, " +
			"	ob.name as task_name, " +
			"	obs.name as space_name, " +
			"	t.work_complete, " +
			"	t.work as total_work, " +
			"	t.work_units, " +
			"	obj.object_type," +
			"	paw.person_id " +
			" from " +
			"	pn_assignment_work paw, " +
			"	pn_object_name ob, " +
			"	pn_object_name obs, " +
			"	pn_object_space pos, " +
			"	pn_task t, " +
			"	pn_class_instance ci, " +
			"	pn_object obj," +
			"	pn_class c " +
			" where " +
			"	obj.object_id = paw.object_id " +
			"	and ob.object_id = paw.object_id " +
			"	and pos.object_id = ob.object_id " +
			"	and obs.object_id = pos.space_id " +
			"	and t.task_id(+) = paw.object_id " +
			"	and ci.class_instance_id(+) = paw.object_id " +
			"	and ci.class_id = c.class_id(+)"+
		    "	and NVL(c.record_status,'A') = 'A'"+
			"	and obj.record_status = 'A' " +
			"	and paw.person_id = "+ personID + 
			"	and paw.work_start <= ?" +
			"	and paw.work_end >= ? 	" +
			"	order by pos.space_id, obj.object_id ";
		
		return workCapturedSQL;
	}

	/**
	 *  Method to populate the work captured result set
	 *  
	 *  @param resultSet evaluated by query 
	 *  @return assignments list for the work captured assignments
	 */
	private List postWorkCapturedExecute(ResultSet resultSet){
		List assignments = new ArrayList();
		try{
			while(resultSet.next()){
				Assignment assignment = null;
				AssignmentType assignmentType = AssignmentType.forObjectType(resultSet.getString("object_type"));
		        if (assignmentType == null) {
					if (log.isDebugEnabled()){
						log.debug(" AssignmentType.forObjectType returned null !");
					}
				} else {	
					assignment = assignmentType.newAssignment();
					assignment.setSpaceID(resultSet.getString("space_id"));
					assignment.setSpaceName(resultSet.getString("space_name"));
					assignment.setObjectID(resultSet.getString("object_id"));
					assignment.setObjectType(resultSet.getString("object_type"));
					assignment.setObjectName(resultSet.getString("task_name"));
					assignment.setPersonID(resultSet.getString("person_id"));
					if(assignment instanceof ScheduleEntryAssignment) {
						ScheduleEntryAssignment entryAssignment = (ScheduleEntryAssignment) assignment;
						entryAssignment.setWork(DatabaseUtils.getTimeQuantity(resultSet, 6, 7));
						entryAssignment.setWorkComplete(DatabaseUtils.getTimeQuantity(resultSet, 5, 7));
						if(entryAssignment.getWork().isZero()) {
							entryAssignment.setPercentComplete(BigDecimal.valueOf(0.00));
						} else {
						entryAssignment.setPercentComplete((entryAssignment.getWorkComplete().divide(entryAssignment.getWork(), 5, BigDecimal.ROUND_HALF_UP)).min(new BigDecimal("1.00")));
						}
					} else if(assignment instanceof FormAssignment) {
						FormAssignment formAssignment = (FormAssignment) assignment;
						formAssignment.setWork(DatabaseUtils.getTimeQuantity(resultSet, 6, 7));
						formAssignment.setWorkComplete(DatabaseUtils.getTimeQuantity(resultSet, 5, 7));
					}
					assignments.add(assignment);
				}
			}
		} catch (SQLException e) {
			log.error("TimesheetManager.postWorkCapturedExecute() failed..."+e.getMessage());
		}
		return assignments;
	}
	
	/**
	 * Method for getting the filtered assignments list
	 *  
	 * @return filtered Assignments 
	 */
	public List getFilteredAssignments() {
		return filteredAssignments;
	}
}
