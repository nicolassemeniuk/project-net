package net.project.schedule.exporter.mvc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.business.report.projectstatus.ProjectWorkplanData;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceFinder;
import net.project.schedule.Schedule;
import net.project.schedule.exporter.IScheduleExporter;
import net.project.util.HTMLUtils;

public class ExportAllScheduleHandler extends HttpServlet {

	private static final long serialVersionUID = -7624185884414061346L;

	private final String PATH = System.getProperty("java.io.tmpdir")
			+ System.getProperty("file.separator");

	private final long STAMP = System.currentTimeMillis();

	private final String FILE_NAME = HTMLUtils
			.escapeForValidFileName("MyBusinessProjectsFiles" + STAMP + ".zip");

	private final String XML_EXTENSION = ".xml";

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String selected = request.getParameter("selected");

		List<String> projectNameFiles = new ArrayList<String>();

		List fileNames = new ArrayList();
		if (selected != null) {
			IScheduleExporter exporter = new ScheduleExporter();
			String businessSelectedId = request.getParameter("selected");
			ProjectSpaceFinder projectSpaceFinder = new ProjectSpaceFinder();
			if (businessSelectedId != null) {
				List projects;
				try {
					projects = projectSpaceFinder
							.findByBusinessID(businessSelectedId);
					
					List projectNotDeleted = new ArrayList();
					Iterator projectsIterator = projects.iterator();
					while(projectsIterator.hasNext()) {
						ProjectWorkplanData project = (ProjectWorkplanData) projectsIterator.next();

						if(!project.projectSpace.getRecordStatus().equals("D")) {
							projectNotDeleted.add(project);
						}
					}

					// Iterate all over the projects
					Iterator projectIterator = projectNotDeleted.iterator();
					FileOutputStream fout = null;
					while (projectIterator.hasNext()) {
						ProjectWorkplanData projectWorkplanData = (ProjectWorkplanData) projectIterator
								.next();
						Schedule schedule = projectWorkplanData.schedule;
						exporter.setSchedule(schedule);
						ByteArrayOutputStream exportedXml = exporter.getXml();

						// Creates the project file
						fout = new FileOutputStream(PATH
								+ exporter.getProjectName() + XML_EXTENSION);
						projectNameFiles.add(exporter.getProjectName()
								+ XML_EXTENSION);

						exportedXml.writeTo(fout);
						fileNames.add(exporter.getProjectName());

					}

					if (fileNames != null && fileNames.size() > 0) {

						this.createZipFile(fileNames);
						response.setContentType("application/zip");
						response.setHeader("Content-Disposition",
								"attachment;filename=" + FILE_NAME);

						ServletOutputStream refactorOutputStream = response
								.getOutputStream();
						int readBytes = 0;
						BufferedInputStream bout = new BufferedInputStream(
								new FileInputStream(PATH + FILE_NAME));
						while ((readBytes = bout.read()) != -1) {
							refactorOutputStream.write(readBytes);
						}
					}

				} catch (PersistenceException e) {
					handleException(request, response, e);
				} catch (Exception e) {
					handleException(request, response, e);
				}
			}
		}

	}
	
	 /**
     * Returns the default error resource to forward to when an exception occurs.
     * <p>
     * Sub-classes may override this to force errors to be handled by a different
     * resource.
     * </p>
     * @return the default error resource, currently <code>/errors.jsp</code>
     */
    protected String getErrorResource() {
        return "/errors.jsp";
    }
	
	 /**
     * Handles an exception in the same way as a JSP page.
     * <p>
     * Forwards to the resource specified by {@link #getErrorResource}.
     * Sub-classes should call this from within <code>doGet</code>, <code>doPost</code>,
     * or <code>service</code> to handle exceptions.
     * </p>
     * @param request the request
     * @param response the response
     * @param e the exception that occurred
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws java.io.IOException, javax.servlet.ServletException {

        request.setAttribute(javax.servlet.jsp.PageContext.EXCEPTION, e);
        request.getRequestDispatcher(getErrorResource()).forward(request, response);

    }

	/**
	 * Method that will create the ZIP file with the corresponding XML Files
	 * 
	 * @param filenames
	 * @return ZipOutputStream
	 */
	public ZipOutputStream createZipFile(List filenames) throws Exception {
		// These are the files to include in the ZIP file

		// Create a buffer for reading the files
		byte[] buf = new byte[1024];

		try {
			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(PATH
					+ FILE_NAME));

			// Compress the files
			for (int i = 0; i < filenames.size(); i++) {

				FileInputStream in = new FileInputStream(PATH
						+ (String) filenames.get(i) + XML_EXTENSION);

				// Add ZIP entry to output stream
				out.putNextEntry(new ZipEntry((String) filenames.get(i)
						+ XML_EXTENSION));

				// Transfer bytes from the file to the ZIP file
				int lenght;
				while ((lenght = in.read(buf)) > 0) {
					out.write(buf, 0, lenght);
				}

				// Complete the entry
				out.closeEntry();
				in.close();
			}
 
			// Complete the ZIP file
			out.close();
			return out;

		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
}