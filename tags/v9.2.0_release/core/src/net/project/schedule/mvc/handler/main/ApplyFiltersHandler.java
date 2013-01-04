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

 package net.project.schedule.mvc.handler.main;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.finder.DateFilter;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.FilterOperator;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.NumberComparator;
import net.project.base.finder.NumberFilter;
import net.project.base.finder.TextComparator;
import net.project.base.finder.TextFilter;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.schedule.Schedule;
import net.project.schedule.TaskFinder;
import net.project.schedule.TaskType;
import net.project.schedule.filters.UserIDFilter;
import net.project.schedule.report.CriticalPathFilter;
import net.project.schedule.report.LateTaskFilter;
import net.project.schedule.report.OverallocatedResourceFilter;
import net.project.schedule.report.PhaseFilter;
import net.project.schedule.report.TaskTypeFilter;
import net.project.schedule.report.TasksAssignedToMeFilter;
import net.project.schedule.report.TasksComingDueFilter;
import net.project.schedule.report.TasksStartedAfterPlannedStartFilter;
import net.project.schedule.report.TasksThatShouldHaveStartedFilter;
import net.project.schedule.report.UnassignedTasksFilter;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.InvalidDateException;
import net.project.util.NumberFormat;
import net.project.util.Validator;

/**
 * This handler applies filters to the workplan so only tasks that fit a certain
 * criteria will be shown.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class ApplyFiltersHandler extends MainProcessingHandler {
    /**
     * This method determines if any specific type should be loaded and if so
     * as the appropriate filter to load the type.
     *
     * @param request a <code>HttpServletRequest</code> that contains the
     * variables that were passed to this handler.
     */
    private void appendTypeFilters(HttpServletRequest request, FinderFilterList filters, ErrorReporter errorReporter) {
        boolean showAllTasks = (request.getParameter("showAllTasks") != null ? request.getParameter("showAllTasks").equals("true") : false);
        boolean showLateTasks = (request.getParameter("showLateTasks") != null ? request.getParameter("showLateTasks").equals("true") : false);
        boolean showWithOverAllocatedResources = (request.getParameter("showWithOverAllocatedResources") != null ? request.getParameter("showWithOverAllocatedResources").equals("true") : false);
        boolean showComingDue = (request.getParameter("showComingDue") != null ? request.getParameter("showComingDue").equals("true") : false);
        boolean showAssignedToUser = (request.getParameter("showAssignedToUser") != null ? request.getParameter("showAssignedToUser").equals("true") : false);
        boolean showOnCriticalPath = (request.getParameter("showOnCriticalPath") != null ? request.getParameter("showOnCriticalPath").equals("true") : false);
        boolean unassignedTasksFilterFlag = (request.getParameter("showUnassigned") != null ? request.getParameter("showUnassigned").equals("true") : false);
        boolean showShouldHaveStarted = (request.getParameter("showShouldHaveStarted") != null ? request.getParameter("showShouldHaveStarted").equals("true") : false);
        boolean showStartedAfterPlannedStart = (request.getParameter("showStartedAfterPlannedStart") != null ? request.getParameter("showStartedAfterPlannedStart").equals("true") : false);

        if (!showAllTasks) {
            FinderFilterList typeFilters = new FinderFilterList("typeFilters", FilterOperator.AND);
            typeFilters.setSelected(true);

            try {
                if (showLateTasks) {
                    LateTaskFilter lateTasksFilters = new LateTaskFilter("showLateTasks");
                    lateTasksFilters.setSelected(true);
                    typeFilters.add(lateTasksFilters);
                }

                if (showWithOverAllocatedResources) {
                    OverallocatedResourceFilter overallocatedFilter = new OverallocatedResourceFilter("showWithOverAllocatedResources");
                    overallocatedFilter.setSelected(true);
                    typeFilters.add(overallocatedFilter);
                }

                if (showComingDue) {
                    TasksComingDueFilter tcdf = new TasksComingDueFilter("showComingDue");
                    tcdf.setSelected(true);
                    typeFilters.add(tcdf);
                }

                if (showAssignedToUser) {
                    String userId = request.getParameter("assignedUser");
                    if(userId != null) {
                        UserIDFilter tasksAssignedToUserFilter = new UserIDFilter("showAssignedToUser", true, userId);
                        tasksAssignedToUserFilter.setSelected(true);
                        typeFilters.add(tasksAssignedToUserFilter);
                    } else {
                        TasksAssignedToMeFilter tasksAssignedToMeFilter = new TasksAssignedToMeFilter("showAssignedToUser");
                        tasksAssignedToMeFilter.setSelected(true);
                        typeFilters.add(tasksAssignedToMeFilter);
                    }
                }

                if (showOnCriticalPath) {
                    CriticalPathFilter criticalTasksFilter = new CriticalPathFilter("showOnCriticalPath");
                    criticalTasksFilter.setSelected(true);
                    typeFilters.add(criticalTasksFilter);
                }

                if (unassignedTasksFilterFlag) {
                    UnassignedTasksFilter unassignedTasksFilter = new UnassignedTasksFilter("showUnassigned");
                    unassignedTasksFilter.setSelected(true);
                    typeFilters.add(unassignedTasksFilter);
                }
                
                if (showShouldHaveStarted) {
                	TasksThatShouldHaveStartedFilter shouldHaveStartedFilter = new TasksThatShouldHaveStartedFilter("showShouldHaveStarted");
                	shouldHaveStartedFilter.setSelected(true);
                	typeFilters.add(shouldHaveStartedFilter);
                }
                
                if (showStartedAfterPlannedStart) {
                    TasksStartedAfterPlannedStartFilter startedAfterPlannedStartFilter = 
                	new TasksStartedAfterPlannedStartFilter("showStartedAfterPlannedStart");
                    startedAfterPlannedStartFilter.setSelected(true);
                    typeFilters.add(startedAfterPlannedStartFilter);
                }

                filters.add(typeFilters);
            } catch (DuplicateFilterIDException e) {
                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.schedule.main.filter.duplicate.error.message", typeFilters.getID())));
            }
        }
    }

    /**
     * Ensure that the requester has proper rights to access this object.  For
     * objects that aren't excluded from security checks, this will just
     * consist of verifying that the parameters that were used to access this
     * page were correct (that is, that the requester didn't try to "spoof it"
     * by using a module and action they have permission to.)
     *
     * @param module the <code>int</code> value representing the module that
     * was passed to security.
     * @param action the <code>int</code> value that was passed through security
     * for the action.
     * @param objectID the <code>String</code> value for objectID that was
     * passed through security.
     * @param request the entire request that was submitted to the schedule
     * controller.
     * @throws net.project.security.AuthorizationFailedException if the user didn't have the proper
     * credentials to view this page, or if they tried to spoof security.
     * @throws net.project.base.PnetException if any other error occurred.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        AccessVerifier.verifyAccess(String.valueOf(Module.SCHEDULE), String.valueOf(Action.VIEW));
    }

    /**
     * Apply filter that limit the start or end date of tasks.
     *
     * @param request a <code>HttpServletRequest</code> object that contains the
     * parameters that we need to populate the filters.
     * @param errorReporter a <code>ErrorReporter</code> through which we can
     * return any errors that we find.
     */
    private void appendDateFilters(HttpServletRequest request, FinderFilterList filters, ErrorReporter errorReporter) {
        DateFilter startDateFilter = new DateFilter("startDateFilter", TaskFinder.DATE_START_COLUMN, false);
        DateFilter endDateFilter = new DateFilter("endDateFilter", TaskFinder.DATE_FINISH_COLUMN, false);

        DateFormat df = SessionManager.getUser().getDateFormatter();
        PnCalendar cal = new PnCalendar();

        if (!Validator.isBlankOrNull(request.getParameter("startDateFilterStart"))) {
            try {
                startDateFilter.setDateRangeStart(df.parseDateString(request.getParameter("startDateFilterStart")));
                startDateFilter.setSelected(true);
            } catch (InvalidDateException e) {
                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.personal.assignments.invalidstartdate.message", request.getParameter("startDateFilterStart"))));
            }
        }

        if (!Validator.isBlankOrNull(request.getParameter("startDateFilterEnd"))) {
            try {
                Date startDateRangeEnd = df.parseDateString(request.getParameter("startDateFilterEnd"));

                //If the start range is 1/1/04 - 1/1/04 (as an example), you'd
                //want anything that starts on 1/1 to qualify.  Unless we adjust
                //the date to the end of the day, it wouldn't.
                startDateRangeEnd = cal.endOfDay(startDateRangeEnd);

                startDateFilter.setDateRangeFinish(startDateRangeEnd);
                startDateFilter.setSelected(true);
            } catch (InvalidDateException e) {
                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.personal.assignments.invalidstartdate.message", request.getParameter("startDateFilterEnd"))));
            }
        }

        try {
            String endDateFilterStart = request.getParameter("endDateFilterStart");
            if (!Validator.isBlankOrNull(endDateFilterStart)) {
                endDateFilter.setDateRangeStart(df.parseDateString(endDateFilterStart));
                endDateFilter.setSelected(true);
            }
        } catch (InvalidDateException e) {
            errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.personal.assignments.invalidenddate.message", request.getParameter("endDateFilterStart"))));
        }
        try {
            String parameter = request.getParameter("endDateFilterEnd");
            if (!Validator.isBlankOrNull(parameter)) {
                Date endDateRangeEnd = df.parseDateString(parameter);

                //See comment above for the startDateRangeEnd.
                endDateRangeEnd = cal.endOfDay(endDateRangeEnd);

                endDateFilter.setDateRangeFinish(endDateRangeEnd);
                endDateFilter.setSelected(true);
            }
        } catch (InvalidDateException e) {
            errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.personal.assignments.invalidenddate.message", request.getParameter("endDateFilterEnd"))));
        }

        try {
            filters.add(startDateFilter);
        } catch (DuplicateFilterIDException e) {
            errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.schedule.main.filter.duplicate.error.message", startDateFilter.getID())));
        }
        try {
            filters.add(endDateFilter);
        } catch (DuplicateFilterIDException e) {
            errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.schedule.main.filter.duplicate.error.message", endDateFilter.getID())));
        }
    }

    /**
     * Apply all filters which I don't have a category for.
     *
     * @param request a <code>HttpServletRequest</code> object that contains the
     * parameters that we need to populate the filters.
     * @param errorReporter a <code>ErrorReporter</code> through which we can
     * return any errors that we find.
     */
    private void appendMiscellaneousFilters(HttpServletRequest request, FinderFilterList filters, ErrorReporter errorReporter) {
        String phaseID = request.getParameter("selectedPhaseID");

        //PhaseID Filter
        if (!Validator.isBlankOrNull(phaseID)) {
            PhaseFilter pf = new PhaseFilter("selectedPhaseID");
            pf.setPhaseID(request.getParameterValues("selectedPhaseID"));
            pf.setSelected(true);

            try {
                filters.add(pf);
            } catch (DuplicateFilterIDException e) {
                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.schedule.main.filter.duplicate.error.message", pf.getID())));
            }
        }

        //TaskName Filter
        if (!Validator.isBlankOrNull(request.getParameter("taskName"))) {
            TextFilter tf = new TextFilter("taskName", TaskFinder.NAME_COLUMN, false);
            tf.setValue(request.getParameter("taskName"));
            tf.setComparator(TextComparator.getForID(request.getParameter("taskNameComparator")));
            tf.setSelected(true);

            try {
                filters.add(tf);
            } catch (DuplicateFilterIDException e) {
                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.schedule.main.filter.duplicate.error.message", tf.getID())));
            }

        }

        //Work percentage complete filter
        if (!Validator.isBlankOrNull(request.getParameter("workPercentComplete"))) {
            //Construct the filter for work complete
            NumberFilter workPCFilter = new NumberFilter("workPercentCompleteFilter", TaskFinder.WORK_PERCENT_COMPLETE_COLUMN, false);
            workPCFilter.setComparator(NumberComparator.getForID(request.getParameter("workPercentCompleteComparator")));
            
            NumberFilter pcFilter = new NumberFilter("percentCompleteFilter", TaskFinder.PERCENT_COMPLETE_COLUMN, false);
            pcFilter.setComparator(NumberComparator.getForID(request.getParameter("workPercentCompleteComparator")));
            
            //Parse the requested percent complete
            try {
                Number percentCompleteRequested = NumberFormat.getInstance().parseNumber(request.getParameter("workPercentComplete"));

                workPCFilter.setNumber(percentCompleteRequested);
                workPCFilter.setSelected(true);
                pcFilter.setNumber(percentCompleteRequested);
                pcFilter.setSelected(true);
                
                //sjmittal: now parse rest of the filters 
                TaskTypeFilter tasksOnly = new TaskTypeFilter("tasksOnly");
                tasksOnly.setSelected(true);
                tasksOnly.setLoadMilestones(false);
                tasksOnly.setLoadTasks(true);

                NumberFilter workNotZeroFilter = new NumberFilter("workNotZeroFilter", TaskFinder.WORK_COLUMN, false);
                workNotZeroFilter.setSelected(true);
                workNotZeroFilter.setComparator(NumberComparator.GREATER_THAN);
                workNotZeroFilter.setNumber(0);

                FinderFilterList sub = new FinderFilterList("sub", FilterOperator.OR);
                sub.setSelected(true);
                sub.add(tasksOnly);
                sub.add(workNotZeroFilter);

                FinderFilterList taskPCFilter = new FinderFilterList("taskPCFilter", FilterOperator.AND);
                taskPCFilter.setSelected(true);
                taskPCFilter.add(workPCFilter);
                taskPCFilter.add(sub);

                //Milestones don't always have a work percent complete.  If this one
                //doesn't, we'll use duration percent complete.
                TaskTypeFilter milestonesOnly = new TaskTypeFilter("milestonesOnly");
                milestonesOnly.setSelected(true);
                milestonesOnly.setLoadMilestones(true);
                milestonesOnly.setLoadTasks(true);

                NumberFilter workZeroFilter = new NumberFilter("workZeroFilter", TaskFinder.WORK_COLUMN, false);
                workZeroFilter.setSelected(true);
                workZeroFilter.setComparator(NumberComparator.EQUALS);
                workZeroFilter.setNumber(0);

                FinderFilterList milestonePCFilter = new FinderFilterList("milestonePCFilter", FilterOperator.AND);
                milestonePCFilter.setSelected(true);
                milestonePCFilter.add(milestonesOnly);
                milestonePCFilter.add(pcFilter);
                milestonePCFilter.add(workZeroFilter);

                try {
                    FinderFilterList percentCompleteFilter = new FinderFilterList("percentCompleteFilter", FilterOperator.OR);
                    percentCompleteFilter.setSelected(true);
                    percentCompleteFilter.add(taskPCFilter);
                    percentCompleteFilter.add(milestonePCFilter);

                    filters.add(percentCompleteFilter);
                } catch (DuplicateFilterIDException e) {
                    errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.schedule.main.filter.duplicate.error.message", pcFilter.getID())));
                }

            } catch (ParseException e) {
                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.schedule.main.filter.invalidpercent.error.message", request.getParameter("workPercentComplete"))));
            }
        }

        if (!Validator.isBlankOrNull(request.getParameter("type"))) {
            TaskTypeFilter taskTypeFilter = new TaskTypeFilter("type");

            String taskType = request.getParameter("type");

            if (taskType.equals(TaskType.MILESTONE.getID())) {
                taskTypeFilter.setLoadMilestones(true);
                taskTypeFilter.setLoadTasks(false);
                taskTypeFilter.setSelected(true);
            } else if (taskType.equals(TaskType.TASK.getID())) {
                taskTypeFilter.setLoadMilestones(false);
                taskTypeFilter.setLoadTasks(true);
                taskTypeFilter.setSelected(true);
            } else {
                taskTypeFilter.setLoadMilestones(true);
                taskTypeFilter.setLoadTasks(true);
                taskTypeFilter.setSelected(false);
            }

            try {
                filters.add(taskTypeFilter);
            } catch (DuplicateFilterIDException e) {
                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.schedule.main.filter.duplicate.error.message", taskTypeFilter.getID())));
            }
        }
    }

    public ApplyFiltersHandler(HttpServletRequest request) {
        super(request);
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
        FinderFilterList filters = new FinderFilterList();

        Map model = new HashMap();
        passThru(model, "module");
        model.put("action", String.valueOf(Action.VIEW));
        ErrorReporter errorReporter = new ErrorReporter();
//        model.put("errorReporter", errorReporter);
        Schedule schedule = (Schedule)getSessionVar("schedule");
        model.put("showFiltersDropDown", "true");

        filters.clear();
        filters.setSelected(true);

        //First, deal with the All Task/Late Tasks/Overallocated Resources, etc
        appendTypeFilters(request, filters, errorReporter);

        //Second, deal with filters that apply to dates
        appendDateFilters(request, filters, errorReporter);

        //Finally, deal with filters the don't fit into a particular category
        appendMiscellaneousFilters(request, filters, errorReporter);

        schedule.setFinderFilterList(filters);

        request.getSession().setAttribute("workspaceFilterListSpaceID", SessionManager.getUser().getCurrentSpace().getID());
        request.getSession().setAttribute("workspaceFilterList", filters);
        request.getSession().setAttribute("errorReporter", errorReporter);

        return model;
    }
}

