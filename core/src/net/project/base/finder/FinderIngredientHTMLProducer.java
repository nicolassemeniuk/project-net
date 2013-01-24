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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 20616 $
|       $Date: 2010-03-27 11:34:26 -0300 (sáb, 27 mar 2010) $
|     $Author: ritesh $
|
+-----------------------------------------------------------------------------*/
package net.project.base.finder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;

import net.project.base.money.MoneyFilter;
import net.project.base.property.PropertyProvider;
import net.project.form.report.FormFieldFilter;
import net.project.gui.calendar.CalendarPopup;
import net.project.portfolio.view.ProjectVisibilityFilter;
import net.project.portfolio.view.SelectedProjectsFilter;
import net.project.project.ProjectSpace;
import net.project.project.ProjectVisibility;
import net.project.resource.filters.assignments.SpaceFilter;
import net.project.schedule.report.FilterUser;
import net.project.schedule.report.UserFilter;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;
import net.project.util.NumberFormat;
import net.project.util.VisitException;

/**
 * Class to produce default HTML for finder ingredients.  To use this class,
 * instantiate it and pass it to the accept method of the filter, grouping, or
 * sorter for which you want to produce HTML.  For example:
 * <pre><code>
 *   FinderIngredientHTMLProducer htmlProducer = new FinderIngredientHTMLProducer();
 *   finderFilterList.accept(htmlProducer);
 *   out.println(htmlProducer.getHTML());
 * </code></pre>
 *
 * @author Matthew Flower
 * @author Tim Morrow
 * @since Version 7.4
 */
public class FinderIngredientHTMLProducer extends EmptyFinderIngredientVisitor {

    /**
     * The maximum height of any selection list, currently <code>4</code>.
     */
    private static int MAXIMUM_LIST_HEIGHT = 4;

    /**
     * The minimum height of a selection list, currently <code>2</code>.
     */
    private static int MINIMUM_LIST_HEIGHT = 2;

    /**
     * Tracks whether the current FinderFilter is being invoked at the outermost
     * level.  This is used to line up all filters correctly.
     */
    private boolean isOuterFilter = false;

    /** String buffer to hold the html that this class is gathering. */
    protected StringBuffer html;

    /**
     * Standard constructor which creates a new instance of a
     * FinderIngredientHTMLProducer class.
     */
    public FinderIngredientHTMLProducer() {
        html = new StringBuffer();
    }

    /**
     * Get the HTML that this producer has produced thus far.
     * @return
     */
    public String getHTML() {
        return html.toString();
    }

    //--------------------------------------------------------------------------
    // Visit Methods for FILTERS
    //--------------------------------------------------------------------------

    /**
     * Iterate through all of the filters in the filter list and call visit their
     * children so they can produce HTML as necessary.
     * <p>
     * Currently produces a complete HTML table containing each filter on
     * a separate line.
     * </p>
     *
     * @param filterList the <code>FinderFilterList</code> over which we are going to
     * iterate
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitFinderFilterList(FinderFilterList filterList) throws VisitException {

        html.append("<table width=\"100%\" border=\"0\" class=\"tableContent\" style=\"padding-left: 5px;\">");

        for (Iterator it = filterList.getAllFilters().iterator(); it.hasNext(); ) {
            FinderFilter currentFilter = (FinderFilter)it.next();

            html.append("<tr>");
            this.isOuterFilter = true;
            currentFilter.accept(this);
            html.append("</tr>");
        }

        html.append("</table>");
    }


    /**
     * Produce HTML for a checkbox filter.
     * This includes the enclosing filter's HTML.
     *
     * @param filter the <code>CheckboxFilter</code> for which we are going to produce HTML.
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitCheckboxFilter(CheckboxFilter filter) throws VisitException {
        html.append("<tr>");

        // The Checkbox
        html.append("<td class=\"tableContent\">");
        html.append("<input type=\"checkbox\" name=\"checkbox").append(filter.getID()).append("\"")
                .append((filter.isSelected() ? " checked" : ""))
                .append(">");
        html.append("</td>");

        // The enclosing filter
        this.isOuterFilter = false;
        filter.getEnclosedFilter().accept(this);

        html.append("</tr>");
    }

    /**
     * Produces HTML for a <code>RadioButtonFilter</code>.
     * @param radioButtonFilter the <code>RadioButtonFilter</code> for which HTML is required
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitRadioButtonFilter(RadioButtonFilter radioButtonFilter) throws VisitException {

        html.append("<tr>");

        html.append("<td colspan=\"4\">");
        html.append("<table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");

        for (Iterator it = radioButtonFilter.getAllFilters().iterator(); it.hasNext(); ) {
            FinderFilter currentFilter = (FinderFilter) it.next();

            html.append("<tr valign=\"top\">");

            // Radio button input element
            html.append("<td class=\"tableContent\" width=\"5px\" >");
            html.append("<input type=\"radio\" name=\"filter").append(radioButtonFilter.getID())
                .append("\" value=\"").append(currentFilter.getID()).append("\"")
                .append((currentFilter.isSelected() ? " checked" : "")).append(">");
            html.append("</td>");

            // Actual filter
            this.isOuterFilter = false;
            currentFilter.accept(this);

            html.append("</tr>");
        }

        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");
    }

    /**
     * Produce HTML for a <code>DateFilter</code>.
     *
     * @param filter the <code>DateFilter</code> for which we are going to
     * produce HTML.
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitDateFilter(DateFilter filter) throws VisitException {
        DateFormat df = SessionManager.getUser().getDateFormatter();

        if (isOuterFilter) {
            html.append("<td>&nbsp;</td>");
        }

        // Label
        html.append("<td class=\"tableContent\">").append(filter.getName()).append("</td>");

        // Input fields
        html.append("<td class=\"tableContent\" colspan=\"2\">");
        html.append("<input type=\"text\" size=\"20\" maxlength=\"40\" name=\"filter")
            .append(filter.getID())
            .append("Start\" value=\"").append(df.formatDate(filter.getDateRangeStart()))
            .append("\">");
        html.append(CalendarPopup.getCalendarPopupHTML("filter"+filter.getID()+"Start", null));
        html.append(" - ");
        html.append("<input type=\"text\" size=\"20\" maxlength=\"40\" name=\"filter")
            .append(filter.getID())
            .append("Finish\" value=\"").append(df.formatDate(filter.getDateRangeFinish()))
            .append("\">");
        html.append(CalendarPopup.getCalendarPopupHTML("filter"+filter.getID()+"Finish", null));

        // Include a checkbox if we must include the option to include
        // empty values
        if (filter.isIncludeEmptyOption()) {
            html.append(makeCheckbox("filter" + filter.getID() + "_isEmptyOptionSelected", "true", "prm.global.finder.filter.emptyoption.label", filter.isEmptyOptionSelected()));
        }

        html.append("</td>");

    }

    /**
     * Produce HTML for a <code>EmptyFinderFilter</code> filter.
     * Includes the filter's name.
     *
     * @param filter the <code>EmptyFinderFilter</code> for which we are going
     * to produce HTML.
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitEmptyFinderFilter(EmptyFinderFilter filter) throws VisitException {
        if (isOuterFilter) {
            html.append("<td>&nbsp;</td>");
        }
        html.append("<td class=\"tableContent\" colspan=\"3\">").append(filter.getName()).append("</td>");
    }

    /**
     * Produces HTML for a <code>NumberFilter</code>.
     *
     * @param filter the <code>NumberFilter</code> for which HTML is required
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitNumberFilter(NumberFilter filter) throws VisitException {
        NumberFormat nf = NumberFormat.getInstance();

        if (isOuterFilter) {
            html.append("<td>&nbsp;</td>");
        }

        // Label
        html.append("<td class=\"tableContent\">").append(filter.getName()).append("</td>");

        // Comparator selection
        html.append("<td class=\"tableContent\">");
        html.append("<select name=\"filter").append(filter.getID()).append("comparator\">");
        html.append(NumberComparator.getOptionList((filter.getComparator() != null ? filter.getComparator() : NumberComparator.DEFAULT)));
        html.append("</select>");
        html.append("</td>");

        // Input fields
        html.append("<td class=\"tableContent\">");
        html.append("<input type=\"text\" name=\"filter").append(filter.getID()).append("\"");
        html.append((filter.getNumber() == null ? "" : " value=\""+nf.formatNumber(filter.getNumber().doubleValue())+"\""));
        html.append(">");

        // Include a checkbox if we must include the option to include
        // empty values
        if (filter.isIncludeEmptyOption()) {
            html.append(makeCheckbox("filter" + filter.getID() + "_isEmptyOptionSelected", "true", "prm.global.finder.filter.emptyoption.label", filter.isEmptyOptionSelected()));
        }

        html.append("</td>");

    }

    /**
     * Produces HTML for a <code>TextFilter</code>.
     * @param filter the <code>TextFilter</code> for which HTML is required
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitTextFilter(TextFilter filter) throws VisitException {

        if (isOuterFilter) {
            html.append("<td>&nbsp;</td>");
        }

        // Label
        html.append("<td class=\"tableContent\">").append(filter.getName()).append("</td>");

        // Comparator selection
        html.append("<td class=\"tableContent\">");
        html.append("<select name=\"filter").append(filter.getID()).append("comparator\">");
        html.append(TextComparator.getOptionList((filter.getComparator() != null ? filter.getComparator() : TextComparator.DEFAULT)));
        html.append("</select>");
        html.append("</td>");

        // Input fields
        html.append("<td class=\"tableContent\">");
        html.append("<input type=\"text\" name=\"filter").append(filter.getID()).append("\"");
        html.append((filter.getValue() == null ? "" : " value=\"" + filter.getValue() + "\""));
        html.append(">");

        // Include a checkbox if we must include the option to include
        // empty values
        if (filter.isIncludeEmptyOption()) {
            html.append(makeCheckbox("filter" + filter.getID() + "_isEmptyOptionSelected", "true", "prm.global.finder.filter.emptyoption.label", filter.isEmptyOptionSelected()));
        }

        html.append("</td>");

    }

    /**
     * Produces HTML for a <code>DomainFilter</code>.
     * @param filter the DomainFilter for which HTML is required
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitDomainFilter(DomainFilter filter) throws VisitException {

        if (isOuterFilter) {
            html.append("<td>&nbsp;</td>");
        }

        // Label
        html.append("<td class=\"tableContent\">").append(filter.getName()).append("</td>");

        // Comparator selection
        html.append("<td class=\"tableContent\">");
        html.append("<select name=\"filter").append(filter.getID()).append("comparator\">");
        html.append(DomainComparator.getOptionList((filter.getComparator() != null ? filter.getComparator() : DomainComparator.DEFAULT)));
        html.append("</select>");
        html.append("</td>");

        // Select field
        html.append("<td class=\"tableContent\" valign=\"bottom\">");
        html.append("<select name=\"filter").append(filter.getID()).append("\" size=\"")
                .append(determineSelectionSize(filter.getAllDomainOptions())).append("\" multiple=\"true\">");
        // Add the options
        html.append(net.project.gui.html.HTMLOptionList.makeHtmlOptionList(filter.getAllDomainOptions(), filter.getSelectedDomainOptions()));
        html.append("</select>");

        // Include a checkbox if we must include the option to include
        // empty values
        if (filter.isIncludeEmptyOption()) {
            html.append(makeCheckbox("filter" + filter.getID() + "_isEmptyOptionSelected", "true", "prm.global.finder.filter.emptyoption.label", filter.isEmptyOptionSelected()));
        }

        html.append("</td>");

    }

    /**
     * Produce HTML for the <code>UserFilter</code>.
     * @param filter the UserFilter for which to produce HTML
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitUserFilter(UserFilter filter) throws VisitException {

        if (isOuterFilter) {
            html.append("<td>&nbsp;</td>");
        }

        // Label
        html.append("<td class=\"tableContent\" width=\"25%\">").append(filter.getName()).append("</td>");

        // Selection list
        html.append("<td class=\"tableContent\" colspan=\"2\">");
        html.append("<select name=\"filter").append(filter.getID()).append("\" size=\"")
            .append(determineSelectionSize(filter.getUserList())).append("\" multiple=\"true\">");

        for (Iterator it = filter.getUserList().iterator(); it.hasNext(); ) {
            FilterUser user = (FilterUser)it.next();
            html.append("<option value=\"").append(user.getID()).append("\"")
                .append((user.isSelected() ? " selected" : "")).append(">")
                .append(user.getDisplayName()).append("</option>");
        }

        //Finish creating the option list
        html.append("</select>");
        html.append("</td>");

    }

    /**
     * Produce HTML for the <code>FormFieldFilter</code>.
     * @param filter the FormFieldFilter for which to produce HTML
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitFormFieldFilter(FormFieldFilter filter) throws VisitException {

        if (isOuterFilter) {
            html.append("<td>&nbsp;</td>");
        }

        // Form Field HTML
        // Write the html presentation as the filter see it.
        // FormFields HTML already produces a <td>
        StringWriter htmlWriter = new StringWriter();
        try {
            filter.getFormField().writeFilterHtml(null, new PrintWriter(htmlWriter));
            html.append(htmlWriter.toString());
        } catch (IOException e) {
            throw new VisitException("Error producing HTML for Form Field: " + e, e);
        }

    }

    /**
     * Produces HTML for a <code>SelectedProjectsFilter</code>.
     * @param filter the filter for which to produce HTML
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitSelectedProjectsFilter(SelectedProjectsFilter filter) throws VisitException {

        // Draw the HTML for the available projects
        // Each project is a checkbox on a table row
        String inputName = "selectedProjectID_" + filter.getID();
        
        html.append("<tr><td class=\"tableContent\"><input type=\"checkbox\" name=\"" + inputName + "_ignore_other_filters\" " + (filter.isIgnoreOtherFilters() ? "checked" : "") + "></td>");
        html.append("<td class=\"tableContent\" colspan=\"2\">");
        html.append(PropertyProvider.get("prm.portfolio.project.filter.selectedprojectsfilter.ignoreotherfilters"));
        html.append("</td></tr>");

        //html.append("<tr><td class=\"tableContent\" colspan=\"3\">&nbsp;</td></tr>");
        //html.append("<tr><td class=\"tableContent\"><input type=\"checkbox\" name=\"select_all\" onClick=\"javascript:changeCheckedState(theForm.").append(inputName).append(", theForm.select_all.checked)\"></td>");
        //html.append("<td class=\"tableContent\" colspan=\"2\">");
        //html.append(PropertyProvider.get("prm.portfolio.project.filter.selectedprojectsfilter.selectall"));
        //html.append("</td></tr>");

        html.append("<tr>");
        html.append("<td width=\"1%\">&nbsp;</td>");
        html.append("<td class=\"tableContent\">");
        html.append(PropertyProvider.get("prm.portfolio.personal.view.edit.filterpage.selectedprojects.title"));
        html.append(":</td>");
        html.append("<td class=\"tableContent\">");
        
        //Selection list
        html.append("<select name=\"").append(inputName).append("\" size=\"")
        .append(determineSelectionSize(filter.getAllProjectSpaces())).append("\" multiple=\"true\">");
	    
        for (Iterator it = filter.getAllProjectSpaces().iterator(); it.hasNext(); ) {
        	ProjectSpace nextProjectSpace = (ProjectSpace) it.next();
	        html.append("<option value=\"").append(nextProjectSpace.getID()).append("\"")
	            .append((filter.isProjectSpaceSelected(nextProjectSpace.getID()) ? " selected" : "")).append(">")
	            .append((HTMLUtils.escape(nextProjectSpace.getName()).replaceAll("'", "&acute;"))).append("</option>");
	    }
	    
	    //Finish creating the option list
        html.append("</select>");
        html.append("</td>");
        html.append("</tr>");


    }

    /**
     * Produces HTML for a <code>ProjectVisibilityFilter</code>.
     * @param filter the filter for which to produce HTML
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitProjectVisibilityFilter(ProjectVisibilityFilter filter) throws VisitException {

        if (isOuterFilter) {
            html.append("<td>&nbsp;</td>");
        }

        String inputName = "projectVisibilityID_" + filter.getID();

        // Select the current visibility, or the default value.
        ProjectVisibility selectVisibility = filter.getProjectVisibility();
        if (selectVisibility == null) {
            selectVisibility = ProjectVisibility.PROJECT_PARTICIPANTS;
        }

        // Label
        html.append("<td class=\"tableContent\">").append(filter.getName()).append("</td>");

        // Radio group
        html.append("<td class=\"tableContent\" colspan=\"2\">");
        html.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");

        html.append("<tr>");
        html.append("<td class=\"tableContent\">");
        html.append("<input type=\"radio\" name=\"").append(inputName).append("\" value=\"")
                .append(ProjectVisibility.PROJECT_PARTICIPANTS.getID()).append("\"")
                .append((selectVisibility.equals(ProjectVisibility.PROJECT_PARTICIPANTS) ? " checked " : ""))
                .append(">&nbsp;");
        html.append(PropertyProvider.get("prm.portfolio.project.filter.visibilityfilter.myprojects"));
        html.append("</td>");
        html.append("</tr>");

        html.append("<tr>");
        html.append("<td class=\"tableContent\">");
        html.append("<input type=\"radio\" name=\"").append(inputName).append("\" value=\"")
                .append(ProjectVisibility.OWNING_BUSINESS_PARTICIPANTS.getID()).append("\"")
                .append((selectVisibility.equals(ProjectVisibility.OWNING_BUSINESS_PARTICIPANTS) ? " checked " : ""))
                .append(">&nbsp;");
        html.append(PropertyProvider.get("prm.portfolio.project.filter.visibilityfilter.mybusinessprojects"));
        html.append("</td>");
        html.append("</tr>");

        html.append("<tr>");
        html.append("<td class=\"tableContent\">");
        html.append("<input type=\"radio\" name=\"").append(inputName).append("\" value=\"")
                .append(ProjectVisibility.GLOBAL.getID()).append("\"")
                .append((selectVisibility.equals(ProjectVisibility.GLOBAL) ? " checked " : ""))
                .append(">&nbsp;");
        html.append(PropertyProvider.get("prm.portfolio.project.filter.visibilityfilter.global"));
        html.append("</td>");
        html.append("</tr>");

        html.append("</table>");
        html.append("</td>");

    }

    /**
     * Produces HTML for a <code>MoneyFilter</code>.
     *
     * @param filter the <code>MoneyFilter</code> for which HTML is required
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitMoneyFilter(MoneyFilter filter) throws VisitException {
        NumberFormat nf = NumberFormat.getInstance();

        if (isOuterFilter) {
            html.append("<td>&nbsp;</td>");
        }

        // First produce HTML for Value part

        // Label
        html.append("<td class=\"tableContent\">").append(filter.getValueFilter().getName()).append("</td>");

        // Comparator selection
        html.append("<td class=\"tableContent\">");
        html.append("<select name=\"filter").append(filter.getID()).append("_value_comparator\">");
        html.append(NumberComparator.getOptionList((filter.getValueFilter().getComparator() != null ? filter.getValueFilter().getComparator() : NumberComparator.DEFAULT)));
        html.append("</select>");
        html.append("</td>");

        // Input fields
        html.append("<td class=\"tableContent\">");
        html.append("<input type=\"text\" name=\"filter").append(filter.getID()).append("_value\"");
        html.append((filter.getValueFilter().getNumber() == null ? "" : " value=\""+nf.formatNumber(filter.getValueFilter().getNumber().doubleValue())+"\""));
        html.append(">");

        // Include a checkbox if we must include the option to include
        // empty values
        if (filter.getValueFilter().isIncludeEmptyOption()) {
            html.append(makeCheckbox("filter" + filter.getID() + "_value_isEmptyOptionSelected", "true", "prm.global.finder.filter.emptyoption.label", filter.getValueFilter().isEmptyOptionSelected()));
        }

        html.append("</td>");

        // Now produce HTML for the currency part

        html.append("</tr>");
        html.append("<tr>");

        html.append("<td>&nbsp;</td>");

        // Label
        html.append("<td class=\"tableContent\">").append(filter.getCurrencyFilter().getName()).append("</td>");

        // Comparator selection
        html.append("<td class=\"tableContent\">");
        html.append("<select name=\"filter").append(filter.getID()).append("_currency_comparator\">");
        html.append(DomainComparator.getOptionList((filter.getCurrencyFilter().getComparator() != null ? filter.getCurrencyFilter().getComparator() : DomainComparator.DEFAULT)));
        html.append("</select>");
        html.append("</td>");

        // Select field
        html.append("<td class=\"tableContent\" valign=\"bottom\">");
        html.append("<select name=\"filter").append(filter.getID()).append("_currency\" size=\"")
                .append(determineSelectionSize(filter.getCurrencyFilter().getAllDomainOptions())).append("\" multiple=\"true\">");
        // Add the options
        html.append(net.project.gui.html.HTMLOptionList.makeHtmlOptionList(filter.getCurrencyFilter().getAllDomainOptions(), filter.getCurrencyFilter().getSelectedDomainOptions()));
        html.append("</select>");

        // Include a checkbox if we must include the option to include
        // empty values
        if (filter.getCurrencyFilter().isIncludeEmptyOption()) {
            html.append(makeCheckbox("filter" + filter.getID() + "_currency_isEmptyOptionSelected", "true", "prm.global.finder.filter.emptyoption.label", filter.getCurrencyFilter().isEmptyOptionSelected()));
        }

    }

    public void visitSpaceFilter(SpaceFilter filter) {
        if (isOuterFilter) {
            html.append("<td>&nbsp;</td>");
        }

        Collection selected = filter.getSelectedSpaces();

        //Label
        html.append("<td class=\"tableContent\">" + filter.getName() + "</td>");

        //Selection List
        html.append("<td class=\"tableContent\" colspan=\"2\">");
        html.append("<select name=\"filter" + filter.getID() + "\" size=\"");
        html.append(determineSelectionSize(filter.getSpaceDescription()));
        html.append( "\" multiple=\"" + filter.isMultipleSelect() + "\">");

        for (Iterator it = filter.getSpaceDescription().iterator(); it.hasNext();) {
            Space space = (Space) it.next();
            html.append("<option value=\"" + space.getID() + "\" " + (selected.contains(space.getID()) ? " selected" : "") + ">");
            html.append(space.getName() + "</option>");
        }

        html.append("</select>");
        html.append("</td>");
    }


    //--------------------------------------------------------------------------
    // Visit Methods for GROUPINGs
    //--------------------------------------------------------------------------

    /**
     * Iterate through a finder grouping list, creating HTML for it and all of
     * its enclosed groupings.
     *
     * @param groupingList the <code>GroupingList</code> that we are going to
     * produce HTML for.
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitFinderGroupingList(FinderGroupingList groupingList) throws VisitException {
        //Create the html table that will house all of the groupings
        html.append("<table border=\"0\">");

        //Iterate through all of the sorters in the list and output them
        for (Iterator it = groupingList.getAllGroupings().iterator(); it.hasNext();) {
            FinderGrouping grouping = (FinderGrouping)it.next();

            //Draw the row for the current grouping
            html.append("<tr>");
            html.append("<td><input type=\"radio\" name=\"grouping\" value=\"")
                .append(grouping.getID()).append("\"").append((grouping.isSelected() ? " checked" : ""))
                .append("></td>");
            html.append("<td class=\"tableContent\">");
            grouping.accept(this);
            html.append("</td>");
        }

        //We are done output sorters, close the table.
        html.append("</table>");
    }

    /**
     * Produce HTML for a <code>EmptyFinderGrouping</code> grouping.
     *
     * @param grouping the <code>EmptyFinderGrouping</code> for which we are
     * going to produce HTML.
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitEmptyFinderGrouping(EmptyFinderGrouping grouping) throws VisitException {
        html.append("<span class=\"tableContent\">" + grouping.getName() + "</span>");
    }

    /**
     * Produce HTML for a <code>FinderGrouping</code>.
     * @param grouping
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitFinderGrouping(FinderGrouping grouping) throws VisitException {
        html.append("<span class=\"tableContent\">" + grouping.getName() + "</span>");
    }

    //--------------------------------------------------------------------------
    // Visit Methods for COLUMNs
    //--------------------------------------------------------------------------

    /**
     * Produce HTML for a collection of <code>FinderColumn</code>s.
     * @param list the list of <code>FinderColumn</code>s for which to produce
     * HTML.
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitFinderColumnList(FinderColumnList list) throws VisitException {

        // Currently Produces a 4 column by 3 row table
        // The first and third columns span all 3 rows
        // To make only 4 individuals cells (A, B, C, D)
        // E.g.
        //    Col     1 2 3 4
        //   Row   1  A   C
        //         2  A B C D
        //         3  A   C
        //
        // Cell A contains the unselected list
        // Cell B contains the select and deselect buttons
        // Cell C contains the selected list
        // Cell D contains the move up and down buttons
        //
        // Select, Deselect, Move Up, Move Down functions are required
        // to be implemented in Javascript

        //
        // 01/14/2003 - Tim
        // None of this made it into implementation due to time constraints
        // Delete this code after version 8.0...
        html.append("<table border=\"0\">");

        // Row 1
        html.append("<tr>");

        // Col 1 - Selection of all columns
        html.append("<td rowspan=\"3\" class=\"tableContent\">");
        html.append("<select name=\"").append("unselectedColumn").append("\" size=\"6\">");
        for (Iterator it = list.getUnselectedColumns().iterator(); it.hasNext(); ) {
            FinderColumn column = (FinderColumn)it.next();
            column.accept(this);
        }
        html.append("</select>");
        html.append("</td>");

        // Col 2 - Blank
        html.append("<td>&nbsp;</td>");

        // Col 3 - Selected columns
        html.append("<td rowspan=\"3\" class=\"tableContent\">");
        html.append("<select name=\"").append("selectedColumn").append("\"  size=\"6\">");
        for (Iterator it = list.getSelectedColumns().iterator(); it.hasNext(); ) {
            FinderColumn column = (FinderColumn)it.next();
            column.accept(this);
        }
        html.append("</select>");
        html.append("</td>");

        // Col 4 - Blank
        html.append("<td>&nbsp;</td>");

        html.append("</tr>");

        // Row 2
        html.append("<tr>");

        // Col 2 - Select / deselect arrows
        html.append("<td class=\"tableContent\">");
        html.append("<table>")
                .append("<tr>").append("<td class=\"tableContent\">")
                .append("<a href=\"javascript:selectColumn('unselectedColumn');\">Select --></a>")
                .append("</td>").append("</tr>")
                .append("<tr>").append("<td class=\"tableContent\">")
                .append("<a href=\"javascript:deselectColumn('selectedColumn');\"><-- Deselect</a>")
                .append("</td>").append("</tr>")
                .append("</table>");
        html.append("</td>");

        // Col 4 - Up / down arrows
        html.append("<td class=\"tableContent\">");
        html.append("<table>")
                .append("<tr>").append("<td class=\"tableContent\">")
                .append("<a href=\"javascript:moveUpColumn('unselectedColumn');\">Up</a>")
                .append("</td>").append("</tr>")
                .append("<tr>").append("<td class=\"tableContent\">")
                .append("<a href=\"javascript:moveDownColumn('unselectedColumn');\">Down</a>")
                .append("</td>").append("</tr>")
                .append("</table>");
        html.append("</td>");

        html.append("</tr>");

        // Row 3
        html.append("<tr>");
        // Col 2 - blank
        html.append("<td>&nbsp;</td>");
        // Col 4 - blank
        html.append("<td>&nbsp;</td>");
        html.append("</tr>");

        html.append("</table>");
    }

    /**
     * Produce HTML for a <code>FinderColumn</code>.
     *
     * @param column the <code>FinderColumn</code> for which we are going to
     * produce HTML.
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitFinderColumn(FinderColumn column) throws VisitException {
        html.append("<option value=\"").append(column.getID()).append("\">").append(column.getColumnDefinition().getName()).append("</option>");
    }

    //--------------------------------------------------------------------------
    // Visit Methods for SORTERs
    //--------------------------------------------------------------------------

    /**
     * Create a table structure for the sorterlist and iterate through its
     * childen to produce html for the entire sorterlist.
     *
     * @param sorterList the <code>FinderSorterList</code> for which we are
     * going to produce HTML.
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitFinderSorterList(FinderSorterList sorterList) throws VisitException {
        //Create the html table that will house all of the sorters
        html.append("<table border=\"0\">");

        //Iterate through all of the sorters in the list and output them
        for (Iterator it = sorterList.getAllSorters().iterator(); it.hasNext();) {
            FinderSorter sorter = (FinderSorter)it.next();

            //Draw the row for the current sorter
            html.append("<tr>");
            html.append("<td><input type=\"checkbox\" name=\"").append(sorter.getID())
                .append("selected\" value=\"true\"").append((sorter.isSelected() ? " checked" : ""))
                .append("></td>");
            html.append("<td class=\"tableContent\">");
            sorter.accept(this);
            html.append("</td>");
        }

        //We are done output sorters, close the table.
        html.append("</table>");
    }

    /**
     * Produces HTML for the specified sorter.
     * @param sorter the <code>FinderSorter</code> for which to produce HTML
     * @throws VisitException if there is a problem producing the HTML
     */
    public void visitFinderSorter(FinderSorter sorter) throws VisitException {
        //Start constructing this sorter in a table
        html.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
        html.append("<tr>");

        //The first column is a dropdown list of fields.
        html.append("<td class=\"tableContent\">");
        html.append("<select name=\"").append(sorter.getID()).append("\">");

        //Iterate through all of the available columns and add them to the
        //select list.
        for (Iterator it = sorter.getColumnList().iterator(); it.hasNext(); ) {
            ColumnDefinition currentColumn = (ColumnDefinition)it.next();
            html.append("<option value=\"").append(currentColumn.getColumnName())
                .append("\"").append((currentColumn.equals(sorter.getSelectedColumn()) ? "selected" : ""))
                .append(">").append(HTMLUtils.escape(currentColumn.getName()).replaceAll("'", "&acute;"));
        }

        html.append("</select>");
        html.append("</td>");

        //The second column asks the user to select ascending or descending
        html.append("<td class=\"tableContent\">");
        html.append("<input type=\"radio\" name=\"").append(sorter.getID())
            .append("order\" value=\"asc\"").append((!sorter.isDescending() ? " checked" : ""))
            .append(">").append(sorter.ASCENDING).append("&nbsp;");
        html.append("<input type=\"radio\" name=\"").append(sorter.getID()).
            append("order\" value=\"desc\"").append((sorter.isDescending() ? " checked" : "")).
            append(">").append(sorter.DESCENDING);
        html.append("</td>");
        html.append("</tr>");

        html.append("</table>");
    }

    /**
     * Determines the appropriate size for a selection list.
     * This is the lesser of {@link #MAXIMUM_LIST_HEIGHT} and contents.size(),
     * but always greater than {@link #MINIMUM_LIST_HEIGHT}.
     * @param contents the collection that will fill the selection list
     * @return the size to make the selection list
     */
    private int determineSelectionSize(Collection contents) {
        return Math.max(Math.min(MAXIMUM_LIST_HEIGHT, contents.size()), MINIMUM_LIST_HEIGHT);
    }

    /**
     * Convenience method to construct an HTML &lt;INPUT type="checkbox ... /&gt;.
     *
     * @param name the name to assign the input field
     * @param value the value to set
     * @param displayToken the token name to use for the display
     * @param isChecked true if the checkbox should be checked by default
     * @return the HTML for the checkbox
     */
    private String makeCheckbox(String name, String value, String displayToken, boolean isChecked) {
        StringBuffer html = new StringBuffer();

        html.append("<input type=\"checkbox\" name=\"").append(name).append("\"");
        html.append("value=\"").append(value).append("\"");
        if (isChecked) {
            html.append(" checked");
        }
        html.append(">").append(PropertyProvider.get(displayToken));

        return html.toString();
    }
}
