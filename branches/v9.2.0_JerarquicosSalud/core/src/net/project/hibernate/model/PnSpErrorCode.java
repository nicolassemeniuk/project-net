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

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PnSpErrorCode generated by hbm2java
 */
@Entity
@Table(name="PN_SP_ERROR_CODES"
)
public class PnSpErrorCode implements Serializable {


     private Integer errorCode;
     private String errorName;
     private String errorDescription;

    public PnSpErrorCode() {
    }

	
    public PnSpErrorCode(Integer errorCode, String errorName) {
        this.errorCode = errorCode;
        this.errorName = errorName;
    }

    public PnSpErrorCode(Integer errorCode, String errorName, String errorDescription) {
       this.errorCode = errorCode;
       this.errorName = errorName;
       this.errorDescription = errorDescription;
    }
   
     @Id 
    @Column(name="ERROR_CODE", nullable=false)
    public Integer getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    
    @Column(name="ERROR_NAME", nullable=false, length=80)
    public String getErrorName() {
        return this.errorName;
    }
    
    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    
    @Column(name="ERROR_DESCRIPTION", length=2000)
    public String getErrorDescription() {
        return this.errorDescription;
    }
    
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("errorCode", getErrorCode())
            .toString();
    }

}
