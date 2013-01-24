package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnEnvelopeHasObjectPK implements Serializable {

    /** identifier field */
    private BigDecimal envelopeId;

    /** identifier field */
    private BigDecimal objectId;

    /** full constructor */
    public PnEnvelopeHasObjectPK(BigDecimal envelopeId, BigDecimal objectId) {
        this.envelopeId = envelopeId;
        this.objectId = objectId;
    }

    /** default constructor */
    public PnEnvelopeHasObjectPK() {
    }

    public BigDecimal getEnvelopeId() {
        return this.envelopeId;
    }

    public void setEnvelopeId(BigDecimal envelopeId) {
        this.envelopeId = envelopeId;
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("envelopeId", getEnvelopeId())
            .append("objectId", getObjectId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnEnvelopeHasObjectPK) ) return false;
        PnEnvelopeHasObjectPK castOther = (PnEnvelopeHasObjectPK) other;
        return new EqualsBuilder()
            .append(this.getEnvelopeId(), castOther.getEnvelopeId())
            .append(this.getObjectId(), castOther.getObjectId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getEnvelopeId())
            .append(getObjectId())
            .toHashCode();
    }

}
