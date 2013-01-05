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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.calendar.PnCalendar;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskDependency;
import net.project.schedule.TaskDependencyType;
import net.project.util.DateUtils;

/**
 * The LinkLinePathCalculator figures out what path a dependency line
 * (aka link line) would take to get from one path to another.  It renders this
 * into a series of JavaScript objects for production of the DHTML/javascript
 * version of the gantt.  (It will also have to do it in java somehow once we
 * are ready to produce the PDF version too)
 *
 * @author Matthew Flower
 * @since Version 7.7
 */
public class LinkLinePathCalculator {
    private Date scheduleStartDate;
    private List linkLinePaths = new ArrayList();
    private PnCalendar cal = new PnCalendar();

    /**
     * For the given schedule find all of the paths necessary to draw the link
     * lines between the predecessor and successor tasks.
     *
     * @param schedule a <code>Schedule</code> for which we are going to
     * calculate the location of the link lines.
     * @throws PersistenceException if the task dependency relationships for the
     * schedule were not loaded when the schedule was loaded AND there was a
     * problem loading them at run time.  Note that is definitely recommended to
     * preload them.  This method will be remarkably slow otherwise.
     */
    public void calculateLinkLines(Schedule schedule) throws PersistenceException {
        scheduleStartDate = schedule.getScheduleStartDate();
        if (schedule.getEarliestTaskStartTimeMS() != -1) {
            scheduleStartDate = DateUtils.min(scheduleStartDate, new Date(schedule.getEarliestTaskStartTimeMS()));
        }
        scheduleStartDate = cal.startOfDay(scheduleStartDate);

        List entries = schedule.getEntries();
        Map entryMap = schedule.getEntryMap();
        //Run through each schedule entry and add the path
        int i = 0;
        for (Iterator it = entries.iterator(); it.hasNext();) {
            ScheduleEntry scheduleEntry = (ScheduleEntry) it.next();
            try{
                findPaths(scheduleEntry, entryMap);
            } catch (NullPointerException e) {
                // do nothing, this is bfd-3184 bugfix, when dependent tasks exist in the project
                // and target task is subtask, when you colapse parent task that target task is missing
                // and line shouldn't be shown in Gantt chart.
            }
        }
    }

    /**
     * Iterates through all of the task dependencies of a schedule entry and
     * finds a link line path for each.
     *
     * @param scheduleEntry a <code>ScheduleEntry</code> for which we are going
     * to find a link line path.
     * @param entryMap a <code>Map</code> containing a id to ScheduleEntry
     * mapping.
     * @throws PersistenceException if the task dependency relationships for the
     * schedule were not loaded when the schedule was loaded AND there was a
     * problem loading them at run time.
     */
    private void findPaths(ScheduleEntry scheduleEntry, Map entryMap) throws PersistenceException {
        for (Iterator it = scheduleEntry.getSuccessors().iterator(); it.hasNext();) {
            TaskDependency td = (TaskDependency)it.next();
            linkLinePaths.add(findPath(scheduleEntry, (ScheduleEntry)entryMap.get(td.getTaskID()), td));
        }
    }

    /**
     * Find a path between a schedule entry and its successor.
     *
     * @param scheduleEntry the schedule entry where we will start drawing at.
     * @param successor the schedule entry where we stop drawing.
     * @param td a <code>TaskDependency</code> object which contains additional
     * information about the type of dependency between these objects, such as
     * whether they have a start-to-start, start-to-finish, etc relationship.
     * @return a <code>List</code> containing multiple PathInfo objects that can
     * be used to reconstruct the path between these two schedule entries.
     */
    protected List findPath(ScheduleEntry scheduleEntry, ScheduleEntry successor, TaskDependency td) {
        List path = new ArrayList();

        PathEndpointInfo pathInfo = getPathEndpointInfo(scheduleEntry, successor, td);

        Position currentPosition = new Position();
        currentPosition.row = pathInfo.startingRow;
        currentPosition.block = pathInfo.startingBlock;
        currentPosition.direction = (pathInfo.startOnBeginning ? Direction.W : Direction.E);

        //Step 1, see if we have to make a u-turn in order to move horizontally
        //to the correct direction.
        makeUTurn(pathInfo, currentPosition, path);

        //Step 2, move horizontally to get to the correct column
        drawHorizontalLine(currentPosition, pathInfo.endingBlock, path);

        //Step 3, turn the correct direction vertically
        turnNorthOrSouth(pathInfo, currentPosition, path);

        //Step 4, move vertically to get to the correct row
        drawVerticalLine(currentPosition, pathInfo.endingRow, path);

        //Step 5, If we aren't going to be ending on top, we need to make another turn
        makeFinalTurn(pathInfo, path, currentPosition);

        //Step 6, draw the final terminator
        drawFinalTerminator(pathInfo, path, currentPosition);

        return path;
    }

    /**
     * The first step in constructing a path is to reorient the link line so a
     * horizontal line can be drawn in the correct position.  For example, if we
     * has tasks that look like this:
     * <tt><pre>
     *       SSSSSSSSSSS-v
     *         v---------v
     *         >->FFFFFFFFFF
     * </pre></tt>
     * The link line is starting out pointing to the east, but it needs to be
     * pointing to the west so it can go back a few blocks horizontally.
     *
     * Not all paths need to make this u-turn.
     *
     * @param pathInfo
     * @param currentPosition
     * @param thisPath
     */
    private void makeUTurn(PathEndpointInfo pathInfo, Position currentPosition, List thisPath) {
        if ((pathInfo.startingBlock > pathInfo.endingBlock) && (currentPosition.direction == Direction.E)) {
            if (pathInfo.startingRow < pathInfo.endingRow) {
                // Two turns on successive rows to go from east to west
                //  XXXXXX---v
                //           v
                //       <---<
                thisPath.add(new Turn(currentPosition.row, currentPosition.block, Direction.W,  Direction.S));
                currentPosition.row++;
                thisPath.add(new Turn(currentPosition.row, currentPosition.block, Direction.N,  Direction.W));
                currentPosition.block--;
                currentPosition.direction = Direction.W;
            } else {
                // Two turns on two rows to go from east to west.
                //       <---<
                //           ^
                //  XXXXXX---^
                thisPath.add(new Turn(currentPosition.row, currentPosition.block, Direction.W,  Direction.N));
                currentPosition.row--;
                thisPath.add(new Turn(currentPosition.row, currentPosition.block, Direction.S,  Direction.W));
                currentPosition.block--;
                currentPosition.direction = Direction.W;
            }
        } else if (pathInfo.startingBlock < pathInfo.endingBlock && (currentPosition.direction == Direction.W)) {
            if (pathInfo.startingRow < pathInfo.endingRow) {
                // Two turns on successive rows to go from west to east
                //  v---XXXXXX
                //  v
                //  >--->
                thisPath.add(new Turn(currentPosition.row, currentPosition.block, Direction.E,  Direction.S));
                currentPosition.row++;
                thisPath.add(new Turn(currentPosition.row, currentPosition.block, Direction.N,  Direction.E));
                currentPosition.block++;
                currentPosition.direction = Direction.E;
            } else {
                // Two turns on two rows to go from west to east.
                //  >--->
                //  ^
                //  ^---XXXXXX
                thisPath.add(new Turn(currentPosition.row, currentPosition.block, Direction.E,  Direction.N));
                currentPosition.row--;
                thisPath.add(new Turn(currentPosition.row, currentPosition.block, Direction.S,  Direction.E));
                currentPosition.block++;
                currentPosition.direction = Direction.E;
            }
        }
    }

    /**
     * Once we have finished drawing the line horizontally, we need to turn
     * north or south so we can draw the vertical line.  This step does that.
     *
     * @param pathInfo
     * @param currentPosition
     * @param thisPath
     */
    private void turnNorthOrSouth(PathEndpointInfo pathInfo, Position currentPosition, List thisPath) {
        Direction dirToTurn = (currentPosition.direction == Direction.W ? Direction.E : Direction.W);

        if (pathInfo.endingRow > currentPosition.row) {
            thisPath.add(new Turn(currentPosition.row, currentPosition.block, dirToTurn, Direction.S));
            currentPosition.direction = Direction.S;
            currentPosition.row++;
        } else if (pathInfo.endingRow < currentPosition.row) {
            thisPath.add(new Turn(currentPosition.row, currentPosition.block, dirToTurn, Direction.N));
            currentPosition.direction = Direction.N;
            currentPosition.row--;
        }
    }

    /**
     * Cover the east-west distance that is needed to connect two tasks that
     * aren't sequential without any gap in time.
     *
     * There are two ways that horizontal distance is covered.  The first
     * occurs when we have to make a u-turn.  The horizontal line will either
     * just above or just below the task.  The whole reason for doing a u-turn
     * is because you need to travel in the same horizontal space as the task,
     * there will be overlap between the blocks the task are in and the blocks
     * the line is in.
     *
     * The second way that a horizontal line is drawn is when you leave a task
     * but you don't need to make a u-turn.  In this situation, there is just a
     * line that leaves the right (usually) side of a task and travels for some
     * distance.  At the end of the line, it will be a north or south turn.
     *
     * @param currentPosition a <code>Position</code> object which shows the
     * position before we draw the horizontal line.  This will affect how we
     * draw the line a bit.
     * @param endingBlock a <code>int</code> containing the ultimate block that
     * we are supposed to end on when we are done drawing.
     * @param thisPath a <code>List</code> that we are appending drawing commands
     * to.
     */
    private void drawHorizontalLine(Position currentPosition, int endingBlock, List thisPath) {
        int horizontalDifference = endingBlock - currentPosition.block;
        if (horizontalDifference > 0) {
            thisPath.add(new Line(currentPosition.row, currentPosition.block,
                Direction.W, horizontalDifference));
            currentPosition.block = endingBlock;
            currentPosition.direction = Direction.E;
        } else if (horizontalDifference < 0) {
            thisPath.add(new Line(currentPosition.row, endingBlock+1, Direction.W, Math.abs(horizontalDifference)));
            currentPosition.block = endingBlock;
            currentPosition.direction = Direction.W;
        }
    }

    /**
     * Drawing a vertical line spans the difference in location between two
     * tasks.  Generally, you will only have to draw a vertical line if the
     * linked tasks are not on sequential rows.
     *
     * @param currentPosition a <code>Position</code> object which shows the
     * beginning position.
     * @param endingRow an <code>int</code> which indicates the row the target
     * task resides in.
     * @param thisPath a <code>List</code> of lines and turns which shows how
     * to draw the path.  We'll append our drawing commands to this list.
     */
    private void drawVerticalLine(Position currentPosition, int endingRow, List thisPath) {
        int verticalDifference = endingRow - currentPosition.row;
        int verticalDifferenceAbs = Math.abs(verticalDifference);


        if (verticalDifferenceAbs > 0) {
            if (endingRow < currentPosition.row) {
                thisPath.add(new Line(endingRow, currentPosition.block, Direction.S, verticalDifferenceAbs+1));
            } else {
                thisPath.add(new Line(currentPosition.row, currentPosition.block, Direction.N, verticalDifferenceAbs));
            }
            currentPosition.row += verticalDifference;
        }
    }

    private void makeFinalTurn(PathEndpointInfo pathInfo, List thisPath, Position currentPosition) {
        if (!pathInfo.endOnTopOfTask) {
            Direction finalDirection = (pathInfo.endOnBeginning ? Direction.E : Direction.W);
            Direction firstHalfOfTurn = (currentPosition.direction == Direction.S ? Direction.N : Direction.S);

            thisPath.add(new Turn(currentPosition.row, currentPosition.block, firstHalfOfTurn, finalDirection));

            currentPosition.direction = finalDirection;
            if (finalDirection == Direction.E) {
                currentPosition.block++;
            } else {
                currentPosition.block--;
            }
        }
    }

    /**
     * This method adds the command to draw the arrow that points into the
     * successor task.
     *
     * @param pathInfo a <code>PathEndpointInfo</code> class that contains
     * information about where the terminator is supposed to be.
     * @param path a <code>List</code> which contains all the drawing commands
     * needed to draw a path between the source and destination.
     * @param currentPosition a <code>Position</code> object which contains
     * information about where we have drawn the line to thus far.
     */
    private void drawFinalTerminator(PathEndpointInfo pathInfo, List path, Position currentPosition) {
        if (pathInfo.endOnTopOfTask) {
            path.add(new Line(pathInfo.endingRow, pathInfo.endingBlock, Direction.N, 1));
        }
        path.add(new Terminator(pathInfo.endingRow, pathInfo.endingBlock, currentPosition.direction));
    }

    /**
     * Get some information that is necessary to draw the line from the
     * predecessor to the successor.
     *
     * @param scheduleEntry the "predecessor" for the link line.  This is the
     * task where we will start our drawing.
     * @param successor the final location where we want our link line to end up.
     * @param td a <code>TaskDependency</code> object which contains information
     * about the nature of the relationship between these tasks.
     * @return a <code>PathEndpointInfo</code> object which contains the
     * information that we gathered.
     */
    private PathEndpointInfo getPathEndpointInfo(ScheduleEntry scheduleEntry, ScheduleEntry successor, TaskDependency td) {
        PathEndpointInfo pathInfo = new PathEndpointInfo();
        
        //Path rows are zero based and count by twos
        pathInfo.startingRow = (scheduleEntry.getSequenceNumber()-1)*2;
        pathInfo.endingRow = (successor.getSequenceNumber()-1)*2;

        //We have a different starting currentPosition depending on whether this is a
        //finish edge or start edge dependency.
        if (td.getDependencyType().equals(TaskDependencyType.START_TO_START)) {
            pathInfo.startOnBeginning = true;
            pathInfo.endOnBeginning = true;
        } else if (td.getDependencyType().equals(TaskDependencyType.START_TO_FINISH)) {
            pathInfo.startOnBeginning = true;
            pathInfo.endOnBeginning = false;
        } else if (td.getDependencyType().equals(TaskDependencyType.FINISH_TO_FINISH)) {
            pathInfo.startOnBeginning = false;
            pathInfo.endOnBeginning = false;
        } else {
            pathInfo.startOnBeginning = false;
            pathInfo.endOnBeginning = true;
        }

        //Determine which block we are supposed to start on.  This will be affected
        //by whether the dependency is connected to the beginning or end of the
        //task.
        if (pathInfo.startOnBeginning) {
            pathInfo.startingBlock = dateToBlock(cal.startOfDay(scheduleEntry.getStartTime()))-1;
        } else {
            pathInfo.startingBlock = dateToBlock(cal.startOfDay(scheduleEntry.getEndTime()))+1;
        }

        //Calculate the block we are supposed to end on.
        if (pathInfo.endOnBeginning) {
            pathInfo.endingBlock = dateToBlock(cal.startOfDay(successor.getStartTime()));
            if ((pathInfo.endingBlock >= pathInfo.startingBlock) && (pathInfo.startingRow < pathInfo.endingRow) && (!pathInfo.startOnBeginning)) {
                //This task is after the predecessor task and is underneath it,
                //this means that we are going to terminate on the top of the
                //task (the arrow will point at the upper left side of the task).
                //Adjust for that.
                pathInfo.endingRow--;
                pathInfo.endOnTopOfTask = true;
            } else {
                pathInfo.endOnTopOfTask = false;
                pathInfo.endingBlock--;
            }
        } else {
            pathInfo.endingBlock = dateToBlock(cal.startOfDay(successor.getEndTime()))+1;
        }

        return pathInfo;
    }

    /**
     * Change a date to a block number that is used in the gantt chart.  We use
     * block numbers so we can zoom in or zoom out easily.  By using blocks, we
     * don't have to know which use dates directly in our gantt chart, which
     * might be rendered using DHTML.  (We wouldn't want to have to use the date
     * arithmetic support in JavaScript -- eew!)
     *
     * @param date a <code>Date</code> object to be converted into a block.
     * @return a <code>int</code> which contains the block for the provided date.
     */
    public int dateToBlock(Date date) {
        return (int)DateUtils.daysBetween(cal, scheduleStartDate, date, true);
    }

    /**
     * This method converts all of the path information that has been calculated
     * into a big array of JavaScript objects that can be used to construct the
     * link lines in our DHTML/JavaScript classes.
     *
     * This method is basically the "Bridge" between the Java code and the
     * JavaScript code of the Gantt chart.
     *
     * @return a <code>String</code> suitable to be assigned to a JavaScript
     * array variable.
     */
    public String toJavaScript() {
        StringBuffer sb = new StringBuffer();

        for (Iterator it = linkLinePaths.iterator(); it.hasNext();) {
            List pathInfoList = (List)it.next();

            sb.append("[");
            for (Iterator it2 = pathInfoList.iterator(); it2.hasNext();) {
                PathInfo pathInfo = (PathInfo) it2.next();
                sb.append(pathInfo.toJavaScript());
                if (it2.hasNext()) {
                    sb.append(",");
                }
            }
            sb.append("]").append(it.hasNext() ? "," : "").append("\n");
        }

        return sb.toString();
    }
}


class Position {
    int row;
    int block;
    Direction direction;
}

class PathEndpointInfo {
    int startingRow;
    int endingRow;
    int startingBlock;
    int endingBlock;
    boolean startOnBeginning;
    boolean endOnBeginning;
    boolean endOnTopOfTask;
}

