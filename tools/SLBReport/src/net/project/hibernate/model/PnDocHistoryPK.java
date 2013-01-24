package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocHistoryPK implements Serializable {

    /** identifier field */
    private BigDecimal docId;

    /** identifier field */
    private BigDecimal docHistoryId;

    /** full constructor */
    public PnDocHistoryPK(BigDecimal docId, BigDecimal docHistoryId) {
        this.docId = docId;
        this.docHistoryId = docHistoryId;
    }

    /** default constructor */
    public PnDocHistoryPK() {
    }

    public BigDecimal getDocId() {
        return this.docId;
    }

    public void setDocId(BigDecimal docId) {
        this.docId = docId;
    }

    public BigDecimal getDocHistoryId() {
        return this.docHistoryId;
    }

    public void setDocHistoryId(BigDecimal docHistoryId) {
        this.docHistoryId = docHistoryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docId", getDocId())
            .append("docHistoryId", getDocHistoryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocHistoryPK) ) return false;
        PnDocHistoryPK castOther = (PnDocHistoryPK) other;
        return new EqualsBuilder()
            .append(this.getDocId(), castOther.getDocId())
            .append(this.getDocHistoryId(), castOther.getDocHistoryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDocId())
            .append(getDocHistoryId())
            .toHashCode();
    }

}
