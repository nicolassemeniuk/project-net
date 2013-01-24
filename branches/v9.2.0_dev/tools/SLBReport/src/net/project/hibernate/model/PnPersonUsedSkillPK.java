package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonUsedSkillPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private BigDecimal skillId;

    /** full constructor */
    public PnPersonUsedSkillPK(BigDecimal spaceId, BigDecimal personId, BigDecimal skillId) {
        this.spaceId = spaceId;
        this.personId = personId;
        this.skillId = skillId;
    }

    /** default constructor */
    public PnPersonUsedSkillPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
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

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("personId", getPersonId())
            .append("skillId", getSkillId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonUsedSkillPK) ) return false;
        PnPersonUsedSkillPK castOther = (PnPersonUsedSkillPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getSkillId(), castOther.getSkillId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getPersonId())
            .append(getSkillId())
            .toHashCode();
    }

}
