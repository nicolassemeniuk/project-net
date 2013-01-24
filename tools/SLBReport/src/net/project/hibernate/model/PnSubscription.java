package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSubscription implements Serializable {

    /** identifier field */
    private BigDecimal subscriptionId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private BigDecimal subscriptionTypeId;

    /** nullable persistent field */
    private Date createdDate;

    /** nullable persistent field */
    private BigDecimal createdById;

    /** nullable persistent field */
    private Date modifiedDate;

    /** nullable persistent field */
    private BigDecimal modifiedBy;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Date crc;

    /** nullable persistent field */
    private Integer deliveryInterval;

    /** persistent field */
    private BigDecimal subscriberBatchId;

    /** nullable persistent field */
    private Clob customMessageClob;

    /** persistent field */
    private Set pnObjectTypeSubscriptions;

    /** persistent field */
    private Set pnObjectHasSubscriptions;

    /** persistent field */
    private Set pnSpaceHasSubscriptions;

    /** persistent field */
    private Set pnSubHasNotifyTypes;

    /** persistent field */
    private Set pnSubscriptionHasGroups;

    /** persistent field */
    private Set pnWorkflowSteps;

    /** full constructor */
    public PnSubscription(BigDecimal subscriptionId, String name, String description, BigDecimal subscriptionTypeId, Date createdDate, BigDecimal createdById, Date modifiedDate, BigDecimal modifiedBy, String recordStatus, Date crc, Integer deliveryInterval, BigDecimal subscriberBatchId, Clob customMessageClob, Set pnObjectTypeSubscriptions, Set pnObjectHasSubscriptions, Set pnSpaceHasSubscriptions, Set pnSubHasNotifyTypes, Set pnSubscriptionHasGroups, Set pnWorkflowSteps) {
        this.subscriptionId = subscriptionId;
        this.name = name;
        this.description = description;
        this.subscriptionTypeId = subscriptionTypeId;
        this.createdDate = createdDate;
        this.createdById = createdById;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
        this.recordStatus = recordStatus;
        this.crc = crc;
        this.deliveryInterval = deliveryInterval;
        this.subscriberBatchId = subscriberBatchId;
        this.customMessageClob = customMessageClob;
        this.pnObjectTypeSubscriptions = pnObjectTypeSubscriptions;
        this.pnObjectHasSubscriptions = pnObjectHasSubscriptions;
        this.pnSpaceHasSubscriptions = pnSpaceHasSubscriptions;
        this.pnSubHasNotifyTypes = pnSubHasNotifyTypes;
        this.pnSubscriptionHasGroups = pnSubscriptionHasGroups;
        this.pnWorkflowSteps = pnWorkflowSteps;
    }

    /** default constructor */
    public PnSubscription() {
    }

    /** minimal constructor */
    public PnSubscription(BigDecimal subscriptionId, String name, String recordStatus, BigDecimal subscriberBatchId, Set pnObjectTypeSubscriptions, Set pnObjectHasSubscriptions, Set pnSpaceHasSubscriptions, Set pnSubHasNotifyTypes, Set pnSubscriptionHasGroups, Set pnWorkflowSteps) {
        this.subscriptionId = subscriptionId;
        this.name = name;
        this.recordStatus = recordStatus;
        this.subscriberBatchId = subscriberBatchId;
        this.pnObjectTypeSubscriptions = pnObjectTypeSubscriptions;
        this.pnObjectHasSubscriptions = pnObjectHasSubscriptions;
        this.pnSpaceHasSubscriptions = pnSpaceHasSubscriptions;
        this.pnSubHasNotifyTypes = pnSubHasNotifyTypes;
        this.pnSubscriptionHasGroups = pnSubscriptionHasGroups;
        this.pnWorkflowSteps = pnWorkflowSteps;
    }

    public BigDecimal getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(BigDecimal subscriptionId) {
        this.subscriptionId = subscriptionId;
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

    public BigDecimal getSubscriptionTypeId() {
        return this.subscriptionTypeId;
    }

    public void setSubscriptionTypeId(BigDecimal subscriptionTypeId) {
        this.subscriptionTypeId = subscriptionTypeId;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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

    public BigDecimal getModifiedBy() {
        return this.modifiedBy;
    }

    public void setModifiedBy(BigDecimal modifiedBy) {
        this.modifiedBy = modifiedBy;
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

    public Set getPnObjectTypeSubscriptions() {
        return this.pnObjectTypeSubscriptions;
    }

    public void setPnObjectTypeSubscriptions(Set pnObjectTypeSubscriptions) {
        this.pnObjectTypeSubscriptions = pnObjectTypeSubscriptions;
    }

    public Set getPnObjectHasSubscriptions() {
        return this.pnObjectHasSubscriptions;
    }

    public void setPnObjectHasSubscriptions(Set pnObjectHasSubscriptions) {
        this.pnObjectHasSubscriptions = pnObjectHasSubscriptions;
    }

    public Set getPnSpaceHasSubscriptions() {
        return this.pnSpaceHasSubscriptions;
    }

    public void setPnSpaceHasSubscriptions(Set pnSpaceHasSubscriptions) {
        this.pnSpaceHasSubscriptions = pnSpaceHasSubscriptions;
    }

    public Set getPnSubHasNotifyTypes() {
        return this.pnSubHasNotifyTypes;
    }

    public void setPnSubHasNotifyTypes(Set pnSubHasNotifyTypes) {
        this.pnSubHasNotifyTypes = pnSubHasNotifyTypes;
    }

    public Set getPnSubscriptionHasGroups() {
        return this.pnSubscriptionHasGroups;
    }

    public void setPnSubscriptionHasGroups(Set pnSubscriptionHasGroups) {
        this.pnSubscriptionHasGroups = pnSubscriptionHasGroups;
    }

    public Set getPnWorkflowSteps() {
        return this.pnWorkflowSteps;
    }

    public void setPnWorkflowSteps(Set pnWorkflowSteps) {
        this.pnWorkflowSteps = pnWorkflowSteps;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("subscriptionId", getSubscriptionId())
            .toString();
    }

}
