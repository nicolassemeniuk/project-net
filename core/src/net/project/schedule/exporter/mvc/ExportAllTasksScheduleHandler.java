package net.project.schedule.exporter.mvc;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceFinder;
import net.project.business.report.projectstatus.ProjectWorkplanData;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.project.ProjectSpaceFinder;
import net.project.schedule.Schedule;
import net.project.schedule.exporter.IScheduleExporter;
import net.project.soa.schedule.Project.Tasks;
import net.project.soa.schedule.Project.Tasks.Task;
import net.project.util.HTMLUtils;
import net.project.util.ParseString;

import org.apache.commons.lang.StringUtils;

public class ExportAllTasksScheduleHandler extends HttpServlet {

	private static final long serialVersionUID = -7624185884414061346L;

	private final String CSV_TASKS = "BusinessTasksCSV";

	private final String PATH = System.getProperty("java.io.tmpdir")
			+ System.getProperty("file.separator");

	private final long STAMP = System.currentTimeMillis();

	private final String FILE_NAME = HTMLUtils
			.escapeForValidFileName("MyBusinessProjectsFiles" + this.STAMP
					+ ".zip");

	private final String CSV_EXTENSION = ".csv";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String selected = request.getParameter("selected");

		List<String> projectNameFiles = new ArrayList<String>();

		List fileNames = new ArrayList();
		if (selected != null) {
			IScheduleExporter exporter = new ScheduleExporter();
			String businessSelectedId = request.getParameter("selected");
			ProjectSpaceFinder projectSpaceFinder = new ProjectSpaceFinder();
			String businessName = StringUtils.EMPTY;
			if (businessSelectedId != null) {
				try {

					BusinessSpaceFinder businessSpaceFinder = new BusinessSpaceFinder();
					List business = businessSpaceFinder
							.findByID(businessSelectedId);
					if (business != null && business.size() == 1) {
						businessName = ((BusinessSpace) business.get(0))
								.getName();
					}

					List projects = projectSpaceFinder
							.findByBusinessID(businessSelectedId);

					List projectNotDeleted = new ArrayList();
					Iterator projectsIterator = projects.iterator();
					while (projectsIterator.hasNext()) {
						ProjectWorkplanData project = (ProjectWorkplanData) projectsIterator
								.next();

						if (!project.projectSpace.getRecordStatus().equals("D")) {
							projectNotDeleted.add(project);
						}
					}

					// Iterate all over the projects
					Iterator projectIterator = projectNotDeleted.iterator();
					FileOutputStream fout = null;

					// Initiate the string buffer
					StringBuffer csvOutput = new StringBuffer();
					String[] fieldLabelTokens = this.getSummaryTaskTokens();
					csvOutput.append("\""
							+ PropertyProvider.get(fieldLabelTokens[0]) + "\"");
					for (int index = 1; index < fieldLabelTokens.length; index++) {
						csvOutput.append(",\""
								+ PropertyProvider.get(fieldLabelTokens[index])
								+ "\"");
					}
					csvOutput.append("\r\n");

					while (projectIterator.hasNext()) {
						ProjectWorkplanData projectWorkplanData = (ProjectWorkplanData) projectIterator
								.next();
						Schedule schedule = projectWorkplanData.schedule;
						ProjectSpace projectSpace = projectWorkplanData.projectSpace;
						exporter.setSchedule(schedule);

						// I will iterate from the collection list of tasks
						Tasks tasksFromProject = exporter.getCsv();
						for (Task task : tasksFromProject.getTask()) {
							// Set the CSV column data by comma separated list
							csvOutput
									.append("\""
											+ this
													.formatFieldDataCSV(businessSelectedId
															.toString()) + "\"");
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(businessName
											.toString()) + "\"");
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(projectSpace
											.getID().toString()) + "\"");
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(projectSpace
											.getName().toString()) + "\"");

							String description = StringUtils.EMPTY;
							if (projectSpace.getDescription() != null) {
								description = projectSpace.getDescription();
							}
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(description)
									+ "\"");

							csvOutput.append(",\""
									+ this.formatFieldDataCSV(projectSpace
											.getMetaData().getProperty(
													"ProjectManager")) + "\"");
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(projectSpace
											.getMetaData().getProperty(
													"ProgramManager")) + "\"");
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(projectSpace
											.getMetaData().getProperty(
													"ExternalProjectID"))
									+ "\"");

							String sponsor = StringUtils.EMPTY;
							if (projectSpace.getSponsor() != null) {
								sponsor = projectSpace.getSponsor();
							}
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(sponsor) + "\"");

							csvOutput.append(",\""
									+ this.formatFieldDataCSV(projectSpace
											.getMetaData().getProperty(
													"Initiative")) + "\"");
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(projectSpace
											.getMetaData().getProperty(
													"FunctionalArea")) + "\"");

							String priorityCode = "";
							if (projectSpace.getPriorityCode() != null) {
								priorityCode = projectSpace.getPriorityCode()
										.getName();
							}
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(priorityCode)
									+ "\"");

							csvOutput.append(",\""
									+ this.formatFieldDataCSV(projectSpace
											.getMetaData().getProperty(
													"ProjectCharter")) + "\"");
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(projectSpace
											.getMetaData().getProperty(
													"TypeOfExpense")) + "\"");
							
							String costCenter = "";
							if (projectSpace.getCostCenter() != null) {
								costCenter = projectSpace.getCostCenter();
							}
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(costCenter)
									+ "\"");
							
							String status = "";
							if (projectSpace.getStatus() != null) {
								status = projectSpace.getStatusName();
							}
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(status)
									+ "\"");
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(task.getID()
											.toString()) + "\"");
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(task.getName())
									+ "\"");
							String isCritical = "Y";
							if (task.isCritical() != null && !task.isCritical()) {
								isCritical = "N";
							}
							csvOutput.append(",\""
									+ this.formatFieldDataCSV(isCritical)
									+ "\"");

							if (task.getCreateDate() != null) {
								csvOutput
										.append(",\""
												+ this
														.formatFieldDataCSV(new SimpleDateFormat(
																"MMM,dd yyyy")
																.format(task
																		.getCreateDate()
																		.toGregorianCalendar()
																		.getTime()))
												+ "\"");
							} else {
								csvOutput.append(",\" \"");
							}
							if (task.getStart() != null) {
								csvOutput
										.append(",\""
												+ this
														.formatFieldDataCSV(new SimpleDateFormat(
																"MMM,dd yyyy")
																.format(task
																		.getStart()
																		.toGregorianCalendar()
																		.getTime()))
												+ "\"");
							} else {
								csvOutput.append(",\" \"");
							}

							if (task.getFinish() != null) {
								csvOutput
										.append(",\""
												+ this
														.formatFieldDataCSV(new SimpleDateFormat(
																"MMM,dd yyyy")
																.format(task
																		.getFinish()
																		.toGregorianCalendar()
																		.getTime()))
												+ "\"");
							} else {
								csvOutput.append(",\" \"");
							}
							try {
								if (task.getDuration() != null) {
									csvOutput
											.append(",\""
													+ this
															.formatFieldDataCSV(""
																	+ task
																			.getDuration()
																			.getHours())
													+ "\"");
								} else {
									csvOutput.append(",\" \"");
								}
							} catch (IllegalArgumentException ia) {
								csvOutput.append(",\" \"");
							}

							csvOutput.append(",\""
									+ this.formatFieldDataCSV(task
											.getPercentComplete().toString())
									+ "\"");
							try {
								if (task.getWork() != null) {
									csvOutput
											.append(",\""
													+ this
															.formatFieldDataCSV(""
																	+ task
																			.getWork()
																			.getHours())
													+ "\"");
								} else {
									csvOutput.append(",\" \"");
								}
							} catch (IllegalArgumentException ia) {
								csvOutput.append(",\" \"");
							}
							if (task.getCost() != null) {
								csvOutput.append(",\""
										+ this.formatFieldDataCSV(task
												.getCost().toString()) + "\"");
							} else {
								csvOutput.append(",\" \"");
							}
							if (task.getActualStart() != null) {
								csvOutput
										.append(",\""
												+ this
														.formatFieldDataCSV(new SimpleDateFormat(
																"MMM,dd yyyy")
																.format(task
																		.getActualStart()
																		.toGregorianCalendar()
																		.getTime()))
												+ "\"");
							} else {
								csvOutput.append(",\" \"");
							}
							if (task.getActualFinish() != null) {
								csvOutput
										.append(",\""
												+ this
														.formatFieldDataCSV(new SimpleDateFormat(
																"MMM,dd yyyy")
																.format(task
																		.getActualFinish()
																		.toGregorianCalendar()
																		.getTime()))
												+ "\"");
							} else {
								csvOutput.append(",\" \"");
							}
							try {
								if (task.getActualDuration() != null) {
									csvOutput
											.append(",\""
													+ this
															.formatFieldDataCSV(""
																	+ task
																			.getActualDuration()
																			.getHours())
													+ "\"");
								} else {
									csvOutput.append(",\" \"");
								}
							} catch (IllegalArgumentException ia) {
								csvOutput.append(",\" \"");
							}
							if (task.getActualCost() != null) {
								csvOutput.append(",\""
										+ this.formatFieldDataCSV(task
												.getActualCost().toString())
										+ "\"");
							} else {
								csvOutput.append(",\" \"");
							}
							try {
								if (task.getRemainingWork() != null) {
									csvOutput.append(",\""
											+ this.formatFieldDataCSV(""
													+ task.getRemainingWork()
															.getHours())
											+ "hr\"");
								} else {
									csvOutput.append(",\" \"");
								}
							} catch (IllegalArgumentException ia) {
								csvOutput.append(",\" \"");
							}
							net.project.soa.schedule.Project.Tasks.Task.Baseline baseline = null;
							if (task.getBaseline() != null
									&& task.getBaseline().size() > 0) {
								baseline = task.getBaseline().get(0);
							}

							if (baseline != null && baseline.getStart() != null) {
								csvOutput
										.append(",\""
												+ this
														.formatFieldDataCSV(new SimpleDateFormat(
																"MMM,dd yyyy")
																.format(baseline
																		.getStart()
																		.toGregorianCalendar()
																		.getTime()))
												+ "\"");
							} else {
								csvOutput.append(",\" \"");
							}
							if (baseline != null
									&& baseline.getFinish() != null) {
								csvOutput
										.append(",\""
												+ this
														.formatFieldDataCSV(new SimpleDateFormat(
																"MMM,dd yyyy")
																.format(baseline
																		.getFinish()
																		.toGregorianCalendar()
																		.getTime()))
												+ "\"");
							} else {
								csvOutput.append(",\" \"");
							}

							csvOutput.append("\r\n");
						}
					}

					String fileCompleteName = CSV_TASKS + CSV_EXTENSION;

					// Creates the project file
					fout = new FileOutputStream(PATH + fileCompleteName);
					projectNameFiles.add(fileCompleteName);

					fout.write(csvOutput.toString().getBytes());
					fileNames.add(fileCompleteName);

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
	 * Returns the default error resource to forward to when an exception
	 * occurs.
	 * <p>
	 * Sub-classes may override this to force errors to be handled by a
	 * different resource.
	 * </p>
	 * 
	 * @return the default error resource, currently <code>/errors.jsp</code>
	 */
	protected String getErrorResource() {
		return "/errors.jsp";
	}

	/**
	 * Handles an exception in the same way as a JSP page.
	 * <p>
	 * Forwards to the resource specified by {@link #getErrorResource}.
	 * Sub-classes should call this from within <code>doGet</code>,
	 * <code>doPost</code>, or <code>service</code> to handle exceptions.
	 * </p>
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param e
	 *            the exception that occurred
	 * @throws java.io.IOException
	 * @throws javax.servlet.ServletException
	 */
	protected void handleException(HttpServletRequest request,
			HttpServletResponse response, Exception e)
			throws java.io.IOException, javax.servlet.ServletException {

		request.setAttribute(javax.servlet.jsp.PageContext.EXCEPTION, e);
		request.getRequestDispatcher(getErrorResource()).forward(request,
				response);

	}

	/**
	 * Formats the field data and returns it in a assignment suitable for using
	 * in a CSV file.
	 * 
	 * @param fieldData
	 *            the field data to be formatted and returned.
	 * @return a comma separated list representation of the field_data formatted
	 *         correctly for this type of field.
	 */
	private String formatFieldDataCSV(String assignmentData) {
		if (assignmentData != null) {
			return (ParseString.escapeDoubleQuotes(assignmentData));
		} else {
			return StringUtils.EMPTY;
		}
	}

	private String[] getSummaryTaskTokens() {

		String[] totalSummaryField = {
				"prm.schedule.task.export.business.id",
				"prm.schedule.task.export.business.name",
				"prm.schedule.task.export.project.id.maxim.custom",
				"prm.schedule.task.export.project.maxim.name",
				"prm.schedule.task.export.project.description.maxim.custom",
				"prm.schedule.task.export.project.maxim.part.number.maxim.custom",
				"prm.schedule.task.export.project.fab.process.maxim.custom",
				"prm.schedule.task.export.project.die.type.family.maxim.custom",
				"prm.schedule.task.export.project.product.line.maxim.custom",
				"prm.schedule.task.export.project.package.maxim.custom",
				"prm.schedule.task.export.project.project.type.maxim.custom",
				"prm.schedule.task.export.project.pass.maxim.custom",
				"prm.schedule.task.export.project.code.fab.maxim.custom",
				"prm.schedule.task.export.project.intro.quarter.maxim.custom",
				"prm.schedule.task.export.project.die.rev.maxim.custom",
				"prm.schedule.task.export.project.overall.status.maxim.custom",
				"prm.schedule.task.export.csv.taskid",
				"prm.schedule.task.export.csv.name",
				"prm.schedule.task.export.csv.critical",
				"prm.schedule.task.export.csv.datecreated",
				"prm.schedule.task.export.csv.datestart",
				"prm.schedule.task.export.csv.datefinish",
				"prm.schedule.task.export.csv.duration",
				"prm.schedule.task.export.csv.percentcomplete",
				"prm.schedule.task.export.csv.work",
				"prm.schedule.export.csv.currentestimatedtotalcost",
				"prm.schedule.export.csv.actualstartdate",
				"prm.schedule.export.csv.actualfinish",
				"prm.schedule.export.csv.duration",
				"prm.schedule.export.csv.actualcost",
				"prm.schedule.export.csv.remainingduration",
				"prm.schedule.export.csv.baseline.start.date",
				"prm.schedule.export.csv.baseline.end.date" };
		return totalSummaryField;

	}

	/**
	 * Method that will create the ZIP file with the corresponding XML Files
	 * 
	 * @param filenames
	 * @return ZipOutputStream
	 * @throws Exception
	 */
	public ZipOutputStream createZipFile(List filenames) throws Exception {
		// These are the files to include in the ZIP file

		// Create a buffer for reading the files
		byte[] buf = new byte[1024];

		try {
			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					this.PATH + this.FILE_NAME));

			// Compress the files
			for (int i = 0; i < filenames.size(); i++) {

				FileInputStream in = new FileInputStream(this.PATH
						+ (String) filenames.get(i));

				// Add ZIP entry to output stream
				out.putNextEntry(new ZipEntry((String) filenames.get(i)));

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