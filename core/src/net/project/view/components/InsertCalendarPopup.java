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
package net.project.view.components;

import net.project.gui.calendar.CalendarPopup;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;

/**
 *
 */
public class InsertCalendarPopup {

	@Parameter(required = true, defaultPrefix = "literal")
	private String fieldName;

	@Parameter(required = false, defaultPrefix = "literal")
	private String formName;
	
	private String htmlString;
	
	/**
	 * Initializing calender popup HTML string. 
	 */
	@SetupRender
	void constructHtml() {
		// Use the CalendarPopup to generate the presentation
		this.htmlString = CalendarPopup.getCalendarPopupHTML(fieldName, formName).toString();
	}
	/**
	 * @return the htmlString
	 */
	public String getHtmlString() {
		return this.htmlString;
	}

}
