package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSkillCategory implements Serializable {

    /** identifier field */
    private BigDecimal skillCategoryId;

    /** persistent field */
    private String skillCategoryName;

    /** nullable persistent field */
    private String skillCategoryDesc;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnSpaceHasSkillCategories;

    /** persistent field */
    private Set pnSkills;

    /** full constructor */
    public PnSkillCategory(BigDecimal skillCategoryId, String skillCategoryName, String skillCategoryDesc, String recordStatus, Set pnSpaceHasSkillCategories, Set pnSkills) {
        this.skillCategoryId = skillCategoryId;
        this.skillCategoryName = skillCategoryName;
        this.skillCategoryDesc = skillCategoryDesc;
        this.recordStatus = recordStatus;
        this.pnSpaceHasSkillCategories = pnSpaceHasSkillCategories;
        this.pnSkills = pnSkills;
    }

    /** default constructor */
    public PnSkillCategory() {
    }

    /** minimal constructor */
    public PnSkillCategory(BigDecimal skillCategoryId, String skillCategoryName, String recordStatus, Set pnSpaceHasSkillCategories, Set pnSkills) {
        this.skillCategoryId = skillCategoryId;
        this.skillCategoryName = skillCategoryName;
        this.recordStatus = recordStatus;
        this.pnSpaceHasSkillCategories = pnSpaceHasSkillCategories;
        this.pnSkills = pnSkills;
    }

    public BigDecimal getSkillCategoryId() {
        return this.skillCategoryId;
    }

    public void setSkillCategoryId(BigDecimal skillCategoryId) {
        this.skillCategoryId = skillCategoryId;
    }

    public String getSkillCategoryName() {
        return this.skillCategoryName;
    }

    public void setSkillCategoryName(String skillCategoryName) {
        this.skillCategoryName = skillCategoryName;
    }

    public String getSkillCategoryDesc() {
        return this.skillCategoryDesc;
    }

    public void setSkillCategoryDesc(String skillCategoryDesc) {
        this.skillCategoryDesc = skillCategoryDesc;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Set getPnSpaceHasSkillCategories() {
        return this.pnSpaceHasSkillCategories;
    }

    public void setPnSpaceHasSkillCategories(Set pnSpaceHasSkillCategories) {
        this.pnSpaceHasSkillCategories = pnSpaceHasSkillCategories;
    }

    public Set getPnSkills() {
        return this.pnSkills;
    }

    public void setPnSkills(Set pnSkills) {
        this.pnSkills = pnSkills;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("skillCategoryId", getSkillCategoryId())
            .toString();
    }

}
