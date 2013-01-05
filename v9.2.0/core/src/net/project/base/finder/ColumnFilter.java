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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.finder;

/**
 * Provides the base for a filter based on a <code>ColumnDefinition</code>.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public abstract class ColumnFilter extends FinderFilter {

    /**
     * This column definition that will be used to construct a SQL where clause
     * from the user's selections.
     */
    private final ColumnDefinition columnDefinition;

    /**
     * Indicates whether this filter can filter on empty values.
     */
    private final boolean isIncludeEmptyOption;

    /**
     * Indicates whether the empty option was selected.
     */
    private boolean isEmptyOptionSelected = false;

    /**
     * Creates a new ColumnFilter for the specified column.
     * The column's name is used for the display name of the filter.
     *
     * @param id a <code>String</code> which uniquely identifies this filter
     * among all filters created in a single FinderIngredients.
     * @param columnDefinition a {@link ColumnDefinition}
     * object which identifies which database column we are going to do database
     * operations to.
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional fields); false
     * if not (for mandatory fields)
     */
    public ColumnFilter(String id, ColumnDefinition columnDefinition, boolean isIncludeEmptyOption) {
        this(id, columnDefinition, columnDefinition.getNameToken(), isIncludeEmptyOption);
    }

    /**
     * Creates a new ColumnFilter for the specified column using a different
     * name token.
     *
     * @param id a <code>String</code> which uniquely identifies this filter
     * among all filters created in a single FinderIngredients.
     * @param columnDefinition a {@link ColumnDefinition}
     * object which identifies which database column we are going to do database
     * operations to.
     * @param nameToken the token to use for the display name of this filter
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional fields); false
     * if not (for mandatory fields)
     */
    public ColumnFilter(String id, ColumnDefinition columnDefinition, String nameToken, boolean isIncludeEmptyOption) {
        super(id, nameToken);
        this.columnDefinition = columnDefinition;
        this.isIncludeEmptyOption = isIncludeEmptyOption;
    }

    /**
     * Returns the column definition for column on which this ColumnFilter
     * will perform filtering.
     * @return the column definition
     */
    public ColumnDefinition getColumnDefinition() {
        return this.columnDefinition;
    }

    /**
     * Indicates whether an option to select empty values is available for
     * this ColumnFilter.  This is typically set for optional database columns.
     * @return true if there is an option to filter on empty values
     */
    public boolean isIncludeEmptyOption() {
        return this.isIncludeEmptyOption;
    }

    /**
     * Specifies that the user selected the option to include empty values in
     * this filter.
     * This is used when constructing the where clause.
     * @param isEmptyOptionSelected true if the user selected the option
     * to include empty values; false otherwise
     */
    public void setEmptyOptionSelected(boolean isEmptyOptionSelected) {
        this.isEmptyOptionSelected = isEmptyOptionSelected;
    }

    /**
     * Indicates whether the user selected the option to include empty values in
     * this filter.
     * @return true if the user selected the option to include empty values;
     * false otherwise
     */
    public boolean isEmptyOptionSelected() {
        return this.isEmptyOptionSelected;
    }

    public void clear() {
        super.clear();
        setEmptyOptionSelected(false);
    }
}
