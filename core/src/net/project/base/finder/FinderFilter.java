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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.util.VisitException;

/**
 * This class is an abstract class that defines (along with it's concrete
 * implementations) operations that can be performed on a finder object.
 * Operations currently include things such as filtering (where clauses) and
 * sorting (order by clauses).  Operation classes can also create their own
 * presentation for those times when you need to make a querying page.
 *
 * @author Matthew Flower
 * @since Gecko Update 4
 */
public abstract class FinderFilter implements Serializable {
    /**
     * Should this operation generate a where clause for a finder.  This is used
     * by a FinderOperationList when getting the list of selected filters.
     */
    private boolean selected = false;
    /**
     * A token value that points to a property where a human-readable name for
     * this finder operation can be found.
     */
    private String nameToken;
    /**
     * A unique identifier for this operation.  This is important so query pages
     * can figure out which finder operation a field relates to.
     */
    private String id;

    /**
     * Common Public constructor.
     * @param id - a unique identifier for this finder filter in the context of
     * a FinderFilterList.  If you are creating a list of filters in one of your
     * objects, each ID should be unique so the update method can find your
     * filters easily.  (Also, that class won't allow two filters with the same
     * ID to be added.)
     * @param nameToken - The token (property) that will allow this filter to
     * look up a human-readable representation of this token.
     */
    public FinderFilter(String id, String nameToken) {
        this.id = id;
        this.nameToken = nameToken;
    }

    /**
     * Get a where clause for this filter that can be applied to a finder.  This
     * where clause will be empty (not null) if the filter isn't selected.
     *
     * @return a <code>String</code> containing a where clause, or an empty
     * string if the filter isn't selected.
     */
    public abstract String getWhereClause();

    /**
     * Get the token that can be used to get a name for this filter.  This name
     * variable is often used to create an HTML representation for a filter.
     *
     * @return a <code>String</code> value containing a token which points to
     * a human-readable name for this filter.
     */
    public String getNameToken() {
        return nameToken;
    }

    /**
     * Get the unique identifier for this filter.  This is often used to give a
     * filter a unique name on an html interface.
     *
     * @return a <code>String</code> value which uniquely identifies this filter
     * among the filters of this current finder.
     */
    public String getID() {
        return id;
    }

    /**
     * Get a human readable name for this token, which is based on the name
     * token which can be fetched using {@link #getNameToken}.
     *
     * @return a <code>String</code> value containing a human-readable name for
     * this filter.
     */
    public String getName() {
        return PropertyProvider.get(nameToken);
    }

    /**
     * This method indicates whether or not a filter has been selected for
     * application to a finder.  Finders will only use filters whose
     * <code>isSelected</code> method returns true.
     *
     * @return a <code>boolean</code> value indicating whether or not this
     * filter has been selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Indicate whether this filter is selected.  Finders will only use filters
     * whose selected attribute is true.
     *
     * @param selected a <code>boolean</code> variable indicating whether this
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Get the filters that should be applied to a database query.  This method
     * is around because some filters are a composite of multiple filters.
     *
     * @return a <code>List</code> value containing the
     * {@link net.project.base.finder.FinderFilter} which the user has selected
     * for inclusion in the SQL statement of a finder.  This list may be empty
     * if the user has not selected the filter.
     */
    public List getSelectedFilters() {
        if (isSelected()) {
            ArrayList thisFilter = new ArrayList();
            thisFilter.add(this);
            return thisFilter;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Get a description of what this filter does.  For example, if it filters
     * tasks assigned to me, this string would say "Tasks Assigned To Me".  If
     * it filtered on date range, this method should return "Tasks with finish
     * dates between 01/02/2003 and 01/02/2005"
     *
     * @return a <code>String</code> containing a human-readable description of
     * what the filter does.
     */
    public abstract String getFilterDescription();

    /**
     * Clears the modifiable properties of this FinderFilter and deselects it.
     * This is commonly called before populating the FinderFilter.
     */
    public void clear() {
        setSelected(false);
        clearProperties();
    }

    /**
     * Clears the modifiable properties of this FinderFilter.
     * Invokes when {@link #clear} is invoked.
     */
    protected abstract void clearProperties();

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitFinderFilter(this);
    }

    /**
     * Indicates whether the specified id is unavailable for use by another filter.
     * An id is unavailable if there is a filter with matching ID already.
     * Uses {@link #matchingID} to locate a filter with matching ID.
     *
     * @param id the id to check
     * @return true if the id is in use by this filter or any filter in this list;
     * false otherwise
     */
    protected final boolean isUnavailable(String id) {
        return (matchingID(id) != null);
    }

    /**
     * Returns the filter matching the specified id.
     * If this filter is not a composite filter and this filter's ID matches
     * the specified ID, then this filter is returned.
     * Otherwise, composite filters are checked and any matching filter is
     * returned.
     * @param id the is of the filter to match
     * @return the matching filter, or <code>null</code> if this nor any
     * contained filter matches the id
     */
    protected FinderFilter matchingID(String id) {
        FinderFilter matchingFilter = null;

        if (getID().equals(id)) {
            matchingFilter = this;
        }

        return matchingFilter;
    }

}
