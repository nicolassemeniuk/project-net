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
 * PnDirectoryHasPerson generated by hbm2java
 */
@Entity
@Table(name="PN_DIRECTORY_HAS_PERSON"
)
public class PnDirectoryHasPerson  implements java.io.Serializable {


    private PnDirectoryHasPersonPK comp_id;
     
    private int isDefault;
     
    private PnPerson pnPerson;
     
    private PnDirectory pnDirectory;

    public PnDirectoryHasPerson() {
    }

	
    public PnDirectoryHasPerson(PnDirectoryHasPersonPK comp_id, int isDefault) {
        this.comp_id = comp_id;
        this.isDefault = isDefault;
    }
    public PnDirectoryHasPerson(PnDirectoryHasPersonPK comp_id, int isDefault, PnPerson pnPerson, PnDirectory pnDirectory) {
       this.comp_id = comp_id;
       this.isDefault = isDefault;
       this.pnPerson = pnPerson;
       this.pnDirectory = pnDirectory;
    }
   
     @EmbeddedId

    
    @AttributeOverrides( {
        @AttributeOverride(name="directoryId", column=@Column(name="DIRECTORY_ID", nullable=false, length=20) ), 
        @AttributeOverride(name="personId", column=@Column(name="PERSON_ID", nullable=false, length=20) ) } )
    public PnDirectoryHasPersonPK getComp_id() {
        return this.comp_id;
    }
    
    public void setComp_id(PnDirectoryHasPersonPK comp_id) {
        this.comp_id = comp_id;
    }

    
    @Column(name="IS_DEFAULT", nullable=false, length=1)
    public int getIsDefault() {
        return this.isDefault;
    }
    
    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

@ManyToOne(fetch=FetchType.LAZY, targetEntity=PnPerson.class)
    @JoinColumn(name="PERSON_ID", insertable=false, updatable=false)
    public PnPerson getPnPerson() {
        return this.pnPerson;
    }
    
    public void setPnPerson(PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

@ManyToOne(fetch=FetchType.LAZY, targetEntity=PnDirectory.class)
    @JoinColumn(name="DIRECTORY_ID", insertable=false, updatable=false)
    public PnDirectory getPnDirectory() {
        return this.pnDirectory;
    }
    
    public void setPnDirectory(PnDirectory pnDirectory) {
        this.pnDirectory = pnDirectory;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDirectoryHasPerson) ) return false;
        PnDirectoryHasPerson castOther = (PnDirectoryHasPerson) other;
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


