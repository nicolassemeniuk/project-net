package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasProcessPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal processId;

    /** full constructor */
    public PnSpaceHasProcessPK(BigDecimal spaceId, BigDecimal processId) {
        this.spaceId = spaceId;
        this.processId = processId;
    }

    /** default constructor */
    public PnSpaceHasProcessPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getProcessId() {
        return this.processId;
    }

    public void setProcessId(BigDecimal processId) {
        this.processId = processId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("processId", getProcessId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasProcessPK) ) return false;
        PnSpaceHasProcessPK castOther = (PnSpaceHasProcessPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getProcessId(), castOther.getProcessId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getProcessId())
            .toHashCode();
    }

}
