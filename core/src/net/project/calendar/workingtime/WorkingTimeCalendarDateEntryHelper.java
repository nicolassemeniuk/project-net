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
|    $Revision: 20472 $
|        $Date: 2010-02-26 11:51:26 -0300 (vie, 26 feb 2010) $
|      $Author: nilesh $
|
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.calendar.TimeBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.InvalidDateException;
import net.project.util.Validator;

/**
 * Provides a helper class for viewing and editing date entries
 * on a working time calendar definition.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class WorkingTimeCalendarDateEntryHelper {

    private static final int WORKING_TIME = 1;
    private static final int NON_WORKING_TIME = 2;

    private final User user;
    private final IWorkingTimeCalendarProvider provider;
    private final WorkingTimeCalendarDefinition calendarDef;

    private String entryID = null;
    private String singleDateFormatted = "";
    private String rangeDateStartFormatted = "";
    private String rangeDateEndFormatted = "";
    private boolean isSingleDate = true;

    private Date singleDate;
    private Date rangeDateStart;
    private Date rangeDateEnd;

    /** The option selected. */
    private int selectedOption = NON_WORKING_TIME;

    /**
     * A helper for editing the working times.
     */
    private final WorkingTimeEditHelper workingTimeEditHelper;
    
    /**
	 * The date descripton, optional field. 
	 */
    private String dateDescription = "";

    /**
     * Creates a new helper for the specified calendar definition and the entry
     * with the specified ID.
     * @param request the request from which to fetch the current user
     * @param provider the provider used to store the updates
     * @param calendarDef the calendar definition from which to fetch the entry
     * @param entryID the ID of the date entry to fetch
     * @throws IllegalStateException if no session attribute 'user' is found
     * @throws IllegalArgumentException if the specified ID is not that of a date entry
     */
    WorkingTimeCalendarDateEntryHelper(HttpServletRequest request, IWorkingTimeCalendarProvider provider, WorkingTimeCalendarDefinition calendarDef, String entryID) {

        this.user = (User) request.getSession().getAttribute("user");
        if (this.user == null) {
            throw new IllegalStateException("Missing session attribute 'user'");
        }

        this.provider = provider;
        this.calendarDef = calendarDef;

        WorkingTimeCalendarEntry entry = this.calendarDef.getEntry(entryID);
        if (!(entry instanceof DateEntry)) {
            throw new IllegalArgumentException("The calendar entry for ID " + entryID + " is not a date entry");
        }

        populateFrom((DateEntry) entry);

        this.workingTimeEditHelper = new WorkingTimeEditHelper(this.user, entry.getWorkingTimes());

    }

    private void populateFrom(DateEntry entry) {
        this.entryID = entry.getEntryID();
        if (entry.isSingleDate()) {
            this.singleDateFormatted = user.getDateFormatter().formatDate(toDate(entry.getStartDayOfYear()));
            this.rangeDateStartFormatted = "";
            this.rangeDateEndFormatted = "";
        } else {
            this.singleDateFormatted = "";
            this.rangeDateStartFormatted = user.getDateFormatter().formatDate(toDate(entry.getStartDayOfYear()));
            this.rangeDateEndFormatted = user.getDateFormatter().formatDate(toDate(entry.getEndDayOfYear()));
        }
        this.isSingleDate = entry.isSingleDate();
        this.dateDescription = entry.getDateDescription();

        if (entry.isWorkingDay()) {
            setWorkingTimeSelected();
        } else {
            setNonWorkingTimeSelected();
        }
    }

    /**
     * Creates a new helper for the specified calendar definition for the purposes
     * of creating a new date entry.
     * @param request the request from which to fetch the current user
     * @param provider the provider used to store the updates
     * @param calendarDef the calendar definition from which to fetch the entry
     * @throws IllegalStateException if no session attribute 'user' is found
     */
    WorkingTimeCalendarDateEntryHelper(HttpServletRequest request, IWorkingTimeCalendarProvider provider, WorkingTimeCalendarDefinition calendarDef) {

        this.user = (User) request.getSession().getAttribute("user");
        if (this.user == null) {
            throw new IllegalStateException("Missing session attribute 'user'");
        }

        this.provider = provider;
        this.calendarDef = calendarDef;

        this.workingTimeEditHelper = new WorkingTimeEditHelper(this.user);

    }

    /**
     * Returns the working time edit helper for this date entry.
     * @return the edit helper
     */
    public WorkingTimeEditHelper getWorkingTimeEditHelper() {
        return this.workingTimeEditHelper;
    }

    public String getSingleDateFormatted() {
        return this.singleDateFormatted;
    }

    public String getRangeDateStartFormatted() {
        return this.rangeDateStartFormatted;
    }

    public String getRangeDateEndFormatted() {
        return this.rangeDateEndFormatted;
    }

    public boolean isSingleDate() {
        return this.isSingleDate;
    }

    public String getEntryID() {
        return this.entryID;
    }

    public boolean isNonWorkingTimeSelected() {
        return (this.selectedOption == NON_WORKING_TIME);
    }

    public void setNonWorkingTimeSelected() {
        this.selectedOption = NON_WORKING_TIME;
    }

    public boolean isWorkingTimeSelected() {
        return (this.selectedOption == WORKING_TIME);
    }

    public void setWorkingTimeSelected() {
        this.selectedOption = WORKING_TIME;
    }

    /**
     * Indicates whether this is a working day.
     * @return true if this is a working day; false otherwise
     */
    private boolean isWorkingDay() {
        return (this.selectedOption == WORKING_TIME);
    }

    /**
     * Constructs a new DateEntry based on the information submitted from
     * the Date Edit page.
     * @param request the request containing submitted parameters
     * @param errorReporter an error reporter to update in the event of any errors
     * @throws IllegalStateException if any mandator pre-checked radio option
     * values are empty
     */
    public void processSubmission(HttpServletRequest request, ErrorReporter errorReporter) {

        // Note: Deliberately coded to save _all_ submitted request parameters
        // so that in the event of an error the page is
        // displayed containing the exact submitted values

        // Process the common items submitted
        processCommon(request);

        // Radio option indicating working or non working time
        String dateValue = request.getParameter("dateValue");
        if ("workingtime".equals(dateValue)) {
            setWorkingTimeSelected();
        } else if ("nonworkingtime".equals(dateValue)) {
            setNonWorkingTimeSelected();
        } else {
            throw new IllegalStateException("Missing or invalid value for 'dateValue' radio option :" + dateValue);
        }

        // Process time settings
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
            String nextName = (String) e.nextElement();

            if (nextName.startsWith("timeControl_")) {
                // We found a parameter controlling a time of form:
                // <prefix>_<position> 
                // where position 0..5
                int position = Integer.valueOf(nextName.substring(nextName.indexOf('_') + 1)).intValue();

                // Construct the names of the time fields
                String startTimeName = "timeStart_" + position;
                String endTimeName = "timeEnd_" + position;

                // Parse the start and end; we don't care about the date portion,
                // just the time
                // It is optional; the found time field may be empty
                // We'll get a null date if no values are selected
                getWorkingTimeEditHelper().setStartTime(position, TimeBean.parseTime(request, startTimeName, new Date(), true));
                getWorkingTimeEditHelper().setEndTime(position, TimeBean.parseTime(request, endTimeName, new Date(), true));

            }
        }

        // Validate inputs
        validate(errorReporter);

    }

    /**
     * Process common items used during different kinds of submissions.
     * @param request the request containing submitted parameters
     */
    private void processCommon(HttpServletRequest request) {

        // Radio option indicating a single date or range of dates
        String dateType = request.getParameter("dateType");
        if ("single".equals(dateType)) {
            this.isSingleDate = true;
        } else if ("range".equals(dateType)) {
            this.isSingleDate = false;
        } else {
            throw new IllegalStateException("Missing or invalid value for 'dateType' radio option :" + dateType);
        }

        // Save the unparsed versions
        this.singleDateFormatted = request.getParameter("singleDateValue");
        this.rangeDateStartFormatted = request.getParameter("rangeDateStartValue");
        this.rangeDateEndFormatted = request.getParameter("rangeDateEndValue");
        this.dateDescription = request.getParameter("dateDescription");

    }

    private void validate(ErrorReporter errorReporter) {

        // Validate the submitted dates
        if (isSingleDate()) {

            if (Validator.isBlankOrNull(this.singleDateFormatted)) {
                errorReporter.addError(new ErrorDescription("singleDateValue", PropertyProvider.get("prm.schedule.workingtime.editdate.singledate.isrequired.message")));
            } else {
                // Parse the single date
                try {
                    this.singleDate = this.user.getDateFormatter().parseDateString(this.singleDateFormatted);
                } catch (InvalidDateException e) {
                    this.singleDate = null;
                    errorReporter.addError(new ErrorDescription("singleDateValue", PropertyProvider.get("prm.schedule.workingtime.editdate.singledate.invaliddate.message")));
                }
            }

        } else {

            if (Validator.isBlankOrNull(this.rangeDateStartFormatted) || Validator.isBlankOrNull(this.rangeDateEndFormatted)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.workingtime.editdate.rangedate.isrequired.message"));
            } else {
                // Parse both dates
                try {
                    this.rangeDateStart = this.user.getDateFormatter().parseDateString(this.rangeDateStartFormatted);
                    this.rangeDateEnd = this.user.getDateFormatter().parseDateString(this.rangeDateEndFormatted);

                    if (this.rangeDateEnd.before(this.rangeDateStart)) {
                        errorReporter.addError(PropertyProvider.get("prm.schedule.workingtime.editdate.rangedate.startbeforeend.message"));
                    }

                } catch (InvalidDateException e) {
                    this.rangeDateStart = null;
                    this.rangeDateEnd = null;
                    errorReporter.addError(PropertyProvider.get("prm.schedule.workingtime.editdate.rangedate.invaliddate.message"));
                }

            }

        }

        if (isWorkingTimeSelected()) {
            // Validate all the working times
            getWorkingTimeEditHelper().validate(errorReporter, new WorkingTimeErrorMessageProvider());

        } else {
            // Nothing to validate; working time is ignored

        }
        
        if(!Validator.isBlankOrNull(this.dateDescription) && !Validator.maxLength(this.dateDescription, 500)){
        	// Validate date description.
        	errorReporter.addError(PropertyProvider.get("prm.schedule.workingtime.editdate.description.invaliddate.message"));
        }

    }

    /**
     * Submits and updates working times according
     * to the setting of the request parameter <code>presetType</code>.
     * <p>
     * The single date / range date items are processed.  The working time
     * radio option is ignored since this method sets it to Working Time.
     * <li> <code>standard</code> - 8:00 - 12:00, 13:00 - 17:00
     * <li> <code>nightshift</code> - 0:00 - 3:00, 4:00 - 8:00, 23:00 - 24:00
     * <li> <code>24hour</code> - 0:00 - 24:00
     * </p>
     * <p>
     * No store is performed.
     * </p>
     * @param request the request from which to get the parameters
     * @throws IllegalStateException if no parameter called presetType is found
     * or it is empty or set to an unknown value
     */
    public void processPreset(HttpServletRequest request) {

        String presetType = request.getParameter("presetType");
        if (Validator.isBlankOrNull(presetType)) {
            throw new IllegalStateException("Missing request parameter 'presetType'");
        }

        // Process the common submitted items
        processCommon(request);

        if (presetType.equals("standard")) {

            setWorkingTimeSelected();
            getWorkingTimeEditHelper().setTimes(0, new Time(8, 0), new Time(12, 0));
            getWorkingTimeEditHelper().setTimes(1, new Time(13, 0), new Time(17, 0));
            getWorkingTimeEditHelper().setTimes(2, null, null);
            getWorkingTimeEditHelper().setTimes(3, null, null);
            getWorkingTimeEditHelper().setTimes(4, null, null);

        } else if (presetType.equals("nightshift")) {

            setWorkingTimeSelected();
            getWorkingTimeEditHelper().setTimes(0, new Time(0, 0), new Time(3, 0));
            getWorkingTimeEditHelper().setTimes(1, new Time(4, 0), new Time(8, 0));
            getWorkingTimeEditHelper().setTimes(2, new Time(23, 0), new Time(24, 0));
            getWorkingTimeEditHelper().setTimes(3, null, null);
            getWorkingTimeEditHelper().setTimes(4, null, null);

        } else if (presetType.equals("24hour")) {

            setWorkingTimeSelected();
            getWorkingTimeEditHelper().setTimes(0, new Time(0, 0), new Time(24, 0));
            getWorkingTimeEditHelper().setTimes(1, null, null);
            getWorkingTimeEditHelper().setTimes(2, null, null);
            getWorkingTimeEditHelper().setTimes(3, null, null);
            getWorkingTimeEditHelper().setTimes(4, null, null);

        } else {
            throw new IllegalStateException("Invalid request parameter 'presetType': " + presetType);
        }

    }

    /**
     * Stores the updateEntry in the calendar definition.
     * The provider is updated to reflect the latest calendar definition.
     */
    public void store() throws PersistenceException {

        DateEntry updateEntry;

        // Now construct the update entry
        if (isWorkingDay()) {
            if (this.isSingleDate) {
                updateEntry = WorkingTimeCalendarEntry.makeWorkingDate(toDayOfYear(this.singleDate));
            } else {
                updateEntry = WorkingTimeCalendarEntry.makeWorkingDate(toDayOfYear(this.rangeDateStart), toDayOfYear(this.rangeDateEnd));
            }
            updateEntry.setWorkingTimes(getWorkingTimeEditHelper().getWorkingTimes());
        } else {
            if (this.isSingleDate) {
                updateEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(toDayOfYear(this.singleDate));
            } else {
                updateEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(toDayOfYear(this.rangeDateStart), toDayOfYear(this.rangeDateEnd));
            }
        }
        updateEntry.setDateDescription(this.dateDescription);
        
        this.calendarDef.updateDateEntries(updateEntry);
        this.provider.store(this.calendarDef.getID());

    }

    /**
     * Converts the specified date to a DayOfYear for the
     * current user's time zone.
     * @param date the date to convert
     * @return the DayOfYear representing year, month and day
     * for the date for the user's time zone
     */
    private DayOfYear toDayOfYear(Date date) {
        return new DayOfYear(date, this.user.getTimeZone());
    }

    /**
     * Converts the specified day of year to a date for the
     * current user's time zone.
     * @param dayOfYear the day of year to convert
     * @return a date that is set to midnight with year, month and day
     * components set to the day of year for the user's time zone
     */
    private Date toDate(DayOfYear dayOfYear) {
        return dayOfYear.toDate(this.user.getTimeZone());
    }

    /**
     * Removes the entry for the current entryID.
     * The provider is updated to reflect the latest calendar definition
     * @throws PersistenceException if there is a problem removing it
     */
    public void remove() throws PersistenceException {
        this.calendarDef.removeDateEntry(this.entryID);
        this.provider.store(this.calendarDef.getID());
    }

    /**
     * Provides error messages when validating working times.
     */
    private static class WorkingTimeErrorMessageProvider implements WorkingTimeEditHelper.IErrorMessageProvider {

        public String getStartTimeEndTimeRequired() {
            return PropertyProvider.get("prm.schedule.workingtime.editdate.startendtimerequired.message");
        }

        public String getEndTimeMustBeAfterStartTime() {
            return PropertyProvider.get("prm.schedule.workingtime.editdate.endtimeafterstart.message");
        }

        public String getStartTimeMustBeAfterPreviousEndTime() {
            return PropertyProvider.get("prm.schedule.workingtime.editdate.starttimeafterend.message");
        }

        public String getWorkingTimeRequired() {
            return PropertyProvider.get("prm.schedule.workingtime.editdate.workingtimerequired.message");
        }

    }

	/**
	 * @return the dateDescription
	 */
	public String getDateDescription() {
		return dateDescription;
	}

	/**
	 * @param dateDescription the dateDescription to set
	 */
	public void setDateDescription(String dateDescription) {
		this.dateDescription = dateDescription;
	}

	/**
	 * @param isSingleDate the isSingleDate to set
	 */
	public void setSingleDate(boolean isSingleDate) {
		this.isSingleDate = isSingleDate;
	}

	/**
	 * @param singleDate the singleDate to set
	 */
	public void setSingleDate(Date singleDate) {
		this.singleDate = singleDate;
	}

}
