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

 package net.project.scheduler;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.project.base.PnetRuntimeException;
import net.project.notification.Event;
import net.project.notification.NotificationManager;
import net.project.util.Validator;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/*
 * This servlet is called once at application server startup.   
 * It is responsible for setting up the quartz timer jobs used the project.net application.
 */
public class SchedulerSetupServlet extends GenericServlet {
    public void init() throws ServletException {
        super.init();
        try {
            schedulerStart();
        } catch (SchedulerException e) {
            throw new ServletException(e);
        } catch (PnetRuntimeException e) {
            throw new ServletException(e);
        }
    }

    public static void schedulerStart() throws SchedulerException {
        
        CronTrigger minuteTrigger1 = null;
        CronTrigger minuteTrigger2 = null;
        CronTrigger hourlyTrigger = null;
        CronTrigger secondsTrigger = null;
        
        Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
        sched.start();
        
        //Delete old Quartz jobs from persistence.  We don't want to persist any job state.
        Logger.getLogger(SchedulerSetupServlet.class).info("Startup:  deleting old quartz jobs.");
        sched.deleteJob("NotificationManager", Scheduler.DEFAULT_GROUP);
        sched.deleteJob("NotificationScheduler", Scheduler.DEFAULT_GROUP);
        sched.deleteJob("PostmanAgent", Scheduler.DEFAULT_GROUP);
        sched.deleteJob("DeleteOrphanedClobs", Scheduler.DEFAULT_GROUP);

        
        //Check for user-disabled notifications (intended for developer debugging only).
        //Use JVM option –Drun.pnet.scheduler=false
        String runProperty = System.getProperty("run.pnet.scheduler");
        if (!Validator.isBlankOrNull(runProperty) && runProperty.equalsIgnoreCase("false")) {
            Logger.getLogger(SchedulerSetupServlet.class).warn("NOTIFICATIONS OFF:  JVM option –Drun.pnet.scheduler=false.  Not starting scheduled notifications.");
        } else {
            try {
            	// Run every 10 minutes
            	minuteTrigger1 = new CronTrigger("MinuteJobsTrigger1", Scheduler.DEFAULT_GROUP, "0 0,10,20,30,40,50 * * * ?");
                // Run every minute
            	minuteTrigger2 = new CronTrigger("MinuteJobsTrigger2", Scheduler.DEFAULT_GROUP, "0 * * * * ?");
                // Run every hour
            	hourlyTrigger = new CronTrigger("HourlyJobsTrigger", Scheduler.DEFAULT_GROUP, "0 0 * * * ?");
                // Run every 15 seconds
            	secondsTrigger = new CronTrigger("secondsTrigger", Scheduler.DEFAULT_GROUP, "15 * * * * ?");
            } catch (ParseException e) {
                throw new PnetRuntimeException("Unable to set up scheduled quartz jobs.", e);
            }

            Logger.getLogger(SchedulerSetupServlet.class).info("Startup:  submitting new quartz jobs.");
            
            // Converts application events into scheduled notifications to be sent in the future.
			JobDetail notificationManagerJobDetail = new JobDetail("NotificationManager", Scheduler.DEFAULT_GROUP, NotificationManager.AsynchronousNotificationJob.class);
			JobDataMap notificationManagerDataMap = notificationManagerJobDetail.getJobDataMap();
			notificationManagerDataMap.put("event", new LinkedList<Event>());
			sched.scheduleJob(notificationManagerJobDetail, secondsTrigger);
            
            //Converts scheduled notifications in to notification that are ready to be sent based on the date-time and the user's notification preferences.
            sched.scheduleJob(new JobDetail("NotificationScheduler", Scheduler.DEFAULT_GROUP, NotificationSchedulerJob.class), minuteTrigger2);

            //Delivers actual email notifications that are ready to be sent.
            sched.scheduleJob(new JobDetail("PostmanAgent", Scheduler.DEFAULT_GROUP, PostmanAgentJob.class), minuteTrigger1);
            
            //Deletes orphaned notification clobs that have been left behind due to deliver exceptions, etc.
            sched.scheduleJob(new JobDetail("DeleteOrphanedClobs", Scheduler.DEFAULT_GROUP, DeleteOrphanedNotificationClobJob.class), hourlyTrigger);
		}
	}

    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
    }

    public String getServletInfo() {
        return null;
    }
}
