package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnNotificationType implements Serializable {

    /** identifier field */
    private BigDecimal notificationTypeId;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private String defaultMessage;

    /** nullable persistent field */
    private Date createDate;

    /** nullable persistent field */
    private BigDecimal createdById;

    /** nullable persistent field */
    private Date modifiedDate;

    /** nullable persistent field */
    private BigDecimal modifiedById;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Date crc;

    /** persistent field */
    private net.project.hibernate.model.PnNotificationObjectType pnNotificationObjectType;

    /** persistent field */
    private Set pnEventHasNotifications;

    /** persistent field */
    private Set pnScheduledSubscriptions;

    /** persistent field */
    private Set pnSubHasNotifyTypes;

    /** full constructor */
    public PnNotificationType(BigDecimal notificationTypeId, String name, String description, String defaultMessage, Date createDate, BigDecimal createdById, Date modifiedDate, BigDecimal modifiedById, String recordStatus, Date crc, net.project.hibernate.model.PnNotificationObjectType pnNotificationObjectType, Set pnEventHasNotifications, Set pnScheduledSubscriptions, Set pnSubHasNotifyTypes) {
        this.notificationTypeId = notificationTypeId;
        this.name = name;
        this.description = description;
        this.defaultMessage = defaultMessage;
        this.createDate = createDate;
        this.createdById = createdById;
        this.modifiedDate = modifiedDate;
        this.modifiedById = modifiedById;
        this.recordStatus = recordStatus;
        this.crc = crc;
        this.pnNotificationObjectType = pnNotificationObjectType;
        this.pnEventHasNotifications = pnEventHasNotifications;
        this.pnScheduledSubscriptions = pnScheduledSubscriptions;
        this.pnSubHasNotifyTypes = pnSubHasNotifyTypes;
    }

    /** default constructor */
    public PnNotificationType() {
    }

    /** minimal constructor */
    public PnNotificationType(BigDecimal notificationTypeId, String recordStatus, net.project.hibernate.model.PnNotificationObjectType pnNotificationObjectType, Set pnEventHasNotifications, Set pnScheduledSubscriptions, Set pnSubHasNotifyTypes) {
        this.notificationTypeId = notificationTypeId;
        this.recordStatus = recordStatus;
        this.pnNotificationObjectType = pnNotificationObjectType;
        this.pnEventHasNotifications = pnEventHasNotifications;
        this.pnScheduledSubscriptions = pnScheduledSubscriptions;
        this.pnSubHasNotifyTypes = pnSubHasNotifyTypes;
    }

    public BigDecimal getNotificationTypeId() {
        return this.notificationTypeId;
    }

    public void setNotificationTypeId(BigDecimal notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public BigDecimal getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(BigDecimal createdById) {
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

    public net.project.hibernate.model.PnNotificationObjectType getPnNotificationObjectType() {
        return this.pnNotificationObjectType;
    }

    public void setPnNotificationObjectType(net.project.hibernate.model.PnNotificationObjectType pnNotificationObjectType) {
        this.pnNotificationObjectType = pnNotificationObjectType;
    }

    public Set getPnEventHasNotifications() {
        return this.pnEventHasNotifications;
    }

    public void setPnEventHasNotifications(Set pnEventHasNotifications) {
        this.pnEventHasNotifications = pnEventHasNotifications;
    }

    public Set getPnScheduledSubscriptions() {
        return this.pnScheduledSubscriptions;
    }

    public void setPnScheduledSubscriptions(Set pnScheduledSubscriptions) {
        this.pnScheduledSubscriptions = pnScheduledSubscriptions;
    }

    public Set getPnSubHasNotifyTypes() {
        return this.pnSubHasNotifyTypes;
    }

    public void setPnSubHasNotifyTypes(Set pnSubHasNotifyTypes) {
        this.pnSubHasNotifyTypes = pnSubHasNotifyTypes;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("notificationTypeId", getNotificationTypeId())
            .toString();
    }

}
