package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLdapDirectoryAttrMapPK implements Serializable {

    /** identifier field */
    private BigDecimal contextId;

    /** identifier field */
    private String attributeId;

    /** full constructor */
    public PnLdapDirectoryAttrMapPK(BigDecimal contextId, String attributeId) {
        this.contextId = contextId;
        this.attributeId = attributeId;
    }

    /** default constructor */
    public PnLdapDirectoryAttrMapPK() {
    }

    public BigDecimal getContextId() {
        return this.contextId;
    }

    public void setContextId(BigDecimal contextId) {
        this.contextId = contextId;
    }

    public String getAttributeId() {
        return this.attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("contextId", getContextId())
            .append("attributeId", getAttributeId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnLdapDirectoryAttrMapPK) ) return false;
        PnLdapDirectoryAttrMapPK castOther = (PnLdapDirectoryAttrMapPK) other;
        return new EqualsBuilder()
            .append(this.getContextId(), castOther.getContextId())
            .append(this.getAttributeId(), castOther.getAttributeId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getContextId())
            .append(getAttributeId())
            .toHashCode();
    }

}
