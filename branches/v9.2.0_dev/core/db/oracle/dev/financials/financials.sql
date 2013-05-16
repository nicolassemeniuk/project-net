-- PN_PROPERTY
insert into PN_PROPERTY values (2000,'en','text','prm.application.nav.space.financial','Financial','A',0,1,null); -- src
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.isenabled','1','A',0,0,null); -- src
insert into PN_PROPERTY values (2000,'en','text','prm.space.spacetypes.financial.name','Financial','A',0,1,null); -- src
-- insert into PN_PROPERTY values (2000,'en','text','prm.financial.nav.dashboard','Dashboard','A',0,1,null); 
insert into PN_PROPERTY values (2000,'en','css','prm.global.css.financial','/styles/financial.css','A',0,0,null); -- src
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.dashboard.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','text','prm.finacialsspace.module.description','Financial Workspace','A',0,1,null);
-- insert into PN_PROPERTY values (2000,'en','text','prm.global.financial.objecttype.financial','Financial','A',0,1,null); -- Adicional

-- Financial portfolio
insert into PN_PROPERTY values (2000,'en','text','prm.financial.financialportfolio.title','Business List','A',0,1,null); -- src
insert into PN_PROPERTY values (2000,'en','text','prm.financial.financialportfolio.channel.memberof.title', 'Business finances you have access to','A',0,1,null);

-- PN_MODULE
insert into PN_MODULE values (175,'financial_space','@prm.financialspace.module.description', 1, null);

-- PN_SPACE_HAS_MODULE
-- Persona con modulo habilitado													
-- Juan Perez=12108, Modulo Financiero=175, Acceso=1
-- insert into PN_SPACE_HAS_MODULE values (12108,175,1); -- Adicional

-- PN_MODULE_PERMISSION 
-- Tipo de acceso al módulo por grupo
-- Juan Perez=12108, Grupo=11327, Modulo de Materiales=175, Acceso=65535 (mascara)
-- insert into PN_MODULE_PERMISSION values (12108,11327,175,65535);

-- PN_OBJECT_TYPE
-- Tipos de objetos
-- insert into PN_OBJECT_TYPE values ('financial','pn_financial','@prm.global.financial.objecttype.financial',null,1,0,0); -- Adicional

