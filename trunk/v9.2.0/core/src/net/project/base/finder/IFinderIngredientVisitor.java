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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.base.finder;

import net.project.base.money.MoneyFilter;
import net.project.form.report.FormFieldFilter;
import net.project.portfolio.view.ProjectVisibilityFilter;
import net.project.portfolio.view.SelectedProjectsFilter;
import net.project.resource.filters.assignments.SpaceFilter;
import net.project.schedule.report.UserFilter;
import net.project.util.VisitException;

public interface IFinderIngredientVisitor {
    /**
     * Produce HTML for a checkbox filter.
     *
     * @param filter the filter for which we are going to produce HTML.
     */
    void visitCheckboxFilter(CheckboxFilter filter) throws net.project.util.VisitException;
    /**
     * Produce HTML for a <code>DateFilter</code>.
     *
     * @param filter the <code>DateFilter</code> for which we are going to
     * produce HTML.
     */
    void visitDateFilter(DateFilter filter) throws net.project.util.VisitException;
    /**
     * Produce HTML for a <code>DomainFilter</code>.
     *
     * @param filter the <code>DomainFilter</code> for which we are going to
     * produce HTML.
     */
    void visitDomainFilter(DomainFilter filter) throws net.project.util.VisitException;
    /**
     * Produce HTML for a <code>EmptyFinderFilter</code> filter.
     *
     * @param filter the <code>EmptyFinderFilter</code> for which we are going
     * to produce HTML.
     */
    void visitEmptyFinderFilter(EmptyFinderFilter filter) throws net.project.util.VisitException;

    /**
     * Produce HTML for a <code>TextFilter</code>.
     * @param filter the <code>TextFilter</code> for which we are going to
     * produce HTML.
     */
    void visitTextFilter(TextFilter filter) throws net.project.util.VisitException;

    /**
     * Produce HTML for a <code>NumberFilter</code>.
     * @param filter the <code>NumberFilter</code> for which we are going to
     * produce HTML.
     */
    void visitNumberFilter(NumberFilter filter) throws net.project.util.VisitException;

    /**
     * Produce HTML for a <code>EmptyFinderGrouping</code> grouping.
     *
     * @param grouping the <code>EmptyFinderGrouping</code> for which we are
     * going to produce HTML.
     */
    void visitEmptyFinderGrouping(EmptyFinderGrouping grouping) throws net.project.util.VisitException;
    /**
     * Produce HTML for a <code>FinderColumn</code>.
     *
     * @param column the <code>FinderColumn</code> for which we are going to
     * produce HTML.
     */
    void visitFinderColumn(FinderColumn column) throws net.project.util.VisitException;

    void visitFinderColumnList(FinderColumnList list) throws net.project.util.VisitException;
    /**
     * Iterate through a finder grouping list, creating HTML for it and all of
     * its enclosed groupings.
     *
     * @param groupingList the <code>GroupingList</code> that we are going to
     * produce HTML for.
     */
    void visitFinderGroupingList(FinderGroupingList groupingList) throws net.project.util.VisitException;
    void visitFinderFilter(FinderFilter filter) throws net.project.util.VisitException;
    /**
     * Iterate through all of the filters in the filter list and call visit their
     * children so they can produce HTML as necessary.
     *
     * @param filterList the <code>FinderFilterList</code> that we are going to
     * iterate.
     */
    void visitFinderFilterList(FinderFilterList filterList) throws net.project.util.VisitException;
    /**
     * Create a table structure for the sorterlist and iterate through its
     * childen to produce html for the entire sorterlist.
     *
     * @param sorterList the <code>FinderSorterList</code> for which we are
     * going to produce HTML.
     */
    void visitFinderSorterList(FinderSorterList sorterList) throws net.project.util.VisitException;
    void visitFinderSorter(FinderSorter sorter) throws net.project.util.VisitException;
    void visitFinderGrouping(FinderGrouping finderGrouping) throws net.project.util.VisitException;
    void visitFinderIngredients(FinderIngredients finderIngredients) throws net.project.util.VisitException;
    void visitRadioButtonFilter(RadioButtonFilter radioButtonFilter) throws net.project.util.VisitException;
    void visitUserFilter(UserFilter filter) throws net.project.util.VisitException;
    void visitSpaceFilter(SpaceFilter filter) throws VisitException;
    void visitFormFieldFilter(FormFieldFilter filter) throws net.project.util.VisitException;
    void visitSelectedProjectsFilter(SelectedProjectsFilter filter) throws net.project.util.VisitException;
    void visitProjectVisibilityFilter(ProjectVisibilityFilter filter) throws net.project.util.VisitException;
    void visitMoneyFilter(MoneyFilter filter) throws net.project.util.VisitException;

    void visitFilterComparator(FilterComparator filterComparator) throws net.project.util.VisitException;

}
