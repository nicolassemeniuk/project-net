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
 *        This view returns the count of envelopes where the envelope's current
 * version has a status that implies "Active"
 *     
 */
@Entity
@Table(name="PN_ENVELOPE_ACTIVE_VIEW"
)
public class PnEnvelopeActiveView  implements java.io.Serializable {

    /** identifier field */
    private BigDecimal workflowId;

    /** identifier field */
    private BigDecimal activeEnvelopeCount;

    public PnEnvelopeActiveView() {
    }

    public PnEnvelopeActiveView(BigDecimal workflowId, BigDecimal activeEnvelopeCount) {
       this.workflowId = workflowId;
       this.activeEnvelopeCount = activeEnvelopeCount;
    }
    
    @Column(name="WORKFLOW_ID", nullable=false, length=20)
    public BigDecimal getWorkflowId() {
        return this.workflowId;
    }
    
    public void setWorkflowId(BigDecimal workflowId) {
        this.workflowId = workflowId;
    }

    
    @Column(name="ACTIVE_ENVELOPE_COUNT", nullable=false, length=22)
    public BigDecimal getActiveEnvelopeCount() {
        return this.activeEnvelopeCount;
    }
    
    public void setActiveEnvelopeCount(BigDecimal activeEnvelopeCount) {
        this.activeEnvelopeCount = activeEnvelopeCount;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("workflowId", getWorkflowId())
            .append("activeEnvelopeCount", getActiveEnvelopeCount())
            .toString();
    }

}


