create or replace view pnet.pn_wf_step_has_group_view as
select sg.step_id, sg.workflow_id, sg.group_id, g.group_name, g.group_desc,
       g.is_principal, sg.is_participant, sg.created_by_id,
       pcreated.display_name as "CREATED_BY_FULL_NAME", sg.created_datetime,
       sg.modified_by_id, pmodified.display_name as "MODIFIED_BY_FULL_NAME",
       sg.modified_datetime, sg.crc, sg.record_status
  from pn_workflow_step_has_group sg, pn_person pcreated, pn_person pmodified,
       (select g.group_id,
               decode (
                   g.is_principal,
                   1, g.principal_owner_display_name,
                   g.group_name
               ) as group_name,
               g.group_desc, g.is_principal
          from pn_group_view g where g.record_status = 'A' ) g
 where pcreated.person_id(+) = sg.created_by_id
   and pmodified.person_id(+) = sg.modified_by_id
   and g.group_id = sg.group_id;

