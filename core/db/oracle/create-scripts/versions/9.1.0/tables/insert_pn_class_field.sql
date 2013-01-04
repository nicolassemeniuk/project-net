INSERT INTO PNET.PN_OBJECT("OBJECT_ID", "OBJECT_TYPE", "DATE_CREATED", "CREATED_BY", "RECORD_STATUS")
VALUES (42, 'form', TO_DATE('07-12-2008 04:28:58','DD-MM-YYYY HH:MI:SS'), 1, 'A');

INSERT INTO PNET.PN_OBJECT_NAME("OBJECT_ID", "NAME")
VALUES (42, '@prm.global.form.elementproperty.hiddenforeaf.label');

alter  TRIGGER pnet.FORM_FIELD_AFT_UPD_NAME disable;

INSERT INTO PNET.PN_CLASS_FIELD("SPACE_ID", "CLASS_ID", "FIELD_ID", "DOMAIN_ID", "DATA_COLUMN_NAME", "ELEMENT_ID", "DATA_TABLE_NAME",
                                            "FIELD_LABEL", "DATA_COLUMN_SIZE", "DATA_COLUMN_EXISTS", "ROW_NUM", "ROW_SPAN", "COLUMN_NUM",
                                            "COLUMN_SPAN", "COLUMN_ID", "USE_DEFAULT", "FIELD_GROUP", "HAS_DOMAIN", "MAX_VALUE", "MIN_VALUE",
                                            "DEFAULT_VALUE", "IS_MULTI_SELECT", "SOURCE_FIELD_ID", "CRC", "RECORD_STATUS", "DATA_COLUMN_SCALE",
                                            "IS_VALUE_REQUIRED", "HIDDEN_FOR_EAF")
VALUES (1, 21, 42, NULL, 'hidden_for_eaf', 5, NULL,
           '@prm.global.form.elementproperty.hiddenforeaf.label', NULL, 0, 1, 1, 2,
           1, NULL, 0, NULL, 0, NULL, NULL, NULL, 0, NULL, NULL, 'A', NULL, 0, 1);

INSERT INTO PNET.PN_CLASS_FIELD("SPACE_ID", "CLASS_ID", "FIELD_ID", "DOMAIN_ID", "DATA_COLUMN_NAME", "ELEMENT_ID", "DATA_TABLE_NAME",
                                            "FIELD_LABEL", "DATA_COLUMN_SIZE", "DATA_COLUMN_EXISTS", "ROW_NUM", "ROW_SPAN", "COLUMN_NUM",
                                            "COLUMN_SPAN", "COLUMN_ID", "USE_DEFAULT", "FIELD_GROUP", "HAS_DOMAIN", "MAX_VALUE", "MIN_VALUE",
                                            "DEFAULT_VALUE", "IS_MULTI_SELECT", "SOURCE_FIELD_ID", "CRC", "RECORD_STATUS", "DATA_COLUMN_SCALE",
                                            "IS_VALUE_REQUIRED", "HIDDEN_FOR_EAF")
VALUES (1, 22, 42, NULL, 'hidden_for_eaf', 5, NULL,
           '@prm.global.form.elementproperty.hiddenforeaf.label', NULL, 0, 1, 1, 2,
           1, NULL, 0, NULL, 0, NULL, NULL, NULL, 0, NULL, NULL, 'A', NULL, 0, 1);
           
INSERT INTO PNET.PN_CLASS_FIELD("SPACE_ID", "CLASS_ID", "FIELD_ID", "DOMAIN_ID", "DATA_COLUMN_NAME", "ELEMENT_ID", "DATA_TABLE_NAME",
                                            "FIELD_LABEL", "DATA_COLUMN_SIZE", "DATA_COLUMN_EXISTS", "ROW_NUM", "ROW_SPAN", "COLUMN_NUM",
                                            "COLUMN_SPAN", "COLUMN_ID", "USE_DEFAULT", "FIELD_GROUP", "HAS_DOMAIN", "MAX_VALUE", "MIN_VALUE",
                                            "DEFAULT_VALUE", "IS_MULTI_SELECT", "SOURCE_FIELD_ID", "CRC", "RECORD_STATUS", "DATA_COLUMN_SCALE",
                                            "IS_VALUE_REQUIRED", "HIDDEN_FOR_EAF")
VALUES (1, 23, 42, NULL, 'hidden_for_eaf', 5, NULL,
           '@prm.global.form.elementproperty.hiddenforeaf.label', NULL, 0, 1, 1, 2,
           1, NULL, 0, NULL, 0, NULL, NULL, NULL, 0, NULL, NULL, 'A', NULL, 0, 1);
           
INSERT INTO PNET.PN_CLASS_FIELD("SPACE_ID", "CLASS_ID", "FIELD_ID", "DOMAIN_ID", "DATA_COLUMN_NAME", "ELEMENT_ID", "DATA_TABLE_NAME",
                                            "FIELD_LABEL", "DATA_COLUMN_SIZE", "DATA_COLUMN_EXISTS", "ROW_NUM", "ROW_SPAN", "COLUMN_NUM",
                                            "COLUMN_SPAN", "COLUMN_ID", "USE_DEFAULT", "FIELD_GROUP", "HAS_DOMAIN", "MAX_VALUE", "MIN_VALUE",
                                            "DEFAULT_VALUE", "IS_MULTI_SELECT", "SOURCE_FIELD_ID", "CRC", "RECORD_STATUS", "DATA_COLUMN_SCALE",
                                            "IS_VALUE_REQUIRED", "HIDDEN_FOR_EAF")
VALUES (1, 24, 42, NULL, 'hidden_for_eaf', 5, NULL,
           '@prm.global.form.elementproperty.hiddenforeaf.label', NULL, 0, 1, 1, 2,
           1, NULL, 0, NULL, 0, NULL, NULL, NULL, 0, NULL, NULL, 'A', NULL, 0, 1);

INSERT INTO PNET.PN_CLASS_FIELD("SPACE_ID", "CLASS_ID", "FIELD_ID", "DOMAIN_ID", "DATA_COLUMN_NAME", "ELEMENT_ID", "DATA_TABLE_NAME",
                                            "FIELD_LABEL", "DATA_COLUMN_SIZE", "DATA_COLUMN_EXISTS", "ROW_NUM", "ROW_SPAN", "COLUMN_NUM",
                                            "COLUMN_SPAN", "COLUMN_ID", "USE_DEFAULT", "FIELD_GROUP", "HAS_DOMAIN", "MAX_VALUE", "MIN_VALUE",
                                            "DEFAULT_VALUE", "IS_MULTI_SELECT", "SOURCE_FIELD_ID", "CRC", "RECORD_STATUS", "DATA_COLUMN_SCALE",
                                            "IS_VALUE_REQUIRED", "HIDDEN_FOR_EAF")
VALUES (1, 25, 42, NULL, 'hidden_for_eaf', 5, NULL,
           '@prm.global.form.elementproperty.hiddenforeaf.label', NULL, 0, 1, 1, 2,
           1, NULL, 0, NULL, 0, NULL, NULL, NULL, 0, NULL, NULL, 'A', NULL, 0, 1);
           
INSERT INTO PNET.PN_CLASS_FIELD("SPACE_ID", "CLASS_ID", "FIELD_ID", "DOMAIN_ID", "DATA_COLUMN_NAME", "ELEMENT_ID", "DATA_TABLE_NAME",
                                            "FIELD_LABEL", "DATA_COLUMN_SIZE", "DATA_COLUMN_EXISTS", "ROW_NUM", "ROW_SPAN", "COLUMN_NUM",
                                            "COLUMN_SPAN", "COLUMN_ID", "USE_DEFAULT", "FIELD_GROUP", "HAS_DOMAIN", "MAX_VALUE", "MIN_VALUE",
                                            "DEFAULT_VALUE", "IS_MULTI_SELECT", "SOURCE_FIELD_ID", "CRC", "RECORD_STATUS", "DATA_COLUMN_SCALE",
                                            "IS_VALUE_REQUIRED", "HIDDEN_FOR_EAF")
VALUES (1, 29, 42, NULL, 'hidden_for_eaf', 5, NULL,
           '@prm.global.form.elementproperty.hiddenforeaf.label', NULL, 0, 1, 1, 2,
           1, NULL, 0, NULL, 0, NULL, NULL, NULL, 0, NULL, NULL, 'A', NULL, 0, 1);
           
INSERT INTO PNET.PN_CLASS_FIELD("SPACE_ID", "CLASS_ID", "FIELD_ID", "DOMAIN_ID", "DATA_COLUMN_NAME", "ELEMENT_ID", "DATA_TABLE_NAME",
                                            "FIELD_LABEL", "DATA_COLUMN_SIZE", "DATA_COLUMN_EXISTS", "ROW_NUM", "ROW_SPAN", "COLUMN_NUM",
                                            "COLUMN_SPAN", "COLUMN_ID", "USE_DEFAULT", "FIELD_GROUP", "HAS_DOMAIN", "MAX_VALUE", "MIN_VALUE",
                                            "DEFAULT_VALUE", "IS_MULTI_SELECT", "SOURCE_FIELD_ID", "CRC", "RECORD_STATUS", "DATA_COLUMN_SCALE",
                                            "IS_VALUE_REQUIRED", "HIDDEN_FOR_EAF")		   
VALUES (1, 31, 42, NULL, 'hidden_for_eaf', 5, NULL,
           '@prm.global.form.elementproperty.hiddenforeaf.label', NULL, 0, 1, 1, 2,
           1, NULL, 0, NULL, 0, NULL, NULL, NULL, 0, NULL, NULL, 'A', NULL, 0, 1);

INSERT INTO PNET.PN_CLASS_FIELD("SPACE_ID", "CLASS_ID", "FIELD_ID", "DOMAIN_ID", "DATA_COLUMN_NAME", "ELEMENT_ID", "DATA_TABLE_NAME",
                                            "FIELD_LABEL", "DATA_COLUMN_SIZE", "DATA_COLUMN_EXISTS", "ROW_NUM", "ROW_SPAN", "COLUMN_NUM",
                                            "COLUMN_SPAN", "COLUMN_ID", "USE_DEFAULT", "FIELD_GROUP", "HAS_DOMAIN", "MAX_VALUE", "MIN_VALUE",
                                            "DEFAULT_VALUE", "IS_MULTI_SELECT", "SOURCE_FIELD_ID", "CRC", "RECORD_STATUS", "DATA_COLUMN_SCALE",
                                            "IS_VALUE_REQUIRED", "HIDDEN_FOR_EAF")		   
VALUES (1, 32, 42, NULL, 'hidden_for_eaf', 5, NULL,
           '@prm.global.form.elementproperty.hiddenforeaf.label', NULL, 0, 1, 1, 2,
           1, NULL, 0, NULL, 0, NULL, NULL,  NULL, 0, NULL, NULL, 'A', NULL,  0, 1);
           
INSERT INTO PNET.PN_CLASS_FIELD("SPACE_ID", "CLASS_ID", "FIELD_ID", "DOMAIN_ID", "DATA_COLUMN_NAME", "ELEMENT_ID", "DATA_TABLE_NAME",
                                            "FIELD_LABEL", "DATA_COLUMN_SIZE", "DATA_COLUMN_EXISTS", "ROW_NUM", "ROW_SPAN", "COLUMN_NUM",
                                            "COLUMN_SPAN", "COLUMN_ID", "USE_DEFAULT", "FIELD_GROUP", "HAS_DOMAIN", "MAX_VALUE", "MIN_VALUE",
                                            "DEFAULT_VALUE", "IS_MULTI_SELECT", "SOURCE_FIELD_ID", "CRC", "RECORD_STATUS", "DATA_COLUMN_SCALE",
                                            "IS_VALUE_REQUIRED", "HIDDEN_FOR_EAF")		   
VALUES (1, 33, 42, NULL, 'hidden_for_eaf', 5, NULL,
           '@prm.global.form.elementproperty.hiddenforeaf.label', NULL, 0, 1, 1, 2, 1, NULL, 0, NULL, 0, NULL, NULL,
           NULL, 0, NULL, NULL, 'A', NULL, 0, 1);
           
INSERT INTO PNET.PN_CLASS_FIELD("SPACE_ID", "CLASS_ID", "FIELD_ID", "DOMAIN_ID", "DATA_COLUMN_NAME", "ELEMENT_ID", "DATA_TABLE_NAME",
                                            "FIELD_LABEL", "DATA_COLUMN_SIZE", "DATA_COLUMN_EXISTS", "ROW_NUM", "ROW_SPAN", "COLUMN_NUM",
                                            "COLUMN_SPAN", "COLUMN_ID", "USE_DEFAULT", "FIELD_GROUP", "HAS_DOMAIN", "MAX_VALUE", "MIN_VALUE",
                                            "DEFAULT_VALUE", "IS_MULTI_SELECT", "SOURCE_FIELD_ID", "CRC", "RECORD_STATUS", "DATA_COLUMN_SCALE",
                                            "IS_VALUE_REQUIRED", "HIDDEN_FOR_EAF")		   
VALUES (1, 40, 42, NULL, 'hidden_for_eaf', 5, NULL, '@prm.global.form.elementproperty.hiddenforeaf.label', NULL, 0, 1, 1, 2,
           1, NULL, 0, NULL, 0, NULL, NULL,  NULL, 0, NULL, NULL, 'A', NULL,  0, 1);
           
INSERT INTO PNET.PN_CLASS_FIELD("SPACE_ID", "CLASS_ID", "FIELD_ID", "DOMAIN_ID", "DATA_COLUMN_NAME", "ELEMENT_ID", "DATA_TABLE_NAME",
                                            "FIELD_LABEL", "DATA_COLUMN_SIZE", "DATA_COLUMN_EXISTS", "ROW_NUM", "ROW_SPAN", "COLUMN_NUM",
                                            "COLUMN_SPAN", "COLUMN_ID", "USE_DEFAULT", "FIELD_GROUP", "HAS_DOMAIN", "MAX_VALUE", "MIN_VALUE",
                                            "DEFAULT_VALUE", "IS_MULTI_SELECT", "SOURCE_FIELD_ID", "CRC", "RECORD_STATUS", "DATA_COLUMN_SCALE",
                                            "IS_VALUE_REQUIRED", "HIDDEN_FOR_EAF")		   
VALUES (1, 41, 42, NULL, 'hidden_for_eaf', 5, NULL, '@prm.global.form.elementproperty.hiddenforeaf.label', NULL, 0, 1, 1, 2, 1, NULL, 0, NULL, 
		0, NULL, NULL, NULL, 0, NULL, NULL, 'A', NULL,  0, 1);
		   
alter  TRIGGER pnet.FORM_FIELD_AFT_UPD_NAME enable;
/