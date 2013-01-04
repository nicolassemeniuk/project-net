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
package net.project.schedule.exporter.mvc;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import net.project.schedule.ScheduleTimeQuantity;
import net.project.schedule.TaskConstraintType;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
/**
 * @author avibha
 *
 */
public class ExportHelper {
	static final String TASK_PRIORITY_LOW = "10";
	static final String TASK_PRIORITY_NORMAL = "20";
	static final String TASK_PRIORITY_HIGH = "30";
	
	static final String MSP_PRIORITY_LOW = "499";
	static final String MSP_PRIORITY_NORMAL = "500"; 
	static final String MSP_PRIORITY_HIGH = "501";
	
	/**
	 * 
	 * @param constraintType
	 * @return
	 */
	public static int getMSPConstraintId(String constraintType) {
		int id;
		int defaultConstraintId = 0;
		try {
			id = Integer.parseInt(constraintType);
		} catch (NumberFormatException e) {
			// TODO: log error
			return Integer.parseInt(TaskConstraintType.DEFAULT_TASK_CONSTRAINT.getID());
		}
		
		switch(id) {
		case 10:
			return 0;
		case 20:
			return 1;
		case 30:
			return 7;
		case 40:
			return 6;
		case 50:
			return 2;
		case 60:
			return 3;
		case 70:
			return 4;
		case 80:
			return 5;
		default:
			return defaultConstraintId;
		}
	}

	/**
	 * 
	 * @param timeQty
	 * @return
	 */
	static Duration getDuration(TimeQuantity timeQty) {
		TimeQuantity tQty = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		
		//int unit = timeQty.getUnits().getUniqueID();
		
	    if (timeQty.getUnits().compareTo(TimeQuantityUnit.HOUR) < 0) {
            // Desired unit is less than hours; use standard conversion
	    	if (timeQty.getUnits().compareTo(TimeQuantityUnit.MINUTE)==0) {
	    		tQty = ScheduleTimeQuantity.convertToUnit(timeQty,TimeQuantityUnit.MINUTE,2, BigDecimal.ROUND_HALF_UP);
	    		minute = tQty.getAmount().intValue();	    
	    	}
	    }
	    else  {
	    	tQty = ScheduleTimeQuantity.convertToUnit(timeQty,TimeQuantityUnit.MINUTE,2, BigDecimal.ROUND_HALF_UP);
    		minute = tQty.getAmount().intValue();	  
	    }
	    	    
		DatatypeFactory dataTypeFactory;
		try {
			dataTypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			return null;
		}
		
		try{
			return dataTypeFactory.newDuration( true, null, null, null, BigInteger.valueOf(hour), BigInteger.valueOf(minute), BigDecimal.valueOf(second));
		} catch (IllegalArgumentException iae) {
			return dataTypeFactory.newDuration(0);
		}
		
	}
	
	/**
	 * Function to return DurationFormat equivalent to msproject
	 * @param durationTQ
	 * @return
	 * see MSPTimeQuantityUnitLookup
	 */
	public static BigInteger getDurationFormat(TimeQuantity durationTQ) {
		int unit = durationTQ.getUnits().getUniqueID();
		String durationFormat="5";	//set default to hour
		switch (unit){
		case 2:	//minute
			durationFormat="3";
			break;
		case 4: //hour
			durationFormat="5";
			break;
		case 8: //day
			durationFormat="7";
			break;
		case 16: //week
			durationFormat="9";
			break;
		case 256: //month
			durationFormat="11";
			break;
		default:
			durationFormat="5";
			break;
		}		
		return new BigInteger(durationFormat);
	}

	/**
	 * Return msproject equivalent priority id
	 * @param priority
	 * @return
	 */
	public static String getPriority(String priority) {
		if(TASK_PRIORITY_LOW.equals(priority)) {
			return MSP_PRIORITY_LOW;
		} else if(TASK_PRIORITY_NORMAL.equals(priority)) {
			return MSP_PRIORITY_NORMAL;
		} else {
			return MSP_PRIORITY_HIGH;
		}
	}
}
