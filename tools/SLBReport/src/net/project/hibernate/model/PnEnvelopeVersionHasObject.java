package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnEnvelopeVersionHasObject implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnEnvelopeVersionHasObjectPK comp_id;

    /** persistent field */
    private String objectType;

    /** persistent field */
    private BigDecimal objectVersionId;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnEnvelopeVersion pnEnvelopeVersion;

    /** nullable persistent field */
    private net.project.hibernate.model.PnEnvelopeHasObject pnEnvelopeHasObject;

    /** persistent field */
    private net.project.hibernate.model.PnEnvelopeObjectClob pnEnvelopeObjectClob;

    /** full constructor */
    public PnEnvelopeVersionHasObject(net.project.hibernate.model.PnEnvelopeVersionHasObjectPK comp_id, String objectType, BigDecimal objectVersionId, Date crc, String recordStatus, net.project.hibernate.model.PnEnvelopeVersion pnEnvelopeVersion, net.project.hibernate.model.PnEnvelopeHasObject pnEnvelopeHasObject, net.project.hibernate.model.PnEnvelopeObjectClob pnEnvelopeObjectClob) {
        this.comp_id = comp_id;
        this.objectType = objectType;
        this.objectVersionId = objectVersionId;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnEnvelopeVersion = pnEnvelopeVersion;
        this.pnEnvelopeHasObject = pnEnvelopeHasObject;
        this.pnEnvelopeObjectClob = pnEnvelopeObjectClob;
    }

    /** default constructor */
    public PnEnvelopeVersionHasObject() {
    }

    /** minimal constructor */
    public PnEnvelopeVersionHasObject(net.project.hibernate.model.PnEnvelopeVersionHasObjectPK comp_id, String objectType, BigDecimal objectVersionId, Date crc, String recordStatus, net.project.hibernate.model.PnEnvelopeObjectClob pnEnvelopeObjectClob) {
        this.comp_id = comp_id;
        this.objectType = objectType;
        this.objectVersionId = objectVersionId;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnEnvelopeObjectClob = pnEnvelopeObjectClob;
    }

    public net.project.hibernate.model.PnEnvelopeVersionHasObjectPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnEnvelopeVersionHasObjectPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public BigDecimal getObjectVersionId() {
        return this.objectVersionId;
    }

    public void setObjectVersionId(BigDecimal objectVersionId) {
        this.objectVersionId = objectVersionId;
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

    public net.project.hibernate.model.PnEnvelopeVersion getPnEnvelopeVersion() {
        return this.pnEnvelopeVersion;
    }

    public void setPnEnvelopeVersion(net.project.hibernate.model.PnEnvelopeVersion pnEnvelopeVersion) {
        this.pnEnvelopeVersion = pnEnvelopeVersion;
    }

    public net.project.hibernate.model.PnEnvelopeHasObject getPnEnvelopeHasObject() {
        return this.pnEnvelopeHasObject;
    }

    public void setPnEnvelopeHasObject(net.project.hibernate.model.PnEnvelopeHasObject pnEnvelopeHasObject) {
        this.pnEnvelopeHasObject = pnEnvelopeHasObject;
    }

    public net.project.hibernate.model.PnEnvelopeObjectClob getPnEnvelopeObjectClob() {
        return this.pnEnvelopeObjectClob;
    }

    public void setPnEnvelopeObjectClob(net.project.hibernate.model.PnEnvelopeObjectClob pnEnvelopeObjectClob) {
        this.pnEnvelopeObjectClob = pnEnvelopeObjectClob;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnEnvelopeVersionHasObject) ) return false;
        PnEnvelopeVersionHasObject castOther = (PnEnvelopeVersionHasObject) other;
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
