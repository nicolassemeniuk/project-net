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

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides a base for creating a JSPView.
 * @author Tim Morrow
 * @since Version 7.6.3
 */
abstract class AbstractJSPView implements IView {

    /** The constructed path to the JSP page representing this view. */
    private final String JSPPath;
    
    private boolean redirect = false;

    /**
     * Create a new JSP view object based on the specified path and view name.
     * @param pathToJSP the path to the JSP page; it should start with a <code>/</code>
     * and contain a jsp page name; for example <code>/schedule/Main.jsp</code>
     * @throws NullPointerException if pathToJSP is null
     */
    protected AbstractJSPView(String pathToJSP) {

        if (pathToJSP == null) {
            throw new NullPointerException("pathToJSP is required");
        }

        this.JSPPath = pathToJSP;
    }

    /**
     * Returns the path to the JSP page that provides the view.
     * @return the JSP path as specified in the constructor
     */
    protected String getJSPPath() {
        return this.JSPPath;
    }

    /**
     * Copies all the entires in <code>model</code> to the request as attributes.
     * @param model a <code>Map</code> containing the request attributes; each
     * key must be a <code>String<code> representing a request attribute name
     * and each value is the request attribute value.
     * @param request a <code>HttpServletRequest</code> object which contains
     * the information passed to the controller; the model entries are added
     * as attributes
     */
    protected void addModelToRequest(Map model, HttpServletRequest request) {
        for (Iterator it = model.keySet().iterator(); it.hasNext();) {
            String attributeName = (String)it.next();
            Object attributeValue = model.get(attributeName);
            request.setAttribute(attributeName, attributeValue);
        }
    }
    
    public boolean isRedirect(){
    	return redirect;
    }
    
    public void setRedirect(boolean redirect){
    	this.redirect = redirect;
    }    
}
