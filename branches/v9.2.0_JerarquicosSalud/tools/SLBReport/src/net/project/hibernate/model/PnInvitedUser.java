package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnInvitedUser implements Serializable {

    /** identifier field */
    private Integer invitationCode;

    /** persistent field */
    private BigDecimal spaceId;

    /** persistent field */
    private String inviteeEmail;

    /** persistent field */
    private Date dateInvited;

    /** nullable persistent field */
    private Date dateResponded;

    /** nullable persistent field */
    private String inviteeFirstname;

    /** nullable persistent field */
    private String inviteeLastname;

    /** nullable persistent field */
    private String inviteeResponsibilities;

    /** persistent field */
    private String invitedStatus;

    /** persistent field */
    private int invitationActedUpon;

    /** persistent field */
    private BigDecimal personId;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** full constructor */
    public PnInvitedUser(Integer invitationCode, BigDecimal spaceId, String inviteeEmail, Date dateInvited, Date dateResponded, String inviteeFirstname, String inviteeLastname, String inviteeResponsibilities, String invitedStatus, int invitationActedUpon, BigDecimal personId, net.project.hibernate.model.PnPerson pnPerson) {
        this.invitationCode = invitationCode;
        this.spaceId = spaceId;
        this.inviteeEmail = inviteeEmail;
        this.dateInvited = dateInvited;
        this.dateResponded = dateResponded;
        this.inviteeFirstname = inviteeFirstname;
        this.inviteeLastname = inviteeLastname;
        this.inviteeResponsibilities = inviteeResponsibilities;
        this.invitedStatus = invitedStatus;
        this.invitationActedUpon = invitationActedUpon;
        this.personId = personId;
        this.pnPerson = pnPerson;
    }

    /** default constructor */
    public PnInvitedUser() {
    }

    /** minimal constructor */
    public PnInvitedUser(Integer invitationCode, BigDecimal spaceId, String inviteeEmail, Date dateInvited, String invitedStatus, int invitationActedUpon, BigDecimal personId, net.project.hibernate.model.PnPerson pnPerson) {
        this.invitationCode = invitationCode;
        this.spaceId = spaceId;
        this.inviteeEmail = inviteeEmail;
        this.dateInvited = dateInvited;
        this.invitedStatus = invitedStatus;
        this.invitationActedUpon = invitationActedUpon;
        this.personId = personId;
        this.pnPerson = pnPerson;
    }

    public Integer getInvitationCode() {
        return this.invitationCode;
    }

    public void setInvitationCode(Integer invitationCode) {
        this.invitationCode = invitationCode;
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public String getInviteeEmail() {
        return this.inviteeEmail;
    }

    public void setInviteeEmail(String inviteeEmail) {
        this.inviteeEmail = inviteeEmail;
    }

    public Date getDateInvited() {
        return this.dateInvited;
    }

    public void setDateInvited(Date dateInvited) {
        this.dateInvited = dateInvited;
    }

    public Date getDateResponded() {
        return this.dateResponded;
    }

    public void setDateResponded(Date dateResponded) {
        this.dateResponded = dateResponded;
    }

    public String getInviteeFirstname() {
        return this.inviteeFirstname;
    }

    public void setInviteeFirstname(String inviteeFirstname) {
        this.inviteeFirstname = inviteeFirstname;
    }

    public String getInviteeLastname() {
        return this.inviteeLastname;
    }

    public void setInviteeLastname(String inviteeLastname) {
        this.inviteeLastname = inviteeLastname;
    }

    public String getInviteeResponsibilities() {
        return this.inviteeResponsibilities;
    }

    public void setInviteeResponsibilities(String inviteeResponsibilities) {
        this.inviteeResponsibilities = inviteeResponsibilities;
    }

    public String getInvitedStatus() {
        return this.invitedStatus;
    }

    public void setInvitedStatus(String invitedStatus) {
        this.invitedStatus = invitedStatus;
    }

    public int getInvitationActedUpon() {
        return this.invitationActedUpon;
    }

    public void setInvitationActedUpon(int invitationActedUpon) {
        this.invitationActedUpon = invitationActedUpon;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("invitationCode", getInvitationCode())
            .toString();
    }

}
