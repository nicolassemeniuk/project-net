package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnShareablePermission implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnShareablePermissionPK comp_id;

    /** nullable persistent field */
    private String shareType;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** full constructor */
    public PnShareablePermission(net.project.hibernate.model.PnShareablePermissionPK comp_id, String shareType, net.project.hibernate.model.PnObject pnObject) {
        this.comp_id = comp_id;
        this.shareType = shareType;
        this.pnObject = pnObject;
    }

    /** default constructor */
    public PnShareablePermission() {
    }

    /** minimal constructor */
    public PnShareablePermission(net.project.hibernate.model.PnShareablePermissionPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnShareablePermissionPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnShareablePermissionPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getShareType() {
        return this.shareType;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnShareablePermission) ) return false;
        PnShareablePermission castOther = (PnShareablePermission) other;
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
