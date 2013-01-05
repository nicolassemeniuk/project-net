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
package net.project.schedule.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.channel.ScopeType;
import net.project.persistence.PersistenceException;
import net.project.resource.Assignment;
import net.project.resource.AssignmentManager;
import net.project.resource.PersonProperty;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.SummaryTask;
import net.project.schedule.TaskEndpointCalculation;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.InvalidDateException;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

/**
 * <p>
 * This servlet handles an ajax request to updated a Workplan.
 * </p>
 * 
 * @author Carlos Montemuiño
 * @author Sachin Mittal
 */
@SuppressWarnings("serial")
public class WorkplanAction extends HttpServlet {
    private final Log log = LogFactory.getLog(WorkplanAction.class);
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.log.debug("doPost() method: entering to the method.");

        // Prepare the response
        this.log.debug("doPost() method: going to prepare the response.");
        response.setContentType("application/text");
        response.setHeader("Cache-Control", "nocache");

        // Get a Printer to output the response.
        final PrintWriter out = response.getWriter();

        //get some request paremeters based on which if-else branch to go
        String hierarchyView = request.getParameter("viewType");
        String workplanInfo = request.getParameter("workplanInfo");
        String columnView = request.getParameter("columnView");
        String editField = request.getParameter("editField");
        String saveAll = request.getParameter("saveAll");
        String cancelAll = request.getParameter("cancelAll");
        
        // Get the Schedule and user which is in session
        final Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");
        final User user = (User) request.getSession().getAttribute("user");
        
        if (StringUtils.isNotEmpty(hierarchyView)) { 
           schedule.setHierarchyView(Integer.parseInt(hierarchyView));
        } else if(StringUtils.isNotEmpty(columnView)) {
            PersonProperty property = PersonProperty.getFromSession(request.getSession());
            property.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
            String hiddenValue = request.getParameter("hidden");
            String newIndxValue = request.getParameter("newIndex");
            if (StringUtils.isNotEmpty(hiddenValue)) {
                boolean visble = !Boolean.parseBoolean(hiddenValue);
                // Remove any old values
                try {
                    property.replace("prm.schedule.main.column", columnView, String.valueOf(visble));
                    out.write("{success: true}");
                } catch (PersistenceException e) {
                    out.write("{success: false, errors: 'Error performing the action.'}");
                }
            } else if (StringUtils.isNotEmpty(newIndxValue)) {
                // Remove any old values
                try {
                    property.replace("prm.schedule.main.column.position", columnView, newIndxValue);
                    out.write("{success: true}");
                } catch (PersistenceException e) {
                    out.write("{success: false, errors: 'Error performing the action.'}");
                }
            }
        } else if(StringUtils.isNotEmpty(workplanInfo) && "true".equals(workplanInfo)) {
            DateFormat df = SessionManager.getUser().getDateFormatter();
            NumberFormat nf = NumberFormat.getInstance();
            if(StringUtils.isNotEmpty(request.getParameter("all"))){	
                  if("true".equals(request.getParameter("all"))) {
                      try {
                          schedule.loadEntries();
                      } catch (PersistenceException e) {
                          throw new ServletException("Error loading the schedule.", e);                
                      }
                  }
            }
            out.write("{workplanInfo: [");
            out.write(JSONObject.quote(df.formatDate(schedule.getScheduleStartDate())) + ",");  
            out.write(JSONObject.quote(df.formatDate(schedule.getScheduleEndDate())) + ",");
            out.write(JSONObject.quote(nf.formatNumber(schedule.getEntries().size())) + ",");
            TimeQuantity divisor = schedule.getTotalWork();
            if (divisor.getAmount().signum() == 0) {
                divisor = new TimeQuantity(1, divisor.getUnits());
            }
            Object[] params = new Object[] {
                schedule.getTotalWorkComplete().toShortString(0,2),
                schedule.getTotalWork().toShortString(0,2),
                NumberFormat.getInstance().formatPercent(schedule.getTotalWorkComplete().divide(divisor, 2, BigDecimal.ROUND_HALF_UP).doubleValue())
            };
            out.write(JSONObject.quote(PropertyProvider.get("prm.schedule.main.work.value", params)));
            out.write("]}");
        } else if(StringUtils.isNotEmpty(saveAll)) {
            Schedule calcSchedule = (Schedule) request.getSession().getAttribute("calcSchedule");
            if(calcSchedule == null || calcSchedule.getID() != schedule.getID() || calcSchedule.getLoadTime().before(schedule.getLoadTime())) {
                //schedule never cloned silently exist!
                out.write("{success: true}");
            } else {
                String idList = request.getParameter("idList");
                String ids[] = StringUtils.split(idList, ",");
                for (String id : ids) {
                    ScheduleEntry entry = calcSchedule.getEntry(id);
                    if (entry != null) {
                        //catch the excpetion and continue with rest
                        try {
                            entry.store(false, calcSchedule);
                            //sjmittal: store assignments separately
                            //note ther are not stored as part of schedule entry store because
                            //assignments need last saved state which never gets cloned from 
                            //original schedule. not sure cloning this is a good idea so here
                            //we have no option but to store seprately
                            //only problem here is that assignments would get stored in a 
                            //separate db transaction, but at this stage we have no other option
                            for (Iterator it = entry.getAssignments().iterator(); it.hasNext();) {
                                Assignment assignment = (Assignment)it.next();
                                assignment.store();
                            }
                        } catch (PersistenceException e) {
                            this.log.error("doPost() method: Entry could not be persisted.", e);
                        }
                    }
                }
                //store schedule also as start date end dates might have been changed
                try {
                    calcSchedule.store();
                    //load the new schedule and set the old cloned one as null
                    request.getSession().setAttribute("calcSchedule", null);
                    schedule.load();
                } catch (PersistenceException e) {
                    this.log.error("doPost() method: Entry could not be persisted.", e);
                }                
                out.write("{success: true}");
            }
        } else if(StringUtils.isNotEmpty(cancelAll)) {
            //set cloned schedule to null again
            request.getSession().setAttribute("calcSchedule", null);
            out.write("{success: true}");
        } else if(StringUtils.isNotEmpty(editField)) {
            String id = request.getParameter("id");
            ScheduleEntry entry = schedule.getEntry(id);
            String oldValue = request.getParameter("oldValue");
            String newValue = request.getParameter("newValue");
            if(entry != null) {
                Schedule calcSchedule = (Schedule) request.getSession().getAttribute("calcSchedule");
                Schedule clonedSchedule;
                try {
                    clonedSchedule = createClonedSchedule(schedule, calcSchedule);
                    //update the updated cloned schedule with this one
                    request.getSession().setAttribute("calcSchedule", clonedSchedule);
                } catch (PersistenceException e) {
                    this.log.error("doPost() method: It can't perform the action because schedule could not be loaded.", e);
                    throw new ServletException(e);
                }
                //get the cloned entry
                entry = clonedSchedule.getEntry(id);
                // check security first
                try {
                    validateSecurity(Module.SCHEDULE, Action.MODIFY, entry.getID(), user, request);
                } catch (AuthorizationFailedException e) {
                    String errorMessage = PropertyProvider.get("prm.schedule.authentication.modify.error", entry.getName());
                    out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    this.log.error("doPost() method: It can't perform the action because user not authenticated.", e);
                    return;
                } catch (PnetException e) {
                    String errorMessage = PropertyProvider.get("prm.schedule.authentication.modify.error");
                    out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    this.log.error("doPost() method: It can't perform the action because exception thrown while authentication.", e);
                    return;
                }
                //prepare the calculater and calculate various changes
                ScheduleEntryCalculator calculator = new ScheduleEntryCalculator(entry, clonedSchedule.getWorkingTimeCalendarProvider());
                if("name".equals(editField)) {
                    if(newValue != null && StringUtils.isNotEmpty(newValue.trim())) {
                        entry.setName(newValue);
                        out.write("{success: true}");
                    } else {
                        String errorMessage = PropertyProvider.get("prm.schedule.taskedit.name.required.message");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    }
                } else if("startDate".equals(editField)) {
                    if(entry.isFromShare()) {
                        String errorMessage = PropertyProvider.get("prm.schedule.main.indenttasks.warning.sharingreadonly");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    } else if(entry instanceof SummaryTask) {
                        String errorMessage = PropertyProvider.get("prm.schedule.taskedit.summarytask.readonly.message");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    } else {
                        DateFormat format = new DateFormat(user);
                        try {
                            Date date = format.parseDateString(newValue, DateFormat.DEFAULT_DATE_FORMAT);
                            calculator.startDateChanged(clonedSchedule, date, user.getTimeZone());
                            //prepare output
                            out.write(getEditJsonResponse(clonedSchedule));
                        } catch (InvalidDateException e) {
                            String errorMessage = PropertyProvider.get("prm.schedule.taskedit.error.startdate.message", newValue);
                            out.write("{success: false, errors: \"" + errorMessage + "\"}");
                        }
                    }
                } else if("endDate".equals(editField)) {
                    if(entry.isFromShare()) {
                        String errorMessage = PropertyProvider.get("prm.schedule.main.indenttasks.warning.sharingreadonly");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    } else if(entry instanceof SummaryTask) {
                        String errorMessage = PropertyProvider.get("prm.schedule.taskedit.summarytask.readonly.message");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    } else {
                        DateFormat format = new DateFormat(user);
                        try {
                            Date date = format.parseDateString(newValue, DateFormat.DEFAULT_DATE_FORMAT);
                            calculator.endDateChanged(clonedSchedule, date, user.getTimeZone());
                            //prepaere output
                            out.write(getEditJsonResponse(clonedSchedule));
                        } catch (InvalidDateException e) {
                            String errorMessage = PropertyProvider.get("prm.schedule.taskedit.error.enddate.message", newValue);
                            out.write("{success: false, errors: \"" + errorMessage + "\"}");
                        } catch (Exception e) {
                            this.log.error("doPost() method: Excpetion performing the action.", e);                            
                            out.write("{success: false, errors: \"" + e.getLocalizedMessage() + "\"}");
                        }
                    }
                } else if("work".equals(editField)) {
                    if(entry.isFromShare()) {
                        String errorMessage = PropertyProvider.get("prm.schedule.main.indenttasks.warning.sharingreadonly");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    } else if(entry instanceof SummaryTask) {
                        String errorMessage = PropertyProvider.get("prm.schedule.taskedit.summarytask.readonly.message");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    } else {
                        ErrorReporter errorReporter = new ErrorReporter();
                        TimeQuantity work = parseWork(newValue, entry.getWorkTQ().getUnits().getHtmlOptionValue(), errorReporter);
                        if(errorReporter.errorsFound()) {
                            String errorMessage = "";
                            Iterator iter = errorReporter.getErrorDescriptions().iterator();
                            while (iter.hasNext())
                            {
                                ErrorDescription description = (ErrorDescription) iter.next();
                                errorMessage += description.getErrorText();
                                if(iter.hasNext()) 
                                    errorMessage += "\n";
                            }
                            out.write("{success: false, errors: \"" + errorMessage + "\"}");
                        } else if(work == null) {
                            out.write("{success: false, errors: \"Error parsing the work for " + newValue + "\"}");
                        } else if(entry.getWorkCompleteTQ().compareTo(work) > 0) {
                            String errorMessage = PropertyProvider.get("prm.schedule.taskedit.error.toomuchworkcomplete.message");
                            out.write("{success: false, errors: \"" + errorMessage + "\"}");
                        } else {
                            try {
                                boolean existingWorkIsZero = entry.getWorkTQ().isZero();
                                TimeQuantity workComplete = entry.getWorkCompleteTQ();
                                BigDecimal percentComplete = entry.getPercentCompleteDecimal();
                                calculator.workChanged(work);
                                if (existingWorkIsZero) {
                                    calculator.workPercentCompleteChanged(percentComplete);
                                } else {
                                    calculator.workCompleteChanged(workComplete);
                                }
                                new TaskEndpointCalculation().recalculateTaskTimesNoLoad(clonedSchedule);
                                //prepare output
                                out.write(getEditJsonResponse(clonedSchedule));
                            } catch (Exception e) {
                                this.log.error("doPost() method: Excpetion performing the action.", e);                                
                                out.write("{success: false, errors: \"" + e.getLocalizedMessage() + "\"}");
                            }
                        }
                    }
                } else if("duration".equals(editField)) {
                    if(entry.isFromShare()) {
                        String errorMessage = PropertyProvider.get("prm.schedule.main.indenttasks.warning.sharingreadonly");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    } else if(entry instanceof SummaryTask) {
                        String errorMessage = PropertyProvider.get("prm.schedule.taskedit.summarytask.readonly.message");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    } else {
                        ErrorReporter errorReporter = new ErrorReporter();
                        TimeQuantity duration = parseDuration(newValue, entry.getDurationTQ().getUnits().getHtmlOptionValue(), errorReporter);
                        if(errorReporter.errorsFound()) {
                            String errorMessage = "";
                            Iterator iter = errorReporter.getErrorDescriptions().iterator();
                            while (iter.hasNext())
                            {
                                ErrorDescription description = (ErrorDescription) iter.next();
                                errorMessage += description.getErrorText();
                                if(iter.hasNext()) 
                                    errorMessage += "\n";
                            }
                            out.write("{success: false, errors: \"" + errorMessage + "\"}");
                        } else if(duration == null) {
                            out.write("{success: false, errors: \"Error parsing the duration for " + newValue + "\"}");
                        } else {
                            try {
                                calculator.durationChanged(duration);
                                new TaskEndpointCalculation().recalculateTaskTimesNoLoad(clonedSchedule);
                                //prepare output
                                out.write(getEditJsonResponse(clonedSchedule));
                            } catch (Exception e) {
                                this.log.error("doPost() method: Excpetion performing the action.", e);                                
                                out.write("{success: false, errors: \"" + e.getLocalizedMessage() + "\"}");       
                            }
                        }
                    }
                } else if("workComplete".equals(editField)) {
                    if(entry.isFromShare()) {
                        String errorMessage = PropertyProvider.get("prm.schedule.main.indenttasks.warning.sharingreadonly");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    } else if(entry instanceof SummaryTask) {
                        String errorMessage = PropertyProvider.get("prm.schedule.taskedit.summarytask.readonly.message");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    } else {
                        ErrorReporter errorReporter = new ErrorReporter();
                        TimeQuantity workComplete = parseWork(newValue, entry.getWorkCompleteTQ().getUnits().getHtmlOptionValue(), errorReporter);
                        if(errorReporter.errorsFound()) {
                            String errorMessage = "";
                            Iterator iter = errorReporter.getErrorDescriptions().iterator();
                            while (iter.hasNext()) {
                                ErrorDescription description = (ErrorDescription) iter.next();
                                errorMessage += description.getErrorText();
                                if(iter.hasNext()) 
                                    errorMessage += "\n";
                            }
                            out.write("{success: false, errors: \"" + errorMessage + "\"}");
                        } else if(workComplete == null) {
                            out.write("{success: false, errors: \"Error parsing the work complete for " + newValue + "\"}");
                        } else if(workComplete.compareTo(entry.getWorkTQ()) > 0) {
                            String errorMessage = PropertyProvider.get("prm.schedule.taskedit.error.toomuchworkcomplete.message");
                            out.write("{success: false, errors: \"" + errorMessage + "\"}");
                        } else {
                            try {
                                boolean existingWorkIsZero = entry.getWorkTQ().isZero();
                                if (!existingWorkIsZero) {
                                    calculator.workCompleteChanged(workComplete);
                                }
                                new TaskEndpointCalculation().recalculateTaskTimesNoLoad(clonedSchedule);
                                //prepare output
                                out.write(getEditJsonResponse(clonedSchedule));
                            } catch (Exception e) {
                                this.log.error("doPost() method: Excpetion performing the action.", e);                                
                                out.write("{success: false, errors: \"" + e.getLocalizedMessage() + "\"}");
                            }
                        }
                    }
                } else if("workPercentComplete".equals(editField)) {
                    if(entry.isFromShare()) {
                        String errorMessage = PropertyProvider.get("prm.schedule.main.indenttasks.warning.sharingreadonly");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    } else if(entry instanceof SummaryTask) {
                        String errorMessage = PropertyProvider.get("prm.schedule.taskedit.summarytask.readonly.message");
                        out.write("{success: false, errors: \"" + errorMessage + "\"}");
                    } else {
                        ErrorReporter errorReporter = new ErrorReporter();
                        BigDecimal workPercentComplete = parseWorkPercentComplete(newValue, errorReporter);
                        if(errorReporter.errorsFound()) {
                            String errorMessage = "";
                            Iterator iter = errorReporter.getErrorDescriptions().iterator();
                            while (iter.hasNext())
                            {
                                ErrorDescription description = (ErrorDescription) iter.next();
                                errorMessage += description.getErrorText();
                                if(iter.hasNext()) 
                                    errorMessage += "\n";
                            }
                            out.write("{success: false, errors: \"" + errorMessage + "\"}");
                        } else if(workPercentComplete == null) {
                            out.write("{success: false, errors: \"Error parsing the work for " + newValue + "\"}");
                        } else {
                            try {
                                calculator.workPercentCompleteChanged(workPercentComplete);
                                new TaskEndpointCalculation().recalculateTaskTimesNoLoad(clonedSchedule);
                                //prepare output          
                                out.write(getEditJsonResponse(clonedSchedule));
                            } catch (Exception e) {
                                this.log.error("doPost() method: Excpetion performing the action.", e);                                
                                out.write("{success: false, errors: \"" + e.getLocalizedMessage() + "\"}");
                            }
                        }
                    }
                } else {
                    out.write("{success: false, errors: \"Not implemented!\"}");
                }
            } else {
                out.write("{success: false, errors: \"Error finding the schedule entry for id " + id + "\"}");
            }
        } else {
            this.log.error("doPost() method: It can't perform the action because the data sent by POST are not correct. Check 'data' parameter is sent when invoking this servlet.");
            out.write("{success: false, errors: \"Error performing the action.\"}");
        }
        out.close();
    }
    
    private Schedule createClonedSchedule(Schedule schedule, Schedule calcSchedule) throws PersistenceException {

        if (schedule.isFiltered()) {
            //Make sure our current calculation schedule isn't null.
            if (calcSchedule == null || calcSchedule.getID() != schedule.getID() || calcSchedule.getLoadTime().before(schedule.getLoadTime())) {
                calcSchedule = (Schedule) schedule.clone();
                calcSchedule.clearFinderFilterList();
                calcSchedule.loadAll();
            }
            //If we get this far, there is a calculation schedule and it is up to date.
        } else {
            //Make sure our current calculation schedule isn't null.
            if (calcSchedule == null || calcSchedule.getID() != schedule.getID() || calcSchedule.getLoadTime().before(schedule.getLoadTime())) {
                //The schedule isn't filtered -- it could probably be used for calculation
                calcSchedule = (Schedule) schedule.clone();
            }
        }

        //Now we have a proper calculation schedule.  We need to not modify this
        //schedule directory -- always modify a clone.  Loading is expensive,
        //cloning is relatively cheap.  We always need the calc schedule to be
        //pristent.  

        return calcSchedule;
    }

    private BigDecimal parseWorkPercentComplete(String percentCompleteParam, ErrorReporter errorReporter) {
        BigDecimal percentComplete = null;
        try {

            if (Validator.isBlankOrNull(percentCompleteParam)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.percentcomplete.required.message"));
            } else if (Validator.isNegative(percentCompleteParam)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message"));
            } else {
                NumberFormat nf = NumberFormat.getInstance();
                if (!percentCompleteParam.endsWith("%")) {
                    percentCompleteParam = percentCompleteParam + "%";
                }
                percentComplete = new BigDecimal(nf.parsePercent(percentCompleteParam).toString());

            }
        } catch (ParseException e) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.invalidpercentcomplete.message"));
        }
        return percentComplete;
    }

    private TimeQuantity parseWorkComplete(String workCompleteAmountString, String workCompleteUnits, ErrorReporter errorReporter) {
        TimeQuantity workComplete = null;
        try {

            if (Validator.isBlankOrNull(workCompleteAmountString)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.workcomplete.required.message"));

            } else if (Validator.isBlankOrNull(workCompleteUnits)){
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.workcompleteunits.required.message"));
                
            } else if(Validator.isNegative(workCompleteAmountString)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.workcomplete.negative.message"));
                
            } else {
                workComplete = TimeQuantity.parse(workCompleteAmountString, workCompleteUnits);
                
            }
        } catch (ParseException e) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.invalidworkcomplete.message"));
        }
        return workComplete;
    }

    private TimeQuantity parseWork(String workAmountString, String workUnits, ErrorReporter errorReporter) {
        TimeQuantity work = null;
        try {

            if (Validator.isBlankOrNull(workAmountString)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.work.required.message"));

            } else if (Validator.isBlankOrNull(workUnits)){
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.workunits.required.message"));
                
            } else if(Validator.isNegative(workAmountString)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.work.negative.message"));
                
            }else {
                work = TimeQuantity.parse(workAmountString, workUnits);
                
            }
        } catch (ParseException e) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.invalidworkamount.message"));
        }
        return work;
    }
    
    private TimeQuantity parseDuration(String durationAmountString, String durationUnits, ErrorReporter errorReporter) {
        TimeQuantity duration = null;
        try {

            if (Validator.isBlankOrNull(durationAmountString)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.duration.required.message"));

            } else if (Validator.isBlankOrNull(durationUnits)){
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.durationunits.required.message"));
                
            } else if(Validator.isNegative(durationAmountString)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.duration.negative.message"));
                
            } else {
                duration = TimeQuantity.parse(durationAmountString, durationUnits);

            }
        } catch (ParseException e) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.invaliddurationamount.message"));
        }
        return duration;
    }
    
    private String getEditJsonResponse(Schedule schedule) {
        //in format id, startdate, enddate, work, workcomplete, duration , % work complete
        Iterator<ScheduleEntry> entries = schedule.getEntries().iterator();
        DateFormat df = DateFormat.getInstance();
        String responseString = "{success: true, data: [";
        while(entries.hasNext()) {
            ScheduleEntry entry = entries.next();
            String entryResponse = "[" +
                entry.getID() + "," +
                "\"" + df.formatDate(entry.getStartTime(), DateFormat.DEFAULT_DATE_FORMAT) + "\"," +
                "\"" + df.formatDate(entry.getEndTime(), DateFormat.DEFAULT_DATE_FORMAT) + "\"," +
                NumberFormat.formatSimpleNumber(entry.getWorkTQ().getAmount().doubleValue(), 0, 2) + "," +
                NumberFormat.formatSimpleNumber(entry.getWorkCompleteTQ().getAmount().doubleValue(), 0, 2) + "," +
                NumberFormat.formatSimpleNumber(entry.getDurationTQ().getAmount().doubleValue(), 0, 2) + "," +
                NumberFormat.formatSimpleNumber(entry.getWorkPercentCompleteDouble(), 0, 2) +
                "]";
            responseString += entryResponse;
            if(entries.hasNext())
                responseString +=",";
        }
        responseString += "]}";
        
        return responseString;
    }
    
    private void validateSecurity(int module, int action, String objectID, User user, HttpServletRequest request) throws AuthorizationFailedException , PnetException {

        //Verify that the user didn't try to sidestep the intended access method
        //AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW, objectID);

        //We need the assignees for checking security
        AssignmentManager am = new AssignmentManager();
        if (!Validator.isBlankOrNull(objectID)) {
            am.setObjectID(objectID);
            am.loadAssigneesForObject();
        }

        if (!am.isUserInAssignmentList(user.getID())) {
            SecurityProvider sp = (SecurityProvider)request.getSession().getAttribute(("securityProvider"));

            /*
             * Check modify permission if user is not assigned task. A null value is
             * sent as the ObjectId because a role could modify a task if such
             * role had granted some modify access at module level. So, the module
             * access overrides the object access.
             * An exception is thrown if they have no permission.
            */            
            sp.securityCheck(null, Integer.toString(Module.SCHEDULE), Action.MODIFY);
        }
    }

}
