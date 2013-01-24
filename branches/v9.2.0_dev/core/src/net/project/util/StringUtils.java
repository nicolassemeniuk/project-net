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
|   $Revision: 18499 $
|       $Date: 2008-12-06 05:23:25 -0200 (sáb, 06 dic 2008) $
|     $Author: ritesh $
|
+-----------------------------------------------------------------------------*/
package net.project.util;

import java.util.Arrays;
import java.util.Calendar;
 

public class StringUtils extends org.apache.commons.lang.StringUtils {
    public static String rpad(String stringToPad, int totalNumberOfCharacters) {
        String toReturn = stringToPad;

        if (stringToPad.length() < totalNumberOfCharacters) {
            toReturn = stringToPad + stringOfChar(totalNumberOfCharacters - stringToPad.length(), ' ');
        }

        return toReturn;
    }

    public static String stringOfChar(int numberOfCharacters, char charToFill) {
        if (numberOfCharacters > 0) {
            char[] chars = new char[numberOfCharacters];
            Arrays.fill(chars, charToFill);

            return new String(chars);
        } else {
            return "";
        }
    }
    
    /**
     * Generates months name json string as per user locale.
     * @return String (JsonMonthsString)
     */
    public static String getJsonMonthsString(){
        String userLocaleMonthListString = "";
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        userLocaleMonthListString = "[";
        for(int index = 0; index < 12 ; index++){
            if(index > 0){
                userLocaleMonthListString += ",";
            }
            userLocaleMonthListString += "'" + DateFormat.getInstance().formatDate(cal.getTime(), "MMM") + "'";
            cal.add(Calendar.MONTH, 1);
        }
        userLocaleMonthListString += "]";
        return userLocaleMonthListString;
    }
    
    /** 
     * This mehtod use to convert Comma Separated Number String to <b>Integer Array</b>.
     * 
     * @param CSNString(Comma Separated Number String)
     * @return <b>Integer</b> integerArray.
     */
    public static Integer[] getIntegerArrayOfCSNString(String CSNString) {
        Integer[] integerArray = null;
        if (isNotEmpty(CSNString)) {
            String stringArray[] = CSNString.split(",");
            if (stringArray.length > 0) {
                integerArray = new Integer[stringArray.length];
                int index = 0;
                for (String stringNumber : stringArray) {
                    if (isNotEmpty(stringNumber)) {
                        integerArray[index++] = Integer.parseInt(stringNumber);
                    }
                }
            }
        }
        return integerArray;
    }
}
