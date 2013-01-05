package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocSpaceHasContainerPK implements Serializable {

    /** identifier field */
    private BigDecimal docSpaceId;

    /** identifier field */
    private BigDecimal docContainerId;

    /** full constructor */
    public PnDocSpaceHasContainerPK(BigDecimal docSpaceId, BigDecimal docContainerId) {
        this.docSpaceId = docSpaceId;
        this.docContainerId = docContainerId;
    }

    /** default constructor */
    public PnDocSpaceHasContainerPK() {
    }

    public BigDecimal getDocSpaceId() {
        return this.docSpaceId;
    }

    public void setDocSpaceId(BigDecimal docSpaceId) {
        this.docSpaceId = docSpaceId;
    }

    public BigDecimal getDocContainerId() {
        return this.docContainerId;
    }

    public void setDocContainerId(BigDecimal docContainerId) {
        this.docContainerId = docContainerId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docSpaceId", getDocSpaceId())
            .append("docContainerId", getDocContainerId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocSpaceHasContainerPK) ) return false;
        PnDocSpaceHasContainerPK castOther = (PnDocSpaceHasContainerPK) other;
        return new EqualsBuilder()
            .append(this.getDocSpaceId(), castOther.getDocSpaceId())
            .append(this.getDocContainerId(), castOther.getDocContainerId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDocSpaceId())
            .append(getDocContainerId())
            .toHashCode();
    }

}
