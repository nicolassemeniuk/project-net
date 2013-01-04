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
package net.project.schedule.mvc.handler.workingtime;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.PnetRuntimeException;
import net.project.base.mvc.Handler;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.persistence.PersistenceException;
import net.project.resource.ResourceWorkingTimeCalendarProvider;
import net.project.schedule.Schedule;
import net.project.security.User;

public abstract class WorkingTimeHandler extends Handler {

    /** the working time calendar provider */
    protected final IWorkingTimeCalendarProvider provider;
    
    public WorkingTimeHandler(HttpServletRequest request) {
        super(request);
        
        String module = request.getParameter("module");
        if(String.valueOf(Module.PERSONAL_SPACE).equals(module)) {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                throw new IllegalStateException("Could not find attribute with name 'user' in session");
            }
            // Grab the provider for the user            
            try {
                provider = ResourceWorkingTimeCalendarProvider.make(user);
            } catch (PersistenceException e) {
                throw new PnetRuntimeException("ResourceWorkingTimeCalendarProvider Could not be initialized:" + e, e);
            }
        } else {
          Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");
          if (schedule == null) {
              throw new IllegalStateException("Could not find attribute with name 'schedule' in session");
          }

          // Grab the provider for the schedule
          provider = schedule.getWorkingTimeCalendarProvider();
        }
    }

}
