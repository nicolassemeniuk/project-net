alter table pn_module add (column_order number(2));

update pn_module set column_order=1 where module_id=150;
update pn_module set column_order=2 where module_id=380;
update pn_module set column_order=3 where module_id=340;
update pn_module set column_order=4 where module_id=140;
update pn_module set column_order=5 where module_id=10;
update pn_module set column_order=6 where module_id=20;
update pn_module set column_order=7 where module_id=30;
update pn_module set column_order=8 where module_id=40;
update pn_module set column_order=9 where module_id=70;
update pn_module set column_order=10 where module_id=60;
update pn_module set column_order=11 where module_id=200;
update pn_module set column_order=1 where module_id=170;