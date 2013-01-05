/* Copyright 2000-2009 Project.net Inc.
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

-- Update table data

UPDATE PN_OBJECT_TYPE SET IS_SECURABLE = 0 WHERE OBJECT_TYPE = 'configuration'
/

UPDATE PN_OBJECT_TYPE SET IS_SECURABLE = 0 WHERE OBJECT_TYPE = 'enterprise'
/

-- Update wiki object type

UPDATE PN_OBJECT_TYPE SET IS_SECURABLE = 0 WHERE OBJECT_TYPE = 'wiki'
/

-- Update is securable as 2 for business and > 0 for project space
UPDATE PN_OBJECT_TYPE SET IS_SECURABLE = 2 WHERE IS_SECURABLE = 1;
/

-- Update is securable as 1 for objects required for project space and not for business space
UPDATE PN_OBJECT_TYPE SET IS_SECURABLE = 1 WHERE OBJECT_TYPE IN ('deliverable', 'process', 'phase', 'gate', 'task');
/