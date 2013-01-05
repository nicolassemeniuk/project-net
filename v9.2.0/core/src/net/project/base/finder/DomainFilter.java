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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.util.VisitException;

/**
 * Provides a filter that filters a column based on or more selections from a
 * domain of values.
 *
 * @author Matthew Flower
 * @author Tim Morrow
 * @since Version 7.4
 */
public abstract class DomainFilter extends ColumnFilter {

    /**
     * A comparator that the user selected.  (Or the default if the user hasn't
     * selected a comparator.
     */
    private DomainComparator comparator = null;

    /**
     * The selected domain options.
     */
    private List selectedDomainOptions = new ArrayList();

    /**
     * Indicates whether this domain filter is based on string values; this
     * affects the SQL statement generated.
     */
    protected final boolean isStringDomain;

    /**
     * Creates a new DomainFilter on the specified column.
     *
     * @param id a <code>String</code> which uniquely identifies this filter
     * among all filters created for this object.
     * @param columnDefinition a {@link ColumnDefinition}
     * object which identifies which database column we are going to do database
     * operations to.
     * @param isIncludeEmptyOption true if this filter should provide an option
     * to include empty values in the results (for optional fields); false
     * if not (for mandatory fields)
     * @param isStringDomain true if the column filtered on contains string
     * values rather than numeric IDs; this affects the SQL statement generated
     */
    public DomainFilter(String id, ColumnDefinition columnDefinition, boolean isIncludeEmptyOption, boolean isStringDomain) {
        super(id, columnDefinition, isIncludeEmptyOption);
        this.isStringDomain = isStringDomain;
    }

    /**
     * Returns an unmodifiable ordered collection of the domain options
     * for this filter.
     * Sub-classes should override this method to return the available
     * domain options.
     *
     * @return the collection where each element is a <code>DomainOption</code>;
     * Includes the empty option if it was indicated that one was required
     */
    public abstract Collection getAllDomainOptions();

    /**
     * Sets the selected domain options based on the specified array of
     * selected values.
     * <p>
     * If no domain option is found with a value equal to one in the array then
     * that value is ignored.  If no domain options are found for any of the
     * values then no options are selected.
     * </p>
     * @param selectedOptionValues the array of values to select;
     * if this filter has a <code>DomainOption</code> with a value matching one of the
     * selectedOptionValues, then that <code>DomainOption</code> is added to the collection
     * of selected domain options
     */
    public void setSelectedDomainOptions(String[] selectedOptionValues) {

        List selectedValueList = Arrays.asList(selectedOptionValues);
        for (Iterator it = getAllDomainOptions().iterator(); it.hasNext(); ) {
            DomainOption nextOption = (DomainOption) it.next();

            // If the selected values contains the value of the current option
            // Then add the current option to the selected list
            if (selectedValueList.contains(nextOption.getValue())) {
                this.selectedDomainOptions.add(nextOption);
            }
        }
    }

    /**
     * Sets the selected domain options based on the list of selected values.
     * Each item in the list needs to be an object of type <code>DomainOption</code>.
     *
     * @param domainOptions the list of values to select.
     */
    public void setSelectedDomainOptions(List domainOptions) {
        this.selectedDomainOptions = domainOptions;
    }

    /**
     * Returns an unmodifiable ordered collection of the domain options
     * which have been selected.
     * @return the collection where each element is a <code>DomainOption</code>
     */
    public Collection getSelectedDomainOptions() {
        return Collections.unmodifiableList(this.selectedDomainOptions);
    }

    /**
     * Specifies the comparator to use with when generating the where clause
     * for this DomainFilter.
     * @param comparator the <code>DomainComparator</code> to use
     */
    public void setComparator(DomainComparator comparator) {
        this.comparator = comparator;
    }

    /**
     * Returns the currently selected comparator of this filter.
     * @return the comparator or null if none has been selected
     */
    public FilterComparator getComparator() {
        return this.comparator;
    }

    /**
     * Get a where clause for this filter that can be applied to a finder.
     * <p>
     * This where clause will be empty (not null) if the filter isn't selected.
     * Similarly, if no domain options are selected, it will behave as if the
     * filter is not selected.
     * </p>
     *
     * @return a <code>String</code> containing a where clause, or an empty
     * string if the filter isn't selected.
     */
    public String getWhereClause() {
        String toReturn;

        // 2004/03/05 - Tim
        // There is an issue whereby it is possible to have no selected domain
        // options, despite the filter being selected
        // This can occur when a previously available (and selected) domain option
        // is removed from the candidate of domain options; that is, setSelectedDomainOptions
        // specifies a collection of options that cannot be located in the domain options

        if (!isSelected() || getSelectedDomainOptions().isEmpty()) {
            toReturn = "";

        } else {

            // Convert the selected options into an list of String values
            List selectionOptionValueList = new ArrayList();
            for (Iterator it = getSelectedDomainOptions().iterator(); it.hasNext(); ) {
                DomainOption nextOption = (DomainOption) it.next();
                selectionOptionValueList.add(nextOption.getValue());
            }

            // Create the DomainComparator where clause
            toReturn = this.comparator.createWhereClause(
                    getColumnDefinition().getColumnName(),
                    (String[]) selectionOptionValueList.toArray(new String[]{}),
                    isEmptyOptionSelected(),
                    this.isStringDomain);

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

        return PropertyProvider.get("prm.global.finder.domainfilter.description",
            new String[] {getName(), comparator.getSymbol(), this.selectedDomainOptions.toString()});

    }

    /**
     * Clears the comparator and empties the selected domain options.
     */
    protected void clearProperties() {
        setComparator(null);
        this.selectedDomainOptions.clear();
    }

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitDomainFilter(this);
    }
}
