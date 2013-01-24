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

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.schedule.Schedule;
import net.project.schedule.exporter.IScheduleExporter;
import net.project.security.AuthorizationFailedException;

/**
 * @author avibha
 *
 */
public class FinishScheduleHandler extends Handler {
	String spaceId;
	public FinishScheduleHandler(HttpServletRequest request) {
		super(request);
		spaceId = (String)request.getSession().getAttribute("spaceId");
	}
	
	/**
	 * 
	 */
	protected String getViewName() {
		return "/schedule/exporter/ScheduleXmlSave.jsp";
	}
	
	/**
	 * 
	 */
	public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException {
	}
	
	/**
	 * 
	 */
	public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map model = Collections.EMPTY_MAP;
		IScheduleExporter exporter = new ScheduleExporter();
		Schedule schedule = (Schedule)request.getSession().getAttribute("schedule");
		exporter.setSchedule(schedule);
		ByteArrayOutputStream exportedXml = exporter.getXml();
		request.setAttribute("exportedXml", exportedXml);
		request.setAttribute("xmlFile", exporter.getProjectName() + ".xml");
		return model;	
	}
}
