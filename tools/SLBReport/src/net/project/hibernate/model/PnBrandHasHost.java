package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBrandHasHost implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnBrandHasHostPK comp_id;

    /** nullable persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnBrand pnBrand;

    /** full constructor */
    public PnBrandHasHost(net.project.hibernate.model.PnBrandHasHostPK comp_id, String recordStatus, net.project.hibernate.model.PnBrand pnBrand) {
        this.comp_id = comp_id;
        this.recordStatus = recordStatus;
        this.pnBrand = pnBrand;
    }

    /** default constructor */
    public PnBrandHasHost() {
    }

    /** minimal constructor */
    public PnBrandHasHost(net.project.hibernate.model.PnBrandHasHostPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnBrandHasHostPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnBrandHasHostPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnBrand getPnBrand() {
        return this.pnBrand;
    }

    public void setPnBrand(net.project.hibernate.model.PnBrand pnBrand) {
        this.pnBrand = pnBrand;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnBrandHasHost) ) return false;
        PnBrandHasHost castOther = (PnBrandHasHost) other;
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
