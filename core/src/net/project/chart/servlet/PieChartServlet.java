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

 package net.project.chart.servlet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.chart.ChartingException;
import net.project.hibernate.model.project_space.ProjectSchedule;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.ServiceFactory;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;


public class PieChartServlet extends HttpServlet {
   
    
    private void createChart(HttpServletRequest request, OutputStream stream) throws IOException, ChartingException {
    	ImageWriter writer = (ImageWriter)ImageIO.getImageWritersByFormatName("JPG").next();
    	MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(stream);
    	writer.setOutput(mcios);
    	
        try {
        	IPnProjectSpaceService service = ServiceFactory.getInstance().getPnProjectSpaceService();
        	
        	Integer numberOfLateTasks = null;
			Integer numberOfComingDueTasks = null;
			Integer numberOfCompletedTasks = null;
			Integer numberOfUnassignedTasks = null;
			
        	if (StringUtils.isNotEmpty(request.getParameter("numberLateTasks"))) {
        		numberOfLateTasks = Integer.parseInt(request.getParameter("numberLateTasks"));
			}
        	
        	if(StringUtils.isNotEmpty(request.getParameter("numberComingDueTasks"))){
        		numberOfComingDueTasks = Integer.parseInt(request.getParameter("numberComingDueTasks"));
        	}
        	
        	if(StringUtils.isNotEmpty(request.getParameter("numberCompletedTasks"))){
        		numberOfCompletedTasks = Integer.parseInt(request.getParameter("numberCompletedTasks"));
        	}
        	
        	if(StringUtils.isNotEmpty(request.getParameter("numberUnassignedTasks"))){
        		numberOfUnassignedTasks = Integer.parseInt(request.getParameter("numberUnassignedTasks"));
        	}
        	
        	DefaultPieDataset dataset = new DefaultPieDataset();
        	dataset.setValue("Late tasks", numberOfLateTasks);
        	dataset.setValue("Tasks coming due this week", numberOfComingDueTasks);
        	dataset.setValue("Tasks completed", numberOfCompletedTasks);
        	dataset.setValue("Unassigned tasks", numberOfUnassignedTasks);
        	
        	DefaultCategoryDataset dcd = new DefaultCategoryDataset();
        	if(numberOfLateTasks > 0) {
        		dcd.addValue(numberOfLateTasks, "Late tasks", "");
        	}
        	
        	if(numberOfComingDueTasks > 0) {
        		dcd.addValue(numberOfComingDueTasks, "Tasks coming due this week", "");
        	}
        	
        	if(numberOfCompletedTasks > 0) {
        		dcd.addValue(numberOfCompletedTasks, "Tasks completed", "");
        	}
        	
        	if(numberOfUnassignedTasks > 0) {
        		dcd.addValue(numberOfUnassignedTasks, "Unassigned tasks", "");
        	}
        	
        	JFreeChart jfc = ChartFactory.createBarChart("", "", "", dcd, PlotOrientation.VERTICAL, false, false, false);
        	
        	/*JFreeChart jfc = ChartFactory.createPieChart("", dataset, true, false, false);
        	PiePlot pp = (PiePlot)jfc.getPlot();
        	pp.setSectionOutlinesVisible(false);
        	pp.setLabelGenerator(null);
        	pp.setSectionPaint(0, ChartColor.RED);
        	pp.setSectionPaint(1, ChartColor.YELLOW);
        	pp.setSectionPaint(2, ChartColor.GREEN);
        	pp.setSectionPaint(3, ChartColor.BLUE);
        	*/
        	
        	CategoryPlot plot = jfc.getCategoryPlot();
        	BarRenderer renderer = (BarRenderer)plot.getRenderer();
        	int bar = 0;
        	
	    	if(numberOfLateTasks > 0) 
	             renderer.setSeriesPaint(bar++, ChartColor.RED );
	         
	        if(numberOfComingDueTasks > 0)
	             renderer.setSeriesPaint(bar++, ChartColor.YELLOW );
	         
	        if(numberOfCompletedTasks > 0)
	             renderer.setSeriesPaint(bar++, ChartColor.GREEN );
	         
	        if(numberOfUnassignedTasks > 0)
	             renderer.setSeriesPaint(bar++, ChartColor.BLUE );
	         
        	jfc.setBorderVisible(false);
        	Color color = Color.WHITE;
        	jfc.setBorderPaint(color);
        	//jfc.setBorderStroke(new BasicStroke(0));
        	jfc.setBackgroundPaint(color);
        	//jfc.getLegend().setPosition(RectangleEdge.RIGHT);
        	BufferedImage image = jfc.createBufferedImage(315, 150);
        	
        	
        	writer.write(image);
        	mcios.close();
        } catch (Exception e) {
            throw new ChartingException("An unexpected exception occurred while generating your chart.", e);
        }
    }

    private void writeDebugImage(BufferedImage bi, String chartType) throws IOException {
        //Debug code that will write the chart out to the filesystem
        File temporaryFile = File.createTempFile("", ".png");
        FileImageOutputStream fios = new FileImageOutputStream(temporaryFile);
        ImageWriter debugWriter = (ImageWriter)ImageIO.getImageWritersByFormatName("PNG");
        debugWriter.setOutput(fios);
        debugWriter.write(bi);

        Logger.getLogger(ChartingServlet.class).debug("Writing debug image of " +
            "chart type " + chartType + " to disk");
    }

   
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }

    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            OutputStream out = response.getOutputStream();
            createChart(request, out);
            response.setContentType("image/png");
            out.close();
        } catch (Throwable t) {
            System.out.println(t.toString());
            t.printStackTrace(System.out);
            Logger.getLogger(ChartingServlet.class).debug("Charting exception", t);
        }
    }
}

