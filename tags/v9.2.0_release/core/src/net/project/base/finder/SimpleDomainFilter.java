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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Provides a domain filter based on an array of domain options.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class SimpleDomainFilter extends DomainFilter {

    /**
     * All available domain options.
     */
    private final List domainOptions = new ArrayList();

    /**
     * Creates a new SimpleDomainFilter on the specified column assuming it contains
     * numberic ID values.
     *
     * @param id a <code>String</code> which uniquely identifies this filter
     * among all filters created for this object.
     * @param columnDefinition a {@link ColumnDefinition}
     * object which identifies which database column we are going to do database
     * operations to.
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional fields); false
     * if not (for mandatory fields)
     * @param options a <code>DomainOption</code> array of items that will appear
     * in the option list.
     */
    public SimpleDomainFilter(String id, ColumnDefinition columnDefinition, boolean isIncludeEmptyOption, DomainOption[] options) {
        this(id, columnDefinition, isIncludeEmptyOption, options, false);
    }

    /**
     * Creates a new SimpleDomainFilter on the specified column.
     *
     * @param id a <code>String</code> which uniquely identifies this filter
     * among all filters created for this object.
     * @param columnDefinition a {@link ColumnDefinition}
     * object which identifies which database column we are going to do database
     * operations to.
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional fields); false
     * if not (for mandatory fields)
     * @param options a <code>DomainOption</code> array of items that will appear
     * in the option list.
     * @param isStringDomain true if the column filtered on contains string
     * values rather than numeric IDs; this affects the SQL statement generated
     */
    public SimpleDomainFilter(String id, ColumnDefinition columnDefinition, boolean isIncludeEmptyOption, DomainOption[] options, boolean isStringDomain) {
        super(id, columnDefinition, isIncludeEmptyOption, isStringDomain);
        this.domainOptions.addAll(Arrays.asList(options));
    }

    /**
     * Returns the domain options collection.
     *
     * @return an unmodifiable collection where each element is a <code>DomainOption</code>
     */
    public Collection getAllDomainOptions() {
        return Collections.unmodifiableList(this.domainOptions);
    }

}
