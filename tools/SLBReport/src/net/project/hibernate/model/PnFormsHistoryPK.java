package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnFormsHistoryPK implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** identifier field */
    private BigDecimal formsHistoryId;

    /** full constructor */
    public PnFormsHistoryPK(BigDecimal objectId, BigDecimal formsHistoryId) {
        this.objectId = objectId;
        this.formsHistoryId = formsHistoryId;
    }

    /** default constructor */
    public PnFormsHistoryPK() {
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public BigDecimal getFormsHistoryId() {
        return this.formsHistoryId;
    }

    public void setFormsHistoryId(BigDecimal formsHistoryId) {
        this.formsHistoryId = formsHistoryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .append("formsHistoryId", getFormsHistoryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnFormsHistoryPK) ) return false;
        PnFormsHistoryPK castOther = (PnFormsHistoryPK) other;
        return new EqualsBuilder()
            .append(this.getObjectId(), castOther.getObjectId())
            .append(this.getFormsHistoryId(), castOther.getFormsHistoryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getObjectId())
            .append(getFormsHistoryId())
            .toHashCode();
    }

}
