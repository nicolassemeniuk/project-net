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

package net.project.schedule.mvc.handler.taskedit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.channel.ScopeType;
import net.project.crossspace.ImportedObject;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentList;
import net.project.resource.PersonProperty;
import net.project.schedule.PredecessorList;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleFinder;
import net.project.schedule.SummaryTask;
import net.project.schedule.Task;
import net.project.schedule.TaskConstraintType;
import net.project.schedule.TaskFinder;
import net.project.schedule.mvc.handler.main.QuickAddHandler;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.util.ErrorReporter;

import org.apache.log4j.Logger;

public class CreateFromExternalProcessingHandler extends Handler {
	private Logger logger = Logger.getLogger(CreateFromExternalProcessingHandler.class);

	private String SQL = "select " + "  1 " + "from " + "  pn_object o, "
			+ "  pn_shared s " + "where "
			+ "  o.object_id = s.exported_object_id and "
			+ "  s.import_container_id = ? and "
			+ "  s.import_space_id = ? and "
			+ "  s.export_container_id = ? and "
			+ "  s.export_space_id = ? and " + "  s.exported_object_id = ? ";

	private boolean store = true;

	public CreateFromExternalProcessingHandler(HttpServletRequest request) {
		super(request);
	}

	/**
	 * Gets the name of the view that will be rendered after processing is
	 * complete.
	 * 
	 * @return a <code>String</code> containing a name that uniquely
	 *         identifies a view that we are going to redirect to after
	 *         processing the request.
	 */
	public String getViewName() {
		return "/workplan/taskview";
	}

	/**
	 * Ensure that the requester has proper rights to access this object. For
	 * objects that aren't excluded from security checks, this will just consist
	 * of verifying that the parameters that were used to access this page were
	 * correct (that is, that the requester didn't try to "spoof it" by using a
	 * module and action they have permission to.)
	 * 
	 * @param module
	 *            the <code>int</code> value representing the module that was
	 *            passed to security.
	 * @param action
	 *            the <code>int</code> value that was passed through security
	 *            for the action.
	 * @param objectID
	 *            the <code>String</code> value for objectID that was passed
	 *            through security.
	 * @param request
	 *            the entire request that was submitted to the schedule
	 *            controller.
	 * @throws net.project.security.AuthorizationFailedException
	 *             if the user didn't have the proper credentials to view this
	 *             page, or if they tried to spoof security.
	 * @throws net.project.base.PnetException
	 *             if any other error occurred.
	 */
	public void validateSecurity(int module, int action, String objectID,HttpServletRequest request) 
		throws AuthorizationFailedException, PnetException {
	}

	/**
	 * Add the necessary elements to the model that are required to render a
	 * view. Often this will include things like loading variables that are
	 * needed in a page and adding them to the model.
	 * 
	 * The views themselves should not be doing any loading from the database.
	 * The whole reason for an mvc architecture is to avoid that. All loading
	 * should occur in the handler.
	 * 
	 * @param request
	 *            the <code>HttpServletRequest</code> that resulted from the
	 *            user submitting the page.
	 * @param response
	 *            the <code>HttpServletResponse</code> that will allow us to
	 *            pass information back to the user.
	 * @return a <code>Map</code> which is the updated model.
	 * @throws Exception
	 *             if any error occurs.
	 */
	public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map model = new HashMap();
		setRedirect(true);

		model.put("module", String.valueOf(Module.SCHEDULE));
		model.put("action", String.valueOf(Action.VIEW));

		// Create a copy of the external object as a task object
		createTaskObjects(request);

		return model;
	}

	private void createTaskObjects(HttpServletRequest request) throws PersistenceException {
		Schedule schedule = (Schedule) getSessionVar("schedule");
		String moveCopyShare = request.getParameter("moveCopyShare");
        ErrorReporter er = new ErrorReporter();        
        request.getSession().setAttribute("errorReporter", er);
		
		//sjmittal code added to get the position where to add imported entries
		String scheduleAboveId = request.getParameter("neighborAbove");
		ScheduleEntry neighborAbove = null;
		if(scheduleAboveId != null)
			neighborAbove = (ScheduleEntry)schedule.getEntryMap().get(scheduleAboveId);
		
		boolean autoCalc = schedule.isAutocalculateTaskEndpoints();
		
		// defualt to regular share, if null.
		if (moveCopyShare == null)
			moveCopyShare = "share";

		// Get the list of all external "task" objects being added
		String[] taskIDs = request.getParameterValues(ObjectType.TASK);
		if (taskIDs != null) {
			TaskFinder tf = new TaskFinder();
			List tasks = tf.findByIDList(Arrays.asList(taskIDs));
			String currProjSpace = schedule.getSpace().getID();
			String errorText = "";
			List<String> errorProneTasks = new ArrayList<String>();

			for (Iterator it = tasks.iterator(); it.hasNext();) {
				ScheduleEntry scheduleEntry = (ScheduleEntry) it.next();
				String originalID = scheduleEntry.getID();
				String originalPlanID = scheduleEntry.getPlanID();
				String workProjSpace = scheduleEntry.getSpaceID();

				if (!currProjSpace.equals(workProjSpace)) {

					DBBean db = new DBBean();
					try {
						db.prepareStatement(SQL);
                        //check if the schedule entry is not already imported
                        db.pstmt.setString(1, schedule.getID());
						db.pstmt.setString(2, schedule.getSpaceId());
						db.pstmt.setString(3, scheduleEntry.getPlanID());
						db.pstmt.setString(4, scheduleEntry.getSpaceID());
						db.pstmt.setString(5, scheduleEntry.getID());
						db.executePrepared();
						if (db.result.next()) {
							errorProneTasks.add(scheduleEntry.getName());
							store = false;
						} else {
						    //check if the schedule entry's schedule is not already imported
						    db.pstmt.setString(1, schedule.getID());
						    db.pstmt.setString(2, schedule.getSpaceId());
						    db.pstmt.setString(3, scheduleEntry.getPlanID());
						    db.pstmt.setString(4, scheduleEntry.getSpaceID());
						    db.pstmt.setString(5, scheduleEntry.getPlanID());
						    db.executePrepared();
						    if (db.result.next()) {
                                ScheduleFinder sf = new ScheduleFinder();
                                Schedule exportedSchedule = sf.findByPlanID(scheduleEntry.getPlanID());
                                er.addError(PropertyProvider.get("prm.crossspace.createexternal.error.duplicatetaskschedule.message", scheduleEntry.getName(), exportedSchedule.getName()));
						        store = false;
						    } else {
						        //check if the schedule entry's parent is not already imported                            
						        db.pstmt.setString(1, schedule.getID());
						        db.pstmt.setString(2, schedule.getSpaceId());
						        db.pstmt.setString(3, scheduleEntry.getPlanID());
						        db.pstmt.setString(4, scheduleEntry.getSpaceID());
						        db.pstmt.setString(5, scheduleEntry.getParentTaskID());
						        db.executePrepared();
						        if (db.result.next()) {
						            er.addError(PropertyProvider.get("prm.crossspace.createexternal.error.duplicateparenttask.message", scheduleEntry.getName(), scheduleEntry.getParentTaskName()));
						            store = false;
						        } else {
						            //check if the schedule entry's any children are not already imported
						            if (scheduleEntry instanceof SummaryTask) {
                                        SummaryTask summaryTask = (SummaryTask) scheduleEntry;
                                        Iterator childTaskIter = summaryTask.getChildren().iterator();
                                        while (childTaskIter.hasNext()) {
                                            ScheduleEntry childTask = (ScheduleEntry) childTaskIter.next();
                                            
                                            db.pstmt.setString(1, schedule.getID());
                                            db.pstmt.setString(2, schedule.getSpaceId());
                                            db.pstmt.setString(3, childTask.getPlanID());
                                            db.pstmt.setString(4, childTask.getSpaceID());
                                            db.pstmt.setString(5, childTask.getID());
                                            db.executePrepared();
                                            if (db.result.next()) {
                                                er.addError(PropertyProvider.get("prm.crossspace.createexternal.error.duplicatechildtask.message", scheduleEntry.getName(), childTask.getName()));
                                                store = false;
                                            }
                                        }
                                    }
						        }
						    }
						}
					} catch (SQLException sqle) {
						logger.error(
								"Error encountered while trying to query for "
										+ "dependencies on object id: "
										+ scheduleEntry.getID(), sqle);
					} finally {
						db.release();
					}

					if (store) {
						if ("move".equals(moveCopyShare)) {
						    //remove from the orignal one
							scheduleEntry.remove();
							//Now update the schedule we removed the items from
							ScheduleFinder sf = new ScheduleFinder();
							Schedule removedItemSchedule = sf.findByPlanID(scheduleEntry.getPlanID());
							removedItemSchedule.recalculateTaskTimes();
						}

						//store the new schedule entry in the current space
						Task newTask = new Task();
						scheduleEntry.setFieldsFromScheduleEntry(newTask);
						newTask.setID(null);
						newTask.setSequenceNumber(0);
						newTask.setPlanID(schedule.getID());
						newTask.setPredecessors(new PredecessorList());
						newTask.setAssignmentList(new AssignmentList());
						newTask.setSpaceID(schedule.getSpaceId());
						newTask.setParentTaskID(null);

						//set the calculation type and constraint type
                        //sjmittal: calc type comes into picture when we assign resource to tasks
                        //share tasks cannot be assigned any so this does not mater much
//						newTask.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
                        //sjmittal: since these are readonly we don't want TEC to modify these
                        //thus sharing them with this constraint makes sense
						newTask.setConstraintType(TaskConstraintType.START_AND_END_DATES_FIXED);
//						newTask.setConstraintDate(newTask.getStartTime());

						
						if(neighborAbove != null) {
							try {
                                PersonProperty property = PersonProperty.getFromSession(request.getSession());
                                property.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
                                String[] expanded = property.get("prm.schedule.main", "node" + neighborAbove.getID() + "expanded");
                                boolean isNeighborAboveExpanded = true;
                                if(expanded != null && expanded.length > 0)
                                    isNeighborAboveExpanded = Boolean.parseBoolean(expanded[0]);
								QuickAddHandler.addScheduleEntryBelow(schedule, newTask, neighborAbove, isNeighborAboveExpanded);
							} catch (PersistenceException e) {
							} catch (SQLException e) {
							}
							//sjmittal currently just catch the exception as its not as crtical as rest of the process.
						}
					
						newTask.store(false, schedule);
						if(neighborAbove != null) {
							//sjmittal we do this as the next imported task would be added below 
							//the just created imported task
							neighborAbove = newTask;
						}

						if ("share".equals(moveCopyShare) || "shareReadOnly".equals(moveCopyShare)) {
							ImportedObject io = new ImportedObject();
							//set exported side
							io.setExportedObjectID(originalID);
							io.setExportContainerID(originalPlanID);
							io.setExportSpaceID(workProjSpace);
							
							//set imported side
							io.setImportContainerID(schedule.getID());
							io.setImportSpaceID(schedule.getSpaceId());
							io.setImportedObjectID(newTask.getID());
							//set the read only mode
							io.setReadOnly("shareReadOnly".equals(moveCopyShare));
							//store the shared objects data
							io.store();
						}
					}
				} else {
					er.addError(PropertyProvider.get("prm.schedule.taskedit.error.sharingtask.message", scheduleEntry.getName()));
				}
			}
			if(errorProneTasks != null){
				if(errorProneTasks.size() > 1){
					for(int index = 0; index < errorProneTasks.size();index++){
						if(index != errorProneTasks.size()-1){
							errorText += errorProneTasks.get(index)+", ";
						}else{
							errorText = errorText.substring(0, errorText.lastIndexOf(","))+" and"+errorProneTasks.get(index);
						}
					}	
					er.addError(PropertyProvider.get("prm.crossspace.createexternal.error.duplicatetask.message", errorText));
				}else if(errorProneTasks.size()== 1){
					er.addError(PropertyProvider.get("prm.crossspace.createexternal.error.duplicatetask.message", errorProneTasks.get(0)));
				}
			}
		}

		// Get the list of schedules being added
		String[] planIDs = request.getParameterValues("plan");
		if (planIDs != null) {
			ScheduleFinder sf = new ScheduleFinder();
			List schedules = sf.findByPlanIDList(Arrays.asList(planIDs));
			String currSchSpace = schedule.getSpace().getID();

			for (Iterator it = schedules.iterator(); it.hasNext();) {
				Schedule scheduleObj = (Schedule) it.next();

				String workSchSpace = scheduleObj.getSpace().getID();
				if (!currSchSpace.equals(workSchSpace)) {

                    DBBean db = new DBBean();
                    try {
                        //check if the schedule is not already imported
                        db.prepareStatement(SQL);
                        db.pstmt.setString(1, schedule.getID());
                        db.pstmt.setString(2, schedule.getSpaceId());
                        db.pstmt.setString(3, scheduleObj.getID());
                        db.pstmt.setString(4, scheduleObj.getSpaceId());
                        db.pstmt.setString(5, scheduleObj.getID());
                        db.executePrepared();
                        if (db.result.next()) {
                            er.addError(PropertyProvider.get("prm.crossspace.createexternal.error.duplicateschedule.message", scheduleObj.getName()));
                            store = false;
                        } else {
                            //check if the schedule's task are not already imported
                            Iterator taskListIter = scheduleObj.getTaskList().iterator();
                            while (taskListIter.hasNext()) {
                                ScheduleEntry task = (ScheduleEntry) taskListIter.next();
                                
                                db.pstmt.setString(1, schedule.getID());
                                db.pstmt.setString(2, schedule.getSpaceId());
                                db.pstmt.setString(3, scheduleObj.getID());
                                db.pstmt.setString(4, scheduleObj.getSpaceId());
                                db.pstmt.setString(5, task.getID());
                                db.executePrepared();
                                if (db.result.next()) {
                                    er.addError(PropertyProvider.get("prm.crossspace.createexternal.error.duplicateschedulestask.message", scheduleObj.getName(), task.getName()));
                                    store = false;
                                }
                                
                            }
                        }
                    } catch (SQLException sqle) {
                        logger.error(
                                "Error encountered while trying to query for "
                                        + "dependencies on object id: "
                                        + scheduleObj.getID(), sqle);
                    } finally {
                        db.release();
                    }
                    
                    if (store) {
                        ScheduleEntry scheduleEntry = new Task();
                        scheduleObj.setFieldsFromSchedule(scheduleEntry);
                        scheduleEntry.setPlanID(schedule.getID());
                        scheduleEntry.setPredecessors(new PredecessorList());
                        scheduleEntry.setAssignmentList(new AssignmentList());
                        scheduleEntry.setParentTaskID(null);
                        //sjmittal: note unlike sharing of schedule entry, calc type and constraint type
                        //are set in the setFieldsFromSchedule method only, cos it is designed
                        //especially for converting shared schedule to a task
                        
                        if(neighborAbove != null) {
                            try {
                                PersonProperty property = PersonProperty.getFromSession(request.getSession());
                                property.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
                                String[] expanded = property.get("prm.schedule.main", "node" + neighborAbove.getID() + "expanded");
                                boolean isNeighborAboveExpanded = true;
                                if(expanded != null && expanded.length > 0)
                                    isNeighborAboveExpanded = Boolean.parseBoolean(expanded[0]);
                                QuickAddHandler.addScheduleEntryBelow(schedule, scheduleEntry, neighborAbove, isNeighborAboveExpanded);
                            } catch (PersistenceException e) {
                            } catch (SQLException e) {
                            }
                            //sjmittal currently just catch the exception as its not as crtical as rest of the process.
                        }

                        scheduleEntry.store(false, schedule);
                        if(neighborAbove != null) {
                            //sjmittal we do this as the next imported task would be added below 
                            //the just created imported task
                            neighborAbove = scheduleEntry;
                        }

                        if ("share".equals(moveCopyShare) || "shareReadOnly".equals(moveCopyShare)) {
                            ImportedObject io = new ImportedObject();
                            //set the export side
                            io.setExportedObjectID(scheduleObj.getID());
                            io.setExportContainerID(scheduleObj.getID());
                            io.setExportSpaceID(scheduleObj.getSpaceId());
                            //set the import side
                            io.setImportContainerID(schedule.getID());
                            io.setImportSpaceID(schedule.getSpaceId());
                            io.setImportedObjectID(scheduleEntry.getID());
                            //set the readonly mode
                            io.setReadOnly("shareReadOnly".equals(moveCopyShare));
                            //store the shared objects data
                            io.store();
                        }
                    }
				} else {
					er.addError(PropertyProvider.get("prm.schedule.taskedit.error.sharingschedule.message", scheduleObj.getName()));
				}

			}
		}
		//finally re-calculate the current schedule task times
		if(autoCalc)
		    schedule.recalculateTaskTimes();
	}
}
