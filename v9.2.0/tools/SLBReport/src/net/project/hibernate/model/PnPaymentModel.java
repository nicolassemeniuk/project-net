package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPaymentModel implements Serializable {

    /** identifier field */
    private BigDecimal paymentModelId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPaymentModelCharge pnPaymentModelCharge;

    /** persistent field */
    private net.project.hibernate.model.PnPaymentModelType pnPaymentModelType;

    /** persistent field */
    private Set pnPaymentInformations;

    /** full constructor */
    public PnPaymentModel(BigDecimal paymentModelId, net.project.hibernate.model.PnPaymentModelCharge pnPaymentModelCharge, net.project.hibernate.model.PnPaymentModelType pnPaymentModelType, Set pnPaymentInformations) {
        this.paymentModelId = paymentModelId;
        this.pnPaymentModelCharge = pnPaymentModelCharge;
        this.pnPaymentModelType = pnPaymentModelType;
        this.pnPaymentInformations = pnPaymentInformations;
    }

    /** default constructor */
    public PnPaymentModel() {
    }

    /** minimal constructor */
    public PnPaymentModel(BigDecimal paymentModelId, net.project.hibernate.model.PnPaymentModelType pnPaymentModelType, Set pnPaymentInformations) {
        this.paymentModelId = paymentModelId;
        this.pnPaymentModelType = pnPaymentModelType;
        this.pnPaymentInformations = pnPaymentInformations;
    }

    public BigDecimal getPaymentModelId() {
        return this.paymentModelId;
    }

    public void setPaymentModelId(BigDecimal paymentModelId) {
        this.paymentModelId = paymentModelId;
    }

    public net.project.hibernate.model.PnPaymentModelCharge getPnPaymentModelCharge() {
        return this.pnPaymentModelCharge;
    }

    public void setPnPaymentModelCharge(net.project.hibernate.model.PnPaymentModelCharge pnPaymentModelCharge) {
        this.pnPaymentModelCharge = pnPaymentModelCharge;
    }

    public net.project.hibernate.model.PnPaymentModelType getPnPaymentModelType() {
        return this.pnPaymentModelType;
    }

    public void setPnPaymentModelType(net.project.hibernate.model.PnPaymentModelType pnPaymentModelType) {
        this.pnPaymentModelType = pnPaymentModelType;
    }

    public Set getPnPaymentInformations() {
        return this.pnPaymentInformations;
    }

    public void setPnPaymentInformations(Set pnPaymentInformations) {
        this.pnPaymentInformations = pnPaymentInformations;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("paymentModelId", getPaymentModelId())
            .toString();
    }

}
