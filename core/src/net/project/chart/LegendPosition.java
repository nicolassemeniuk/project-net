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

 package net.project.chart;

import java.util.ArrayList;

/**
 * Which position the legend should be located in -- north south east or west.
 *
 * @author Matthew Flower
 * @since 3 Dec 2005
 */
public class LegendPosition {
    private static ArrayList legendPositionTypes = new ArrayList();

    /** Position the legend on the north side of the map. */
    public static final LegendPosition NORTH = new LegendPosition(0);
    public static final LegendPosition EAST = new LegendPosition(90);
    public static final LegendPosition SOUTH = new LegendPosition(180);
    public static final LegendPosition WEST = new LegendPosition(270);

    int compassPosition = 0;

    private LegendPosition(int compassPosition) {
        this.compassPosition = compassPosition;
        legendPositionTypes.add(legendPositionTypes);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final LegendPosition that = (LegendPosition) o;

        if (compassPosition != that.compassPosition) return false;

        return true;
    }

    public int hashCode() {
        return compassPosition;
    }
}
