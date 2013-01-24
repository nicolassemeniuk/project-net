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

 package net.project.datatransform.csv;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.util.FileUtils;

import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Provides handling for Csv File Uploads.  Parses the uploaded file and adds a
 * CSV object to the session.
 *
 * @author Matthew Flower
 * @since 22 December 2005
 */
public class CSVUpload extends SimpleFormController {
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException bindException) throws Exception {
        CSVUploadCommand cuCommand = (CSVUploadCommand)command;

        //First verify that the user used appropriate security to get to this page
        verifySecurity();

        try {
            HttpSession session = request.getSession();

            // Create Beans
            CSVWizard csvWizard = (CSVWizard)session.getAttribute("csvWizard");
            if (csvWizard == null) {
                csvWizard = new CSVWizard();
                session.setAttribute("csvWizard", csvWizard);
            }
            
            if(cuCommand.getFile() == null || cuCommand.getFile().isEmpty()) {
                csvWizard.invalidateFormat();
                Map model = new HashMap();
                return new ModelAndView("/tiles_csv_stepone", model);
            }
            
            CSV csv = new CSV();
            session.removeAttribute("csv");
            session.setAttribute("csv", csv);

            try {
                // Now parse the Csv file
                createCsv(cuCommand.getFile(), csv, request);

                // Now redirect to the next page
                response.sendRedirect(SessionManager.getJSPRootURL() + "/datatransform/csv/ColumnMap.jsp");

            } catch (InvalidFormatException e) {
                // Could not parse the file
                // Return to upload page
                csvWizard.invalidateFormat();
                response.sendRedirect(SessionManager.getJSPRootURL() + "/datatransform/csv/FileUpload.jsp");

            }

        } catch (Exception e) {
            // Save the exception for the errors page to get
            request.setAttribute(javax.servlet.jsp.PageContext.EXCEPTION, e);
            return new ModelAndView("/tiles_error");

        }

        Map model = new HashMap();
        return new ModelAndView("/tiles_csv_upload", model);
    }

    private void verifySecurity() {
        AccessVerifier.verifyAccess(Module.FORM, Action.VIEW);
    }

    /**
     * Creates the Csv object from the uploaded file.
     * @param csv the CSV object to use to parse the file content
     * @throws net.project.base.PnetException if the File parameter is not found
     * @throws net.project.datatransform.csv.InvalidFormatException
     * if the CSV file cannot be parsed
     */
    private void createCsv(MultipartFile file, CSV csv, HttpServletRequest request)
        throws PnetException, InvalidFormatException {

        // Check for the presence of the File parameter
        if (file == null || file.isEmpty()) {
            throw new PnetException("Missing File parameter in CSVUploadServlet");
        }

        // Parse the file
        String tempFileLocation = FileUtils.commitUploadedFileToFileSystem(file);
        java.io.File tempFile = new java.io.File(tempFileLocation);
   //Avinash:----------selected encoding otherwise default from request---------
        //csv.setCharacterEncoding(request.getCharacterEncoding());
        if(request.getParameter("Encoding")!=null)
        	csv.setCharacterEncoding(request.getParameter("Encoding"));
        else
        	csv.setCharacterEncoding(request.getCharacterEncoding());
   //Avinash:----------getting selected encoding otherwise default from request---
        
        csv.parse(tempFile);
        if (csv.getCSVColumns().size() == 0) {
            throw new InvalidFormatException("Invalid File uploaded");
        }
    }
}
