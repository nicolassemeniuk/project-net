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
set feedback off
set define off
prompt Disabling foreign key constraints for PN_PROPERTY...
alter table PN_PROPERTY disable constraint PN_PROPERTY_FK2;
prompt Loading PN_PROPERTY...

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
-- please in UPDATE clause use only those columns you want to update and not all of them
-- usual column for update will be property_value where property value resides

-- START update token for openasource configuration 
/* Updating required tokens for all brands. */
/* These should not be overridden by any brand configuration without a commercial license or written agreement from Project.net, inc. */

update pn_property set property_value ='/images/logo/powered_by_pnet_150.gif' where property='prm.global.brand.logo.login'
/
update pn_property set property_value ='center' where property='prm.global.footer.alignment'
/
update pn_property set property_value ='/images/logo/powered_by_pnet_150.gif' where property='prm.global.footer.poweredby.logo'
/
update pn_property set property_value ='http://www.project.net' where property='prm.global.footer.poweredby.href'
/
update pn_property set property_value ='1' where property='prm.global.footer.copyright.newline'
/
update pn_property set property_value ='Copyright 2000-2009 Project.net, Inc.' where property='prm.global.footer.copyright'
/
update pn_property set property_value ='1' where property='prm.global.footer.copyright.isenabled'
/
update pn_property set property_value ='1' where property='prm.global.footer.copyright.href.isenabled'
/
update pn_property set property_value ='http://www.project.net' where property='prm.global.footer.copyright.href'
/

/* Updating reccommended Open Source tokens only for the Default configuration */

update pn_property set property_value ='1' where property='prm.global.poweredby.isenabled' and context_id='2000'
/
update pn_property set property_value ='http://www.project.net' where property='prm.global.brand.logo.login.href' and context_id='2000'
/
update pn_property set property_value ='0' where property='prm.global.license.create.creditcard.isenabled' and context_id='2000'
/
update pn_property set property_value ='0' where property='prm.global.license.create.defaultchargecode.isenabled' and context_id='2000'
/
update pn_property set property_value ='1' where property='prm.global.license.create.defaultlicensekey.isenabled' and context_id='2000'
/
update pn_property set property_value ='1' where property='prm.global.license.create.enteredlicensekey.isenabled' and context_id='2000'
/
update pn_property set property_value ='0' where property='prm.global.license.create.ischargecodeenabled' and context_id='2000'
/
update pn_property set property_value ='1' where property='prm.global.license.create.trial.isenabled' and context_id='2000'
/
update pn_property set property_value ='1' where property='prm.global.license.isenabled' and context_id='2000'
/
update pn_property set property_value ='0' where property='prm.global.license.login.islicenserequired' and context_id='2000'
/
update pn_property set property_value ='1' where property='prm.global.license.registration.isenabled' and context_id='2000'
/
update pn_property set property_value ='1' where property='prm.global.registration.termsofuse.isenabled' and context_id='2000'
/
update pn_property set property_value ='0' where property='prm.project.create.termsofuse.isenabled' and context_id='2000'
/
update pn_property set property_value ='Copyright Project.net and others.  Licensed under the GNU General Public License 3.0 which can be viewed here: http://www.gnu.org/licenses/gpl-3.0.html' 	where property='prm.global.legal.termsofuse' and context_id='2000'
/

-- END update token for openasource configuration 

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.main.view.option.indented.ajax.name', 'Indented', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.workingtime.list.cannotremove.message', 'You cannot remove any calendar as no default is set to replace it.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.filerequired.message', 'File to upload is a required field', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.module.history', 'Form defintion import', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.channel.selectfile.title', 'Select File to Import', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.channel.required.title', 'FIELDS IN BLACK ARE REQUIRED', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.file.label', 'File:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.main.import.label', 'Import', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.properties.currentestimatedtotalcost.currency.label','Current Estimated Cost Currency','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.properties.actualcosttodate.currency.label','Actual Cost-To-Date Currency','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.viewfilter.label','View :','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.commenton.label','Comment On','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.from.label','From','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.comments.label','comments :','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.addacomment.link','add a comment','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.edit.link','edit','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.blogit', 'BlogIt', 'A', 0, 0, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.blogit.alt', 'BlogIt', 'A', 0, 0, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.blogit.image.off', '/images/icons/toolbar-gen-blogit_off.gif', 'A', 0, 0, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.blogit.image.on', '/images/icons/toolbar-gen-blogit_on.gif', 'A', 0, 0, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.blogit.image.over', '/images/icons/toolbar-gen-blogit_over.gif', 'A', 0, 0, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.crossspace.createshare.externaltasks.warning', 'Unable to create shares for the following task(s): {0}. Sharing objects which are themselves shared from other workspaces is not permitted.', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.memberedit.chanelbartitle.label', 'Edit Participant', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.envelope.abort.confirmationmessage', 'Are you sure, abort envelope?', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.task.results.name.column', 'Name', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription1.channel.create.fields.required', 'FIELDS IN BLACK ARE REQUIRED', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.main.channel.myassignments.error.acceptedinvitationconstraint.message', 'It is required to accept a Project Invitation ({0}) before accepting a Task Assignment.', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.allspacedisplaypermission', '0', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.invalidstartdate', 'The schedule start date is not a valid date.', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.edit.editTitle', 'Editing page', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.history.historyTitle', 'History For Wiki Page', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.view.viewTitle', 'Wiki Page', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.edit.ownerid.error', 'Please Select the Owner', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.envelope.abort.label', 'Abort Envelope', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.setup.workingtimecalendar.description', 'Specify your working and non-working time.', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.setup.workingtimecalendar.link', 'Work Calendar', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'It is after this task''''s deadline of {0}.'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.list.afterdeadline.message';

update pn_property p set 
       p.property_value = 'Phases'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.nav.process';

update pn_property p set 
       p.property_value = 'New'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.create';

update pn_property p set 
       p.property_value = 'New'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.create.alt';

update pn_property p set 
       p.property_value = 'New'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.action.create';

update pn_property p set 
       p.property_value = 'New'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.action.create.alt';

update pn_property p set 
       p.property_value = 'New'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.create';

update pn_property p set 
       p.property_value = 'New'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.create.alt';

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.modify';

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.modify.alt';

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.modify';

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.modify.alt';

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.action.modify';

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.action.modify.alt';

update pn_property p set 
       p.property_value = 'Delete'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.remove';

update pn_property p set 
       p.property_value = 'Delete'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.remove.alt';

update pn_property p set 
       p.property_value = 'Delete'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.remove.alt';

update pn_property p set 
       p.property_value = 'Editing Page'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.wiki.edit.editTitle';

update pn_property p set 
       p.property_value = 'History For Wiki Page'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.wiki.history.historyTitle';

update pn_property p set 
       p.property_value = 'Admin'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.nav.applicationspace';

update pn_property p set 
       p.property_value = 'Workplan Start Date:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.schedulestartdate.label';

update pn_property p set 
       p.property_value = 'Workplan End Date:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.scheduleenddate.label';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.edit.describe.label','Edit summary (describe the changes you have made):','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.attach.image.describe.label','Select image to attach to this page:','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.menu.pageLinks.label','What Links Here','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.menu.changeHistory.label','Change History','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.menu.pageIndex.label','Index By Title','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.menu.recentChanges.label','Recent Changes','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.defaultpagesize.value', '50', 'A', 0, 1, null);

update pn_property p set 
      p.property_value = 'Primary Email:'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.profile.name.primaryemail.label';

update pn_property p set 
      p.property_value = 'Your Name and Email Information'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.profile.name.channel.info.titile';

update pn_property p set 
       p.property_value = 'Your Address Information'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.profile.address.channel.info.title';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.name.skype.label', 'Skype:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.name.skillsbio.label', 'Skills/Bio:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.setup.interfacesettings.description', 'Set your default interface settings', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.setup.manageforms.label', 'Create, edit, or delete personal forms', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Edit personal information for your profile, account, and for the directories'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.setup.personalprofile.description';
      
update pn_property p set 
       p.property_value = 'Edit or delete your private project templates'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.setup.projecttemplates.description';
      
update pn_property p set 
       p.property_value = 'Personal Setup'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.setuppage.title';
      
update pn_property p set 
       p.property_value = 'Responsible licenses setup'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.setup.managelicenses.description';
      
update pn_property p set 
       p.property_value = 'Licensing'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.setup.managelicenses.link';
      
update pn_property p set 
       p.property_value = 'Manage your subscriptions for email notifications'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.setup.notifications.description';
      

update pn_property p set 
       p.property_value = 'New Group'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.discussion.main.create.tooltip';

update pn_property p set 
       p.property_value = 'Delete Group'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.discussion.main.delete.tooltip';
      
update pn_property p set 
       p.property_value = 'Edit Group'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.discussion.main.modify.tooltip';
 
update pn_property p set 
       p.property_value = 'View Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.discussion.main.properties.tooltip';
 
update pn_property p set 
       p.property_value = 'New Subject'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.discussion.threadlist.create.tooltip'; 
 
update pn_property p set 
       p.property_value = 'New Subscription'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.managesubscriptions.create.tooltip'; 
       
update pn_property p set 
       p.property_value = 'Edit Project'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.main.modify.button.tooltip'; 
      
update pn_property p set 
       p.property_value = 'View Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.main.properties.button.tooltip'; 
      
update pn_property p set 
       p.property_value = 'New Task'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.create.tooltip'; 
      
update pn_property p set 
       p.property_value = 'New Task'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.gantt.createtask.tooltip';
      
update pn_property p set 
       p.property_value = 'Restore Item'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.main.undodelete.button.tooltip';

update pn_property p set 
       p.property_value = 'Update % Complete'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.percentage';

update pn_property p set 
       p.property_value = 'Recalculate'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.recalculate';

update pn_property p set 
       p.property_value = 'Next Post'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.action.next_post';

update pn_property p set 
       p.property_value = 'Manage Forms'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.formsedit.link';

update pn_property p set 
       p.property_value = '1'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.poweredby.isenabled';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.name.validskillsbio.message', 'Please do not enter more than 300 characters for Skills/Bio.', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.name.validskillsbio.cannothtmltag.message', 'Skills/Bio text could not contain HTML tags', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.personalimageupload.selectimagetoupload.label','Select image to upload:','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.personalimageupload.imagetypenotsupported.message','This file type is not supported. \n Please select the image file of type .gif, .png, .jpeg, .bmp.','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.personalimageupload.selectimagetoupload.message','Please select an image to upload.','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.currentlynotworking.message','Currently not working on any project.','','A',0,1);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.otherprojects.textname', 'No Business Owner', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.externalformaccess.isenabled', '1', 'A', 1, 0, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.definitionedit.externalaccess.label', 'Allow external access', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.personalimageupload.uploadbutton.caption','Upload','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.personalimageupload.cancelbutton.caption','Cancel','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.confirmremoveimage.message','This will remove your current picture. \n Do you want to continue?.','','A',0,1);

update pn_property p set 
       p.PROPERTY_VALUE_CLOB = 'Copyright 2000-2009 Project.net, Inc.'       
where 
      p.context_id = 2000 and
      p.language = 'en' and 
      p.property = 'prm.global.footer.copyright';
      
update pn_property p set 
       p.property_value = '1'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.help.external.isenabled';
         
update pn_property p set 
       p.property_value = '1'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.help.helpdesk.external.isenabled';

update pn_property p set 
       p.property_value = 'http://doc.project.net/9_0:{0}_{1}'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.help.helpdesk.pagesection.external.href';     
      
update pn_property p set 
       p.property_value = 'http://doc.project.net/9_0:{0}'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.help.helpdesk.page.external.href';                 

update pn_property p set 
       p.property_value = 'http://doc.project.net/9_0:{0}'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.help.page.external.href'; 
      
update pn_property p set 
       p.property_value = 'http://doc.project.net/9_0:{0}_{1}'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.help.pagesection.external.href'; 
      
      
update pn_property p set 
       p.property_value = 'Percent Complete has to be a positive number less than or equal to 100.'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.resource.updatework.error.pencentcomplete.range.message'; 

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','boolean','prm.versioncheck.isenabled','1','','A',1,0);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.versioncheck.message','There is a newer version of Project.net available; select Utilities > Build Info menu for more information.','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.versioncheck.disabled.message','Check for newer version is disabled; select Utilities > Build Info menu for more information.','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.versioncheck.enabled.message','Check for newer version is enabled; select the Utilities > Build Info menu for more information.','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.versioncheck.message.newversion','Project.net X.X.X is available.','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.versioncheck.notavailable.message','Project.net version is unavailable. Next check in {@prm.versioncheck.check.period.in.hours} hours.','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.versioncheck.disable.label','Disable version check.','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.versioncheck.enable.label','Enable version check.','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.versioncheck.disabled.label','Version check is disabled.','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.versioncheck.privacy.policy','Application Metrics submitted to Project.net, Inc. during version check.<br/> Refer to <a href="http://www.project.net/privacy_policy.htm">privacy policy.</a>','','A',0,1);
update pn_property p set 
       p.property_value = 'There is a newer version of Project.net available; select Utilities > Build Info menu for more information.'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.versioncheck.message'; 
update pn_property p set 
       p.property_value = 'Disable version check.'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.versioncheck.disable.label';      
update pn_property p set 
       p.property_value = 'Enable version check.'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.versioncheck.enable.label';        
update pn_property p set 
       p.property_value = 'Application Metrics submitted to Project.net, Inc. during version check.<br/> Refer to <a target="_blank" href="http://www.project.net/privacy_policy.htm">privacy policy.</a>'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.versioncheck.privacy.policy';
      
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.application.nav.space.personal','Personal','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.application.nav.space.business','Business','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.application.nav.space.projects','Projects','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.application.nav.space.enterprise','Enterprise','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.application.nav.space.resource','Resources','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.personalizepage.title', 'Personalize', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.tokens.title', 'Tokens', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.nav.blog.title', 'Blog', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.main.remove.button.tooltip', 'Remove Document', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.main.new.button.tooltip', 'New Form', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.main.edit.button.tooltip', 'Edit Form', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.main.delete.button.tooltip', 'Delete Form', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.main.copy.button.tooltip', 'Copy Form', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.global.actions.icon.isenabled', '1', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.eaf.creatoremail.label', 'Email:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.email.validate.wrongformat.message', 'Wrong email address format', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.confirmationpage.thanksforsubmiting.message', 'Thank you for submiting form', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.confirmationpage.formname.label', 'Form name:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.confirmationpage.recordnumber.label', 'Created form record number:', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Personalize Page'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.personalizepage.link'; 

update pn_property p set 
       p.property_value = 'Edit Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.bookmarkproperties.modify.button.tooltip'; 

update pn_property p set 
       p.property_value = 'Edit Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.containerproperties.modify.button.tooltip'; 

update pn_property p set 
       p.property_value = 'Edit Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.directory.modify.button.tooltip'; 

update pn_property p set 
       p.property_value = 'Edit Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.main.modify.button.tooltip'; 

update pn_property p set 
       p.property_value = 'Edit Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.tabs.modify.button.tooltip'; 

update pn_property p set 
       p.property_value = 'Edit Properties'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.viewsubscription.modify.tooltip'; 

update pn_property p set 
       p.property_value = 'Login:'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.login.loginname.label';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.workingtime.editdate.description.invaliddate.message', 'Description must be less than 500 characters.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.personal.myassignments.blogit.agileworkremaining.isenabled', '0', 'A', 0, 0, null);

update pn_property p set 
       p.property_value = 'by'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.blog.viewblog.entrypostedby.label';

update pn_property p set
       p.property_value = 'New comment'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.blog.viewblog.addacomment.link';

update pn_property p set
       p.property_value = 'Edit'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.blog.viewblog.edit.link';

update pn_property p set
       p.property_value = 'Load Design'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.designer.main.import.label';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.blog.header','Blog','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.blogview.label','Blog View','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.projectblog.header','Project Blog','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.myprofile.link','My Profile','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.timesheet.timeworked.label', 'Time Worked:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.timesheet.viewexistingtimesheet.message', 'View Existing Time Sheet Entries.', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.timesheet.timeworked.header', 'Time Worked', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.taskhistory.historynotfound.message', 'History Not Found', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.taskhistory.historyof.label', 'History of: ', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.changeofestimatedtimetofinish.header', 'Change Of Estimated Time To Finish', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.baselineplan.header', 'Baseline Plan', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.adjustment.header', 'Adjustment', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.percentcomplete.label', '% Complete', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.estimatedremaining.label', 'Estimated Remaining', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.submitbutton.caption', 'Submit', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.cancelbutton.caption', 'Cancel', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.changepercentcomplete.reasonforchange.header', 'Reason for Change (required)', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.hidecomment.link', 'Hide Comments', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.project.label', 'Project:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.email.label', 'Email:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.skype.label', 'Skype:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.phone.label', 'Phone:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.mobile.label', 'Mobile:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.projectmanager.label', 'Project Manager:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.profile.link', 'Profile', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.resourceautomaticallyadded.message', '* Resource will be automatically added to the Team Members role.', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.doubleclickonprojectnametoviewdetails.message', 'Double click on project name to view details.', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.doubleclickonbusinessnametoviewdetails.message', 'Double click on business name to view details.', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.selectrole.label', 'Select Role*:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.applybutton.caption', 'Apply', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.directory.cancelbutton.caption', 'Cancel', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workday.goto.lable', 'Go To', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workweekmodify.workweek.link', 'Work Week ', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workweekmodify.basedondefaultcalendar.label', '(Based on Default Calendar)', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workdate.nodatesspecified.label', 'No dates specified', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.myassignment.utilizationsummary.link', 'Utilization Summary', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workday.individualdates.header', 'Individual Dates', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workweekmodify.addanother.lable', 'Add Another', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workweekmodify.workdate.header', 'Work Date', 'A', 0, 1, null);


update pn_property p set
       p.property_value = 'New Business'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.businessportfolio.create.button.tooltip';	  

update pn_property p set
       p.property_value = 'Edit Business'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.editbusiness.label';

update pn_property p set
       p.property_value = 'New Project'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.project.portfolio.create.tooltip';

update pn_property p set
       p.property_value = 'Edit Task'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.modify.tooltip';

update pn_property p set
       p.property_value = 'Notify'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.notify.tooltip';

update pn_property p set
       p.property_value = 'Share Task'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.share';

update pn_property p set
       p.property_value = 'Add External Task'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.addexternal';

update pn_property p set
       p.property_value = 'Assign Task to Phase'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.phase';

update pn_property p set
       p.property_value = 'Move Task Up'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.taskup';

update pn_property p set
       p.property_value = 'Move Task Down'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.taskdown';

update pn_property p set
       p.property_value = 'Unindent Task'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.taskleft';

update pn_property p set
       p.property_value = 'Indent Task'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.taskright';

update pn_property p set
       p.property_value = 'Assign Resource'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.schedule.resources';

update pn_property p set
       p.property_value = 'New {@prm.project.process.phase.label}'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.process.phase.create.button.label';

update pn_property p set
       p.property_value = 'Business List'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.businessportfolio.title';

update pn_property p set
       p.property_value = 'Edit Profile'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.profile.module.history';

update pn_property p set
       p.property_value = 'My {@prm.global.tool.dashboard.name}'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.nav.dashboard';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.mainpage.form.title', 'My {@prm.global.tool.form.name}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.mainpage.personal.title', 'My {@prm.global.tool.document.name}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.mainpage.personal.title', 'My {@prm.project.nav.calendar}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.managesubscriptions.edit.tooltip', 'Edit Subscription', 'A', 0, 1, null);
	  
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.managesubscriptions.delete.tooltip', 'Delete Subscription', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.main.perdelete.button.tooltip', 'Permanently Delete Item', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.main.create.button.tooltip', 'New Template', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.main.delete.button.tooltip', 'Delete Template', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.create.button.tooltip', 'New Workflow', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.edit.button.tooltip', 'Edit Workflow', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.delete.button.tooltip', 'Delete Workflow', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.view.button.tooltip', 'View Properties', 'A', 0, 1, null);


insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.main.create.button.tooltip', 'New Project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.main.delete.button.tooltip', 'Delete Project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.setup.object.shares.delete.tootip', 'Remove Share', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.document.toolbar.link.tooltip', 'Link Document', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.main.externalurl.message', 'External access url', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.definitionedit.externalurl.label', 'External access url', 'A', 0, 1, null);


insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.membertitle.column', 'Member Title', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.stastus.onlinepresence.label', 'Online Presence:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.stastus.onlinestatus.label', 'Online', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.stastus.Offlinestatus.label', 'Offline', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.currenttime.label', 'Current Time:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.timezone.label', 'Time zone:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.recentactivity.header', 'Recent Activity', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.lastlogin.label', 'Last Login:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.lastblogentry.label', 'Last Blog Entry:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.noblog.message', 'No Blogs', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.profileview.header','Profile','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.myblog.link','My Blog','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.editprofileinfo.link','Edit Profile','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.uploadpicture.link','Upload Picture','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.removepicture.link','Remove Picture','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.contactinfo.header','Contact Info','','A',0,1);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.profile.teaminfo.header','Project Membership','','A',0,1);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.upload.addimage.link', 'Add Image', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.upload.cancel.link', 'Cancel', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.upload.selectfile.label', 'File:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.upload.filedescription.label', 'File Description (optional):', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.edit.edithelplink.label', 'Editing help', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.edit.edithelplink.link', 'http://doc.project.net/User_Guide', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.menu.createNewWikiPage.label', 'Create Page', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.view.whatlinkshere.nolinksheremsg', 'There are no wiki pages that link to this one!', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.view.whatlinkshere.labelmsg', 'What links here:', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.menu.linkstoexistingpages.label', 'Links On This Page', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.view.linkstoexistingpages.nolinksheremsg', 'This page has no inner wiki links to existing wiki pages.', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.view.linkstoexistingpages.labelmsg', 'Links to existing wiki pages:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.index.noentriesmsg.labelmsg', 'This wiki does not contain any pages!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.index.wikipageindex.headermsg', 'This is an alphabetical list of pages you can read on this wiki space.', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.index.wikirecentchangesindex.headermsg', 'This is a list of recently changed wiki pages in this wiki space.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.innerwikiprefix.wiki', 'http://doc.project.net/', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.innerwikiprefix.help', 'http://doc.project.net/User_Guide#', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.innerwikiprefix.trac', 'http://dev.project.net/trac/', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.innerwikiprefix.wikipedia', 'http://en.wikipedia.org/wiki/', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.baselineenddate.column', 'Baseline End Date', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.baselinestartdate.column', 'Baseline Start Date', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.baselinework.column', 'Baseline Work', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.baselineduration.column', 'Baseline Duration', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.startvariance.column', 'Start Variance', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.endvariance.column', 'Finish Variance', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.workvariance.column', 'Work Variance', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.durationvariance.column', 'Duration Variance', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.statusnotifiers.column', 'Status Notifiers', 'A', 0, 1, null);

update pn_property p set
      p.property_value = 'Blog-it'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.standard.blogit';
		
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.list.create.button.tooltip', 'New Record', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.list.modify.button.tooltip', 'Edit Record', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.list.remove.button.tooltip', 'Remove Record', 'A', 0, 1, null);	  
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldsmanager.create.button.tooltip', 'New Field', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldsmanager.remove.button.tooltip', 'Remove Field', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldsmanager.modify.button.tooltip', 'Edit Field', 'A', 0, 1, null);	  

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.listsmanager.create.button.tooltip', 'New List', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.listsmanager.remove.button.tooltip', 'Remove List', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.listsmanager.modify.button.tooltip', 'Edit List', 'A', 0, 1, null);	  	  
	  
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.export', 'Export MSP', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.export.alt', 'Export MSP', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.export.image.off', '/images/icons/toolbar-gen-properties_off.gif', 'A', 0, 0, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.export.image.on', '/images/icons/toolbar-gen-properties_on.gif', 'A', 0, 0, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.export.image.over', '/images/icons/toolbar-rollover-properties.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.import', 'Import MSP', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.import.alt', 'Import MSP', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.import.image.off', '/images/icons/toolbar-gen-create_off.gif', 'A', 0, 0, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.import.image.on', '/images/icons/toolbar-gen-create_on.gif', 'A', 0, 0, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.import.image.over', '/images/icons/toolbar-rollover-create.gif', 'A', 0, 0, null);

update pn_property p set
      p.property_value = 'Import MSP xml'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.importxml';

update pn_property p set
      p.property_value = 'Export to MSP xml'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.exportxml';

update pn_property p set
      p.property_value = 'Resources'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.resource.nav.resource_management.title';

update pn_property p set
      p.property_value = 'New Advanced Search'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.search.results.newsearch.button.label';

insert into PN_PROPERTY ( CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values ( 2000, 'en', 'text', 'prm.resource.language.italian.name', 'Italian', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resources.assignor.label', 'Assignor', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldsmanager.removeactive.message', 'Are you sure you want to remove this field from the form? \r\n Removing fields from an Active form can cause loss of data.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.project.viewproject.showteammateonlinestatus.isenabled', '0', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.list.search.button.tooltip', 'Search Form', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.javascript.general.schedule.taskedit.error.message', 'Error while editing task ', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.csvimport.fileupload.xlsnotsupported.message', 'Please export your spreadsheet to .CSV format, then upload the CSV file.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.checkout.returndate.error.estreturndateinpast.message', 'Est Return date can not be past date', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.checkout.returndate.error.blank.message', 'Est Return date can not be blank', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.javascript.admin.user.disable.error.message', 'Cannot disable a DELETED User', 'A', 0, 1, null);

update pn_property p set
      p.property_value = 'Invalid file path or empty file.'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.importobject.invalid.file.path';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.viewblog.confirmdeleteblogentry.message','Are you sure to delete this blog entry?','','A',0,1);

update pn_property p set
      p.property_value = 'Message :'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.blog.addweblogentrycomment.content.label';

update pn_property p set
      p.property_value = 'Message :'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.blog.addweblogentry.content.label';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','boolean','prm.resource.assignments.workcaptureonnonworkingday.isenabled','1','','A',0,1);

update pn_property p set
      p.property_value = '{0} is not normally a working day for {1}.'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.resource.assignments.update.error.invalidtime.message';

update pn_property p set 
       p.property_value = 'EDIT'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.document.documentproperties.modify.link';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.discussion.postview.channel.links.editlink','Edit','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.modifybusiness.descriptionlength.message', 'Description must be less than 1000 characters', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.create.wizard.step2.websiteurl.message', 'Enter valid website name', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Business Address Information',
       p.property_value_clob = null,
       p.property_type = 'text'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.modifybusiness.channel.address.title';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.memberview.changepersonimage.errormessage', 'Other participants image cannot be modified.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.memberview.removeimage.errormessage', 'Other participants image cannot be deleted.', 'A', 0, 1, null);

update pn_property p set
      p.property_value = 'Project Info'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.profile.teaminfo.header';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.process.deletedeliverable.label', '{@all.global.toolbar.channel.remove} Deliverable', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.resource.roster.removeperson.projectspaceadmingroup.message','You can not remove the last remaining member of the workspace administrator role. The project may only be disabled.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.resource.roster.removeperson.businessspaceadmingroup.message','You can not remove the last remaining member of the workspace administrator role. The business may only be disabled.','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.javascript.verifyselection.noprojectsinlist.error.message', 'There are no projects in the list', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.personal.assignments.filter.defaultassignmenttypetask.isenabled', '1', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.personal.assignments.filter.defaultassignmenttypemeeting.isenabled', '1', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.personal.assignments.filter.defaultassignmenttypeform.isenabled', '1', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.taskassignmentpermission.message', 'You do not have permission to administrate this project.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.clickheretoseemoreposts.message', 'Click here to see more posts.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.addpersonalimage.title', 'Change Person Image', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.addpersonalimage.channel.personalimage.title', 'Change Person Image', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.addpersonalimage.personalimage.label', 'Person image :', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.uploadimage.submit.button.label', 'Upload image', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.global.skype.isenabled', '1', 'A', 1, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.personal.imageinvalid.message','Invalid image selected. Please select proper image.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.form.confirmationpage.newformrecord.label','Add new form record','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.channel.templates.title', 'Business Templates', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.viewtemplates.link', 'View Templates', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.projecttemplates.description', 'View and Edit business templates.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.main.export.label', 'Export', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.main.print.label', 'Print', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.formatnotsupported.message', 'Unsupported file format.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.main.externaltooltip.message', 'External form access url', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.form.designer.fieldsmanager.hideineaf.button.tooltip','Hide/Unhide','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.form.designer.fieldedit.hidden.column','Hiden in EAF','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.dear','Dear ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.assignment_task_by','You have been assigned the following task in the Project.net system by ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.project_name','Project Name: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.task_name','Task Name: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.description','Description: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.none','None','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.planned_start','Planned Start: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.planned_finish','Planned Finish: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.priority','Priority: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.task_apper_on_personal_page','This task will appear on your Personal Assignments page the next time you login to Project.net. ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.assignment.questions_contact_help','If you have questions about this assignment, please contact the sender of this email.  If you have questions about Project.net, or have technical difficulties accessing this assignment, please see ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.final_step_confirming','FINAL STEP -- Confirming your email address','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.complete_click_link','The creation of your Project.net user account is almost complete. To complete this registration, please click on the following link:','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.link_does_not_work','If the above link does not work, you can also use the "Verification Code" link on Project.net login page to enter your email address and the following Verification Code directly:','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.login_page','Login Page:  ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.verification_code','Verification Code:  ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.email_address','Email Address:  ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.please_note','Please note, you will not be able to access Project.net until your registration is confirmed using this verification code.  You only have to enter this verification code once.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.regver.questions_contact_help','If you have questions about Project.net, or have technical difficulties with the completion of your registration please see ','','A',0,1); 

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.invited_to_by','You have been invited to the following meeting by ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.name','Meeting Name: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.purpose','Meeting Purpose: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.time','Meeting Time: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.location','Meeting Location: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.inviters_comments','Inviter''s Comments: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.invitees','Invitees: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.further_details','For further details on this meeting, please login to your project.net application.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.contact_inviter','If you have questions about this meeting or why you have been invited to this meeting, please contact ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.calendar.meeting.questions_contact_help','If you have questions about the Project.net application, or have technical difficulties accepting, rejecting, or attending this meeting, please see ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.login.reminder','Here is the reminder of your Project.net application login name you requested.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.login.name','    Login Name: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.login.questions_contact_help','If you have questions about Project.net, or have technical difficulties logging into Project.net, please see ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.password.complete_reset_link','To complete your Project.net password reset, please click on the following link:','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.password.link_does_not_work','If the above link does not work, you can also use the "Verification Code" link on Project.net login page and enter the following verification code directly.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.password.login_page','    Login Page:  ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.registration.forgotten.password.questions_contact_help','If you have questions about Project.net, or have technical difficulties completing your password reset, please see ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.form.externalformnotification.data_entered_for_form','Data entered for form:  ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.info','This email was automatically sent to you based on your email notification settings in the Project.net application.  You can choose to have most notifications delivered in bulk daily or weekly.  To change you notification settings, use Setup link on your Personal Workspace page or the from a Project Workspace.  Click on this link to login:','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.questions_contact_help','For help using Project.net, please see ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.workspace','Workspace: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.default_message','Default Message: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.login_to_review','To review this item, login to the project.net application:  ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.by',' by ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.mailto','mailto:','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.path','Path: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.document_name','Document Name: ','','A',0,1); 

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.description','Description: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.author','Author: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.url','URL: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.envelope_details','Envelope Details','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.envelope_name','Envelope Name: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.last_comments','Last 5 Comments','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.status','Status: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.current_step','Current Step: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.instructions','Instructions:','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.created_at_step','Created at step ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.transitioned_using','Transitioned using ','','A',0,1); 

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.to_step',' to step ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.folder_name','Folder Name: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.full_path','Full Path: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.news_item','News Item  :  ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.discussion_group_name','Discussion Group Name  :  ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.charter','Charter  :  ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.task_name','Task Name  : ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.priority','Priority   : ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.status2','Status     : ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.start_date','Start Date : ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.end_date','End Date   : ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.url2','URL        : ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.form_name','Form Name: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.row_id','Row ID: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.discussion_group_name2','Discussion Group Name:  ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.posted_for','Posted For: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.posted_by','Posted By: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.delivery.role_name','Role Name: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.registered_space_invite.invited_by','You have been invited by ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.registered_space_invite.participate_workspace',' to participate on the following workspace hosted on the Project.net application:','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.registered_space_invite.workspace_name','Workspace Name: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.registered_space_invite.description','Description: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.registered_space_invite.responsibilities','Responsibilities: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.registered_space_invite.message','Message: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.registered_space_invite.workspace_visit_click','This workspace will be listed on your Personal page the next time you login to Project.net.  To visit the workspace, click on the link below:','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.registered_space_invite.questions_contact_help','If you have questions about this workspace, please contact the sender of this email.  If you have questions about Project.net, or have technical difficulties with this invitation please see ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.participant_delete.removed_from','You have been removed from the following ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.participant_delete.workspace_within_application',' workspace within the Project.net application:','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.participant_delete.workspace_name','Workspace Name: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.participant_delete.removed_by','Removed By: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.participant_delete.no_access_workspace','You may no longer be able to access this workspace.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.participant_delete.questions_about_delete','If you have any questions about this ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.participant_delete.contact_sender',', please contact the sender of this email.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.participant_delete.questions_contact_help','If you have any questions about Project.net, or have technical difficulties , please see ','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.blog.notification.isenabled', '1', 'A', 1, 0, null);

update pn_property p set
      p.property_value = 'Are you sure you want to remove this object?'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.javascript.confirmdeletion.message';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.personalizepage.image.over', '/images/icons/project-personalize-page_over.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.personalizepage.image.on', '/images/icons/project-personalize-page_on.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.view.edit.descriptioninvalid.message', 'Description must be less than 250 characters.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.setup.object.shares.deletetaskconfirmation.message', 'Are you sure to remove this shared task?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.disable.image.on', '/images/icons/toolbar-gen-disable_on.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.disable.image.over', '/images/icons/toolbar-rollover-disable.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.project.process.deletedeliverable.image.over', '/images/icons/toolbar-rollover-deletedeliverable.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.project.process.deletedeliverable.image.on', '/images/icons/toolbar-gen-deletedeliverable_on.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.personal.assignments.myassignment.utilizationsummary.image.over', '/images/icons/toolbar-utilization-summary-icon-over.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.personal.assignments.myassignment.utilizationsummary.image.on', '/images/icons/toolbar-utilization-summary-icon-on.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.form.elementproperty.hiddenforeaf.label', 'Hide in EAF', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldedit.requiredhidden.message', 'Required field can not be hidden', 'A', 0, 1, null);

update pn_property p set
      p.property_value = 'Visible in EAF'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.designer.fieldedit.hidden.column';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formedit.error.moreworkcomplete.message', 'The assigned form has more work complete than work.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.step.link.newstep', 'New Step', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.step.link.editstep', 'Edit Step', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.step.link.deletestep', 'Delete Step', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.transition.link.newtransition', 'New Transition', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.transition.link.edittransition', 'Edit Transition', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.transition.link.deletetransition', 'Delete Transition', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directorypage.roster.column.lastblogit', 'Last Blog-it', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.main.modified.confirm.message', 'Schedule has modified entries. Do you wish to continue without submitting the changes?', 'A', 0, 1, null);

update pn_property p set
      p.property_value = 'Add another form record'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.confirmationpage.newformrecord.label';

update pn_property p set
      p.property_value = 'The form has been submitted and a copy sent to your email address.'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.confirmationpage.thanksforsubmiting.message';	  
	  
update pn_property p set
      p.property_value = 'Submitter email (a copy will be sent to this address)'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.eaf.creatoremail.label';	  	  
	  
update pn_property p set
      p.property_value = 'Allow external access (EAF)'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.designer.definitionedit.externalaccess.label';	  	 

update pn_property p set 
       p.property_value = '1'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.wiki.isenabled';

update pn_property p set 
       p.property_value = '1'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.blog.isenabled';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldedit.menuchoice.message', 'Choice values should be defined for this field.', 'A', 0, 1, null);


update pn_property p set
       p.property_value = 'Save Design'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.designer.main.export.label';

update pn_property p set
       p.property_value = 'Import Records'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.form.list.import.label';

update pn_property p set
       p.property_value = 'Export Records'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.form.list.export.label';	  
	  
update pn_property p set
       p.property_value = 'Print Records'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.designer.main.print.label';	  	  
	  
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.report.outputtype.xls.name', 'MS Excel (XLS)', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.view.edit.duplicatecolumnorder.message', 'The duplicate column order number is not allowed', 'A', 0, 1, null);

update pn_property p set
      p.property_value = 'Are you sure you want to remove this object?'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.javascript.confirmdeletion.message';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.failed.validation','Failed security validation' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.not.permissions.failed','You have not been granted permission to perform the requested action on that item.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.standard.label','Standard' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.secure.ssl.label','Secure (SSL)' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.browser.not.supported.label','Your browser is not supported by this application.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.browser.only.supported.label','We only support the following browsers:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.netscape.navigator.6.or.higher.label','Netscape Navigator 6.0 or Higher' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.expl.navigator.5.5.or.higher.label','Microsoft Internet Explorer 5.5 or higher' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.read.browser.requirements.label','Read the browser requirements' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.read.browser.requirements.label.second.part','for this application.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.current.browser.information.label','Your current browser information:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.agent.label','Agent:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.browser.name.label','Browser Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.app.version.label','App Version:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.return.label','Return' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.comming.soon.message','This feature will be coming soon' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.comming.soon.label','Coming Soon' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.bad.browser.label','Bad Browser' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.cookies.required.enable.message','Cookies are required to be turned on to use our site.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.welcome.label','Welcome' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.click.enter.application.alt','Click here to enter application' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.available.commercial.release','This option is available in commercial release.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.initializing.error.application.message','Error initializing application.  Properties could not be loaded.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.initializing.error.application.message2','Error initializing application.  No page was requested.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.unavailable.label','Unavailable' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.error.description.label','Error Description' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.reproduction.steps.label','Reproduction Steps' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.bug.report.label','Bug Report' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.instructions.label','Thank you for helping us to improve our product.  Please fill in the description below of what you were doing in the application when the error was thrown.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.click.here.report.label','Click here to hide the contents of the report' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrros.username.label','User Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.current.date.label','Current Date:' ,'A', 0, 1, null);

--insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.name.label','Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.user.id.label','User ID:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.browser.info.label','Browser Info:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.server.name.label','Server Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.current.space.id.label','Current Space ID:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.current.space.label','Current Space:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrros.email.label','Email:' ,'A', 0, 1, null);

--insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.stacktrace.label','Stack Trace:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.current.path.info.label','Current Path Info:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.user.agent.unavailable.label','User agent unavailable' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.base.reporterrors.server.name.unavailable.label','Server name unavailable' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.location.label','Location:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.description.label','Description:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.user.label','User:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.reproduction.label','Reproduction:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.error.recorder.for.analysis.label','This error has been recorded for analysis by our support team.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.main.return.page.label','Please press the return button to go back to the previous page.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.document.path.to.repository.label','Path to Repository Root' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.document.path.exists.label','Path Exists' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.document.path.writeable.label','Path is Writeable' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.document.is.active.label','Is Active' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.document.failed.security.violation.label','Failed security validation: This functionality is only available to application administrators.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.keyword.label','Keyword:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.main.message.label','Clicking a letter will search only the users first name and last name.  Entering a keyword will search the users first name, last name and email address (including alternates).' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.user.status.label','User Status:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.domain.name.label','Domain Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.description.label','Description:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.directory.label','Directory' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.authentication.provider.label','Authentication Provider:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.supported.configurations.label','Supported Configurations:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.required.verification.label','Require verification of email address after registration?' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.check.message.label','Check this option to ensure that the email address specified by a user during registration actually belongs to that user.  Only uncheck this option when the email address is automatically specified by the directory provider during registration and cannot be changed by the user until after they have logged in.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.allow.purchase.label','Allow users to purchase licenses via credit card?' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.registration.instructions.label','Registration Instructions:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.instructions.1.label','Enter the instructions that will be displayed when a user registers and is asked to choose a domain.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.instructions.2.label','The instructions should indicate the circumstances under which a user would select this domain.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.instructions.3.label','The instructions may include references to token values.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.instructions.4.label','For example, to refer to a Submit button:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.invalid.missing.action.label','Invalid or missing action in DomainEditProcessing.jsp' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.any.users.configurations.label','Do not add support for the target domain to any configurations. Any users who log in under these configurations will NOT be asked to migrate.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.domain.any.users.configurations.2.label','Add support for the target domain to the selected configurations: Any users who log in under the selected configurations will be asked to migrate.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.form.workspace.type.label','WorkSpace Type :' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.form.all.label','All' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.project.label','Project' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.business.label','Business' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.personal.label','Personal' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.methodology.label','Methodology' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.forms.footer.label','Form(s) copied successfully.  A copied form is not available until it is activated.  To activate the form(s) visit the Forms Designer in the target space.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.forms.include.license.label','License:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.forms.include.domains.label','Domain(s):' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.invoice.search.label','Invoice Search' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.invoice.pay.info.label','Pay Info:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.invoice.first.last.name.label','First/Last Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.invoice.license.key.label','License Key:' ,'A', 0, 1, null);

--insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.invoice.search.label','Search' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.enter.email.address.label','Enter User Email Address :' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.send.notification.label','Send Notification' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.create.label','Create License' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.create.preview.label','Create License - Preview' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.error.license.message','Error - This license is a "node-locked" license, but the product installation id to which it is locked differs from the product installation id of this system.<br> This license cannot be used in this system.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.node.id.label','License Node ID:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.current.system.label','Current system Node ID:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.license.not.being.installed.label','This license has not been installed yet. Please review the license properties listed below and click finish to install the license.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.create.license.results.label','Create License Results' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.created.succesfulyy.label','You have successfully installed this block license. You may want make a note of the license key.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.key.label','Generated License Key:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.block.license.message','The block license you were trying to install is already installed in your system.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.contact.project.net.license.message','Please contact Project.net for a new block license.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.view.license.properties.label','View license properties' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.update.license.properties.label','Update license properties' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.create.license.properties.label','Create new license properties' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.install.new.label','Install a new license' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.create.new.label','Create a new license' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.purchase.label','Purchase licenses' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.update.master.properties.label','Update Master Properties - Preview' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.warining.new.product.message','Warning - New Product installation ID is different from current product installation id. Installing these properties will invalidate all licenses created with the current product installation id.<br> This might include <b>ALL</b> licenses.<br> Any users using these licenses will be required to select alternate licenses at login. Click Cancel to avoid updating the properties' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.warining.new.product.message2','Warning - This system does not have previously installed master properties but has one or more existing licenses. These licenses have a chance of becoming invalid if these were created with a different installation id.<br>This might include <b>ALL</b> licenses.<br>Any users using these licenses will be required to select alternate licenses at login.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.enter.properties.label','Enter Properties:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.title.reason.code.js','Please enter a title for this reason code.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.enter.message.reason.code','Please enter some message for this reason code.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.message.must.be.1000.characters.js','Message must be less than 1000 characters' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.disable.license.js','Are you sure you want to disable this license?\nNo users associated with this license will be able to log in until it is re-enabled.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.licese.cancel.license.js','Are you sure you want to cancel this license? Cancelled license can not be re-enabled.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.enable.license.js','Are you sure you want to enable this license?\nAll users associated with this license would be able to log in.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.disable.or.cancel.message','Once you disable or cancel a license you will no longer be able to associate users with it.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.new.status.label','New Status' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.enabled.label','Enabled :' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.allow.associated.login.message','Allows all users associated with this license to login.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.disabled.label','Disabled :' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.prevents.users.associated.message','Temporarily prevents all users associated with this license from logging in.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.cancelled.label','Cancelled :' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.users.associated.message','Forces all users associated with this license to choose another license the next time they log on.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.reason.code.label','Reason Code' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.select.reason.code.label','Select from existing reason codes :' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.create.new.reason.code.label','Create a new reason code' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.title.label','title :' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.message.label','Message :' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.question.js','This query will return ALL users of the domain(s) with the specified user status.  Continue?' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.search.users.label','Search Users' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.license.clicking.letter.will.search.message','Clicking a letter will search only the users first name and last name. Entering a keyword will search the users first name, last name and email address (including alternates).' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.business.label','Businesses' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.letter.workspace.message','Clicking a letter or entering the keyword will search for occurences inside the workspace name.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.workspace.label','WorkSpace :' ,'A', 0, 1, null);

--insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.business.label','Business' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.project.label','Project' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.status.label','Status :' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.active.label','Active' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.disabled.label','Disabled' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.filter.results.label','Filter Results (number of matches):' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.disable.project.question.js','Are you sure you want to disable this project?' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.activate.project.question.js','Are you sure you want to activate these projects ?' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.disable.all.project.question.js','Are you sure you want to disable these projects ?' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.projects.label','Projects' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.portfolio.message2.label','Clicking a letter or entering the keyword will search for occurences inside the workspace name.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.username.required.js','Username is required.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.update.profile.js','Do you want to update the profile?' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.login.and.password.label','Your login name and password' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.username.message','The username listed is the name of the user in the LDAP directory. This username should only be changed if the username in LDAP has changed.  Otherwise, changing this username will leave the user unable to login.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.current.login.name','Current Login Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.assign.license.label','Assign License' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.problem.message','There was a problem associating person with the specified license. The person is already associated to the specified license.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.unhandled.missing.action.message','Unhandled or missing action in LicenseUpdaterProcessing.jsp' ,'A', 0, 1, null);

--insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.login.and.password.label','Login name and password' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.may.change.login.name.label','You may change the login name, password and jog question/answer.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.current.login.name.label','Current Login Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.change.login.name.label','Change Login Name' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.new.login.name.label','New Login Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.change.password.label','Change Password' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.new.password.label','New Password:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.retype.new.password.label','Retype New Password:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.change.job.question.or.answer.label','Change Jog Question or Answer' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.job.question.message','The Jog Question is the question the user will be asked if they forget their password.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.jog.question.label','Jog Question:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.jog.answer.label','Jog Answer:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.update.profile.question.js','Do you want to update the profile' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.length.login.js','Your New Login must be between 4 and 16 characters' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.lenght.login.2.js','Your New Login can only be lowercase letters and numbers' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.lenght.password.js','Your New Password must be between 6 and 16 characters' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.lenght.password.and.confirmation.js','Password and confirmation password must match.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.must.enter.question.js','You must enter a jog question' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.must.enter.answer.js','You must enter a jog answer' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.failed.security.validation.message','Failed security validation: This functionality is only available to application administrators.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.missing.parameter.message','Missing parameter in ProfileName.jsp' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.address.information.label','Address Information' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.address.line1.label','Address - line 1:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.address.line2.label','Address - line 2:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.city.label','City:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.state.province.label','State/Province:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.select.state.label','Select State' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.zip.postal.code.label','Zip or Postal Code:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.country.label','Country:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.select.country.label','Select Country' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.work.phone.number.label','Work Phone Number:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.work.fax.number.label','Work FAX Number:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.mobile.phone.number.label','Mobile Phone Number:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.pager.number.label','Pager Number:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.pager.email.label','Pager Email:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.time.zone.label','Time Zone:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.license.information.label','License Information' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.person.currenlty.license.label','This person does not currently have a license.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.no.previuos.licenses.found.label','No previous licenses found.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.license.history.label','License History' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.missing.parameter.label','Missing parameter in ProfileLicense.jsp' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.select.option.label','Please select an option' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.update.profile.label','Do you want to update this profile' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.provide.information.label','We will not provide this information to third parties unless you request it.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.see.our.strict.label','See our strict' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.privacy.policy.label','Privacy Policy' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.fileds.required.label','FIELDS IN BLACK ARE REQUIRED.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.name.email.information.label','Name and Email Information' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.name.prefix.label','Name Prefix:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.mr.ms.label','(Mr., Ms., Dr., etc.)' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.first.name.label','First Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.middle.name.label','Middle Name/Initial:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.last.name.label','Last Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.name.suffix.label','Name Suffix:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.jr.mba.label','(Jr., MBA, AIA, etc.)' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.display.name.label','Display Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.how.user.appear.to.other.label','(How the users name will appear to others.)' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.email.address.label','Email Address:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.alternative.email1.label','Alternate Email 1:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.alternative.email2.label','Alternate Email 2:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.skype.label','Skype:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.profile.skills.bio.label','Skills/Bio:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.settings.system.label','System Settings' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.settings.note.label','Note:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.settings.message.label','All application servers must be restarted for changes to take effect.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.settings.requires.restart.label','Requires restart. One or more settings are different from the currently loaded values.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.settings.current.value.label','Current value:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.settings.use.default.value.label','Use the default value' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.settings.specify.value.label','Specify a value' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.settings.warning.label','Warning: Changing this setting to an illegal value could prevent your ability to login.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.settings.changing.alt','Changing this setting to an illegal value could prevent your ability to login' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.settings.example.values.label','Example values:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.status.edit.system.label','Edit System Status Information' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.status.message.title.label','Message Title :' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.status.set.active.label','Set as Active  :' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.status.message.text.label','Message Text :' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.utilities.invalidate.state.message','Invalide State when attempting to apply template.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.utilities.missing.action.message','Missing action in ApplyMethodologyProcessing.jsp.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.utilities.build.information.label','Build Information' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.utilities.new.application.version.label','New Application Version:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.utilities.application.version.label','Application Version:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.utilities.database.version.label','Database Version:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.utilities.build.date.label','Build Date:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.utilities.currently.jdbc.driver.label','Currently unavailable with this JDBC driver' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.utilities.message.label','This page lists of the handlers used internally by Project.net to serve pages.This is of primary use to OEM customers who have access to source code and that are doing deep modification to the navigation of Project.net.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.utililies.search.for.class.label','Search for Class (fully referenced)' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.utililies.class.name.label','Class Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.workspace.move.project.label','Move Projects to Another Business' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.workspace.delete.business.label','Delete Business and selected Projects' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.workspace.select.following.label','Select one of the following' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.description.label','Description:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.admin.responsabilities.label','Responsibilities:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.base.message','If this window does not close automatically, please click the X button on the title bar.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.base.closing.message','Closing window...' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.add.token.label','Add Token' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.token.name.label','Token Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.default.en.value.label','Default (en) Value:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.property.type.label','Property Type:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.value.label','Value:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.is.tranlatable.label','Is Translatable:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.true.label','True' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.false.label','False' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.brand.settings.label','Brand Settings' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.brand.label','Brand:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.language.label','Language:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.reload.token.cache.label','Reload Token Cache (all sessions!)' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.filter.tokens.label','Filter Tokens' ,'A', 0, 1, null);

--insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.token.name.label','Token Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.token.value.label','Token Value:' ,'A', 0, 1, null);

--insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.property.type.label','Property Type:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.all.label','All' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.category.label','Category:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.show.tokens.label','Show Tokens:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.first.100.tokens.label','First 100 tokens' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.first.200.tokens.label','First 200 tokens' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.first.500.tokens.label','First 500 tokens' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.first.1000.tokens.label','First 1000 tokens' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.all.tokens.label','All Tokens' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.edit.tokens.label','Edit Tokens' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.brand.name.label','Brand Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.brand.abbreviation.label','Brand Abbreviation:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.brand.description.label','Brand Description:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.default.languages.label','Default Language:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.supported.languages.label','Supported Languages:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.supported.hostnames.label','Supported Hostnames (CSV):' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.conf.name.label','Configuration Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.description.label','Description:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.abbreviaton.label','Abbreviation:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.default.language.label','Default Language:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.supported.language.label','Supported Languages:' ,'A', 0, 1, null);

--insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.supported.hostnames.label','Supported Hostnames:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.configuration.name.label','Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.contact.delete.question.js','Are you sure you want to DELETE this form?' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.contact.module.not.available.workspace.label','The Contacts Module is not available for this workspace.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.contact.view.label','View:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.form.designer.can.delete.label','You can not delete all the fields of a list that is already defined or activated.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.form.designer.missing.parameter.label','Missing parameter id in FormCopyProcessing.jsp' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.form.designer.feature.not.implemented.label','Feature not implemented yet' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.notification.label','Run the Subscription Scheduler' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.notification.temporary.run.subscription.label','TEMPORARY:  run the subscription agent.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.notification.temporary.run.postman.agent.label','TEMPORARY: Run the Postman Agent' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.notification.temporary.postman.agent.label','Run the Postman Agent' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.include.blog.entries.label','Blog entries for task:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.include.blog.comments.label','comments:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.include.blog.add.comment.label','add a comment' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.include.blog.edit.label','edit' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.login.name.and.password.label','Your login name and password' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.message1','Your login name and password are stored in your LDAP entry.They may only be changed in the LDAP directory.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.current.login.name.label','Current Login Name:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.update.profile.label','Do you want to update your profile' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.blog.label','Blog' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.wiki.label','Wiki' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.links.label','Links' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.assignments.label','Assignments' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.new.entry.label','new entry' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.personal.application.admin.label','Application Administartor should not migrate Domains' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.project.project.label','Project:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.project.description.label','Description:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.project.manager.label','Manager:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.project.status.label','Status:' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.project.completion.label','Completion' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.registration.ldap.label','Forgotten LDAP Login Name or Password' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.registration.ldap.message','This application does not maintain your LDAP password.Please contact your LDAP Administrator.' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.resource.include.add.activity.alt','Add Activity' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.resource.include.add.task.alt','Add Task' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.roster.change.personal.image.label','Change Personal Image' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) values (2000, 'en', 'text', 'prm.project.roster.remove.image.label','Remove Image' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.project.viewproject.latetasks.link','Late Tasks' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.project.viewproject.taskcomingduethisweek.link','Tasks Coming Due This Week' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.project.viewproject.taskscompleted.link','Tasks Completed' ,'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.project.viewproject.unassignedtasks.link','Unassigned Tasks' ,'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'All invitees were added to the directory but there was a problem sending email notifications.'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.invite.memberinformation.emailsendingerror.message';

update pn_property p set 
       p.property_value = '0'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.resource.directory.isenabled';

/* Turn off form sharing */
update pn_property p set 
       p.property_value = '0'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.form.sharing.isenabled';

update pn_property p set 
       p.property_value = 'Create, edit or delete personal forms'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.personal.setup.manageforms.label';

update pn_property p set 
       p.property_value = 'View, restore or permanently delete items from your personal workspace'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.personal.setup.trashcan.view.label';

update pn_property p set 
       p.property_value = 'Notifications'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.notification.link';

update pn_property p set
	   p.property_type = 'text',
       p.property_value = 'Manage your subscriptions for email notifications for this business'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.notification.label';

update pn_property p set 
       p.property_value = 'Business Properties'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.editbusiness.link';

update pn_property p set 
       p.property_value = 'Edit business properties, including type, address, etc.'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.editbusiness.label';

update pn_property p set 
       p.property_value = 'Add or remove, people and roles in this business'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.directory.label';

update pn_property p set 
       p.property_value = 'Create business templates'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.methodology.label';

update pn_property p set 
       p.property_value = 'Manage Workflows'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.workflow.link';

update pn_property p set 
       p.property_value = 'Create, edit or delete workflows for this business'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.workflow.label';

update pn_property p set 
       p.property_value = 'Create, edit or delete forms for this business'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.formsedit.label';

update pn_property p set 
       p.property_value = 'View, restore or permanently delete items for this business'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.trashcan.view.label';

update pn_property p set 
       p.property_value = 'Manage your subscriptions for email notifications for this project'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.managepersonalnotifications.label';

update pn_property p set 
       p.property_value = 'Project Properties'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.projectinformation.link';

update pn_property p set 
       p.property_value = 'Edit project properties, including status, dates, etc.'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.projectedit.label';

update pn_property p set 
       p.property_value = 'Manage Workflows'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.workflows.link';

update pn_property p set 
       p.property_value = 'Create, edit or delete workflows for this project'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.workflowsedit.label';

update pn_property p set 
       p.property_value = 'Create, edit or delete forms for this project'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.channel.formsedit.label';

update pn_property p set 
       p.property_value = 'Sharing'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.shares.link';

update pn_property p set 
       p.property_value = 'View, restore or permanently delete items for this project'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.trashcan.view.label';

update pn_property p set
      p.property_value = 'Delete Record'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.list.remove.button.tooltip';

update pn_property p set 
       p.property_value = 'Edit Notification'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.viewsubscription.modify.tooltip'; 

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'notification.viewsubscription.remove.link', 'Delete Notification', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.discussion.threadlist.delete.link', 'Delete Post', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.modify.link', 'Edit Task', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.business.main.modify.link','Edit Business' ,'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Tasks due this week'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.project.viewproject.taskcomingduethisweek.link';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.process.deletedeliverable.confirmmsg', 'Are you sure to delete this Deliverable', 'A', 0, 1, null);
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.process.deletegate.label', 'Delete Gate', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.process.deletegate.comfirmmsg', 'Are you sure to delete this Gate', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.process.nodeliverableinlist.error.message', 'There are no deliverable in the list', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.user.search.type.option.wiki.value', 'Wiki', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.wiki.results.channel.title', 'Wiki', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.wiki.simple.title', 'Simple Wiki Search:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.wiki.simple.namedescription.label', 'Page Name or Contents', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.wiki.advanced.title', ' Advanced Wiki Search:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.wiki.advanced.pagename.label', 'Page Name:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.wiki.advanced.contents.label', 'Contents:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.wiki.results.pagename.column', 'Page Name', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.user.search.type.option.blogs.value', 'Blogs', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.blog.results.channel.title', 'Blog', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.blog.simple.title', 'Simple Blog Search:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.blog.simple.namedescription.label', 'Subject or Message', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.blog.advanced.title', ' Advanced Blog Search:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.blog.advanced.subject.label', 'Subject:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.blog.advanced.message.label', 'Message:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.blog.results.subject.column', 'Subject', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.blog.results.message.column', 'Message', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.blog.results.nosubject.message', '...No Subject...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.extconfirm.title', 'Confirm', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.meetingagendalist.deleteagenda.confirm', 'Delete this agenda item?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.meetingattendeelist.deleteattendee.confirm', 'Delete this attendee?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.post.deleteselectedpost.confirm', 'Are you sure you want to delete the post selected?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.removeconfiguration.confirm', 'Remove this Configuration?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.admin.createinvoice.createnewinvoice.confirm', 'Are you sure you want to create a new invoice?  NOTE:  By doing this a new invoice will be created and you will immediately be billed.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.admin.ledgerview.loadall.confirm', 'You have not set any filters! This will load all entries in the ledger.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.admin.viewinvoicelist.loadallinvoice.confirm', 'You are about to load all invoices in the system! Continue?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.admin.businessportfolio.showallbusiness.confirm', 'This query will return ALL Businesses with the specified status.  Continue?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.admin.profileaddress.updateprofile.confirm', 'Do you want to update this profile?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.admin.setting.main.resetsetting.confirm', 'Reset all settings to default values?\nNote: A restart will still be required for default values to take effect.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.admin.status.main.removestatus.confirm','Remove this status?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.admin.userlist.showalluser.confirm','This query will return ALL users of the domain(s) with the specified user status.  Continue?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.admin.userlist.deleteuser.confirm','Are you sure you want to permanently DELETE this user?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.admin.userlist.disableuser.confirm','Are you sure you want to DISABLE this user? \n Note: Disabled user will not be able to register again.', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Please select checkbox'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.security.group.creategroupwizard.validateinherit.message';

update pn_property p set 
       p.property_value = 'Invite Member'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.directory.create.button.tooltip';

update pn_property p set 
       p.property_value = 'Remove Member'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.directory.remove.button.tooltip';

update pn_property p set 
       p.property_value = 'Edit Member Properties'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.memberview.modify.button.tooltip';

update pn_property p set 
       p.property_value = 'Edit Member Properties'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.memberedit.chanelbartitle.label';

update pn_property p set 
       p.property_value = 'Invite Member'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.invite.memberaddition.channel.inviteparticipant.title';

update pn_property p set 
       p.property_value = 'Edit Role'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.grouplistview.modify.tooltip';

update pn_property p set 
       p.property_value = 'New Role'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.grouplistview.create.tooltip';

update pn_property p set 
       p.property_value = 'Delete Role'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.grouplistview.remove.tooltip';
      
update pn_property p set 
       p.property_value = 'Project members only'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.visibility.projectparticipants.name';

update pn_property p set 
       p.property_value = 'Owning business members'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.visibility.owningbusinessparticipants.name';

update pn_property p set 
       p.property_value = 'Are you sure you want to delete this role?'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.grouplistview.remove.message';
      
update pn_property p set 
       p.property_value = 'Delete Member'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.directory.remove.button.tooltip';

update pn_property p set 
       p.property_value = 'Are you sure you want to delete this person from this directory?'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.directory.userremovalwarning.message';

update pn_property p set 
       p.property_value = 'Invite Member - Member Information'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.invite.memberinformation.channel.inviteparticipants.title';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.newpage.invalidpagename.message', 'Invalid page name.', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.newpage.blankpagename.message', 'You must provide a wiki page name', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.newpage.pageexistconfirm.message', 'A page exists with same name. Do you want to view it?', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.main.verifyname.nohtmlcontent.message', 'No html contents allowed in task name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.process.deletephase.label','Delete Phase', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'You may only select tasks which are next to each other.'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.javascript.verifyadjoiningselection.error.message';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.search.advanced.daterange.invalid.message', 'After date can not be greater than before date', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.modified.norecords.message', 'There are no modified tasks to save.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.application.domainmigration.wizardpage4.finish.confirm', 'This will generate migration records for ALL Active users of the domain.  Continue?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.admin.portfolio.search.projectfilter.confirm', 'This query will return ALL Projects with the specified status.  Continue?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.calendar.meeting.deletemeeting.confirm','Do you want to delete this meeting?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.event.remove.confirm.message', 'Do you want to delete this event?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.admin.profileaddress.confirm','Do you want to update this profile?', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.javascript.admin.invoice.search.error.message', 'You have not set any filters! This will load all entries in the ledger.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.javascript.admin.license.modifystatus.error.message', 'Select new status', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.javascript.admin.license.reasoncode.error.message', 'Select reason code', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.sharedtask.message', 'Schedule contains one or more shared tasks, hence the schedule dates cannot be shifted.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.deletedfrom.edit.error.message', 'You can not edit deleted form.', 'A', 0, 1, null);


insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.ungistered_space_invite.to_create_new_account','TO CREATE A NEW USER ACCOUNT','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.ungistered_space_invite.access_new_space','To access this workspace, you must first create a user account.  To register as a new user of the Project.net application, please click on the following link:','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.ungistered_space_invite.access_from_personal','To access this workspace after you create your account, simply login to the Project.net application and follow the link to this workspace from your Personal page.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.ungistered_space_invite.note_about_email','IMPORTANT NOTES ABOUT WHICH EMAIL ADDRESS TO USE','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.ungistered_space_invite.use_same_email','* When creating your new project.net user account, you *must* use the same email address that this message was sent to.  Otherwise, you will not be granted access to the workspace.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.ungistered_space_invite.reply_if_different','* If you already have a Project.net user account with a different email address, please reply to sender of this email with your appropriate email address to use when inviting you this workspace.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.ungistered_space_invite.invite_to_other_email','* If choose to register with a different email address, please notify the sender of this email so they can invite you using that email address.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.ungistered_space_invite.email_for_notifications','* The project.net application sends various system-defined and user-defined email notifications to the email address you enter when creating a user account.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.ungistered_space_invite.alter_email_later','* You can change your email address later as well as define several alternate email addresses after you create your user account.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.domain_migration.complete_process','You have sucessfully completed the Project.net domain migration process which resulted in following changes.  Please remember to use your new domain name and user name the next time you login.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.domain_migration.new_domain_name','New Domain Name: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.domain_migration.user_name','User Name: ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.domain_migration.help','If you face any difficulties as a result of this Domain Migration Process, please see ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.notification.roster.domain_migration.contact_admin',' or contact your Project.net application administrator.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.form.designer.fieldedit.numberofdigits.message','The number of decimal digits field should be an integer number','','A',0,1);
  
update pn_property p set 
       p.property_value = 'Message should not be blank'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.blog.addweblogentry.validation.message';

update pn_property p set 
       p.property_value = 'Message should not be blank'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.blog.addweblogentrycomment.validation.message';
      
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.dashboard.overassigned.title','Over Assigned','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.dashboard.assigned.title','Assigned','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.dashboard.close.title','Close','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.dashboard.up.title','Up','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.dashboard.down.title','Down','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.workplan.invalidmaximumhours.error.message','Actual hours can not be more than 66 years.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.form.designer.definitionedit.hideassignmentfield.label','Hide Assignment Fields in External Forms','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.resource.module.description','Resources','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.module.description','Blog','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.registration.forgottenpasswordwizard.emailmustbevalid.message','Your email must be a valid address. yourname@domain.com','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.schedule.authentication.modify.error','You do not have permission to modify the task {0}.','','A',0,1);

update pn_property p set 
       p.property_value = 'You do not have permission to modify the task {0}.'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.authentication.modify.error';

update pn_property p set 
       p.property_value = 'Allow External Access (EAF):'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.designer.definitionedit.externalaccess.label';

update pn_property p set 
       p.property_value = 'Include Discussions:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.designer.definitionedit.includediscussions.label';	  
	  
update pn_property p set 
       p.property_value = 'Include Document Storage:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.designer.definitionedit.includedocuments.label';	  	  
	  
update pn_property p set 
       p.property_value = 'Include Assignment Fields:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.designer.definitionedit.includeassignments.label';
	  
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.form.designer.definitionedit.showassignmentfield.label','Show Assignment Fields in EAF:','','A',0,1);	  

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.baselineedit.descriptionsize.message', 'Baseline description cannot be more than 1000 characters.', 'A', 0, 1, null);
	  
update pn_property p set 
       p.property_value = 'Dashboard'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.projectspace.module.description';
      
update pn_property p set 
       p.property_value = 'Documents'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.module.description';
      
update pn_property p set 
       p.property_value = 'Discussions'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.discussion.module.description';      
      
update pn_property p set 
       p.property_value = 'Forms'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.module.description'; 
      
update pn_property p set 
       p.property_value = 'Phases'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.process.module.description'; 
      
update pn_property p set 
       p.property_value = 'Workplan'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.module.description';    
update pn_property p set 
       p.property_value = 'Dashboard '
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.businessspace.module.description';       
update pn_property p set 
       p.property_value = 'History'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.assignment.assignmenthistory.label';

update pn_property p set 
       p.property_value = 'Current'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.assignment.currentassignment.label';	  

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.resource.prereleasefunctionality.label.isenabled', '0', 'A', 0, 0, null);

update pn_property p set 
       p.property_value = 'You have made changes on this page, would you like to ignore the changes?'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.resource.allocatebyresource.ignorethechanges.message';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invite.memberinformation.messagefield.invalid.message', 'Message field must be less than 250 characters.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.main.limit.workhour.message', 'Enter work hour within limit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.modify.logoempty.error.message', 'Logo is Empty', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.view.manageviews.removelink.exception', 'Unknown or Deleted view ID:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldedit.numericfieldslength.message', 'Total number of digit should not be more than 38 for this field type.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.directory.directory.tab.assignments.isenabled', '0', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.calendar.meetingedit.descriptionlength.message','Description must be less than 500 characters.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.template.create.businessname','Business Name','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.javascript.document.verifyform.description.error.message', 'The description must be less than 500 characters', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Description must be less than 1000 characters'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.process.modifygate.descriptionsize.message';

update pn_property p set 
       p.property_value = 'Description must be less than 500 characters'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.importobject.description.maxlength';

update pn_property p set 
       p.property_value = 'Enter valid website name(eg: http://www.project.net)'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.create.wizard.step2.websiteurl.message';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.personalimageupload.invalidimagepath.message', 'Invalid file path!', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.icalendar.invalid.key.message','Invalid iCalendar Public Secure Key','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.javascript.validatefieldseletion.noenoughquantity.error.message', 'You may not delete the last remaining form field.  A form must have at least one field view defined.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.csvimport.fileupload.invalidfilepath.message', 'Invalid file path!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.formdefintionimport.fileupload.invalidfilepath.message', 'Invalid file path!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.address.mobilephone.invalid.error.message', 'Mobile number must be a valid number.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.address.pagerphone.invalid.error.message', 'Pager phone number must be a valid number.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.address.faxphone.invalid.error.message', 'Fax number must be a valid number.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.address.email.invalid.error.message', 'Enter valid pager email.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.view.edit.budgetcostinvalid.message', 'Budgeted Total Cost Value must be numeric.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.memberview.noimagetoremove.errormessage', 'No image to remove', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Phases'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.methodology.nav.process'; 

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.modifycontainer.folderdescriptionvalidation.message', 'Description must be less than 500 characters', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.meetings.edit.datestring.error.starttimecannotafterendtime.message', 'The specified meeting start time can not be after meeting end time.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.eventedit.eventtime.error.starttimecannotafterendtime.message', 'The specified event start time can not be after event end time.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.view.edit.minusnotallowed.message', 'Column order can not be negetive.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.nomembersadddirectorysearchresults.validationwarning.message', 'There are no persons to select.', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = '/images/icons/channelbar-submit.gif'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.channel.submit.image.on';
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.pageurl.title', 'Page URL', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.showtitlesonly.link', 'Show Titles Only', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.hidepictures.link', 'Hide Pictures', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.showfullentries.link', 'Show Full Entries', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.showpictures.link', 'Show Pictures', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.list.create.page.title', 'New Record', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.showtitles.image.on', '/images/icons/toolbar-show-titles-on.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.showtitles.image.over', '/images/icons/toolbar-show-titles-over.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.myprofile.image.over', '/images/icons/toolbar-myprofile-over_.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.myprofile.image.on', '/images/icons/toolbar-myprofile-on.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.showpicture.image.on', '/images/icons/toolbar-show-picture-on.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.showpicture.image.over', '/images/icons/toolbar-show-picture-over.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.wikipageurl.image.on', '/images/icons/toolbar-pers-wiki_pageurl_on.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.wikipageurl.image.over', '/images/icons/toolbar-rollover-wiki_pageurl.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.uploadpicture.image.over', '/images/icons/toolbar-rollover-upload-image.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.uploadpicture.image.on', '/images/icons/toolbar-upload-image-on.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.wiki.image.on', '/images/icons/toolbar-gen-wiki_on.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.wiki.image.over', '/images/icons/toolbar-rollover-wiki.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.project.dashboard.news.postedby', 'Posted by', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.report.channel.sorting.errormessage', 'Sorting options is conflicted!', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Assignments: In Progress, Late, Starting in 2 Days'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.personal.main.channel.currentassignments.title';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.main.taskup.invalid.message', 'You cannot move the selected task up.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.envelope.abort.message', 'Abort Envelope', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.form.view.list.numberfield.align','center','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.main.accessdenied.toquickadd.message', 'You do not have permission to perform action - Create.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.main.modified.extended.confirm.message', 'Are you sure you want to navigate away from this page?\n\nSchedule has modified entries. Do you wish to continue without submitting the changes?\n\nPress OK to continue, or Cancel to stay on the current page.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.eventedit.facilityrequired.maxlength', 'Facility Description must be less than 500 characters.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.meeting.itemdescription.maxlength', 'Item Description must be less than 500 characters.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.channelbarbutton.title.minimize', 'Minimize', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.channelbarbutton.title.maximize', 'Maximize', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.channelbarbutton.title.close', 'Close', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.assignments.current.column.project', 'Project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.dashboard.setupanewmeeting.link', 'Set up a meeting', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.dashboard.addnewdocument.link', 'Go to Documents', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.dashboard.createnews.link', 'Create a News Item', 'A', 0, 1, null);

update pn_property p set
       p.property_value = 'Business Members'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.main.channel.teammembers.title';
      
update pn_property p set 
       p.property_value = 'Completion'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.project.portfolio.completionpercentage.column';
      
update pn_property p set 
       p.property_value = 'Documents modified within last 7 days'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.main.channel.documentsmodified.title';
      
update pn_property p set 
       p.property_value = 'Forms modified within last 10 days'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.global.main.channel.formsmodified.title.value';
      
update pn_property p set
       p.property_value = 'View Task Properties'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.properties.tooltip';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.main.xsl.checkedoutbyme', 'Checked out by me', 'A', 0, 1, null);

update pn_property p set
       p.property_value = 'Check-In Comments is a required field'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.document.checkin.checkincommentsvalidation.message';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.showtitlesonly.image.off', '/images/icons/toolbar-show-titles-off.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.hidepictures.image.off', '/images/icons/toolbar-show-picture-off.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.modify.logoempty.error.message', 'Logo is Empty', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.discussion.threadlist.reply.link', 'New Reply', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Previous Post'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'all.global.toolbar.action.previous_post';

update pn_property p set 
       p.property_value = 'Document Folder'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.main.page.history';	  	  
	  

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.sessiontimeout.sessionexpired.alert.message', 'Your current {0} session is about to expire. For your security {0} session automatically ends after {1} of inactivity. This greatly reduces the chances that someone else will access your information if you forgot to logout.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.sessiontimeout.sessionexpired.timedout.message', 'Your current {0} session has expired and you have been logged out. For your security, {0} session automatically ends after {1} of inactivity.This greatly reduces the chances that someone else will access your information if you forgot to logout.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.sessiontimeout.sessionexpired.title.message', 'Your session has expired', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.sessiontimeout.areyoustillthere.confirm.message', 'Are you still there?', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.sessiontimeout.stayonthispage.confirm.message', 'Close this dialog to remain on this page', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.sessiontimeout.currentsessionexpire.confirm.message', 'Your current {0} session will expire in ', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.sessiontimeout.currentsessionexpire.minute.confirm.message', 'Minutes ', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.sessiontimeout.currentsession.continue.option.message', 'Continue with my current {0} session', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.sessiontimeout.currentsession.logout.option.message', 'Log out now and end my {0} session', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.sessiontimeout.afterexpiredpopup.expiredat.showtime.message', 'Your {0} session expired at', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.sessiontimeout.afterexpiredpopup.login.message', 'Log in to start a new {0} session', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.totalworkdonebymemberson.message', '{0} Reported {1} hours work completed, during {2} to {3}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.addweblogentry.explainwhyimportant.message', 'Explain why this entry is important', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.discussion.threadlist.info.link', 'View Properties', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.timeentry.title', 'Time Entry :', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.day.title', 'day', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.week.title', 'week', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.totalreported.title', 'Total <br/>Reported', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.history.title', 'History', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.estimatedremaining.title', 'Estimated <br/>Remaining', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.totalestimated.title', 'Total <br/>Estimated', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.complete.title', 'Complete', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.done.option.title', 'Done', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.estimatesnotaccurate.title', 'Estimates Not Accurate?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.estimatesnotaccurateexplain.message', 'Your message should explain why <br/> you changed the estimates.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.versioncheck.check.period.in.hours', '24', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.workingtime.createpersonalresourcecalendar.note.label', 'Note: Create personal resource calendars in {@prm.application.nav.space.personal} / {@prm.global.tool.dashboard.name} / {@prm.personal.nav.setup} / {@prm.personal.setup.workingtimecalendar.link}', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.workcapturedinfofortask.message', '{0} has captured {1} work on {2} for {3}.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.worknotcapturedinfofortask.message', '{0} has not captured any work on {1} for {2}.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.workcapturedinfoforallassignments.message', '{0} has captured total {1} work for all assignments including {2} on {3}.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.worknotcapturedinfoforallassignments.message', '{0} has not captured any work for any assignment including {1} on {2}.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.blogit.defaulttab.recentblogstab', '0', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.news.link.title', 'Create a News Item', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.phase.link.title', 'Create New Phase', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.subproject.link.title', 'Create New Sub Project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.subproject.create.link', 'Create a new subproject', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.setupmeeting.link.title', 'Set Up a Meeting', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.setup.interfacesettings.channel.pagesize.errormessage', 'Maximum page size must be valid number', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.setup.interfacesettings.channel.pagesize.numbererrormessage', 'Maximum page size should not be less than 50', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.setup.interfacesettings.channel.emptypagesize.errormessage', 'Maximum page size should not be blank', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.setup.interfacesettings.channel.showpagesize.title', 'Show', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.setup.interfacesettings.channel.showtaskperpage.title', 'tasks per page', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.setup.interfacesettings.channel.pagesize.title', 'Maximum page size:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.main.noitemsinlist.errormessage', 'No items in list', 'A', 1, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.upload.imagealreadyexists.message', 'The file with the same name is already attached to this wiki page! <br/>If you continue you will update that file with new one! ', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.upload.notsupportedfiletype.message', 'Not supported file type! Please, upload image files only! ', 'A', 0, 1, null);

update pn_property p set
       p.property_value = 'The task has been already deleted.'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.error.message.noscheduleentry';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.myassignment.taskassignwindow.error.noprojectortaskavailable', 'No any project or task available to create new assignment.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.pagenotexist.message', 'Wiki page does not exist. Would you like to', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.createit.link', 'create it', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.preview.previewTitle', 'Preview Wiki Page', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.oldrevisionfound.message', 'This is an old revision of this page as edited by {0}. It may differ significantly from the ', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.revisionpage.currentversion.link', 'current version', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.revisionpage.activeRevision.link', 'active', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.revisionpage.makethisrevisionactive.message', 'Make this revision ', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.oldrevisionnotfound.message', 'Sorry this revision not found for this page ', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.revisionpage.seecurrentversion.link', 'See current version', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.historypage.column.version', 'Version', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.historypage.column.author', 'Author', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.historypage.column.comment', 'Comment', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.historypage.column.date', 'Date', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.historypage.latestversion', '(Latest Version)', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.historypage.latestVersionCommentTextempty', 'no comment', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.wiki.editpage.setwikipageaccesspermissions.isenabled', '0', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.allprojectmembers', 'All project members', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.allauthenticatedusers', 'All authenticated users', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.allunauthenticated', 'Everyone [All unauthenticated]', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.changepermission', 'Change', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.hidepermission', 'Hide', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.accesspermission', 'Access Permission :', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.wikicontentblank.errormessage', 'Wiki page content should not be blank.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.menu.searchwiki.option', 'Search Wiki:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.menu.toolbox.menutitle', 'Toolbox', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.menu.editpage.option', 'Edit Page', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.menu.wiki.menutitle', 'Wiki', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.menu.toppage.option', 'Top Page', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.menu.uploadimage.option', 'Upload Image', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.left.projectmanager.title', 'Project Manager:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.uploaddiv.uploadimage.title', 'Upload Image', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.bottom.editpage.link', 'Edit Page', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.bottom.editinghelp.link', 'Editing Help', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.left.createpage.link', 'Create Page', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.left.deletepage.link', 'Delete Page', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.left.indexbytitle.link', 'Index By Title', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.left.recentchanges.link', 'Recent Changes', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.edit.save.button', 'Save', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.edit.preview.button', 'Preview', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.upload.addimage.button', 'Add Image', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.upload.cancel.button', 'Cancel', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.invalidwikipagetitle.message', 'The requested page title was invalid, empty, or an incorrectly linked inter-language or inter-wiki title.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.index.all.label', 'All', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.revisionpage.currentversion.bottomlink', 'Current Version', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.revisionpage.activerevision.bottomlink', 'Activate Revision', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.deletedsamepagename.message', 'Page with the same name is deleted! Do you want to', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.undelete.link', 'undelete', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.or.lable', 'it or', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.createnew.link', 'create new ', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.wiki.showattachmentsonrootpage.isenabled', '0', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.undefinedoperation.message', 'Undefined operation performed', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.wiki.rightalignedtopcontentblock.isenabled', '1', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.deleteimage.link', 'Delete Image', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.pageindex.lasteditor.label', 'Last Editor:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.pageindex.editedon.label', 'Edited on', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.pageindex.editcomment.label', 'Description:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.left.allimages.link', 'All Images', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.objecttype.description', 'Wiki', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.wbs.column', 'WBS', 'A', 0, 1, null);
 
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.template.currentspacename', 'Pn_Current_Space_Name', 'A', 1, 0, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.template.projectstartdate', 'Pn_Project_Start_Date', 'A', 1, 0, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.template.projectschedulelatetasks', 'Pn_Project_Schedule_Late_Tasks', 'A', 1, 0, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.template.projectscheduleunassignedtasks', 'Pn_Project_Schedule_Unassigned_Tasks', 'A', 1, 0, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.template.projectscheduletasksduethisweek', 'Pn_Project_Schedule_Tasks_Due_This_Week', 'A', 1, 0, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.configuration.template.projectschedulecompletedtasks', 'Pn_Project_Schedule_Completed_Tasks', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.index.noimageentriesmsg.labelmsg', 'This wiki does not contain any images!', 'A', 0, 1, null);
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.wiki.edit.wikipagename.label','Edit wiki page name:','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.index.wikipageindex.allimages.headermsg', 'This is an alphabetical list of all images you can see on this wiki space.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.subtitle', 'Invite New Members to', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.email.title', 'Or invite member by e-mail address:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.email.message', 'Enter the name and e-mail address of a person you want to invite', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.participant.number', 'Current Members of', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.participant.totalnumber', 'Total Members:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.participant.invited', 'Invited:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.sendemail.toallinvities', 'Send E-mail To All Invitees', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.sendemail.additioncomments', 'Send an Additional Comment', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.addroles.nextpage', 'You can add roles on next screen', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directorypage.roster.column.teammember', 'Team Member', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directorypage.roster.column.titles', 'Titles', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.directory.label', 'Business Directory:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.name.label', 'Search:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.assignments.update.blogitsubject.label', 'Subject:', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.assignments.update.blogitmessage.label', 'Message:', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.assignments.update.timesheet', 'Timesheet', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.nav.timesheet', 'Timesheet', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.personal.timesheet.isenabled', '1', 'A', 1, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.resource.timesheet.weeklytotalcolumn.isenabled', '1', 'A', 1, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.resource.timesheet.projecttotalcolumn.isenabled', '1', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directorypage.newinvitee.defaultrole.title', 'New Invitees Default Roles', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directorypage.newinvitee.defaultrole.subtitle', 'Change default roles and add Title and Responsiblities to new invitees', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.activity.isenabled', '1', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.nav.activity.title', 'Activity', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.wiki', 'Wiki page', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.task', 'Task', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.news', 'News item', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.formdata', 'Form data', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.formdata.form', 'in form', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.form', 'Form', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.document', 'Document', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.blogentry', 'Blog entry about', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.blogentryby', 'by', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.blogcomment', 'Blog comment to', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.blogcomment.blogentry', 'blog entry about', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.blogcommentby', 'by', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.noactivityfound.message', 'No activities found.', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.title', 'Activity', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.toolbox.heading', 'Toolbox', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.recentactivity', 'Recent Activity', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.entriesfrom.message', 'Entries from <b>{0}</b> till <b>{1}</b>', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.show', 'Show', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.daysperpage', 'days per page', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.filters', 'Filters', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.clearall', 'Clear All Marks', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.teammembers', 'Team Members :', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.showactivityon', 'Show Activity on :', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.undefinedaction.message', 'Undefined schedule action', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.wiki', 'Wiki', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.wiki.alt', 'Wiki', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.exportpdf', 'Export Gantt PDF', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.exportpdf.alt', 'Export Gantt PDF', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.importuser.csvcolumn.name', 'Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.importuser.csvcolumn.emailaddress', 'EmailAddress', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.importuser.title.name', 'Import Members', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.importuser.filesupported.format', 'Unable to import selected file, please check file type and selected file. Supported files(xml,csv and vcf)', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.importuser.filesupported.type', 'File Type', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.importuser.filesupported.xmltype', 'xml', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.importuser.filesupported.csvtype', 'csv', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.importuser.filesupported.vcftype', 'vcf', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.invitemember.usergridtitle.name', 'Business Members', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.invitemember.nomember.message', 'No Members', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.invitemember.newinvitees.message', 'New Invitees', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.invitemember.noinviteesadded.message', 'No Invitees added', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.projectandtask.label', 'Project and Task', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.weeklytotal.label', 'Weekly Total', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.projecttotal.label', 'Assign. Total', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.actualwork.label', 'Actual Work', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.selectassignment.message', 'Select any assignment from left to see corresponding blog entries.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.blogentries.message', 'Blog posts from {0} - {1}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.newblogentry.label', 'New Blog Entry', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.grandtotal.label', 'Grand Totals', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.totalforproject.label', 'Total For {0}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.viewassignments.label', 'View Assignments:', 'A', 0, 1, null);

--insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
--values (2000, 'en', 'text', 'prm.directory.directory.invitemember.nomember.message', 'Please select business directory', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.resource.timesheet.monthlyview.isenabled', '0', 'A', 1, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.resource.timesheet.editmode.isenabled', '1', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.select.message', 'Please select atleast one activity before applying filter.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.loading.message', 'Loading...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.errorpage.formnotavailable.message', 'Form not available', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Daily Totals'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.resource.timesheet.grandtotal.label';
      
update pn_property p set 
       p.property_value = 'Assignments'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.resource.timesheet.projectandtask.label';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.error.jumptodate.message', 'Please enter valid date.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.directory.directory.searchmode.isenabled', '0', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.nav.wiki', 'Wiki', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.business.wiki.isenabled', '1', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.importuser.filesupported.searchfilemessage', 'Select File: ', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.container', 'Document folder', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.project', 'Project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.main.sharedformdelete.message', 'Shared form can be deleted only in owner space', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.blog.selecttask.message', 'Select any task from left to see corresponding blog entries.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.cusomizecolumntooltip.message', 'Cusomize your column visibility settings.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.expandcollapsetooltip.message', 'Expand all or collase all node.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.loading.message', 'Loading...', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.resource.timesheet.workcapturecomment.isenabled', '0', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.blogentryconfirm.message', 'You can not edit until you save the current blog entry. Do you want to save blog entry?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.capturework.message', 'Please capture some work !', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.captureworkcomment.message', 'Please enter comment !', 'A', 0, 1, null); 

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.invalidworkCapture.message', 'Please enter valid number !', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.filter.currentassignments.label', 'Current', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.filter.completedassignments.label', 'Completed', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.filter.allassignments.label', 'Uncompleted', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.resource.timesheet.personalizepage.isenabled', '0', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.rsstitle', 'RSS', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.invitemember.addnewinviteebutton.caption', 'Add New Invitees', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.roles.groupeditaddbutton.caption', 'Add', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.roles.groupeditremovebutton.caption', 'Remove', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.roles.groupeditcancelbutton.caption', 'Cancel', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.roles.groupeditsubmitbutton.caption', 'Submit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.memberedit.membereditsubmitbutton.caption', 'Submit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.modified.norecords.revert.message', 'There are no modified tasks to revert.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.resource.timesheet.blogpostion.isenabled', '1', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.rss.projectnametitle', 'Project related RSS feeds', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.rss.wrongurl', 'Wrong URL', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.rss.wrongdescription', 'Specified URL does not include required parameters.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.rss.norssfeeds', ' No RSS feeds for specified criteria.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.button.quickadd.caption', 'Quick Add', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.button.submit.caption', 'Submit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.button.cancel.caption', 'Cancel', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.bulkinvitation.alt', 'Manage Directories', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.bulkinvitation', 'Manage Directories', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.modified.confirm.message', 'Timesheet has modified entries. Do you wish to continue without submitting the changes?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.activateedit.musthavedesignfields.message', 'You must add at least one form field!', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Unsupported file format. Supported file format is xml'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.formdefintionimport.fileupload.formatnotsupported.message';


insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.projectsubproject.headertitle', 'Choose a business or project to add members to', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.membername.headertitle', 'Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.selectallcheck.title', 'all', 'A', 0, 1, null);   

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.totalmember.header', 'Total Members:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.submitbuttontitle.header', 'Invite New Members', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.title.name', 'Invite Members of a Business to Multiple Businesses and Projects', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.filter.allworkcaptured.label', 'Work Captured', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.filter.priorsix.label', 'Prior Six Months', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.filter.futuresix.label', 'Future Six Months', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.invalid.jumptodate.message', 'Invalid date !', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.resetblogentry.confirm.message', 'All the changes will be discarded ! Do you want to reset?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.blogentrypost.confirm.message', 'Post blog without subject?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.jumptodate', 'Jump to Date:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.project.activity.rssmouseoverimagepath', '/images/activitylog/toolbar_rss_on.gif', 'A', 0, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.project.activity.rssmouseoutimagepath', '/images/activitylog/toolbar_rss_over.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.previouslink', 'Previous', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.resource.timesheet.exporttoexcelon.imagepath', '/images/icons/timesheet-export_on.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'prm.resource.timesheet.exporttoexcelover.imagepath', '/images/icons/timesheet-export_over.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.exporttoexcel.link', 'Export to Excel', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.methodology.template.used.message', 'There are projects/businesses created with this template. You can not edit it now !', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.managedirectory.button.tooltip', 'Manage Directories', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkresponsiblity.submitbutton.caption', 'Submit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskedit.error.invalidworkunit.message', 'Invalid work unit.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskedit.error.invalidworkcompleteunit.message', 'Invalid work complete unit.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskedit.error.invaliddurationunit.message', 'Invalid duration unit.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.submit.link', 'Submit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.reset.link', 'Reset', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.collapseall.link', 'Collapse All', 'A', 0, 1, null);


insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.jumptodate.label', 'Jump to Date', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.hoursadded.label', 'Hours Added', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.blogitmessage.label', 'Blog-it Message', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.addcomment.label', 'Add a Comment', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.commenthere.label', 'Comment Here', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.submit.label', 'Submit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.cancel.label', 'Cancel', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.jumptodate.noactivityfound.message', 'No activities found for selected date.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.expandall.link', 'Expand All', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.moreworkinvalid.error', 'Total work can not be more than 24 Hours', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldedit.menuchoices.nodefault.label', 'No default', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'All non-erroneous rows have been successfully inserted'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.form.csvimport.results.allrowsinserted.message';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.tab.schedule.caption', 'Schedule', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.tab.ganttview.caption', 'Gantt Chart', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.tab.flatview.caption', 'Flat View', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.marked.label', 'Marked', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.unmarked.label', 'Unmarked', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.link.columnsettings.label', 'Column Settings', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.link.collapseall.label', 'Collapse All', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.link.expanddall.label', 'Expand All', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.link.filters.label', 'Filters', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.tab.blogs.caption', 'Blogs', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.today.label', 'Today', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.errorpage.objectdeleted', 'Object Deleted', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.errorpage.alreadydeletedmessage', '{0} : {1} is deleted!', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.editproperties.changepicture.link','Change Picture','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.editproperties.viewpersonalprofile.link','View Personal Profile','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.editproperties.removepicture.link','Remove Picture','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.memberedit.role.label','Role','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.memberedit.teammemberrole.label','Team member','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.memberview.blogit.link','Blog-it','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.memberview.editmemberproperty.link','Edit Member Properties','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.memberview.viewmemberproperty.link','View Member Properties','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.blogcommentview.createblogpostlabel','created blog post','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.blogcommentview.commentedonlabel','commented on','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.blogcommentview.blogpostcommented','commented on blog post','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.blogcommentview.commented','commented','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.unmarkedactivityerror','Sorry, activity can not be unmarked. Some error encountered while unmarking.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.markedactivityerror','Sorry, activity can not be marked. Some error encountered while marking.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.markingerrormessage','Error occurred while marking.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.selectfilterobjectmessage','Please select at least one filter object.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.getactivitieserror','Error occurred while getting activities.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.getblogerror','Error occurred while getting blog.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.commentdeletedmessage','Comment can not be posted, Since blog entry is already deleted.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.commentnotsavedmessage','Sorry, comment can not be saved. Some error encountered while saving. ','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.project.activity.contentnotblankmessage','Comment content should not be blank.','','A',0,1);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.directory.memberview.skype.label','Skype','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invite.memberaddition.firstname.defaultmessage', 'First Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invite.memberaddition.lastname.defaultmessage', 'Last Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invite.memberaddition.email.defaultmessage', 'E-mail', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invite.memberaddition.partialfirstorlastname.defaultmessage', 'Using partial first or last name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.meeting.addattendees.nomembers.business.message', 'There are no other team members in this business available<br>to add to this business.', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'End Date:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.scheduleenddate.label';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.invaliddateformat', 'Date is not valid - It must be in the format of ', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.datevaluenotblankmessage', 'Date should not be blank', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.customizecolumn.clearall.label', 'Clear All', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.customizecolumn.page.title', 'Column Settings', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.movingmember.label', 'Moving Members to Invitee List...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.removeuser.tooltip', 'Remove from Invitee list.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.movingmembererrormessage.label', 'No member is selected to move', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.addtoinviteeerrormessage.label', 'Please add invitees to new invitees list', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.loadingbusinessmembererrormessage.label', 'Loading Business Members...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.addingprojectorsubbusinesserrormessage.label', 'Adding ', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.searchusers.label', 'Searching members...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.showprocessing.label', 'Processing...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.selectmembertoinvite.label', 'Please select atleast one member to invite', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitaion.selectprojectorsubbusinessalreadyaddederrormsg.label', 'Selected Project or Sub business has been already added.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.noprojectorsubbusinesserrormsg.label', 'No sub project or business to add.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitaion.selectprojectorsubbusinesserrormsg.label', 'Please select sub project or business to add.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.totalbusinsessmember.label', 'Total Members: ', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.selectedbusinsessmember.label', 'Selected: ', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitaion.businsessmemberlisheader.label', '(Last Name, First Name)', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.security.editrole.remove.select.message', 'Please select at least one item in the Role/Person list to remove', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.security.editrole.remove.noitems.message', 'There are no items in the Role/Person list to remove', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.importuser.tooltip', 'Import Members', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Change default roles add Title and Responsibilities to new Invitees'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.directorypage.newinvitee.defaultrole.subtitle';     

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.inlineblogit.subject.emptytext', 'Subject', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.inlineblogit.message.emptytext', 'Message', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.all', 'All', 'A', 0, 1, null);
    
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.markedentries', 'Marked Entries', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.allteam', 'All Team', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.projeclistmessage', 'Select a Sub-Business or Project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.noprojecinlistmessage', 'no sub project or sub business', 'A', 0, 1, null);
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.report.businessworkcompleted.xslpath', '/report/xsl/businessworkcompleted.xsl', 'A', 0, 0, null);	  

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.report.businessworkcompleted.name', 'Time Submitted Report', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.report.businessworkcompleted.description', ' 	Reports on when assignees have done work on assignments.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.report.workcompleted.space.name', 'Space name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.blog.important', 'Important', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.blog.mycommentedblog', 'My Posts That Are Commented', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.export.ms.project.no.owned.projects.message', 'There are no owned projects by this business', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.importuser.filetype.label', 'File Type: ', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.deleteproject.tooltip', 'Delete Project', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'E-mail'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.memberedit.emailaddress.label';
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.selectblogeventmessage', 'Please select at least one blog event.' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.blogheader.label', 'Blog' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.persistingcolumn.message', 'Persisting column order.' , 'A', 0, 1, null);	   

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.entriesfrom.label', 'Entries from {0}' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.entriesfromto.label', 'Entries from {0} to {1}' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.comment.label', 'comment' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.viewcomment.label', 'View Comments' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.hidecomment.label', 'Hide Comments' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.nobusiness.message', 'No business to select' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkresponsiblity.cancelbutton.caption', 'Cancel', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.tab.wiki.caption', 'Wiki', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.wiki.selecttask.message', 'Select any task from left to see corresponding wiki pages.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tab.wiki.assignpagetotask.loading.message', 'Assigning...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.tab.menu.caption', 'Wiki Menu', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.tab.backtopage.menu.caption', 'Back To Page Index', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.tab.edit.menu.caption', 'Edit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.tab.cancel.menu.caption', 'Cancel', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.tab.assignpagetotask.menu.caption', 'Assign Page To Selected Object', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schdule.columnsetting.error.message', 'Error occurred while loading popup.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schdule.columnsetting.loadingpopup.message', 'Loading Popup...', 'A', 0, 1, null);


insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.businessmeber.title', 'Choose a business directory to add members from:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.businessmeber.title', '1. Choose a business directory to add members from:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.chooseproject.title', '2. Build a table of businesses and projects:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.addprojectorsubbusiness.label', 'Add to Table', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Selected Project or Sub business has been already added.'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.bulkinvitaion.selectprojectorsubbusinessalreadyaddederrormsg.label';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.inlineblogit.taskname.caption', 'Re : {0}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.previousweek.tooltip', 'Previous Week', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.nextweek.tooltip', 'Next Week', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.closeblogpanel.title', 'Close', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.openblogpanel.title', 'Open', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.jumptodate.error.message', 'date is not valid - It must be in the format of', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.blogentries.loading.message', 'Loading blog entries', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.exporttoexcel.header.caption', 'Weekly Timesheet', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.serverrequest.errormessage', 'Server Request Failed...', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = '32'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.profile.username.maxsize';

update pn_property p set 
       p.property_value = '4'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.profile.username.minsize';
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.jumpto.today.title', 'Jump to today date', 'A', 0, 1, null);

update pn_property p set 
      p.property_value = 'Index By Page Title:'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.wiki.menu.pageIndex.label';
      
update pn_property p set 
      p.property_value = 'Index of Images'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.wiki.left.allimages.link';
      
update pn_property p set 
      p.property_value = 'Page Change History'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.wiki.menu.changeHistory.label';
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.menu.showattachments.label', 'Show Attachments', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.expandall.label', 'Expand All' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.collapseall.label', 'Collapse All' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.groupby.label', 'Group By' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.assignmenttype.label', 'Assignment Type' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.assignment.label', 'Assignment' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.filter.projectfilter.label', 'Project Filter', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.filter.dates.label', 'Dates', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.filter.percentcomplete.label', '%Complete', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.filter.business.label', 'Business', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.totalassignment.gridfooter.label', 'Total Assignments:' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.groupbybusiness.text', 'Business' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.groupbyproject.text', 'Project' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.groupbyassignment.text', 'Assignment' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.assignmenttypemeeting.text', 'Meeting' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.assignmenttypetask.text', 'Task' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.assignmenttypeform.text', 'Form' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.assignororassignee.assignedtome.text', 'Assigned to me' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.assignororassignee.assignedbyme.text', 'Assigned by me' , 'A', 0, 1, null);

update pn_property p set
       p.property_value = 'Show Tasks:'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.filtercheckboxes.header.label';
      
update pn_property p set
       p.property_value = 'All'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.alltasks.label';
      
update pn_property p set
       p.property_value = 'On the Critical Path'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.tasksonthecriticalpath.label';
      
update pn_property p set
       p.property_value = 'Late'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.latetasks.label';

update pn_property p set
       p.property_value = 'Should Have Started'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.tasksshouldhavestarted.label';

update pn_property p set
       p.property_value = 'Coming Due'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.taskscomingdue.label';
      
update pn_property p set
       p.property_value = 'Started After Their Planned Start'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.tasksstartedafterplannedstart.label';
      
update pn_property p set
       p.property_value = 'Unassigned'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.unassignedtasks.label';

update pn_property p set
       p.property_value = 'Assigned to User'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.showtasksassignedtouser.label';
      
update pn_property p set
       p.property_value = 'Work % Complete:'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.workpercentcomplete.label';

update pn_property p set
       p.property_value = 'Start Date Range:'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.startdatefilter.label';
      
update pn_property p set
       p.property_value = 'End Date Range:'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.enddatefilter.label';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemember.businessmeber.maintitle', 'By individual member:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemember.businessmeber.subtitle', 'Find all businesses and projects that someone is assigned to:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemember.chooseproject.maintitle', 'By business or project:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemember.chooseproject.subtitle', 'Choose a business or project to remove members from:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.managedirectory.invitemember.tab', 'Invite Members', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.managedirectory.removemember.tab', 'Remove Members', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.managedirectory.removemember.orlabel', '-or-', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.managedirectory.removemember.submitbutton.label', 'Remove Unchecked Members', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.managedirectory.removemember.errormessage.label', 'You can not remove the last remaining member of the workspace administrator role. The highlighted space may only be disabled.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemember.selectmembertoremove.label', 'Please uncheck a member to remove', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removingmember.showremoving.label', 'Removing Members...', 'A', 0, 1, null);

update pn_property p set
	   p.property_type = 'text',
       p.property_value = 'Your email must be a valid address. eg: yourname@domain.com'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.invite.memberaddition.emailaddressvalidation.message';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.bulkinvitation.deletebusiness.tooltip', 'Delete Business', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.customizecolumn.page.title', 'Column Settings', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.customize.assignment.button.submit.caption', 'Submit', 'A', 0, 1, null);
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.customizecolumn.clearall.label', 'Clear All', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.customize.assignment.button.cancel.caption', 'Cancel', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.link.columnsettings.label', 'Column Settings', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemember.title.name', 'Remove Members from Businesses and Projects', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.alreadyexistemail.errormessage', 'Member having this email address already added in invitee list', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.historypage.wikidiff.difflink', 'Diff Link', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.historypage.wikidiff.seediff', 'Diff', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.historypage.wikidiff.version', 'Version', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schdule.tasklist.checkuncheckall.title', 'Click to select or unselect all checkboxes', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemember.loadingmembermessage.label', 'Loading Members...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.loading.message', 'Loading...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.persistingcolumn.message', 'Persisting column order...' , 'A', 0, 1, null);	   

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.wikidiff.contentedited.message', 'Content edited by', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.wikidiff.contenteditedbyyou', ' Content edited by you', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.wikidiff.overwrtebutton.caption', ' Overwrite and Submit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.wikidiff.contentchanged.message', 'has already changed the original content of page', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.wikidiff.currentversion.message', 'Current Version', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.upload.invalidfile.message', 'Image name should not contain {0} characters', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.whatlinkhere.nowikipage.message', 'There are no wiki pages that link to this one.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.whatlinkhere.whatlinkhere.message', 'What links here', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.deletepage.suretodelete.message', 'Are you sure you want to delete this page?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.imageupload.invalidpath.message', 'Invalid image path.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.createpage.entername.message', 'Please enter name for new Wiki Page:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.createpage.wikipagename.title', 'Wiki Page Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.wiki.title', 'Wiki', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.news.title', 'News', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.project.title', 'Project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.blogentry.title', 'Blog', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.blogcomment.title', 'Blog Comment', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.blogentrydeleted.errormessage', 'Blog entry has been deleted', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.blogentrycheck.errormessage', 'Error occurred while checking blog entry deleted or not', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemembers.nomemberfound.searchmsg', 'Searched member not found in any sub business or project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.blog.selecttask.message', 'Select any object from left to see corresponding blog entries.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.wiki.selecttask.message', 'Select any object from left to see corresponding wiki pages.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.nonworkingwork.errormessage', 'Are you sure, you want to capture work on non working day?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.wikidiff.showdiff.message', 'Show diff', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpage.wikidiff.hidediff.message', 'Hide diff', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.filter.show.assignments.label', 'Show Assignments', 'A', 0, 1, null);

update pn_property p set 
      p.property_value = 'Date range'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.assignment.filter.dates.label';

update pn_property p set 
      p.property_value = 'Work % Complete'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.assignment.filter.percentcomplete.label';

update pn_property p set 
      p.property_value = 'All'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.assignments.allassignments.label';

update pn_property p set 
      p.property_value = 'In Progress'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.assignments.inprogress.label';

update pn_property p set 
      p.property_value = 'Late'       
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.assignments.lateassignments.label';
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.errorpage.hiddenmessage', '{0} : {1} is hidden!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.expand.lable', 'Expand', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.collapse.lable', 'Collapse', 'A', 0, 1, null);      

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.withoutoremail.title', 'Invite member by e-mail address:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.quickadd.message', 'Please save or revert your last made changes before adding the new task.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.workcolumnmustvisible.message', 'Work column must be visible for adding new task.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.teammembers', 'Team Members : ', 'A', 0, 1, null);      

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.monthlist', 'Month', 'A', 0, 1, null);      

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.yearlist', 'Year', 'A', 0, 1, null);      

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.loadingerrormessage', 'Error occurred while loading time summary data.', 'A', 0, 1, null);      

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.teammembers', 'Team Members', 'A', 0, 1, null);      

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.monthlytotalmessage', 'Total', 'A', 0, 1, null);      

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.dailytotalmessage', ' Daily Total', 'A', 0, 1, null);      

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.nosummaryfoundmessage', 'No Time summary record found', 'A', 0, 1, null);      

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.bottom.renamepagename.link', 'Rename Page', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.editpagename.messagebox.label', 'Please enter name to edit wiki page name:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.renameerror.message', 'Error occured while renaming wiki page', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.samepageexists.error.message', 'A page already exists with same name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.gridheader.assignmentname', 'Assignment' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.gridheader.type', 'Type' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.gridheader.workspace', 'Project' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.gridheader.startdate', 'Start Date' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.gridheader.enddate', 'Due Date' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.gridheader.actualstartdate', 'Actual Start Date' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.gridheader.percentcomplete', '% Complete' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.gridheader.work', 'Work' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.gridheader.workcomplete', 'Work Complete' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.gridheader.workremaining', 'Work Remaining' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.gridheader.assignee', 'Assignee' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.gridheader.assignor', 'Assignor' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.allassignments.link', 'All', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.assignedbyme.link', 'Assigned By Me', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.completedassignments.link', 'Completed', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.inprogress.link', 'In Progress', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.comingdue.link', 'Coming Due', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.shouldhavestarted.link', 'Should Have Started', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.lateassignments.link', 'Late', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.completedassignments.tab.label', 'Completed', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.inprogress.tab.label', 'In Progress', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.comingdue.tab.label', 'Coming Due', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.shouldhavestarted.tab.label', 'Should have comp...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.textfield.name.label', 'Name' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.textfield.description.label', 'Description' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.numberfield.Work.label', 'Work' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.startdate.label', 'Start Date' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.duedate.label', 'Due Date' , 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.blogpost.message', '{0}  time submittal or post found for {1} until {2}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.blogposts.message', '{0}  time submittal or posts found for {1} until {2}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.blogposts.empty.message', 'No blog posts found for assignment {0}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.allteammembers', 'All Team Members ', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.loadingmessage', 'Loading...', 'A', 0, 1, null);


update pn_property p set
	   p.property_type = 'text',
       p.property_value = '{@prm.project.nav.schedule}'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.methodology.nav.scheduling';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.unindent.error.message', 'Unindent operation can not be performed.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.share.select.permission.message', 'Select atleast one workspace option.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.assigne.resource.error.message', '% assigned must greater than 0.', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Percent must be in the range 1..100'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.taskview.resources.error.percentoutofrange.message';

update pn_property p set 
       p.property_value = 'The task: {0} has already been added external to this project.'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.crossspace.createexternal.error.duplicatetask.message';
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.previoustab.message', 'Previous', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.nexttab.message', 'Next', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schdule.splitter.open.title', 'Open', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schdule.splitter.close.title', 'Close', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.timesummarylink', 'Time Summary', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.total', ' Total of ', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.main.taskdownfailed.error.message', 'Task Down operation cannot be performed', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.loadmembers.selectmember.error.message', 'Please select at least one user', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.loadmembers.nomemberfound.error.message', 'No record found', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskdetail.resources.assign.assigned.column', '% Assigned', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskdetail.resources.assign.workcomplete.column', 'Work Complete', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.meeting.time.to.label', 'to', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.detailsnotavailablehere.message', 'Details are not available here for selected object.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.taskdetail.noassignments.message', 'No resource assigned to this task.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.taskdetail.noagendaitems.message', 'No agenda items in meeting.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.twopaneview.tab.detail.caption', 'Detail', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.no.phase.error.message', 'Project has no Phase to link.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.select.phase.error.message', 'Select the Phase.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.share.select.allowableaction.message', 'Select atleast one allowable action.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.twopaneview.detail.selectobject.message', 'Select any object from left to see its details.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.twopaneview.detail.selecttask.message', 'Select any task from left to see its details.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.filter.invaliddate.message', 'Invalid Date.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.filter.invaliddaterange.message', 'Invalid Date range.<br/> Start date can not be after end date.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.properties.channel.description.title', 'General', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.properties.channel.address.title', 'Address', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.update.error.invalidtime.message', '{0} is not normally a working day', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.startdate', 'Start date', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.enddate', 'End date', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.weeklytotal', 'Weekly Total', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.addweblogentry.securityvalidation.message', 'You do not have permssion to create blog entry in this space.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.wiki.doesnotsupportforobject.message', 'Wiki is not supported for this selected object!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.button.add.caption', 'Add', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.savingphase.message', 'Saving phase...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.methodology.template.directory.forbiden.message', 'You can not invite people into template project/business!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.deleteweblogentrywithtimeentry.confirm.message', 'Are you sure to delete this blog entry? <br /> This will not remove time entry. To remove time entry you need to make correcting time entry in another blog entry or the time sheet.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.format.msexcel07.name', 'Excel 2007', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.format.mspowerpoint07.name', 'PowerPoint 2007', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.format.msword07.name', 'Word 2007', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.errorpage.accessdeniedmessage', 'you do not have permission to view {0} : {1}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.business.timesubmital.report.showassignment.isenabled', '0', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.exportcsv.link', 'Export to CSV', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.resources.cantassignmentresource.message', 'You can not assign resource in template !', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.expand.tooltip', 'Expand', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.collapse.tooltip', 'Collapse', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.importobject.url.validation', 'Please enter valid URL', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.wiki.doesnotsupportforobject.message', 'Wiki is not supported for selected object', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.wiki.menu.unassignbutton.message', 'Unassing page from selected object', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Document Name'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.projects.documentsmodified.header.title.name';


update pn_property p set 
       p.property_value = 'Contains'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.finder.textcomparator.contains.symbol';

update pn_property p set 
       p.property_value = '{@prm.methodology.nav.process}'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.template.create.select.process';

update pn_property p set 
       p.property_value = '{@prm.project.nav.schedule}'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.template.create.select.schedule';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.showdailyview', 'Show Daily View', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.showassignments', 'Show Assignments', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.invalidstartdatemsg', 'Invalid start date.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.invalidenddatemsg', 'Invalid end date.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.ispublished.viewonly.validation.message', 'A workflow may not be edited while it is Published. To continue to make changes, its status must be changed to Unpublished. If you choose not to change its status, you will be able to view the workflow but cannot save any changes.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.skype.invalid.error.message', 'Special characters not allowed in skype name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.skype.charrange.error.message', 'Skype name must be between 6 and 32 characters in length', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.skype.spaces.error.message', 'Please enter valid skype name', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Edit'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.process.modify.button.label';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workflow.envelope.include.propertiesdetail.spacename.label', 'WorkSpace Name:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.estimatedtotalwork.error.message', 'Estimated work cannot be lesser than completed work.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.workhourslessthanzero.error.message', 'Please enter hours greater than zero.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.imagereferences.title', 'Image references', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.imagereferences.noreferences.message', 'There are <strong>no references to this image</strong> in this wiki space!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.imagereferences.imgnotdeleteable.message', 'Please, <strong>remove the references</strong> to this image from the specified pages, in order to be able to remove the image!', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Time Submitted'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.report.businessworkcompleted.name';
      
update pn_property p set 
       p.property_value = 'Time worked on assignments submitted by team members'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.report.businessworkcompleted.description';

update pn_property p set 
       p.property_value = 'Responsible licenses setup'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.setup.channel.licenses.title';
      
update pn_property p set 
       p.property_value = 'Specify your working and non-working time'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.setup.workingtimecalendar.description';

update pn_property p set 
       p.property_value = 'Your Personal Settings for this Project'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.channel.personalsetup.title';
      
update pn_property p set 
       p.property_value = 'Add or remove people and roles in this project'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.directoryedit.label';
      
update pn_property p set 
       p.property_value = 'Create project templates'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.templatesedit.label';

update pn_property p set 
       p.property_value = 'Edit security settings for this project'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.channel.managesecurity.label';

update pn_property p set 
       p.property_value = 'Your Global Settings'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.channel.personalglobalsetup.title';
      
update pn_property p set
	   p.property_type = 'text',
       p.property_value = 'Your Personal Settings for this Business'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.channel.personalsettings.title';

update pn_property p set 
       p.property_value = 'Go to Blog and see more posts'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.blog.viewblog.clickheretoseemoreposts.message';

update pn_property p set 
       p.property_value = 'Edit security settings for this business'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.securitysettings.label';      

update pn_property p set 
       p.property_value = 'View and edit business templates.'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.projecttemplates.description';
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.title', 'Time Submitted', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.filterpreset.header', 'Filter Presets', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.tab.label', 'Assignments', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Status Icons and Colors'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.tasklistdecorating.pagetitle';

update pn_property p set 
       p.property_value = 'Finish:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.scheduleenddate.label';
      
update pn_property p set
       p.property_value = 'Finish Date Range:'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.enddatefilter.label';

update pn_property p set
       p.property_value = 'Actual Finish'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.list.actualenddate.column';

update pn_property p set
       p.property_value = 'Baseline Finish'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.list.baselineenddate.column';  

update pn_property p set
       p.property_value = 'Schedule Finish:'
where
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.properties.scheduleenddate.label';  

update pn_property p set 
       p.property_value = 'Workplan Start:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.main.schedulestartdate.label';

update pn_property p set 
       p.property_value = 'Schedule Start:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.properties.schedulestartdate.label';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.timesummary.exportcsv.personname.label', 'Person Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.timesummary.exportcsv.projectname.label', 'Project Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.timesummary.exportcsv.assignmenttype.label', 'Assignment Type', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.timesummary.exportcsv.assignmentname.label', 'Assignment Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.timesummary.exportcsv.captureddate.label', 'Captured date', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.timesummary.exportcsv.workhour.label', 'Work hour', 'A', 0, 1, null);

update pn_property p set  
       p.property_value = 'Overall Status Color:'        
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.project.properties.status.label'; 
 
update pn_property p set  
       p.property_value = 'Financial Status Color:'        
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.project.properties.financialstatus.label'; 
 
update pn_property p set  
       p.property_value = 'Schedule Status Color:'        
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.project.properties.schedulestatus.label'; 
 
update pn_property p set  
       p.property_value = 'Resource Status Color:'        
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.project.properties.resourcestatus.label'; 

update pn_property p set 
       p.property_value = 'There was a problem performing the transition. Might be transition has been already performed.<br/>Click Return then try re-loading the envelope.'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.workflow.errors.transitionproblem.clickreturn.message';
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.newpage.invalidpagenamelength.message', 'Page name length exceeds 50 characters limit', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Project'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.document.documentlist.space.label';

update pn_property p set 
       p.property_value = 'Template'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.methodologyspace.module.description';

update pn_property p set 
       p.property_value = '/images/menu/logo_pnet.png'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.header.banner.image';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.timesummary.checkdate.invaliddaterange.label', 'Invalid Date range.<br/> Start date can not be after end date.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.create.namealreadyexist.error.message', 'Template with this name is already exist.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.propertiesedit.removelogo.message', 'Are you sure to remove project logo?', 'A', 0, 1, null);

update pn_property p set  
       p.property_value = 'Remove yourself from the project'        
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.project.portfoliodelete.option.removeyourself.name'; 

update pn_property p set  
      p.property_value = 'Delete the project itself'        
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.project.portfoliodelete.option.disableproject.name'; 

      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.cancel.button.label', 'Cancel', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.imagenotexists.error.message', 'ERROR: No image has been uploaded with name ', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.tab.header.message', 'Wiki page found for {0}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.dayslabel.today', 'Today', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.dayslabel.yesterday', 'Yesterday', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.overdays.message', 'over {0} days', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.daysago.message', '{0} days ago', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.lastblogit.overdayslimit', '60', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.grouplistview.subjectmessageblank.errormessage', 'The Email Subject and Message fields should not be empty.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.grouplistview.blanksubject.errormessage', 'The Email Subject field should not be empty.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.grouplistview.blankmessage.errormessage', 'The Email Message field should not be empty.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.deletecomment.link', 'Delete', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.unauthorizeduser.message', 'Unauthorized user, You do not have permission to delete this blog comment.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.failedtodelete.message', 'Sorry, comment can not be deleted. Some error encountered while deleting.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.change.title', 'Change', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.dailytotals.title', 'Daily Totals', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.blogentryurl.label', 'Blog Entry URL', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schdule.toolbox.heading.title', 'Schedule', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecorating.selectimage.button', 'Select Image', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.crossspace.createshare.objectsshared.label', 'Object(s) to be Shared', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.crossspace.createshare.allowedactions.label', 'Allowed Actions', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.crossshare.finsspace.nameofspace.label', 'Type all or part of the name of the space', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.crossshare.finsspace.add.label', 'Add?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.crossshare.finsspace.spacename.label', 'Space Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.crossshare.finsspace.searchmatches.label', 'Search Matches', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.nobackground.option', 'No Background', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.ltblue.color', 'Lt Blue', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.blue.color', 'Blue', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.pink.color', 'Pink', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.gray.color', 'Gray', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.green.color', 'Green', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.ltgreen.color', 'Lt Green', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.dkred.color', 'Dk Red', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.dkblue.color', 'Dk Blue', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.olive.color', 'Olive', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.purple.color', 'Purple', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.red.color', 'Red', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.silver.color', 'Silver', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.teal.color', 'Teal', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.white.color', 'White', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.tasklistdecoration.yellow.color', 'Yellow', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.viewbutton.label', 'View', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.goto.heading', 'Go To', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.personalizepage.choosechannel.message', 'Choose which channels to display on your dashboard', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.report.reportlist.title', 'Report List', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.timesubmital.report.noassignments.message', 'No assignments found to export.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.lastnamefirsename.label', '(Last Name, First Name)', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.selectbusiness.option', 'Select Business', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.importuser.uploadbutton.caption', 'Upload', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.directory.importuser.cancelbutton.caption', 'Cancel', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.projectcreated.message', 'created project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.projectupdated.message', 'updated project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.overallstatuschanged.message', 'changed the overall status of', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.invitedmembers.message', 'new members to project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.removedmembers.message', 'members from project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.activity.createdblogpost.message', 'created blog post', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.activity.editedblogpost.message', 'edited blog post', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.activity.deletedblogpost.message', 'deleted blog post', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.activity.createdwikipage.message', 'created wiki page', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.activity.modifiedwikipage.message', 'modified wiki page', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.activity.deletedwikipage.message', 'deleted wiki page', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.activity.uploadedwikiimage.message', 'uploaded wiki image', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.news.activity.postednews.message', 'posted news', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.news.activity.editednews.message', 'edited news', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.news.activity.deletednews.message', 'deleted news', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.formrecord.activity.createdformrecord.message', 'created', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.formrecord.activity.editedformrecord.message', 'edited', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.formrecord.activity.deletedformrecord.message', 'deleted', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.task.activity.createdtask.message', 'created task', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.task.activity.removedtask.message', 'removed task', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.task.activity.editedtask.message', 'edited properties of task', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.activity.createdform.message', 'created form', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.activity.removedform.message', 'removed form', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.activity.modifiedform.message', 'modified form', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.activity.uploadeddocument.message', 'uploaded document', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.activity.checkedindocument.message', 'checked in document', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.activity.checkedoutdocument.message', 'checked out document', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.activity.vieweddocument.message', 'viewed document', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.activity.modifieddocument.message', 'modified properties of document', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.activity.removeddocument.message', 'removed document', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.activity.undocheckedout.message', 'undid a check out of document', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.activity.createdfolder.message', 'created folder', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.activity.removedfolder.message', 'removed folder', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.activity.moveddocument.message', 'moved document', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blogcomment.activity.commentonblog.message', 'commented on blog post', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.activity.by.message', 'by', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.activity.at.message', 'at', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.activity.dateon.message', 'on', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blogcomment.activity.rssfeed.commentis.message', 'comment is :', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.activity.continueddatemessage.message', '{0} continues on next page', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.activity.selectall.message', 'Select All', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.activity.clearall.message', 'Clear All', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.activity.button.apply.message', 'Apply', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.activity.displaydate.message', 'cont.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.activity.nextentries.title', 'Next', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.activity.preventries.title', 'Previous', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.invitedmember.message', 'new member to project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.removedmember.message', 'member from project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.memberadded.message', 'added', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.activity.memberremoved.message', 'removed', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.leftheading.message', 'My Assignments', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignments.newassignment.label', 'New Assignment', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.nonodestocollapse.message', 'No nodes to collapse', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.assignment.nonodestoexpand.message', 'No nodes to expand', 'A', 0, 1, null);

update pn_property p set  
      p.property_value = 'Apply'        
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.personal.assignments.applyfilters.label';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.newassignment.header', 'New Assignment', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.selectresource.message', 'Please select a resource to assign task', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.percentassigned.message', 'Please enter % Assigned for selected resorurce', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.waiting.message', 'Please Wait...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.serverrequest.message', 'Server request failed please try again...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.workerror.message', 'Please enter valid work', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.invaliddate.message', 'Please enter valid Date', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.invalidtaskname.message', 'Please enter valid task name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.taskdescription.message', 'Task description cannot be more than 1000 characters', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.verifyname.nohtmlcontent.message', 'No html contents allowed in task name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.taskorproject.message', 'Please select a Task or Project to create new task assignment', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.utilization.tooltip', 'Click on image to view resource utilization summary', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.resourceassignmentgrid.resources.label', 'Resources :', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.viewblog.lastedited.date.label', 'Last edited on :', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.viewblog.hoursubmitted.label', 'Hours Submitted:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.viewblog.changeestimate.label', 'Changed Estimate:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.viewblog.multiplework.hoursubmitted.label', 'Multiple Work Hours Submitted:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.viewblog.blogentry.hidecomment.label', 'Hide Comments', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.viewblog.blogentry.delete.label', 'Delete', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invite.addresponsibility.blankinviteelist.message', 'There is no invitee to invite', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.listedit.musthaveuserdefinefields.message', 'You must add at least one user defined form field!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.activateedit.listmusthaveuserdefinefields.message', 'Please select at least one user defined field for lists : {0} ', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.definitionedit.shared.label', 'Shared', 'A', 0, 1, null); 

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.listedit.shared.withthisworkspace.label', 'Shared with others in this workspace', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.listedit.shared.justmyself.label', 'Just myself', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.listedit.shared.listvisibility.label', 'List Visibility: ', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.update.timerangeerror.message', 'Time {0} is overlapping with previous time {1}.  Please enter non overlapping times.', 'A', 0, 1, null);


insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.title', 'Dashboard', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectmanager.label', 'Project Manager:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.status.label', 'Status:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.completion.label', 'Completion:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.description.label', 'Description:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.subprojectof.label', 'This is the Sub Project of:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.toolbox.title', 'Toolbox', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.main.personalize.button.tooltip', 'Personalize Page', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.goto.title', 'Go to', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.goto.subprojects.label', 'Subprojects', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.goto.reports.label', 'Reports', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.goto.news.label', 'News', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.taskcompletion.channel.title', 'Task Completion', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectcompletion.channel.title', 'Project Completion', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectcompletion.start.label', 'Start', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectcompletion.finish.label', 'Finish', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectcompletion.completion.label', 'Completion', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectcompletion.planned.label', 'Planned:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectcompletion.actual.label', 'Actual:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.phasesandmilestone.channel.title', 'Phases and Milestones', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.overallstatus.label', 'O', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.financialstatus.label', 'F', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.schedulestatus.label', 'S', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.resourcestatus.label', 'R', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.phasesandmilestone.name.label', 'Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.phasesandmilestone.enddate.label', 'End Date', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.phasesandmilestone.status.label', 'Status', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.phasesandmilestone.progress.label', 'Progress', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.subprojects.channel.title', 'Subprojects', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.subprojects.name.label', 'Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.subprojects.completion.label', 'Completion', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.upcomingmeetings.channel.title', 'Upcoming Meetings', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.upcomingmeetings.meetingname.label', 'Meeting Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.upcomingmeetings.date.label', 'Date', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectnews.channel.title', 'Project News', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectnews.newsondate.message', 'on', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectTeam.channel.title', 'Project Team : {0} Members', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectTeam.name.label', 'Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectTeam.lastblogit.label', 'Last Blog-it', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectTeam.assignments.tooltip', 'Assignments', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.projectTeam.skypestatus.tooltip', 'My status', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.changeswithindays.channel.title', 'Changes Within 5 Days', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.changeswithindays.blogs.label', 'Blogs', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.changeswithindays.noentries.message', 'No Entries', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.changeswithindays.wiki.label', 'Wiki', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.changeswithindays.forms.label', 'Forms', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.changeswithindays.documents.label', 'Documents', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.changeswithindays.discussion.label', 'Discussion', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.blogs.tab', 'Blogs', 'A', 0, 1, null);

update pn_property p set  
      p.PROPERTY_TYPE = 'text'        
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.schedule.fixoverallocations.newscheduleenddate.name';
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.doesnothavecreatedwiki.message', 'This {0} wiki doesn''t have wiki created.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.createit.link.label', 'Create it!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.createpagefromscratch.message', 'Create wiki page from scratch', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.chooseone.message', 'Or choose one from the following:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.partofwiki.message', 'Wiki page for selected item will be a part of appropriate project wiki.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.createwikifirst.message', 'Create project wiki first!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.objecthasnowiki.message', 'This object has no wiki pages!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.createwiki.link.label', 'Create wiki?', 'A', 0, 1, null);      

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.changeswithindays.newentry.message', 'new entry', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.dashboard.changeswithindays.newentries.message', 'new entries', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.wiki.title', 'Wiki', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.left.showattachments.tooltip', 'Show Attachments', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.edit.submit.button', 'Submit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.edit.preview.button', 'Preview', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.linksonthispage.title', 'Links on this page', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.nowikilinkpresent.message', 'There are no inner wiki link to existing wiki pages on this wiki page.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.loading.message', 'Loading...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.requestfailed.error.message', 'Server request failed please try again...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.popupwindow.close.button', 'Close', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.blankcontent.error.message', 'Wiki page content should not be blank.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.saving.message', 'Saving...', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.allocation.coulmn.label', 'Allocation{0}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.viewdetails.column.resource.label', 'Resource', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.belowzero.error.message', 'Total number of hours worked for any assignment cannot go below zero', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.calculateestimate.failure.message', 'Error occurred while calculating total estimated hours, please try again!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.endtimeafterstarttime.error.message', 'End time must be after Start time', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.serverrequest.failure.message', 'Server Request Failed..', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.historydetail.failure.message', 'Error occurred while getting history for selected assignment.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.internalserver.error.message', 'Internal Server Error!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.workcomplete.label', 'Work Complete', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.timesheet.workremaining.label', 'Work Remaining', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.finder.column.meta.externalprojectid', 'Project ID:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.finder.column.projectid', 'Project ID:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.finder.column.meta.projectmanager', 'Project Manager:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.finder.column.meta.programmanager', 'Program Manager:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.finder.column.meta.initiative', 'Initiative:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.finder.column.meta.functionalarea', 'Functional Area:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.finder.column.meta.projectcharter', 'Project Charter:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.finder.column.includeseveryone', 'Includes Projects where everyone is invited', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.finder.column.meta.typeofexpense', 'Type of Expense:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.channel.personalize.submit.label', 'Submit', 'A', 0, 1, null); 

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.ganttview.taskname.column.label', 'Task Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.ganttview.duration.column.label', 'Duration', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.ganttview.start.column.label', 'Start', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.ganttview.finish.column.label', 'Finish', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.ganttview.predecessors.column.label', 'Predecessors', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.ganttview.resourcenames.column.label', 'Resource Names', 'A', 0, 1, null);

update pn_property p set  
      p.property_value = 'Apply'        
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.schedule.main.applyfilters.message';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.teaminfo.label', 'Team Info', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.projectname.label', 'Project Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.projectdescription.label', 'Project Description', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.project.label', 'Project :', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.last.label', 'Last', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.blog.label', 'Blog', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.entry.label', 'Entry', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.saving.message', '{@prm.global.saving.message}', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.selectproject.errormessage', 'Please select a project before posting a blog entry.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.noprojectInlist.errormessage', 'There are no projects in the list', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.notsupported.errormessage', 'Blogit is not supported for selected object type', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.selectrowbeforepostingblog.errormessage', 'Please select a row before posting a blog entry.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.selectformrecordbeforepostingblog.errormessage', 'Please select a form record before posting a blog entry', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.cannotpostblogforurl.errormessage', 'You cannot post blog for URL.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.cannotpostblogforfolder.errormessage', 'You cannot post blog for folder.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.selectdocumentbeforeblogposting.errormessage', 'Please select a document before posting a blog entry.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.selectitembeforeblogposting.errormessage', 'Please select an item from the list', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.notsupportedtopage.errormessage', 'Currently BlogIt is not supported for this page', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.blogcomment.blank.validationmessage', 'Message should not be blank.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.notenterworkdone.message', 'Can not set work done, until some work complete entered.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.blogactivation.message', 'It is not possible to post blog entry without activating the blog. <br> Please first activate the blog by clicking on blog link from top menubar.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.loadingblogentry.errormessage', 'Error occurred while getting blog entries.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.activation.errormessage', 'Error occurred while activating blog', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.activateblogbyclickingbloglink.message', '<b>Blog activation failed. Please activate the blog by clicking on blog link from the navbar.</b>', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.savingcommentfailed.errormessage', 'Sorry, comment can not be saved. Some error encountered while saving.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.loadingrecentblogentry.message', 'Loading recent blogs...', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.deleteblog.unathorizeduserpermission.message', 'Unauthorized user, You do not have permission to delete this blog entry', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.delete.blogfailed.message', 'Sorry, deleting this blog entry is failed. Please try later.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.checkingandredirect.error.message', 'Some error occured while showing the object details. Please try again.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.noblogentryfoundfortask.message', 'No blog entry found for this task!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.noblogsfortask.message', 'Blog entries not found for task {0}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.tab.selectobject.message', 'Select any object from left pane to see corresponding blog entries.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.tab.noblogentriesobject.message', 'No blog entries for this object', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.tab.noblogsfound.message', 'Blog entries not found', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.tab.blogentriesshown.message', '{0} blog entries shown', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.tab.blogentriesfound.message', '{0} blog entries found', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.tab.oneblogentryfound.message', '1 blog entry found', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.lastblog.request.failure.message', 'Error occured while loading last blog entry.', 'A', 0, 1, null);

update pn_property p set  
      p.property_value = 'Blog-it'
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'all.global.toolbar.standard.blogit.alt';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.editprofileinfo.alt', 'Edit Profile Info', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.uploadpicture.alt', 'Upload Picture', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.removepicture.alt', 'Remove Picture', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.skypestaus.alt', 'My Status', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.personalimageupload.uploadimage.title', 'Upload Image', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.personalimageupload.upload.caption', 'Upload', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.personalimageupload.cancel.caption', 'Cancel', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.personal.profile.mypicture.alt', 'My Picture', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.groupheading.workcalendar.label', 'Work Calendar', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workweekmodify.defineworkweek.link', 'Define Work Week', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workweekmodify.modifyworkweek.link', 'Modify Work Week', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workweekmodify.usedefaultweek.link', 'Use Default Week', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workdate.newdate.link', 'New Date', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workdate.modifydate.link', 'Modify Date', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workdate.removedate.link', 'Remove Date', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.workdate.workweek.link', 'Work Week', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.workcalendar.individualdates.link', 'Individual Dates', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.groupheading.icalendar.label', 'iCalendar', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.icalendar.export.label', 'Export', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.icalendar.viewurl.label', 'View URL', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.document.groupheading.label', 'Document', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.filter.invaliddate.message', 'Invalid Date.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.blogpost.noselection.message', 'Please select a node from tree before posting a blog entry.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.filters.duedate.emptytext', 'Due Date', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.filters.startdate.emptytext', 'Start Date', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.filters.dates.label', 'Dates', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.filters.invaliddateformat.errormessage', 'Current date is not a valid date. It must be in the format {0}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.filters.invalidstartdate.errormessage', 'Invalid start date!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.filters.invalidduedate.errormessage', 'Invalid due date!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.filters.emptyduedate.errormessage', 'Please select due date.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.filters.emptystartdate.errormessage', 'Please select start date.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.filters.startdateafterduedate.errormessage', 'Start date can not be after due date.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.newassignment.clicktoedit.tooltip', 'Click to edit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.newassignment.project.label', 'Project :', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.calendartimesheet.weeklytimsheet.link', 'Weekly Timesheet', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.calendartimesheet.view.button', 'View', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.calendartimesheet.total.label', 'Total', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.calendartimesheet.monthtotal.label', 'Month Total :', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.interfacesettings.startingpage.personalassignments.option', 'Personal Assignments', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.interfacesettings.startingpage.personalworkspace.option', 'Personal Workspace', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.interfacesettings.startingpage.business.option', 'Business', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.interfacesettings.startingpage.project.option', 'Project', 'A', 0, 1, null);

update pn_property p set  
      p.property_value = 'You can not edit template !'
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.methodology.template.used.message';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.loadinginviteefailure.errormessage', 'Error occurred while loading invitee', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.addingnewinviteefailure.errormessage', 'Error occurred while adding new invitee', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.searchinginviteefailure.errormessage', 'Error occurred while searhing invitee', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemembers.addingspacefailure.errormessage', 'Error occurred while adding project or sub business', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemembers.changedirectoryloadmemberfailure.errormessage', 'Error occurred while while changing directory to load members', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemembers.deletespacefailure.errormessage', 'Error occurred while deleting project or sub business', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemembers.loadmembertoremovefailure.errormessage', 'Error occurred while adding member for remove from space', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemembers.removememberfailure.errormessage', 'Error occurred while removing member from space', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemembers.removespacefailure.errormessage', 'Error occurred while remove project or sub business', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemembers.addingspacefailure.errormessage', 'Error occurred while adding project or sub business', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.movinginviteefailure.errormessage', 'Error occurred while moving invitee', 'A', 0, 1, null);


update pn_property p set  
      p.property_value = '0'
where
      p.context_id = 2000 and
      p.language = 'en' and 
      p.property = 'prm.business.reports.isenabled';

update pn_property p set  
      p.property_value = '0'
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.global.footer.copyright.newline';     

update pn_property p set  
      p.property_value = '1'
where
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.project.viewproject.showteammateonlinestatus.isenabled'; 


update pn_property p set  
      p.property_value = '200'
where
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.project.visibility.defaultvalue'; 

update pn_property p set  
      p.property_value = '4'
where
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.global.profile.username.minsize'; 

update pn_property p set
      p.property_value = '1'
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
 
update pn_property p set
      p.property_value = '0'
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.global.form.sharing.isenabled';


update pn_property p set  
      p.property_value = '0'
where  
      p.context_id = 2000 and 
      p.language = 'en' and 
      p.property = 'prm.global.skype.isenabled'; 

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.create.wizard.step2.invalidpostalcode.message', 'Please enter valid postal code', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.create.wizard.step2.invalidphoneno.message', 'Please enter valid phone no.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.create.wizard.step2.invalidfaxno.message', 'Please enter valid fax no.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.blogit.loading.message', '{@prm.global.loading.message}', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.edit.include.creatoremailaddress.errormsg', 'Your email must be a valid address. <br/>eg: yourname@domain.com', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.resource.timesheet.empty.excelsheet.errormessage', 'No assignments found to export!', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.indentedtaskmoveup.error.message', 'Unable to perform the "Move Task Up" for an indented task', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.removemembers.nospacepresentforsearchmember.errormessage', 'There is no project or sub business exist to search members.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.directory.invitemember.alreadyinvitedemail.errormessage', 'Member having this email address already invited', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.calendar.meeting.addattendees.selectoneattendee.message', 'You must select at least one attendee', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.news.newsedit.topic.errormessage', 'Please enter news topic', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.confirm.message', 'Confirm', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.delete.button', 'Delete', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.close.button', 'Close', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.deleteImage.suretodelete.message', 'Are you sure you want to delete this ?', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.deletingattachmentfailed.message', 'Deleting the attachment failed. Please try again after some time.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.leftheader.label', 'Projects', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.groupheader.label', 'Project Portfolios', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.blog.selectproject.message', 'Select any project from left to see corresponding blog entries.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.selectproject.message', 'Select any project from left to see corresponding wiki entries.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.form.designer.fieldedit.maxlength.change.message', 'Max length of field can not be increased', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY) 
values (2000,'en','text','prm.blog.addweblogentry.htmlvalidation.message','Please close the missing end tag in content','','A',0,1);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.viewsubscription.details.by.label', 'by', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.name.maxim.custom', 'Die ID (XX01Z)/Project ID', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.id.maxim.custom', 'GUID', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.export.csv.main.message', 'With this option you will export all the Tasks from all Projects and Sub Projects of the selected Business.', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.export.csv.main.delay.message', 'The Process could take several minutes, please wait.', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.description.maxim.custom', 'Brief Description', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.maxim.part.number.maxim.custom', 'Maxim Part Number', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.fab.process.maxim.custom', 'FAB Process', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.die.type.family.maxim.custom', 'Die Type Family', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.product.line.maxim.custom', 'Product Line', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.package.maxim.custom', 'Package', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.project.type.maxim.custom', 'Project Type', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.pass.maxim.custom', 'Pass', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.code.fab.maxim.custom', 'Code/FAB', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.intro.quarter.maxim.custom', 'Intro Quarter', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.die.rev.maxim.custom', 'Die Rev', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.overall.status.maxim.custom', 'Overall Status', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.csv.taskid', 'Task Id', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.csv.name.custom', 'Task Name', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.csv.critical', 'Is Critical', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.csv.datecreated', 'Created Date', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.csv.datestart', 'Start Date', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.csv.datefinish', 'End Date', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.csv.duration', 'Duration (hours)', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.csv.percentcomplete', '% Percent Complete', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.csv.work', 'Work', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.export.csv.currentestimatedtotalcost', 'Estimated Total Cost', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.export.csv.actualstartdate', 'Actual Start Date', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.export.csv.actualfinish', 'Actual Finish', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.export.csv.duration', 'Duration (hours)', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.export.csv.actualcost', 'Actual Cost', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.export.csv.remainingduration', 'Remaining Duration (hours)', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.export.csv.baseline.start.date', 'Baseline Start Date', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.export.csv.baseline.end.date', 'Baseline End Date', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.export.csv.baseline.work.duration', 'Baseline Work Duration (hours)', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.business.id', 'Business Id', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.business.name.custom', 'Business Name', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.id', 'Project Id', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.task.export.project.name.custom', 'Project Name', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.export.tasks', 'Export Tasks (CSV)', 'A', 0, 1, null);
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.export.tasks.alt', 'Export Tasks (CSV)', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.providerport.value', '', 'A', 1, 0, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.ldapauthenticationattributename.value', '', 'A', 1, 0, null); 
 
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'boolean', 'prm.domains.ldapcosignconfiguration.isenabled', '0', 'A', 1, 0, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.ldapprotocol.label', 'LDAP Protocol:', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.initialcontextfactoryname.label', 'Initial Context Factory Name:', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.providerhostname.label', 'Provider Host Name:', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.protocol.value', '', 'A', 1, 0, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.initialcontextfactory.value', 'com.sun.jndi.ldap.LdapCtxFactory', 'A', 1, 0, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.providerhost.value', '', 'A', 1, 0, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.providerportnumber.label', 'Provider Port Number:', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.ldapauthenticationattributename.label', 'LDAP Authentication Attribute Name:', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.enableldapcosign.label', 'Enable CoSign Login:', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.ldapauthenticationattributevalue.label', 'LDAP Authentication Attribute Value:', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.ldapauthenticationattributevalue.value', '', 'A', 1, 0, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.ldaproot.label', 'LDAP Search Root:', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.ldaproot.value', '', 'A', 1, 0, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.ldapfilterattribute.label', 'LDAP Filter Attribute:', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.ldapfilterattribute.value', '', 'A', 1, 0, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'boolean', 'prm.domains.cosigninternalfilter.isenabled', '0', 'A', 1, 0, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.cosignloginserverurl.label', 'Cosign Login Server Url', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.cosignloginserverurl.value', '', 'A', 1, 0, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.cosignlogouturl.label', 'Cosign Logout Url', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.ldapcosignconfiguration.cosignlogouturl.value', '', 'A', 1, 0, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.tab.createdomain.label', 'Create Domain', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.tab.editdomain.label', 'Edit Domain', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.tab.directoryproviderconfiguration.label', 'Directory Provider Configuration', 'A', 0, 1, null); 
 
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.domains.tab.ldapco-signconfiguration.label', 'LDAP Co-Sign Configuration', 'A', 0, 1, null); 

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'boolean', 'prm.domains.cosignexternalfilter.isenabled', '0', 'A', 1, 0, null); 

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB) 
values (2000, 'en', 'text', 'prm.business.main.exportexcel.link', 'Export Tasks (Excel)', 'A', 0, 1, null); 

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.domains.samlexternalsso.isenabled', '0', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.domains.samlconfiguration.isenabled', '0', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.domains.tab.samlconfiguration.label', 'SAML Configuration', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.domains.samlconfiguration.enablesaml.label', 'Enable SAML Login:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.domains.samlconfiguration.samlloginserverurl.label', 'SAML Login Server URL:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.domains.samlconfiguration.samlloginserverurl.value', '', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.domains.samlconfiguration.samllogouturl.label', 'SAML Logout URL:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.domains.samlconfiguration.samllogouturl.value', '', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = '1'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.business.reports.isenabled';

commit;

prompt 0 records loaded
prompt Enabling foreign key constraints for PN_PROPERTY...
alter table PN_PROPERTY enable constraint PN_PROPERTY_FK2;
set feedback on
set define on
prompt Done.
