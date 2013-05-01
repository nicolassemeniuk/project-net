/************************************************************************************
 *				PN_PROPERTY
************************************************************************************/

-- prm.directory.directorypage.materials.column.id

-- Enterprise


select * from PN_PROPERTY a where a.PROPERTY like '%prm.material.create.wizard.step1.channel.generalinformation.title%';

delete from PN_PROPERTY a where a.PROPERTY like '%prm.material.create.wizard.step1.channel.generalinformation.title%';
-- update pn_property set Property_value='1' where pn_property.property = 'prm.enterprise.isenabled'
-- insert into PN_PROPERTY values (2000,'en','boolean','prm.enterprise.isenabled','1','A',0,0,null);

-- Insercion de una propiedad
-- insert into PN_PROPERTY values (2000,'en','text','propiedad','valor','A',0,1,null);


-- Reports
insert into PN_PROPERTY values (2000,'en','text','prm.project.report.projectmaterialreport.name','Project Materials Report','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.report.projectmaterialreport.description','Lists the Materials in the Proyect','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.report.projectmaterialreport.xslpath','/report/xsl/projectmaterialreport.xsl','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.material.report.materialname.name','Material Name','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.report.materialdescription.name','Description','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.report.materialcost.name','Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.report.materialtype.name','Type','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.report.materialconsumable.name','Cons.','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.material.report.totalmaterials.name','Total Number of Materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.report.totalcost.name','Total Cost of Materials','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.material.report.projectmaterialreport.grouping.default.name','No Grouping','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.report.projectmaterialreport.grouping.bytype.name','Group By Type','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.report.common.grouping.bytype.name','Group By Type','A',0,1,null);

                                                  
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.report.projectmaterialreport.showallmaterials.name','Show All Materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.report.common.filter.consumable.name','Consumable','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.report.common.filter.notconsumable.name','No Consumable','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.report.common.reportsummary.name','Report Summary','A',0,1,null);


insert into PN_PROPERTY values (2000,'en','text','prm.material.columndefs.materials.name','Materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.columndefs.material.name','Name','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.columndefs.material.description','Description','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.columndefs.material.cost','Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.columndefs.material.typeid','Type','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.columndefs.material.type','Type','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.columndefs.material.consumable','Consumable','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.columndefs.material.consumable.yes','Yes','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.columndefs.material.consumable.no','No','A',0,1,null);

--Report type and sequence
insert into pn_space_type_has_report_type values('project','pmr');
insert into pn_report_sequence values('pmr',11);

-- MaterialAllocation
insert into PN_PROPERTY values (2000,'en','text','prm.material.allocation.materialcalendarsfor.label','Utilization Calendars for {0}','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.allocation.materiallistfor.label','Material Assignments for {0}','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.allocationlist.dates.name','Dates (s)','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.allocationlist.spacename.name','Workspace Name','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.allocationlist.percentassigned.name','% Assigned','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.allocationlist.taskname.name','Task Name','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.allocation.legend.none.name','Material Not Assigned','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.allocation.legend.full.name','Material Assigned','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.allocation.legend.over.name','Material Over-assigned','A',0,1,null);

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
insert into PN_PROPERTY values (2000,'en','text','prm.material.main.list.name','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.main.list.type','Type','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.main.list.cost','Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.main.list.consumable','Consumable','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.main.list.description','Description','A',0,1,null);

-- Assign material dialog
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.assignmaterialsdialog.title','Materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.assignmaterialsdialog.addtoexisting','Add to Existing Material Assignments','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.assignmaterialsdialog.replaceexisting','Replace Existing Material Assignments','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.assignmaterialsdialog.materials','Materials','A',0,1,null);

-- New Material
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.channel.generalinformation.title','General Information   FIELDS IN BLACK ARE REQUIRED','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.title.label','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.materialnamerequired.message','Material Name is a required field','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.materialnamelength.message','Material name must be less than 40 characters','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.materialdescriptionlength.message','Description must be less than 240 characters','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.materialcostformat.message','Material Cost must be a valid number','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.lefttitle.label','Create New Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.righttitle.label','Material Identity','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.channel.generalinformation.title',null,'A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.name.label','Material Name','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.type.label','Type','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.cost.label','Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.consumable.label','Consumable','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.create.wizard.step1.description.label','Description','A',0,1,null);

-- Modify Material
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.title.label','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.namerequired.message','Material Name is a required field','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.descriptionlength.message','Description must be less than 200 characters','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.channel.modify.title','Modify Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.materialname.label','Material Name','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.materialtype.label','Type','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.materialcost.label','Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.materialdescription.label','Description','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.modifymaterial.canteditwithassignments.label','This Material has assignments. To change the Consumable field remove all assignations.','A',0,1,null);

-- Disable Material
insert into PN_PROPERTY values (2000,'en','text','prm.material.delete.wizard.step1.title','Delete Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.delete.wizard.step1.pagetitle','Material Delete Wizard','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.delete.wizard.step1.rightpagetitle','Step 1','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.delete.wizard.step1.channel.selectoption','Select one of the following option(s)','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.delete.wizard.step1.button.finish.label','Finish','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.delete.wizard.step1.option.disablematerial.label','Disable the Material for this Project Workspace','A',0,1,null);

-- Propiedades para la vista de ver material
insert into PN_PROPERTY values (2000,'en','text','prm.material.viewmaterial.channel.view.title','View Material','A',0,1,null);                                                  
insert into PN_PROPERTY values (2000,'en','text','prm.material.viewmaterial.materialname.label','Material Name','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.viewmaterial.materialtype.label','Type','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.viewmaterial.materialcost.label','Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.viewmaterial.materialconsumable.label','Consumable','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.material.viewmaterial.materialdescription.label','Description','A',0,1,null);

-- Workplan
insert into PN_PROPERTY values (2000,'en','text','all.global.toolbar.standard.materials','Assign Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','all.global.toolbar.standard.materials.alt','Assign Material(s)','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','imagepath','all.global.toolbar.standard.materials.image.off','/images/icons/toolbar-gen-properties_off.gif','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','imagepath','all.global.toolbar.standard.materials.image.on','/images/icons/toolbar-gen-properties_on.gif','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','imagepath','all.global.toolbar.standard.materials.image.over','/images/icons/toolbar-rollover-properties.gif','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.main.materialassignments','Materials','A',0,1,null);

-- Workplan details
insert into PN_PROPERTY values (2000,'en','text','prm.taskdetail.nomaterialassignments.message','No materials assigned to this task','A',0,1,null);

-- Dashboard
insert into PN_PROPERTY values (2000,'en','text','prm.project.dashboard.projectmaterials.channel.title','Project Materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.dashboard.materials.link.title','Create a Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.dashboard.projectmaterials.name.label','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.dashboard.projectmaterials.workingcalendar.label','Utilization Summary','A',0,1,null);

-- Schedule Properties
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.tasklistdecorating.hasMaterialAssigment.label','Task has one or more material assignments','A',0,1,null);

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

