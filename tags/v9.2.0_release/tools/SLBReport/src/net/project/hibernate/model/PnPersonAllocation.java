package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonAllocation implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPersonAllocationPK comp_id;

    /** nullable persistent field */
    private Integer hoursAllocated;

    /** nullable persistent field */
    private Date allocationDate;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** full constructor */
    public PnPersonAllocation(net.project.hibernate.model.PnPersonAllocationPK comp_id, Integer hoursAllocated, Date allocationDate, net.project.hibernate.model.PnPerson pnPerson) {
        this.comp_id = comp_id;
        this.hoursAllocated = hoursAllocated;
        this.allocationDate = allocationDate;
        this.pnPerson = pnPerson;
    }

    /** default constructor */
    public PnPersonAllocation() {
    }

    /** minimal constructor */
    public PnPersonAllocation(net.project.hibernate.model.PnPersonAllocationPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPersonAllocationPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPersonAllocationPK comp_id) {
        this.comp_id = comp_id;
    }

    public Integer getHoursAllocated() {
        return this.hoursAllocated;
    }

    public void setHoursAllocated(Integer hoursAllocated) {
        this.hoursAllocated = hoursAllocated;
    }

    public Date getAllocationDate() {
        return this.allocationDate;
    }

    public void setAllocationDate(Date allocationDate) {
        this.allocationDate = allocationDate;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonAllocation) ) return false;
        PnPersonAllocation castOther = (PnPersonAllocation) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
