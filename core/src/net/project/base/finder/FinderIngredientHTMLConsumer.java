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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.finder;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.PnetRuntimeException;
import net.project.base.money.MoneyFilter;
import net.project.form.report.FormFieldFilter;
import net.project.portfolio.view.ProjectVisibilityFilter;
import net.project.portfolio.view.SelectedProjectsFilter;
import net.project.project.ProjectVisibility;
import net.project.resource.filters.assignments.SpaceFilter;
import net.project.schedule.report.FilterUser;
import net.project.schedule.report.UserFilter;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.InvalidDateException;
import net.project.util.JSPUtils;
import net.project.util.NumberFormat;
import net.project.util.VisitException;

/**
 * Provides visit methods for consuming posted FinderIngredients HTML parameters.
 * The HTML parameters that are consumed are produced by {@link FinderIngredientHTMLProducer}.
 * <p>
 * Typical usage of this class is from a JSP Processing page:
 * <pre><code>
 *   ...
 *   FinderIngredientHTMLConsumer consumer = new FinderIngredientHTMLConsumer(request);
 *   accept(finderIngredients);
 *   ...
 * </code></pre>
 * </p>
 * @author Matthew Flower
 * @author Tim Morrow
 * @since Version 7.4
 */
public class FinderIngredientHTMLConsumer extends EmptyFinderIngredientVisitor {


    /**
     * The request containing the properties from which to update the
     * FinderIngredient components.
     */
    private final HttpServletRequest request;

    /**
     * Creates a new FinderIngredientHTMLConsumer for the parameters in
     * the specified request.
     * @param request the request containing the parameters from which to
     * update the FinderIngredient components
     */
    public FinderIngredientHTMLConsumer(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Visits the FinderIngredients and updates its properties and the
     * properties of the FinderColumnList, FinderFilterList, FinderGroupingList
     * and FinderSorterList.
     * @param finderIngredients the FinderIngredients to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitFinderIngredients(FinderIngredients finderIngredients) throws VisitException {
        finderIngredients.getFinderColumnList().accept(this);
        finderIngredients.getFinderFilterList().accept(this);
        finderIngredients.getFinderGroupingList().accept(this);
        finderIngredients.getFinderSorterList().accept(this);
    }

    //
    // Filter Methods
    //

    /**
     * Visits the <code>FinderFilterList</code> and updates its properties
     * and the properties of each <code>FinderFilter</code> in the list from
     * the current request.
     * @param filterList the FinderFilterList containing the FinderFilters to
     * update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitFinderFilterList(FinderFilterList filterList) throws VisitException {

        for (Iterator it = filterList.getAllFilters().iterator(); it.hasNext(); ) {
            FinderFilter nextFilter = (FinderFilter) it.next();
            nextFilter.accept(this);
        }

    }

    /**
     * Visits the CheckboxFilter and updates its properties from the current
     * request.
     * @param filter the CheckboxFilter to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitCheckboxFilter(CheckboxFilter filter) throws VisitException {
        filter.clear();

        //First, see if the checkbox was selected
        String checkboxValue = request.getParameter("checkbox"+filter.getID());
        filter.setSelected(checkboxValue != null && checkboxValue.trim().length() > 0);

        //Now let the enclosed filter decide what was selected internally.
        filter.getEnclosedFilter().accept(this);
    }

    /**
     * Visits the RadioButtonFilter and updates its properties from the current
     * request.
     * @param filter the RadioButtonFilter to update
     * @throws VisitException
     */
    public void visitRadioButtonFilter(RadioButtonFilter filter) throws VisitException {
        String selectedID = request.getParameter("filter"+filter.getID());

        //Iterate through all of the filters in this radio button filter so we
        //can set the selected filter, as well as calling the update properties
        //method for each filter.
        Iterator it = filter.getAllFilters().iterator();
        while (it.hasNext()) {
            FinderFilter currentFilter = (FinderFilter)it.next();

            //Propagate the properties update to the filter so it can update
            //its own properties accordingly.
            currentFilter.accept(this);

            //Set the selected property of this filter.  This is done after the
            //filter sets its own properties so we can override whether or not
            //it thought it was selected.
            currentFilter.setSelected(currentFilter.getID().equals(selectedID));
        }
    }

    /**
     * Visits the UserFilter and updates its properties from the current
     * request.
     * @param filter the UserFilter to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitUserFilter(UserFilter filter) throws VisitException {
        filter.clear();
        String[] selectedPersons = request.getParameterValues("filter"+filter.getID());

        if (selectedPersons != null) {
            List selectedPersonIDList = Arrays.asList(selectedPersons);

            //Iterate through our existing list so we can set the selected parameter
            filter.setSelected(false);
            for (Iterator it = filter.getUserList().iterator(); it.hasNext();) {
                FilterUser user = (FilterUser)it.next();
                user.setSelected(selectedPersonIDList.contains(user.getID()));

                if (user.isSelected()) {
                    filter.setSelected(true);
                }
            }
        }
    }

    /**
     * Sets the properties from the current request into the space filter.
     *
     * @param filter a <code>SpaceFilter</code> that will be populated from the
     * request.
     * @throws VisitException if there is a problem consuming the HTML.
     */
    public void visitSpaceFilter(SpaceFilter filter) throws VisitException {
        filter.clear();
        String[] selectedSpaces = request.getParameterValues("filter"+filter.getID());

        if (selectedSpaces != null) {
            filter.setSelected(true);
            filter.setSelectedSpaces(Arrays.asList(selectedSpaces));
        }
    }

    /**
     * Visits the FormFieldFilter and updates its properties from the current
     * request.
     * @param filter the FormFieldFilter to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitFormFieldFilter(FormFieldFilter filter) throws VisitException {
        filter.clear();
        filter.setSelected(true);
        filter.setFieldFilter(filter.getFormField().processFilterHttpPost(request));
    }

    /**
     * Visits the DateFilter and updates its properties from the current
     * request.
     * @param filter the DateFilter to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitDateFilter(DateFilter filter) throws VisitException {
        DateFormat df = SessionManager.getUser().getDateFormatter();

        // Deselect the filter; it will only be selected if we find some values
        filter.clear();

    	/*
		 * The Calendar is used to set the time to 00:00 and 23:00. This is
		 * necessary because the date creation sets the time to an
		 * undeterminated time. Then, some queries are wrong because in the
		 * database, dates with same "MM/DD/YYYY" could mismatch because the
		 * time difference.
		 */
		Calendar cal = new GregorianCalendar();
		
        //Set the start date, if it was supplied
        String startDateParameter = request.getParameter("filter" + filter.getID() + "Start");        
        if ((startDateParameter != null) && (startDateParameter.trim().length() > 0)) {
            try {
				cal.setTime(df.parseDateString(startDateParameter));
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.AM_PM, Calendar.AM);
				filter.setDateRangeStart(cal.getTime());
                filter.setSelected(true);
            } catch (InvalidDateException idp) {
                throw new PnetRuntimeException("Unable to parse date parameter.  "+startDateParameter+" is not a valid date.", idp) {};
            }
        } else {
            filter.setDateRangeStart(null);
        }

        //Set the end date, if it was supplied
        String endDateParameter = request.getParameter("filter" + filter.getID() + "Finish");
        if ((endDateParameter != null) && (endDateParameter.trim().length() > 0)) {
            try {
				cal.setTime(df.parseDateString(endDateParameter));
				cal.set(Calendar.HOUR, 11);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.AM_PM, Calendar.PM);
				filter.setDateRangeFinish(cal.getTime());
                //filter.setDateRangeFinish(df.parseDateString(endDateParameter));
                filter.setSelected(true);
            } catch (InvalidDateException idp) {
                throw new PnetRuntimeException("Unable to parse date parameter.  "+startDateParameter+" is not a valid date.", idp) {};
            }
        } else {
            filter.setDateRangeFinish(null);
        }

        // Check to see if empty values should be included
        String isEmptyOptionSelectedParameter = request.getParameter("filter" + filter.getID() + "_isEmptyOptionSelected");
        if (!JSPUtils.isEmpty(isEmptyOptionSelectedParameter)) {
            filter.setEmptyOptionSelected(true);
            filter.setSelected(true);
        }

    }

    /**
     * Visits the NumberFilter and updates its properties from the current
     * request.
     * @param filter the NumberFilter to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitNumberFilter(NumberFilter filter) throws VisitException {

        // Deselect the filter; it will only be selected if we find some values
        filter.clear();

        String numberString = request.getParameter("filter" + filter.getID());
        try {
            if ((numberString != null) && (numberString.trim().length() > 0)) {
                filter.setSelected(true);
                filter.setNumber(NumberFormat.getInstance().parseNumber(numberString));
            }

            String comparatorString = request.getParameter("filter"+filter.getID()+"comparator");
            if ((comparatorString != null) && (comparatorString.trim().length() > 0)) {
                filter.setComparator(NumberComparator.getForID(comparatorString));
            }
        } catch (ParseException e) {
            throw new VisitException("Invalid number " + numberString);
        }

        // Check to see if empty values should be included
        String isIncludeEmptyOptionParameter = request.getParameter("filter" + filter.getID() + "_isEmptyOptionSelected");
        if (!JSPUtils.isEmpty(isIncludeEmptyOptionParameter)) {
            filter.setEmptyOptionSelected(true);
            filter.setSelected(true);
        }

    }

    /**
     * Visits the TextFilter and updates its properties from the current
     * request.
     * @param filter the TextFilter to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitTextFilter(TextFilter filter) throws VisitException {

        // Deselect the filter; it will only be selected if we find a value
        filter.setSelected(false);

        // Grab the value from the request
        // If it is present, then we select the filter and set its value
        String submittedValue = request.getParameter("filter" + filter.getID());
        if ((submittedValue != null) && (submittedValue.trim().length() > 0)) {
            filter.setSelected(true);
            filter.setValue(submittedValue);
        }

        // If we find a comparator, then we set it also
        String comparatorString = request.getParameter("filter"+filter.getID()+"comparator");
        if ((comparatorString != null) && (comparatorString.trim().length() > 0)) {
            filter.setComparator(TextComparator.getForID(comparatorString));
        }

        // Check to see if empty values should be included
        String isEmptyOptionSelectedParameter = request.getParameter("filter" + filter.getID() + "_isEmptyOptionSelected");
        if (!JSPUtils.isEmpty(isEmptyOptionSelectedParameter)) {
            filter.setEmptyOptionSelected(true);
            filter.setSelected(true);
        }

    }

    /**
     * Visits the DomainFilter and updates its properties from the current
     * request.
     * @param filter the DomainFilter to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitDomainFilter(DomainFilter filter) throws VisitException {

        // Deselect the filter; it will only be selected if we find some values
        filter.clear();

        // Grab the selected values from the request
        // If there are some, then we select the filter and set its selected
        // values
        String[] submittedValues = request.getParameterValues("filter" + filter.getID());
        if (submittedValues != null && submittedValues.length > 0) {
            filter.setSelected(true);
            filter.setSelectedDomainOptions(submittedValues);
        }

        // If we find a comparator, then we set it also
        String comparatorString = request.getParameter("filter"+filter.getID()+"comparator");
        if ((comparatorString != null) && (comparatorString.trim().length() > 0)) {
            filter.setComparator(DomainComparator.getForID(comparatorString));
        }

        // Check to see if empty values should be included
        String isEmptyOptionSelectedParameter = request.getParameter("filter" + filter.getID() + "_isEmptyOptionSelected");
        if (!JSPUtils.isEmpty(isEmptyOptionSelectedParameter)) {
            filter.setEmptyOptionSelected(true);
            filter.setSelected(true);
        }

    }

    /**
     * Visits the SelectedProjectsFilter and updates its properties from the current
     * request.
     * @param filter the SelectedProjectsFilter to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitSelectedProjectsFilter(SelectedProjectsFilter filter) throws VisitException {

        // The name of the parameters containing the selected project space IDs
        String inputName = "selectedProjectID_" + filter.getID();

        filter.clear();

        // Grab the selected IDs from the request
        // if there are some, then we select the filter and set its selected
        // IDs
        String[] selectedProjectIDs = request.getParameterValues(inputName);
        if (selectedProjectIDs != null && selectedProjectIDs.length > 0) {
            filter.setSelected(true);
            filter.setSelectedProjectSpaceIDs(selectedProjectIDs);
        }

        String ignoreOtherFilters = request.getParameter(inputName + "_ignore_other_filters");
        if (!JSPUtils.isEmpty(ignoreOtherFilters)) {
            filter.setIgnoreOtherFilters(true);
        }
    }

    /**
     * Visits the ProjectVisibilityFilter and updates its properties from the current
     * request.
     * @param filter the ProjectVisibilityFilter to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitProjectVisibilityFilter(ProjectVisibilityFilter filter) throws VisitException {

        String inputName = "projectVisibilityID_" + filter.getID();

        filter.clear();

        // Grab the selected value from the request
        String selectedProjectVisibilityID = request.getParameter(inputName);

        if (selectedProjectVisibilityID != null && selectedProjectVisibilityID.trim().length() > 0) {
            filter.setSelected(true);
            filter.setProjectVisibility(ProjectVisibility.findByID(selectedProjectVisibilityID));
        }

    }

    /**
     * Visits the MoneyFilter and updates its properties from the current
     * request.
     * @param filter the MoneyFilter to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitMoneyFilter(MoneyFilter filter) throws VisitException {

        // Deselect the filter; it will only be selected if we find some values
        filter.clear();

        // First get the Value part HTML
        String numberString = request.getParameter("filter" + filter.getID() + "_value");
        try {
            if ((numberString != null) && (numberString.trim().length() > 0)) {
                filter.getValueFilter().setSelected(true);
                filter.getValueFilter().setNumber(NumberFormat.getInstance().parseNumber(numberString));
            }

            String comparatorString = request.getParameter("filter"+filter.getID()+"_value_comparator");
            if ((comparatorString != null) && (comparatorString.trim().length() > 0)) {
                filter.getValueFilter().setComparator(NumberComparator.getForID(comparatorString));
            }
        } catch (ParseException e) {
            throw new VisitException("Invalid number " + numberString);
        }

        // Check to see if empty values should be included
        String isIncludeEmptyOptionParameter = request.getParameter("filter" + filter.getID() + "_value_isEmptyOptionSelected");
        if (!JSPUtils.isEmpty(isIncludeEmptyOptionParameter)) {
            filter.getValueFilter().setEmptyOptionSelected(true);
            filter.getValueFilter().setSelected(true);
        }

        // Now get the Currency part HTML

        // Grab the selected values from the request
        // If there are some, then we select the filter and set its selected
        // values
        String[] submittedValues = request.getParameterValues("filter" + filter.getID() + "_currency");
        if (submittedValues != null && submittedValues.length > 0) {
            filter.getCurrencyFilter().setSelected(true);
            filter.getCurrencyFilter().setSelectedDomainOptions(submittedValues);
        }

        // If we find a comparator, then we set it also
        String comparatorString = request.getParameter("filter"+filter.getID()+"_currency_comparator");
        if ((comparatorString != null) && (comparatorString.trim().length() > 0)) {
            filter.getCurrencyFilter().setComparator(DomainComparator.getForID(comparatorString));
        }

        // Check to see if empty values should be included
        isIncludeEmptyOptionParameter = request.getParameter("filter" + filter.getID() + "_currency_isEmptyOptionSelected");
        if (!JSPUtils.isEmpty(isIncludeEmptyOptionParameter)) {
            filter.getCurrencyFilter().setEmptyOptionSelected(true);
            filter.getCurrencyFilter().setSelected(true);
        }

    }

    /**
     * Visits the EmptyFinderFilter.
     * There are no properties to update.
     * @param filter the EmptyFinderFilter to visit
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitEmptyFinderFilter(EmptyFinderFilter filter) throws VisitException {
        // No action
    }

    //
    // Groupings
    //

    /**
     * Visits the <code>FinderGroupingList</code> and updates its properties
     * and the properties of each <code>FinderGrouping</code> in the list from
     * the current request.
     * @param groupingList the FinderGroupingList containing the FinderGroupings to
     * update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitFinderGroupingList(FinderGroupingList groupingList) throws VisitException {
        for (Iterator it = groupingList.getAllGroupings().iterator(); it.hasNext(); ) {
            FinderGrouping fg = (FinderGrouping)it.next();
            fg.accept(this);
        }
    }

    /**
     * Visits the FinderGrouping and updates its properties from the current
     * request.
     * @param grouping the FinderGrouping to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitFinderGrouping(FinderGrouping grouping) throws VisitException {
        grouping.setSelected(grouping.getID().equals(request.getParameter("grouping")));
    }

    /**
     * Visits the EmptyFinderGrouping and updates its properties from the current
     * request.
     * @param grouping the EmptyFinderGrouping to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitEmptyFinderGrouping(EmptyFinderGrouping grouping) throws VisitException {
        grouping.setSelected(grouping.getID().equals(request.getParameter("grouping")));
    }

    //
    // Sorters
    //

    /**
     * Visits the <code>FinderSorterList</code> and updates its properties
     * and the properties of each <code>FinderSorter</code> in the list from
     * the current request.
     * @param sorterList the FinderSorterList containing the FinderSorters to
     * update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitFinderSorterList(FinderSorterList sorterList) throws VisitException {
        for (Iterator it = sorterList.getAllSorters().iterator(); it.hasNext(); ) {
            FinderSorter currentSorter = (FinderSorter)it.next();
            currentSorter.accept(this);
        }
    }

    /**
     * Visits the FinderSorter and updates its properties from the current
     * request.
     * @param sorter the FinderSorter to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitFinderSorter(FinderSorter sorter) throws VisitException {
        //Indicate whether this sorter has been selected by the user
        String selectedProperty = request.getParameter(sorter.getID()+"selected");
        sorter.setSelected(((selectedProperty != null) && (selectedProperty.equals("true"))));

        // Now grab the id of the column selected, which is really its name
        // We don't use column ID since only persistable sorters require
        // columns to have IDs
        // There are many sorters and columns that are not persisted
        String selectedID = request.getParameter(sorter.getID());
        sorter.setSelectedColumnByName(selectedID);

        //Determine the order of this column
        String order = request.getParameter(sorter.getID()+"order");
        sorter.setDescending((order != null) && (order.equals("desc")));
    }

    //
    // Columns
    //

    /**
     * Visits the <code>FinderColumnList</code> and updates its properties
     * and the properties of each <code>FinderColumn</code> in the list from
     * the current request.
     * @param columnList the FinderColumnList containing the FinderColumns to
     * update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitFinderColumnList(FinderColumnList columnList) throws VisitException {
        for (Iterator it = columnList.getAllColumns().iterator(); it.hasNext(); ) {
            FinderColumn currentColumn = (FinderColumn) it.next();
            currentColumn.accept(this);
        }
    }

    /**
     * Visits the FinderColumn and updates its properties from the current
     * request.
     * @param column the FinderColumn to update
     * @throws VisitException if there is a problem consuming the HTML
     */
    public void visitFinderColumn(FinderColumn column) throws VisitException {
        column.setSelected(false);
    }

}
