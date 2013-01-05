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
|   $Revision: 20342 $
|       $Date: 2010-01-28 13:04:26 -0300 (jue, 28 ene 2010) $
|     $Author: ritesh $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.portfolio.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.finder.FinderIngredients;

/**
 * View Builder for Personal Portfolio.
 * This provides filter pages and sorter pages specific to building a
 * personal portfolio view.  There are separate pages for filtering and
 * for selecting additional projects to include.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class PersonalPortfolioViewBuilder extends ViewBuilder {


    /**
     * Creates a new PersonalPortfolioViewBuilder for the specified context.
     * The context is used for providing a list of views and for creating
     * new views.
     *
     * @param viewContext the view context
     */
    public PersonalPortfolioViewBuilder(PersonalPortfolioViewContext viewContext) {
        super(viewContext);
    }

    /**
     * Returns an ordered collection of filter pages.
     * Currently provides two pages, one for the filters for Project Properties,
     * the other for selecting additional projects to always include in this list.
     * @return the filter pages
     */
    public Collection getFilterPages() {
        List pageList = new ArrayList();
        pageList.add(new ViewBuilderFilterPage("10", "prm.portfolio.personal.view.edit.filterpage.properties.title",
                ((PersonalPortfolioFinderIngredients) getView().getFinderIngredients()).getProjectPropertiesFilterList()));
        pageList.add(new ViewBuilderFilterPage("20", "prm.portfolio.personal.view.edit.filterpage.selectedprojects.title",
               ((PersonalPortfolioFinderIngredients) getView().getFinderIngredients()).getSelectedProjectsFilterList()));
        return pageList;
    }
    
    /**
     * Returns an ordered collection of sorter pages to be displayed when
     * building a view.
     * Currently provides a single page.
     *
     * @return the sorter pages
     */
    public Collection getSorterPages() {
        List pageList = new ArrayList();
        pageList.add(new ViewBuilderSorterPage("10", "prm.portfolio.personal.view.edit.sorterpage.title",
                getView().getFinderIngredients().getFinderSorterList()));
        return pageList;
    }

    public String getMetaColumnsHTML() {
        FinderIngredients ingredients = getView().getFinderIngredients();
        if (ingredients instanceof PersonalPortfolioFinderIngredients) {
            return ((PersonalPortfolioFinderIngredients) ingredients).getMetaColumnList().getHTML();
        }
        return "";
    }

    public void updateMetaColumnsFromRequest(HttpServletRequest request) {
        FinderIngredients ingredients = getView().getFinderIngredients();
        if (ingredients instanceof PersonalPortfolioFinderIngredients) {
            ((PersonalPortfolioFinderIngredients) ingredients).getMetaColumnList().updateFromRequest(request);
        }
    }
}
