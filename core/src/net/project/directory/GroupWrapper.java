/**
 * 
 */
package net.project.directory;

import java.io.Serializable;

import net.project.gui.html.IHTMLOption;


public class GroupWrapper implements IHTMLOption, Serializable, Comparable {
	
	private String groupId;
	
	private String groupName;
	
	private boolean groupDefaultSelected;
	
	public GroupWrapper(){

	}

	public GroupWrapper(String groupId,String groupName){
		this.groupId = groupId;
		this.groupName = groupName;
	}
	
	public GroupWrapper(String groupId, String groupName, boolean groupDefaultSelected){
		this.groupId = groupId;
		this.groupName = groupName;
		this.groupDefaultSelected = groupDefaultSelected;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/* (non-Javadoc)
	 * @see net.project.gui.html.IHTMLOption#getHtmlOptionDisplay()
	 */
	public String getHtmlOptionDisplay() {
		return groupName;
	}

	/* (non-Javadoc)
	 * @see net.project.gui.html.IHTMLOption#getHtmlOptionValue()
	 */
	public String getHtmlOptionValue() {
		return groupId;
	}

	/**
	 * @return the groupDefaultSelected
	 */
	public boolean isGroupDefaultSelected() {
		return groupDefaultSelected;
	}

	/**
	 * @param groupDefaultSelected the groupDefaultSelected to set
	 */
	public void setGroupDefaultSelected(boolean groupDefaultSelected) {
		this.groupDefaultSelected = groupDefaultSelected;
	}

    /**
     * Compare the GroupWrapper object by group name
     * @param obj the GroupWrapper object
     * @return int
     */
	public int compareTo(Object obj) {
    	return this.groupName.compareToIgnoreCase(((GroupWrapper) obj).groupName);
	}

}
