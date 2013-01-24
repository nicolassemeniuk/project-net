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
CREATE OR REPLACE PACKAGE news IS
--==================================================================
-- Purpose: PNET News procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- TimM        27-Jan-01  Created it.
-- Deepak      10-Oct-01  Modified it to log events
--==================================================================

   -- Package constants
   G_news_object_type           constant pn_object.object_type%type := 'news';

   -- Raise-able errors
   unspecified_error  exception;
   pragma exception_init(unspecified_error, -20000);
   space_not_found    exception;

----------------------------------------------------------------------
-- CREATE_NEWS
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    PROCEDURE create_news
      ( i_space_id       IN varchar2,
        i_topic          IN varchar2,
        i_priority_id    in varchar2,
        i_notification_id    in varchar2,
        i_posted_by_id   in varchar2,
        i_posted_datetime in date,
        i_created_by_id  IN varchar2,
        i_is_message_null in number,
        o_message_clob   OUT clob,
        o_news_id        OUT varchar2);

----------------------------------------------------------------------
-- MODIFY_NEWS
-- Update an existing news
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure modify_news (
        i_news_id        in varchar2,
        i_topic          IN varchar2,
        i_priority_id    in varchar2,
        i_notification_id    in varchar2,
        i_posted_by_id   in varchar2,
        i_posted_datetime in date,
        i_modified_by_id in varchar2,
        i_crc            in date,
        i_is_message_null in number,
        o_message_clob   OUT clob);

----------------------------------------------------------------------
-- REMOVE_NEWS
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_news (
        i_news_id        in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number);
----------------------------------------------------------------------



----------------------------------------------------------------------
-- LOG_EVENT
-- Logs the event that happens to the NEWS item
----------------------------------------------------------------------
   PROCEDURE log_event
    (
        news_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    );
----------------------------------------------------------------------


END; -- Package Specification NEWS
/

