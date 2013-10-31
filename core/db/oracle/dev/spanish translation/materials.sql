/************************************************************************************
 *				PN_PROPERTY's
************************************************************************************/

-- Reports
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.materialreport.name','Reporte de Materiales','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.materialreport.description','Lista los Materiales del Espacio de Trabajo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.materialreport.xslpath','/report/xsl/projectmaterialreport.xsl','A',0,1,null);

insert into PN_PROPERTY values (2000,'es','text','prm.material.report.materialname.name','Nombre del Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.materialdescription.name','Descripción','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.materialcost.name','Costo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.materialtype.name','Tipo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.materialconsumable.name','Cons.','A',0,1,null);

insert into PN_PROPERTY values (2000,'es','text','prm.material.report.totalmaterials.name','Número total de Materiales','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.totalcost.name','Costo total de Materiales','A',0,1,null);

insert into PN_PROPERTY values (2000,'es','text','prm.material.report.projectmaterialreport.grouping.default.name','Sin Agrupar','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.projectmaterialreport.grouping.bytype.name','Agrupar por Tipo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.common.grouping.bytype.name','Agrupado por Tipo','A',0,1,null);

                                                  
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.report.projectmaterialreport.showallmaterials.name','Mostrar todos los Materiales','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.common.filter.consumable.name','Consumible','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.common.filter.notconsumable.name','No Consumible','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.report.common.reportsummary.name','Sumario del Reporte','A',0,1,null);


insert into PN_PROPERTY values (2000,'es','text','prm.material.columndefs.materials.name','Materiales','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.columndefs.material.name','Nombre','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.columndefs.material.description','Descripción','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.columndefs.material.cost','Costo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.columndefs.material.typeid','Tipo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.columndefs.material.type','Tipo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.columndefs.material.consumable','Consumible','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.columndefs.material.consumable.yes','Si','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.columndefs.material.consumable.no','No','A',0,1,null);

-- Material allocation
insert into PN_PROPERTY values (2000,'es','text','prm.material.allocation.title','Asignaciones del Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.allocation.materialcalendarsfor.label','Calendarios de utilización para {0}','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.allocation.materiallistfor.label','Asignaciones de Material para {0}','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.allocationlist.dates.name','Fecha (s)','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.allocationlist.spacename.name','Espacio de Trabajo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.allocationlist.percentassigned.name','% Asignado','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.allocationlist.taskname.name','Nombre de Tarea','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.allocation.legend.none.name','Material No Asignado','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.allocation.legend.full.name','Material Asignado','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.allocation.legend.over.name','Material Sobre-asignado','A',0,1,null);

-- Propiedades de TaskView Materials.
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.taskview.material.cantassignmentmaterial.message','No puede asignar un material a una plantilla!','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.taskview.material.unsavedchanges.message','No ha guardado todavía sus cambios','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.taskview.material.oncriticalpath.message','Esta tarea está en el camino crítico','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.taskview.material.overallocatedmaterials.message','Esta tarea tiene materiales sobre-asignados','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.taskview.material.lookforfixes.message','(Buscar arreglos)','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.taskedit.material.cannotassignonshared.message','Los Materiales no pueden ser asignados a tareas compartidas. Todo el trabajo de asignación debe ocurrir en la tarea o cronograma de origen.','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.taskedit.material.assignor.label','Asignador','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.taskview.material.channel.assign.title','Asignar Materiales a una Tarea','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.taskview.material.assign.material.column','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.taskview.material.assign.workingcalendar.column','Sumario de<br>Utilización','A',0,1,null);

insert into PN_PROPERTY values (2000,'es','text','prm.material.nav.dashboard','Escritorio','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.materialspace.module.description','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.global.materials.objecttype.material','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.taskview.materials.tab','Materiales','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.directory.directory.tab.materials.title','Materiales','A',0,1,null);

-- Error harcodeado en taskedit/assignments
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.taskedit.resources.cannotassignonshared.message','Las asignaciones no pueden ser agregadas a Tareas compartidas. Todo el trabajo de asignación debe ocurrir en la tarea o cronograma de origen.','A',0,1,null);

-- Navbar Proyecto/Material
insert into PN_PROPERTY values (2000,'es','boolean','prm.project.material.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'es','text','prm.project.nav.material','Materiales','A',0,1,null);

-- Navbar Negocio/Material
insert into PN_PROPERTY values (2000,'es','boolean','prm.business.material.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'es','text','prm.business.nav.material','Materiales','A',0,1,null);

-- Portafolio de Materiales
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.channel.title','Materiales a los que posee acceso','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.title','Lista de Materiales','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.create.button.tooltip','Nuevo Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.modify.button.tooltip','Modificar Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.remove.button.tooltip','Borrar Material','A',0,1,null);

insert into PN_PROPERTY values (2000,'es','text','prm.material.main.module.history','Materiales','A',0,1,null);

-- Directorio de Materiales
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.authorizationfailed.message','Acceso a el Espacio de Trabajo de Materiales denegado.','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.list.name','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.list.type','Tipo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.list.cost','Costo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.list.consumable','Consumible','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.list.description','Descripción','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.material.tab.businessmaterials.title','Materiales del Negocio','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.material.tab.projectmaterials.title','Materiales del Proyecto','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.roster.namesearch.label','Buscar','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.roster.typesearch.label','Tipo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.roster.consumablesearch.label','Consumible','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.roster.costrangesearch.label','Rango de Costos','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.material.mincostamountincorrect.message','El Costo mínimo debe ser un valor positivo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.material.maxcostamountincorrect.message','El Costo máximo debe ser un valor positivo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.main.material.costincorrectrange.message','El costo máximo no puede ser menor que el mínimo','A',0,1,null);

-- Diálogo de Asignación de Material
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.assignmaterialsdialog.title','Materiales','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.assignmaterialsdialog.addtoexisting','Agregar a asignaciones de Materiales existentes','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.assignmaterialsdialog.replaceexisting','Reemplazar asignaciones de Materiales existentes','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.assignmaterialsdialog.materials','Materiales','A',0,1,null);

-- Nuevo Material
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.channel.generalinformation.title','Información General LOS CAMPOS EN NEGRITA SON REQUERIDOS','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.title.label','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.materialnamerequired.message','Nombre del Material es un campo requerido','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.materialnamelength.message','El Nombre del Material debe tener menos de 40 caracteres','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.materialdescriptionlength.message','La descripción debe ser menor a 240 caracteres','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.materialcostformat.message','El Costo del Material debe ser un valor número válido','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.lefttitle.label','Crear un nuevo Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.righttitle.label','Identidad del Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.name.label','Nombre del Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.type.label','Tipo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.cost.label','Costo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.consumable.label','Consumible','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.create.wizard.step1.description.label','Descripción','A',0,1,null);

-- Modificar Material
insert into PN_PROPERTY values (2000,'es','text','prm.material.modifymaterial.title.label','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.modifymaterial.namerequired.message','Nombre del Material es un campo requerido','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.modifymaterial.descriptionlength.message','La descripción debe ser menor a 240 caracteres','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.modifymaterial.channel.modify.title','Modificar Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.modifymaterial.materialname.label','Nombre del Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.modifymaterial.materialtype.label','Tipo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.modifymaterial.materialcost.label','Costo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.modifymaterial.materialdescription.label','Descripción','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.modifymaterial.canteditwithassignments.label','Este Material posee asignaciones. Para cambiar el campo Consumible se deben remover todas las mismas.','A',0,1,null);

-- Eliminar Material
insert into PN_PROPERTY values (2000,'es','text','prm.material.delete.wizard.step1.title','Eliminar Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.delete.wizard.step1.pagetitle','Asistente de Eliminación de Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.delete.wizard.step1.rightpagetitle','Paso 1','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.delete.wizard.step1.channel.selectoption','Seleccione una de las siguientes opciones','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.delete.wizard.step1.button.finish.label','Finalizar','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.delete.wizard.step1.option.disablematerial.label','Deshabilitar el Material para este Espacio de Trabajo','A',0,1,null);

-- Propiedades para la vista de ver material
insert into PN_PROPERTY values (2000,'es','text','prm.material.viewmaterial.channel.view.title','Ver Material','A',0,1,null);                                                  
insert into PN_PROPERTY values (2000,'es','text','prm.material.viewmaterial.materialname.label','Nombre de Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.viewmaterial.materialtype.label','Tipo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.viewmaterial.materialcost.label','Costo','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.viewmaterial.materialconsumable.label','Consumible','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.material.viewmaterial.materialdescription.label','Descripción','A',0,1,null);

-- Plan de Trabajo
insert into PN_PROPERTY values (2000,'es','text','all.global.toolbar.standard.materials','Asignar Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','all.global.toolbar.standard.materials.alt','Asignar Materiales','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','imagepath','all.global.toolbar.standard.materials.image.off','/images/icons/toolbar-gen-properties_off.gif','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','imagepath','all.global.toolbar.standard.materials.image.on','/images/icons/toolbar-gen-properties_on.gif','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','imagepath','all.global.toolbar.standard.materials.image.over','/images/icons/toolbar-rollover-properties.gif','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.main.materialassignments','Materiales','A',0,1,null);

-- Detalles del Plan de Trabajo
insert into PN_PROPERTY values (2000,'es','text','prm.taskdetail.nomaterialassignments.message','No hay materiales asignados a esta tarea','A',0,1,null);

-- Escritorio
insert into PN_PROPERTY values (2000,'es','text','prm.project.dashboard.projectmaterials.channel.title','Materiales del Proyecto','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.project.dashboard.materials.link.title','Crear un Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.project.dashboard.projectmaterials.name.label','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.project.dashboard.projectmaterials.workingcalendar.label','Sumario de Utilización','A',0,1,null);

-- Propiedades del Cronograma
insert into PN_PROPERTY values (2000,'es','text','prm.schedule.tasklistdecorating.hasMaterialAssigment.label','La Tarea tiene uno o más Materiales asignados','A',0,1,null);

-- Escritorio del Negocio - Canal de Materiales del Negocio
insert into PN_PROPERTY values (2000,'es','boolean','prm.business.main.channel.businessmaterials.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'es','text','prm.business.main.channel.businessmaterials.title','Materiales del Negocio','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.business.dashboard.businessmaterials.name.label','Material','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.business.dashboard.businessmaterials.workingcalendar.label','Sumario de Utilización','A',0,1,null);
insert into PN_PROPERTY values (2000,'es','text','prm.business.dashboard.creatematerials.link','Crear un Material','A',0,1,null);

COMMIT;

/