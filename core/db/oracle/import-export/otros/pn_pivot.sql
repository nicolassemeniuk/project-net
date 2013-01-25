-- truncate table "PNET"."PN_PIVOT" drop storage

-- Llenado de la tabla PN_PIVOT
prompt Loading PN_PIVOT...
INSERT INTO "PNET"."PN_PIVOT"
SELECT hunthous.x+tenthous.x+thous.x+huns.x+tens.x+ones.x from
(select 0 x from dual
  union select 1 from dual
  union select 2 from dual
  union select 3 from dual
  union select 4 from dual
  union select 5 from dual
  union select 6 from dual
  union select 7 from dual
  union select 8 from dual
  union select 9 from dual
) ones,
(select 0 x from dual
  union select 10 from dual
  union select 20 from dual
  union select 30 from dual
  union select 40 from dual
  union select 50 from dual
  union select 60 from dual
  union select 70 from dual
  union select 80 from dual
  union select 90 from dual
) tens,
(select 0 x from dual
  union select 100 from dual
  union select 200 from dual
  union select 300 from dual
  union select 400 from dual
  union select 500 from dual
  union select 600 from dual
  union select 700 from dual
  union select 800 from dual
  union select 900 from dual
) huns,
(select 0 x from dual
  union select 1000 from dual
  union select 2000 from dual
  union select 3000 from dual
  union select 4000 from dual
  union select 5000 from dual
  union select 6000 from dual
  union select 7000 from dual
  union select 8000 from dual
  union select 9000 from dual
) thous,
(select 0 x from dual
  union select 10000 from dual
  union select 20000 from dual
  union select 30000 from dual
  union select 40000 from dual
  union select 50000 from dual
  union select 60000 from dual
  union select 70000 from dual
  union select 80000 from dual
  union select 90000 from dual
) tenthous,
(select 0 x from dual
  union select 100000 from dual
  union select 200000 from dual
  union select 300000 from dual
  union select 400000 from dual
  union select 500000 from dual
  union select 600000 from dual
  union select 700000 from dual
  union select 800000 from dual
  union select 900000 from dual
) hunthous;
commit;
prompt 1000000 records loaded
