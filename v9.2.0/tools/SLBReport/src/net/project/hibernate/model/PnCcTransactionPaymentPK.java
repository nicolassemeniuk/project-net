package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCcTransactionPaymentPK implements Serializable {

    /** identifier field */
    private BigDecimal transactionId;

    /** identifier field */
    private BigDecimal paymentId;

    /** full constructor */
    public PnCcTransactionPaymentPK(BigDecimal transactionId, BigDecimal paymentId) {
        this.transactionId = transactionId;
        this.paymentId = paymentId;
    }

    /** default constructor */
    public PnCcTransactionPaymentPK() {
    }

    public BigDecimal getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(BigDecimal transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getPaymentId() {
        return this.paymentId;
    }

    public void setPaymentId(BigDecimal paymentId) {
        this.paymentId = paymentId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("transactionId", getTransactionId())
            .append("paymentId", getPaymentId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnCcTransactionPaymentPK) ) return false;
        PnCcTransactionPaymentPK castOther = (PnCcTransactionPaymentPK) other;
        return new EqualsBuilder()
            .append(this.getTransactionId(), castOther.getTransactionId())
            .append(this.getPaymentId(), castOther.getPaymentId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTransactionId())
            .append(getPaymentId())
            .toHashCode();
    }

}
