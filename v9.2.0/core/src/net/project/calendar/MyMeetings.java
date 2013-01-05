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
|    $RCSfile$
|   $Revision: 20568 $
|       $Date: 2010-03-15 10:12:59 -0300 (lun, 15 mar 2010) $
|     $Author: ritesh $
|
|
+----------------------------------------------------------------------*/
package net.project.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.persistence.IXMLPersistence;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.xml.XMLFormatter;
import net.project.xml.XMLUtils;

/**
 * Provides This class is used by the myMeetings.jsp page to provide and XML
 * representation of a users upcoming meetings to be displayed in
 * the myMeetings channel.
 *
 * @author  Mike Brevoort
 * @author Tim Morrow
 * @since Version 1
 */
public class MyMeetings implements java.io.Serializable, net.project.persistence.IXMLPersistence {

    //
    // Static members
    //

    /**
     * The default maximum number of meetings to display, currently <code>100</code>.
     */
    private static final int DEFAULT_MAXIMUM_DISPLAY = 100;

    /**
     * The default day range, currently <code>7</code>.
     */
    private static final int DEFAULT_DAY_RANGE = 7;


    //
    // Instance members
    //

    /**
     * The maximum number of meetings to display.
     */
    private int displayMax = DEFAULT_MAXIMUM_DISPLAY;

    /**
     * The number of days ahead to search for meetings.
     */
    private int dayRange = DEFAULT_DAY_RANGE;

    /**
     * The current user for customizing a calendar.
     */
    private User user = null;

    /**
     * The stylesheet to use for transformation.
     */
    private String stylesheet = null;

    /**
     * The meetings to present.
     */
    private final Collection meetingList = new ArrayList();

    /**
     * Creates a new MyMeetings with a default maximum number of meetings of 100
     * and default day range of 7.
     */
    public MyMeetings() {
        // Do nothing
    }

    /**
     * Sets the current user, required to customize a calendar for their locale
     * and timezone.
     * @param user the current user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Converts the object to XML representation without the XML version tag.
     * The XML is of the standard "channel" XML format.
     *  @return XML representation
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append("<channel>\n");
        xml.append("<table_def>\n");
        xml.append("<col>" + PropertyProvider.get("prm.calendar.main.meetings.list.meeting.label") + "</col>\n");
        xml.append("<col>" + PropertyProvider.get("prm.calendar.main.meetings.list.date.label") + "</col>\n");
        xml.append("<col>" + PropertyProvider.get("prm.calendar.main.meetings.list.start.label") + " (" + this.user.getDateFormatter().formatDate(new Date(),"z") + ") </col>\n");
        xml.append("<col>" + PropertyProvider.get("prm.calendar.main.meetings.list.end.label") + " (" +  this.user.getDateFormatter().formatDate(new Date(),"z") + ") </col>\n");
        xml.append("<col>" + PropertyProvider.get("prm.calendar.main.meetings.list.status.label") + "</col>\n");
        xml.append("</table_def>\n");

        xml.append("<content>\n");

        if (this.meetingList.isEmpty()) {
            // If the user has no upcoming meetings, display a message that says that
            xml.append("<row>\n");
            xml.append("<data_href>\n");
            xml.append("<label>" + PropertyProvider.get("all.global.dashboard.setupanewmeeting.link") + "</label>\n");
            xml.append("<href>"+ SessionManager.getJSPRootURL()+"/calendar/Main.jsp?module=" + Module.CALENDAR +"</href>\n");
            xml.append("</data_href>\n");
            xml.append("<data> </data>\n");
            xml.append("<data> </data>\n");
            xml.append("<data> </data>\n");
            xml.append("<data> </data>\n");
            xml.append("</row>");

        } else {
            // If the user actually has meetings create the XML representation
            // of the table.

            // Ensure we limit the number of meetings to the maximum number
            int count = 0;
            for (Iterator it = meetingList.iterator(); it.hasNext() && (count < this.displayMax); count++) {
                Meeting event = (Meeting) it.next();

                String id = event.getID();
                String name = event.getName();
                String attendeeStatus = event.getCurrentUserAttendeeStatus();

                // We must format the the dates since the XSL stylesheet cannot
                // tell which data items are dates
                String startTimeString = this.user.getDateFormatter().formatTime(event.getStartTime(), "h:mm a");
                String endTimeString = this.user.getDateFormatter().formatTime(event.getEndTime(), "h:mm a");
                String dateString = this.user.getDateFormatter().formatDateMedium(event.getStartTime());

                String href = URLFactory.makeURL(id, ObjectType.MEETING);

                xml.append("<row>\n");

                xml.append("<data_href>\n");
                xml.append("<label>" + XMLUtils.escape(name) + "</label>\n");
                xml.append("<href>" + href + "</href>\n");
                xml.append("</data_href>\n");
                xml.append("<data>" + XMLUtils.escape(dateString) + "</data>\n");
                xml.append("<data>" + XMLUtils.escape(startTimeString) + "</data>\n");
                xml.append("<data>" + XMLUtils.escape(endTimeString) + "</data>\n");

                if (attendeeStatus != null) {
                    xml.append("<data>" + XMLUtils.escape(attendeeStatus) + "</data>\n");
                } else {
                    xml.append("<data></data>\n");
                }

                xml.append("</row>\n");

            }

        }

        xml.append("</content>\n");
        xml.append("</channel>\n");

        return xml.toString();
    }


    /**
     *  getXML()
     * Converts the object to XML representation.
     * This method returns the object as XML text.
     *
     *  @return XML representation
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }


    /**
     *  setStylesheet()
     *
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation
     * of the component to a presentation form.
     * This method returns the object as XML text.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation
     * of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        this.stylesheet = styleSheetFileName;
    }

    /**
     * Sets the maximum number of items to display.
     * The XML contains no more than this number of meetings.
     * @param displayMax the maximum number of meetings to display
     */
    public void setDisplayMax(int displayMax) {
        this.displayMax = displayMax;
    }

    /**
     * Specifies the number of days ahead to search for meetings.
     * @param dayRange the number of days ahead to include meetings from
     */
    public void setDayRange(int dayRange) {
        this.dayRange = dayRange;
    }

    /**
     * Loads the meeting entries.
     * @throws net.project.persistence.PersistenceException if there is a problem
     * loading
     * @throws IllegalStateException if user is null
     */
    public void loadEntries()
            throws net.project.persistence.PersistenceException {

        if (this.user == null) {
            throw new IllegalStateException("user is required");
        }

        PnCalendar calendar = new PnCalendar(user);
        calendar.setSpace(this.user.getCurrentSpace());
        calendar.loadIfNeeded();
        calendar.setStartDate(calendar.startOfDay(PnCalendar.currentTime()));

        // Calculate the end date by adding the dayRange to
        // the start time
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(calendar.getTime());
        gc.add(GregorianCalendar.DATE, this.dayRange);

        calendar.setEndDate(gc.getTime());

        String[] entryTypes = {"meeting"};
        calendar.loadEntries(entryTypes, false);

        this.meetingList.clear();
        this.meetingList.addAll(calendar.getEntries());


    }
    
    public Collection getMeetingList(){
    	return this.meetingList;
    }

    /**
     * Returns the presentation of the component.
     * This method will apply the stylesheet to the XML representation of the component and
     * return the resulting HTML.
     * @return the transformed XML
     * @throws IllegalStateException if stylesheet has not been specified
     */
    public String getPresentation() {

        if (this.stylesheet == null) {
            throw new IllegalStateException("stylesheet is required");
        }

        XMLFormatter xml = new XMLFormatter();
        xml.setStylesheet(this.stylesheet);
        return xml.getPresentation(getXML());
    }

}
