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
 * The direction class represents the direction that a line or arrow is oriented
 * in.  There is a slight peculiarity worth noting though:
 * <tt><pre>
 *     ***********
 *     *    N    *
 *     *    *    *
 *     *W*******E*
 *     *    *    *
 *     *    S    *
 *     ***********
 * </pre></tt>
 *
 * Even if you are heading south, if you only want the line segment in the
 * northern part of the box and not in the southern, your direction has to be
 * 'N'.
 *
 * @author Matthew Flower
 * @since Version 7.7
 */
class Direction {
    static Direction N = new Direction('N');
    static Direction S = new Direction('S');
    static Direction E = new Direction('E');
    static Direction W = new Direction('W');

    private char id;
    private Direction(char id) {
        this.id = id;
    }

    public String toString() {
        return ""+id;
    }
}
