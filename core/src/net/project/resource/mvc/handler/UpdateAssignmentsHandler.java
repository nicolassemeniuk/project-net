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

 package net.project.resource.mvc.handler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.PnetRuntimeException;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.StringDomainFilter;
import net.project.base.finder.TextComparator;
import net.project.base.finder.TextFilter;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.channel.ScopeType;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.resource.Assignment;
import net.project.resource.AssignmentFinder;
import net.project.resource.AssignmentManager;
import net.project.resource.AssignmentStatus;
import net.project.resource.AssignmentType;
import net.project.resource.PersonProperty;
import net.project.resource.ResourceWorkingTimeCalendarProvider;
import net.project.resource.TimesheetManager;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SecurityProvider;
import net.project.security.ServletSecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.security.group.GroupTypeID;
import net.project.util.CollectionUtils;
import net.project.util.DateFormat;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.InvalidDateException;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Handles the data loading and security checks for the UpdateAssignment.jsp
 * page.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class UpdateAssignmentsHandler extends Handler {

    /**
     * The start of the date range that we are displaying on the update
     * assignment jsp page.
     */
    private Date startDate;
    /**
     * The end of the date range that we are displaying on the update assignment
     * jsp page.
     */
    private Date endDate;

    /**
     * Maps object ids to assignments.
     */
    private Map assignmentMap;
    
    private String monthName;
    
    private String weekStartDate;
    
    private String weekEndDateString;
    
    private String weekStartMonthName;
    
    private String weekEndMonthName;
    
    private IPnPersonService pnPersonService;
    
	private WorkingTimeCalendarDefinition  workingTimeCalendarDefinition;

	private ResourceWorkingTimeCalendarProvider calendarProvider;
    
	private DateFormat dateFormat = null; 
	
	private User user;
	
	private boolean excludeUserForWorkCapture;
	
	private PersonProperty property;
	
	private List<String> spaceAdminAccessCache = null;
	
    public static class DateHeader {
        public String dayOfWeek;
        public String date;
        public boolean isWorkingDay;
    }

    public UpdateAssignmentsHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Gets the name of the view that will be rendered after processing is
     * complete.
     *
     * @return a <code>String</code> containing a name that uniquely identifies
     *         a view that we are going to redirect to after processing the
     *         request.
     */
    public String getViewName() {
        return "/resource/UpdateAssignments.jsp";
    }

    /**
     * Ensure that the requester has proper rights to access this object.  For
     * objects that aren't excluded from security checks, this will just consist
     * of verifying that the parameters that were used to access this page were
     * correct (that is, that the requester didn't try to "spoof it" by using a
     * module and action they have permission to.)
     *
     * @param module the <code>int</code> value representing the module that was
     * passed to security.
     * @param action the <code>int</code> value that was passed through security
     * for the action.
     * @param objectID the <code>String</code> value for objectID that was
     * passed through security.
     * @param request the entire request that was submitted to the schedule
     * controller.
     * @throws net.project.security.AuthorizationFailedException if the user
     * didn't have the proper credentials to view this page, or if they tried to
     * spoof security.
     * @throws net.project.base.PnetException if any other error occurred.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        AccessVerifier.verifyAccess(module, action, objectID);
    }

    /**
     * Add the necessary elements to the model that are required to render a
     * view.  Often this will include things like loading variables that are
     * needed in a page and adding them to the model.
     *
     * The views themselves should not be doing any loading from the database.
     * The whole reason for an mvc architecture is to avoid that.  All loading
     * should occur in the handler.
     *
     * @param request the <code>HttpServletRequest</code> that resulted from the
     * user submitting the page.
     * @param response the <code>HttpServletResponse</code> that will allow us
     * to pass information back to the user.
     * @return a <code>Map</code> which is the updated model.
     * @throws Exception if any error occurs.
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        excludeUserForWorkCapture = false;
        ErrorReporter errorReporter = null; 
        //sjmittal fix of bfd 3078 we need to create a new error reporter only if no error reporter
        // is passed to this handler from previous request despatcher
        if(request.getAttribute("errorReporter") == null) {
            errorReporter = new ErrorReporter();
            model.put("errorReporter", errorReporter);
        }
        model.put("readOnly", new Boolean(false));
        
        SecurityProvider securityProvider=(SecurityProvider) getSessionVar("securityProvider");
        securityProvider.securityCheck(request.getParameter("load"), Integer.toString(Module.PERSONAL_SPACE), Action.MODIFY);
        
        getWindowHeightWidth(model);
        //Make a collection of the time quantities that we want to display as
        //being selectable.
        TimeQuantityUnit[] units = new TimeQuantityUnit[] {
            TimeQuantityUnit.HOUR,
            TimeQuantityUnit.DAY,
            TimeQuantityUnit.WEEK
        };
        model.put("timeQuantityUnits", Arrays.asList(units));

        Date jumpToDate = null;
        String personId = request.getParameter("personId");
        
        if(!Validator.isNumeric(personId)) {
        	personId = SessionManager.getUser().getID();
        }
        
        if(personId.equals(SessionManager.getUser().getID())) {
        	 dateFormat= SessionManager.getUser().getDateFormatter();
        	 user = SessionManager.getUser();
        } else {
        	user = new User(personId);
        	dateFormat = user.getDateFormatter();
        }
       
        try {
            if (!Validator.isBlankOrNull(request.getParameter("jtodate")) && !request.getParameter("jtodate").trim().equals(dateFormat.getDateFormatExample())) {
            	jumpToDate = dateFormat.parseDateString(request.getParameter("jtodate"));
			} 
        } catch (InvalidDateException e) {
        	// Do nothing handle this error only from one place
        }
        
        try {//Find Non working days for the user
			calendarProvider = (ResourceWorkingTimeCalendarProvider) ResourceWorkingTimeCalendarProvider
					.make(user);
			this.workingTimeCalendarDefinition = calendarProvider.getForResourceID(user.getID());
		} catch (Exception e) {
			Logger.getLogger(UpdateAssignmentsHandler.class).error("Error occured while inintializie wroking time for person..." + e.getMessage());
		}
		
 	   //Get the dates we'd be looking at if we scrolled
        setScrollDates(request, model, errorReporter);
        //Get the column headers for each day
        getDateHeaders(model, personId);
        String assignmentStatus = request.getParameter("assignmentStatus");
        if(StringUtils.isEmpty(assignmentStatus)) {
        	assignmentStatus = getPropertyValue(property, "timesheetfilter", "allassignment");
        } else {
        	saveStatus("timesheetfilter", assignmentStatus, property);
        }
        //Figure out how many hours were worked on each day
        Map summaryDateValues = setDateValues(model, personId, assignmentStatus);
        Map grandTotalValues = setGrandTotalValue(model);
        Map dateValues = (Map)model.get("dateValues");
        List assignments = null;
        Date startOfWeek = null;
        //Get the Objectid List for Calendar Time Sheet and display the assignments
        if(Boolean.valueOf(request.getParameter("isFromTimeSheet")).booleanValue()){
	        if(request.getParameter("startDate") != null){
	        	List timesheetObjectId = null;
	        	if(jumpToDate == null || request.getParameter("jtodate").trim().equals(dateFormat.getDateFormatExample())) {
	        		startOfWeek = new Date(Long.parseLong(request.getParameter("startDate")));
	        	} else {
	        		Date jumpDate = new PnCalendar().startOfWeek(jumpToDate);
	        		startOfWeek = jumpDate;
	        	}
	        	assignments = getFilteredWeeklyAssignments(request, model, errorReporter,timesheetObjectId, assignmentStatus, personId);
	        	spaceAdminAccessCache = (List<String>)request.getSession().getAttribute("spaceAdminAccessCache");
	        	if(org.apache.commons.collections.CollectionUtils.isEmpty(spaceAdminAccessCache)) {
	        		spaceAdminAccessCache = getAdministratableSpacesByPersonId(personId);
	        	}
	        	model.put("assignments", assignments);
	        	
	        }
        } else {
            //Get the assignments that we are displaying
            assignments = getAssignments(request, model, errorReporter, null, null, null);
            model.put("assignments", assignments);
       
        }	
        //Load the list of the user's projects to populate the "Space" filter.
        //sjmittal: not needed here as timesheet module would take care now
//        ProjectPortfolioBean portfolio = loadProjectPortfolio(SessionManager.getUser());
//        model.put("portfolio", portfolio);

            //Put some date objects into the session that we'll use repeatedly
            putObjectsInSession(request, request.getSession(), assignments, summaryDateValues, startOfWeek, dateValues, assignments, grandTotalValues);
        
        //Know where to go to if the user has passed a return location
        passThru(model, "returnTo");
        passThru(model, "returnTo2");
        model.put("assignmentStatus", assignmentStatus);
        return model;
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
    private void putObjectsInSession(HttpServletRequest request, HttpSession session, List assignments, Map summaryDateValues, Date startOfWeek, Map dateValues, List excelAssignments, Map grandTotalValues) throws PersistenceException {
        session.setAttribute("updateAssignmentsList", assignments);
        session.setAttribute("summaryDateValues", summaryDateValues);
        session.setAttribute("dateValues", dateValues);
        if(startOfWeek != null){
        	session.setAttribute("startOfWeek", startOfWeek);
        }
        //Put a map of assignments into the session too
        //Create a map of assignments
        session.setAttribute("updateAssignmentsMap", assignmentMap);
        
        
        ResourceWorkingTimeCalendarProvider calendarProvider = (ResourceWorkingTimeCalendarProvider) ResourceWorkingTimeCalendarProvider.make(SessionManager.getUser());
        String personId = SessionManager.getUser().getID();
//        calendarProvider.setUserID(personId);
//        calendarProvider.load();
        session.setAttribute("updateAssignmentsCalendarProvider", calendarProvider);

        Map planIDMap = getPlanIDMap(personId);
        session.setAttribute("updateAssignmentsPlanIDMap", planIDMap);
        
    	session.setAttribute("excelAssignments", excelAssignments);
    	session.setAttribute("grandTotalValues", grandTotalValues);
    	session.setAttribute("spaceAdminAccessCache", spaceAdminAccessCache);
    	
    	PersonProperty property = new PersonProperty();
    	property.setScope(ScopeType.GLOBAL.makeScope(user));
    	session.setAttribute("property", property);
    	property.prefetchForContextPrefix("prm.resource.timesheet");
    }

    /**
     * Set the dates in the model that would be the start of the date range we
     * are scrolling to if the hits one of the date scroll buttons.
     *
     * @param request a <code>HttpServletRequest</code> that allows us to look
     * up what the beginning of the current week is.
     * @param model a <code>Map</code> we will set our objects into.
     */
    private void setScrollDates(HttpServletRequest request, Map model, ErrorReporter errorReporter) {
        PnCalendar cal = new PnCalendar();

        //See if the request included a date to start
        String dateToStart = null;
        
        if(StringUtils.isEmpty(request.getParameter("jtodate")) || request.getParameter("jtodate").trim().equals(dateFormat.getDateFormatExample())) {
        	dateToStart = request.getParameter("startDate");
	        if (dateToStart != null) {
	            startDate = new Date(Long.parseLong(dateToStart));
	        } else {
	            cal.setTime(new Date());
	            startDate = cal.startOfWeek();
	        }
        } else {
        	dateFormat = SessionManager.getUser().getDateFormatter();
            try {
                if (!Validator.isBlankOrNull(request.getParameter("jtodate")) && !request.getParameter("jtodate").trim().equals(dateFormat.getDateFormatExample())) {
                	startDate = cal.startOfWeek(dateFormat.parseDateString(request.getParameter("jtodate")));
    			}
            } catch (InvalidDateException e) {
            	startDate = cal.startOfWeek(new Date(Long.parseLong(request.getParameter("startDate"))));
            }
        }
        
        model.put("dateRangeStart", startDate);

        //Scroll back one week
        cal.setTime(startDate);
        cal.add(PnCalendar.DATE, -7);
        model.put("scrollBackStartDate", cal.getTime());

        //Scroll to one week past the current start date of the range of dates
        //we are displaying.
        cal.setTime(startDate);
        cal.add(PnCalendar.DATE, 7);
        model.put("scrollForwardStartDate", cal.getTime());

        endDate = cal.endOfDay();
    }

    /**
     * Get the assignment objects that match the filter that the user has set
     * for assignments
     *
     * @param request a <code>HttpServletRequest</code> object that we can use
     * to look up what filters the user has set.
     * @param model a <code>Map</code> object which we will use to make sure we
     * still show the same filters when the page is displayed again to the user.
     * @return a <code>List</code> of assignments which match the filtering
     * criteria.
     * @throws PersistenceException if there is any difficulty loading the
     * assignments.
     */
    private List getAssignments(HttpServletRequest request, Map model, ErrorReporter errorReporter, List timesheetObjectId, String assignmentStatus, String personId) throws PersistenceException {
        String objectID = "";
        String[] objectIDList = null;
        if(timesheetObjectId == null)
        	objectID = request.getParameter("objectID");
        else {
	        objectIDList = new String[timesheetObjectId.size()];
	        if(objectIDList.length == 1){
	        	objectID = timesheetObjectId.get(0).toString();
	        }
	        for(int objList =0; objList < timesheetObjectId.size();objList++){
		    	objectIDList[objList] = timesheetObjectId.get(objList).toString();
	        }
        }
        
        List assignments = null;
        
        List currentAssignmentList = new ArrayList();
        currentAssignmentList.add(AssignmentStatus.ACCEPTED);
        currentAssignmentList.add(AssignmentStatus.IN_PROCESS);
        
        AssignmentType[] assignmentTypes = {AssignmentType.TASK, AssignmentType.FORM};
        //sjmittal: due to decoupling: not anymore !!
        //Make sure we only load objects with type "task"
//        TextFilter objectTypeFilter = new TextFilter("objectType", AssignmentFinder.OBJECT_TYPE_COLUMN, false);
//        objectTypeFilter.setSelected(true);
//        objectTypeFilter.setComparator((TextComparator)TextComparator.EQUALS);
//        objectTypeFilter.setValue("task");
//        filters.add(objectTypeFilter);

        //Make sure we don't load summary tasks
//        TextFilter noSummaryTasksFilter = new TextFilter("noSummaryTask", AssignmentFinder.TASK_TYPE_COLUMN, false);
//        noSummaryTasksFilter.setSelected(true);
//        noSummaryTasksFilter.setComparator((TextComparator)TextComparator.NOT_EQUAL);
//        noSummaryTasksFilter.setValue("summary");
//        filters.add(noSummaryTasksFilter);
        String userID;
        String personName = SessionManager.getUser().getDisplayName();
        if(StringUtils.isNotEmpty(personId) && !personId.equals(SessionManager.getUser().getID())){
        	userID = personId;
        	personName = getPersonNameById(userID);
        } else {
        	userID = SessionManager.getUser().getID();
        }
        
        request.setAttribute("personName", personName);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(timesheetObjectId) || !Validator.isBlankOrNull(objectID)) {
            //sjmittal:
            //this has been click for capture work so we are loading 
            //assignments from pn_assignment table.
            //if no assignment is present is creates a dummy assignment object in memory
            //and the work against that is logged only in the pn_assignment_work table
        	if(timesheetObjectId == null)
        		objectIDList = request.getParameterValues("objectID");
        		
            FinderFilterList filters = new FinderFilterList();
            filters.setSelected(true);
            AssignmentManager mgr = new AssignmentManager();

            if (objectIDList.length == 1) {
                TextFilter objectIDFilter = new TextFilter("objectID", AssignmentFinder.OBJECT_ID_COLUMN, false);
                objectIDFilter.setSelected(true);
                objectIDFilter.setComparator((TextComparator)TextComparator.EQUALS);
                objectIDFilter.setValue(objectID);
                filters.add(objectIDFilter);
            } else {
                StringDomainFilter filter = new StringDomainFilter("objectID", "", AssignmentFinder.OBJECT_ID_COLUMN, (TextComparator)TextComparator.EQUALS);
                filter.setSelected(true);
                filter.setSelectedValues(objectIDList);
                filters.add(filter);
            }
            
            mgr.setPersonID(userID);
            //Execute the filter conditions 
            if(StringUtils.isNotEmpty(assignmentStatus)){
            	if(assignmentStatus.equalsIgnoreCase("completedassignment")){
            		TextFilter percentCompleteFilter = new TextFilter("percentComplete", AssignmentFinder.PERCENT_COMPLETE_COLUMN, false);
            		percentCompleteFilter.setSelected(true);
            		percentCompleteFilter.setComparator((TextComparator)TextComparator.EQUALS);
            		percentCompleteFilter.setValue("1.00");
                    filters.add(percentCompleteFilter);
            	} else if(assignmentStatus.equalsIgnoreCase("allassignment")) {
            		mgr.setStatusFilter(currentAssignmentList);
            	} else if(assignmentStatus.equalsIgnoreCase("currentassignment")) {
            		mgr.setStatusFilter(currentAssignmentList);
            		//filters.add(new CurrentAssignmentsFilter("timesheetCurrentAssignments"));
            	}
            }else {
        		mgr.setStatusFilter(currentAssignmentList);
        	}
            mgr.setAssignmentTypesFilter(assignmentTypes);
            mgr.addFilters(filters);
            mgr.setOrderBy(AssignmentFinder.SPACE_ID_COLUMN);
            mgr.setOrderDescending(false);
            mgr.loadAssignments();
            assignments = mgr.getAssignments();
            model.put("showFilterPane", new Boolean(false));
        } else {
            //sjmittal: 
            // this has been clicked from the timesheet module
            // we need to get all the assignments and activities for this person
            // best is to fetch it from pn_assignment_work as all the work
            //is stored in this table
            Map dateValues =  (Map) model.get("dateValues");
            Iterator iter = dateValues.keySet().iterator();
            //get all the object ids against which work has been logged
            //in the timesheet period
            Set<String> objectIdSet = new HashSet<String>();
            while(iter.hasNext()) {
                AssignmentDate aDate = (AssignmentDate) iter.next();
                TimeQuantity work = (TimeQuantity) dateValues.get(aDate);
                objectIdSet.add(aDate.id);
            }
            
            Object[] srcArray = objectIdSet.toArray();
            //populate the objectIDList
            objectIDList = new String[srcArray.length];
            System.arraycopy(srcArray, 0, objectIDList, 0, srcArray.length);
            //here assignments is blank
            assignments = new ArrayList();
            model.put("showFilterPane", new Boolean(true));
        }

        passThru(model, "objectID");

        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(assignments)){
	        assignmentMap = CollectionUtils.listToMap(assignments, new CollectionUtils.MapKeyLocator() {
	            public Object getKey(Object listObject) {
	                return ((Assignment)listObject).getObjectID();
	            }
	        });
        }
        
       /* Map spaceIDMap = new HashMap();
        Map spaceNameMap = new HashMap();
        Map nameMap = new HashMap();
        Map typeMap = new HashMap();
        //Load the name and space id for each object
        if (objectIDList.length > 0) {
            DBBean db = new DBBean();
            try {
                //Construct where clause to match object ids
                String whereClause = "  and (";
                for (int i = 0; i < objectIDList.length; i++) {
                    if (i > 0) {
                        whereClause += " or ";
                    }
                    whereClause += " oname.object_id = " + objectIDList[i];
                }
                whereClause += ")";

                //needed for decoupling tasks from assignmment's work
                db.prepareStatement(
                        "select + USE_NL(o oname) USE_NL(oname spc)  "+
                        "  oname.object_id, "+
                        "  oname.name, "+
                        "  spc.space_id, "+
                        "  o.object_type, "+
                        "  pps.project_name "+
//                      "  coalesce(shp.space_id, spc.space_id) as space_id "+
                        "from "+
                        "  pn_object o,"+
                        "  pn_object_name oname, "+
//                      "  pn_space_has_plan shp, "+
//                      "  pn_plan_has_task pht, "+
                        "  pn_object_space spc,"+
                        "  pn_project_space pps "+
                        "where "+
//                      "  oname.object_id = pht.task_id(+) "+
//                      "  and pht.plan_id = shp.plan_id(+) "+
                        " o.object_id = oname.object_id(+) "+
                        " and oname.object_id = spc.object_id(+) "+
                        " and spc.space_id = pps.project_id "+
                        whereClause
                );
                db.executePrepared();

                while (db.result.next()) {
                    String currentObjectID = db.result.getString(1);
                    nameMap.put(currentObjectID, db.result.getString(2));
                    spaceIDMap.put(currentObjectID, db.result.getString(3));
                    typeMap.put(currentObjectID, db.result.getString(4));
                    spaceNameMap.put(currentObjectID, db.result.getString(5));
                }
            } catch (SQLException sqle) {
                throw new PersistenceException("Unable to load names and space id's for objects", sqle);
            } finally {
                db.release();
            }
        }

        //Now we will look through the id's to see if we have already loaded them,
        //if not, we can assume they aren't in the database.  We'll create unsaved
        //assignments.
        boolean assignmentsNotLookedUp = ((Boolean ) model.get("showFilterPane")).booleanValue();
        for (int i = 0; i < objectIDList.length; i++) {
            String id = objectIDList[i];
            if(MapUtils.isNotEmpty(assignmentMap)) {
	            Assignment assn = (Assignment)assignmentMap.get(id);
	
	            //sjmittal: note currently assignments can be non tasks
	            //also. In future more Assignment objects would be added
	            //like forms. Each assignment type might have to be handled differently
	            if (assn == null) {
	                //try to get the assignment from the pn_assignment if not fetched before
	                if (assignmentsNotLookedUp) {
	                    AssignmentFinder finder = new AssignmentFinder();
	                    //add the object id
	                    TextFilter objectIDFilter = new TextFilter("objectID", AssignmentFinder.OBJECT_ID_COLUMN, false);
	                    objectIDFilter.setSelected(true);
	                    objectIDFilter.setComparator((TextComparator)TextComparator.EQUALS);
	                    objectIDFilter.setValue(id);
	                    
	                    //add the space id
	                    TextFilter spaceIDFilter = new TextFilter("spaceID", AssignmentFinder.SPACE_ID_COLUMN, false);
	                    spaceIDFilter.setSelected(true);
	                    spaceIDFilter.setComparator((TextComparator)TextComparator.EQUALS);
	                    spaceIDFilter.setValue((String)spaceIDMap.get(id));
	
	                    //add the person id
	                    TextFilter personIDFilter = new TextFilter("personID", AssignmentFinder.PERSON_ID_COLUMN, false);
	                    personIDFilter.setSelected(true);
	                    personIDFilter.setComparator((TextComparator)TextComparator.EQUALS);
	                    personIDFilter.setValue(userID);
	
	                    finder.addFinderFilter(objectIDFilter);
	                    finder.addFinderFilter(spaceIDFilter);
	                    finder.addFinderFilter(personIDFilter);
	                    //sjmittal: note this would at max return 1 row because objectid, spaceid, personid
	                    //consitute the primary key for this table
	                    Collection collection = finder.findAll();
	                    if(collection.size() > 0) {
	                        assn = (Assignment) collection.iterator().next();
	                        
	                        assignments.add(assn);
	                        assignmentMap.put(id, assn);
	                    }
	                }
	            }
	                
	            if(assn == null) {
	                String type = (String) typeMap.get(id);
	
	                if(ObjectType.TASK.equals(type)) {
	                    //create a new task type assignment
	                    //sjmittal:
	                    //this case would occur if the resource wants to enter time against
	                    //task that is not yet assigned to him. thus there is no assignment entry
	                    ScheduleEntryAssignment seAssn = new ScheduleEntryAssignment();
	                    List assignmentLogs = new AssignmentWorkLogFinder().findByObjectIDPersonID(id, userID);
	                    TimeQuantity totalWork = AssignmentWorkLogUtils.getWorkLoggedForAssignee(assignmentLogs, userID);
	
	                    seAssn.setWork(totalWork);
	                    seAssn.setWorkComplete(totalWork);
	                    seAssn.setPercentComplete(new BigDecimal(1));
	                    seAssn.setComplete(false);
	                    seAssn.setPercentAssigned(100);
	                    seAssn.setPersonID(userID);
	                    seAssn.setSpaceID((String)spaceIDMap.get(id));
	                    seAssn.setSpaceName((String)spaceNameMap.get(id));
	                    seAssn.setObjectID(id);
	                    seAssn.setObjectName((String)nameMap.get(id));
	
	                    assignments.add(seAssn);
	                    assignmentMap.put(id, seAssn);
	                } else {
	                    //sjmittal: as of now no other assignment type needs to be created
	                    continue;
	                }
	            }
	            else if(assn instanceof ScheduleEntryAssignment) {
	                ScheduleEntryAssignment seAssn = (ScheduleEntryAssignment) assn;
	                //load the underlying object ie the task
	                TaskFinder finder = new TaskFinder();
	                ScheduleEntry task = finder.findObjectByID(id);
	
	                //Don't create assignments for summary tasks.  Their work is
	                //created based on the work of their children.
	                //sjmittal: I think the work for summary task gets added to to 
	                //the work performed by its children
	                //                if (task.getTaskType().equals(TaskType.SUMMARY)) {
	                //                    errorReporter.addError(
	                //                        PropertyProvider.get("prm.personal.assignments.summaryTaskWarning",
	                //                            task.getNameMaxLength40())
	                //                    );
	                //                    continue;
	                //                }
	
	                //bfd-set the plan finnished date
	                seAssn.setEndTime(task.getEndTime());
	            } else if(assn instanceof ActivityAssignment) {
	                //sjmittal: unlike task assignment as of now nothing special needs to be done here as of now
	                continue;
	            } else if(assn instanceof FormAssignment) {
	                //sjmittal: unlike task assignment as of now nothing special needs to be done here as of now
	                continue;
	            } else {
	                //remove the entries which cannot have work log againts them as of now
	                assignments.remove(assignmentMap.remove(id));
	            }
            }
        } */

        return assignments;
    }

    /**
     * Get the column headers for each day we'll display.
     *
     * @param model a <code>Map</code> into which we'll set the information we
     * find.
     */
    private void getDateHeaders(Map model, String personId) {
    	PnCalendar cal = new PnCalendar();
    	DateFormat format = SessionManager.getUser().getDateFormatter();
    	List dateHeaders = new LinkedList();
        List dateLongNames = new ArrayList();
        String endMonthName;
        //Iterate through 7 days, storing a header for each
        try{
	        cal.setTime(startDate);
	        for (int i = 0; i < 7; i++) {
	            DateHeader dh = new DateHeader();
	            dh.date = format.formatDate(cal.getTime(), java.text.DateFormat.DATE_FIELD);
	            dh.dayOfWeek = format.formatDate(cal.getTime(), "EEE");
	            dh.isWorkingDay = workingTimeCalendarDefinition.isWorkingDay(cal);
	            dateHeaders.add(dh);
	            dateLongNames.add(String.valueOf(format.parseDateString(format.formatDate(cal.getTime())).getTime()));
	            weekEndDateString = format.formatDate(cal.getTime(), java.text.DateFormat.DATE_FIELD);
	            cal.add(Calendar.DATE,  1);
	        }
	        monthName = format.formatDate(startDate, "MMMM");
	        endMonthName = format.formatDate(cal.endOfWeek(startDate), "MMMM");
	        weekStartMonthName = format.formatDate(startDate, "MMM");
	        weekEndMonthName = format.formatDate(cal.endOfWeek(startDate), "MMM");
	        weekStartDate = format.formatDate(startDate, java.text.DateFormat.DATE_FIELD);
	        request.setAttribute("monthName", monthName);
	        request.setAttribute("endMonthName", endMonthName);
	        request.setAttribute("weekStartMonthName", weekStartMonthName);
	        request.setAttribute("weekEndMonthName", weekEndMonthName);
	        request.setAttribute("weekStartDate", weekStartDate);
	        request.setAttribute("weekEndDateString", weekEndDateString);
        }catch (Exception e) {
			Logger.getLogger(UpdateAssignmentsHandler.class).error("UpdateAssignmentHandler.getDateHeaders()..."+e.getMessage());
		}
        model.put("dateHeaders", dateHeaders);
        model.put("dateLongNames", dateLongNames);
    }

    /**
     * Determine the amount of work that has been done on each date we are
     * displaying for each object.  Also, find the sum of all work that has
     * been done on each date.
     *
     * @return a Map of the total work performed for each day
     * @throws SQLException if there is a problem loading the information from
     * the database.
     */
    private Map setDateValues(Map model, String personId, String assignmentStatus) throws SQLException {
        Map dateValues = new HashMap();
        Map summaryDateValues = new HashMap();

        PnCalendar cal = new PnCalendar();
        String sqlForDateValues = 
			"select aw.object_id, aw.work_start, aw.work_end, aw.work, aw.work_units "+
            "from " +
            "  pn_assignment_work aw," +
            "  pn_object o "+
            "where aw.work_start <= ? ";
        	if(!excludeUserForWorkCapture){
        		sqlForDateValues += " and aw.person_id = ?";
        	}
        	sqlForDateValues +=  "  and o.object_id = aw.object_id ";
			if(StringUtils.isNotEmpty(assignmentStatus)) {
	            if(assignmentStatus.equalsIgnoreCase("completedassignment")) {
	            	sqlForDateValues += "  and aw.work_end >= ? ";
	            	sqlForDateValues +="  and aw.percent_complete <= 1 and o.record_status = 'A' ";
	            } 
	            
	            if(assignmentStatus.equalsIgnoreCase("currentassignment")){
	            	sqlForDateValues += "  and aw.work_end >= ? ";
	            	sqlForDateValues += " and aw.percent_complete <= 1 and o.record_status = 'A' ";	
	            }
	            
	            if(assignmentStatus.equalsIgnoreCase("allassignment")){
	            	sqlForDateValues += " and aw.percent_complete <= 1 and o.record_status = 'A' ";
	            }
	            
	            if(assignmentStatus.equalsIgnoreCase("allworkcaptured")){
	            	sqlForDateValues += "  and aw.work_end >= ? ";
	            }
			} else {
				sqlForDateValues += "  and aw.work_end >= ? ";
				sqlForDateValues += " and aw.percent_complete < 1 ";	
            }

		DBBean db = new DBBean();
        try {
            db.prepareStatement(sqlForDateValues);
            if(StringUtils.isEmpty(personId)) {
            	personId = SessionManager.getUser().getID();
            }
            if(!excludeUserForWorkCapture){
            	db.pstmt.setString(2, personId);
            }
            DatabaseUtils.setTimestamp(db.pstmt, 1, dateFormat.parseDateString(dateFormat.formatDate(endDate)));
            if(StringUtils.isNotEmpty(assignmentStatus)) {
	            if(assignmentStatus.equalsIgnoreCase("currentassignment")){
	            	DatabaseUtils.setTimestamp(db.pstmt, 3, dateFormat.parseDateString(dateFormat.formatDate(startDate)));
	            } else if(assignmentStatus.equalsIgnoreCase("completedassignment")){
	            	DatabaseUtils.setTimestamp(db.pstmt, 3, dateFormat.parseDateString(dateFormat.formatDate(startDate)));
	            } else if(assignmentStatus.equalsIgnoreCase("allworkcaptured")){
	            	DatabaseUtils.setTimestamp(db.pstmt, 3, dateFormat.parseDateString(dateFormat.formatDate(startDate)));
	            }
            } else {
            	DatabaseUtils.setTimestamp(db.pstmt, 3, dateFormat.parseDateString(dateFormat.formatDate(startDate)));
            }

            db.executePrepared();

            //Iterate through all the assignment work and assign it to the
            //correct day in our map.
            while (db.result.next()) {
                String objectID = db.result.getString(1);
                Date workStart = dateFormat.parseDateString(dateFormat.formatDate(DatabaseUtils.getTimestamp(db.result, 2)));
                Date workEnd = DatabaseUtils.getTimestamp(db.result, 3);
                TimeQuantity work = DatabaseUtils.getTimeQuantity(db.result,  4, 5);

                Date endOfDay = cal.endOfDay(workStart);
                //All work is on the same day
                addWorkToDay(dateValues, summaryDateValues, workStart.getTime(), work.getAmount().doubleValue(), objectID);
            }
        } catch (InvalidDateException e) {
        	Logger.getLogger(UpdateAssignmentsHandler.class).error("Error occurred while parsing dates..."+e.getMessage());
		} 
        finally {
            db.release();
        }

        model.put("dateValues", dateValues);
        model.put("summaryDateValues", summaryDateValues);
        return summaryDateValues;
    }

    private void addWorkToDay(Map dateValues, Map summaryDateValues, long dateID, double work, String objectID) {
        //First, add the work to the summary we are collecting for that day.
        double existingSummaryWork = summaryDateValues.get(dateID) != null ? (Double)summaryDateValues.get(dateID) : 0.0f;
        summaryDateValues.put(dateID, existingSummaryWork + work);

        //Second, add the work for this object/day
        Double existingDayWork = dateValues.get(objectID+"X"+dateID) != null ? (Double)dateValues.get(objectID+"X"+dateID) : 0.0f;
        work = work + existingDayWork;
        dateValues.put(objectID+"X"+dateID, work);
    }
    
    private Map getPlanIDMap(String userID) {
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
            
            if(MapUtils.isNotEmpty(assignmentMap)) {
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
            }
        } catch (SQLException sqle) {
            throw new PnetRuntimeException(sqle);
        } finally {
            db.release();
        }

        return planIDMap;
    }
    
    /** Method for retrieving object id list for the selected week of selected person
     * @param startDate
     * @param personId
     * @return object id list for the selected week of selected person
     */
    public List<String> getObjectIdList(Date startDate, String personId, String assignmentStatus){
    	List<String> objectID=new ArrayList<String>();
    		PnCalendar cal = new PnCalendar();
    		Date weekStartDate = null;
    		Date weekEndDate = null;
    		Date endDate = cal.endOfWeek(startDate);
    		try {
				weekStartDate = dateFormat.parseDateString(dateFormat.formatDate(startDate));
				weekEndDate = dateFormat.parseDateString(dateFormat.formatDate(endDate));
			} catch (InvalidDateException pnetEx) {
				Logger.getLogger(UpdateAssignmentsHandler.class).error("Error occured while parsing date.."+pnetEx.getMessage());
				weekStartDate = cal.startOfWeek(new Date(Long.parseLong(request.getParameter("startDate"))));
				weekEndDate =  cal.endOfWeek(weekStartDate);
			}
			
			String sqlForObjectIdList = "select DISTINCT aw.object_id from pn_assignment aw, pn_object o ";
			if(StringUtils.isNotEmpty(assignmentStatus) && assignmentStatus.equalsIgnoreCase("allworkcaptured")) {
				sqlForObjectIdList += ", pn_assignment_work paw ";
				sqlForObjectIdList += " where paw.work_start <= ? ";
				if(!excludeUserForWorkCapture){
					sqlForObjectIdList += "and paw.person_id = ? ";
				}
				sqlForObjectIdList += "and o.object_id = aw.object_id ";
			} else {
				sqlForObjectIdList += " where aw.start_date <= ? and aw.person_id = ? and o.object_id = aw.object_id ";
			}
			if(StringUtils.isNotEmpty(assignmentStatus) && assignmentStatus.equalsIgnoreCase("allworkcaptured")) {
				sqlForObjectIdList += "  and paw.work_end >= ? and aw.object_id = paw.object_id ";
			} else {
				sqlForObjectIdList += "  and o.record_status = 'A' ";
			}
			if(StringUtils.isNotEmpty(assignmentStatus)) {
	            if(assignmentStatus.equalsIgnoreCase("completedassignment")) {
	            	sqlForObjectIdList += "  and aw.end_date >= ? and aw.percent_complete ='1.00' ";	
	            } 
	            
	            if(assignmentStatus.equalsIgnoreCase("currentassignment")){
	            	sqlForObjectIdList += "  and aw.end_date >= ? and aw.percent_complete < '1.00' ";	
	            }
	            
	            if(assignmentStatus.equalsIgnoreCase("allassignment")){
	            	sqlForObjectIdList += " and aw.percent_complete < '1.00' ";
	            }
	            if(assignmentStatus.equalsIgnoreCase("prior_six") || assignmentStatus.equalsIgnoreCase("future_six")){
	            	sqlForObjectIdList += "  and aw.end_date >= ? and aw.percent_complete < '1.00' ";
	            }
			} else {
				sqlForObjectIdList += "  and aw.end_date >= ? and aw.percent_complete < '1.00' ";	
            } 
    		DBBean db = new DBBean();
	        try {
	            db.prepareStatement(sqlForObjectIdList);
	            if(StringUtils.isEmpty(personId)) {
	            	personId = SessionManager.getUser().getID();
	            }
	            if(!excludeUserForWorkCapture){
	            	db.pstmt.setString(2, personId);
	            }
	            if(StringUtils.isNotEmpty(assignmentStatus)){
		            if(assignmentStatus.equalsIgnoreCase("prior_six")){
	            		PnCalendar prior_six = new PnCalendar();
	            		prior_six.setTime(weekEndDate);
	            		prior_six.add(Calendar.MONTH, -6);
	            		weekStartDate = dateFormat.parseDateString(dateFormat.formatDate(prior_six.getTime()));
		            	DatabaseUtils.setTimestamp(db.pstmt, 3, weekStartDate);
		            } else if(assignmentStatus.equalsIgnoreCase("future_six")){
	            		PnCalendar future_six = new PnCalendar();
	            		future_six.setTime(weekStartDate);
	            		future_six.add(Calendar.MONTH, 6);
	            		weekEndDate = dateFormat.parseDateString(dateFormat.formatDate(future_six.getTime()));
		            	DatabaseUtils.setTimestamp(db.pstmt, 1, weekEndDate);
		            } else if(assignmentStatus.equalsIgnoreCase("currentassignment") || assignmentStatus.equalsIgnoreCase("completedassignment") || assignmentStatus.equalsIgnoreCase("allassignment")) {
		            	DatabaseUtils.setTimestamp(db.pstmt, 1, weekEndDate);
		            }
	            } else {
	            	DatabaseUtils.setTimestamp(db.pstmt, 1, weekEndDate);
	            }
	            if(StringUtils.isNotEmpty(assignmentStatus)) {
		            if(assignmentStatus.equalsIgnoreCase("currentassignment")){
		            	DatabaseUtils.setTimestamp(db.pstmt, 3, weekStartDate);
		            }
		            if(assignmentStatus.equalsIgnoreCase("completedassignment")){
		            	DatabaseUtils.setTimestamp(db.pstmt, 3, weekStartDate);
		            }
		            if(assignmentStatus.equalsIgnoreCase("allworkcaptured")){
		            	DatabaseUtils.setTimestamp(db.pstmt, 1, weekEndDate);
		            	DatabaseUtils.setTimestamp(db.pstmt, 3, weekStartDate);
		            }
		            if(assignmentStatus.equalsIgnoreCase("future_six")) {
		            	DatabaseUtils.setTimestamp(db.pstmt, 3, weekStartDate);
		            } 
		            if(assignmentStatus.equalsIgnoreCase("prior_six")) {
		            	DatabaseUtils.setTimestamp(db.pstmt, 1, weekEndDate);
		            } 
	            } else {
	            	DatabaseUtils.setTimestamp(db.pstmt, 3, weekStartDate);
	            }
	            db.executePrepared();
	            
	            while (db.result.next()) {
	                objectID.add(db.result.getString(1));
	            }
	        } catch (Exception e) {
				Logger.getLogger(UpdateAssignmentsHandler.class).error("Error occured while getting the objectid list.."+e.getMessage());
			}finally {
	            db.release();
	        }
    	return objectID;	
    }
    
    /** Method for retrieving user name by user id.
     * @param userId
     * @return userName
     */
    public String getPersonNameById(String userId){
    	String userName = "";
    	if(StringUtils.isNotEmpty(userId)){
	    	pnPersonService = ServiceFactory.getInstance().getPnPersonService();
	    	PnPerson pnPerson = pnPersonService.getPersonNameById(Integer.parseInt(userId));
	    	userName = pnPerson.getDisplayName();
    	}
    	return userName;
    }
    
    /** 
     * Method for generating Grand Total for any type of assignment 
     * and any type filter criteria
     * 
     * @param model 
     * 				put Summarized grand total model to access it on the page
     * @return 
     * 				summarized grand total for the selected week
     * 
     */
    public Map setGrandTotalValue(Map model){
    	Map grandTotalValues = new HashMap();
    	PnCalendar cal = new PnCalendar();
    	cal.setTime(startDate);
    	Date weekEndDate = cal.endOfWeek(startDate);
    	
    	String sqlGrandTotal = "select object_id, work_start, work_end, work, work_units, person_id " +
    			" from pn_assignment_work where work_start <= ? and work_end >= ? " +
    			" and person_id = ?";
    	
    	Date weekStartDate = null;
    	Date weekEnd = null;
    	try{
    		weekStartDate = dateFormat.parseDateString(dateFormat.formatDate(startDate));
    		weekEnd =  cal.endOfWeek(weekStartDate);
    	}catch (InvalidDateException e) {
    		weekStartDate = cal.startOfWeek(new Date(Long.parseLong(request.getParameter("startDate"))));
    		weekEnd =  cal.endOfWeek(weekStartDate);
		}
    	
    	DBBean db = new DBBean();
    	 try {
	            db.prepareStatement(sqlGrandTotal);
	            
	            DatabaseUtils.setTimestamp(db.pstmt, 1, weekEnd);
            	DatabaseUtils.setTimestamp(db.pstmt, 2, weekStartDate);
	            db.pstmt.setString(3, SessionManager.getUser().getID());
	            db.executePrepared();
	            
	            while (db.result.next()) {
	            	String objectID = db.result.getString(1);
	                Date workStart = DatabaseUtils.getTimestamp(db.result, 2);
	                Date workEnd = DatabaseUtils.getTimestamp(db.result, 3);
	                TimeQuantity work = DatabaseUtils.getTimeQuantity(db.result,  4, 5);
	                
	                AssignmentDate dateID = new AssignmentDate(cal.startOfDay(workStart), objectID);
                    addWorkToDay(grandTotalValues, dateID, work);
	            }
    	 } catch (SQLException e) {
    		 Logger.getLogger(UpdateAssignmentsHandler.class).error("UpdateAssignmentHandler.setGrandTotalValues() failed..."+e.getMessage());
		} finally {
			 db.release();
		}

		model.put("grandTotalValues", grandTotalValues);
    	return grandTotalValues;
    }
    
    /** 
     * Method for preparing summarized total for selected week
     * 
     * @param grandTotalValues 
     *  				inserts values in this map
     * @param dateID
     * 					dateID with assignments objectid
     * @param work
     * 					work for the day.
     */
    private void addWorkToDay(Map grandTotalValues, AssignmentDate dateID, TimeQuantity work) {
        //First, add the work to the summary we are collecting for that day.
    	TimeQuantity existingSummaryWork = (TimeQuantity)grandTotalValues.get(dateID.date);
        if (existingSummaryWork == null) {
        	grandTotalValues.put(dateID.date, work);
        } else {
        	grandTotalValues.put(dateID.date, existingSummaryWork.add(work));
        }
    }
    
    /**
     * Mehthod to get filtered assignments which are assigned to this user 
     * according to the filter criteria. 
     *
     * @param request a <code>HttpServletRequest</code> object that we can use
     * to look up what filters the user has set.
     * @param model a <code>Map</code> object which we will use to make sure we
     * still show the same filters when the page is displayed again to the user.
     * @return a <code>List</code> of assignments which match the filtering
     * criteria.
     * @throws PersistenceException if there is any difficulty loading the
     * assignments.
     */
    private List getFilteredWeeklyAssignments(HttpServletRequest request, Map model, ErrorReporter errorReporter, List timesheetObjectId, String assignmentStatus, String personId) throws PersistenceException {
    	List assignments = new ArrayList();
    	Date weekStartDate = null;
    	boolean isAllWorkCaptured = false;
    	PnCalendar calendar = new PnCalendar();
    	try{
    		if(!Validator.isBlankOrNull(request.getParameter("jtodate")) && !request.getParameter("jtodate").trim().equals(dateFormat.getDateFormatExample())){
    			weekStartDate = dateFormat.parseDateString(request.getParameter("jtodate"));
    		} else {
    			weekStartDate = dateFormat.parseDateString(dateFormat.formatDate(new Date(Long.parseLong(request.getParameter("startDate")))));
    		}
    	}catch (InvalidDateException e) {
    		Logger.getLogger(UpdateAssignmentsHandler.class).error("Error occured while parsing dates.."+e.getMessage());
    		weekStartDate = calendar.startOfWeek(new Date(Long.parseLong(request.getParameter("startDate"))));
    		ErrorDescription ed = new ErrorDescription("jtodate", PropertyProvider.get("prm.resource.timesheet.error.jumptodate.message"));
            errorReporter.addError(ed);
		}
    	
    	String personName = SessionManager.getUser().getDisplayName();
    	request.setAttribute("personName", personName);
    	
    	calendar.setTime(weekStartDate);
    	Date weekEndDate = calendar.endOfWeek(weekStartDate);
    	
    	Boolean isPercentComplete = true;
    	try {
	    	if(StringUtils.isNotEmpty(assignmentStatus)){
	    		if(assignmentStatus.equalsIgnoreCase("allassignment")) {
	    			weekStartDate = null;
	    		} else if(assignmentStatus.equalsIgnoreCase("completedassignment")){
	    			isPercentComplete = false;
	    		} else if(assignmentStatus.equalsIgnoreCase("future_six")){
	    			PnCalendar future_six = new PnCalendar();
	        		future_six.setTime(weekStartDate);
	        		future_six.add(Calendar.MONTH, 6);
	        		weekEndDate = dateFormat.parseDateString(dateFormat.formatDate(future_six.getTime()));
	    		} else if(assignmentStatus.equalsIgnoreCase("prior_six")){
	        		PnCalendar prior_six = new PnCalendar();
	        		prior_six.setTime(weekEndDate);
	        		prior_six.add(Calendar.MONTH, -6);
	        		weekStartDate = dateFormat.parseDateString(dateFormat.formatDate(prior_six.getTime()));
	    		}  else if(assignmentStatus.equalsIgnoreCase("allworkcaptured")){
	    			isPercentComplete = null;
	    			isAllWorkCaptured = true;
	    		}
	    	}
    	} catch (InvalidDateException e) {
    		Logger.getLogger(UpdateAssignmentsHandler.class).error("Error occured while parsing dates.."+e.getMessage());
		} catch (Exception e){
			Logger.getLogger(UpdateAssignmentsHandler.class).error("UpdateAssignmentHandler.getFilteredWeeklyAssignments() failed..."+e.getMessage());
		}
    	
    	List<String> objectStatus = new ArrayList<String>();
    	List<String> objectType = new ArrayList<String>();
    	
    	objectStatus.add(AssignmentStatus.ACCEPTED.getID());
    	objectStatus.add(AssignmentStatus.IN_PROCESS.getID());
    	
    	objectType.add(AssignmentType.TASK.getObjectType());
    	objectType.add(AssignmentType.FORM.getObjectType());
    	
    	try {
	    	TimesheetManager timesheetManager = new TimesheetManager();
	    	timesheetManager.findByDate(personId, weekStartDate, weekEndDate, isPercentComplete, objectStatus, objectType, isAllWorkCaptured);
	    	assignments = timesheetManager.getFilteredAssignments();
    	}catch (Exception sqle) {
    		Logger.getLogger(UpdateAssignmentsHandler.class).error("UpdateAssignmentHandler.getFilteredWeeklyAssignments() by timesheetManager.findByDate() failed..."+sqle.getMessage());
		}
    	
    	model.put("showFilterPane", new Boolean(true));
    	
    	passThru(model, "objectID");

        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(assignments)){
	        assignmentMap = CollectionUtils.listToMap(assignments, new CollectionUtils.MapKeyLocator() {
	            public Object getKey(Object listObject) {
	                return ((Assignment)listObject).getObjectID();
	            }
	        });
        }
    	
    	return assignments;
    }
    
    /**
     * Returns all the spaces in which the person is 
     * Space Administrator
     * 
     * @param personID the person's id
     * @return list of the space id's
     */
    private List<String> getAdministratableSpacesByPersonId(String personID) {
    		spaceAdminAccessCache = new ArrayList<String>();
    		String spaceListSql = 
    				" select " +
    				" distinct pshg.space_Id " +
    				" from " +
    				" pn_space_has_group pshg, pn_group_has_person pghp, pn_group_view pgv " +
    				" where " + 
    				" pghp.group_id = pshg.group_id "+
    				" and pgv.group_id = pshg.group_id "+
    				" and pghp.person_id = " + personID +
    				" and pgv.record_status = 'A' and pgv.group_type_id = "+ GroupTypeID.SPACE_ADMINISTRATOR.getID() ;
    		
    		DBBean db = new DBBean();
    		try {
   	             db.prepareStatement(spaceListSql);
   	             db.executePrepared();
	   	         while (db.result.next()) {
	   	        	spaceAdminAccessCache.add(db.result.getString(1));
	   	         }
    		} catch (Exception e) {
				Logger.getLogger(UpdateAssignmentsHandler.class).error("UpdateAssignmentsHandler.getAdministratableSpacesByPersonId() failed, error Occured.."+e.getMessage());
			}
    	return spaceAdminAccessCache;
    }
    
    /**
     * Get the previous selected filter as per user
     * @param props
     * @param property
     * @param isNull
     * @return filter criteria
     */
    private String getPropertyValue(PersonProperty props, String property, String isNull) {
    	if(props == null){
    		props = new PersonProperty();
    		props.setScope(ScopeType.GLOBAL.makeScope(user));
    		props.prefetchForContextPrefix("prm.resource.timesheet");
        	request.getSession().setAttribute("property", props);
    	}
        String[] expandedProps = props.get("prm.resource.timesheet", property, true);
        return (expandedProps != null && expandedProps.length > 0 ? expandedProps[0]: isNull);
    }
    
    /**
     *  Save the user status for the selected filter
     *   
     * @param context filter context 
     * @param filterCriteria filter criteria to set
     * @param property person propery to set
     */
    private void saveStatus(String context, String filterCriteria, PersonProperty property){
    	if(property == null) {
    		property = new PersonProperty();
    		property.setScope(ScopeType.GLOBAL.makeScope(user));
    	}
    	try {
			property.replace("prm.resource.timesheet", context, filterCriteria);
		} catch(PersistenceException ex){
			Logger.getLogger(UpdateAssignmentsHandler.class).error("UpdateAssignmentHandler.saveStatus() failed..."+ex.getMessage());
		}
    }
    
    /**
     * Method for getting the window and height and width
     *  
     * @param model return calculated values as per screen resolution
     */
    private void getWindowHeightWidth(Map model){
    	HttpSession session = request.getSession();
    	String windowWidth = ServletSecurityProvider.getCookie(request, "w");
    	String windowHeight = ServletSecurityProvider.getCookie(request, "h");
    	int screenHeight = 0;
    	int screenWidth = 0;
    	// default width
    	String allWidth = "100%";
    	
    	// default height
    	String assignmentlistHeight = "100%";
    	String splitterHeight = "";
    	String blogPanelHeight = "";
    	try{
    		if(StringUtils.isNotEmpty(windowHeight) && windowHeight != "null" && StringUtils.isNotEmpty(windowWidth) &&  windowWidth != "null") {
    			screenHeight = Integer.valueOf(windowHeight);
    			screenWidth = Integer.valueOf(windowWidth);
    			
    			allWidth = ""+(screenWidth - 223);
    			
    			assignmentlistHeight = "height:"+(screenHeight-255)+"px;";
    			splitterHeight = "height:"+(screenHeight-180)+"px;";
    			blogPanelHeight = "height:"+(screenHeight-200)+"px;";
    			
    		} else if(session.getAttribute("windowWidth") != null && session.getAttribute("windowWidth") != "null" && session.getAttribute("windowHeight") != null && session.getAttribute("windowHeight") != "null") {
    			screenHeight = Integer.valueOf((String)session.getAttribute("windowHeight"));
    			screenWidth = Integer.valueOf((String)session.getAttribute("windowWidth"));
    			
    			allWidth= ""+(screenWidth - 212);
    			
    			assignmentlistHeight = "height:"+(screenHeight-255)+"px;";
    			splitterHeight = "height:"+(screenHeight-180)+"px;";
    			blogPanelHeight = "height:"+(screenHeight-200)+"px;";
    		}
    	} catch(Exception e) {
    		Logger.getLogger(UpdateAssignmentsHandler.class).error("UpdateAssignmentsHandler.getWindowHeightWidth() failed..");
    	}
    	session.setAttribute("windowHeight", windowHeight);
    	session.setAttribute("windowWidth", windowWidth);
    	// Set Width in request
    	model.put("allWidth", allWidth);
    	
    	// set height in request
    	model.put("assignmentlistHeight", assignmentlistHeight);
    	model.put("blogPanelHeight", blogPanelHeight);
    	model.put("splitterHeight", splitterHeight);
    }
    
//    private ProjectPortfolioBean loadProjectPortfolio(User user) throws PersistenceException {
//        ProjectPortfolioBean portfolio = new ProjectPortfolioBean();
//        portfolio.setID(user.getMembershipPortfolioID());
//        portfolio.setUser(user);
//        portfolio.load();
//
//        return portfolio;
//    }
}
