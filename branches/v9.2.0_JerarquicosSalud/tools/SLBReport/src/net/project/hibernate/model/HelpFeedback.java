package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class HelpFeedback implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.HelpFeedbackPK comp_id;

    /** persistent field */
    private String subject;

    /** nullable persistent field */
    private Long keyId;

    /** nullable persistent field */
    private String comments;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnProjectSpace pnProjectSpace;

    /** full constructor */
    public HelpFeedback(net.project.hibernate.model.HelpFeedbackPK comp_id, String subject, Long keyId, String comments, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnProjectSpace pnProjectSpace) {
        this.comp_id = comp_id;
        this.subject = subject;
        this.keyId = keyId;
        this.comments = comments;
        this.pnPerson = pnPerson;
        this.pnProjectSpace = pnProjectSpace;
    }

    /** default constructor */
    public HelpFeedback() {
    }

    /** minimal constructor */
    public HelpFeedback(net.project.hibernate.model.HelpFeedbackPK comp_id, String subject, net.project.hibernate.model.PnProjectSpace pnProjectSpace) {
        this.comp_id = comp_id;
        this.subject = subject;
        this.pnProjectSpace = pnProjectSpace;
    }

    public net.project.hibernate.model.HelpFeedbackPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.HelpFeedbackPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getKeyId() {
        return this.keyId;
    }

    public void setKeyId(Long keyId) {
        this.keyId = keyId;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnProjectSpace getPnProjectSpace() {
        return this.pnProjectSpace;
    }

    public void setPnProjectSpace(net.project.hibernate.model.PnProjectSpace pnProjectSpace) {
        this.pnProjectSpace = pnProjectSpace;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof HelpFeedback) ) return false;
        HelpFeedback castOther = (HelpFeedback) other;
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
