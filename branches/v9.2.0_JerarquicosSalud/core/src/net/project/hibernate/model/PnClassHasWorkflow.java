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
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnClassHasWorkflow generated by hbm2java
 */
@Entity
@Table(name="PN_CLASS_HAS_WORKFLOW"
)
public class PnClassHasWorkflow  implements java.io.Serializable {

	/** identifier field */
    private PnClassHasWorkflowPK comp_id;
     
	/** nullable persistent field */
    private Integer isDefault;

    public PnClassHasWorkflow() {
    }

	
    public PnClassHasWorkflow(PnClassHasWorkflowPK comp_id) {
        this.comp_id = comp_id;
    }
    public PnClassHasWorkflow(PnClassHasWorkflowPK comp_id, Integer isDefault) {
       this.comp_id = comp_id;
       this.isDefault = isDefault;
    }
   
    @EmbeddedId
    @AttributeOverrides( {
        @AttributeOverride(name="classId", column=@Column(name="CLASS_ID", nullable=false, length=20) ), 
        @AttributeOverride(name="workflowId", column=@Column(name="WORKFLOW_ID", nullable=false, length=20) ) } )
    public PnClassHasWorkflowPK getComp_id() {
        return this.comp_id;
    }
    
    public void setComp_id(PnClassHasWorkflowPK comp_id) {
        this.comp_id = comp_id;
    }

    
    @Column(name="IS_DEFAULT", length=1)
    public Integer getIsDefault() {
        return this.isDefault;
    }
    
    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassHasWorkflow) ) return false;
        PnClassHasWorkflow castOther = (PnClassHasWorkflow) other;
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


