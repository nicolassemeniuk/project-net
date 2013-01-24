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
|    $Revision: 19063 $
|        $Date: 2009-04-05 14:27:40 -0300 (dom, 05 abr 2009) $
|      $Author: nilesh $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.api.handler;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.api.StatusView;
import net.project.api.model.ScheduleCache;
import net.project.api.model.ScheduleEntryCache;
import net.project.api.model.Stats;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentList;
import net.project.resource.AssignmentStatus;
import net.project.resource.Roster;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskPriority;
import net.project.security.AuthorizationFailedException;
import net.project.space.GenericSpace;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;
import net.project.xml.XMLException;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * A handler that updates schedule entries.
 * 
 * @author Tim Morrow
 * @since Version 7.6.4
 */
public class ScheduleEntryUpdateHandler extends Handler implements IGatewayHandler {

    private static final Logger logger = Logger.getLogger(ScheduleEntryUpdateHandler.class);

    /** The name of the roster cache attribute. */
    private static final String ROSTER_CACHE_ATTRIBUTE_NAME = "net.project.api.handler.RosterCache";

    /** The current servlet context used for storing cached data. */
    private final ServletContext context;

    public ScheduleEntryUpdateHandler(HttpServletRequest request, ServletContext servletContext) {
        super(request);
        this.context = servletContext;
    }

    public String getViewName() {
        return null;
    }

    public IView getView() {
        return StatusView.OK;
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {

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
     * @return request items to provide to the view
     * @throws IOException      
     * @throws ServletException 
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        logger.info("Begin");
        long start = System.currentTimeMillis();

        try {

            // Build JDOM structure from request content
            Document doc;
            try {
                String xml = request.getParameter("xml");
                doc = new SAXBuilder().build(new StringReader(xml));
            } catch (JDOMException e) {
                throw new ServletException("Error reading XML: " + e, e);
            } catch (IOException ioe) {
                throw new ServletException("Error reading XML: " + ioe, ioe);
            }

            // Process XML
            try {
                processScheduleEntryUpdate(doc.getRootElement());

            } catch (XMLException e) {
                logger.warn("Error reading XML", e);
                throw new ServletException("Error processing XML: " + e, e);

            } catch (PersistenceException e) {
                logger.warn("Error storing schedule entries", e);
                throw new ServletException("Error loading or storing schedule entries: " + e, e);
            }

        } catch (RuntimeException e) {
            logger.warn("Error in update handler", e);
            throw e;

        } finally {
            long end = System.currentTimeMillis() - start;

            // Log the elapsed time with the statistics
            Stats.get(this.context).logUpdate(end);

            logger.info("End. Total Execution time: " + end);

        }

        return Collections.EMPTY_MAP;
    }

    /**
     * Checks that the element has the expected name.
     * 
     * @param expectedName the name the element should have
     * @param element      the element who's name must match
     * @throws XMLException if the element's name does not match
     */
    private static void checkElementName(String expectedName, Element element) throws XMLException {
        if (!element.getName().equals(expectedName)) {
            throw new XMLException("Expected element named '" + expectedName + "', found '" + element.getName() + "'");
        }
    }

    /**
     * Process all the elements for the schedule entry update XML.
     * 
     * @param rootElement the ScheduleEntryUpdate element
     * @throws XMLException         if there is a problem with the XML
     * @throws PersistenceException if there is a problem loading or storing schedule entries
     */
    private void processScheduleEntryUpdate(Element rootElement) throws XMLException, PersistenceException {

        checkElementName("ScheduleEntryUpdate", rootElement);

        String personID = rootElement.getChildTextTrim("PersonID");
        if (Validator.isBlankOrNull(personID)) {
            throw new XMLException("Missing or empty element PersonID");
        }

        // Grab all the ScheduleEntry children
        Collection scheduleEntryElements = rootElement.getChildren("ScheduleEntry");

        DBBean db = new DBBean();
        try {

            db.setAutoCommit(false);

            for (Iterator it = scheduleEntryElements.iterator(); it.hasNext();) {
                Element nextScheduleEntryElement = (Element) it.next();
                processScheduleEntryElement(db, nextScheduleEntryElement);
            }

            db.commit();

        } catch (SQLException e) {
            throw new PersistenceException("Error updating schedule entries: " + e, e);

        } finally {
            db.release();

        }

    }

    /**
     * Process the ScheduleEntry element.
     * 
     * @param db                   the DBBean in which to perform the store transaction
     * @param scheduleEntryElement the task element to processScheduleEntryUpdate
     * @throws XMLException         if the task element is missing a required child element
     * @throws PersistenceException if there is a problem loading storing the task
     */
    private void processScheduleEntryElement(DBBean db, Element scheduleEntryElement) throws XMLException, PersistenceException {

        checkElementName("ScheduleEntry", scheduleEntryElement);

        // Grab the values from the element
        String spaceID = scheduleEntryElement.getChildTextTrim("SpaceID");
        if (Validator.isBlankOrNull(spaceID)) {
            throw new XMLException("Missing element to content SpaceID");
        }

        String scheduleID = scheduleEntryElement.getChildTextTrim("ScheduleID");
        if (Validator.isBlankOrNull(scheduleID)) {
            throw new XMLException("Missing element or content ScheduleID");
        }

        String scheduleEntryID = scheduleEntryElement.getChildTextTrim("ScheduleEntryID");
        if (Validator.isBlankOrNull(scheduleEntryID)) {
            throw new XMLException("Missing element or content ScheduleEntryID");
        }

        TaskPriority priority = constructTaskPriority(scheduleEntryElement.getChild("PriorityID"));
        TimeQuantity work = constructTimeQuantity(scheduleEntryElement.getChild("Work"));
        TimeQuantity workComplete = constructTimeQuantity(scheduleEntryElement.getChild("WorkComplete"));

        //
        // Load, update and store the scheduleEntry
        //

        // Fetch schedule entry (from cache)
        ScheduleEntry scheduleEntry = fetchScheduleEntry(scheduleEntryID);

        // Set all the updated values
        if (priority != null) {
            scheduleEntry.setPriority(priority);
        }
        if (work != null) {
            scheduleEntry.setWork(work);
        }
        if (workComplete != null) {
            scheduleEntry.setWorkComplete(workComplete);
        }

        // Now process any assignments
        // TODO - Not tested at all; currently XML has no Assignments elements
        processAssignmentsElement(scheduleEntryElement.getChild("Assignments"), scheduleEntry, spaceID);

        try {
            scheduleEntry.setSendNotifications(false);
            scheduleEntry.store(false, getSchedule(scheduleID), db);

        } catch (SQLException e) {
            throw new PersistenceException("Error storing scheduleEntry with id " + scheduleEntryID + ": " + e, e);
        }

    }

    /**
     * Fetches the schedule entry for the specified ID.
     * @param scheduleEntryID the ID of the schedule entry to fetch
     * @return the schedule entry for the matching ID
     * @throws IllegalArgumentException if a schedule entry with the ID is not
     * found in the cache
     */
    private ScheduleEntry fetchScheduleEntry(String scheduleEntryID) {

        ScheduleEntryCache cache = ScheduleEntryCache.get(this.context);

        ScheduleEntry scheduleEntry = cache.get(scheduleEntryID);

        if (scheduleEntry == null) {
            logger.warn("Schedule entry with id " + scheduleEntryID + " not found in cache.  Cache contains " + cache.size() + " entries.");
            throw new IllegalArgumentException("Schedule entry with id " + scheduleEntryID + " not found in cache.  Cache contains " + cache.size() + " entries.");
        }

        return scheduleEntry;
    }

    /**
     * Parses a TaskPriority element and returns the TaskPriority.
     * <p/>
     * If the specified element is null then a null value is returned.
     * </p>
     * 
     * @param taskPriorityElement the element that is the TaskPriority
     * @return the TaskPriority or null
     * @throws XMLException if the element is specified but is missing content
     */
    private TaskPriority constructTaskPriority(Element taskPriorityElement)
            throws XMLException {

        TaskPriority priority;

        if (taskPriorityElement == null) {
            priority = null;

        } else {
            String priorityID = taskPriorityElement.getTextTrim();
            if (Validator.isBlankOrNull(priorityID)) {
                throw new XMLException("Missing content in Priority element");
            }

            priority = TaskPriority.getForID(priorityID);
        }

        return priority;
    }

    /**
     * Parses a TimeQUantity element and returns the TimeQuantity.
     * <p/>
     * If the specified element is null then a null value is returned.
     * </p>
     * 
     * @param timeQuantityElement the element that is the time quantity
     * @return the TimeQuantity or null
     * @throws XMLException if the element is specified but is missing a required
     *                      child element
     */
    private TimeQuantity constructTimeQuantity(Element timeQuantityElement)
            throws XMLException {

        TimeQuantity timeQuantity;

        if (timeQuantityElement == null) {
            timeQuantity = null;

        } else {
            String amount = timeQuantityElement.getChildTextTrim("Amount");
            if (Validator.isBlankOrNull(amount)) {
                throw new XMLException("Missing or empty TimeQuantity element Amount");
            }

            String unitID = timeQuantityElement.getChildTextTrim("UnitID");
            if (Validator.isBlankOrNull(unitID)) {
                throw new XMLException("Missing or empty TimeQuantity element Unit");
            }

            timeQuantity = new TimeQuantity(new BigDecimal(amount), TimeQuantityUnit.getForID(unitID));
        }

        return timeQuantity;
    }

    /**
     * Process an Assignments element (if not null).
     * <p/>
     * Adds the assignment to the schedule entry's assignment list
     * Makes no assignments changes if the element is null.
     * </p>
     * 
     * @param assignmentsElement the assignments element
     * @param scheduleEntry      the schedule entry who's assignments to update
     * @param spaceID            the space in which this schedule entry exists
     * @throws XMLException if there is a problenm reading the XML
     */
    private void processAssignmentsElement(Element assignmentsElement, ScheduleEntry scheduleEntry, String spaceID) throws XMLException {

        if (assignmentsElement == null) {
            // Do nothing

        } else {

            checkElementName("Assignments", assignmentsElement);

            AssignmentList assignmentList = scheduleEntry.getAssignmentList();
            for (Iterator it = assignmentsElement.getChildren("Assignment").iterator(); it.hasNext();) {
                Element nextElement = (Element) it.next();
                assignmentList.addAssignment(constructAssignment(nextElement, scheduleEntry, spaceID));
            }

            // TODO Need to do this?
            assignmentList.recalculateAssignmentWork(scheduleEntry.getWorkTQ());
        }

    }

    /**
     * Constructs a schedule entry assignment from the assignment element.
     * 
     * @param assignmentElement 
     * @param scheduleEntry     
     * @param spaceID           the id of the space
     * @return the assignment
     * @throws XMLException if there is a missing required element, invalid
     *                      percentage or the resource does not belong to the space
     */
    private ScheduleEntryAssignment constructAssignment(Element assignmentElement, ScheduleEntry scheduleEntry, String spaceID) throws XMLException {

        checkElementName("Assignment", assignmentElement);

        String resourceID = assignmentElement.getChildTextTrim("ResourceID");
        if (Validator.isBlankOrNull(resourceID)) {
            throw new XMLException("Missing or empty content Assignment element ResourceID");
        }

        String allocationPercentage = assignmentElement.getChildTextTrim("AllocationPercentage");
        if (Validator.isBlankOrNull(allocationPercentage)) {
            throw new XMLException("Missing or empty content Assignment element AllocationPercentage");
        }

        int percentAssigned;
        try {
            percentAssigned = ScheduleEntryAssignment.parsePercentAssigned(allocationPercentage);
        } catch (ParseException e) {
            throw new XMLException("Invalid percent value: " + allocationPercentage);
        }
        if (!ScheduleEntryAssignment.isValidPercentAssigned(percentAssigned)) {
            throw new XMLException("Percent assigned must be between 0 and 1000: " + allocationPercentage);
        }

        // Check the resource is from this space
        Roster roster = getRoster(spaceID);
        if (roster.getPerson(resourceID) == null) {
            throw new XMLException("Resource for ID " + resourceID + " not found in roster for space " + spaceID);
        }

        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
        assignment.setSpaceID(spaceID);
        assignment.setPersonID(resourceID);
        assignment.setObjectID(scheduleEntry.getID());
        assignment.setPercentAssigned(percentAssigned);
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setPrimaryOwner(true);
        assignment.setStartTime(scheduleEntry.getStartTime());
        assignment.setEndTime(scheduleEntry.getEndTime());
        assignment.setTimeZone(roster.getAnyPerson(assignment.getPersonID()).getTimeZone());

        return assignment;
    }

    /**
     * Returns a schedule for the specified ID from the
     * application-scoped schedule cache.
     * @param scheduleID the ID of the schedule to get
     * @return the Schedule for that ID
     * @throws NullPointerException if the scheduleID is null
     */
    private Schedule getSchedule(String scheduleID) {

        if (scheduleID == null) {
            throw new NullPointerException("scheduleID is required");
        }

        ScheduleCache cache = ScheduleCache.get(this.context);
        Schedule schedule = cache.get(scheduleID);
        if (schedule == null) {
            logger.warn("Schedule with ID " + scheduleID + " not found in cache.  Cache contains " + cache.size() + " items.");
            throw new IllegalArgumentException("Schedule with ID " + scheduleID + " not found in cache.  Cache contains " + cache.size() + " items.");
        }

        return schedule;
    }

    /**
     * Returns a roster for the specified spaceID.
     * 
     * @param spaceID the ID of the space for which to get the roster
     * @return the roster for that spaceID
     * @throws NullPointerException if the spaceID is null
     */
    private Roster getRoster(String spaceID) {

        if (spaceID == null) {
            throw new NullPointerException("spaceID is required");
        }

        Map rosterCache = (Map) this.context.getAttribute(ROSTER_CACHE_ATTRIBUTE_NAME);
        if (rosterCache == null) {
            rosterCache = new HashMap();
            this.context.setAttribute(ROSTER_CACHE_ATTRIBUTE_NAME, rosterCache);
        }

        Roster roster = (Roster) rosterCache.get(spaceID);
        if (roster == null) {
            roster = new Roster();
            roster.setSpace(new GenericSpace(spaceID));
            roster.load();
            rosterCache.put(spaceID, roster);
        }

        return roster;
    }

}
