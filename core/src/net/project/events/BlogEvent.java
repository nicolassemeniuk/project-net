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
public class BlogEvent extends ApplicationEvent {
	
	private String blogEntryTitle;
	
	/**
	 * 
	 */
	public BlogEvent(EventType eventType) {
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
			setDescription(ActivityLogManager.blogDescription(Integer.parseInt(getObjectID()), EventType.NEW, getObjectType()));
		} else if(getType() == EventType.EDITED){
			setDescription(ActivityLogManager.blogDescription(Integer.parseInt(getObjectID()), EventType.EDITED, getObjectType()));
		} else if(getType()== EventType.DELETED){
			setDescription(ActivityLogManager.blogDescription(Integer.parseInt(getObjectID()), EventType.DELETED, getObjectType()));
		} else if(getType()== EventType.COMMENTED){
			setDescription(ActivityLogManager.blogDescription(Integer.parseInt(getObjectID()), EventType.COMMENTED, getObjectType()));
		}
	}
		
	/**
	 * @return the blogEntryTitle
	 */
	public String getBlogEntryTitle() {
		return blogEntryTitle;
	}

	/**
	 * @param blogEntryTitle the blogEntryTitle to set
	 */
	public void setBlogEntryTitle(String blogEntryTitle) {
		this.blogEntryTitle = blogEntryTitle;
	}

}
