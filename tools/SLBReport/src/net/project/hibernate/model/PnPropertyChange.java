package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPropertyChange implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPropertyChangePK comp_id;

    /** persistent field */
    private Date lastUpdatedDatetime;

    /** full constructor */
    public PnPropertyChange(net.project.hibernate.model.PnPropertyChangePK comp_id, Date lastUpdatedDatetime) {
        this.comp_id = comp_id;
        this.lastUpdatedDatetime = lastUpdatedDatetime;
    }

    /** default constructor */
    public PnPropertyChange() {
    }

    public net.project.hibernate.model.PnPropertyChangePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPropertyChangePK comp_id) {
        this.comp_id = comp_id;
    }

    public Date getLastUpdatedDatetime() {
        return this.lastUpdatedDatetime;
    }

    public void setLastUpdatedDatetime(Date lastUpdatedDatetime) {
        this.lastUpdatedDatetime = lastUpdatedDatetime;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPropertyChange) ) return false;
        PnPropertyChange castOther = (PnPropertyChange) other;
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
