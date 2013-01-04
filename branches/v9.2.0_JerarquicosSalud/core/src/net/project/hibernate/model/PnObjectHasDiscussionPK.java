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
 * PnObjectHasDiscussionPK generated by hbm2java
 */
@Embeddable
public class PnObjectHasDiscussionPK implements Serializable {


     private Integer objectId;
     private Integer discussionGroupId;

    public PnObjectHasDiscussionPK() {
    }

    public PnObjectHasDiscussionPK(Integer objectId, Integer discussionGroupId) {
       this.objectId = objectId;
       this.discussionGroupId = discussionGroupId;
    }
   
    @Column(name="OBJECT_ID", nullable=false, length=20)
    public Integer getObjectId() {
        return this.objectId;
    }
    
    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }


    @Column(name="DISCUSSION_GROUP_ID", nullable=false, length=20)
    public Integer getDiscussionGroupId() {
        return this.discussionGroupId;
    }
    
    public void setDiscussionGroupId(Integer discussionGroupId) {
        this.discussionGroupId = discussionGroupId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .append("discussionGroupId", getDiscussionGroupId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnObjectHasDiscussionPK) ) return false;
        PnObjectHasDiscussionPK castOther = (PnObjectHasDiscussionPK) other;
        return new EqualsBuilder()
            .append(this.getObjectId(), castOther.getObjectId())
            .append(this.getDiscussionGroupId(), castOther.getDiscussionGroupId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getObjectId())
            .append(getDiscussionGroupId())
            .toHashCode();
    }

}
