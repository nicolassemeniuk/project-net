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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.portfolio.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DomainComparator;
import net.project.base.finder.DomainFilter;
import net.project.base.finder.DomainOption;
import net.project.business.BusinessSpace;
import net.project.persistence.PersistenceException;
import net.project.portfolio.BusinessPortfolio;

/**
 * Provides a domain filter based on businesses which may own a project.
 *
 * @author Tim Morrow
 * @since 7.4
 */
public class OwnedBusinessFilter extends DomainFilter {

    /**
     * The user's business portfolio which provides the businesses for
     * selection.
     */
    private final BusinessPortfolio businessPortfolio;

    /**
     * Creates a new OwnedBusinessFilter on the specified column.
     * The businesses in the portfolio are not loaded until needed.
     *
     * @param id a <code>String</code> which uniquely identifies this filter
     * among all filters created for this object.
     * @param columnDefinition a {@link ColumnDefinition}
     * object which identifies which database column we are going to do database
     * operations to.
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional fields); false
     * if not (for mandatory fields)
     * @param businessPortfolio the user's portfolio of businesses used
     * to provide the selection list of domain options
     */
    public OwnedBusinessFilter(String id, ColumnDefinition columnDefinition, boolean isIncludeEmptyOption, BusinessPortfolio businessPortfolio) {
        super(id, columnDefinition, isIncludeEmptyOption, false);
        this.businessPortfolio = businessPortfolio;
    }

    /**
     * Returns an unmodifiable ordered collection of the domain options
     * for this filter, constructed from the businesses in the business
     * portfolio.
     *
     * @return the collection where each element is a <code>DomainOption</code>
     */
    public Collection getAllDomainOptions() {

        if (!businessPortfolio.isEntriesLoaded()) {
            try {
                businessPortfolio.load();

            } catch (PersistenceException e) {
                // No businesses
            }
        }

        return makeDomainOptions(businessPortfolio);
    }

    /**
     * Makes a collection of <code>DomainOption</code>s from the collection of
     * business spaces.  Each value is the business space ID and the display
     * part is the business space name.
     * @param businessSpaces the business spaces to build as domain options
     * @return the collection of DomainOptions based on the business space IDs and
     * names
     */
    private Collection makeDomainOptions(Collection businessSpaces) {

        List domainOptions = new ArrayList();

        for (Iterator it = businessSpaces.iterator(); it.hasNext(); ) {
            BusinessSpace nextBusinessSpace = (BusinessSpace) it.next();

            DomainOption domainOption = new DomainOption(nextBusinessSpace.getID(),
                    nextBusinessSpace.getName(),
                    true);

            domainOptions.add(domainOption);
        }

        return Collections.unmodifiableList(domainOptions);
    }

    public String getWhereClause() {
        return "";
    }

    /**
     *
     * @param s string to check
     * @return true if s is in the list of selected domain options, false otherwise
     */
    public boolean matches(String s) {
        if (s == null || "".equals(s)) {
            return isEmptyOptionSelected();
        }
        boolean equals = this.getComparator().getID().equals(DomainComparator.EQUALS.getID());
        for (Object o : getSelectedDomainOptions()) {
            DomainOption domainOption = (DomainOption) o;
            if (s.equals(domainOption.getValue()))
                return equals;
        }
        return !equals;
    }
}

