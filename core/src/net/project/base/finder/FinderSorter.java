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
|   $Revision: 20910 $
|       $Date: 2010-06-03 10:45:19 -0300 (jue, 03 jun 2010) $
|     $Author: ritesh $
|
+-----------------------------------------------------------------------------*/
package net.project.base.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.util.VisitException;

/**
 * A finder sorter represents a "where clause" to a finder object.  These multiple
 * <code>FinderSorter</code objects can be added to a <code>Finder</code> to
 * change the order with which the records are outputted.
 * <p>
 * A <code>FinderSorter</code> is final since we require the ability to persist
 * it as XML and do not permit sub-classes from adding additional behavior.
 * </p>
 *
 * @see net.project.base.finder.Finder
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FinderSorter {
    /**
     * The human-readable String used to identify a sorting sorted from lowest
     * value to highest value.
     */
    public String ASCENDING = PropertyProvider.get("prm.global.finder.findersorter.ascending"); //"Ascending";
    /**
     * The human-readable String used to identify a sorting sorted from highest
     * value to lowest value.
     */
    public String DESCENDING = PropertyProvider.get("prm.global.finder.findersorter.descending"); //"Descending";
    /**
     * Should this operation generate a order by clause for a finder.  This is
     * used by a FinderOperationList when getting the list of selected sorters.
     */
    private boolean selected = false;
    /**
     * The name of the selected column.
     */
    private ColumnDefinition selectedColumn;
    /**
     * A unique identifier for this operation.  This is important so query pages
     * can figure out which finder operation a field relates to.
     */
    private String id;
    /**
     * Indicates a database column name that is necessary for constructing the
     * where clause or order by clause.  This might not be used by all filters,
     * but all of them have used it so far, so it seems worthwhile.
     */
    private ArrayList columns = new ArrayList();
    /**
     * Indicates whether the order by clause should sort the column in descending
     * order.
     */
    private boolean isDescending = false;

    /**
     * Standard constructor which creates a <code>FinderSorter</code> object.
     *
     * @param id a <code>String</code> which uniquely identifies this
     * <code>FinderSorter</code>.
     */
    public FinderSorter(String id) {
        this.id = id;
    }

    /**
     * Constructor to make a single finder sorter that will be added to a finder
     * to sort the result set.
     *
     * Note that by using this constructor, you will automatically be selecting
     * the finder sorter.
     *
     * @param column a <code>ColumnDefinition</code> that defines which column
     * will be sorted.
     * @param isDescending true to sort descending, that is from highest value to lowest value;
     * false to sort ascending, from lowest value to highest value
     */
    public FinderSorter(ColumnDefinition column, boolean isDescending) {
        this("", new ColumnDefinition[]{column}, column);
        this.id = String.valueOf(columns.size()*10);
        this.isDescending = isDescending;
        this.selected = true;
    }

    /**
     * Constructor which predefines the columns used by this sorter.
     *
     * @param id a <code>String</code> value that unique identifies this sorter.
     * This is used in generated HTML and in HTML processing so the code can
     * determine which sorter a user is selecting information about.
     * @param columns a <code>ColumnDefinition</code> array that contains all of
     * the columns that should be displayed for sorting.
     * @param defaultColumn a <code>ColumnDefinition</code> value that should be
     * selected by default.  This value must be in the list of columns provided
     * to this method.
     */
    public FinderSorter(String id, ColumnDefinition[] columns, ColumnDefinition defaultColumn) {
        this.columns.addAll(Arrays.asList(columns));
        this.id = id;
        this.selectedColumn = defaultColumn;
    }

    /**
     * Get the order by clause for this operation.  This is effectively just the
     * column name with "desc" if necessary.  This method is used by a finder to
     * construct a SQL statement.
     *
     * @return a <code>String</code> value indicating the order by clause.
     */
    public String getOrderByClause() {
        String toReturn = "";

        if ((selected) && (selectedColumn != null)) {
            toReturn = selectedColumn.getColumnName();

            if (isDescending) {
                toReturn += " desc";
            }
        }

        return toReturn;
    }

    /**
     * Add a column to the list of available columns that the user can select
     * for sorting.
     *
     * @param column a <code>ColumnDefinition</code> that the user can choose
     * to sort with.
     */
    public void addColumn(ColumnDefinition column) {
        columns.add(column);
    }

    /**
     * Add all of the columns in the <code>List</code> parameter to the internal
     * column list.  All of the items in the list are assumed to be of type
     * {@link net.project.base.finder.ColumnDefinition}.  An error will occur if
     * all items in the list are not of this type.
     *
     * @param columns a <code>List</code> value that contains column definitions
     * to be added to the internal list of columns that will be used to create a
     * selection list of columns for the user.
     */
    public void addColumnList(List columns) {
        columns.addAll(columns);
    }

    /**
     * Get the list of columns that will be used to construct a selection list
     * of columns.
     *
     * @return a <code>List</code> value containing zero or more
     * {@link net.project.base.finder.ColumnDefinition} objects.
     */
    protected List getColumnList() {
        return columns;
    }

    /**
     * Remove all of the column definitions that are being stored internally.
     */
    public void clearColumnList() {
        columns.clear();
    }

    /**
     * Get a value that will uniquely identify this finder sorter.
     *
     * @return a <code>String</code> value that uniquely identifies this
     * <code>FinderSorter</code>
     */
    public String getID() {
        return id;
    }

    /**
     * Determine whether this sorter will be applied to a finder.
     *
     * @return a <code>boolean</code> value that indicates whether this sorter
     * will be be applied to a finder.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Indicate whether this sorter will be applied to a finder.
     *
     * @param selected a <code>boolean</code> value which indicates whether this
     * sorter will be applied to a finder.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Sets the selected column to the column with the same ID as specified.
     * If no columns match the specified ID, no column is selected.
     * @param columnID the ID of the column to select
     */
    void setSelectedColumnByID(String columnID) {

        // Iterate through the columns to find the column that matches this ID
        selectedColumn = null;
        for (Iterator it = columns.iterator(); it.hasNext(); ) {
            ColumnDefinition col = (ColumnDefinition) it.next();

            if (col.getID().equals(columnID)) {
                selectedColumn = col;
                break;
            }
        }

    }
    /**
     * Sets the selected column to the column with the same name as specified.
     * If no columns match the specified name, no column is selected.
     * @param columnName the name of the column to select
     */
    public void setSelectedColumnByName(String columnName) {

        //Iterate through the columns to find the column that matches this ID
        selectedColumn = null;
        for (Iterator it = columns.iterator(); it.hasNext(); ) {
            ColumnDefinition col = (ColumnDefinition)it.next();

            if (col.getColumnName().equals(columnName)) {
                selectedColumn = col;
                break;
            }
        }

    }

    /**
     * Return the ColumnDefinition which corresponds to the column that the
     * user has selected.
     *
     * @return a <code>ColumnDefinition</code> that has been selected by the
     * user for use in a sql statement.
     */
    public ColumnDefinition getSelectedColumn() {
        return selectedColumn;
    }

    /**
     * Specifies whether the finder should be sorted in descending order.
     * @param isDescending true if the column should be sorted in descending
     * order; false if ascending order
     */
    public void setDescending(boolean isDescending) {
        this.isDescending = isDescending;
    }

    /**
     * Returns whether the finder should be sorted in descending order for the
     * selected column.
     *
     * @return a <code>boolean</code> value indicating whether the column should
     * be sorted in descending order.
     */
    public boolean isDescending() {
        return isDescending;
    }

    /**
     * Get a description of this sorter as it would appear in a paramter list in
     * a report.  A parameter list lists what parameters were given to the report.
     * A typical sorter description should look something like: "Task Name (Ascending)".
     *
     * @return a <code>String</code> value giving the description of this sorter.
     */
    public String getSorterDescription() {
        return selectedColumn.getName() + " " + (isDescending ? DESCENDING : ASCENDING);
    }

    /**
     * Accepts an <code>IFinderIngredientVisitor</code> and visits this
     * FinderSorter.
     *
     * @param visitor an <code>IFinderIngredientVisitor</code> to use
     * to visit this FinderSorter
     */
    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitFinderSorter(this);
    }

	/**
	 * @return the columns
	 */
	public ArrayList getColumns() {
		return columns;
	}
}
