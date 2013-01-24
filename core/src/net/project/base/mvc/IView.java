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
package net.project.base.mvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IView {
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
    void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
