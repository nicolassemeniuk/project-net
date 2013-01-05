package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPropertyPK implements Serializable {

    /** identifier field */
    private BigDecimal contextId;

    /** identifier field */
    private String language;

    /** identifier field */
    private String property;

    /** full constructor */
    public PnPropertyPK(BigDecimal contextId, String language, String property) {
        this.contextId = contextId;
        this.language = language;
        this.property = property;
    }

    /** default constructor */
    public PnPropertyPK() {
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

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("contextId", getContextId())
            .append("language", getLanguage())
            .append("property", getProperty())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPropertyPK) ) return false;
        PnPropertyPK castOther = (PnPropertyPK) other;
        return new EqualsBuilder()
            .append(this.getContextId(), castOther.getContextId())
            .append(this.getLanguage(), castOther.getLanguage())
            .append(this.getProperty(), castOther.getProperty())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getContextId())
            .append(getLanguage())
            .append(getProperty())
            .toHashCode();
    }

}
