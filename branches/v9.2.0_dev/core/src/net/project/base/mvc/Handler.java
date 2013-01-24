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
package net.project.base.mvc;

import java.text.ParseException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.PnetException;
import net.project.security.AuthorizationFailedException;
import net.project.util.Validator;

/**
 * A handler is an object that performs the "logic" associated with a page.
 * <p>
 * This class provides a base class that other handlers should extend, as
 * controllers will be calling the methods defined in this abstract base class.
 * </p>
 * <p>
 * A default view type of {@link JSPView} is returned based on a string view name
 * provided by implementing classes.  However, implementing classes are free to override
 * {@link #getView()} to provide any other type of view.
 * </p>
 * @author Carlos Montemuiño
 * @author Matthew Flower
 * @since Version 7.6
 */
public abstract class Handler {
    protected final HttpServletRequest request;
    protected ViewType viewType = ViewType.NOT_SPECIFIED;
    protected boolean redirect = false;
    
    /**
     * Standard constructor.
     *
     * @param request a <code>HttpServletRequest</code> object that gives the
     * code access to the request parameters.
     */
    public Handler(HttpServletRequest request) {
        this.request = request;

        // Mimic ServletSecurity's printing of JSP page name; makes it easy for
        // development to see what handler is invoked
        System.out.println("Handler: " + getClass().getName());
    }

    /**
     * Provides a way to take a variable that existed in the request object and
     * to pass that object into the model.
     *
     * @param model a <code>Map</code> object to which we are going to add the
     * requested variable.
     * @param variable a <code>String</code> containing the name of the variable
     * that we are going to add to the map.
     * @return a <code>boolean</code> value indicating if we have found the
     * variable that we were looking for.
     */
    protected boolean passThru(Map model, String variable) {
        boolean variableFound = false;

        if (request.getAttribute(variable) != null) {
            model.put(variable, request.getAttribute(variable));
            variableFound = true;
        } else if (request.getParameterValues(variable) != null && request.getParameterValues(variable).length > 1) {
            model.put(variable, request.getParameterValues(variable));
            variableFound = true;
        } else if (request.getParameter(variable) != null) {
            model.put(variable, request.getParameter(variable));
            variableFound = true;
        }

        return variableFound;
    }

    protected void passThruAll(Map model) {
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = (String)parameterNames.nextElement();
            model.put(paramName, request.getParameter(paramName));
        }
    }

    /**
     * Get a session variable.
     *
     * @param variableName a <code>String</code> value that is the name of the
     * session variable we are fetching.
     * @return a <code>Object</code> containing the session variable that was
     * request, or null if the variable was not found.
     */
    protected Object getSessionVar(String variableName) {
        return request.getSession().getAttribute(variableName);
    }

    /**
     * Set a session variable.
     * 
     * @param variableName
     * @param value
     */
    protected void setSessionVar(String variableName, Object value) {
        request.getSession().setAttribute(variableName, value);
    }

    /**
     * Returns a request parameter or request attribute if the request
     * parameter was null.
     * @param variableName the name of the parameter/attribute to get
     * @return the request parameter if a parameter was found for
     * the name and is not blank or null; otherwise a request
     * attribute with the specified name (or null if none is found)
     */
    protected Object getVar(String variableName) {
        Object var = request.getParameter(variableName);
        
/*Avinash:-----CHECK FOR NULL OR BLANK OR "null" VALUE OF var */        
        if (Validator.isBlankOrNull((String)var) || "null".equals(var)) {
//Avinash:---------------------------------------------------------------
            var = request.getAttribute(variableName);
        }

        return var;
    }

    /**
     * Gets the name of the view that will be rendered after processing is
     * complete.
     * <p>
     * This method is called by {@link #getView()} to construct the default
     * view type.  Implementing classes should return a view name when the
     * default view type is required.  If a custom view type is required, then
     * <code>getView()</code> should be overridden and this method should return
     * an <code>UnsupportedOperationException</code>.
     * </p>
     * @return a <code>String</code> containing a name that uniquely identifies
     * a view that we are going to redirect to after processing the request.
     * @throws UnsupportedOperationException if the implementing class does not
     * provide a view name and instead has overridden {@link #getView()}
     */
    protected abstract String getViewName();

    /**
     * Returns the view that will be rendered after processing is complete.
     * <p>
     * The default implementation currently returns a
     * {@link JSPView} constructed using the view name. <br>
     * Any sub-classes requring a different view type should override this
     * method.
     * </p>
     * <p>
     * This method may not be called until after {@link #handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
     * Implementing classes should ensure that a view is available either by providing
     * a view name through {@link #getViewName()} or by overriding this method.
     * </p>
     * @return the view used to render the model
     * @see #getViewName()
     */
    public IView getView() {
        if (getViewType().equals(ViewType.FORWARDING_VIEW)) {
            return new ForwardingJSPView(getViewName());
        } else if (getViewType().equals(ViewType.INCLUDING_VIEW)) {
            return new IncludingJSPView(getViewName());
        } else {
            return new JSPView(getViewName(), isRedirect());
        }
    }

    public ViewType getViewType() {
        return viewType;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    public boolean isRedirect(){
    	return redirect;
    }
    
    public void setRedirect(boolean redirect){
    	this.redirect = redirect;
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
     * @throws AuthorizationFailedException if the user didn't have the proper
     * credentials to view this page, or if they tried to spoof security.
     * @throws PnetException if any other error occurred.
     */
    public abstract void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException;

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
    public abstract Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}

