/**
 * 
 */
package net.project.events;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import net.project.base.EventException;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.util.InvalidDateException;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * Provides defualt implementation of activity event
 */
public abstract class AbstractEvent implements Serializable {

    /**
     * The Object Type for the target Object, which is really
     * the event type and is actually the same as <code>code</code>.
     * */
	protected String objectType = null;
    
    protected String objectID;

    /**
     * The ID of the person performing the action that causes
     * the event.
     */
    private String initiatorID = null;

    /**
     * The display name of the person performing the action that
     * causes the event.
     */
    private String eventBy = null;

    /** The date at which the event occurred. */
    private Date eventDate;

    /** The Space ID of the space in which the event occurred. */
    private String spaceID = null;
    
    protected EventType type;
    
    /** The description of the object on which event occurred. */
    protected String description;
    
    /** The parent object id of target object id. */
    private String parentObjectId;
    
    /** The record status of the object on which event occurred */
    private String objectRecordStatus;
    
    /** Flag whether blogentry is important or not*/
    private Integer isImportant;
    
    private boolean isPublished;
    
	public AbstractEvent() {
		this.eventDate = Calendar.getInstance().getTime();
	}
    
    /**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.initiatorID = user.getID();
		this.eventBy = user.getDisplayName();
	}
	
	/**
	 * Method for processing event object before publishing
	 */
	public void beforePublish(){
	}
	
	/**
	 * Method to publish an event to JMS queue
	 * @throws EventException
	 */
	public void publish() throws EventException {
		// before publish hook for event intgrators
		beforePublish();
		
		if(StringUtils.isEmpty(getObjectID()) || StringUtils.isEmpty(getObjectType())
				|| getType() == null || StringUtils.isEmpty(getSpaceID())){
			throw new EventException("One of the required values are not set for publihsing an event");
		}
				
		try {
			ServiceFactory.getInstance().getEventPublisher().publish(this);	
			setPublished(true);
		} catch (Exception e) {
			throw new EventException(e.getMessage(), e);			
		}
		
		// after publish hook for event intgrators
		afterPublish();
	}
	
	/**
	 * Method for processing event object after publishing 
	 */
	public void afterPublish(){
	}
    
	/**
	 * @return the eventBy
	 */
	public String getEventBy() {
		return eventBy;
	}

	/**
	 * @param eventBy the eventBy to set
	 */
	public void setEventBy(String eventBy) {
		this.eventBy = eventBy;
	}
	
	/**
	 * @return the eventType
	 */
	public EventType getType() {
		return type;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setType(EventType type) {
	}

	/**
	 * @return the initiatorID
	 */
	public String getInitiatorID() {
		return initiatorID;
	}

	/**
	 * @param initiatorID the initiatorID to set
	 */
	public void setInitiatorID(String initiatorID) {
		this.initiatorID = initiatorID;
	}

	/**
	 * @return the objectID
	 */
	public String getObjectID() {
		return objectID;
	}

	/**
	 * @param objectID the objectID to set
	 */
	public void setObjectID(String objectID) {
	}

	/**
	 * @return the spaceID
	 */
	public String getSpaceID() {
		return spaceID;
	}

	/**
	 * @param spaceID the spaceID to set
	 */
	public void setSpaceID(String spaceID) {
		this.spaceID = spaceID;
	}

	/**
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * @param objectType the objectType to set
	 */
	public void setObjectType(String objectType) {
	}

	/**
	 * @return the eventDate
	 */
	public Date getEventDate() {
		return eventDate;
	}

	/**
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
	}

	/**
	 * @return the isPublished
	 */
	public boolean isPublished() {
		return isPublished;
	}

	/**
	 * @param isPublished the isPublished to set
	 */
	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}

	/**
	 * @return the parentObjectId
	 */
	public String getParentObjectId() {
		return parentObjectId;
	}

	/**
	 * @param parentObjectId the parentObjectId to set
	 */
	public void setParentObjectId(String parentObjectId) {
		this.parentObjectId = parentObjectId;
	}

	/**
	 * @return the objectRecordStatus
	 */
	public String getObjectRecordStatus() {
		return objectRecordStatus;
	}

	/**
	 * @param objectRecordStatus the objectRecordStatus to set
	 */
	public void setObjectRecordStatus(String objectRecordStatus) {
		this.objectRecordStatus = objectRecordStatus;
	}

	/**
	 * @return Integer
	 */
	public Integer getIsImportant() {
		return isImportant;
	}

	/**
	 * @param Integer isImportant
	 */
	public void setIsImportant(Integer isImportant) {
		this.isImportant = isImportant;
	}
}
