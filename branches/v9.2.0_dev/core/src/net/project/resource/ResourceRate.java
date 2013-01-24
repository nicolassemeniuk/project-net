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
package net.project.resource;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author avinash
 *
 */
public class ResourceRate {

	private BigDecimal costPerUse;
	private BigDecimal overtimeRate;
	private BigInteger overtimeRateFormat;
	private XMLGregorianCalendar ratesFrom;
	private XMLGregorianCalendar ratesTo;
	private BigInteger rateTable; 
	private BigDecimal standardRate;
	private BigInteger standardRateFormat;

	/**
	 * 
	 * @param costPerUse
	 * @param overtimeRate
	 * @param overtimeRateFormat
	 * @param ratesFrom
	 * @param ratesTo
	 * @param rateTable
	 * @param standardRate
	 * @param standardRateFormat
	 */
	public ResourceRate(BigDecimal costPerUse, BigDecimal overtimeRate, BigInteger overtimeRateFormat, XMLGregorianCalendar ratesFrom, XMLGregorianCalendar ratesTo, BigInteger rateTable, BigDecimal standardRate, BigInteger standardRateFormat) {
		this.costPerUse = costPerUse;
		this.overtimeRate = overtimeRate;
		this.overtimeRateFormat = overtimeRateFormat;
		this.ratesFrom = ratesFrom;
		this.ratesTo = ratesTo;
		this.rateTable = rateTable;
		this.standardRate = standardRate;
		this.standardRateFormat = standardRateFormat;
	}

	public BigDecimal getCostPerUse() {
		return costPerUse;
	}

	public void setCostPerUse(BigDecimal costPerUse) {
		this.costPerUse = costPerUse;
	}

	public BigDecimal getOvertimeRate() {
		return overtimeRate;
	}

	public void setOvertimeRate(BigDecimal overtimeRate) {
		this.overtimeRate = overtimeRate;
	}

	public BigInteger getOvertimeRateFormat() {
		return overtimeRateFormat;
	}

	public void setOvertimeRateFormat(BigInteger overtimeRateFormat) {
		this.overtimeRateFormat = overtimeRateFormat;
	}

	public XMLGregorianCalendar getRatesFrom() {
		return ratesFrom;
	}

	public void setRatesFrom(XMLGregorianCalendar ratesFrom) {
		this.ratesFrom = ratesFrom;
	}

	public XMLGregorianCalendar getRatesTo() {
		return ratesTo;
	}

	public void setRatesTo(XMLGregorianCalendar ratesTo) {
		this.ratesTo = ratesTo;
	}

	public BigInteger getRateTable() {
		return rateTable;
	}

	public void setRateTable(BigInteger rateTable) {
		this.rateTable = rateTable;
	}

	public BigDecimal getStandardRate() {
		return standardRate;
	}

	public void setStandardRate(BigDecimal standardRate) {
		this.standardRate = standardRate;
	}

	public BigInteger getStandardRateFormat() {
		return standardRateFormat;
	}

	public void setStandardRateFormat(BigInteger standardRateFormat) {
		this.standardRateFormat = standardRateFormat;
	}
	
}
