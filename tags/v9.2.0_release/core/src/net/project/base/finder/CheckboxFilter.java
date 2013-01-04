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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.base.finder;

import net.project.util.VisitException;

/**
 * A <code>CheckboxFilter</code> is a contain that holds another filter and
 * decides whether it is selected by displaying a checkbox.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class CheckboxFilter extends FinderFilter {
    /** This is the filter that is being wrapped by the checkbox filter. */
    private FinderFilter enclosedFilter = null;

    /**
     * Standard constructor.
     *
     * @param id a unique identifier for this checkbox.  Every filter requires a
     * unique identifier so that when they are rendered on the interface, they
     * can all have a separate name.
     * @param enclosedFilter the <code>FinderFilter</code> that this checkbox is
     * wrapping.
     */
    public CheckboxFilter(String id, FinderFilter enclosedFilter) {
        super(id, null);
        this.enclosedFilter = enclosedFilter;
    }

    /**
     * Get a where clause for this filter that can be applied to a finder.  This
     * where clause will be empty (not null) if the filter isn't selected.
     *
     * @return a <code>String</code> containing a where clause, or an empty
     * string if the filter isn't selected.
     */
    public String getWhereClause() {
        String whereClause = "";

        if ((isSelected()) && (enclosedFilter.isSelected())) {
            whereClause = enclosedFilter.getWhereClause();
        }

        return whereClause;
    }

    /**
     * Get a description of what this filter does.  For example, if it filters
     * tasks assigned to me, this string would say "Tasks Assigned To Me".  If
     * it filtered on date range, this method should return "Tasks with finish
     * dates between 01/02/2003 and 01/02/2005"
     *
     * @return a <code>String</code> value describing the filter being applied.
     */
    public String getFilterDescription() {
        return enclosedFilter.getFilterDescription();
    }

    protected void clearProperties() {
        // No properties to clear
    }

    /**
     * Get the filter that this checkbox is wrapping.
     *
     * @return a <code>FinderFilter</code> object which this CheckboxFilter is
     * wrapping.
     */
    public FinderFilter getEnclosedFilter() {
        return enclosedFilter;
    }

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitCheckboxFilter(this);
    }
}
