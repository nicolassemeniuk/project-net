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

 package net.project.base.finder;

import java.util.Date;

import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.DateUtils;
import net.project.util.VisitException;

/**
 * Filter a finder based on the start and end of a date.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class DateFilter extends ColumnFilter {

    /** The start of the date range that the user has entered. */
    private Date dateRangeStart = null;

    /** The end of the date range that the user has entered. */
    private Date dateRangeFinish = null;

    /** Option to ensure that we are only comparing dates, not the time as well. */
    private boolean truncateDates = false;
    
    /** Option to hint if any date range comparision is to be moade or not */
    private boolean isRange = false; 

    /**
     * Creates a new DateFilter allowing filtering on empty values.
     * The column's name is used for the display name of the filter.
     *
     * @param id a <code>String</code> value which uniquely identifies this
     * filter.
     * @param columnDef the <code>ColumnDefinition</code> which identifies which
     * date column this filter is going to be filtering.
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional fields); false
     * if not (for mandatory fields)
     */
    public DateFilter(String id, ColumnDefinition columnDef, boolean isIncludeEmptyOption) {
        super(id, columnDef, isIncludeEmptyOption);
    }

    /**
     * Creates a new DateFilter allowing filtering on empty values.
     * The column's name is used for the display name of the filter.
     *
     * @param id a <code>String</code> value which uniquely identifies this
     * filter.
     * @param columnDef the <code>ColumnDefinition</code> which identifies which
     * date column this filter is going to be filtering.
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional fields); false
     * if not (for mandatory fields)
     * @param truncateDates if true, we will ignore time of date when we are
     * comparing dates in the database.
     */
    public DateFilter(String id, ColumnDefinition columnDef, boolean isIncludeEmptyOption, boolean truncateDates) {
        super(id, columnDef, isIncludeEmptyOption);
        this.truncateDates = truncateDates;
    }

    /**
     * Creates a new DateFilter with a specific name token.
     *
     * @param id a <code>String</code> value which uniquely identifies this
     * filter.
     * @param nameToken - The token (property) that will allow this filter to
     * look up a human-readable representation of this token.
     * @param columnDef the <code>ColumnDefinition</code> which identifies which
     * date column this filter is going to be filtering.
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional fields); false
     * if not (for mandatory fields)
     */
    public DateFilter(String id, ColumnDefinition columnDef, String nameToken, boolean isIncludeEmptyOption) {
        super(id, columnDef, nameToken, isIncludeEmptyOption);
    }

    /**
     * Get a where clause for this filter that can be applied to a finder.  This
     * where clause will be empty (not null) if the filter isn't selected.
     *
     * @return a <code>String</code> containing a where clause, or an empty
     * string if the filter isn't selected.
     */
    public String getWhereClause() {
        StringBuffer whereClause = new StringBuffer();
        boolean hasValues = false;
        Date dateStart = dateRangeStart;
        Date dateFinish = dateRangeFinish;

        //If we are going to truncate dates, we adjust the start and finish
        //dates here to match midnight in the user's time zone.
        if (truncateDates) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            //Adjust the dates for internationalization purposes.  We are
            //getting midnight because this is the "truncation" of the date.
            if (dateStart != null) {
                dateStart = cal.getMidnight(dateStart);
            }
            if (dateFinish != null) {
                dateFinish = cal.endOfDay(dateFinish);
            }
        }

        hasValues = (dateStart != null || dateFinish != null);

        if (hasValues || isEmptyOptionSelected()) {

            whereClause.append("(");

            if (hasValues) {

                //Add a parenthetic just in case both date aren't null and we need
                //two where clauses.
                whereClause.append("(");

                //Create the where clause for the start of the date range
                if (dateStart != null) {
                    if(!isRange && dateFinish == null) {
                        whereClause.append(getColumnDefinition().getColumnName()).append(" = ")
                        .append(DateUtils.getDatabaseDateString(dateStart));
                    } else {
                        whereClause.append(getColumnDefinition().getColumnName()).append(" >= ")
                            .append(DateUtils.getDatabaseDateString(dateStart));
                    }

                    //Append an end if there is another statement we are going to add.
                    if (dateFinish != null) {
                        whereClause.append(" and ");
                    }
                }

                //Create the where clause for the end of the date range
                if (dateFinish != null) {
                    whereClause.append(getColumnDefinition().getColumnName()).append(" <= ")
                        .append(DateUtils.getDatabaseDateString(dateFinish));
                }

                //Close the parenthetic statement that we added above to hold both
                //clauses.
                whereClause.append(")");

            }

            if (hasValues && isEmptyOptionSelected()) {
                whereClause.append(" OR ");
            }

            if (isEmptyOptionSelected()) {
                whereClause.append(getColumnDefinition().getColumnName()).append(" IS NULL");
            }

            whereClause.append(")");
        }

        return whereClause.toString();
    }

    /**
     * Get a description of what this filter does.  This method should return
     * something like "Tasks with finish dates between 01/02/2003 and 01/02/2005".
     *
     * @return a <code>String</code> value describing what this filter does in
     * order to produce a parameter list.
     */
    public String getFilterDescription() {
        DateFormat df = SessionManager.getUser().getDateFormatter();
        String[] dates = new String[] {
            getColumnDefinition().getName(),
            df.formatDate(dateRangeStart),
            df.formatDate(dateRangeFinish) };

        return PropertyProvider.get("prm.report.filter.datefilter.description", dates);
    }

    /**
     * Specifies the date value for the start date of the date range.
     * @param date the start date value
     */
    public void setDateRangeStart(Date date) {
        this.dateRangeStart = date;
        this.isRange = true;
    }
    
    /**
     * Specifies the date value for the start date of the date range.
     * @param date the start date value
     * @param isRange specifies if any range comparision has to be made.
     *        if isRange is set to be <code>false</code> and no dateRangeFinish 
     *        is set then the where clause would compare the dateRangeStart as equal to.
     * @see DateFilter#setDateRangeFinish(Date)
     */
    public void setDateRangeStart(Date date, boolean isRange) {
        this.dateRangeStart = date;
        this.isRange = isRange;
    }

    /**
     * Get a value corresponding to the beginning of the date range that this
     * filter is going to filter on.
     *
     * @return a <code>Date</code> value indicating the start of the date range
     * on which we are going to filter records.
     */
    public Date getDateRangeStart() {
        return dateRangeStart;
    }

    /**
     * Specifies the date value for the finish date of the date range.
     * @param date the start date value
     */
    public void setDateRangeFinish(Date date) {
        this.dateRangeFinish = date;
    }

    /**
     * Get a value corresponding to the end of the date range that this
     * filter is going to filter on.
     *
     * @return a <code>Date</code> value indicating the end of the date range
     * on which we are going to filter records.
     */
    public Date getDateRangeFinish() {
        return dateRangeFinish;
    }

    /**
     * When this flag is set, we will only compare date values and we will
     * ignore the time flag.  This is useful because users might (or probably
     * will) just be typing a date in a field but not a time.
     *
     * @return a <code>boolean</code> indicating if we are truncating dates.
     */
    public boolean isTruncateDates() {
        return truncateDates;
    }

    /**
     * Tells this date filter whether it should be comparing only date values
     * and not the time values that are also part of a date value by default.
     *
     * This flag is internationalization sensitive.  If you send a midnight
     * parameter, it will truncate to midnight in your time zone.
     *
     * @param truncateDates a <code>boolean</code> indicating if we should
     * truncate all dates to midnight.
     */
    public void setTruncateDates(boolean truncateDates) {
        this.truncateDates = truncateDates;
    }

    protected void clearProperties() {
        setDateRangeStart(null);
        setDateRangeFinish(null);
    }

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitDateFilter(this);
    }

}
