/**
 * 
 */
package net.project.events;

import net.project.activity.ActivityLogManager;
import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;

public class ProjectEvent extends ApplicationEvent {

	private String name;
	
	private int noOfmembers;
	
	public static final String MEMBER_ADDED = PropertyProvider.get("prm.project.activity.memberadded.message");
	
	public static final String MEMBER_REMOVED = PropertyProvider.get("prm.project.activity.memberremoved.message");

	/**
	 * 
	 */
	public ProjectEvent(EventType eventType) {
		init();
		setType(eventType);
	}

	public void init() {
		setUser(SessionManager.getUser());
	}

	/* (non-Javadoc)
	 * @see net.project.events.ApplicationEvent#beforePublish()
	 */
	@Override
	public void beforePublish() {
		if (getType() == EventType.NEW) {
			setDescription(ActivityLogManager.projectDescription(EventType.NEW, getObjectRecordStatus(), getNoOfMembers()));
		} else if (getType() == EventType.EDITED){
			setDescription(ActivityLogManager.projectDescription(EventType.EDITED, getObjectRecordStatus(), getNoOfMembers()));
		} else if (getType() == EventType.MEMBER_ADDED_TO_SPACE){
			setDescription(ActivityLogManager.projectDescription(EventType.MEMBER_ADDED_TO_SPACE, getObjectRecordStatus(), getNoOfMembers()));
		} else if (getType() == EventType.MEMBER_DELETED_FROM_SPACE){
			setDescription(ActivityLogManager.projectDescription(EventType.MEMBER_DELETED_FROM_SPACE, getObjectRecordStatus(), getNoOfMembers()));
		} else if (getType() == EventType.OVERALL_STATUS_CHANGED){
			setDescription(ActivityLogManager.projectDescription(EventType.OVERALL_STATUS_CHANGED, getObjectRecordStatus(), getNoOfMembers()));
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
	 * @return no. of members
	 */
	public int getNoOfMembers() {
		return noOfmembers;
	}
	
	/**
	 * @param no. of members
	 */
	public void setNoOfMembers(int noOfmembers) {
		this.noOfmembers = noOfmembers;
	}
}
