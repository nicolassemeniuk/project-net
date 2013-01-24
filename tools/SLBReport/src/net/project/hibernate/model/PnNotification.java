package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnNotification implements Serializable {

    /** identifier field */
    private BigDecimal notificationId;

    /** persistent field */
    private String deliveryAddress;

    /** nullable persistent field */
    private Date createdDate;

    /** nullable persistent field */
    private String createdById;

    /** nullable persistent field */
    private Date modifiedDate;

    /** persistent field */
    private BigDecimal modifiedById;

    /** nullable persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Date crc;

    /** nullable persistent field */
    private String deliveryFromAddress;

    /** nullable persistent field */
    private BigDecimal customizationUserId;

    /** nullable persistent field */
    private BigDecimal senderId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnNotificationQueue pnNotificationQueue;

    /** persistent field */
    private net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType;

    /** persistent field */
    private net.project.hibernate.model.PnNotificationClob pnNotificationClob;

    /** persistent field */
    private Set pnNotificationLogs;

    /** full constructor */
    public PnNotification(BigDecimal notificationId, String deliveryAddress, Date createdDate, String createdById, Date modifiedDate, BigDecimal modifiedById, String recordStatus, Date crc, String deliveryFromAddress, BigDecimal customizationUserId, BigDecimal senderId, net.project.hibernate.model.PnNotificationQueue pnNotificationQueue, net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType, net.project.hibernate.model.PnNotificationClob pnNotificationClob, Set pnNotificationLogs) {
        this.notificationId = notificationId;
        this.deliveryAddress = deliveryAddress;
        this.createdDate = createdDate;
        this.createdById = createdById;
        this.modifiedDate = modifiedDate;
        this.modifiedById = modifiedById;
        this.recordStatus = recordStatus;
        this.crc = crc;
        this.deliveryFromAddress = deliveryFromAddress;
        this.customizationUserId = customizationUserId;
        this.senderId = senderId;
        this.pnNotificationQueue = pnNotificationQueue;
        this.pnNotificationDeliveryType = pnNotificationDeliveryType;
        this.pnNotificationClob = pnNotificationClob;
        this.pnNotificationLogs = pnNotificationLogs;
    }

    /** default constructor */
    public PnNotification() {
    }

    /** minimal constructor */
    public PnNotification(BigDecimal notificationId, String deliveryAddress, BigDecimal modifiedById, net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType, net.project.hibernate.model.PnNotificationClob pnNotificationClob, Set pnNotificationLogs) {
        this.notificationId = notificationId;
        this.deliveryAddress = deliveryAddress;
        this.modifiedById = modifiedById;
        this.pnNotificationDeliveryType = pnNotificationDeliveryType;
        this.pnNotificationClob = pnNotificationClob;
        this.pnNotificationLogs = pnNotificationLogs;
    }

    public BigDecimal getNotificationId() {
        return this.notificationId;
    }

    public void setNotificationId(BigDecimal notificationId) {
        this.notificationId = notificationId;
    }

    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public BigDecimal getModifiedById() {
        return this.modifiedById;
    }

    public void setModifiedById(BigDecimal modifiedById) {
        this.modifiedById = modifiedById;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public String getDeliveryFromAddress() {
        return this.deliveryFromAddress;
    }

    public void setDeliveryFromAddress(String deliveryFromAddress) {
        this.deliveryFromAddress = deliveryFromAddress;
    }

    public BigDecimal getCustomizationUserId() {
        return this.customizationUserId;
    }

    public void setCustomizationUserId(BigDecimal customizationUserId) {
        this.customizationUserId = customizationUserId;
    }

    public BigDecimal getSenderId() {
        return this.senderId;
    }

    public void setSenderId(BigDecimal senderId) {
        this.senderId = senderId;
    }

    public net.project.hibernate.model.PnNotificationQueue getPnNotificationQueue() {
        return this.pnNotificationQueue;
    }

    public void setPnNotificationQueue(net.project.hibernate.model.PnNotificationQueue pnNotificationQueue) {
        this.pnNotificationQueue = pnNotificationQueue;
    }

    public net.project.hibernate.model.PnNotificationDeliveryType getPnNotificationDeliveryType() {
        return this.pnNotificationDeliveryType;
    }

    public void setPnNotificationDeliveryType(net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType) {
        this.pnNotificationDeliveryType = pnNotificationDeliveryType;
    }

    public net.project.hibernate.model.PnNotificationClob getPnNotificationClob() {
        return this.pnNotificationClob;
    }

    public void setPnNotificationClob(net.project.hibernate.model.PnNotificationClob pnNotificationClob) {
        this.pnNotificationClob = pnNotificationClob;
    }

    public Set getPnNotificationLogs() {
        return this.pnNotificationLogs;
    }

    public void setPnNotificationLogs(Set pnNotificationLogs) {
        this.pnNotificationLogs = pnNotificationLogs;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("notificationId", getNotificationId())
            .toString();
    }

}
