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
package net.project.calendar;

import net.project.base.property.PropertyProvider;

/**
 * Class to represent a quarter of a certain year, for example "Q1 2002".
 *
 * To create a calendar quarter from a date, use
 * {@link net.project.calendar.PnCalendar#getQuarter}.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class CalendarQuarter implements Comparable {
    /**
     * An integer containing 0-3 which represents the quarter that this
     * object points to.
     */
    private int quarter;
    /**
     * Integer containing the year of this calendar quarter.
     */
    private int year;

    /**
     * Creates a new instance of CalendarQuarter while initializing the quarter
     * and year.
     *
     * @param quarter a <code>int</code> value containing 0-3 to represent the
     * first through 4 quarter of a year.
     * @param year a <code>int</code> value representing a year.
     */
    public CalendarQuarter(int quarter, int year) {
        this.quarter = quarter;
        this.year = year;
    }

    /**
     * Get the quarter of the year that this object points to.
     *
     * @return a <code>int</code> value containing 0-3 which represents which
     * quarter of the year this object represents.
     */
    public int getQuarter() {
        return quarter;
    }

    /**
     * Sets the quarter of the year that this object points to.
     *
     * @param quarter an <code>int</code> value containing 0-3 which represents
     * which quarter of the year this object represents.
     */
    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    /**
     * Get the year that this object points to.
     *
     * @return a <code>int</code> object pointing to a year.
     */
    public int getYear() {
        return year;
    }

    /**
     * Set the year in which this calendar quarter occurs.  For example, for
     * "Q1 2000", setYear(2000).
     *
     * @param year a <code>int</code> value containing the year in which this
     * calendar quarter occurs.
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Add a given number of quarters to this CalendarQuarter to produce a new
     * CalendarQuarter.  This addition returns an entirely separate object and
     * makes no change to the current object.
     *
     * @param numberOfQuarters an <code>int</code> value containing the number
     * of quarters to add to this quarter.
     * @return a <code>CalendarQuarter</code> object which is preinitialized to
     * the an amount which is the sum of the current <code>CalendarQuarter</code>
     * plus the number of quarters specified by the <code>numberOfQuarters</code>
     * parameter.
     */
    public CalendarQuarter add(int numberOfQuarters) {
        int newQuarters = quarter+(numberOfQuarters % 4);
        int newYears = year+(numberOfQuarters / 4);

        //Test to make sure the user wasn't trying to add a negative number of quarters.
        while (newQuarters > 3) {
            //The modulus operator produced a negative number of quarters
            //between 1 and 3.  We need to subtract this from the equivalent of
            //of "10" in base 4 to find the correct number of quarters.
            newQuarters = newQuarters-4;

            //We've crossed a year boundary, decrement it.
            newYears++;
        }
        //If we crossed a year boundary when subtracting quarters, fix it
        while (newQuarters < 0) {
            newQuarters += 4;
            newYears--;
        }


        return new CalendarQuarter(newQuarters,newYears);
    }

    /**
     * Subtract the parameter from this CalendarQuarter to produce an integer
     * which is the number of quarters between the two.
     *
     * @param subtractant a <code>CalendarQuarter</code> amount that we are
     * going to subtract from this amount.
     * @return an <code>int</code> value which represents the number of quarters
     * between two CalendarQuarters.
     */
    public int subtract(CalendarQuarter subtractant) {
        int years = year - subtractant.year;
        int quarters = quarter - subtractant.quarter;

        return years*4 + quarters;
    }

    /**
     * Subtract a given number of calendar quarters from this calendar quarter
     * to produce a new calendar quarter.  For example, if you were to subtract
     * 4 quarters from Q4 2003, you would get Q4 2002.
     *
     * @param quartersToSubtract a <code>int</code> value containing the number
     * of quarters to subtract.
     * @return a <code>CalendarQuarter</code> object which is completely
     * independent of the current object and which contains the result of this
     * objects subtraction.
     */
    public CalendarQuarter subtract(int quartersToSubtract) {
        int newQuarter = quarter-(quartersToSubtract%4);
        int newYear = year-(quartersToSubtract/4);

        //If we crossed a year boundary when subtracting quarters, fix it
        while (newQuarter < 0) {
            newQuarter += 4;
            newYear--;
        }
        //Test to make sure the user wasn't trying to add a negative number of quarters.
        while (newQuarter > 3) {
            //The modulus operator produced a negative number of quarters
            //between 1 and 3.  We need to subtract this from the equivalent of
            //of "10" in base 4 to find the correct number of quarters.
            newQuarter = newQuarter-4;

            //We've crossed a year boundary, decrement it.
            newYear++;
        }

        return new CalendarQuarter(newQuarter, newYear);
    }

    /**
     * Get the latest quarter of the two quarters passed into this method.
     *
     * @param q1 a <code>CalendarQuarter</code> object which we are going to
     * compare to the other parameter in order to determine which is the latest.
     * @param q2 a <code>CalendarQuarter</code> object which we are going to
     * compare to the other parameter in order to determine which is the latest.
     * @return a <code>CalendarQuarter</code> which is the latest of the two
     * parameters.
     */
    public static CalendarQuarter max(CalendarQuarter q1, CalendarQuarter q2) {
        if (q1.compareTo(q2) >= 0) {
            return q1;
        } else {
            return q2;
        }
    }

    /**
     * Get the earliest quarter of the two quarters passed into this method.
     *
     * @param q1 a <code>CalendarQuarter</code> object which we are going to
     * compare to the other parameter in order to determine which is the earliest.
     * @param q2 a <code>CalendarQuarter</code> object which we are going to
     * compare to the other parameter in order to determine which is the earliest.
     * @return a <code>CalendarQuarter</code> which is the earliest of the two
     * parameters.
     */
    public static CalendarQuarter min(CalendarQuarter q1, CalendarQuarter q2) {
        if (q1.compareTo(q2) > 0) {
            return q2;
        } else {
            return q1;
        }
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    public String toString() {
        return PropertyProvider.get("prm.global.calendarquarter.name",
            String.valueOf(quarter+1), String.valueOf(year));
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarQuarter)) return false;

        final CalendarQuarter calendarQuarter = (CalendarQuarter)o;

        if (quarter != calendarQuarter.quarter) return false;
        if (year != calendarQuarter.year) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = quarter;
        result = 29 * result + year;
        return result;
    }

    /**
     * Compare this CalendarQuarter to another CalendarQuarter in order to
     * implement the {@link java.util.Comparator} interface.
     *
     * @param o an object to compare to this object.
     * @return > 0 if the object being compared is less than this object, 0
     * if the objects are equivalent, or < 1 if the object being compared is
     * greater than this object.
     * @throws ClassCastException if the object passed through the <code>o</code>
     * parameter is not a <code>CalendarQuarter</code> object.
     */
    public int compareTo(Object o) {
        int valueToReturn;

        CalendarQuarter quarterToCompare = (CalendarQuarter)o;

        //Test to see if the compared object is null
        if (quarterToCompare == null) {
            return -1;
        }

        Integer thisYear = new Integer(year);
        Integer comparisonYear = new Integer(quarterToCompare.year);

        int yearComparison = thisYear.compareTo(comparisonYear);
        if (yearComparison != 0) {
            //The quarters are not in the same year.  The comparison of the years
            //will be a proper return value.
            valueToReturn = yearComparison;
        } else {
            //Both quarters are in the same year, compare the quarter values
            //themselves to determine the proper return value.
            Integer thisQuarter = new Integer(quarter);
            Integer comparisonQuarter = new Integer(quarterToCompare.quarter);

            valueToReturn = thisQuarter.compareTo(comparisonQuarter);
        }

        return valueToReturn;
    }

}
