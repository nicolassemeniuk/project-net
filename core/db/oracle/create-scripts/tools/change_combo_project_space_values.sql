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
/* First Deletes all the values from the Combos */
prompt 
prompt First Deletes all the values from the Combos
prompt 

delete from PN_PROJECT_SPACE_META_COMBO;

/* Add Specific Values For the First Combo Value of the Property Project */
prompt 
prompt Add Specific Values For the First Combo Value of the Property Project
prompt 
insert into PN_PROJECT_SPACE_META_COMBO(PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) VALUES (7, 'First Value','First Label')
insert into PN_PROJECT_SPACE_META_COMBO(PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) VALUES (7, 'Second Value','Second Label')
insert into PN_PROJECT_SPACE_META_COMBO(PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) VALUES (7, 'XXX Value','XXX Label')

prompt 
prompt Add Specific Values For the Second Combo Value of the Property Project
prompt 
/* Add Specific Values For the Second Combo Value of the Property Project */
insert into PN_PROJECT_SPACE_META_COMBO(PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) VALUES (6, 'First Value','First Label')
insert into PN_PROJECT_SPACE_META_COMBO(PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) VALUES (6, 'Second Value','Second Label')
insert into PN_PROJECT_SPACE_META_COMBO(PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) VALUES (7, 'XXX Value','XXX Label')

/* Change the Tokens From the Pages that are being changed (For Type of Expense) */
prompt 
prompt Change the Tokens From the Pages that are being changed (For Type of Expense)
prompt 
update PN_PROPERTY set property_value = 'New Value' where property = 'prm.project.create.wizard.meta.typeofexpense'
update PN_PROPERTY set property_value = 'New Value' where property = 'prm.project.propertiesedit.meta.typeofexpense.label'
update PN_PROPERTY set property_value = 'New Value' where property = 'prm.project.properties.meta.typeofexpense.label'

/* Change the Tokens From the Pages that are being changed (For Functional Area) */
prompt 
prompt Change the Tokens From the Pages that are being changed (For Functional Area)
prompt 
update PN_PROPERTY set property_value = 'New Value 2' where property = 'prm.project.create.wizard.meta.functionalarea'
update PN_PROPERTY set property_value = 'New Value 2' where property = 'prm.project.propertiesedit.meta.functionalarea.label'
update PN_PROPERTY set property_value = 'New Value 2' where property = 'prm.project.properties.meta.functionalarea.label'