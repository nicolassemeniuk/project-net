
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

/* Creating pn_shared_forms_visiblity */

CREATE TABLE PN_SHARED_FORMS_VISIBLITY
( SPACE_ID NUMBER(20,0),
  CLASS_ID NUMBER(20,0),
  CHILD_IDS VARCHAR2(1000),
  CONSTRAINT PN_SHARED_FORMS_CLASS_FK
  FOREIGN KEY (
    CLASS_ID
  )
  REFERENCES PNET.PN_CLASS (CLASS_ID
  ) ENABLE VALIDATE,
  CONSTRAINT PN_SHARED_FORMS_VISIBLITY_PK1
  PRIMARY KEY (SPACE_ID, CLASS_ID)
) 
/

