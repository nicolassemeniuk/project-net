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
CREATE OR REPLACE PACKAGE BODY calendar IS

---------------------------------------------------------------------
-- STORE_MEETING
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Robin       25-Apr-00  Created from Adam's procedures.

Procedure store_meeting
(
    i_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_calendar_id IN NUMBER,
    i_event_id IN NUMBER,
    i_meeting_id IN NUMBER,
    i_host_id IN NUMBER,
    i_event_name IN VARCHAR2,
    i_frequency_type_id IN NUMBER,
    i_facility_id IN NUMBER,
    i_start_date IN DATE,
    i_end_date IN DATE,
    o_event_desc_clob OUT CLOB,
    o_event_purpose_clob OUT CLOB,
    o_meeting_id OUT NUMBER,
    o_event_id OUT NUMBER
)
IS
BEGIN

    -- NEW MEETING, INSERT
    IF ((i_meeting_id IS NULL) OR (i_meeting_id = '')) THEN

        -- Create the object
        o_event_id := BASE.CREATE_OBJECT('event', i_person_id, 'A');
        o_meeting_id := BASE.CREATE_OBJECT('meeting', i_person_id, 'A');

        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(o_event_id, 'event', i_space_id, i_person_id);
        SECURITY.CREATE_SECURITY_PERMISSIONS(o_meeting_id, 'meeting', i_space_id, i_person_id);

        -- create the pn_calendar_event
        INSERT INTO pn_calendar_event
            (calendar_event_id, event_name, event_desc_clob, frequency_type_id,
             event_type_id, facility_id, event_purpose_clob, start_date, end_date, record_status)
        VALUES
            (o_event_id, i_event_name, empty_clob(), i_frequency_type_id,
             100, i_facility_id, empty_clob(), i_start_date, i_end_date, 'A')
        returning
                event_desc_clob, event_purpose_clob into o_event_desc_clob, o_event_purpose_clob;

       -- create the pn_meeting
        INSERT INTO pn_meeting
            (meeting_id, calendar_event_id, next_agenda_item_seq, host_id)
        VALUES
            (o_meeting_id, o_event_id, 1, i_host_id);

        -- link event to space's calendar
        INSERT INTO pn_calendar_has_event
            (calendar_id, calendar_event_id)
        VALUES
            (i_calendar_id, o_event_id);



     -- UPDATE EXISTING MEETING
     ELSE
        -- maintain current event and meeting id's
        o_event_id := i_event_id;
        o_meeting_id := i_meeting_id;

        -- update the event
        UPDATE pn_calendar_event
            SET
                facility_id = i_facility_id,
                event_name = i_event_name,
                event_purpose_clob = empty_clob(),
                event_desc_clob = empty_clob(),
                frequency_type_id = i_frequency_type_id,
                start_date = i_start_date,
                end_date = i_end_date
            WHERE
            	calendar_event_id = i_event_id
            returning
                event_desc_clob, event_purpose_clob into o_event_desc_clob, o_event_purpose_clob;

        IF SQL%NOTFOUND THEN
            raise e_no_data;
        END IF;

        -- update the meeting
        UPDATE pn_meeting
            SET host_id = i_host_id
            WHERE meeting_id = o_meeting_id;


        IF SQL%NOTFOUND THEN
            raise e_no_data;
        END IF;

    END IF;
END store_meeting;


---------------------------------------------------------------------
-- STORE_EVENT
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  -------------------------------------------
-- Robin       25-Apr-00  Created from Adam's procedures.

Procedure STORE_EVENT
(
    i_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_calendar_id IN NUMBER,
    i_event_id IN NUMBER,
    i_event_name IN VARCHAR2,
    i_frequency_type_id IN NUMBER,
    i_facility_id IN NUMBER,
    i_start_date IN DATE,
    i_end_date IN DATE,
    o_event_desc_clob OUT CLOB,
    o_event_purpose_clob OUT CLOB,
    o_event_id OUT NUMBER
)
IS
BEGIN

     -- NEW EVENT, INSERT
    IF ((i_event_id IS NULL) OR (i_event_id = '')) THEN

        -- Create the object
        o_event_id := BASE.CREATE_OBJECT('event', i_person_id, 'A');

         -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(o_event_id, 'event', i_space_id, i_person_id);

        -- create the pn_calendar_event
        INSERT INTO pn_calendar_event
            (calendar_event_id, event_name, event_desc_clob, frequency_type_id,
            event_type_id, facility_id, event_purpose_clob, start_date, end_date, record_status)
        VALUES
            (o_event_id, i_event_name, empty_clob(), i_frequency_type_id,
             200, i_facility_id, empty_clob(), i_start_date, i_end_date, 'A')
        returning
            event_desc_clob, event_purpose_clob into o_event_desc_clob, o_event_purpose_clob;

        -- link event to space's calendar
        INSERT INTO pn_calendar_has_event
            (calendar_id, calendar_event_id)
        VALUES
            (i_calendar_id, o_event_id);

    -- UPDATE EXISTING MEETING
    ELSE
        -- maintain current event and meeting id's
        o_event_id := i_event_id;

        -- update the event
        UPDATE pn_calendar_event
            SET
                facility_id = i_facility_id,
                event_name = i_event_name,
                event_purpose_clob = empty_clob(),
                event_desc_clob = empty_clob(),
                frequency_type_id = i_frequency_type_id,
                start_date = i_start_date,
                end_date = i_end_date
            WHERE
            	calendar_event_id = i_event_id
            returning
                event_desc_clob, event_purpose_clob into o_event_desc_clob, o_event_purpose_clob;

        IF SQL%NOTFOUND THEN
            raise e_no_data;
        END IF;

    END IF;
END store_event;


---------------------------------------------------------------------
-- STORE_AGENDA_ITEM
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  -------------------------------------------
-- Robin       25-Apr-00  Created from Adam's procedures.

Procedure STORE_AGENDA_ITEM
(
    i_person_id IN NUMBER,
    i_meeting_id IN NUMBER,
    i_agenda_item_id IN NUMBER,
    i_item_name IN VARCHAR2,
    i_item_desc IN VARCHAR2,
    i_time_alloted IN VARCHAR2,
    i_status_id IN NUMBER,
    i_owner_id IN NUMBER,
    i_item_sequence IN NUMBER,
    i_is_meeting_notes_null IN NUMBER,
    o_meeting_notes_clob OUT CLOB
)

IS
    v_agenda_item_id    pn_agenda_item.agenda_item_id%TYPE;
    v_old_seq           pn_agenda_item.item_sequence%TYPE;
    v_agenda_item_seq   pn_agenda_item.item_sequence%TYPE;

BEGIN

     -- NEW AGENDA ITEM, INSERT
    IF ((i_agenda_item_id IS NULL) OR (i_agenda_item_id = '')) THEN

        -- register new agenda_item object
        v_agenda_item_id := BASE.CREATE_OBJECT('agenda_item', i_person_id, 'A');

        -- increment the meeting's agenda seq number
        UPDATE pn_meeting
            SET next_agenda_item_seq = next_agenda_item_seq + 1
            WHERE meeting_id = i_meeting_id
            RETURNING  next_agenda_item_seq INTO v_agenda_item_seq;

        IF SQL%NOTFOUND THEN
            raise e_no_data;
        END IF;

        -- must increment the meeting's agenda seq number and figure out the next sequence number
        IF ((i_item_sequence IS NULL) OR (i_item_sequence = '')) THEN

            -- user didn't specify a insert position, add to end of list.
            v_agenda_item_seq := (v_agenda_item_seq - 1);

        ELSE

             v_agenda_item_seq := i_item_sequence;

        END IF;

        -- Renumber the agenda items below the one inserted if needed.
        UPDATE pn_agenda_item
           SET item_sequence = item_sequence + 1
           WHERE meeting_id = i_meeting_id
             AND item_sequence >= v_agenda_item_seq;

        if (i_is_meeting_notes_null > 0) then
            -- Create the agenda item with null meeting notes
            INSERT INTO pn_agenda_item
                (meeting_id, agenda_item_id, item_name, item_desc, time_alloted,
                 status_id, owner_id, item_sequence, meeting_notes_clob, record_status)
            VALUES
                (i_meeting_id, v_agenda_item_id, i_item_name, i_item_desc, i_time_alloted,
                 i_status_id, i_owner_id, v_agenda_item_seq, null, 'A')
            returning
                meeting_notes_clob into o_meeting_notes_clob;
        else
            -- Create the agenda item with empty meeting notes for subsequent
            -- streaming
            INSERT INTO pn_agenda_item
                (meeting_id, agenda_item_id, item_name, item_desc, time_alloted,
                 status_id, owner_id, item_sequence, meeting_notes_clob, record_status)
            VALUES
                (i_meeting_id, v_agenda_item_id, i_item_name, i_item_desc, i_time_alloted,
                 i_status_id, i_owner_id, v_agenda_item_seq, empty_clob(), 'A')
            returning
                meeting_notes_clob into o_meeting_notes_clob;
        end if;


    -- UDATE EXISTING ITEM
    ELSE

        -- Figure out the item's old sequence number
        SELECT item_sequence into v_old_seq
           FROM pn_agenda_item
           WHERE meeting_id = i_meeting_id
             AND agenda_item_id = i_agenda_item_id;

        -- Renumber the other agenda items if this item's seq number changed.
        IF (i_item_sequence <> v_old_seq) THEN
            IF (i_item_sequence < v_old_seq) THEN
                UPDATE pn_agenda_item
                  SET item_sequence = item_sequence + 1
                  WHERE meeting_id = i_meeting_id
                    AND ((item_sequence >= i_item_sequence) AND (item_sequence < v_old_seq));
            ELSE
                UPDATE pn_agenda_item
                  SET item_sequence = item_sequence - 1
                  WHERE meeting_id = i_meeting_id
                    AND ((item_sequence > v_old_seq) AND (item_sequence <= i_item_sequence));

            END IF;
            IF SQL%NOTFOUND THEN
                raise e_no_data;
            END IF;
        END IF;

        if (i_is_meeting_notes_null > 0) then
            -- update the event with null meeting notes
            UPDATE pn_agenda_item
               SET item_name = i_item_name,
                   item_desc = i_item_desc,
                   time_alloted = i_time_alloted,
                   status_id = i_status_id,
                   owner_id = i_owner_id,
                   item_sequence = i_item_sequence,
                   meeting_notes_clob = null
               WHERE meeting_id = i_meeting_id
                 AND agenda_item_id = i_agenda_item_id
               returning
                   meeting_notes_clob into o_meeting_notes_clob;
         else
            -- update the event with empty meeting notes for subsequent streaming
            UPDATE pn_agenda_item
               SET item_name = i_item_name,
                   item_desc = i_item_desc,
                   time_alloted = i_time_alloted,
                   status_id = i_status_id,
                   owner_id = i_owner_id,
                   item_sequence = i_item_sequence,
                   meeting_notes_clob = empty_clob()
               WHERE meeting_id = i_meeting_id
                 AND agenda_item_id = i_agenda_item_id
               returning
                   meeting_notes_clob into o_meeting_notes_clob;

         end if;

         IF SQL%NOTFOUND THEN
             raise e_no_data;
         END IF;

    END IF;
END store_agenda_item;


---------------------------------------------------------------------
-- REMOVE_AGENDA_ITEM
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  -------------------------------------------
-- Robin       25-Apr-00  Created from Adam's procedures.

Procedure REMOVE_AGENDA_ITEM
(
    i_agenda_item_id IN NUMBER,
    i_meeting_id IN NUMBER,
    o_status OUT NUMBER
)

IS

    stored_proc_name VARCHAR2(100):= 'CALENDAR.REMOVE_AGENDA_MEETING';
    v_old_seq pn_agenda_item.item_sequence%TYPE;

BEGIN
    -- Figure out the item's current sequence number
    SELECT item_sequence into v_old_seq
        FROM pn_agenda_item
        WHERE meeting_id = i_meeting_id
          AND agenda_item_id = i_agenda_item_id;

    -- Renumber the other agenda items
    UPDATE pn_agenda_item
      SET item_sequence = item_sequence - 1
      WHERE meeting_id = i_meeting_id
        AND item_sequence > v_old_seq;

    -- set the agenda items status as deleted
    UPDATE pn_agenda_item
      SET record_status = 'D'
      WHERE meeting_id = i_meeting_id
        AND agenda_item_id = i_agenda_item_id;

    -- adjust the next agenda item sequence
    UPDATE pn_meeting
      SET next_agenda_item_seq = next_agenda_item_seq - 1
      WHERE meeting_id = i_meeting_id
        AND next_agenda_item_seq > 1;

    COMMIT;
    o_status := success;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        o_status := no_data;

    WHEN e_null_constraint THEN
        ROLLBACK;
        o_status := null_field;

    WHEN e_check_constraint THEN
        ROLLBACK;
        o_status := check_violated;

    WHEN e_unique_constraint THEN
        ROLLBACK;
        o_status := dupe_key;

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;


END remove_agenda_item;

END; -- Package Body CALENDAR
/

