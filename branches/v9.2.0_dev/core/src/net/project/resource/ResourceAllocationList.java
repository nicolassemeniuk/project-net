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

 package net.project.resource;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import net.project.calendar.PnCalendar;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.util.time.ITimeRangeValue;
import net.project.util.time.TimeRangeAggregator;

/**
 * This class is responsible for holding collections of resource allocations.
 * It can also load lists of resource allocations.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class ResourceAllocationList {
    /** Calendar used to zero out time. */
    PnCalendar cal = new PnCalendar();
    TimeRangeAggregator aggregator = new TimeRangeAggregator();
    String personID;

    private class Allocation implements ITimeRangeValue {
        private final Date startDate, endDate;
        private final BigDecimal allocation;

        public Allocation(Date startDate, Date endDate, BigDecimal allocation) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.allocation = allocation;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public BigDecimal getValue() {
            return allocation;
        }
    }

    /**
     * Load all of the resource allocations related to a person between given
     * dates.
     */
    public void loadResourceAllocationsForPerson(String personID) throws PersistenceException {
        this.personID = personID;

        DBBean db = new DBBean();
        try {
            String sql =
                "select "+
                "  a.start_date, "+
                "  a.end_date, "+
                "  a.percent_allocated "+
                "from "+
                "  pn_assignment a, "+
                "  pn_task t, "+
                "  pn_object o "+
                "where "+
                "  a.record_status = 'A' "+
                "  and a.object_id = t.task_id "+
                "  and t.record_status = 'A' "+
                "  and a.space_id = o.object_id "+
                "  and o.record_status = 'A' " +
                "  and a.person_id = ? ";

            db.prepareStatement(sql);
            db.pstmt.setString(1, personID);
            db.executePrepared();

            aggregator = new TimeRangeAggregator();
            while (db.result.next()) {
                Timestamp startDateTS = db.result.getTimestamp(1);
                Timestamp endDateTS = db.result.getTimestamp(2);
                if (startDateTS != null && endDateTS != null) {
                    Date startDate = new Date(startDateTS.getTime());
                    Date endDate = new Date(endDateTS.getTime());

                    Allocation allocation = new Allocation(startDate, endDate, db.result.getBigDecimal(3));
                    aggregator.insert(allocation);
                }
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to load allocations for user: "+sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Get a resource allocation object which indicates the amount of allocation
     * a resource had on a given day.
     *
     * @param date a <code>Date</code> object which indicates which day we wish
     * to return an allocation for.  We will automatically truncate this value
     * to be equal to "midnight" before looking for the allocation, so there is
     * no reason to do so beforehand.
     * @return a <code>ResourceAllocation</code> object which indicates the
     * amount of allocation on a given day, or "Null" if the date is out of the
     * loaded range.
     */
    public ResourceAssignment getAllocationForDate(Date date) {
        Date startDate = cal.startOfDay(date);
        Date endDate = cal.endOfDay(date);
        BigDecimal allocation = aggregator.findMaximumConcurrent(startDate, endDate);

        return new ResourceAssignment(date, allocation.intValue(), personID);
    }

    /**
     * Get the largest amount of allocation that we loaded.
     *
     * @return an <code>int</code> value containing the largest amount of
     * work that a user has been allocated.
     */
    public int getMaximumAllocation(Date startDate, Date endDate) {
        return aggregator.findMaximumConcurrent(startDate, endDate).intValue();
    }

    /**
     * This method adds a resource allocation to this object.  Generally,
     * allocations shouldn't be added externally, instead call the
     * {@link #loadResourceAllocationsForPerson} method to add allocations to
     * the object.  The method was added for convenience while doing unit tests.
     *
     * @param allocation a <code>ResourceAllocation</code> object which we are
     * going to store internally.  The AllocationDate of this object is going to
     * be used as a key to look it up later, so it cannot be null.
     */
    public void addAllocation(ResourceAssignment allocation) {
        aggregator.insert(new Allocation(
            cal.startOfDay(allocation.getAssignmentDate()),
            cal.endOfDay(allocation.getAssignmentDate()),
            new BigDecimal(allocation.getPercentAssigned())
        ));
    }
}
