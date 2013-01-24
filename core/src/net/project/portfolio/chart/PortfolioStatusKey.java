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
package net.project.portfolio.chart;

import net.project.calendar.CalendarQuarter;
import net.project.code.ColorCode;

/**
 * In order to roll up data for this report, there must be a "count" of
 * projects which correspond to a quarter and a color code.  This key
 * represents a combination of that quarter and color code which will be
 * used to look up the count of how many project occurred with those
 * parameters.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class PortfolioStatusKey implements Comparable {
    /** The quarter in which the project occurrence is supposed to be completed. */
    private CalendarQuarter quarter;
    /** The color status of the project. */
    private ColorCode colorCode = null;
    /**
     * Static value for "RED" color codes which allows us to sort RED color
     * codes earliest on the bar chart.
     */
    private static Integer RED_CODE = new Integer(0);
    /**
     * Static value for "YELLOW" color codes which allows us to sort YELLOW color
     * codes in the middle of any bar of the bar chart.
     */
    private static Integer YELLOW_CODE = new Integer(1);
    /**
     * Static value for "GREEN" color codes which allows us to sort GREEN color
     * codes at the top of bars on the bar chart.
     */
    private static Integer GREEN_CODE = new Integer(2);

    /**
     * Get the order of this color code in the bar chart.
     *
     * @param code a <code>ColorCode</code> object which describes the status of
     * a project.
     * @return a <code>int</code> which indicates the order in which this color
     * appears in a chart.
     */
    private Integer getColorCodeSequence(ColorCode code) {
        if (code.equals(ColorCode.RED)) {
            return RED_CODE;
        } else if (code.equals(ColorCode.YELLOW)) {
            return YELLOW_CODE;
        } else {
            return GREEN_CODE;
        }
    }

    /**
     * Standard constructor to assemble a portfolio status key object from its
     * component parts.
     *
     * @param quarter a <code>CalendarQuarter</code> which indicates the quarter
     * in which a project is completed.
     * @param colorCode a <code>ColorCode</code> which indicates the color code
     * of a project.
     */
    PortfolioStatusKey(CalendarQuarter quarter, ColorCode colorCode) {
        this.quarter = quarter;
        this.colorCode = colorCode;
    }

    /**
     * Get the quarter component of this key.
     *
     * @return a <code>CalendarQuarter</code> object which is the quarter
     * component of the key.
     */
    public CalendarQuarter getQuarter() {
        return quarter;
    }

    /**
     * Compare this PortfolioStatusKey to another PortfolioStatusKey in order to
     * implement the {@link java.util.Comparator} interface.
     *
     * @param o an object to compare to this object.
     * @return > 0 if the object being compared is greater than this object, 0
     * if the objects are equivalent, or < 1 if the object being compared is
     * less than this object.
     * @throws ClassCastException if the object passed through the <code>o</code>
     * parameter is not a <code>PortfolioStatusKey</code> object.
     */
    public int compareTo(Object o) {
        int comparisonValue;
        PortfolioStatusKey keyToCompare = (PortfolioStatusKey)o;

        //Look for null quarters, which can occur for the "unscheduled" quarters
        //They will automatically be sorted to the end.
        if ((keyToCompare.quarter == null) && (quarter == null)) {
            //We need to compare based on color code
            comparisonValue = getColorCodeSequence(colorCode).compareTo(
                getColorCodeSequence(keyToCompare.colorCode));
        } else if (keyToCompare.quarter == null) {
            comparisonValue = 1;
        } else if (quarter == null) {
            comparisonValue = -1;
        } else {
            //No null values, compare normally
            if (keyToCompare.quarter.equals(quarter)) {
                //We need to compare based on color code
                comparisonValue = getColorCodeSequence(colorCode).compareTo(
                    getColorCodeSequence(keyToCompare.colorCode));
            } else {
                comparisonValue = quarter.compareTo(keyToCompare.quarter);
            }
        }

        return comparisonValue;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PortfolioStatusKey)) return false;

        final PortfolioStatusKey portfolioStatusKey = (PortfolioStatusKey)o;

        if (!colorCode.equals(portfolioStatusKey.colorCode)) return false;
        if (!quarter.equals(portfolioStatusKey.quarter)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = quarter.hashCode();
        result = 29 * result + colorCode.hashCode();
        return result;
    }
}
