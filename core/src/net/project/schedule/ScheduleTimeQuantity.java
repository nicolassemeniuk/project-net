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

package net.project.schedule;

import net.project.security.SessionManager;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

import java.math.BigDecimal;

/**
 * Provides a helper class for dealing with schedule time quantities.
 * <p/>
 * The number of hours in a day, hours in a week and days in a month is theoretically
 * configurable.  In the absence of configurability of these values, the defaults are:
 * <ul>
 * <li>8 hours in a day
 * <li>40 hours in a week
 * <li>20 days in a month
 * </ul>
 * This class provides those values and conversion functionality.
 *
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class ScheduleTimeQuantity {

    public static final BigDecimal DEFAULT_HOURS_PER_DAY = new BigDecimal("8.00");
    public static final BigDecimal DEFAULT_HOURS_PER_WEEK = new BigDecimal("40.00");
    public static final BigDecimal DEFAULT_DAYS_PER_MONTH = new BigDecimal("20");

    /**
     * Converts the specified time quantity to hours using default hours per day, hours per week and
     * days per month when unit is greater than hours.
     * <p>
     * This is useful before manipulating schedule time quantites (such as adding time quantities together).
     * </p>
     *
     * @param timeQuantity the time quantity to convert
     * @return the time quantity expressed as hours; when the specified time quantity's unit is greater
     *         than HOURs, no loss of precision occurs; when the unit is smaller than HOURs, two decimal places
     *         are preserved
     * @throws IllegalArgumentException if the specified time quantity's unit is greater than month
     */
    public static TimeQuantity convertToHour(TimeQuantity timeQuantity) {
        TimeQuantity result = timeQuantity;
        Schedule schedule = SessionManager.getSchedule();

        if (timeQuantity.getUnits().compareTo(TimeQuantityUnit.MONTH) > 0) {
            throw new IllegalArgumentException("Unable to convert time quantity with unit greater than month: " + timeQuantity.getUnits().getAbbreviation());
        }

        if (result.getUnits().equals(TimeQuantityUnit.MONTH)) {
            result = new TimeQuantity(result.getAmount().multiply(schedule != null ? schedule.getDaysPerMonth() : DEFAULT_DAYS_PER_MONTH), TimeQuantityUnit.DAY);
        }

        if (result.getUnits().equals(TimeQuantityUnit.WEEK)) {
            result = new TimeQuantity(result.getAmount().multiply(schedule != null ? schedule.getHoursPerWeek() : DEFAULT_HOURS_PER_WEEK), TimeQuantityUnit.HOUR);
        }

        if (result.getUnits().equals(TimeQuantityUnit.DAY)) {
            result = new TimeQuantity(result.getAmount().multiply((schedule != null && schedule.getHoursPerDay() != null ? schedule.getHoursPerDay() :DEFAULT_HOURS_PER_DAY)), TimeQuantityUnit.HOUR);
        }

        if (result.getUnits().compareTo(TimeQuantityUnit.HOUR) < 0) {
            result = result.convertTo(TimeQuantityUnit.HOUR, 3, BigDecimal.ROUND_HALF_UP);
        }

        // Sanity check that after conversion, units are HOUR
        assert result.getUnits().equals(TimeQuantityUnit.HOUR);

        return result;
    }

    /**
     * Converts the specified time quantity to the specified desired unit, with scale and rounding mode when
     * appropriate.
     * <p>
     * This is useful after manipulating schedule time quantities to convert back to an input time quantity.
     * </p>
     *
     * @param timeQuantity the time quantity to convert; conversion will only occur if the current unit is not equal
     *                     to the desired unit
     * @param desiredUnit  the desired unit
     * @param scale        the scale to use when converting to a greater unit
     * @param roundingMode the rounding mode to use when converting to a greater unit
     * @return the converted time quantity, or the original time quantity if it is already in the desired unit
     * @throws NullPointerException if timeQuantity or desiredUnit is null
     */
    public static TimeQuantity convertToUnit(final TimeQuantity timeQuantity, final TimeQuantityUnit desiredUnit, final int scale, final int roundingMode) {

        if (timeQuantity == null || desiredUnit == null) {
            throw new NullPointerException("timeQuantity and desiredUnit are required");
        }

        TimeQuantity result = timeQuantity;
        Schedule schedule = SessionManager.getSchedule();

        if (result.getUnits().compareTo(desiredUnit) == 0) {
            // Already in desired unit
            // Scale it
            result = result.convertToScale(scale, roundingMode);

        } else {

            // Start by calculating HOURs
            TimeQuantity resultHours;

            if (result.getUnits().equals(TimeQuantityUnit.HOUR)) {
                // Already hours
                resultHours = result;

            } else if (result.getUnits().compareTo(TimeQuantityUnit.HOUR) > 0) {
                // Current amount greater than hours
                // Convert to hours using special amounts
                resultHours = convertToHour(result);

            } else {
                // Current unit less than hours
                // Use standard conversion to hours, maintaining precision
                resultHours = result.convertTo(TimeQuantityUnit.HOUR, 10, BigDecimal.ROUND_HALF_UP);
            }

            // Now convert hours to appropriate unit
            if (desiredUnit.compareTo(TimeQuantityUnit.HOUR) <= 0) {
                // Desired unit is hours or less; use standard conversion
                result = resultHours.convertTo(desiredUnit, scale, roundingMode);

            } else {
                // Desired unit is greater than hours
                if (desiredUnit.equals(TimeQuantityUnit.DAY)) {
                    result = new TimeQuantity(resultHours.getAmount().divide((schedule != null ? schedule.getHoursPerDay() : DEFAULT_HOURS_PER_DAY), scale, roundingMode), TimeQuantityUnit.DAY);

                } else if (desiredUnit.equals(TimeQuantityUnit.WEEK)) {
                    result = new TimeQuantity(resultHours.getAmount().divide((schedule != null ? schedule.getHoursPerWeek() : DEFAULT_HOURS_PER_WEEK), scale, roundingMode), TimeQuantityUnit.WEEK);

                } else {
                    // MONTH
                    BigDecimal daysAmount = resultHours.getAmount().divide((schedule != null ? schedule.getHoursPerDay() : DEFAULT_HOURS_PER_DAY), 10, BigDecimal.ROUND_HALF_UP);
                    result = new TimeQuantity(daysAmount.divide(schedule != null ? schedule.getDaysPerMonth() : DEFAULT_DAYS_PER_MONTH, scale, roundingMode), TimeQuantityUnit.MONTH);
                }

                // Scale the result appropriately
                result = result.convertToScale(scale, roundingMode);
            }

        }

        // Sanity check; resultant units are what we wanted
        assert result.getUnits().equals(desiredUnit);

        return result;
    }

}
