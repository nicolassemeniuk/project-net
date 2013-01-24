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
CREATE OR REPLACE PACKAGE BODY util
IS

-- Purpose: Provides utility classes to use inside of PL/SQL code.
--
-- MODIFICATION HISTORY
-- Person      Date          Comments
-- ---------   ------        ------------------------------------------
-- Matt        040204        Created
-- Carlos      09-Aug-2007   Added the add_working_days function

  FUNCTION compare_dates (date1 IN DATE, date2 IN DATE)
    RETURN BOOLEAN
  IS
    v_comparison   BOOLEAN;
  BEGIN
    if (date1 IS NULL AND date2 IS NULL)
    then
      v_comparison := true;
    elsif (   (date1 IS NULL AND date2 IS NOT NULL)
           OR (date1 IS NOT NULL and date2 IS NULL)
          )
    then
      v_comparison := false;
    elsif (date1 <> date2)
    then
      v_comparison := false;
    else
      v_comparison := true;
    end if;

    return v_comparison;
  END compare_dates;

  FUNCTION compare_strings (string1 IN VARCHAR, string2 IN VARCHAR)
    RETURN BOOLEAN
  IS
    v_comparison   BOOLEAN;
  BEGIN
    if (string1 IS NULL and string2 IS NULL)
    then
      v_comparison := true;
    elsif (   (string1 IS NULL AND string2 IS NOT NULL)
           OR (string1 IS NOT NULL and string2 IS NULL)
          )
    then
      v_comparison := false;
    elsif (string1 <> string2)
    then
      v_comparison := false;
    else
      v_comparison := true;
    end if;

    return v_comparison;
  END compare_strings;

  FUNCTION compare_numbers (number1 IN NUMBER, number2 IN NUMBER)
    RETURN BOOLEAN
  IS
    v_comparison   BOOLEAN;
  BEGIN
    if (number1 IS NULL and number2 IS NULL)
    then
      v_comparison := true;
    elsif (   (number1 IS NULL AND number2 IS NOT NULL)
           OR (number1 IS NOT NULL and number2 IS NULL)
          )
    then
      v_comparison := false;
    elsif (number1 <> number2)
    then
      v_comparison := false;
    else
      v_comparison := true;
    end if;

    return v_comparison;
  END compare_numbers;

-------------------------------------------------------------------------------
-- ADD_WORKING_DAYS
-------------------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date          Comments
-- ---------   ------        --------------------------------------------------
-- Carlos      09-Aug-2007   Created

-- PURPOSE:
-- This function takes the start_date value and adds the number of days in the
-- days variable, skipping Saturdays and Sundays.

-- NOTE:
-- This function does not skip holidays as Oracle has no way of recognizing
-- which days are holidays. However, you could try populating a holiday table
-- and then query this table to determine additional days to skip.
-------------------------------------------------------------------------------
  FUNCTION add_working_days
     (start_date IN DATE, days IN NUMBER)
     RETURN DATE
  IS
     v_counter NUMBER;
     v_new_date DATE;
     v_day_number NUMBER;

     -- Error Logging
     stored_proc_name VARCHAR2(100):= 'UTIL.ADD_WORKING_DAYS';
  BEGIN

     /* This routine will add a specified number of days (ie: days) to a date (ie: start_date). */
     /* It will skip all weekend days - Saturdays and Sundays */
     v_counter := 1;
     v_new_date := start_date;

     /* Loop to determine how many days to add */
     WHILE v_counter <= days
     LOOP
        /* Add a day */
        v_new_date := v_new_date + 1;
        v_day_number := to_char(v_new_date, 'd');

        /* Increment counter if day falls between Monday to Friday (1 = Sunday)*/
        IF v_day_number >= 2 AND v_day_number <= 6 THEN
           v_counter := v_counter + 1;
        END if;

     END LOOP;

  RETURN v_new_date;

  -- Log exceptions
  EXCEPTION
  WHEN OTHERS THEN
    BEGIN
      BASE.LOG_ERROR(stored_proc_name, SQLCODE, SQLERRM);
      RAISE;
    END;

  END add_working_days;

END; -- Package
/

