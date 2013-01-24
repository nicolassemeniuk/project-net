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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import net.project.base.property.PropertyProvider;
import net.project.database.DatabaseUtils;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskDependency;
import net.project.security.Action;
import net.project.util.Conversion;
import net.project.util.ErrorReporter;

/**
 * This handler links one or more tasks that appear on the schedule main page.
 */
public class LinkTasksHandler extends MultiItemHandler {
    public LinkTasksHandler(HttpServletRequest request) {
        super(request);
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        passThru(model, "module");
        model.put("action", String.valueOf(Action.VIEW));

        String idList[] = request.getParameterValues("selected");
        if(null == idList){
            idList = StringUtils.split((String) request.getParameter("selectedIds"), ',');
        }
        //Check to make sure there aren't any shared tasks
        Map tasks = schedule.getEntryMap();
        List sharedTasksNames = new ArrayList();
        for (int i = 1; i < idList.length; i++) {
            String taskID = idList[i];
            ScheduleEntry task = (ScheduleEntry)tasks.get(taskID);
            if (task.isFromShare()/* && task.isShareReadOnly()*/) {
                sharedTasksNames.add(task.getNameMaxLength40());
            }
        }
        if (!sharedTasksNames.isEmpty()) {
            ErrorReporter er = new ErrorReporter();
            er.addError(PropertyProvider.get("prm.schedule.main.linktasks.sharereadonly.error", Conversion.toCommaSeparatedString(sharedTasksNames)));
            request.getSession().setAttribute("errorReporter", er);

            //Keep the selection so the user can correct the problem.
            List selectedID = Arrays.asList(idList);
            model.put("selectedList", DatabaseUtils.collectionToCSV(selectedID));
        } else {
            if (idList.length > 0) {
                ErrorReporter er = TaskDependency.linkTasks(idList, schedule);
                request.getSession().setAttribute("errorReporter", er);
            }
        }

        return model;
    }
}
