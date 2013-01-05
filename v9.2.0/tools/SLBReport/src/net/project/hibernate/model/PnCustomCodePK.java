package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCustomCodePK implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** identifier field */
    private BigDecimal codeTypeId;

    /** full constructor */
    public PnCustomCodePK(BigDecimal objectId, BigDecimal codeTypeId) {
        this.objectId = objectId;
        this.codeTypeId = codeTypeId;
    }

    /** default constructor */
    public PnCustomCodePK() {
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public BigDecimal getCodeTypeId() {
        return this.codeTypeId;
    }

    public void setCodeTypeId(BigDecimal codeTypeId) {
        this.codeTypeId = codeTypeId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .append("codeTypeId", getCodeTypeId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnCustomCodePK) ) return false;
        PnCustomCodePK castOther = (PnCustomCodePK) other;
        return new EqualsBuilder()
            .append(this.getObjectId(), castOther.getObjectId())
            .append(this.getCodeTypeId(), castOther.getCodeTypeId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getObjectId())
            .append(getCodeTypeId())
            .toHashCode();
    }

}
