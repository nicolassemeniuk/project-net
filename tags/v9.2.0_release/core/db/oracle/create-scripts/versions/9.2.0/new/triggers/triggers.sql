
--------------------------------------------------------
PROMPT DDL for Trigger ACTIVITY_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "ACTIVITY_AFT_INS" 
AFTER INSERT
ON PNET.PN_ACTIVITY
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
  INSERT INTO PN_OBJECT_SPACE
      (object_id, space_id)
    VALUES
      (:NEW.object_id, :NEW.space_id);
END;
/
ALTER TRIGGER "ACTIVITY_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger ACTIVITY_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "ACTIVITY_AFT_UPD_NAME" 
AFTER INSERT OR UPDATE OF name ON PNET.PN_ACTIVITY
FOR EACH ROW
 WHEN (NEW.name != OLD.name OR OLD.name IS NULL) BEGIN
  IF INSERTING THEN
    INSERT INTO PN_OBJECT_NAME
      (object_id, name)
    VALUES
      (:NEW.object_id, :NEW.name);
  ELSE
    UPDATE PN_OBJECT_NAME
    SET name = :NEW.name
    WHERE object_id = :NEW.object_id;
  END IF;
END;
/
ALTER TRIGGER "ACTIVITY_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger ADDRESS_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "ADDRESS_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_address
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.address_id;
end;
/
ALTER TRIGGER "ADDRESS_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger AGENDA_ITEM_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "AGENDA_ITEM_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_agenda_item
for each row
 WHEN (new.record_status != new.record_status) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.meeting_id;
end;
/
ALTER TRIGGER "AGENDA_ITEM_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger APPLICATION_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "APPLICATION_AFT_UPD_NAME" 
after insert or update of application_name on PNET.pn_application_space
for each row
 WHEN ((new.application_name != old.application_name) or old.application_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.application_id, :new.application_name);
  else
    update pn_object_name
    set name = :new.application_name
    where object_id = :new.application_id;
  end if;
end;
/
ALTER TRIGGER "APPLICATION_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger APPLICATION_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "APPLICATION_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_application_space
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.application_id;
end;
/
ALTER TRIGGER "APPLICATION_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger BOOKMARK_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "BOOKMARK_AFT_UPD_NAME" 
after insert or update of name on PNET.pn_bookmark
for each row
 WHEN ((new.name != old.name) or old.name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.bookmark_id, :new.name);
  else
    update pn_object_name
    set name = :new.name
    where object_id = :new.bookmark_id;
  end if;
end;
/
ALTER TRIGGER "BOOKMARK_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger BOOKMARK_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "BOOKMARK_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_bookmark
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.bookmark_id;
end;
/
ALTER TRIGGER "BOOKMARK_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger BRAND_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "BRAND_AFT_UPD_NAME" 
after insert or update of brand_name on PNET.pn_brand
for each row
 WHEN ((new.brand_name != old.brand_name) or old.brand_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.brand_id, :new.brand_name);
  else
    update pn_object_name
    set name = :new.brand_name
    where object_id = :new.brand_id;
  end if;
end;
/
ALTER TRIGGER "BRAND_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger BRAND_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "BRAND_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_brand
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.brand_id;
end;
/
ALTER TRIGGER "BRAND_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger BUSINESS_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "BUSINESS_AFT_UPD_NAME" 
after insert or update of business_name on PNET.pn_business
for each row
 WHEN ((new.business_name != old.business_name) or old.business_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.business_id, :new.business_name);
  else
    update pn_object_name
    set name = :new.business_name
    where object_id = :new.business_id;
  end if;
end;
/
ALTER TRIGGER "BUSINESS_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger BUSINESS_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "BUSINESS_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_business
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.business_id;
end;
/
ALTER TRIGGER "BUSINESS_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger CALENDAR_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "CALENDAR_AFT_INS" 
AFTER INSERT
ON pn_space_has_calendar
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  values
  (:new.calendar_id, :new.space_id);
end;
/
ALTER TRIGGER "CALENDAR_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger CALENDAR_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "CALENDAR_AFT_UPD_NAME" 
after insert or update of calendar_name on PNET.pn_calendar
for each row
 WHEN ((new.calendar_name != old.calendar_name) or old.calendar_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.calendar_id, :new.calendar_name);
  else
    update pn_object_name
    set name = :new.calendar_name
    where object_id = :new.calendar_id;
  end if;
end;
/
ALTER TRIGGER "CALENDAR_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger CALENDAR_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "CALENDAR_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_calendar
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.calendar_id;
end;
/
ALTER TRIGGER "CALENDAR_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger CALENDAR_EVENT_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "CALENDAR_EVENT_AFT_INS" 
AFTER INSERT
ON pn_calendar_has_event
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  select
    :new.calendar_event_id,
    shc.space_id
  from
    pn_space_has_calendar shc
  where
    shc.calendar_id = :new.calendar_id;
end;
/
ALTER TRIGGER "CALENDAR_EVENT_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger CLASS_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "CLASS_AFT_INS" 
AFTER INSERT
ON pn_class
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  values
  (:new.class_id, :new.owner_space_id);
end;
/
ALTER TRIGGER "CLASS_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger CLASS_INSTANCE_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "CLASS_INSTANCE_AFT_INS" 
AFTER INSERT
ON pn_class_instance
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  values
  (:new.class_instance_id, :new.space_id);
end;
/
ALTER TRIGGER "CLASS_INSTANCE_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger CONFIGURATION_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "CONFIGURATION_AFT_UPD_NAME" 
after insert or update of configuration_name on PNET.pn_configuration_space
for each row
 WHEN ((new.configuration_name != old.configuration_name) or old.configuration_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.configuration_id, :new.configuration_name);
  else
    update pn_object_name
    set name = :new.configuration_name
    where object_id = :new.configuration_id;
  end if;
end;
/
ALTER TRIGGER "CONFIGURATION_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger CONFIGURATION_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "CONFIGURATION_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_configuration_space
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.configuration_id;
end;
/
ALTER TRIGGER "CONFIGURATION_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DELIVERABLE_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DELIVERABLE_AFT_INS" 
AFTER INSERT
ON pn_phase_has_deliverable
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  select
    :new.deliverable_id, shp.space_id
  from
    pn_space_has_process shp,
    pn_phase ph
  where
    shp.process_id = ph.process_id
    and ph.phase_id = :new.phase_id;
end;
/
ALTER TRIGGER "DELIVERABLE_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DELIVERABLE_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DELIVERABLE_AFT_UPD_NAME" 
after insert or update of deliverable_name on PNET.pn_deliverable
for each row
 WHEN ((new.deliverable_name != old.deliverable_name) or old.deliverable_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.deliverable_id, :new.deliverable_name);
  else
    update pn_object_name
    set name = :new.deliverable_name
    where object_id = :new.deliverable_id;
  end if;
end;
/
ALTER TRIGGER "DELIVERABLE_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DELIVERABLE_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DELIVERABLE_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_deliverable
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.deliverable_id;
end;
/
ALTER TRIGGER "DELIVERABLE_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DIRECTORY_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DIRECTORY_AFT_INS" 
AFTER INSERT
ON pn_space_has_directory
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  values
  (:new.directory_id, :new.space_id);
end;
/
ALTER TRIGGER "DIRECTORY_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DISC_GROUP_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DISC_GROUP_AFT_UPD_NAME" 
after insert or update of discussion_group_name on PNET.pn_discussion_group
for each row
 WHEN ((new.discussion_group_name != old.discussion_group_name) or old.discussion_group_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.discussion_group_id, :new.discussion_group_name);
  else
    update pn_object_name
    set name = :new.discussion_group_name
    where object_id = :new.discussion_group_id;
  end if;
end;
/
ALTER TRIGGER "DISC_GROUP_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DISC_GROUP_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DISC_GROUP_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_discussion_group
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.discussion_group_id;
end;
/
ALTER TRIGGER "DISC_GROUP_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DOCUMENT_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DOCUMENT_AFT_INS" 
AFTER INSERT
ON pn_doc_container_has_object
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  select
    :new.object_id, space_id
  from
    pn_space_has_doc_space shds,
    pn_doc_space_has_container dshc
  where
    shds.doc_space_id = dshc.doc_space_id
    and dshc.doc_container_id = :new.doc_container_id
    and not exists (select 1 from pn_object_space where object_id = :new.object_id and space_id = shds.space_id);
end;
/
ALTER TRIGGER "DOCUMENT_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DOCUMENT_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DOCUMENT_AFT_UPD_NAME" 
after insert or update of doc_name on PNET.pn_document
for each row
 WHEN ((new.doc_name != old.doc_name) or old.doc_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.doc_id, :new.doc_name);
  else
    update pn_object_name
    set name = :new.doc_name
    where object_id = :new.doc_id;
  end if;
end;
/
ALTER TRIGGER "DOCUMENT_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DOCUMENT_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DOCUMENT_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_document
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.doc_id;
end;
/
ALTER TRIGGER "DOCUMENT_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DOC_CONTAINER_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DOC_CONTAINER_AFT_UPD_NAME" 
after insert or update of container_name on PNET.pn_doc_container
for each row
 WHEN ((new.container_name != old.container_name) or old.container_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.doc_container_id, :new.container_name);
  else
    update pn_object_name
    set name = :new.container_name
    where object_id = :new.doc_container_id;
  end if;
end;
/
ALTER TRIGGER "DOC_CONTAINER_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DOC_CONTAINER_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DOC_CONTAINER_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_doc_container
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.doc_container_id;
end;
/
ALTER TRIGGER "DOC_CONTAINER_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DOC_SPACE_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DOC_SPACE_AFT_UPD_NAME" 
after insert or update of doc_space_name on PNET.pn_doc_space
for each row
 WHEN ((new.doc_space_name != old.doc_space_name) or old.doc_space_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.doc_space_id, :new.doc_space_name);
  else
    update pn_object_name
    set name = :new.doc_space_name
    where object_id = :new.doc_space_id;
  end if;
end;
/
ALTER TRIGGER "DOC_SPACE_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DOC_SPACE_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DOC_SPACE_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_doc_space
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.doc_space_id;
end;
/
ALTER TRIGGER "DOC_SPACE_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DOMAIN_VALUE_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DOMAIN_VALUE_AFT_UPD_NAME" 
after insert or update of domain_value_name on PNET.pn_class_domain_values
for each row
 WHEN ((new.domain_value_name != old.domain_value_name) or old.domain_value_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.domain_value_id, :new.domain_value_name);
  else
    update pn_object_name
    set name = :new.domain_value_name
    where object_id = :new.domain_value_id;
  end if;
end;
/
ALTER TRIGGER "DOMAIN_VALUE_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger DOMAIN_VALUE_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DOMAIN_VALUE_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_class_domain_values
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.domain_value_id;
end;
/
ALTER TRIGGER "DOMAIN_VALUE_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger ENVELOPE_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "ENVELOPE_AFT_UPD_NAME" 
after insert or update of envelope_name on PNET.pn_workflow_envelope
for each row
 WHEN ((new.envelope_name != old.envelope_name) or old.envelope_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.envelope_id, :new.envelope_name);
  else
    update pn_object_name
    set name = :new.envelope_name
    where object_id = :new.envelope_id;
  end if;
end;
/
ALTER TRIGGER "ENVELOPE_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger ENVELOPE_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "ENVELOPE_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_workflow_envelope
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.envelope_id;
end;
/
ALTER TRIGGER "ENVELOPE_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger ENV_HISTORY_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "ENV_HISTORY_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_envelope_history
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.history_id;
end;
/
ALTER TRIGGER "ENV_HISTORY_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger ENV_VERSION_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "ENV_VERSION_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_envelope_version
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.version_id;
end;
/
ALTER TRIGGER "ENV_VERSION_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger EVENT_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "EVENT_AFT_UPD_NAME" 
AFTER INSERT  OR UPDATE OF
  event_name
ON pnet.pn_calendar_event
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
 WHEN ((new.event_name != old.event_name) or old.event_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.calendar_event_id, :new.event_name);
  else
    update pn_object_name
    set name = :new.event_name
    where object_id = :new.calendar_event_id;

    --We have to update meeting's here too because the meeting trigger wouldn't
    --know about the change
    update pn_object_name
    set name = :new.event_name
    where object_id = (select meeting_id from pn_meeting where calendar_event_id = :new.calendar_event_id);
  end if;
end;
/
ALTER TRIGGER "EVENT_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger EVENT_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "EVENT_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_calendar_event
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.calendar_event_id;
end;
/
ALTER TRIGGER "EVENT_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger FORM_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "FORM_AFT_UPD_NAME" 
after insert or update of class_name on PNET.pn_class
for each row
 WHEN ((new.class_name != old.class_name) or old.class_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.class_id, :new.class_name);
  else
    update pn_object_name
    set name = :new.class_name
    where object_id = :new.class_id;
  end if;
end;
/
ALTER TRIGGER "FORM_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger FORM_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "FORM_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_class
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.class_id;
end;
/
ALTER TRIGGER "FORM_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger FORM_DOMAIN_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "FORM_DOMAIN_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_class_domain
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.domain_id;
end;
/
ALTER TRIGGER "FORM_DOMAIN_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger FORM_FIELD_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "FORM_FIELD_AFT_UPD_NAME" 
after insert or update of field_label on PNET.pn_class_field
for each row
 WHEN ((new.field_label != old.field_label) or old.field_label is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.field_id, :new.field_label);
  else
    update pn_object_name
    set name = :new.field_label
    where object_id = :new.field_id;
  end if;
end;
/
ALTER TRIGGER "FORM_FIELD_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger FORM_FIELD_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "FORM_FIELD_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_class_field
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.field_id;
end;
/
ALTER TRIGGER "FORM_FIELD_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger FORM_INST_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "FORM_INST_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_class_instance
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.class_instance_id;
end;
/
ALTER TRIGGER "FORM_INST_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger FORM_LIST_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "FORM_LIST_AFT_UPD_NAME" 
after insert or update of list_name on PNET.pn_class_list
for each row
 WHEN ((new.list_name != old.list_name) or old.list_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.list_id, :new.list_name);
  else
    update pn_object_name
    set name = :new.list_name
    where object_id = :new.list_id;
  end if;
end;
/
ALTER TRIGGER "FORM_LIST_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger FORM_LIST_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "FORM_LIST_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_class_list
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.list_id;
end;
/
ALTER TRIGGER "FORM_LIST_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger GATE_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "GATE_AFT_INS" 
AFTER INSERT
ON pn_gate
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  select
    :new.gate_id,
    space_id
  from
    pn_phase ph,
    pn_space_has_process shp
  where
    ph.phase_id = :new.phase_id
    and ph.process_id = shp.process_id;
end;
/
ALTER TRIGGER "GATE_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger GATE_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "GATE_AFT_UPD_NAME" 
after insert or update of gate_name on PNET.pn_gate
for each row
 WHEN ((new.gate_name != old.gate_name) or old.gate_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.gate_id, :new.gate_name);
  else
    update pn_object_name
    set name = :new.gate_name
    where object_id = :new.gate_id;
  end if;
end;
/
ALTER TRIGGER "GATE_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger GATE_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "GATE_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_gate
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.gate_id;
end;
/
ALTER TRIGGER "GATE_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger GROUP_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "GROUP_AFT_INS" 
AFTER INSERT
ON pnet.pn_space_has_group
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
declare
  v_exists number;
begin
  --We need to check that the object isn't already in this
  --table and space because the group may have been added
  --from a parent business, removed, then added again.
  v_exists := 0;
  select count(*) into v_exists from pn_object_space
  where object_id = :new.group_id
    and space_id = :new.space_id;

  if (v_exists = 0) then
      insert into pn_object_space
      (object_id, space_id)
      values
      (:new.group_id, :new.space_id);
  end if;
end;
/
ALTER TRIGGER "GROUP_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger GROUP_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "GROUP_AFT_UPD_NAME" 
after insert or update of group_name on PNET.pn_group
for each row
 WHEN ((new.group_name != old.group_name) or old.group_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.group_id, :new.group_name);
  else
    update pn_object_name
    set name = :new.group_name
    where object_id = :new.group_id;
  end if;
end;
/
ALTER TRIGGER "GROUP_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger GROUP_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "GROUP_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_group
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.group_id;
end;
/
ALTER TRIGGER "GROUP_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger MEETING_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "MEETING_AFT_INS" 
AFTER INSERT
ON pn_meeting
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  select
    :new.meeting_id,
    shc.space_id
  from
    pn_calendar_has_event che,
    pn_space_has_calendar shc
  where
    che.calendar_id = shc.calendar_id
    and calendar_event_id = :new.calendar_event_id;
end;
/
ALTER TRIGGER "MEETING_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger MEETING_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "MEETING_AFT_UPD_NAME" 
AFTER INSERT  OR UPDATE
ON pnet.pn_meeting
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
 WHEN ((new.calendar_event_id != old.calendar_event_id) or old.calendar_event_id is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    select
      :new.meeting_id, ce.event_name
    from
      pn_calendar_event ce
    where
      ce.calendar_event_id = :new.calendar_event_id;
  else
    update pn_object_name
    set name =
      (select event_name from pn_calendar_event ce where ce.calendar_event_id = :new.calendar_event_id)
    where
      object_id = :new.meeting_id;
  end if;
end;
/
ALTER TRIGGER "MEETING_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger METHODOLOGY_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "METHODOLOGY_AFT_UPD_NAME" 
after insert or update of methodology_name on PNET.pn_methodology_space
for each row
 WHEN ((new.methodology_name != old.methodology_name) or old.methodology_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.methodology_id, :new.methodology_name);
  else
    update pn_object_name
    set name = :new.methodology_name
    where object_id = :new.methodology_id;
  end if;
end;
/
ALTER TRIGGER "METHODOLOGY_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger METHODOLOGY_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "METHODOLOGY_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_methodology_space
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.methodology_id;
end;
/
ALTER TRIGGER "METHODOLOGY_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger NEWS_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NEWS_AFT_INS" 
AFTER INSERT
ON pn_space_has_news
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  values
  (:new.news_id, :new.space_id);
end;
/
ALTER TRIGGER "NEWS_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger NEWS_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NEWS_AFT_UPD_NAME" 
after insert or update of topic on PNET.pn_news
for each row
 WHEN ((new.topic != old.topic) or old.topic is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.news_id, :new.topic);
  else
    update pn_object_name
    set name = :new.topic
    where object_id = :new.news_id;
  end if;
end;
/
ALTER TRIGGER "NEWS_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger NEWS_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "NEWS_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_news
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.news_id;
end;
/
ALTER TRIGGER "NEWS_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PERSON_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PERSON_AFT_UPD_NAME" 
after insert or update of display_name on PNET.pn_person
for each row
 WHEN ((new.display_name != old.display_name) or old.display_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.person_id, :new.display_name);
  else
    update pn_object_name
    set name = :new.display_name
    where object_id = :new.person_id;
  end if;
end;
/
ALTER TRIGGER "PERSON_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PERSON_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PERSON_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_person
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.person_id;
end;
/
ALTER TRIGGER "PERSON_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PHASE_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PHASE_AFT_INS" 
AFTER INSERT
ON pn_phase
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  select
    :new.phase_id,
    space_id
  from
    pn_space_has_process
  where
    process_id = :new.process_id;
end;
/
ALTER TRIGGER "PHASE_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PHASE_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PHASE_AFT_UPD_NAME" 
after insert or update of phase_name on PNET.pn_phase
for each row
 WHEN ((new.phase_name != old.phase_name) or old.phase_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.phase_id, :new.phase_name);
  else
    update pn_object_name
    set name = :new.phase_name
    where object_id = :new.phase_id;
  end if;
end;
/
ALTER TRIGGER "PHASE_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PHASE_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PHASE_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_phase
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.phase_id;
end;
/
ALTER TRIGGER "PHASE_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PLAN_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PLAN_AFT_INS" 
AFTER INSERT
ON pn_space_has_plan
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  values
  (:new.plan_id, :new.space_id);
end;
/
ALTER TRIGGER "PLAN_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PLAN_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PLAN_AFT_UPD_NAME" 
after insert or update of plan_name on PNET.pn_plan
for each row
 WHEN ((new.plan_name != old.plan_name) or old.plan_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.plan_id, :new.plan_name);
  else
    update pn_object_name
    set name = :new.plan_name
    where object_id = :new.plan_id;
  end if;
end;
/
ALTER TRIGGER "PLAN_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PORTFOLIO_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PORTFOLIO_AFT_UPD_NAME" 
after insert or update of portfolio_name on PNET.pn_portfolio
for each row
 WHEN ((new.portfolio_name != old.portfolio_name) or old.portfolio_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.portfolio_id, :new.portfolio_name);
  else
    update pn_object_name
    set name = :new.portfolio_name
    where object_id = :new.portfolio_id;
  end if;
end;
/
ALTER TRIGGER "PORTFOLIO_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PORTFOLIO_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PORTFOLIO_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_portfolio
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.portfolio_id;
end;
/
ALTER TRIGGER "PORTFOLIO_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger POST_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "POST_AFT_UPD_NAME" 
after insert or update of subject on PNET.pn_post
for each row
 WHEN ((new.subject != old.subject) or old.subject is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.post_id, :new.subject);
  else
    update pn_object_name
    set name = :new.subject
    where object_id = :new.post_id;
  end if;
end;
/
ALTER TRIGGER "POST_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger POST_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "POST_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_post
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.post_id;
end;
/
ALTER TRIGGER "POST_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PROCESS_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PROCESS_AFT_INS" 
AFTER INSERT
ON pn_space_has_process
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  values
  (:new.process_id, :new.space_id);
end;
/
ALTER TRIGGER "PROCESS_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PROCESS_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PROCESS_AFT_UPD_NAME" 
after insert or update of process_name on PNET.pn_process
for each row
 WHEN ((new.process_name != old.process_name) or old.process_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.process_id, :new.process_name);
  else
    update pn_object_name
    set name = :new.process_name
    where object_id = :new.process_id;
  end if;
end;
/
ALTER TRIGGER "PROCESS_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PROCESS_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PROCESS_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_process
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.process_id;
end;
/
ALTER TRIGGER "PROCESS_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PROJECT_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PROJECT_AFT_UPD_NAME" 
after insert or update of project_name on PNET.pn_project_space
for each row
 WHEN ((new.project_name != old.project_name) or old.project_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.project_id, :new.project_name);
  else
    update pn_object_name
    set name = :new.project_name
    where object_id = :new.project_id;
  end if;
end;
/
ALTER TRIGGER "PROJECT_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger PROJECT_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "PROJECT_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_project_space
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.project_id;
end;
/
ALTER TRIGGER "PROJECT_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger STEP_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "STEP_AFT_UPD_NAME" 
after insert or update of step_name on PNET.pn_workflow_step
for each row
 WHEN ((new.step_name != old.step_name) or old.step_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.step_id, :new.step_name);
  else
    update pn_object_name
    set name = :new.step_name
    where object_id = :new.step_id;
  end if;
end;
/
ALTER TRIGGER "STEP_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger STEP_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "STEP_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_workflow_step
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.step_id;
end;
/
ALTER TRIGGER "STEP_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger SUBSCRIPTION_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "SUBSCRIPTION_AFT_UPD_NAME" 
after insert or update of name on PNET.pn_subscription
for each row
 WHEN ((new.name != old.name) or old.name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.subscription_id, :new.name);
  else
    update pn_object_name
    set name = :new.name
    where object_id = :new.subscription_id;
  end if;
end;
/
ALTER TRIGGER "SUBSCRIPTION_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger SUBSCRIPTION_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "SUBSCRIPTION_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_subscription
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.subscription_id;
end;
/
ALTER TRIGGER "SUBSCRIPTION_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger S_SUBSCRIPTION_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "S_SUBSCRIPTION_AFT_UPD_NAME" 
after insert or update of name on PNET.pn_scheduled_subscription
for each row
 WHEN ((new.name != old.name) or old.name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.scheduled_subscription_id, :new.name);
  else
    update pn_object_name
    set name = :new.name
    where object_id = :new.scheduled_subscription_id;
  end if;
end;
/
ALTER TRIGGER "S_SUBSCRIPTION_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger S_SUBSCRIPTION_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "S_SUBSCRIPTION_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_scheduled_subscription
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.scheduled_subscription_id;
end;
/
ALTER TRIGGER "S_SUBSCRIPTION_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger TASK_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TASK_AFT_INS" 
AFTER INSERT
ON pn_plan_has_task
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  select
    :new.task_id, space_id
  from
    pn_space_has_plan shp
  where
    shp.plan_id = :new.plan_id;
end;
/
ALTER TRIGGER "TASK_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger TASK_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TASK_AFT_UPD_NAME" 
after insert or update of task_name on PNET.pn_task
for each row
 WHEN ((new.task_name != old.task_name) or old.task_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.task_id, :new.task_name);
  else
    update pn_object_name
    set name = :new.task_name
    where object_id = :new.task_id;
  end if;
end;
/
ALTER TRIGGER "TASK_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger TASK_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TASK_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_task
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.task_id;
end;
/
ALTER TRIGGER "TASK_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger TRANSITION_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRANSITION_AFT_UPD_NAME" 
after insert or update of transition_verb on PNET.pn_workflow_transition
for each row
 WHEN ((new.transition_verb != old.transition_verb) or old.transition_verb is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.transition_id, :new.transition_verb);
  else
    update pn_object_name
    set name = :new.transition_verb
    where object_id = :new.transition_id;
  end if;
end;
/
ALTER TRIGGER "TRANSITION_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger TRANSITION_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRANSITION_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_workflow_transition
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.transition_id;
end;
/
ALTER TRIGGER "TRANSITION_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger USER_DOMAIN_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "USER_DOMAIN_AFT_UPD_NAME" 
after insert or update of name on PNET.pn_user_domain
for each row
 WHEN ((new.name != old.name) or old.name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.domain_id, :new.name);
  else
    update pn_object_name
    set name = :new.name
    where object_id = :new.domain_id;
  end if;
end;
/
ALTER TRIGGER "USER_DOMAIN_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger USER_DOMAIN_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "USER_DOMAIN_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_user_domain
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.domain_id;
end;
/
ALTER TRIGGER "USER_DOMAIN_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger WORKFLOW_AFT_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "WORKFLOW_AFT_INS" 
AFTER INSERT
ON pn_space_has_workflow
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  values
  (:new.workflow_id, :new.space_id);
end;
/
ALTER TRIGGER "WORKFLOW_AFT_INS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger WORKFLOW_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "WORKFLOW_AFT_UPD_NAME" 
after insert or update of workflow_name on PNET.pn_workflow
for each row
 WHEN ((new.workflow_name != old.workflow_name) or old.workflow_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.workflow_id, :new.workflow_name);
  else
    update pn_object_name
    set name = :new.workflow_name
    where object_id = :new.workflow_id;
  end if;
end;
/
ALTER TRIGGER "WORKFLOW_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger WORKFLOW_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "WORKFLOW_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_workflow
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.workflow_id;
end;
/
ALTER TRIGGER "WORKFLOW_AFT_UPD_STATUS" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger WORKFLOW_RULE_AFT_UPD_NAME
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "WORKFLOW_RULE_AFT_UPD_NAME" 
after insert or update of rule_name on PNET.pn_workflow_rule
for each row
 WHEN ((new.rule_name != old.rule_name) or old.rule_name is null) begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.rule_id, :new.rule_name);
  else
    update pn_object_name
    set name = :new.rule_name
    where object_id = :new.rule_id;
  end if;
end;
/
ALTER TRIGGER "WORKFLOW_RULE_AFT_UPD_NAME" ENABLE;
--------------------------------------------------------
PROMPT DDL for Trigger WORKFLOW_RULE_AFT_UPD_STATUS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "WORKFLOW_RULE_AFT_UPD_STATUS" 
after update of record_status on PNET.pn_workflow_rule
for each row
 WHEN ((new.record_status != old.record_status) or old.record_status is null) begin
  update pn_object
  set record_status = :new.record_status
  where object_id = :new.rule_id;
end;
/
ALTER TRIGGER "WORKFLOW_RULE_AFT_UPD_STATUS" ENABLE;