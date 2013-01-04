package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCreditCardTransaction implements Serializable {

    /** identifier field */
    private BigDecimal transactionId;

    /** nullable persistent field */
    private String vendorTransactionId;

    /** persistent field */
    private Date dateSubmitted;

    /** persistent field */
    private String transactionType;

    /** persistent field */
    private BigDecimal transactionAmount;

    /** persistent field */
    private String transactionAmountCurrency;

    /** nullable persistent field */
    private String authorizationCode;

    /** nullable persistent field */
    private Integer isDuplicate;

    /** persistent field */
    private Set pnCcTransactionPayments;

    /** full constructor */
    public PnCreditCardTransaction(BigDecimal transactionId, String vendorTransactionId, Date dateSubmitted, String transactionType, BigDecimal transactionAmount, String transactionAmountCurrency, String authorizationCode, Integer isDuplicate, Set pnCcTransactionPayments) {
        this.transactionId = transactionId;
        this.vendorTransactionId = vendorTransactionId;
        this.dateSubmitted = dateSubmitted;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.transactionAmountCurrency = transactionAmountCurrency;
        this.authorizationCode = authorizationCode;
        this.isDuplicate = isDuplicate;
        this.pnCcTransactionPayments = pnCcTransactionPayments;
    }

    /** default constructor */
    public PnCreditCardTransaction() {
    }

    /** minimal constructor */
    public PnCreditCardTransaction(BigDecimal transactionId, Date dateSubmitted, String transactionType, BigDecimal transactionAmount, String transactionAmountCurrency, Set pnCcTransactionPayments) {
        this.transactionId = transactionId;
        this.dateSubmitted = dateSubmitted;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.transactionAmountCurrency = transactionAmountCurrency;
        this.pnCcTransactionPayments = pnCcTransactionPayments;
    }

    public BigDecimal getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(BigDecimal transactionId) {
        this.transactionId = transactionId;
    }

    public String getVendorTransactionId() {
        return this.vendorTransactionId;
    }

    public void setVendorTransactionId(String vendorTransactionId) {
        this.vendorTransactionId = vendorTransactionId;
    }

    public Date getDateSubmitted() {
        return this.dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getTransactionAmount() {
        return this.transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionAmountCurrency() {
        return this.transactionAmountCurrency;
    }

    public void setTransactionAmountCurrency(String transactionAmountCurrency) {
        this.transactionAmountCurrency = transactionAmountCurrency;
    }

    public String getAuthorizationCode() {
        return this.authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public Integer getIsDuplicate() {
        return this.isDuplicate;
    }

    public void setIsDuplicate(Integer isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

    public Set getPnCcTransactionPayments() {
        return this.pnCcTransactionPayments;
    }

    public void setPnCcTransactionPayments(Set pnCcTransactionPayments) {
        this.pnCcTransactionPayments = pnCcTransactionPayments;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("transactionId", getTransactionId())
            .toString();
    }

}
