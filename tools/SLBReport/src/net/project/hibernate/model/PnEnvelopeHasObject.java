package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnEnvelopeHasObject implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnEnvelopeHasObjectPK comp_id;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** nullable persistent field */
    private net.project.hibernate.model.PnWorkflowEnvelope pnWorkflowEnvelope;

    /** persistent field */
    private Set pnEnvelopeVersionHasObjects;

    /** full constructor */
    public PnEnvelopeHasObject(net.project.hibernate.model.PnEnvelopeHasObjectPK comp_id, Date crc, String recordStatus, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnWorkflowEnvelope pnWorkflowEnvelope, Set pnEnvelopeVersionHasObjects) {
        this.comp_id = comp_id;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnWorkflowEnvelope = pnWorkflowEnvelope;
        this.pnEnvelopeVersionHasObjects = pnEnvelopeVersionHasObjects;
    }

    /** default constructor */
    public PnEnvelopeHasObject() {
    }

    /** minimal constructor */
    public PnEnvelopeHasObject(net.project.hibernate.model.PnEnvelopeHasObjectPK comp_id, Date crc, String recordStatus, Set pnEnvelopeVersionHasObjects) {
        this.comp_id = comp_id;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnEnvelopeVersionHasObjects = pnEnvelopeVersionHasObjects;
    }

    public net.project.hibernate.model.PnEnvelopeHasObjectPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnEnvelopeHasObjectPK comp_id) {
        this.comp_id = comp_id;
    }

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnWorkflowEnvelope getPnWorkflowEnvelope() {
        return this.pnWorkflowEnvelope;
    }

    public void setPnWorkflowEnvelope(net.project.hibernate.model.PnWorkflowEnvelope pnWorkflowEnvelope) {
        this.pnWorkflowEnvelope = pnWorkflowEnvelope;
    }

    public Set getPnEnvelopeVersionHasObjects() {
        return this.pnEnvelopeVersionHasObjects;
    }

    public void setPnEnvelopeVersionHasObjects(Set pnEnvelopeVersionHasObjects) {
        this.pnEnvelopeVersionHasObjects = pnEnvelopeVersionHasObjects;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnEnvelopeHasObject) ) return false;
        PnEnvelopeHasObject castOther = (PnEnvelopeHasObject) other;
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
