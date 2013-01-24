/**
 * 
 */
package net.project.events;

import java.util.Date;

import net.project.activity.ActivityLogManager;
import net.project.security.SessionManager;

/**
 *
 */
public class NewsEvent extends ApplicationEvent {
	
	private String name;
	
	public NewsEvent(EventType eventType) {
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
			setDescription(ActivityLogManager.newsDescription(EventType.NEW, getObjectRecordStatus()));
		} else if(getType() == EventType.EDITED){
			setDescription(ActivityLogManager.newsDescription(EventType.EDITED, getObjectRecordStatus()));
		} else if(getType() == EventType.DELETED){
			setDescription(ActivityLogManager.newsDescription(EventType.DELETED, getObjectRecordStatus()));
		}
	}
	
	/* (non-Javadoc)
	 * @see net.project.notification.ApplicationEvent#afterPublish()
	 */
	@Override
	public void afterPublish() {
		super.afterPublish();
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
