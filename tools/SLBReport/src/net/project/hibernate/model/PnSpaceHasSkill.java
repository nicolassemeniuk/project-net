package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasSkill implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasSkillPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnSkill pnSkill;

    /** full constructor */
    public PnSpaceHasSkill(net.project.hibernate.model.PnSpaceHasSkillPK comp_id, net.project.hibernate.model.PnSkill pnSkill) {
        this.comp_id = comp_id;
        this.pnSkill = pnSkill;
    }

    /** default constructor */
    public PnSpaceHasSkill() {
    }

    /** minimal constructor */
    public PnSpaceHasSkill(net.project.hibernate.model.PnSpaceHasSkillPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceHasSkillPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasSkillPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSkill getPnSkill() {
        return this.pnSkill;
    }

    public void setPnSkill(net.project.hibernate.model.PnSkill pnSkill) {
        this.pnSkill = pnSkill;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasSkill) ) return false;
        PnSpaceHasSkill castOther = (PnSpaceHasSkill) other;
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
