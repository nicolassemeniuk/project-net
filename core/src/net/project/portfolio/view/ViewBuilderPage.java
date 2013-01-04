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

import net.project.base.property.PropertyProvider;

/**
 * Provides a page displayed in a view builder.
 * A page has an ID which is used for looking up the page in the view builder.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public abstract class ViewBuilderPage {

    protected final String id;
    protected final String nameToken;

    /**
     * Creates a new page.
     * @param id the ID of this page; this should be different from all other
     * IDs in the same collection of pages
     * @param nameToken the token that provides the display name of this page
     */
    public ViewBuilderPage(String id, String nameToken) {
        this.id = id;
        this.nameToken = nameToken;
    }

    /**
     * Returns the ID of this page; used to uniquely identify this page among
     * other pages in a collection
     * @return the ID of this page
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the display name of this page.
     * @return the display name
     */
    public String getName() {
        return PropertyProvider.get(nameToken);
    }
}

