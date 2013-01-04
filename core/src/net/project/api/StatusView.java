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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.api;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.mvc.IView;
import net.project.util.Validator;

/**
 * A view that represents an HTTP response code.
 */
public abstract class StatusView implements IView {

    //
    // Static members
    //

    /** OK status. */
    public static final StatusView OK = new OKStatus();

    /** ERROR status. */
    public static final StatusView ERROR = new ErrorStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error");

    //
    // Instance members
    //

    /**
     * Renders the view for this Status.
     * @param model
     * @param request
     * @param response
     * @throws IOException
     */
    public abstract void render(Map model, HttpServletRequest request, HttpServletResponse response) throws IOException;

    //
    // Nested top-level classes
    //

    /**
     * OK Response.
     */
    private static class OKStatus extends StatusView {

        public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws IOException {
            String message = "OK";

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            response.setContentLength(message.length());

            Writer writer = new BufferedWriter(response.getWriter());
            writer.write(message);
            writer.flush();
        }
    }

    /**
     * Error response.
     */
    private static class ErrorStatus extends StatusView {

        private final int responseCode;
        private final String defaultMessage;

        private ErrorStatus(int responseCode, String defaultMessage) {
            this.responseCode = responseCode;
            this.defaultMessage = defaultMessage;
        }

        public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws IOException {
            String message = (String) model.get("message");
            if (Validator.isBlankOrNull(message)) {
                message = this.defaultMessage;
            }
            response.sendError(this.responseCode, message);
        }
    }

}
