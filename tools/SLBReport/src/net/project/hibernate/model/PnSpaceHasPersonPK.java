package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasPersonPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal personId;

    /** full constructor */
    public PnSpaceHasPersonPK(BigDecimal spaceId, BigDecimal personId) {
        this.spaceId = spaceId;
        this.personId = personId;
    }

    /** default constructor */
    public PnSpaceHasPersonPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("personId", getPersonId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasPersonPK) ) return false;
        PnSpaceHasPersonPK castOther = (PnSpaceHasPersonPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getPersonId(), castOther.getPersonId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getPersonId())
            .toHashCode();
    }

}
