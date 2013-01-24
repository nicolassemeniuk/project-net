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
 package net.project.resource.mvc.handler;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.finder.EmptyFinderFilter;
import net.project.base.finder.FilterOperator;
import net.project.base.finder.FinderFilter;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.NumberComparator;
import net.project.base.finder.PercentFilter;
import net.project.base.finder.StringDomainFilter;
import net.project.base.finder.TextComparator;
import net.project.base.finder.TextFilter;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.resource.AssignmentFinder;
import net.project.resource.AssignmentStatus;
import net.project.resource.AssignmentType;
import net.project.resource.filters.assignments.AssignmentTypesFilter;
import net.project.resource.filters.assignments.ComingDueFilter;
import net.project.resource.filters.assignments.InProcessFilter;
import net.project.resource.filters.assignments.LateAssignmentFilter;
import net.project.resource.filters.assignments.ShouldHaveStartedFilter;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.util.DateFormat;
import net.project.util.DateRange;
import net.project.util.ErrorReporter;
import net.project.util.NumberFormat;
import net.project.util.Validator;

/**
 * This handler will take user information about how they want to filter the
 * assignment list and create the appropriate finder filters.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class PersonalAssignmentsFilterHandler extends Handler {
    public static String PERCENT_COMPLETE_NAME = "percentComplete";
    public static String PERCENT_COMPLETE_COMPARATOR_NAME = "percentCompleteComparator";
    public static String ASSIGNMENT_TYPES_NAME = "filterType";
    public static String SPACE_NAME = "filterSpace";
    public static String ASSIGNMENT_COMPARATOR_NAME = "filterNameComparator";
    public static String ASSIGNMENT_NAME = "filterName";
    public static String LATE_ASSIGNMENTS_NAME = "filterLateAssignments";
    public static String COMING_DUE_NAME = "filterAssignmentsComingDue";
    public static String SHOULD_HAVE_STARTED_NAME = "filterShouldHaveStarted";
    public static String IN_PROGRESS_NAME = "filterInProgress";

    public static String STATUS_NAME = "filterStatus";
    public static String ASSIGNMENT_START_NAME = "assignmentDateStart";
    public static String ASSIGNMENT_END_NAME = "assignmentDateEnd";

    private ErrorReporter errorReporter = new ErrorReporter();

    public PersonalAssignmentsFilterHandler(HttpServletRequest request) {
        super(request);
    }

    public String getViewName() {
        return "/servlet/AssignmentController/PersonalAssignments";
    }

    public void validateSecurity(int module, int action, String objectID,
        HttpServletRequest request) throws AuthorizationFailedException, PnetException {

        AccessVerifier.verifyAccess(Module.PERSONAL_SPACE, Action.VIEW);
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        model.put("module", String.valueOf(Module.PERSONAL_SPACE));
        model.put("action", String.valueOf(Action.VIEW));

        model.put("showFiltersDropDown", "true");

        FinderFilterList filters = new FinderFilterList(FilterOperator.AND);
        filters.setSelected(true);
        filters.add(getSpaceIDFilter(request));
        filters.add(getAssignmentTypesFilter(request));

        filters.add(getLateFilter(request));
        filters.add(getComingDueFilter(request));
        filters.add(getShouldHaveStartedFilter(request));
        filters.add(getInProgressFilter(request));

        filters.add(getAssignmentStatusFilter(request));
        filters.add(getAssignedDatesFilter(request));
        filters.add(getPercentCompleteFilter(request));
        filters.add(getNameFilter(request));

        request.getSession().setAttribute("personalAssignmentFilterList", filters);
        request.getSession().setAttribute("errorReporter", errorReporter);

        return model;
    }

    /**
     *
     * @param request
     * @return
     */
    private FinderFilter getAssignedDatesFilter(HttpServletRequest request) {
        FinderFilter filter = new EmptyFinderFilter(ASSIGNMENT_START_NAME);

        DateFormat df = DateFormat.getInstance();
        Date rangeStart = null, rangeEnd = null;
        boolean createFilter = false;
        String endDateParameter = request.getParameter(ASSIGNMENT_END_NAME);
        String startDateParameter = request.getParameter(ASSIGNMENT_START_NAME);

        if (!Validator.isBlankOrNull(startDateParameter)) {
            try {
                rangeStart = df.parseDateString(startDateParameter);
                createFilter = true;
            } catch (Exception e) {
                errorReporter.addError(PropertyProvider.get("prm.personal.assignments.invalidstartdate.message", request.getParameter(ASSIGNMENT_START_NAME)));
            }
        }

        if (!Validator.isBlankOrNull(endDateParameter)) {
            try {
                rangeEnd = df.parseDateString(endDateParameter);
                createFilter = true;
            } catch (Exception e) {
                errorReporter.addError(PropertyProvider.get("prm.personal.assignments.invalidenddate.message", request.getParameter(ASSIGNMENT_END_NAME)));
            }
        }

        if (createFilter) {
            DateRange dateRange= new DateRange(rangeStart, rangeEnd);
            filter = dateRange.getElapsedTimeFilter(ASSIGNMENT_START_NAME, AssignmentFinder.START_DATE_COLUMN, AssignmentFinder.END_DATE_COLUMN);
        }

        return filter;
    }

    private FinderFilter getAssignmentStatusFilter(HttpServletRequest request) {
        StringDomainFilter statusFilter = new StringDomainFilter(STATUS_NAME, "", AssignmentFinder.STATUS_ID_COLUMN, (TextComparator)TextComparator.EQUALS);
        String[] statusValues = request.getParameterValues(STATUS_NAME);
        statusFilter.setSelected(statusValues.length > 0 && !Validator.isBlankOrNull(statusValues[0]));

        //See if the user has sent the "allOpen" value
        List statusValuesList = new ArrayList(Arrays.asList(statusValues));
        if (statusValuesList.contains("allOpen")) {
            statusValuesList.remove("allOpen");

            for (Iterator it = AssignmentStatus.OPEN_STATUSES.iterator(); it.hasNext();) {
                AssignmentStatus assignmentStatus = (AssignmentStatus) it.next();
                statusValuesList.add(assignmentStatus.getID());
            }
        }
        String[] arrayValues = new String[statusValuesList.size()];
        statusFilter.setSelectedValues((String[])statusValuesList.toArray(arrayValues));

        return statusFilter;
    }

    private FinderFilter getInProgressFilter(HttpServletRequest request) {
        InProcessFilter inProcessFilter = new InProcessFilter(IN_PROGRESS_NAME);
        inProcessFilter.setSelected(!Validator.isBlankOrNull(request.getParameter(IN_PROGRESS_NAME)));

        return inProcessFilter;
    }

    private FinderFilter getShouldHaveStartedFilter(HttpServletRequest request) {
        ShouldHaveStartedFilter filter = new ShouldHaveStartedFilter(SHOULD_HAVE_STARTED_NAME);
        filter.setSelected(!Validator.isBlankOrNull(request.getParameter(SHOULD_HAVE_STARTED_NAME)));

        return filter;
    }

    private FinderFilter getComingDueFilter(HttpServletRequest request) {
        ComingDueFilter comingDueFilter = new ComingDueFilter(COMING_DUE_NAME);
        comingDueFilter.setSelected(!Validator.isBlankOrNull(request.getParameter(COMING_DUE_NAME)));

        return comingDueFilter;
    }

    private FinderFilter getLateFilter(HttpServletRequest request) {
        LateAssignmentFilter filter = new LateAssignmentFilter(LATE_ASSIGNMENTS_NAME);
        filter.setSelected(!Validator.isBlankOrNull(request.getParameter(LATE_ASSIGNMENTS_NAME)));

        return filter;
    }

    private FinderFilter getPercentCompleteFilter(HttpServletRequest request) {
        NumberFormat nf = NumberFormat.getInstance();
        boolean isPercentCompleteBlank = Validator.isBlankOrNull(request.getParameter(PERCENT_COMPLETE_NAME));

        FinderFilter percentCompleteFilter = new EmptyFinderFilter(PersonalAssignmentsFilterHandler.PERCENT_COMPLETE_NAME);

        if (!isPercentCompleteBlank) {
            try {
                Number percentNumber = nf.parseNumber(request.getParameter(PERCENT_COMPLETE_NAME));
                
                percentCompleteFilter = new PercentFilter(PersonalAssignmentsFilterHandler.PERCENT_COMPLETE_NAME, AssignmentFinder.PERCENT_COMPLETE_COLUMN, false, true);
                percentCompleteFilter.setSelected(!isPercentCompleteBlank);
                ((PercentFilter)percentCompleteFilter).setComparator(NumberComparator.getForID(request.getParameter(PersonalAssignmentsFilterHandler.PERCENT_COMPLETE_COMPARATOR_NAME)));
                ((PercentFilter)percentCompleteFilter).setNumber(percentNumber);
            } catch (ParseException e) {
                errorReporter.addError(MessageFormat.format(PropertyProvider.get("prm.assignments.invalidnumber.message"), new Object[] {request.getParameter(PERCENT_COMPLETE_NAME)}));
            }
        } 

        return percentCompleteFilter;
    }

    private FinderFilter getAssignmentTypesFilter(HttpServletRequest request) {
        AssignmentTypesFilter assignmentTypeFilter = new AssignmentTypesFilter(ASSIGNMENT_TYPES_NAME);
        assignmentTypeFilter.setSelected(true);

        String[] assignmentTypes = request.getParameterValues(ASSIGNMENT_TYPES_NAME);
        if(assignmentTypes == null || Arrays.asList(assignmentTypes).contains("all"))
            assignmentTypeFilter.setAssignmentTypes(AssignmentType.ALL);
        else
            assignmentTypeFilter.setAssignmentTypes(assignmentTypes);

        return assignmentTypeFilter;
    }

    private FinderFilter getSpaceIDFilter(HttpServletRequest request) {
        String[] spaceIDs = request.getParameterValues(SPACE_NAME);
        StringDomainFilter spaceIDFilter = new StringDomainFilter(SPACE_NAME, "", AssignmentFinder.SPACE_ID_COLUMN, (TextComparator)TextComparator.EQUALS);
        spaceIDFilter.setSelected(spaceIDs != null && !Validator.isBlankOrNull(spaceIDs[0]));
        spaceIDFilter.setSelectedValues(spaceIDs);

        return spaceIDFilter;
    }

    private FinderFilter getNameFilter(HttpServletRequest request) {
        TextFilter filter = new TextFilter(ASSIGNMENT_NAME, AssignmentFinder.OBJECT_NAME_COLUMN, false);
        filter.setSelected(!Validator.isBlankOrNull(request.getParameter(ASSIGNMENT_NAME)));
        filter.setComparator(TextComparator.getForID(request.getParameter(ASSIGNMENT_COMPARATOR_NAME)));
        filter.setValue(request.getParameter(ASSIGNMENT_NAME));

        return filter;
    }
}
