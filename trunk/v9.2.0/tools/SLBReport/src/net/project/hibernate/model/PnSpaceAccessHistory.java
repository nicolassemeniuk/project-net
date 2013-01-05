package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceAccessHistory implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceAccessHistoryPK comp_id;

    /** nullable persistent field */
    private Date accessDate;

    /** full constructor */
    public PnSpaceAccessHistory(net.project.hibernate.model.PnSpaceAccessHistoryPK comp_id, Date accessDate) {
        this.comp_id = comp_id;
        this.accessDate = accessDate;
    }

    /** default constructor */
    public PnSpaceAccessHistory() {
    }

    /** minimal constructor */
    public PnSpaceAccessHistory(net.project.hibernate.model.PnSpaceAccessHistoryPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceAccessHistoryPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceAccessHistoryPK comp_id) {
        this.comp_id = comp_id;
    }

    public Date getAccessDate() {
        return this.accessDate;
    }

    public void setAccessDate(Date accessDate) {
        this.accessDate = accessDate;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceAccessHistory) ) return false;
        PnSpaceAccessHistory castOther = (PnSpaceAccessHistory) other;
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
