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

import net.project.base.finder.FinderFilterList;

/**
 * Provides a single page containing filters to be displayed when building
 * a view.
 * This allows different filters to be displayed on different pages; useful
 * when there are a large number of filters.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class ViewBuilderFilterPage extends ViewBuilderPage {

    private final FinderFilterList finderFilterList;

    /**
     * Creates a new filter page.
     * @param id the ID of this page; this should be different from all other
     * IDs in the same collection of filter pages
     * @param nameToken the token that provides the display name of this page
     * @param finderFilterList the filter list containing the filters to
     * be included on this page
     */
    ViewBuilderFilterPage(String id, String nameToken, FinderFilterList finderFilterList) {
        super(id, nameToken);
        this.finderFilterList = finderFilterList;
    }

    /**
     * Returns the filter list which provides the filters to be included on
     * this page.
     * @return the filter list
     */
    public FinderFilterList getFinderFilterList() {
        return this.finderFilterList;
    }

}
