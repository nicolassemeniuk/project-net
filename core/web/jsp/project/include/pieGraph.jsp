<%--
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
--%>

<%@ page 
    contentType="image/png"
    info="News" 
    language="java" 
    errorPage="/errors.jsp"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<%@page import="net.project.hibernate.service.ServiceFactory"%>
<%@page import="net.project.hibernate.service.IPnProjectSpaceService"%>
<%@page import="net.project.hibernate.model.project_space.ProjectSchedule"%>
<%@page import="org.jfree.chart.JFreeChart"%>
<%@page import="org.jfree.chart.ChartFactory"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@page import="org.jfree.chart.plot.PiePlot"%>
<%@page import="java.awt.image.BufferedImage"%>
<%@page import="javax.imageio.ImageWriter"%>
<%@page import="javax.imageio.ImageIO"%>
<%@page import="javax.imageio.stream.MemoryCacheImageOutputStream"%>
<%@page import="java.awt.Color"%>
<%@page import="java.awt.BasicStroke"%>
<%@page import="net.project.chart.ChartColor"%>
<%@page import="org.jfree.ui.RectangleEdge"%>
<%@page import="org.jfree.chart.plot.PlotOrientation"%>
<%@page import="org.jfree.data.category.DefaultCategoryDataset"%>
<%@page import="org.jfree.chart.plot.Plot"%>
<%@page import="org.jfree.chart.plot.CategoryPlot"%>
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />

<%
	
	IPnProjectSpaceService service = ServiceFactory.getInstance().getPnProjectSpaceService();
	ProjectSchedule projectSchedule = service.getProjectSchedule(Integer.parseInt(projectSpace.getID()));
	
	DefaultPieDataset dataset = new DefaultPieDataset();
	dataset.setValue("Late tasks", projectSchedule.getNumberOfLateTasks());
	dataset.setValue("Tasks coming due this week", projectSchedule.getNumberOfTaskComingDue());
	dataset.setValue("Tasks completed", projectSchedule.getNumberOfCompletedTasks());
	dataset.setValue("Unassigned tasks", projectSchedule.getNumberOfUnassignedTasks());
	
	DefaultCategoryDataset dcd = new DefaultCategoryDataset();
	if(projectSchedule.getNumberOfLateTasks() > 0) {
		dcd.addValue(projectSchedule.getNumberOfLateTasks(), "Late tasks", "");
	}
	
	if(projectSchedule.getNumberOfTaskComingDue() > 0) {
		dcd.addValue(projectSchedule.getNumberOfTaskComingDue(), "Tasks coming due this week", "");
	}
	
	if(projectSchedule.getNumberOfCompletedTasks() > 0) {
		dcd.addValue(projectSchedule.getNumberOfCompletedTasks(), "Tasks completed", "");
	}
	
	if(projectSchedule.getNumberOfUnassignedTasks() > 0) {
		dcd.addValue(projectSchedule.getNumberOfUnassignedTasks(), "Unassigned tasks", "");
	}
	
	JFreeChart jfc = ChartFactory.createBarChart("", "", "", dcd, PlotOrientation.VERTICAL, true, false, false);
	
	/*JFreeChart jfc = ChartFactory.createPieChart("", dataset, true, false, false);
	PiePlot pp = (PiePlot)jfc.getPlot();
	pp.setSectionOutlinesVisible(false);
	pp.setLabelGenerator(null);
	pp.setSectionPaint(0, ChartColor.RED);
	pp.setSectionPaint(1, ChartColor.YELLOW);
	pp.setSectionPaint(2, ChartColor.GREEN);
	pp.setSectionPaint(3, ChartColor.BLUE);
	*/
	jfc.setBorderVisible(false);
	Color color = Color.WHITE;
	jfc.setBorderPaint(color);
	//jfc.setBorderStroke(new BasicStroke(0));
	jfc.setBackgroundPaint(color);
	jfc.getLegend().setPosition(RectangleEdge.BOTTOM);
	BufferedImage image = jfc.createBufferedImage(315, 150);
	
	ImageWriter writer = (ImageWriter)ImageIO.getImageWritersByFormatName("PNG").next();
	MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(response.getOutputStream());
	writer.setOutput(mcios);
	writer.write(image);
	mcios.close();

%>