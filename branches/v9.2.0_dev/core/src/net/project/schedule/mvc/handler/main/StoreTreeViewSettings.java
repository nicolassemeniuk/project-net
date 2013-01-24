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

import java.text.ParseException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.channel.ScopeType;
import net.project.resource.PersonProperty;
import net.project.resource.mvc.view.WorkChangedView;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;

public class StoreTreeViewSettings extends Handler {
    /**
     * Standard constructor.
     *
     * @param request a <code>HttpServletRequest</code> object that gives the
     *                code access to the request parameters.
     */
    public StoreTreeViewSettings(HttpServletRequest request) {
        super(request);
    }

    /**
     * Gets the name of the view that will be rendered after processing is
     * complete.
     * <p/>
     * This method is called by {@link #getView()} to construct the default
     * view type.  Implementing classes should return a view name when the
     * default view type is required.  If a custom view type is required, then
     * <code>getView()</code> should be overridden and this method should return
     * an <code>UnsupportedOperationException</code>.
     * </p>
     *
     * @return a <code>String</code> containing a name that uniquely identifies
     *         a view that we are going to redirect to after processing the request.
     * @throws UnsupportedOperationException if the implementing class does not
     *                                       provide a view name and instead has overridden {@link #getView()}
     */
    protected String getViewName() {
        throw new UnsupportedOperationException("StoreTreeViewSettings cannot return a simple view name.  Use getView() instead.");
    }

    /**
     * Returns a Javascript view for rendering the model.
     * @return the view
     */
    public IView getView() {
        return new IView() {
            public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                //do nothing!!
            }
        };
    }
    
    /**
     * Ensure that the requester has proper rights to access this object.  For
     * objects that aren't excluded from security checks, this will just
     * consist of verifying that the parameters that were used to access this
     * page were correct (that is, that the requester didn't try to "spoof it"
     * by using a module and action they have permission to.)
     *
     * @param module   the <code>int</code> value representing the module that
     *                 was passed to security.
     * @param action   the <code>int</code> value that was passed through security
     *                 for the action.
     * @param objectID the <code>String</code> value for objectID that was
     *                 passed through security.
     * @param request  the entire request that was submitted to the schedule
     *                 controller.
     * @throws net.project.security.AuthorizationFailedException
     *                                        if the user didn't have the proper
     *                                        credentials to view this page, or if they tried to spoof security.
     * @throws net.project.base.PnetException if any other error occurred.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException {
        //Not necessary, done in current user's scope only
    }

    /**
     * Add the necessary elements to the model that are required to render a
     * view.  Often this will include things like loading variables that are
     * needed in a page and adding them to the model.
     * <p/>
     * The views themselves should not be doing any loading from the database.
     * The whole reason for an mvc architecture is to avoid that.  All loading
     * should occur in the handler.
     *
     * @param request  the <code>HttpServletRequest</code> that resulted from the
     *                 user submitting the page.
     * @param response the <code>HttpServletResponse</code> that will allow us
     *                 to pass information back to the user.
     * @return a <code>Map</code> which is the updated model.
     * @throws Exception if any error occurs.
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //Store the setting
        String propertyName = request.getParameter("name");
        String propertyValue = request.getParameter("value");

        PersonProperty property = PersonProperty.getFromSession(request.getSession());
        property.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));

        //Remove any old values
        property.replace("prm.schedule.main", propertyName, propertyValue);

        return Collections.EMPTY_MAP;
    }
}
