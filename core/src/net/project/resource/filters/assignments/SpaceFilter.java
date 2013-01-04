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
package net.project.resource.filters.assignments;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.FinderFilter;
import net.project.base.finder.IFinderIngredientVisitor;
import net.project.base.property.PropertyProvider;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;
import net.project.space.Space;
import net.project.space.SpaceList;
import net.project.util.CollectionUtils;
import net.project.util.Conversion;
import net.project.util.VisitException;

/**
 * A finder filter to select spaces globally.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class SpaceFilter extends FinderFilter {
    /** A collection of space objects which we will use to describe the spaces. */
    private List spaceDescription = new LinkedList();
    /**
     * A Map of space ID to space object.  This is useful if we have to show
     * descriptions.
     */
    private final Map spaceIDToSpace = new HashMap();

    /** A collection of string objects which indicates which spaces are selected. */
    private Collection selectedSpaces = new LinkedList();
    /** The column we will be filtering on. */
    private final ColumnDefinition filteringColumn;
    /** This determines if one can select multiple spaces */
    boolean isMultipleSelect;

    public SpaceFilter(String id, String nameToken, ColumnDefinition def) {
        super(id, nameToken);
        this.filteringColumn = def;
        this.isMultipleSelect = true;
    }
    
    public SpaceFilter(String id, String nameToken, ColumnDefinition def, boolean isMultipleSelect) {
        this(id, nameToken, def);
        this.isMultipleSelect = isMultipleSelect;
    }

    public void loadSpaces(String[] spaceTypes) throws PersistenceException {
        SpaceList sl = new SpaceList();
        sl.loadFilteredTypes(spaceTypes);
        spaceDescription = sl;

        spaceIDToSpace.clear();
        spaceIDToSpace.putAll(CollectionUtils.listToMap(spaceDescription, new CollectionUtils.MapKeyLocator() {
            public Object getKey(Object listObject) {
                return ((Space)listObject).getID();
            }
        }));
    }

    /**
     * Get a where clause for this filter that can be applied to a finder.  This
     * where clause will be empty (not null) if the filter isn't selected.
     *
     * @return a <code>String</code> containing a where clause, or an empty
     *         string if the filter isn't selected.
     */
    public String getWhereClause() {
        return filteringColumn.getColumnName() + " in ("+
            DatabaseUtils.collectionToCSV(selectedSpaces)+")";
    }

    /**
     * Get a description of what this filter does.  For example, if it filters
     * tasks assigned to me, this string would say "Tasks Assigned To Me".  If
     * it filtered on date range, this method should return "Tasks with finish
     * dates between 01/02/2003 and 01/02/2005"
     *
     * @return a <code>String</code> containing a human-readable description of
     *         what the filter does.
     */
    public String getFilterDescription() {
        List selectedSpaceNames = new LinkedList();
        for (Iterator it = selectedSpaces.iterator(); it.hasNext();) {
            String id = (String) it.next();
            Space space = (Space)spaceIDToSpace.get(id);
            selectedSpaceNames.add(space.getName());
        }
        String spaceNameList = Conversion.toCommaSeparatedString(selectedSpaceNames);

        return PropertyProvider.get("prm.report.common.spacefilter.description", spaceNameList);
    }

    /**
     * Clears the modifiable properties of this FinderFilter. Invokes when
     * {@link #clear} is invoked.
     */
    protected void clearProperties() {
        // Note: We don't clear the map of IDs to descriptions, since these cannot be repopulated except
        // through calling load()
        this.spaceDescription = null;
        this.selectedSpaces = null;
    }

    public Collection getSelectedSpaces() {
        return selectedSpaces;
    }

    public void setSelectedSpaces(Collection selectedSpaces) {
        this.selectedSpaces = selectedSpaces;
    }

    public List getSpaceDescription() {
        return spaceDescription;
    }

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitSpaceFilter(this);
    }
    
	/**
	 * @return Returns the isMultipleSelect.
	 */
	public boolean isMultipleSelect() {
		return isMultipleSelect;
	}
}
