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
|    $Revision: 18888 $
|        $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|      $Author: avinash $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.HTMLOption;
import net.project.gui.html.IHTMLOption;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.security.SessionManager;
import net.project.xml.document.XMLDocument;

/**
 * Provides a helper for listing working time calendars.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class WorkingTimeCalendarListHelper implements IXMLPersistence {

    //
    // Static Members
    //

    /**
     * Returns a collection of HTML options for each working time calendar
     * in the specified collection.
     * @param calendars a collection where each element is an <code>WorkingTimeCalendarDefinition</code>
     * @param defaultCalendarID the ID of the default calendar which is used
     * to change the display of the option; when null all options are displayed
     * in the same way
     * @return a collection where each element is an <code>IHTMLOption</code>
     */
    static Collection getWorkingTimeCalendarOptions(Collection calendars, String defaultCalendarID) {
        Collection options = new ArrayList();

        for (Iterator it = calendars.iterator(); it.hasNext();) {
            WorkingTimeCalendarDefinition nextcalendarDefinition = (WorkingTimeCalendarDefinition) it.next();
            options.add(new WorkingTimeCalendarOption(
                    nextcalendarDefinition.getID(),
                    nextcalendarDefinition.getDisplayName(),
                    (defaultCalendarID != null && defaultCalendarID.equals(nextcalendarDefinition.getID()))
            ));
        }

        return options;
    }

    //
    // Instance members
    //

    private IWorkingTimeCalendarProvider provider;


    /**
     * Initialize the helper based on the request.
     * <p>
     * Assumes there is a session attribute called <code>user</code> and <code>schedule</code>.
     * Places the loaded calendar definition in the request using the specified
     * attribute name.
     * </p>
     * @param request the request containing required attributes
     * @throws IllegalStateException if no schedule attribute is found in the session
     */
    public WorkingTimeCalendarListHelper(HttpServletRequest request, IWorkingTimeCalendarProvider provider) {

//        Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");
//        if (schedule == null) {
//            throw new IllegalStateException("Could not find attribute with name 'schedule' in session");
//        }

        // Grab the provider for the schedule
//        provider = schedule.getWorkingTimeCalendarProvider();
        
        this.provider = provider;
    }

    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    /**
     * Returns the XML representation which is all working time calendar definitions.
     * <p>
     * Calendars are ordered first by base calendars, then resource calendars
     * and in name order.
     * </p>
     * @return the xml representation
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    private XMLDocument getXMLDocument() {
        XMLDocument xml = new XMLDocument();

        xml.startElement("WorkingTimeCalendarDefinitions");
        xml.addElement("JSPRootURL", SessionManager.getJSPRootURL());
        xml.addElement("DefaultCalendarID", this.provider.getDefault().getID());

        for (Iterator it = getAllCalendarsOrderedIterator(); it.hasNext();) {
            WorkingTimeCalendarDefinition nextcalendarDefinition = (WorkingTimeCalendarDefinition) it.next();
            xml.addElement(nextcalendarDefinition.getXMLDocument());
        }
        xml.endElement();

        return xml;
    }

    /**
     * Returns an ordered iterator over all calendars.
     * <p>
     * Calendars are ordered with base calendars first then by name.
     * </p>
     * @return an iterator where each element is a <code>WorkingTimeCalendarDefinition</code>
     */
    private Iterator getAllCalendarsOrderedIterator() {
        List sortedCalendars = new ArrayList(this.provider.getAll());
        Collections.sort(sortedCalendars, new BaseFirstNameOrderComparator());
        return sortedCalendars.iterator();
    }

    /**
     * Returns a collection of base calendars ordered by name.
     * @return a collection where each element is a <code>WorkingTimeCalendarDefinition</code>
     */
    private Collection getBaseCalendarsOrdered() {
        List sortedCalendars = new ArrayList(this.provider.getBaseCalendars());
        Collections.sort(sortedCalendars, new BaseFirstNameOrderComparator());
        return sortedCalendars;
    }

    public boolean hasDependentCalendars(String calendarId){
        for (Iterator it = getAllCalendarsOrderedIterator(); it.hasNext();) {
            WorkingTimeCalendarDefinition nextcalendarDefinition = (WorkingTimeCalendarDefinition) it.next();
            if (!nextcalendarDefinition.isBaseCalendar() && nextcalendarDefinition.getParentCalendarID() != null && nextcalendarDefinition.getParentCalendarID().equals(calendarId) )
            	return true;
        }
    	
    	return false;
    }
    
    /**
     * Returns a collection of options suitable for selection as a default
     * calendar.
     * <p>
     * Currently this includes base calendars. If no calendars exist then
     * a default empty-value option is returned.
     * </p>
     * @return an unmodifiable collection where each element is an <code>IHTMLOption</code>
     */
    public Collection getDefaultCalendarOptions() {
        // We don't pass in the current calendar ID since we don't want it
        // to be displayed any differently for the purpose of the selection list
        Collection availableOptions = getWorkingTimeCalendarOptions(getBaseCalendarsOrdered(), null);
        if (availableOptions.isEmpty()) {
            availableOptions = Collections.singleton(new HTMLOption("", PropertyProvider.get("prm.schedule.workingtime.calendarselect.option.none.display")));
        }

        return Collections.unmodifiableCollection(availableOptions);
    }

    /**
     * Returns the default calendar ID.
     * @return the ID of the default calendar
     */
    public String getDefaultCalendarID() {
        return this.provider.getDefault().getID();
    }

    /**
     * Switches the default calendar to the one with the specified ID.
     * @param defaultCalendarID the ID of the new default calendar
     * @throws PersistenceException if there is a problem changing it
     */
    public void changeDefaultCalendar(String defaultCalendarID) throws PersistenceException {
        this.provider.changeDefaultCalendar(defaultCalendarID);
    }

    //
    // Nested top-level classes
    //

    /**
     * Provides a comparator that places base calendars before
     * non-base calendars, otherwise compares display names.
     */
    private static class BaseFirstNameOrderComparator implements Comparator {

        public int compare(Object o1, Object o2) {

            WorkingTimeCalendarDefinition calendarDef1 = (WorkingTimeCalendarDefinition) o1;
            WorkingTimeCalendarDefinition calendarDef2 = (WorkingTimeCalendarDefinition) o2;

            int result;

            if ((calendarDef1.isBaseCalendar() && calendarDef2.isBaseCalendar()) ||
                    (!calendarDef1.isBaseCalendar() && !calendarDef2.isBaseCalendar())) {

                // Both base or non-base; comparison based on display names
                result = calendarDef1.getDisplayName().compareTo(calendarDef2.getDisplayName());

            } else if (calendarDef1.isBaseCalendar()) {
                result = -1;

            } else {
                result = 1;

            }

            return result;
        }

    }

    /**
     * Provides an IHTMLOption for a working time calendar.
     */
    private static class WorkingTimeCalendarOption implements IHTMLOption {

        /**
         * The ID of the working time calendar.
         */
        private final String id;

        /**
         * The display name of the working time calendar.
         */
        private final String name;

        /**
         * Indicates whether this option represents the default
         * working time calendar.
         */
        private final boolean isDefault;

        /**
         * Creates a new option for the specified calendar id and name.
         * @param id the ID of the calendar; this will be the option value
         * @param name the display name of the calendar; this will be the option
         * display
         * @param isDefault true if the calendar for the specified ID
         * is the default calendar.  The default calendar is
         * displayed differently in the option list
         */
        private WorkingTimeCalendarOption(String id, String name, boolean isDefault) {
            this.id = id;
            this.name = name;
            this.isDefault = isDefault;
        }

        public String getHtmlOptionValue() {
            return id;
        }

        public String getHtmlOptionDisplay() {
            String displayValue;

            if (isDefault) {
                displayValue = PropertyProvider.get("prm.schedule.workingtime.view.options.default", name);
            } else {
                displayValue = name;
            }

            return displayValue;
        }
    }

}
