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
 |    $Revision: 15807 $
 |        $Date: 2007-04-09 03:03:58 +0530 (Mon, 09 Apr 2007) $
 |      $Author: avinash $
 |                                                                       
 +----------------------------------------------------------------------*/
package net.project.schedule.importer;

import net.project.base.PnetException;

/**
 * Provides an exception that indicates some problem occurred during MPD Import.
 * 
 * @author Tim Morrow
 * @since Version 7.6.3
 */
class ImportException extends PnetException {

	/**
	 * Indicates an error occurred during an import.
	 * 
	 * @param message
	 *            the message
	 */
	public ImportException(String message) {
		super(message);
	}

	/**
	 * Indicates an error occurred during an import caused by another Throwable.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the causing exception
	 */
	public ImportException(String message, Throwable cause) {
		super(message, cause);
	}

}
