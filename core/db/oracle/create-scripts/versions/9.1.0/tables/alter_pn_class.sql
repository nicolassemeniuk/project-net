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
-- Alter the pn_class table to add supports_external_access for allowing form external access

ALTER TABLE PNET.PN_CLASS
ADD
(SUPPORTS_EXTERNAL_ACCESS NUMBER(1,0) DEFAULT 0
)
/

ALTER TABLE PNET.PN_CLASS
ADD
(EXTERNAL_CLASS_ID NUMBER(20,0)
)
/


ALTER TABLE PNET.PN_CLASS
ADD
(HIDE_ASSIGNMENT_FIELDS_IN_EAF NUMBER(1,0) DEFAULT 0
)
/

ALTER TABLE PNET.PN_CLASS
ADD
(SHARED NUMBER(1,0) DEFAULT 0
)
/

ALTER TABLE PNET.PN_SPACE_HAS_CLASS
ADD
(VISIBLE NUMBER(1,0) DEFAULT 1
)
/

-- Alter the pn_class table to increase length of class_abbreviation column.
ALTER TABLE PNET.PN_CLASS 
MODIFY 
(CLASS_ABBREVIATION VARCHAR2(40 BYTE))
/

