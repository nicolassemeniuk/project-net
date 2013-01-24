package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPost implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPostPK comp_id;

    /** persistent field */
    private String subject;

    /** persistent field */
    private Date datePosted;

    /** nullable persistent field */
    private BigDecimal parentId;

    /** persistent field */
    private BigDecimal urgencyId;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDiscussionGroup pnDiscussionGroup;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnPostBodyClob pnPostBodyClob;

    /** persistent field */
    private Set pnPostHistories;

    /** persistent field */
    private Set pnPostReaders;

    /** full constructor */
    public PnPost(net.project.hibernate.model.PnPostPK comp_id, String subject, Date datePosted, BigDecimal parentId, BigDecimal urgencyId, String recordStatus, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnDiscussionGroup pnDiscussionGroup, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnPostBodyClob pnPostBodyClob, Set pnPostHistories, Set pnPostReaders) {
        this.comp_id = comp_id;
        this.subject = subject;
        this.datePosted = datePosted;
        this.parentId = parentId;
        this.urgencyId = urgencyId;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnDiscussionGroup = pnDiscussionGroup;
        this.pnPerson = pnPerson;
        this.pnPostBodyClob = pnPostBodyClob;
        this.pnPostHistories = pnPostHistories;
        this.pnPostReaders = pnPostReaders;
    }

    /** default constructor */
    public PnPost() {
    }

    /** minimal constructor */
    public PnPost(net.project.hibernate.model.PnPostPK comp_id, String subject, Date datePosted, BigDecimal urgencyId, String recordStatus, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnPostBodyClob pnPostBodyClob, Set pnPostHistories, Set pnPostReaders) {
        this.comp_id = comp_id;
        this.subject = subject;
        this.datePosted = datePosted;
        this.urgencyId = urgencyId;
        this.recordStatus = recordStatus;
        this.pnPerson = pnPerson;
        this.pnPostBodyClob = pnPostBodyClob;
        this.pnPostHistories = pnPostHistories;
        this.pnPostReaders = pnPostReaders;
    }

    public net.project.hibernate.model.PnPostPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPostPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getDatePosted() {
        return this.datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public BigDecimal getParentId() {
        return this.parentId;
    }

    public void setParentId(BigDecimal parentId) {
        this.parentId = parentId;
    }

    public BigDecimal getUrgencyId() {
        return this.urgencyId;
    }

    public void setUrgencyId(BigDecimal urgencyId) {
        this.urgencyId = urgencyId;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
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

    public net.project.hibernate.model.PnPostBodyClob getPnPostBodyClob() {
        return this.pnPostBodyClob;
    }

    public void setPnPostBodyClob(net.project.hibernate.model.PnPostBodyClob pnPostBodyClob) {
        this.pnPostBodyClob = pnPostBodyClob;
    }

    public Set getPnPostHistories() {
        return this.pnPostHistories;
    }

    public void setPnPostHistories(Set pnPostHistories) {
        this.pnPostHistories = pnPostHistories;
    }

    public Set getPnPostReaders() {
        return this.pnPostReaders;
    }

    public void setPnPostReaders(Set pnPostReaders) {
        this.pnPostReaders = pnPostReaders;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPost) ) return false;
        PnPost castOther = (PnPost) other;
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
