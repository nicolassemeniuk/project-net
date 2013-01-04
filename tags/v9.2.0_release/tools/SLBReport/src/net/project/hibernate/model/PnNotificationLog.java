package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnNotificationLog implements Serializable {

    /** identifier field */
    private BigDecimal notificationLogId;

    /** nullable persistent field */
    private BigDecimal numberOfAttempts;

    /** persistent field */
    private BigDecimal deliveryStatus;

    /** nullable persistent field */
    private Date deliveryTime;

    /** nullable persistent field */
    private String deliveryNotes;

    /** nullable persistent field */
    private String recordStatus;

    /** persistent field */
    private net.project.hibernate.model.PnNotification pnNotification;

    /** full constructor */
    public PnNotificationLog(BigDecimal notificationLogId, BigDecimal numberOfAttempts, BigDecimal deliveryStatus, Date deliveryTime, String deliveryNotes, String recordStatus, net.project.hibernate.model.PnNotification pnNotification) {
        this.notificationLogId = notificationLogId;
        this.numberOfAttempts = numberOfAttempts;
        this.deliveryStatus = deliveryStatus;
        this.deliveryTime = deliveryTime;
        this.deliveryNotes = deliveryNotes;
        this.recordStatus = recordStatus;
        this.pnNotification = pnNotification;
    }

    /** default constructor */
    public PnNotificationLog() {
    }

    /** minimal constructor */
    public PnNotificationLog(BigDecimal notificationLogId, BigDecimal deliveryStatus, net.project.hibernate.model.PnNotification pnNotification) {
        this.notificationLogId = notificationLogId;
        this.deliveryStatus = deliveryStatus;
        this.pnNotification = pnNotification;
    }

    public BigDecimal getNotificationLogId() {
        return this.notificationLogId;
    }

    public void setNotificationLogId(BigDecimal notificationLogId) {
        this.notificationLogId = notificationLogId;
    }

    public BigDecimal getNumberOfAttempts() {
        return this.numberOfAttempts;
    }

    public void setNumberOfAttempts(BigDecimal numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }

    public BigDecimal getDeliveryStatus() {
        return this.deliveryStatus;
    }

    public void setDeliveryStatus(BigDecimal deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Date getDeliveryTime() {
        return this.deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDeliveryNotes() {
        return this.deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnNotification getPnNotification() {
        return this.pnNotification;
    }

    public void setPnNotification(net.project.hibernate.model.PnNotification pnNotification) {
        this.pnNotification = pnNotification;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("notificationLogId", getNotificationLogId())
            .toString();
    }

}
