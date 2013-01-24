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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.base.finder;

import net.project.util.VisitException;

/**
 * This filter represents a filter that needs to be display in a query screen
 * but that does not effect the SQL statement of a finder at all.  For example,
 * this could be a filter which shows the text "Show all Tasks".
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class EmptyFinderFilter extends FinderFilter {
    private static String NAME_TOKEN = "prm.report.filters.emptyfinderfilter.description";

    /**
     * Constructor which creates a new instance of EmptyFinderFilter.  When using
     * this constructor, a name token doesn't have to be supplied.  It is
     * assumed to be <code>prm.report.filters.emptyfinderfilter.description</code>.
     *
     * @param id - a unique identifier for this finder filter in the context of
     * a FinderFilterList.  If you are creating a list of filters in one of your
     * objects, each ID should be unique so the update method can find your
     * filters easily.  (Also, that class won't allow two filters with the same
     * ID to be added.)
     */
    public EmptyFinderFilter(String id) {
        super(id, NAME_TOKEN);
    }

    /**
     * Default constructor.
     *
     * @param id - a unique identifier for this finder filter in the context of
     * a FinderFilterList.  If you are creating a list of filters in one of your
     * objects, each ID should be unique so the update method can find your
     * filters easily.  (Also, that class won't allow two filters with the same
     * ID to be added.)
     * @param nameToken - The token (property) that will allow this filter to
     * look up a human-readable representation of this token.
     */
    public EmptyFinderFilter(String id, String nameToken) {
        super(id, nameToken);
    }

    /**
     * Constructor which creates a new instance of EmptyFinderFilter.  When using
     * this constructor, a name token doesn't have to be supplied.  It is
     * assumed to be <code>prm.report.filters.emptyfinderfilter.description</code>.
     *
     * @param id - a unique identifier for this finder filter in the context of
     * a FinderFilterList.  If you are creating a list of filters in one of your
     * objects, each ID should be unique so the update method can find your
     * filters easily.  (Also, that class won't allow two filters with the same
     * ID to be added.)
     * @param selected a <code>boolean</code> value indicating whether this
     * finder is selected by default.
     */
    public EmptyFinderFilter(String id, boolean selected) {
        super(id, NAME_TOKEN);
        setSelected(selected);
    }

    /**
     * Default constructor.
     *
     * @param id - a unique identifier for this finder filter in the context of
     * a FinderFilterList.  If you are creating a list of filters in one of your
     * objects, each ID should be unique so the update method can find your
     * filters easily.  (Also, that class won't allow two filters with the same
     * ID to be added.)
     * @param nameToken - The token (property) that will allow this filter to
     * look up a human-readable representation of this token.
     * @param selected a <code>boolean</code> value indicating whether this
     * finder is selected by default.
     */
    public EmptyFinderFilter(String id, String nameToken, boolean selected) {
        super(id, nameToken);
        setSelected(selected);
    }

    /**
     * Get the where clause for an empty finder filter, which should always be
     * empty.
     *
     * @return a <code>String</code> value which should always be empty.
     */
    public String getWhereClause() {
        return "";
    }

    public String getFilterDescription() {
        return getName();
    }

    protected void clearProperties() {
        // No properties to clear
    }

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitEmptyFinderFilter(this);
    }

}
