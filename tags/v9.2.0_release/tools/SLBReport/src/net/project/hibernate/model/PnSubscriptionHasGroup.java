package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSubscriptionHasGroup implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSubscriptionHasGroupPK comp_id;

    /** persistent field */
    private BigDecimal subscriberBatchId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnSubscription pnSubscription;

    /** persistent field */
    private net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType;

    /** full constructor */
    public PnSubscriptionHasGroup(net.project.hibernate.model.PnSubscriptionHasGroupPK comp_id, BigDecimal subscriberBatchId, net.project.hibernate.model.PnSubscription pnSubscription, net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType) {
        this.comp_id = comp_id;
        this.subscriberBatchId = subscriberBatchId;
        this.pnSubscription = pnSubscription;
        this.pnNotificationDeliveryType = pnNotificationDeliveryType;
    }

    /** default constructor */
    public PnSubscriptionHasGroup() {
    }

    /** minimal constructor */
    public PnSubscriptionHasGroup(net.project.hibernate.model.PnSubscriptionHasGroupPK comp_id, BigDecimal subscriberBatchId, net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType) {
        this.comp_id = comp_id;
        this.subscriberBatchId = subscriberBatchId;
        this.pnNotificationDeliveryType = pnNotificationDeliveryType;
    }

    public net.project.hibernate.model.PnSubscriptionHasGroupPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSubscriptionHasGroupPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getSubscriberBatchId() {
        return this.subscriberBatchId;
    }

    public void setSubscriberBatchId(BigDecimal subscriberBatchId) {
        this.subscriberBatchId = subscriberBatchId;
    }

    public net.project.hibernate.model.PnSubscription getPnSubscription() {
        return this.pnSubscription;
    }

    public void setPnSubscription(net.project.hibernate.model.PnSubscription pnSubscription) {
        this.pnSubscription = pnSubscription;
    }

    public net.project.hibernate.model.PnNotificationDeliveryType getPnNotificationDeliveryType() {
        return this.pnNotificationDeliveryType;
    }

    public void setPnNotificationDeliveryType(net.project.hibernate.model.PnNotificationDeliveryType pnNotificationDeliveryType) {
        this.pnNotificationDeliveryType = pnNotificationDeliveryType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSubscriptionHasGroup) ) return false;
        PnSubscriptionHasGroup castOther = (PnSubscriptionHasGroup) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
