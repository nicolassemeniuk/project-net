package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPostHistoryPK implements Serializable {

    /** identifier field */
    private BigDecimal postId;

    /** identifier field */
    private BigDecimal discussionGroupId;

    /** identifier field */
    private BigDecimal postHistoryId;

    /** full constructor */
    public PnPostHistoryPK(BigDecimal postId, BigDecimal discussionGroupId, BigDecimal postHistoryId) {
        this.postId = postId;
        this.discussionGroupId = discussionGroupId;
        this.postHistoryId = postHistoryId;
    }

    /** default constructor */
    public PnPostHistoryPK() {
    }

    public BigDecimal getPostId() {
        return this.postId;
    }

    public void setPostId(BigDecimal postId) {
        this.postId = postId;
    }

    public BigDecimal getDiscussionGroupId() {
        return this.discussionGroupId;
    }

    public void setDiscussionGroupId(BigDecimal discussionGroupId) {
        this.discussionGroupId = discussionGroupId;
    }

    public BigDecimal getPostHistoryId() {
        return this.postHistoryId;
    }

    public void setPostHistoryId(BigDecimal postHistoryId) {
        this.postHistoryId = postHistoryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("postId", getPostId())
            .append("discussionGroupId", getDiscussionGroupId())
            .append("postHistoryId", getPostHistoryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPostHistoryPK) ) return false;
        PnPostHistoryPK castOther = (PnPostHistoryPK) other;
        return new EqualsBuilder()
            .append(this.getPostId(), castOther.getPostId())
            .append(this.getDiscussionGroupId(), castOther.getDiscussionGroupId())
            .append(this.getPostHistoryId(), castOther.getPostHistoryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPostId())
            .append(getDiscussionGroupId())
            .append(getPostHistoryId())
            .toHashCode();
    }

}
