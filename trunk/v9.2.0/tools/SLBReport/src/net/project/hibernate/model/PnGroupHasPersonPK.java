package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnGroupHasPersonPK implements Serializable {

    /** identifier field */
    private BigDecimal groupId;

    /** identifier field */
    private BigDecimal personId;

    /** full constructor */
    public PnGroupHasPersonPK(BigDecimal groupId, BigDecimal personId) {
        this.groupId = groupId;
        this.personId = personId;
    }

    /** default constructor */
    public PnGroupHasPersonPK() {
    }

    public BigDecimal getGroupId() {
        return this.groupId;
    }

    public void setGroupId(BigDecimal groupId) {
        this.groupId = groupId;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("groupId", getGroupId())
            .append("personId", getPersonId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnGroupHasPersonPK) ) return false;
        PnGroupHasPersonPK castOther = (PnGroupHasPersonPK) other;
        return new EqualsBuilder()
            .append(this.getGroupId(), castOther.getGroupId())
            .append(this.getPersonId(), castOther.getPersonId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getGroupId())
            .append(getPersonId())
            .toHashCode();
    }

}
