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
package net.project.schedule.mvc.view;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import net.project.api.JSONView;
import net.project.base.mvc.AbstractJavaScriptView;
import net.project.base.mvc.ViewException;
import net.project.schedule.mvc.view.taskcalculate.ScheduleEntryChangeView;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;

public class ScheduleMainBetaView extends JSONView {

    public String getJSONData(Map model, HttpServletRequest request) {
        //first get from model
        ErrorReporter errorReporter = (ErrorReporter) model.get("errorReporter");
        //then try to get from request
        if(errorReporter == null) {
            errorReporter = (ErrorReporter) request.getAttribute("errorReporter");
            //lastly try to get from session
            if(errorReporter == null) {
                errorReporter = (ErrorReporter) request.getSession().getAttribute("errorReporter");
            }
        }
        String response = "{";
        if (errorReporter == null || !errorReporter.errorsFound()) {
            response += "success: true";
        } else {
            response += "success: false, errors: [";
            Iterator<ErrorDescription> errorDescriptions = errorReporter.getErrorDescriptions().iterator();
            while (errorDescriptions.hasNext()) {
                ErrorDescription errorDescription = errorDescriptions.next();
                response += JSONObject.quote(errorDescription.getErrorText());
                if (errorDescriptions.hasNext())
                    response += ",";
            }
            response += "]";
            errorReporter.clear();
        }

        response += "}";
        return response;
    }

}
