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



update pn_property p set 
       p.property_value = '1'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.reports.isenabled';

update pn_property p set 
       p.property_value = '1'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.timesubmital.report.showassignment.isenabled';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.leftheader.label', 'Projects', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.groupheader.label', 'Project Portfolios', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.blog.selectproject.message', 'Select any project from left to see corresponding blog entries.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.wiki.selectproject.message', 'Select any project from left to see corresponding wiki entries.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription2.status.label', 'Status:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription2.statusenable.label', 'Enabled', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription2.statusdisable.label', 'Disabled', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription2.description.label', 'Description', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription2.selectevent.label', 'Actions to be Notified About:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription2.moreoption.label', 'More Options', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription2.lessoption.label', 'Less Options', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription2.submitbutton.label', 'Submit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription2.cancelbutton.label', 'Cancel', 'A', 0, 1, null);

update pn_property p set 
      p.property_value = 'New Notification:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.createsubscription2.channel.create.title';

update pn_property p set 
      p.property_value = '(Seperate by commas)'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.editsubscription.separate.text';

update pn_property p set 
      p.property_value = 'Name of Notification:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.createsubscription2.name.label';

update pn_property p set 
       p.property_value = 'Manage your notifications'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.personal.setup.notifications.description';

update pn_property p set
	   p.property_type = 'text',
       p.property_value = 'Manage your notifications for this business'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.business.setup.notification.label';

update pn_property p set 
       p.property_value = 'Manage your notifications for this project'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.managepersonalnotifications.label';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.button.recalculate.caption', 'Recalculate', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.gridheader.projectname', 'Project Name', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.gridheader.percentcomplete', '% Work Complete', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.gridheader.startdate', 'Start Date', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.gridheader.finishdate', 'Finish Date', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.gridheader.overallstatuscode', 'O', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.gridheader.financialstatuscode', 'F', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.gridheader.schedulestatuscode', 'S', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.gridheader.resourcestatuscode', 'R', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.editsubscription.status.label', 'Status:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.editsubscription.statusenable.label', 'Enabled', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.editsubscription.statusdisable.label', 'Disabled', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.editsubscription.createdate.label', 'Created', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.editsubscription.modified.label', ', Modified', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Edit Notification'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.notification.editsubscription.channel.edit.title';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.editsubscription.itemsnotified.label', 'Item(s) to be Notified About:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.editsubscription.itemlocation.label', 'Item location:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.editsubscription.itemtype.label', 'Item Type:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.editsubscription.itemname.label', 'Item Name:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.editsubscription.actionnotified.label', 'Actions to be Notified About:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.editsubscription.button.submit.label', 'Submit', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.editsubscription.selectaddmembers.label', 'Select Additional Team Members or Enter External E-mail Addresses to Send This Notification to:', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Name of Notification'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.notification.editsubscription.name.label';

update pn_property p set 
       p.property_value = 'E-mail'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.notification.deliverytype.email.name';
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.category.general.label', 'General', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.category.status.label', 'Status', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.category.calculated.label', 'Calculated', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Workplan Column Settings'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.schedule.customizecolumn.page.title';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription2.actiontonotifiedrequired.message', 'Actions to be Notified About is a required field', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfoliio.gridheader.status', 'Status', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.toolbar.standard.personalizepage.label', 'Personalize Page', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Manage Notifications'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.global.notification.main.managesubscriptions.2.link';

update pn_property p set 
       p.property_value = 'Add or manage notifications  for this {0}.'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.global.notification.main.managesubscriptions.description';

update pn_property p set 
       p.property_value = 'Create a Type Notification'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.global.notification.main.createsubscriptions.2.link';

update pn_property p set 
       p.property_value = 'Create a notification by module for this {0}.'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.global.notification.main.createsubscriptions.description';

update pn_property p set 
       p.property_value = 'Run the Notification Scheduler'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.project.notification.label';

update pn_property p set 
       p.property_value = 'Edit Notification'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.notification.managesubscriptions.edit.tooltip';

update pn_property p set 
       p.property_value = 'New Notification'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.notification.managesubscriptions.create.tooltip';

update pn_property p set 
       p.property_value = 'Delete Notification'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.notification.managesubscriptions.delete.tooltip';

update pn_property p set 
       p.property_value = 'Manage Notifications'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.notification.managesubscriptions.channel.manage.title';

update pn_property p set 
       p.property_value = 'Notification Details'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.notification.viewsubscription.channel.details.title';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.setup.saveastemplates.link', 'Save as Template', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.setup.templatescreatefromproject.label', 'Create a project template from this project', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.saveastemplate.link', 'Save as Template', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.saveastemplate.label', 'Create a business template from this business', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.managetemplates.link', 'Manage Templates', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.managetemplatespnet.description', 'View, edit and create business and project templates owned by this business', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.managetemplatespnet.expandall', 'Expand All', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.managetemplatespnet.collapseall', 'Collapse All', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.main.edit.button.tooltip', 'Edit Template', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Notification'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.notification.managesubscriptions.list.subscription.column';

update pn_property p set 
       p.property_value = 'Create New Notification'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.notification.createtypesubscription1.module.history';

update pn_property p set 
       p.property_value = 'Edit Notification'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.notification.editsubscription.module.history';

update pn_property p set 
       p.property_value = 'There are currently no notifications'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.notification.managesubscriptions.list.none.message';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.toolbox.heading.title', 'Views', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.toolbox.item.exportpdf.label', 'Export to pdf', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription2.selectevent.new.label', 'Item and Actions to be Notified About:', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Details'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.twopaneview.tab.detail.caption';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.twopaneview.blog.paging.entries.label', 'Entries {0} - {1} of {2}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.twopaneview.blog.paging.next.label', 'Next {0}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.twopaneview.blog.paging.prevoius.label', 'Previous {0}', 'A', 0, 1, null);
      
update pn_property p set 
      p.property_value = 'Name of Notification:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.createsubscription2.name.label';

update pn_property p set 
      p.property_value = 'New Notification'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.createsubscription2.channel.create.title';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.toolbox.item.exportcsv.label', 'Export to CSV', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.wiki.create.description', 'New', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.wiki.uploadimage.description', 'Image Uploaded', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.delivery.blog_name', 'Blog Name:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.delivery.blog_url', 'Blog Url:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.delivery.wiki_page_name', 'Wiki Page Name:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.delivery.wiki_page_url', 'Wiki Page Url:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.objecttype.blog.name', 'Blog', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.blog.new.description', 'New', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.blog.edited.description', 'Edited', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.blog.deleted.description', 'Deleted', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.blog.commented.description', 'Commented', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.blog.new.defaultmessage', 'New blog entry created', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.blog.edited.defaultmessage', 'Blog entry edited', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.blog.deleted.defaultmessage', 'Blog entry deleted', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.blog.commented.defaultmessage', 'Blog entry commented', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.objecttype.wiki.name', 'Wiki', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.wiki.new.description', 'New', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.wiki.edited.description', 'Edited', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.wiki.deleted.description', 'Deleted', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.wiki.create.defaultmessage', 'New wiki page created', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.wiki.edited.defaultmessage', 'Wiki page edited', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.wiki.deleted.defaultmessage', 'Wiki page deleted', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.wiki.uploadimage.defaultmessage', 'Wiki page image uploaded', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.projectedited.description', 'Project Edited', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.projectdeleted.description', 'Project Deleted', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.projectedited.defaultmessage', 'Project edited', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.projectdeleted.defaultmessage', 'Project deleted', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.delivery.project_name', 'Project Name:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.delivery.project_url', 'Project URL:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.customizecolumn.page.title', 'Project Portfolio Column Settings', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.column.category.general.lable', 'General', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.column.category.completion.lable', 'Completion', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.column.category.financial.lable', 'Financial', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.column.category.status.lable', 'Status', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.toolbox.item.savecurrentsettings.label', 'Save Current Settings', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.toolbox.item.deletesavedview.label', 'Delete Saved View', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.saveview.page.title', 'Save Current Settings as a View', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.saveview.page.viewname.lable', 'View Name:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.saveview.page.makedefaultview.lable', 'Make Default View:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.saveview.page.onmyprojectportfolio.lable', 'On My Project Portfolio', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.saveview.page.onmypersonaldashboard.lable', 'On My Personal Dashboard', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.popup.button.submit.caption', 'Submit', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.popup.button.cancle.caption', 'Cancle', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.error.blankviewname.message', 'Please enter View Name', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.participantinvited.description', 'Participant Invited', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.participantedited.description', 'Participant Edited', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.participantinvited.defaultmessage', 'Participant invited', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.type.participantedited.defaultmessage', 'Participant edited', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.delivery.participant_name', 'Participant Name:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.delivery.participant_url', 'Participant URL:', 'A', 0, 1, null);

update pn_property p set 
      p.property_value = 'Participant Deleted'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.participantdeletion.description';

update pn_property p set 
      p.property_value = 'Participant Deleted'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.participantdeletion.default.message';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.modifyview.confirmprompt.title', 'Confirm Modify View', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.modifyview.confirmprompt.message', 'Do you want to replace {0} with new settings', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.deleteview.confirmprompt.title', 'Confirm Delete View', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.deleteview.confirmprompt.message', 'Are you sure you want to delete the saved portfolio view {0} ?', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.saveview.page.makeviewshared.lable', 'Make This a Shared view', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.saveview.page.owningbussiness.lable', 'Owning Business:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.saveview.page.visibleto.lable', 'Visible to:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.saveview.page.alluser.lable', 'All Users', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Edit the properties of a document or folder'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.modifydocumentproperties.description';

update pn_property p set 
       p.property_value = 'New Form'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.createform.description';

update pn_property p set 
       p.property_value = 'Edit Form'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.modifyform.description';

update pn_property p set 
       p.property_value = 'New Form Data'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.createformdata.description';

update pn_property p set 
       p.property_value = 'Edit Form Data'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.modifyformdata.description';

update pn_property p set
	  p.property_value = 'New Discussion Group'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.notification.type.creatediscussiongroup.description';

update pn_property p set 
       p.property_value = 'Edit Discussion Group'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.modifydiscussiongroup.description';


update pn_property p set 
       p.property_value = 'New Post'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.createpost.description';

update pn_property p set 
       p.property_value = 'New Reply'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.createreply.description';

update pn_property p set 
       p.property_value = 'Edit Post'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.modifypost.description';

update pn_property p set 
       p.property_value = 'New Task'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.createtask.description';

update pn_property p set 
       p.property_value = 'Edit Task'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.type.modifytask.description';

update pn_property p set 
       p.property_value = 'Cancel'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.portfolio.popup.button.cancle.caption';

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.mainpage.selectview.label', 'Your Portfolio Views:', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.mainpage.selectsharedview.label', 'Shared Portfolio Views:', 'A', 0, 1, null);
     
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.mainpage.sharedviewlist.noneavailable.itemtext', '(none available)', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.error.selectbusiness.message', 'Please select Business from list to make view sharable', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.error.checkalluser.message', 'Please select All Users to make view sharable among Business members ', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.mainpage.viewlist.defaultview.itemtext', 'Project Tree (default)', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.mainpage.viewlist.sharedviewnamesuffix.text', '(shared)', 'A', 0, 1, null);

update pn_property p set
	  p.property_value = 'Select Item Type'
where 
	  p.context_id=2000 and
	  p.language = 'en' and
      p.property = 'prm.notification.createtypesubscription1.selectobjecttype.option';
      
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.error.viewnamecontainssharedword.message', 'View name should not contain {0} word', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.error.duplicateviewname.message', 'View already exists with same name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.twopaneview.cusomizecolumntooltip.message', 'Cusomize your column visibility settings.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.twopaneview.collapsealltooltip.message', 'Collapse All', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.twopaneview.expandalltooltip.message', 'Expand All', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.managetemplatespnet.methodologylist', 'Methodology List', 'A', 0, 1, null);

update pn_property p set
	  p.property_value = 'Notification Properties'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.notification.viewsubscription.channel.details.title';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.global.twopaneview.link.filters.lable', 'Filters', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.error.defaultviewfiltersapplied.message', 'Filters can not be applied on default view', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.column.projectname.lable', 'Project Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.column.description.lable', 'Description', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.column.finishdate.lable', 'Finish Date', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.create.business', 'Business', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.create.project', 'Project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.property.owner.business', 'Owning Business', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.methodology.visibleto.label', 'Visible to', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.methodology.allusers.label', 'All Users', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.create.includedmodules', 'Included Modules', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.main.newbusiness.button.tooltip', 'New Business', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.main.newproject.button.tooltip', 'New Project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.main.managetemplates.heading', 'Manage Templates', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.methodology.dialog.saveastemplate.label', 'Save as Template', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.create.success.message', 'A template named {0} was created in business {1}!', 'A', 0, 1, null);
							
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.methodology.dialog.submit.button', 'Submit', 'A', 0, 1, null);
									
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.methodology.dialog.cancel.button', 'Cancel', 'A', 0, 1, null);
									
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.methodology.dialog.templatecreated.message', 'Template Created', 'A', 0, 1, null);
							
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.methodology.dialog.templatesavingerrors.message', 'Methodology Saving encountered errors!', 'A', 0, 1, null);
	
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.methodology.dialog.ajaxrequesterror.heading.message', 'Ajax Request Error!', 'A', 0, 1, null);
						
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.methodology.dialog.ajaxrequesterror.message', 'Server Ajax Request Failure!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.main.channel.edit.title', 'Edit Template', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.properties.templateapplied.label', 'Template Applied', 'A', 0, 1, null);

update pn_property p set
	  p.property_value = 'Status'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.portfolio.column.overallstatus.title';
      
update pn_property p set
	  p.property_value = '% Work Complete'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.propertiesedit.completion.label';

update pn_property p set
	  p.property_value = 'Overall Status Code (O)'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.properties.status.label';
      
update pn_property p set
	  p.property_value = 'Overall Financial Code (F)'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.properties.financialstatus.label';

update pn_property p set
	  p.property_value = 'Overall Schedule Code (S)'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.properties.schedulestatus.label';

update pn_property p set
	  p.property_value = 'Overall Resource Code (R)'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.properties.resourcestatus.label';
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.portfolio.link', 'Manage Portfolios', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.portfolio.label', 'View and delete shared portfolio views saved in this business', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.manageview.viewtable.heading', 'Portfolio Views Saved in this Business', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.manageview.deleteview.lable', 'Delete View', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.manageview.viewtable.header.viewname.label', 'Portfolio View Name', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.manageview.viewtable.header.createdby.label', 'Created By', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.manageview.viewtable.header.datelastmodified.label', 'Date Last Modified', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.manageview.viewtable.bottom.totalview.label', 'Total', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.error.makeviewshared.message', 'Please select a business from list or check All Users to make this view sharable', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.global.view.visibility.isenabled', '{@prm.global.globalvisibility.isenabled}', 'A', 1, 0, null);

update pn_property p set
	  p.property_value = 'Customer'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.properties.sponsor.label';

update pn_property p set
	  p.property_value = 'Priority'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.properties.priority.label';

update pn_property p set
	  p.property_value = 'Risk Rating'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.properties.risk.label';

update pn_property p set
	  p.property_value = 'Budgeted Total Cost'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.properties.budgetedtotalcost.label';

update pn_property p set
	  p.property_value = 'Budgeted Total Cost Currency'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.portfolio.finder.column.budgetedtotalcostcurrency';

update pn_property p set
	  p.property_value = 'Current Estimated Total Cost'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.properties.currentestimatedtotalcost.label';

update pn_property p set
	  p.property_value = 'Actual Cost To Date'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.properties.actualcosttodate.label';    

update pn_property p set
	  p.property_value = 'Estimated ROI'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.properties.estimatedroi.label';    

update pn_property p set
	  p.property_value = 'Cost Center'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.project.properties.costcenter.label'; 
  
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.error.viewmodified.message', 'Current view has been changed', 'A', 0, 1, null);
  
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.manageview.deletionnotallowed.message', 'Sorry! You do not have permission to delete this view', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.manageview.error.viewnotavailabletodelete.message', 'Shared views are not available in this business to delete', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.manageview.deleteview.label', 'Delete View', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.column.templateapplied.lable', 'Template Applied', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.saveview.page.error.sharedviewmodification.message', 'Shared view cannot be modified, Please enter another view name', 'A', 0, 1, null);

update pn_property p set
	  p.property_value = 'Additional External Email Addresses Receiving This Notification:'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.notification.viewsubscription.details.externalemail.label';
      
update pn_property p set 
      p.property_value = '(Separate by commas)'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.editsubscription.separate.text';

update pn_property p set 
      p.property_value = 'You must select the Item type'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.createtypesubscription1.selectobjecttype.message';

update pn_property p set 
      p.property_value = 'Notification Properties'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.viewsubscription.module.history';

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

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.managesubscriptions.editnotification', 'You do not have permission to edit this notification', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.managesubscriptions.deletenotification', 'You do not have permission to delete this notification', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.notask.message', 'Task not exist', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskedit.warning.scheduledatechagne.message', 'This change will move schedule {0} date to {1}. Press OK to continue, or Cancel to revert the changes.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskedit.warning.workcompleteediting.message', 'Changes of work complete amount will not to add for any resource. You should capture work for the task instead of directly editing it. Press OK to capture work, or Cancel to ignore this message.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.error.viewchangeonmodifiedview.message', 'Current view has been modified, Do you wish to continue without saving the changes?', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.project.portfolio.error.blognotfoundforproject.message', 'Blogs not found for selected project', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.portfolio.export.image.over', '/images/icons/form-export_over.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.portfolio.export.image.on', '/images/icons/form-export_on.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.portfolio.savecurrentsettings.image.over', '/images/icons/project-personalize-page_over.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.portfolio.savecurrentsettings.image.on', '/images/icons/project-personalize-page_on.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskedit.warning.nonworkingday.message', 'You moved {0} to {1} on nonworking day. ({2})', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskedit.warning.nextworkingday.message', 'Move {0} to the next working day.({1})', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskedit.warning.makeworkingday.message', 'Make {0} a working day', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskedit.warning.warning.title', 'Workplan Warning', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.chargecode.link', 'Manage Charge Codes', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.inlineeditwarning.label', 'Enable inline editing warning message', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.setup.chargecode.label', 'Create, edit or delete charge codes for this business', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.unassignedworkcapture.label', 'Enable Un-assigned task work capture', 'A', 0, 1, null);


update pn_property p set 
      p.property_value = 'Description:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.createsubscription2.description.label';
      
update pn_property p set 
      p.property_value = 'Created:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.editsubscription.createdate.label';
      
update pn_property p set 
       p.property_value = 'Name of Notification:'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.editsubscription.name.label';
      
update pn_property p set 
       p.property_value = 'Are you sure you want to delete this notification?'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.setup.notificationremovalwarning.message';

update pn_property p set 
       p.property_value = 'Notifications'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.notification.managesubscriptions.module.history';

update pn_property p set
	  p.property_value = 'Apply Template'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.template.main.managetemplates.heading';
      

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.hoursperday.mumericrequired.message', 'Hours Per Day should not be empty or non numeric value.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.hoursperweek.mumericrequired.message', 'Hours Per Week should not be empty or non numeric value.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.dayspermonth.mumericrequired.message', 'Days Per Month should not be empty or non numeric value.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.hoursperday.range.message', 'Hours Per Day value should be in range 0 to 24.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.hoursperweek.range.message', 'Hours Per Week value should be in range 0 to 168.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.daysspermonth.range.message', 'Days Per Month value should be in range 0 to 30', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.workingtimes.required.message', 'Working Times values should not be empty.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.hoursperday.label', 'Hours Per Day:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.hoursperweek.label', 'Hours Per Week:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.dayspermonth.label', 'Days Per Month:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.workingtimes.channltitle', 'Working Times', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.resourcecalendar.label', 'For Task Assignment Calculation:', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.resourcecalendar.personaloption.label', 'Use resource personal working time calendar.', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.properties.resourcecalendar.scheduleoption.label', 'Use resource schedule working time calendar.', 'A', 0, 1, null);

update pn_property p set
	  p.property_value = 'Templates'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.global.tool.template.name';

update pn_property p set
	  p.property_value = 'Template Name is a required field'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.template.templifyspace.namerequired.message';

update pn_property p set
	  p.property_value = 'Description'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.template.property.name.description';
      
update pn_property p set
	  p.property_value = 'Owning Business'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.template.main.owner';
      
update pn_property p set
	  p.property_value = 'Template Properties'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.template.main.channel.properties.title';
      
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.templifyspace.includedmodulesrequired.message', 'Included Modules is a required field', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.templifyspace.templatetype', 'Template Type', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.workingtimecalendar.defaultbase.name', '{0} Base Calendar', 'A', 0, 1, null);
      
insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.chargecode.leftheader.viewchargecode.label', 'View Charge Codes', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.chargecode.create.button.label', 'New Charge Code', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.chargecode.modify.button.label', 'Edit Charge Code', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.chargecode.delete.button.label', 'Delete Charge Code', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.chargecode.chargecodetable.header.chargecodeno.label', 'Charge Code no.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.chargecode.chargecodetable.header.chargecodename.label', 'Charge Code Name', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.chargecode.chargecodetable.header.chargecodedescription.label', 'Charge Code Description', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.chargecode.save.page.title', 'Save Charge Code', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.chargecode.edit.page.title', 'Edit Charge Code', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.popup.button.submit.caption', 'Submit', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'all.global.popup.button.cancle.caption', 'Cancel', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.chargecode.label', 'Charge Code', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.list.chargecode.column', 'Charge Code', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.business.chargecode.dropdownlist.defaultoption.label', 'none', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.notification.createsubscription2.notificationsavingfailed.message', 'Notification saving failed... please try again.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.assignments.blogpost.noprojectortaskavailable.message', 'No project or task is available to post blog.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.twopaneview.tab.wiki.header.wikipagenotfound.message', 'Wiki page not found for {0}.', 'A', 0, 1, null);

update pn_property p set
	  p.property_type = 'text'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.global.integerrequired.validation.message';

update pn_property p set
	  p.property_type = 'text'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.schedule.fixoverallocations.returntoassignmentspage.label';

update pn_property p set
	  p.property_type = 'text'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.workflow.step.sequence.integerrequired.validation.message';
      
update pn_property p set
	  p.property_type = 'imagepath'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'all.global.toolbar.channel.help.image.on';

update pn_property p set
	  p.property_value = 'Delete Link'
where 
	  p.context_id = 2000 and
	  p.language = 'en' and
      p.property = 'prm.personal.wiki.menu.unassignbutton.message';

update pn_property p set 
       p.property_value = 'Copyright 2000-2010 Project.net, Inc.',
       p.property_value_clob = 'Copyright 2000-2010 Project.net, Inc.'
where 
      p.context_id=2000 and
      p.language = 'en' and
      p.property = 'prm.global.footer.copyright';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.blogentryfound.message', 'blog entry found', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.blogentriesfound.message', 'blog entries found', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.noblogentriesfound.message', 'Blog entries not found', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.viewblog.noblogentriesfoundfortask.message', 'Blog entries not found for selected task', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.collapseall.image.on', '/images/icons/toolbar-gen-collapse_on.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.collapseall.image.over', '/images/icons/toolbar-gen-collapse_over.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.expandall.image.on', '/images/icons/toolbar-gen-expand_on.gif', 'A', 0, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'imagepath', 'all.global.toolbar.standard.expandall.image.over', '/images/icons/toolbar-gen-expand_over.gif', 'A', 0, 0, null);

update pn_property p set
       p.property_value = '(First Name, Last Name)'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.directory.invitemember.lastnamefirsename.label';
      

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.schedule.toolbar.phase.isenabled', '0', 'A', 1, 0, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.domain.profile.title', 'Domain Profile', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.domain.profile.localerequired.message', '{@prm.registration.userprofile.localerequired.message}', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.security.editrole.add.people.select.message', 'Please select at least one item from the People list to add!', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.security.editrole.add.roles.select.message', 'Please select at least one item from the Roles list to add!', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.security.editrole.add.noitems.message', 'There are no items in the list to be added!', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.security.editrole.add.selecttab.message', 'Please select People or Roles tab to be able to select items for adding!', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.invalidtaskname.message', 'Invalid task name.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.schedule.taskview.errorstoringtask.message', 'Error storing task.', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.registration.userprofile.invalidphoneno.message', '{@prm.business.create.wizard.step2.invalidphoneno.message}', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.global.configuration.overwrite-httpscheme', '0', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.deleteimage.confirmation.message', 'Are you sure you want to delete this image?', 'A', 0, 1, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.wiki.deleteimage.confirmation.title', 'Confirm', 'A', 0, 1, null);

update pn_property p set 
       p.property_value = 'Project.net Notification <noreply@{@prm.global.brand.defaulthost}>'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.global.default.email.fromaddress';

update pn_property p set
       p.property_value = 'Are you sure you want to capture work on a non-working day?'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.resource.timesheet.nonworkingwork.errormessage';

update pn_property p set
       p.property_value = 'Jump to'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.wiki.index.all.label';

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'boolean', 'prm.global.business.managechargecode.isenabled', '0', 'A', 1, 0, null);

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)  
values (2000, 'en', 'text', 'prm.business.main.exportexcel.link', 'Export Tasks (Excel)', 'A', 0, 1, null);  

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)  
values (2000, 'en', 'text', 'prm.project.portfolio.customizecolumn.multilevelsort.label', 'Multi-level Sorting', 'A', 0, 1, null);  

insert into pn_property (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)  
values (2000, 'en', 'text', 'prm.project.portfolio.customizecolumn.multilevelsort.warning', 'If you sort by a column in the table by clicking on a table header, this multi level sorting will be turned off.', 'A', 0, 1, null);  

update pn_property p set
       p.property_value = 'Project Name'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.portfolio.finder.column.name';
      
update pn_property p set
       p.property_value = 'Business Owner'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.portfolio.finder.column.owningbusiness';
      
update pn_property p set
       p.property_value = 'Finish Date'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.portfolio.finder.column.enddate';
      
update pn_property p set
       p.property_value = '% Work Complete'
where 
      p.context_id = 2000 and
      p.language = 'en' and
      p.property = 'prm.project.portfolio.finder.column.percentcomplete';

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

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.create.fail.message', 'Tempate named {0} was created in business {1}, but it may not contain all the expected data!', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.template.create.fail.title', 'Error occured during template creation', 'A', 0, 1, null);

commit;
prompt Enabling foreign key constraints for PN_PROPERTY...
alter table PN_PROPERTY enable constraint PN_PROPERTY_FK2;
set feedback on
set define on
prompt Done.
