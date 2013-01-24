package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSkillHasSubskill implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSkillHasSubskillPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnSkill pnSkillByParentSkillId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnSkill pnSkillByChildSkillId;

    /** full constructor */
    public PnSkillHasSubskill(net.project.hibernate.model.PnSkillHasSubskillPK comp_id, net.project.hibernate.model.PnSkill pnSkillByParentSkillId, net.project.hibernate.model.PnSkill pnSkillByChildSkillId) {
        this.comp_id = comp_id;
        this.pnSkillByParentSkillId = pnSkillByParentSkillId;
        this.pnSkillByChildSkillId = pnSkillByChildSkillId;
    }

    /** default constructor */
    public PnSkillHasSubskill() {
    }

    /** minimal constructor */
    public PnSkillHasSubskill(net.project.hibernate.model.PnSkillHasSubskillPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSkillHasSubskillPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSkillHasSubskillPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSkill getPnSkillByParentSkillId() {
        return this.pnSkillByParentSkillId;
    }

    public void setPnSkillByParentSkillId(net.project.hibernate.model.PnSkill pnSkillByParentSkillId) {
        this.pnSkillByParentSkillId = pnSkillByParentSkillId;
    }

    public net.project.hibernate.model.PnSkill getPnSkillByChildSkillId() {
        return this.pnSkillByChildSkillId;
    }

    public void setPnSkillByChildSkillId(net.project.hibernate.model.PnSkill pnSkillByChildSkillId) {
        this.pnSkillByChildSkillId = pnSkillByChildSkillId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSkillHasSubskill) ) return false;
        PnSkillHasSubskill castOther = (PnSkillHasSubskill) other;
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
