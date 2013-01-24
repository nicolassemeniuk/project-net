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

/**
 * This class implements all methods in the IFinderIngredientVisitor.  Classes
 * that subclass this method are protected from changes to the interface.  When
 * changes occur there, they will not need to implement the new methods.
 *
 * If a customer is attempting to modify our API to add an additional finder,
 * sorter, or grouping they should add a visit method for their object to both
 * this method and the IFinderIngredientVisitor.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class EmptyFinderIngredientVisitor implements IFinderIngredientVisitor {

    /**
     * Produce HTML for a checkbox filter.
     *
     * @param filter the filter for which we are going to produce HTML.
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitCheckboxFilter(CheckboxFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitCheckboxFilter(CheckboxFilter has not" +
            "been implemented.");
    }

    /**
     * Produce HTML for a <code>DateFilter</code>.
     *
     * @param filter the <code>DateFilter</code> for which we are going to
     * produce HTML.
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitDateFilter(DateFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitDateFilter(DateFilter) has not been implemented.");
    }

    /**
     * Produce HTML for a <code>DomainFilter</code>.
     *
     * @param filter the <code>DomainFilter</code> for which we are going to
     * produce HTML.
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitDomainFilter(DomainFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitDomainFilter(DomainFilter) has not been implemented.");
    }

    /**
     * Produce HTML for a <code>EmptyFinderFilter</code> filter.
     *
     * @param filter the <code>EmptyFinderFilter</code> for which we are going
     * to produce HTML.
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitEmptyFinderFilter(EmptyFinderFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitEmptyFinderFilter(EmptyFinderFilter) has not been implemented.");
    }

    /**
     * Produce HTML for a <code>EmptyFinderGrouping</code> grouping.
     *
     * @param grouping the <code>EmptyFinderGrouping</code> for which we are
     * going to produce HTML.
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitEmptyFinderGrouping(EmptyFinderGrouping grouping) throws VisitException {
        throw new UnsupportedOperationException("visitEmptyFinderGrouping(EmptyFinderGrouping) has not been implemented.");
    }

    /**
     * Produce HTML for a <code>FinderColumn</code>.
     *
     * @param column the <code>FinderColumn</code> for which we are going to
     * produce HTML.
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitFinderColumn(FinderColumn column) throws VisitException {
        throw new UnsupportedOperationException("visitFinderColumn(FinderColumn) has not been implemented.");
    }

   /**
    * @throws UnsupportedOperationException <b>at all times<b>.  This method is
    * not implemented and is here so that users can implement the
    * IFinderIngredientVisitor even if they don't need implementations for some
    * features.
    */
    public void visitFinderColumnList(FinderColumnList list) throws VisitException {
        throw new UnsupportedOperationException("visitFinderColumnList(FinderColumnList) has not been implemented.");
    }

    /**
     * Iterate through a finder grouping list, creating HTML for it and all of
     * its enclosed groupings.
     *
     * @param groupingList the <code>GroupingList</code> that we are going to
     * produce HTML for.
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitFinderGroupingList(FinderGroupingList groupingList) throws VisitException {
        throw new UnsupportedOperationException("visitFinderGroupingList(FinderGroupingList) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitFinderFilter(FinderFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitFinderFilter(FinderFilter) has not been implemented.");
    }

    /**
     * Iterate through all of the filters in the filter list and call visit their
     * children so they can produce HTML as necessary.
     *
     * @param filterList the <code>FinderFilterList</code> that we are going to
     * iterate.
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitFinderFilterList(FinderFilterList filterList) throws VisitException {
        throw new UnsupportedOperationException("visitFinderFilterList(FinderFilterList) has not been implemented.");
    }

    /**
     * Create a table structure for the sorterlist and iterate through its
     * childen to produce html for the entire sorterlist.
     *
     * @param sorterList the <code>FinderSorterList</code> for which we are
     * going to produce HTML.
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitFinderSorterList(FinderSorterList sorterList) throws VisitException {
        throw new UnsupportedOperationException("visitFinderSorterList(FinderSorterList) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitFinderSorter(FinderSorter sorter) throws VisitException {
        throw new UnsupportedOperationException("visitFinderSorter(FinderSorter) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitFinderGrouping(FinderGrouping finderGrouping) throws VisitException {
        throw new UnsupportedOperationException("visitFinderGrouping(FinderGrouping) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitFinderIngredients(FinderIngredients finderIngredients) throws VisitException {
        throw new UnsupportedOperationException("visitFinderIngredients(FinderIngredients) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitNumberFilter(NumberFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitNumberFilter(NumberFilter) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitTextFilter(TextFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitTextFilter(TextFilter) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitRadioButtonFilter(RadioButtonFilter radioButtonFilter) throws VisitException {
        throw new UnsupportedOperationException("visitRadioButtonFilter(RadioButtonFilter) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitUserFilter(UserFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitUserFilter(UserFilter) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitSpaceFilter(SpaceFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitSpaceFilter(SpaceFilter) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitFormFieldFilter(FormFieldFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitFormFieldFilter(FormFieldFilter) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitSelectedProjectsFilter(SelectedProjectsFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitSelectedProjectsFilter(SelectedProjectsFilter) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitProjectVisibilityFilter(ProjectVisibilityFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitProjectVisibilityFilter(ProjectVisibilityFilter) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitMoneyFilter(MoneyFilter filter) throws VisitException {
        throw new UnsupportedOperationException("visitMoneyFilter(MoneyVisibilityFilter) has not been implemented.");
    }

    /**
     * @throws UnsupportedOperationException <b>at all times<b>.  This method is
     * not implemented and is here so that users can implement the
     * IFinderIngredientVisitor even if they don't need implementations for some
     * features.
     */
    public void visitFilterComparator(FilterComparator filterComparator) throws VisitException {
        throw new UnsupportedOperationException("visitFilterComparator(FilterComparator) has not been implemented.");
    }

}
