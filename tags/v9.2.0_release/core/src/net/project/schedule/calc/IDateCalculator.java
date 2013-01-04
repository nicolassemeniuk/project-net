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

 package net.project.schedule.calc;

import java.util.Date;

/**
 * Implementing classes calculate finish dates from a start date or
 * start dates from a finish date.
 * <p>
 * <b>Note:</b> Methods in this class are called very frequently (mostly by TaskEndpointCalculation)
 * and so must be as performant as possible.  Suggest profiling of implementation classes
 * to determine bottlenecks.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public interface IDateCalculator {

    /**
     * Calculates a finish date based on the specified start date.
     * @param startDate the start date from which to calculate the
     * finish date
     * @return the finish date
     */
    Date calculateFinishDate(Date startDate);

    /**
     * Calculates a start date based on the specified finish date.
     * @param finishDate the finish date from which to calculate the
     * start date
     * @return the start date
     */
    Date calculateStartDate(Date finishDate);

}