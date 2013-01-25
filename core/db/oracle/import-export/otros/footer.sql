-- Concatenar con:
-- cat header.sql export.sql footer.sql > projectnet.sql

-- Carga de las licencias que no pueden ser exportadas por el SQL Developer
prompt Loading licenses...
UPDATE "PNET"."PN_LICENSE_CERTIFICATE_LOB" SET CERTIFICATE_LOB_DATA = '7fa4b9ecdf03de54470e4e93aa6dbfae95e37888c5430ce10e245d9c8d9fb8289f20a124565fca314b3065df4fe40fa0df9549e54fc2067c81e3978489c61dd093c307da69951a14c0a11ff4e1c68b50164429f9300eacb23c9ede1debda3c20f1ac625a0d1eb3e41ccf9e1f392483eaebb1c55af56fe15c4faec3d2f54928df4eb2f3343d64c69f53a5bf3395bb7e6903bae21bf54b5630427cb25e46b77e27ac06b97e1da6806aed6bbda977b5fac84eb2f3343d64c69fcea61c97b4a7e67bc0e66cae41e50df93d6bcbcade1ae85e1e6f8bea2309fd6b1b649bc7473d80b21af51aa08881383153ae22d12a84e85e1a9fae7919e6423a10d50ebef98421c1e816a04ffa4936cb341f0e09c20f5818cdf567d3ff4fdf3e9865786575b257512cf262256fcae8ac7a019c7e8ba5917b7ed474b3a82d03199fed88d7cd04924e22fbd4d4ace11a5685637f510fdf721510c61eb902e7f4d41b649bc7473d80b2c61e7d30d1545495c07157e0c940917d9ba0433ec91179fa02adef963927a9282cb50aa7ebe4c3e7c4ccf467dfd4c2ae03920f040302ba8600f396c67d8493a8c37ee808529049992196a2735a184301db0e16492962195ecf8d7601648c77fbb6a6391cef0a155679cf41729ec7bcf324b7232fe6b795f322c3a254cfe9f66c10d3f7c4f437fd817248141ded1012bee4521f7b5e6a2db95b064f38338170aed1ca6a72504a7ad060305f26a53a481a9ba0433ec91179faaa35d657a5ab8e30cc56d8f54535a3727d1d1ac5ac30cbc21c64d86a25bc2c88c286c6353ffec069515ed65f9777915cc4b9be2879b93bc889b2bbb659d0929b63579fac31200fde6882b7023fcaf87a4b30a6cd4aec8d7de36a5e006206d212bec2c93d3f2834a3904a5b21ec63e1bcf0ec1055b2540156ae20dd8da106bc42636e3216301511250ea2a7878a31a712e071013840b9f53c426e89984db2af03493994471d8d3452150d663b5fc8a86ac984960499adfc43385d719c09a6771742a96fb432062b8ca29729e6d0b35b3ef7dd8e2db2a6c098512eafaac7e06db59a5b298ae1405b428a82a87a4d7e56e0beab5a8cd183ae3be975627deee245cbb20b97c10d75fa3995b9a9f2eeb19c9049c33a0e74b07af9e579e58e6e32d7f9c29c3ec0ad529e934c1df824ca998633f108eafc812cf15df2fe72a91aca0fa7eeb4216523719c310cc35ba09b11c6bedb35a12c5c223f3fc48fc38a850b41ce0ca3ce47c01c3fd438b0563e9d9aef213eb523e98a29146a0dfefc53432356cbd188ed885c93af254499d937856db83c63c83bc03acb9fb4cd9add8b5d659b15e8b5c36af0e5d6c625febbae8937d430' WHERE CERTIFICATE_ID = 6008;

prompt No olvidar de llenar la tabla PN_LICENSE_MASTER_PROP_CLOB

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

-- Para que desconecte el sqlpus y vuelva al shell
EXIT;
