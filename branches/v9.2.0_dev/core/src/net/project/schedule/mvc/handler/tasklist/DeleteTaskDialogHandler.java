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

 package net.project.schedule.mvc.handler.tasklist;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.database.DatabaseUtils;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.SummaryTask;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;

/**
 * This page is the handler implementation for the page that comes up when you
 * click on the resources toolbar button on the workplan.
 *
 * @author Matthew Flower
 * @since Version 8.2.0
 */
public class DeleteTaskDialogHandler extends Handler {
    private String viewName = "/schedule/DeleteTaskDialog.jsp";
    private String isSummary = "no";
    private String deleteTask = "0";
    
    public DeleteTaskDialogHandler(HttpServletRequest request) {
        super(request);
    }

    protected String getViewName() {
        return viewName;
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        Schedule schedule = (Schedule)getSessionVar("schedule");
        Map scheduleEntries = schedule.getEntryMap();

        String[] ids = request.getParameterValues("selected");
        
        if(null == ids){
            ids = StringUtils.split((String) request.getParameter("selectedIds"), ',');
        }

        List newIDList = new ArrayList();
        for (int i = 0; i < ids.length; i++) {
            ScheduleEntry entry = (ScheduleEntry)scheduleEntries.get(ids[i]);
            newIDList.add(ids[i]);
            if (entry instanceof SummaryTask) {
                isSummary = "yes";
            } else {
                isSummary = "no";
            }
        }
        
        model.put("errorMessage", PropertyProvider.get("prm.schedule.sharedtasks.allresources"));
        model.put("module", String.valueOf(Module.SCHEDULE));
        model.put("action", String.valueOf(Action.VIEW));
        model.put("idList", DatabaseUtils.collectionToCSV(newIDList));
        model.put("isSummary", isSummary);
        model.put("deleteTask", deleteTask);

        return model;
    }
}
