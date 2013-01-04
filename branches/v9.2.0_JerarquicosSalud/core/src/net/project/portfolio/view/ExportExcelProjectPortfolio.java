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
package net.project.portfolio.view;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.property.PropertyProvider;
import net.project.code.ColorCode;
import net.project.project.ProjectPortfolioRow;
import net.project.resource.mvc.handler.ExportExcelTimesheet;
import net.project.util.ProjectNode;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * Export Excel sheet for project portfolio
 */
public class ExportExcelProjectPortfolio extends HttpServlet{
	
	// Export the project portfolio report
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		OutputStream out = null;
		
		List<ProjectNode> projectList = (List<ProjectNode>) request.getSession().getAttribute("projectList");
		List<MetaColumn> projectColumnList =  (List<MetaColumn>) request.getSession().getAttribute("projectColumnList");
		try {
				out = response.getOutputStream();
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFCellStyle cellStyleGreen = wb.createCellStyle();
				HSSFCellStyle cellStyleRed = wb.createCellStyle();
				HSSFCellStyle cellStyleYellow = wb.createCellStyle();
				
				cellStyleGreen.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);
				cellStyleGreen.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

				cellStyleRed.setFillForegroundColor(HSSFColor.RED.index);
				cellStyleRed.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

				cellStyleYellow.setFillForegroundColor(HSSFColor.YELLOW.index);
				cellStyleYellow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

				HSSFSheet sheet = wb.createSheet("Portfolio");
				HSSFRow rowHeader = sheet.createRow((short) 0);
				rowHeader.setHeightInPoints(25);

				int columncellindex = 0;
				boolean parentProjectColumnSelected = false;
				// check check parent project name column is selected or not  
				for(MetaColumn projectColumn: projectColumnList)
					if(projectColumn.getPropertyName().equals("SubprojectOf")){
						parentProjectColumnSelected = true;
						break;
					}

				for(MetaColumn projectColumn: projectColumnList){
					// if parent project name column is not selected 
					// then add new column to excell sheet to display name of parentproject  
					if(!parentProjectColumnSelected && projectColumn.getPropertyName().equalsIgnoreCase("name")){
						rowHeader.createCell((short) columncellindex).setCellValue(new HSSFRichTextString(projectColumn.getDescription()));
			        	columncellindex ++;
						rowHeader.createCell((short) columncellindex).setCellValue(new HSSFRichTextString(PropertyProvider.get("prm.project.create.wizard.subprojectof")));
			        	columncellindex ++;
					} else {
						rowHeader.createCell((short) columncellindex).setCellValue(new HSSFRichTextString(projectColumn.getDescription()));
			        	columncellindex ++;
					}
			    }
			     
				int listIndex = 1;
		        for(ProjectNode project: projectList){
		        	List<ProjectPortfolioRow> projectListRow = project.getSequensedProject();

					short columnIndex = 0;
		        	HSSFRow memberHeader = sheet.createRow((short) listIndex);

		        	if(!parentProjectColumnSelected){
						memberHeader.createCell((short) columnIndex).setCellValue(new HSSFRichTextString(project.getProject().getProjectName()));
						columnIndex ++;
						memberHeader.createCell(columnIndex).setCellValue(new HSSFRichTextString(project.getProject().getSubProjectOf()));
					} else {
						memberHeader.createCell((short) columnIndex).setCellValue(new HSSFRichTextString(project.getProject().getProjectName()));
					}

		        	columnIndex ++;

		        	for(ProjectPortfolioRow projectData: projectListRow){
		        		if(StringUtils.equalsIgnoreCase(projectData.getType(),"OverallStatus")
		        				|| StringUtils.equalsIgnoreCase(projectData.getType(),"FinancialStatus") 
		        				|| StringUtils.equalsIgnoreCase(projectData.getType(),"ScheduleStatus") 
		        				|| StringUtils.equalsIgnoreCase(projectData.getType(),"ResourceStatus")){

		        			if(projectData.getActualValue().equals(ColorCode.GREEN.getID())){
		        				memberHeader.createCell(columnIndex).setCellStyle(cellStyleGreen);
							} else if(projectData.getActualValue().equals(ColorCode.YELLOW.getID())){
								memberHeader.createCell(columnIndex).setCellStyle(cellStyleYellow);
							} else if(projectData.getActualValue().equals(ColorCode.RED.getID())){
								memberHeader.createCell(columnIndex).setCellStyle(cellStyleRed);
							}
		        		} else if (StringUtils.equalsIgnoreCase(projectData.getType(),"percent_complete")){
							memberHeader.createCell(columnIndex).setCellValue(new HSSFRichTextString(projectData.getActualValue()));
		        		} else {
							if(!parentProjectColumnSelected && projectData.getType().equalsIgnoreCase("name")){
								memberHeader.createCell(columnIndex).setCellValue(new HSSFRichTextString(projectData.getDisplayValue()));
								columnIndex ++;
								memberHeader.createCell(columnIndex).setCellValue(new HSSFRichTextString(project.getProject().getSubProjectOf()));
							} else {
								memberHeader.createCell(columnIndex).setCellValue(new HSSFRichTextString(projectData.getDisplayValue()));
							}

		        		}
		        		columnIndex ++;
		        	}
					listIndex ++;
		        }

				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition", "attachment; filename=portfolio.xls");
				wb.write(out);
				out.close();
				
			} catch (Exception e) {
				Logger.getLogger(ExportExcelTimesheet.class).error(
				"Exception in Excel Project Portfolio Servlet.." + e.getMessage());
			} finally {
				if (out != null)
					out.close();
			}
	}
}
