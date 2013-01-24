create or replace view pnet.pn_user_view as
select
   u.user_id,
    p.display_name,
    u.username,
    p.user_status,
    u.domain_id,
    u.last_login,
    u.is_login,
    u.last_brand_id,
    u.user_session_key,
    u.last_login_time,
    case when phl.person_id is null then 0 else 1 end as has_license
from
    pn_user u, pn_person_view p,
    pn_person_has_license phl
where
    u.user_id = p.person_id
and phl.person_id(+) = u.user_id;

