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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.project.schedule.calc.TaskCalculationType;

/**
 * Provides a helper for converting MSP task types and effort driven flags into
 * our TaskCalculationType.
 * 
 * @author Tim Morrow
 * @since Version 7.7.0
 */
class MSPTaskCalculationType {

	/**
	 * Provides a lookup from MSP's task type value (Fixed Unit, Duration, Work)
	 * to our FixedElement value.
	 * <p>
	 * Each key is an <code>Integer</code>, each value is a
	 * <code>TaskCalculationType.FixedElement</code>.
	 * </p>
	 */
	static final Map MSP_FIXED_LOOKUP;
	static {
		Map map = new HashMap();
		map.put(new Integer(0), TaskCalculationType.FixedElement.UNIT);
		map.put(new Integer(1), TaskCalculationType.FixedElement.DURATION);
		map.put(new Integer(2), TaskCalculationType.FixedElement.WORK);
		MSP_FIXED_LOOKUP = Collections.unmodifiableMap(map);
	}

	/**
	 * Constructs a TaskCalculationType from the specified MSP task type and
	 * effort driven flag.
	 * 
	 * @param taskType
	 *            the MSP task type
	 *            <ul>
	 *            <li>0 - Fixed Unit
	 *            <li>1 - Fixed Duration
	 *            <li>2 - Fixed Work
	 *            </ul>
	 * @param isEffortDriven
	 *            the effort driven flag
	 * @return the task calculation type
	 */
	static TaskCalculationType makeTaskCalculationType(int taskType, boolean isEffortDriven) {
		return TaskCalculationType.makeFromComponents((TaskCalculationType.FixedElement) MSP_FIXED_LOOKUP.get(new Integer(taskType)), isEffortDriven);
	}

}