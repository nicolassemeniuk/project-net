package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDeliveryAddress implements Serializable {

    /** identifier field */
    private BigDecimal addressId;

    /** persistent field */
    private BigDecimal addressGroupId;

    /** persistent field */
    private String address;

    /** persistent field */
    private net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType;

    /** full constructor */
    public PnDeliveryAddress(BigDecimal addressId, BigDecimal addressGroupId, String address, net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType) {
        this.addressId = addressId;
        this.addressGroupId = addressGroupId;
        this.address = address;
        this.pnNotificationDeliveryType = pnNotificationDeliveryType;
    }

    /** default constructor */
    public PnDeliveryAddress() {
    }

    public BigDecimal getAddressId() {
        return this.addressId;
    }

    public void setAddressId(BigDecimal addressId) {
        this.addressId = addressId;
    }

    public BigDecimal getAddressGroupId() {
        return this.addressGroupId;
    }

    public void setAddressGroupId(BigDecimal addressGroupId) {
        this.addressGroupId = addressGroupId;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public net.project.hibernate.model.PnNotificationDeliveryType getPnNotificationDeliveryType() {
        return this.pnNotificationDeliveryType;
    }

    public void setPnNotificationDeliveryType(net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType) {
        this.pnNotificationDeliveryType = pnNotificationDeliveryType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("addressId", getAddressId())
            .toString();
    }

}
