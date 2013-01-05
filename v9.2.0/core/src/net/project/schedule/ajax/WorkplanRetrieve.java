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

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.persistence.PersistenceException;
import net.project.resource.PersonalPropertyMap;
import net.project.schedule.Schedule;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * This servlet handles an ajax request to get the Workplan as a nested tree.
 * </p>
 * 
 * @author Carlos Montemuiño
 * @author Sachin Mittal
 */
@SuppressWarnings("serial")
public class WorkplanRetrieve extends HttpServlet {
    private static final Log LOG = LogFactory.getLog(WorkplanRetrieve.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.debug("doPost() method: entering to the method.");
        int range = 0;
        int offset = 0;
        
        if(StringUtils.isNotEmpty(request.getParameter("limit")) && StringUtils.isNotEmpty(request.getParameter("start"))){
        	range = Integer.parseInt(request.getParameter("limit"));
        	offset = Integer.parseInt(request.getParameter("start"));
        }
        if(offset != 0){
        	range = range + offset;
        }	
        if(StringUtils.isEmpty(request.getParameter("all"))){	
        	Cookie cookie = new Cookie("scheduleGridOffset_"+SessionManager.getUser().getID(),""+offset);
        	cookie.setPath(SessionManager.getJSPRootURL()+"/");
        	response.addCookie(cookie);
       	}
        	
        // Prepare the response
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Cache-Control", "nocache");

        // Get a Printer to output the response.
        final PrintWriter out = response.getWriter();

        // Get the Schedule from session
        final Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");

        // Check the Schedule is not null
        if (null == schedule) {
            LOG.error("Error finding the schedule.");
            throw new ServletException("Error finding the schedule.");
        }
        LOG.debug("Found a schedule into session");
        
        String loadAll =  request.getParameter("all");
        if("true".equals(loadAll)) {
            try {
                schedule.loadEntries();
            } catch (PersistenceException e) {
                LOG.error("Error loading the schedule.", e);
                throw new ServletException("Error loading the schedule.", e);                
            }
        }
        
        PersonalPropertyMap propertyMap;
        try {
            propertyMap = new PersonalPropertyMap("prm.schedule.tasklist");
        } catch (PersistenceException e) {
            LOG.error("Error loading the person property.", e);
            throw new ServletException("Error loading the person property.", e);                
        }
        if(range >= schedule.getTaskList().size()){
        	range = schedule.getTaskList().size();
        }
        if(schedule.getTaskList().size() > range){
        	out.write("{\"success\":true,\"total\":"
				+ schedule.getTaskList().size()
				+ ",\"data\":[");
        	out.write(schedule.getJSONForSchedule(propertyMap, offset, range));
        } else{
        	out.write("{\"success\":true,\"total\":"
    				+ schedule.getTaskList().size()
    				+ ",\"data\":[");
            out.write(schedule.getJSONForSchedule(propertyMap, offset, schedule.getTaskList().size()));
        }
        out.write("]}");
        out.close();
    }
}
