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
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * PnResourceListHasPersonsPK generated by hbm2java
 */
@Embeddable
public class PnResourceListHasPersonsPK implements Serializable {


     private Integer resourceListId;
     private Integer personId;

    public PnResourceListHasPersonsPK() {
    }

    public PnResourceListHasPersonsPK(Integer resourceListId, Integer personId) {
       this.resourceListId = resourceListId;
       this.personId = personId;
    }
   


    @Column(name="RESOURCE_LIST_ID", nullable=false, length=20)
    public Integer getResourceListId() {
        return this.resourceListId;
    }
    
    public void setResourceListId(Integer resourceListId) {
        this.resourceListId = resourceListId;
    }


    @Column(name="PERSON_ID", nullable=false, length=20)
    public Integer getPersonId() {
        return this.personId;
    }
    
    public void setPersonId(Integer personId) {
        this.personId = personId;
    }




}


