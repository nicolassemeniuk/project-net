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
CREATE OR REPLACE PACKAGE task IS

-- Purpose: Repository for TASK Related procedures
--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
   -- Deepak   11-Oct-01 Added Log event procedure , more later

    -- Package constants
   G_task_object_type           constant pn_object.object_type%type := 'task';

   -- Raise-able errors
   unspecified_error  exception;
   pragma exception_init(unspecified_error, -20000);
   space_not_found    exception;

----------------------------------------------------------------------
-- LOG_EVENT
-- Logs the event that happens to the NEWS item
----------------------------------------------------------------------
   PROCEDURE log_event
    (
        task_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    );
----------------------------------------------------------------------

END; -- Package spec
/

