
/* Adding object type of wiki in pn_object_type table */

insert into pn_object_type (object_type, master_table_name, object_type_desc, parent_object_type,
	default_permission_actions, is_securable, is_workflowable) values (
	'wiki', 'pn_wiki_page', '@prm.wiki.objecttype.description', null, 207, 1, 0
)
/

/*
 * Alter the pn_wiki_page table to add created_by, created_date, access_level columns
 */

ALTER TABLE PN_WIKI_PAGE ADD (
	CREATED_BY NUMBER(20),
	CREATED_DATE DATE,
	ACCESS_LEVEL NUMBER(1) DEFAULT 0
)
/

/* 
 * Alter the pn_wiki_page table to add constraint on created_by columns as foreign key
 */

ALTER TABLE PN_WIKI_PAGE
ADD CONSTRAINT WIKI_PAGE_CREATED_BY_FK FOREIGN KEY (CREATED_BY)
REFERENCES PN_PERSON (PERSON_ID)
/

CREATE INDEX WIKI_CREATED_BY ON PN_WIKI_PAGE (CREATED_BY) 
/