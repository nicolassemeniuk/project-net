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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.project.gui.html.HTMLOptionList;

/**
 * Provides an enumeration of <code>DomainComparator</code>s.
 * A <code>DomainComparator</code> provides a where clause for filtering on a column that
 * contains a domain value.  It is able to test for equality or inequality
 * with a NULL value.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class DomainComparator extends SimplePatternComparator {

    //
    // Static Members
    //

    /** The list of all DomainFilterComparators that are available. */
    private static List comparatorList = new ArrayList();

    /**
     * Provides comparison of the form <code>column IN (expression list) OR column IS NULL</code>.
     */
    public static FilterComparator EQUALS = new DomainComparator("equals", "prm.global.finder.domaincomparator.equals.symbol", "{0} IN ({1})", "{0} IS NULL");

    /**
     * Provides comparison of the form <code>column NOT IN (expression list) AND column IS NOT NULL</code>.
     */
    public static FilterComparator NOT_EQUAL = new DomainComparator("notequals", "prm.global.finder.domaincomparator.notequal.symbol", "{0} NOT IN ({1})", "{0} IS NULL");

    /**
     * The DomainComparator that will be selected in the option list if no selection is passed in,
     * currently {@link #EQUALS}.
     */
    public static FilterComparator DEFAULT = EQUALS;

    /**
     * Get the list of available <code>DomainComparators</code> arranged as an
     * HTML option list.
     *
     * @param selected a <code>DomainComparator</code> which signifies which
     * html option is selected initially.
     * @return a <code>String</code> value containing an HTML option list as a
     * <code>String</code>.
     */
    public static String getOptionList(FilterComparator selected) {
        return HTMLOptionList.makeHtmlOptionList(DomainComparator.comparatorList, selected).toString();
    }

    /**
     * Get the text comparator which corresponds to the supplied ID, or
     * {@link #DEFAULT} if there is not one that corresponds.
     *
     * @param id a <code>String</code> value corresponding to the id value for
     * an already existing DomainComparator.
     * @return a <code>DomainComparator</code> which has the same id as the id
     * parameter the calling method passed in.  Otherwise, the {@link #DEFAULT}
     * <code>DomainComparator</code>.
     */
    public static DomainComparator getForID(String id) {
        return (DomainComparator) FilterComparator.getForID(id, DomainComparator.comparatorList, DomainComparator.DEFAULT);
    }

    /**
     * Creates the where clause based on the pattern.
     *
     * @param fieldName the name of the field to add to the where clause
     * @param fieldValues the values to include in the where clause
     * @param isNullIncluded true if null values should be included by the
     * where clause
     * @param isStringDomain true if the field contains string values rather
     * than numeric values; in that case, the values are quoted
     * @return the generated where clause
     */
    public String createWhereClause(String fieldName, String[] fieldValues, boolean isNullIncluded, boolean isStringDomain) {

        String[] values = null;

        if (fieldValues != null && fieldValues.length > 0) {

            // Since the number of fieldValues here is dynamic, there is not
            // an individual replacement parameter in the pattern for each value.
            // Instead, the pattern assumes a single value which contains a
            // comma-separated list of values
            StringBuffer commaSeparatedValues = new StringBuffer();
            boolean isAfterFirst = false;

            for (Iterator it = Arrays.asList(fieldValues).iterator(); it.hasNext(); ) {
                String nextValue  = (String) it.next();

                if (isAfterFirst) {
                    commaSeparatedValues.append(", ");
                }

                if (isStringDomain) {
                    commaSeparatedValues.append("'").append(nextValue).append("'");
                } else {
                    commaSeparatedValues.append(nextValue);
                }

                isAfterFirst = true;
            }

            values = new String[]{commaSeparatedValues.toString()};
        }


        // Return where clause with comma-separated values as first and only
        // parameter in pattern
        return super.createWhereClause(fieldName, values, isNullIncluded);
    }

    //--------------------------------------------------------------------------
    //Implementation methods
    //--------------------------------------------------------------------------

    /**
     * Creates a new DomainComparator.
     *
     * @param id a <code>String</code> value which uniquely identifies this
     * DomainComparator.
     * @param displayToken a <code>String</code> value which contains a property
     * value which points to a string which will used to display this
     * <code>FilterComparator</code>.
     */
    private DomainComparator(String id, String displayToken, String comparisonPattern, String nullComparisonPattern) {
        super(id, displayToken, comparisonPattern, nullComparisonPattern);
        assertUniqueID(comparatorList, this);
        comparatorList.add(this);
    }

}
