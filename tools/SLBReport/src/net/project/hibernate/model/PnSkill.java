package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSkill implements Serializable {

    /** identifier field */
    private BigDecimal skillId;

    /** persistent field */
    private String skillName;

    /** nullable persistent field */
    private String skillDesc;

    /** nullable persistent field */
    private BigDecimal parentSkillId;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private net.project.hibernate.model.PnSkillCategory pnSkillCategory;

    /** persistent field */
    private Set pnPersonHasSkills;

    /** persistent field */
    private Set pnSkillHasSubskillsByParentSkillId;

    /** persistent field */
    private Set pnSkillHasSubskillsByChildSkillId;

    /** persistent field */
    private Set pnSpaceHasSkills;

    /** full constructor */
    public PnSkill(BigDecimal skillId, String skillName, String skillDesc, BigDecimal parentSkillId, String recordStatus, net.project.hibernate.model.PnSkillCategory pnSkillCategory, Set pnPersonHasSkills, Set pnSkillHasSubskillsByParentSkillId, Set pnSkillHasSubskillsByChildSkillId, Set pnSpaceHasSkills) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.skillDesc = skillDesc;
        this.parentSkillId = parentSkillId;
        this.recordStatus = recordStatus;
        this.pnSkillCategory = pnSkillCategory;
        this.pnPersonHasSkills = pnPersonHasSkills;
        this.pnSkillHasSubskillsByParentSkillId = pnSkillHasSubskillsByParentSkillId;
        this.pnSkillHasSubskillsByChildSkillId = pnSkillHasSubskillsByChildSkillId;
        this.pnSpaceHasSkills = pnSpaceHasSkills;
    }

    /** default constructor */
    public PnSkill() {
    }

    /** minimal constructor */
    public PnSkill(BigDecimal skillId, String skillName, String recordStatus, net.project.hibernate.model.PnSkillCategory pnSkillCategory, Set pnPersonHasSkills, Set pnSkillHasSubskillsByParentSkillId, Set pnSkillHasSubskillsByChildSkillId, Set pnSpaceHasSkills) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.recordStatus = recordStatus;
        this.pnSkillCategory = pnSkillCategory;
        this.pnPersonHasSkills = pnPersonHasSkills;
        this.pnSkillHasSubskillsByParentSkillId = pnSkillHasSubskillsByParentSkillId;
        this.pnSkillHasSubskillsByChildSkillId = pnSkillHasSubskillsByChildSkillId;
        this.pnSpaceHasSkills = pnSpaceHasSkills;
    }

    public BigDecimal getSkillId() {
        return this.skillId;
    }

    public void setSkillId(BigDecimal skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return this.skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillDesc() {
        return this.skillDesc;
    }

    public void setSkillDesc(String skillDesc) {
        this.skillDesc = skillDesc;
    }

    public BigDecimal getParentSkillId() {
        return this.parentSkillId;
    }

    public void setParentSkillId(BigDecimal parentSkillId) {
        this.parentSkillId = parentSkillId;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnSkillCategory getPnSkillCategory() {
        return this.pnSkillCategory;
    }

    public void setPnSkillCategory(net.project.hibernate.model.PnSkillCategory pnSkillCategory) {
        this.pnSkillCategory = pnSkillCategory;
    }

    public Set getPnPersonHasSkills() {
        return this.pnPersonHasSkills;
    }

    public void setPnPersonHasSkills(Set pnPersonHasSkills) {
        this.pnPersonHasSkills = pnPersonHasSkills;
    }

    public Set getPnSkillHasSubskillsByParentSkillId() {
        return this.pnSkillHasSubskillsByParentSkillId;
    }

    public void setPnSkillHasSubskillsByParentSkillId(Set pnSkillHasSubskillsByParentSkillId) {
        this.pnSkillHasSubskillsByParentSkillId = pnSkillHasSubskillsByParentSkillId;
    }

    public Set getPnSkillHasSubskillsByChildSkillId() {
        return this.pnSkillHasSubskillsByChildSkillId;
    }

    public void setPnSkillHasSubskillsByChildSkillId(Set pnSkillHasSubskillsByChildSkillId) {
        this.pnSkillHasSubskillsByChildSkillId = pnSkillHasSubskillsByChildSkillId;
    }

    public Set getPnSpaceHasSkills() {
        return this.pnSpaceHasSkills;
    }

    public void setPnSpaceHasSkills(Set pnSpaceHasSkills) {
        this.pnSpaceHasSkills = pnSpaceHasSkills;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("skillId", getSkillId())
            .toString();
    }

}
