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
 * PnEnvelopeVersionHasObjectPK generated by hbm2java
 */
@Embeddable
public class PnEnvelopeVersionHasObjectPK  implements java.io.Serializable {

    /** identifier field */
    private Integer objectId;

    /** identifier field */
    private Integer versionId;

    /** identifier field */
    private Integer envelopeId;

    public PnEnvelopeVersionHasObjectPK() {
    }

    public PnEnvelopeVersionHasObjectPK(Integer objectId, Integer versionId, Integer envelopeId) {
       this.objectId = objectId;
       this.versionId = versionId;
       this.envelopeId = envelopeId;
    }

    @Column(name="OBJECT_ID", nullable=false, length=20)
    public Integer getObjectId() {
        return this.objectId;
    }
    
    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }


    @Column(name="VERSION_ID", nullable=false, length=20)
    public Integer getVersionId() {
        return this.versionId;
    }
    
    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }


    @Column(name="ENVELOPE_ID", nullable=false, length=20)
    public Integer getEnvelopeId() {
        return this.envelopeId;
    }
    
    public void setEnvelopeId(Integer envelopeId) {
        this.envelopeId = envelopeId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .append("versionId", getVersionId())
            .append("envelopeId", getEnvelopeId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnEnvelopeVersionHasObjectPK) ) return false;
        PnEnvelopeVersionHasObjectPK castOther = (PnEnvelopeVersionHasObjectPK) other;
        return new EqualsBuilder()
            .append(this.getObjectId(), castOther.getObjectId())
            .append(this.getVersionId(), castOther.getVersionId())
            .append(this.getEnvelopeId(), castOther.getEnvelopeId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getObjectId())
            .append(getVersionId())
            .append(getEnvelopeId())
            .toHashCode();
    }
}


