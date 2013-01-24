/**
 * 
 */
package net.project.hibernate.service.impl;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

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
import net.project.hibernate.service.IWorkplanFilterHandler;
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
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.InvalidDateException;
import net.project.util.NumberFormat;
import net.project.util.Validator;

@Service(value="workplanFilterHandler")
public class WorkplanFilterHandlerImpl implements IWorkplanFilterHandler {
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IWorkplanFilterHandler#applyFilter(javax.servlet.http.HttpServletRequest)
	 */
	public void applyFilter(HttpServletRequest request,
							Schedule clonedSchedule,
							boolean showAllTasks,
							boolean showLateTasks,
							boolean showComingDue,
							boolean showUnassigned,
							boolean showAssignedToUser,
							boolean showOnCriticalPath,
							boolean showShouldHaveStarted,
							boolean showStartedAfterPlannedStart,
							String showByAssignedToUser,
							String phaseID,
							String taskName,
							String taskNameComparator,
							String workPercentComplete,
							String workPercentCompleteComparator,
							String taskType,
							String startDateFilterStart,
							String startDateFilterEnd,
							String endDateFilterStart,
							String endDateFilterEnd) {
		FinderFilterList filters = new FinderFilterList();

       
        ErrorReporter errorReporter = new ErrorReporter();
//        model.put("errorReporter", errorReporter);
        filters.clear();
        filters.setSelected(true);

        //First, deal with the All Task/Late Tasks/Overallocated Resources, etc
        appendTypeFilters(showAllTasks,
			        		showLateTasks,
			        		showComingDue,
			        		showUnassigned,
			        		showAssignedToUser,            
			        		showOnCriticalPath,
			        		showShouldHaveStarted,
			        		showStartedAfterPlannedStart,
			        		showByAssignedToUser,
			        		filters, errorReporter);

        //Second, deal with filters that apply to dates
        appendDateFilters(startDateFilterStart,
			        		startDateFilterEnd,
			        		endDateFilterStart,
			        		endDateFilterEnd, 
			        		filters, errorReporter);

        //Finally, deal with filters the don't fit into a particular category
        appendMiscellaneousFilters(phaseID,
					        		taskName,
					        		taskNameComparator,
					        		workPercentComplete,
					        		workPercentCompleteComparator,
					        		taskType,
					        		filters, errorReporter);

        clonedSchedule.setFinderFilterList(filters);

        request.getSession().setAttribute("workspaceFilterListSpaceID", SessionManager.getUser().getCurrentSpace().getID());
        request.getSession().setAttribute("workspaceFilterList", filters);
        request.getSession().setAttribute("errorReporter", errorReporter);
		
	}
	
	 /**
     * This method determines if any specific type should be loaded and if so
     * as the appropriate filter to load the type.
     *
     * @param request a <code>HttpServletRequest</code> that contains the
     * variables that were passed to this handler.
     */
    private void appendTypeFilters(boolean showAllTasks,
									boolean showLateTasks,
									boolean showComingDue,
									boolean showUnassigned,
									boolean showAssignedToUser,
									boolean showOnCriticalPath,
									boolean showShouldHaveStarted,
									boolean showStartedAfterPlannedStart,
									String showByAssignedToUser,
									FinderFilterList filters, ErrorReporter errorReporter) {
        if (!showAllTasks) {
            FinderFilterList typeFilters = new FinderFilterList("typeFilters", FilterOperator.AND);
            typeFilters.setSelected(true);

            try {
                if (showLateTasks) {
                    LateTaskFilter lateTasksFilters = new LateTaskFilter("showLateTasks");
                    lateTasksFilters.setSelected(true);
                    typeFilters.add(lateTasksFilters);
                }

                if (showComingDue) {
                    TasksComingDueFilter tcdf = new TasksComingDueFilter("showComingDue");
                    tcdf.setSelected(true);
                    typeFilters.add(tcdf);
                }

                if (showAssignedToUser) {
                    if(showByAssignedToUser != null) {
                        UserIDFilter tasksAssignedToUserFilter = new UserIDFilter("showAssignedToUser", true, showByAssignedToUser);
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

                if (showUnassigned) {
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
     * Apply filter that limit the start or end date of tasks.
     *
     * @param request a <code>HttpServletRequest</code> object that contains the
     * parameters that we need to populate the filters.
     * @param errorReporter a <code>ErrorReporter</code> through which we can
     * return any errors that we find.
     */
    private void appendDateFilters(String startDateFilterStart,
						    		String startDateFilterEnd,
						    		String endDateFilterStart,
						    		String endDateFilterEnd,
						    		FinderFilterList filters, ErrorReporter errorReporter) {
        DateFilter startDateFilter = new DateFilter("startDateFilter", TaskFinder.DATE_START_COLUMN, false);
        DateFilter endDateFilter = new DateFilter("endDateFilter", TaskFinder.DATE_FINISH_COLUMN, false);

        DateFormat df = SessionManager.getUser().getDateFormatter();
        PnCalendar cal = new PnCalendar();

        if (!Validator.isBlankOrNull(startDateFilterStart)) {
            try {
                startDateFilter.setDateRangeStart(df.parseDateString(startDateFilterStart));
                startDateFilter.setSelected(true);
            } catch (InvalidDateException e) {
                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.personal.assignments.invalidstartdate.message", startDateFilterStart)));
            }
        }

        if (!Validator.isBlankOrNull(startDateFilterEnd)) {
            try {
                Date startDateRangeEnd = df.parseDateString(startDateFilterEnd);

                //If the start range is 1/1/04 - 1/1/04 (as an example), you'd
                //want anything that starts on 1/1 to qualify.  Unless we adjust
                //the date to the end of the day, it wouldn't.
                startDateRangeEnd = cal.endOfDay(startDateRangeEnd);

                startDateFilter.setDateRangeFinish(startDateRangeEnd);
                startDateFilter.setSelected(true);
            } catch (InvalidDateException e) {
                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.personal.assignments.invalidstartdate.message", startDateFilterEnd)));
            }
        }

        try {
            if (!Validator.isBlankOrNull(endDateFilterStart)) {
                endDateFilter.setDateRangeStart(df.parseDateString(endDateFilterStart));
                endDateFilter.setSelected(true);
            }
        } catch (InvalidDateException e) {
            errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.personal.assignments.invalidenddate.message", endDateFilterStart)));
        }
        try {
            if (!Validator.isBlankOrNull(endDateFilterEnd)) {
                Date endDateRangeEnd = df.parseDateString(endDateFilterEnd);

                //See comment above for the startDateRangeEnd.
                endDateRangeEnd = cal.endOfDay(endDateRangeEnd);

                endDateFilter.setDateRangeFinish(endDateRangeEnd);
                endDateFilter.setSelected(true);
            }
        } catch (InvalidDateException e) {
            errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.personal.assignments.invalidenddate.message", endDateFilterEnd)));
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
    private void appendMiscellaneousFilters(String phaseID,
											String taskName,
											String taskNameComparator,
											String workPercentComplete,
											String workPercentCompleteComparator,
											String taskType,
											FinderFilterList filters, ErrorReporter errorReporter) {
        //PhaseID Filter
        if (!Validator.isBlankOrNull(phaseID)) {
            PhaseFilter pf = new PhaseFilter("selectedPhaseID");
            pf.setPhaseID(phaseID);
            pf.setSelected(true);

            try {
                filters.add(pf);
            } catch (DuplicateFilterIDException e) {
                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.schedule.main.filter.duplicate.error.message", pf.getID())));
            }
        }

        //TaskName Filter
        if (!Validator.isBlankOrNull(taskName)) {
            TextFilter tf = new TextFilter("taskName", TaskFinder.NAME_COLUMN, false);
            tf.setValue(taskName);
            tf.setComparator(TextComparator.getForID(taskNameComparator));
            tf.setSelected(true);

            try {
                filters.add(tf);
            } catch (DuplicateFilterIDException e) {
                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.schedule.main.filter.duplicate.error.message", tf.getID())));
            }

        }

        //Work percentage complete filter
        if (!Validator.isBlankOrNull(workPercentComplete)) {
            //Construct the filter for work complete
            NumberFilter workPCFilter = new NumberFilter("workPercentCompleteFilter", TaskFinder.WORK_PERCENT_COMPLETE_COLUMN, false);
            workPCFilter.setComparator(NumberComparator.getForID(workPercentCompleteComparator));
            
            NumberFilter pcFilter = new NumberFilter("percentCompleteFilter", TaskFinder.PERCENT_COMPLETE_COLUMN, false);
            pcFilter.setComparator(NumberComparator.getForID(workPercentCompleteComparator));
            
            //Parse the requested percent complete
            try {
                Number percentCompleteRequested = NumberFormat.getInstance().parseNumber(workPercentComplete);

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
                errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.schedule.main.filter.invalidpercent.error.message", workPercentComplete)));
            }
        }

        if (!Validator.isBlankOrNull(taskType)) {
            TaskTypeFilter taskTypeFilter = new TaskTypeFilter("type");

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
}
