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

 package net.project.schedule.mvc;

import javax.servlet.http.HttpServletRequest;

import net.project.base.mvc.ControllerException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.HandlerMapping;
import net.project.schedule.mvc.handler.baseline.AddTaskToBaselineHandler;
import net.project.schedule.mvc.handler.baseline.BaselineCreateHandler;
import net.project.schedule.mvc.handler.baseline.BaselineCreateProcessingHandler;
import net.project.schedule.mvc.handler.baseline.BaselineListHandler;
import net.project.schedule.mvc.handler.baseline.BaselineModifyHandler;
import net.project.schedule.mvc.handler.baseline.BaselineRefreshPlanHandler;
import net.project.schedule.mvc.handler.baseline.BaselineRefreshTaskHandler;
import net.project.schedule.mvc.handler.baseline.BaselineRemoveHandler;
import net.project.schedule.mvc.handler.baseline.BaselineViewHandler;
import net.project.schedule.mvc.handler.gantt.StoreSettings;
import net.project.schedule.mvc.handler.main.ApplyFiltersHandler;
import net.project.schedule.mvc.handler.main.ApplyInconsistentTaskFiltersHandler;
import net.project.schedule.mvc.handler.main.ApplyInconsistentTaskWorkFiltersHandler;
import net.project.schedule.mvc.handler.main.CreateFromExternalHandler;
import net.project.schedule.mvc.handler.main.DeleteTaskHandler;
import net.project.schedule.mvc.handler.main.IndentHandler;
import net.project.schedule.mvc.handler.main.LinkTasksHandler;
import net.project.schedule.mvc.handler.main.ModifyPermissionsHandler;
import net.project.schedule.mvc.handler.main.PercentageChangeHandler;
import net.project.schedule.mvc.handler.main.QuickAddHandler;
import net.project.schedule.mvc.handler.main.RecalculateScheduleHandler;
import net.project.schedule.mvc.handler.main.ResequenceScheduleHandler;
import net.project.schedule.mvc.handler.main.SetPhaseHandler;
import net.project.schedule.mvc.handler.main.ShareHandler;
import net.project.schedule.mvc.handler.main.TaskDownHandler;
import net.project.schedule.mvc.handler.main.TaskUpHandler;
import net.project.schedule.mvc.handler.main.UnindentHandler;
import net.project.schedule.mvc.handler.main.UnlinkTasksHandler;
import net.project.schedule.mvc.handler.properties.SharingHandler;
import net.project.schedule.mvc.handler.properties.TaskListDecoratingProcessing;
import net.project.schedule.mvc.handler.taskcalculate.AssignmentAddRemoveHandler;
import net.project.schedule.mvc.handler.taskcalculate.AssignmentModifyPercentHandler;
import net.project.schedule.mvc.handler.taskcalculate.AssignmentModifyWorkHandler;
import net.project.schedule.mvc.handler.taskcalculate.ConstraintChangeHandler;
import net.project.schedule.mvc.handler.taskcalculate.DateChangeHandler;
import net.project.schedule.mvc.handler.taskcalculate.DurationChangeHandler;
import net.project.schedule.mvc.handler.taskcalculate.WorkChangeHandler;
import net.project.schedule.mvc.handler.taskcalculate.WorkCompleteChangeHandler;
import net.project.schedule.mvc.handler.taskcalculate.WorkPercentCompleteChangeHandler;
import net.project.schedule.mvc.handler.taskedit.CreateFromExternalProcessingHandler;
import net.project.schedule.mvc.handler.taskedit.MoreDependenciesHandler;
import net.project.schedule.mvc.handler.taskedit.RecalculateHandler;
import net.project.schedule.mvc.handler.taskedit.TaskCreateHandler;
import net.project.schedule.mvc.handler.taskedit.TaskModifyAssignmentsHandler;
import net.project.schedule.mvc.handler.taskedit.TaskModifyHandler;
import net.project.schedule.mvc.handler.taskeditprocessing.TaskCreateProcessingHandler;
import net.project.schedule.mvc.handler.taskeditprocessing.TaskModifyProcessingHandler;
import net.project.schedule.mvc.handler.tasklist.AssignResourcesDialogHandlerProcessing;
import net.project.schedule.mvc.handler.taskview.FixOverallocationsHandler;
import net.project.schedule.mvc.handler.taskview.FixOverallocationsProcessingHandler;
import net.project.schedule.mvc.handler.taskview.TaskAdvancedHandler;
import net.project.schedule.mvc.handler.taskview.TaskAssignmentHandler;
import net.project.schedule.mvc.handler.taskview.TaskAssignmentProcessingHandler;
import net.project.schedule.mvc.handler.taskview.TaskDependencyHandler;
import net.project.schedule.mvc.handler.taskview.TaskHistoryHandler;
import net.project.schedule.mvc.handler.taskview.TaskMaterialAssignmentHandler;
import net.project.schedule.mvc.handler.taskview.TaskFinancialHandler;
import net.project.schedule.mvc.handler.taskview.TaskViewHandler;
import net.project.schedule.mvc.handler.workingtime.WorkingTimeCreateHandler;
import net.project.schedule.mvc.handler.workingtime.WorkingTimeEditDateHandler;
import net.project.schedule.mvc.handler.workingtime.WorkingTimeEditHandler;
import net.project.schedule.mvc.handler.workingtime.WorkingTimeListHandler;
import net.project.schedule.mvc.handler.workingtime.WorkingTimeViewHandler;
import net.project.util.Validator;

/**
 * This class is responsible for finding the correct handler for any request
 * that has been submitted to the ScheduleController.
 *
 * @author Matthew Flower
 * @since Version 7.6
 */
class ScheduleHandlerMapping extends HandlerMapping {

    protected Handler doGetHandler(HttpServletRequest request) throws ControllerException {
        String pathInfo = request.getPathInfo();
        if (Validator.isBlankOrNull(pathInfo)) {
            pathInfo = request.getParameter("handlerName");
        }
        if (Validator.isBlankOrNull(pathInfo)) {
            pathInfo = request.getParameter("theAction");
        }

        if (pathInfo.equalsIgnoreCase("/taskedit")) {
            return new TaskModifyHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskEdit/Assignments")) {
            return new TaskModifyAssignmentsHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/taskcreate")) {
            return new TaskCreateHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/taskeditprocessing/Create")) {
            return new TaskCreateProcessingHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/taskeditprocessing/Modify")) {
            return new TaskModifyProcessingHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskEditProcessing/Recalculate")) {
            return new RecalculateHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskEditProcessing/MoreDependencies")) {
            return new MoreDependenciesHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskEditProcessing/RefreshBaselineTask")) {
            return new BaselineRefreshTaskHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskEditProcessing/AddTaskToBaseline")) {
            return new AddTaskToBaselineHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskView")) {
            return new TaskViewHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskView/Assignments")) {
            return new TaskAssignmentHandler(request);
            //@nicolas added
        } else if (pathInfo.equalsIgnoreCase("/TaskView/Materials")) {
            return new TaskMaterialAssignmentHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskView/Financial")) {
            return new TaskFinancialHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskView/AssignmentsProcessing")) {
            return new TaskAssignmentProcessingHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskView/AssignmentModifyPercent")) {
            return new AssignmentModifyPercentHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskView/AssignmentModifyWork")) {
            return new AssignmentModifyWorkHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskView/Dependencies")) {
            return new TaskDependencyHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskView/Advanced")) {
            return new TaskAdvancedHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskView/History")) {
            return new TaskHistoryHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskView/FixOverallocations")) {
            return new FixOverallocationsHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskView/FixOverallocationsProcessing")) {
            return new FixOverallocationsProcessingHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskList/AssignResourcesDialogProcessing")) {
            return new AssignResourcesDialogHandlerProcessing(request);
        } else if (pathInfo.equalsIgnoreCase("/WorkingTime/List")) {
            return new WorkingTimeListHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/WorkingTime/View")) {
            return new WorkingTimeViewHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/WorkingTime/Edit")) {
            return new WorkingTimeEditHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/WorkingTime/Create")) {
            return new WorkingTimeCreateHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/WorkingTime/EditDate")) {
            return new WorkingTimeEditDateHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/ScheduleProperties/TaskListDecoratingProcessing")) {
            return new TaskListDecoratingProcessing(request);
        } else if (pathInfo.equalsIgnoreCase("/ScheduleProperties/Sharing")) {
            return new SharingHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/Baseline/Create")) {
            return new BaselineCreateHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/Baseline/CreateProcessing")) {
            return new BaselineCreateProcessingHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/Baseline/List")) {
            return new BaselineListHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/Baseline/View")) {
            return new BaselineViewHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/Baseline/Modify")) {
            return new BaselineModifyHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/Baseline/Remove")) {
            return new BaselineRemoveHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/Baseline/RefreshBaselinePlan")) {
            return new BaselineRefreshPlanHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/LinkTasks")) {
            return new LinkTasksHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/UnlinkTasks")) {
            return new UnlinkTasksHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/Recalculate")) {
            return new RecalculateScheduleHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/Resequence")) {
            return new ResequenceScheduleHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/Indent")) {
            return new IndentHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/Unindent")) {
            return new UnindentHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/TaskUp")) {
            return new TaskUpHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/TaskDown")) {
            return new TaskDownHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/Percentage")) {
            return new PercentageChangeHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/SetPhase")) {
            return new SetPhaseHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/ApplyFilters")) {
            return new ApplyFiltersHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/FindInconsistentTaskFilters")) {
            return new ApplyInconsistentTaskFiltersHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/FindInconsistentTaskWorkFilters")) {
            return new ApplyInconsistentTaskWorkFiltersHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/DeleteTask")) {
            return new DeleteTaskHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/ModifyPermissions")) {
            return new ModifyPermissionsHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/QuickAdd")) {
            return new QuickAddHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/MainProcessing/Share")) {
            return new ShareHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/Sharing/CreateFromExternal")) {
            return new CreateFromExternalHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/Sharing/CreateFromExternalProcessing")) {
            return new CreateFromExternalProcessingHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskCalculate/DurationChange")) {
            return new DurationChangeHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskCalculate/WorkChange")) {
            return new WorkChangeHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskCalculate/WorkCompleteChange")) {
            return new WorkCompleteChangeHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskCalculate/WorkPercentCompleteChange")) {
            return new WorkPercentCompleteChangeHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskCalculate/AssignmentAddRemove")) {
            return new AssignmentAddRemoveHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskCalculate/DateChange")) {
            return new DateChangeHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/TaskCalculate/ConstraintChange")) {
            return new ConstraintChangeHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/Gantt/StoreSettings")) {
            return new StoreSettings(request);
        } else {
            throw new ControllerException("Unrecognized schedule module: " +pathInfo);
        }
    }
}
