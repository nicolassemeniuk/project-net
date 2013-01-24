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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * TimeQuantity is a class designed to encapsulate the idea of a numeric quantity
 * of time along with the appropriate unit of time.  By doing this, we can more
 * easily allow conversions between time quantities and we can encourage time
 * quantities to be stored in a standard format in the database.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class TimeQuantity implements Comparable, Serializable {

    //
    // Static Members
    //
    public static TimeQuantity O_DAYS = new TimeQuantity(0, TimeQuantityUnit.DAY);
    public static TimeQuantity O_HOURS = new TimeQuantity(0, TimeQuantityUnit.HOUR);

    /**
     * Parses the specified amount and units and returns a time quantity constructed
     * from those.
     * <p>
     * The amount is assumed to be in the appropriate format for the user's locale.
     * </p>
     * @param amount the amount formatted for the user's locale
     * @param unitsID the ID of the <code>TimeQuantityUnit</code>
     * @throws ParseException if there is a problem parsing the amount for the user's locale
     * @throws IllegalArgumentException if either parameter is null or blank
     */
    public static TimeQuantity parse(String amount, String unitsID) throws ParseException {

        if (Validator.isBlankOrNull(amount) || Validator.isBlankOrNull(unitsID)) {
            throw new IllegalArgumentException("amount and unitsID are required");
        }
        NumberFormat nf = NumberFormat.getInstance();
        Number amountNumber = NumberFormat.getInstance().parseNumber(amount);
        TimeQuantityUnit unit = TimeQuantityUnit.getForID(Integer.parseInt(unitsID));

        return new TimeQuantity(amountNumber, unit);
    }

    //
    // Instance Members
    //

    /** This is the precision that we should use if the user doesn't specify precision. */
    private final int ARBITRARY_PRECISION = 10;

    /** Which time unit this time quantity is in, e.g. Seconds. */
    private TimeQuantityUnit unit;
    /** How much time is in this time quantity. (The 4 in 4 hours.) */
    private BigDecimal amount;

    /**
     * Empty constructor required for serialization.
     */
    protected TimeQuantity() {
    }

    /**
     * Public constructor that creates a new time quantity.
     *
     * @param amount A <code>Number</code> amount that indicates the numeric
     * amount of time.  (The 60 in 60 seconds)
     * @param unit A <CODE>TimeQuantityUnit</CODE> value that indicates the type
     * of time that is being recorded.  (The seconds in 60 seconds.)
     * @see #TimeQuantity(long, TimeQuantityUnit)
     */
    public TimeQuantity(Number amount, TimeQuantityUnit unit) {
        // We convert amount to a string since Java appears
        // to eliminate any precision errors
        // Otherwise, 0.5555 would create a BigDecimal of 0.55549999999999999378275106209912337362766265869140625
        this(new BigDecimal(amount.toString()), unit);
    }

    /**
     * Public constructor that creates a new time quantity.
     *
     * @param amount A <code>BigDecimal</code> amount that indicates the numeric
     * amount of time.  (The 60 in 60 seconds)
     * @param unit A <CODE>TimeQuantityUnit</CODE> value that indicates the type
     * of time that is being recorded.  (The seconds in 60 seconds.)
     * @see #TimeQuantity(long, TimeQuantityUnit)
     */
    public TimeQuantity(BigDecimal amount, TimeQuantityUnit unit) {
        this.amount = amount;
        this.unit = unit;
    }

    /**
     * Public constructor that creates a new time quantity.
     *
     * @param amount A <code>String</code> amount that indicates the numeric
     * amount of time.  (The 60 in 60 seconds).
     * @param unit A <CODE>TimeQuantityUnit</CODE> value that indicates the type
     * of time that is being recorded.  (The seconds in 60 seconds.)
     * @see #TimeQuantity(long, TimeQuantityUnit)
     */
    public TimeQuantity(String amount, TimeQuantityUnit unit) {
        this.amount = new BigDecimal(amount);
        this.unit = unit;
    }

    /**
     * Public constructor that creates a new time quantity.
     *
     * @param amount A <code>long</code> amount that indicates the numeric amount
     * of time.  (The 60 in 60 seconds)
     * @param unit A <CODE>TimeQuantityUnit</CODE> value that indicates the type
     * of time that is being recorded.  (The seconds in 60 seconds.)
     * @see #TimeQuantity(BigDecimal, TimeQuantityUnit)
     */
    public TimeQuantity(long amount, TimeQuantityUnit unit) {
        this(BigDecimal.valueOf(amount), unit);
    }

    /**
     * Public constructor that creates a new time quantity.
     *
     * @param amount A <code>double</code> amount that indicates the numeric amount
     * of time.  (The 60 in 60 seconds)
     * @param unit A <CODE>TimeQuantityUnit</CODE> value that indicates the type
     * of time that is being recorded.  (The seconds in 60 seconds.)
     * @see #TimeQuantity(BigDecimal, TimeQuantityUnit)
     * @see #TimeQuantity(long, TimeQuantityUnit)
     */
    public TimeQuantity(double amount, TimeQuantityUnit unit) {
        this(new Double(amount), unit);
    }

    /**
     * Get the units of this time quantity.  (Seconds, for example)
     *
     * @return The unit of the time quantity.  (Seconds, for example)
     * @see #getAmount
     * @return the units of time of this time quantity.
     */
    public TimeQuantityUnit getUnits() {
        return unit;
    }

    /**
     * Get the numeric amount of time in this time quantity.  (The 60 in 60 seconds.)
     *
     * @see #getUnits
     * @return the numeric amount of time in this time quantity.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Get a string representation of the time amount and quantity.  The string
     * representation is dependent on the tokens defined in TimeQuantityUnit to
     * determined a localized value for things like "seconds."  This method will
     * return a string similar to "60 Seconds"
     */
    public String toString() {
        String toReturn;

        if (amount.equals(BigDecimal.valueOf(1))) {
            toReturn = NumberFormat.getInstance().formatNumber(amount.doubleValue())
                + " " + unit.getName();
        } else {
            toReturn = NumberFormat.getInstance().formatNumber(amount.doubleValue())
                + " " + unit.getPluralName();
        }

        return toReturn;
    }

    /**
     * Get a string representation of the time amount and quantity.  The string
     * representation is dependent on the tokens defined in TimeQuantityUnit to
     * determined a localized value for things like "seconds."  This method will
     * return a string similar to "60 s"
     *
     * @param minimumDigits an <code>int</code> containing the minimum number of
     * digits you want to appear after the decimal.
     * @param maximumDigits an <code>int</code> indicating the maximum number of
     * digits you want to appear after the decimal.
     */
    public String toShortString(int minimumDigits, int maximumDigits) {
        String toReturn;

        if (amount.doubleValue() == 1) {
            toReturn = NumberFormat.getInstance().formatNumber(amount.doubleValue(), minimumDigits, maximumDigits)
                + " " + unit.getAbbreviation();
        } else {
            toReturn = NumberFormat.getInstance().formatNumber(amount.doubleValue(), minimumDigits, maximumDigits)
                + " " + unit.getPluralAbbreviation();
        }

        return toReturn;
    }

    /**
     * Convert the current TimeQuantity into another smaller unit.
     * <p>
     * This should be used when the new unit represents "smaller" quantities
     * than this unit.  For example, converting 1 second to milliseconds.
     * It converts to a scale of zero with.
     * </p>
     * @param newUnit a <code>TimeQuantityUnit</code> variable indicating the new
     * unit that we would like returned.
     * @return a <code>TimeQuantity</code> which contains the proper amount for
     * the current time quantity translated into a different unit.
     */
    public TimeQuantity convertTo(TimeQuantityUnit newUnit) {
        return convertTo(newUnit, amount.scale(), BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Convert the current TimeQuantity into another unit.  This method would be
     * used to convert 60 seconds into one minute using:
     * <code>tq.convertTo(TimeQuantityUnit.MINUTE);</code>
     *
     * @param newUnit a <code>TimeQuantityUnit</code> variable indicating the new
     * unit that we would like returned.
     * @param scale the number of decimal positions that this conversion should
     * maintain.
     * @return a <code>TimeQuantity</code> which contains the proper amount for
     * the current time quantity translated into a different unit.
     */
    public TimeQuantity convertTo(TimeQuantityUnit newUnit, int scale) {
        return convertTo(newUnit, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Convert the current TimeQuantity into another unit specifying the scale
     * and rounding mode.
     *
     * @param newUnit a <code>TimeQuantityUnit</code> variable indicating the new
     * unit that we would like returned.
     * @param scale the number of decimal positions that this conversion should
     * maintain.
     * @param roundingMode the rounding mode to use to scale the quantity
     * @return a <code>TimeQuantity</code> which contains the proper amount for
     * the current time quantity translated into a different unit.
     */
    public TimeQuantity convertTo(TimeQuantityUnit newUnit, int scale, int roundingMode) {
        BigDecimal newAmount =  amount.multiply(unit.getBase());
        newAmount = newAmount.divide(newUnit.getBase(), scale, roundingMode);

        return new TimeQuantity(newAmount, newUnit);
    }

    /**
     * Return a new TimeQuantity whose scale is equal to the scale parameter.
     * Note that this method will not alter this TimeQuantity in any way.
     *
     * @param scale a <code>int</code> which represents the "scale" of the
     * amount.  For more information on scale, see {@link java.math.BigDecimal}.
     * @param roundingMode a <code>int</code> value which indicates how the
     * number should be rounded if the new scale doesn't have as much precision.
     * @return a <code>TimeQuantity</code> which is a clone of the current
     * TimeQuantity class, except that it has the specified scale.
     */
    public TimeQuantity convertToScale(int scale, int roundingMode) {
        BigDecimal newAmount = amount.setScale(scale, roundingMode);

        return new TimeQuantity(newAmount, unit);
    }

    /**
     * Add one time quantity to another to produce a sum.
     * <p>
     * The result has the same unit as this time quantity.
     * The amount of the result is rounded up to the tenth decimal place;
     * its scale is reduced be eliminating trailing zeros from the amount.
     * It works as you would expect a calculator to work.
     * <p>
     * For example, 1.001 seconds + 1.199 seconds = 2.2 seconds and NOT 2.200 seconds;
     * the amount will have a scale of 1.
     * </p>
     * @param timeQuantity a <code>TimeQuantity</code> value that will be added
     * to this object to produce a TimeQuantity sum of the two.
     * @return a sum of this time quantity and the <code>timeQuantity</code>
     * parameter; the unit will be the same as this unit
     */
    public TimeQuantity add(TimeQuantity timeQuantity) {
        TimeQuantity tq = add(timeQuantity, java.lang.Math.max(timeQuantity.amount.scale(), java.lang.Math.max(ARBITRARY_PRECISION, amount.scale())), BigDecimal.ROUND_HALF_UP);
        return dynamicScale(tq);
    }

    /**
     * Adds the specified time quantity to this one.
     * <p>
     * The result has the same unit as this time quantity.
     * The result is set to the specified scale using the specified rounding mode, if necessary.
     * </p>
     * @param timeQuantity the time quantity to add to this one
     * @param scale the scale of the result
     * @param roundingMode the rounding mode
     * @return the sum of this and the specified time quantity with this unit
     */
    TimeQuantity add(TimeQuantity timeQuantity, int scale, int roundingMode) {
        TimeQuantity quantitySameUnit;

        if (this.getUnits().equals(timeQuantity.getUnits())) {
            // Same Unit; no need to convert
            quantitySameUnit = timeQuantity;
        } else {
            // Operand has different units; convert to this
            quantitySameUnit = timeQuantity.convertTo(getUnits(), scale, roundingMode);
        }

        // Now add the amounts
        return new TimeQuantity(getAmount().add(quantitySameUnit.getAmount()), getUnits());
    }

    /**
     * Subtract a time quantity from the current time quantity in order to
     * create a new time quantity.  This method will not alter the original time
     * quantity.
     * <p>
     * The result has the same unit as this time quantity.
     * The amount of the result is rounded up to the tenth decimal place;
     * its scale is reduced be eliminating trailing zeros from the amount.
     * It works as you would expect a calculator to work.
     * </p>
     * @param timeQuantity a {@link net.project.util.TimeQuantity} that we
     * wish to subtract from the value that this object currently holds.
     * @return a new {@link net.project.util.TimeQuantity} object which contains
     * the amount of time of this object minus the amount of time supplied by the
     * parameter.
     */
    public TimeQuantity subtract(TimeQuantity timeQuantity) {
        TimeQuantity newTQ = subtract(timeQuantity, java.lang.Math.max(timeQuantity.amount.scale(), java.lang.Math.max(ARBITRARY_PRECISION, amount.scale())), BigDecimal.ROUND_HALF_UP);
        return dynamicScale(newTQ);
    }

    /**
     * Subtract a time quantity from the current time quantity in order to
     * create a new time quantity.  This method will not alter the original time
     * quantity.
     * <p>
     * The result has the same unit as this time quantity.
     * The result is set to the specified scale using the specified rounding mode, if necessary.
     * </p>
     * @param timeQuantity a {@link net.project.util.TimeQuantity} that we
     * wish to subtract from the value that this object currently holds.
     * @return a new {@link net.project.util.TimeQuantity} object which contains
     * the amount of time of this object minus the amount of time supplied by the
     * parameter.
     */
    TimeQuantity subtract(TimeQuantity timeQuantity, int scale, int roundingMode) {
        TimeQuantity quantitySameUnit;

        if (this.getUnits().equals(timeQuantity.getUnits())) {
            // Same Unit; no need to convert
            quantitySameUnit = timeQuantity;
        } else {
            // Operand has different units; convert to this
            quantitySameUnit = timeQuantity.convertTo(getUnits(), scale, roundingMode);
        }
        // Now subtract the amounts
        return new TimeQuantity(getAmount().subtract(quantitySameUnit.getAmount()), getUnits());
    }

    /**
     * Multiply this object (which will be the multiplier) by the
     * <code>multiplicand</code> parameter to produce a product.
     *
     * @param multiplicand a number that we are going to multiply with this
     * object to produce a new TimeQuantity.
     * @return a <code>TimeQuantity</code> which is the product of the
     * multiplication of this objects amount with the multiplicand.
     */
    public TimeQuantity multiply(BigDecimal multiplicand) {
        BigDecimal productScalar = getAmount().multiply(multiplicand);
        return new TimeQuantity(productScalar, getUnits());
    }

    /**
     * Divides this time quantity amount by the specified value to
     * produce a time quantity with the same unit.
     * @param divisor the amount to divide by
     * @param scale the number of zeros after the decimal to scale to
     * @param roundingRule these are constants defined in the
     * {@link java.math.BigDecimal} which determine how to round the result.
     * Some examples are {@link java.math.BigDecimal#ROUND_HALF_UP} and
     * {@link java.math.BigDecimal#ROUND_UNNECESSARY}.  Please note that if you
     * pass the <code>ROUND_UNNECESSARY</code> constant and you have not allowed
     * a sufficient amount of scale to hold the resulting division that a
     * runtime {@link java.lang.ArithmeticException} will be thrown.
     * @return a time quantity with this time quantity's unit who's amount is
     * the result of the division
     */
    public TimeQuantity divide(BigDecimal divisor, int scale, int roundingRule) {
        return new TimeQuantity(getAmount().divide(divisor, scale, roundingRule), getUnits());
    }

    /**
     * Divides this time quantity by the specified time quantity to
     * produce a ratio.
     * <p>
     * TimeQuantity units are normalized before computing the result.
     * </p>
     * @param divisor the time quantity divide by
     * @param scale the number of zeros after the decimal to scale to
     * @param roundingRule these are constants defined in the
     * {@link java.math.BigDecimal} which determine how to round the result.
     * Some examples are {@link java.math.BigDecimal#ROUND_HALF_UP} and
     * {@link java.math.BigDecimal#ROUND_UNNECESSARY}.  Please note that if you
     * pass the <code>ROUND_UNNECESSARY</code> constant and you have not allowed
     * a sufficient amount of scale to hold the resulting division that a
     * runtime {@link java.lang.ArithmeticException} will be thrown.
     * @return a ratio of this time quantity to the specified time quantity
     */
    public BigDecimal divide(TimeQuantity divisor, int scale, int roundingRule) {
        TimeQuantity numerator;
        TimeQuantity denominator;
        if (this.getUnits().compareTo(divisor.getUnits()) > 0) {
            // This has larger units
            // Downconvert this (this scale will be sufficient to avoid rounding)
            numerator = this.convertTo(divisor.getUnits(), this.getAmount().scale(), BigDecimal.ROUND_UNNECESSARY);
            denominator = divisor;

        } else if (this.getUnits().compareTo(divisor.getUnits()) < 0) {
            // This has smaller units
            // Downconvert divisor (divisor scale will be sufficient to avoid rounding)
            numerator = this;
            denominator = divisor.convertTo(this.getUnits(), divisor.getAmount().scale(), BigDecimal.ROUND_UNNECESSARY);
        } else {
            // Same units
            numerator = this;
            denominator = divisor;
        }

        // Assert we have the same units
        assert (numerator.getUnits().equals(denominator.getUnits()));

        // Now divide the amounts
        return numerator.getAmount().divide(denominator.getAmount(), scale, roundingRule);
    }

    /**
     * Return a new TimeQuantity whose amount has the same scale as is suggested
     * by the parameter to this method.
     *
     * @param scale a scale that the returned time quantity will have.
     * @return a <code>TimeQuantity</code> which has an amount set at the specified
     * scale.
     */
    public TimeQuantity setScale(int scale) {
        return new TimeQuantity(amount.setScale(scale, BigDecimal.ROUND_HALF_UP), unit);
    }

    /**
     * Return a new TimeQuantity whose amount has the same scale as is suggested
     * by the parameter to this method.
     *
     * @param scale a scale that the returned time quantity will have.
     * @return a <code>TimeQuantity</code> which has an amount set at the specified
     * scale.
     */
    public TimeQuantity setScale(int scale, int roundingRule) {
        return new TimeQuantity(amount.setScale(scale, roundingRule), unit);
    }

    /**
     * If this TimeQuantity contains a fractional part, this method will convert
     * the TimeQuantity into a collection of TimeQuantities that each have a
     * whole value.
     *
     * For example, if you had 4.5 hours, this method would return a list
     * containing two objects: 1 TimeQuantity of 4 hours and another with 30
     * minutes.
     *
     * @return a <code>List</code> of <code>TimeQuantity</code> units.
     */
    public List stratify() {
        List strata = new ArrayList();

        TimeQuantity currentStrata = this;
        do {
            // The candidate uses the longValue of the amount which discards
            // any fractional portion
            // If the whole part is non-zero then we add it
            TimeQuantity candidateToAdd =
                new TimeQuantity(currentStrata.getAmount().longValue(), currentStrata.getUnits());

            if (candidateToAdd.getAmount().signum() != 0) {
                strata.add(candidateToAdd);
            }

            // Subtract the amount that you already added to the strata.
            // Note: We don't subtract the TimeQuantity objects since that
            // would require us to make a decision about the scale of the result
            // We want to preserve the scale
            // By subtracting the amounts, the scale will be the greater scale
            // of the current strata and the added amount (which is always a scale of zero)
            currentStrata = new TimeQuantity(currentStrata.getAmount().subtract(candidateToAdd.getAmount()), currentStrata.getUnits());

            // Convert to the next smallest unit
            // We maintain the current strata's scale (which is greater than necessary to maintain
            // the current strat's value in a smaller unit)
            currentStrata = currentStrata.convertTo(currentStrata.getUnits().getSmallerUnit(), currentStrata.getAmount().scale());

        } while (currentStrata.getAmount().signum() != 0);

        return strata;
    }

    /**
     * Return a TimeQuantity with the same magnitude and units as the current
     * TimeQuantity, but on the opposite side of zero.  For example, 2 days will
     * return -2 days.
     *
     * @return a <code>TimeQuantity</code> containing the opposite amount and
     * same units as the current Time Quantity.
     */
    public TimeQuantity negate() {
        return new TimeQuantity(getAmount().negate(), getUnits());
    }

    /**
     * Indicates whether this time quantity's represents a zero amount.
     * @return true if this time quantity's amount is zero; false otherwise
     */
    public boolean isZero() {
        return (getAmount().signum() == 0);
    }

    /**
     * Computes the absolute value of the TimeQuantity.
     *
     * @return a <code>TimeQuantity</code> which has the same units and amount,
     * except that the amount must be positive.
     */
    public TimeQuantity abs() {
        return new TimeQuantity(getAmount().abs(), getUnits());
    }

    /**
     * Finds the greatest of two time quantities.
     */
    public static TimeQuantity max(TimeQuantity first, TimeQuantity second) {
        if (first.compareTo(second) >= 0) {
            return first;
        } else {
            return second;
        }
    }

    /**
     * Indicates whether the specified object is a TimeQuantity with the
     * same amount and units as this.
     * @param o the TimeQuantity to compare with
     * @return true if the specified TimeQuantity's amount is the same (ignoring scale)
     * and if the unit is the same; false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeQuantity)) return false;

        final TimeQuantity timeQuantity = (TimeQuantity)o;

        if (amount.compareTo(timeQuantity.amount) != 0) return false;
        if (!unit.equals(timeQuantity.unit)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = unit.hashCode();
        result = 29 * result + amount.hashCode();
        return result;
    }

    /**
     * Compares this TimeQuantity with the specific object.
     * Two compared TimeQuantities are equal if, after converting to a common unit,
     * their amounts are equal (ignoring scale).
     * @param o the TimeQuantity to compare
     * @return a negative integer, zero, or a positive integer as this TimeQuantity
     * is less than, equal to, or greater than the specified TimeQuantity.
     */
    public int compareTo(Object o) {
        TimeQuantity otherTQ = (TimeQuantity)o;

        if (unit.equals(otherTQ.unit)) {
            return amount.compareTo(otherTQ.amount);
        } else {
            TimeQuantity thisTQ = this.convertTo(TimeQuantityUnit.MILLISECOND, BigDecimal.ROUND_UNNECESSARY);
            otherTQ = otherTQ.convertTo(TimeQuantityUnit.MILLISECOND, BigDecimal.ROUND_UNNECESSARY);

            return thisTQ.amount.compareTo(otherTQ.amount);
        }
    }

    /**
     * Reduces the scale of the specified time quantity such the resulting
     * time quantity has the smallest possible scale while still maintaining
     * equality.
     * <p>
     * More clearly, removes trailing zeros from the part to the right of
     * the decimal point.
     * However, if the specified time quantity has no trailing zeros or
     * then the resulting time quantity's scale is unaffected.
     * @param tqToScale a <code>TimeQuantity</code> whose amount might have a
     * lot of zeros at the end.
     * @return a <code>TimeQuantity</code> whose amount has the correct scale
     * for the numbers in it.
     */
    static TimeQuantity dynamicScale(TimeQuantity tqToScale) {
        TimeQuantity toReturn = tqToScale;

        if (tqToScale.amount.scale() > 0) {
            BigDecimal newAmount = NumberUtils.dynamicScale(tqToScale.amount);
            toReturn = new TimeQuantity(newAmount, tqToScale.unit);
        }

        return toReturn;
    }

    /**
     * Formats the amount of this time quantity for the current user's locale.
     * <p>
     * This does not include the units.
     * </p>
     * @return the formatted amount
     */
    public String formatAmount() {
        return NumberFormat.getInstance().formatNumber(getAmount().doubleValue(), 0, 2);
    }
    
    /**
     * @param value
     * @return
     */
    public BigDecimal converToHours(){
    	if (unit.getUniqueID() == TimeQuantityUnit.DAY.getUniqueID()) {
    		return getAmount().multiply(BigDecimal.valueOf(8));
		} else if (unit.getUniqueID() == TimeQuantityUnit.WEEK.getUniqueID()) {//if workunit is week
			return getAmount().multiply(BigDecimal.valueOf(40));
		} else{
			return getAmount();
		}
    }
}
