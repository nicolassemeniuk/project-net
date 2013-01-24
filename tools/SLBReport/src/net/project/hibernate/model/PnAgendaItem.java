package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnAgendaItem implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnAgendaItemPK comp_id;

    /** persistent field */
    private String itemName;

    /** nullable persistent field */
    private String itemDesc;

    /** nullable persistent field */
    private String timeAlloted;

    /** persistent field */
    private BigDecimal statusId;

    /** nullable persistent field */
    private Integer itemSequence;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Clob meetingNotesClob;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** nullable persistent field */
    private net.project.hibernate.model.PnMeeting pnMeeting;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** full constructor */
    public PnAgendaItem(net.project.hibernate.model.PnAgendaItemPK comp_id, String itemName, String itemDesc, String timeAlloted, BigDecimal statusId, Integer itemSequence, String recordStatus, Clob meetingNotesClob, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnMeeting pnMeeting, net.project.hibernate.model.PnPerson pnPerson) {
        this.comp_id = comp_id;
        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.timeAlloted = timeAlloted;
        this.statusId = statusId;
        this.itemSequence = itemSequence;
        this.recordStatus = recordStatus;
        this.meetingNotesClob = meetingNotesClob;
        this.pnObject = pnObject;
        this.pnMeeting = pnMeeting;
        this.pnPerson = pnPerson;
    }

    /** default constructor */
    public PnAgendaItem() {
    }

    /** minimal constructor */
    public PnAgendaItem(net.project.hibernate.model.PnAgendaItemPK comp_id, String itemName, BigDecimal statusId, String recordStatus, net.project.hibernate.model.PnPerson pnPerson) {
        this.comp_id = comp_id;
        this.itemName = itemName;
        this.statusId = statusId;
        this.recordStatus = recordStatus;
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnAgendaItemPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnAgendaItemPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDesc() {
        return this.itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getTimeAlloted() {
        return this.timeAlloted;
    }

    public void setTimeAlloted(String timeAlloted) {
        this.timeAlloted = timeAlloted;
    }

    public BigDecimal getStatusId() {
        return this.statusId;
    }

    public void setStatusId(BigDecimal statusId) {
        this.statusId = statusId;
    }

    public Integer getItemSequence() {
        return this.itemSequence;
    }

    public void setItemSequence(Integer itemSequence) {
        this.itemSequence = itemSequence;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Clob getMeetingNotesClob() {
        return this.meetingNotesClob;
    }

    public void setMeetingNotesClob(Clob meetingNotesClob) {
        this.meetingNotesClob = meetingNotesClob;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnMeeting getPnMeeting() {
        return this.pnMeeting;
    }

    public void setPnMeeting(net.project.hibernate.model.PnMeeting pnMeeting) {
        this.pnMeeting = pnMeeting;
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
        if ( !(other instanceof PnAgendaItem) ) return false;
        PnAgendaItem castOther = (PnAgendaItem) other;
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
