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
/**
  $RCSfile$

  Script to delete all outstanding notifications.
  
  Use this script to clean out all notifications if errors are occurring in
  the queue, or if the notification scheduler is to be restarted after an
  extended period of inactivity. Schedule Subscriptions are NOT affected.
  
  This script performs staged commmits.  No commit is necessary after running.
  
*/
set serveroutput on
declare
   -- Select all notifications not in queue
   cursor select_cur is
      select n.notification_id, n.notification_clob_id
        from pn_notification n;

   v_commit_count    number := 200;
   v_success_count   number := 0;
   v_count           number := 0;
begin
   for select_rec in select_cur loop
      delete from pn_notification_log nl
            where nl.notification_id = select_rec.notification_id;

      delete from pn_notification_queue nq
            where nq.notification_id = select_rec.notification_id;

      delete from pn_notification n
            where n.notification_id = select_rec.notification_id;

      delete from pn_notification_clob nc
            where nc.object_id = select_rec.notification_clob_id;

      v_count := v_count + 1;

      if (v_count = v_commit_count) then
         commit;
         v_success_count := v_success_count + v_count;
         v_count := 0;
      end if;
   end loop;

   commit;
   v_success_count := v_success_count + v_count;
   dbms_output.put_line (
      'Success.  Committed '|| v_success_count || ' updates.'
   );
exception
   when others then
      begin
         rollback;
         dbms_output.put_line (
            'Error updating after committing '|| v_success_count || ' updates.'
         );
         raise;
      end;
end;
/
exit;
