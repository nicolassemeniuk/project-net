/**
 * 
 */
package net.project.events;

import net.project.activity.ActivityLogManager;
import net.project.security.SessionManager;

/**
 *
 */
public class FormEvent extends ApplicationEvent {
	
	private String name;
	
	private String formNameWithSequenceNumber;
	
	private boolean isEafForm = false;
	
	private String creatorEmailAddress;
	
	public FormEvent(EventType eventType) {
		init();
		setType(eventType);
	}
	
	public void init(){
		setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
		setUser(SessionManager.getUser());
	}
	
	/* (non-Javadoc)
	 * @see net.project.notification.ApplicationEvent#beforePublish()
	 */
	@Override
	public void beforePublish() {
		if(getType() == EventType.NEW){
			setDescription(ActivityLogManager.formAndFormDataDescription(EventType.NEW, getObjectType(), getObjectRecordStatus(), getFormNameWithSequenceNumber(), getCreatorEmailAddress(), isEafForm()));
		} else if(getType() == EventType.DELETED){
			setDescription(ActivityLogManager.formAndFormDataDescription(EventType.DELETED, getObjectType(), getObjectRecordStatus(), getFormNameWithSequenceNumber(), getCreatorEmailAddress(), isEafForm()));
		} else if(getType() == EventType.EDITED){
			setDescription(ActivityLogManager.formAndFormDataDescription(EventType.EDITED, getObjectType(), getObjectRecordStatus(), getFormNameWithSequenceNumber(), getCreatorEmailAddress(), isEafForm()));
		} else if(getType() == EventType.FORM_RECORD_CREATED){
			setDescription(ActivityLogManager.formAndFormDataDescription(EventType.FORM_RECORD_CREATED, getObjectType(), getObjectRecordStatus(), getFormNameWithSequenceNumber(), getCreatorEmailAddress(), isEafForm()));
		} else if(getType() == EventType.FORM_RECORD_MODIFIED){
			setDescription(ActivityLogManager.formAndFormDataDescription(EventType.FORM_RECORD_MODIFIED, getObjectType(), getObjectRecordStatus(), getFormNameWithSequenceNumber(), getCreatorEmailAddress(), isEafForm()));
		}
	}	

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return String
	 */
	public String getFormNameWithSequenceNumber() {
		return formNameWithSequenceNumber;
	}

	/**
	 * @param String formNameWithSequenceNumber
	 */
	public void setFormNameWithSequenceNumber(String formNameWithSequenceNumber) {
		this.formNameWithSequenceNumber = formNameWithSequenceNumber;
	}

	/**
	 * @return the creatorEmailAddress
	 */
	public String getCreatorEmailAddress() {
		return creatorEmailAddress;
	}

	/**
	 * @param creatorEmailAddress the creatorEmailAddress to set
	 */
	public void setCreatorEmailAddress(String creatorEmailAddress) {
		this.creatorEmailAddress = creatorEmailAddress;
	}

	/**
	 * @return the isEafForm
	 */
	public boolean isEafForm() {
		return isEafForm;
	}

	/**
	 * @param isEafForm the isEafForm to set
	 */
	public void setEafForm(boolean isEafForm) {
		this.isEafForm = isEafForm;
	}
}
