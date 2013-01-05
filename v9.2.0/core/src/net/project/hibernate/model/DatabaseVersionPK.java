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


import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * DatabaseVersionPK generated by hbm2java
 */
@Embeddable
public class DatabaseVersionPK  implements java.io.Serializable {

    /** identifier field */
    private Integer majorVersion;

    /** identifier field */
    private Integer minorVersion;

    /** identifier field */
    private Integer subMinorVersion;

    public DatabaseVersionPK() {
    }

    public DatabaseVersionPK(Integer majorVersion, Integer minorVersion, Integer subMinorVersion) {
       this.majorVersion = majorVersion;
       this.minorVersion = minorVersion;
       this.subMinorVersion = subMinorVersion;
    }
   


    @Column(name="MAJOR_VERSION", nullable=false, length=3)
    public Integer getMajorVersion() {
        return this.majorVersion;
    }
    
    public void setMajorVersion(Integer majorVersion) {
        this.majorVersion = majorVersion;
    }


    @Column(name="MINOR_VERSION", nullable=false, length=3)
    public Integer getMinorVersion() {
        return this.minorVersion;
    }
    
    public void setMinorVersion(Integer minorVersion) {
        this.minorVersion = minorVersion;
    }


    @Column(name="SUB_MINOR_VERSION", nullable=false, length=3)
    public Integer getSubMinorVersion() {
        return this.subMinorVersion;
    }
    
    public void setSubMinorVersion(Integer subMinorVersion) {
        this.subMinorVersion = subMinorVersion;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("majorVersion", getMajorVersion())
            .append("minorVersion", getMinorVersion())
            .append("subMinorVersion", getSubMinorVersion())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof DatabaseVersionPK) ) return false;
        DatabaseVersionPK castOther = (DatabaseVersionPK) other;
        return new EqualsBuilder()
            .append(this.getMajorVersion(), castOther.getMajorVersion())
            .append(this.getMinorVersion(), castOther.getMinorVersion())
            .append(this.getSubMinorVersion(), castOther.getSubMinorVersion())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getMajorVersion())
            .append(getMinorVersion())
            .append(getSubMinorVersion())
            .toHashCode();
    }



}


