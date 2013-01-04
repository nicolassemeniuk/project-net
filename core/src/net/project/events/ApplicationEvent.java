package net.project.events;


/**
 *
 */
public class ApplicationEvent extends AbstractEvent {
	
	/**
	 * 
	 */
	public ApplicationEvent() {
		super();
	}
	
	public ApplicationEvent(String objectID, String objectType, EventType eventType, String description){
		setObjectID(objectID);
		setObjectType(objectType);
		setDescription(description);
		setType(eventType);
	}
	
	/* (non-Javadoc)
	 * @see net.project.notification.AbstractEvent#setEventType(java.lang.String)
	 */
	public void setType(EventType type) {
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see net.project.notification.AbstractEvent#setObjectID(java.lang.String)
	 */
	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}
	
	/* (non-Javadoc)
	 * @see net.project.notification.AbstractEvent#setObjectType(java.lang.String)
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	
	/* (non-Javadoc)
	 * @see net.project.events.AbstractEvent#setDescription(java.lang.String)
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/* (non-Javadoc)
	 * @see net.project.notification.AbstractEvent#beforePublish()
	 */
	public void beforePublish() {
	}
	
	/* (non-Javadoc)
	 * @see net.project.notification.AbstractEvent#afterPublish()
	 */
	public void afterPublish() {
	}

}
