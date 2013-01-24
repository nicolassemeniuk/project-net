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
import java.math.BigInteger;

import net.project.base.property.PropertyProvider;

/**
 * Provides the number of working hours left to allocate.
 * Converts from working hours to actual hours based on the allocated
 * resource work factor.
 *
 * @author Tim Morrow
 * @since Version 7.6.2
 */
class Allocation {

    /**
     * This constant defines 30 seconds as a fraction of an hour to enough decimal places
     * to distinguish it from 29 seconds.
     */
    private static final BigDecimal THIRTY_SECONDS_AS_HOUR = new BigDecimal("0.0083334");

    /**
     * Indicates whether we're looking for an end time.
     * This is required to make decisions on whether to locate the end of
     * working time or start of working time blocks.
     */
    private final boolean isEndTimeRequired;

    /** The sum of the allocated resources; this affects the actual hours. */
    private final BigDecimal workFactor;

    /**
     * Indicates whether the workHours were positive or negative.
     * This value can change direction if more hours than remaining are subtracted
     * from the work hours.
     */
    private boolean isPositive;

    /**
     * The time remaining.  This is maintained to a precision of 3 to ensure
     * that no rounding errors occur.
     */
    private BigDecimal workHoursRemaining;

    /**
     * Creates a new work hour allocation based on the specified allocated
     * resource work factor and number of work hours remaining.
     * @param workFactor the sum of allocated resources; a value of 1 means
     * the actual hours are the same as the work hours.  A value of 2 might
     * mean 2 people are assigned 100% and the actual hours will be half
     * the work hours.
     * @param workHoursRemaining the number of work hours remaining to
     * allocate
     * @throws IllegalArgumentException if actual time (work hours / work factor) exceeds
     * about 66 years (at 24 hours per day, 365 days per year)
     */
    Allocation(BigDecimal workFactor, BigDecimal workHoursRemaining) {
        // We're looking for an end time if the initial work is positive or zero
        this.isEndTimeRequired = workHoursRemaining.signum() >=0;
        this.workFactor = workFactor;

        // Work hours stores the absoulte value
        // We maintain a scale of 3 so that our precison will not drop
        // hours and minutes
        this.isPositive = workHoursRemaining.signum() >= 0;
        this.workHoursRemaining = workHoursRemaining.abs().setScale(WorkingTime.MINUTE_ARITHMETIC_SCALE, BigDecimal.ROUND_HALF_UP);

        // Check that a sensible amount of work was specified for the work factor
        validateWorkAmount();
    }

    /**
     * Validates that the amount of work specified in combination with the
     * work factor does not exceed a sensible amount.
     * <p>
     * If the actual amount of work to be performed is too great, calculations may
     * run indefinitely.
     * MSP 2002 limits calendars to about 66 years (1/1/1984 - 12/31/2049).
     * With a standard working time calendar that is 137,760 hours.
     * Even at 24 hours per day, 365 days per year that is 578160 hours.
     * Thus, actual work cannot exceed this value.
     * </p>
     * <p>
     * It is important to check these values because we allow importation of tasks
     * without assignments.  Thus a reasonable work hour with an assignment may become
     * unreasonable with a 100% assignment.
     * </p>
     * <p>
     * For example, work hours of 1,666,666,666 hours is allowable when work factor
     * is 12,098.34 (1,209,834 % assigned) but disallowed when work factor is 100%.
     * </p>
     * @throws IllegalArgumentException if actual hours exceeds 578160.
     */
    private void validateWorkAmount() {
        if (getActualHoursRemaining().compareTo(new BigDecimal("578160")) > 0) {
            throw new IllegalArgumentException(PropertyProvider.get("prm.project.workplan.invalidmaximumhours.error.message"));
        }
    }

    /**
     * Indicates whether an end time or start time is required.
     * This is set to true if the work hours remaining specified in the
     * constructor is positive (meaning forward in time, therfore looking
     * for an end date).
     * @return true if an end time is required; false if a start time is
     * required
     */
    boolean isEndTimeRequired() {
        return this.isEndTimeRequired;
    }

    /**
     * Indicates whether there is any work left.
     * <p>
     * Defined as some work hours greater or equal 30 seconds.
     * We discard less than 30 seconds of work since it cannot be allocated as it is
     * zero minutes when rounded to the nearest minute.
     * <p>
     * @return true if there are work hours; false if there are no
     * work hours
     */
    boolean isWorkRemaining() {
        return workHoursRemaining.signum() != 0 && (workHoursRemaining.compareTo(THIRTY_SECONDS_AS_HOUR) >= 0);
    }

    /**
     * Indicates whether there are positive or negative work hours to allocate.
     * This may change depending on whether hours are added or subtracted
     * from the remaining hours.
     * @return true if there is a positive number of work hours to allocate;
     * false if there is a negative number
     */
    boolean isPositive() {
        return this.isPositive;
    }

    /**
     * Returns the actual time remaining to complete the work, considering the
     * allocated resource factor.
     * <p>
     * This is an absolute value.
     * It is the work remaining divided by the resource factor.  For example,
     * with an resource factor of 2 and 40 hours of work to do, the actual
     * hours remaining are 20.  With a resource factor of 0.25 and 40 hours
     * of work to do, the actual hours remaining are 160.
     * </p>
     * @return the actual time remaining;  this value is an absolute value.
     * Use {@link #isPositive} to determine whether the time remaining
     * is positive or negative
     */
    SimpleTimeQuantity getActualTimeRemaining() {
        return new SimpleTimeQuantity(getActualHoursRemaining());
    }

    /**
     * Returns the actual hours remaining to complete the work considering the
     * allocated resource factor.
     * @return the actual hours remaining
     */
    private BigDecimal getActualHoursRemaining() {
        return this.workHoursRemaining.divide(workFactor, WorkingTime.MINUTE_ARITHMETIC_SCALE, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Sets the number of hours remaining to zero.
     */
    void zeroHoursRemaining() {
        this.workHoursRemaining = new BigDecimal("0").setScale(WorkingTime.MINUTE_ARITHMETIC_SCALE);
    }

    /**
     * Calculates the number of actual weeks remaining and updates the
     * work hours remaining to reflect the subtracted number of weeks.
     * <p>
     * The work hours remaining are adjusted by subtracting the number
     * of whole weeks (the result) multiplied by the number of hours
     * in the week multiplied by the work factor.
     * </p>
     * @param hoursInWeek the number of working hours in a week
     * @return the number of weeks that can be added to a calendar; this
     * is a whole number that is rounded down from any fractional number
     * of weeks; it has a scale of 0; it is an absolute value
     */
    BigDecimal subtractActualWeeksRemaining(BigDecimal hoursInWeek) {

        // work hours divided by number of people who are doing it divided by hours in week
        // Gives the actual whole number of weeks needed
        BigInteger actualWeeks = getActualHoursRemaining()
                .divide(hoursInWeek, 0, BigDecimal.ROUND_DOWN).toBigInteger();

        if (actualWeeks.signum() != 0) {
            // There are more than zero actual weeks
            // Recalculate the work hours remaining
            // by subtracting the that number of hours
            this.workHoursRemaining = this.workHoursRemaining.subtract(
                    new BigDecimal(actualWeeks).multiply(hoursInWeek).multiply(this.workFactor))
                    .setScale(WorkingTime.MINUTE_ARITHMETIC_SCALE, BigDecimal.ROUND_HALF_UP);

        }

        return new BigDecimal(actualWeeks);
    }

    /**
     * Subtracts the specified number of actual hours from the remaining
     * work.
     * <p>
     * For example, if work hours remaining is 40, work factor is 2
     * then calling <code>subtractActualHoursRemaining(10)</code> will
     * leave work hours of 20 since 10 actual hours results in 20 hours
     * of work completed.
     * </p>
     * <p>
     * <b>Note:</b> If there are fewer hours remaining then the specified
     * actual hours to subtract, then this method will invert the {@link #isPositive}
     * flag and set actual hours remaining to the difference.
     * For example, if work hours remaining is 8, work factor is 1 then
     * calling <code>subtractActualHoursRemaining(10)</code> will leave
     * work hours of 2 with {@link #isPositive} inverted.
     * <br>
     * In the case where <code>isPositive</code> was true, it is now false
     * indicating to go back in time by 2 hours.  In the case where
     * <code>isPositive</code> was false, it will become true indicating
     * to go forward in time by 2 hours.
     * </p>
     * @param actualHours the number of actual hours to subtract from
     * the work hours given the work factor
     */
    void subtractActualHoursRemaining(BigDecimal actualHours) {

        BigDecimal workingHours = actualHours.multiply(workFactor);

        if (workingHours.compareTo(this.workHoursRemaining) > 0) {
            // More work than time remaining
            // Reverse direction
            this.workHoursRemaining = workingHours.subtract(this.workHoursRemaining).setScale(WorkingTime.MINUTE_ARITHMETIC_SCALE, BigDecimal.ROUND_HALF_UP);
            this.isPositive = !this.isPositive;

        } else {
            this.workHoursRemaining = this.workHoursRemaining.subtract(workingHours).setScale(WorkingTime.MINUTE_ARITHMETIC_SCALE, BigDecimal.ROUND_HALF_UP);
        }

    }

}
