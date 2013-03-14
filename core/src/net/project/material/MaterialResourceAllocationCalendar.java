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

 package net.project.material;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.calendar.PnCalendar;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.ResourceAssignment;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.DateUtils;
import net.project.util.NumberFormat;

/**
 * This method queries a ResourceAllocationList object to find
 * information about how much a material is allocated to tasks each day.
 * After gathering this information, it can produce XML which can be
 * transformed into a calendar to display this information.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class MaterialResourceAllocationCalendar implements IXMLPersistence {
    /**
     * A list of calendar objects which contain the information to produce the
     * calendar xml.
     */
    private List allocationCalendars = new ArrayList();
    /**
     * This list contains objects which are the display names for the days of
     * the week that will appear at the top of the calendar.
     */
    private List daysOfWeek;
    /**
     * This is the calendar that will be used to add time to a date.
     */
    private PnCalendar calendar;

    /** The start date of the period we are generating calendars for. */
    private Date calendarStart;
    /** The end date of the period we are generating calendars for. */
    private Date calendarEnd;
    private DateFormat dateFormat;
    private NumberFormat numberFormat;

    /**
     * Standard constructor.
     */
    public MaterialResourceAllocationCalendar() {
        this.dateFormat = SessionManager.getUser().getDateFormatter();
        this.numberFormat = NumberFormat.getInstance();
    }

    /**
     * Set the calendar that we will use for date arithmetic.
     *
     * @param calendar a <code>PnCalendar</code> object that we will use for
     * date arithmetic.
     */
    public void setCalendar(PnCalendar calendar) {
        this.calendar = calendar;
    }

    /**
     * Get the start date of the period we are going to report on.
     *
     * @return a <code>Date</code> object which represents the earliest date we
     * are going to report on.
     */
    public Date getCalendarStart() {
        return calendarStart;
    }

    /**
     * Set the start date of the earliest time we are going to report on.
     *
     * @param calendarStart a <code>Date</code> object which represents the
     * earliest date we are going to report on.
     */
    public void setCalendarStart(Date calendarStart) {
        this.calendarStart = calendarStart;
    }

    /**
     * Get the latest date for which we are going to report resource allocations.
     *
     * @return a <code>Date</code> object which is the latest date on which we
     * are going to report resource allocations.
     */
    public Date getCalendarEnd() {
        return calendarEnd;
    }

    /**
     * Set the latest date for which we will collect resource allocations.
     *
     * @param calendarEnd a <code>Date</code> object which represents the latest
     * date we will report on.
     */
    public void setCalendarEnd(Date calendarEnd) {
        this.calendarEnd = calendarEnd;
    }

    /**
     * This method compresses the resource allocation list into a
     * multi-dimensional array.
     */
    void loadAllocations(String materialID) throws PersistenceException {
    	Date currentDate = calendar.startOfMonth(calendarStart);
    	currentDate = DateUtils.addDay( currentDate, 1);
    	currentDate.setHours(0);
    	Date lastDate = calendarEnd;
        CalendarMonth calendarMonth = null;
        int currentMonth = -1;
        MaterialResourceAllocationList ral = new MaterialResourceAllocationList();
        ral.loadAllocationsForMaterial(materialID);

        while(currentDate.before(lastDate) || (currentMonth == currentDate.getMonth() && currentDate.getDate() == lastDate.getDate()) ) {
            calendar.setTime(currentDate);
            int month = calendar.get(Calendar.MONTH);
            if (month != currentMonth) {
                //We are in a new month.  Store the old one
                if (calendarMonth != null) {
                    allocationCalendars.add(calendarMonth);
                }
                calendarMonth = new CalendarMonth();
                calendarMonth.monthName = dateFormat.formatDate(currentDate, "MMMM yyyy");
                currentMonth = month;
            }

            //Create a new allocation object for this date
            ResourceAssignment resourceAllocation = ral.getAllocationForDate(currentDate);

            //If there isn't an allocation on this date, assume zero
            if (resourceAllocation == null) {
                resourceAllocation = new ResourceAssignment();
                resourceAllocation.setAssignmentDate(currentDate);
                resourceAllocation.setPercentAssigned(0);
            }

            //Figure out which slot to put it in the array
            RowCol rowCol = findRowAndCol(currentDate);
            if (rowCol.row != -1){
            	calendarMonth.allocations[rowCol.row][rowCol.col] = resourceAllocation;
            }
                

            //Iterate the current date for the next iterator of the while loop
            currentDate = DateUtils.addDay(currentDate, 1);
        }
        //Store the final month
        allocationCalendars.add(calendarMonth);
    }

    RowCol findRowAndCol(Date currentDate) {
        RowCol rowCol = new RowCol();

        calendar.setTime(currentDate);

        rowCol.row = calendar.get(Calendar.WEEK_OF_MONTH);
        rowCol.col = ((calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek() + 7) % 7);

        return rowCol;
    }

    void loadDaysOfWeekAbbr() {
        daysOfWeek = new ArrayList();
        calendar.setTime(calendar.startOfWeek(calendar.getTime()));

        for (int i = 0; i < 7; i++) {
            daysOfWeek.add(dateFormat.formatDate(calendar.getTime(), "E"));
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }
    }

    /**
     * Load the information about resource allocations from the database.
     *
     * @param materialID a <code>String</code> containing the person id of the
     * person for which we are going to load resource allocation information.
     * @throws PersistenceException if there is a problem loading the
     * information from the database.
     */
    public void load(String materialID) throws PersistenceException {
        loadAllocations(materialID);
        loadDaysOfWeekAbbr();
    }

    /**
     * Returns this object's XML representation, including the XML version tag.
     * @return XML representation of this object
     * @see IXMLPersistence#getXMLBody
     * @see IXMLPersistence#XML_VERSION
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     * @return XML representation of this object
     * @see IXMLPersistence#getXML
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<MaterialResourceAllocationCalendar>");


        for (Iterator it = allocationCalendars.iterator(); it.hasNext();) {
            CalendarMonth calendarMonth = (CalendarMonth)it.next();
            xml.append("<Month>");

            //Append the month that will appear at the top of the calendar
            xml.append("<DisplayName>").append(calendarMonth.monthName).append("</DisplayName>");

            //Append the abbreviations for the days of the week that will appear
            //at the top of the calendar.
            xml.append("<DaysOfWeek>");
            for (Iterator it2 = daysOfWeek.iterator(); it2.hasNext();) {
                String s = (String)it2.next();
                s = s.substring(0,1);
                xml.append("<DayOfWeek>").append(s).append("</DayOfWeek>");
            }
            xml.append("</DaysOfWeek>");


            //Append all of the "days" of the calendar
            xml.append("<Allocations>");
            for (int i = 0; i < calendarMonth.allocations.length; i++) {
                xml.append("<Row>");
                for (int j = 0; j < calendarMonth.allocations[i].length; j++) {
                    xml.append("<Col>");
                    ResourceAssignment ra = calendarMonth.allocations[i][j];
                    if (ra != null) {
                        xml.append("<Allocation>");
                        xml.append("<Day>").append(dateFormat.formatDate(ra.getAssignmentDate(), "dd")).append("</Day>");
                        xml.append("<Percent>").append(numberFormat.formatPercent((float)ra.getPercentAssigned()/100)).append("</Percent>");
                        xml.append("<PercentNumeric>").append(ra.getPercentAssigned()).append("</PercentNumeric>");
                        xml.append("</Allocation>");
                    }
                    xml.append("</Col>");
                }
                xml.append("</Row>");
            }
            xml.append("</Allocations>");

            xml.append("</Month>");
        }

        xml.append("</MaterialResourceAllocationCalendar>");
        return xml.toString();
    }
}

/**
 * Class which holds the name of a month and an array which is set up like a
 * calendar or grid.  The Resource allocations appear in the cells that they
 * would appear on in a wall calendar.
 */
class CalendarMonth {
    String monthName;
    /** A "wall calendar" of resource allocations. */
    ResourceAssignment allocations[][] = new ResourceAssignment[7][7];
}

/**
 * A simple class to encapsulate the concept of "column" and "row".
 */
class RowCol {
    int row, col;

    public RowCol() {
    }

    public RowCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RowCol)) return false;

        final RowCol rowCol = (RowCol)o;

        if (col != rowCol.col) return false;
        if (row != rowCol.row) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = row;
        result = 29 * result + col;
        return result;
    }

    public String toString() {
        return "RowCol: Col " + col + " Row " + row;
    }
}
