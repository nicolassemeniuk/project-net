package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTaskHistory implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnTaskHistoryPK comp_id;

    /** nullable persistent field */
    private String actionComment;

    /** nullable persistent field */
    private Date actionDate;

    /** nullable persistent field */
    private String actionName;

    /** nullable persistent field */
    private net.project.hibernate.model.PnTask pnTask;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnTaskActionLookup pnTaskActionLookup;

    /** full constructor */
    public PnTaskHistory(net.project.hibernate.model.PnTaskHistoryPK comp_id, String actionComment, Date actionDate, String actionName, net.project.hibernate.model.PnTask pnTask, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnTaskActionLookup pnTaskActionLookup) {
        this.comp_id = comp_id;
        this.actionComment = actionComment;
        this.actionDate = actionDate;
        this.actionName = actionName;
        this.pnTask = pnTask;
        this.pnPerson = pnPerson;
        this.pnTaskActionLookup = pnTaskActionLookup;
    }

    /** default constructor */
    public PnTaskHistory() {
    }

    /** minimal constructor */
    public PnTaskHistory(net.project.hibernate.model.PnTaskHistoryPK comp_id, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnTaskActionLookup pnTaskActionLookup) {
        this.comp_id = comp_id;
        this.pnPerson = pnPerson;
        this.pnTaskActionLookup = pnTaskActionLookup;
    }

    public net.project.hibernate.model.PnTaskHistoryPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnTaskHistoryPK comp_id) {
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

    public net.project.hibernate.model.PnTask getPnTask() {
        return this.pnTask;
    }

    public void setPnTask(net.project.hibernate.model.PnTask pnTask) {
        this.pnTask = pnTask;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnTaskActionLookup getPnTaskActionLookup() {
        return this.pnTaskActionLookup;
    }

    public void setPnTaskActionLookup(net.project.hibernate.model.PnTaskActionLookup pnTaskActionLookup) {
        this.pnTaskActionLookup = pnTaskActionLookup;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnTaskHistory) ) return false;
        PnTaskHistory castOther = (PnTaskHistory) other;
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
