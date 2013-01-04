package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDiscussionHistoryPK implements Serializable {

    /** identifier field */
    private BigDecimal discussionGroupId;

    /** identifier field */
    private BigDecimal discussionGroupHistoryId;

    /** full constructor */
    public PnDiscussionHistoryPK(BigDecimal discussionGroupId, BigDecimal discussionGroupHistoryId) {
        this.discussionGroupId = discussionGroupId;
        this.discussionGroupHistoryId = discussionGroupHistoryId;
    }

    /** default constructor */
    public PnDiscussionHistoryPK() {
    }

    public BigDecimal getDiscussionGroupId() {
        return this.discussionGroupId;
    }

    public void setDiscussionGroupId(BigDecimal discussionGroupId) {
        this.discussionGroupId = discussionGroupId;
    }

    public BigDecimal getDiscussionGroupHistoryId() {
        return this.discussionGroupHistoryId;
    }

    public void setDiscussionGroupHistoryId(BigDecimal discussionGroupHistoryId) {
        this.discussionGroupHistoryId = discussionGroupHistoryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("discussionGroupId", getDiscussionGroupId())
            .append("discussionGroupHistoryId", getDiscussionGroupHistoryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDiscussionHistoryPK) ) return false;
        PnDiscussionHistoryPK castOther = (PnDiscussionHistoryPK) other;
        return new EqualsBuilder()
            .append(this.getDiscussionGroupId(), castOther.getDiscussionGroupId())
            .append(this.getDiscussionGroupHistoryId(), castOther.getDiscussionGroupHistoryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDiscussionGroupId())
            .append(getDiscussionGroupHistoryId())
            .toHashCode();
    }

}
