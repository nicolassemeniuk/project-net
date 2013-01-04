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

/*----------------------------------------------------------------------+
 |                                                                       
 |     $RCSfile$
 |    $Revision: 14865 $
 |        $Date: 2006-03-31 01:19:17 -0300 (Vie, 31 Mar 2006) $
 |      $Author: avinash $
 |                                                                       
 +----------------------------------------------------------------------*/
package net.project.process;

import java.util.HashMap;

import net.project.persistence.PersistenceException;

/**
 * Provides a mechanism for copying existing process from one space to another.
 * 
 * @author Carlos Montemuiño
 * @since 8.2.0
 */
public class ProcessCopier {

	/**
	 * The space where the copy will be made from.
	 * 
	 * @since 8.2.0
	 */
	private final String fromSpaceID;

	/**
	 * The space where the copy will be made to.
	 * 
	 * @since 8.2.0
	 */
	private final String toSpaceID;

	/**
	 * Creates a new Process Copier to copy the processe that exists for one
	 * space to a new space.
	 * 
	 * @param fromSpaceID
	 *            the space from which to copy the process
	 * @param toSpaceID
	 *            the space to which to copy the process
	 */
	public ProcessCopier(String fromSpaceID, String toSpaceID) {
		this.fromSpaceID = fromSpaceID;
		this.toSpaceID = toSpaceID;
	}

	/**
	 * Copies all the process and all phases from one space to another.
	 * 
	 * @return a <code>HashMap</code> with a map between old phases and new
	 *         phases.
	 * @throws PersistenceException
	 *             if there is a problem loading or storing when calling to
	 *             Process#copyToSpace.
	 * @since 8.2.0
	 */
	public HashMap copyAll() throws PersistenceException {
		/* First, load the actual process we want to copy from. */
		Process fromProcess = new Process();
		fromProcess.setSpaceID(this.fromSpaceID);
		/* Now, copy the process. 
		 * copyToSpace copies all active phase, gate and deliverables */
		return fromProcess.copyToSpace(this.toSpaceID);
	}
}