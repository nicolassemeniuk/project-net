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
<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/xml; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@ page 
			import="net.project.calendar.ICalendarEntry,
                 net.project.calendar.Meeting,
                 net.project.calendar.PnCalendar,
                 net.project.persistence.PersistenceException,
                 net.project.project.ProjectSpace,
                 net.project.schedule.ScheduleEntry,
                 net.project.security.User,
                 net.project.util.DateFormat,
                 java.util.Calendar,
                 java.util.Collections,
                 java.util.Date,
                 java.util.HashMap,
                 java.util.List,
                 java.util.Set,
                 java.util.Vector, java.util.Comparator" %>
<%
    try {
        User user = net.project.security.SessionManager.getUser();
        PnCalendar calendar = new PnCalendar(user);
        calendar.setSpaceId(user.getID());
        Date date = PnCalendar.currentTime();
        calendar.setTime(date);
        calendar.loadIfNeeded();

        int yearNum = calendar.get(Calendar.YEAR);
        calendar.setStartDate(calendar.startOfYear(yearNum));
        calendar.setEndDate(calendar.endOfYear(yearNum));

        calendar.loadEntries(new String[]{"meeting", "event", "milestone", "task"});
        List calendarEntries = calendar.getEntries();
        
        System.out.println("\n\n\n----------------------------------");
        System.out.println("\ncalendarEntries: " + calendarEntries);
        System.out.println("\ncalendarEntries size: " + calendarEntries.size());
        System.out.println("\n\n\n----------------------------------");

        for (Object entry : calendarEntries) {
            ICalendarEntry iCalendarEntry = (ICalendarEntry) entry;
            System.out.println("\n\n\n----------------------------------");
            System.out.println("iCalendarEntry.getName() = " + iCalendarEntry.getName());
            System.out.println("iCalendarEntry.getType() = " + iCalendarEntry.getType());
            if (iCalendarEntry instanceof Meeting) {
                Meeting meeting = (Meeting) iCalendarEntry;
                System.out.println("meeting.getSpaceID() = " + meeting.getSpaceID());
                ProjectSpace projectSpace = new ProjectSpace(meeting.getSpaceID());
                projectSpace.load();
                System.out.println("projectSpace.getName() = " + projectSpace.getName());
            }
            if (iCalendarEntry instanceof ScheduleEntry) {
                ScheduleEntry scheduleEntry = (ScheduleEntry) iCalendarEntry;
                System.out.println("scheduleEntry.getSpaceID() = " + scheduleEntry.getSpaceID());
                ProjectSpace projectSpace = new ProjectSpace(scheduleEntry.getSpaceID());
                projectSpace.load();
                System.out.println("projectSpace.getName() = " + projectSpace.getName());
            }
        }

        HashMap<String, String> spaceId2Name = new HashMap<String, String>();
        HashMap<String, List<ICalendarEntry>> spaceId2Entries = new HashMap<String, List<ICalendarEntry>>();
        for (Object entry : calendarEntries) {
            ICalendarEntry iCalendarEntry = (ICalendarEntry) entry;
            String spaceId = null;
            if (iCalendarEntry instanceof Meeting) {
                Meeting meeting = (Meeting) iCalendarEntry;
                spaceId = meeting.getSpaceID();
            }
            if (iCalendarEntry instanceof ScheduleEntry) {
                ScheduleEntry scheduleEntry = (ScheduleEntry) iCalendarEntry;
                spaceId = scheduleEntry.getSpaceID();
            }
            if (spaceId != null) {
                ProjectSpace projectSpace = new ProjectSpace(spaceId);
                projectSpace.load();
                spaceId2Name.put(projectSpace.getID(), projectSpace.getName());

                List<ICalendarEntry> list = spaceId2Entries.get(spaceId);
                if (list == null) {
                    list = new Vector<ICalendarEntry>();
                    spaceId2Entries.put(spaceId, list);
                }
                list.add(iCalendarEntry);
            }
        }

        out.println("<schedule>");
        List<String> spaceIds = new Vector<String>();
        spaceIds.addAll(spaceId2Name.keySet());
        Collections.sort(spaceIds);

        DateFormat dateFormat = DateFormat.getInstance();

       	Map<String, List<ScheduleEntry>> priorities = new HashMap<String, List<ScheduleEntry>>();
       	List<String> keys = new ArrayList<String>();
       	
       	for(int i=0; i<spaceIds.size(); i++) {
			String spaceId = spaceIds.get(i);
        	
            List<ICalendarEntry> list = spaceId2Entries.get(spaceId);
            Collections.sort(list, new Comparator<ICalendarEntry>() {
                public int compare(ICalendarEntry o1, ICalendarEntry o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            
            for (int j=0; j<list.size(); j++) {
            	ICalendarEntry iCalendarEntry = list.get(j);
            	if (iCalendarEntry instanceof ScheduleEntry) {
                    ScheduleEntry scheduleEntry = (ScheduleEntry) iCalendarEntry;
                    List<ScheduleEntry> priorityList = priorities.get(scheduleEntry.getPriority().toString());
                    if(priorityList == null) {
                    	priorityList = new ArrayList<ScheduleEntry>();
                    	priorities.put(scheduleEntry.getPriority().toString(), priorityList);
                    	keys.add(scheduleEntry.getPriority().toString());
                    }
                    priorityList.add(scheduleEntry);
                }
            }
        }
       	
       	
       	for(int i=0; i<keys.size(); i++) {
       		List<ScheduleEntry> list = priorities.get(keys.get(i));
       		
       		for(int j=0; j<list.size(); j++) {
       			ScheduleEntry entry = list.get(j);
       			
       			ProjectSpace projectSpace = new ProjectSpace(entry.getSpaceID());
                projectSpace.load();
       			
       			out.println("<task>");
       			out.println("<project>" + projectSpace.getName() + "</project>");
       			out.println("<id>" + entry.getID() + "</id>");
       			out.println("<sequence>" + entry.getSequenceNumber() + "</sequence>");
       			out.println("<name>" + entry.getName() + "</name>");
       			out.println("<description />");
       			out.println("<type>Task</type>");
       			out.println("<isMilestone>0</isMilestone>");
       			out.println("<isSummaryTask>0</isSummaryTask>");
       			out.println("<startDate>" + dateFormat.formatDate(entry.getStartTime(), "M/d/yy") + "</startDate>");
        		out.println("<endDate>" + dateFormat.formatDate(entry.getEndTime(), "M/d/yy") + "</endDate>");
        		out.println("<startTime>" + entry.getStartTimeString() + "</startTime>");
        		out.println("<endTime>" + entry.getEndTimeString() + "</endTime>");
        		out.println("<duration>" + entry.getDurationFormatted() + "</duration>");
        		out.println("<durationInHours />");
        		out.println("<url />");
        		out.println("<startDateTime />");
        		out.println("<endDateTime />");
        		out.println("<work />");
        		out.println("<workUnits />");
        		out.println("<workInHours />");
        		out.println("<workComplete />");
        		out.println("<workCompleteUnits />");
        		out.println("<workString />");
        		out.println("<workCompleteString />");
        		out.println("<workRemainingString>" + entry.getWorkRemaining().formatAmount() + "</workRemainingString>");
        		out.println("<workVariance />");
        		out.println("<workVarianceAmount />");
        		out.println("<durationVariance />");
        		out.println("<durationVarianceAmount />");
        		out.println("<actualStart />");
        		out.println("<actualFinish />");
        		out.println("<priority>" + entry.getPriorityString() + "</priority>");
        		out.println("<priorityString>" + entry.getPriorityString() + "</priorityString>");
        		out.println("<percentComplete />");
        		out.println("<workPercentComplete />");
        		out.println("<dateCreated />");
        		out.println("<dateModified />");
        		out.println("<calculationType />");
        		out.println("<modifiedById />");
        		out.println("<modifiedBy />");
        		out.println("<phase_id />");
        		out.println("<phase_name />");
        		out.println("<parent_task_id />");
        		out.println("<parent_task_name />");
        		out.println("<hierarchy_level>1</hierarchy_level>");
        		out.println("<is_orphan>1</is_orphan>");
        		out.println("<assigneeString />");
        		out.println("<TaskDependencyList count='0'><PopupInfo></PopupInfo><idlist /></TaskDependencyList>");
        		out.println("<TaskConstraint><tooltip>Task Constraint As Soon As Possible</tooltip><type>As Soon As Possible</type><constraintDate /><deadline /></TaskConstraint>");
    			
	        	out.println("</task>");
       		}
       	}
    } catch (PersistenceException e) {
        e.printStackTrace();
    }
    out.println("</schedule>\n");
%>