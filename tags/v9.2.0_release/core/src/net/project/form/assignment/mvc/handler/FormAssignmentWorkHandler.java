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
package net.project.form.assignment.mvc.handler;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.form.assignment.mvc.view.FormAssignmentXMLView;
import net.project.resource.AssignmentWorkLogEntry;
import net.project.resource.AssignmentWorkLogFinder;
import net.project.security.AuthorizationFailedException;

public class FormAssignmentWorkHandler extends Handler {

    public FormAssignmentWorkHandler(HttpServletRequest request) { 
        super(request);
    }
    
    protected String getViewName() {
        throw new UnsupportedOperationException("FormAssignmentHandler cannot return a simple view name.  Use getView() instead.");
    }
    
    /**
     * Returns a Javascript view for rendering the model.
     * @return the view
     */
    public IView getView() {
        return new FormAssignmentXMLView();
    }
    
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        
        String formDataID = request.getParameter("formDataID");
        String personID = request.getParameter("personID");
        
        AssignmentWorkLogFinder finder = new AssignmentWorkLogFinder();
        List workLogEntries = finder.findByObjectIDPersonID(formDataID, personID);
        
        StringBuffer xml = new StringBuffer();
        xml.append("<workLog_list>\n");
        for(int i = 0; i < workLogEntries.size(); i++) {
            xml.append(((AssignmentWorkLogEntry) workLogEntries.get(i)).getXMLBody());
        }
        xml.append("</workLog_list>\n");        
        
        model.put("XML", xml.toString());
//        System.out.println(xml);
        return model;
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException {
    }

}
