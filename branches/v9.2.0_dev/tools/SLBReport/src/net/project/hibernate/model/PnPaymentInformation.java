package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPaymentInformation implements Serializable {

    /** identifier field */
    private BigDecimal paymentId;

    /** nullable persistent field */
    private BigDecimal partyId;

    /** persistent field */
    private net.project.hibernate.model.PnPaymentModel pnPaymentModel;

    /** persistent field */
    private Set pnBills;

    /** persistent field */
    private Set pnCcTransactionPayments;

    /** persistent field */
    private Set pnLicenses;

    /** persistent field */
    private Set pnLedgers;

    /** full constructor */
    public PnPaymentInformation(BigDecimal paymentId, BigDecimal partyId, net.project.hibernate.model.PnPaymentModel pnPaymentModel, Set pnBills, Set pnCcTransactionPayments, Set pnLicenses, Set pnLedgers) {
        this.paymentId = paymentId;
        this.partyId = partyId;
        this.pnPaymentModel = pnPaymentModel;
        this.pnBills = pnBills;
        this.pnCcTransactionPayments = pnCcTransactionPayments;
        this.pnLicenses = pnLicenses;
        this.pnLedgers = pnLedgers;
    }

    /** default constructor */
    public PnPaymentInformation() {
    }

    /** minimal constructor */
    public PnPaymentInformation(BigDecimal paymentId, net.project.hibernate.model.PnPaymentModel pnPaymentModel, Set pnBills, Set pnCcTransactionPayments, Set pnLicenses, Set pnLedgers) {
        this.paymentId = paymentId;
        this.pnPaymentModel = pnPaymentModel;
        this.pnBills = pnBills;
        this.pnCcTransactionPayments = pnCcTransactionPayments;
        this.pnLicenses = pnLicenses;
        this.pnLedgers = pnLedgers;
    }

    public BigDecimal getPaymentId() {
        return this.paymentId;
    }

    public void setPaymentId(BigDecimal paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getPartyId() {
        return this.partyId;
    }

    public void setPartyId(BigDecimal partyId) {
        this.partyId = partyId;
    }

    public net.project.hibernate.model.PnPaymentModel getPnPaymentModel() {
        return this.pnPaymentModel;
    }

    public void setPnPaymentModel(net.project.hibernate.model.PnPaymentModel pnPaymentModel) {
        this.pnPaymentModel = pnPaymentModel;
    }

    public Set getPnBills() {
        return this.pnBills;
    }

    public void setPnBills(Set pnBills) {
        this.pnBills = pnBills;
    }

    public Set getPnCcTransactionPayments() {
        return this.pnCcTransactionPayments;
    }

    public void setPnCcTransactionPayments(Set pnCcTransactionPayments) {
        this.pnCcTransactionPayments = pnCcTransactionPayments;
    }

    public Set getPnLicenses() {
        return this.pnLicenses;
    }

    public void setPnLicenses(Set pnLicenses) {
        this.pnLicenses = pnLicenses;
    }

    public Set getPnLedgers() {
        return this.pnLedgers;
    }

    public void setPnLedgers(Set pnLedgers) {
        this.pnLedgers = pnLedgers;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("paymentId", getPaymentId())
            .toString();
    }

}
