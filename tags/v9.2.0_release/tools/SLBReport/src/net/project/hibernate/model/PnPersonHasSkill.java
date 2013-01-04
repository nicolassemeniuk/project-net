package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonHasSkill implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPersonHasSkillPK comp_id;

    /** persistent field */
    private BigDecimal proficiencyCode;

    /** nullable persistent field */
    private Integer monthsExperience;

    /** nullable persistent field */
    private Date mostRecentUse;

    /** nullable persistent field */
    private net.project.hibernate.model.PnSkill pnSkill;

    /** persistent field */
    private Set pnPersonUsedSkills;

    /** persistent field */
    private Set pnPersonSkillComments;

    /** full constructor */
    public PnPersonHasSkill(net.project.hibernate.model.PnPersonHasSkillPK comp_id, BigDecimal proficiencyCode, Integer monthsExperience, Date mostRecentUse, net.project.hibernate.model.PnSkill pnSkill, Set pnPersonUsedSkills, Set pnPersonSkillComments) {
        this.comp_id = comp_id;
        this.proficiencyCode = proficiencyCode;
        this.monthsExperience = monthsExperience;
        this.mostRecentUse = mostRecentUse;
        this.pnSkill = pnSkill;
        this.pnPersonUsedSkills = pnPersonUsedSkills;
        this.pnPersonSkillComments = pnPersonSkillComments;
    }

    /** default constructor */
    public PnPersonHasSkill() {
    }

    /** minimal constructor */
    public PnPersonHasSkill(net.project.hibernate.model.PnPersonHasSkillPK comp_id, BigDecimal proficiencyCode, Set pnPersonUsedSkills, Set pnPersonSkillComments) {
        this.comp_id = comp_id;
        this.proficiencyCode = proficiencyCode;
        this.pnPersonUsedSkills = pnPersonUsedSkills;
        this.pnPersonSkillComments = pnPersonSkillComments;
    }

    public net.project.hibernate.model.PnPersonHasSkillPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPersonHasSkillPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getProficiencyCode() {
        return this.proficiencyCode;
    }

    public void setProficiencyCode(BigDecimal proficiencyCode) {
        this.proficiencyCode = proficiencyCode;
    }

    public Integer getMonthsExperience() {
        return this.monthsExperience;
    }

    public void setMonthsExperience(Integer monthsExperience) {
        this.monthsExperience = monthsExperience;
    }

    public Date getMostRecentUse() {
        return this.mostRecentUse;
    }

    public void setMostRecentUse(Date mostRecentUse) {
        this.mostRecentUse = mostRecentUse;
    }

    public net.project.hibernate.model.PnSkill getPnSkill() {
        return this.pnSkill;
    }

    public void setPnSkill(net.project.hibernate.model.PnSkill pnSkill) {
        this.pnSkill = pnSkill;
    }

    public Set getPnPersonUsedSkills() {
        return this.pnPersonUsedSkills;
    }

    public void setPnPersonUsedSkills(Set pnPersonUsedSkills) {
        this.pnPersonUsedSkills = pnPersonUsedSkills;
    }

    public Set getPnPersonSkillComments() {
        return this.pnPersonSkillComments;
    }

    public void setPnPersonSkillComments(Set pnPersonSkillComments) {
        this.pnPersonSkillComments = pnPersonSkillComments;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonHasSkill) ) return false;
        PnPersonHasSkill castOther = (PnPersonHasSkill) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
