package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasSkillCategoryPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal skillCategoryId;

    /** full constructor */
    public PnSpaceHasSkillCategoryPK(BigDecimal spaceId, BigDecimal skillCategoryId) {
        this.spaceId = spaceId;
        this.skillCategoryId = skillCategoryId;
    }

    /** default constructor */
    public PnSpaceHasSkillCategoryPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getSkillCategoryId() {
        return this.skillCategoryId;
    }

    public void setSkillCategoryId(BigDecimal skillCategoryId) {
        this.skillCategoryId = skillCategoryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("skillCategoryId", getSkillCategoryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasSkillCategoryPK) ) return false;
        PnSpaceHasSkillCategoryPK castOther = (PnSpaceHasSkillCategoryPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getSkillCategoryId(), castOther.getSkillCategoryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getSkillCategoryId())
            .toHashCode();
    }

}
