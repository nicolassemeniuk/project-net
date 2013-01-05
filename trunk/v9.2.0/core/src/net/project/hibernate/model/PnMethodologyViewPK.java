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

public class PnMethodologyViewPK implements Serializable {
	
    /** identifier field */
    private Integer methodologyId;

    /** identifier field */
    private Integer parentSpaceId;

    /** identifier field */
    private Integer childSpaceId;
    
    public PnMethodologyViewPK(Integer methodologyId, Integer parentSpaceId, Integer childSpaceId) {
        this.methodologyId = methodologyId;
        this.parentSpaceId = parentSpaceId;
        this.childSpaceId = childSpaceId;
    }
    
    /** default constructor */
    public PnMethodologyViewPK() {
    }
    
    public Integer getMethodologyId() {
        return this.methodologyId;
    }

    public void setMethodologyId(Integer methodologyId) {
        this.methodologyId = methodologyId;
    }

    public Integer getParentSpaceId() {
        return this.parentSpaceId;
    }

    public void setParentSpaceId(Integer parentSpaceId) {
        this.parentSpaceId = parentSpaceId;
    }

    public Integer getChildSpaceId() {
        return this.childSpaceId;
    }

    public void setChildSpaceId(Integer childSpaceId) {
        this.childSpaceId = childSpaceId;
    }

}
