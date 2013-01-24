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
CREATE OR REPLACE PACKAGE BODY user_domain IS

  FUNCTION getCountUsersForDomain
  ( i_domain_id IN number)
  RETURN  number

  IS

    v_count number := -1;

  BEGIN

    select count(u.user_id) into v_count from pn_user u, pn_person p
    where u.domain_id = i_domain_id
    and u.user_id = p.person_id
    and p.user_status = 'Active';

    return v_count;

  End;


PROCEDURE CREATE_MIGRATION_RECORD
(
        i_domain_migration_id in number,
        i_source_domain_id in number ,
        i_migration_status_id in number
) is

    -- general variables

     v_sysdate DATE := SYSDATE;
     v_cnt number (7) := 0;
     v_tot number (7) := 0;


    stored_proc_name VARCHAR2(100):= 'USER_DOMAIN.CREATE_MIGRATION_RECORD';

      CURSOR C_USERS IS SELECT user_id FROM pn_user_view
            WHERE domain_id = i_source_domain_id and user_status = 'Active' and user_id <> 1;


BEGIN

    FOR REC IN C_USERS LOOP

        v_tot := v_tot + 1;
        v_cnt := v_cnt + 1;

        if (v_cnt >= 50) then
            commit;
            v_cnt := 0;
         end if;

            INSERT INTO pn_user_domain_migration ( user_id , domain_migration_id , migration_status_id , 	activity_date , is_current )
                    VALUES ( REC.user_id , i_domain_migration_id ,i_migration_status_id , v_sysdate , '1');

            UPDATE pn_user_domain_migration SET is_current = '0' WHERE domain_migration_id <> 		i_domain_migration_id and user_id = REC.user_id;


    END LOOP;

EXCEPTION

    WHEN OTHERS THEN

        BASE.LOG_ERROR (stored_proc_name, SQLCODE, SQLERRM);
        raise;

END;


PROCEDURE removeUser
(
        i_domain_id in number,
        i_user_id in varchar2
)   IS

    -- general variables

    v_sysdate DATE := SYSDATE;

    -- debugging / error logging variables

    stored_proc_name VARCHAR2(100):= 'USER_DOMAIN.removeUser';

BEGIN

    delete from pn_user where domain_id = i_domain_id and user_id = i_user_id;

EXCEPTION

    WHEN OTHERS THEN

        BASE.LOG_ERROR (stored_proc_name, SQLCODE, SQLERRM);
        raise;

END;

   -- Enter further code below as specified in the Package spec.
END;
/

