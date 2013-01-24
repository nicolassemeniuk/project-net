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

import java.text.MessageFormat;

import net.project.util.TimeQuantityUnit;

/**
 * This class is designed to allow proper sorting of "columns" which contain
 * time quantities.  In reality, these columns are actually aggregates of two
 * columns.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class TimeQuantityFinderSorter extends FinderSorter {
    private ColumnDefinition quantityCol;
    private ColumnDefinition unitCol;
    private int hoursPerDay;
    private int hoursPerWeek;

    /**
     * Constructor.
     *
     * @param quantityCol a <code>ColumnDefinition</code> for the column that
     * holds the scalar quantity of time.
     * @param unitCol a <code>ColumnDefinition</code> for the units (Hours,
     * Days, Weeks) that the time quantity is in.
     * @param isDescending indicates if the column should be sorted in reverse
     * order.
     * @param hoursPerDay number of hours in a day for purpose of converting
     * hours to day.
     * @param hoursPerWeek number of hours in a week for purpose of converting
     * hours to weeks.
     */
    public TimeQuantityFinderSorter(ColumnDefinition quantityCol, ColumnDefinition unitCol, boolean isDescending, int hoursPerDay, int hoursPerWeek) {
        super(quantityCol, isDescending);
        this.quantityCol = quantityCol;
        this.unitCol = unitCol;
        this.hoursPerDay = hoursPerDay;
        this.hoursPerWeek = hoursPerWeek;
    }

    /**
     * Column that holds the scalar quantity of time.
     *
     * @return a <code>ColumnDefinition</code> for the column that
     * holds the scalar quantity of time.
     */
    public ColumnDefinition getQuantityCol() {
        return quantityCol;
    }

    /**
     * Set the column that holds the scalar quantity of time.
     *
     * @param quantityCol a <code>ColumnDefinition</code> for the column that
     * holds the scalar quantity of time.
     */
    public void setQuantityCol(ColumnDefinition quantityCol) {
        this.quantityCol = quantityCol;
    }

    /**
     * Column for the units (Hours, Days, Weeks) that the time quantity is in.
     *
     * @return a <code>ColumnDefinition</code> for the column that
     * holds the units (Hours, Days, Weeks) that the time quantity is in.
     */
    public ColumnDefinition getUnitCol() {
        return unitCol;
    }

    /**
     * Set the column for the units (Hours, Days, Weeks) that the time quantity
     * is in.
     *
     * @param unitCol a <code>ColumnDefinition</code> for the column that
     * holds the units (Hours, Days, Weeks) that the time quantity is in.
     */
    public void setUnitCol(ColumnDefinition unitCol) {
        this.unitCol = unitCol;
    }

    public String getOrderByClause() {
        String sql = "decode({1}, {2}, {0}, {3}, {0}*{5}, {4}, {0}*{6})";

        sql = MessageFormat.format(sql, new Object[] {
            quantityCol.getColumnName(),
            unitCol.getColumnName(),
            new Integer(TimeQuantityUnit.HOUR.getUniqueID()),
            new Integer(TimeQuantityUnit.DAY.getUniqueID()),
            new Integer(TimeQuantityUnit.WEEK.getUniqueID()),
            new Integer(hoursPerDay),
            new Integer(hoursPerWeek)
        });

        if (isDescending()) {
            sql += " desc";
        }

        return sql;
    }
}
