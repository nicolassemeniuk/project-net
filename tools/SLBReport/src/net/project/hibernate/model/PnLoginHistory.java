package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLoginHistory implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnLoginHistoryPK comp_id;

    /** nullable persistent field */
    private String loginNameUsed;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** full constructor */
    public PnLoginHistory(net.project.hibernate.model.PnLoginHistoryPK comp_id, String loginNameUsed, net.project.hibernate.model.PnPerson pnPerson) {
        this.comp_id = comp_id;
        this.loginNameUsed = loginNameUsed;
        this.pnPerson = pnPerson;
    }

    /** default constructor */
    public PnLoginHistory() {
    }

    /** minimal constructor */
    public PnLoginHistory(net.project.hibernate.model.PnLoginHistoryPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnLoginHistoryPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnLoginHistoryPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getLoginNameUsed() {
        return this.loginNameUsed;
    }

    public void setLoginNameUsed(String loginNameUsed) {
        this.loginNameUsed = loginNameUsed;
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
        if ( !(other instanceof PnLoginHistory) ) return false;
        PnLoginHistory castOther = (PnLoginHistory) other;
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
