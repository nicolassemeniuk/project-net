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
import java.text.NumberFormat;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.project.ProjectSpace;

public class ProjectSpaceWrapper implements Serializable {

    private ProjectSpace projectSpace;

    public ProjectSpaceWrapper() {

    }

    public ProjectSpaceWrapper(ProjectSpace projectSpace) {
        this.projectSpace = projectSpace;
    }

    public String getName() {
    	 return projectSpace.getName();
    }

    public boolean getImprovementCode() {
        return projectSpace.getImprovementCode() != null;
    }

    public boolean getFinancialStatusCode() {
        return projectSpace.getFinancialStatusColorCode() != null;
    }

    public boolean getScheduleStatusCode() {
        return projectSpace.getScheduleStatusColorCode() != null;
    }

    public boolean getResourceStatusCode() {
        return projectSpace.getResourceStatusColorCode() != null;
    }
    
    public String getImprovementCodeUrl() {
        return projectSpace.getImprovementCode().getImageURL(
                projectSpace.getColorCode());
    }
    
    public String getFinancialStatusCodeUrl() {
        return projectSpace.getFinancialStatusImprovementCode().getImageURL(
                projectSpace.getFinancialStatusColorCode());
    }
    
    public String getScheduleStatusCodeUrl() {
        return projectSpace.getScheduleStatusImprovementCode().getImageURL(
                projectSpace.getScheduleStatusColorCode());
    }
    
    public String getResourceStatusCodeUrl() {
        return projectSpace.getResourceStatusImprovementCode().getImageURL(
                projectSpace.getResourceStatusColorCode());
    }
    
    public String getPercentComplete() {
        String p = projectSpace.getPercentComplete();
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        String percent = nf.format(Double.parseDouble(p));
        return percent;
    }
    
    public String getPercentCompleteWidth() {
        String p = projectSpace.getPercentComplete();
        if(p.equals("") || p.equals("1")) {
            return "1";
        } else {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);
            String percent = nf.format(Double.parseDouble(p));
            return percent; 
        }
    }
    
    public String getUrl() {
        return "/project/Dashboard?id=" + this.projectSpace.getID() + "&module=" + Module.PROJECT_SPACE;
    }

	public ProjectSpace getProjectSpace() {
		return projectSpace;
	}
	
	/**
	 * Method returns the Improvement Title 
	 * @return String
	 */
	public String getImprovementTitle() {
		return PropertyProvider.get(projectSpace.getImprovementCode().getNameToken())+ " / "
				+ PropertyProvider.get(projectSpace.getColorCode().getNameToken());
	}

	/**
	 * Method returns the Finanacial Status Title
	 * @return String
	 */
	public String getFinancialStatusTitle() {
		return PropertyProvider.get(projectSpace.getFinancialStatusImprovementCode().getNameToken())+ " / "
				+ PropertyProvider.get(projectSpace.getFinancialStatusColorCode().getNameToken());
	}

	/**
	 * Method returns Schedule Status Title
	 * @return String
	 */
	public String getScheduleStatusTitle() {
		return PropertyProvider.get(projectSpace.getScheduleStatusImprovementCode().getNameToken())+ " / "
				+ PropertyProvider.get(projectSpace.getScheduleStatusColorCode().getNameToken());
	}

	/**
	 * Method returns Resource Status Title 
	 * @return String
	 */
	public String getResourceStatusTitle() {
		return PropertyProvider.get(projectSpace.getResourceStatusImprovementCode().getNameToken())+ " / "
				+ PropertyProvider.get(projectSpace.getResourceStatusColorCode().getNameToken());
	}

}
