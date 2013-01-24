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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides a FilterComparator based on simple patterns.
 * <p>
 * This comparator provides two patterns used to format the column names
 * and values.  The column name is assumed to be parameter zero and the values
 * are parameters 1..n in the pattern.
 * A null comparison pattern is used specifically for testing for the null
 * value.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class SimplePatternComparator extends FilterComparator {

    /** The pattern that will be used to construct the SQL statement. */
    private String comparisonPattern = null;

    /** The pattern used when matching NULL values. */
    private String nullComparisonPattern = null;

    /**
     * Creates a new SimplePatternComparator.
     * <p>
     * The comparison pattern is defined by {@link java.text.MessageFormat}.
     * The following <i>FormatElements</i> are permitted:
     * <ul>
     * <li><code>{0}</code> - the column name will be inserted here</li>
     * <li><code>{1} ... {n}</code> - values will be inserted in these positions.</li>
     * </ul>
     * <b>Note:</b> Single quote characters must be doubled up, since ordinarily
     * they are used to escape other items.
     * </p>
     * <p>
     * For example, for a simple number "greater than" comparison, the pattern
     * would be: <br />
     * <code>{0} &gt; {1}</code> <br />
     * For a text "like" comparison, the pattern would be: <br />
     * <code>{0} like ''%{1}%''</code> <br />
     * </p>
     * @param id a <code>String</code> value which uniquely identifies this
     * SimplePatternComparator.
     * @param displayToken a <code>String</code> value which contains a property
     * value which points to a string which will be used to display this
     * <code>SimplePatternComparator</code>.
     * @param comparisonPattern a <code>String</code> value which contains a
     * pattern string which will be used to construct a database conditional
     * clause; parameter {0} is assumed to be the column name and parameters
     * {1} .. {n} the values.
     * @param nullComparisonPattern a String value containing a pattern string
     * used to construct a conditional clause for matching null values;
     * parameter {0} is assumed to be the column name and parameters
     * {1} .. {n} the values.  For example: <code>{0} IS NULL</code>
     */
    protected SimplePatternComparator(String id, String displayToken, String comparisonPattern, String nullComparisonPattern) {
        super(id, displayToken);
        this.comparisonPattern = comparisonPattern;
        this.nullComparisonPattern = nullComparisonPattern;
    }

    /**
     * Create a where clause that can be used in a SQL statement.
     * The where clause is constructed from the specified field name, values
     * and the comparison patterns of this filter.  If the specified <code>fieldValues</code>
     * array is not null and has at least one element, the comparison pattern is
     * used.  If it is null or empty, that pattern is not used.
     * Additionally, if <code>isNullIncluded</code> then the nullComparisonPattern
     * is used.  If both patterns are used they are <code>ORd</code> together.
     *
     * @param fieldName the name of the field that we are going to use to filter
     * records in a SQL Script.
     * @param fieldValues the values to use to construct the where clause
     * @return a <code>String</code> value prepared to be used in a SQL where
     * clause.  This string will not have an "and" or an "or" in front of it.
     */
    public String createWhereClause(String fieldName, String[] fieldValues, boolean isNullIncluded) {

        // First construct the overall pattern
        boolean hasValues = (fieldValues != null && fieldValues.length > 0);
        StringBuffer pattern = new StringBuffer();

        pattern.append("(");

        if (hasValues) {
            pattern.append(this.comparisonPattern);
        }

        if (hasValues && isNullIncluded) {
            pattern.append(" OR ");
        }

        if (isNullIncluded) {
            pattern.append(this.nullComparisonPattern);
        }

        pattern.append(")");


        // Create a message format based on this filter's comparison pattern
        MessageFormat messageFormat = new MessageFormat(pattern.toString());

        // Create the parameters to the format pattern
        // Parameter at position 0 is always the field name
        // Parameters at positions 1..n are the values
        List parameterList = new ArrayList();
        parameterList.add(fieldName);
        if (hasValues) {
            parameterList.addAll(Arrays.asList(fieldValues));
        }

        // Return the parameters formatted based on the pattern
        return messageFormat.format(parameterList.toArray());
    }

}
