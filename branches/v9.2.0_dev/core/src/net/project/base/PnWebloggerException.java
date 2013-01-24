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
package net.project.base;

import org.apache.roller.RollerException;

/**
 * @author
 *
 */
public class PnWebloggerException extends RollerException {

	public PnWebloggerException() {
		super();
	}

	/**
	 * Construct WebloggerException with message string.
	 *
	 * @param s Error message string.
	 */
	public PnWebloggerException(String s) {
		super(s);
	}

	/**
	 * Construct WebloggerException, wrapping existing throwable.
	 *
	 * @param s Error message
	 * @param t Existing connection to wrap.
	 */
	public PnWebloggerException(String s, Throwable t) {
		super(s, t);
	}

	/**
	 * Construct WebloggerException, wrapping existing throwable.
	 *
	 * @param t Existing exception to be wrapped.
	 */
	public PnWebloggerException(Throwable t) {
		super(t);
	}

}
