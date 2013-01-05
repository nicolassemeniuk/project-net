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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.ColumnFilter;
import net.project.base.finder.FilterComparator;
import net.project.base.finder.IFinderIngredientVisitor;
import net.project.base.finder.SimplePatternComparator;
import net.project.base.property.PropertyProvider;
import net.project.project.ProjectSpace;
import net.project.util.VisitException;

/**
 * Provides a filter for filtering on selected projects.
 * The filter is based on a column and a selection made from a collection of
 * projects.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class SelectedProjectsFilter extends ColumnFilter {
    // if true then other filters will be ignored and the result will contain only the selected projects
    private boolean ignoreOtherFilters = false;

    /**
     * The <code>ProjectSpace</code>s which may be selected for filtering
     */
    private final List projectSpaceList = new ArrayList();

    /**
     * The set of selected project spaces.
     * This set is a subset of project spaces in projectSpaceList.
     */
    private final Set selectedProjectSpaceSet = new HashSet();

    /**
     * Creates a new SelectedProjectsFilter for specified project spaces.
     *
     * @param id the id of this filter
     * @param columnDefinition the column to use in the where clause; it
     * is assumed that this column contains project IDs in the database
     * to which this filter applies
     * @param projectSpaces the collection of <code>ProjectSpace</code>s
     * on which filtering is possible
     */
    public SelectedProjectsFilter(String id, ColumnDefinition columnDefinition, Collection projectSpaces) {
        super(id, columnDefinition, false);
        this.projectSpaceList.addAll(projectSpaces);
    }


    public boolean isIgnoreOtherFilters() {
        return ignoreOtherFilters;
    }

    public void setIgnoreOtherFilters(boolean ignoreOtherFilters) {
        this.ignoreOtherFilters = ignoreOtherFilters;
    }

    /**
     * Returns all <code>ProjectSpace</code>s in this filter.
     * The selected project IDs must come from this ProjectSpace.
     * This filter is inherently insecure: it is up to the using class to
     * ensure the projects available for selection or the selected projects
     * do not violate security.
     *
     * @return the collection where each element is a <code>ProjectSpace</code>.
     */
    public Collection getAllProjectSpaces() {
        return Collections.unmodifiableList(projectSpaceList);
    }

    /**
     * Returns selected <code>ProjectSpace</code>s in this filter.
     * This filter is inherently insecure: it is up to the using class to
     * ensure the projects available for selection or the selected projects
     * do not violate security.
     *
     * @return the collection where each element is a <code>ProjectSpace</code>.
     */
    public Collection getSelectedProjectSpaces() {
        return Collections.unmodifiableSet(selectedProjectSpaceSet);
    }

    /**
     * Sets selected project space IDs to those specified.
     * After calling, {@link #getSelectedProjectSpaces} will return a collection
     * of <code>ProjectSpace</code>s where each <code>ProjectSpace</code>'s ID will correspond to an ID
     * in the specified array.
     * @param projectSpaceIDs the array of project space IDs to filter on
     */
    public void setSelectedProjectSpaceIDs(String[] projectSpaceIDs) {
        this.selectedProjectSpaceSet.clear();

        // Make a set from the array of selected project space IDs
        // This makes it easier to lookup values
        Set selectedProjectIDs = new HashSet(Arrays.asList(projectSpaceIDs));

        // Iterate over all project spaces, checking to see if each one's ID
        // is in the set of selected IDs
        // If so, then add that ProjectSpace to the set of selected Project Spaces.
        for (Iterator it = getAllProjectSpaces().iterator(); it.hasNext(); ) {
            ProjectSpace nextProjectSpace = (ProjectSpace) it.next();

            if (selectedProjectIDs.contains(nextProjectSpace.getID())) {
                this.selectedProjectSpaceSet.add(nextProjectSpace);
            }

        }
    }


    /**
     * Indicates whether the project space with the specified ID has been selected
     * in this filter.
     * @param projectSpaceID the id of the project space to check for
     * @return true if the project space with that ID has been selected;
     * false otherwise
     */
    public boolean isProjectSpaceSelected(String projectSpaceID) {
        boolean isSelected = false;

        for (Iterator it = getSelectedProjectSpaces().iterator(); it.hasNext() && !isSelected; ) {
            ProjectSpace nextProjectSpace = (ProjectSpace) it.next();
            if (nextProjectSpace.getID().equals(projectSpaceID)) {
                isSelected = true;
            }
        }

        return isSelected;
    }

    /**
     * Get a where clause for this filter that can be applied to a finder.
     * This where clause will be empty (not null) if the filter isn't selected.
     * The where clause limits the specified column to contain values within
     * the set of selected project space IDs.
     *
     * @return a <code>String</code> containing a where clause, or an empty
     * string if the filter isn't selected.
     */
    public String getWhereClause() {
        String toReturn;

        if (!isSelected()) {
            toReturn = "";

        } else {

            if (getSelectedProjectSpaces().isEmpty()) {
                // BFD-2042; 3/24/2004 - Tim
                // No selected project spaces previously resulted in an error
                // We will simply ignore the filter in that case
                toReturn = "";

            } else {

                // Create comma-separated list of IDs
                StringBuffer commaSeparatedIDs = new StringBuffer();
                boolean isAfterFirst = false;

                for (Iterator it = getSelectedProjectSpaces().iterator(); it.hasNext(); ) {
                    ProjectSpace nextProjectSpace  = (ProjectSpace) it.next();

                    if (isAfterFirst) {
                        commaSeparatedIDs.append(", ");
                    }

                    commaSeparatedIDs.append(nextProjectSpace.getID());
                    isAfterFirst = true;
                }


                // Create the where clause
                toReturn = SelectedProjectsComparator.DEFAULT.createWhereClause(
                        getColumnDefinition().getColumnName(),
                        new String[]{commaSeparatedIDs.toString()},
                        false);

            }


        }

        return toReturn;
    }

    /**
     * Get a description of what this filter does.  For example, if it filters
     * tasks assigned to me, this string would say "Tasks Assigned To Me".  If
     * it filtered on date range, this method should return "Tasks with finish
     * dates between 01/02/2003 and 01/02/2005"
     *
     * @return a <code>String</code> value describing this filter.
     */
    public String getFilterDescription() {
        return PropertyProvider.get("prm.global.finder.selectedprojectsfilter.description");
    }

    /**
     * Clears the selected projects.
     */
    protected void clearProperties() {
        this.selectedProjectSpaceSet.clear();
        ignoreOtherFilters = false;
    }

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitSelectedProjectsFilter(this);
    }

    /**
     * A comparator is simply used for convenience to construct the where clause.
     * This one doesn't support a null comparison pattern.
     */
    private static class SelectedProjectsComparator extends SimplePatternComparator {

        private static final FilterComparator DEFAULT = new SelectedProjectsComparator("default", "", "{0} in ({1})", "");

        public SelectedProjectsComparator(String id, String displayToken, String comparisonPattern, String nullComparisonPattern) {
            super(id, displayToken, comparisonPattern, nullComparisonPattern);
        }

    }

    /**
     *
     * @param s string to check, id of the space
     * @return true if s is in the list of selected project spaces list, false otherwise
     */
    public boolean matches(String s) {
        if (s == null || "".equals(s)) {
            return isEmptyOptionSelected();
        }
        for (Object o : getSelectedProjectSpaces()) {
            ProjectSpace projectSpace = (ProjectSpace) o;
            if (s.equals(projectSpace.getID()))
                return true;
        }
        return false;
    }
}

