package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnObjectLinkPK implements Serializable {

    /** identifier field */
    private BigDecimal fromObjectId;

    /** identifier field */
    private BigDecimal toObjectId;

    /** identifier field */
    private Integer context;

    /** full constructor */
    public PnObjectLinkPK(BigDecimal fromObjectId, BigDecimal toObjectId, Integer context) {
        this.fromObjectId = fromObjectId;
        this.toObjectId = toObjectId;
        this.context = context;
    }

    /** default constructor */
    public PnObjectLinkPK() {
    }

    public BigDecimal getFromObjectId() {
        return this.fromObjectId;
    }

    public void setFromObjectId(BigDecimal fromObjectId) {
        this.fromObjectId = fromObjectId;
    }

    public BigDecimal getToObjectId() {
        return this.toObjectId;
    }

    public void setToObjectId(BigDecimal toObjectId) {
        this.toObjectId = toObjectId;
    }

    public Integer getContext() {
        return this.context;
    }

    public void setContext(Integer context) {
        this.context = context;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("fromObjectId", getFromObjectId())
            .append("toObjectId", getToObjectId())
            .append("context", getContext())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnObjectLinkPK) ) return false;
        PnObjectLinkPK castOther = (PnObjectLinkPK) other;
        return new EqualsBuilder()
            .append(this.getFromObjectId(), castOther.getFromObjectId())
            .append(this.getToObjectId(), castOther.getToObjectId())
            .append(this.getContext(), castOther.getContext())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getFromObjectId())
            .append(getToObjectId())
            .append(getContext())
            .toHashCode();
    }

}
