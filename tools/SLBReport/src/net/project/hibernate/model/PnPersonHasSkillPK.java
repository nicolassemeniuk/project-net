package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonHasSkillPK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private BigDecimal skillId;

    /** full constructor */
    public PnPersonHasSkillPK(BigDecimal personId, BigDecimal skillId) {
        this.personId = personId;
        this.skillId = skillId;
    }

    /** default constructor */
    public PnPersonHasSkillPK() {
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
            .append("personId", getPersonId())
            .append("skillId", getSkillId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonHasSkillPK) ) return false;
        PnPersonHasSkillPK castOther = (PnPersonHasSkillPK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getSkillId(), castOther.getSkillId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getSkillId())
            .toHashCode();
    }

}
