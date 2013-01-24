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
import net.project.database.DBFormat;
import net.project.util.VisitException;

/**
 * Text filter creates a filter field which allows a user to compare a database
 * field to a number in the ways defined by the {@link TextComparator}
 * object.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class TextFilter extends ColumnFilter {
    /**
     * A string value which the user entered.
     */
    private String value = null;

    /**
     * A comparator that the user selected.  (Or the default if the user hasn't
     * selected a comparator.
     */
    private FilterComparator comparator = null;

    /**
     * Creates a new TextFilter on the specified column.
     * @param id a <code>String</code> which uniquely identifies this filter
     * among all filters created for this object.
     * @param columnDefinition a {@link ColumnDefinition}
     * object which identifies which database column we are going to do database
     * operations to.
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional fields); false
     * if not (for mandatory fields)
     */
    public TextFilter(String id, ColumnDefinition columnDefinition, boolean isIncludeEmptyOption) {
        super(id, columnDefinition, isIncludeEmptyOption);
    }

    /**
     * Specifies the value to use when generating the where clause for this
     * TextFilter.
     * @param value the text value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns the current value of this text filter.
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Specifies the comparator to use with when generating the where clause
     * for this TextFilter.
     * @param comparator the <code>TextComparator</code> to use
     */
    public void setComparator(TextComparator comparator) {
        this.comparator = comparator;
    }

    /**
     * Returns the currently selected comparator of this filter.
     * @return the comparator or null if none has been selected
     */
    public FilterComparator getComparator() {
        return this.comparator;
    }

    /**
     * Get a where clause for this filter that can be applied to a finder.  This
     * where clause will be empty (not null) if the filter isn't selected.
     *
     * @return a <code>String</code> containing a where clause, or an empty
     * string if the filter isn't selected.
     */
    public String getWhereClause() {
        String toReturn;

        if (!isSelected()) {
            toReturn = "";
        } else {
            toReturn = comparator.createWhereClause(getColumnDefinition().getColumnName(),
                new String[]{ DBFormat.escape(value) }, isEmptyOptionSelected());
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
        return PropertyProvider.get("prm.global.finder.textfilter.description",
            new String[] {getName(), comparator.getSymbol(), this.value});
    }

    /**
     * Clears the comparator and value.
     */
    protected void clearProperties() {
        setComparator(null);
        setValue(null);
    }

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitTextFilter(this);
    }

}
