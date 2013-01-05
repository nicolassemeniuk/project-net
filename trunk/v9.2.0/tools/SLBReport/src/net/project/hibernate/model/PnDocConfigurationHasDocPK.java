package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocConfigurationHasDocPK implements Serializable {

    /** identifier field */
    private BigDecimal docConfigurationId;

    /** identifier field */
    private BigDecimal docVersionId;

    /** full constructor */
    public PnDocConfigurationHasDocPK(BigDecimal docConfigurationId, BigDecimal docVersionId) {
        this.docConfigurationId = docConfigurationId;
        this.docVersionId = docVersionId;
    }

    /** default constructor */
    public PnDocConfigurationHasDocPK() {
    }

    public BigDecimal getDocConfigurationId() {
        return this.docConfigurationId;
    }

    public void setDocConfigurationId(BigDecimal docConfigurationId) {
        this.docConfigurationId = docConfigurationId;
    }

    public BigDecimal getDocVersionId() {
        return this.docVersionId;
    }

    public void setDocVersionId(BigDecimal docVersionId) {
        this.docVersionId = docVersionId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docConfigurationId", getDocConfigurationId())
            .append("docVersionId", getDocVersionId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocConfigurationHasDocPK) ) return false;
        PnDocConfigurationHasDocPK castOther = (PnDocConfigurationHasDocPK) other;
        return new EqualsBuilder()
            .append(this.getDocConfigurationId(), castOther.getDocConfigurationId())
            .append(this.getDocVersionId(), castOther.getDocVersionId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDocConfigurationId())
            .append(getDocVersionId())
            .toHashCode();
    }

}
