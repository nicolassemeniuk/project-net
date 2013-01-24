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

 package net.project.calendar.workingtime;

import java.math.BigDecimal;

import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a minute time quantity.
 * <p>
 * This is a simple amount of minutes.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class SimpleTimeQuantity {

    // Implementation Note:  This was previously implemented using a TimeQuantity internally.
    // However, there is very little to be gained from this; we always maintain minutes and there
    // are only two places that we must convert to other units.
    // SimpleTimeQuantity is used very frequently and has been measured to be a performance
    // bottleneck.  Thus we maintain a long amount internally.

    /** Constant simply used to avoid overhead of constructing new object every time its needed. */
    private static final BigDecimal MINUTES_IN_HOUR = new BigDecimal("60");

    /**
     * The amount of minutes.
     */
    private final long minutes;

    /**
     * Creates a new time quantity for the specified number of hours and
     * minutes.
     * @param hours the hours; greater than or equal to zero
     * @param minutes the minutes in the range 0..59
     * @throws IllegalArgumentException if the hour or minutes are invalid
     */
    SimpleTimeQuantity(long hours, long minutes) {
        this(hours, minutes, false);
    }

    /**
     * Creates a new time quantity for the specified number of hours and
     * minutes indicating whether it is a negative value.
     * @param hours the hours; greater than or equal to zero
     * @param minutes the minutes in the range 0..59
     * @param isNegative true if this is a negative value; false otherwise
     * @throws IllegalArgumentException if the hour or minutes are invalid
     */
    SimpleTimeQuantity(long hours, long minutes, boolean isNegative) {

        if (hours < 0) {
            throw new IllegalArgumentException("hours must be greater or equal to zero");
        }

        if (minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException("minutes must be in the range 0..59");
        }

        long totalMinutes = (hours * 60) + minutes;
        this.minutes = (isNegative ? (totalMinutes * -1) : totalMinutes);
    }

    /**
     * Creates a new time quantity for the specified hour as a decimal.
     * <p>
     * For example 8.5 is 8 hours 30 minutes.
     * </p>
     * @param hourDecimal the fractional hour value; may be negative
     */
    SimpleTimeQuantity(BigDecimal hourDecimal) {
        // Convert to minutes; rounds any fraction of minutes up
        this(hourDecimal.multiply(MINUTES_IN_HOUR).setScale(0, BigDecimal.ROUND_HALF_UP).longValue());
    }

    /**
     * Creates a new time quantity for the specified number of minutes.
     * @param minutes the minutes; may be negative
     */
    SimpleTimeQuantity(long minutes) {
        this.minutes = minutes;
    }

    /**
     * Returns the current value as a decimal hour value.
     * <p>
     * For example, 8 hours 30 minutes is 8.500.
     * </p>
     * @return the decimal hour value with a scale of 3 rounded up
     */
    public BigDecimal toHour() {
        return new TimeQuantity(minutes, TimeQuantityUnit.MINUTE).convertTo(TimeQuantityUnit.HOUR, WorkingTime.MINUTE_ARITHMETIC_SCALE, BigDecimal.ROUND_HALF_UP).getAmount();
    }

    /**
     * Returns the current value as a day value.
     * @param hoursInDay the number of hours in a day; must be greater than
     * zero
     * @return the day value with a scale of 3 rounded up
     * @throws IllegalArgumentException if hoursInDay is less than or equal to zero
     */
    BigDecimal toDay(int hoursInDay) {

        if (hoursInDay <= 0) {
            throw new IllegalArgumentException("hoursInDay must be greater than zero");
        }

        // The current minutes expressed as a day value is the ratio of
        // current minutes to minutes in day
        TimeQuantity minutesInDay = new TimeQuantity(hoursInDay, TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        return new BigDecimal(minutes).divide(minutesInDay.getAmount(), WorkingTime.MINUTE_ARITHMETIC_SCALE, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Returns the current value as a minute value.
     * @return the number of minutes
     */
    public long toMinutes() {
        return minutes;
    }

    /**
     * Adds the specified time quantity to this one.
     * @param quantity the quantity to add
     * @return the sum of the quantities
     * @throws NullPointerException if quantity is null
     */
    SimpleTimeQuantity add(SimpleTimeQuantity quantity) {
        if (quantity == null) {
            throw new NullPointerException("quantity is required");
        }

        return new SimpleTimeQuantity(this.minutes + quantity.minutes);
    }

    /**
     * Subtracts the specified time quantity from this one.
     * @param quantity the quantity to subtract
     * @return the result of the subtraction
     * @throws NullPointerException if quantity is null
     */
    SimpleTimeQuantity subtract(SimpleTimeQuantity quantity) {

        if (quantity == null) {
            throw new NullPointerException("quantity is required");
        }

        return new SimpleTimeQuantity(this.minutes - quantity.minutes);
    }

    /**
     * Divides this time quantity by the specified amount.
     * @param amount the amount to divide by
     * @return the result of the division, to the nearest minute
     * @throws NullPointerException if amount is null
     * @throws ArithmeticException for same reasons as {@link BigDecimal#divide}
     */
    SimpleTimeQuantity divide(BigDecimal amount) {

        if (amount == null) {
            throw new NullPointerException("amount is required");
        }

        // Divide the minutes by the amount, rounding up to nearest minute
        return new SimpleTimeQuantity(new BigDecimal(String.valueOf(minutes)).divide(amount, 0, BigDecimal.ROUND_HALF_UP).longValue());
    }


    /**
     * Compares the this SimpleTimeQuantity with the specified SimpleTimeQuantity.
     * @param quantity the quantity to be compared
     * @return -1, 0, 1 as this quantity is less than, equal to or greater than
     * the specified quanity
     * @throws NullPointerException if quantity is null
     */
    int compareTo(SimpleTimeQuantity quantity) {
        if (quantity == null) {
            throw new NullPointerException("quantity is required");
        }
        return (this.minutes < quantity.minutes ? -1 : (this.minutes == quantity.minutes ? 0 : 1));
    }

    /**
     * Indicates whether this time quantity is zero.
     * @return true if it is zero; false otherwise
     */
    boolean isZero() {
        return (this.minutes == 0);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleTimeQuantity)) {
            return false;
        }

        final SimpleTimeQuantity simpleTimeQuantity = (SimpleTimeQuantity) o;

        if (minutes != simpleTimeQuantity.minutes) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (int) (minutes ^ (minutes >>> 32));
    }

    public String toString() {
        return ("[" + this.minutes + " minutes]");
    }
}
