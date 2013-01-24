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
// Generated Jun 13, 2009 11:41:49 PM by Hibernate Tools 3.2.4.GA


import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnDefaultObjectPermissionPK generated by hbm2java
 */
@Embeddable
public class PnDefaultObjectPermissionPK  implements java.io.Serializable {

    /** identifier field */
    private Integer spaceId;

    /** identifier field */
    private String objectType;

    /** identifier field */
    private Integer groupId;

    public PnDefaultObjectPermissionPK() {
    }

    public PnDefaultObjectPermissionPK(Integer spaceId, String objectType, Integer groupId) {
       this.spaceId = spaceId;
       this.objectType = objectType;
       this.groupId = groupId;
    }
   


    @Column(name="SPACE_ID", nullable=false, length=20)
    public Integer getSpaceId() {
        return this.spaceId;
    }
    
    public void setSpaceId(Integer spaceId) {
        this.spaceId = spaceId;
    }


    @Column(name="OBJECT_TYPE", nullable=false, length=80)
    public String getObjectType() {
        return this.objectType;
    }
    
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }


    @Column(name="GROUP_ID", nullable=false, length=20)
    public Integer getGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("objectType", getObjectType())
            .append("groupId", getGroupId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDefaultObjectPermissionPK) ) return false;
        PnDefaultObjectPermissionPK castOther = (PnDefaultObjectPermissionPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getObjectType(), castOther.getObjectType())
            .append(this.getGroupId(), castOther.getGroupId())
            .isEquals();
    }

	public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getObjectType())
            .append(getGroupId())
            .toHashCode();
    }
}


