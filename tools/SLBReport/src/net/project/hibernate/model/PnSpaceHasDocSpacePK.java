package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasDocSpacePK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal docSpaceId;

    /** full constructor */
    public PnSpaceHasDocSpacePK(BigDecimal spaceId, BigDecimal docSpaceId) {
        this.spaceId = spaceId;
        this.docSpaceId = docSpaceId;
    }

    /** default constructor */
    public PnSpaceHasDocSpacePK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getDocSpaceId() {
        return this.docSpaceId;
    }

    public void setDocSpaceId(BigDecimal docSpaceId) {
        this.docSpaceId = docSpaceId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("docSpaceId", getDocSpaceId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasDocSpacePK) ) return false;
        PnSpaceHasDocSpacePK castOther = (PnSpaceHasDocSpacePK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getDocSpaceId(), castOther.getDocSpaceId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getDocSpaceId())
            .toHashCode();
    }

}
