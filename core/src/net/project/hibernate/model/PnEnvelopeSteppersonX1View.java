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
 *        This is a PRIVATE view for use by WORKFLOW only -- The view will produce duplicate rows.   See PN_ENVELOPE_STEP_PERSON_VIEW for public one.
 *     
 */
@Entity
@Table(name="PN_ENVELOPE_STEPPERSON_X1_VIEW"
)
public class PnEnvelopeSteppersonX1View  implements java.io.Serializable {

    /** identifier field */
    private BigDecimal envelopeId;

    /** identifier field */
    private BigDecimal workflowId;

    /** identifier field */
    private BigDecimal stepId;

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private Integer isParticipant;

    public PnEnvelopeSteppersonX1View() {
    }

    public PnEnvelopeSteppersonX1View(BigDecimal envelopeId, BigDecimal workflowId, BigDecimal stepId, BigDecimal personId, Integer isParticipant) {
       this.envelopeId = envelopeId;
       this.workflowId = workflowId;
       this.stepId = stepId;
       this.personId = personId;
       this.isParticipant = isParticipant;
    }
   

    
    @Column(name="ENVELOPE_ID", nullable=false, length=20)
    public BigDecimal getEnvelopeId() {
        return this.envelopeId;
    }
    
    public void setEnvelopeId(BigDecimal envelopeId) {
        this.envelopeId = envelopeId;
    }

    
    @Column(name="WORKFLOW_ID", nullable=false, length=20)
    public BigDecimal getWorkflowId() {
        return this.workflowId;
    }
    
    public void setWorkflowId(BigDecimal workflowId) {
        this.workflowId = workflowId;
    }

    
    @Column(name="STEP_ID", nullable=false, length=20)
    public BigDecimal getStepId() {
        return this.stepId;
    }
    
    public void setStepId(BigDecimal stepId) {
        this.stepId = stepId;
    }

    
    @Column(name="PERSON_ID", nullable=false, length=20)
    public BigDecimal getPersonId() {
        return this.personId;
    }
    
    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    
    @Column(name="IS_PARTICIPANT", nullable=false, length=1)
    public Integer getIsParticipant() {
        return this.isParticipant;
    }
    
    public void setIsParticipant(Integer isParticipant) {
        this.isParticipant = isParticipant;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("envelopeId", getEnvelopeId())
            .append("workflowId", getWorkflowId())
            .append("stepId", getStepId())
            .append("personId", getPersonId())
            .append("isParticipant", getIsParticipant())
            .toString();
    }

}


