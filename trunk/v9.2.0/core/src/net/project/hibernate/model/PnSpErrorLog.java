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
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnSpErrorLog generated by hbm2java
 */
@Entity
@Table(name="PN_SP_ERROR_LOG"
)
public class PnSpErrorLog implements Serializable {


     private Date timestamp;
     private String storedProcName;
     private Integer errorCode;
     private String errorMsg;

    public PnSpErrorLog() {
    }

    public PnSpErrorLog(Date timestamp, String storedProcName, Integer errorCode, String errorMsg) {
       this.timestamp = timestamp;
       this.storedProcName = storedProcName;
       this.errorCode = errorCode;
       this.errorMsg = errorMsg;
    }
   

    @Id
    @Column(name="TIMESTAMP", nullable=false, length=7)
    public Date getTimestamp() {
        return this.timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    
    @Column(name="STORED_PROC_NAME", nullable=false, length=60)
    public String getStoredProcName() {
        return this.storedProcName;
    }
    
    public void setStoredProcName(String storedProcName) {
        this.storedProcName = storedProcName;
    }

    
    @Column(name="ERROR_CODE", nullable=false, length=20)
    public Integer getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    
    @Column(name="ERROR_MSG", nullable=false, length=240)
    public String getErrorMsg() {
        return this.errorMsg;
    }
    
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("timestamp", getTimestamp())
            .append("storedProcName", getStoredProcName())
            .append("errorCode", getErrorCode())
            .append("errorMsg", getErrorMsg())
            .toString();
    }

}
