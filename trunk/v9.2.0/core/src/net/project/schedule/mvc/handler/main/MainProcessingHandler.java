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

 package net.project.schedule.mvc.handler.main;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.ForwardingJSPView;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.base.mvc.IncludingJSPView;
import net.project.base.mvc.JSPView;
import net.project.base.mvc.ViewType;
import net.project.schedule.Schedule;
import net.project.schedule.mvc.view.ScheduleMainBetaView;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SecurityProvider;

/**
 * This class provides a base class that handles the functionality when
 * submitting the schedule "main" page.
 */
public abstract class MainProcessingHandler extends Handler {
    /**
     * Many handlers need the security provider to check security, this is
     * offered for convenience.
     */
    protected SecurityProvider securityProvider;
    /** All subhandlers need the schedule, this is for convenience too. */
    protected Schedule schedule;
    

    public MainProcessingHandler(HttpServletRequest request) {
        super(request);
        this.securityProvider = (SecurityProvider)request.getSession().getAttribute("securityProvider");
        this.schedule = (Schedule)request.getSession().getAttribute("schedule");
    }

    /**
     * Gets the view that will be rendered after processing is complete.
     *
     * @return a <code>IView</code> containing view
     */
    public IView getView() {
        return new ScheduleMainBetaView();
    }
    //sjmittal: please don't make this method non final as we don't want none of the 
    //sub class to define view names as the response UI would always be rendered
    //using the getView method. So if for sub class the returning view is different than main.jsp
    //then override the getView method instead.
    public final String getViewName() {
        throw new UnsupportedOperationException("View is determined by getView method");
    }

    /**
     * Ensure that the requester has proper rights to access this object.  For
     * objects that aren't excluded from security checks, this will just
     * consist of verifying that the parameters that were used to access this
     * page were correct (that is, that the requester didn't try to "spoof it"
     * by using a module and action they have permission to.)
     *
     * @param module the <code>int</code> value representing the module that
     * was passed to security.
     * @param action the <code>int</code> value that was passed through security
     * for the action.
     * @param objectID the <code>String</code> value for objectID that was
     * passed through security.
     * @param request the entire request that was submitted to the schedule
     * controller.
     * @throws net.project.security.AuthorizationFailedException if the user didn't have the proper
     * credentials to view this page, or if they tried to spoof security.
     * @throws net.project.base.PnetException if any other error occurred.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        AccessVerifier.verifyAccess(String.valueOf(Module.SCHEDULE), String.valueOf(Action.MODIFY));
    }
}
