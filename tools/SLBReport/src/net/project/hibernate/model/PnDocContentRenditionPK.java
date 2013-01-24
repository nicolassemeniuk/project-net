package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocContentRenditionPK implements Serializable {

    /** identifier field */
    private BigDecimal docRenditionId;

    /** identifier field */
    private BigDecimal docContentId;

    /** full constructor */
    public PnDocContentRenditionPK(BigDecimal docRenditionId, BigDecimal docContentId) {
        this.docRenditionId = docRenditionId;
        this.docContentId = docContentId;
    }

    /** default constructor */
    public PnDocContentRenditionPK() {
    }

    public BigDecimal getDocRenditionId() {
        return this.docRenditionId;
    }

    public void setDocRenditionId(BigDecimal docRenditionId) {
        this.docRenditionId = docRenditionId;
    }

    public BigDecimal getDocContentId() {
        return this.docContentId;
    }

    public void setDocContentId(BigDecimal docContentId) {
        this.docContentId = docContentId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docRenditionId", getDocRenditionId())
            .append("docContentId", getDocContentId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocContentRenditionPK) ) return false;
        PnDocContentRenditionPK castOther = (PnDocContentRenditionPK) other;
        return new EqualsBuilder()
            .append(this.getDocRenditionId(), castOther.getDocRenditionId())
            .append(this.getDocContentId(), castOther.getDocContentId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDocRenditionId())
            .append(getDocContentId())
            .toHashCode();
    }

}
