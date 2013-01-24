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

 package net.project.resource.mvc.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.finder.DateFilter;
import net.project.base.finder.FinderFilter;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.NumberComparator;
import net.project.base.finder.NumberFilter;
import net.project.base.finder.PercentFilter;
import net.project.base.finder.StringDomainFilter;
import net.project.base.finder.TextFilter;
import net.project.base.mvc.Handler;
import net.project.calendar.PnCalendar;
import net.project.persistence.PersistenceException;
import net.project.portfolio.ProjectPortfolioBean;
import net.project.resource.AssignmentFinder;
import net.project.resource.AssignmentManagerBean;
import net.project.resource.AssignmentType;
import net.project.resource.filters.assignments.AssignmentTypesFilter;
import net.project.resource.filters.assignments.ComingDueFilter;
import net.project.resource.filters.assignments.InProcessFilter;
import net.project.resource.filters.assignments.LateAssignmentFilter;
import net.project.resource.filters.assignments.ShouldHaveStartedFilter;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.PersonalSpace;
import net.project.space.Space;
import net.project.util.DateFormat;
import net.project.util.ErrorReporter;
import net.project.util.NumberFormat;

public class PersonalAssignmentsHandler extends Handler {
    private Date startDate = null;

    public PersonalAssignmentsHandler(HttpServletRequest request) {
        super(request);
    }

    public String getViewName() {
        String mode = request.getParameter("mode");
        if(mode != null && "2".equals(mode))
            return "/resource/PopupPersonalAssignments.jsp";
        return "/resource/PersonalAssignments.jsp";
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        AccessVerifier.verifyAccess(Module.PERSONAL_SPACE, Action.VIEW);
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        User user = SessionManager.getUser();
        passThru(model, "showFiltersDropDown");

        //Make sure we are in the personal space
        ensurePersonalSpace(request, user);

        //Make sure there is an error reporter
        if (request.getSession().getAttribute("errorReporter") == null) {
            request.getSession().setAttribute("errorReporter", new ErrorReporter());
        }

        model.put("allocationSummaryStartTime", getAllocationSummaryStartTime());

        //Set up default filter values
        ensureFilter(request);

        //Load the assignments
        AssignmentManagerBean assignmentManagerBean = loadAssignments(request, user);
        model.put("assignmentManagerBean", assignmentManagerBean);

        passThru(model, "status");

        //Load the list of the user's projects to populate the "Space" filter.
        ProjectPortfolioBean portfolio = loadProjectPortfolio(user);
        model.put("portfolio", portfolio);

        //Load values into the model to fill in the filters with what the user
        //has previously selected
        passFilterValues(model);

        return model;
    }

    private String getAllocationSummaryStartTime() {
        long startOfMonth;
        PnCalendar cal = new PnCalendar();

        if (startDate == null) {
            startOfMonth = cal.startOfMonth(new Date()).getTime();
        } else {
            startOfMonth = cal.startOfMonth(startDate).getTime();
        }

        return String.valueOf(startOfMonth);
    }

    private void passFilterValues(Map model) {
        FinderFilterList filterList = (FinderFilterList)request.getSession().getAttribute("personalAssignmentFilterList");

        NumberFormat nf = NumberFormat.getInstance();
        DateFormat df = DateFormat.getInstance();

        StringDomainFilter spaceIDFilter = (StringDomainFilter)filterList.deepSearch(PersonalAssignmentsFilterHandler.SPACE_NAME);
        model.put(PersonalAssignmentsFilterHandler.SPACE_NAME, (spaceIDFilter != null && spaceIDFilter.getSelectedValues() != null ? spaceIDFilter.getSelectedValues() : new String[]{}));

        AssignmentTypesFilter assignmentTypesFilter = (AssignmentTypesFilter)filterList.deepSearch(PersonalAssignmentsFilterHandler.ASSIGNMENT_TYPES_NAME);
        model.put(PersonalAssignmentsFilterHandler.ASSIGNMENT_TYPES_NAME, assignmentTypesFilter.getAssignmentTypes());

        LateAssignmentFilter lateAssignmentFilter =  (LateAssignmentFilter)filterList.deepSearch(PersonalAssignmentsFilterHandler.LATE_ASSIGNMENTS_NAME);
        model.put(PersonalAssignmentsFilterHandler.LATE_ASSIGNMENTS_NAME, new Boolean(lateAssignmentFilter != null ? lateAssignmentFilter.isSelected() : false));

        ComingDueFilter comingDueFilter = (ComingDueFilter)filterList.deepSearch(PersonalAssignmentsFilterHandler.COMING_DUE_NAME);
        model.put(PersonalAssignmentsFilterHandler.COMING_DUE_NAME, new Boolean(comingDueFilter != null ? comingDueFilter.isSelected() : false));

        ShouldHaveStartedFilter shouldHaveStartedFilter = (ShouldHaveStartedFilter)filterList.deepSearch(PersonalAssignmentsFilterHandler.SHOULD_HAVE_STARTED_NAME);
        model.put(PersonalAssignmentsFilterHandler.SHOULD_HAVE_STARTED_NAME, new Boolean(shouldHaveStartedFilter != null ? shouldHaveStartedFilter.isSelected() : false));

        InProcessFilter inProcessFilter = (InProcessFilter)filterList.deepSearch(PersonalAssignmentsFilterHandler.IN_PROGRESS_NAME);
        model.put(PersonalAssignmentsFilterHandler.IN_PROGRESS_NAME, new Boolean(inProcessFilter != null ? inProcessFilter.isSelected() : false));

        StringDomainFilter statusFilter = (StringDomainFilter)filterList.deepSearch(PersonalAssignmentsFilterHandler.STATUS_NAME);
        model.put(PersonalAssignmentsFilterHandler.STATUS_NAME, (statusFilter != null ? statusFilter.getSelectedValues() : new String[] {}));

        FinderFilter assignedFilter = filterList.deepSearch(PersonalAssignmentsFilterHandler.ASSIGNMENT_START_NAME);
        String startDate = "", endDate = "";
        if (assignedFilter instanceof FinderFilterList) {
            FinderFilterList assignedDatesFilter = (FinderFilterList)assignedFilter;
            DateFilter startDateFilter = (assignedDatesFilter != null ? (DateFilter)assignedDatesFilter.deepSearch("startDate") : null);
            DateFilter endDateFilter = (assignedDatesFilter != null ? (DateFilter)assignedDatesFilter.deepSearch("endDate") : null);
            startDate = (endDateFilter != null ? df.formatDate(endDateFilter.getDateRangeStart()) : "");
            endDate = (startDateFilter != null ? df.formatDate(startDateFilter.getDateRangeFinish()) : "");

            //Save the start date for later
            this.startDate = (endDateFilter != null ? endDateFilter.getDateRangeStart() : null);
        }
        model.put(PersonalAssignmentsFilterHandler.ASSIGNMENT_START_NAME, startDate);
        model.put(PersonalAssignmentsFilterHandler.ASSIGNMENT_END_NAME, endDate);

        FinderFilter percentFilter = filterList.deepSearch(PersonalAssignmentsFilterHandler.PERCENT_COMPLETE_NAME);
        String percent = "", comparator = "";
        if (percentFilter instanceof NumberFilter) {
            NumberFilter percentCompleteFilter = (NumberFilter) percentFilter;
            Number percentComplete = percentCompleteFilter.getNumber();
            percent = (percentComplete == null ? "" : nf.formatNumber(percentComplete.longValue()));
            comparator = percentCompleteFilter.getComparator().getID();
        }
        model.put(PersonalAssignmentsFilterHandler.PERCENT_COMPLETE_NAME, percent);
        model.put(PersonalAssignmentsFilterHandler.PERCENT_COMPLETE_COMPARATOR_NAME, comparator);

        TextFilter nameFilter = (TextFilter)filterList.deepSearch(PersonalAssignmentsFilterHandler.ASSIGNMENT_NAME);
        model.put(PersonalAssignmentsFilterHandler.ASSIGNMENT_NAME, (nameFilter != null ? nameFilter.getValue() : ""));
        model.put(PersonalAssignmentsFilterHandler.ASSIGNMENT_COMPARATOR_NAME, (nameFilter != null ? nameFilter.getComparator().getID() : ""));
    }

    private void ensureFilter(HttpServletRequest request) {
        FinderFilterList filterList = (FinderFilterList)request.getSession().getAttribute("personalAssignmentFilterList");

        if (filterList == null) {
            filterList = new FinderFilterList();
            request.getSession().setAttribute("personalAssignmentFilterList", filterList);

            //Default filters
            PercentFilter percentCompleteFilter = new PercentFilter(PersonalAssignmentsFilterHandler.PERCENT_COMPLETE_NAME, AssignmentFinder.PERCENT_COMPLETE_COLUMN, false, true);
            percentCompleteFilter.setSelected(true);
            percentCompleteFilter.setComparator(NumberComparator.LESS_THAN);
            percentCompleteFilter.setNumber(100);
            filterList.add(percentCompleteFilter);

            AssignmentTypesFilter assignmentTypesFilter = new AssignmentTypesFilter(PersonalAssignmentsFilterHandler.ASSIGNMENT_TYPES_NAME);
            assignmentTypesFilter.setSelected(true);
            assignmentTypesFilter.setAssignmentTypes(Arrays.asList(new AssignmentType[] {AssignmentType.TASK}));
            filterList.add(assignmentTypesFilter);
        }
    }

    private ProjectPortfolioBean loadProjectPortfolio(User user) throws PersistenceException {
        ProjectPortfolioBean portfolio = new ProjectPortfolioBean();
        portfolio.setID(user.getMembershipPortfolioID());
        portfolio.setUser(user);
        portfolio.load();

        return portfolio;
    }

    private AssignmentManagerBean loadAssignments(HttpServletRequest request, User user) throws PersistenceException {
        AssignmentManagerBean assignmentManagerBean = new AssignmentManagerBean();
        String space_id = request.getParameter("space");
        if(space_id == null)
            space_id = user.getCurrentSpace().getID();
        if(space_id.equals(user.getID()))
           space_id = null;
        assignmentManagerBean.setSpaceID(space_id);
        assignmentManagerBean.setPersonID(user.getID());
        assignmentManagerBean.setOrderBy(AssignmentFinder.END_DATE_COLUMN);
        assignmentManagerBean.setOrderDescending(false);

        FinderFilterList filterList = (FinderFilterList)request.getSession().getAttribute("personalAssignmentFilterList");
        if (filterList != null) {
            assignmentManagerBean.addFilters(filterList);
        }
        assignmentManagerBean.loadAssignments();

        return assignmentManagerBean;
    }

    /**
     * See if the user is currently in the personal space.  If not, change space
     * to the personal space.
     *
     * @param request a <code>HttpServletRequest</code> containing the
     * information passed in the request.
     * @throws PnetException if the current space cannot be set.
     */
    private void ensurePersonalSpace(HttpServletRequest request, User user) throws PnetException {
        // Check the security based on the space
        String mySpace = user.getCurrentSpace().getType();
        if (!mySpace.equals(Space.PERSONAL_SPACE)) {
            user.setCurrentSpace((PersonalSpace)request.getSession().getAttribute("personalSpace"));
            mySpace=user.getCurrentSpace().getType();
        }
    }
}
