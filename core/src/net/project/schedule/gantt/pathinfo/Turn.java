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

 package net.project.schedule.gantt.pathinfo;

/**
 * This class represents a change in direction that occurs in a single row.
 * This means that you cannot turn from west to west, for example, because that
 * couldn't occur in one row.
 *
 * @author Matthew Flower
 * @since Version 7.7
 */
class Turn extends PathInfo {
    int row;
    int block;
    Direction startingDirection;
    Direction endingDirection;

    public Turn(int row, int block, Direction startingDirection, Direction endingDirection) {
        this.row = row;
        this.block = block;
        this.startingDirection = startingDirection;
        this.endingDirection = endingDirection;
    }

    public String toJavaScript() {
        return "new Turn("+row+","+block+",'"+startingDirection.toString()+"','"+endingDirection+"')";
    }
}

