/**
 * 
 */
package net.project.events;

import java.util.Date;

import net.project.activity.ActivityLogManager;
import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;

/**
 *
 */
public class DocumentEvent extends ApplicationEvent {
	
	private String name;
	
	/**
	 * 
	 */
	public DocumentEvent(EventType eventType) {
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
			setDescription(ActivityLogManager.documentDescription(EventType.NEW, getObjectRecordStatus()));
		} else if(getType() == EventType.UNDO_REMOVE_CONTAINER){
			setDescription(ActivityLogManager.documentDescription(EventType.UNDO_REMOVE_CONTAINER, getObjectRecordStatus()));
		} else if(getType() == EventType.DELETED){
			setDescription(ActivityLogManager.documentDescription(EventType.DELETED, getObjectRecordStatus()));
		} else if(getType() == EventType.EDITED){
			setDescription(ActivityLogManager.documentDescription(EventType.EDITED, getObjectRecordStatus()));
		} else if(getType() == EventType.CHECKED_OUT){
			setDescription(ActivityLogManager.documentDescription(EventType.CHECKED_OUT, getObjectRecordStatus()));
		} else if(getType() == EventType.UNDO_CHECKED_OUT){
			setDescription(ActivityLogManager.documentDescription(EventType.UNDO_CHECKED_OUT, getObjectRecordStatus()));
		} else if(getType() == EventType.VIEWED){
			setDescription(ActivityLogManager.documentDescription(EventType.VIEWED, getObjectRecordStatus()));
		} else if(getType() == EventType.MOVED){
			setDescription(ActivityLogManager.documentDescription(EventType.MOVED, getObjectRecordStatus()));
		} else if(getType() == EventType.FOLDER_CREATED){
			setDescription(ActivityLogManager.documentDescription(EventType.FOLDER_CREATED, getObjectRecordStatus()));
		}else if(getType() == EventType.FOLDER_DELETED){
			setDescription(ActivityLogManager.documentDescription(EventType.FOLDER_DELETED, getObjectRecordStatus()));
		} else if(getType() == EventType.CHECKED_IN){
			setDescription(ActivityLogManager.documentDescription(EventType.CHECKED_IN, getObjectRecordStatus()));
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

}
