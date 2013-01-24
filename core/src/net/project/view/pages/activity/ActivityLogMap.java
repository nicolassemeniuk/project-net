/**
 * 
 */
package net.project.view.pages.activity;

import java.util.Date;
import java.util.List;

import net.project.hibernate.model.PnActivityLog;

/**
 *
 */
public class ActivityLogMap {
	
	private Date key;
	
	private List<PnActivityLog> values;


	public ActivityLogMap() {
	}
	
	public ActivityLogMap(Date key, List<PnActivityLog> values) {
		this.key = key;
		this.values = values;
	}

	/**
	 * @return Returns the key.
	 */
	public Date getKey() {
		return key;
	}

	/**
	 * @param key The key to set.
	 */
	public void setKey(Date key) {
		this.key = key;
	}

	/**
	 * @return Returns the values.
	 */
	public List<PnActivityLog> getValues() {
		return values;
	}

	/**
	 * @param values The values to set.
	 */
	public void setValues(List<PnActivityLog> values) {
		this.values = values;
	}
}