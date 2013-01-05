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
|   $Revision: 20010 $
|       $Date: 2009-09-23 11:30:15 -0300 (mié, 23 sep 2009) $
|     $Author: ritesh $
|
+-----------------------------------------------------------------------------*/
package net.project.resource.mvc.handler;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;

import net.project.base.PnetException;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.TextComparator;
import net.project.base.finder.TextFilter;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.base.property.PropertyProvider;
import net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.form.assignment.FormAssignment;
import net.project.persistence.PersistenceException;
import net.project.resource.ActivityAssignment;
import net.project.resource.Assignment;
import net.project.resource.AssignmentFinder;
import net.project.resource.ResourceWorkingTimeCalendarProvider;
import net.project.resource.ScheduleEntryAssignment;
import net.project.resource.mvc.view.WorkChangedView;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;

/**
 * Computes a new percentage complete for a task on the fly given updated work
 * that the user has entered.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class WorkChangeHandler extends Handler {

    /** Stores the errors that we find during processing. */
    protected ErrorReporter errorReporter = new ErrorReporter();
    /** PlanID to resource calendar mapping. */
    private ResourceWorkingTimeCalendarProvider calendarProvider;
    /** Map of object ID to plan ID's. */
    private Map planIDMap;

    private String textId;
    public WorkChangeHandler(HttpServletRequest request) {
        super(request);
        calendarProvider = (ResourceWorkingTimeCalendarProvider)request.getSession().getAttribute("updateAssignmentsCalendarProvider");
        planIDMap = (Map)request.getSession().getAttribute("updateAssignmentsPlanIDMap");
    }

    /**
     * This implementation throws an <code>UnsupportedOperationException</code>
     * always since this class overrides {@link #getView()} to return a view.
     * @return never returns
     * @throws UnsupportedOperationException always
     */
    public String getViewName() {
        throw new UnsupportedOperationException("WorkChangeHandler cannot return a simple view name.  Use getView() instead.");
    }

    /**
     * Returns a Javascript view for rendering the model.
     * @return the view
     */
    public IView getView() {
        return new WorkChangedView();
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

        String id = request.getParameter("objectID");

        findUpdatedPercentComplete(request, id, model);
        passThru(model, "objectID");

        return model;
    }

    private void findUpdatedPercentComplete(HttpServletRequest request, String id, Map model) throws ParseException {
        Date earliestStartDate = null;
        Date latestStartDate = null;

        //Find all of the work complete.
        //sjmittal added the validations for fix of bfd 3076
        List workCompleteStrings = new ArrayList();
        //Total work check. It should not be below zero.
        double totalRowWork = 0;
        textId = request.getParameter("textId");
        for (int i = 0; i < 7; i++) {
            String workString = request.getParameter("wc"+i);
            Date startDate = new Date(Long.parseLong(request.getParameter("tv"+i)));
            
            if (Validator.isBlankOrNull(workString)) {
                continue;
            } else if (!Validator.isNumeric(workString)) {
                errorReporter.addError(new ErrorDescription(textId, PropertyProvider.get("prm.resource.updatework.error.invalid.message", workString, DateFormat.getInstance().formatDate(startDate))));
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
            totalRowWork += Double.parseDouble(workString);
        }
//        if(Validator.isNegative(""+totalRowWork)) {
//            errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.resource.updatework.total.negative.error.message")));
//        }
        
        //this is for all the rows ie tasks for that particular datecolumn
        double totalColumnWork = 0;
        String dateLongName = request.getParameter("dateLongName");
        int totalAssignments = Integer.parseInt(request.getParameter("totalAssignments"));
        Date workDate = new Date(Long.parseLong(dateLongName));
        for (int i = 0; i < totalAssignments; i++) {
            String workString = request.getParameter("dln"+i);;            
            if (Validator.isBlankOrNull(workString)) {
                continue;
            } else if (!Validator.isNumeric(workString)) {
                //sjmittal: this is verified above
//                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.resource.updatework.error.invalid.message", workString, DateFormat.getInstance().formatDate(workDate))));
                continue;
            } 
            totalColumnWork += Double.parseDouble(workString);
        }
//        if(Validator.isNegative(""+totalColumnWork)) {
//            errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.resource.updatework.total.negative.error.message")));
//        }
        
        String percentComplete = null;
        TimeQuantity assnWork = TimeQuantity.O_HOURS;
        TimeQuantity currentWork = assnWork;
        NumberFormat nf = NumberFormat.getInstance();
        Map assignmentMap = (Map)request.getSession().getAttribute("updateAssignmentsMap");
        List assignments = (List)request.getSession().getAttribute("updateAssignmentsList");
        Map summaryDateValues = (Map)request.getSession().getAttribute("grandTotalValues");
        Assignment baseAssn = (Assignment)assignmentMap.get(id);

        boolean isNonWorkingDayWorkCapture = false;
        boolean isWork24DayWorkCapture = false;
        
        //one final check
        TimeQuantity newWorkForDay = new TimeQuantity(nf.parseNumber(""+totalColumnWork), TimeQuantityUnit.HOUR);
        TimeQuantity oldWorkForDay = (TimeQuantity) summaryDateValues.get(workDate);
        //no old work added till now for this week
        if(oldWorkForDay == null)
            oldWorkForDay = TimeQuantity.O_HOURS;
        if (newWorkForDay.add(oldWorkForDay).abs().compareTo(UpdateAssignmentsProcessingHandler.MAXIMUM_WORK_HOURS) > 0) {
            errorReporter.addError(new ErrorDescription(textId, PropertyProvider.get(
                    "prm.resource.assignments.update.error.invalidwork.message", 
                    newWorkForDay.add(oldWorkForDay).formatAmount(), 
                    UpdateAssignmentsProcessingHandler.MAXIMUM_WORK_HOURS.formatAmount())));
            isWork24DayWorkCapture = true;
        } else {
        	isWork24DayWorkCapture = false;
        }

        //We decide to wait until we have a list of work complete strings so we
        //only have to load the assignment if there are really updates to do.
        if (workCompleteStrings.size() > 0 && !errorReporter.errorsFound()) {

            if (baseAssn != null && baseAssn instanceof ScheduleEntryAssignment) {
                //case of schedule entry assignment created
                ScheduleEntryAssignment assn = (ScheduleEntryAssignment) baseAssn;
                currentWork = ScheduleTimeQuantity.convertToHour(assn.getWorkComplete());
                assnWork = ScheduleTimeQuantity.convertToHour(assn.getWork());
                
                currentWork = getCurrentWork(workCompleteStrings, nf, currentWork);
                //Make sure we know the total amount of work in the task which might
                //be changed if the user has reported more work than is assigned
                if (assnWork.compareTo(currentWork) < 0) {
                    assnWork = currentWork;
                }
                //Calculate the percent complete.  We do a clone first so we don't mess
                //up the preloaded assignments.
                percentComplete = getPercentComplete(currentWork, assnWork.isZero() ? new TimeQuantity(1, TimeQuantityUnit.HOUR) : assnWork, nf);
                //finally calculate the esimated finnish date
                if (assn.getActualStart() == null || assn.getActualStart().after(earliestStartDate)) {
                    assn.setActualStart(earliestStartDate);
    
                    WorkingTimeCalendarDefinition def = calendarProvider.getForPlanID((String)planIDMap.get(id));
                    assn.calculateEstimatedFinish(def, calendarProvider.getDefaultTimeZone());

                } else if (assn.getEstimatedFinish() == null) {
                    WorkingTimeCalendarDefinition def = calendarProvider.getForPlanID((String)planIDMap.get(id));
                    assn.calculateEstimatedFinish(def, calendarProvider.getDefaultTimeZone());

                }
                isNonWorkingDayWorkCapture = isNonWorkingDay((String) planIDMap.get(id), workDate, model, 
												newWorkForDay, oldWorkForDay, percentComplete, assnWork, currentWork);
            }   else if (baseAssn != null && baseAssn instanceof FormAssignment) {
                //case of form assignment created
                FormAssignment assn = (FormAssignment) baseAssn;
                currentWork = ScheduleTimeQuantity.convertToHour(assn.getWorkComplete());
                assnWork = ScheduleTimeQuantity.convertToHour(assn.getWork());
                
                currentWork = getCurrentWork(workCompleteStrings, nf, currentWork);
                //Make sure we know the total amount of work in the form assignment which might
                //be changed if the user has reported more work than is assigned
                if (assnWork.compareTo(currentWork) < 0) {
                    assnWork = currentWork;
                }
                //Calculate the percent complete.
                percentComplete = getPercentComplete(currentWork, assnWork, nf);
                assn.setStartTime(earliestStartDate);
                assn.setEndTime(latestStartDate);
                
                isNonWorkingDayWorkCapture = isNonWorkingDay((String) planIDMap.get(id), workDate, model, 
                								newWorkForDay, oldWorkForDay, percentComplete, assnWork, currentWork);
            } else  if (baseAssn != null && baseAssn instanceof ActivityAssignment) {
                //case of activity assignment created or already present in the Map
                ActivityAssignment assn = (ActivityAssignment) baseAssn;
                assnWork = assn.getWork();

                assnWork = getCurrentWork(workCompleteStrings, nf, assnWork == null ? TimeQuantity.O_HOURS: assnWork);
                currentWork = assnWork;
                percentComplete = nf.formatPercent(1.0, 2);
                assn.setStartTime(earliestStartDate);
                assn.setEndTime(latestStartDate);
                
                updateWorkModel(model, percentComplete, assnWork, currentWork);
            } else {
                //case of new activity or task added just now in the Map
                //sjmittal (note*): form assignment cannot be added on fly in the Map
                //as these are added from a seperate module so personal assignments or timesheet page
                //would always show form assignments the are already there. so this case in not considered
                //for form assignments
                ScheduleEntryAssignment seAssn = null;
                try {
                    AssignmentFinder finder = new AssignmentFinder();
                    FinderFilterList filters = new FinderFilterList();
                    filters.setSelected(true);
                    
                    TextFilter personFilter = new TextFilter("person_id", AssignmentFinder.PERSON_ID_COLUMN, false);
                    personFilter.setSelected(true);
                    personFilter.setComparator((TextComparator)TextComparator.EQUALS);
                    personFilter.setValue(SessionManager.getUser().getID());
                    filters.add(personFilter);
                    
                    TextFilter objectIDFilter = new TextFilter("object_id", AssignmentFinder.OBJECT_ID_COLUMN, false);
                    objectIDFilter.setSelected(true);
                    objectIDFilter.setComparator((TextComparator)TextComparator.EQUALS);
                    objectIDFilter.setValue(id);
                    filters.add(objectIDFilter);
                    
                    finder.addFinderFilterList(filters);
                    Collection asignments = finder.findAll();
                    if(asignments.size() == 1) {
                        Assignment assn = (Assignment) asignments.iterator().next();
                        if(assn instanceof ScheduleEntryAssignment) {
                            seAssn = (ScheduleEntryAssignment) assn;
                        }
                    }
                } catch (PersistenceException e) {
                }                
                if(seAssn != null) {
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
                    //up the preloaded assignments.
                    percentComplete = getPercentComplete(currentWork, assnWork.isZero() ? new TimeQuantity(1, TimeQuantityUnit.HOUR) : assnWork, nf);
                    //finally calculate the esimated finnish date
                    if (seAssn.getActualStart() == null || seAssn.getActualStart().after(earliestStartDate)) {
                        seAssn.setActualStart(earliestStartDate);
        
                        WorkingTimeCalendarDefinition def = calendarProvider.getForPlanID((String)planIDMap.get(id));
                        seAssn.calculateEstimatedFinish(def, calendarProvider.getDefaultTimeZone());

                    } else if (seAssn.getEstimatedFinish() == null) {
                        WorkingTimeCalendarDefinition def = calendarProvider.getForPlanID((String)planIDMap.get(id));
                        seAssn.calculateEstimatedFinish(def, calendarProvider.getDefaultTimeZone());

                    }
                }
                else {
                    ActivityAssignment assn = new ActivityAssignment();
                    assignmentMap.put(id, assn);
                    assignments.add(assn);

                    
                    assnWork = getCurrentWork(workCompleteStrings, nf, assnWork);
                    currentWork = assnWork;
                    percentComplete = nf.formatPercent(1.0, 2);
                    assn.setStartTime(earliestStartDate);
                    assn.setEndTime(latestStartDate);
                }
                
                updateWorkModel(model, percentComplete, assnWork, currentWork);
            }
            
            //update the values for that column
            model.put("dateLongName", dateLongName);
//            TimeQuantity newWorkForDay = new TimeQuantity(nf.parseNumber(""+totalColumnWork), TimeQuantityUnit.HOUR);
//            TimeQuantity oldWorkForDay = (TimeQuantity) summaryDateValues.get(workDate);
            //no old work added till now for this week
//            if(oldWorkForDay == null)
//                oldWorkForDay = TimeQuantity.O_HOURS;
            model.put("dateLongNameWork", oldWorkForDay.add(newWorkForDay));
        } else {
            //sjmittal: this is the case if some error occured in work change handler 
            if (baseAssn != null && baseAssn instanceof ScheduleEntryAssignment) {
                //case of schedule entry assignment created
                ScheduleEntryAssignment assn = (ScheduleEntryAssignment) baseAssn;
                percentComplete = getPercentComplete(ScheduleTimeQuantity.convertToHour(assn.getWorkComplete()), ScheduleTimeQuantity.convertToHour(assn.getWork()), nf);

                model.put("work", assn.getWork());
                model.put("percentComplete", percentComplete);
                model.put("workComplete", assn.getWorkComplete());
                model.put("workRemaining", assn.getWorkRemaining());
            } else if (baseAssn != null && baseAssn instanceof FormAssignment) {
                //case of schedule entry assignment created
                FormAssignment assn = (FormAssignment) baseAssn;
                percentComplete = getPercentComplete(assn.getWorkComplete(), assn.getWork(), nf);

                model.put("work", assn.getWork());
                model.put("percentComplete", percentComplete);
                model.put("workComplete", assn.getWorkComplete());
                model.put("workRemaining", assn.getWorkRemaining());
            } else if(baseAssn != null && baseAssn instanceof ActivityAssignment) {
                //case of activity assignment created or already present in the Map
                ActivityAssignment assn = (ActivityAssignment) baseAssn;
                percentComplete = nf.formatPercent(1.0, 2);
                
                model.put("work", assn.getWork());
                model.put("percentComplete", percentComplete);
                model.put("workComplete", assn.getWork());
                model.put("workRemaining", TimeQuantity.O_HOURS);
            } else {
                percentComplete = nf.formatPercent(0.0, 2);
                
                model.put("work", null);
                model.put("percentComplete", percentComplete);
                model.put("workComplete", null);
                model.put("workRemaining", TimeQuantity.O_HOURS);
            }
            
            //update the values for that column
            model.put("dateLongName", dateLongName);
            model.put("dateLongNameWork", null);
        }
        
        passThru(model, "fromTimesheet");
        model.put("donterase", "true");
        model.put("errorReporter", errorReporter);

        model.put("spaceId", request.getParameter("spaceID"));
        model.put("isNonWorkingDayWorkCapture", isNonWorkingDayWorkCapture);
        model.put("isWork24DayWorkCapture", isWork24DayWorkCapture);
    }

    private void updateWorkModel(Map model, String percentComplete, TimeQuantity assnWork, TimeQuantity currentWork) {
        //sjmittal: -ive work entries are allowed but if work or work complete beocmes < 0
        //then we have a problem!!
        if(assnWork.compareTo(TimeQuantity.O_HOURS) < 0 || currentWork.compareTo(TimeQuantity.O_HOURS) < 0) {
            errorReporter.addError(new ErrorDescription(textId, PropertyProvider.get("prm.resource.updatework.total.negative.error.message")));
        }        
        model.put("work", assnWork);
        model.put("percentComplete", percentComplete);
        model.put("workComplete", currentWork);
        model.put("workRemaining", assnWork.subtract(currentWork));
    }

    private TimeQuantity getCurrentWork(List workCompleteStrings, NumberFormat nf, TimeQuantity assnWork) throws ParseException {
        for (Iterator it = workCompleteStrings.iterator(); it.hasNext();) {
            String work = (String) it.next();
            assnWork = assnWork.add(new TimeQuantity(nf.parseNumber(work), TimeQuantityUnit.HOUR));
        }
        return assnWork;
    }

    private String getPercentComplete(TimeQuantity currentWork, TimeQuantity assnWork, NumberFormat nf) {
        String percentComplete;

        if (!assnWork.isZero()) {
            double percentDecimal = currentWork.divide(assnWork, 4, BigDecimal.ROUND_HALF_UP).doubleValue();
            percentDecimal = (percentDecimal > 1.0 ? 1.0 : percentDecimal);
            percentComplete = nf.formatPercent(percentDecimal, 2);
        } else {
            percentComplete = nf.formatPercent(1.0, 2);
        }

        return percentComplete;
    }
    
    /**
     *  Method to check whether the day is working or non working day
     *  and update the model with non working day validation message
     *  
     * @param planID, scheduleID create working time calendar of schedule
     * @param workDate, date on which work is captured
     * @param model,  model to handle request values
     * @param newWorkForDay, new work for complete day
     * @param oldWorkForDay, old work for complete day
     * @param percentComplete, percent complete value after calculations
     * @param assnWork, actual assigned work to selected assignment 
     * @param currentWork, current work that needs to be updated in request model
     * @return true if work captured day is non working day
     */
    private boolean isNonWorkingDay(String planID, Date workDate, Map model, TimeQuantity newWorkForDay, 
    				TimeQuantity oldWorkForDay, String percentComplete, TimeQuantity assnWork, TimeQuantity currentWork) {
    	boolean isNonWorkingDayWorkCapture = false;
    	DateFormat df = DateFormat.getInstance();
    	
    	WorkingTimeCalendarDefinition def = calendarProvider.getForPlanID(planID);
        IWorkingTimeCalendar cal = new DefinitionBasedWorkingTimeCalendar(calendarProvider.getDefaultTimeZone(), def);
        
        // checking for the token to capture work on non working day before updating the percent complete
        boolean checkForNonWorkingDay = PropertyProvider.getBoolean("prm.resource.assignments.workcaptureonnonworkingday.isenabled");
        if (checkForNonWorkingDay && !cal.isWorkingDay(workDate)) {
        	if(newWorkForDay.subtract(oldWorkForDay).abs().compareTo(new TimeQuantity(0, TimeQuantityUnit.HOUR)) == 0){
        		isNonWorkingDayWorkCapture = false;
        		updateWorkModel(model, percentComplete, assnWork, currentWork);
        	} else {
        		isNonWorkingDayWorkCapture = true;
        		errorReporter.addError(new ErrorDescription(textId, PropertyProvider.get("prm.resource.timesheet.update.error.invalidtime.message", df.formatDate(workDate))));
        		updateWorkModel(model, percentComplete, assnWork, currentWork);
        	}
        } else {
        	updateWorkModel(model, percentComplete, assnWork, currentWork);
        }
    	return isNonWorkingDayWorkCapture;
    }
}
