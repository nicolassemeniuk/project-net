package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonPropertyPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private String context;

    /** identifier field */
    private String property;

    /** identifier field */
    private String value;

    /** full constructor */
    public PnPersonPropertyPK(BigDecimal spaceId, BigDecimal personId, String context, String property, String value) {
        this.spaceId = spaceId;
        this.personId = personId;
        this.context = context;
        this.property = property;
        this.value = value;
    }

    /** default constructor */
    public PnPersonPropertyPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public String getContext() {
        return this.context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("personId", getPersonId())
            .append("context", getContext())
            .append("property", getProperty())
            .append("value", getValue())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonPropertyPK) ) return false;
        PnPersonPropertyPK castOther = (PnPersonPropertyPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getContext(), castOther.getContext())
            .append(this.getProperty(), castOther.getProperty())
            .append(this.getValue(), castOther.getValue())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getPersonId())
            .append(getContext())
            .append(getProperty())
            .append(getValue())
            .toHashCode();
    }

}
