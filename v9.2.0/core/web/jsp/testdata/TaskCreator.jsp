<%--
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
--%>

<%@ page
    info="Creates lots of tasks"
    import="net.project.schedule.Task,
            net.project.security.SessionManager,
            net.project.space.Space,
            net.project.schedule.TaskDependency,
            net.project.schedule.PredecessorList,
            java.util.Random,
            net.project.schedule.TaskDependencyType,
            java.util.Date,
            net.project.calendar.PnCalendar,
            java.util.GregorianCalendar,
            net.project.schedule.TaskConstraintType,
            net.project.schedule.ScheduleEntry,
            net.project.schedule.ScheduleFinder,
            net.project.schedule.Schedule,
            net.project.util.TimeQuantity,
            net.project.util.TimeQuantityUnit"
%>
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />
<%
    int tasksToCreate = Integer.valueOf(request.getParameter("numberToCreate")).intValue();
    Space currentSpace = SessionManager.getUser().getCurrentSpace();
    String[] taskIDs = new String[tasksToCreate];
    Random randomID = new Random();
    Date todaysDate = new Date();
    PnCalendar cal = new PnCalendar();

    //If the schedule isn't loaded, load it
    if (!schedule.isLoaded()) {
        ScheduleFinder sf = new ScheduleFinder();
        schedule = (Schedule)sf.findBySpace(currentSpace).get(0);
        session.setAttribute("schedule", schedule);
    }

    //Create bunches of tasks.
    for (int i = 0; i < tasksToCreate; i++) {
        //Reinitialize the calendar
        cal.setTime(todaysDate);

        ScheduleEntry t;
        t = new Task();
        t.setPlanID(schedule.getID());
        t.setName("AutoTask "+i);
        cal.add(PnCalendar.DATE, randomID.nextInt(200));
        t.setStartTimeD(cal.getTime());
        cal.add(PnCalendar.DATE, 5);
        t.setEndTimeD(cal.getTime());
        t.setPriority(String.valueOf(randomID.nextInt(2)*10+10));
        t.setConstraintType(TaskConstraintType.AS_SOON_AS_POSSIBLE);
        t.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        t.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));

        //Create some random dependencies
        int dependenciesToCreate = Math.min(3, i-1);
        PredecessorList dl = new PredecessorList();
        for (int y = 0; y < dependenciesToCreate; y++) {
            TaskDependency td = new TaskDependency();
            td.setDependencyID(taskIDs[randomID.nextInt(i-1)]);
            td.setDependencyType(TaskDependencyType.FINISH_TO_START);
            dl.add(td);
        }
        t.setPredecessors(dl);

        //Store the task in the database
        t.store();

        //Store the task id so we can refer to it later
        taskIDs[i] = t.getID();
    }
%>
Created <%=tasksToCreate%> tasks.