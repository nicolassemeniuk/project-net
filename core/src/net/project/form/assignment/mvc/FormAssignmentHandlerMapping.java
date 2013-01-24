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
|   $Revision: 15790 $
|       $Date: 2007-04-02 07:17:33 +0530 (Mon, 02 Apr 2007) $
|     $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.form.assignment.mvc;

import javax.servlet.http.HttpServletRequest;

import net.project.base.mvc.ControllerException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.HandlerMapping;
import net.project.form.assignment.mvc.handler.FormAssignmentAddRemoveHandler;
import net.project.form.assignment.mvc.handler.FormAssignmentCheckAssignedUserHander;
import net.project.form.assignment.mvc.handler.FormAssignmentHandler;
import net.project.form.assignment.mvc.handler.FormAssignmentUpdateHandler;
import net.project.form.assignment.mvc.handler.FormAssignmentWorkHandler;
import net.project.form.assignment.mvc.handler.FormAssignmentWorkUpdateHandler;
import net.project.util.Validator;

/**
 * This class is responsible for finding the correct handler for any request
 * that has been submitted to the FormAssignmentController.
 *
 * @author Sachin Mittal
 * @since Version 8.4
 */
public class FormAssignmentHandlerMapping extends HandlerMapping {
    protected Handler doGetHandler(HttpServletRequest request) throws ControllerException {
        //Get the path information minus the first slash.
        String pathInfo = request.getPathInfo();
        if (Validator.isBlankOrNull(pathInfo)) {
            pathInfo = request.getParameter("handlerName");
        }
        if (Validator.isBlankOrNull(pathInfo)) {
            pathInfo = request.getParameter("theAction");
        }

        if (pathInfo.equalsIgnoreCase("/FormAssignmentsView")) {
            return new FormAssignmentHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/FormAssignmentWorkView")) {
            return new FormAssignmentWorkHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/FormAssignmentAddRemove")) {
            return new FormAssignmentAddRemoveHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/FormAssignmentUpdate")) {
            return new FormAssignmentUpdateHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/FormAssignmentWorkUpdate")) {
            return new FormAssignmentWorkUpdateHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/FormAssignmentCheckAssignedUser")) {
            return new FormAssignmentCheckAssignedUserHander(request);
        } else {
            throw new ControllerException("Unrecognized assignments module: " + pathInfo);
        }

    }
}
