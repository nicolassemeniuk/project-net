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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.portfolio.view;

import java.util.List;

/**
 * Provides a list of views, both default views and modifiable views
 * for a specific context.
 */
public interface IViewList {

    /**
     * Specifies the context for the view list.
     * All views returned by this list belong to that context.
     * @param context the context
     */
    public void setViewContext(IViewContext context);

    /**
     * Returns the default views for the current context.
     * These are views which cannot be modified by the user.
     * @return the list of default views where each element is a <code>IView</code>
     * or the empty list if there are none
     */
    public List getDefaultViews();

    /**
     * Returns all modifiable views for the current context.
     * @return a list where each element is a <code>IView</code> or the empty
     * list if there are none
     */
    public List getModifiableViews();

    /**
     * Returns all available views for the current context.
     * @return a list where each element is a <code>IView</code> or the empty
     * list if there are none
     */
    public List getAllViews();

    /**
     * Returns the modifiable view with the specified id.
     * @param id the id of the view to get
     * @return the modifiable view
     */
    public IView forID(String id);

    /**
     * Returns the XML for modifiable views including version tag.
     * @return the XML representation of modifiable views
     */
    public String getModifiableViewsXML();

    /**
     * Returns the XML for modifiable views excluding version tag.
     * @return the XML representation of modifiable views
     */
    public String getModifiableViewsXMLBody();
}
