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
package net.project.schedule.mvc.handler.baseline;

import javax.servlet.http.HttpServletRequest;

import net.project.base.mvc.Handler;
import net.project.persistence.PersistenceException;
import net.project.schedule.Baseline;
import net.project.schedule.BaselineFinder;
import net.project.util.Validator;

public abstract class AbstractBaselineModifyHandler extends Handler {
    public AbstractBaselineModifyHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Gets the name of the view that will be rendered after processing is
     * complete.
     *
     * @return a <code>String</code> containing a name that uniquely identifies
     *         a view that we are going to redirect to after processing the
     *         request.
     */
    public String getViewName() {
        return "/schedule/baseline/Modify.jsp";
    }

    /**
     * Get a baseline to display on the creation screen.  This will be a brand
     * new baseline unless the user has already tried to create one and got an
     * error.
     *
     * @param request a <code>HttpServletRequest</code> that was used when calling
     * this servlet.
     * @return a <code>Baseline</code> object to display on the create page.
     */
    protected Baseline getBaseline(HttpServletRequest request, boolean loadFromDB) throws PersistenceException {
        Baseline baseline;

        if (!Validator.isBlankOrNull(request.getParameter("donterase"))) {
            baseline = (Baseline)getSessionVar("baseline");
        } else if (loadFromDB) {
            BaselineFinder finder = new BaselineFinder();
            String baselineID = request.getParameter("baselineID");
            baseline = finder.findBaseline(baselineID);
        } else {
            baseline = new Baseline();
        }

        request.getSession().setAttribute("baseline", baseline);

        return baseline;
    }
}
