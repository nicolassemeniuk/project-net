/**
 * 
 */
package net.project.view.pages.activity;

import java.io.Serializable;
import java.util.List;

public class ActivityObject implements Serializable {
	
	private View.ActivityObjects objectName; 
	
	private String name;
	
	private Integer id;
	/**
	 * 
	 */
	private List actions;
	
	private boolean isForm; 
	
	/**
	 * 
	 */
	public ActivityObject(View.ActivityObjects objectName, List actions, boolean isForm) {
		this.objectName = objectName;
		this.actions = actions;
		this.isForm = isForm;
	}

	public ActivityObject(String objectName, Integer id, List actions) {
		this.name = objectName;
		this.actions = actions;
		this.id = id;
	}
	/**
	 * @return the actions
	 */
	public List getActions() {
		return actions;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(List actions) {
		this.actions = actions;
	}

	/**
	 * @return the objectName
	 */
	public View.ActivityObjects getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName the objectName to set
	 */
	public void setObjectName(View.ActivityObjects objectName) {
		this.objectName = objectName;
	}	
	
	/**
	 * Return string of object name
	 * @return String
	 */
	public String getObjectNameString(){
		return objectName.toString();
	}

	/**
	 * @return the isForm
	 */
	public boolean getIsForm() {
		return isForm;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}