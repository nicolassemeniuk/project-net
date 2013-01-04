package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnNotificationQueue implements Serializable {

    /** identifier field */
    private BigDecimal notificationId;

    /** nullable persistent field */
    private Date postedDate;

    /** nullable persistent field */
    private String postedById;

    /** nullable persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private BigDecimal batchId;

    /** nullable persistent field */
    private String deliveryStatus;

    /** nullable persistent field */
    private BigDecimal numberOfRetries;

    /** nullable persistent field */
    private Integer isImmediate;

    /** nullable persistent field */
    private net.project.hibernate.model.PnNotification pnNotification;

    /** full constructor */
    public PnNotificationQueue(BigDecimal notificationId, Date postedDate, String postedById, String recordStatus, BigDecimal batchId, String deliveryStatus, BigDecimal numberOfRetries, Integer isImmediate, net.project.hibernate.model.PnNotification pnNotification) {
        this.notificationId = notificationId;
        this.postedDate = postedDate;
        this.postedById = postedById;
        this.recordStatus = recordStatus;
        this.batchId = batchId;
        this.deliveryStatus = deliveryStatus;
        this.numberOfRetries = numberOfRetries;
        this.isImmediate = isImmediate;
        this.pnNotification = pnNotification;
    }

    /** default constructor */
    public PnNotificationQueue() {
    }

    /** minimal constructor */
    public PnNotificationQueue(BigDecimal notificationId) {
        this.notificationId = notificationId;
    }

    public BigDecimal getNotificationId() {
        return this.notificationId;
    }

    public void setNotificationId(BigDecimal notificationId) {
        this.notificationId = notificationId;
    }

    public Date getPostedDate() {
        return this.postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public String getPostedById() {
        return this.postedById;
    }

    public void setPostedById(String postedById) {
        this.postedById = postedById;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public BigDecimal getBatchId() {
        return this.batchId;
    }

    public void setBatchId(BigDecimal batchId) {
        this.batchId = batchId;
    }

    public String getDeliveryStatus() {
        return this.deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public BigDecimal getNumberOfRetries() {
        return this.numberOfRetries;
    }

    public void setNumberOfRetries(BigDecimal numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }

    public Integer getIsImmediate() {
        return this.isImmediate;
    }

    public void setIsImmediate(Integer isImmediate) {
        this.isImmediate = isImmediate;
    }

    public net.project.hibernate.model.PnNotification getPnNotification() {
        return this.pnNotification;
    }

    public void setPnNotification(net.project.hibernate.model.PnNotification pnNotification) {
        this.pnNotification = pnNotification;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("notificationId", getNotificationId())
            .toString();
    }

}
