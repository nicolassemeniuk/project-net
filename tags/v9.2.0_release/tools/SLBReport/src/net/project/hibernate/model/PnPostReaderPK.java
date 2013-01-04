package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPostReaderPK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private BigDecimal postId;

    /** identifier field */
    private BigDecimal discussionGroupId;

    /** full constructor */
    public PnPostReaderPK(BigDecimal personId, BigDecimal postId, BigDecimal discussionGroupId) {
        this.personId = personId;
        this.postId = postId;
        this.discussionGroupId = discussionGroupId;
    }

    /** default constructor */
    public PnPostReaderPK() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
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
            .append("personId", getPersonId())
            .append("postId", getPostId())
            .append("discussionGroupId", getDiscussionGroupId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPostReaderPK) ) return false;
        PnPostReaderPK castOther = (PnPostReaderPK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getPostId(), castOther.getPostId())
            .append(this.getDiscussionGroupId(), castOther.getDiscussionGroupId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getPostId())
            .append(getDiscussionGroupId())
            .toHashCode();
    }

}
