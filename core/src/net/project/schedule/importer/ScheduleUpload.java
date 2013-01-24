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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.PnetRuntimeException;
import net.project.base.property.PropertyProvider;
import net.project.schedule.Schedule;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Receive uploaded MS Project xml files
 */
public class ScheduleUpload extends SimpleFormController {
	private Logger logger = Logger.getLogger(ScheduleUpload.class);

	private String temporaryFileLocation = null;

	private MultipartFile scheduleFile = null;

	private String action;

	private String module;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * Verify that the security parameters sent to this servlet are the same
	 * ones that we expect to be used. If we didn't check this, it would be
	 * possible to request this response with any permissions that we already
	 * have.
	 */
	private void verifySecurity() {
		AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY, null);
	}

	/**
	 * Returns the path to which XML files should be uploaded.
	 * 
	 * @return the value of the <code>xmlImportTempDirectory</code> setting
	 */
	private String getUploadDestinationPath() {
		return SessionManager.getXMLImportTempDirectory();
	}

	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
			BindException bindException) throws Exception {

		scheduleFile = ((ScheduleUploadCommand) command).getScheduleFile();
		try {
			// Make sure the proper security permissions were checked when
			// attempting
			// to access this controller
			verifySecurity();
			verifyFileExists();

			/*
			 * Transfer the file to a location where it can be read -- this is
			 * expecially important on linux machines where the OS can't read
			 * the file and needs to transfer it to a place where it can be
			 * read.
			 */
			File newFileLocation = File.createTempFile("imp", ".xml", new File(getUploadDestinationPath()));
			scheduleFile.transferTo(newFileLocation);
			temporaryFileLocation = newFileLocation.getPath();
			logger.debug("Temporary upload file written to " + temporaryFileLocation);
			// remember the name of tmp file to delete it after import
			request.getSession().setAttribute("temporaryFileLocation", temporaryFileLocation);
			
			// Initialize an importer from the uploaded information
			IScheduleImporter importer = initializeImporter();
			request.getSession().setAttribute("scheduleImporter", importer);

			Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");
			importer.setSchedule(schedule);

			// Decide whether or not the default is to import the start and end
			// date of the schedule
			boolean importStartAndEnd = schedule.getScheduleStartDate() == null || schedule.getTaskList().size() == 0;

			Map model = new HashMap();
			model.put("module", String.valueOf(Module.SCHEDULE));
			model.put("action", String.valueOf(Action.MODIFY));
			model.put("importStartAndEnd", Boolean.valueOf(importStartAndEnd));

			return new ModelAndView("/tiles_schedule_upload", model);

		} catch (AuthorizationFailedException afe) {
			// Create Beans
			SecurityProvider securityProvider = (SecurityProvider) request.getSession()
					.getAttribute("securityProvider");

			logger.debug("Authorization failed in ScheduleUpload.  " + securityProvider.getExplanationString());
			throw afe;

		} catch (Exception e) {
			ErrorReporter reporter = new ErrorReporter();
			reporter.setOverallError(PropertyProvider.get("prm.schedule.import.unabletoimportselectedfile.message"));
			reporter.addError(new ErrorDescription(e.getMessage()));

			Map model = new HashMap();
			model.put("module", String.valueOf(Module.SCHEDULE));
			model.put("action", String.valueOf(Action.MODIFY));
			model.put("errorReporter", reporter);

			logger.debug("Unexpected error in handleParameters", e);
			return new ModelAndView("/tiles_import", model);
		}
	}

	private void verifyFileExists() {
		if (scheduleFile == null || scheduleFile.isEmpty()) {
			logger.debug("Missing file parameters in ScheduleUpload");
			throw new PnetRuntimeException("Missing File parameter in ScheduleUpload");
		}
	}

	/*
	 * initlaises the jaxb context and unmarshals the xml document to java
	 * objects hirarchy
	 */
	private IScheduleImporter initializeImporter() throws PnetException {

		IScheduleImporter importer = new XMLImporter();
		importer.setFileName(temporaryFileLocation);
		importer.init();

		return importer;
	}
}
