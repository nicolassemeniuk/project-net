package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowHasObjectType implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnWorkflowHasObjectTypePK comp_id;

    /** nullable persistent field */
    private String subTypeId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnWorkflow pnWorkflow;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObjectType pnObjectType;

    /** full constructor */
    public PnWorkflowHasObjectType(net.project.hibernate.model.PnWorkflowHasObjectTypePK comp_id, String subTypeId, net.project.hibernate.model.PnWorkflow pnWorkflow, net.project.hibernate.model.PnObjectType pnObjectType) {
        this.comp_id = comp_id;
        this.subTypeId = subTypeId;
        this.pnWorkflow = pnWorkflow;
        this.pnObjectType = pnObjectType;
    }

    /** default constructor */
    public PnWorkflowHasObjectType() {
    }

    /** minimal constructor */
    public PnWorkflowHasObjectType(net.project.hibernate.model.PnWorkflowHasObjectTypePK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnWorkflowHasObjectTypePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnWorkflowHasObjectTypePK comp_id) {
        this.comp_id = comp_id;
    }

    public String getSubTypeId() {
        return this.subTypeId;
    }

    public void setSubTypeId(String subTypeId) {
        this.subTypeId = subTypeId;
    }

    public net.project.hibernate.model.PnWorkflow getPnWorkflow() {
        return this.pnWorkflow;
    }

    public void setPnWorkflow(net.project.hibernate.model.PnWorkflow pnWorkflow) {
        this.pnWorkflow = pnWorkflow;
    }

    public net.project.hibernate.model.PnObjectType getPnObjectType() {
        return this.pnObjectType;
    }

    public void setPnObjectType(net.project.hibernate.model.PnObjectType pnObjectType) {
        this.pnObjectType = pnObjectType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWorkflowHasObjectType) ) return false;
        PnWorkflowHasObjectType castOther = (PnWorkflowHasObjectType) other;
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
