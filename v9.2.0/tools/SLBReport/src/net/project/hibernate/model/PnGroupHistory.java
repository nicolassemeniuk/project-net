package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnGroupHistory implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnGroupHistoryPK comp_id;

    /** nullable persistent field */
    private String actionComment;

    /** nullable persistent field */
    private Date actionDate;

    /** nullable persistent field */
    private String actionName;

    /** nullable persistent field */
    private net.project.hibernate.model.PnGroup pnGroup;

    /** persistent field */
    private net.project.hibernate.model.PnGroupActionLookup pnGroupActionLookup;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** full constructor */
    public PnGroupHistory(net.project.hibernate.model.PnGroupHistoryPK comp_id, String actionComment, Date actionDate, String actionName, net.project.hibernate.model.PnGroup pnGroup, net.project.hibernate.model.PnGroupActionLookup pnGroupActionLookup, net.project.hibernate.model.PnPerson pnPerson) {
        this.comp_id = comp_id;
        this.actionComment = actionComment;
        this.actionDate = actionDate;
        this.actionName = actionName;
        this.pnGroup = pnGroup;
        this.pnGroupActionLookup = pnGroupActionLookup;
        this.pnPerson = pnPerson;
    }

    /** default constructor */
    public PnGroupHistory() {
    }

    /** minimal constructor */
    public PnGroupHistory(net.project.hibernate.model.PnGroupHistoryPK comp_id, net.project.hibernate.model.PnGroupActionLookup pnGroupActionLookup, net.project.hibernate.model.PnPerson pnPerson) {
        this.comp_id = comp_id;
        this.pnGroupActionLookup = pnGroupActionLookup;
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnGroupHistoryPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnGroupHistoryPK comp_id) {
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

    public net.project.hibernate.model.PnGroup getPnGroup() {
        return this.pnGroup;
    }

    public void setPnGroup(net.project.hibernate.model.PnGroup pnGroup) {
        this.pnGroup = pnGroup;
    }

    public net.project.hibernate.model.PnGroupActionLookup getPnGroupActionLookup() {
        return this.pnGroupActionLookup;
    }

    public void setPnGroupActionLookup(net.project.hibernate.model.PnGroupActionLookup pnGroupActionLookup) {
        this.pnGroupActionLookup = pnGroupActionLookup;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnGroupHistory) ) return false;
        PnGroupHistory castOther = (PnGroupHistory) other;
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
