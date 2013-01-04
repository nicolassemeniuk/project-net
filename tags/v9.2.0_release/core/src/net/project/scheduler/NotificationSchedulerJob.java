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

import java.util.HashMap;

import net.project.base.compatibility.Compatibility;
import net.project.base.compatibility.modern.LocalSessionProvider;
import net.project.base.property.PropertyException;
import net.project.base.property.PropertyProvider;
import net.project.notification.NotificationException;
import net.project.notification.NotificationScheduler;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class NotificationSchedulerJob implements StatefulJob {

    /**
     * Checks for user notifications that are ready for delivery.  
     * Creates individual notifications for future delivery by the Postman.
     * 
     * <p> Called by the <code>{@link org.quartz.Scheduler}</code> when a
     * <code>{@link org.quartz.Trigger}</code> fires that is associated with the
     * <code>Job</code>. </p>
     *
     * <p> The implementation may wish to set a {@link org.quartz.JobExecutionContext#setResult(Object)
     * result} object on the {@link org.quartz.JobExecutionContext} before this
     * method exits.  The result itself is meaningless to Quartz, but may be
     * informative to <code>{@link org.quartz.JobListener}s</code> or
     * <code>{@link org.quartz.TriggerListener}s</code> that are watching the
     * job's execution. </p>
     *
     * @throws org.quartz.JobExecutionException if there is an exception while
     * executing the job.
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
    	Logger.getLogger(NotificationSchedulerJob.class).debug("Readying Subscriptions for delivery via NotificationScheduler...");
 
    	try {
    		PropertyProvider.loadDefaultContext();
            NotificationScheduler notificationScheduler = new NotificationScheduler();
            notificationScheduler.schedule();
        } catch (Exception e) {
        	Logger.getLogger(NotificationSchedulerJob.class).debug("Error executing NotificationSchedulerJob", e);
            throw new JobExecutionException(e);
        }
    }
}
