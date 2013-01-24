package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnGroupHasGroup implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnGroupHasGroupPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnGroup pnGroupByMemberGroupId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnGroup pnGroupByGroupId;

    /** full constructor */
    public PnGroupHasGroup(net.project.hibernate.model.PnGroupHasGroupPK comp_id, net.project.hibernate.model.PnGroup pnGroupByMemberGroupId, net.project.hibernate.model.PnGroup pnGroupByGroupId) {
        this.comp_id = comp_id;
        this.pnGroupByMemberGroupId = pnGroupByMemberGroupId;
        this.pnGroupByGroupId = pnGroupByGroupId;
    }

    /** default constructor */
    public PnGroupHasGroup() {
    }

    /** minimal constructor */
    public PnGroupHasGroup(net.project.hibernate.model.PnGroupHasGroupPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnGroupHasGroupPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnGroupHasGroupPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnGroup getPnGroupByMemberGroupId() {
        return this.pnGroupByMemberGroupId;
    }

    public void setPnGroupByMemberGroupId(net.project.hibernate.model.PnGroup pnGroupByMemberGroupId) {
        this.pnGroupByMemberGroupId = pnGroupByMemberGroupId;
    }

    public net.project.hibernate.model.PnGroup getPnGroupByGroupId() {
        return this.pnGroupByGroupId;
    }

    public void setPnGroupByGroupId(net.project.hibernate.model.PnGroup pnGroupByGroupId) {
        this.pnGroupByGroupId = pnGroupByGroupId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnGroupHasGroup) ) return false;
        PnGroupHasGroup castOther = (PnGroupHasGroup) other;
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
