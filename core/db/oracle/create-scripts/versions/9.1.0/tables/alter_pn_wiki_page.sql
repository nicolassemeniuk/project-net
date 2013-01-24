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
-- Alter the pn_wiki_page table to add column comment_text for commenting wiki edits

alter table PN_WIKI_PAGE add (
    COMMENT_TEXT    CLOB
)
/

-- Alter the pn_wiki_page table to add column record_status (when creating db from scratch - Ivana noticed issue that this column doesn't exist)
alter table PN_WIKI_PAGE add (
    RECORD_STATUS    VARCHAR2(1) default 'A'
)
/


prompt
prompt Changing PN_WIKI_PAGE table for fixing parent_page_name column type,
prompt and creating table PN_WIKI_PAGE_BACKUP (backuping current pn_wiki_page data)
prompt ==============================================================================
prompt

/**
 *	Backing up obsolete PN_WIKI_PAGE table data (with PARENT_PAGE_NAME of VARCHAR type)
 */
create table PN_WIKI_PAGE_BACKUP as select * from PN_WIKI_PAGE
/

/**
 *	Adding new column PARENT_PAGE_ID to hold new type and value out of old PARENT_PAGE_NAME column
 */
alter table PN_WIKI_PAGE add (parent_page_id number(20))
/

update PN_WIKI_PAGE p
set p.parent_page_id = (SELECT pp.wiki_page_id 
                   			FROM PN_WIKI_PAGE pp
                   			WHERE pp.owner_object_id=p.owner_object_id AND pp.page_name=p.parent_page_name)
/

/**
 *	Procedure for fixing issues introduced with primar version of [Create Page] option
 */
-- repair errors introduced with 'Create Page' option - replace all " " with "_" in parent_page_name column
Declare
    -- rows that contain parent_page_id set to NULL, but their parent_page_name is NOT NULL are persisted in problematicRows variable
    type arr_problematic_rec IS TABLE OF PN_WIKI_PAGE % rowtype;
    problematicRows arr_problematic_rec;
    
begin
    -- populate problematicRows variable with problematic rows from PN_WIKI_PAGE table
    select *
        bulk collect
        into problematicRows
         FROM PN_WIKI_PAGE pp 
         WHERE pp.parent_page_name is NOT NULL AND pp.parent_page_id is NULL;
    
    for i in 1..problematicRows.count loop
        UPDATE PN_WIKI_PAGE p
               SET p.parent_page_id =(SELECT t.wiki_page_id
                                          FROM pn_wiki_page t
                                          WHERE t.page_name = REPLACE(problematicRows(i).parent_page_name, ' ', '_') AND
                                                t.owner_object_id = problematicRows(i).owner_object_id)
                WHERE p.wiki_page_id = problematicRows(i).wiki_page_id;
         dbms_output.put_line('Updated row: ' || problematicRows(i).wiki_page_id);
    end loop;
    
end;
/

/**
 *	Dropping old column with obsolete type of VARCHAR
 */
alter table PN_WIKI_PAGE drop column parent_page_name
/

/**
 * Adding foreing key fk_parent_id referencing wiki_page_id column
 */
alter table PN_WIKI_PAGE add constraint
	fk_pn_wiki_page_parent_id foreign key(PARENT_PAGE_ID) 
		references PN_WIKI_PAGE(WIKI_PAGE_ID)
/

/**
 *	Set columns that have record_status set to NULL to "A". Fix.
 */
update PN_WIKI_PAGE p
	set p.record_status = 'A'
	where p.record_status is NULL
/

prompt ==============================================================================
prompt