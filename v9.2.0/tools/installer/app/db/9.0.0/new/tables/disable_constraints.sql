-- prompt  Disabling foreign key constraints for PN_OBJECT...
alter table PN_OBJECT disable constraint OBJECT_FK1
/
alter table PN_OBJECT disable constraint OBJECT_FK2
/
-- prompt  Disabling foreign key constraints for PN_PERSON...
alter table PN_PERSON disable constraint PERSON_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_ADDRESS...
alter table PN_ADDRESS disable constraint ADDRESS_FK1
/
alter table PN_ADDRESS disable constraint ADDRESS_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_AUTHENTICATOR...
alter table PN_AUTHENTICATOR disable constraint PN_AUTHENTICATOR_FK1
/
-- prompt  Disabling foreign key constraints for PN_BRAND...
alter table PN_BRAND disable constraint PN_BRAND_FK1
/
-- prompt  Disabling foreign key constraints for PN_BRAND_SUPPORTS_LANGUAGE...
alter table PN_BRAND_SUPPORTS_LANGUAGE disable constraint PN_BRAND_SUPPORTS_LANGUAGE_FK1
/
-- prompt  Disabling foreign key constraints for PN_CALENDAR...
alter table PN_CALENDAR disable constraint CALENDAR_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_CLASS...
alter table PN_CLASS disable constraint CLASS_FK1
/
alter table PN_CLASS disable constraint CLASS_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_CLASS_DOMAIN...
alter table PN_CLASS_DOMAIN disable constraint CLASS_DOMAIN_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_CLASS_DOMAIN_VALUES...
alter table PN_CLASS_DOMAIN_VALUES disable constraint CLASS_DOMAIN_VALUES_FK1
/
alter table PN_CLASS_DOMAIN_VALUES disable constraint CLASS_DOMAIN_VAL_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_CLASS_FIELD_PROPERTY...
alter table PN_CLASS_FIELD_PROPERTY disable constraint CLASS_FIELD_PROPERTY_FK1
/
-- prompt  Disabling foreign key constraints for PN_CLASS_TYPE_ELEMENT...
alter table PN_CLASS_TYPE_ELEMENT disable constraint CLASS_TYPE_ELEMENT_FK1
/
alter table PN_CLASS_TYPE_ELEMENT disable constraint CLASS_TYPE_ELEMENT_FK2
/
-- prompt  Disabling foreign key constraints for PN_GROUP...
alter table PN_GROUP disable constraint PN_GROUP_FK2
/
alter table PN_GROUP disable constraint PN_GROUP_FK3
/
-- prompt  Disabling foreign key constraints for PN_DEFAULT_OBJECT_PERMISSION...
alter table PN_DEFAULT_OBJECT_PERMISSION disable constraint DEFAULT_OBJ_PERM_FK1
/
alter table PN_DEFAULT_OBJECT_PERMISSION disable constraint DEFAULT_OBJ_PERM_FK2
/
-- prompt  Disabling foreign key constraints for PN_DIRECTORY_HAS_PERSON...
alter table PN_DIRECTORY_HAS_PERSON disable constraint DIRECTORY_PERSON_FK1
/
alter table PN_DIRECTORY_HAS_PERSON disable constraint DIRECTORY_PERSON_FK2
/
-- prompt  Disabling foreign key constraints for PN_DOC_CONTAINER...
alter table PN_DOC_CONTAINER disable constraint DOC_CONTAINER_FK1
/
alter table PN_DOC_CONTAINER disable constraint DOC_CONTAINER_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_DOC_PROVIDER...
alter table PN_DOC_PROVIDER disable constraint DOC_PROVIDER_FK1
/
-- prompt  Disabling foreign key constraints for PN_DOC_SPACE...
alter table PN_DOC_SPACE disable constraint DOC_SPACE_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_DOC_PROVIDER_HAS_DOC_SPACE...
alter table PN_DOC_PROVIDER_HAS_DOC_SPACE disable constraint DOC_PROV_DOC_SPACE_FK1
/
alter table PN_DOC_PROVIDER_HAS_DOC_SPACE disable constraint DOC_PROV_DOC_SPACE_FK2
/
-- prompt  Disabling foreign key constraints for PN_DOC_SPACE_HAS_CONTAINER...
alter table PN_DOC_SPACE_HAS_CONTAINER disable constraint DOC_SPACE_CONTAINER_FK1
/
alter table PN_DOC_SPACE_HAS_CONTAINER disable constraint DOC_SPACE_CONTAINER_FK2
/
-- prompt  Disabling foreign key constraints for PN_ELEMENT_DISPLAY_CLASS...
alter table PN_ELEMENT_DISPLAY_CLASS disable constraint ELEMENT_DISPLAY_CLASS_FK1
/
alter table PN_ELEMENT_DISPLAY_CLASS disable constraint ELEMENT_DISPLAY_CLASS_FK2
/
-- prompt  Disabling foreign key constraints for PN_ELEMENT_PROPERTY...
alter table PN_ELEMENT_PROPERTY disable constraint ELEMENT_PROPERTY_FK1
/
alter table PN_ELEMENT_PROPERTY disable constraint ELEMENT_PROPERTY_FK2
/
-- prompt  Disabling foreign key constraints for PN_EVENT_TYPE...
alter table PN_EVENT_TYPE disable constraint PN_EVENT_TYPE_FK1
/
-- prompt  Disabling foreign key constraints for PN_NOTIFICATION_TYPE...
alter table PN_NOTIFICATION_TYPE disable constraint NOTIFICATION_TYPE_FK1
/
-- prompt  Disabling foreign key constraints for PN_EVENT_HAS_NOTIFICATION...
alter table PN_EVENT_HAS_NOTIFICATION disable constraint PN_EVENT_HAS_NOTIFICATION_FK1
/
alter table PN_EVENT_HAS_NOTIFICATION disable constraint PN_EVENT_HAS_NOTIFICATION_FK2
/
-- prompt  Disabling foreign key constraints for PN_GLOBAL_CODE...
alter table PN_GLOBAL_CODE disable constraint GLOBAL_CODE_FK1
/
-- prompt  Disabling foreign key constraints for PN_GROUP_HAS_PERSON...
alter table PN_GROUP_HAS_PERSON disable constraint GROUP_HAS_PERSON_FK1
/
alter table PN_GROUP_HAS_PERSON disable constraint GROUP_HAS_PERSON_FK2
/
-- prompt  Disabling foreign key constraints for PN_LOGIN_HISTORY...
alter table PN_LOGIN_HISTORY disable constraint LOGIN_HISTORY_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_MODULE...
alter table PN_SPACE_HAS_MODULE disable constraint SPACE_HAS_MODULE_FK1
/
-- prompt  Disabling foreign key constraints for PN_MODULE_PERMISSION...
alter table PN_MODULE_PERMISSION disable constraint MODULE_PERMISSION_FK1
/
alter table PN_MODULE_PERMISSION disable constraint MODULE_PERMISSION_FK2
/
-- prompt  Disabling foreign key constraints for PN_OBJECT_PERMISSION...
alter table PN_OBJECT_PERMISSION disable constraint OBJECT_PERMISSION_FK1
/
alter table PN_OBJECT_PERMISSION disable constraint OBJECT_PERMISSION_FK2
/
-- prompt  Disabling foreign key constraints for PN_OBJECT_TYPE_SUPPORTS_ACTION...
alter table PN_OBJECT_TYPE_SUPPORTS_ACTION disable constraint OBJ_TYPE_SUP_ACTION_FK1
/
alter table PN_OBJECT_TYPE_SUPPORTS_ACTION disable constraint OBJ_TYPE_SUP_ACTION_FK2
/
-- prompt  Disabling foreign key constraints for PN_PERSON_AUTHENTICATOR...
alter table PN_PERSON_AUTHENTICATOR disable constraint PERSON_AUTH_FK2
/
alter table PN_PERSON_AUTHENTICATOR disable constraint PN_PERSON_AUTH_FK1
/
-- prompt  Disabling foreign key constraints for PN_PERSON_NOTIFICATION_ADDRESS...
alter table PN_PERSON_NOTIFICATION_ADDRESS disable constraint PERSON_NOTIFICATION_ADDR_FK1
/
alter table PN_PERSON_NOTIFICATION_ADDRESS disable constraint PERSON_NOTIFICATION_ADDR_FK2
/
-- prompt  Disabling foreign key constraints for PN_PERSON_PROFILE...
alter table PN_PERSON_PROFILE disable constraint PN_PERSON_PROFILE_FK1
/
-- prompt  Disabling foreign key constraints for PN_PORTFOLIO...
alter table PN_PORTFOLIO disable constraint PORTFOLIO_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_PORTFOLIO_HAS_SPACE...
alter table PN_PORTFOLIO_HAS_SPACE disable constraint PORTFOLIO_SPACE_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_CALENDAR...
alter table PN_SPACE_HAS_CALENDAR disable constraint SPACE_HAS_CALENDAR_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_DIRECTORY...
alter table PN_SPACE_HAS_DIRECTORY disable constraint SPACE_DIRECTORY_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_DOC_SPACE...
alter table PN_SPACE_HAS_DOC_SPACE disable constraint SPACE_HAS_DOC_SPACE_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_GROUP...
alter table PN_SPACE_HAS_GROUP disable constraint SPACE_HAS_GROUP_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_PERSON...
alter table PN_SPACE_HAS_PERSON disable constraint SPACE_PERSON_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_PORTFOLIO...
alter table PN_SPACE_HAS_PORTFOLIO disable constraint SPACE_PORTFOLIO_FK1
/
-- prompt  Disabling foreign key constraints for PN_STATE_LOOKUP...
alter table PN_STATE_LOOKUP disable constraint STATE_LOOKUP_FK1
/
-- prompt  Disabling foreign key constraints for PN_USER...
alter table PN_USER disable constraint PN_USER_FK1
/
alter table PN_USER disable constraint PN_USER_FK2
/
-- prompt  Disabling foreign key constraints for PN_USER_DEFAULT_CREDENTIALS...
alter table PN_USER_DEFAULT_CREDENTIALS disable constraint PN_USER_DEF_CREDENTIALS_FK1
/