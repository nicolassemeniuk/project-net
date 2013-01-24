package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSkillHasSubskillPK implements Serializable {

    /** identifier field */
    private BigDecimal parentSkillId;

    /** identifier field */
    private BigDecimal childSkillId;

    /** full constructor */
    public PnSkillHasSubskillPK(BigDecimal parentSkillId, BigDecimal childSkillId) {
        this.parentSkillId = parentSkillId;
        this.childSkillId = childSkillId;
    }

    /** default constructor */
    public PnSkillHasSubskillPK() {
    }

    public BigDecimal getParentSkillId() {
        return this.parentSkillId;
    }

    public void setParentSkillId(BigDecimal parentSkillId) {
        this.parentSkillId = parentSkillId;
    }

    public BigDecimal getChildSkillId() {
        return this.childSkillId;
    }

    public void setChildSkillId(BigDecimal childSkillId) {
        this.childSkillId = childSkillId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("parentSkillId", getParentSkillId())
            .append("childSkillId", getChildSkillId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSkillHasSubskillPK) ) return false;
        PnSkillHasSubskillPK castOther = (PnSkillHasSubskillPK) other;
        return new EqualsBuilder()
            .append(this.getParentSkillId(), castOther.getParentSkillId())
            .append(this.getChildSkillId(), castOther.getChildSkillId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getParentSkillId())
            .append(getChildSkillId())
            .toHashCode();
    }

}
