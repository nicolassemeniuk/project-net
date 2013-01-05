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
/*-----------------------------------------------------------------------------+
 |
 |    $RCSfile$
 |   $Revision: 15807 $
 |       $Date: 2007-04-09 03:03:58 +0530 (Mon, 09 Apr 2007) $
 |     $Author: avinash $
 |
 +-----------------------------------------------------------------------------*/
package net.project.schedule.importer;

import java.math.BigDecimal;

import net.project.schedule.ScheduleTimeQuantity;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

class MSPTimeQuantity {
	private int minuteAmount;

	private int destinationFormat;

	public MSPTimeQuantity(int minuteAmount, int destinationFormat) {
		this.minuteAmount = minuteAmount;
		this.destinationFormat = destinationFormat;
	}

	public TimeQuantity getTimeQuantity() {

		BigDecimal storedMinutes = new BigDecimal(minuteAmount).divide(new BigDecimal(10), 0, BigDecimal.ROUND_HALF_UP);
		TimeQuantityUnit destinationUnit = MSPTimeQuantityUnitLookup.getForInt(destinationFormat).getTimeQuantityUnit();

		// Convert the stored minutes value to the destination unit as
		// determined by the destination format
		// This performs the correct conversion such that equivalent of 24 hours
		// in DAY format is 3 days
		TimeQuantity tqToReturn = ScheduleTimeQuantity.convertToUnit(new TimeQuantity(storedMinutes, TimeQuantityUnit.MINUTE), destinationUnit, 2, BigDecimal.ROUND_HALF_UP);

		return tqToReturn;
	}

}
