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
package net.project.schedule.importer;

import java.util.HashMap;
import java.util.Map;

import net.project.util.TimeQuantityUnit;

/**
 * Class to allow a mapping from MS project time quantity id's to Project.Net
 * TimeQuantityUnits. This class contains multiple instances of a
 * <code>MSPTimeQuantity</code> class and a map of MS project id which lead to
 * the instances of these classes.
 * 
 * @author Matthew Flower
 * @since Version 7.4
 */
class MSPTimeQuantityUnitLookup {
	private static Map unitMap = new HashMap();

	// Populate the lookup map appropriately.
	static {
		unitMap.put(new Integer(3), new MSPTimeQuantityUnit(false, false, TimeQuantityUnit.MINUTE)); // Minute
		unitMap.put(new Integer(4), new MSPTimeQuantityUnit(true, false, TimeQuantityUnit.MINUTE)); // Elapsed
																									// Minute
		unitMap.put(new Integer(5), new MSPTimeQuantityUnit(false, false, TimeQuantityUnit.HOUR)); // Hour
		unitMap.put(new Integer(6), new MSPTimeQuantityUnit(true, false, TimeQuantityUnit.HOUR)); // Elapsed
																									// Hour
		unitMap.put(new Integer(7), new MSPTimeQuantityUnit(false, false, TimeQuantityUnit.DAY)); // Day
		unitMap.put(new Integer(8), new MSPTimeQuantityUnit(true, false, TimeQuantityUnit.DAY)); // Elapsed
																									// Day
		unitMap.put(new Integer(9), new MSPTimeQuantityUnit(false, false, TimeQuantityUnit.WEEK)); // Week
		unitMap.put(new Integer(10), new MSPTimeQuantityUnit(true, false, TimeQuantityUnit.WEEK)); // Elapsed
																									// Week
		unitMap.put(new Integer(11), new MSPTimeQuantityUnit(false, false, TimeQuantityUnit.MONTH)); // Month
		unitMap.put(new Integer(12), new MSPTimeQuantityUnit(true, false, TimeQuantityUnit.MONTH)); // Elapsed
																									// Month
		unitMap.put(new Integer(21), new MSPTimeQuantityUnit(false, false, TimeQuantityUnit.DAY)); // ???
																									// Not
																									// really
																									// sure
																									// --
																									// Seems
																									// like
																									// DAY
		unitMap.put(new Integer(35), new MSPTimeQuantityUnit(false, true, TimeQuantityUnit.MINUTE)); // Estimated
																										// Minute
		unitMap.put(new Integer(36), new MSPTimeQuantityUnit(true, true, TimeQuantityUnit.MINUTE)); // Estimated
																									// Elapsed
																									// Minute
		unitMap.put(new Integer(37), new MSPTimeQuantityUnit(false, true, TimeQuantityUnit.HOUR)); // Estimated
																									// Hour
		unitMap.put(new Integer(38), new MSPTimeQuantityUnit(true, true, TimeQuantityUnit.HOUR)); // Estimated
																									// Elapsed
																									// Hour
		unitMap.put(new Integer(39), new MSPTimeQuantityUnit(false, true, TimeQuantityUnit.DAY)); // Estimated
																									// Day
		unitMap.put(new Integer(40), new MSPTimeQuantityUnit(true, true, TimeQuantityUnit.DAY)); // Estimated
																									// Elapsed
																									// Day
		unitMap.put(new Integer(41), new MSPTimeQuantityUnit(false, true, TimeQuantityUnit.WEEK)); // Estimated
																									// Week
		unitMap.put(new Integer(42), new MSPTimeQuantityUnit(true, true, TimeQuantityUnit.WEEK)); // Estimated
																									// Elapsed
																									// Week
		unitMap.put(new Integer(43), new MSPTimeQuantityUnit(false, true, TimeQuantityUnit.MONTH)); // Estimated
																									// Month
		unitMap.put(new Integer(44), new MSPTimeQuantityUnit(true, true, TimeQuantityUnit.MONTH)); // Estimated
																									// Elapsed
																									// Month
		unitMap.put(new Integer(53), new MSPTimeQuantityUnit(true, true, TimeQuantityUnit.DAY)); // Estimated
																									// Day

		// Default unit for a unit int found in some project files
		unitMap.put(new Integer(0), new MSPTimeQuantityUnit(false, false, TimeQuantityUnit.HOUR));
	}

	/**
	 * Time Quantity class for MS project Time Quantities.
	 * 
	 * @author Matthew Flower
	 * @since Version 7.4
	 */
	public static class MSPTimeQuantityUnit {
		private boolean isElapsed;

		private boolean isEstimated;

		private TimeQuantityUnit timeQuantityUnit;

		/**
		 * Construct a new <code>MSPTimeQuantityUnit</code> instance.
		 * 
		 * @param elapsed
		 *            a <code>boolean</code> value indicating whether this is
		 *            elapsed time or planned time.
		 * @param estimated
		 *            a <code>boolean</code> value indicating whether this is
		 *            estimated or define time.
		 * @param timeQuantityUnit
		 *            a <code>TimeQuantityUnit</code> that this
		 *            MSPTimeQuantityUnit corresponds to.
		 */
		public MSPTimeQuantityUnit(boolean elapsed, boolean estimated, TimeQuantityUnit timeQuantityUnit) {
			isElapsed = elapsed;
			isEstimated = estimated;
			this.timeQuantityUnit = timeQuantityUnit;
		}

		public boolean isElapsed() {
			return isElapsed;
		}

		public void setElapsed(boolean elapsed) {
			isElapsed = elapsed;
		}

		public boolean isEstimated() {
			return isEstimated;
		}

		public void setEstimated(boolean estimated) {
			isEstimated = estimated;
		}

		public TimeQuantityUnit getTimeQuantityUnit() {
			return timeQuantityUnit;
		}

		public void setTimeQuantityUnit(TimeQuantityUnit timeQuantityUnit) {
			this.timeQuantityUnit = timeQuantityUnit;
		}
	}

	public static MSPTimeQuantityUnit getForInt(int unitID) {
		MSPTimeQuantityUnit unit = (MSPTimeQuantityUnit) unitMap.get(new Integer(unitID));
		if (unit == null) {
			throw new IllegalArgumentException("Unsupported MS Project time quantity unit with id: " + unitID);
		}
		return unit;
	}
}
