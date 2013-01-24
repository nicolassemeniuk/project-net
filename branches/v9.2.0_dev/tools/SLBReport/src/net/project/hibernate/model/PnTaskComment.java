package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTaskComment implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnTaskCommentPK comp_id;

    /** persistent field */
    private BigDecimal createdById;

    /** persistent field */
    private Date createdDatetime;

    /** nullable persistent field */
    private Clob textClob;

    /** full constructor */
    public PnTaskComment(net.project.hibernate.model.PnTaskCommentPK comp_id, BigDecimal createdById, Date createdDatetime, Clob textClob) {
        this.comp_id = comp_id;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.textClob = textClob;
    }

    /** default constructor */
    public PnTaskComment() {
    }

    /** minimal constructor */
    public PnTaskComment(net.project.hibernate.model.PnTaskCommentPK comp_id, BigDecimal createdById, Date createdDatetime) {
        this.comp_id = comp_id;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
    }

    public net.project.hibernate.model.PnTaskCommentPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnTaskCommentPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(BigDecimal createdById) {
        this.createdById = createdById;
    }

    public Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Clob getTextClob() {
        return this.textClob;
    }

    public void setTextClob(Clob textClob) {
        this.textClob = textClob;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnTaskComment) ) return false;
        PnTaskComment castOther = (PnTaskComment) other;
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
