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
package net.project.view.pages.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.model.project_space.ProjectPhase;
import net.project.security.Action;
import net.project.security.SessionManager;

public class ProjectPhaseWrapper implements Serializable {

    private ProjectPhase phase;

    public ProjectPhaseWrapper() {

    }

    public ProjectPhaseWrapper(ProjectPhase phase) {
        this.phase = phase;
        List<PnTask> milestonesAux = phase.getMilestones();
    }

    public ProjectPhase getPhase() {
        return phase;
    }

    public void setPhase(ProjectPhase phase) {
        this.phase = phase;
    }

    public String getUrl() {
        return "/process/ViewPhase.jsp?id=" + phase.getPhaseId() + "&module="
                + Module.PROCESS + "&action=" + Action.VIEW;
    }

    public String getName() {
        return phase.getPhaseName();
    }

    public String getEndDate() {
        String endDate = "";
        if (phase.getEndDate() != null) {
            endDate = SessionManager.getUser().getDateFormatter().formatDateMedium(
                    phase.getEndDate());
        }
        return endDate;
    }

    public String getStatus() {
        return PropertyProvider.get(phase.getStatusCode());
    }

    public String getPercentComplete() {
        return phase.getPercentComplete().toString();
    }
}
