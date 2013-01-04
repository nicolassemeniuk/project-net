package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnEnvelopeVersionHasObjectPK implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** identifier field */
    private BigDecimal versionId;

    /** identifier field */
    private BigDecimal envelopeId;

    /** full constructor */
    public PnEnvelopeVersionHasObjectPK(BigDecimal objectId, BigDecimal versionId, BigDecimal envelopeId) {
        this.objectId = objectId;
        this.versionId = versionId;
        this.envelopeId = envelopeId;
    }

    /** default constructor */
    public PnEnvelopeVersionHasObjectPK() {
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public BigDecimal getVersionId() {
        return this.versionId;
    }

    public void setVersionId(BigDecimal versionId) {
        this.versionId = versionId;
    }

    public BigDecimal getEnvelopeId() {
        return this.envelopeId;
    }

    public void setEnvelopeId(BigDecimal envelopeId) {
        this.envelopeId = envelopeId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .append("versionId", getVersionId())
            .append("envelopeId", getEnvelopeId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnEnvelopeVersionHasObjectPK) ) return false;
        PnEnvelopeVersionHasObjectPK castOther = (PnEnvelopeVersionHasObjectPK) other;
        return new EqualsBuilder()
            .append(this.getObjectId(), castOther.getObjectId())
            .append(this.getVersionId(), castOther.getVersionId())
            .append(this.getEnvelopeId(), castOther.getEnvelopeId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getObjectId())
            .append(getVersionId())
            .append(getEnvelopeId())
            .toHashCode();
    }

}
