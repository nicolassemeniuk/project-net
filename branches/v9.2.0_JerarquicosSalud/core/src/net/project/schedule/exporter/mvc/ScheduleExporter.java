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
package net.project.schedule.exporter.mvc;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import net.project.soa.schedule.Project.Tasks.Task.Baseline;
import net.project.calendar.workingtime.DayOfWeekEntry;
import net.project.calendar.workingtime.ScheduleWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WeekDayBean;
import net.project.calendar.workingtime.WorkingTime;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.hibernate.model.PnPersonCosts;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.resource.Person;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskDependencyType;
import net.project.schedule.TaskList;
import net.project.schedule.TaskType;
import net.project.schedule.calc.TaskCalculationType;
import net.project.schedule.exporter.IScheduleExporter;
import net.project.soa.schedule.ObjectFactory;
import net.project.soa.schedule.Project;
import net.project.soa.schedule.Project.Assignments;
import net.project.soa.schedule.Project.Calendars;
import net.project.soa.schedule.Project.Resources;
import net.project.soa.schedule.Project.Tasks;
import net.project.soa.schedule.Project.Assignments.Assignment;
import net.project.soa.schedule.Project.Calendars.Calendar;
import net.project.soa.schedule.Project.Calendars.Calendar.WeekDays;
import net.project.soa.schedule.Project.Calendars.Calendar.WeekDays.WeekDay;
import net.project.soa.schedule.Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes;
import net.project.soa.schedule.Project.Resources.Resource;
import net.project.soa.schedule.Project.Resources.Resource.Rates;
import net.project.soa.schedule.Project.Resources.Resource.Rates.Rate;
import net.project.soa.schedule.Project.Tasks.Task;
import net.project.soa.schedule.Project.Tasks.Task.Baseline;
import net.project.soa.schedule.Project.Tasks.Task.PredecessorLink;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

import org.apache.log4j.Logger;
import org.hibernate.Session;

/**
 * This class is responsible for creating a jaxb context, populate the project
 * information and marshalling the project information to xml file.
 * 
 * @author avibha
 * 
 */
public class ScheduleExporter implements IScheduleExporter {
	private Logger logger = Logger.getLogger(ScheduleExporter.class);

	Project projectData = null;

	public void setProjectData(Project projectData) {
		this.projectData = projectData;
	}

	public Project getProjectData() {
		return projectData;
	}

	String spaceId = null;

	Schedule schedule = null;

	JAXBContext context = null;

	String projectName;

	ObjectFactory objectFactory = null;

	Marshaller marshaller = null;

	Tasks tasks = null;

	Assignments xmlAssignments = null;

	Calendars xmlCalendars = null;

	Resources resources = null;

	ScheduleWorkingTimeCalendarProvider calProvider = null;

	String baseCalendarUID = "0";

	public static final String TASKTYPE_TASK = "0";

	public static final String TASKTYPE_SUMMARY = "1";

	public static final String SCHEDULE_TASKTYPE_DEFAULT = "0";

	public static final String SCHEDULE_BASE_CALENDAR_UID = "-1";

	public static final String CURRENCY_SYMBOL_POSITION = "0";

	public static final String RESOURCE_TYPE_WORK = "1"; // Work Type
	
	public static final String LAG_DAY_FORMAT = "7";
	
	public static final String LAG_HOURS_FORMAT = "5"; 
	
	public static final String LAG_WEEK_FORMAT = "9";
	
	public static final String UNKNOWN_RESOURCE = "-65535";

	// resource

	private int assignmentUidCount;

	private int taskUidCount;

	private int calendarUidCount;

	private Map<String, String> personResourceIdMap;

	/** A map of MSP UID to project.net task id. */
	private final Map<Integer, Integer> taskMap = new HashMap<Integer, Integer>();

	/**
	 * Build and return the content for the xml file.
	 */
	public ByteArrayOutputStream getXml() throws Exception {
		init();
		loadProjectInfo();
		loadCalendars();
		loadTasks();
		return getProjectAsXml();
	}
	
	/**
	 * Build and return the content for the xml file.
	 */
	public Tasks getCsv() throws Exception {
		init();
		loadProjectInfo();
		loadCalendars();
		loadTasks();
		return this.projectData.getTasks();
	}

	/**
	 * 
	 * 
	 */
	private void loadCalendars() {
		calProvider = (ScheduleWorkingTimeCalendarProvider) schedule.getWorkingTimeCalendarProvider();
		xmlCalendars = objectFactory.createProjectCalendars();
		Collection calendars = calProvider.getBaseCalendars();
		Iterator iter = calendars.iterator();
		while (iter.hasNext()) {
			WorkingTimeCalendarDefinition calendarDefinition = (WorkingTimeCalendarDefinition) iter.next();
			loadCalendar(calendarDefinition);
		}
		projectData.setCalendars(this.xmlCalendars);
	}

	/**
	 * 
	 * @param calendarDefinition
	 */
	private void loadCalendar(WorkingTimeCalendarDefinition calendarDefinition) {
		Calendar xmlCalendar = objectFactory.createProjectCalendarsCalendar();
		xmlCalendar.setName(calendarDefinition.getDisplayName());
		Collection weekDays = calendarDefinition.getDayOfWeekEntries();
		if (weekDays != null && weekDays.size() > 0)
			loadWeekDays(weekDays, xmlCalendar);
		xmlCalendar.setIsBaseCalendar(calendarDefinition.isBaseCalendar());
		xmlCalendar.setUID(new BigInteger(String.valueOf(calendarUidCount++)));
		if (calendarDefinition.isBaseCalendar()) {
			baseCalendarUID = String.valueOf(calendarUidCount - 1);
			xmlCalendar.setBaseCalendarUID(new BigInteger(SCHEDULE_BASE_CALENDAR_UID));
			projectData.setCalendarUID(xmlCalendar.getUID());
		} else {
			xmlCalendar.setBaseCalendarUID(new BigInteger(baseCalendarUID));
		}
		this.xmlCalendars.getCalendar().add(xmlCalendar);
	}

	/**
	 * 
	 * @param weekDays
	 * @param xmlCalendar
	 * @return
	 */
	private void loadWeekDays(Collection weekDays, Calendar xmlCalendar) {
		WeekDays xmlWeekDays = objectFactory.createProjectCalendarsCalendarWeekDays();
		WeekDay xmlWeekDay = null;
		Iterator iter = weekDays.iterator();
		DayOfWeekEntry dayOfWeek = null;
		List weekDaysList = new ArrayList();
		while (iter.hasNext()) {
			dayOfWeek = (DayOfWeekEntry) iter.next();
			WeekDayBean weekDayBean = dayOfWeek.getWeekDayBean();
			weekDaysList.add(weekDayBean);
		}
		Collections.sort(weekDaysList, new WeekDayComparator());
		Iterator iter2 = weekDaysList.iterator();
		while (iter2.hasNext()) {
			WeekDayBean weekDayBean = (WeekDayBean) iter2.next();
			xmlWeekDay = objectFactory.createProjectCalendarsCalendarWeekDaysWeekDay();
			xmlWeekDay.setDayWorking(weekDayBean.isDayWorking());
			xmlWeekDay.setDayType(new BigInteger(weekDayBean.getDayType()));
			if (weekDayBean.getWorkingTimes() != null && weekDayBean.getWorkingTimes().size() > 0)
				loadWorkingTimes(xmlWeekDay, weekDayBean.getWorkingTimes());
			xmlWeekDays.getWeekDay().add(xmlWeekDay);
		}
		xmlCalendar.setWeekDays(xmlWeekDays);
	}

	/**
	 * 
	 * @param workingTimes
	 */
	private void loadWorkingTimes(WeekDay xmlWeekDay, List<WorkingTime> workingTimesList) {
		WorkingTimes xmlWorkingTimes = objectFactory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimes();
		Iterator iter = workingTimesList.iterator();
		WorkingTime workingTime = null;
		Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime xmlWorkingTime = null;
		while (iter.hasNext()) {
			xmlWorkingTime = objectFactory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimesWorkingTime();
			workingTime = (WorkingTime) iter.next();
			xmlWorkingTime.setFromTime(calToXGC(workingTime.getStartTime().getHour(), workingTime.getStartTime().getMinute()));
            xmlWorkingTime.setToTime(calToXGC(workingTime.getEndTime().getHour(), workingTime.getEndTime().getMinute()));
            xmlWorkingTimes.getWorkingTime().add(xmlWorkingTime);
		}
		xmlWeekDay.setWorkingTimes(xmlWorkingTimes);
	}

	/**
	 * Build the projectData object which holds the project information to be
	 * exported.
	 * 
	 * @throws Exception
	 */
	private void init() throws Exception {
		assignmentUidCount = 0;
		taskUidCount = 1;
		calendarUidCount = 1;
		personResourceIdMap = new HashMap();
		context = JAXBContext.newInstance("net.project.soa.schedule");
		objectFactory = new ObjectFactory();
		marshaller = context.createMarshaller();
		projectData = objectFactory.createProject();
		spaceId = schedule.getSpaceId();
		projectData.setUID(spaceId);
		projectData.setDefaultStartTime(date2XMLGregorianCalendar(schedule.getEarliestStartDate()));
		projectData.setDefaultFinishTime(date2XMLGregorianCalendar(schedule.getEarliestFinishDate()));

		projectData.setSplitsInProgressTasks(true);
		projectData.setNewTasksEffortDriven(true);
		projectData.setDefaultTaskType(new BigInteger(SCHEDULE_TASKTYPE_DEFAULT));
	}

	/**
	 * Marshal the projectData to a string
	 * 
	 * @return
	 * @throws Exception
	 */
	private ByteArrayOutputStream getProjectAsXml() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(projectData, baos);
		return baos;
	}

	/**
	 * load tasks for the current project.
	 * 
	 */
	private void loadTasks() {
		TaskList srcList = null;
		Task destTask = null;
		Iterator iter = null;

		if (schedule != null)
			srcList = schedule.getTaskList();
		if (srcList != null)
			iter = srcList.iterator();

		tasks = objectFactory.createProjectTasks();
		xmlAssignments = objectFactory.createProjectAssignments();
		resources = objectFactory.createProjectResources();
		int currentHierarchyLevel = 1;
		int previousHierarchyLevel = 1;
		long prevTaskId = 0;
		long currentTaskId = 0;
		String outLineNumber = "1";
		while (iter.hasNext()) {
			ScheduleEntry srcTask = (ScheduleEntry) iter.next();

			loadAssignment(xmlAssignments, srcTask);
			destTask = objectFactory.createProjectTasksTask();
			destTask.setID(new BigInteger(String.valueOf(taskUidCount++)));
			
			try {
				if(taskMap.get(srcTask.getID()) == null && srcTask.getPredecessors() != null && srcTask.getPredecessors().size() > 0) {
					String[] dependencyIDs = srcTask.getDependentIds().split(",");
					for(int index=0 ; index < dependencyIDs.length; index++) {
							taskMap.put(new Integer(srcTask.getPredecessors().get(index).getDependencyID()), srcTask.getPredecessors().get(index).getSequenceNumber());
					}
				}
			}catch (PersistenceException e) {
				logger.warn("Inconsistant data found while exporting dependency list for each task :"+e.getMessage());
			}
			
			destTask.setUID(destTask.getID());
			destTask.setIsNull(false);
			destTask.setCreateDate(date2XMLGregorianCalendar(new Date()));

			if (srcTask.getActualStartTime() != null)
				destTask.setActualStart(date2XMLGregorianCalendar(srcTask.getActualStartTime()));
			if (srcTask.getActualEndTime() != null)
				destTask.setActualFinish(date2XMLGregorianCalendar(srcTask.getActualEndTime()));

			int constraintId = ExportHelper.getMSPConstraintId(srcTask.getConstraintType().getID());
			destTask.setConstraintType(new BigInteger(String.valueOf(constraintId)));
			destTask.setName(srcTask.getName());
			if (srcTask.getConstraintDate() != null)
				destTask.setConstraintDate(date2XMLGregorianCalendar(srcTask.getConstraintDate()));

			BigDecimal percentComplete = srcTask.getPercentCompleteDecimal();
			percentComplete = percentComplete.multiply(new BigDecimal(100));
			BigDecimal workPercentComplete = srcTask.getWorkPercentCompleteDecimal();
			
			workPercentComplete = workPercentComplete.multiply(new BigDecimal(100));
			destTask.setPercentComplete(percentComplete.toBigInteger());
			destTask.setPercentWorkComplete(workPercentComplete.toBigInteger());
			destTask.setPriority(new BigInteger(ExportHelper.getPriority(srcTask.getPriority().getID())));
			if (srcTask.getWorkVariance() != null && srcTask.getWorkVariance().getAmount() != null)
				destTask.setWorkVariance(srcTask.getWorkVariance().getAmount().floatValue());
			destTask.setSummary(TaskType.SUMMARY.equals(srcTask.getTaskType()));

			destTask.setMilestone(srcTask.isMilestone());

			setStartAndFinishDates(destTask, srcTask.getStartTime(), srcTask.getEndTime());

			if (!srcTask.getDurationTQ().isZero() && (null != srcTask.getDurationTQ())) {
				destTask.setDuration(ExportHelper.getDuration(srcTask.getDurationTQ()));
				destTask.setDurationFormat(ExportHelper.getDurationFormat(srcTask.getDurationTQ()));
			}

			destTask.setWork(ExportHelper.getDuration(srcTask.getWorkTQ()));

			TaskCalculationType taskCalcType = srcTask.getTaskCalculationType();
			boolean isEffortDriven = taskCalcType.isEffortDriven();
			destTask.setEffortDriven(isEffortDriven);
			if( taskCalcType.isFixedDuration() ) {
				destTask.setType(BigInteger.valueOf(1));
				destTask.setLevelAssignments(true);
			} else if( taskCalcType.isFixedUnit() ) {
				destTask.setType(BigInteger.valueOf(0));
				destTask.setLevelAssignments(false);
			} else {
				destTask.setType(BigInteger.valueOf(2));
				destTask.setLevelAssignments(true);
			}
			
			Baseline baseline = new Baseline();
			
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(srcTask.getStartTime());
			XMLGregorianCalendar date2;
			try {
				date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				baseline.setStart(date2);
			} catch (DatatypeConfigurationException e) {
				baseline.setStart(null);
				e.printStackTrace();
			}

			GregorianCalendar c2 = new GregorianCalendar();
			c2.setTime(srcTask.getEndTime());
			XMLGregorianCalendar date3;
			try {
				date3 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				baseline.setFinish(date3);
			} catch (DatatypeConfigurationException e) {
				baseline.setFinish(null);
				e.printStackTrace();
			}
			
			destTask.getBaseline().add(baseline);
			
			destTask.setCritical(srcTask.isCriticalPath());
			destTask.setExternalTask(srcTask.isFromShare());
			destTask.setEarlyStart(date2XMLGregorianCalendar(srcTask.getEarliestStartDate()));
			destTask.setEarlyFinish(date2XMLGregorianCalendar(srcTask.getEarliestFinishDate()));
			destTask.setLateStart(date2XMLGregorianCalendar(srcTask.getLatestStartDate()));
			destTask.setLateFinish(date2XMLGregorianCalendar(srcTask.getLatestFinishDate()));
			if(taskCalcType.isFixedUnit()) {
				destTask.setResumeValid(false);
			} else {
				destTask.setResumeValid(true);
			}
			destTask.setNotes(srcTask.getDescription());
            //other wise milestone tasks are not exported as milesones. we need some work to be set
            if(srcTask.isMilestone() && srcTask.getDurationTQ().subtract(srcTask.getActualDuration()).isZero())
                destTask.setRemainingDuration(ExportHelper.getDuration(new TimeQuantity(1, TimeQuantityUnit.MINUTE)));
            else 
                destTask.setRemainingDuration(ExportHelper.getDuration(srcTask.getDurationTQ()).subtract(ExportHelper.getDuration(srcTask.getActualDuration())));
			
			destTask.setActualDuration(ExportHelper.getDuration(srcTask.getActualDuration()));
			destTask.setActualWork(ExportHelper.getDuration(srcTask.getWorkCompleteTQ()));
			destTask.setFixedCostAccrual("3");

			TimeQuantity variance = srcTask.getStartDateVariance(schedule.getWorkingTimeCalendarProvider());
			BigInteger startVariance = variance.getAmount().toBigInteger();
			destTask.setStartVariance(startVariance);

			variance = srcTask.getEndDateVariance(schedule.getWorkingTimeCalendarProvider());
			BigInteger finishVariance = variance.getAmount().toBigInteger();
			destTask.setFinishVariance(finishVariance);

			variance = srcTask.getWorkVariance();
			float workVariance = variance.getAmount().floatValue();
			destTask.setWorkVariance(workVariance);

			destTask.setOutlineLevel(new BigInteger(srcTask.getHierarchyLevel()));
			currentHierarchyLevel = destTask.getOutlineLevel().intValue();
			currentTaskId = destTask.getID().longValue();
			if (prevTaskId != 0) {
				outLineNumber = incrementOutlineNumber(previousHierarchyLevel, currentHierarchyLevel, outLineNumber);
			}
			prevTaskId = currentTaskId;
			previousHierarchyLevel = currentHierarchyLevel;
			destTask.setOutlineNumber(outLineNumber);
			destTask.setWBS(outLineNumber);
			destTask.setDeadline(date2XMLGregorianCalendar(srcTask.getDeadline()));
			try {
				if (srcTask.getPredecessors() != null && srcTask.getPredecessors().size() > 0) {

					List<PredecessorLink> pl = destTask.getPredecessorLink();

					for (int i = 0; i < srcTask.getPredecessors().size(); i++) {
						PredecessorLink predecessorLink = new PredecessorLink();
						predecessorLink.setCrossProject(false);
						if(srcTask.getPredecessors().get(i).getLag().getUnits() == TimeQuantityUnit.DAY ) {
							predecessorLink.setLinkLag(new BigInteger(srcTask.getPredecessors().get(i).getLag().formatAmount()).multiply(BigInteger.valueOf(60 * 8 * 10))); 
                            // min * hr * 10
							predecessorLink.setLagFormat(new BigInteger(LAG_DAY_FORMAT));
						} else if( srcTask.getPredecessors().get(i).getLag().getUnits() == TimeQuantityUnit.HOUR ) {
							predecessorLink.setLinkLag(new BigInteger(srcTask.getPredecessors().get(i).getLag().formatAmount()).multiply(BigInteger.valueOf(60 * 10))); 
                            // min * 10
							predecessorLink.setLagFormat(new BigInteger(LAG_HOURS_FORMAT));
						} else if( srcTask.getPredecessors().get(i).getLag().getUnits() == TimeQuantityUnit.WEEK ) {
							predecessorLink.setLinkLag(new BigInteger(srcTask.getPredecessors().get(i).getLag().formatAmount()).multiply(BigInteger.valueOf(60 * 8 * 6 * 10))); 
                            // min * hr * days * 10 
							predecessorLink.setLagFormat(new BigInteger(LAG_WEEK_FORMAT));
						}
				
						Integer taskUID = taskMap.get(new Integer(srcTask.getPredecessorsNoLazyLoad().get(i).getDependencyID()));
						predecessorLink.setPredecessorUID(new BigInteger(taskUID.toString()));
						predecessorLink.setType(BigInteger.valueOf(TaskDependencyType.convertToMSPID(Integer.parseInt(srcTask.getPredecessors().get(i).getDependencyTypeID()))));
						pl.add(predecessorLink);
					}
				}
			} catch (Exception predecessorLinkException) {
				logger.warn("Inconsistant data found while exporting dependency list :"
						+ predecessorLinkException.getMessage());
			}
			tasks.getTask().add(destTask);
		}
		projectData.setTasks(tasks);
		projectData.setAssignments(xmlAssignments);
		projectData.setResources(resources);

	}

	private BigInteger toMSPTaskType(TaskCalculationType taskCalcType) {
		if ( taskCalcType.isFixedDuration() )
			return BigInteger.valueOf(0);
		if ( taskCalcType.isFixedUnit() )
			return BigInteger.valueOf(1);
		if ( taskCalcType.isFixedWork() )
			return BigInteger.valueOf(2);
		return null;
	}

	private void setStartAndFinishDates(Task destTask, Date startTime, Date endTime) {
		GregorianCalendar start = new GregorianCalendar();
		start.setTime(startTime);
		GregorianCalendar end = new GregorianCalendar();
		end.setTime(endTime);
		
		if(start.get(java.util.Calendar.YEAR) == end.get(java.util.Calendar.YEAR) && 
			start.get(java.util.Calendar.MONTH) == end.get(java.util.Calendar.MONTH) &&
			start.get(java.util.Calendar.DAY_OF_MONTH) == end.get(java.util.Calendar.DAY_OF_MONTH)){
			
			end.add(java.util.Calendar.MINUTE, 1); // handle ms project zero day bug.
		}
		destTask.setStart(date2XMLGregorianCalendar(startTime));
		destTask.setFinish(date2XMLGregorianCalendar(end.getTime()));	
		
	}

	/**
	 * Function to generate outLevelNumber for current level, depending on
	 * previous Level
	 * 
	 * @param prevHierarchyLevel
	 * @param currentHierarchyLevel
	 * @param outLineNumber
	 * @return
	 */
	public static String incrementOutlineNumber(int prevHierarchyLevel, int currentHierarchyLevel, String outLineNumber) {

		String[] levels = null;
		if (currentHierarchyLevel > prevHierarchyLevel) {
			outLineNumber += ".1";
		} else if (currentHierarchyLevel < prevHierarchyLevel) {
			int delta = prevHierarchyLevel - currentHierarchyLevel;
			outLineNumber = outLineNumber.replace(".", "-");
			levels = outLineNumber.split("-");
			outLineNumber = "";
			if (currentHierarchyLevel != 1) {
				int i = 0;
				for (; i < levels.length - delta - 1; i++) {
					outLineNumber += levels[i] + ".";
				}
				outLineNumber += (Integer.parseInt(levels[i]) + 1);
			} else {
				outLineNumber = "" + (Integer.parseInt(levels[0]) + 1);
			}
		} else {
			outLineNumber = outLineNumber.replace(".", "-");
			levels = outLineNumber.split("-");
			outLineNumber = "";
			int i = 0;
			for (; i < levels.length - 1; i++) {
				outLineNumber += levels[i] + ".";
			}
			outLineNumber += "" + (Integer.parseInt(levels[i]) + 1);
		}
		return outLineNumber;
	}

	/**
	 * load assignments for a task
	 * 
	 * @param assignments
	 * @param task
	 */
	private void loadAssignment(Assignments assignments, ScheduleEntry task) {
		ScheduleEntryAssignment assignment = null;
		Assignment xmlAssignment = null;
		String planID = task.getPlanID();
		Collection assignmentList = task.getAssignments();
		Iterator iter = assignmentList.iterator();
		while (iter.hasNext()) {
			xmlAssignment = objectFactory.createProjectAssignmentsAssignment();
			assignment = (ScheduleEntryAssignment) iter.next();
			int resourceUid = loadResource(resources, assignment, planID);
			TimeQuantity timeQty = assignment.getWork();
			Duration work = ExportHelper.getDuration(timeQty);
			if (work != null)
				xmlAssignment.setWork(work);

			timeQty = assignment.getWorkComplete();
			Duration actualWork = ExportHelper.getDuration(timeQty);
			xmlAssignment.setActualWork(actualWork);
			xmlAssignment.setUID(new BigInteger(String.valueOf(assignmentUidCount++)));
			xmlAssignment.setPercentWorkComplete(assignment.getPercentComplete().multiply(new BigDecimal(100)).toBigInteger());
			xmlAssignment.setUnits(assignment.getPercentAssigned().floatValue());
			xmlAssignment.setTaskUID(new BigInteger(String.valueOf(taskUidCount)));
			xmlAssignment.setResourceUID(new BigInteger(String.valueOf(resourceUid)));
			xmlAssignment.setRemainingWork(ExportHelper.getDuration(assignment.getWorkRemaining()));
            xmlAssignment.setMilestone(task.isMilestone());

			if (assignment.getActualStart() != null)
				xmlAssignment.setActualStart(date2XMLGregorianCalendar(assignment.getActualStart()));
			if (assignment.getActualFinish() != null)
				xmlAssignment.setActualFinish(date2XMLGregorianCalendar(assignment.getActualFinish()));
			if (assignment.getStartTime() != null)
				xmlAssignment.setStart(date2XMLGregorianCalendar(assignment.getStartTime()));
			if (assignment.getEndTime() != null) {
				xmlAssignment.setStop(date2XMLGregorianCalendar(assignment.getEndTime()));
				xmlAssignment.setFinish(date2XMLGregorianCalendar(assignment.getEndTime()));
			}
			assignments.getAssignment().add(xmlAssignment);
		}
		if(xmlAssignment == null){
			xmlAssignment = objectFactory.createProjectAssignmentsAssignment();
			xmlAssignment.setUID(new BigInteger(String.valueOf(assignmentUidCount++)));
			xmlAssignment.setPercentWorkComplete( task.getPercentCompleteDecimal().multiply(new BigDecimal(100)).toBigInteger());
			xmlAssignment.setTaskUID(new BigInteger(String.valueOf(taskUidCount)));
			xmlAssignment.setResourceUID(new BigInteger(String.valueOf(UNKNOWN_RESOURCE)));
            xmlAssignment.setMilestone(task.isMilestone());
			assignments.getAssignment().add(xmlAssignment);
		}
	}

	/**
	 * Load a resource information from an assignment
	 * 
	 * @param resources
	 * @param assignment
	 * @param planID
	 */
	private int loadResource(Resources resources, ScheduleEntryAssignment assignment, String planID) {
		Resource resource = objectFactory.createProjectResourcesResource();
		resource.setName(assignment.getPersonName());
		String personID = assignment.getPersonID();
		int resId = getResourceUid(personID);
		if (resId < personResourceIdMap.values().size() + 1) // Resource
			// already added
			return resId;
		// If resource not already added, Add mapping for the new resource
		personResourceIdMap.put(personID, String.valueOf(resId));
		Person person = new Person(personID);
		try {
			person.load();
		} catch (PersistenceException e) {
			logger.error(e.getMessage());
		}
		String calendarUID = loadResourceCalendar(personID);
		if (calendarUID != null)
			resource.setCalendarUID(new BigInteger(calendarUID));
		else
			resource.setCalendarUID(new BigInteger(baseCalendarUID));

		resource.setType(new BigInteger(RESOURCE_TYPE_WORK));
		resource.setIsNull(false);
		resource.setEmailAddress(person.getEmail());
		resource.setUID(new BigInteger(String.valueOf(resId)));
		resource.setID(resource.getUID());
		// resource rates are not imported/exported from MSXML as of 9.0
		// Open the hibernate session
		//Session hbSession = DBTransactionManager.getSession();
		//Rates resourceRates = objectFactory.createProjectResourcesResourceRates();

		//loadResourceRates(hbSession, resource, personID, resourceRates);
		//resource.setRates(resourceRates);
		//hbSession.close();

		resources.getResource().add(resource);
		return resId;
	}

	private int getResourceUid(String personID) {
		Collection ids = personResourceIdMap.values();
		String strId = (String) personResourceIdMap.get(personID);
		if (strId == null) {
			return ids.size() + 1;
		} else {
			return Integer.parseInt(strId);
		}
	}

	/**
	 * 
	 * @param personID
	 */
	private String loadResourceCalendar(String personID) {
		WorkingTimeCalendarDefinition resourceCalendar = calProvider.getForResourceID(personID);
		if (resourceCalendar != null) {
			loadCalendar(resourceCalendar);
			return String.valueOf(calendarUidCount - 1);
		} else {
			return null;
		}
	}

	/**
	 * Load rate card details for a resource
	 * 
	 * @param hbSession
	 * @param resource
	 * @param personID
	 * @param resourceRates
	 */
	/*private void loadResourceRates(Session hbSession, Resource resource, String personID, Rates resourceRates) {

		List rates = hbSession.createQuery("from PnPersonCosts rateCard where rateCard.personId = ?").setBigInteger(0,
				new BigInteger(personID)).list();
		Iterator iter = rates.iterator();
		Rate destRateCard = null;
		while (iter.hasNext()) {
			destRateCard = objectFactory.createProjectResourcesResourceRatesRate();
			PnPersonCosts srcRate = (PnPersonCosts) iter.next();
			destRateCard.setCostPerUse(srcRate.getCostPerUse());
			destRateCard.setOvertimeRate(srcRate.getOvertimeRate());
			destRateCard.setOvertimeRateFormat(srcRate.getOvertimeRateFormat());
			if (srcRate.getRatesFrom() != null)
				destRateCard.setRatesFrom(date2XMLGregorianCalendar(srcRate.getRatesFrom()));
			if (srcRate.getRatesTo() != null)
				destRateCard.setRatesTo(date2XMLGregorianCalendar(srcRate.getRatesTo()));
			destRateCard.setRateTable(srcRate.getRateTable());
			destRateCard.setStandardRate(srcRate.getStandardRate());
			destRateCard.setStandardRateFormat(srcRate.getStandardRateFormat());
			resourceRates.getRate().add(destRateCard);
		}
	}*/

	/**
	 * 
	 * 
	 */
	private void loadProjectInfo() {
		ProjectSpace projectSpace = new ProjectSpace(projectData.getUID());
		try {
			projectSpace.load();
		} catch (PersistenceException e) {
			logger.error(e.getMessage());
		}
		projectData.setTitle(projectSpace.getDescription());
		projectData.setName(projectSpace.getName());
		projectName = projectSpace.getName();
		projectData.setDefaultTaskType(new BigInteger(SCHEDULE_TASKTYPE_DEFAULT));
		try {
			if (projectSpace.getCreationDate(spaceId) != null)
				projectData.setCreationDate(date2XMLGregorianCalendar(projectSpace.getCreationDate(spaceId)));
		} catch (PersistenceException e) {
			logger.error(e.getMessage());
		}

		if (schedule.getScheduleStartDate() != null)
			projectData.setStartDate(date2XMLGregorianCalendar(schedule.getScheduleStartDate()));
		else
			projectData.setStartDate(date2XMLGregorianCalendar(new Date()));

		if (schedule.getScheduleEndDate() != null)
			projectData.setFinishDate(date2XMLGregorianCalendar(schedule.getScheduleEndDate()));
		else
			projectData.setFinishDate(date2XMLGregorianCalendar(new Date()));

		projectData.setCurrencySymbol(projectSpace.getDefaultCurrency().getSymbol());
		projectData.setCurrencySymbolPosition(new BigInteger(CURRENCY_SYMBOL_POSITION));
		projectData.setCurrencyDigits(new BigInteger("" + projectSpace.getDefaultCurrency().getDefaultFractionDigits()));
	}

	/**
	 * 
	 * @return
	 */
	public Schedule getSchedule() {
		return schedule;
	}

	/**
	 * 
	 */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * Convert <Code>java.util.Date</Code> to <Code>javax.xml.datatype.XMLGregorianCalendar</Code>
	 * 
	 * @param d
	 * @return XMLGregorianCalendar
	 */
	public XMLGregorianCalendar date2XMLGregorianCalendar(java.util.Date dt) {
		if (dt == null)
			return null;
		XMLGregorianCalendar xgc = null;
		GregorianCalendar gc = new GregorianCalendar(net.project.security.SessionManager.getUser().getTimeZone());
		gc.setTime(dt);
		try {
			xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar();
			xgc.setYear(new BigInteger(String.valueOf(gc.get(java.util.Calendar.YEAR))));
			xgc.setMonth(gc.get(java.util.Calendar.MONTH) + 1);
			xgc.setDay(gc.get(java.util.Calendar.DAY_OF_MONTH));
			xgc.setHour(gc.get(java.util.Calendar.HOUR_OF_DAY));
			xgc.setMinute(gc.get(java.util.Calendar.MINUTE));
			xgc.setSecond(gc.get(java.util.Calendar.SECOND));
		} catch (DatatypeConfigurationException e) {
			logger.error(e.getMessage());
		}
		return xgc;
	}

	/**
	 * Convert hour minute to <Code>javax.xml.datatype.XMLGregorianCalendar</Code>
	 * 
	 * @param cal
	 * @return XMLGregorianCalendar
	 */
	private XMLGregorianCalendar calToXGC(int hour, int minute) {
		XMLGregorianCalendar xgc = null;
		try {
			xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            if(hour < 24) {
    			xgc.setHour(hour);
    			xgc.setMinute(minute);
            } else {
                xgc.setHour(23);
                xgc.setMinute(59);
            }
			xgc.setSecond(0);
		} catch (DatatypeConfigurationException e) {
			logger.error(e.getMessage());
		}
		return xgc;
	}

	/**
	 * 
	 */
	public String getProjectName() {
		return projectName;
	}
}
