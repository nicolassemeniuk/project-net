package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCcTransactionPayment implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnCcTransactionPaymentPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnCreditCardTransaction pnCreditCardTransaction;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPaymentInformation pnPaymentInformation;

    /** full constructor */
    public PnCcTransactionPayment(net.project.hibernate.model.PnCcTransactionPaymentPK comp_id, net.project.hibernate.model.PnCreditCardTransaction pnCreditCardTransaction, net.project.hibernate.model.PnPaymentInformation pnPaymentInformation) {
        this.comp_id = comp_id;
        this.pnCreditCardTransaction = pnCreditCardTransaction;
        this.pnPaymentInformation = pnPaymentInformation;
    }

    /** default constructor */
    public PnCcTransactionPayment() {
    }

    /** minimal constructor */
    public PnCcTransactionPayment(net.project.hibernate.model.PnCcTransactionPaymentPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnCcTransactionPaymentPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnCcTransactionPaymentPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnCreditCardTransaction getPnCreditCardTransaction() {
        return this.pnCreditCardTransaction;
    }

    public void setPnCreditCardTransaction(net.project.hibernate.model.PnCreditCardTransaction pnCreditCardTransaction) {
        this.pnCreditCardTransaction = pnCreditCardTransaction;
    }

    public net.project.hibernate.model.PnPaymentInformation getPnPaymentInformation() {
        return this.pnPaymentInformation;
    }

    public void setPnPaymentInformation(net.project.hibernate.model.PnPaymentInformation pnPaymentInformation) {
        this.pnPaymentInformation = pnPaymentInformation;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnCcTransactionPayment) ) return false;
        PnCcTransactionPayment castOther = (PnCcTransactionPayment) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
