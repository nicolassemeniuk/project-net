package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPostPK implements Serializable {

    /** identifier field */
    private BigDecimal postId;

    /** identifier field */
    private BigDecimal discussionGroupId;

    /** full constructor */
    public PnPostPK(BigDecimal postId, BigDecimal discussionGroupId) {
        this.postId = postId;
        this.discussionGroupId = discussionGroupId;
    }

    /** default constructor */
    public PnPostPK() {
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

    public String toString() {
        return new ToStringBuilder(this)
            .append("postId", getPostId())
            .append("discussionGroupId", getDiscussionGroupId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPostPK) ) return false;
        PnPostPK castOther = (PnPostPK) other;
        return new EqualsBuilder()
            .append(this.getPostId(), castOther.getPostId())
            .append(this.getDiscussionGroupId(), castOther.getDiscussionGroupId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPostId())
            .append(getDiscussionGroupId())
            .toHashCode();
    }

}
