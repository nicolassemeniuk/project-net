/* Creating object types blog, blog_entry and blog_comment */

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blog.objecttype.description', 'Blog', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blogentry.objecttype.description', 'Blog Entry', 'A', 0, 1, null);

insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.blogcomment.objecttype.description', 'Blog Comment', 'A', 0, 1, null);

insert into pn_object_type (object_type, master_table_name, object_type_desc, parent_object_type,
	default_permission_actions, is_securable, is_workflowable) values (
	'blog', 'pn_weblog', '@prm.blog.objecttype.description', null, 15, 0, 0
)
/

insert into pn_object_type (object_type, master_table_name, object_type_desc, parent_object_type,
	default_permission_actions, is_securable, is_workflowable) values (
	'blog_entry', 'pn_weblog_entry', '@prm.blogentry.objecttype.description', 'blog', 15, 0, 0
)
/

insert into pn_object_type (object_type, master_table_name, object_type_desc, parent_object_type,
	default_permission_actions, is_securable, is_workflowable) values (
	'blog_comment', 'pn_weblog_comment', '@prm.blogcomment.objecttype.description', 'blog_entry', 15, 0, 0
)
/