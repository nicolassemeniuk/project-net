package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnScheduledSubscription implements Serializable {

    /** identifier field */
    private BigDecimal scheduledSubscriptionId;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private BigDecimal targetObjectId;

    /** nullable persistent field */
    private String targetObjectType;

    /** nullable persistent field */
    private String targetObjectXml;

    /** nullable persistent field */
    private Date eventTime;

    /** nullable persistent field */
    private BigDecimal eventType;

    /** nullable persistent field */
    private BigDecimal isQueued;

    /** nullable persistent field */
    private Date createDate;

    /** persistent field */
    private BigDecimal createdById;

    /** nullable persistent field */
    private Date modifiedDate;

    /** persistent field */
    private BigDecimal modifiedById;

    /** nullable persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Date crc;

    /** nullable persistent field */
    private Integer deliveryInterval;

    /** nullable persistent field */
    private Date deliveryDate;

    /** nullable persistent field */
    private BigDecimal targetObjectClobId;

    /** nullable persistent field */
    private BigDecimal batchId;

    /** nullable persistent field */
    private BigDecimal subscriberBatchId;

    /** nullable persistent field */
    private Clob customMessageClob;

    /** nullable persistent field */
    private BigDecimal spaceId;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnNotificationType pnNotificationType;

    /** full constructor */
    public PnScheduledSubscription(BigDecimal scheduledSubscriptionId, String name, String description, BigDecimal targetObjectId, String targetObjectType, String targetObjectXml, Date eventTime, BigDecimal eventType, BigDecimal isQueued, Date createDate, BigDecimal createdById, Date modifiedDate, BigDecimal modifiedById, String recordStatus, Date crc, Integer deliveryInterval, Date deliveryDate, BigDecimal targetObjectClobId, BigDecimal batchId, BigDecimal subscriberBatchId, Clob customMessageClob, BigDecimal spaceId, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnNotificationType pnNotificationType) {
        this.scheduledSubscriptionId = scheduledSubscriptionId;
        this.name = name;
        this.description = description;
        this.targetObjectId = targetObjectId;
        this.targetObjectType = targetObjectType;
        this.targetObjectXml = targetObjectXml;
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.isQueued = isQueued;
        this.createDate = createDate;
        this.createdById = createdById;
        this.modifiedDate = modifiedDate;
        this.modifiedById = modifiedById;
        this.recordStatus = recordStatus;
        this.crc = crc;
        this.deliveryInterval = deliveryInterval;
        this.deliveryDate = deliveryDate;
        this.targetObjectClobId = targetObjectClobId;
        this.batchId = batchId;
        this.subscriberBatchId = subscriberBatchId;
        this.customMessageClob = customMessageClob;
        this.spaceId = spaceId;
        this.pnPerson = pnPerson;
        this.pnNotificationType = pnNotificationType;
    }

    /** default constructor */
    public PnScheduledSubscription() {
    }

    /** minimal constructor */
    public PnScheduledSubscription(BigDecimal scheduledSubscriptionId, BigDecimal createdById, BigDecimal modifiedById, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnNotificationType pnNotificationType) {
        this.scheduledSubscriptionId = scheduledSubscriptionId;
        this.createdById = createdById;
        this.modifiedById = modifiedById;
        this.pnPerson = pnPerson;
        this.pnNotificationType = pnNotificationType;
    }

    public BigDecimal getScheduledSubscriptionId() {
        return this.scheduledSubscriptionId;
    }

    public void setScheduledSubscriptionId(BigDecimal scheduledSubscriptionId) {
        this.scheduledSubscriptionId = scheduledSubscriptionId;
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

    public BigDecimal getTargetObjectId() {
        return this.targetObjectId;
    }

    public void setTargetObjectId(BigDecimal targetObjectId) {
        this.targetObjectId = targetObjectId;
    }

    public String getTargetObjectType() {
        return this.targetObjectType;
    }

    public void setTargetObjectType(String targetObjectType) {
        this.targetObjectType = targetObjectType;
    }

    public String getTargetObjectXml() {
        return this.targetObjectXml;
    }

    public void setTargetObjectXml(String targetObjectXml) {
        this.targetObjectXml = targetObjectXml;
    }

    public Date getEventTime() {
        return this.eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public BigDecimal getEventType() {
        return this.eventType;
    }

    public void setEventType(BigDecimal eventType) {
        this.eventType = eventType;
    }

    public BigDecimal getIsQueued() {
        return this.isQueued;
    }

    public void setIsQueued(BigDecimal isQueued) {
        this.isQueued = isQueued;
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

    public Integer getDeliveryInterval() {
        return this.deliveryInterval;
    }

    public void setDeliveryInterval(Integer deliveryInterval) {
        this.deliveryInterval = deliveryInterval;
    }

    public Date getDeliveryDate() {
        return this.deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public BigDecimal getTargetObjectClobId() {
        return this.targetObjectClobId;
    }

    public void setTargetObjectClobId(BigDecimal targetObjectClobId) {
        this.targetObjectClobId = targetObjectClobId;
    }

    public BigDecimal getBatchId() {
        return this.batchId;
    }

    public void setBatchId(BigDecimal batchId) {
        this.batchId = batchId;
    }

    public BigDecimal getSubscriberBatchId() {
        return this.subscriberBatchId;
    }

    public void setSubscriberBatchId(BigDecimal subscriberBatchId) {
        this.subscriberBatchId = subscriberBatchId;
    }

    public Clob getCustomMessageClob() {
        return this.customMessageClob;
    }

    public void setCustomMessageClob(Clob customMessageClob) {
        this.customMessageClob = customMessageClob;
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnNotificationType getPnNotificationType() {
        return this.pnNotificationType;
    }

    public void setPnNotificationType(net.project.hibernate.model.PnNotificationType pnNotificationType) {
        this.pnNotificationType = pnNotificationType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("scheduledSubscriptionId", getScheduledSubscriptionId())
            .toString();
    }

}
