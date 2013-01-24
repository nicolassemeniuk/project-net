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
package net.project.hibernate.service.impl.utilities;


import java.sql.Clob;

public class CreateDiscussionGroupClass {
    private Integer discussionGroupId;
    private Clob welcomeMessagePostClob;
    private Clob discussionGrpCharterClob;

    public Integer getDiscussionGroupId() {
        return discussionGroupId;
    }

    public void setDiscussionGroupId(Integer discussionGroupId) {
        this.discussionGroupId = discussionGroupId;
    }

    public Clob getWelcomeMessagePostClob() {
        return welcomeMessagePostClob;
    }

    public void setWelcomeMessagePostClob(Clob welcomeMessagePostClob) {
        this.welcomeMessagePostClob = welcomeMessagePostClob;
    }

    public Clob getDiscussionGrpCharterClob() {
        return discussionGrpCharterClob;
    }

    public void setDiscussionGrpCharterClob(Clob discussionGrpCharterClob) {
        this.discussionGrpCharterClob = discussionGrpCharterClob;
    }
}
