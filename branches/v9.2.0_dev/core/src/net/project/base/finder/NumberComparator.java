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
|   $Revision: 19063 $
|       $Date: 2009-04-05 14:27:40 -0300 (dom, 05 abr 2009) $
|     $Author: nilesh $
|
+-----------------------------------------------------------------------------*/
package net.project.base.finder;

import java.util.ArrayList;
import java.util.List;

import net.project.gui.html.HTMLOptionList;
import net.project.gui.tml.TMLOptionList;

/**
 * A list of operators that can be applied to a numeric database field.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class NumberComparator extends SimplePatternComparator {

    /** The list of all NumberComparators that are available. */
    private static List comparatorList = new ArrayList();

    /** A number comparison for the equals (=) sign. */
    public static NumberComparator EQUALS = new NumberComparator("equals", "prm.global.finder.numbercomparator.equals.name", "{0} = {1}", "{0} IS NULL");

    /** A number comparison for the not equals (!=) sign. */
    public static NumberComparator NOT_EQUAL = new NumberComparator("notequals", "prm.global.finder.numbercomparator.notequal.name", "{0} <> {1}", "{0} IS NULL");

    /** A number comparison for the less than (<) sign. */
    public static NumberComparator LESS_THAN = new NumberComparator("lessthan", "prm.global.finder.numbercomparator.lessthan.name", "{0} < {1}", "{0} IS NULL");

    /** A number comparison for the greater than (>) sign. */
    public static NumberComparator GREATER_THAN = new NumberComparator("greaterthan", "prm.global.finder.numbercomparator.greaterthan.name", "{0} > {1}", "{0} IS NULL");

    /**
     * The NumberComparator that will be selected in the option list if no selection is passed in,
     * currently {@link #EQUALS}.
     */
    public static NumberComparator DEFAULT = EQUALS;

    /**
     * Get the list of available <code>NumberComparators</code> arranged as an
     * HTML option list.
     *
     * @param selected a <code>NumberComparator</code> which signifies which
     * html option is selected initially.
     * @return a <code>String</code> value containing an HTML option list as a
     * <code>String</code>.
     */
    public static String getOptionList(FilterComparator selected) {
        return HTMLOptionList.makeHtmlOptionList(NumberComparator.comparatorList, selected).toString();
    }

    /**
     * Get the number comparator which corresponds to the supplied ID, or
     * {@link #DEFAULT} if there is not one that corresponds.
     *
     * @param id a <code>String</code> value corresponding to the id value for
     * an already existing NumberComparator.
     * @return a <code>NumberComparator</code> which has the same id as the id
     * parameter the calling method passed in.  Otherwise, the {@link #DEFAULT}
     * <code>NumberComparator</code>.
     */
    public static NumberComparator getForID(String id) {
        return (NumberComparator) FilterComparator.getForID(id, NumberComparator.comparatorList, NumberComparator.DEFAULT);
    }

    //--------------------------------------------------------------------------
    //Implementation methods
    //--------------------------------------------------------------------------

    /**
     * Creates a new NumberComparator.
     *
     * @param id a <code>String</code> value which uniquely identifies this
     * NumberComparator.
     * @param displayToken a <code>String</code> value which contains a property
     * value which points to a string which will used to display this
     * <code>FilterComparator</code>.
     * @param comparisonPattern a <code>String</code> value which contains a
     * pattern string which will be used to construct a database conditional
     * clause.
     * @param nullComparisonPattern a String value containing a pattern string
     * used to construct a conditional clause for matching null values;
     * parameter {0} is assumed to be the column name and parameters
     * {1} .. {n} the values.  For example: <code>{0} IS NULL</code>
     */
    private NumberComparator(String id, String displayToken, String comparisonPattern, String nullComparisonPattern) {
        super(id, displayToken, comparisonPattern, nullComparisonPattern);
        assertUniqueID(comparatorList, this);
        comparatorList.add(this);
    }
    
    /**
     * 
     */
    public static String getTmlOptionList() {
        return TMLOptionList.makeTmlOptionList(NumberComparator.comparatorList).toString();
    }
}
