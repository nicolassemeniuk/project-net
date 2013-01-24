package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocHistory implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnDocHistoryPK comp_id;

    /** nullable persistent field */
    private String actionComment;

    /** nullable persistent field */
    private Date actionDate;

    /** nullable persistent field */
    private String actionName;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocument pnDocument;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnDocActionLookup pnDocActionLookup;

    /** full constructor */
    public PnDocHistory(net.project.hibernate.model.PnDocHistoryPK comp_id, String actionComment, Date actionDate, String actionName, net.project.hibernate.model.PnDocument pnDocument, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnDocActionLookup pnDocActionLookup) {
        this.comp_id = comp_id;
        this.actionComment = actionComment;
        this.actionDate = actionDate;
        this.actionName = actionName;
        this.pnDocument = pnDocument;
        this.pnPerson = pnPerson;
        this.pnDocActionLookup = pnDocActionLookup;
    }

    /** default constructor */
    public PnDocHistory() {
    }

    /** minimal constructor */
    public PnDocHistory(net.project.hibernate.model.PnDocHistoryPK comp_id, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnDocActionLookup pnDocActionLookup) {
        this.comp_id = comp_id;
        this.pnPerson = pnPerson;
        this.pnDocActionLookup = pnDocActionLookup;
    }

    public net.project.hibernate.model.PnDocHistoryPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnDocHistoryPK comp_id) {
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

    public net.project.hibernate.model.PnDocument getPnDocument() {
        return this.pnDocument;
    }

    public void setPnDocument(net.project.hibernate.model.PnDocument pnDocument) {
        this.pnDocument = pnDocument;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnDocActionLookup getPnDocActionLookup() {
        return this.pnDocActionLookup;
    }

    public void setPnDocActionLookup(net.project.hibernate.model.PnDocActionLookup pnDocActionLookup) {
        this.pnDocActionLookup = pnDocActionLookup;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocHistory) ) return false;
        PnDocHistory castOther = (PnDocHistory) other;
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
