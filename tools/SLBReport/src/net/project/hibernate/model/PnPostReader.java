package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPostReader implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPostReaderPK comp_id;

    /** persistent field */
    private Date dateRead;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPost pnPost;

    /** full constructor */
    public PnPostReader(net.project.hibernate.model.PnPostReaderPK comp_id, Date dateRead, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnPost pnPost) {
        this.comp_id = comp_id;
        this.dateRead = dateRead;
        this.pnPerson = pnPerson;
        this.pnPost = pnPost;
    }

    /** default constructor */
    public PnPostReader() {
    }

    /** minimal constructor */
    public PnPostReader(net.project.hibernate.model.PnPostReaderPK comp_id, Date dateRead) {
        this.comp_id = comp_id;
        this.dateRead = dateRead;
    }

    public net.project.hibernate.model.PnPostReaderPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPostReaderPK comp_id) {
        this.comp_id = comp_id;
    }

    public Date getDateRead() {
        return this.dateRead;
    }

    public void setDateRead(Date dateRead) {
        this.dateRead = dateRead;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnPost getPnPost() {
        return this.pnPost;
    }

    public void setPnPost(net.project.hibernate.model.PnPost pnPost) {
        this.pnPost = pnPost;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPostReader) ) return false;
        PnPostReader castOther = (PnPostReader) other;
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
