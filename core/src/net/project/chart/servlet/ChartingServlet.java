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

import net.project.base.property.PropertyProvider;
import net.project.chart.ChartType;
import net.project.chart.ChartingException;
import net.project.chart.IChart;
import net.project.chart.MissingChartDataException;

import org.apache.log4j.Logger;

/**
 * This servlet calls helper objects to produce charts in PNG format.  The charts
 * that can be produced by this servlet must be registered with
 * {@link net.project.chart.ChartType} and require all of the necessary parameters
 * that the appropriate charting object requires.  (Look in the reporting module
 * for a little bit more illumination.)
 *
 * The type of chart that you will be producing will be passed into this servlet
 * with the request parameter name "chartType".  All other parameters are defined
 * by the appropriate charting object.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ChartingServlet extends HttpServlet {
    /**
     * Token points to: "Parameter {0} was not passed to this servlet.
     * This parameter is required."
     */
    private String PARAMETER_MISSING_TOKEN = "prm.global.chart.servlet.parametermissing.message";

    /**
     * Write a chart to the output stream given the information in the request
     * object.
     *
     * @param request a <code>HttpServletRequest</code> object which will supply
     * the parameters to initialize the report.
     * @param stream an <code>OutputStream</code> that the charting image will be
     * written to.
     * @throws IOException if there is an error writing the image to the output
     * stream.
     * @throws ChartingException if any error occurs while creating the chart.
     * All errors are rethrown inside of a ChartingException.
     */
    private void createChart(HttpServletRequest request, OutputStream stream) throws IOException, ChartingException {
        ImageWriter writer = (ImageWriter)ImageIO.getImageWritersByFormatName("PNG").next();
        MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(stream);
        writer.setOutput(mcios);

        try {
            String chartType = request.getParameter("chartType");
            if ((chartType == null) || (chartType.trim().length() == 0)) {
                throw new ChartingException(PropertyProvider.get(PARAMETER_MISSING_TOKEN, "chartType"));
            }
            IChart chart = ChartType.getForID(chartType).getInstance();
            chart.populateParameters(request);
            BufferedImage bi = chart.getChart();

            //writeDebugImage(bi, chartType);

            writer.write(bi);
            mcios.close();
        } catch (MissingChartDataException e) {
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

    /**
     * Called by the server (via the <code>service</code> method) to allow a
     * servlet to handle a POST request.
     *
     * The HTTP POST method allows the client to send data of unlimited length
     * to the Web server a single time and is useful when posting information
     * such as credit card numbers.
     *
     * <p>When overriding this method, read the request data, write the response
     * headers, get the response's writer or output stream object, and finally,
     * write the response data. It's best to include content type and encoding.
     * When using a <code>PrintWriter</code> object to return the response, set
     * the content type before accessing the <code>PrintWriter</code> object.
     *
     * <p>The servlet container must write the headers before committing the
     * response, because in HTTP the headers must be sent before the response
     * body.
     *
     * <p>Where possible, set the Content-Length header (with the {@link
     * javax.servlet.ServletResponse#setContentLength} method), to allow the
     * servlet container to use a persistent connection to return its response
     * to the client, improving performance. The content length is automatically
     * set if the entire response fits inside the response buffer.
     *
     * <p>When using HTTP 1.1 chunked encoding (which means that the response
     * has a Transfer-Encoding header), do not set the Content-Length header.
     *
     * <p>This method does not need to be either safe or idempotent. Operations
     * requested through POST can have side effects for which the user can be
     * held accountable, for example, updating stored data or buying items
     * online.
     *
     * <p>If the HTTP POST request is incorrectly formatted, <code>doPost</code>
     * returns an HTTP "Bad Request" message.
     *
     * @param     request     an {@link HttpServletRequest} object that contains
     *                        the request the client has made of the servlet
     * @param     response	an {@link HttpServletResponse} object that contains
     *                        the response the servlet sends to the client
     * @see       javax.servlet.ServletOutputStream
     * @see       javax.servlet.ServletResponse#setContentType
     * @exception IOException if an input or output error is
     *                        detected when the servlet handles the request
     * @exception ServletException if the request for the POST could
     *                        not be handled
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }

    /**
     * Called by the server (via the <code>service</code> method) to allow a
     * servlet to handle a GET request.
     *
     * <p>Overriding this method to support a GET request also automatically
     * supports an HTTP HEAD request. A HEAD request is a GET request that
     * returns no body in the response, only the request header fields.
     *
     * <p>When overriding this method, read the request data, write the response
     * headers, get the response's writer or output stream object, and finally,
     * write the response data. It's best to include content type and encoding.
     * When using a <code>PrintWriter</code> object to return the response, set
     * the content type before accessing the <code>PrintWriter</code> object.
     *
     * <p>The servlet container must write the headers before committing the
     * response, because in HTTP the headers must be sent before the response
     * body.
     *
     * <p>Where possible, set the Content-Length header (with the {@link
     * javax.servlet.ServletResponse#setContentLength} method), to allow the
     * servlet container to use a persistent connection to return its response
     * to the client, improving performance. The content length is automatically
     * set if the entire response fits inside the response buffer.
     *
     * <p>The GET method should be safe, that is, without any side effects for
     * which users are held responsible. For example, most form queries have no
     * side effects. If a client request is intended to change stored data, the
     * request should use some other HTTP method.
     *
     * <p>The GET method should also be idempotent, meaning that it can be
     * safely repeated. Sometimes making a method safe also makes it idempotent.
     * For example, repeating queries is both safe and idempotent, but buying a
     * product online or modifying data is neither safe nor idempotent.
     *
     * <p>If the request is incorrectly formatted, <code>doGet</code> returns an
     * HTTP "Bad Request" message.
     *
     * @param request an  {@link HttpServletRequest} object that contains the
     * request the client has made of the servlet
     * @param response an {@link HttpServletResponse} object that contains the
     * response the servlet sends to the client
     * @see javax.servlet.ServletResponse#setContentType
     * @exception IOException if an input or output error is detected when the
     * servlet handles the GET request
     * @exception ServletException if the request for the GET could not be
     * handled
     */
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
            //throw new ServletException(t);
        }
    }
}

