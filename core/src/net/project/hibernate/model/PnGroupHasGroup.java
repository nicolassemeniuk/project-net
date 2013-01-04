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


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnGroupHasGroup generated by hbm2java
 */
@Entity
@Table(name="PN_GROUP_HAS_GROUP"
)
public class PnGroupHasGroup  implements java.io.Serializable {

    /** identifier field */
    private PnGroupHasGroupPK comp_id;
    
    /** nullable persistent field */
    private PnGroup pnGroupByMemberGroupId;
    
    /** nullable persistent field */
    private PnGroup pnGroupByGroupId;

    public PnGroupHasGroup() {
    }

    public PnGroupHasGroup(PnGroupHasGroupPK comp_id) {
        this.comp_id = comp_id;
    }
    
    public PnGroupHasGroup(PnGroupHasGroupPK comp_id, PnGroup pnGroupByMemberGroupId, PnGroup pnGroupByGroupId) {
       this.comp_id = comp_id;
       this.pnGroupByMemberGroupId = pnGroupByMemberGroupId;
       this.pnGroupByGroupId = pnGroupByGroupId;
    }
   
    @EmbeddedId
    @AttributeOverrides( {
        @AttributeOverride(name="groupId", column=@Column(name="GROUP_ID", nullable=false, length=20) ), 
        @AttributeOverride(name="memberGroupId", column=@Column(name="MEMBER_GROUP_ID", nullable=false, length=20) ) } )
    public PnGroupHasGroupPK getComp_id() {
        return this.comp_id;
    }
    
    public void setComp_id(PnGroupHasGroupPK comp_id) {
        this.comp_id = comp_id;
    }

    @ManyToOne(fetch=FetchType.LAZY, targetEntity=PnGroup.class)
    @JoinColumn(name="MEMBER_GROUP_ID", insertable=false, updatable=false)
    public PnGroup getPnGroupByMemberGroupId() {
        return this.pnGroupByMemberGroupId;
    }
    
    public void setPnGroupByMemberGroupId(PnGroup pnGroupByMemberGroupId) {
        this.pnGroupByMemberGroupId = pnGroupByMemberGroupId;
    }

    @ManyToOne(fetch=FetchType.LAZY, targetEntity=PnGroup.class)
    @JoinColumn(name="GROUP_ID", insertable=false, updatable=false)
    public PnGroup getPnGroupByGroupId() {
        return this.pnGroupByGroupId;
    }
    
    public void setPnGroupByGroupId(PnGroup pnGroupByGroupId) {
        this.pnGroupByGroupId = pnGroupByGroupId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnGroupHasGroup) ) return false;
        PnGroupHasGroup castOther = (PnGroupHasGroup) other;
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


