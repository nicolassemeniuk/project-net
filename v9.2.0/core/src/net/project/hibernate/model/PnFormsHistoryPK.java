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
 * PnFormsHistoryPK generated by hbm2java
 */
@Embeddable
public class PnFormsHistoryPK  implements java.io.Serializable {

    /** identifier field */
    private Integer objectId;

    /** identifier field */
    private Integer formsHistoryId;

    public PnFormsHistoryPK() {
    }

    public PnFormsHistoryPK(Integer objectId, Integer formsHistoryId) {
       this.objectId = objectId;
       this.formsHistoryId = formsHistoryId;
    }
   


    @Column(name="OBJECT_ID", nullable=false, length=20)
    public Integer getObjectId() {
        return this.objectId;
    }
    
    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }


    @Column(name="FORMS_HISTORY_ID", nullable=false, length=20)
    public Integer getFormsHistoryId() {
        return this.formsHistoryId;
    }
    
    public void setFormsHistoryId(Integer formsHistoryId) {
        this.formsHistoryId = formsHistoryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .append("formsHistoryId", getFormsHistoryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnFormsHistoryPK) ) return false;
        PnFormsHistoryPK castOther = (PnFormsHistoryPK) other;
        return new EqualsBuilder()
            .append(this.getObjectId(), castOther.getObjectId())
            .append(this.getFormsHistoryId(), castOther.getFormsHistoryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getObjectId())
            .append(getFormsHistoryId())
            .toHashCode();
    }
}


