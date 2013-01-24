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

 package net.project.util;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;

/**
 * Class which defines all the available units of time that can be used by the
 * time quantity class.
 *
 * @author Matthew Flower
 * @see net.project.util.TimeQuantity
 */
public class TimeQuantityUnit implements IHTMLOption, Comparable, Serializable {

    /** ArrayList to contain all possible units of time quantity. */
    private static final ArrayList quantities = new ArrayList();

    /**
     * Static variable which defines a time quantity of seconds.  This value can
     * be localized by modifying the token value
     * <code>prm.global.timequantity.second.name</code>.
     */
    public static final TimeQuantityUnit SECOND = new TimeQuantityUnit(new BigDecimal("1"), 1, "prm.global.timequantity.second.name", GregorianCalendar.SECOND, "second");
    /**
     * Static variable which defines a time quantity of minutes.  This value can
     * be localized by modifying the token value
     * <code>prm.global.timequantity.minute.name</code>
     */
    public static final TimeQuantityUnit MINUTE = new TimeQuantityUnit(SECOND.getBase().multiply(new BigDecimal(60)), 2, "prm.global.timequantity.minute.name", GregorianCalendar.MINUTE, "minute");
    /**
     * Static variable which defines a time quantity of hours.  This value can be localized by modifying the token
     * value <code>prm.global.timequantity.hour</code>
     */
    public static final TimeQuantityUnit HOUR = new TimeQuantityUnit(MINUTE.getBase().multiply(new BigDecimal(60)), 4, "prm.global.timequantity.hour.name", GregorianCalendar.HOUR, "hour");
    /**
     * Static variable which defines a time quantity of days.  This value can be localized by modifying the token
     * value <code>prm.global.timequantity.day</code>
     */
    public static final TimeQuantityUnit DAY = new TimeQuantityUnit(HOUR.getBase().multiply(new BigDecimal(24)), 8, "prm.global.timequantity.day.name", GregorianCalendar.DATE, "day");
    /**
     * Static variable which defines a time quantity of stardard 7-day week.  This value can be localized by modifying the token
     * value <code>prm.global.timequantity.week</code>
     */
    public static final TimeQuantityUnit WEEK = new TimeQuantityUnit(DAY.getBase().multiply(new BigDecimal(7)), 16, "prm.global.timequantity.week.name", GregorianCalendar.WEEK_OF_YEAR, "week");
    //MAFTODO: Get rid of the work day quantities when we add resource calendars, or at least fix how they are used in net.project.util.DateUtils.
    /**
     * Static variable which defines a time quantity of a 8 hour work day.  This
     * value can be localized by modifying the token value
     * <code>prm.global.timequantity.workday</code>.  Ultimately, it is
     * envisioned that this specific <code>TimeQuantityUnit</code> will have a
     * corresponding TimeQuantityUnit that will accept a Resource calendar so
     * you can indicate the working days and non-working days for a resource.
     */
    public static final TimeQuantityUnit STANDARD_WORK_DAY = new TimeQuantityUnit(HOUR.getBase().multiply(new BigDecimal(8)), 32,"prm.global.timequantity.workday.name", GregorianCalendar.DATE, "workday");
    /**
     * Static variable which defines a time quantity of a 40-hour work week.
     * This value can be localized by modifying the token value
     * <code>prm.global.timequantity.workweek</code>.  Ultimately, it is
     * envisioned that this specific <code>TimeQuantityUnit</code> will have a
     * corresponding TimeQuantityUnit that will accept a Resource calendar so
     * you can indicate the working days and non-working days for a resource.
     */
    public static final TimeQuantityUnit STANDARD_WORK_WEEK = new TimeQuantityUnit(HOUR.getBase().multiply(new BigDecimal(40)), 64,"prm.global.timequantity.workweek.name", GregorianCalendar.WEEK_OF_YEAR, "workweek");
    /**
     * Static variable which defines a time quantity of seconds.  This value can be localized by modifying the token
     * value <code>prm.global.timequantity.second.name</code>.
     */
    public static final TimeQuantityUnit MILLISECOND = new TimeQuantityUnit(new BigDecimal("0.001"), 128, "prm.global.timequantity.millisecond.name", GregorianCalendar.MILLISECOND, "millisecond");
    /**
     * Static variable which defined a time quantity of a 30 day month.
     */
    public static final TimeQuantityUnit MONTH = new TimeQuantityUnit(DAY.getBase().multiply(new BigDecimal(20)), 256, "prm.global.timequantity.month.name", GregorianCalendar.MONTH, "month");
    /**
     * Defines the default <code>TimeQuantityUnit</code> which will be used by getForID if there is no matching id.
     */
    public static final TimeQuantityUnit DEFAULT = DAY;

    private static List hierarchyOfUnits = new ArrayList();
    static {
        hierarchyOfUnits.add(MILLISECOND);
        hierarchyOfUnits.add(SECOND);
        hierarchyOfUnits.add(MINUTE);
        hierarchyOfUnits.add(HOUR);
        hierarchyOfUnits.add(DAY);
        hierarchyOfUnits.add(WEEK);
        hierarchyOfUnits.add(MONTH);
    }

    /**
     * Get the TimeQuantityUnit which corresponds to the database id supplied by the uniqueID.
     *
     * @param uniqueID a <code>int</code> value which defines which TimeQuantityUnit
     * we are looking up.
     * @return the <code>TimeQuantityUnit</code> which corresponds to the
     * uniqueID supplied by the <code>uniqueID</code> parameter.  If the
     * <code>uniqueID</code> does not correspond to a valid uniqueID, the
     * default <code>TimeQuantityUnit</code> will be returned instead.
     */
    public static TimeQuantityUnit getForID(int uniqueID) {
        TimeQuantityUnit toReturn = DEFAULT;
        Iterator it = quantities.iterator();

        //Iterate through all instantiated <code>TimeQuantityUnit</code> values
        //looking for one that matches the parameter.
        while (it.hasNext()) {
            TimeQuantityUnit current = (TimeQuantityUnit)it.next();
            if (current.getUniqueID() == uniqueID) {
                toReturn = current;
                break;
            }
        }

        return toReturn;
    }

    /**
     * Get the TimeQuantityUnit which corresponds to the database id supplied by the uniqueID.
     *
     * @param uniqueIDString a <code>String</code> value which defines which
     * TimeQuantityUnit we are looking up.  This is the unique id for the time
     * quantity unit which can be fetched from the getID() method.
     * @return the <code>TimeQuantityUnit</code> which corresponds to the
     * uniqueID supplied by the <code>uniqueID</code> parameter.
     * @throws NullPointerException if the parameter is null
     * @throws IllegalArgumentException if the specified id is not a number or
     * does not match the ID of a <code>TimeQuantityUnit</code>
     */
    public static TimeQuantityUnit getForID(String uniqueIDString) {

        if (uniqueIDString == null) {
            throw new NullPointerException("uniqueIDString is required");
        }

        TimeQuantityUnit toReturn = null;

        try {
            int uniqueID = Integer.parseInt(uniqueIDString);

            Iterator it = quantities.iterator();

            //Iterate through all instantiated <code>TimeQuantityUnit</code> values
            //looking for one that matches the parameter.
            while (it.hasNext()) {
                TimeQuantityUnit current = (TimeQuantityUnit)it.next();
                if (current.getUniqueID() == uniqueID) {
                    toReturn = current;
                    break;
                }
            }

        } catch (NumberFormatException e) {
            throw (IllegalArgumentException) new IllegalArgumentException("Illegal id " + uniqueIDString).initCause(e);
        }

        if (toReturn == null) {
            throw new IllegalArgumentException("No TimeQuantityUnit found of id " + uniqueIDString);
        }

        return toReturn;
    }

    /**
     * Get the HTML code that will render a list of all of the available
     * <code>TimeQuantityUnits</code> variables.
     *
     * @param selectedUnit a <code>TimeQuantityUnit</code> variable which
     * identifies which unit should be selected in the HTML option list.
     * @return a <code>String</code> value containing all of the HTML needed to
     * render the contents of an HTML <select></select> list.  This method will
     * only return the individual <option></option> values for the list, creating
     * the select list is left for the user so they can define the field name
     * for the list.
     * @see #getHTMLOptionList(TimeQuantityUnit, TimeQuantityUnit[])
     * @see #getHTMLOptionList(TimeQuantityUnit, List)
     */
    public static String getHTMLOptionList(TimeQuantityUnit selectedUnit) {
        return getHTMLOptionList(selectedUnit, quantities);
    }

    /**
     * Get the HTML code that will render a list of all of the available
     * <code>TimeQuantityUnits</code> variables.  This overloaded method is
     * provided so the programmer can decide which time quantities should be
     * shown, instead of showing all of them by default.
     *
     * @param selectedUnit a <code>TimeQuantityUnit</code> variable which
     * identifies which unit should be selected in the HTML option list.
     * @param availableUnits an array that contains all of the
     * <code>TimeQuantityUnit</code> objects that should be in the list.
     * @return a <code>String</code> value containing all of the HTML needed to
     * render the contents of an HTML <select></select> list.  This method will
     * only return the individual <option></option> values for the list, creating
     * the select list is left for the user so they can define the field name
     * for the list.
     *
     * @see #getHTMLOptionList(TimeQuantityUnit)
     * @see #getHTMLOptionList(TimeQuantityUnit, List)
     */
    public static String getHTMLOptionList(TimeQuantityUnit selectedUnit, TimeQuantityUnit[] availableUnits) {
        return getHTMLOptionList(selectedUnit, Arrays.asList(availableUnits));
    }

   /**
    * Get the HTML code that will render a list of all of the available
    * <code>TimeQuantityUnits</code> variables.  This overloaded method is
    * provided so the programmer can decide which time quantities should be
    * shown, instead of showing all of them by default.
    *
    * @param selectedUnit a <code>TimeQuantityUnit</code> variable which
    * identifies which unit should be selected in the HTML option list.
    * @param availableUnits an array that contains all of the
    * <code>TimeQuantityUnit</code> objects that should be in the list.
    * @return a <code>String</code> value containing all of the HTML needed to
    * render the contents of an HTML <select></select> list.  This method will
    * only return the individual <option></option> values for the list, creating
    * the select list is left for the user so they can define the field name
    * for the list.
    *
    * @see #getHTMLOptionList(TimeQuantityUnit)
    * @see #getHTMLOptionList(TimeQuantityUnit, TimeQuantityUnit[])
    */
    public static String getHTMLOptionList(TimeQuantityUnit selectedUnit, List availableUnits) {
        StringBuffer html = new StringBuffer();

        Iterator it = availableUnits.iterator();
        while (it.hasNext()) {
            TimeQuantityUnit tqu = (TimeQuantityUnit)it.next();
            html.append("<option value=\"").append(tqu.getUniqueID()).append("\"")
                .append((selectedUnit.equals(tqu) ? " SELECTED" : ""))
                .append(">").append(tqu.getPluralName()).append("</option>\r\n");
        }

        return html.toString();
    }

    //--------------------------------------------------------------------------
    // Implementation
    //--------------------------------------------------------------------------

    /**
     * The base variable explains the relationship between this variable and the 1.  That is, if SECOND is 1, MINUTE is
     * 60, HOUR is 3600, and so on.
     */
    private final BigDecimal base;
    /**
     * An integer that is unique to every TimeQuantityUnit.  This value is primarily used for storing this value in the
     * database.
     */
    private final int uniqueID;
    /**
     * The database token that points to a english (or another language) description of the TimeQuantityUnit.
     */
    private final String nameToken;
    /**
     * This is the equivalend time quantity in a Gregorian Calendar.  This is
     * important for when you need to add a time quantity using a date in a
     * Gregorian Calendar object.
     */
    private final int gregorianEquivalent;
    /**
     * Internal identifier, used for <code>toString</code> conversions.
     */
    private final String internalName;

    /**
     * Private constructor for TimeQuantityUnit that initializes all of the private variables.
     *
     * @param base a <code>BigDecimal</code> value that identifies how this TimeQuantityUnit relates to the default
     * of {@see #SECOND}.
     * @param uniqueID a value that uniquely identifies this <code>TimeQuantityUnit</code> in the database.
     * @param nameToken a <code>String</code> value that points to an english (or another language) description of this
     * @param internalName the internal name of the time quantity, used for display during debugging
     */
    private TimeQuantityUnit(BigDecimal base, int uniqueID, String nameToken, int gregorianEquivalent, String internalName) {
        this.base = base;
        this.uniqueID = uniqueID;
        this.nameToken = nameToken;
        this.gregorianEquivalent = gregorianEquivalent;
        this.internalName = internalName;
        quantities.add(this);
    }

    /**
     * Get the base value for this TimeQuantityUnit which identifies how this <code>TimeQuantityUnit</code> relates to
     *
     * {@link #SECOND}.
     * @return The <code>BigDecimal</code> value of TimeQuantityUnit which identifies how this <code>TimeQuantityUnit</code>
     * relates to {@link #SECOND}.
     */
    public BigDecimal getBase() {
        return base;
    }

    /**
     * Get the value that uniquely identifies this TimeQuantityUnit, for use (for example) in the database.
     *
     * @return the <code>int</code> value that uniquely identifies this TimeQuantityUnit.
     */
    public int getUniqueID() {
        return uniqueID;
    }

    /**
     * Get the human-readable name of this <code>TimeQuantityUnit</code>.  This value is localized using tokens.
     *
     * @return A <code>String</code> value which contains the human-readable for of this <code>TimeQuantityUnit</code>
     */
    public String getName() {
        return PropertyProvider.get(nameToken);
    }

    /**
     * Get the plural version of the human-readable name of this <code>TimeQuantityUnit</code>.
     * For example, if the normal unit is second, this method would return seconds.
     *
     * This name is looked up by appending ".plural" to the normal token name.
     *
     * @return a <code>String</code> which contains the plural version of this
     * time quantity unit.
     */
    public String getPluralName() {
        return PropertyProvider.get(nameToken+".plural");
    }

    /**
     * Get the singular abbreviated name for the current time unit.  For example,
     * <code>TimeQuantityUnit.HOUR.getAbbreviation()</code> should be equal to
     * "hr".  If an abbreviation is not available for this time quantity, the
     * original time quantity name will be used.  For example, if a token
     * hadn't been available the abbreviation for HOOR, "hour" would have been
     * returned instead.
     *
     * This valid is constructed based on appending ".abbreviation" to the
     * initial token name that was specified as part of the constructor.
     *
     * @return a <code>String</code> value containing the abbreviation of the
     * current time quantity.
     * @see #getPluralAbbreviation
     */
    public String getAbbreviation() {
        String abbreviation = PropertyProvider.get(nameToken+".abbreviation");

        if ((abbreviation == null) || (abbreviation.trim().length() == 0)) {
            abbreviation = getName();
        }

        return abbreviation;
    }

    /**
     * Get the plural abbreviated name for the current time unit.  For example,
     * <code>TimeQuantityUnit.HOUR.getPluralAbbreviation()</code> should be
     * equal to "hrs".  If an abbreviation is not available for this time
     * quantity, {@link #getPluralName} will be used.  For example, if a token
     * hadn't been available the abbreviation for HOOR, "hours" would have been
     * returned instead.
     *
     * This valid is constructed based on appending ".abbreviation.plural" to
     * the initial token name that was specified as part of the constructor.
     *
     * @return a <code>String</code> value containing the abbreviation of the
     * current time quantity.
     * @see #getAbbreviation
     */
    public String getPluralAbbreviation() {
        String abbreviation = PropertyProvider.get(nameToken+".abbreviation.plural");

        if ((abbreviation == null) || (abbreviation.trim().length() == 0)) {
            abbreviation = getPluralName();
        }

        return abbreviation;
    }

    /**
     * This is the equivalent to this time quantity unit in the gregorian
     * calendar.  This can be used with the {@link java.util.Calendar#add}
     * and {@link java.util.Calendar#roll} methods for the field parameter.
     *
     * @return a <code>int</code> value that can be used in the add and roll
     * methods of the java.util.Calendar objects
     */
    public int getGregorianEquivalent() {
        return gregorianEquivalent;
    }

    /**
     * For the current TimeQuantityUnit, return the next smallest unit.  For
     * example, for a MINUTE, return a SECOND; for a SECOND, return a
     * MILLISECOND.
     *
     * @return a <code>TimeQuantityUnit</code> which is the next smallest unit
     * from the current unit.
     */
    public TimeQuantityUnit getSmallerUnit() {
        TimeQuantityUnit toTest = this;
        if (toTest == STANDARD_WORK_DAY) {
            toTest = DAY;
        } else if (toTest == STANDARD_WORK_WEEK) {
            toTest = WEEK;
        }

        int index = hierarchyOfUnits.indexOf(toTest);

        if (index < 1) {
            return null;
        } else {
            return (TimeQuantityUnit)hierarchyOfUnits.get(index-1);
        }
    }

    /**
     * For the current TimeQuantityUnit, return the next largest unit.  For
     * example, for a MINUTE, return an HOUR; for an HOUR, return a DAY.
     *
     * @return a <code>TimeQuantityUnit</code> which is the next largest unit
     * from the current unit.
     */
    public TimeQuantityUnit getLargerUnit() {
        int index = hierarchyOfUnits.indexOf(this);

        if (index == hierarchyOfUnits.size()-1 || index == -1) {
            return null;
        } else {
            return (TimeQuantityUnit)hierarchyOfUnits.get(index+1);
        }
    }

    public boolean equals(Object o) {
        boolean isEqual = true;

        if (o instanceof TimeQuantityUnit) {
            isEqual = (((TimeQuantityUnit)o).getUniqueID() == this.getUniqueID());
        } else {
            isEqual = false;
        }

        return isEqual;
    }

    public int hashCode() {
        return this.getUniqueID();
    }

    /**
     * Compares this unit to the specified unit with the comparison
     * based on the unit hierarchy where a smaller unit is less than
     * a larger unit.
     * <p>
     * For example, <code>TimeQuantityUnit.MINUTE < TimeQuantityUnit.DAY</code>.
     * </p>
     * <p>
     * Hierarchy is (where first item has lowest value):
     * MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH.
     * </p>
     * @param o the time quantity to compare to
     * @return the result of the comparison; less than zero if this unit is smaller
     * than the specified unit; zero if units are equal; greater than zero if
     * this unit is greater than the specified unit
     */
    public int compareTo(Object o) {
        if (!(o instanceof TimeQuantityUnit))
            throw new ClassCastException("Must be of type TimeQuantityUnit");

        int thisindex = hierarchyOfUnits.indexOf(this);
        int thatindex = hierarchyOfUnits.indexOf(o);

        if (thisindex < thatindex) {
            return -1;
        } else if (thisindex == thatindex) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Returns a string representation of this <code>TimeQuantityUnit</code>,
     * suitable for debugging only.
     * @return the string representation
     */
    public String toString() {
        return this.internalName;
    }

    //
    // Implementing IHTMLOption
    //

    public String getHtmlOptionValue() {
        return String.valueOf(getUniqueID());
    }

    public String getHtmlOptionDisplay() {
        return getPluralName();
    }
}

