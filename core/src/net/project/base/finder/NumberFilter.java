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

import net.project.base.property.PropertyProvider;
import net.project.util.NumberFormat;
import net.project.util.VisitException;

/**
 * Number filter creates a filter field which allows a user to compare a database
 * field to a number in the ways defined by the {@link net.project.base.finder.NumberComparator}
 * object.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class NumberFilter extends ColumnFilter {

    /**
     * A number value which the user entered.  This will be used when constructing
     * the where clause.
     */
    private Number number;

    /**
     * A comparator that the user selected.  (Or the default if the user hasn't
     * selected a comparator.
     */
    private FilterComparator comparator;

    /**
     * Creates a new NumberFilter for the specified column.
     *
     * @param id a <code>String</code> which uniquely identifies this filter
     * among all filters created for this object.
     * @param columnDef a {@link net.project.base.finder.ColumnDefinition}
     * object which identifies which database column we are going to do database
     * operations to.
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional fields); false
     * if not (for mandatory fields)
     */
    public NumberFilter(String id, ColumnDefinition columnDef, boolean isIncludeEmptyOption) {
        super(id, columnDef, isIncludeEmptyOption);
    }

    /**
     * Sets the number to filter on.
     * @param number the number
     */
    public void setNumber(Number number) {
        this.number = number;
    }

    /**
     * Sets the number to filter on.
     *
     * @param number a <code>long</code> value that we are going to filter on.
     */
    public void setNumber(long number) {
        this.number = new Long(number);
    }

    /**
     * Returns the number being filtered on.
     * @return the number
     */
    public Number getNumber() {
        return number;
    }

    /**
     * Specifies the comparator to use with when generating the where clause
     * for this NumberFilter.
     * @param comparator the <code>NumberComparator</code> to use
     */
    public void setComparator(NumberComparator comparator) {
        this.comparator = comparator;
    }

    /**
     * Returns the current comparator used when generating the where clause
     * for this filter.
     * @return the current comparator
     */
    public FilterComparator getComparator() {
        return comparator;
    }


    /**
     * Get a where clause for this filter that can be applied to a finder.  This
     * where clause will be empty (not null) if the filter isn't selected.
     *
     * @return a <code>String</code> containing a where clause, or an empty
     * string if the filter isn't selected.
     */
    public String getWhereClause() {
        return getWhereClause(number);
    }

    /**
     * Get the where clause, using the number passed in the <code>number</code>
     * param to signify the percent complete.
     *
     * @param number a <code>Number</code> value which signifies the number we
     * are making a where clause for.
     * @return a <code>String</code> containing a where clause, or an empty
     * string if the filter isn't selected.
     */ 
    protected String getWhereClause(Number number) {
        String toReturn;

        if (!isSelected()) {
            toReturn = "";
        } else {
            // Make sure we only pass parameter values if we have a parameter
            String[] params;
            if (number != null) {
                params = new String[]{number.toString()};
            } else {
                params = null;
            }
            toReturn = comparator.createWhereClause(getColumnDefinition().getColumnName(), params, isEmptyOptionSelected());
        }

        return toReturn;
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

        return PropertyProvider.get("prm.global.finder.numberfilter.description", new String[] { getName(), comparator.getSymbol(), number == null ? "" : nf.formatNumber(number.doubleValue()) });
    }

    /**
     * Clears the comparator and number.
     */
    protected void clearProperties() {
        setComparator(null);
        setNumber(null);
    }

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitNumberFilter(this);
    }

}
