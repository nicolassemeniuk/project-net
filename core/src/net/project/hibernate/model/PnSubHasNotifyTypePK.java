/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.hibernate.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * PnSubHasNotifyTypePK generated by hbm2java
 */
@Embeddable
public class PnSubHasNotifyTypePK implements Serializable {


     private Integer notificationTypeId;
     private Integer subscriptionId;

    public PnSubHasNotifyTypePK() {
    }

    public PnSubHasNotifyTypePK(Integer notificationTypeId, Integer subscriptionId) {
       this.notificationTypeId = notificationTypeId;
       this.subscriptionId = subscriptionId;
    }
   


    @Column(name="NOTIFICATION_TYPE_ID", nullable=false, length=20)
    public Integer getNotificationTypeId() {
        return this.notificationTypeId;
    }
    
    public void setNotificationTypeId(Integer notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }


    @Column(name="SUBSCRIPTION_ID", nullable=false, length=20)
    public Integer getSubscriptionId() {
        return this.subscriptionId;
    }
    
    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("notificationTypeId", getNotificationTypeId())
            .append("subscriptionId", getSubscriptionId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSubHasNotifyTypePK) ) return false;
        PnSubHasNotifyTypePK castOther = (PnSubHasNotifyTypePK) other;
        return new EqualsBuilder()
            .append(this.getNotificationTypeId(), castOther.getNotificationTypeId())
            .append(this.getSubscriptionId(), castOther.getSubscriptionId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getNotificationTypeId())
            .append(getSubscriptionId())
            .toHashCode();
    }

}
