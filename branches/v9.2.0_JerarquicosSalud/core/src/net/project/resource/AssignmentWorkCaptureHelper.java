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
/**
 * 
 */
package net.project.resource;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.project.base.ObjectType;
import net.project.base.PnetRuntimeException;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.StringDomainFilter;
import net.project.base.finder.TextComparator;
import net.project.base.finder.TextFilter;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.form.assignment.FormAssignment;
import net.project.hibernate.model.PnAssignmentWork;
import net.project.hibernate.service.IPnAssignmentWorkService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.resource.mvc.handler.AssignmentDate;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryDateCalculator;
import net.project.schedule.ScheduleEntryHistory;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.schedule.TaskFinder;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.CollectionUtils;
import net.project.util.DateFormat;
import net.project.util.DateRange;
import net.project.util.DateUtils;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.InvalidDateException;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 */
public class AssignmentWorkCaptureHelper {
	
	 	private static Logger log = Logger.getLogger(AssignmentWorkCaptureHelper.class);

	    public static final TimeQuantity MAXIMUM_WORK_HOURS = new TimeQuantity(new BigDecimal("24"), TimeQuantityUnit.HOUR);

	    protected ErrorReporter errors;

	    private ResourceWorkingTimeCalendarProvider calendarProvider;

	    protected ErrorReporter errorReporter;

	    public Map planIDMap;

	    private Date startDate;

	    private Date endDate;

	    public List dateHeaders;

	    public List dateLongNames;

	    private List assignments;

	    public Map dateValues;

	    public Map summaryDateValues;

	    public Map assignmentMap;

	    private Boolean showFilterPane;

	    private String percentComplete = null;

		private String comments;
		
	    private TimeQuantity assnWork = TimeQuantity.O_HOURS;

	    private TimeQuantity currentWork = assnWork;

	    public Date scrollBackStartDate;

	    public Date scrollForwardStartDate;

		private String objectId;
		
	    private TaskFinder finder = new TaskFinder();
	    
	    private boolean isCaluculateDuration = false;
	    
	    public NumberFormat nf = NumberFormat.getInstance();

	    public static class DateHeader {
	    	
	        public String dayOfWeek;

	        public String date;
	        
	        public Boolean isToday;
	        
	        public Boolean isTodaysDayInWeek;
	        
	        public String dateHeaderLongname;
	        
	        public Boolean isNonWorkingDay; 
	        
			/**
			 * @return the date
			 */
			public String getDate() {
				return date;
			}

			/**
			 * @return the dayOfWeek
			 */
			public String getDayOfWeek() {
				return dayOfWeek;
			}

			/**
			 * @return the isToday
			 */
			public Boolean getIsToday() {
				return isToday;
			}

			/**
			 * @param isToday the isToday to set
			 */
			public void setIsToday(Boolean isToday) {
				this.isToday = isToday;
			}

			/**
			 * @return the isTodaysDayInWeek
			 */
			public Boolean getIsTodaysDayInWeek() {
				return isTodaysDayInWeek;
			}

			/**
			 * @param isTodaysDayInWeek the isTodaysDayInWeek to set
			 */
			public void setIsTodaysDayInWeek(Boolean isTodaysDayInWeek) {
				this.isTodaysDayInWeek = isTodaysDayInWeek;
			}
			
			/**
			 * @return the dateHeaderLongname
			 */
			public String getDateHeaderLongname() {
				return dateHeaderLongname;
			}
			
			/**
			 * @return the isWorkingDay
			 */
			public Boolean getIsNonWorkingDay() {
				return isNonWorkingDay;
			}
			
			/**
			 * @param isNonWorkingDay the isNonWorkingDay to set
			 */
			public void setIsNonWorkingDay(Boolean isNonWorkingDay) {
				this.isNonWorkingDay = isNonWorkingDay;
			}
			
	    }
	    
	    public static class DateLongName {
	    	
	        public String work;

	        public String valueName;
	        
	        public String valueValue;
	        
	        public String name;
	        
	        public int forcount;

	        public Boolean isTodaysDayInWeek;
	        
	        public Boolean isNonWorkingDay; 
	        
	        public String oldWorkForDay; 
			
            public String date;
            
			/**
			 * @return the forcount
			 */
			public int getForcount() {
				return forcount;
			}

			/**
			 * @return the name
			 */
			public String getName() {
				return name;
			}

			/**
			 * @return the valueName
			 */
			public String getValueName() {
				return valueName;
			}

			/**
			 * @return the valueValue
			 */
			public String getValueValue() {
				return valueValue;
			}

			/**
			 * @return the work
			 */
			public String getWork() {
				return work;
			}
			
			/**
			 * @return the isTodaysDayInWeek
			 */
			public Boolean getIsTodaysDayInWeek() {
				return isTodaysDayInWeek;
			}

			/**
			 * @param isTodaysDayInWeek the isTodaysDayInWeek to set
			 */
			public void setIsTodaysDayInWeek(Boolean isTodaysDayInWeek) {
				this.isTodaysDayInWeek = isTodaysDayInWeek;
			}
			
			/**
			 * @return the isWorkingDay
			 */
			public Boolean getIsNonWorkingDay() {
				return isNonWorkingDay;
			}
			
			/**
			 * @param isNonWorkingDay the isNonWorkingDay to set
			 */
			public void setIsNonWorkingDay(Boolean isNonWorkingDay) {
				this.isNonWorkingDay = isNonWorkingDay;
			}
			
			/**
			 * @return the oldWorkForDay
			 */
			public String getOldWorkForDay() {
				return oldWorkForDay;
			}

			/**
			 * @param oldWorkForDay the oldWorkForDay to set
			 */
			public void setOldWorkForDay(String oldWorkForDay) {
				this.oldWorkForDay = oldWorkForDay;
			}
            
            /**
             * @return the date
             */
            public String getDate() {
                return date;
            }
	    }
	    
	    /**
		 * Return the String as array for time values as array in blogit.js for some validations 
		 * while updating the time sheet. 
		 * @param dateToStrart string sent while scrolling the week/days
		 * @param  scrollType string sent while scrolling to identify for week/days
		 * @param objectId to filter data
		 * and null for First time load.
		 */
	    public String getTimeValueForJS(String dateToStart, String scrollType, String objectId) {
	        PnCalendar cal = new PnCalendar();
	        if (scrollType.equalsIgnoreCase("week")) {
	            if (dateToStart != null) {
	                startDate = new Date(Long.parseLong(dateToStart));
	            } else {
	                cal.setTime(new Date());
	                startDate = cal.startOfWeek();
	            }
	            cal.setTime(startDate);
	            cal.add(PnCalendar.DATE, -7);
	            scrollBackStartDate = cal.getTime();

	            cal.setTime(startDate);
	            cal.add(PnCalendar.DATE, 7);
	            scrollForwardStartDate = cal.getTime();
	            endDate = cal.endOfDay();
	        } else {
	            if (dateToStart != null) {
	                startDate = new Date(Long.parseLong(dateToStart));
	            } else {
	                cal.setTime(new Date());
	                startDate = cal.startOfDay();
	            }
	            cal.setTime(startDate);
	            cal.add(PnCalendar.DATE, -1);
	            scrollBackStartDate = cal.getTime();

	            cal.setTime(startDate);
	            cal.add(PnCalendar.DATE, 1);
	            scrollForwardStartDate = cal.getTime();
	            endDate = cal.endOfDay();
	        }

	        // Get the column headers for each day
	        getDateHeaders(scrollType);
	        // Figure out how many hours were worked on each day
	        setDateValues(objectId, scrollType);
	        String timesValueString = "";
	        for (Iterator it = dateLongNames.iterator(); it.hasNext();) {
	            String timeValue = (String) it.next();
	            timesValueString += timeValue + "" + (it.hasNext() ? "," : "");
	        }

	        return timesValueString;
	    }

	    /**
	     * Determine the amount of work that has been done on each date we are
	     * displaying for each object.  Also, find the sum of all work that has
	     * been done on each date.
	     *
	     * catch SQLException if there is a problem loading the information from
	     * the database.
	     */
	    public void setDateValues(String objectId, String scrollType) {
	        dateValues = new HashMap();
	        summaryDateValues = new HashMap();

	        PnCalendar cal = new PnCalendar();
	        DBBean db = new DBBean();
	        try {
	            db.prepareStatement(
	                "select" +
	                "  aw.object_id, aw.work_start, aw.work_end, aw.work, aw.work_units "+
	                "from " +
	                "  pn_assignment_work aw," +
	                "  pn_object o "+
	                "where " +
	                "  aw.person_id = ? " +
	                "  and aw.work_start <= ? " +
	                "  and aw.work_end >= ? " +
	                "  and o.object_id = aw.object_id "
	            );

	            db.pstmt.setString(1, SessionManager.getUser().getID());
	            DatabaseUtils.setTimestamp(db.pstmt, 2, endDate);
	            DatabaseUtils.setTimestamp(db.pstmt, 3, startDate);

	            db.executePrepared();

	            // Iterate through all the assignment work and assign it to the
	            // correct day in our map.
	            while (db.result.next()) {
	                String objectID = db.result.getString(1);
	                Date workStart = DatabaseUtils.getTimestamp(db.result, 2);
	                Date workEnd = DatabaseUtils.getTimestamp(db.result, 3);
	                TimeQuantity work = DatabaseUtils.getTimeQuantity(db.result, 4, 5);

	                Date endOfDay = cal.endOfDay(workStart);
                    // All work is on the same day
                    AssignmentDate dateID = new AssignmentDate(cal.startOfDay(workStart), objectID);
                    addWorkToDay(dateValues, summaryDateValues, dateID, work);
	            }
	        } catch (SQLException e) {
	            log.error("AssignmentWorkCaptureHelper.setDateValues() failed..."+e.getMessage());
	        } finally {
	            db.release();
	        }
	    }
	    
	    /**
	     * Find the sum of all work that has been done 
	     * between startdate and enddate. Store the total amount of work done in HashMap for each day  
	     * @param startDate 
	     * @param endDate 
	     * @param personId 
	     * catch ParseException if there is a problem while parsing the amount of work in TimeQuantity
	     */
	    public Map getTotalWorkHoursForMonth(Date startDate, Date endDate, Integer[] personId, boolean isSubmittal){
	    	PnCalendar cal = null;
    		cal = new PnCalendar(SessionManager.getUser());
	    	
	    	summaryDateValues = new HashMap();
	    	IPnAssignmentWorkService assignmentWorkService = ServiceFactory.getInstance().getPnAssignmentWorkService();
	    	try	{
			    	List<PnAssignmentWork> arrayList = null;	  
		    		if((personId != null) && startDate != null && endDate != null){
		    			arrayList = assignmentWorkService.getTotalWorkByDate(personId, startDate, endDate, null);
		    			if(arrayList != null){
		    				for(PnAssignmentWork assignmentWork: arrayList ){
		    					AssignmentDate dateID = null;
		    					String objectID = assignmentWork.getObjectId().toString();
		    					Date workStart = assignmentWork.getWorkStart();
		    					if(isSubmittal){
		    						String workedBypersonId =  assignmentWork.getPersonId().toString();
		    						dateID = new AssignmentDate(cal.startOfDay(workStart), objectID+"_"+workedBypersonId);
		    					} else {
		    						dateID = new AssignmentDate(cal.startOfDay(workStart),objectID);
		    					}
		    					addWorkToDay(summaryDateValues, dateID, (TimeQuantity.parse(assignmentWork.getWork().toString(), assignmentWork.getWorkUnits().toString())), isSubmittal);
		    				}
		    			}
		    		}
	    	} catch (ParseException e) {
				log.error("Error Occurred while retrieving total work by date..."+e.getMessage());
			}
	    	return summaryDateValues;
	    }
	    
	    /**
		 * Add work done on particular day in the summaryDateValues Map.
		 * @param summaryDateValues
		 * @param dateID
		 * @param work
		 */
	    public void addWorkToDay(Map summaryDateValues, AssignmentDate dateID, TimeQuantity work, boolean isTimeSubmittal) {
	        if(isTimeSubmittal){
		        TimeQuantity existingDayWork = (TimeQuantity) summaryDateValues.get(dateID);
		        if (existingDayWork != null) {
		            work = work.add(existingDayWork);
		        }
		        summaryDateValues.put(dateID, work);
	        } else {
	        	//First, add the work to the summary we are collecting for that day.
		        TimeQuantity existingSummaryWork = (TimeQuantity) summaryDateValues.get(dateID.date);
		        if (existingSummaryWork == null) {
		            this.summaryDateValues.put(dateID.date, work);
		        } else {
		            this.summaryDateValues.put(dateID.date, existingSummaryWork.add(work));
		        }
	        }
	    }
	    
	    /**
		 * Add work done on perticular day in the summaryDateValues Map.
		 * @param dateValues
		 * @param summaryDateValues
		 * @param dateID
		 * @param work
		 */
	    public void addWorkToDay(Map dateValues, Map summaryDateValues, AssignmentDate dateID, TimeQuantity work) {
	        // First, add the work to the summary we are collecting for that day.
	        TimeQuantity existingSummaryWork = (TimeQuantity) summaryDateValues.get(dateID.date);
	        if (existingSummaryWork == null) {
	            this.summaryDateValues.put(dateID.date, work);
	        } else {
	            this.summaryDateValues.put(dateID.date, existingSummaryWork.add(work));
	        }

	        // Second, add the work for this object/day
	        TimeQuantity existingDayWork = (TimeQuantity) dateValues.get(dateID);
	        if (existingDayWork != null) {
	            work = work.add(existingDayWork);
	        }
	        dateValues.put(dateID, work);
	    }

	    /**
		 * Get the column headers for each day we'll display,
		 * according to dateToStart String has been sent to getTimeValueForJS method
		 * and set to dateHeaders and dateLongNames list.
		 */
	    public void getDateHeaders(String scrollType) {
	        PnCalendar cal = new PnCalendar();
	        dateHeaders = new LinkedList();
	        dateLongNames = new ArrayList();

	        // Iterate through 7 days, storing a header for each
	        DateFormat dateFormat = SessionManager.getUser().getDateFormatter();
	        SimpleDateFormat dayOfWeekFormatter = new SimpleDateFormat("EEE");
	        // please set the timezone for fix of bfd 2897
	        dayOfWeekFormatter.setTimeZone(cal.getTimeZone());
	        cal.setTime(startDate);
	        if (scrollType.equalsIgnoreCase("week")) {
	            for (int headerIndex = 0; headerIndex < 7; headerIndex++) {
	                DateHeader dh = new DateHeader();
	                dh.date = dateFormat.formatDate(cal.getTime(), "MMM dd"); //java.text.DateFormat.SHORT
	                dh.dayOfWeek = dateFormat.formatDate(cal.getTime(),"EEE");
	                dh.dateHeaderLongname = String.valueOf(cal.getTime().getTime());
	                dateHeaders.add(dh);
	                dateLongNames.add(String.valueOf(cal.getTime().getTime()));
	                cal.add(Calendar.DATE, 1);
	            }
	        } else {
	            DateHeader dh = new DateHeader();
	            Calendar todaysDate = Calendar.getInstance();
	            
	            if (todaysDate.get(Calendar.DATE) == cal.get(Calendar.DATE)
						&& todaysDate.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
						&& todaysDate.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
					dh.dayOfWeek = "Today";
				} else {
                    dh.dayOfWeek = dateFormat.formatDate(cal.getTime(),"EEE");
                }
            	
                dh.date = dateFormat.formatDate(cal.getTime(), "MMM dd"); //java.text.DateFormat.SHORT
	            dh.dateHeaderLongname = String.valueOf(cal.getTime().getTime());
	            dateHeaders.add(dh);
	            dateLongNames.add(String.valueOf(cal.getTime().getTime()));
	            cal.add(Calendar.DATE, 1);
	        }
	    }

	    /**
	     * Get the assignment objects that match the filter that the user has set
	     * for assignments
	     *
	     * @param ObjectID that we can use
	     * to look up assignment by ObjectID.
	     * Set<code>List</code> of assignments which match the filtering
	     * criteria.
	     * @throws PersistenceException if there is any difficulty loading the
	     * assignments.
	     */	
	    public List getAssignments(String objectID1) throws PersistenceException {
	        String objectID = objectID1;
	        String[] objectIDList = { objectID1 };
	        assignments = null;

	        String personId = SessionManager.getUser().getID();
	        if (!Validator.isBlankOrNull(objectID)) {
	            // this has been click for capture work so we are loading
	            // assignments from pn_assignment table.
				//if no assignment is present is creates a dummy assignment object in memory
				//and the work against that is logged only in the pn_assignment_work table

	            FinderFilterList filters = new FinderFilterList();
	            filters.setSelected(true);
	            AssignmentManager mgr = new AssignmentManager();

	            if (objectIDList.length == 1) {
	                TextFilter objectIDFilter = new TextFilter("objectID", AssignmentFinder.OBJECT_ID_COLUMN, false);
	                objectIDFilter.setSelected(true);
	                objectIDFilter.setComparator((TextComparator) TextComparator.EQUALS);
	                objectIDFilter.setValue(objectID);
	                filters.add(objectIDFilter);
	            } else {
	                StringDomainFilter filter = new StringDomainFilter("objectID", "", AssignmentFinder.OBJECT_ID_COLUMN, (TextComparator) TextComparator.EQUALS);
	                filter.setSelected(true);
	                filter.setSelectedValues(objectIDList);

	                filters.add(filter);
	            }

	            mgr.setPersonID(personId);
	            
	            // include assignments having status of personal asignments types i.e. ASSIGNED, ACCEPTED, IN_PROCESS 
	            mgr.setStatusFilter(AssignmentStatus.personalAssignmentTypes);

	            mgr.addFilters(filters);
	            mgr.loadAssignments();
	            assignments = mgr.getAssignments();
	            showFilterPane = new Boolean(false);
	        } else {
	            // this has been clicked from the timesheet module
	            // we need to get all the assignments and activities for this person
	            // best is to fetch it from pn_assignment_work as all the work
	            // is stored in this table
	            Iterator iter = dateValues.keySet().iterator();
	            // get all the object ids against which work has been logged
	            // in the timesheet period
	            Set<String> objectIdSet = new HashSet<String>();
	            while (iter.hasNext()) {
	                AssignmentDate aDate = (AssignmentDate) iter.next();
	                TimeQuantity work = (TimeQuantity) dateValues.get(aDate);
	                objectIdSet.add(aDate.id);
	            }

	            Object[] srcArray = objectIdSet.toArray();
	            // populate the objectIDList
	            objectIDList = new String[srcArray.length];
	            System.arraycopy(srcArray, 0, objectIDList, 0, srcArray.length);
	            // here assignments is blank
	            assignments = new ArrayList();
	            showFilterPane = new Boolean(true);
	        }
	        assignmentMap = CollectionUtils.listToMap(assignments, new CollectionUtils.MapKeyLocator() {
	            public Object getKey(Object listObject) {
	                return ((Assignment) listObject).getObjectID();
	            }
	        });

	        Map spaceIDMap = new HashMap();
	        Map nameMap = new HashMap();
	        Map typeMap = new HashMap();
	        // Load the name and space id for each object
	        if (objectIDList.length > 0) {
	            DBBean db = new DBBean();
	            try {
	                // Construct where clause to match object ids
	                String whereClause = "  and (";
	                for (int objectIdIndex = 0; objectIdIndex < objectIDList.length; objectIdIndex++) {
	                    if (objectIdIndex > 0) {
	                        whereClause += " or ";
	                    }
	                    whereClause += " oname.object_id = " + objectIDList[objectIdIndex];
	                }
	                whereClause += ")";

	                // needed for decoupling tasks from assignmment's work
					db.prepareStatement("select /*+ USE_NL(o oname) USE_NL(oname spc) */ "
									+ "  oname.object_id, "
									+ "  oname.name, "
									+ "  spc.space_id, "
									+ "  o.object_type "
									+ "from "
									+ "  pn_object o,"
									+ "  pn_object_name oname, "
									+ "  pn_object_space spc "
									+ "where "
									+ "  o.object_id = oname.object_id(+) "
									+ "  and oname.object_id = spc.object_id(+) "
									+ whereClause);
	                db.executePrepared();

	                while (db.result.next()) {
	                    String currentObjectID = db.result.getString(1);
	                    nameMap.put(currentObjectID, db.result.getString(2));
	                    spaceIDMap.put(currentObjectID, db.result.getString(3));
	                    typeMap.put(currentObjectID, db.result.getString(4));
	                }
	            } catch (SQLException sqle) {
					throw new PersistenceException(
							"Unable to load names and space id's for objects", sqle);
	            } finally {
	                db.release();
	            }
	        }

			//Now we will look through the id's to see if we have already loaded them,
			//if not, we can assume they aren't in the database.  We'll create unsaved
			//assignments.
	        boolean assignmentsNotLookedUp = ((Boolean) showFilterPane).booleanValue();
	        for (int i = 0; i < objectIDList.length; i++) {
	            String id = objectIDList[i];
	            Assignment assn = (Assignment) assignmentMap.get(id);

	            // note currently assignments can be non tasks
	            // also. In future more Assignment objects would be added
				//like forms. Each assignment type might have to be handled differently
	            if (assn == null) {
					//try to get the assignment from the pn_assignment if not fetched before
	                if (assignmentsNotLookedUp) {
	                    AssignmentFinder finder = new AssignmentFinder();
	                    // add the object id
	                    TextFilter objectIDFilter = new TextFilter("objectID", AssignmentFinder.OBJECT_ID_COLUMN, false);
	                    objectIDFilter.setSelected(true);
	                    objectIDFilter.setComparator((TextComparator) TextComparator.EQUALS);
	                    objectIDFilter.setValue(id);

	                    // add the space id
	                    TextFilter spaceIDFilter = new TextFilter("spaceID", AssignmentFinder.SPACE_ID_COLUMN, false);
	                    spaceIDFilter.setSelected(true);
	                    spaceIDFilter.setComparator((TextComparator) TextComparator.EQUALS);
	                    spaceIDFilter.setValue((String) spaceIDMap.get(id));

	                    // add the person id
	                    TextFilter personIDFilter = new TextFilter("personID", AssignmentFinder.PERSON_ID_COLUMN, false);
	                    personIDFilter.setSelected(true);
	                    personIDFilter.setComparator((TextComparator) TextComparator.EQUALS);
	                    personIDFilter.setValue(personId);

	                    finder.addFinderFilter(objectIDFilter);
	                    finder.addFinderFilter(spaceIDFilter);
	                    finder.addFinderFilter(personIDFilter);
						//note this would at max return 1 row because objectid, spaceid, personid
	                    // consitute the primary key for this table
	                    Collection collection = finder.findAll();
	                    if (collection.size() > 0) {
	                        assn = (Assignment) collection.iterator().next();

	                        assignments.add(assn);
	                        assignmentMap.put(id, assn);
	                    }
	                }
	            }

	            if (assn == null) {
	                String type = (String) typeMap.get(id);

	                if (ObjectType.TASK.equals(type)) {
	                    // create a new task type assignment
						//this case would occur if the resource wants to enter time against
						//task that is not yet assigned to him. thus there is no assignment entry
	                    ScheduleEntryAssignment seAssn = new ScheduleEntryAssignment();
	                    List assignmentLogs = new AssignmentWorkLogFinder().findByObjectIDPersonID(id, personId);
	                    TimeQuantity totalWork = AssignmentWorkLogUtils.getWorkLoggedForAssignee(assignmentLogs, personId);

	                    seAssn.setWork(totalWork);
	                    seAssn.setWorkComplete(totalWork);
                        if (totalWork.getAmount().equals(new BigDecimal(0))) {
                            seAssn.setPercentComplete(new BigDecimal(0));
                        } else {
                            seAssn.setPercentComplete(new BigDecimal(1));
                        }
	                    seAssn.setComplete(false);
	                    seAssn.setPercentAssigned(100);
	                    seAssn.setPersonID(personId);
	                    seAssn.setSpaceID((String) spaceIDMap.get(id));
	                    seAssn.setObjectID(id);
	                    seAssn.setObjectName((String) nameMap.get(id));

	                    assignments.add(seAssn);
	                    assignmentMap.put(id, seAssn);
	                } else {
	                    // as of now no other assignment type needs to be created
	                    continue;
	                }
	            } else if (assn instanceof ScheduleEntryAssignment) {
	                ScheduleEntryAssignment seAssn = (ScheduleEntryAssignment) assn;
	                // load the underlying object ie the task
	                TaskFinder finder = new TaskFinder();
	                ScheduleEntry task = finder.findObjectByID(id);

	                // Don't create assignments for summary tasks. Their work is
	                // created based on the work of their children.
	                // I think the work for summary task gets added to
	                // the work performed by its children

	                // bfd-set the plan finnished date
	                seAssn.setEndTime(task.getEndTime());
	            } else if (assn instanceof ActivityAssignment) {
					//unlike task assignment as of now nothing special needs to be done here as of now
	                continue;
	            } else if (assn instanceof FormAssignment) {
					//unlike task assignment as of now nothing special needs to be done here as of now
	                continue;
	            } else {
					//remove the entries which cannot have work log againts them as of now
	                assignments.remove(assignmentMap.remove(id));
	            }
	        }
	        return assignments;
	    }
	    
	    /**
	     * Converting the hours to days if hours are greator than 80
	     * @param hours
	     * @return
	     */
	    public String convertHoursToDays(String hours){
	    	String days = hours;
		try {
			// converting work hrs to days if work hrs are greator than 80
			TimeQuantity workQty = new TimeQuantity(nf.parseNumber("" + days), TimeQuantityUnit.HOUR);
			if (workQty.compareTo(new TimeQuantity(nf.parseNumber("" + 80.0), TimeQuantityUnit.HOUR)) > 0) {
				workQty = workQty.divide(ScheduleTimeQuantity.DEFAULT_HOURS_PER_DAY, 4, BigDecimal.ROUND_HALF_UP);
                days = new TimeQuantity(workQty.getAmount(), TimeQuantityUnit.DAY).toShortString(0, 1);
			}
		} catch (Exception e) {
			log.error("Error occurred while converting hours to days " + e.getMessage());
		}
		return days;
	    }

	    /**
		 * From the request object, grab the hours that users claims they've worked on various days. <p/> This method
		 * will update {@link #errors} if any problems occur processing Update the Time sheet By getting values as user
		 * enters Manage it by Object it for what user is updating the time sheet.
		 * 
		 * @param request
		 *            a <code>HttpServletRequest</code> object that we can use to get the user's updated fields.
		 * @param id
		 *            as Object ID for what User is updating it.
		 * @return String as JS function 1)to update the Percent and other thing like Remaining work,total work
		 *         completed etc. 2)To Show the error message if error found in errorReporter while updating.
		 * @throws PersistenceException
		 *             if there is any difficulty updating the Time sheet and calculating the percent complete. Add the
		 *             error found while updating the Time sheet. like Invalid input as more than 24 hrs work a day.
		 *             Which are returened to js if found.
		 */
	    public String findUpdatedPercentComplete(HttpServletRequest request) throws ParseException {
	        String id = request.getParameter("objectId");
	        errorReporter = new ErrorReporter();
	        String scrollType = request.getParameter("scrollType") == null ? "week" : request.getParameter("scrollType");
	        int for_cnt;
	        if (scrollType.equalsIgnoreCase("week")) {
	            for_cnt = 7;
	        } else {
	            for_cnt = 1;
	        }
	        Date earliestStartDate = null;
	        Date latestStartDate = null;

	        calendarProvider = (ResourceWorkingTimeCalendarProvider) request.getSession().getAttribute("updateAssignmentsCalendarProvider");
	        planIDMap = (Map) request.getSession().getAttribute("updateAssignmentsPlanIDMap");

	        // Find all of the work complete.
	        // added the validations for fix of bfd 3076
	        List workCompleteStrings = new ArrayList();
	        // Total work check. It should not be below zero.

	        double totalRowWork = 0;
	        for (int i = 0; i < for_cnt; i++) {
	            String workString = request.getParameter("wc" + i);
	            Date startDate = new Date(Long.parseLong(request.getParameter("tv" + i)));

	            if (Validator.isBlankOrNull(workString)) {
	                continue;
	            } else if (!Validator.isNumeric(workString)) {
	                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.resource.updatework.error.invalid.message", workString, DateFormat.getInstance().formatDate(startDate))));
	                continue;
	            } else {
	                workCompleteStrings.add(workString);
	                if (earliestStartDate == null || earliestStartDate.after(startDate)) {
	                    earliestStartDate = startDate;
	                }
	                if (latestStartDate == null || latestStartDate.before(startDate)) {
	                    latestStartDate = startDate;
	                }
	            }
	        }
	        // this is for all the rows ie tasks for that particular datecolumn
	        TimeQuantity totalColumnWork = new TimeQuantity(nf.parseNumber("" +0), TimeQuantityUnit.HOUR);
	        String dateLongName = request.getParameter("dateLongName");
	        int totalAssignments = Integer.parseInt(request.getParameter("totalAssignments"));
	        Date workDate = new Date(Long.parseLong(dateLongName));
	        for (int i = 0; i < totalAssignments; i++) {
	            String workString = request.getParameter("dln" + i);            
	            if (Validator.isBlankOrNull(workString)) {
	                continue;
	            } else if (!Validator.isNumeric(workString)) {
	                continue;
	            }
	            totalColumnWork = totalColumnWork.add(new TimeQuantity(nf.parseNumber("" + workString), TimeQuantityUnit.HOUR));
	        }
	        assignmentMap = (Map) request.getSession().getAttribute("updateAssignmentsMap");
	        assignments = (List) request.getSession().getAttribute("updateAssignmentsList");
	        summaryDateValues = (Map) request.getSession().getAttribute("summaryDateValues");
	        Assignment baseAssn = (Assignment) assignmentMap.get(id);

	        // one final check
	        TimeQuantity newWorkForDay = new TimeQuantity(nf.parseNumber("" + totalColumnWork.getAmount()), TimeQuantityUnit.HOUR);
	        TimeQuantity oldWorkForDay = (TimeQuantity) summaryDateValues.get(workDate);
	        // no old work added till now for this week
	        if (oldWorkForDay == null)
	            oldWorkForDay = TimeQuantity.O_HOURS;
	        newWorkForDay = newWorkForDay.add(oldWorkForDay);
	        if (newWorkForDay.abs().compareTo(MAXIMUM_WORK_HOURS) > 0) {
	            errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.resource.assignments.update.error.invalidwork.message", newWorkForDay.formatAmount(), MAXIMUM_WORK_HOURS.formatAmount())));
	        }

	        // We decide to wait until we have a list of work complete strings so we
	        // only have to load the assignment if there are really updates to do.
	        if (workCompleteStrings.size() > 0 && !errorReporter.errorsFound()) {

	            if (baseAssn != null && baseAssn instanceof ScheduleEntryAssignment) {
	                // case of schedule entry assignment created
	                ScheduleEntryAssignment assn = (ScheduleEntryAssignment) baseAssn;
	                currentWork = ScheduleTimeQuantity.convertToHour(assn.getWorkComplete());
	                assnWork = ScheduleTimeQuantity.convertToHour(assn.getWork());

	                currentWork = getCurrentWork(workCompleteStrings, nf, currentWork);
					//Make sure we know the total amount of work in the task which might
					//be changed if the user has reported more work than is assigned
	                if (assnWork.compareTo(currentWork) < 0) {
	                    assnWork = currentWork;
	                }
	                // Calculate the percent complete. We do a clone first so we
	                // don't mess
	                // up the preloaded assignments.
	                percentComplete = getPercentComplete(currentWork, assnWork.isZero() ? new TimeQuantity(0, TimeQuantityUnit.HOUR) : assnWork, nf);
	                // finally calculate the esimated finnish date
	                if (assn.getActualStart() == null || assn.getActualStart().after(earliestStartDate)) {
	                    assn.setActualStart(earliestStartDate);

	                    WorkingTimeCalendarDefinition def = calendarProvider.getForPlanID((String) planIDMap.get(id));
	                    assn.calculateEstimatedFinish(def, calendarProvider.getDefaultTimeZone());

	                } else if (assn.getEstimatedFinish() == null) {
	                    WorkingTimeCalendarDefinition def = calendarProvider.getForPlanID((String) planIDMap.get(id));
	                    assn.calculateEstimatedFinish(def, calendarProvider.getDefaultTimeZone());

	                }
	            } else if (baseAssn != null && baseAssn instanceof FormAssignment) {
	                // case of form assignment created
	                FormAssignment assn = (FormAssignment) baseAssn;
	                currentWork = ScheduleTimeQuantity.convertToHour(assn.getWorkComplete());
	                assnWork = ScheduleTimeQuantity.convertToHour(assn.getWork());

	                currentWork = getCurrentWork(workCompleteStrings, nf, currentWork);
	                // Make sure we know the total amount of work in the form
	                // assignment which might
	                // be changed if the user has reported more work than is
	                // assigned
	                if (assnWork.compareTo(currentWork) < 0) {
	                    assnWork = currentWork;
	                }
	                // Calculate the percent complete.
                    percentComplete = getPercentComplete(currentWork, assnWork.isZero() ? new TimeQuantity(0, TimeQuantityUnit.HOUR) : assnWork, nf);
	                assn.setStartTime(earliestStartDate);
	                assn.setEndTime(latestStartDate);

	                // updateWorkModel(model, percentComplete, assnWork,
	                // currentWork);
	            } else if (baseAssn != null && baseAssn instanceof ActivityAssignment) {
	                // case of activity assignment created or already present in the
	                // Map
	                ActivityAssignment assn = (ActivityAssignment) baseAssn;
	                assnWork = assn.getWork();

	                assnWork = getCurrentWork(workCompleteStrings, nf, assnWork == null ? TimeQuantity.O_HOURS : assnWork);
	                currentWork = assnWork;
	                percentComplete = nf.formatPercent(1.0, 2);
	                assn.setStartTime(earliestStartDate);
	                assn.setEndTime(latestStartDate);

	            } else {
					//Currently this wont execute because of only one task can be update at a time. 
	                // case of new activity or task added just now in the Map
	                // form assignment cannot be added on fly in the Map
					//as these are added from a seperate module so personal assignments or timesheet page
					//would always show form assignments the are already there. so this case in not considered
	                // for form assignments
	                ScheduleEntryAssignment seAssn = null;
	                try {
	                    AssignmentFinder finder = new AssignmentFinder();
	                    FinderFilterList filters = new FinderFilterList();
	                    filters.setSelected(true);

	                    TextFilter personFilter = new TextFilter("person_id", AssignmentFinder.PERSON_ID_COLUMN, false);
	                    personFilter.setSelected(true);
	                    personFilter.setComparator((TextComparator) TextComparator.EQUALS);
	                    personFilter.setValue(SessionManager.getUser().getID());
	                    filters.add(personFilter);

	                    TextFilter objectIDFilter = new TextFilter("object_id", AssignmentFinder.OBJECT_ID_COLUMN, false);
	                    objectIDFilter.setSelected(true);
	                    objectIDFilter.setComparator((TextComparator) TextComparator.EQUALS);
	                    objectIDFilter.setValue(id);
	                    filters.add(objectIDFilter);

	                    finder.addFinderFilterList(filters);
	                    Collection asignments = finder.findAll();
	                    if (asignments.size() == 1) {
	                        Assignment assn = (Assignment) asignments.iterator().next();
	                        if (assn instanceof ScheduleEntryAssignment) {
	                            seAssn = (ScheduleEntryAssignment) assn;
	                        }
	                    }
	                } catch (PersistenceException e) {
	                }
	                if (seAssn != null) {
	                    assignmentMap.put(id, seAssn);
	                    assignments.add(seAssn);

	                    currentWork = ScheduleTimeQuantity.convertToHour(seAssn.getWorkComplete());
	                    assnWork = ScheduleTimeQuantity.convertToHour(seAssn.getWork());

	                    currentWork = getCurrentWork(workCompleteStrings, nf, currentWork);
						//Make sure we know the total amount of work in the task which might
						//be changed if the user has reported more work than is assigned
	                    if (assnWork.compareTo(currentWork) < 0) {
	                        assnWork = currentWork;
	                    }
						//Calculate the percent complete.  We do a clone first so we don't mess
	                    // up the preloaded assignments.
	                    percentComplete = getPercentComplete(currentWork, assnWork.isZero() ? new TimeQuantity(1, TimeQuantityUnit.HOUR) : assnWork, nf);
	                    // finally calculate the esimated finnish date
	                    if (seAssn.getActualStart() == null || seAssn.getActualStart().after(earliestStartDate)) {
	                        seAssn.setActualStart(earliestStartDate);

	                        WorkingTimeCalendarDefinition def = calendarProvider.getForPlanID((String) planIDMap.get(id));
	                        seAssn.calculateEstimatedFinish(def, calendarProvider.getDefaultTimeZone());
	                    } else if (seAssn.getEstimatedFinish() == null) {
	                        WorkingTimeCalendarDefinition def = calendarProvider.getForPlanID((String) planIDMap.get(id));
	                        seAssn.calculateEstimatedFinish(def, calendarProvider.getDefaultTimeZone());
	                    }
	                } else {
	                    ActivityAssignment assn = new ActivityAssignment();
	                    assignmentMap.put(id, assn);
	                    assignments.add(assn);

	                    assnWork = getCurrentWork(workCompleteStrings, nf, assnWork);
	                    currentWork = assnWork;
	                    percentComplete = nf.formatPercent(1.0, 2);
	                    assn.setStartTime(earliestStartDate);
	                    assn.setEndTime(latestStartDate);
	                }
	            }
	        } else {
	            // this is the case if some error occured in work change handler
	            if (baseAssn != null && baseAssn instanceof ScheduleEntryAssignment) {
	                // case of schedule entry assignment created
	                ScheduleEntryAssignment assn = (ScheduleEntryAssignment) baseAssn;
	                percentComplete = getPercentComplete(ScheduleTimeQuantity.convertToHour(assn.getWorkComplete()), ScheduleTimeQuantity.convertToHour(assn.getWork()), nf);
                    currentWork = assn.getWorkComplete();
                    assnWork = assn.getWork();
	            } else if (baseAssn != null && baseAssn instanceof FormAssignment) {
	                // case of schedule entry assignment created
	                FormAssignment assn = (FormAssignment) baseAssn;
	                percentComplete = getPercentComplete(assn.getWorkComplete(), assn.getWork(), nf);
                    currentWork = assn.getWorkComplete();
                    assnWork = assn.getWork();
	            } else if (baseAssn != null && baseAssn instanceof ActivityAssignment) {
					//case of activity assignment created or already present in the Map
	                ActivityAssignment assn = (ActivityAssignment) baseAssn;
	                percentComplete = nf.formatPercent(1.0, 2);
	            } else {
	                percentComplete = nf.formatPercent(0.0, 2);
	            }

	        }
	        // Set and send a Js function according NoError/Error found .
	        String js = "";
	        if (errorReporter.errorsFound()) {
	            js = "showErrorDiv('" + getJavaScriptErrors(errorReporter) + "');";
	        } else {
				js = "setWorkComplete('" + percentComplete + "','" + currentWork.toShortString(0, 2) + "','"
						+ convertHoursToDays(assnWork.subtract(currentWork).toShortString(0, 2)) + "','"
						+ convertHoursToDays(assnWork.toShortString(0, 2)) + "','"
						+ assnWork.convertTo(TimeQuantityUnit.HOUR, 2).getAmount().toString() + "');";
	        }
	        return js;
	    }

	    /**
		 * To get errors found while updating and saving of hours for work sheet as js method format 
	     * @param errorReporter
	     * @return String as JS compatible error message.
	     */
	    protected static String getJavaScriptErrors(ErrorReporter errorReporter) {
	        StringBuffer result = new StringBuffer();
	        for (Iterator iterator = errorReporter.getErrorDescriptions().iterator(); iterator.hasNext();) {
	            ErrorDescription nextError = (ErrorDescription) iterator.next();
	            buildJavascriptError(result, nextError.getErrorText());
	        }
	        return result.toString();
	    }

	    /**
	     * Build javascript error
	     * @param result
	     * @param text
	     */
	    private static void buildJavascriptError(StringBuffer result, String text) {
	        if (text != null) {
	            result.append(formatJavascriptString(text));
	        }
	    }

	    /**
	     * Format javascript string
	     * @param text
	     * @return
	     */
	    protected static String formatJavascriptString(String text) {
	        return text.replace('\'', '"') + "<br/>";
	    }

	    /**
		 * Get percentage of work complete by calculation of current work and assigned work
		 * @param currentWork
		 * @param assnWork
		 * @param nf
		 * @return
		 */	
	    public String getPercentComplete(TimeQuantity currentWork, TimeQuantity assnWork, NumberFormat nf) {
	        String percentComplete;
	        if (!assnWork.isZero()) {
	            double percentDecimal = currentWork.divide(assnWork, 4, BigDecimal.ROUND_HALF_UP).doubleValue();
	            percentDecimal = (percentDecimal > 1.0 ? 1.0 : percentDecimal);
	            percentComplete = nf.formatPercent(percentDecimal, 2);
	        } else {
	            percentComplete = nf.formatPercent(0.0, 2);
	        }
	        return percentComplete;
	    }

	    public TimeQuantity getCurrentWork(List workCompleteStrings, NumberFormat nf, TimeQuantity assnWork) throws ParseException {
	        for (Iterator it = workCompleteStrings.iterator(); it.hasNext();) {
	            String work = (String) it.next();
	            assnWork = assnWork.add(new TimeQuantity(nf.parseNumber(work), TimeQuantityUnit.HOUR));
	        }
	        return assnWork;
	    }
	    
	    /**
		 * Call from putObjectsInSession method to set the planIDMap by getting map entries of perticular user
		 * @param userID to filter from database
		 * @return
		 */
	    public Map getPlanIDMap(String userID, Map assignmentMap) {
	        Map planIDMap = new HashMap();
	        DBBean db = new DBBean();
	        try {
	            db.prepareStatement(
	                "select "+
	                "  a.object_id, "+
	                "  pht.plan_id "+
	                "from "+
	                "  pn_plan_has_task pht, "+
	                "  pn_assignment a "+
	                "where "+
	                "  pht.task_id = a.object_id  " +
	                "  and a.person_id = ? "
	            );
	            db.pstmt.setString(1, userID);
	            db.executePrepared();

	            while (db.result.next()) {
	                planIDMap.put(db.result.getString(1), db.result.getString(2));
	            }

	            List objectIDNeedingPlan = new LinkedList(assignmentMap.keySet());

	            //We need to get the id's for these objects specifically.
	            Iterator iter = objectIDNeedingPlan.iterator();
	            while (iter.hasNext()) {
	                String objectID = (String) iter.next();
	                if (planIDMap.containsKey(objectID)) {
	                    iter.remove();
	                }
	            }

	            if (!objectIDNeedingPlan.isEmpty()) {
	                //Now we have a list to load from
	                db.prepareStatement(
	                        "select " +
	                        "  pht.task_id, " +
	                        "  pht.plan_id " +
	                        "from " +
	                        "  pn_plan_has_task pht " +
	                        "where " +
	                        "  pht.task_id in (" + DatabaseUtils.collectionToCSV(objectIDNeedingPlan) + ")"
	                );
	                db.executePrepared();

	                while (db.result.next()) {
	                    planIDMap.put(db.result.getString(1), db.result.getString(2));
	                }
	            }
	        } catch (SQLException sqle) {
	            throw new PnetRuntimeException(sqle);
	        } finally {
	            db.release();
	        }
	        return planIDMap;
	    }	    

	    /**
	     * Puts some objects in session that we aren't going to use in this handler,
	     * but that we are going to use if this page is processed.
	     *
	     * @param session a <code>HttpSession</code> object that we are going to
	     * store the objects in.
	     * @param assignments a <code>List</code> of assignments that we have already
	     * loaded for other purposes.  (We'll store that too.)
	     * @param summaryDateValues a <code>Map</code> of total work for each date
	     * @throws PersistenceException if there is trouble loading some of the
	     * things we are going to store.
	     */
	    public void putObjectsInSession(HttpServletRequest request, HttpSession session) throws PersistenceException {
	        session.setAttribute("updateAssignmentsList", assignments);
	        session.setAttribute("summaryDateValues", summaryDateValues);

	        // Put a map of assignments into the session too
	        // Create a map of assignments
	        session.setAttribute("updateAssignmentsMap", assignmentMap);

	        ResourceWorkingTimeCalendarProvider calendarProvider = (ResourceWorkingTimeCalendarProvider) ResourceWorkingTimeCalendarProvider.make(SessionManager.getUser());
	        String personId = SessionManager.getUser().getID();
	        // calendarProvider.setUserID(personId);
	        // calendarProvider.load();
	        session.setAttribute("updateAssignmentsCalendarProvider", calendarProvider);
	        //planIDMap = getPlanIDMap(personId, assignmentMap);	        	
	        //planIDMap = pnAssignmentWorkDAO.getPlanIDMap(personId, assignmentMap);
	        session.setAttribute("updateAssignmentsPlanIDMap", planIDMap);
	    }

	    /**
		 * Store the user change in to database. 
		 * Return an error message if error found,
		 * other wise save it to database. 
		 * @param request
		 * @return
		 * @throws PersistenceException
		 */
	    public String processUserChanges(HttpServletRequest request) throws PersistenceException {
	        errors = new ErrorReporter();
	        String js = "";
	        Map scheduleCache = new HashMap();
	        Map taskCache = new HashMap();
	        Set tasksToStore = new HashSet();
	        Set workLogsToStore = new HashSet();
			String comments = request.getParameter("comments");
			String chargeCodeId = request.getParameter("chargeCodeId");
			boolean workDone = Boolean.parseBoolean(request.getParameter("workDone"));
			if(StringUtils.isNotBlank(comments)){
				this.comments = comments;
			}
	        summaryDateValues = (Map) request.getSession().getAttribute("summaryDateValues");
	        calendarProvider = (ResourceWorkingTimeCalendarProvider) request.getSession().getAttribute("updateAssignmentsCalendarProvider");
	        planIDMap = (Map) request.getSession().getAttribute("updateAssignmentsPlanIDMap");
	        assignmentMap = (Map) request.getSession().getAttribute("updateAssignmentsMap");

	        TimeQuantity totalWork = createWorkLog(request, taskCache, scheduleCache, tasksToStore, workLogsToStore, workDone);
	        try {
				processWorkAssigned(request, taskCache, tasksToStore);
			} catch (ParseException pnetEx) {
				log.error("Error occurred while parsing work assigned" + pnetEx.getMessage());
			}
	        storeUserChanges(workLogsToStore, tasksToStore, scheduleCache, chargeCodeId);

	        // add the pervious work performed in the week to total work
	        /*Iterator workValues = summaryDateValues.values().iterator();
	        while (workValues.hasNext()) {
	            TimeQuantity summayWorkQuantity = (TimeQuantity) workValues.next();
	            totalWork = totalWork.add(summayWorkQuantity);
	        }*/
	        if (errors.errorsFound()) {
	            js = "showNotWorkingDayErrorDiv('" + getJavaScriptErrors(errors) + "');";
	        } else {
	            js = "submitted";
	        }
	        return js;
	    }

	    public void storeUserChanges(Set workLogsToStore, Set tasksToStore, Map scheduleCache, String chargeCodeId) throws PersistenceException {
	    	Integer[] assignmentWorkId = null;
	        if (!errors.errorsFound()) {
	            DBBean db = new DBBean();
	            try {
	                db.setAutoCommit(false);

	                // Store the work logs that need to be stored
					String personId = SessionManager.getUser().getID();
					if(StringUtils.isNotBlank(this.comments)){
						new AssignmentWorkLogDAO().store(personId,objectId,this.comments, db);
					}
	                for (Iterator it = workLogsToStore.iterator(); it.hasNext();) {
	                    List workLogs = (List) it.next();
	                    assignmentWorkId = new AssignmentWorkLogDAO().store(workLogs, db);
	                }
	                //Converting Set tasksToStore to scheduleEntryToStoreArray array.
                    //which will use in next loop to decide that schedule is to recalculate or not. 
                    Object [] scheduleEntryToStoreArray = tasksToStore.toArray();
                    
                    //Store the tasks that need to be stored
                    for(int index = 0 ; index< scheduleEntryToStoreArray.length ; index++){
	                    ScheduleEntry entry = (ScheduleEntry) scheduleEntryToStoreArray[index];

						//Find the schedule related to the schedule entry for this assignment
	                    String planID = (String) planIDMap.get(entry.getID());
	                    Schedule schedule = findSchedule(scheduleCache, planID);
	                    IWorkingTimeCalendarProvider provider = schedule.getWorkingTimeCalendarProvider();

	                    if (!entry.getAssignmentList().isEmpty()) {
	                        // for fix of bfd 4938
							//why duration needs to be re computed when capturing work!!
	                        // entry.calculateDuration(provider);
	                        // fix for bfd 3103
	                        // please set the actual end date also
	                        if (entry.getWorkPercentComplete().toString().compareTo("100%") == 0) {
	                            Date endDate = entry.getResourceCalendar(provider).getEndOfWorkingDay(new Date());
	                            endDate = DateUtils.max(endDate, entry.getEndTime());
                                endDate = DateUtils.max(entry.getActualEndTime(), endDate);
                                entry.setActualEndTimeD(endDate);
                                // fix for bug-924
                                // duration needs to be recalculated when work percent complete changed to 100 %
                                entry.calculateDuration(provider);
	                        } else {
	                        	// calculate duration if estimate changed
	                        	if(isCaluculateDuration){
	                        		entry.calculateDuration(provider);
	                        		isCaluculateDuration = false;
	                        	}
                                entry.setActualEndTimeD(null);   
                            }
	                    }
	                    entry.store(true, schedule, db);
                        
                       //This is to recalculate task assignment start date after task get work captured.
	                   // Since recalculation will not effective until task stored schedule entry is committed and reloaded.
                       //Commit stored schedule entry and recalculate schedule.
                       //this condition is applied to avoid same schedue reloadnig and recalculation multiple time.  
                       if(scheduleEntryToStoreArray.length - 1 == index //in last iteration this conditon will be true so db.commit() will definitely execute.
                           || !planIDMap.get(entry.getID()).equals(planIDMap.get(((ScheduleEntry) scheduleEntryToStoreArray[index + 1]).getID()))){
                           db.commit();
                           schedule.reloadIfNeeded();
                           schedule.recalculateTaskTimes();
                       }
	                }
	                
	            } catch (SQLException sqle) {
	                try {
	                    db.rollback();
	                } catch (SQLException e) {
	                    // Pass on original SQLException
	                }
	                throw new PersistenceException("Unexpected SQLException", sqle);
	            } finally {
	                db.release();
	            }
	        }
	        // Assign charge code to assignment work capture.
            if(StringUtils.isNotEmpty(chargeCodeId)){
            	for(Integer workCaptureId : assignmentWorkId){
            		if(workCaptureId != null)
                		ServiceFactory.getInstance().getPnObjectHasChargeCodeService().save(workCaptureId, Integer.valueOf(chargeCodeId), Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID()));
            	}
            }
	    }

	    /**
	     * Helper method to make sure that a task isn't loaded twice before we store
	     * it.
	     *
	     * @param taskCache a <code>Map</code> of tasks that we have already loaded
	     * during this update.
	     * @param taskID a <code>String</code> containing the id of a task that we
	     * wish to find.
	     * @return a <code>ScheduleEntry</code> which matches the task id provided
	     * to this method.
	     * @throws PersistenceException if there is an error loading the task id.
	     */
	    public ScheduleEntry findTask(Map taskCache, String taskID) throws PersistenceException {
	        ScheduleEntry entry = (ScheduleEntry) taskCache.get(taskID);

	        if (entry == null) {
	            entry = finder.findObjectByID(taskID, false, true);
	            taskCache.put(taskID, entry);
	        }

	        return entry;
	    }

	    public TimeQuantity createWorkLog(HttpServletRequest request, Map taskCache, Map scheduleCache, Set tasksToStore, Set workLogsToStore, boolean workDone) throws PersistenceException {
	        // Collect all the dates, work, and id's.
	        MultiMap map = parseWorkLogInfoFromRequest(request);

	        // get the total work logged for timesheet total work
	        TimeQuantity totalWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
	        // Iterate through the work log entries so we can save the entries
	        // and update the task
	        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
	            String taskID = (String) it.next();
	            List rawLogEntries = (List) map.get(taskID);
	            Collections.sort(rawLogEntries);

	            // Update the assignment with the work that has been done
	            Assignment assignment = (Assignment) assignmentMap.get(taskID);
	            if (assignment instanceof ScheduleEntryAssignment) {
	                ScheduleEntryAssignment seAssignment = (ScheduleEntryAssignment) assignment;

	                WorkLogSummary summary = createWorkLogEntries(rawLogEntries, seAssignment, taskID, workLogsToStore, workDone);
	                totalWork = totalWork.add(summary.newWorkReported);

	                // Now find the schedule entry for the assignment
	                // We have to load all assignments since we're going to be
	                // adding one
	                // and recalculating duration
	                ScheduleEntry entry = findTask(taskCache, taskID);

	                // Update up the actual start and estimated finish, if needed
	                if (seAssignment.getActualStart() == null || seAssignment.getActualStart().after(summary.earliestWork)) {
	                    seAssignment.setActualStart(summary.earliestWork);
	                    WorkingTimeCalendarDefinition def = calendarProvider.getForPlanID((String) planIDMap.get(taskID));
	                    seAssignment.calculateEstimatedFinish(def,calendarProvider.getDefaultTimeZone());
						//DefinitionBasedWorkingTimeCalendar cal = calendarProvider
						//		.getCalendarForPlan((String) planIDMap.get(taskID));
						//seAssignment.calculateEstimatedFinish(cal);

	                    Schedule schedule = findSchedule(scheduleCache, entry.getPlanID());
	                    IWorkingTimeCalendarProvider provider = schedule.getWorkingTimeCalendarProvider();
	                    ScheduleEntryDateCalculator calc = new ScheduleEntryDateCalculator(entry, provider);
	                    calc.addWorkAndupdateAssignmentDates(summary.earliestWork);
	                }
	                seAssignment.setWorkComplete(seAssignment.getWorkComplete().add(summary.newWorkReported));

	                if (entry.getAssignmentList().containsForResource(seAssignment)) {
	                    // only add the work complete of the assignment is assigned
	                    // to that task

	                    TimeQuantityUnit unallocatedWorkUnit = entry.getUnallocatedWorkComplete().getUnits();

	                    // get the new work un allocated
	                    if (summary.newWorkReported.compareTo(entry.getUnallocatedWorkComplete()) > 0) {
	                        // if new work > un allocated work
	                        // then new work to be added to total work complete - un
	                        // allocated work
	                        TimeQuantity workUnallocated = entry.getWorkCompleteTQ().subtract(entry.getUnallocatedWorkComplete());
	                        //Fix:bug-1516, Round it to two digit after decimal.
	                        entry.setWorkComplete(ScheduleTimeQuantity.convertToUnit(workUnallocated.add(summary.newWorkReported), unallocatedWorkUnit, 2, BigDecimal.ROUND_HALF_UP));
	                        // set un allocated work to be zero
	                        entry.setUnallocatedWorkComplete(ScheduleTimeQuantity.convertToUnit(TimeQuantity.O_HOURS, unallocatedWorkUnit, 3, BigDecimal.ROUND_HALF_UP));
	                    } else if (summary.newWorkReported.compareTo(TimeQuantity.O_HOURS) > 0) {
	                        // if new work is +ive but less then the un allocated
	                        // work
	                        // redeuce the un allocated work by new work
	                        TimeQuantity workUnallocated = entry.getUnallocatedWorkComplete().subtract(summary.newWorkReported);
	                        entry.setUnallocatedWorkComplete(workUnallocated);
	                        // don't change the total work comple
	                    } else {
	                        // if new work is -ive reduce the total work by new work
	                        entry.setWorkComplete(entry.getWorkCompleteTQ().add(summary.newWorkReported));
	                        // don't change the un allocated work
	                    }
	                }

					//Check the actual start date of the task.  If any of the reported
					//dates are before the current actual start date, then change it
	                Date earliestDate = entry.getActualStartTime();
                    Date earliestFinishDate = entry.getActualEndTime();
	                for (Iterator it2 = rawLogEntries.iterator(); it2.hasNext();) {
	                    DateUpdate dateUpdate = (DateUpdate) it2.next();
	                    if (earliestDate == null) {
	                        earliestDate = dateUpdate.workStart;
	                    } else {
	                        earliestDate = DateUtils.min(earliestDate, dateUpdate.workStart);
	                    }
                        if (earliestFinishDate == null) {
                            earliestFinishDate = dateUpdate.workEnd; 
                        } else {
                            earliestFinishDate = DateUtils.max(earliestFinishDate, dateUpdate.workEnd);
                        }
	                }
	                if (earliestDate != null) {
	                    entry.setActualStartTimeD(earliestDate);
	                }
	                //Without assignment we can not set actual finish date, since we do not update work complete for unassigned task. 
                    if (earliestFinishDate != null && entry.getAssignmentList().containsForResource(seAssignment)) {
                        entry.setActualEndTimeD(earliestFinishDate);
                    }

	                tasksToStore.add(entry);
	            } else if (assignment instanceof FormAssignment) {
	                FormAssignment fAssignment = (FormAssignment) assignment;

	                WorkLogSummary summary = createWorkLogEntries(request, rawLogEntries, fAssignment, taskID, workLogsToStore);
	                totalWork = totalWork.add(summary.newWorkReported);
	            } else if (assignment instanceof ActivityAssignment) {
	                ActivityAssignment aAssignment = (ActivityAssignment) assignment;

	                WorkLogSummary summary = createWorkLogEntries(request, rawLogEntries, aAssignment, taskID, workLogsToStore);
	                totalWork = totalWork.add(summary.newWorkReported);
	            }
	        }
	        return totalWork;
	    }

	    public WorkLogSummary createWorkLogEntries(HttpServletRequest request, List values, ActivityAssignment assignment, String id, Set workLogsToStore) throws PersistenceException {
	        Date earliestStartDate = null;
	        TimeQuantity newWorkReported = new TimeQuantity(0, TimeQuantityUnit.HOUR);
	        String personID = SessionManager.getUser().getID();

	        // store the new adhoc activity created in memory in the db first
	        if (!assignment.isLoaded()) {
	            String name = request.getParameter("activityId" + id);
	            if (Validator.isBlankOrNull(name)) {
	                log.error("error in createworklogentries()-->" + PropertyProvider.get("prm.resource.timesheet.javascript.name.alert.message"));
	                errors.addError(new ErrorDescription(PropertyProvider.get("prm.resource.timesheet.javascript.name.alert.message")));
	            } else {
	                // if this is successful then we need to populate
	                // some of the needed fields for the assignment too
	                // this method should set the object id for activity
	                assignment.setName(name);
	                assignment.setPersonID(personID);
	                assignment.setSpaceID(personID);
	                assignment.store();

	                // activity is by default accepted,
	                // person assigned is always the primary owner
	                // and <x> percent assigned
	                assignment.setStatus(AssignmentStatus.ACCEPTED);
	                assignment.setPercentAssigned(100); // TODO:
	                assignment.setPrimaryOwner(true);
	            }
	        } else {
	            // store the assignment here as in
	            // storeUserChanges we store the worklog entries and
	            // tasks and its assignments
	            assignment.store();
	        }

	        // Update the work log with the work that has been done
	        List workLogs = new LinkedList();
	        for (Iterator it2 = values.iterator(); it2.hasNext();) {
	            DateUpdate dateUpdate = (DateUpdate) it2.next();

	            if (earliestStartDate == null || earliestStartDate.after(dateUpdate.workStart)) {
	                earliestStartDate = dateUpdate.workStart;
	            }

	            AssignmentWorkLogEntry workLog = new AssignmentWorkLogEntry();
	            workLog.setAssigneeID(personID);

	            workLog.setObjectID(assignment.getObjectID());
	            workLog.setDatesWorked(new DateRange(dateUpdate.workStart, dateUpdate.workEnd));
	            workLog.setHoursWorked(dateUpdate.workAmount);
	            workLog.setComment(dateUpdate.comment);
	            workLog.setRemainingWork(TimeQuantity.O_HOURS);
	            workLog.setModifiedByID(SessionManager.getUser().getID());
	            workLog.setScheduledWork(dateUpdate.workAmount);
	            workLog.setLogDate(dateUpdate.workStart);

	            // Figure out percent complete
	            // Since an unplanned activity they're 100%
	            BigDecimal currentPercentComplete = new BigDecimal("1.00");
	            workLog.setPercentComplete(currentPercentComplete);

	            workLogs.add(workLog);

	            // Keep track of the new work reported
	            newWorkReported = newWorkReported.add(dateUpdate.workAmount);
	        }

	        WorkLogSummary summary = new WorkLogSummary(newWorkReported, earliestStartDate);
	        workLogsToStore.add(workLogs);
	        return summary;
	    }

	    public WorkLogSummary createWorkLogEntries(HttpServletRequest request, List values, FormAssignment assignment, String id, Set workLogsToStore) throws PersistenceException {
	        Date earliestStartDate = null;
	        TimeQuantity newWorkReported = new TimeQuantity(0, TimeQuantityUnit.HOUR);
	        String personID = SessionManager.getUser().getID();

	        TimeQuantity cumulativeWorkDelta = new TimeQuantity(0, TimeQuantityUnit.HOUR);

	        // Update the work log with the work that has been done
	        List workLogs = new LinkedList();
	        for (Iterator it2 = values.iterator(); it2.hasNext();) {
	            DateUpdate dateUpdate = (DateUpdate) it2.next();

	            cumulativeWorkDelta = cumulativeWorkDelta.add(dateUpdate.workAmount);
	            TimeQuantity currentWorkComplete = assignment.getWorkComplete().add(cumulativeWorkDelta);
	            TimeQuantity remainingWork = (!assignment.getWork().isZero() ? assignment.getWork().subtract(currentWorkComplete) : TimeQuantity.O_HOURS);

	            if (earliestStartDate == null || earliestStartDate.after(dateUpdate.workStart)) {
	                earliestStartDate = dateUpdate.workStart;
	            }

	            AssignmentWorkLogEntry workLog = new AssignmentWorkLogEntry();
	            workLog.setAssigneeID(personID);
	            workLog.setObjectID(assignment.getObjectID());
	            workLog.setDatesWorked(new DateRange(dateUpdate.workStart, dateUpdate.workEnd));
	            workLog.setHoursWorked(dateUpdate.workAmount);
	            workLog.setComment(dateUpdate.comment);
	            workLog.setRemainingWork(remainingWork);
	            workLog.setModifiedByID(SessionManager.getUser().getID());
	            workLog.setScheduledWork(assignment.getWork());
	            workLog.setLogDate(dateUpdate.workStart);

	            // Figure out percent complete
	            BigDecimal currentPercentComplete;
	            if (assignment.getWork().isZero()) {
	                // Currently has no work; then they're 100% complete because they've done
	                // some work.  This occurs when adding a new assignment while updating work complete
	                currentPercentComplete = new BigDecimal("1.00");
	            } else {
	                // Currently has work; percent complete is ratio of work complete to work, at most 100%
	                currentPercentComplete = currentWorkComplete.divide(assignment.getWork(), 5, BigDecimal.ROUND_HALF_UP);
	                currentPercentComplete = currentPercentComplete.min(new BigDecimal("1.00"));
	            }
	            workLog.setPercentComplete(currentPercentComplete);

	            workLogs.add(workLog);

	            // Keep track of the new work reported
	            newWorkReported = newWorkReported.add(dateUpdate.workAmount);
	        }

	        WorkLogSummary summary = new WorkLogSummary(newWorkReported, earliestStartDate);
	        workLogsToStore.add(workLogs);

	        assignment.setWorkComplete(assignment.getWorkComplete().add(summary.newWorkReported));
	        // store the assignment here as in
	        // storeUserChanges we store the worklog entries and
	        // tasks and its assignments
	        assignment.store();
	        return summary;
	    }

	    public WorkLogSummary createWorkLogEntries(List values, ScheduleEntryAssignment assignment, String id, Set workLogsToStore, boolean workDone) {
	        Date earliestStartDate = null;
	        TimeQuantity newWorkReported = new TimeQuantity(0, TimeQuantityUnit.HOUR);
	        TimeQuantity cumulativeWorkDelta = new TimeQuantity(0, TimeQuantityUnit.HOUR);
	        String personID = SessionManager.getUser().getID();

	        // Update the work log with the work that has been done
	        List workLogs = new LinkedList();
	        for (Iterator it2 = values.iterator(); it2.hasNext();) {
	            DateUpdate dateUpdate = (DateUpdate) it2.next();

	            cumulativeWorkDelta = cumulativeWorkDelta.add(dateUpdate.workAmount);
	            TimeQuantity currentWorkComplete = assignment.getWorkComplete().add(cumulativeWorkDelta);
	            TimeQuantity remainingWork;
	            TimeQuantity scheduledWork;
	            
	            // if workDone is checked while work capture from blogit then task will be completed
	            // hence we need to set remainingWork to zero and also scheduled work to  
	            if(workDone){
	            	remainingWork = TimeQuantity.O_HOURS;
	            	scheduledWork = currentWorkComplete;
	            } else {
	            	scheduledWork = assignment.getWork();
	            	remainingWork = (!assignment.getWork().isZero() ? assignment.getWork().subtract(currentWorkComplete) : TimeQuantity.O_HOURS);
	            }
	            if (earliestStartDate == null || earliestStartDate.after(dateUpdate.workStart)) {
	                earliestStartDate = dateUpdate.workStart;
	            }

	            AssignmentWorkLogEntry workLog = new AssignmentWorkLogEntry();
	            workLog.setAssigneeID(personID);
	            workLog.setObjectID(id);
	            workLog.setDatesWorked(new DateRange(dateUpdate.workStart, dateUpdate.workEnd));
	            workLog.setHoursWorked(dateUpdate.workAmount);
	            workLog.setComment(dateUpdate.comment);
	            workLog.setRemainingWork(remainingWork);
	            workLog.setModifiedByID(SessionManager.getUser().getID());
	            workLog.setScheduledWork(scheduledWork);
	            workLog.setLogDate(dateUpdate.workStart);

	            // Figure out percent complete
	            BigDecimal currentPercentComplete;
	            if (assignment.getWork().isZero()) {
					// Currently has no work; then they're 100% complete because they've done
					// some work.  This occurs when adding a new assignment while updating work complete
	                currentPercentComplete = new BigDecimal("1.00");
	            } else {
					// Currently has work; percent complete is ratio of work complete to work, at most 100%
	                currentPercentComplete = currentWorkComplete.divide(assignment.getWork(), 5, BigDecimal.ROUND_HALF_UP);
	                currentPercentComplete = currentPercentComplete.min(new BigDecimal("1.00"));
	            }
	            workLog.setPercentComplete(currentPercentComplete);

	            workLogs.add(workLog);

	            // Keep track of the new work reported
	            newWorkReported = newWorkReported.add(dateUpdate.workAmount);
	        }

	        WorkLogSummary summary = new WorkLogSummary(newWorkReported, earliestStartDate);
	        workLogsToStore.add(workLogs);
	        return summary;
	    }

	    public Schedule findSchedule(Map scheduleCache, String planID) throws PersistenceException {
	        Schedule schedule = (Schedule) scheduleCache.get(planID);
	        if (schedule == null) {
	            schedule = new Schedule();
	            schedule.setID(planID);
	            schedule.load();
	            scheduleCache.put(planID, schedule);
	        }
	        return schedule;
	    }

	    public void processWorkAssigned(HttpServletRequest request, Map taskCache, Set tasksToStore) throws PersistenceException, ParseException {
	        //Iterate through the work changes and make sure that all tasks
	        //now have the correct amount of work assigned to them.
	        Map workChanges = collectWorkChanges(request);

	        for (Iterator it = workChanges.keySet().iterator(); it.hasNext();) {
	            String taskID = (String) it.next();
	            Assignment assignment = (Assignment) assignmentMap.get(taskID);
	            if (assignment instanceof ScheduleEntryAssignment) {
	                ScheduleEntryAssignment seAssignment = (ScheduleEntryAssignment) assignment;

	                ScheduleEntry entry = findTask(taskCache, taskID);

	                //Make sure there wasn't an update to the amount of work for the assignment.
	                String workUpdateString = request.getParameter("wkUpdate" + taskID);
	                
	                //Calculate Fixed duration assignments assigned percerntage
	                TaskCalculationType calcType = entry.getTaskCalculationType();
	                if (calcType.isFixedDuration()) {
		                BigDecimal percentAssignedDecimal = seAssignment.getPercentAssignedDecimal();
		                // Now calculate percentage based on the ratio of their new work to old work
		                BigDecimal newPercentAssigned = null;
		                if(seAssignment.getWork().isZero()) {
		                	newPercentAssigned = BigDecimal.valueOf(0.00); 
		                } else {
		                	newPercentAssigned = percentAssignedDecimal.multiply(new TimeQuantity(new BigDecimal(workUpdateString), TimeQuantityUnit.HOUR).divide(seAssignment.getWork(), 3, BigDecimal.ROUND_HALF_UP));
		               }
		                seAssignment.setPercentAssignedDecimal(newPercentAssigned);
	                }
	                
	                TimeQuantity newWork, workDelta = null;
	                if (!Validator.isBlankOrNull(workUpdateString)) {
	                    newWork = new TimeQuantity(nf.parseNumber(workUpdateString), TimeQuantityUnit.HOUR);
	                    workDelta = newWork.subtract(ScheduleTimeQuantity.convertToHour(seAssignment.getWork()));
	                    seAssignment.setWork(newWork);
	                }

	                //Only store an assignment if there really was one.
	                if (entry.getAssignmentList().containsForResource(seAssignment)) {
	                    //Make sure that the scheduleEntryAssignment has our copy of the
	                    //assignment.  It has all of the important update.
	                    entry.getAssignmentList().replaceAssignment(seAssignment);

	                    if (workDelta != null) {
	                        // add the delta work to the tasks work since the task is assigned
	                        entry.setWork(entry.getWorkTQ().add(workDelta));
	                    }

	                    //Storing the task will automatically store the assignment
	                    tasksToStore.add(entry);
	                } else if (workDelta != null) {
	                    // add it to unassocitated work since the task is not assigned
	                    //and there is some delta work
	                    entry.setUnassociatedWorkComplete(entry.getUnassociatedWorkComplete().add(workDelta));

	                    tasksToStore.add(entry);
	                }

	                // if (workDelta != null) {
	                // entry.setWork(entry.getWorkTQ().add(workDelta));
	                // tasksToStore.add(entry);
	                // }
	            } else if (assignment instanceof FormAssignment) {
	                FormAssignment fAssignment = (FormAssignment) assignment;

	                String workUpdateString = request.getParameter("wkUpdate" + taskID);
	                TimeQuantity newWork;
	                if (!Validator.isBlankOrNull(workUpdateString)) {
	                    newWork = new TimeQuantity(nf.parseNumber(workUpdateString), TimeQuantityUnit.HOUR);
	                    fAssignment.setWork(newWork);
	                    //store any work changes here as you may not get a chance to do that later
	                    fAssignment.store();
	                }
	                
	            } else if (assignment instanceof ActivityAssignment) {
	                ActivityAssignment aAssignment = (ActivityAssignment) assignment;

	                String workUpdateString = request.getParameter("wkUpdate" + taskID);
	                TimeQuantity newWork;
	                if (!Validator.isBlankOrNull(workUpdateString)) {
	                    newWork = new TimeQuantity(nf.parseNumber(workUpdateString), TimeQuantityUnit.HOUR);
	                    aAssignment.setWork(newWork);
	                }
	            }
	        }
	    }

	    public Map collectWorkChanges(HttpServletRequest request) throws ParseException {
	        Map workUpdate = new HashMap();
	        Enumeration parameterNames = request.getParameterNames();
	        while (parameterNames.hasMoreElements()) {
	            String paramName = (String) parameterNames.nextElement();
	            if (paramName.startsWith("wkUpdate")) {
	                String workUpdateString = request.getParameter(paramName);
	                if (!Validator.isBlankOrNull(workUpdateString)) {
	                    TimeQuantity newWork = new TimeQuantity(nf.parseNumber(workUpdateString), TimeQuantityUnit.HOUR);
	                    String id = paramName.substring(8);
	                    workUpdate.put(id, newWork);
	                }
	            }
	        }
	        return workUpdate;
	    }

	    /**
	     * From the request object, grab the hours that users claims they've worked
	     * on various days.  Put the information into a form that we can use to store
	     * it in the database.
	     * <p/>
	     * This method will update {@link #errors} if any problems occur processing
	     * any work values.  In that case the state of the map is undefined.
	     * @param request a <code>HttpServletRequest</code> that we'll use to find
	     * what times the user has reported.
	     * @return a <code>MultiMap</code> which maps object id strings to multiple
	     * time quantities that the user has worked.
	     */
	    public MultiMap parseWorkLogInfoFromRequest(HttpServletRequest request) {
	        MultiMap map = new MultiHashMap();
	        DateFormat df = DateFormat.getInstance();        
	        Enumeration parameterNames = request.getParameterNames();
	        while (parameterNames.hasMoreElements()) {
	            String paramName = (String) parameterNames.nextElement();

	            if (paramName.startsWith("dateupdX")) {
	                StringTokenizer tok = new StringTokenizer(paramName.substring(8), "X");

	                String id = tok.nextToken();
	                String dateSeconds = tok.nextToken();

	                //If the user didn't identify specific dates they worked, we'll
	                //use the beginning of the day to find likely times.
	                Date date = new Date(Long.parseLong(dateSeconds));

	                String paramValue = request.getParameter(paramName);
	                if (!Validator.isBlankOrNull(paramValue) && !paramValue.equals("0")) {
	                    try {
	                        TimeQuantity work = new TimeQuantity(nf.parseNumber(paramValue), TimeQuantityUnit.HOUR);
	                        TimeQuantity oldWorkForDay = (TimeQuantity) summaryDateValues.get(date);
	                        if (oldWorkForDay == null)
	            	            oldWorkForDay = TimeQuantity.O_HOURS;
	            	        TimeQuantity newWorkForDay = work.add(oldWorkForDay);
	                        if (newWorkForDay.abs().compareTo(MAXIMUM_WORK_HOURS) > 0) {
	                            // Positive or negative value exceeds maximum we allow for one day
	                            // Some limit necessary to prevent schedule calculation errors with massive work values
	                            errors.addError(new ErrorDescription(paramName, PropertyProvider.get("prm.resource.assignments.update.error.invalidwork.message", ""+newWorkForDay.getAmount().intValue(), MAXIMUM_WORK_HOURS.formatAmount())));
	                        } else {
	                            // Work amount is OK

	                            //If the user specified a start date
	                            String[] dateStartValues = request.getParameterValues("dateStartX" + id + "X" + dateSeconds);
	                            if (dateStartValues != null) {
	                                String[] dateEndValues = request.getParameterValues("dateEndX" + id + "X" + dateSeconds);
	                                String[] commentValues = request.getParameterValues("dateCommentX" + id + "X" + dateSeconds);

	                                for (int i = 0; i < dateStartValues.length; i++) {
	                                    Date dateStart = new Date(Long.parseLong(dateStartValues[i]));
	                                    Date dateEnd = new Date(Long.parseLong(dateEndValues[i]));
	                                    DateRange dr = new DateRange(dateStart, dateEnd);
	                                    TimeQuantity rangeWork = dr.getTimeQuantity(TimeQuantityUnit.HOUR, 3);

	                                    map.put(id, new DateUpdate(rangeWork, dateStart, dateEnd, commentValues[i]));
	                                }
	                            } else {
	                                PnCalendar calendar = new PnCalendar(calendarProvider.getDefaultTimeZone());
	                                //sjmittal: make start day and end day start and end of the day instead of start and end of working day
	                                //as work logged here can be more that work that can be done between working times
	                                Date dateStart = calendar.startOfDay(date);
	                                Date dateEnd = calendar.endOfDay(date);
	                                map.put(id, new DateUpdate(work, dateStart, dateEnd));
	                                
	                                WorkingTimeCalendarDefinition def = calendarProvider.getForPlanID((String) planIDMap.get(id));
	                                IWorkingTimeCalendar cal = new DefinitionBasedWorkingTimeCalendar(calendarProvider.getDefaultTimeZone(), def);

	                                // checking for the token to capture work on non working day before updating the percent complete
	                                boolean checkForNonWorkingDay = !PropertyProvider.get("prm.resource.assignments.workcaptureonnonworkingday.isenabled").equals("1");
	                                if(checkForNonWorkingDay) {
		                                if (!cal.isWorkingDay(date)) {
		                                    Assignment assn = (Assignment) assignmentMap.get(id);
		                                    errors.addError(new ErrorDescription(paramName, PropertyProvider.get("prm.resource.assignments.update.error.invalidtime.message", df.formatDate(date), assn.getObjectName())));
		                                }
	                                }
	                            }
	                        }
	                    } catch (ParseException e) {
	                        // Unable to parse work amount
	                        errors.addError(new ErrorDescription(paramName, PropertyProvider.get("prm.resource.assignments.update.error.invalidwork.message", paramValue, MAXIMUM_WORK_HOURS.formatAmount())));
	                    }
	                }
	            }
	        }
	        return map;
	    }

	    /**
	     * A data structure containing the information that the user entered (or
	     * implied) when they were entering data.
	     */
	    private class DateUpdate implements Comparable {
	        TimeQuantity workAmount;

	        Date workStart;

	        Date workEnd;

	        String comment;

	        public DateUpdate(TimeQuantity workAmount, Date workStart, Date workEnd, String comment) {
	            this.workAmount = workAmount;
	            this.workStart = workStart;
	            this.workEnd = workEnd;
	            this.comment = comment;
	        }

	        public DateUpdate(TimeQuantity workAmount, Date workStart, Date workEnd) {
	            this(workAmount, workStart, workEnd, "");
	        }
			public DateUpdate(String comment) {
				this.comment=comment;
			}

	        public int compareTo(Object o) {
	            DateUpdate co = (DateUpdate) o;

	            return this.workEnd.compareTo(co.workEnd);
	        }
	    }

	    /**
	     * Data structure that reports some summary data found while processing the
	     * work logs that need to be stored.
	     */
	    private class WorkLogSummary {
	        final TimeQuantity newWorkReported;

	        final Date earliestWork;

	        public WorkLogSummary(TimeQuantity newWorkReported, Date earliestWork) {
	            this.newWorkReported = newWorkReported;
	            this.earliestWork = earliestWork;
	        }
	    }
	    
	    /**
		 * Get History of assignment work capture history
		 * 
		 * @param objectId assignment identifier
		 * @param personId person identifier
		 * @return List of history page.
		 */
		public static List getHistory(String objectId, String personId) {
			List historyEntries = new LinkedList();
			ScheduleEntryHistory history = new ScheduleEntryHistory();			
			history.setTaskID(objectId);
			try {
				history.load(personId);
				historyEntries = getFormattedHistory(history.getAllHistoryEntry());
			} catch (Exception e) {
				log.error("Error occured while getting history for assignment :" + e.getMessage());
			}
			return historyEntries;
		}
        
        /**
		 * To get formatted date of schedule entries
		 * 
		 * @param historyEntries
		 * @return historyEntries by fomatting the date as
		 */
		public static List<ScheduleEntry> getFormattedHistory(List historyEntries) {
			List<ScheduleEntry> scheduleEntrylist = historyEntries;
			DateFormat userDateFormat = SessionManager.getUser().getDateFormatter();
			for (ScheduleEntry entry : scheduleEntrylist) {
				entry.setFormattedModifiedDate(userDateFormat.formatDate(entry.getLastModifiedDate(),
						"EEE MMM dd, yyyy h:mm a"));
			}
			return scheduleEntrylist;
		}
		
		/**
		 * Adjustment of percentage for update and set into database
		 * @param request a <code>HttpServletRequest</code> that we'll use to get the updated percentage by user 
		 * @return String as js function to set the changed percentage and work
		 * @throws ParseException
		 */
		public String changePC(HttpServletRequest request) throws ParseException {
			ErrorReporter errors = new ErrorReporter();
			String objectID = request.getParameter("objectId");
			String type = request.getParameter("type");
			if (StringUtils.isNotBlank(type) && type.equals("update")) {
				Map assignmentMap = (Map) request.getSession().getAttribute("updateAssignmentsMap");
				Assignment assn = (Assignment) assignmentMap.get(objectID);
				
				// Figure out the total amount of work that has been completed.
				TimeQuantity actualWorkComplete = getWorkComplete(assn);
				
				if (actualWorkComplete.isZero()) {
					errors.addError(PropertyProvider.get("prm.personal.assignments.cannotsetpercentcomplete.message"));
					if (errors.errorsFound()) {
						return "showErrorDiv1('" + getJavaScriptErrors(errors) + "');";
					}
				}
				
				/*TimeQuantity work = null;
				if(StringUtils.isNotEmpty(request.getParameter("work"))){
					work = new  TimeQuantity(request.getParameter("work"), TimeQuantityUnit.HOUR);
				}*/
				
				BigDecimal percentComplete = null;
				String pc = request.getParameter("pc");
				try {
					// Try to parse as pecent
					percentComplete = new BigDecimal(nf.parsePercent(pc).toString());
				} catch (ParseException ex) {
					// Assume had no '%' sign. Parse as number
					percentComplete = new BigDecimal(nf.parseNumber(pc).toString());
					percentComplete = percentComplete.movePointLeft(2);
				}
				if (!Validator.isInRange(percentComplete.doubleValue(), 0.00001, 1)) {
					errors.addError(new ErrorDescription(
									PropertyProvider.get("prm.resource.updatework.error.pencentcomplete.range.message")));
					if (errors.errorsFound()) {
						return "showErrorDiv1('" + getJavaScriptErrors(errors) + "');";
					}
				}
				
				//TimeQuantity workComplete = work.multiply(percentComplete);
				//TimeQuantity workRemaining = work.subtract(workComplete);
				
				TimeQuantity work = actualWorkComplete.divide(percentComplete, 3, BigDecimal.ROUND_HALF_UP);
				TimeQuantity workRemaining = work.subtract(actualWorkComplete);
				
				return "setPercent('"+ pc + "','" 
				+ ((work != null ) ? work.toShortString(0, 2) : "")  + "','" 
				+ ((workRemaining != null) ? workRemaining.toShortString(0, 2) : "") + "','" 
				+ ((work != null) ? work.convertTo(TimeQuantityUnit.HOUR, 2).getAmount().toString() : "") + "');";
			} else {
				try {
					isCaluculateDuration = true;
					return processUserChanges(request);
				} catch (Exception e) {
					log.error("Error occured saving percentage:" + e.getMessage());
					return "ShowPercentError();";
				}

			}
		}

		private TimeQuantity getWorkComplete(Assignment assn) {
			if (assn instanceof ScheduleEntryAssignment)
				return ((ScheduleEntryAssignment) assn).getWorkComplete();
			else if (assn instanceof FormAssignment)
				return ((FormAssignment) assn).getWorkComplete();
			else
				return ((ActivityAssignment) assn).getWork();
		}

		private BigDecimal getPercentComplete(Assignment assn) {
			if (assn instanceof ScheduleEntryAssignment)
				return ((ScheduleEntryAssignment) assn).getPercentComplete();
			else if (assn instanceof FormAssignment)
				return ((FormAssignment) assn).getPercentComplete();
			else
				return new BigDecimal(1.0);
		}

}
