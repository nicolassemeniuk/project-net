/**
 * 
 */
package net.project.schedule;

import java.util.List;

/**
 * @author 
 *
 */
public class ScheduleRow {
	
	/**
	 * 
	 */
	private ScheduleEntry scheduleEntry;
	
	/**
	 * 
	 */
	private List<ScheduleColumn> scheduleColumns;
	
	/**
	 * 
	 */
	private boolean firstRow;
	
	/**
	 * @param scheduleEntry
	 * @param scheduleColumns
	 */
	public ScheduleRow(ScheduleEntry scheduleEntry, List<ScheduleColumn> scheduleColumns) {
		this.scheduleEntry = scheduleEntry;
		this.scheduleColumns = scheduleColumns;
	}
	/**
	 * @return the scheduleColumns
	 */
	public List<ScheduleColumn> getScheduleColumns() {
		return scheduleColumns;
	}
	/**
	 * @param scheduleColumns the scheduleColumns to set
	 */
	public void setScheduleColumns(List<ScheduleColumn> scheduleColumns) {
		this.scheduleColumns = scheduleColumns;
	}
	/**
	 * @return the scheduleEntry
	 */
	public ScheduleEntry getScheduleEntry() {
		return scheduleEntry;
	}
	/**
	 * @param scheduleEntry the scheduleEntry to set
	 */
	public void setScheduleEntry(ScheduleEntry scheduleEntry) {
		this.scheduleEntry = scheduleEntry;
	}
	/**
	 * @return the firstRow
	 */
	public boolean isFirstRow() {
		return firstRow;
	}

}
