package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasSkillCategory implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasSkillCategoryPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnSkillCategory pnSkillCategory;

    /** full constructor */
    public PnSpaceHasSkillCategory(net.project.hibernate.model.PnSpaceHasSkillCategoryPK comp_id, net.project.hibernate.model.PnSkillCategory pnSkillCategory) {
        this.comp_id = comp_id;
        this.pnSkillCategory = pnSkillCategory;
    }

    /** default constructor */
    public PnSpaceHasSkillCategory() {
    }

    /** minimal constructor */
    public PnSpaceHasSkillCategory(net.project.hibernate.model.PnSpaceHasSkillCategoryPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceHasSkillCategoryPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasSkillCategoryPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSkillCategory getPnSkillCategory() {
        return this.pnSkillCategory;
    }

    public void setPnSkillCategory(net.project.hibernate.model.PnSkillCategory pnSkillCategory) {
        this.pnSkillCategory = pnSkillCategory;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasSkillCategory) ) return false;
        PnSpaceHasSkillCategory castOther = (PnSpaceHasSkillCategory) other;
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
