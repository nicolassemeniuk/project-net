package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnGroupHasGroupPK implements Serializable {

    /** identifier field */
    private BigDecimal groupId;

    /** identifier field */
    private BigDecimal memberGroupId;

    /** full constructor */
    public PnGroupHasGroupPK(BigDecimal groupId, BigDecimal memberGroupId) {
        this.groupId = groupId;
        this.memberGroupId = memberGroupId;
    }

    /** default constructor */
    public PnGroupHasGroupPK() {
    }

    public BigDecimal getGroupId() {
        return this.groupId;
    }

    public void setGroupId(BigDecimal groupId) {
        this.groupId = groupId;
    }

    public BigDecimal getMemberGroupId() {
        return this.memberGroupId;
    }

    public void setMemberGroupId(BigDecimal memberGroupId) {
        this.memberGroupId = memberGroupId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("groupId", getGroupId())
            .append("memberGroupId", getMemberGroupId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnGroupHasGroupPK) ) return false;
        PnGroupHasGroupPK castOther = (PnGroupHasGroupPK) other;
        return new EqualsBuilder()
            .append(this.getGroupId(), castOther.getGroupId())
            .append(this.getMemberGroupId(), castOther.getMemberGroupId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getGroupId())
            .append(getMemberGroupId())
            .toHashCode();
    }

}
