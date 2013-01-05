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

import java.math.BigDecimal;

/**
 * NumberFilter subclass which allows percent values to be filtered on without
 * having to worry about dividing by 100 for fractional percents.
 *
 * @author Matthew Flower
 * @since Version 7.7.1
 */
public class PercentFilter extends NumberFilter {
    private boolean percentIsStoredFractionally;

    /**
     * Creates a new NumberFilter for the specified column.
     *
     * @param id a <code>String</code> which uniquely identifies this filter
     * among all filters created for this object.
     * @param columnDef a {@link ColumnDefinition}
     * object which identifies which database column we are going to do database
     * operations to.
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional fields); false if
     * not (for mandatory fields)
     * @param percentIsStoredFractionally indicates if the percent being filtered
     * upon is stored fractionally in the database.  (For example, 1=100%, 0.50=50%)
     */
    public PercentFilter(String id, ColumnDefinition columnDef, boolean isIncludeEmptyOption, boolean percentIsStoredFractionally) {
        super(id, columnDef, isIncludeEmptyOption);
        this.percentIsStoredFractionally = percentIsStoredFractionally;
    }

    /**
     * Get a where clause for this filter that can be applied to a finder.  This
     * where clause will be empty (not null) if the filter isn't selected.
     *
     * @return a <code>String</code> containing a where clause, or an empty
     *         string if the filter isn't selected.
     */
    public String getWhereClause() {
        Number number = getNumber();

        if (number != null && percentIsStoredFractionally) {
            number = new BigDecimal(number.doubleValue()).divide(new BigDecimal(100), 3, BigDecimal.ROUND_HALF_UP);
        }

        return getWhereClause(number);
    }
}
