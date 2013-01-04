create or replace view pnet.pn_methodology_view as
select
    m.methodology_id,
    s.parent_space_id,
    s.child_space_id,
    m.based_on_space_id,
    m.methodology_name,
    m.methodology_desc,
    m.use_scenario_clob,
    m.status_id,
    m.created_by_id,
    profile.get_display_name(m.created_by_id) as created_by,
    m.created_date,
    m.modified_by_id,
    m.modified_date,
    profile.get_display_name(m.modified_by_id) as modified_by,
    m.record_status,
    m.is_global,
    m.crc
from
    pn_methodology_space m, pn_space_has_space s
where
    s.child_space_id = m.methodology_id;

