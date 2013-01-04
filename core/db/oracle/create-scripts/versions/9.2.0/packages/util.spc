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
CREATE OR REPLACE PACKAGE util IS

-- Purpose: Provides utilities helpful in PL/SQL
--
-- MODIFICATION HISTORY
-- ---------   ------        ------------------------------------------
-- Matt        040204        Created
-- Carlos      09-Aug-2007   Added the add_working_days function
--

FUNCTION compare_dates (
  date1 IN DATE,
  date2 IN DATE
)
RETURN BOOLEAN;

FUNCTION compare_strings (
  string1 IN VARCHAR,
  string2 IN VARCHAR
)
RETURN BOOLEAN;

FUNCTION compare_numbers (
  number1 IN NUMBER,
  number2 IN NUMBER
)
RETURN BOOLEAN;

FUNCTION add_working_days (
  start_date IN DATE,
  days IN NUMBER
)
RETURN DATE;

FUNCTION get_name (
  pnet_object_id IN NUMBER
)
RETURN VARCHAR2;

TYPE cursor_type is REF CURSOR;

END; -- Package spec
/

