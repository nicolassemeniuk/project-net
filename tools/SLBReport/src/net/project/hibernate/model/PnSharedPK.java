package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSharedPK implements Serializable {

    /** identifier field */
    private BigDecimal exportedObjectId;

    /** identifier field */
    private BigDecimal importContainerId;

    /** full constructor */
    public PnSharedPK(BigDecimal exportedObjectId, BigDecimal importContainerId) {
        this.exportedObjectId = exportedObjectId;
        this.importContainerId = importContainerId;
    }

    /** default constructor */
    public PnSharedPK() {
    }

    public BigDecimal getExportedObjectId() {
        return this.exportedObjectId;
    }

    public void setExportedObjectId(BigDecimal exportedObjectId) {
        this.exportedObjectId = exportedObjectId;
    }

    public BigDecimal getImportContainerId() {
        return this.importContainerId;
    }

    public void setImportContainerId(BigDecimal importContainerId) {
        this.importContainerId = importContainerId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("exportedObjectId", getExportedObjectId())
            .append("importContainerId", getImportContainerId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSharedPK) ) return false;
        PnSharedPK castOther = (PnSharedPK) other;
        return new EqualsBuilder()
            .append(this.getExportedObjectId(), castOther.getExportedObjectId())
            .append(this.getImportContainerId(), castOther.getImportContainerId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getExportedObjectId())
            .append(getImportContainerId())
            .toHashCode();
    }

}
