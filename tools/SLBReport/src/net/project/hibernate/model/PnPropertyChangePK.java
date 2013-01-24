package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPropertyChangePK implements Serializable {

    /** identifier field */
    private BigDecimal contextId;

    /** identifier field */
    private String language;

    /** full constructor */
    public PnPropertyChangePK(BigDecimal contextId, String language) {
        this.contextId = contextId;
        this.language = language;
    }

    /** default constructor */
    public PnPropertyChangePK() {
    }

    public BigDecimal getContextId() {
        return this.contextId;
    }

    public void setContextId(BigDecimal contextId) {
        this.contextId = contextId;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("contextId", getContextId())
            .append("language", getLanguage())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPropertyChangePK) ) return false;
        PnPropertyChangePK castOther = (PnPropertyChangePK) other;
        return new EqualsBuilder()
            .append(this.getContextId(), castOther.getContextId())
            .append(this.getLanguage(), castOther.getLanguage())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getContextId())
            .append(getLanguage())
            .toHashCode();
    }

}
