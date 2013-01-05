package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonUsedSkill implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPersonUsedSkillPK comp_id;

    /** nullable persistent field */
    private Date startDate;

    /** nullable persistent field */
    private Date endDate;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPersonHasSkill pnPersonHasSkill;

    /** full constructor */
    public PnPersonUsedSkill(net.project.hibernate.model.PnPersonUsedSkillPK comp_id, Date startDate, Date endDate, String description, net.project.hibernate.model.PnPersonHasSkill pnPersonHasSkill) {
        this.comp_id = comp_id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.pnPersonHasSkill = pnPersonHasSkill;
    }

    /** default constructor */
    public PnPersonUsedSkill() {
    }

    /** minimal constructor */
    public PnPersonUsedSkill(net.project.hibernate.model.PnPersonUsedSkillPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPersonUsedSkillPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPersonUsedSkillPK comp_id) {
        this.comp_id = comp_id;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public net.project.hibernate.model.PnPersonHasSkill getPnPersonHasSkill() {
        return this.pnPersonHasSkill;
    }

    public void setPnPersonHasSkill(net.project.hibernate.model.PnPersonHasSkill pnPersonHasSkill) {
        this.pnPersonHasSkill = pnPersonHasSkill;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonUsedSkill) ) return false;
        PnPersonUsedSkill castOther = (PnPersonUsedSkill) other;
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
