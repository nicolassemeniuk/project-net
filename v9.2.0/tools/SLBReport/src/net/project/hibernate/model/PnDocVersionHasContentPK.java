package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocVersionHasContentPK implements Serializable {

    /** identifier field */
    private BigDecimal docVersionId;

    /** identifier field */
    private BigDecimal docContentId;

    /** full constructor */
    public PnDocVersionHasContentPK(BigDecimal docVersionId, BigDecimal docContentId) {
        this.docVersionId = docVersionId;
        this.docContentId = docContentId;
    }

    /** default constructor */
    public PnDocVersionHasContentPK() {
    }

    public BigDecimal getDocVersionId() {
        return this.docVersionId;
    }

    public void setDocVersionId(BigDecimal docVersionId) {
        this.docVersionId = docVersionId;
    }

    public BigDecimal getDocContentId() {
        return this.docContentId;
    }

    public void setDocContentId(BigDecimal docContentId) {
        this.docContentId = docContentId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docVersionId", getDocVersionId())
            .append("docContentId", getDocContentId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocVersionHasContentPK) ) return false;
        PnDocVersionHasContentPK castOther = (PnDocVersionHasContentPK) other;
        return new EqualsBuilder()
            .append(this.getDocVersionId(), castOther.getDocVersionId())
            .append(this.getDocContentId(), castOther.getDocContentId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDocVersionId())
            .append(getDocContentId())
            .toHashCode();
    }

}
