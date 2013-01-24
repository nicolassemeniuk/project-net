package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnNotificationDeliveryType implements Serializable {

    /** identifier field */
    private BigDecimal deliveryTypeId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnPersonNotificationAddresses;

    /** persistent field */
    private Set pnDeliveryAddresses;

    /** persistent field */
    private Set pnSubscriptionHasGroups;

    /** persistent field */
    private Set pnNotifications;

    /** full constructor */
    public PnNotificationDeliveryType(BigDecimal deliveryTypeId, String name, String description, String recordStatus, Set pnPersonNotificationAddresses, Set pnDeliveryAddresses, Set pnSubscriptionHasGroups, Set pnNotifications) {
        this.deliveryTypeId = deliveryTypeId;
        this.name = name;
        this.description = description;
        this.recordStatus = recordStatus;
        this.pnPersonNotificationAddresses = pnPersonNotificationAddresses;
        this.pnDeliveryAddresses = pnDeliveryAddresses;
        this.pnSubscriptionHasGroups = pnSubscriptionHasGroups;
        this.pnNotifications = pnNotifications;
    }

    /** default constructor */
    public PnNotificationDeliveryType() {
    }

    /** minimal constructor */
    public PnNotificationDeliveryType(BigDecimal deliveryTypeId, String name, Set pnPersonNotificationAddresses, Set pnDeliveryAddresses, Set pnSubscriptionHasGroups, Set pnNotifications) {
        this.deliveryTypeId = deliveryTypeId;
        this.name = name;
        this.pnPersonNotificationAddresses = pnPersonNotificationAddresses;
        this.pnDeliveryAddresses = pnDeliveryAddresses;
        this.pnSubscriptionHasGroups = pnSubscriptionHasGroups;
        this.pnNotifications = pnNotifications;
    }

    public PnNotificationDeliveryType(BigDecimal deliveryTypeId) {
    	this.deliveryTypeId = deliveryTypeId;
    }
    
    public BigDecimal getDeliveryTypeId() {
        return this.deliveryTypeId;
    }

    public void setDeliveryTypeId(BigDecimal deliveryTypeId) {
        this.deliveryTypeId = deliveryTypeId;
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

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Set getPnPersonNotificationAddresses() {
        return this.pnPersonNotificationAddresses;
    }

    public void setPnPersonNotificationAddresses(Set pnPersonNotificationAddresses) {
        this.pnPersonNotificationAddresses = pnPersonNotificationAddresses;
    }

    public Set getPnDeliveryAddresses() {
        return this.pnDeliveryAddresses;
    }

    public void setPnDeliveryAddresses(Set pnDeliveryAddresses) {
        this.pnDeliveryAddresses = pnDeliveryAddresses;
    }

    public Set getPnSubscriptionHasGroups() {
        return this.pnSubscriptionHasGroups;
    }

    public void setPnSubscriptionHasGroups(Set pnSubscriptionHasGroups) {
        this.pnSubscriptionHasGroups = pnSubscriptionHasGroups;
    }

    public Set getPnNotifications() {
        return this.pnNotifications;
    }

    public void setPnNotifications(Set pnNotifications) {
        this.pnNotifications = pnNotifications;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("deliveryTypeId", getDeliveryTypeId())
            .toString();
    }

}
