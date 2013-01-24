/**
 * 
 */
package net.project.schedule.importer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import net.project.database.DBBean;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskConstraintType;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.DateFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * This class is used for import task functionality
 * 
 */
public class XLSImporter {

	Logger logger = Logger.getLogger(XLSImporter.class);

	private String logMessage = "";
	
	private Schedule schedule = null;
	
	private Integer previousProjectID = 0;

	@SuppressWarnings( { "unchecked", "deprecation" })
	public void updateTasks(InputStream inputStream, HttpServletRequest request, JspWriter out) {
		DBBean db = new DBBean();
		PreparedStatement projectTaskStatement = null;
		PreparedStatement updateDatesStatement = null;
		ResultSet projectTask = null;

		POIFSFileSystem fileSystem = null;

		int excelRowNumber = 0;
		int importNumber = 0;
		
		HSSFWorkbook workBook = null;
		HSSFSheet sheet = null;
		Iterator<HSSFRow> workBookRows = null;
		HSSFRow excelRow = null;
		HSSFCell excelCell = null;
		Integer excelProjectId = null;

		Date dateStart = null;
		Date dateFinish = null;
		String taskName = null;

		// query to select task for excel row
		String selectTask = "select"
				+ "	t.task_id as task_id,t.task_name, t.task_type, t.date_start, t.date_finish, t.duration_units, "
				+ " t.work_percent_complete, t.work_complete, t.work_complete_units  from    "
				+ "	pn_space_has_plan shp, 	pn_plan p,    pn_plan_has_task pht, pn_task t,    "
				+ "	pn_task pt,    pn_phase_has_task phht, 	pn_phase ph,   	pn_shared shrd,    "
				+ "	pn_shareable shbl,    	pn_object_name spon,  pn_current_task_version ctv  "
				+ " where    shp.plan_id = p.plan_id   and  shp.plan_id = pht.plan_id   and "
				+ "	p.plan_id = pht.plan_id   and  pht.task_id = t.task_id   and  "
				+ "	t.parent_task_id = pt.task_id(+)   and t.task_id = phht.task_id(+)   and  "
				+ "	phht.phase_id = ph.phase_id(+)   and ph.record_status(+) = 'A'   and  "
				+ "	t.task_id = shrd.imported_object_id(+)   and  "
				+ "	shrd.exported_object_id = shbl.object_id(+)   and shbl.space_id = spon.object_id(+)   and "
				+ "	t.task_id = ctv.task_id(+)  and	shp.space_id = ? and t.task_name= ? and "
				+ "	t.record_status = 'A' order by t.seq";
		
		String updateTaskDates = "update pn_task set date_start = ?, date_finish = ?, duration = ?, constraint_type = ?, constraint_date = ?, work_complete = ?, work_complete_units = ?, work_percent_complete = ? where task_id = ?";

		try {
			db.openConnection();
			db.connection.setAutoCommit(false);

			try {
				// load excel document
				fileSystem = new POIFSFileSystem(inputStream);
				workBook = new HSSFWorkbook(fileSystem);
				sheet = workBook.getSheetAt(0);
				workBookRows = sheet.rowIterator();
				projectTaskStatement = db.connection.prepareStatement(selectTask);

				// for all tasks in excel
				Long StartTime = System.currentTimeMillis();
				while (workBookRows.hasNext()) {
					Long taskStartTime = System.currentTimeMillis();
					try {
						// reset project id for each iteration
						excelProjectId = null;
						
						logMessage += excelRowNumber + " :";
						out.println((excelRowNumber + 1) + ": ");

						excelRow = workBookRows.next();
						// first column of excel - projectId
						try {
							// try to read project id from excel
							excelProjectId = (int) excelRow.getCell(0).getNumericCellValue();
						} catch (NumberFormatException e) {
							logMessage += "<span style=\"color:red;\">Invalid ProjectId - " + e.getMessage() + "</span><br>";
							out.println("<span style=\"color:red;\">Invalid ProjectId - " + e.getMessage() + "</span><br>");
							logger.error("Error occured while getting project id.." + e.getMessage());
						} catch (NullPointerException e) {
							logMessage += "<span style=\"color:red;\">Blank project id - " + e.getMessage() + "</span><br>";
							out.println("<span style=\"color:red;\">Blank project id - " + e.getMessage() + "</span><br>");
							logger.error("Error occured while getting project id.." + e.getMessage());
						}

						if (excelProjectId != null) {
							updateDatesStatement = db.connection.prepareStatement(updateTaskDates);
							
							// clear parameters
							projectTaskStatement.clearParameters();
							updateDatesStatement.clearParameters();
							
							projectTaskStatement.setInt(1, excelProjectId);
							// get/set task name
							taskName = excelRow.getCell(1).getRichStringCellValue().toString();
							projectTaskStatement.setString(2, taskName);

							projectTask = projectTaskStatement.executeQuery();

							if (projectTask.next()) {
								boolean executeUpdate = true;
								dateFinish = null;
								dateStart = null;

								Integer taskId = projectTask.getInt("task_id");

								try {
									// get start date cell
									excelCell = excelRow.getCell(2);
									if (excelCell != null) {
										dateStart = new Date(excelCell.getDateCellValue().getTime());
									} else {
										dateStart = projectTask.getDate("date_start");
									}
								} catch (Exception e) {
									executeUpdate = false;
									logMessage += "<span style=\"color:red;\">Wrong start date  - " + e.getMessage() + " </span><br>";
									out.println("<span style=\"color:red;\">Wrong start date  - " + e.getMessage() + " </span><br>");
									logger.error("Wrong start date data for projectId: " + excelProjectId
											+ " and task name: " + taskName + " !");
								}

								try {
									// get finish date from excel
									excelCell = excelRow.getCell(3);
									if (excelCell != null) {
										dateFinish = new Date(excelCell.getDateCellValue().getTime());
									} else {
										dateFinish = projectTask.getDate("date_finish");
									}
								} catch (Exception e) {
									executeUpdate = false;
									logMessage += "<span style=\"color:red;\">Wrong finish date  - " + e.getMessage() + " </span><br>";
									out.println("<span style=\"color:red;\">Wrong finish date  - " + e.getMessage() + " </span><br>");
									logger.error("Wrong finish date data for projectId: " + excelProjectId
											+ " and task name: " + taskName + " !");
								}

								Double workPercentComplete = 0.0;
								TimeQuantity workComplete = new TimeQuantity(0, TimeQuantityUnit.HOUR);
								ScheduleEntry entry =null;
								
								try {
									// get % complete
									excelCell = excelRow.getCell(4);
									if (excelCell == null) {
										workPercentComplete = projectTask.getDouble("work_percent_complete");
									} else {
										workPercentComplete = (Double) excelCell.getNumericCellValue();
									}

									entry = startDateEndDateChanged(taskId, dateStart, dateFinish, excelProjectId, request);
									
									// GET WORK PERCENT COMPLETE INFORMATION
									workComplete = calculateWorkComplete(taskId, workPercentComplete, excelProjectId, request);
								} catch (Exception e) {
									executeUpdate = false;
									logMessage += "<span style=\"color:red;\">Invalid % complete - " + e.getMessage() + "</span> <br>";
									out.println("<span style=\"color:red;\">Invalid % complete - " + e.getMessage() + "</span> <br>");
									logger.error("Error occurred while updating task details " + e.getMessage());
									workPercentComplete = 0.0;
								}

								if (executeUpdate) {
									
									updateDatesStatement.setTimestamp(1, new Timestamp(entry.getStartTime().getTime()));
									updateDatesStatement.setTimestamp(2, new Timestamp(entry.getEndTime().getTime()));
									updateDatesStatement.setString(3, entry.getDuration());
									updateDatesStatement.setString(4, entry.getConstraintType().getID());
									
									if(entry.getConstraintDate() != null){
										updateDatesStatement.setTimestamp(5, new Timestamp(entry.getConstraintDate().getTime()));
									} else {
										updateDatesStatement.setTimestamp(5, null);
									}
									
									updateDatesStatement.setDouble(6, workComplete.getAmount().doubleValue());
									updateDatesStatement.setInt(7, workComplete.getUnits().getUniqueID());
									updateDatesStatement.setDouble(8, workPercentComplete);
									updateDatesStatement.setInt(9, taskId);

									updateDatesStatement.executeUpdate();
									db.connection.commit();
									
									// successfully updated task 
									out.println(taskName + " updated. <br/>");
									out.flush();
									importNumber++;
									logger.info(importNumber + ": Import complete." + " time " + (System.currentTimeMillis() - StartTime) + " Task Time " + (System.currentTimeMillis() - taskStartTime));
								}
							} else {
								// if task doesn't exist then print the message
								out.println("<span style=\"color:red;\"> Task doesnot exist </span> <br>");
							}
						}// if project id != null

					}catch (Exception e) {
						logMessage += "<span style=\"color:red;\">Internal error " + e.getMessage() + " </span> <br>";
						out.println("<span style=\"color:red;\">Internal error " + e.getMessage() + " </span> <br>");
						logger.error("Error in opening connection..." + e.getMessage());
					} finally {
						try {
							projectTask.close();
							updateDatesStatement.close();
						} catch (Exception e) {/* ignore */
						}
					}
					excelRowNumber++;
				}
				
				//logger.info("Total time required for "+ excelRowNumber + " updation is " + (System.currentTimeMillis() - StartTime) + " ms");
				projectTaskStatement.close();
				
				logMessage += "<br>Tasks : " + sheet.getPhysicalNumberOfRows() + ", Imported : " +importNumber + ", Failed : <span style=\"color:red;\">" + (excelRowNumber - importNumber)  +"</span><br>";
				out.println("<br>Total Tasks : " + sheet.getPhysicalNumberOfRows() + ",   Imported : " +importNumber + ",   Failed : <span style=\"color:red;\">" + (excelRowNumber - importNumber)  +"</span><br>");
				
			} catch (IOException e) {
				logMessage += "File not found <br>";
				out.println("File not found <br>");
				System.out.println("File not found in the specified path.");
			}
		} catch (Exception e) {
			logMessage += "Task import failed due to internal error <br>";
			try {
				out.println("Task import failed due to internal error <br>");
			} catch (IOException e1) {
				logger.error("XLSImported.updateTask() failed..." + e.getMessage());
			}
			logger.error("XLSImported.updateTask() failed..." + e.getMessage());
		} finally {
			db.release();
		}
	}

	/**
	 * get duration in specified date interval.
	 * 
	 * @param startDate
	 *            interval start date
	 * @param endDate
	 *            interval end date
	 * @return duration
	 */
	private float calculateDuration(Date startDate, Date endDate, String durationUnits) {
		float workingDuration = 0;

		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(startDate);

		Calendar calTo = Calendar.getInstance();
		calTo.setTime(endDate);

		calTo.add(Calendar.DATE, 1);
		while (calFrom.getTime().before(calTo.getTime())) {
			if (calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				// add default work duration of default working days.
				workingDuration++;
			}
			calFrom.add(Calendar.DATE, 1);
		}

		if (durationUnits.equals("4")) {
			workingDuration = workingDuration * 8;
		} else if (durationUnits.equals("16")) {
			workingDuration = workingDuration / 5;
		}
		return workingDuration;
	}

	/**
	 * Find the constraint type from start date and end date
	 * 
	 * @param startDate
	 * @param endDate
	 * @param newStartDate
	 * @param newEndDate
	 * @return task constraint type
	 */
	private String getTaskConstraintType(Date startDate, Date endDate, Date newStartDate, Date newEndDate) {
		return !startDate.equals(newStartDate) || !endDate.equals(newEndDate) ? TaskConstraintType.START_NO_EARLIER_THAN
				.getID()
				: TaskConstraintType.AS_SOON_AS_POSSIBLE.getID(); // START_NO_EARLIER_THAN
	}

	/**
	 * Find the constraint date from start date and end date
	 * 
	 * @param startDate
	 * @param endDate
	 * @param newStartDate
	 * @param newEndDate
	 * @return task constraint date
	 */
	private Date getTaskConstraintDate(Date startDate, Date endDate, Date newStartDate, Date newEndDate) {
		return !newStartDate.equals(startDate) || !endDate.equals(newEndDate) ? newStartDate : null;
	}

	/**
	 * Calculates worked percent complete change according to the project and
	 * task
	 * 
	 * @param taskId
	 * @param work_complete
	 * @param projectId
	 * @return Work Complete in TimeQunantity
	 */
	private TimeQuantity calculateWorkComplete(Integer taskId, double work_percent_complete, Integer projectId, HttpServletRequest request) {
		schedule = SessionManager.getSchedule();
		
		// if project id is changed then we have to load schedule for the new project
		if(!previousProjectID.equals(projectId) && schedule.getEntry(String.valueOf(taskId))== null){
			schedule = loadSchedule(projectId);
			request.getSession().setAttribute("schedule", schedule);
		}
		previousProjectID = projectId;
		ScheduleEntry scheduleEntry = null;
		if (schedule != null) {
			scheduleEntry = schedule.getEntry(String.valueOf(taskId));
			ServiceFactory.getInstance().getWorkplanWorkPercentChangeHandler().workPercentChanged(
					String.valueOf(taskId), scheduleEntry, String.valueOf(work_percent_complete), schedule);
		}
		if(work_percent_complete == 100){
			return scheduleEntry.getWorkTQ();
		}
		return scheduleEntry.getWorkCompleteTQ();
	}
	
	
	/**
	 * @param taskId
	 * @param newStartDate
	 * @param newEndDate
	 * @param projectId
	 * @param request
	 * @return
	 */
	private ScheduleEntry startDateEndDateChanged(Integer taskId, Date newStartDate, Date newEndDate, Integer projectId, HttpServletRequest request) {
		ScheduleEntry scheduleEntry = null;
		schedule = SessionManager.getSchedule();

		// if project id is changed then we have to load schedule for the new project
		if(!previousProjectID.equals(projectId)){
			schedule = loadSchedule(projectId);
			request.getSession().setAttribute("schedule", schedule);
		}
		
		previousProjectID = projectId;
		if(schedule != null){
			scheduleEntry = schedule.getEntry(String.valueOf(taskId));
			DateFormat dateFormat = SessionManager.getUser().getDateFormatter();
			//ServiceFactory.getInstance().getWorkplanDateChangeHandler().changeStartDate(String.valueOf(taskId), dateFormat.formatDate(newStartDate), schedule);
			//ServiceFactory.getInstance().getWorkplanDateChangeHandler().changeEndDate(String.valueOf(taskId), dateFormat.formatDate(newEndDate), schedule);
		}
		return scheduleEntry;
	}

	/**
	 * Method for loading schedule
	 * 
	 * @param projectId
	 * @return schedule
	 */
	private Schedule loadSchedule(Integer projectId) {
		try {
			schedule = new Schedule();
			Space space = SpaceFactory.constructSpaceFromID(String.valueOf(projectId));
			schedule.setSpace(space);
			schedule.loadAll();
		} catch (PersistenceException pnetEx) {
			logger.error("Error occurred while getting schedule details " + pnetEx.getMessage());
		}
		return schedule;
	}

	/**
	 * @return the logMessage
	 */
	public String getLogMessage() {
		return logMessage;
	}

	/**
	 * @return the schedule
	 */
	public Schedule getSchedule() {
		return schedule;
	}
}
