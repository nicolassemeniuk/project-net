package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnObjectHasDiscussionPK implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** identifier field */
    private BigDecimal discussionGroupId;

    /** full constructor */
    public PnObjectHasDiscussionPK(BigDecimal objectId, BigDecimal discussionGroupId) {
        this.objectId = objectId;
        this.discussionGroupId = discussionGroupId;
    }

    /** default constructor */
    public PnObjectHasDiscussionPK() {
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public BigDecimal getDiscussionGroupId() {
        return this.discussionGroupId;
    }

    public void setDiscussionGroupId(BigDecimal discussionGroupId) {
        this.discussionGroupId = discussionGroupId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .append("discussionGroupId", getDiscussionGroupId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnObjectHasDiscussionPK) ) return false;
        PnObjectHasDiscussionPK castOther = (PnObjectHasDiscussionPK) other;
        return new EqualsBuilder()
            .append(this.getObjectId(), castOther.getObjectId())
            .append(this.getDiscussionGroupId(), castOther.getDiscussionGroupId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getObjectId())
            .append(getDiscussionGroupId())
            .toHashCode();
    }

}
