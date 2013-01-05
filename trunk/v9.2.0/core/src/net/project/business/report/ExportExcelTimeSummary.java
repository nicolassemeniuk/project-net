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
package net.project.business.report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.property.PropertyProvider;
import net.project.resource.mvc.handler.ExportExcelTimesheet;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Export Excel sheet for business time summary
 */
public class ExportExcelTimeSummary extends HttpServlet{

	// Export the time submital report for business space
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		OutputStream out = null;
		
		List<AssignmentList> detailTimeSubmittal = (List<AssignmentList>) request.getSession().getAttribute("detailTimeSubmittal");
		Double[] dailyTotal =  (Double[]) request.getSession().getAttribute("dailyTotalList");
		String personName = (String)  request.getSession().getAttribute("personName");
		Integer totalDays = (Integer)  request.getSession().getAttribute("totalDays");
		String DateString = (String)  request.getSession().getAttribute("DateString");
		List<TimeSubmitalWeek> weekList =  (List<TimeSubmitalWeek>) request.getSession().getAttribute("weekList");
		String grantTotal = (String)  request.getSession().getAttribute("grantTotal");
		try {
				out = response.getOutputStream();
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet sheet = wb.createSheet("Timesummary");
				List<TimeSubmitalWeek> workHourList = new ArrayList();
				HSSFRow rowHeader = sheet.createRow((short) 0);
				rowHeader.setHeightInPoints(25);
				rowHeader.createCell((short) 0).setCellValue(new HSSFRichTextString(personName));
				rowHeader.createCell((short) 1).setCellValue(new HSSFRichTextString(DateString));
				rowHeader.createCell((short) 5).setCellValue(new HSSFRichTextString(PropertyProvider.get(PropertyProvider.get("prm.business.report.businessworkcompleted.name"))));
				
				HSSFRow dayHeader = sheet.createRow((short) 1);
				dayHeader.setHeightInPoints(45);
				int dayIndex = 3;
				for(TimeSubmitalWeek week : weekList){
					if(week != null){
						dayHeader.createCell((short) dayIndex).setCellValue(new HSSFRichTextString(week.getDateString()));
					} else {
						dayHeader.createCell((short) dayIndex).setCellValue(new HSSFRichTextString(PropertyProvider.get("prm.resource.timesheet.weeklytotal.label")));
					}
					dayIndex++;
				}
				dayHeader.createCell((short) dayIndex).setCellValue(new HSSFRichTextString(PropertyProvider.get("prm.business.timesubmital.report.monthlytotalmessage")));
				int listIndex = 2;
				for(AssignmentList assignmentList : detailTimeSubmittal){
					
					if(StringUtils.isNotEmpty(assignmentList.getMemberName())){
						listIndex = listIndex + 1;
						HSSFRow memberHeader = sheet.createRow((short) listIndex);
						memberHeader.createCell((short) 0).setCellValue(new HSSFRichTextString(assignmentList.getMemberName()));
						listIndex ++;
					}
					if(StringUtils.isNotEmpty(assignmentList.getProjectName())){
						HSSFRow projectHeader = sheet.createRow((short) listIndex);
						projectHeader.createCell((short) 0).setCellValue(new HSSFRichTextString(assignmentList.getProjectName()));
						listIndex ++;
					}
					HSSFRow taskHeader = sheet.createRow((short) listIndex);
					int listCell = 1;
					if(StringUtils.isNotEmpty(assignmentList.getTaskName())){
						taskHeader.createCell((short) listCell++).setCellValue(new HSSFRichTextString(assignmentList.getTaskName()));
						listCell = listCell + 1;
					}	
					
					workHourList = assignmentList.getWorkHourList();
					if(workHourList != null){
						for(TimeSubmitalWeek workHour: workHourList){
							if(StringUtils.isNotEmpty(workHour.getDateString())) {
								taskHeader.createCell((short) listCell++).setCellValue(new HSSFRichTextString(workHour.getWork()));
							} else {
								taskHeader.createCell((short) listCell++).setCellValue(new HSSFRichTextString(workHour.getWeeklyTotal()));
							}
						}
						taskHeader.createCell((short) listCell).setCellValue(new HSSFRichTextString(assignmentList.getMonthlyTotal()));
						listIndex++;
					}
					
				}
				listIndex = listIndex + 2;
				HSSFRow dailyTotalHeader = sheet.createRow((short) listIndex);
				dailyTotalHeader.createCell((short) 0).setCellValue(new HSSFRichTextString(PropertyProvider.get("prm.business.timesubmital.report.dailytotalmessage")));
				int dailyHourIndex = 3;
				for(int i = 0; i < weekList.size(); i++){
					dailyTotalHeader.createCell((short) dailyHourIndex++).setCellValue(dailyTotal[i]);
				}
				dailyTotalHeader.createCell((short) dailyHourIndex++).setCellValue(new HSSFRichTextString(grantTotal));
				
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition", "attachment; filename=timesummary.xls");
				wb.write(out);
				out.close();
				
			} catch (Exception e) {
				Logger.getLogger(ExportExcelTimesheet.class).error(
				"Exception in Excel Timesummary Servlet.." + e.getMessage());
			} finally {
				if (out != null)
					out.close();
			}
	}
}
