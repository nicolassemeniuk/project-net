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
package net.project.schedule.mvc.handler.main;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import net.project.base.Module;
import net.project.schedule.Schedule;
import net.project.schedule.TaskListUtils;
import net.project.security.Action;
import net.project.security.SecurityProvider;
import net.project.util.ErrorReporter;

/**
 * Set the work percent complete for several schedule entries in the task list.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class PercentageChangeHandler extends MultiItemHandler {
    public PercentageChangeHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Add the necessary elements to the model that are required to render a
     * view.  Often this will include things like loading variables that are
     * needed in a page and adding them to the model.
     *
     * The views themselves should not be doing any loading from the database.
     * The whole reason for an mvc architecture is to avoid that.  All loading
     * should occur in the handler.
     *
     * @param request the <code>HttpServletRequest</code> that resulted from the
     * user submitting the page.
     * @param response the <code>HttpServletResponse</code> that will allow us
     * to pass information back to the user.
     * @return a <code>Map</code> which is the updated model.
     * @throws Exception if any error occurs.
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        passThru(model, "module");
        model.put("action", String.valueOf(Action.VIEW));

        //Change the percentage complete
        String idList[] = request.getParameterValues("selected");
        if(null == idList){
            idList = StringUtils.split((String) request.getParameter("selectedIds"), ',');
        }
        String percentageString = request.getParameter("percentage");
        //try to get rid of % if accidently there
        int percentPos = percentageString.indexOf('%');
		if (percentPos > -1)
			percentageString = percentageString.substring(0, percentPos);

		double percentage = 0;
		try {
			percentage = Double.parseDouble(percentageString);
		} catch (NumberFormatException e) {
			// just do nothing if garbage was entered
			return model;
		}
        
        //check security
    	SecurityProvider securityProvider= (SecurityProvider) getSessionVar("securityProvider");
    	securityProvider.multiSecurityCheck(idList, String.valueOf(Module.SCHEDULE), Action.MODIFY);

        //If work is zero, then work complete and percent complete aren't related
/*        if (new Schedule().getTotalWork().isZero()) {
        	ErrorReporter er1 = new ErrorReporter();
            er1.addError(PropertyProvider.get("prm.schedule.taskedit.error.toomuchworkcomplete.message"));
            request.getSession().setAttribute("errorReporter", er1);
            return model;
        }
*/    	
        //Set the error handler that will be used to render warnings or errors.
        ErrorReporter er = TaskListUtils.setWorkPercentComplete(idList, percentage, (Schedule)getSessionVar("schedule"));
        request.getSession().setAttribute("errorReporter", er);

        return model;
    }
}
