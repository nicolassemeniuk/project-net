package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonSkillCommentPK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private BigDecimal skillId;

    /** identifier field */
    private BigDecimal commentId;

    /** full constructor */
    public PnPersonSkillCommentPK(BigDecimal personId, BigDecimal skillId, BigDecimal commentId) {
        this.personId = personId;
        this.skillId = skillId;
        this.commentId = commentId;
    }

    /** default constructor */
    public PnPersonSkillCommentPK() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public BigDecimal getSkillId() {
        return this.skillId;
    }

    public void setSkillId(BigDecimal skillId) {
        this.skillId = skillId;
    }

    public BigDecimal getCommentId() {
        return this.commentId;
    }

    public void setCommentId(BigDecimal commentId) {
        this.commentId = commentId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .append("skillId", getSkillId())
            .append("commentId", getCommentId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonSkillCommentPK) ) return false;
        PnPersonSkillCommentPK castOther = (PnPersonSkillCommentPK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getSkillId(), castOther.getSkillId())
            .append(this.getCommentId(), castOther.getCommentId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getSkillId())
            .append(getCommentId())
            .toHashCode();
    }

}
