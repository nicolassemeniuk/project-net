/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
-- setfeedback off
-- setdefine off
-- prompt Disabling foreign key constraints for PN_PROPERTY...
alter table PN_PROPERTY disable constraint PN_PROPERTY_FK2
/
-- prompt Loading PN_PROPERTY...

-- sample  insert record 
-- insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
-- values (2000, 'en', 'text', 'some.key.value', 'Some property value', 'A', 0, 1, null);
-- here add new properties

-- sample  update of existing record 
--	update pn_property p set 
--	       p.property_type = 'some property type to set',
--	       p.property_value = 'some property_value to set',
--	       p.record_status = 'some record status',
--	       p.is_system_property = some_system_property,
--	       p.is_translatable_property = some_translatable_property,
--	       p.property_value_clob = 'some property value clob'
--	where 
--	      p.context_id=2000 and
--	      p.language = 'en' and
--	      p.property = 'prm.workflow.envelope.abort.label'
-- values under apostrophes are VARCHAR2 and without are NUMBER type so please pay attention
-- please in UPDATE caluse use only those columns you want to update and not all of them
-- usual column for update will be property_value where property value resides


insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.main.view.option.indented.ajax.name', 'Indented (Beta)', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.workingtime.list.cannotremove.message', 'You cannot remove any calendar as no default is set to replace it.', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.filerequired.message', 'File to upload is a required field', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.module.history', 'Form defintion import', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.channel.selectfile.title', 'Select File to Import', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.channel.required.title', 'FIELDS IN BLACK ARE REQUIRED', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.file.label', 'File:', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.main.import.label', 'Import', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.properties.currentestimatedtotalcost.currency.label','Current Estimated Cost Currency','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.properties.actualcosttodate.currency.label','Actual Cost-To-Date Currency','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.viewfilter.label','View :','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.commenton.label','Comment On','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.from.label','From','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.comments.label','comments :','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.addacomment.link','add a comment','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.edit.link','edit','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.blogit', 'BlogIt', 'A', 0, 0, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.blogit.alt', 'BlogIt', 'A', 0, 0, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.blogit.image.off', '/images/icons/toolbar-gen-blogit_off.gif', 'A', 0, 0, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.blogit.image.on', '/images/icons/toolbar-gen-blogit_on.gif', 'A', 0, 0, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.blogit.image.over', '/images/icons/toolbar-gen-blogit_over.gif', 'A', 0, 0, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.update.timerangeerror.message', 'Time {0} is overlapping with previous time {1}.  Please enter non overlapping times.', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.crossspace.createshare.externaltasks.warning', 'Unable to create shares for the following task(s): {0}. Sharing objects which are themselves shared from other workspaces is not permitted.', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.memberedit.chanelbartitle.label', 'Edit Participant', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.envelope.abort.confirmationmessage', 'Are you sure, abort envelope?', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.task.results.name.column', 'Name', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription1.channel.create.fields.required', 'FIELDS IN BLACK ARE REQUIRED', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.main.channel.myassignments.error.acceptedinvitationconstraint.message', 'It is required to accept a Project Invitation ({0}) before accepting a Task Assignment.', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.allspacedisplaypermission', '0', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.invalidstartdate', 'The schedule start date is not a valid date.', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.edit.editTitle', 'Editing page', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.history.historyTitle', 'History For Wiki Page', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.view.viewTitle', 'Wiki Page', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.edit.ownerid.error', 'Please Select the Owner', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.envelope.abort.label', 'Abort Envelope', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.setup.workingtimecalendar.description', 'Specify your working and non-working time.', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.setup.workingtimecalendar.link', 'Work Calendar', 'A', 0, 1, null)
/

update pn_property p set 
       p.property_value = 'It is after this task''''s deadline of {0}.'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.list.afterdeadline.message'
/

update pn_property p set 
       p.property_value = 'Phases'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.nav.process'
/

update pn_property p set 
       p.property_value = 'New'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.create'
/

update pn_property p set 
       p.property_value = 'New'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.create.alt'
/

update pn_property p set 
       p.property_value = 'New'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.action.create'
/

update pn_property p set 
       p.property_value = 'New'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.action.create.alt'
/

update pn_property p set 
       p.property_value = 'New'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.create'
/

update pn_property p set 
       p.property_value = 'New'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.create.alt'
/

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.modify'
/

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.modify.alt'
/

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.modify'
/

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.modify.alt'
/

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.action.modify'
/

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.action.modify.alt'
/

update pn_property p set 
       p.property_value = 'Delete'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.remove'
/

update pn_property p set 
       p.property_value = 'Delete'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.remove.alt'
/

update pn_property p set 
       p.property_value = 'Delete'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.remove.alt'
/

update pn_property p set 
       p.property_value = 'Editing Page'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.wiki.edit.editTitle'
/

update pn_property p set 
       p.property_value = 'History For Wiki Page'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.wiki.history.historyTitle'
/

update pn_property p set 
       p.property_value = 'Admin'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.nav.applicationspace'
/

update pn_property p set 
       p.property_value = 'Workplan Start Date:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.schedulestartdate.label'
/

update pn_property p set 
       p.property_value = 'Workplan End Date:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.scheduleenddate.label'
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.edit.describe.label','Edit summary (describe the changes you have made):','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.attach.image.describe.label','Select image to attach to this page:','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.menu.pageLinks.label','What Links Here','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.menu.changeHistory.label','Change History','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.menu.pageIndex.label','Index By Title','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.menu.recentChanges.label','Recent Changes','','A',0,1)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.defaultpagesize.value', '50', 'A', 0, 1, null)
/

update pn_property p set 
      p.property_value = 'Primary Email:'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.profile.name.primaryemail.label'
/

update pn_property p set 
      p.property_value = 'Your Name and Email Information'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.profile.name.channel.info.titile'
/

update pn_property p set 
       p.property_value = 'Your Address Information'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.profile.address.channel.info.title'
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.name.skype.label', 'Skype:', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.name.skillsbio.label', 'Skills/Bio:', 'A', 0, 1, null)
/

update pn_property p set 
       p.property_value = 'New Group'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.discussion.main.create.tooltip'
/

update pn_property p set 
       p.property_value = 'Delete Group'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.discussion.main.delete.tooltip'
/
      
update pn_property p set 
       p.property_value = 'Edit Group'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.discussion.main.modify.tooltip'
/
 
update pn_property p set 
       p.property_value = 'View Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.discussion.main.properties.tooltip'
/
 
update pn_property p set 
       p.property_value = 'New Post'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.discussion.threadlist.create.tooltip'
/ 
 
update pn_property p set 
       p.property_value = 'New Subscription'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.managesubscriptions.create.tooltip'
/ 
       
update pn_property p set 
       p.property_value = 'Edit Project'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.main.modify.button.tooltip'
/ 
      
update pn_property p set 
       p.property_value = 'View Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.main.properties.button.tooltip'
/ 
      
update pn_property p set 
       p.property_value = 'New Task'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.create.tooltip'
/ 
      
update pn_property p set 
       p.property_value = 'View Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.properties.tooltip'
/
      
update pn_property p set 
       p.property_value = 'New Task'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.gantt.createtask.tooltip'
/
      
update pn_property p set 
       p.property_value = 'Restore Item'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.main.undodelete.button.tooltip'
/

update pn_property p set 
       p.property_value = 'Update % Complete'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.percentage'
/

update pn_property p set 
       p.property_value = 'Recalculate'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.recalculate'
/

update pn_property p set 
       p.property_value = 'Next Post'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.action.next_post'
/

update pn_property p set 
       p.property_value = 'Manage Forms'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.formsedit.link'
/

update pn_property p set 
       p.property_value = '0'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.poweredby.isenabled'
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.name.validskillsbio.message', 'Please do not enter more than 300 characters for Skills/Bio.', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.name.validskillsbio.cannothtmltag.message', 'Skills/Bio text could not contain HTML tags', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.personalimageupload.selectimagetoupload.label','Select image to upload:','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.personalimageupload.imagetypenotsupported.message','This file type is not supported. \n Please select the image file of type .gif, .png, .jpeg, .bmp.','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.personalimageupload.selectimagetoupload.message','Please select an image to upload.','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.currentlynotworking.message','Currently not working on any project.','','A',0,1)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.otherprojects.textname', 'No Business Owner', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.externalformaccess.isenabled', '0', 'A', 0, 0, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.definitionedit.externalaccess.label', 'Allow external access', 'A', 0, 1, null)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.personalimageupload.uploadbutton.caption','Upload','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.personalimageupload.cancelbutton.caption','Cancel','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.confirmremoveimage.message','This will remove your current picture. \n Do you want to continue?.','','A',0,1)
/



update pn_property p set 
       p.PROPERTY_VALUE_CLOB = 'Copyright 2000-2008 Project.net, Inc.'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.footer.copyright'
/
      
update pn_property p set 
       p.property_value = '1'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.help.external.isenabled'
/
         
update pn_property p set 
       p.property_value = 'http://doc.project.net/9_0:{0}_{1}'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.help.helpdesk.pagesection.external.href'
/     
      
update pn_property p set 
       p.property_value = 'http://doc.project.net/9_0:{0}'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.help.helpdesk.page.external.href'
/                 

update pn_property p set 
       p.property_value = 'http://doc.project.net/9_0:{0}'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.help.page.external.href'
/ 
      
update pn_property p set 
       p.property_value = 'http://doc.project.net/9_0:{0}_{1}'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.help.pagesection.external.href'
/ 
      
      
update pn_property p set 
       p.property_value = 'Percent Complete has to be a positive number less than or equal to 100.'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.resource.updatework.error.pencentcomplete.range.message'
/ 


insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','boolean','prm.versioncheck.isenabled','1','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.versioncheck.message','There is a newer version of Project.net available, access the Application Administration > Utilities > Build Info module for more information.','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.versioncheck.message.newversion','Project.net X.X.X is available.','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.application.nav.space.personal','Personal','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.application.nav.space.business','Business','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.application.nav.space.projects','Projects','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.application.nav.space.enterprise','Enterprise','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.application.nav.space.resource','Resources','','A',0,1)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.personalizepage.title', 'Personalize', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.tokens.title', 'Tokens', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.nav.blog.title', 'Blog', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.main.remove.button.tooltip', 'Remove Document', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.main.new.button.tooltip', 'New Form', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.main.edit.button.tooltip', 'Edit Form', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.main.delete.button.tooltip', 'Delete Form', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.main.copy.button.tooltip', 'Copy Form', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.global.actions.icon.isenabled', '0', 'A', 0, 0, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.eaf.creatoremail.label', 'Email:', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.email.validate.wrongformat.message', 'Wrong email address format', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.confirmationpage.thanksforsubmiting.message', 'Thank you for submiting form', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.confirmationpage.formname.label', 'Form name:', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.confirmationpage.recordnumber.label', 'Created form record number:', 'A', 0, 1, null)
/

update pn_property p set 
       p.property_value = 'Personalize Page'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.personalizepage.link'
/ 

update pn_property p set 
       p.property_value = 'Edit Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.bookmarkproperties.modify.button.tooltip'
/ 

update pn_property p set 
       p.property_value = 'Edit Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.containerproperties.modify.button.tooltip'
/ 

update pn_property p set 
       p.property_value = 'Edit Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.directory.modify.button.tooltip'
/ 

update pn_property p set 
       p.property_value = 'Edit Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.main.modify.button.tooltip'
/ 

update pn_property p set 
       p.property_value = 'Edit Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.tabs.modify.button.tooltip'
/ 

update pn_property p set 
       p.property_value = 'Edit Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.viewsubscription.modify.tooltip'
/ 

update pn_property p set 
       p.property_value = 'Login:'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.login.loginname.label'
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.workingtime.editdate.description.invaliddate.message', 'Description must be less than 500 characters.', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.personal.myassignments.blogit.agileworkremaining.isenabled', '0', 'A', 0, 0, null)
/

update pn_property p set 
       p.property_value = 'by'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.blog.viewblog.entrypostedby.label'
/

update pn_property p set
       p.property_value = 'New comment'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.blog.viewblog.addacomment.link'
/

update pn_property p set
       p.property_value = 'Edit'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.blog.viewblog.edit.link'
/

update pn_property p set
       p.property_value = 'Load'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.designer.main.import.label'
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.blog.header','Blog','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.blogview.label','Blog View','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.projectblog.header','Project Blog','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.myprofile.link','My Profile','','A',0,1)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.timesheet.timeworked.label', 'Time Worked:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.timesheet.viewexistingtimesheet.message', 'View Existing Time Sheet Entries.', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.timesheet.timeworked.header', 'Time Worked', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.taskhistory.historynotfound.message', 'History Not Found', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.taskhistory.historyof.label', 'History of: ', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.changeofestimatedtimetofinish.header', 'Change Of Estimated Time To Finish', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.baselineplan.header', 'Baseline Plan', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.adjustment.header', 'Adjustment', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.percentcomplete.label', '% Complete', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.estimatedremaining.label', 'Estimated Remaining', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.submitbutton.caption', 'Submit', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.cancelbutton.caption', 'Cancel', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.reasonforchange.header', 'Reason for Change (required)', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.hidecomment.link', 'Hide Comments', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.project.label', 'Project:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.email.label', 'Email:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.skype.label', 'Skype:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.phone.label', 'Phone:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.mobile.label', 'Mobile:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.projectmanager.label', 'Project Manager:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.profile.link', 'Profile', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.resourceautomaticallyadded.message', '* Resource will be automatically added to the Team Members role.', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.doubleclickonprojectnametoviewdetails.message', 'Double click on project name to view details.', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.doubleclickonbusinessnametoviewdetails.message', 'Double click on business name to view details.', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.selectrole.label', 'Select Role*:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.applybutton.caption', 'Apply', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.cancelbutton.caption', 'Cancel', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workday.goto.lable', 'Go To', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workweekmodify.workweek.link', 'Work Week ', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workweekmodify.basedondefaultcalendar.label', '(Based on Default Calendar)', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workdate.nodatesspecified.label', 'No dates specified', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.myassignment.utilizationsummary.link', 'Utilization Summary', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workday.individualdates.header', 'Individual Dates', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workweekmodify.addanother.lable', 'Add Another', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workweekmodify.workdate.header', 'Work Date', 'A', 0, 1, null)
/


update pn_property p set
       p.property_value = 'New Business'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.businessportfolio.create.button.tooltip'
/	  

update pn_property p set
       p.property_value = 'Edit Business'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.editbusiness.label'
/

update pn_property p set
       p.property_value = 'New Project'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.project.portfolio.create.tooltip'
/

update pn_property p set
       p.property_value = 'Edit Task'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.modify.tooltip'
/

update pn_property p set
       p.property_value = 'Notify'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.notify.tooltip'
/

update pn_property p set
       p.property_value = 'Share Task'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.share'
/

update pn_property p set
       p.property_value = 'Add External Task'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.addexternal'
/

update pn_property p set
       p.property_value = 'Assign Task to Phase'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.phase'
/

update pn_property p set
       p.property_value = 'Move Task Up'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.taskup'
/

update pn_property p set
       p.property_value = 'Move Task Down'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.taskdown'
/

update pn_property p set
       p.property_value = 'Unindent Task'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.taskleft'
/

update pn_property p set
       p.property_value = 'Indent Task'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.taskright'
/

update pn_property p set
       p.property_value = 'Assign Resource'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.resources'
/

update pn_property p set
       p.property_value = 'New {@prm.project.process.phase.label}'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.process.phase.create.button.label'
/

update pn_property p set
       p.property_value = 'Business List'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.businessportfolio.title'
/

update pn_property p set
       p.property_value = 'Edit Profile'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.profile.module.history'
/

update pn_property p set
       p.property_value = 'My {@prm.global.tool.dashboard.name}'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.nav.dashboard'
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.mainpage.form.title', 'My {@prm.global.tool.form.name}', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.mainpage.personal.title', 'My {@prm.global.tool.document.name}', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.mainpage.personal.title', 'My {@prm.project.nav.calendar}', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.managesubscriptions.edit.tooltip', 'Edit Subscription', 'A', 0, 1, null)
/
	  
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.managesubscriptions.delete.tooltip', 'Delete Subscription', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.main.perdelete.button.tooltip', 'Permanently Delete Item', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.main.create.button.tooltip', 'New Template', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.main.delete.button.tooltip', 'Delete Template', 'A', 0, 1, null)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.create.button.tooltip', 'New Workflow', 'A', 0, 1, null)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.edit.button.tooltip', 'Edit Workflow', 'A', 0, 1, null)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.delete.button.tooltip', 'Delete Workflow', 'A', 0, 1, null)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.view.button.tooltip', 'View Properties', 'A', 0, 1, null)
/


insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.main.create.button.tooltip', 'New Project', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.main.delete.button.tooltip', 'Delete Project', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.setup.object.shares.delete.tootip', 'Remove Share', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.document.toolbar.link.tooltip', 'Link Document', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.main.externalurl.message', 'External access url', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.definitionedit.externalurl.label', 'External access url', 'A', 0, 1, null)
/


insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.membertitle.column', 'Member Title', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.stastus.onlinepresence.label', 'Online Presence:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.stastus.onlinestatus.label', 'Online', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.stastus.Offlinestatus.label', 'Offline', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.currenttime.label', 'Current Time:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.timezone.label', 'Time zone:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.recentactivity.header', 'Recent Activity', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.lastlogin.label', 'Last Login:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.lastblogentry.label', 'Last Blog Entry:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.noblog.message', 'No Blogs', 'A', 0, 1, null)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.profileview.header','Profile','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.myblog.link','My Blog','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.editprofileinfo.link','Edit Profile','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.uploadpicture.link','Upload Picture','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.removepicture.link','Remove Picture','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.contactinfo.header','Contact Info','','A',0,1)
/
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.teaminfo.header','Team Info','','A',0,1)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.upload.addimage.link', 'Add Image', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.upload.cancel.link', 'Cancel', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.upload.selectfile.label', 'File:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.upload.filedescription.label', 'File Description (optional):', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.edit.edithelplink.label', 'Editing help', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.edit.edithelplink.link', 'http://doc.project.net/User_Guide', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.menu.createNewWikiPage.label', 'Create Page', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.view.whatlinkshere.nolinksheremsg', 'There are no wiki pages that link to this one!', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.view.whatlinkshere.labelmsg', 'What links here:', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.menu.linkstoexistingpages.label', 'Links on this page', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.view.linkstoexistingpages.nolinksheremsg', 'This page has no inner wiki links to existing wiki pages.', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.view.linkstoexistingpages.labelmsg', 'Links to existing wiki pages:', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.index.noentriesmsg.labelmsg', 'This wiki does not contain any pages!', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.index.wikipageindex.headermsg', 'This is an alphabetical list of pages you can read on this wiki space.', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.index.wikirecentchangesindex.headermsg', 'This is a list of recently changed wiki pages in this wiki space.', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.innerwikiprefix.wiki', 'http://doc.project.net/', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.innerwikiprefix.help', 'http://doc.project.net/User_Guide#', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.innerwikiprefix.trac', 'http://dev.project.net/trac/', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.innerwikiprefix.wikipedia', 'http://en.wikipedia.org/wiki/', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.baselineenddate.column', 'Baseline End Date', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.baselinestartdate.column', 'Baseline Start Date', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.baselinework.column', 'Baseline Work', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.baselineduration.column', 'Baseline Duration', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.startvariance.column', 'Start Variance', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.endvariance.column', 'Finish Variance', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.workvariance.column', 'Work Variance', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.durationvariance.column', 'Duration Variance', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.statusnotifiers.column', 'Status Notifiers', 'A', 0, 1, null)
/

update pn_property p set
      p.property_value = 'Blog-it'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.blogit'
/

		
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.list.create.button.tooltip', 'New Record', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.list.modify.button.tooltip', 'Edit Record', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.list.remove.button.tooltip', 'Remove Record', 'A', 0, 1, null)
/	  
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldsmanager.create.button.tooltip', 'New Field', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldsmanager.remove.button.tooltip', 'Remove Field', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldsmanager.modify.button.tooltip', 'Edit Field', 'A', 0, 1, null)
/	  

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.listsmanager.create.button.tooltip', 'New List', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.listsmanager.remove.button.tooltip', 'Remove List', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.listsmanager.modify.button.tooltip', 'Edit List', 'A', 0, 1, null)
/	  	  
	  
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.export', 'Export MSP', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.export.alt', 'Export MSP', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.export.image.off', '/images/icons/toolbar-gen-properties_off.gif', 'A', 0, 0, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.export.image.on', '/images/icons/toolbar-gen-properties_on.gif', 'A', 0, 0, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.export.image.over', '/images/icons/toolbar-rollover-properties.gif', 'A', 0, 0, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.import', 'Import MSP', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.import.alt', 'Import MSP', 'A', 0, 1, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.import.image.off', '/images/icons/toolbar-gen-create_off.gif', 'A', 0, 0, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.import.image.on', '/images/icons/toolbar-gen-create_on.gif', 'A', 0, 0, null)
/
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.import.image.over', '/images/icons/toolbar-rollover-create.gif', 'A', 0, 0, null)
/

update pn_property p set
      p.property_value = 'Import MSP xml'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.importxml'
/

update pn_property p set
      p.property_value = 'Export to MSP xml'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.exportxml'
/

update pn_property p set
      p.property_value = 'Resources'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.resource.nav.resource_management.title'
/

update pn_property p set
      p.property_value = 'New Advanced Search'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.search.results.newsearch.button.label'
/

insert into PN_PROPERTY ( CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values ( 2000, 'en', 'text', 'prm.resource.language.italian.name', 'Italian', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resources.assignor.label', 'Assignor', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldsmanager.removeactive.message', 'Are you sure you want to remove this field from the form? \r\n Removing fields from an Active form can cause loss of data.', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.project.viewproject.showteammateonlinestatus.isenabled', '0', 'A', 0, 0, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.list.search.button.tooltip', 'Search Form', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.javascript.general.schedule.taskedit.error.message', 'Error while editing task ', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.csvimport.fileupload.xlsnotsupported.message', 'Please export your spreadsheet to .CSV format, then upload the CSV file.', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.checkout.returndate.error.estreturndateinpast.message', 'Est Return date can not be past date', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.checkout.returndate.error.blank.message', 'Est Return date can not be blank', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.javascript.admin.user.disable.error.message', 'Cannot disable a DELETED User', 'A', 0, 1, null)
/

update pn_property p set
      p.property_value = 'Invalid file path or empty file.'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.importobject.invalid.file.path'
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.confirmdeleteblogentry.message','Are you sure to delete this blog entry?','','A',0,1)
/

update pn_property p set
      p.property_value = 'Message :'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.blog.addweblogentrycomment.content.label'
/

update pn_property p set
      p.property_value = 'Message :'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.blog.addweblogentry.content.label'
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','boolean','prm.resource.assignments.workcaptureonnonworkingday.isenabled','1','','A',0,1)
/

update pn_property p set
      p.property_value = '{0} is not normally a working day for {1}.'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.resource.assignments.update.error.invalidtime.message'
/

update pn_property p set 
       p.property_value = 'EDIT'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.document.documentproperties.modify.link'
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.discussion.postview.channel.links.editlink','Edit','','A',0,1)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.modifybusiness.descriptionlength.message', 'Description must be less than 1000 characters', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.create.wizard.step2.websiteurl.message', 'Enter valid website name', 'A', 0, 1, null)
/

update pn_property p set 
       p.property_value = 'Business Address Information'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.modifybusiness.channel.address.title'
/      

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.memberview.changepersonimage.errormessage', 'Other participants image cannot be modified.', 'A', 0, 1, null)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.memberview.removeimage.errormessage', 'Other participants image cannot be deleted.', 'A', 0, 1, null)
/

update pn_property p set
      p.property_value = 'Project Info'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.profile.teaminfo.header'
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.process.deletedeliverable.label', '{@all.global.toolbar.channel.remove} Deliverable', 'A', 0, 1, null)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.resource.roster.removeperson.projectspaceadmingroup.message','You can not remove the last remaining member of the workspace administrator role. The project may only be disabled.','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.resource.roster.removeperson.businessspaceadmingroup.message','You can not remove the last remaining member of the workspace administrator role. The business may only be disabled.','','A',0,1)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.javascript.verifyselection.noprojectsinlist.error.message', 'There are no projects in the list', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.personal.assignments.filter.defaultassignmenttypetask.isenabled', '1', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.personal.assignments.filter.defaultassignmenttypemeeting.isenabled', '1', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.personal.assignments.filter.defaultassignmenttypeform.isenabled', '1', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.taskassignmentpermission.message', 'You do not have permission to administrate this project.', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.clickheretoseemoreposts.message', 'Click here to see more posts.', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.addpersonalimage.title', 'Change Person Image', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.addpersonalimage.channel.personalimage.title', 'Change Person Image', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.addpersonalimage.personalimage.label', 'Person image :', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.uploadimage.submit.button.label', 'Upload image', 'A', 0, 1, null)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.global.skype.isenabled','1','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.imageinvalid.message','Invalid image selected. Please select proper image.','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.form.confirmationpage.newformrecord.label','Add new form record','','A',0,1)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.channel.templates.title', 'Business Templates', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.viewtemplates.link', 'View Templates', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.projecttemplates.description', 'View and Edit business templates.', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.main.export.label', 'Export', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.main.print.label', 'Print', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.formatnotsupported.message', 'Unsupported file format.', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.main.externaltooltip.message', 'External form access url', 'A', 0, 1, null)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.form.designer.fieldsmanager.hideineaf.button.tooltip','Hide/Unhide','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.form.designer.fieldedit.hidden.column','Hiden in EAF','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.dear','Dear ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.assignment_task_by','You have been assigned the following task in the Project.net system by ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.project_name','Project Name: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.task_name','Task Name: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.description','Description: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.none','None','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.planned_start','Planned Start: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.planned_finish','Planned Finish: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.priority','Priority: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.task_apper_on_personal_page','This task will appear on your Personal Assignments page the next time you login to Project.net. ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.questions_contact_help','If you have questions about this assignment, please contact the sender of this email.  If you have questions about Project.net, or have technical difficulties accessing this assignment, please see ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.final_step_confirming','FINAL STEP -- Confirming your email address','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.complete_click_link','The creation of your Project.net user account is almost complete. To complete this registration, please click on the following link:','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.link_does_not_work','If the above link does not work, you can also use the "Verification Code" link on Project.net login page to enter your email address and the following Verification Code directly:','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.login_page','Login Page:  ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.verification_code','Verification Code:  ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.email_address','Email Address:  ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.please_note','Please note, you will not be able to access Project.net until your registration is confirmed using this verification code.  You only have to enter this verification code once.','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.questions_contact_help','If you have questions about Project.net, or have technical difficulties with the completion of your registration please see ','','A',0,1)
/ 

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.invited_to_by','You have been invited to the following meeting by ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.name','Meeting Name: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.purpose','Meeting Purpose: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.time','Meeting Time: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.location','Meeting Location: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.inviters_comments','Inviter''s Comments: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.invitees','Invitees: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.further_details','For further details on this meeting, please login to your project.net application.  You may accept or decline this meeting when viewing the details of meeting from your Personal page.','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.contact_inviter','If you have questions about this meeting or why you have been invited to this meeting, please contact ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.questions_contact_help','If you have questions about the Project.net application, or have technical difficulties accepting, rejecting, or attending this meeting, please see ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.login.reminder','Here is the reminder of your Project.net application login name you requested.','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.login.name','    Login Name: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.login.questions_contact_help','If you have questions about Project.net, or have technical difficulties logging into Project.net, please see ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.password.complete_reset_link','To complete your Project.net password reset, please click on the following link:','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.password.link_does_not_work','If the above link does not work, you can also use the "Verification Code" link on Project.net login page and enter the following verification code directly.','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.password.login_page','    Login Page:  ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.password.questions_contact_help','If you have questions about Project.net, or have technical difficulties completing your password reset, please see ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.form.externalformnotification.data_entered_for_form','Data entered for form:  ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.info','This email was automatically sent to you based on your email notification settings in the Project.net application.  You can choose to have most notifications delivered in bulk daily or weekly.  To change you notification settings, use Setup link on your Personal Workspace page or the from a Project Workspace.  Click on this link to login:','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.questions_contact_help','For help using Project.net, please see ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.workspace','Workspace: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.default_message','Default Message: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.login_to_review','To review this item, login to the project.net application:  ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.by',' by ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.mailto','mailto:','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.path','Path: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.document_name','Document Name: ','','A',0,1)
/ 

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.description','Description: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.author','Author: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.url','URL: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.envelope_details','Envelope Details','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.envelope_name','Envelope Name: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.last_comments','Last 5 Comments','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.status','Status: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.current_step','Current Step: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.instructions','Instructions:','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.created_at_step','Created at step ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.transitioned_using','Transitioned using ','','A',0,1)
/ 

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.to_step',' to step ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.folder_name','Folder Name: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.full_path','Full Path: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.news_item','News Item  :  ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.discussion_group_name','Discussion Group Name  :  ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.charter','Charter  :  ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.task_name','Task Name  : ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.priority','Priority   : ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.status2','Status     : ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.start_date','Start Date : ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.end_date','End Date   : ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.url2','URL        : ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.form_name','Form Name: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.row_id','Row ID: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.discussion_group_name2','Discussion Group Name:  ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.posted_for','Posted For: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.posted_by','Posted By: ','','A',0,1)
/

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.role_name','Role Name: ','','A',0,1)
/

update pn_property p set
      p.property_value = 'Are you sure you want to remove this object?'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.javascript.confirmdeletion.message'
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.personalizepage.image.over', '/images/icons/project-personalize-page_over.gif', 'A', 0, 0, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.personalizepage.image.on', '/images/icons/project-personalize-page_on.gif', 'A', 0, 0, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.view.edit.descriptioninvalid.message', 'Description must be less than 250 characters.', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.setup.object.shares.deletetaskconfirmation.message', 'Are you sure to remove this shared task?', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.disable.image.on', '/images/icons/toolbar-gen-disable_on.gif', 'A', 0, 0, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.disable.image.over', '/images/icons/toolbar-rollover-disable.gif', 'A', 0, 0, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.project.process.deletedeliverable.image.over', '/images/icons/toolbar-rollover-deletedeliverable.gif', 'A', 0, 0, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.project.process.deletedeliverable.image.on', '/images/icons/toolbar-gen-deletedeliverable_on.gif', 'A', 0, 0, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.personal.assignments.myassignment.utilizationsummary.image.over', '/images/icons/toolbar-utilization-summary-icon-over.gif', 'A', 0, 0, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.personal.assignments.myassignment.utilizationsummary.image.on', '/images/icons/toolbar-utilization-summary-icon-on.gif', 'A', 0, 0, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.form.elementproperty.hiddenforeaf.label', 'Hide in EAF', 'A', 0, 1, null)
/

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldedit.requiredhidden.message', 'Required field can not be hidden', 'A', 0, 1, null)
/

update pn_property p set
      p.property_value = 'Visible in EAF'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.designer.fieldedit.hidden.column'
/

update pn_property p set 
       p.property_value = '1'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.wiki.isenabled'
/

commit
/
-- prompt 0 records loaded
-- prompt Enabling foreign key constraints for PN_PROPERTY...
alter table PN_PROPERTY enable constraint PN_PROPERTY_FK2
/
-- setfeedback on
-- setdefine on
-- prompt Done.
