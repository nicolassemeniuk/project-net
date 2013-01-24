package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocContainerHasObjectPK implements Serializable {

    /** identifier field */
    private BigDecimal docContainerId;

    /** identifier field */
    private BigDecimal objectId;

    /** full constructor */
    public PnDocContainerHasObjectPK(BigDecimal docContainerId, BigDecimal objectId) {
        this.docContainerId = docContainerId;
        this.objectId = objectId;
    }

    /** default constructor */
    public PnDocContainerHasObjectPK() {
    }

    public BigDecimal getDocContainerId() {
        return this.docContainerId;
    }

    public void setDocContainerId(BigDecimal docContainerId) {
        this.docContainerId = docContainerId;
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docContainerId", getDocContainerId())
            .append("objectId", getObjectId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocContainerHasObjectPK) ) return false;
        PnDocContainerHasObjectPK castOther = (PnDocContainerHasObjectPK) other;
        return new EqualsBuilder()
            .append(this.getDocContainerId(), castOther.getDocContainerId())
            .append(this.getObjectId(), castOther.getObjectId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDocContainerId())
            .append(getObjectId())
            .toHashCode();
    }

}
