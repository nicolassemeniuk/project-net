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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.resource.mvc.view;

import net.project.base.mvc.ForwardingJSPView;
import net.project.base.mvc.IView;

/**
 * Return an appropriate view given a view name.
 *
 * @author Matthew Flower
 * @since Version 7.6
 */
public class ViewResolver {

    /**
     * Return an appropriate view given the current view name.
     * <p>
     * If the viewName starts with a "/", we use it as a JSP path.
     * Otherwise, if we assume the viewName refers to a JSP page in
     * in resource.
     * </p>
     *
     * @param viewName a <code>String</code> which should indicate the view we
     * are going to render.
     * @return a <code>IView</code> that will allow us to send content to the
     * user.
     */
    public static IView resolveViewname(String viewName) {

        // Currently all views are JSP views in schedule
        if (viewName.startsWith("/")) {
            return new ForwardingJSPView(viewName);
        } else {
            return new ForwardingJSPView("/resource/" + viewName + ".jsp");
        }
    }
}
