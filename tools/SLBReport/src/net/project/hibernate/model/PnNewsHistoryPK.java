package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnNewsHistoryPK implements Serializable {

    /** identifier field */
    private BigDecimal newsId;

    /** identifier field */
    private BigDecimal newsHistoryId;

    /** full constructor */
    public PnNewsHistoryPK(BigDecimal newsId, BigDecimal newsHistoryId) {
        this.newsId = newsId;
        this.newsHistoryId = newsHistoryId;
    }

    /** default constructor */
    public PnNewsHistoryPK() {
    }

    public BigDecimal getNewsId() {
        return this.newsId;
    }

    public void setNewsId(BigDecimal newsId) {
        this.newsId = newsId;
    }

    public BigDecimal getNewsHistoryId() {
        return this.newsHistoryId;
    }

    public void setNewsHistoryId(BigDecimal newsHistoryId) {
        this.newsHistoryId = newsHistoryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("newsId", getNewsId())
            .append("newsHistoryId", getNewsHistoryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnNewsHistoryPK) ) return false;
        PnNewsHistoryPK castOther = (PnNewsHistoryPK) other;
        return new EqualsBuilder()
            .append(this.getNewsId(), castOther.getNewsId())
            .append(this.getNewsHistoryId(), castOther.getNewsHistoryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getNewsId())
            .append(getNewsHistoryId())
            .toHashCode();
    }

}
