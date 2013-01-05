package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPhaseHasDeliverablePK implements Serializable {

    /** identifier field */
    private BigDecimal phaseId;

    /** identifier field */
    private BigDecimal deliverableId;

    /** full constructor */
    public PnPhaseHasDeliverablePK(BigDecimal phaseId, BigDecimal deliverableId) {
        this.phaseId = phaseId;
        this.deliverableId = deliverableId;
    }

    /** default constructor */
    public PnPhaseHasDeliverablePK() {
    }

    public BigDecimal getPhaseId() {
        return this.phaseId;
    }

    public void setPhaseId(BigDecimal phaseId) {
        this.phaseId = phaseId;
    }

    public BigDecimal getDeliverableId() {
        return this.deliverableId;
    }

    public void setDeliverableId(BigDecimal deliverableId) {
        this.deliverableId = deliverableId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("phaseId", getPhaseId())
            .append("deliverableId", getDeliverableId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPhaseHasDeliverablePK) ) return false;
        PnPhaseHasDeliverablePK castOther = (PnPhaseHasDeliverablePK) other;
        return new EqualsBuilder()
            .append(this.getPhaseId(), castOther.getPhaseId())
            .append(this.getDeliverableId(), castOther.getDeliverableId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPhaseId())
            .append(getDeliverableId())
            .toHashCode();
    }

}
