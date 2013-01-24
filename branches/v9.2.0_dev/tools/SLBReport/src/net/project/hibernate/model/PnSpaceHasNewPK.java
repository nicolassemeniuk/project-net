package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasNewPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal newsId;

    /** full constructor */
    public PnSpaceHasNewPK(BigDecimal spaceId, BigDecimal newsId) {
        this.spaceId = spaceId;
        this.newsId = newsId;
    }

    /** default constructor */
    public PnSpaceHasNewPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getNewsId() {
        return this.newsId;
    }

    public void setNewsId(BigDecimal newsId) {
        this.newsId = newsId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("newsId", getNewsId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasNewPK) ) return false;
        PnSpaceHasNewPK castOther = (PnSpaceHasNewPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getNewsId(), castOther.getNewsId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getNewsId())
            .toHashCode();
    }

}
