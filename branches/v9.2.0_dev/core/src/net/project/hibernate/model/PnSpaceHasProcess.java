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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * PnSpaceHasProcess generated by hbm2java
 */
@Entity
@Table(name="PN_SPACE_HAS_PROCESS"
)
public class PnSpaceHasProcess implements Serializable {


     private PnSpaceHasProcessPK comp_id;
     private PnProcess pnProcess;

    public PnSpaceHasProcess() {
    }

	
    public PnSpaceHasProcess(PnSpaceHasProcessPK comp_id) {
        this.comp_id = comp_id;
    }
    public PnSpaceHasProcess(PnSpaceHasProcessPK comp_id, PnProcess pnProcess) {
       this.comp_id = comp_id;
       this.pnProcess = pnProcess;
    }
   
     @EmbeddedId

    
    @AttributeOverrides( {
        @AttributeOverride(name="spaceId", column=@Column(name="SPACE_ID", nullable=false, length=20) ), 
        @AttributeOverride(name="processId", column=@Column(name="PROCESS_ID", nullable=false, length=20) ) } )
    public PnSpaceHasProcessPK getComp_id() {
        return this.comp_id;
    }
    
    public void setComp_id(PnSpaceHasProcessPK comp_id) {
        this.comp_id = comp_id;
    }

@ManyToOne(fetch=FetchType.LAZY, targetEntity=PnProcess.class)
    @JoinColumn(name="PROCESS_ID", insertable=false, updatable=false)
    public PnProcess getPnProcess() {
        return this.pnProcess;
    }
    
    public void setPnProcess(PnProcess pnProcess) {
        this.pnProcess = pnProcess;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasProcess) ) return false;
        PnSpaceHasProcess castOther = (PnSpaceHasProcess) other;
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
