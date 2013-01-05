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
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * PnSpaceHasSkillPK generated by hbm2java
 */
@Embeddable
public class PnSpaceHasSkillPK implements Serializable {


     private Integer spaceId;
     private Integer skillId;

    public PnSpaceHasSkillPK() {
    }

    public PnSpaceHasSkillPK(Integer spaceId, Integer skillId) {
       this.spaceId = spaceId;
       this.skillId = skillId;
    }
   


    @Column(name="SPACE_ID", nullable=false, length=20)
    public Integer getSpaceId() {
        return this.spaceId;
    }
    
    public void setSpaceId(Integer spaceId) {
        this.spaceId = spaceId;
    }


    @Column(name="SKILL_ID", nullable=false, length=20)
    public Integer getSkillId() {
        return this.skillId;
    }
    
    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("skillId", getSkillId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasSkillPK) ) return false;
        PnSpaceHasSkillPK castOther = (PnSpaceHasSkillPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getSkillId(), castOther.getSkillId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getSkillId())
            .toHashCode();
    }

}
