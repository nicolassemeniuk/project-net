package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonNotificationAddress implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** persistent field */
    private String deliveryAddress;

    /** nullable persistent field */
    private Integer isDefault;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType;

    /** full constructor */
    public PnPersonNotificationAddress(BigDecimal personId, String deliveryAddress, Integer isDefault, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType) {
        this.personId = personId;
        this.deliveryAddress = deliveryAddress;
        this.isDefault = isDefault;
        this.pnPerson = pnPerson;
        this.pnNotificationDeliveryType = pnNotificationDeliveryType;
    }

    /** default constructor */
    public PnPersonNotificationAddress() {
    }

    /** minimal constructor */
    public PnPersonNotificationAddress(BigDecimal personId, String deliveryAddress, net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType) {
        this.personId = personId;
        this.deliveryAddress = deliveryAddress;
        this.pnNotificationDeliveryType = pnNotificationDeliveryType;
    }
    
    public PnPersonNotificationAddress(BigDecimal personId, BigDecimal deliveryTypeId, String deliveryAddress,  Integer isDefault) {
        this.personId = personId;
        this.deliveryAddress = deliveryAddress;
        PnNotificationDeliveryType pnNotificationDeliveryTypeNew = new PnNotificationDeliveryType(deliveryTypeId);
        this.pnNotificationDeliveryType = pnNotificationDeliveryTypeNew;
        this.isDefault = isDefault;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Integer getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnNotificationDeliveryType getPnNotificationDeliveryType() {
        return this.pnNotificationDeliveryType;
    }

    public void setPnNotificationDeliveryType(net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType) {
        this.pnNotificationDeliveryType = pnNotificationDeliveryType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .toString();
    }

}
