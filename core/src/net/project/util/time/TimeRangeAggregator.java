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
package net.project.util.time;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class is responsible for finding the total value of all ITimeRangeValues
 * that occur within a given range of time.
 *
 * For example, let's assume that you have created a number of assignments.
 * All these assignments have a date range in which they occur as well as a
 * percentage of allocation.
 *
 * This class will allow you to ask questions like what is the maximum
 * percentage of time that John Doe is working between Jan 1st and Jan 2nd.
 *
 * The current version of this class is not using a particularly performant
 * algorithm for insertion into the aggregator.  This makes this class of very
 * limited use.  I'd avoid using it for generic purposes.  (Yet).
 *
 * @author Matthew Flower
 * @since Version 7.6.5
 */
public class TimeRangeAggregator {
    private List concurrentNodeLists = new LinkedList();

    /**
     * A time range node is used to construct our linked list of "node sets".
     */
    private class TimeRangeNode {
        public ITimeRangeValue value;
        public TimeRangeNode nextConcurrentNode;

        public TimeRangeNode(ITimeRangeValue value) {
            this.value = value;
        }
    }

    /**
     * Add a new value between a given time range.
     *
     * @param value a <code>ITimeRangeValue</code> object.
     */
    public void insert(ITimeRangeValue value) {
        //Keeps track of nodes we are adjacent to, even if they aren't in a set
        Set anyAdjacentNode = new HashSet();
        boolean anySetFound = false;

        for (Iterator it = concurrentNodeLists.iterator(); it.hasNext();) {
            TimeRangeNode node = (TimeRangeNode) it.next();
            TimeRangeNode lastNode = null;
            boolean setFound = true;

            //Iterate through all the nodes in this set.  Unless we find a node
            //that we are not concurrent with, we belong to this set.
            while (node != null) {
                if (!isConcurrent(value.getStartDate(), value.getEndDate(), node)) {
                     setFound = false;
                } else {
                    anyAdjacentNode.add(node.value);
                }
                lastNode = node;
                node = node.nextConcurrentNode;
            }

            if (setFound) {
                lastNode.nextConcurrentNode = new TimeRangeNode(value);
                anySetFound = true;
            }
        }

        //There are no sets this new node agrees with.  Create a new set
        if (!anySetFound) {
            TimeRangeNode newNode = new TimeRangeNode(value);
            TimeRangeNode currentNode = newNode;

            for (Iterator it = anyAdjacentNode.iterator(); it.hasNext();) {
                ITimeRangeValue iTimeRangeValue = (ITimeRangeValue) it.next();
                currentNode.nextConcurrentNode = new TimeRangeNode(iTimeRangeValue);
                currentNode = currentNode.nextConcurrentNode;
            }

            concurrentNodeLists.add(newNode);
        }
    }

    public void insertAll(List values) {
        for (Iterator it = values.iterator(); it.hasNext();) {
            ITimeRangeValue value = (ITimeRangeValue) it.next();
            insert(value);
        }
    }

    /**
     * Given a time range, find the maximum sum of the values that are occurring
     * at the same time.
     *
     * For example, if you were looking at assignments, find the maximum amount
     * that a person is assigned in a given time frame.
     *
     * @param testStart a <code>Date</code> which indicates the start of the
     * date range we are going to check.
     * @param testEnd a <code>Date</code> which indicates the end of the date
     * range we are going to check.
     * @return a <code>BigDecimal</code> indicating the maximum concurrency
     * which is going on in the provided time range.
     */
    public BigDecimal findMaximumConcurrent(Date testStart, Date testEnd) {
        BigDecimal maxValue = new BigDecimal(0);

        for (Iterator it = concurrentNodeLists.iterator(); it.hasNext();) {
            TimeRangeNode node = (TimeRangeNode)it.next();
            BigDecimal currentAggregate = new BigDecimal(0);
            boolean concurrencyFound = false;

            //Now test all the nodes in this "node set"
            while (node != null) {
                if (isConcurrent(testStart, testEnd, node)) {
                    currentAggregate = currentAggregate.add(node.value.getValue());
                    concurrencyFound = true;
                }

                node = node.nextConcurrentNode;
            }

            if (concurrencyFound && currentAggregate.compareTo(maxValue) > 0) {
                maxValue = currentAggregate;
            }
        }

        return maxValue;
    }

    private boolean isConcurrent(Date testStart, Date testEnd, Date valueStart, Date valueEnd) {
        return ((testStart.after(valueStart) && testStart.before(valueEnd)) ||  //  XX[XXX ]
                (testEnd.after(valueStart) && testEnd.before(valueEnd)) ||      //  [ XX]XXX
                (testStart.equals(valueStart) && testEnd.equals(valueEnd)) ||   //  [XXXXXX]
                (testStart.before(valueStart) && testEnd.after(valueEnd)) ||    //  [ XXX  ]
                (testStart.after(valueStart) && testEnd.before(valueEnd)) ||    //  XX[XX]XX
                (testStart.equals(valueStart)) ||                               //  [XXXX    ]
                (testEnd.equals(valueEnd)));                                    //  [ XXXXX]
    }

    private boolean isConcurrent(Date testStart, Date testEnd, TimeRangeNode node) {
        Date valueStart = node.value.getStartDate();
        Date valueEnd = node.value.getEndDate();
        return isConcurrent(testStart, testEnd, valueStart, valueEnd);
    }
}

