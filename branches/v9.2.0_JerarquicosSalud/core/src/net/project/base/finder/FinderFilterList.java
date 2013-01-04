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

 package net.project.base.finder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import net.project.util.VisitException;

/**
 * A collection of FinderFilters that produced a SQL WHERE clause based on
 * a FilterOperator.
 * Note that this is also a FinderFilter to allow nested FinderFilterLists.
 *
 * @author Matthew Flower
 * @author Tim Morrow
 * @since Version 7.4
 */
public class FinderFilterList extends FinderFilter implements Cloneable, Serializable {

    /**
     * The operator to use to build the where clause for the filters in
     * this list.
     */
    private final FilterOperator operator;

    /**
     * The internal list of filters used by this object.  We don't extend
     * from an <code>ArrayList</code> so we can perform additional handling
     * when someone adds a new filter to the filter list.
     */
    private LinkedHashMap filterList = new LinkedHashMap();

    /**
     * Creates a new FinderFilterList with an AND operator.
     */
    public FinderFilterList() {
        this(FilterOperator.AND);
    }

    /**
     * Creates a new finder filter list object which is prepopulated based on an
     * existing list of filters that are stored in a standard java
     * java.util.List.
     *
     * @param finderFilters a <code>java.util.List</code> containing only
     * FinderFilterList objects.
     * @param selected a <code>boolean</code> value indicating whether or not
     * the list should be set as selected by default.  This will only affect the
     * finder filter list itself, it has no affect on the filters contained in
     * this list.
     */
    public FinderFilterList(List finderFilters, boolean selected) {
        this(FilterOperator.AND);
        setSelected(selected);

        for (Iterator it = finderFilters.iterator(); it.hasNext();) {
            FinderFilter finderFilter = (FinderFilter) it.next();
            filterList.put(finderFilter.getID(), finderFilter);
        }
    }

    /**
     * Creates a new FinderFilterList with specified operator.
     * @param operator the filter operator to use to construct the where clause
     */
    public FinderFilterList(FilterOperator operator) {
        this("", operator);
    }

    /**
     * Creates a new FinderFilterList with an ID and with specified operator.
     * @param id the ID of this filter
     * @param operator the filter operator to use to construct the where clause
     */
    public FinderFilterList(String id, FilterOperator operator) {
        super(id, "");
        this.operator = operator;
    }

    /**
     * Add a new filter to the list of filters.
     *
     * @param filter a <code>FinderFilter</code> object which should be added to
     * the internal list of filters.
     * @throws DuplicateFilterIDException if there is already a filter in the
     * list that contains this same ID; this checks all filters in the list,
     * including other FilterLists
     */
    public void add(FinderFilter filter) throws DuplicateFilterIDException {
        //Check to make sure that a filter with this ID hasn't already been added
        if (filter.getID() != null && filter.getID().length() > 0) {

            if (filterList.containsKey(filter.getID())) {
                throw new DuplicateFilterIDException("There is already a filter "+
                    "with the ID "+filter.getID()+" in this filter list.  This exception" +
                    "has been thrown to protect you from bad side effects that "+
                    "could occur when you have two filters with the same ID.");
            }

        }

        filterList.put(filter.getID(), filter);
    }

    /**
     * Remove all <code>FinderFilter</code> objects from the internal list of
     * finder filters.
     */
    public void clear() {
        filterList.clear();
    }

    /**
     * Return a pointer to the internal list of filters.
     *
     * @return a <code>Iterator</code> object pointing to the list of filters.
     */
    public List getAllFilters() {
        return new ArrayList(filterList.values());
    }

    /**
     * Get all of the filters that have the "selected" property set to true.
     * This normally corresponds to the filters that the user has selected in a
     * query screen.
     * <p>
     * <b>Note:</b> This descends through all filters in this list; any
     * composite filters (other filter lists) have their individual element
     * filters added to the list.
     *
     * @return a <code>Iterator</code> object that points to all of the Filter
     * objects that have been selected.
     */
    public List getSelectedFilters() {
        ArrayList selectedFilters = new ArrayList();

        Iterator it = getAllFilters().iterator();
        while (it.hasNext()) {
            FinderFilter currentFilter = (FinderFilter)it.next();
            selectedFilters.addAll(currentFilter.getSelectedFilters());
        }

        return selectedFilters;
    }

    /**
     * Accept a visitor who will visit every filter in this list.
     *
     * @param visitor
     */
    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitFinderFilterList(this);
    }

    /**
     * Return the total number of items (selected or not) that appear in this
     * filter list.
     *
     * @return an <code>int</code> value containing the number of items that
     * appear in this filter list.
     */
    public int size() {
        return filterList.size();
    }

    //
    // Extending FinderFilter
    //

    /**
     * Returns the where clause for this list of <code>FinderFilter</code>s.
     * The where clause is constructed by combining all <code>FinderFilter</code>s in the
     * list with the <code>FilterOperator</code> specified during initialization.
     *
     * @return the where clause or the empty string if no filters were selected
     */
    public String getWhereClause() {
        boolean foundOneFilter = false;

        StringBuffer result = new StringBuffer();
        boolean isAfterFirst = false;

        // We iterate over all filters since a filter will only return
        // a where clause if it is itself selected
        for (Iterator it = getAllFilters().iterator(); it.hasNext(); ) {
            FinderFilter nextFilter = (FinderFilter) it.next();

            String whereClause = nextFilter.getWhereClause();
            if (whereClause != null && whereClause.trim().length() > 0) {

                foundOneFilter = true;

                // For second and subsequent filters, ensure they are
                // joined with filter operator
                if (isAfterFirst) {
                    result.append(" ").append(this.operator.getSQLOperator()).append(" ");
                }

                // Now add where clause of the next filter
                result.append(whereClause);
                isAfterFirst = true;

            }

        }

        // If we found at least one filter, then wrap entire clause in parenthesis
        // to ensure its clauses are evaluated with the correct precedence
        // Otherwise, we leave the result empty
        if (foundOneFilter) {
            result.insert(0, "(").append(")");
        }

        return result.toString();
    }

    public String getFilterDescription() {
        return getName();
    }

    protected void clearProperties() {
        // Nothing to do
    }

    /**
     * Returns the filter matching the specified id.
     * Checks all contained FinderFilters.
     * @param id the is of the filter to match
     * @return the matching filter, or <code>null</code> if this nor any                                                                                 1
     * contained filter matches the id
     */
    protected FinderFilter matchingID(String id) {
        FinderFilter matchingFilter = null;

        if (getID().equals(id)) {
            matchingFilter = this;
        } else {

            // Iterate over all filters in this list, stopping when a filter
            // matching the id is found
            for (Iterator it = getAllFilters().iterator(); it.hasNext() && matchingFilter == null; ) {
                FinderFilter nextFilter = (FinderFilter) it.next();
                matchingFilter = nextFilter.matchingID(id);
            }

        }

        return matchingFilter;
    }

    /**
     * Replaces any existing filter in the finder filter list.
     *
     * @param id a <code>String</code> which contains the unique identifier for
     * this finder filter.
     * @param filterToAdd a <code>FinderFilter</code> to add to the list.
     */
    public void replace(String id, FinderFilter filterToAdd) {
        filterList.put(id, filterToAdd);
    }

    /**
     * Removes a specified filter from the finder filter list.
     *
     * @param id a <code>String</code> containing the id     of the finder filter
     * we wish to remove from the list.
     */
    public void remove(String id) {
        filterList.remove(id);
    }

    /**
     * Get the finder filter which corresponds to this identifier.
     *
     * @param id a <code>String</code> which uniquely identifies a finder
     * filter.
     * @return a <code>FinderFilter</code> which corresponds to the supplied id.
     */
    public FinderFilter get(String id) {
        return (FinderFilter)filterList.get(id);
    }

    public FinderFilter deepSearch(String id) {
        FinderFilter filter = get(id);
        if (filter == null) {
            for (Iterator it = filterList.values().iterator(); it.hasNext();) {
                FinderFilter finderFilter = (FinderFilter)it.next();
                if (finderFilter instanceof FinderFilterList) {
                    filter = ((FinderFilterList)finderFilter).deepSearch(id);
                    if (filter != null) {
                        break;
                    }

                }
            }
        }

        return filter;
    }

    public Object clone() {
        FinderFilterList clone = new FinderFilterList(operator);
        clone.filterList = (LinkedHashMap)filterList.clone();

        return clone;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FinderFilterList)) return false;

        final FinderFilterList finderFilterList = (FinderFilterList) o;

        if (filterList != null ? !filterList.equals(finderFilterList.filterList) : finderFilterList.filterList != null) return false;
        if (operator != null ? !operator.equals(finderFilterList.operator) : finderFilterList.operator != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (operator != null ? operator.hashCode() : 0);
        result = 29 * result + (filterList != null ? filterList.hashCode() : 0);
        return result;
    }
}
