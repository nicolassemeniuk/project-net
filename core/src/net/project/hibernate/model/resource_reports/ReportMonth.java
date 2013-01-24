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
package net.project.hibernate.model.resource_reports;

import java.math.BigDecimal;

/**
 * 
 * Assignment details report class
 * stores total assigned work for selected month 
 * for user for selected task
 *
 */
public class ReportMonth {

	/**
	 * month 
	 */
	private Integer monthInYear;

	/**
	 *  year
	 */
	private Integer year;

	/**
	 * total assigned work for month
	 */
	private Float totalWork;
	
	/**
	 * project allocation for selected month
	 */
	private BigDecimal allocation;

	/**
	 * allocation identifier - 
	 * necesary for updating existing allocation data 
	 */
	private Integer allocationId;
	
	/**
	 * @return Returns the monthInYear.
	 */
	public Integer getMonthInYear() {
		return monthInYear;
	}

	/**
	 * @param monthInYear The monthInYear to set.
	 */
	public void setMonthInYear(Integer monthInYear) {
		this.monthInYear = monthInYear;
	}

	/**
	 * @return Returns the totalWork.
	 */
	public Float getTotalWork() {
		return totalWork;
	}

	/**
	 * @param totalWork The totalWork to set.
	 */
	public void setTotalWork(Float totalWork) {
		this.totalWork = totalWork;
	}

	/**
	 * @return Returns the year.
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * @param year The year to set.
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

		
	/**
	 * @return Returns the allocation.
	 */
	public BigDecimal getAllocation() {
		return allocation;
	}

	/**
	 * @param allocation The allocation to set.
	 */
	public void setAllocation(BigDecimal allocation) {
		this.allocation = allocation;
	}

	
	
	/**
	 * @return Returns the allocationId.
	 */
	public Integer getAllocationId() {
		return allocationId;
	}

	/**
	 * @param allocationId The allocationId to set.
	 */
	public void setAllocationId(Integer allocationId) {
		this.allocationId = allocationId;
	}

	public ReportMonth() {
		super();
	}

}
