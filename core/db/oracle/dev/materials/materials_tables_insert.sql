/************************************************************************************
 *				PN_PROPERTY
************************************************************************************/

-- prm.directory.directorypage.materials.column.id

-- Seleccion de una propiedad

select *
from PN_PROPERTY a
where a.PROPERTY like '%enterprise%'

update pn_property set Property_value='1' where pn_property.property = 'prm.enterprise.isenabled'

insert into PN_PROPERTY values (2000,'en','boolean','prm.enterprise.isenabled','1','A',0,0,null);

-- Insercion de una propiedad
-- insert into PN_PROPERTY values (2000,'en','text','propiedad','valor','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.application.nav.space.material','Materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.space.spacetypes.materials.name','Materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.nav.dashboard','Dashboard','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.materialspace.module.description','Materials Workspace','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.global.materials.objecttype.material','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.materials.tab','Materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.directory.directory.tab.materials.title','Materials','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','css','prm.global.css.material','/styles/material.css','A',0,0,null);

insert into PN_PROPERTY values (2000,'en','boolean','prm.material.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','boolean','prm.material.dashboard.isenabled','1','A',0,0,null);

-- Portafolio de Materiales
insert into PN_PROPERTY values (2000,'en','text','prm.material.materialportfolio.channel.memberof.title','Materials you have access to','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.materialportfolio.title','Material List','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.materialportfolio.create.button.tooltip','New Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.materialportfolio.modify.button.tooltip','Modify Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.materialportfolio.remove.button.tooltip','Delete Material','A',0,1,null);



--Material main
insert into PN_PROPERTY values (2000,'en','text','prm.material.main.authorizationfailed.message','Access to Material Workspace denied','A',0,1,null);


-- Propiedades para la vista de nuevo material
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.materialnamerequired.message','Material Name is a required field','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.materialdescriptionlength.message','Description must be less than 200 characters','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.lefttitle.label','Create New Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.righttitle.label','Material Identity','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.channel.generalinformation.title',null,'A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.name.label','Material Name','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.type.label','Material Type','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.cost.label','Material Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.description.label','Material Description','A',0,1,null);

-- Propiedades para la vista de modificar material
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.namerequired.message','Material Name is a required field','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.descriptionlength.message','Description must be less than 200 characters','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.channel.modify.title','Modify Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.materialname.label','Material Name','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.materialtype.label','Material Type','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.materialcost.label','Material Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.materialdescription.label','Material Description','A',0,1,null);

/************************************************************************************
 * 				PN_OBJECT_TYPE
 *			   Tipos de objetos
************************************************************************************/
insert into PN_OBJECT_TYPE values ('material','pn_material','@prm.global.material.objecttype.material',null,1,0,0);

/************************************************************************************
 *				PN_MODULE
 *				 Modulos
************************************************************************************/
insert into PN_MODULE values (260,'material','@prm.materialspace.module.description', 1, null);



