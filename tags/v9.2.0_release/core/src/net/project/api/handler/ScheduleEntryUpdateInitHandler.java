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

 /*----------------------------------------------------------------------+
|                                                                       
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.api.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.api.XMLView;
import net.project.api.model.ScheduleCache;
import net.project.api.model.ScheduleEntryCache;
import net.project.api.model.ScheduleEntrySelector;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.persistence.PersistenceException;
import net.project.schedule.TaskPriority;
import net.project.security.AuthorizationFailedException;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.xml.document.XMLDocument;

/**
 * A handler that selects schedule entries for update.
 * <p>
 * They are added to an application-scoped cache.
 * </p>
 * @see ScheduleEntryCache
 * @author Tim Morrow
 * @since Version 7.6.4
 */
public class ScheduleEntryUpdateInitHandler extends Handler implements IGatewayHandler {

    /** All task priorities as a list to aid in getting a random one. */
    private static final List TASK_PRIORITY_LIST = new ArrayList();
    static {
        TASK_PRIORITY_LIST.addAll(TaskPriority.getAll());
    }

    /** The current servlet context used for storing cached data. */
    private final ServletContext context;

    private IView view = new ScheduleEntryUpdateXMLView();

    public ScheduleEntryUpdateInitHandler(HttpServletRequest request, ServletContext servletContext) {
        super(request);
        this.context = servletContext;
    }

    public String getViewName() {
        return null;
    }

    public IView getView() {
        return this.view;
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request)
            throws AuthorizationFailedException, PnetException {

        // Always succeed

    }

    /**
     * Reads and processes the XML from the request body.
     * <p/>
     * Assumes it is <code>ScheduleEntryUpdate</code> XML.
     * </p>
     * 
     * @param request  
     * @param response 
     * @return a <code>content</code> element containing XML content
     * @throws IOException      
     * @throws ServletException 
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Map model = new HashMap();

        try {
            // Determine a manager personID
            String managerPersonID = PersonProvider.getPersonProvider(this.context).checkOutPersonID();

            // Load 300 tasks for that person adding them to the
            // application-scoped cache
            ScheduleEntrySelector scheduleEntrySelector = new ScheduleEntrySelector(managerPersonID, ScheduleCache.get(this.context), ScheduleEntryCache.get(this.context));
            scheduleEntrySelector.load();

            model.put("scheduleEntrySelector", scheduleEntrySelector);

        } catch (PersistenceException e) {
            throw new ServletException("Error getting tasks to update: " + e, e);

        }

        return model;
    }

    /**
     * Provides a view of the update XML.
     */
    private static class ScheduleEntryUpdateXMLView extends XMLView implements IView {

        /**
         * Returns the update XML as a string.
         * @return the XML string
         */
        protected String getContent(Map model) {

            ScheduleEntrySelector scheduleEntrySelector = (ScheduleEntrySelector) model.get("scheduleEntrySelector");

            XMLDocument doc = new XMLDocument();
            Random random = new Random();

            doc.startElement("ScheduleEntryUpdate");
            doc.addElement("PersonID", scheduleEntrySelector.getPersonID());

            for (Iterator it = scheduleEntrySelector.getScheduleEntryUpdates().iterator(); it.hasNext();) {
                ScheduleEntrySelector.ScheduleEntryUpdate nextUpdate = (ScheduleEntrySelector.ScheduleEntryUpdate) it.next();

                doc.startElement("ScheduleEntry");

                // Information about the schedule entry to update
                doc.addElement("SpaceID", nextUpdate.getSpaceID());
                doc.addElement("ScheduleID", nextUpdate.getScheduleID());
                doc.addElement("ScheduleEntryID", nextUpdate.getScheduleEntryID());

                // Now the things to change
                doc.addElement("PriorityID", getRandomTaskPriority(random).getID());

                doc.startElement("Work");
                TimeQuantity work = getRandomWork(random);
                doc.addElement("Amount", work.getAmount());
                doc.addElement("UnitID", String.valueOf(work.getUnits().getUniqueID()));
                doc.endElement();

                doc.startElement("WorkComplete");
                TimeQuantity workComplete = getRandomWorkComplete(random, work);
                doc.addElement("Amount", workComplete.getAmount());
                doc.addElement("UnitID", String.valueOf(workComplete.getUnits().getUniqueID()));
                doc.endElement();

                // TODO add assignments modification

                doc.endElement();

            }

            doc.endElement();
            doc.setPrettyFormat(false);

            return doc.getXMLString();
        }

        /**
         * Returns a random TaskPriority.
         * @param random the random number generator to use.
         * @return the task priority
         */
        private TaskPriority getRandomTaskPriority(Random random) {
            return (TaskPriority) TASK_PRIORITY_LIST.get(random.nextInt(TASK_PRIORITY_LIST.size()));
        }

        /**
         * Returns a TimeQuantity set to a random amount of work between
         * 8 and 40 hours.
         * @param random the random generator to use
         * @return the time quantity for work
         */
        private TimeQuantity getRandomWork(Random random) {
            return new TimeQuantity(random.nextInt(33) + 8, TimeQuantityUnit.HOUR);
        }

        /**
         * Returns a random amount of work complete which is between 0 and the
         * specified amount of work.
         * @param random the random generator to use
         * @param work the maximum amount of work
         * @return the work complete time quantity
         */
        private TimeQuantity getRandomWorkComplete(Random random, TimeQuantity work) {
             return new TimeQuantity(random.nextInt(work.getAmount().intValue() + 1), TimeQuantityUnit.HOUR);
        }

    }

}
