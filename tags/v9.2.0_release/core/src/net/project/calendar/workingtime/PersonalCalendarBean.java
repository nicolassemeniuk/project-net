/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
/**
 * 
 */
package net.project.calendar.workingtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class PersonalCalendarBean implements Serializable {
	
	private String dayOfWeekFormatted;
	
	private int dayNumber;
	
	private boolean workingDay;
	
	private boolean rowChange;
	
	private String workingTimeChecked;
	
	private String nonWorkingTimeChecked;
	
	private String htmlClass;
	
	private String entryId;
	
	private String dateDisplay;
	
	private String dateDescription;
	
	private List<PersonalCalendarBean.BoxDateBean> timeBoxIndexList = new ArrayList<PersonalCalendarBean.BoxDateBean>();

	/**
	 * @return the dayNumber
	 */
	public int getDayNumber() {
		return dayNumber;
	}

	/**
	 * @param dayNumber the dayNumber to set
	 */
	public void setDayNumber(int dayNumber) {
		this.dayNumber = dayNumber;
	}

	/**
	 * @return the dayOfWeekFormatted
	 */
	public String getDayOfWeekFormatted() {
		return dayOfWeekFormatted;
	}

	

	/**
	 * @param dayOfWeekFormatted the dayOfWeekFormatted to set
	 */
	public void setDayOfWeekFormatted(String dayOfWeekFormatted) {
		this.dayOfWeekFormatted = dayOfWeekFormatted;
	}

	
	

	/**
	 * @return the timeBoxIndexList
	 */
	public List<PersonalCalendarBean.BoxDateBean> getTimeBoxIndexList() {
		return timeBoxIndexList;
	}

	/**
	 * @param timeBoxIndexList the timeBoxIndexList to set
	 */
	public void setTimeBoxIndexList(List<PersonalCalendarBean.BoxDateBean> timeBoxIndexList) {
		this.timeBoxIndexList = timeBoxIndexList;
	}
	
	
	public class BoxDateBean implements Serializable{
		
		private Integer timeBoxIndex;
		
		private Date startDate;
		
		private Date endDate;
		
		private String startTimeName;
		
		private String endTimeName;
		
		private boolean disabled;
		
		private String timeControlName;
		
		
		/**
		 * @return the disabled
		 */
		public boolean isDisabled() {
			return disabled;
		}

		/**
		 * @param disabled the disabled to set
		 */
		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}

		/**
		 * @return the endTimeName
		 */
		public String getEndTimeName() {
			return endTimeName;
		}

		/**
		 * @param endTimeName the endTimeName to set
		 */
		public void setEndTimeName(String endTimeName) {
			this.endTimeName = endTimeName;
		}

		/**
		 * @return the startTimeName
		 */
		public String getStartTimeName() {
			return startTimeName;
		}

		/**
		 * @param startTimeName the startTimeName to set
		 */
		public void setStartTimeName(String startTimeName) {
			this.startTimeName = startTimeName;
		}

		/**
		 * @return the endDate
		 */
		public Date getEndDate() {
			return endDate;
		}

		/**
		 * @param endDate the endDate to set
		 */
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		/**
		 * @return the startDate
		 */
		public Date getStartDate() {
			return startDate;
		}

		/**
		 * @param startDate the startDate to set
		 */
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		/**
		 * @return the timeBoxIndex
		 */
		public Integer getTimeBoxIndex() {
			return timeBoxIndex;
		}

		/**
		 * @param timeBoxIndex the timeBoxIndex to set
		 */
		public void setTimeBoxIndex(Integer timeBoxIndex) {
			this.timeBoxIndex = timeBoxIndex;
		}

		/**
		 * @return the timeControlName
		 */
		public String getTimeControlName() {
			return timeControlName;
		}

		/**
		 * @param timeControlName the timeControlName to set
		 */
		public void setTimeControlName(String timeControlName) {
			this.timeControlName = timeControlName;
		}
	}


	/**
	 * @return the workingDay
	 */
	public boolean isWorkingDay() {
		return workingDay;
	}

	/**
	 * @param workingDay the workingDay to set
	 */
	public void setWorkingDay(boolean workingDay) {
		this.workingDay = workingDay;
	}

	/**
	 * @return the rowChange
	 */
	public boolean isRowChange() {
		return rowChange;
	}

	/**
	 * @param rowChange the rowChange to set
	 */
	public void setRowChange(boolean rowChange) {
		this.rowChange = rowChange;
	}

	/**
	 * @return the nonWorkingTimeChecked
	 */
	public String getNonWorkingTimeChecked() {
		return nonWorkingTimeChecked;
	}

	/**
	 * @param nonWorkingTimeChecked the nonWorkingTimeChecked to set
	 */
	public void setNonWorkingTimeChecked(String nonWorkingTimeChecked) {
		this.nonWorkingTimeChecked = nonWorkingTimeChecked;
	}

	/**
	 * @return the workingTimeChecked
	 */
	public String getWorkingTimeChecked() {
		return workingTimeChecked;
	}

	/**
	 * @param workingTimeChecked the workingTimeChecked to set
	 */
	public void setWorkingTimeChecked(String workingTimeChecked) {
		this.workingTimeChecked = workingTimeChecked;
	}

	/**
	 * @return the htmlClass
	 */
	public String getHtmlClass() {
		return htmlClass;
	}

	/**
	 * @param htmlClass the htmlClass to set
	 */
	public void setHtmlClass(String htmlClass) {
		this.htmlClass = htmlClass;
	}

	/**
	 * @return the dateDisplay
	 */
	public String getDateDisplay() {
		return dateDisplay;
	}

	/**
	 * @param dateDisplay the dateDisplay to set
	 */
	public void setDateDisplay(String dateDisplay) {
		this.dateDisplay = dateDisplay;
	}

	/**
	 * @return the entryId
	 */
	public String getEntryId() {
		return entryId;
	}

	/**
	 * @param entryId the entryId to set
	 */
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	/**
	 * @return the dateDescription
	 */
	public String getDateDescription() {
		return dateDescription;
	}

	/**
	 * @param dateDescription the dateDescription to set
	 */
	public void setDateDescription(String dateDescription) {
		this.dateDescription = dateDescription;
	}
}



