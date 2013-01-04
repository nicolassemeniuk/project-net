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
package net.project.form.report;

import net.project.base.finder.FinderFilter;
import net.project.base.finder.IFinderIngredientVisitor;
import net.project.form.FieldFilter;
import net.project.form.FormField;
import net.project.util.VisitException;

/**
 * Filter class to wrap a {@link net.project.form.FieldFilter}.  The
 * <code>FieldFilter</code> is the form module's equivalent to a FinderFilter.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FormFieldFilter extends FinderFilter {
    /**
     * The field that we are going to allow the application user to filter
     * data.
     */
    private FormField formField;
    /**
     * This data structure is forms equivalent of a FinderFilter.  It supplies
     * the actual mechanism to create a SQLStatement and to store the data.
     */
    private FieldFilter fieldFilter;

    /**
     * Standard constructor which sets up the filter.
     *
     * @param id a <code>String</code> value which serves as a unique identifier
     * for this filter when it appears in a list.
     * @param nameToken a <code>String</code> value which is used to look up a
     * name for this token.
     * @param formField a <code>FormField</code> value which we are filtering on.
     */
    public FormFieldFilter(String id, String nameToken, FormField formField) {
        super(id, nameToken);
        this.formField = formField;
    }

    /**
     * Get a where clause for this filter that can be applied to a finder.  This
     * where clause will be empty (not null) if the filter isn't selected.
     *
     * @return a <code>String</code> containing a where clause, or an empty
     * string if the filter isn't selected.
     */
    public String getWhereClause() {
        String toReturn;

        if (fieldFilter == null) {
            toReturn = null;
        } else {
            toReturn = formField.getFilterSQL(fieldFilter, "");

            //Trim around the where clause
            toReturn = toReturn.trim();

            if (toReturn.equals("()")) {
                toReturn = null;
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
     * @return a <code>String</code> containing a human-readable description of
     * what the filter does.
     */
    public String getFilterDescription() {
        String description;

        if (fieldFilter == null) {
            description = "";
        } else {
            description = fieldFilter.toString();
        }

        return description;
    }

    /**
     * Clears the field filter.
     */
    protected void clearProperties() {
        setFieldFilter(null);
    }

    /**
     * Get the form field that this filter is allowing us to filter data on.
     *
     * @return a <code>FormField</code> on which we are allowing the user to
     * filter data.
     */
    public FormField getFormField() {
        return formField;
    }

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitFormFieldFilter(this);
    }

    public void setFieldFilter(FieldFilter fieldFilter) {
        this.fieldFilter = fieldFilter;
    }

}
