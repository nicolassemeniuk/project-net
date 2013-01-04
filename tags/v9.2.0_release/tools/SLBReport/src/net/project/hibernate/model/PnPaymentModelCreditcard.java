package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPaymentModelCreditcard implements Serializable {

    /** identifier field */
    private BigDecimal paymentModelId;

    /** persistent field */
    private String cardNumber;

    /** persistent field */
    private int cardExpiryMonth;

    /** persistent field */
    private int cardExpiryYear;

    /** full constructor */
    public PnPaymentModelCreditcard(BigDecimal paymentModelId, String cardNumber, int cardExpiryMonth, int cardExpiryYear) {
        this.paymentModelId = paymentModelId;
        this.cardNumber = cardNumber;
        this.cardExpiryMonth = cardExpiryMonth;
        this.cardExpiryYear = cardExpiryYear;
    }

    /** default constructor */
    public PnPaymentModelCreditcard() {
    }

    public BigDecimal getPaymentModelId() {
        return this.paymentModelId;
    }

    public void setPaymentModelId(BigDecimal paymentModelId) {
        this.paymentModelId = paymentModelId;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getCardExpiryMonth() {
        return this.cardExpiryMonth;
    }

    public void setCardExpiryMonth(int cardExpiryMonth) {
        this.cardExpiryMonth = cardExpiryMonth;
    }

    public int getCardExpiryYear() {
        return this.cardExpiryYear;
    }

    public void setCardExpiryYear(int cardExpiryYear) {
        this.cardExpiryYear = cardExpiryYear;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("paymentModelId", getPaymentModelId())
            .toString();
    }

}
