package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasPropertySheet implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasPropertySheetPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPropertySheet pnPropertySheet;

    /** full constructor */
    public PnSpaceHasPropertySheet(net.project.hibernate.model.PnSpaceHasPropertySheetPK comp_id, net.project.hibernate.model.PnPropertySheet pnPropertySheet) {
        this.comp_id = comp_id;
        this.pnPropertySheet = pnPropertySheet;
    }

    /** default constructor */
    public PnSpaceHasPropertySheet() {
    }

    /** minimal constructor */
    public PnSpaceHasPropertySheet(net.project.hibernate.model.PnSpaceHasPropertySheetPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceHasPropertySheetPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasPropertySheetPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPropertySheet getPnPropertySheet() {
        return this.pnPropertySheet;
    }

    public void setPnPropertySheet(net.project.hibernate.model.PnPropertySheet pnPropertySheet) {
        this.pnPropertySheet = pnPropertySheet;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasPropertySheet) ) return false;
        PnSpaceHasPropertySheet castOther = (PnSpaceHasPropertySheet) other;
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
