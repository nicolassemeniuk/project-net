package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnUserHasMasterBusinessPK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private BigDecimal businessId;

    /** full constructor */
    public PnUserHasMasterBusinessPK(BigDecimal personId, BigDecimal businessId) {
        this.personId = personId;
        this.businessId = businessId;
    }

    /** default constructor */
    public PnUserHasMasterBusinessPK() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public BigDecimal getBusinessId() {
        return this.businessId;
    }

    public void setBusinessId(BigDecimal businessId) {
        this.businessId = businessId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .append("businessId", getBusinessId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnUserHasMasterBusinessPK) ) return false;
        PnUserHasMasterBusinessPK castOther = (PnUserHasMasterBusinessPK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getBusinessId(), castOther.getBusinessId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getBusinessId())
            .toHashCode();
    }

}
