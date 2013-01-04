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
package net.project.base.money;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DomainFilter;
import net.project.base.finder.DomainOption;
import net.project.base.finder.FinderFilter;
import net.project.base.finder.IFinderIngredientVisitor;
import net.project.base.finder.NumberFilter;
import net.project.base.finder.SimpleDomainFilter;
import net.project.base.property.PropertyProvider;
import net.project.util.Currency;
import net.project.util.NumberFormat;
import net.project.util.VisitException;

/**
 * Money filter provides a number filter and a currency domain filter.
 * Number filter creates a filter field which allows a user to compare a database
 * field to a number in the ways defined by the {@link net.project.base.finder.NumberComparator}
 * object.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class MoneyFilter extends FinderFilter {

    //
    // Static Members
    //

    /**
     * Makes a collection of <code>DomainOption</code>s from the array of
     * currency codes.  Each value is the currency code and the display
     * part is the currency code.
     * @param currencyCodes the currency codes to build as domain options
     * @return the array of DomainOptions based on the currency codes
     */
    public static DomainOption[] makeDomainOptions(Collection currencyCodes) {

        List domainOptions = new ArrayList();

        for (Iterator it = currencyCodes.iterator(); it.hasNext(); ) {
            String nextCurrencyCode = (String) it.next();

            // Create a domain option for the currency code and its actual
            // display text
            DomainOption domainOption = new DomainOption(nextCurrencyCode,
                    Currency.getFullDisplayName(java.util.Currency.getInstance(nextCurrencyCode)),
                    true);

            domainOptions.add(domainOption);
        }

        DomainOption.sort(domainOptions);
        return (DomainOption[]) domainOptions.toArray(new DomainOption[]{});
    }

    //
    // Instance Members
    //

    /**
     * The Number filter for filtering on the value part of the MoneyFilter.
     */
    private final NumberFilter valueFilter;

    /**
     * The Domain filter for filtering on the currency code part of the MoneyFilter.
     */
    private final DomainFilter currencyFilter;

    /**
     * Creates a new MoneyFilter for the specified value column and currency
     * column.
     *
     * @param id a <code>String</code> which uniquely identifies this filter
     * among all filters created for this object.
     * @param valueColumnDef a {@link net.project.base.finder.ColumnDefinition}
     * object which identifies which database column contains the money value
     * @param currencyColumnDef a {@link net.project.base.finder.ColumnDefinition}
     * object which identifies which database column containing the currency
     * code
     * @param currencyCodes the collection of currency codes to be presented
     * in the currency code filter
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional money fields); false
     * if not (for mandatory fields)
     */
    public MoneyFilter(String id, ColumnDefinition valueColumnDef,
                       ColumnDefinition currencyColumnDef, Collection currencyCodes, boolean isIncludeEmptyOption) {

        super(id, "");
        valueFilter = new NumberFilter(id, valueColumnDef, isIncludeEmptyOption);
        currencyFilter = new SimpleDomainFilter(id, currencyColumnDef, isIncludeEmptyOption, makeDomainOptions(currencyCodes), true);
    }

    /**
     * Returns the filter for the value part of this money filter.
     * @return a NumberFilter for filtering on the value part
     */
    public NumberFilter getValueFilter() {
        return this.valueFilter;
    }

    /**
     * Returns the filter for the currency part of this money filter.
     * @return a SimpleDomainFilter for filtering on the currency part
     */
    public DomainFilter getCurrencyFilter() {
        return this.currencyFilter;
    }

    /**
     * Indicates whether this money filter is selected.
     * @return true if either the value filter or currency filter is selected;
     * false if neither is selected
     */
    public boolean isSelected() {
        return (getValueFilter().isSelected() || getCurrencyFilter().isSelected());
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

        if (!isSelected()) {
            // Neither contained filter is selected
            // WhereClause remains empty

        } else {
            // We know that at least one filter is selected

            whereClause = new StringBuffer("(");

            if (getValueFilter().isSelected()) {
                whereClause.append(getValueFilter().getWhereClause());
            }

            if (getValueFilter().isSelected() && this.currencyFilter.isSelected()) {
                whereClause.append(" AND ");
            }

            if (getCurrencyFilter().isSelected()) {
                whereClause.append(this.currencyFilter.getWhereClause());
            }

            whereClause.append(")");
        }

        return whereClause.toString();
    }

    /**
     * Get a description of what this filter does.  For example, if it filters
     * tasks assigned to me, this string would say "Tasks Assigned To Me".  If
     * it filtered on date range, this method should return "Tasks with finish
     * dates between 01/02/2003 and 01/02/2005"
     *
     * @return a <code>String</code> value describing this filter.
     */
    public String getFilterDescription() {
        NumberFormat nf = NumberFormat.getInstance();

        return PropertyProvider.get("prm.global.finder.numberfilter.description",
            new String[] {getName(), getValueFilter().getComparator().getSymbol(),
                          nf.formatNumber(getValueFilter().getNumber().doubleValue())});
    }

    /**
     * Clears the comparator and number.
     */
    protected void clearProperties() {
        getValueFilter().setSelected(false);
        getValueFilter().clear();
        getValueFilter().setSelected(false);
        getCurrencyFilter().clear();

    }

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitMoneyFilter(this);
    }

}
