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
 * PnWorkflowStepPK generated by hbm2java
 */
@Embeddable
public class PnWorkflowStepPK implements Serializable {


     private Integer stepId;
     private Integer workflowId;

    public PnWorkflowStepPK() {
    }

    public PnWorkflowStepPK(Integer stepId, Integer workflowId) {
       this.stepId = stepId;
       this.workflowId = workflowId;
    }
   


    @Column(name="STEP_ID", nullable=false, length=20)
    public Integer getStepId() {
        return this.stepId;
    }
    
    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }


    @Column(name="WORKFLOW_ID", nullable=false, length=20)
    public Integer getWorkflowId() {
        return this.workflowId;
    }
    
    public void setWorkflowId(Integer workflowId) {
        this.workflowId = workflowId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("stepId", getStepId())
            .append("workflowId", getWorkflowId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWorkflowStepPK) ) return false;
        PnWorkflowStepPK castOther = (PnWorkflowStepPK) other;
        return new EqualsBuilder()
            .append(this.getStepId(), castOther.getStepId())
            .append(this.getWorkflowId(), castOther.getWorkflowId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getStepId())
            .append(getWorkflowId())
            .toHashCode();
    }

}
