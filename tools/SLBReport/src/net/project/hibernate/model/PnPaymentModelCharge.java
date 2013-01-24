package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPaymentModelCharge implements Serializable {

    /** identifier field */
    private BigDecimal paymentModelId;

    /** persistent field */
    private String chargeCode;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPaymentModel pnPaymentModel;

    /** full constructor */
    public PnPaymentModelCharge(BigDecimal paymentModelId, String chargeCode, net.project.hibernate.model.PnPaymentModel pnPaymentModel) {
        this.paymentModelId = paymentModelId;
        this.chargeCode = chargeCode;
        this.pnPaymentModel = pnPaymentModel;
    }

    /** default constructor */
    public PnPaymentModelCharge() {
    }

    /** minimal constructor */
    public PnPaymentModelCharge(BigDecimal paymentModelId, String chargeCode) {
        this.paymentModelId = paymentModelId;
        this.chargeCode = chargeCode;
    }

    public BigDecimal getPaymentModelId() {
        return this.paymentModelId;
    }

    public void setPaymentModelId(BigDecimal paymentModelId) {
        this.paymentModelId = paymentModelId;
    }

    public String getChargeCode() {
        return this.chargeCode;
    }

    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    public net.project.hibernate.model.PnPaymentModel getPnPaymentModel() {
        return this.pnPaymentModel;
    }

    public void setPnPaymentModel(net.project.hibernate.model.PnPaymentModel pnPaymentModel) {
        this.pnPaymentModel = pnPaymentModel;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("paymentModelId", getPaymentModelId())
            .toString();
    }

}
