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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnDocHandler generated by hbm2java
 */
@Entity
@Table(name="PN_DOC_HANDLER"
)
public class PnDocHandler  implements java.io.Serializable {

    /** identifier field */
    private Integer docFormatId;

    /** identifier field */
    private String action;

    /** identifier field */
    private String actionHandler;

    /** identifier field */
    private Integer isDefault;

    public PnDocHandler() {
    }

    public PnDocHandler(Integer docFormatId, String action, String actionHandler, Integer isDefault) {
       this.docFormatId = docFormatId;
       this.action = action;
       this.actionHandler = actionHandler;
       this.isDefault = isDefault;
    }
   

    @Id
    @Column(name="DOC_FORMAT_ID", nullable=false, length=20)
    public Integer getDocFormatId() {
        return this.docFormatId;
    }
    
    public void setDocFormatId(Integer docFormatId) {
        this.docFormatId = docFormatId;
    }

    
    @Column(name="ACTION", nullable=false, length=200)
    public String getAction() {
        return this.action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }

    
    @Column(name="ACTION_HANDLER", nullable=false, length=200)
    public String getActionHandler() {
        return this.actionHandler;
    }
    
    public void setActionHandler(String actionHandler) {
        this.actionHandler = actionHandler;
    }

    
    @Column(name="IS_DEFAULT", nullable=false, length=1)
    public Integer getIsDefault() {
        return this.isDefault;
    }
    
    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docFormatId", getDocFormatId())
            .append("action", getAction())
            .append("actionHandler", getActionHandler())
            .append("isDefault", getIsDefault())
            .toString();
    }

}


