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
import javax.persistence.Table;

/**
 * PnTaskDependency generated by hbm2java
 */
@Entity
@Table(name="PN_TASK_DEPENDENCY"
)
public class PnTaskDependency implements Serializable {


     private PnTaskDependencyPK comp_id;
     private Integer lag;
     private String lagUnits;

    public PnTaskDependency() {
    }

	
    public PnTaskDependency(PnTaskDependencyPK comp_id) {
        this.comp_id = comp_id;
    }

    public PnTaskDependency(PnTaskDependencyPK comp_id, Integer lag, String lagUnits) {
       this.comp_id = comp_id;
       this.lag = lag;
       this.lagUnits = lagUnits;
    }
   
     @EmbeddedId

    
    @AttributeOverrides( {
        @AttributeOverride(name="taskId", column=@Column(name="TASK_ID", nullable=false, length=20) ), 
        @AttributeOverride(name="dependencyId", column=@Column(name="DEPENDENCY_ID", nullable=false, length=20) ), 
        @AttributeOverride(name="dependencyTypeId", column=@Column(name="DEPENDENCY_TYPE_ID", nullable=false, length=20) ) } )
    public PnTaskDependencyPK getComp_id() {
        return this.comp_id;
    }
    
    public void setComp_id(PnTaskDependencyPK comp_id) {
        this.comp_id = comp_id;
    }

    
    @Column(name="LAG", length=22)
    public Integer getLag() {
        return this.lag;
    }
    
    public void setLag(Integer lag) {
        this.lag = lag;
    }

    
    @Column(name="LAG_UNITS", length=5)
    public String getLagUnits() {
        return this.lagUnits;
    }
    
    public void setLagUnits(String lagUnits) {
        this.lagUnits = lagUnits;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnTaskDependency) ) return false;
        PnTaskDependency castOther = (PnTaskDependency) other;
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
