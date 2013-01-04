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
create or replace view pn_envelope_history_view as
select
    hist.history_id,
    hist.envelope_id,
    env.envelope_name,
    env.envelope_description,
    hist.action_by_id,
    paction.display_name as "ACTION_BY_FULL_NAME",
    hist.history_action_id,
    act.action_name,
    act.action_description,
    hist.action_datetime,
    hist.history_message_id,
    hist.crc,
    hist.record_status
from
    pn_envelope_history hist,
    pn_workflow_envelope env,
    pn_envelope_history_action act,
    pn_person paction
where
    env.envelope_id = hist.envelope_id and
    act.history_action_id = hist.history_action_id and
    paction.person_id = hist.action_by_id
/

