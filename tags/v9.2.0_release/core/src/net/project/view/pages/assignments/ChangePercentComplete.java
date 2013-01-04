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
package net.project.view.pages.assignments;

import javax.servlet.http.HttpServletRequest;

import net.project.util.NumberFormat;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

/**
 *
 */
public class ChangePercentComplete {

	private Integer moduleId;

	private String objectId;

	private String selectedTask;

	private String percentComplete;

	private String workRemained;
	private String workRemainedToDays;

	@Inject
	private RequestGlobals requestGlobals;

	public void onActivate() {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		try {
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();

		if (StringUtils.isNotEmpty(request.getParameter("moduleId"))) {
			moduleId = Integer.parseInt(request.getParameter("moduleId"));
		}
		if (StringUtils.isNotEmpty(request.getParameter("objectId"))) {
			objectId = request.getParameter("objectId");
		}
		if (StringUtils.isNotEmpty(request.getParameter("selectedTask"))) {
			selectedTask = request.getParameter("selectedTask");
		}
		if (StringUtils.isNotEmpty(request.getParameter("percentComplete"))) {
			percentComplete = request.getParameter("percentComplete");
		}
		if (StringUtils.isNotEmpty(request.getParameter("workRemained"))) {
				workRemained = request.getParameter("workRemained").replace("hrs", "").trim();
			}
			if (StringUtils.isNotEmpty(workRemained)) {
				Double hours = Double.parseDouble(workRemained);
				if (hours > 80.0) {
					workRemainedToDays = NumberFormat.getInstance().formatNumber((hours / 8.0), "##.#") + " days";
				} else {
					workRemainedToDays = hours + " hrs";
				}
			}
		} catch (Exception e) {
			Logger.getLogger(ChangePercentComplete.class).error("Error occured while setting values from request: "+e.getMessage());
		}
	}

	/**
	 * @return the percentComplete
	 */
	public String getPercentComplete() {
		return percentComplete;
	}

	/**
	 * @return the moduleId
	 */
	public Integer getModuleId() {
		return moduleId;
	}

	/**
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @return the selectedTask
	 */
	public String getSelectedTask() {
		return selectedTask;
	}

	/**
	 * @return the workRemained
	 */
	public String getWorkRemained() {
		return workRemained;
	}

	/**
	 * @param percentComplete
	 *            the percentComplete to set
	 */
	public void setPercentComplete(String percentComplete) {
		this.percentComplete = percentComplete;
	}

	/**
	 * @return the workRemainedToDays
	 */
	public String getWorkRemainedToDays() {
		return workRemainedToDays;
	}

}
