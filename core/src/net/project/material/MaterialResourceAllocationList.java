package net.project.material;

import java.math.BigDecimal;
import java.util.Date;

import net.project.calendar.PnCalendar;
import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.resource.ResourceAssignment;
import net.project.util.time.ITimeRangeValue;
import net.project.util.time.TimeRangeAggregator;

public class MaterialResourceAllocationList {
    /** Calendar used to zero out time. */
    PnCalendar cal = new PnCalendar();
    TimeRangeAggregator aggregator = new TimeRangeAggregator();
    String materialID;

    /**
     * Load all of the resource allocations related to a material between given
     * dates.
     */
    public void loadAllocationsForMaterial(String materialID) throws PersistenceException {
        this.materialID = materialID;
        aggregator = new TimeRangeAggregator();

        PnMaterialAssignmentList assignments = ServiceFactory.getInstance().getPnMaterialAssignmentService().getAssignmentsForMaterial(materialID);
        
        for(PnMaterialAssignment assignment : assignments){
          MaterialAllocation allocation = new MaterialAllocation(assignment.getStartDate(), assignment.getEndDate(), assignment.getPercentAllocated());
          aggregator.insert(allocation);
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

        return new ResourceAssignment(date, allocation.intValue(), materialID);
    }

    /**
     * Get the largest amount of allocation that we loaded.
     *
     * @return an <code>int</code> value containing the largest amount of
     * work that a user has been allocated.
     */
//    public int getMaximumAllocation(Date startDate, Date endDate) {
//        return aggregator.findMaximumConcurrent(startDate, endDate).intValue();
//    }

    /**
     * This method adds a resource allocation to this object.  Generally,
     * allocations shouldn't be added externally, instead call the
     * {@link #loadResourceAllocationsForMaterial} method to add allocations to
     * the object.  The method was added for convenience while doing unit tests.
     *
     * @param allocation a <code>ResourceAllocation</code> object which we are
     * going to store internally.  The AllocationDate of this object is going to
     * be used as a key to look it up later, so it cannot be null.
     */
    public void addAllocation(ResourceAssignment allocation) {
        aggregator.insert(new MaterialAllocation(
            cal.startOfDay(allocation.getAssignmentDate()),
            cal.endOfDay(allocation.getAssignmentDate()),
            new BigDecimal(allocation.getPercentAssigned())
        ));
    }
    
    private class MaterialAllocation implements ITimeRangeValue {
        private final Date startDate, endDate;
        private final BigDecimal allocation;

        public MaterialAllocation(Date startDate, Date endDate, BigDecimal allocation) {
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
}
