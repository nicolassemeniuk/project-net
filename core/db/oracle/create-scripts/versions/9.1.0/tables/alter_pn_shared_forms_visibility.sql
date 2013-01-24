alter table
pn_shared_forms_visiblity
add (child_id clob);

update
pn_shared_forms_visiblity
set
child_id=child_ids;

alter table
pn_shared_forms_visiblity
drop column child_ids;

alter table
pn_shared_forms_visiblity
rename column
child_id to child_ids;
