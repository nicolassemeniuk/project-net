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
 *        Provides system settings irrespective of configuration and across all instances of a web application.
 *     
 */
@Entity
@Table(name="PN_SYSTEM_SETTING"
)
public class PnSystemSetting implements Serializable {


     private String name;
     private String value;

    public PnSystemSetting() {
    }

	
    public PnSystemSetting(String name) {
        this.name = name;
    }
    public PnSystemSetting(String name, String value) {
       this.name = name;
       this.value = value;
    }
   
     @Id 
    @Column(name="NAME", nullable=false)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="VALUE", length=1000)
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("name", getName())
            .toString();
    }

}
