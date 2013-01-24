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


/**
 * This filter allows you to select one or more values that appear in list and
 * filter them in a where clause.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class StringDomainFilter extends FinderFilter {
    /** Column name that the selected values will be compared against. */
    private ColumnDefinition column;
    /** The values that the user wants to search for. */
    private String[] selectedValues;
    /** The way that the column and values will be compared. */
    private TextComparator operator;
    /** Whether or not null values ought to be included when searching. */
    private boolean includeNullValues = false;

    public StringDomainFilter(String id, String nameToken, ColumnDefinition column, TextComparator operator) {
        super(id, nameToken);

        this.column = column;
        this.operator = operator;
    }

    public String getWhereClause() {
        String sql = "";

        if (isSelected()) {
            if (!(selectedValues == null) && (selectedValues.length > 0)) {
                sql += "(";
                for (int i = 0; i < selectedValues.length; i++) {
                    sql += "("+operator.createWhereClause(column.getColumnName(),
                        new String[] {selectedValues[i]}, false)+")";
                    if (i < selectedValues.length-1) {
                        sql += " or ";
                    }
                }

                if (includeNullValues) {
                    sql += " or ("+column.getColumnName()+" is null)";
                }
                sql += ")";
            }
        }

        return sql;
    }

    public String getFilterDescription() {
        return "";
    }

    /**
     * Get the list of values being searched for by this filter.
     *
     * @return a <code>String[]</code> of values.
     */
    public String[] getSelectedValues() {
        return selectedValues;
    }

    public void setSelectedValues(String[] selectedValues) {
        this.selectedValues = selectedValues;
    }

    /**
     * Whether or not the sql statement should return a row if the column is
     * null.
     *
     * @return
     */
    public boolean isIncludeNullValues() {
        return includeNullValues;
    }

    /**
     * Whether or not the sql statement should return a row if the column is
     * null.
     *
     * @param includeNullValues
     */
    public void setIncludeNullValues(boolean includeNullValues) {
        this.includeNullValues = includeNullValues;
    }

    protected void clearProperties() {
        selectedValues = null;
        includeNullValues = false;
    }


}
