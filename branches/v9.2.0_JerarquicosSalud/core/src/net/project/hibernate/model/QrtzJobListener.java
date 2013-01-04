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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * QrtzJobListener generated by hbm2java
 */
@Entity
@Table(name="QRTZ_JOB_LISTENERS"
)
public class QrtzJobListener  implements Serializable {


     private QrtzJobListenerPK comp_id;
     private QrtzJobDetail qrtzJobDetail;

    public QrtzJobListener() {
    }

	
    public QrtzJobListener(QrtzJobListenerPK comp_id) {
        this.comp_id = comp_id;
    }
    public QrtzJobListener(QrtzJobListenerPK comp_id, QrtzJobDetail qrtzJobDetail) {
       this.comp_id = comp_id;
       this.qrtzJobDetail = qrtzJobDetail;
    }
   
     @EmbeddedId

    
    @AttributeOverrides( {
        @AttributeOverride(name="jobName", column=@Column(name="JOB_NAME", nullable=false, length=80) ), 
        @AttributeOverride(name="jobGroup", column=@Column(name="JOB_GROUP", nullable=false, length=80) ), 
        @AttributeOverride(name="jobListener", column=@Column(name="JOB_LISTENER", nullable=false, length=80) ) } )
    public QrtzJobListenerPK getComp_id() {
        return this.comp_id;
    }
    
    public void setComp_id(QrtzJobListenerPK comp_id) {
        this.comp_id = comp_id;
    }

@ManyToOne(fetch=FetchType.LAZY, targetEntity=QrtzJobDetail.class)
    @JoinColumns( { 
        @JoinColumn(name="JOB_NAME", referencedColumnName="JOB_NAME", insertable=false, updatable=false), 
        @JoinColumn(name="JOB_GROUP", referencedColumnName="JOB_GROUP", insertable=false, updatable=false) } )
    public QrtzJobDetail getQrtzJobDetail() {
        return this.qrtzJobDetail;
    }
    
    public void setQrtzJobDetail(QrtzJobDetail qrtzJobDetail) {
        this.qrtzJobDetail = qrtzJobDetail;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof QrtzJobListener) ) return false;
        QrtzJobListener castOther = (QrtzJobListener) other;
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
