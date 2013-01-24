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
package net.project.base.finder;

import net.project.util.VisitException;

/**
 * Provides a column that may be selected to be included in the results
 * of a finder query.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class FinderColumn {

    /**
     * The id of this column definition.
     * This is used for XML persistence and data capture.
     */
    private String id = null;

    /**
     * The <code>ColumnDefintion</code> on which this column is based.
     */
    private ColumnDefinition columnDefinition = null;

    /**
     * Indicates whether this <code>FinderColumn</code> has been selected.
     */
    private boolean isSelected = false;

    /**
     * Creates a <code>FinderColumn</code> with the specified id and
     * based on a <code>ColumnDefinition</code>.
     * @param id the id of this FinderColumn
     * @param columnDefinition the ColumnDefinition that this column
     * is based on
     */
    public FinderColumn(String id, ColumnDefinition columnDefinition) {
        this.id = id;
        this.columnDefinition = columnDefinition;
    }

    /**
     * Returns the id of this FinderColumn.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the column definition that this FinderColumn represents
     * a selection of.
     * @return the column definition
     */
    public ColumnDefinition getColumnDefinition() {
        return this.columnDefinition;
    }

    /**
     * Specifies whether this column is selected.
     * @param isSelected <code>true</code> if this column is selected;
     * <code>false</code> if not
     */
    void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * Indicates whether this column has been selected.
     * @return <code>true</code> if this column is selected; <code>false</code> otherwise
     */
    public boolean isSelected() {
        return this.isSelected;
    }

    /**
     * Accepts an {@link IFinderIngredientVisitor} and invokes the
     * <code>visitFinderColumn</code> method.
     * @param visitor the IFinderIngredientVisitor
     * @throws net.project.util.VisitException if there is a problem while visiting this
     * FinderColumn
     * @throws ClassCastException if the visitor is not an IFinderIngredientVisitor
     */
    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitFinderColumn(this);
    }

}
