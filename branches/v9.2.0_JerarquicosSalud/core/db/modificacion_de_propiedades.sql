SET DEFINE OFF;
INSERT INTO PN_PROPERTY ( CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB ) VALUES ( 2000, 'es', 'text', 'prm.project.defaultcurrency.code', 'ARS', 'A', 0, 1, NULL);


/* Provincias */

DELETE PN_STATE_LOOKUP;

insert into PN_STATE_LOOKUP values ('N', 'Provincia No Argentina', 'AR');
insert into PN_STATE_LOOKUP values ('BA', 'Buenos Aires', 'AR');
insert into PN_STATE_LOOKUP values ('CF', 'Capital Federal', 'AR');
insert into PN_STATE_LOOKUP values ('CT', 'Catamarca', 'AR');
insert into PN_STATE_LOOKUP values ('CH', 'Chaco', 'AR');
insert into PN_STATE_LOOKUP values ('CU', 'Chubut', 'AR');
insert into PN_STATE_LOOKUP values ('CB', 'Córdoba', 'AR');
insert into PN_STATE_LOOKUP values ('CO', 'Corrientes', 'AR');
insert into PN_STATE_LOOKUP values ('ER', 'Entre Ríos', 'AR');
insert into PN_STATE_LOOKUP values ('FS', 'Formosa', 'AR');

insert into PN_STATE_LOOKUP values ('JU', 'Jujuy', 'AR');
insert into PN_STATE_LOOKUP values ('LP', 'La Pampa', 'AR');
insert into PN_STATE_LOOKUP values ('LR', 'La Rioja', 'AR');
insert into PN_STATE_LOOKUP values ('MZ', 'Mendoza', 'AR');
insert into PN_STATE_LOOKUP values ('MS', 'Misiones', 'AR');
insert into PN_STATE_LOOKUP values ('NQ', 'Neuquén', 'AR');
insert into PN_STATE_LOOKUP values ('RN', 'Río Negro', 'AR');

insert into PN_STATE_LOOKUP values ('SA', 'Salta', 'AR');
insert into PN_STATE_LOOKUP values ('SJ', 'San Juan', 'AR');
insert into PN_STATE_LOOKUP values ('SL', 'San Luis', 'AR');

insert into PN_STATE_LOOKUP values ('SC', 'Santa Cruz', 'AR');
insert into PN_STATE_LOOKUP values ('SF', 'Santa Fe', 'AR');
insert into PN_STATE_LOOKUP values ('SE', 'Santiago del Estero', 'AR');

insert into PN_STATE_LOOKUP values ('TF', 'Tierra del Fuego', 'AR');
insert into PN_STATE_LOOKUP values ('TU', 'Tucumán', 'AR');

commit;
SET DEFINE ON;