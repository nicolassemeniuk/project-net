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
CREATE OR REPLACE PACKAGE BODY process IS

---------------------------------------------------------------------
-- STORE_PROCESS
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation from Brian's procs.
-- Robin       11-May-00   Added error handling.

Procedure STORE_PROCESS
(
    p_person_id IN NUMBER,
    p_space_id IN NUMBER,
    p_process_id IN NUMBER,
    p_process_name IN VARCHAR2,
    p_process_desc IN VARCHAR2,
    p_current_phase_id IN NUMBER,
    p_last_gate_passed_id IN NUMBER,
    p_record_status IN VARCHAR2,
    o_process_id OUT NUMBER,
    o_status OUT NUMBER
)
IS
    v_process_id NUMBER(20);
    stored_proc_name VARCHAR2(100) := 'STORE_PROCESS';

BEGIN
    -- NEW PROCESS, INSERT
    IF ((p_process_id IS NULL) OR (p_process_id = ''))
    THEN

        -- Create the object
        v_process_id := BASE.CREATE_OBJECT('process', p_person_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_process_id, 'process', p_space_id, p_person_id);

        INSERT INTO
            pn_process
        (
            process_id,
            process_name,
            process_desc,
            current_phase_id,
            last_gate_passed_id,
            record_status
        )
        VALUES
        (
            v_process_id,
            p_process_name,
            p_process_desc,
            p_current_phase_id,
            p_last_gate_passed_id,
            p_record_status
        );

        -- Create new pn_project_has_process record

        INSERT INTO
            pn_space_has_process
        (
            space_id,
            process_id
        )
        VALUES
        (
            p_space_id,
            v_process_id
        );

        o_process_id := v_process_id;

    -- EXISTING PROCESS, UPDATE
    ELSE

        UPDATE
                pn_process
        SET
                process_id = p_process_id,
                process_name = p_process_name,
                process_desc = p_process_desc,
                current_phase_id = p_current_phase_id,
                last_gate_passed_id = p_last_gate_passed_id,
                record_status = p_record_status
        WHERE
                process_id = p_process_id;

        o_process_id := p_process_id;

        IF SQL%NOTFOUND THEN
            o_status := no_data;
            ROLLBACK;
            RETURN;
        END IF;

    END IF;

    o_status := success;
    COMMIT;

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


END;


---------------------------------------------------------------------
-- COPY_PROCESS
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Phil        18-Sep-00   Created

Procedure COPY_PROCESS
(
    i_from_space_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_return_value OUT NUMBER
)
IS
    -- variable declaration

    v_old_process_id            pn_process.process_id%type;
    v_new_process_id            pn_process.process_id%type;
    v_phase_id                  pn_phase.phase_id%type;
    v_process_rec               pn_process%rowtype;
    stored_proc_name VARCHAR2(100) := 'STORE_PROCESS';

    v_cmd_status                NUMBER := 0;

    -- cursor definition

    CURSOR c_processes (i_space_id pn_object.object_id%type) IS
        select process_id from pn_space_has_process
        where space_id = i_space_id;


    CURSOR c_phases (i_process_id pn_process.process_id%type) IS
        select phase_id from pn_phase where process_id = i_process_id
        and record_status = 'A';

BEGIN


        /* -------------------------------  COPY processes  ------------------------------- */

        -- FOR EACH OF THE PROCESSES IN THIS SPACE
        OPEN c_processes (i_from_space_id);
        <<process_loop>>
    	LOOP

    		FETCH c_processes INTO v_old_process_id;
    		EXIT WHEN ((c_processes%NOTFOUND));

            -- Create the object
            v_new_process_id := BASE.CREATE_OBJECT('process', i_actor_id, 'A');
            -- Apply default security permissions
            SECURITY.CREATE_SECURITY_PERMISSIONS(v_new_process_id, 'process', i_to_space_id, i_actor_id);

            -- get the data from the existing process
            select * into v_process_rec from pn_process where process_id = v_old_process_id;

            INSERT INTO pn_process
            (
                process_id,
                process_name,
                process_desc,
                current_phase_id,
                last_gate_passed_id,
                record_status
            )
            VALUES
            (
                v_new_process_id,
                v_process_rec.process_name,
                v_process_rec.process_desc,
                v_process_rec.current_phase_id,
                v_process_rec.last_gate_passed_id,
                'A'
            );

            -- Create new pn_project_has_process record

            INSERT INTO
                pn_space_has_process
            (
                space_id,
                process_id
            )
            VALUES
            (
                i_to_space_id,
                v_new_process_id
            );


            /* -------------------------------  COPY phases  ------------------------------- */

            -- FOR EACH OF THE PHASES IN THIS PROCESS
            OPEN c_phases (v_old_process_id);
            <<phase_loop>>
        	LOOP

        		FETCH c_phases INTO v_phase_id;
        		EXIT WHEN ((c_phases%NOTFOUND) OR (v_cmd_status <> 0));

                    copy_phase (v_phase_id, v_new_process_id, i_to_space_id, i_actor_id, v_cmd_status);

            END LOOP phase_loop;
    	    CLOSE c_phases;


    -- NOW END PROCESS LOOP
    END LOOP process_loop;
	CLOSE c_processes;

    o_return_value := v_cmd_status;
    COMMIT;



EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        o_return_value := no_data;

    WHEN e_null_constraint THEN
        ROLLBACK;
        o_return_value := null_field;

    WHEN e_check_constraint THEN
        ROLLBACK;
        o_return_value := check_violated;

    WHEN e_unique_constraint THEN
        ROLLBACK;
        o_return_value := dupe_key;

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_return_value := no_parent_key;

    WHEN OTHERS THEN
        o_return_value := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;


END;


---------------------------------------------------------------------
-- REMOVE_PROCESS
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Brian       30-Apr-00   Created for Process Module
-- Robin       11-May-00   Added error handling.

Procedure REMOVE_PROCESS
(
    p_process_id IN NUMBER,
    o_status OUT NUMBER
)

IS

    stored_proc_name VARCHAR2(100) := 'REMOVE_PROCESS';

BEGIN

    -- lock tables temporarily
    LOCK TABLE pn_phase IN SHARE MODE;
    LOCK TABLE pn_gate IN SHARE MODE;
    LOCK TABLE pn_phase_has_deliverable IN SHARE MODE;

    UPDATE pn_process SET record_status = 'D'
        WHERE process_id = p_process_id;

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id = p_process_id;

    UPDATE pn_phase SET record_status = 'D'
        WHERE process_id = p_process_id;

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id =
            ( SELECT phase_id
                FROM pn_phase
                WHERE process_id = p_process_id );

    UPDATE pn_gate SET record_status = 'D'
        WHERE phase_id =
            ( SELECT phase_id
                FROM pn_phase
                WHERE process_id = p_process_id );

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id =
            ( SELECT gate_id
                FROM pn_gate
                WHERE phase_id =
                    ( SELECT phase_id
                        FROM pn_phase
                        WHERE process_id = p_process_id ));

    UPDATE pn_deliverable
        SET record_status = 'D'
        WHERE deliverable_id =
            ( SELECT deliverable_id
                FROM pn_phase_has_deliverable
                WHERE phase_id =
                    ( SELECT phase_id
                        FROM pn_phase
                        WHERE process_id = p_process_id ));

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id =
            ( SELECT deliverable_id
                FROM pn_phase_has_deliverable
                WHERE phase_id =
                    ( SELECT phase_id
                        FROM pn_phase
                        WHERE process_id = p_process_id ));



    o_status := success;
    COMMIT;

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
END;

---------------------------------------------------------------------
-- STORE_PHASE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation from Brian's procs.
-- Robin       11-May-00   Added error handling.

Procedure STORE_PHASE
(
    p_person_id IN NUMBER,
    p_phase_id  IN NUMBER,
    p_process_id IN NUMBER,
    p_space_id IN NUMBER,
    p_phase_name IN VARCHAR2,
    p_phase_desc IN VARCHAR2,
    p_sequence IN NUMBER,
    p_status_id IN NUMBER,
    p_record_status IN VARCHAR2,
    p_progress_reporting_method IN VARCHAR2,
    p_start_date IN DATE,
    p_end_date IN DATE,
    p_entered_percent_complete IN NUMBER,
    o_phase_id OUT NUMBER,
    o_status OUT NUMBER
)

IS
    v_phase_id NUMBER(20);
    stored_proc_name VARCHAR2(100) := 'STORE_PHASE';

BEGIN
    -- NEW PHASE, INSERT
    IF ((p_phase_id IS NULL) OR (p_phase_id = ''))
    THEN

        -- Create the object
        v_phase_id := BASE.CREATE_OBJECT('phase', p_person_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_phase_id, 'phase', p_space_id, p_person_id);

        INSERT INTO
            pn_phase
        (
            phase_id,
            process_id,
            phase_name,
            phase_desc,
            start_date,
            end_date,
            sequence,
            status_id,
            entered_percent_complete,
            progress_reporting_method,
            record_status
        )
        VALUES
        (

            v_phase_id,
            p_process_id,
            p_phase_name,
            p_phase_desc,
            p_start_date,
            p_end_date,
            p_sequence,
            p_status_id,
            p_entered_percent_complete,
            p_progress_reporting_method,
            p_record_status
        );

        o_phase_id := v_phase_id;

    -- EXISTING PHASE, UPDATE
    ELSE

        UPDATE
                pn_phase
        SET
                phase_id = p_phase_id,
                process_id = p_process_id,
                phase_name = p_phase_name,
                phase_desc = p_phase_desc,
                start_date = p_start_date,
                end_date = p_end_date,
                sequence = p_sequence,
                status_id = p_status_id,
                entered_percent_complete = p_entered_percent_complete,
                progress_reporting_method = p_progress_reporting_method,
                record_status = p_record_status
        WHERE
                phase_id = p_phase_id;

        o_phase_id := p_phase_id;


        IF SQL%NOTFOUND THEN
            o_status := no_data;
            ROLLBACK;
            RETURN;
        END IF;

    END IF;

    o_status := success;
    COMMIT;

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
END;


---------------------------------------------------------------------
-- COPY_PHASE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- PHIL        18-Sep-00   Created

Procedure COPY_PHASE
(
    i_phase_id  IN NUMBER,
    i_to_process_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_return_value OUT NUMBER
 )

IS

    -- variable declaration
    v_new_phase_id NUMBER(20);
    v_phase_rec     pn_phase%rowtype;
    v_gate_id       pn_gate.gate_id%type;
    v_deliverable_id pn_deliverable.deliverable_id%type;

    stored_proc_name VARCHAR2(100) := 'COPY_PHASE';
    v_cmd_status        NUMBER := 0;

    -- cursor definition

    CURSOR c_gates (i_phase_id pn_phase.phase_id%type) IS
        select gate_id from pn_gate
        where phase_id = i_phase_id and record_status = 'A';

    CURSOR c_deliverables (i_phase_id pn_phase.phase_id%type) IS
        select
          pd.deliverable_id
        from
          pn_phase_has_deliverable phd,
          pn_deliverable pd
        where
          pd.deliverable_id = phd.deliverable_id
          and phase_id = i_phase_id
          and pd.record_status = 'A';


BEGIN

        /* -------------------------------  COPY phase  ------------------------------- */


        -- Create the object
        v_new_phase_id := BASE.CREATE_OBJECT('phase', i_actor_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_new_phase_id, 'phase', i_to_space_id, i_actor_id);

        select * into v_phase_rec from pn_phase where phase_id = i_phase_id;

        INSERT INTO
            pn_phase
        (
            phase_id,
            process_id,
            phase_name,
            phase_desc,
            start_date,
            end_date,
            sequence,
            status_id,
            entered_percent_complete,
            record_status
        )
        VALUES
        (

            v_new_phase_id,
            i_to_process_id,
            v_phase_rec.phase_name,
            v_phase_rec.phase_desc,
            NULL,  -- no start and end dates
            NULL,
            v_phase_rec.sequence,
            10, -- NOT STARTED
            0, -- nothing done, new project
            'A'
        );


        /* -------------------------------  COPY gates  ------------------------------- */

        -- FOR EACH OF THE PHASES IN THIS PROCESS
        OPEN c_gates (i_phase_id);
        <<gate_loop>>
    	LOOP

    		FETCH c_gates INTO v_gate_id;
    		EXIT WHEN ((c_gates%NOTFOUND) OR (v_cmd_status <> 0));

                copy_gate (v_gate_id, v_new_phase_id, i_to_space_id, i_actor_id, v_cmd_status);

        END LOOP gate_loop;
	    CLOSE c_gates;


        /* -------------------------------  COPY deliverables  ------------------------------- */

        -- FOR EACH OF THE PHASES IN THIS PROCESS
        OPEN c_deliverables (i_phase_id);
        <<deliverable_loop>>
    	LOOP

    		FETCH c_deliverables INTO v_deliverable_id;
    		EXIT WHEN ((c_deliverables%NOTFOUND) OR (v_cmd_status <> 0));

                copy_deliverable (v_deliverable_id, v_new_phase_id, i_to_space_id, i_actor_id, v_cmd_status);

        END LOOP deliverable_loop;
	    CLOSE c_deliverables;


    o_return_value := success;

 EXCEPTION
    WHEN NO_DATA_FOUND THEN
        o_return_value := no_data;

    WHEN e_null_constraint THEN
        o_return_value := null_field;

    WHEN e_check_constraint THEN
        o_return_value := check_violated;

    WHEN e_unique_constraint THEN
        o_return_value := dupe_key;

    WHEN e_no_parent_key THEN
        o_return_value := no_parent_key;

    WHEN OTHERS THEN
        o_return_value := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
END;








---------------------------------------------------------------------
-- REMOVE_PHASE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Brian       30-Apr-00   Created for Process Module
-- Robin       11-May-00   Added error handling.
-- Matt        28-Feb-05   Removed transaction handling and error handling
--                         from procedure -- we always do this in Java code now

Procedure REMOVE_PHASE
(
    p_phase_id IN NUMBER
)
IS
BEGIN
    UPDATE pn_phase SET record_status = 'D'
        WHERE phase_id = p_phase_id;

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id = p_phase_id;

    UPDATE pn_gate SET record_status = 'D'
        WHERE phase_id = p_phase_id;

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id in
            ( SELECT gate_id
                FROM pn_gate
                WHERE phase_id = p_phase_id );

    UPDATE pn_deliverable SET record_status = 'D'
        WHERE deliverable_id in
            ( SELECT deliverable_id
                FROM pn_phase_has_deliverable
                WHERE phase_id = p_phase_id );

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id in
            ( SELECT deliverable_id
                FROM pn_phase_has_deliverable
                WHERE phase_id = p_phase_id );
END;

---------------------------------------------------------------------
-- STORE_GATE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation from Brian's procs.
-- Robin       11-May-00   Added error handling.


Procedure STORE_GATE
(
    p_person_id IN NUMBER,
    p_gate_id IN NUMBER,
    p_phase_id  IN NUMBER,
    p_space_id IN NUMBER,
    p_gate_name IN VARCHAR2,
    p_gate_desc IN VARCHAR2,
    p_gate_date IN DATE,
    p_status_id IN NUMBER,
    p_record_status IN VARCHAR2,
    o_gate_id OUT NUMBER,
    o_status OUT NUMBER
)

IS
    v_gate_id NUMBER(20);
    stored_proc_name VARCHAR2(60):='STORE_GATE';

BEGIN
    -- NEW GATE, INSERT
    IF ((p_gate_id IS NULL) OR (p_gate_id = '')) THEN

        -- Create the object
        v_gate_id := BASE.CREATE_OBJECT('gate', p_person_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_gate_id, 'gate', p_space_id, p_person_id);

        INSERT INTO
            pn_gate
        (
            gate_id,
            phase_id,
            gate_name,
            gate_desc,
            gate_date,
            status_id,
            record_status
        )
        VALUES
        (
            v_gate_id,
            p_phase_id,
            p_gate_name,
            p_gate_desc,
            p_gate_date,
            p_status_id,
            p_record_status
        );

        o_gate_id := v_gate_id;


        -- EXISTING GATE, UPDATE
    ELSE


        UPDATE
            pn_gate
        SET
            gate_id = p_gate_id,
            phase_id = p_phase_id,
            gate_name = p_gate_name,
            gate_desc = p_gate_desc,
            gate_date = p_gate_date,
            status_id = p_status_id,
            record_status = p_record_status
        WHERE
            gate_id = p_gate_id;

        IF SQL%NOTFOUND THEN
            o_status := no_data;
            ROLLBACK;
            RETURN;
        END IF;

    END IF;

    o_status := success;
    COMMIT;

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
END;


---------------------------------------------------------------------
-- COPY_GATE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Phil        18-Sep-00   Created

Procedure COPY_GATE
(
    i_gate_id IN NUMBER,
    i_to_phase_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_status OUT NUMBER
)

IS
    -- variable declaration
    v_new_gate_id NUMBER(20);
    v_gate_rec      pn_gate%rowtype;

    stored_proc_name VARCHAR2(60):='STORE_GATE';

BEGIN
        -- Create the object
        v_new_gate_id := BASE.CREATE_OBJECT('gate', i_actor_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_new_gate_id, 'gate', i_to_space_id, i_actor_id);

        -- get old gate information
        select * into v_gate_rec from pn_gate where gate_id = i_gate_id;

        INSERT INTO pn_gate
        (
            gate_id,
            phase_id,
            gate_name,
            gate_desc,
            gate_date,
            status_id,
            record_status
        )
        VALUES
        (
            v_new_gate_id,
            i_to_phase_id,
            v_gate_rec.gate_name,
            v_gate_rec.gate_desc,
            NULL, -- no gate date
            10, -- NOT SCHEDULED
            'A'
        );


    o_status := success;

 EXCEPTION
    WHEN NO_DATA_FOUND THEN
        o_status := no_data;

    WHEN e_null_constraint THEN
        o_status := null_field;

    WHEN e_check_constraint THEN
        o_status := check_violated;

    WHEN e_unique_constraint THEN
        o_status := dupe_key;

    WHEN e_no_parent_key THEN
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
END;



---------------------------------------------------------------------
-- REMOVE_GATE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Brian       30-Apr-00   Created for Process Module
-- Robin       11-May-00   Added error handling.

Procedure REMOVE_GATE
(
    p_gate_id IN NUMBER,
    o_status OUT NUMBER
)

IS
    stored_proc_name VARCHAR2(100) := 'REMOVE_GATE';

BEGIN
    UPDATE pn_gate SET record_status = 'D'
        WHERE gate_id = p_gate_id;

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id = p_gate_id;


    o_status := success;
    COMMIT;

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

END;


---------------------------------------------------------------------
-- STORE_DELIVERABLE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation from Brian's procs.
-- Robin       11-May-00   Added error handling.

Procedure STORE_DELIVERABLE
(
    p_person_id IN NUMBER,
    p_phase_id IN NUMBER,
    p_deliverable_id IN NUMBER,
    p_space_id IN NUMBER,
    p_deliverable_name IN VARCHAR2,
    p_deliverable_desc IN VARCHAR2,
    p_status_id IN NUMBER,
    p_methodology_deliverable_id IN NUMBER,
    p_is_optional IN NUMBER,
    p_record_status IN VARCHAR2,
    i_is_deliverable_comments_null in number,
    o_deliverable_comments_clob out clob,
    o_deliverable_id OUT NUMBER
)
IS
    v_deliverable_id NUMBER(20);
    stored_proc_name VARCHAR2(100) := 'STORE_DELIVERABLE';

BEGIN
    -- NEW deliverable, INSERT
    IF ((p_deliverable_id IS NULL) OR (p_deliverable_id = ''))
    THEN
        -- Create the object
        v_deliverable_id := BASE.CREATE_OBJECT('deliverable', p_person_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_deliverable_id, 'deliverable', p_space_id, p_person_id);

        if (i_is_deliverable_comments_null > 0) then
            -- Insert with null comments
            INSERT INTO pn_deliverable
                (deliverable_id, deliverable_name, deliverable_desc, deliverable_comments_clob,
                 status_id, methodology_deliverable_id, is_optional, record_status)
            VALUES
                (v_deliverable_id, p_deliverable_name, p_deliverable_desc, null,
                 p_status_id, p_methodology_deliverable_id, p_is_optional, p_record_status)
            returning
                deliverable_comments_clob into o_deliverable_comments_clob;
        else
            -- Insert with empty clob
            INSERT INTO pn_deliverable
                (deliverable_id, deliverable_name, deliverable_desc, deliverable_comments_clob,
                 status_id, methodology_deliverable_id, is_optional, record_status)
            VALUES
                (v_deliverable_id, p_deliverable_name, p_deliverable_desc, empty_clob(),
                 p_status_id, p_methodology_deliverable_id, p_is_optional, p_record_status)
            returning
                deliverable_comments_clob into o_deliverable_comments_clob;
        end if;

        -- Create new pn_phase_has_deliverable record

        INSERT INTO
            pn_phase_has_deliverable
        (
            phase_id,
            deliverable_id
        )
        VALUES
        (
            p_phase_id,
            v_deliverable_id
        );

        o_deliverable_id := v_deliverable_id;

    -- EXISTING DELIVERABLE, UPDATE
    ELSE

        if (i_is_deliverable_comments_null > 0) then
            -- Insert null into clob
            UPDATE
                pn_deliverable
            SET
                deliverable_id = p_deliverable_id,
                deliverable_name = p_deliverable_name,
                deliverable_desc = p_deliverable_desc,
                deliverable_comments_clob = null,
                status_id = p_status_id,
                methodology_deliverable_id = p_methodology_deliverable_id,
                is_optional = p_is_optional,
                record_status = p_record_status
            WHERE
                deliverable_id = p_deliverable_id
            returning
                deliverable_comments_clob into o_deliverable_comments_clob;
        else
            -- clear out clob for subsequent update
            UPDATE
                pn_deliverable
            SET
                deliverable_id = p_deliverable_id,
                deliverable_name = p_deliverable_name,
                deliverable_desc = p_deliverable_desc,
                deliverable_comments_clob = empty_clob(),
                status_id = p_status_id,
                methodology_deliverable_id = p_methodology_deliverable_id,
                is_optional = p_is_optional,
                record_status = p_record_status
            WHERE
                deliverable_id = p_deliverable_id
            returning
                deliverable_comments_clob into o_deliverable_comments_clob;
        end if;

        o_deliverable_id := p_deliverable_id;

    END IF;

 EXCEPTION
    WHEN OTHERS THEN
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
END;



---------------------------------------------------------------------
-- COPY_DELIVERABLE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Phil        18-Sep-00   Created

Procedure COPY_DELIVERABLE
(
    i_deliverable_id IN NUMBER,
    i_to_phase_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_status OUT NUMBER
)
IS
    v_new_deliverable_id NUMBER(20);
    v_deliverable_rec pn_deliverable%rowtype;

    stored_proc_name VARCHAR2(100) := 'STORE_DELIVERABLE';

BEGIN
        -- Create the object
        v_new_deliverable_id := BASE.CREATE_OBJECT('deliverable', i_actor_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_new_deliverable_id, 'deliverable', i_to_space_id, i_actor_id);


        -- get the old deliverable info
        select * into v_deliverable_rec from pn_deliverable
        where deliverable_id = i_deliverable_id;

        INSERT INTO pn_deliverable
        (
            deliverable_id,
            deliverable_name,
            deliverable_desc,
            deliverable_comments_clob,
            status_id,
            methodology_deliverable_id,
            is_optional,
            record_status
        )
        VALUES
        (
            v_new_deliverable_id,
            v_deliverable_rec.deliverable_name,
            v_deliverable_rec.deliverable_desc,
            v_deliverable_rec.deliverable_comments_clob,
            10, -- NOT STARTED
            v_deliverable_rec.methodology_deliverable_id,
            v_deliverable_rec.is_optional,
            'A'
        );

        -- Create new pn_phase_has_deliverable record

        INSERT INTO
            pn_phase_has_deliverable
        (
            phase_id,
            deliverable_id
        )
        VALUES
        (
            i_to_phase_id,
            v_new_deliverable_id
        );

    o_status := success;

 EXCEPTION
    WHEN OTHERS THEN
        o_status := generic_error;
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
END;

---------------------------------------------------------------------
-- REMOVE_DELIVERABLE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Brian       30-Apr-00   Created for Process Module
-- Robin       11-May-00   Added error handling.

Procedure REMOVE_DELIVERABLE
(
    p_deliverable_id IN NUMBER,
    o_status OUT NUMBER
)
IS
    stored_proc_name VARCHAR2(100) := 'REMOVE_DELIVERABLE';

BEGIN

    UPDATE pn_deliverable SET record_status = 'D'
        WHERE deliverable_id = p_deliverable_id;

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id = p_deliverable_id;

    o_status := success;
    COMMIT;

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
END;

-- This function returns the percent complete (as a decimal)
-- calculated based on the sum of work_ms and work_complete_ms of each
-- schedule entry associated with the specified phase.
-- Note, summary tasks are (necessarily) not included in this calculation.
function GET_PHASE_SCHEDULE_COMPLETE (
    i_phase_id in NUMBER
)
    return NUMBER
is
    v_work_complete NUMBER;
    v_work NUMBER;
    o_percent_complete NUMBER;
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.GET_PHASE_ENTRIES_PERCENT_COMPLETE';
begin

    -- first get the work for all active (non-summary) tasks in the plan
    select sum(t.work_ms) into v_work
    from pn_task t, pn_phase_has_task pht
    where pht.phase_id = i_phase_id
    and t.task_id = pht.task_id
    and t.record_status = 'A'
    and t.task_type != 'summary';

    -- next get the work complete for all active (non-summary) tasks in the plan
    select sum(t.work_complete_ms) into v_work_complete
    from pn_task t, pn_phase_has_task pht
    where pht.phase_id = i_phase_id
    and t.task_id = pht.task_id
    and t.record_status = 'A'
    and t.task_type != 'summary';

    if (v_work = 0) then
        o_percent_complete := 0;
    else
        o_percent_complete := (v_work_complete / v_work) * 100;
    end if;

    return o_percent_complete;

end GET_PHASE_SCHEDULE_COMPLETE;

-- returns the earliest start date of the group of tasks
-- "owned" by this phase
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Carlos      07-Aug-06  Removed summary task restrictions in select
--                        in order to be able to retrieve the start date
--                        when a phase in linked to a summary task.
function GET_PHASE_WORKPLAN_START (
    i_phase_id in NUMBER
)
    return DATE
 is
    startDate DATE;
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.GET_PHASE_WORKPLAN_START';
begin

    select min(t.date_start) into startDate
    from pn_task t, pn_phase_has_task pht
    where pht.phase_id = i_phase_id
    and t.task_id = pht.task_id
    and t.record_status = 'A';

    return startDate;

end GET_PHASE_WORKPLAN_START;

-- returns the latest finish date of the group of tasks
-- "owned" by this phase
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Carlos      07-Aug-06  Removed summary task restrictions in select
--                        in order to be able to retrieve finish date
--                        when a phase in linked to a summary task.
function GET_PHASE_WORKPLAN_FINISH (
    i_phase_id in NUMBER
)
    return DATE
 is
    finishDate DATE;
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.GET_PHASE_WORKPLAN_FINISH';
begin

    select max(t.date_finish) into finishDate
    from pn_task t, pn_phase_has_task pht
    where pht.phase_id = i_phase_id
    and t.task_id = pht.task_id
    and t.record_status = 'A';

    return finishDate;

end GET_PHASE_WORKPLAN_FINISH;

END; -- Package Body PROCESS
/

