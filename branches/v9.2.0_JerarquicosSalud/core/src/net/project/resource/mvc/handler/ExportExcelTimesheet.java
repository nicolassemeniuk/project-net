package net.project.resource.mvc.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.form.assignment.FormAssignment;
import net.project.resource.ActivityAssignment;
import net.project.resource.Assignment;
import net.project.resource.ScheduleEntryAssignment;
import net.project.util.DateFormat;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


public class ExportExcelTimesheet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		OutputStream out = null;
		NumberFormat numberFormat = NumberFormat.getInstance();
		try {
			out = response.getOutputStream();
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Timesheet");
			DateFormat dateFormat = DateFormat.getInstance();
			PnCalendar cal = new PnCalendar();

			// Create a row and put some cells in it. Rows are 0 based.
			HSSFRow rowHeader = sheet.createRow((short) 0);
			rowHeader.setHeightInPoints(45);
			HSSFCell cellHeader = rowHeader.createCell((short) 5);
			cellHeader.setCellValue(new HSSFRichTextString(PropertyProvider.get("prm.resource.timesheet.exporttoexcel.header.caption")));
			
			HSSFRow monthHeader = sheet.createRow((short) 1);
			String weekEndDateString = "";
			HSSFRow row = sheet.createRow((short) 2);
			Date startDate = new Date(Long.parseLong(request.getParameter("startDate")));
			cal.setTime(cal.startOfWeek(startDate));
			// Set week headers
			for (int weekHeader = 2; weekHeader <= 8; weekHeader++) {
				row.createCell((short) weekHeader).setCellValue(
						(new HSSFRichTextString(dateFormat.formatDate(cal.getTime(), "EEE") + " "
								+ dateFormat.formatDate(cal.getTime(), "d"))));
				weekEndDateString = dateFormat.formatDate(cal.getTime(), "d");
				cal.add(Calendar.DATE, 1);
			}
			String currentMonthName = dateFormat.formatDate(startDate, "MMM"); 
			String newMonthName = dateFormat.formatDate(cal.endOfWeek(startDate), "MMM");
			String monthName = !currentMonthName.equals(newMonthName) ? newMonthName : "";
			String headerString = currentMonthName+" "+ dateFormat.formatDate(startDate, "d") +" - "+ monthName +" "+ weekEndDateString;
			monthHeader.createCell((short)5).setCellValue(new HSSFRichTextString(headerString));
			
			row.createCell((short) 9).setCellValue(
					new HSSFRichTextString(PropertyProvider.get("prm.resource.timesheet.weeklytotal.label")));
			row.createCell((short) 10).setCellValue(
					new HSSFRichTextString(PropertyProvider.get("prm.resource.timesheet.projecttotal.label")));

			// Set project name and object name
			List assignments = (List) session.getAttribute("excelAssignments");
			Map dateValues = (Map) session.getAttribute("dateValues");
			Map grandTotalValues = (Map) session.getAttribute("grandTotalValues");
			if (CollectionUtils.isNotEmpty(assignments)) {
				String[] objectNames = getObjectName(assignments);
				String[] spaceNames = getSpaceName(assignments);
				List<Double> workValues = getWorkForAssignmentByDate(assignments, dateValues, startDate);
				int innerRowIndex = 2;
				int innerCellIndex = 2;
				int workIndex = 0;
				String rowNum = "";
				Double projectTotal[] = new Double[7];
				Double grandTotal[] = new Double[7];
				for (int rowIndex = 0; rowIndex < assignments.size(); rowIndex++) {
					Assignment assignment = (Assignment) assignments.get(rowIndex);
					String spaceID = assignment.getSpaceID();
					// Create project name cells
					if (StringUtils.isNotEmpty(spaceNames[rowIndex])) {
						projectTotal = new Double[7];
						HSSFRow projectRow = sheet.createRow((short) (innerRowIndex++));
						projectRow.createCell((short) 0).setCellValue(new HSSFRichTextString(spaceNames[rowIndex]));
					}
					// Create assignment name row and it's cells
					HSSFRow objectRow = sheet.createRow((short) (innerRowIndex++));
					objectRow.createCell((short) 1).setCellValue(new HSSFRichTextString(objectNames[rowIndex]));

					Double weeklyTotal = 0.0;

					// Create cells for work captured values
					for (int index = 0; index < 7; index++) {
						innerCellIndex = (index == 6) ? innerCellIndex = 1 : innerCellIndex;
						if (workValues.get(workIndex) != 0.0) {
							objectRow.createCell((short) (innerCellIndex == 1 ? innerCellIndex + (index+1) : innerCellIndex))
									.setCellValue(Double.valueOf(numberFormat.formatNumber(workValues.get(workIndex), 0, 2)));
						} else {
							objectRow.createCell((short) (innerCellIndex == 1 ? innerCellIndex + (index+1) : innerCellIndex))
									.setCellValue(new HSSFRichTextString(""));
						}
						weeklyTotal += workValues.get(workIndex);
						projectTotal[index] = projectTotal[index] != null ? projectTotal[index]
								+ workValues.get(workIndex) : 0.0 + workValues.get(workIndex);
								
						workIndex++;
						innerCellIndex++;
					}
					// Create cell for weekly total of each assignment
					objectRow.createCell((short) (9)).setCellValue(Double.valueOf(numberFormat.formatNumber(weeklyTotal, 0, 2)));

					// Create cell for Assignment total
					if (assignment instanceof ScheduleEntryAssignment) {
						ScheduleEntryAssignment seAssignment = (ScheduleEntryAssignment) assignment;
						objectRow.createCell((short) 10).setCellValue(Double.valueOf(
								numberFormat.formatNumber(seAssignment.getWorkComplete().getAmount().doubleValue(), 0, 2)));
					} else if (assignment instanceof FormAssignment) {
						FormAssignment fAssignment = (FormAssignment) assignment;
						objectRow.createCell((short) 10).setCellValue(Double.valueOf(
								numberFormat.formatNumber(fAssignment.getWorkComplete().getAmount().doubleValue(), 0, 2)));
					} else if (assignment instanceof ActivityAssignment) {
						ActivityAssignment aAssignment = (ActivityAssignment) assignment;
						objectRow.createCell((short) 10).setCellValue(Double.valueOf(
								numberFormat.formatNumber(aAssignment.getWork().getAmount().doubleValue(), 0, 2)));
					}
					rowNum = "" + objectRow.getRowNum();

					// Create row for displaying total for project
					if (rowIndex < (assignments.size() - 1)) {
						if (!((Assignment) assignments.get(rowIndex + 1)).getSpaceID().equals(spaceID)) {
							setTotalForProject(innerRowIndex++, assignment.getSpaceName(), projectTotal, sheet, numberFormat);
						}
					} else {
						setTotalForProject(innerRowIndex++, assignment.getSpaceName(), projectTotal, sheet, numberFormat);
					}
				}

				HSSFRow sumRow = sheet.createRow((short) (innerRowIndex + 1));
				HSSFCell sumCell = null;
				sumCell = sumRow.createCell((short) 0);
				rowNum = "" + (Integer.parseInt(rowNum) + 1);
				sumCell.setCellValue(new HSSFRichTextString(PropertyProvider
						.get("prm.resource.timesheet.grandtotal.label")));
				
				PnCalendar totalCal = new PnCalendar();
				totalCal.setTime(startDate);
				TimeQuantity summaryWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
				for (int sumIndex = 2; sumIndex < 9; sumIndex++) {
					sumCell = sumRow.createCell((short) sumIndex);
					if(grandTotalValues.get(totalCal.getTime()) != null) {
                    	summaryWork = (TimeQuantity)grandTotalValues.get(totalCal.getTime());
                    	sumCell.setCellValue(Double.valueOf(numberFormat.formatNumber(summaryWork.getAmount().doubleValue(), 0, 2)));
                    } else {
                    	sumCell.setCellValue(Double.valueOf("0"));
                    }
					totalCal.add(Calendar.DATE, 1);
				}
			} else {
				HSSFRow noAssignments = sheet.createRow((short) 3);
				noAssignments.createCell((short) 2).setCellValue(new HSSFRichTextString(PropertyProvider.get("prm.resource.assignments.update.noassignmentsmatch")));
			}

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=timesheet.xls");

			wb.write(out);
			out.close();
			return;
		} catch (Exception e) {
			Logger.getLogger(ExportExcelTimesheet.class).error(
					"Exception in Excel Timesheet Servlet.." + e.getMessage());
		} finally {
			if (out != null)
				out.close();
		}
	}

	/**
	 * Method for getting assignment names
	 * 
	 * @param assignments
	 *            is the actual list of assignments and project names
	 * @return assignment names
	 */
	private String[] getObjectName(List assignments) {
		String objectName[] = new String[assignments.size()];
		for (int objectIndex = 0; objectIndex < assignments.size(); objectIndex++) {
			Assignment assignment = (Assignment) assignments.get(objectIndex);
			objectName[objectIndex] = assignment.getObjectName();
		}
		return objectName;
	}

	/**
	 * Method for getting project name
	 * 
	 * @param assignments
	 *            is the actual list of assignments and project names
	 * @return projectnames
	 */
	private String[] getSpaceName(List assignments) {
		String spaceNames[] = new String[assignments.size()];
		String spaceID = "0";
		for (int objectIndex = 0; objectIndex < assignments.size(); objectIndex++) {
			Assignment assignment = (Assignment) assignments.get(objectIndex);
			if (!spaceID.equals(assignment.getSpaceID())) {
				spaceNames[objectIndex] = assignment.getSpaceName();
				spaceID = assignment.getSpaceID();
			}
		}
		return spaceNames;
	}

	/**
	 * Method to get work by each assignment
	 * 
	 * @param assignments
	 *            is the actual list of assignments and project names
	 * @param dateValues
	 *            is the Map which contains aggregate work for the date, for assignment
	 * @param startDate
	 *            is the date for start of week
	 * @return List of works for each assignment in list
	 */
	private List<Double> getWorkForAssignmentByDate(List assignments, Map dateValues, Date startDate) {
		List<Double> workValues = new ArrayList<Double>();
		PnCalendar calendar = new PnCalendar();
		calendar.setTime(startDate);
		Date date = calendar.startOfWeek(startDate);
		for (int objectIndex = 0; objectIndex < assignments.size(); objectIndex++) {
			Assignment assignment = (Assignment) assignments.get(objectIndex);
			for (int dateIndex = 0; dateIndex < 7; dateIndex++) {
				Double work = (Double) dateValues.get(assignment.getObjectID()+"X"+date.getTime());
				workValues.add(work != null ? work : 0.0);
				calendar.add(Calendar.DATE, 1);
				date = calendar.getTime();
				if (dateIndex == 6) {
					date = startDate;
					calendar.setTime(date);
				}
			}
		}
		return workValues;
	}

	/**
	 * Method to add Project total row
	 * 
	 * @param innerRowIndex
	 *            Row number where to set
	 * @param spaceName
	 *            Project name to display
	 * @param projectTotal
	 *            array of project total values
	 * @param sheet
	 *            Worksheet where to reside the Project Total row
	 */
	private void setTotalForProject(int innerRowIndex, String spaceName, Double[] projectTotal, HSSFSheet sheet, NumberFormat numberFormat) {
		HSSFRow projectTotalRow = sheet.createRow((short) (innerRowIndex++));
		projectTotalRow.createCell((short) 0)
				.setCellValue(new HSSFRichTextString(PropertyProvider.get("prm.resource.timesheet.totalforproject.label", spaceName)));
		for (int projectTotalIndex = 2; projectTotalIndex < 9; projectTotalIndex++) {
			projectTotalRow.createCell((short) projectTotalIndex).setCellValue(Double.valueOf(numberFormat.formatNumber(projectTotal[projectTotalIndex - 2], 0, 2)));
		}
	}
}
