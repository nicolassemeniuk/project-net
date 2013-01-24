create or replace view pnet.pn_methodology_by_user_view as
select
    m.methodology_id,
    m.methodology_name,
    m.methodology_desc,
    m.is_global,
    m.parent_space_id,
    m.child_space_id,
    m.based_on_space_id,
    s.person_id,
    profile.get_display_name(s.person_id) as person ,m.record_status
from
    pn_methodology_view m, pn_space_has_person s
where
    s.space_id = m.child_space_id;

