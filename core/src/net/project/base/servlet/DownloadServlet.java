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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.IDownloadable;

/**
 * Provides a mechanism for downloading the content of any object as a file. <br>
 * Usage: <code><pre>
 * pageContext.setAttribute("myObject", myObject, PageContext.SESSION_SCOPE);
 * pageContext.forward("/servlet/DownloadServlet?downloadableObjectAttributeName=myObject");
 * </pre></code><br>
 * Request Parameter Inputs:<br><pre>
 * downloadableObjectAttributeName  -  session attribute name of IDownloadable object
 * cleanup                          -  true means the session object will be removed;
 *                                     false means it will not
 * </pre>
 */
public class DownloadServlet extends HttpServlet {

    /**
     * Request parameter that specifies the session attribute name of
     * the IDownloadable object, currently <code>downloadableObjectAttributeName</code>.
     */
    public static final String REQUEST_PARAMETER_ATTRIBUTE_NAME = "downloadableObjectAttributeName";

    /**
     * Request parameter that specifies whether to clean up the session object.
     */
    public static final String REQUEST_PARAMETER_CLEANUP = "cleanup";


    public void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {        
        doPost(request, response);
    }


    /**
     * Downloads an object as a file.
     * The object must implement {@link net.project.base.IDownloadable}.  It
     * is located in the session by the name given by the request parameter
     * <code>{@link #REQUEST_PARAMETER_ATTRIBUTE_NAME}</code>.
     * @param request the request
     * @param response the response
     * @throws IOException if there is a problem
     * @throws ServletException if there is a problem
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {   
        
        String sessionAttributeName = null;
        boolean isCleanupRequired = false;
        IDownloadable downloadableObject = null;
        InputStream in = null;
        ServletOutputStream out = null;

        // Get the session object attribute name and throw an exception
        // if it is missing
        sessionAttributeName = request.getParameter(REQUEST_PARAMETER_ATTRIBUTE_NAME);
        if (sessionAttributeName == null) {
            throw new ServletException("DownloadServlet: Missing request parameter " + REQUEST_PARAMETER_ATTRIBUTE_NAME);
        }
        isCleanupRequired = (request.getParameter(REQUEST_PARAMETER_CLEANUP) != null && request.getParameter(REQUEST_PARAMETER_CLEANUP).equals("true"));

        // Get the downloadable object from the session
        downloadableObject = (IDownloadable) request.getSession().getAttribute(sessionAttributeName);

        // Set the reponse headers
        response.setHeader("Content-disposition", "attachment; filename=" + downloadableObject.getFileName());
        if (downloadableObject.getLength() > 0) {
            response.setHeader("Content-length", "" + downloadableObject.getLength());
        }
        response.setContentType(downloadableObject.getContentType());
        
        // Now write the file
        in = downloadableObject.getInputStream();
        out = response.getOutputStream();
        write(in, out);
        
        // Clean up the session object if necessary
        if (isCleanupRequired) {
            request.getSession().removeAttribute(REQUEST_PARAMETER_ATTRIBUTE_NAME);
        }
    }


    /**
     * Writes the contents of the inputstream to the outputstream.
     * @param in the inputstream to read from
     * @param out the outputstream to write to
     * @throws IOException if there is a problem reading or writing
     */
    private void write(InputStream in, OutputStream out) throws IOException {
        int c = -1;
        while ((c = in.read()) != -1) {
            out.write(c);
        }

    }

}
