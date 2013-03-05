/************************************************************************************
 *				PN_PROPERTY
************************************************************************************/

-- prm.directory.directorypage.materials.column.id

-- Enterprise


select * from PN_PROPERTY a where a.PROPERTY like '%prm.schedule.taskview.resources.assign.workingcalendar.column%';



-- Propiedades de TaskView Materials (materiales de la tarea).
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.material.cantassignmentmaterial.message','You cannot assign a material in a template!','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.material.unsavedchanges.message','You haven''t saved your changes yet','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.material.oncriticalpath.message','This task is in the critical path','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.material.overallocatedmaterials.message','This task has over-assigned materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.material.lookforfixes.message','(Look for Fixes)','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskedit.material.cannotassignonshared.message','Materials cannot be added to shares.','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskedit.material.assignor.label','Assignor','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.material.channel.assign.title','Assign Materials to a Task','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.material.assign.material.column','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.material.assign.workingcalendar.column','Utilization<br>Summary','A',0,1,null);



-- update pn_property set Property_value='1' where pn_property.property = 'prm.enterprise.isenabled'
-- insert into PN_PROPERTY values (2000,'en','boolean','prm.enterprise.isenabled','1','A',0,0,null);

-- Insercion de una propiedad
-- insert into PN_PROPERTY values (2000,'en','text','propiedad','valor','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.application.nav.space.material','Materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.space.spacetypes.materials.name','Materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.nav.dashboard','Dashboard','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.materialspace.module.description','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.global.materials.objecttype.material','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.materials.tab','Materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.directory.directory.tab.materials.title','Materials','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','css','prm.global.css.material','/styles/material.css','A',0,0,null);

insert into PN_PROPERTY values (2000,'en','boolean','prm.material.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','boolean','prm.material.dashboard.isenabled','1','A',0,0,null);

--Error harcodeado en taskedit/assignments
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskedit.resources.cannotassignonshared.message','Assignments cannot be added to shares.  All assignment work must occur in the source task or schedule.','A',0,1,null);

--Navbar Project/Material
insert into PN_PROPERTY values (2000,'en','boolean','prm.project.material.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.nav.material','Materials','A',0,1,null);

-- Portafolio de Materiales
insert into PN_PROPERTY values (2000,'en','text','prm.material.main.channel.title','Materials you have access to','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.main.title','Material List','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.main.create.button.tooltip','New Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.main.modify.button.tooltip','Modify Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.main.remove.button.tooltip','Delete Material','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.material.main.module.history','Materials','A',0,1,null);

--Material main
insert into PN_PROPERTY values (2000,'en','text','prm.material.main.authorizationfailed.message','Access to Material Workspace denied','A',0,1,null);

-- Workplan
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.main.materialassignments','Materials','A',0,1,null);

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

-- Propiedades para la vista de borrar material
insert into PN_PROPERTY values (2000,'en','text','prm.material.delete.wizard.step1.title','Delete Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.delete.wizard.step1.pagetitle','Material Delete Wizard','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.delete.wizard.step1.rightpagetitle','Step 1','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.delete.wizard.step1.channel.selectoption','Select one of the following option(s)','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.delete.wizard.step1.button.finish.label','Finish','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.delete.wizard.step1.option.disablematerial.label','Disable the Material for this Project Workspace','A',0,1,null);





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

COMMIT;

