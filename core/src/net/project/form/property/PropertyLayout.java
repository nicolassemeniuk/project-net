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
package net.project.form.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * PropertyLayout maintains a collection of properties at various
 * positions.  It provides methods to fetch properties at a particular
 * position.
 */
class PropertyLayout 
        extends ArrayList 
        implements java.io.Serializable {

    /** Highest row number */
    private int maxRow = 0;

    /**
     * Returns a collection where each element is a CustomPropertyCollection
     * of the properties on a row.
     * The first element represents the first row, etc.  The number of elements
     * is equal to the greatest row number in the layout.  If no custom properties
     * are present at a row, an empty CustomPropertyCollection is included.
     * @return the collection of CustomPropertyCollection objects
     */
    public Collection getRowsCollection() {
        ArrayList rows = new ArrayList();
        CustomPropertyCollection properties = null;

        // Iterate over each row number getting the properties at a position
        // for that row.  If there are no properties at any given row
        // then an empty CustomPropertyCollection is added to the rows collection

        for (int i = 1; i <= maxRow; i++) {
            properties = getPropertiesForPosition(new Position(i));
            if (properties == null) {
                rows.add(new CustomPropertyCollection());
            } else {
                rows.add(properties);
            }
        }

        return rows;
    }


    /**
     * Adds a property to the specified position.
     * @param position the position at which to add the property
     * @param property the property to add
     */
    public void addProperty(Position position, ICustomProperty property) {
        CustomPropertyCollection properties = null;

        // Get the properties at the position
        properties = getPropertiesForPosition(position);
        
        // If there are no properties at that position,
        // create a new at that position with empty properties
        if (properties == null) {
            properties = new CustomPropertyCollection();
            addEntry(position, properties);
        }

        // Add the property to the properties at the position
        properties.add(property);

        // Save max row if this position is the greatest
        if (position.getRow() > this.maxRow) {
            this.maxRow = position.getRow();
        }

    }


    /**
     * Returns the CustomPropertyCollection of properties for
     * a specific position.
     * @param position the position for which to get properties
     * @return the properties at that row number or null if there are none
     */
    private CustomPropertyCollection getPropertiesForPosition(Position position) {
        CustomPropertyCollection properties = null;
        LayoutEntry entry = null;

        // Iterate over each entry, finding the properties for an entry
        // with a matching position
        // If none are found, then properties will remain null

        Iterator it = iterator();
        while (it.hasNext()) {
            entry = (LayoutEntry) it.next();
            if (entry.getPosition().equals(position)) {
                properties = entry.getProperties();
                break;
            }
        }

        return properties;
    }


    /**
     * Adds a new entry for the specified position and properties.
     * @param position the position for the entry
     * @param properties the custom properties for the entry
     */
    private void addEntry(Position position, CustomPropertyCollection properties) {
        LayoutEntry entry = new LayoutEntry(position, properties);
        add(entry);
    }


    //
    // Inner classes
    //

    /**
     * This represents position in the property layout.
     */
    public static class Position {
        
        private int row = 1;

        public Position(int row) {
            this.row = row;
        }

        public int getRow() {
            return row;
        }

        /**
         * @return true if obj is a Position with same row number
         */
        public boolean equals(Object obj) {
            if (obj != null &&
                obj instanceof Position &&
                ((Position) obj).getRow() == this.getRow() ) {

                return true;
            }
            return false;
        }

    }

    /**
     * A LayoutEntry is a simpel structure for maintain a position and a
     * collection of properties at that position.
     */
    private static class LayoutEntry {

        private Position position = null;
        private CustomPropertyCollection properties = null;

        public LayoutEntry(Position position, CustomPropertyCollection properties) {
            this.position = position;
            this.properties = properties;
        }

        public Position getPosition() {
            return this.position;
        }

        public CustomPropertyCollection getProperties() {
            return this.properties;
        }

    }

}
