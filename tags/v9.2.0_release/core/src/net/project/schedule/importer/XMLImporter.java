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
package net.project.schedule.importer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.project.base.property.PropertyProvider;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.Roster;
import net.project.schedule.PredecessorList;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryFactory;
import net.project.schedule.TaskConstraintType;
import net.project.schedule.TaskDependency;
import net.project.schedule.TaskDependencyType;
import net.project.schedule.TaskPriority;
import net.project.schedule.TaskType;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.SessionManager;
import net.project.soa.schedule.Project;
import net.project.soa.schedule.Project.Assignments.Assignment;
import net.project.soa.schedule.Project.Calendars.Calendar;
import net.project.soa.schedule.Project.Resources.Resource;
import net.project.soa.schedule.Project.Tasks.Task;
import net.project.soa.schedule.Project.Tasks.Task.PredecessorLink;
import net.project.space.Space;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;
import oracle.jdbc.driver.OracleConnection;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Class designed to import XML_SUPPORTED files into the Project.Net database.
 * An XML_SUPPORTED file is a file format created by Microsoft which contains MS
 * Project file.
 * 
 * 
 * @author Avinash Bhamare
 * @since Version 8.3.0
 */
public class XMLImporter implements IScheduleImporter {

	//
	// Static Members
	//

	private Logger logger = Logger.getLogger(ScheduleUpload.class);

	/**
	 * Maximum depth of subtasks we support. This is kind of arbitrary, but
	 * needs to be here because we create an array based on this value.
	 */
	private static final int MAXIMUM_OUTLINE_DEPTH = 256;

	/**
	 * projectData jaxb object in unmarshled from the xml file We need to have
	 * the limit on number of tasks one file can contain This limit is added so
	 * that we can configure this limit as per the server resources, by default
	 * it is 512, but this can be overwritten by system property
	 */
	private static final int MAXIMUM_TASK_ALLOWED = 10;

	/**
	 * Helper method that constructs a work amount from a work value stored in
	 * an XML. <p/> The value is divided by 1000 to get a minute value, then a
	 * time quantity constructed with a scale of 2.
	 * 
	 * @param storedWorkValue
	 *            the raw value in the XML column
	 * @return the time quantity for that work with a scale of 2
	 */
	static TimeQuantity constructWork(double storedWorkValue) {
		BigDecimal workAmountMinutes = new BigDecimal(String.valueOf(storedWorkValue)).movePointLeft(3);
		return new TimeQuantity(workAmountMinutes, TimeQuantityUnit.SECOND).convertTo(TimeQuantityUnit.HOUR, 2);
	}

	//
	// Instance Members
	//

	/** The local filename of the XML file that holds this database. */
	private String fileName;

	/** Whether or not we are going to import tasks as part of the import. */
	private boolean importTasks;

	/** Whether or not we are going to import assignments as part of the import. */
	private boolean importAssignments;

	/**
	 * Indicates whether or not we are going to import working time calendars as
	 * part of the import.
	 */
	private boolean importWorkingTimeCalendars;

	/**
	 * Indicates whether or not we are going to import working time calendars
	 * for resources as part of the import.
	 */
	private boolean importResourceWorkingTimeCalendars;

	/**
	 * Indicates if we should import the start and finish dates of the schedule
	 * from the MS Project file.
	 */
	private boolean importStartAndFinishDates = false;

	/**
	 * Indicates whether to update the schedule default calendar with the
	 * imported default calendar; otherwise the imported default calendar will
	 * be added to the schedule.
	 */
	private boolean isUpdateDefaultCalendar;

	/**
	 * The project id of the selected project in the XML file that we are going
	 * to import. Althouh this is not required for single file import, because
	 * xml file exported from microsoft will have only one project in it. But as
	 * we plan to use same importer for webservices functionality, which can
	 * have xml document with multiple projects, hence this project id.
	 */
	private int msProjectID;

	/** The start date of the project as seen in MS Project. */
	private Date mspStartDate;

	/** The end date of the project as seen in MS Project. */
	private Date mspEndDate;

	/**
	 * A map of MSP resource id to a
	 * {@link net.project.soa.schedule.Project.Resources.Resource} object that
	 * holds the information for that resource.
	 */
	private final Map<Integer, Resource> resources = new HashMap<Integer, Resource>();

	/** A map of MSP tasks ids to {@link net.project.schedule.Task} object. */
	private final Map<Integer, ScheduleEntry> taskMap = new HashMap<Integer, ScheduleEntry>();

	/** A map of MSP task ids to Project.net task ID's. */
	private final Map taskIDMap = new HashMap();

	/**
	 * A list of tasks in the same order that the appeared in MS Project. It is
	 * important to maintain this order because it will determine the order that
	 * will appear after we've imported into PNET. Each entry is an
	 * <code>Integer</code>.
	 */
	private final LinkedHashSet<Integer> orderedTaskList = new LinkedHashSet<Integer>();

	/**
	 * A map of MSP assignments ids to
	 * {@link net.project.soa.schedule.Project.Assignments.Assignment} objects.
	 */
	private final Map<Integer, Assignment> assignmentMap = new HashMap<Integer, Assignment>();

	/**
	 * A multi-map of MSP task ids to {@link Project.Assignments.Assignment}
	 * objects where each assignment is assiged to the MSP task ID. <p/> Each
	 * key is an <code>Integer</code> task ID and each value is a
	 * <code>Collection</code> where each element of the collection is an
	 * <code>Project.Assignments.Assignment</code>.
	 */
	private final MultiMap taskAssignmentMap = new MultiValueMap();

	/**
	 * A map of MSP link successor uid's to an arraylist of
	 * {@link net.project.schedule.importer.MSPLink} objects.
	 */
	private final Map<Integer, List> dependencies = new HashMap<Integer, List>();

	/**
	 * A map of MSP task uid's to parent MSP task uid; each value is the direct
	 * ancestor of the key.
	 */
	private final Map<Integer, Integer> parents = new HashMap<Integer, Integer>();

	/** All calendars found in the MS project database. */
	private final MSPCalendars allCallendars = new MSPCalendars();

	/** The base calendars to import. */
	private Collection<Project.Calendars.Calendar> baseCalendars = Collections.EMPTY_LIST;

	/** The resource calendars to import. */
	private Collection<Project.Calendars.Calendar> resourceCalendars = Collections.EMPTY_LIST;

	/**
	 * A helper class to produce an unambigous resource to person mapping for
	 * the purposes of assigning resource working time calendars.
	 */
	private ResourceResolver resourceResolver;
    
    /**
     * A Map to store the resource id mapping with its assignor
     */
    private Map assignorMap = new HashMap();

	/**
	 * A <code>Space</code> object which represents the space from which this
	 * import was called.
	 */
	private Space currentSpace;

	/**
	 * An object which will contain any errors that occur as part of this
	 * import.
	 */
	private final ErrorReporter errorReporter = new ErrorReporter();

	/** The schedule object that we are importing into. */
	private Schedule schedule;

	/** The roster required for locating persons and their time zones. */
	private Roster roster;

	/**
	 * Project class which is an jaxb mapped class to hold the content
	 */
	private Project projectData;
	
	private TimeZone userTimeZone = SessionManager.getUser().getTimeZone();
	private Locale userLocale = SessionManager.getUser().getLocale();

	/**
	 * Get an object that can contain and report on errors that occurred during
	 * the import.
	 * 
	 * @return a <code>net.project.util.ErrorReporter</code> object.
	 */
	public ErrorReporter getErrorReporter() {
		return errorReporter;
	}

	/**
     * This method ensures that the output String has only
     * valid XML unicode characters as specified by the
     * XML 1.0 standard. For reference, please see
     * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
     * standard</a>. This method will return an empty
     * String if the input is null or empty.
     *
     * @param in The String whose non-valid characters we want to remove.
     * @return The in String, stripped of non-valid characters.
     */
    public String stripNonValidXMLCharacters(String in) {
    	
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.

        if (in == null || ("".equals(in))) return ""; // vacancy test.
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
            if ((current == 0x9) ||
                (current == 0xA) ||
                (current == 0xD) ||
                ((current >= 0x20) && (current <= 0xD7FF)) ||
                ((current >= 0xE000) && (current <= 0xFFFD)) ||
                ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    } 
    
	public void init() throws ImportException {
		
		try {
			
			String cleanXml = stripNonValidXMLCharacters(FileUtils.readFileToString(new File(fileName), SessionManager.getCharacterEncoding()));
			
			// create a JAXBContext capable of handling classes related to xml
			// project
			JAXBContext jc = JAXBContext.newInstance("net.project.soa.schedule");
			// create an Unmarshaller
			Unmarshaller u = jc.createUnmarshaller();
			// unmarshal a po instance document into a tree of Java content
			// objects composed of classes from the primer.po package.
			projectData = (Project) u.unmarshal(new ByteArrayInputStream(cleanXml.getBytes(SessionManager.getCharacterEncoding())));
			// projectData.getResources().getResource().get(0).getRates().getRate().get(0);
		} catch (JAXBException je) {
			je.printStackTrace();
			logger.error("Unable to unmarshal the xml file error: " + je.getMessage());
			throw new ImportException(PropertyProvider.get("prm.schedule.import.unsupportedfiletype.message"), je);
		} catch (Exception ioe) {
			ioe.printStackTrace();
			logger.error("Exception while unmarshaling xml import file, error: " + ioe.getMessage());
			throw new ImportException(PropertyProvider.get("prm.schedule.import.unabletoimportselectedfile.message"), ioe);
		}
		if (null != projectData.getTasks() && null != projectData.getTasks().getTask()) {
			int numberOftasks = projectData.getTasks().getTask().size();
			int maxTasksAllowed = PropertyProvider.getInt("prm.schedule.import.xml.maxtasksallowed");
			if (maxTasksAllowed <= 0)
				maxTasksAllowed = MAXIMUM_TASK_ALLOWED;
			if (maxTasksAllowed < numberOftasks)
				throw new ImportException(PropertyProvider.get("prm.schedule.import.xml.maxtasksexceeded.message"));
		}
	}

	/**
	 * Set the space that contains the schedule into which we are importing the
	 * schedule entries.
	 * 
	 * @param currentSpace
	 *            a <code>Space</code> which is the current space.
	 */
	public void setCurrentSpace(Space currentSpace) {
		this.currentSpace = currentSpace;
	}

	/**
	 * Indicates if we are going to be importing tasks when we do the import.
	 * 
	 * @return a <code>boolean</code> value indicating if we are going to
	 *         import tasks.
	 */
	public boolean isImportTasks() {
		return importTasks;
	}

	/**
	 * Indicate if we are going to be importing tasks when we do the import.
	 * 
	 * @param importTasks
	 *            a <code>boolean</code> indicating if we are going to be
	 *            importing tasks.
	 */
	public void setImportTasks(boolean importTasks) {
		this.importTasks = importTasks;
	}

	public boolean isImportAssignments() {
		return importAssignments;
	}

	public void setImportAssignments(boolean importAssignments) {
		this.importAssignments = importAssignments;
	}

	/**
	 * Indicates if we are going to load the start and end dates of the project
	 * from MSP and set them for our schedule. This will be the default behavior
	 * if the schedule start and finish date have not been set in the past.
	 * 
	 * @return a <code>boolean</code> indicating if the start and end dates
	 *         are going to be imported.
	 */
	public boolean isImportStartAndEndDates() {
		return importStartAndFinishDates;
	}

	/**
	 * Indicates whether we are going to load the start and end dates of the
	 * project from MSP and set them for our schedule.
	 * 
	 * @param importStartAndEndDates
	 *            a <code>boolean</code> indicating if we are going to load
	 *            the start and end dates for our project from the MPD file.
	 */
	public void setImportStartAndEndDates(boolean importStartAndEndDates) {
		this.importStartAndFinishDates = importStartAndEndDates;
	}

	public boolean isImportWorkingTimeCalendars() {
		return this.importWorkingTimeCalendars;
	}

	public void setImportWorkingTimeCalendars(boolean importWorkingTimeCalendars) {
		this.importWorkingTimeCalendars = importWorkingTimeCalendars;
	}

	public boolean isImportResourceWorkingTimeCalendars() {
		return this.importResourceWorkingTimeCalendars;
	}

	public void setImportResourceWorkingTimeCalendars(boolean importWorkingTimeCalendars) {
		this.importResourceWorkingTimeCalendars = importWorkingTimeCalendars;
	}

	public boolean isImportStartAndFinishDates() {
		return importStartAndFinishDates;
	}

	public void setImportStartAndFinishDates(boolean importStartAndFinishDates) {
		this.importStartAndFinishDates = importStartAndFinishDates;
	}

	/**
	 * Specifies whether to update the existing default calendar with the
	 * default from the MSP file.
	 * 
	 * @param isUpdateDefaultCalendar
	 *            true means update the existing default calendar; false means a
	 *            new calendar will be created and it will not be made default.
	 *            If there is no current default calendar, the MSP default will
	 *            be added AND made default.
	 */
	public void setUpdateDefaultCalendar(boolean isUpdateDefaultCalendar) {
		this.isUpdateDefaultCalendar = isUpdateDefaultCalendar;
	}

	/**
	 * 
	 * @return a <code>String</code> value containing the absolute filename of
	 *         the file that contains the xml file that we are trying to import.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 
	 * @param fileName
	 *            a <code>String</code> value containing the name of the XML
	 *            file that we are trying to import.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Get the html required to render the options of an html option list that
	 * contains all of the projects that appear in this xml file.
	 * 
	 * @return a <code>String</code> value containing one or more xml files.
	 * @throws ImportException
	 *             if there is a problem interpreting any project values
	 */
	public String getProjectNameHTMLOptionList() throws ImportException {
		StringBuffer result = new StringBuffer();
		result = result.append("<option value=\"").append("1").append("\"");
		result.append(">").append(projectData.getName()).append("</option>").append("\n");
		return result.toString();
	}

	/**
	 * Get the name of the project that we are going to import.
	 * 
	 * @return a <code>String</code> containing the name of the project we are
	 *         going to import.
	 */
	public String getProjectName() {
		return projectData.getName();
	}

	/**
	 * This method sets the uid of the project that we are going to load from
	 * the XML file.
	 * 
	 * @param projectID
	 *            a <code>int</code> which represents a valid project uid
	 *            found in the imported xml file.
	 */
	public void setMSProjectID(int projectID) {
		this.msProjectID = projectID;
	}

	/**
	 * Get the number of tasks that appear in this MS Project file.
	 * 
	 * @return a <code>int</code> value containing the number of tasks that
	 *         appear in the current xml file.
	 */
	public int getTaskCount() {
		return taskMap.size();
	}

	public Collection getResources() {
		return resources.values();
	}

	/**
	 * Returns the number of resources to be imported as determined by the
	 * resource map.
	 * 
	 * @return the number of resources to be imported
	 */
	public int getResourceCount() {
		return getResources().size();
	}

	/**
	 * Set the schedule that the schedule entries will be imported into.
	 * 
	 * @param schedule
	 *            the <code>Schedule</code> that the schedule entries will be
	 *            imported into.
	 */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * Sets the current roster used for locating person names and time zones.
	 * 
	 * @param roster
	 *            the current roster
	 */
	public void setRoster(Roster roster) {
		this.roster = roster;
	}

	/**
	 * Returns the count of working time calendars to be imported. Assumes
	 * {@link #prepareImport} has been called.
	 * 
	 * @return the number of working time calendars to be imported; this is the
	 *         sum of the based and resource calendars
	 */
	public int getWorkingTimeCalendarImportCount() {
		return this.baseCalendars.size() + this.resourceCalendars.size();
	}

	/**
	 * Load all of the information from the MS Project xml into java objects
	 * 
	 * @throws PersistenceException
	 *             under no conditions. This has to exist due to a deficiency in
	 *             the task.getConstraint() api.
	 */
	public void load() throws PersistenceException {

		loadProjectInfo();
		loadResources();
		loadTasks();
		loadDependencies();
		loadAssignments();
		loadCalendars();
		updateUnallocatedWork();
	}

	private void loadDependencies() {

		List<Task> taskList = projectData.getTasks().getTask();
		Iterator<Task> it = taskList.iterator();

		List thisObjectsDependencies = new ArrayList();
		int currentID = -1;
		while (it.hasNext()) {
			Task task = it.next();

			if (task.getPredecessorLink() == null || task.getPredecessorLink().size() == 0) {
				continue;
			}

			for (int i = 0; i < task.getPredecessorLink().size(); i++) {
				PredecessorLink xmlLink = task.getPredecessorLink().get(i);
				// Create an object which will hold some temporary information
				// about
				// ms project task id's until we have imported those objects
				// into the
				// project.net database. (At that point, they'll have *REAL*
				// task id's.
				MSPLink link = new MSPLink();
				ScheduleEntry predecessor = (ScheduleEntry) taskMap.get(new Integer(xmlLink.getPredecessorUID().intValue()));

				link.setPredecessorID(new Integer(xmlLink.getPredecessorUID().intValue()));
				link.setSuccessorID(new Integer(task.getUID().intValue()));

				TaskDependency td = new TaskDependency();
				link.setTaskDependency(td);

				td.setDependencyType(TaskDependencyType.getForMSPID(xmlLink.getType().intValue()));

				int lagAmount = xmlLink.getLinkLag().intValue();
				int lagFormat = xmlLink.getLagFormat().intValue();

				// 19 = percentage value
				if (lagFormat == 19) {
					// Actual time quantity is based on the predecessor duration

					if (predecessor == null) {
						throw new IllegalStateException("Unable to find predecessor with ID " + link.getPredecessorID()
								+ " for calculating lag from a percentage");
					}

					// Multiply the duration by the percentage expressed as a
					// decimal
					// This returns a lag time quantity expressed in the same
					// unit as the duration
					TimeQuantity lagTimeQuantity = predecessor.getDurationTQ().multiply(new BigDecimal(new BigInteger(String.valueOf(lagAmount)), 2));

					td.setLag(lagTimeQuantity);

				} else {
					td.setLag(new MSPTimeQuantity(lagAmount, lagFormat).getTimeQuantity());
				}

				// If we are on a new id, create a new list to hold the
				// dependencies
				// for the current task
				if (currentID != link.getSuccessorID()) {
					currentID = link.getSuccessorID();
					thisObjectsDependencies = new LinkedList();
					dependencies.put(new Integer(currentID), thisObjectsDependencies);
				}

				thisObjectsDependencies.add(link);
			}// for all dependencies
		}// while all tasks

	}

	private void updateUnallocatedWork() {
		for (Iterator it = taskMap.values().iterator(); it.hasNext();) {
			ScheduleEntry entry = (ScheduleEntry) it.next();
			if (taskAssignmentMap.containsKey(entry.getID())) {
				// Because our source is MS Project, assume that there is no
				// unallocated
				// work complete if any is allocated
				entry.setUnallocatedWorkComplete(TimeQuantity.O_HOURS);
			}

		}
	}

	private void loadProjectInfo() {
		mspStartDate = projectData.getStartDate().toGregorianCalendar(userTimeZone, userLocale, projectData.getStartDate() ).getTime();
		mspEndDate = projectData.getFinishDate().toGregorianCalendar(userTimeZone, userLocale, projectData.getFinishDate()).getTime();
	}

	private void loadTasks() throws PersistenceException {

		List<Task> taskList = projectData.getTasks().getTask();
		Iterator tskItr = taskList.iterator();

		int[] outlineParents = new int[MAXIMUM_OUTLINE_DEPTH];
		int currentOutlineLevel, previousOutlineLevel = 1;

		while (tskItr.hasNext()) {
			Task task = (Task) tskItr.next();

			if (!task.isIsNull()) {
				int uid = task.getUID().intValue();

				Boolean splitsInProgressTasks = projectData.isSplitsInProgressTasks();
                // Check to see if this is a split task; and keep track that
				// this project contains split tasks.
				if (splitsInProgressTasks != null && splitsInProgressTasks.booleanValue())
					setHasSplitTasks(true);

				// It is possible to create "blank" tasks in MS Project in order
				// to add
				// whitespace. This functionality isn'se supported in
				// Project.Net, so
				// we skip those tasks.
				String taskName = task.getName();
				if (Validator.isBlankOrNull(taskName) || uid == 0) {
					continue;
				}

				TaskType type;
				if (task.isSummary()) {
					type = TaskType.SUMMARY;
				} else {
					type = TaskType.TASK;
				}

				ScheduleEntry se = ScheduleEntryFactory.createFromType(type);
				se.setMilestone(task.isMilestone());
				if (!isNull(task.getDuration())) {
					long duration = task.getDuration().getTimeInMillis(task.getStart().toGregorianCalendar(userTimeZone, userLocale, task.getStart()).getTime()) / 100 / 60;
                    se.setDuration(new MSPTimeQuantity(new Long(duration).intValue(), task.getDurationFormat().intValue()).getTimeQuantity());
				}
				if (!isNull(task.getStart()))
					se.setStartTimeD(task.getStart().toGregorianCalendar(userTimeZone, userLocale, task.getStart()).getTime());
				if (!isNull(task.getFinish()))
					se.setEndTimeD(task.getFinish().toGregorianCalendar(userTimeZone, userLocale, task.getFinish()).getTime());

				if (!isNull(task.getActualStart()))
					se.setActualStartTimeD(task.getActualStart().toGregorianCalendar(userTimeZone, userLocale, task.getActualStart()).getTime());
				if (!isNull(task.getActualFinish()))
					se.setActualEndTimeD(task.getActualFinish().toGregorianCalendar(userTimeZone, userLocale, task.getActualFinish()).getTime());

				BigDecimal percentComplete = new BigDecimal(task.getPercentComplete());
					percentComplete = percentComplete.divide(new BigDecimal(100), 3, BigDecimal.ROUND_HALF_DOWN);
					se.setPercentComplete(percentComplete);

				if (taskName.length() > 255) {
					ErrorDescription ed = new ErrorDescription(PropertyProvider.get("prm.schedule.import.xml.tasknametoolong.warning", taskName));
					errorReporter.addError(ed);
					taskName = taskName.substring(0, 255);
				}
				se.setName(taskName);
				if (!isNull(task.getPriority()))
					se.setPriority(TaskPriority.getForMSPID(task.getPriority().intValue()).getID());

				if (!isNull(task.getWork()))
					se.setWork(constructWork(task.getWork().getTimeInMillis(task.getStart().toGregorianCalendar(userTimeZone, userLocale, task.getStart()).getTime())));
				if (!isNull(task.getActualWork()))
					se.setWorkComplete(constructWork(task.getActualWork().getTimeInMillis(task.getStart().toGregorianCalendar(userTimeZone, userLocale, task.getStart()).getTime())));

				// We'll update these values later if there are assignments
				se.setUnallocatedWorkComplete(se.getWorkCompleteTQ());

				if (!(se.getWorkTQ().isZero() && isNull(task.getPercentWorkComplete()))) {
					BigDecimal workPercentComplete = new BigDecimal(task.getPercentWorkComplete());
					workPercentComplete = workPercentComplete.divide(new BigDecimal(100), 3, BigDecimal.ROUND_HALF_DOWN);
					se.setWorkPercentComplete(workPercentComplete);
					// se.setWorkPercentComplete(new
					// BigDecimal(task.getPercentWorkComplete().intValue() /
					// 100));
				}

				if (!isNull(task.getDeadline()))
					se.setDeadline(task.getDeadline().toGregorianCalendar(userTimeZone, userLocale, task.getDeadline()).getTime());

				String taskDescription = task.getNotes();
				if (isNull(taskDescription))
					taskDescription = "";
				if (taskDescription.length() > 500) {
					ErrorDescription ed = new ErrorDescription(PropertyProvider.get("prm.schedule.import.xml.taskdescriptiontoolong.warning", taskName));
					errorReporter.addError(ed);
					taskDescription = taskDescription.substring(0, 500);
				}
				se.setDescription(taskDescription);
				se.setPlanID(schedule.getID());
				se.setCriticalPath(task.isCritical());
				if (!isNull(task.getConstraintType()))
					se.setConstraintType(TaskConstraintType.getForMSPID(task.getConstraintType().intValue()));
				if (!isNull(task.getConstraintDate()))
					se.setConstraintDate(task.getConstraintDate().toGregorianCalendar(userTimeZone, userLocale, task.getConstraintDate()).getTime());

				// EST, EFT, LST, LFT
				if (!isNull(task.getEarlyStart()))
					se.setEarliestStartDate(task.getEarlyStart().toGregorianCalendar(userTimeZone, userLocale, task.getEarlyStart()).getTime());
				if (!isNull(task.getEarlyFinish()))
					se.setEarliestFinishDate(task.getEarlyFinish().toGregorianCalendar(userTimeZone, userLocale, task.getEarlyFinish()).getTime());
				if (!isNull(task.getLateStart()))
					se.setLatestStartDate(task.getLateStart().toGregorianCalendar(userTimeZone, userLocale, task.getLateStart()).getTime());
				if (!isNull(task.getLateFinish()))
					se.setLatestFinishDate(task.getLateFinish().toGregorianCalendar(userTimeZone, userLocale, task.getLateFinish()).getTime());

				boolean isEffortDriven = task.isEffortDriven();
				int taskType = task.getType().intValue();
				if (!MSPTaskCalculationType.MSP_FIXED_LOOKUP.containsKey(new Integer(taskType))) {
					throw new PersistenceException("Unknown MS Project task type with value " + taskType);
				}
				se.setTaskCalculationType(MSPTaskCalculationType.makeTaskCalculationType(taskType, isEffortDriven));
                
                //wbs and wbslevel
                se.setWBS(task.getWBS());
                se.setWBSLevel(task.getWBSLevel());

				// See if we are at a new outline depth. If so, we need to store
				// the parent id.
				currentOutlineLevel = task.getOutlineLevel().intValue();
				outlineParents[currentOutlineLevel] = uid;
				if (currentOutlineLevel > previousOutlineLevel) {
					// We are at a new depth level, set the parent in the map
					parents.put(new Integer(uid), new Integer(outlineParents[currentOutlineLevel - 1]));
				}

				Integer uidInteger = new Integer(uid);
				taskMap.put(uidInteger, se);
				orderedTaskList.add(uidInteger);
			}
		}
	}

	/**
	 * parameter hasSplitTasks indicates whether project has one or more split
	 * tasks.
	 */
	private boolean hasSplitTasks = false;

	/**
	 * The imported project contains split tasks.
	 * 
	 * @return true if the file contains at least one split task.
	 */
	public boolean hasSplitTasks() {
		return this.hasSplitTasks;
	}

	/**
	 * The imported project contains split tasks.
	 * 
	 * @param hasSplitTasks
	 */
	public void setHasSplitTasks(boolean hasSplitTasks) {
		this.hasSplitTasks = hasSplitTasks;
	}

	private void loadAssignments() {
		List<Assignment> assignmentList = projectData.getAssignments().getAssignment();
		Iterator assignmentItr = assignmentList.iterator();
		while (assignmentItr.hasNext()) {
			Assignment assignment = (Assignment) assignmentItr.next();
			taskAssignmentMap.put(new Integer(assignment.getTaskUID().intValue()), assignment);
		}
	}

	public void loadResources() {
		List<Resource> resList = projectData.getResources().getResource();
		Iterator itr = resList.iterator();
		while (itr.hasNext()) {
			Resource resource = (Resource) itr.next();
			resources.put(new Integer(resource.getUID().intValue()), resource);
		}
	}

	/**
	 * Loads calendars and data from XML file.
	 * <p>
	 * Updates <code>allCallendars</code>.
	 * </p>
	 */
	private void loadCalendars() {
		List<Calendar> calList = projectData.getCalendars().getCalendar();
		Iterator calItr = calList.iterator();
		while (calItr.hasNext()) {
			Calendar calendar = (Calendar) calItr.next();
			allCallendars.add(calendar);
		}
	}

	/**
	 * Returns the ResourceResolver.
	 * 
	 * @return the resource resoulver
	 */
	public ResourceResolver getResourceResolver() {
		if (this.resourceResolver == null) {
			createResourceResolver();
		}
		return this.resourceResolver;
	}
    
    /**
     * 
     * @return map whose key is the resource id and value is the assignor id
     */
    public Map getAssignorMapper() {
        return this.assignorMap;
    }

	public void createResourceResolver() {
		this.resourceResolver = new ResourceResolver(this.resources, this.allCallendars, this.roster);
	}

	/**
	 * Prepares to import by analyzing loaded MSP values and determing what is
	 * to be imported based on user-specified flags and resource maps.
	 * 
	 * @throws ImportException
	 *             if there is an unexpected problem preparing to import
	 */
	public void prepareImport() throws ImportException {

		// Use a set so that we only have one of each calendar
		Set<Project.Calendars.Calendar> baseCalendarsToImport = new HashSet<Project.Calendars.Calendar>();
		Set<Project.Calendars.Calendar> resourceCalendarsToImport = new HashSet<Project.Calendars.Calendar>();

		if (isImportWorkingTimeCalendars()) {
			// Add default calendar to calendars to import
			baseCalendarsToImport.add(this.allCallendars.getDefaultCalendar(projectData));

			if (isImportResourceWorkingTimeCalendars()) {

				// Add the calendars which belong to mapped resources
				for (Iterator<Entry<Integer, Resource>> it = this.resources.entrySet().iterator(); it.hasNext();) {
					Resource nextResource = it.next().getValue();

					if (nextResource.getCalendarUID() != null) {
						// We found a person mapped to this resource (and no
						// other resource)
						resourceCalendarsToImport.add(this.allCallendars.get(nextResource.getCalendarUID().intValue()));

						Project.Calendars.Calendar baseCalendar = null;
						// We also want its base calendar
						if (!isNull(this.allCallendars.get(nextResource.getCalendarUID().intValue())) && !isNull(this.allCallendars.get(nextResource.getCalendarUID().intValue()).getBaseCalendarUID()))
							baseCalendar = this.allCallendars.get(this.allCallendars.get(nextResource.getCalendarUID().intValue()).getBaseCalendarUID().intValue());

						if (baseCalendar == null) {
							// throw new ImportException("Calendar with UID " +
							// this.allCallendars.get(nextResource.getCalendarUID().intValue())
							// + " refers to a base calendar that does not
							// exist: " +
							// this.allCallendars.get(nextResource.getCalendarUID().intValue()).getBaseCalendarUID().intValue());
							// Found that xml could refer to the base calender
							// as -1, which is not a valid calender
							// Just Ignoring ...
						} else {
							baseCalendarsToImport.add(baseCalendar);
						}
					}

				}
			}

		}

		// We have base and resource calendars to import
		this.baseCalendars = baseCalendarsToImport;
		this.resourceCalendars = resourceCalendarsToImport;
	}

	/**
	 * This method is called to import all the schedule entries into the
	 * schedule after the preliminary setup methods have completed.
	 * 
	 * @throws PersistenceException
	 *             if there is an unhandled problem storing any part of the
	 *             schedule
	 */
	public void importSchedule() throws PersistenceException {

		int defaultTaskType = projectData.getDefaultTaskType().intValue();
        boolean booleanValue = projectData.isNewTasksEffortDriven() == null ? false : projectData.isNewTasksEffortDriven().booleanValue();
        // Populate MS Project values in the schedule
		TaskCalculationType taskCalculationType = MSPTaskCalculationType.makeTaskCalculationType(defaultTaskType, booleanValue);
		this.schedule.setDefaultTaskCalculationType(taskCalculationType);

		if (isImportTasks()) {

			// First, import the tasks. We need to make sure we do this is
			// "hierarchical" order.
			// Otherwise the parents might not exist when the children try to
			// add them.

			Set taskIDSet = initialTaskStore();

			// Note: After intitialTaskStore() all other stores should handle
			// errors
			// using the "errorReporter".
			// By this stage, the tasks are stored; further errors won't roll
			// back
			// the tasks. Using the "errorReporter" to handle errors lets the
			// user know that only part of the import failed

			// Now, fix anything that was missing because we needed real task
			// id's,
			// for example parent mapping and task dependencies.
			for (Iterator it = taskIDSet.iterator(); it.hasNext();) {
				//
				// Task Dependencies
				//
				Integer id = (Integer) it.next();
				ScheduleEntry t = (ScheduleEntry) taskMap.get(id);
				List tdl = (List) dependencies.get(id);

				// First, check to make sure that this task had a dependency
				// list
				DBBean db = new DBBean();
				try {
					if (tdl != null) {
						PredecessorList dependencyList = new PredecessorList();
						dependencyList.setTaskID(t.getID());

						// Iterate through each of the dependencies and update
						// them so
						// that they have proper task id's and dependency id's.
						for (Iterator it2 = tdl.iterator(); it2.hasNext();) {
							MSPLink link = (MSPLink) it2.next();
							TaskDependency td = link.getTaskDependency();
							td.setTaskID(t.getID());
							String dependencyID = (String) taskIDMap.get(new Integer(link.getPredecessorID()));
							if (dependencyID == null) {
								continue;
							}
							td.setDependencyID(dependencyID);
							dependencyList.add(td);
						}

						// If there are any dependencies, add them to the task
						if (dependencyList.size() > 0) {
							t.setPredecessors(dependencyList);
						}

						dependencyList.insert(db);
					}
				} catch (SQLException sqle) {
					Logger.getLogger(XMLImporter.class).debug("Unable to store dependencies: ", sqle);
					ErrorDescription ed = new ErrorDescription("Unable to store dependencies for task \"" + t.getName() + "\"");
					errorReporter.addError(ed);

				} finally {
					db.release();
				}
			}
			if (isImportAssignments()) {
//				importResourceRates();
				importAssignments();
			}
		}

		if (isImportWorkingTimeCalendars()) {
			importWorkingTimeCalendars();
		}

		if (isImportStartAndEndDates()) {
			importStartAndEndDates();
		}
        
        //sjmittal: store this in the end
        this.schedule.store();

	}

    //sjmittal: commenting the code untill functionality in place 
//	/**
//	 * This method uses hibernate to persist data for Resource Rates to
//	 * PN_PERSON_COSTS table.
//	 * 
//	 */
//	private void importResourceRates() {
//		Session hbSession = DBTransactionManager.getSession();
//
//		Transaction tx = hbSession.beginTransaction();
//		try {
//			Collection<Resource> collection = null;
//			if (resources != null) {
//				collection = resources.values();
//				Iterator<Resource> iter = collection.iterator();
//
//				while (iter.hasNext()) {
//					Resource resource = iter.next();
//					List<Rate> rates = null;
//					if (resource != null && resource.getRates() != null) {
//						rates = resource.getRates().getRate();
//						if (rates != null && rates.size() > 0) {
//							PnPersonCostsDAO costsDAO = new PnPersonCostsDAO();
//							List<ResourceRate> resRates = getResourceRates(rates);
//							Integer personId = resourceResolver.getPersonIDRepresentingResourceUID(new Integer(resource.getUID().toString()));
//							if (personId != null)
//								costsDAO.savePersonCosts(new BigInteger(personId.toString()), Integer.parseInt(schedule.getSpaceId()), resRates, hbSession);
//						}
//					}
//				}
//				tx.commit();
//			}
//			hbSession.flush();
//			hbSession.close();
//		} catch (HibernateException e) {
//			Logger.getLogger(XMLImporter.class).debug("Unable to save rate for resources.", e);
//			if (tx != null && tx.isActive())
//				tx.rollback();
//			ErrorDescription ed = new ErrorDescription("Unable to store one or more Rate cards for resources.");
//			errorReporter.addError(ed);
//		}
//	}
//
//	/**
//	 * Get a list of ResourceRate Beans
//	 * 
//	 * @param rates
//	 * @return
//	 */
//	private List<ResourceRate> getResourceRates(List<Rate> rates) {
//		List<ResourceRate> resRates = new ArrayList<ResourceRate>();
//		Iterator iter = rates.iterator();
//		ResourceRate resRate = null;
//		while (iter.hasNext()) {
//			Rate rate = (Rate) iter.next();
//			if (!containsRateCard(resRates, rate.getRateTable().intValue())) {
//				resRate = new ResourceRate(rate.getCostPerUse(), rate.getOvertimeRate(), rate.getOvertimeRateFormat(),rate.getRatesFrom(), rate.getRatesTo(), rate.getRateTable(), rate.getStandardRate(), rate.getStandardRateFormat());
//				resRates.add(resRate);
//			}
//		}
//		return resRates;
//	}
//
//	/**
//	 * This will check if there are multiple rate cards for a resource which
//	 * refer to the same rate table.
//	 * 
//	 * @return
//	 */
//	private boolean containsRateCard(List<ResourceRate> resRates, int currentRateTable) {
//		Iterator<ResourceRate> iter = resRates.iterator();
//		while (iter.hasNext()) {
//			ResourceRate resRate = iter.next();
//			if (resRate.getRateTable().intValue() == currentRateTable) {
//				return true;
//			}
//		}
//		return false;
//	}

	/**
	 * Imports assignments by storing each assignment based on the mapping of
	 * MSP Assignment Resource ID to our person ID. <p/> Handles MSP Assignments
	 * who are not mapped, or are mapped to more than one resource.
	 */
	private void importAssignments() {

		if (resourceResolver.hasResourceMapping()) {

			ImportAssignmentsHelper helper = new ImportAssignmentsHelper(resourceResolver, assignorMap, currentSpace, roster, schedule.getWorkingTimeCalendarProvider(), errorReporter);

			// Iterate over each MSP Task, creating assignments based on the
			// mapping of MSP Assignment
			// to the person ID
			for (Iterator iterator = this.taskIDMap.keySet().iterator(); iterator.hasNext();) {
				Integer mspTaskID = (Integer) iterator.next();

				// Grab all the assignments for this task
				Collection mspAssignments = (Collection) taskAssignmentMap.get(mspTaskID);

				// If there are no resource mappings for any of the msp
				// assignments
				// We simply continue to the next task
				if (mspAssignments != null && resourceResolver.hasResourceMapping(mspAssignments)) {
					ScheduleEntry entry = (ScheduleEntry) taskMap.get(mspTaskID);

					// Now import the assignments; this _will_ store the
					// assignments
					helper.importAssignmentsForTask(entry, mspAssignments);
				}

			}

		}

	}

	/**
	 * Imports the project start and end date from MS Project into the
	 * project.net schedule.
	 */
	private void importStartAndEndDates() throws PersistenceException {
//		boolean store = false;

		if (mspStartDate != null) {
			schedule.setScheduleStartDate(mspStartDate);
//			store = true;
		}
		if (mspEndDate != null) {
			schedule.setScheduleEndDate(mspEndDate);
//			store = true;
		}

//		if (store) {
//			schedule.store();
//		}

		if (isImportStartAndFinishDates()) {
			schedule.setScheduleStartDate(projectData.getStartDate().toGregorianCalendar(userTimeZone, userLocale, projectData.getStartDate()).getTime());
			schedule.setScheduleEndDate(projectData.getFinishDate().toGregorianCalendar(userTimeZone, userLocale, projectData.getFinishDate()).getTime());
//			schedule.store();
		}

	}

	/**
	 * Imports the working time calendars.
	 * <p>
	 * The goal of the import is:
	 * <ul>
	 * <li>The imported calendars should follow the structure (parent-child
	 * relationships) of the MSP calendars.
	 * <li>An opportunity is provided to replace the current schedule default
	 * with the MSP default such that any existing resources that inherit from
	 * the current schedule default will therefore be inheriting from the MSP
	 * default
	 * </ul>
	 * </p>
	 * <p>
	 * <ul>
	 * <li>Imports the default MSP calendar.
	 * <ul>
	 * <li>If the selection was made to update the schedule default calendar
	 * then the current schedule default calendar is updated with matching
	 * working time entries
	 * <li>If the selection was made to add the MSP default calendar then it is
	 * added to the schedule; the default schedule calendar (if any) is not
	 * modified
	 * <li>If there was no current schedule default then the MSP default
	 * calendar is made default
	 * </ul>
	 * <li>Imports all other base calendars by adding them to the schedule.
	 * <li>Imports calendars for all mapped resources; a resource can only have
	 * one calendar so any existing resource calendars are updated
	 * <ul>
	 * <li>The resource's calendar is updated to inherit from corresponding
	 * imported base calendar; this action may change a resource's parent
	 * calendar
	 * </ul>
	 * </ul>
	 * </p>
	 */
	private void importWorkingTimeCalendars() {
		Map<Integer, String> calendarIDMap = null;
		WorkingTimeCalendarDefinition newCalendarDef = null;
		try {
			IWorkingTimeCalendarProvider provider = this.schedule.getWorkingTimeCalendarProvider();

			// Determine the schedule default calendar definition, if any
			WorkingTimeCalendarDefinition defaultCalendarDef = null;
			if (this.schedule.getDefaultCalendarID() != null) {
				defaultCalendarDef = provider.get(this.schedule.getDefaultCalendarID());
			}

			final boolean hasScheduleDefault = (defaultCalendarDef != null);
			Project.Calendars.Calendar defaultCalendar = this.allCallendars.getDefaultCalendar(projectData);
			if (null != defaultCalendar) {
				if (isUpdateDefaultCalendar && hasScheduleDefault) {
					// We're updating and schedule has a default
					// We update that default calendar
					MSPCalendars.updateCalendarDefinitionEntries(defaultCalendar, defaultCalendarDef);
					provider.store(defaultCalendarDef.getID());

				} else {
					// We're adding or there is no schedule default

					// First add the new calendar
					defaultCalendarDef = MSPCalendars.makeBaseCalendarDefinition(defaultCalendar);
					provider.create(defaultCalendarDef);

					// If there isn't a current default then we'll make the
					// newly
					// added calendar the default
					if (!hasScheduleDefault) {
						provider.changeDefaultCalendar(defaultCalendarDef.getID());
					}
				}

				// Remove the default calendar from base calendars so that we
				// don't try and store it more than once
				this.baseCalendars.remove(defaultCalendar);

				// Create a map of base calendar UIDs to stored calendar IDs
				// We need this when storing non-base (resource) calendars
				calendarIDMap = new HashMap<Integer, String>();

				// Initially populate it with the default calendar
				calendarIDMap.put(new Integer(defaultCalendar.getUID().intValue()), defaultCalendarDef.getID());
			}
			// Now store remaining base calendars
			// We have to do these first since we need the IDs to store
			// the resource calendars
			if (null != baseCalendars) {
				for (Iterator<Project.Calendars.Calendar> it = baseCalendars.iterator(); it.hasNext();) {
					Project.Calendars.Calendar nextCalendar = it.next();
					if (null != nextCalendar) {
						newCalendarDef = MSPCalendars.makeBaseCalendarDefinition(nextCalendar);
						provider.create(newCalendarDef);

						// Store the MSP ID to our ID for storing the resource
						// calendars
						calendarIDMap.put(new Integer(nextCalendar.getUID().intValue()), newCalendarDef.getID());
					}
				}
			}

			// Store resource calendar, looking up parent calendar IDs from
			// base calendar IDs
			if (null != resourceCalendars) {
				for (Iterator<Project.Calendars.Calendar> it = resourceCalendars.iterator(); it.hasNext();) {
					Project.Calendars.Calendar nextCalendar = it.next();

					Integer personID = null;
					BigInteger resourceId = null;
					resourceId = getResourceIdForCalendar(nextCalendar);
					if (null != resourceId)
						personID = resourceResolver.getPersonIDRepresentingResourceUID(new Integer(resourceId.intValue())); // .toString();
					String parentCalendarID = null;
					if (null != nextCalendar && null != nextCalendar.getBaseCalendarUID())
						parentCalendarID = (String) calendarIDMap.get(new Integer(nextCalendar.getBaseCalendarUID()
								.intValue()));

					WorkingTimeCalendarDefinition currentResourceCalendarDef = null;
					if (null != parentCalendarID && null != personID && !"null".equals(parentCalendarID)) {
						currentResourceCalendarDef = provider.getForResourceID(personID.toString());
						if (currentResourceCalendarDef != null) {
							// Person has a calendar definition
							// We'll update its entries and change the parent
							// definition to the corresponding imported calendar
							MSPCalendars.updateCalendarDefinitionEntries(nextCalendar, currentResourceCalendarDef);
							currentResourceCalendarDef.updateParentCalendar(provider.get(parentCalendarID));
							provider.store(currentResourceCalendarDef.getID());

						} else {
							// Person has no calendar definition
							newCalendarDef = MSPCalendars.makeResourceCalendarDefinition(nextCalendar, parentCalendarID, personID.toString());
							provider.create(newCalendarDef);

						}
					}
				}
			}

		} catch (PersistenceException e) {
			Logger.getLogger(XMLImporter.class).debug("Unable to store a working time calendar.", e);
			ErrorDescription ed = new ErrorDescription("Unable to store a working time calendar");
			errorReporter.addError(ed);
		}
	}

	/**
	 * Stores all the tasks. Assumes
	 * 
	 * @return
	 * @throws PersistenceException
	 */
	private Set initialTaskStore() throws PersistenceException {
		LinkedHashSet taskIDSet = orderedTaskList;
		LinkedHashSet needToStore = (LinkedHashSet) orderedTaskList.clone();
		LinkedHashSet readyToStore = new LinkedHashSet();
		DBBean db = new DBBean();

		try {
			db.openConnection();
			Connection connection = db.getConnection();
			if (connection instanceof OracleConnection) {
				((OracleConnection) connection).setDefaultExecuteBatch(5);
			}

			while (!needToStore.isEmpty()) {
				// Anything that is in the ready to store list has already been
				// stored, clear it out so we can start again.
				readyToStore.clear();

				// Go through the list of tasks that need to be stored and see
				// which
				// are ready to be stored.
				for (Iterator it = needToStore.iterator(); it.hasNext();) {
					Integer id = (Integer) it.next();

					// If this task doesn't have a parent, or if its parent has
					// already been stored, it is ready to be stored.
					if (!parents.containsKey(id) || (!needToStore.contains(parents.get(id)) && !readyToStore.contains(parents.get(id)))) {
						readyToStore.add(id);
						it.remove();
					}
				}

				// Store the tasks that we identified as being "ready"
				for (Iterator it = readyToStore.iterator(); it.hasNext();) {
					Integer id = (Integer) it.next();
					ScheduleEntry se = (ScheduleEntry) taskMap.get(id);

					Integer parentUID;
					if ((parentUID = (Integer) parents.get(id)) != null) {
						// The task we're about to store has a parent
						// Grab the ScheduleEntry for the parent UID
						// and update the parent task ID to match its ID
						se.setParentTaskID(((ScheduleEntry) taskMap.get(parentUID)).getID());
					}

					storeNewTask(id, se, db);
				}
			}
			if (connection instanceof OracleConnection) {
				((OracleConnection) connection).setDefaultExecuteBatch(5);
			}

		} catch (SQLException sqle) {
			throw new PersistenceException("Error storing tasks during import: " + sqle, sqle);

		} finally {
			db.release();
		}

		return taskIDSet;
	}

	/**
	 * Stores the specified schedule entry and updates <code>taskIDMap</code>
	 * to map the MS project UID to the schedule entry; this allows future
	 * descendant tasks to locate their stored parent ScheduleEntry.
	 * 
	 * @param msProjectID
	 *            the MSP UID of the schedule entry being stored
	 * @param se
	 *            the schedule entry to store
	 * @param db
	 *            the DBBean in which to perform the transaction
	 */
	private void storeNewTask(Integer msProjectID, ScheduleEntry se, DBBean db) {
		se.setSendNotifications(false);
		se.setIgnoreTimePortionOfDate(false);

		try {
//			se.setIgnoreTimePortionOfDate(false); //sjmittal: why repeat!!
			se.store(false, schedule, db);
			taskIDMap.put(msProjectID, se.getID());
		} catch (SQLException e) {
			Logger.getLogger(XMLImporter.class).debug("Unable to store new task.", e);
			ErrorDescription ed = new ErrorDescription(PropertyProvider.get("prm.schedule.import.xml.unabletoimporttask.message", se.getName()));
			errorReporter.addError(ed);
		} catch (PersistenceException e) {
			Logger.getLogger(XMLImporter.class).debug("Unable to store new task", e);
			ErrorDescription ed = new ErrorDescription(PropertyProvider.get("prm.schedule.import.xml.unabletoimporttask.message", se.getName()));
			errorReporter.addError(ed);
		}
	}

	/**
	 * Return a map of MS Task ID to tasks. This is mostly useful for unit
	 * testing.
	 * 
	 * @return a <code>Map</code> of MSP Task id's to Project.net
	 *         ScheduleEntry objects.
	 */
	public Map getTaskMap() {
		return this.taskMap;
	}

	private BigInteger getResourceIdForCalendar(Project.Calendars.Calendar nextCalendar) {
		for (Iterator<Entry<Integer, Resource>> it = this.resources.entrySet().iterator(); it.hasNext();) {
			Resource nextResource = it.next().getValue();
			// nextCalender could be null !!
			// IT IS POSSIBLE that the resource in ms xml refers to a calendar
			// UID which is
			// NOT present in exported xml file in ms xml 2007,
			// HOPE this gets resolved in next ms proj release of 2007
			if (null != nextResource && null != nextCalendar && nextResource.getCalendarUID() != null && nextCalendar.getUID().intValue() == nextResource.getCalendarUID().intValue()) {
				return nextResource.getID();
			}

		}
		return null;
	}

	public boolean isNull(Object objToCheck) {
		return (null == objToCheck) ? true : false;
	}
}
