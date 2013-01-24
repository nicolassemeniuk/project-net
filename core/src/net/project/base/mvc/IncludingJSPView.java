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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 19757 $
|       $Date: 2009-08-16 06:23:23 -0300 (dom, 16 ago 2009) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.base.mvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IncludingJSPView extends AbstractJSPView {
    /**
     * Create a new JSP view object based on the specified path and view name.
     *
     * @param pathToJSP the path to the JSP page; it should start with a
     * <code>/</code> and contain a jsp page name; for example
     * <code>/schedule/Main.jsp</code>
     * @throws NullPointerException if pathToJSP is null
     */
    public IncludingJSPView(String pathToJSP) {
        super(pathToJSP);
    }

    /**
     * Create a model from a view.  This may occur though a variety of methods,
     * including XSLT, JSP, binary content, etc.
     *
     * @param model a <code>Map</code> containing name to object mappings for
     * the objects required to render the view.
     * @param request a <code>HttpServletRequest</code> object which contains
     * the parameters passed to the controller servlet.
     * @param response a <code>HttpServletResponse</code> object which allows us
     * to render the view.
     * @throws Exception if an error occurs while rendering the view.
     */
    public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        addModelToRequest(model, request);
        response.setContentType("text/html; charset=UTF-8");
        request.getRequestDispatcher(getJSPPath()).include(request, response);
    }
}
