package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonSkillComment implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPersonSkillCommentPK comp_id;

    /** nullable persistent field */
    private Date dateAdded;

    /** nullable persistent field */
    private String personComment;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPersonByPersonId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPersonHasSkill pnPersonHasSkill;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPersonByAddedBy;

    /** full constructor */
    public PnPersonSkillComment(net.project.hibernate.model.PnPersonSkillCommentPK comp_id, Date dateAdded, String personComment, String recordStatus, net.project.hibernate.model.PnPerson pnPersonByPersonId, net.project.hibernate.model.PnPersonHasSkill pnPersonHasSkill, net.project.hibernate.model.PnPerson pnPersonByAddedBy) {
        this.comp_id = comp_id;
        this.dateAdded = dateAdded;
        this.personComment = personComment;
        this.recordStatus = recordStatus;
        this.pnPersonByPersonId = pnPersonByPersonId;
        this.pnPersonHasSkill = pnPersonHasSkill;
        this.pnPersonByAddedBy = pnPersonByAddedBy;
    }

    /** default constructor */
    public PnPersonSkillComment() {
    }

    /** minimal constructor */
    public PnPersonSkillComment(net.project.hibernate.model.PnPersonSkillCommentPK comp_id, String recordStatus, net.project.hibernate.model.PnPerson pnPersonByAddedBy) {
        this.comp_id = comp_id;
        this.recordStatus = recordStatus;
        this.pnPersonByAddedBy = pnPersonByAddedBy;
    }

    public net.project.hibernate.model.PnPersonSkillCommentPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPersonSkillCommentPK comp_id) {
        this.comp_id = comp_id;
    }

    public Date getDateAdded() {
        return this.dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getPersonComment() {
        return this.personComment;
    }

    public void setPersonComment(String personComment) {
        this.personComment = personComment;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnPerson getPnPersonByPersonId() {
        return this.pnPersonByPersonId;
    }

    public void setPnPersonByPersonId(net.project.hibernate.model.PnPerson pnPersonByPersonId) {
        this.pnPersonByPersonId = pnPersonByPersonId;
    }

    public net.project.hibernate.model.PnPersonHasSkill getPnPersonHasSkill() {
        return this.pnPersonHasSkill;
    }

    public void setPnPersonHasSkill(net.project.hibernate.model.PnPersonHasSkill pnPersonHasSkill) {
        this.pnPersonHasSkill = pnPersonHasSkill;
    }

    public net.project.hibernate.model.PnPerson getPnPersonByAddedBy() {
        return this.pnPersonByAddedBy;
    }

    public void setPnPersonByAddedBy(net.project.hibernate.model.PnPerson pnPersonByAddedBy) {
        this.pnPersonByAddedBy = pnPersonByAddedBy;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonSkillComment) ) return false;
        PnPersonSkillComment castOther = (PnPersonSkillComment) other;
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
