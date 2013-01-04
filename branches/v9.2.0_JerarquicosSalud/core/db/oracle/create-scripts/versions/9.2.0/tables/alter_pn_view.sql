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
-- Alter table PN_VIEW to add IS_VIEW_SHARED column

ALTER TABLE PN_VIEW
ADD (IS_VIEW_SHARED NUMBER(1,0) DEFAULT 0);

-- Alter table PN_VIEW to add IS_VISIBLE_TO_ALL column

ALTER TABLE PN_VIEW
ADD (IS_VISIBLE_TO_ALL NUMBER(1,0) DEFAULT 0);
