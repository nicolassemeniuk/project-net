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


import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *        Counts the members of every group.
 * Only returns groups that have members
 *     
 */
@Entity
@Table(name="PN_GROUP_MEMBER_COUNT_VIEW"
)
public class PnGroupMemberCountView  implements java.io.Serializable {

    /** identifier field */
    private BigDecimal groupId;

    /** identifier field */
    private BigDecimal memberCount;

    public PnGroupMemberCountView() {
    }

    public PnGroupMemberCountView(BigDecimal groupId, BigDecimal memberCount) {
       this.groupId = groupId;
       this.memberCount = memberCount;
    }
   

    
    @Column(name="GROUP_ID", nullable=false, length=20)
    public BigDecimal getGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(BigDecimal groupId) {
        this.groupId = groupId;
    }

    
    @Column(name="MEMBER_COUNT", nullable=false, length=22)
    public BigDecimal getMemberCount() {
        return this.memberCount;
    }
    
    public void setMemberCount(BigDecimal memberCount) {
        this.memberCount = memberCount;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("groupId", getGroupId())
            .append("memberCount", getMemberCount())
            .toString();
    }

}


