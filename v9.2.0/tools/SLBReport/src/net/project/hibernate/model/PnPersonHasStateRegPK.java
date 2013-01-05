package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonHasStateRegPK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private String stateCode;

    /** full constructor */
    public PnPersonHasStateRegPK(BigDecimal personId, String stateCode) {
        this.personId = personId;
        this.stateCode = stateCode;
    }

    /** default constructor */
    public PnPersonHasStateRegPK() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public String getStateCode() {
        return this.stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .append("stateCode", getStateCode())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonHasStateRegPK) ) return false;
        PnPersonHasStateRegPK castOther = (PnPersonHasStateRegPK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getStateCode(), castOther.getStateCode())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getStateCode())
            .toHashCode();
    }

}
