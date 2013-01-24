package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDiscussionHistory implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnDiscussionHistoryPK comp_id;

    /** nullable persistent field */
    private String actionComment;

    /** nullable persistent field */
    private Date actionDate;

    /** nullable persistent field */
    private String actionName;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDiscussionGroup pnDiscussionGroup;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnDiscussionActionLookup pnDiscussionActionLookup;

    /** full constructor */
    public PnDiscussionHistory(net.project.hibernate.model.PnDiscussionHistoryPK comp_id, String actionComment, Date actionDate, String actionName, net.project.hibernate.model.PnDiscussionGroup pnDiscussionGroup, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnDiscussionActionLookup pnDiscussionActionLookup) {
        this.comp_id = comp_id;
        this.actionComment = actionComment;
        this.actionDate = actionDate;
        this.actionName = actionName;
        this.pnDiscussionGroup = pnDiscussionGroup;
        this.pnPerson = pnPerson;
        this.pnDiscussionActionLookup = pnDiscussionActionLookup;
    }

    /** default constructor */
    public PnDiscussionHistory() {
    }

    /** minimal constructor */
    public PnDiscussionHistory(net.project.hibernate.model.PnDiscussionHistoryPK comp_id, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnDiscussionActionLookup pnDiscussionActionLookup) {
        this.comp_id = comp_id;
        this.pnPerson = pnPerson;
        this.pnDiscussionActionLookup = pnDiscussionActionLookup;
    }

    public net.project.hibernate.model.PnDiscussionHistoryPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnDiscussionHistoryPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getActionComment() {
        return this.actionComment;
    }

    public void setActionComment(String actionComment) {
        this.actionComment = actionComment;
    }

    public Date getActionDate() {
        return this.actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public String getActionName() {
        return this.actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public net.project.hibernate.model.PnDiscussionGroup getPnDiscussionGroup() {
        return this.pnDiscussionGroup;
    }

    public void setPnDiscussionGroup(net.project.hibernate.model.PnDiscussionGroup pnDiscussionGroup) {
        this.pnDiscussionGroup = pnDiscussionGroup;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnDiscussionActionLookup getPnDiscussionActionLookup() {
        return this.pnDiscussionActionLookup;
    }

    public void setPnDiscussionActionLookup(net.project.hibernate.model.PnDiscussionActionLookup pnDiscussionActionLookup) {
        this.pnDiscussionActionLookup = pnDiscussionActionLookup;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDiscussionHistory) ) return false;
        PnDiscussionHistory castOther = (PnDiscussionHistory) other;
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
