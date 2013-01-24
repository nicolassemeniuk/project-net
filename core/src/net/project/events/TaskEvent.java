/**
 * 
 */
package net.project.events;

import net.project.activity.ActivityLogManager;
import net.project.security.SessionManager;

/**
 *
 */
public class TaskEvent extends ApplicationEvent {
	
	private String taskName;
	
	/**
	 * 
	 */
	public TaskEvent(EventType eventType) {
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
			setDescription(ActivityLogManager.taskDescription(EventType.NEW, getObjectRecordStatus()));
		} else if(getType() == EventType.EDITED){
			setDescription(ActivityLogManager.taskDescription(EventType.EDITED, getObjectRecordStatus()));
		} else if(getType() == EventType.DELETED){
			setDescription(ActivityLogManager.taskDescription(EventType.DELETED, getObjectRecordStatus()));
		}
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
}
