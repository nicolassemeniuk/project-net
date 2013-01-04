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

import net.project.base.finder.FinderSorterList;

/**
 * Provides a single page containing sorters to be displayed when building
 * a view.
 * This allows different sorters to be displayed on different pages; useful
 * when there are a large number of sorters.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class ViewBuilderSorterPage extends ViewBuilderPage {

    private final FinderSorterList finderSorterList;

    /**
     * Creates a new sorter page.
     * @param id the ID of this page; this should be different from all other
     * IDs in the same collection of sorter pages
     * @param nameToken the token that provides the display name of this page
     * @param finderSorterList the sorter list containing the sorters to
     * be included on this page
     */
    ViewBuilderSorterPage(String id, String nameToken, FinderSorterList finderSorterList) {
        super(id, nameToken);
        this.finderSorterList = finderSorterList;
    }

    /**
     * Returns the sorter list which provides the sorters to be included on
     * this page.
     * @return the sorter list
     */
    public FinderSorterList getFinderSorterList() {
        return this.finderSorterList;
    }

}
