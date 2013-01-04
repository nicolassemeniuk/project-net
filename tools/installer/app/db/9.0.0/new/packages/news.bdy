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
CREATE OR REPLACE PACKAGE BODY news IS
--==================================================================
-- Purpose: News procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- TimM        27-Jan-01  Created it.
-- Deepak      10-Oct-01  Modified it to log events
--==================================================================

/*--------------------------------------------------------------------
  PROCEDURE PROTOTYPES
  Required for private procedures so that they can be used
  lexically earlier than their definition.
--------------------------------------------------------------------*/

    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean;

    function getSpaceID(i_news_id in number)
        return number;
/*--------------------------------------------------------------------
  END OF PROCEDURE PROTOTYPES
--------------------------------------------------------------------*/


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
        o_news_id        OUT varchar2)
    IS
        v_space_id      pn_space_has_news.space_id%type := to_number(i_space_id);
        v_priority_id   pn_news.priority_id%type := to_number(i_priority_id);
        v_notification_id   pn_news.notification_id%type := to_number(i_notification_id);
        v_posted_by_id  pn_news.posted_by_id%type := to_number(i_posted_by_id);
        v_created_by_id pn_news.created_by_id%type := to_number(i_created_by_id);

        v_datetime      date;
        v_news_id       pn_news.news_id%type;

        stored_proc_name VARCHAR2(100):= 'NEWS.CREATE_NEWS';

    BEGIN

        -- Create new entry in pn_object
        v_news_id := base.create_object(
            G_news_object_type,
            v_created_by_id,
            base.active_record_status);

        SECURITY.CREATE_SECURITY_PERMISSIONS(v_news_id, G_news_object_type, v_space_id, v_created_by_id);

        -- Get current datetime
        SELECT SYSDATE INTO v_datetime FROM dual;

        if (i_is_message_null = 1) then
            -- Create the news record with a null CLOB
            INSERT INTO pn_news (news_id, topic, message_clob, priority_id, notification_id,
                posted_by_id, posted_datetime, created_by_id, created_datetime,
                crc, record_status)
            VALUES (v_news_id, i_topic, null, v_priority_id, v_notification_id,
                v_posted_by_id, i_posted_datetime, v_created_by_id, v_datetime,
                sysdate, base.active_record_status)
            RETURNING message_clob into o_message_clob;
        else
            -- Create the news record with an empty CLOB
            INSERT INTO pn_news (news_id, topic, message_clob, priority_id, notification_id,
                posted_by_id, posted_datetime, created_by_id, created_datetime,
                crc, record_status)
            VALUES (v_news_id, i_topic, empty_clob, v_priority_id, v_notification_id,
                v_posted_by_id, i_posted_datetime, v_created_by_id, v_datetime,
                sysdate, base.active_record_status)
            RETURNING message_clob into o_message_clob;
        end if;

        -- Associate news with space
        INSERT INTO pn_space_has_news (
            space_id,
            news_id)
        VALUES (
            v_space_id,
            v_news_id);

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */

        -- Set output parameters
        o_news_id := to_char(v_news_id);

      EXCEPTION
      WHEN OTHERS THEN
        BEGIN
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
            raise;
        END;

      END;
----------------------------------------------------------------------

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
        o_message_clob   OUT clob)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_news_id        pn_news.news_id%type := to_number(i_news_id);
        v_priority_id    pn_news.priority_id%type := to_number(i_priority_id);
        v_notification_id   pn_news.notification_id%type := to_number(i_notification_id);
        v_posted_by_id   pn_news.posted_by_id%type := to_number(i_posted_by_id);
        v_modified_by_id pn_news.created_by_id%type := to_number(i_modified_by_id);

        v_datetime       date;
        v_current_crc    pn_news.crc%type;

        stored_proc_name varchar2(100):= 'NEWS.MODIFY_NEWS';
    begin
        -- Check news has not changed
        select crc into v_current_crc
            from pn_news
            where news_id = v_news_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            if (i_is_message_null = 1) then
                -- Set the message clob to null
                update
                    pn_news n
                set
                    n.topic = i_topic,
                    n.message_clob = null,
                    n.priority_id = v_priority_id,
                    n.notification_id = v_notification_id,
                    n.posted_by_id = v_posted_by_id,
                    n.posted_datetime = i_posted_datetime,
                    n.modified_by_id = v_modified_by_id,
                    n.modified_datetime = v_datetime,
                    n.crc = sysdate,
                    n.record_status = base.active_record_status
                where
                    n.news_id = v_news_id
                returning
                    n.message_clob into o_message_clob;
            else
                -- Set the message clob to empty_clob() so that it can
                -- be replaced with new data
                update
                    pn_news n
                set
                    n.topic = i_topic,
                    n.message_clob = empty_clob,
                    n.priority_id = v_priority_id,
                    n.notification_id = v_notification_id,
                    n.posted_by_id = v_posted_by_id,
                    n.posted_datetime = i_posted_datetime,
                    n.modified_by_id = v_modified_by_id,
                    n.modified_datetime = v_datetime,
                    n.crc = sysdate,
                    n.record_status = base.active_record_status
                where
                    n.news_id = v_news_id
                returning
                    n.message_clob into o_message_clob;
            end if;

        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */

    exception
        when others then
        begin
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
            raise;
        end;
    end modify_news;

----------------------------------------------------------------------


----------------------------------------------------------------------
-- REMOVE_NEWS
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_news (
        i_news_id        in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_news_id        pn_news.news_id%type := to_number(i_news_id);
        v_modified_by_id pn_news.created_by_id%type := to_number(i_modified_by_id);

        v_datetime       date;
        v_current_crc    pn_news.crc%type;

        stored_proc_name varchar2(100):= 'NEWS.REMOVE_NEWS';
    begin
        -- Check news has not changed, may raise record_locked exception
        select crc into v_current_crc
            from pn_news
            where news_id = v_news_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then
            select sysdate into v_datetime from dual;
            update
                pn_news n
            set
                n.modified_by_id = v_modified_by_id,
                n.modified_datetime = v_datetime,
                n.crc = sysdate,
                n.record_status = base.deleted_record_status
            where
                n.news_id = v_news_id;

            o_return_value := BASE.OPERATION_SUCCESSFUL;
        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */

    exception
        when record_locked then
        begin
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;
    end remove_news;
----------------------------------------------------------------------


----------------------------------------------------------------------
-- PRIVATE crc_matches
-- Compare two crc values
-- i_original_crc - first crc value
-- i_new_crc - second cec value
-- returns true if both values are identical, false otherwise
----------------------------------------------------------------------
    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean
    is
        v_crc_match     boolean := false;

    begin
        if (i_original_crc = i_new_crc) then
            v_crc_match := true;
        end if;
        return v_crc_match;
    end crc_matches;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE getSpaceID
-- get the space id of a news
-- i_news_id - the news id to get space id from
-- returns space_id
-- throws exception if there is a problem
----------------------------------------------------------------------
    function getSpaceID(i_news_id in number)
        return number
    is
        v_space_id  pn_space_has_news.space_id%type := null;

    begin
        select
            sp.space_id into v_space_id
        from
            pn_space_has_news sp
        where
            sp.news_id = i_news_id;

        return v_space_id;

    exception
        when NO_DATA_FOUND then
        begin
            raise space_not_found;
        end;
    end getSpaceID;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- LOGS_EVENT
----------------------------------------------------------------------
 PROCEDURE log_event
    (
        news_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    )

IS
    v_news_id     pn_news.news_id%type := TO_NUMBER(news_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_news_history.news_history_id%type;
    v_action          pn_news_history.action%type := action;
    v_action_name     pn_news_history.action_name%type := action_name;
    v_action_comment  pn_news_history.action_comment%type := notes;

BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- insert a history record for this document

    insert into pn_news_history (
        news_id,
        news_history_id,
        action,
        action_name,
        action_by_id,
        action_date,
        action_comment)
    values (
        v_news_id,
        v_history_id,
        v_action,
        v_action_name,
        v_whoami,
        SYSDATE,
        v_action_comment
    );

COMMIT;
EXCEPTION
    WHEN OTHERS THEN
    BEGIN
         base.log_error('NEWS.LOG_EVENT', sqlcode, sqlerrm);
        raise;
    END;
END log_event;

-- Procedure LOG_EVENT
----------------------------------------------------------------------


END; -- Package Body NEWS
/

