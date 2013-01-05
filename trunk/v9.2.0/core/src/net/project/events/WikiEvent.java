/**
 * 
 */
package net.project.events;

import net.project.activity.ActivityLogManager;
import net.project.security.SessionManager;

/**
 *
 */
public class WikiEvent extends ApplicationEvent {
	
	/** The name of wiki page on which event occurred. */
	private String name;
	
	/**
	 * 
	 */
	public WikiEvent(EventType eventType) {
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
			setDescription(ActivityLogManager.wikiDescription(EventType.NEW, getName(), getObjectRecordStatus()));
		} else if(getType() == EventType.EDITED){
			setDescription(ActivityLogManager.wikiDescription(EventType.EDITED, getName(), getObjectRecordStatus()));
		} else if(getType() == EventType.DELETED){
			setDescription(ActivityLogManager.wikiDescription(EventType.DELETED, getName(), getObjectRecordStatus()));
		} else if(getType() == EventType.IMAGE_UPLOADED){
			setDescription(ActivityLogManager.wikiDescription(EventType.IMAGE_UPLOADED, getName(), getObjectRecordStatus()));
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
