package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnGroupHistoryPK implements Serializable {

    /** identifier field */
    private BigDecimal groupId;

    /** identifier field */
    private BigDecimal groupHistoryId;

    /** full constructor */
    public PnGroupHistoryPK(BigDecimal groupId, BigDecimal groupHistoryId) {
        this.groupId = groupId;
        this.groupHistoryId = groupHistoryId;
    }

    /** default constructor */
    public PnGroupHistoryPK() {
    }

    public BigDecimal getGroupId() {
        return this.groupId;
    }

    public void setGroupId(BigDecimal groupId) {
        this.groupId = groupId;
    }

    public BigDecimal getGroupHistoryId() {
        return this.groupHistoryId;
    }

    public void setGroupHistoryId(BigDecimal groupHistoryId) {
        this.groupHistoryId = groupHistoryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("groupId", getGroupId())
            .append("groupHistoryId", getGroupHistoryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnGroupHistoryPK) ) return false;
        PnGroupHistoryPK castOther = (PnGroupHistoryPK) other;
        return new EqualsBuilder()
            .append(this.getGroupId(), castOther.getGroupId())
            .append(this.getGroupHistoryId(), castOther.getGroupHistoryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getGroupId())
            .append(getGroupHistoryId())
            .toHashCode();
    }

}
