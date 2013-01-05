package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasSkillPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal skillId;

    /** full constructor */
    public PnSpaceHasSkillPK(BigDecimal spaceId, BigDecimal skillId) {
        this.spaceId = spaceId;
        this.skillId = skillId;
    }

    /** default constructor */
    public PnSpaceHasSkillPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getSkillId() {
        return this.skillId;
    }

    public void setSkillId(BigDecimal skillId) {
        this.skillId = skillId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("skillId", getSkillId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasSkillPK) ) return false;
        PnSpaceHasSkillPK castOther = (PnSpaceHasSkillPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getSkillId(), castOther.getSkillId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getSkillId())
            .toHashCode();
    }

}
