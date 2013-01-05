package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassHasWorkflow implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnClassHasWorkflowPK comp_id;

    /** nullable persistent field */
    private Integer isDefault;

    /** full constructor */
    public PnClassHasWorkflow(net.project.hibernate.model.PnClassHasWorkflowPK comp_id, Integer isDefault) {
        this.comp_id = comp_id;
        this.isDefault = isDefault;
    }

    /** default constructor */
    public PnClassHasWorkflow() {
    }

    /** minimal constructor */
    public PnClassHasWorkflow(net.project.hibernate.model.PnClassHasWorkflowPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnClassHasWorkflowPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnClassHasWorkflowPK comp_id) {
        this.comp_id = comp_id;
    }

    public Integer getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassHasWorkflow) ) return false;
        PnClassHasWorkflow castOther = (PnClassHasWorkflow) other;
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
