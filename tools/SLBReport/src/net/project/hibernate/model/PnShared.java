package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnShared implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSharedPK comp_id;

    /** persistent field */
    private BigDecimal importSpaceId;

    /** nullable persistent field */
    private BigDecimal importedObjectId;

    /** nullable persistent field */
    private BigDecimal exportSpaceId;

    /** nullable persistent field */
    private BigDecimal exportContainerId;

    /** nullable persistent field */
    private Integer readOnly;

    /** nullable persistent field */
    private BigDecimal traversed;

    /** full constructor */
    public PnShared(net.project.hibernate.model.PnSharedPK comp_id, BigDecimal importSpaceId, BigDecimal importedObjectId, BigDecimal exportSpaceId, BigDecimal exportContainerId, Integer readOnly, BigDecimal traversed) {
        this.comp_id = comp_id;
        this.importSpaceId = importSpaceId;
        this.importedObjectId = importedObjectId;
        this.exportSpaceId = exportSpaceId;
        this.exportContainerId = exportContainerId;
        this.readOnly = readOnly;
        this.traversed = traversed;
    }

    /** default constructor */
    public PnShared() {
    }

    /** minimal constructor */
    public PnShared(net.project.hibernate.model.PnSharedPK comp_id, BigDecimal importSpaceId) {
        this.comp_id = comp_id;
        this.importSpaceId = importSpaceId;
    }

    public net.project.hibernate.model.PnSharedPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSharedPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getImportSpaceId() {
        return this.importSpaceId;
    }

    public void setImportSpaceId(BigDecimal importSpaceId) {
        this.importSpaceId = importSpaceId;
    }

    public BigDecimal getImportedObjectId() {
        return this.importedObjectId;
    }

    public void setImportedObjectId(BigDecimal importedObjectId) {
        this.importedObjectId = importedObjectId;
    }

    public BigDecimal getExportSpaceId() {
        return this.exportSpaceId;
    }

    public void setExportSpaceId(BigDecimal exportSpaceId) {
        this.exportSpaceId = exportSpaceId;
    }

    public BigDecimal getExportContainerId() {
        return this.exportContainerId;
    }

    public void setExportContainerId(BigDecimal exportContainerId) {
        this.exportContainerId = exportContainerId;
    }

    public Integer getReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(Integer readOnly) {
        this.readOnly = readOnly;
    }

    public BigDecimal getTraversed() {
        return this.traversed;
    }

    public void setTraversed(BigDecimal traversed) {
        this.traversed = traversed;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnShared) ) return false;
        PnShared castOther = (PnShared) other;
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
