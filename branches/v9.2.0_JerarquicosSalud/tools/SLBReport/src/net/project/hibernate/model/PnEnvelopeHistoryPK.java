package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnEnvelopeHistoryPK implements Serializable {

    /** identifier field */
    private BigDecimal historyId;

    /** identifier field */
    private BigDecimal envelopeId;

    /** full constructor */
    public PnEnvelopeHistoryPK(BigDecimal historyId, BigDecimal envelopeId) {
        this.historyId = historyId;
        this.envelopeId = envelopeId;
    }

    /** default constructor */
    public PnEnvelopeHistoryPK() {
    }

    public BigDecimal getHistoryId() {
        return this.historyId;
    }

    public void setHistoryId(BigDecimal historyId) {
        this.historyId = historyId;
    }

    public BigDecimal getEnvelopeId() {
        return this.envelopeId;
    }

    public void setEnvelopeId(BigDecimal envelopeId) {
        this.envelopeId = envelopeId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("historyId", getHistoryId())
            .append("envelopeId", getEnvelopeId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnEnvelopeHistoryPK) ) return false;
        PnEnvelopeHistoryPK castOther = (PnEnvelopeHistoryPK) other;
        return new EqualsBuilder()
            .append(this.getHistoryId(), castOther.getHistoryId())
            .append(this.getEnvelopeId(), castOther.getEnvelopeId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getHistoryId())
            .append(getEnvelopeId())
            .toHashCode();
    }

}
