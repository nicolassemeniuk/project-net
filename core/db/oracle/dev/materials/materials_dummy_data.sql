/* Dummy inserts */
INSERT INTO PN_MATERIAL_TYPE values (1,'Construction','A');
INSERT INTO PN_MATERIAL_TYPE values (2,'Electronics','A');
INSERT INTO PN_MATERIAL_TYPE values (3,'Disposable','A');
INSERT INTO PN_MATERIAL_TYPE values (4,'Machinery','A');
INSERT INTO PN_MATERIAL_TYPE values (5,'Other','A');

INSERT INTO PN_MATERIAL values (1,'Material 1','Este es el primer material',1,5000.00,'A');
INSERT INTO PN_MATERIAL values (2,'Material 2','Este es el segundo material',3,215.00,'A');
INSERT INTO PN_MATERIAL values (3,'Material 3','Este es el tercer material',1,789.00,'A');

INSERT INTO PN_SPACE_HAS_MATERIAL values (8062,1,'A');



/************************************************************************************
 *				PN_SPACE_HAS_MODULE
 *			Persona con modulo habilitado
************************************************************************************/
--Nicolas=6080, Modulo de Materiales=175, Acceso=1
insert into PN_SPACE_HAS_MODULE values (6080,175,1);

/************************************************************************************
 *				PN_MODULE_PERMISSION
 *			Tipo de acceso al modulo por grupo
************************************************************************************/
--Nicolas=6080, Grupo=6083, Modulo de Materiales=175, Acceso=65535 (mascara)
insert into PN_MODULE_PERMISSION values (6080,6083,175,65535);