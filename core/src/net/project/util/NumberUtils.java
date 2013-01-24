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
|   $Revision: 20665 $
|       $Date: 2010-04-06 09:49:31 -0300 (mar, 06 abr 2010) $
|     $Author: ritesh $
|
+-----------------------------------------------------------------------------*/
package net.project.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberUtils {
    /**
     * Reduces the scale of the specified BigDecimal such the resulting
     * BigDecimal has the smallest possible scale while still maintaining
     * equality.
     * <p>
     * More clearly, removes trailing zeros from the part to the right of
     * the decimal point.
     * However, if the specified BigDecimal has no trailing zeros or
     * then the resulting time quantity's scale is unaffected.
     * @param amount a <code>BigDecimal</code> whose amount might have a
     * lot of zeros at the end.
     * @return a <code>BigDecimal</code> whose scale has been adjusted such that
     * there are no trailing zeros in the decimal portion.
     */
    public static BigDecimal dynamicScale(BigDecimal amount) {
        BigDecimal toReturn = amount;

        if (amount.scale() > 0) {
            BigInteger unscaledBI = amount.unscaledValue();
            
            //sjmittal: why are we comparing with MAX_VALUE ? 
            //Do we deal with +ive amount only in this method !!!
            //Please do the proper comparision
            if (unscaledBI.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) < 0 &&
            		unscaledBI.compareTo(new BigInteger(String.valueOf(Long.MIN_VALUE))) > 0) {
                long unscaled = amount.unscaledValue().longValue();
                int newScale = amount.scale();

                while (newScale > 0) {
                    if (unscaled % 10 == 0) {
                        unscaled = unscaled / 10;
                        newScale--;
                    } else {
                        break;
                    }
                }

                toReturn = amount.setScale(newScale);
            } else {
                //We can't scale this using a long value because it is too large.
                //We have to use the BigInteger directly.  This is quite a bit
                //slower, but we have to do it to handle very large numbers --
                //otherwise we'll get an error when setting the scale because of
                //an overflow in the long unscaled value.
                int newScale = amount.scale();

                while (newScale > 0) {
                    if (unscaledBI.mod(new BigInteger("10")).signum() == 0) {
                        unscaledBI = unscaledBI.divide(new BigInteger("10"));
                        newScale--;
                    } else {
                        break;
                    }
                }

                toReturn = amount.setScale(newScale);
            }
        }

        return toReturn;
    }

    public static Comparable max(Comparable comp1, Comparable comp2) {
        int comparison = comp1.compareTo(comp2);
        if (comparison > 0 || comparison == 0) {
            return comp1;
        } else {
            return comp2;
        }
    }

    public static Comparable min(Comparable comp1, Comparable comp2) {
        int comparison = comp1.compareTo(comp2);
        if (comparison < 0 || comparison == 0) {
            return comp1;
        } else {
            return comp2;
        }
    }
    
	/**
	 * To get rounded value of the given number upto given decimal places. 
	 * 
	 * @param numberToBeRounded
	 * @param decimalPlace
	 * @return roundedValue
	 */
	public static int round(double numberToBeRounded, int decimalPlace){
	    BigDecimal roundedValue = new BigDecimal(Double.valueOf(numberToBeRounded));
	    roundedValue = roundedValue.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
	    return roundedValue.intValue();
	  }
}
