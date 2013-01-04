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

 package net.project.schedule.gantt.servlet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.schedule.Schedule;
import net.project.schedule.gantt.TimeScale;
import net.project.security.User;
import net.project.util.DateUtils;

import org.apache.log4j.Logger;

/**
 * This servlet is responsible for drawing the background image for Gantt
 * charts.
 *
 * @author Matthew Flower
 * @since Version 8.0.0
 */
public class GanttBackgroundServlet extends HttpServlet {
    private static final Color DARK_GREY_COLOR = new Color(172,168,153);
    private static final Color LIGHT_GREY_COLOR = new Color(192,192,192);
    private Logger logger = Logger.getLogger(GanttBackgroundServlet.class);

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            //Get the type of image we are drawing.
            String viewID = request.getParameter("viewID");

            ImageWriter writer = (ImageWriter)ImageIO.getImageWritersByFormatName("PNG").next();
            ServletOutputStream output = response.getOutputStream();
            MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(output);
            writer.setOutput(mcios);

            BufferedImage bi = getNewImage(request, viewID);
            Graphics2D g = (Graphics2D)bi.getGraphics();

            //Set the background of the image white
            g.setPaint(Color.WHITE);
            g.fillRect(0,0,bi.getWidth(),bi.getHeight());

            //Shade non-working days grey
            if (viewID.equals(TimeScale.DAILY.getID())) {
                drawNonWorkingDayBackground(g, request, viewID);
            }

            //Draw the boundary between days
            drawTopHeaderBoundary(g, viewID, request);

            writer.write(bi);
            mcios.close();

            response.setContentType("image/png");
            output.close();
        } catch (Throwable e) {
            logger.debug("Unexpected error while fetching gantt background.", e);
            throw new ServletException(e);
        }
    }

    private void drawTopHeaderBoundary(Graphics2D g, String viewID, HttpServletRequest request) {
        g.setPaint(DARK_GREY_COLOR);

        PnCalendar cal = new PnCalendar();
        Schedule schedule = (Schedule)request.getSession().getAttribute("schedule");
        Date ganttStartDate = schedule.getScheduleStartDate();
        if (schedule.getEarliestTaskStartTimeMS() != -1) {
            ganttStartDate = DateUtils.min(ganttStartDate, new Date(schedule.getEarliestTaskStartTimeMS()));
        }
        cal.setTime(schedule.getScheduleEndDate());
        cal.add(Calendar.DATE, 14);
        Date ganttEndDate = cal.getTime();

        if (viewID.equals(TimeScale.WEEKLY.getID())) {
            float pixelsPerDay = Float.parseFloat(request.getParameter("pixelsPerDay"));

            Date topHeaderStart = cal.startOfMonth(ganttStartDate);
            Date topHeaderEnd = cal.endOfMonth(ganttEndDate);

            //Make sure to show at least 10 weeks (It makes the gantt prettier)
            long monthsToShow = DateUtils.monthsBetween(topHeaderStart, topHeaderEnd);
            if (monthsToShow < 10) {
                monthsToShow = 10;

                cal.setTime(cal.endOfMonth(ganttStartDate));
                cal.add(Calendar.MONTH,  10);
                topHeaderEnd = cal.getTime();
            }

            double currentBoundaryLine = 0;
            cal.setTime(topHeaderStart);
            while (cal.getTime().before(topHeaderEnd)) {
                currentBoundaryLine += cal.getActualMaximum(Calendar.DAY_OF_MONTH) * pixelsPerDay;
                cal.add(PnCalendar.MONTH,  1);
                g.draw(new Rectangle((int)Math.round(currentBoundaryLine)-1,0,0,0));
            }

        } else if (viewID.equals(TimeScale.MONTHLY.getID())) {
            float pixelsPerDay = Float.parseFloat(request.getParameter("pixelsPerDay"));

            Date topHeaderStart = cal.startOfMonth(ganttStartDate);
            Date topHeaderEnd = cal.endOfMonth(ganttEndDate);

            //Make sure to show at least 10 weeks (It makes the gantt prettier)
            long monthsToShow = DateUtils.monthsBetween(topHeaderStart, topHeaderEnd);
            if (monthsToShow < 10) {
                monthsToShow = 10;

                cal.setTime(cal.endOfMonth(ganttStartDate));
                cal.add(Calendar.MONTH,  10);
                topHeaderEnd = cal.getTime();
            }

            double currentBoundaryLine = 0;
            cal.setTime(topHeaderStart);
            while (cal.getTime().before(topHeaderEnd)) {
                currentBoundaryLine += cal.getActualMaximum(Calendar.DAY_OF_MONTH) * pixelsPerDay;
                cal.add(PnCalendar.MONTH,  1);
                g.draw(new Rectangle((int)Math.round(currentBoundaryLine)-1,0,0,0));
            }

        } else {
            String imageWidthString = request.getParameter("width");
            int imageWidth = (imageWidthString != null ? Integer.parseInt(imageWidthString) : 105);
            g.draw(new Rectangle(imageWidth-1,0,0,0));
        }
    }

    private void drawNonWorkingDayBackground(Graphics2D g, HttpServletRequest request, String viewID) {
        if (viewID.equals("1")) {
            int pixelsPerDay = Integer.parseInt(request.getParameter("pixelsPerDay"));

            Schedule schedule = (Schedule)request.getSession().getAttribute("schedule");
            IWorkingTimeCalendar wtCal = schedule.getWorkingTimeCalendar();
            User user = (User)request.getSession().getAttribute("user");
            PnCalendar cal = new PnCalendar(user);
            int firstDayOfWeek = cal.getFirstDayOfWeek();

            for (int i = 0; i < 7; i++) {
                int dayToTest = (firstDayOfWeek+i);
                if (dayToTest > 7) {
                    dayToTest -= 7;
                }

                if (!wtCal.isStandardWorkingDay(dayToTest)) {
                    //Draw first row
                    int startingPixelRow1 = pixelsPerDay*i+1;
                    g.setPaint(LIGHT_GREY_COLOR);
                    for (int j = 0; j < 4; j++) {
                        g.draw(new Rectangle(startingPixelRow1+(j*4), 0, 0, 0));
                    }

                    //Draw the second row
                    int startingPixelRow2 = pixelsPerDay*i+3;
                    for (int j = 0; j < 4; j++) {
                        g.draw(new Rectangle(startingPixelRow2+(j*4), 1, 0, 0));
                    }
                }
            }

            /*
            //Draw the first row of dots
            for (int i = 0; i < 3; i++) {
                g.setPaint(LIGHT_GREY_COLOR);
                g.draw(new Rectangle(4+(i*4), 0, 0, 0));
            }
            for (int i = 0; i < 3; i++) {
                g.setPaint(LIGHT_GREY_COLOR);
                g.draw(new Rectangle(94+(i*4), 0, 0, 0));
            }

            //Draw the second row of dots
            for (int i = 0; i < 4; i++) {
                g.setPaint(LIGHT_GREY_COLOR);
                g.draw(new Rectangle(2+(i*4), 1, 0, 0));
            }
            for (int i = 0; i < 4; i++) {
                g.setPaint(LIGHT_GREY_COLOR);
                g.draw(new Rectangle(92+(i*4), 1, 0, 0));
            }
            */
        }
    }

    private BufferedImage getNewImage(HttpServletRequest request, String viewID) {
        //Figure out the width of the background image
        int imageWidth = 105;
        PnCalendar cal = new PnCalendar();

        imageWidth = getImageWidth(viewID, request, cal);
        return new BufferedImage(imageWidth, 2, BufferedImage.TYPE_INT_RGB);
    }

    private int getImageWidth(String viewID, HttpServletRequest request, PnCalendar cal) {
        int imageWidth;
        Schedule schedule = (Schedule)request.getSession().getAttribute("schedule");
        Date ganttStartDate = schedule.getScheduleStartDate();
        if (schedule.getEarliestTaskStartTimeMS() != -1) {
            ganttStartDate = DateUtils.min(ganttStartDate, new Date(schedule.getEarliestTaskStartTimeMS()));
        }
        cal.setTime(schedule.getScheduleEndDate());
        cal.add(Calendar.DATE, 14);
        Date ganttEndDate = cal.getTime();

        if (viewID.equals(TimeScale.WEEKLY.getID())) {

            Date topHeaderStart = cal.startOfMonth(ganttStartDate);
            Date topHeaderEnd = cal.endOfMonth(ganttEndDate);

            //Make sure to show at least 10 weeks (It makes the gantt prettier)
            long monthsToShow = DateUtils.monthsBetween(topHeaderStart, topHeaderEnd);
            if (monthsToShow < 10) {
                monthsToShow = 10;

                cal.setTime(cal.endOfMonth(ganttStartDate));
                cal.add(Calendar.MONTH,  10);
                topHeaderEnd = cal.getTime();
            }

            long daysToShow = DateUtils.daysBetween(cal, topHeaderStart, topHeaderEnd);
            float pixelsPerDay = Float.parseFloat(request.getParameter("pixelsPerDay"));
            imageWidth = Math.round(pixelsPerDay*daysToShow);
        } else if (viewID.equals(TimeScale.MONTHLY.getID())) {
            Date topHeaderStart = cal.startOfMonth(ganttStartDate);
            Date topHeaderEnd = cal.endOfMonth(ganttEndDate);

            //Make sure to show at least 10 weeks (It makes the gantt prettier)
            long monthsToShow = DateUtils.monthsBetween(topHeaderStart, topHeaderEnd);
            if (monthsToShow < 10) {
                monthsToShow = 10;

                cal.setTime(cal.endOfMonth(ganttStartDate));
                cal.add(Calendar.MONTH,  10);
                topHeaderEnd = cal.getTime();
            }

            long daysToShow = DateUtils.daysBetween(cal, topHeaderStart, topHeaderEnd);
            float pixelsPerDay = Float.parseFloat(request.getParameter("pixelsPerDay"));
            imageWidth = Math.round(pixelsPerDay*daysToShow);
        } else {
            String imageWidthString = request.getParameter("width");
            imageWidth = (imageWidthString != null ? Integer.parseInt(imageWidthString) : 105);
        }

        return imageWidth;
    }
}
