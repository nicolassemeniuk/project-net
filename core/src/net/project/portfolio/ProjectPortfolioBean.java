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

 package net.project.portfolio;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.model.PnBusiness;
import net.project.space.Space;
import net.project.util.HTMLUtils;
import net.project.view.pages.directory.BusinessWrapper;
import net.project.xml.XMLFormatter;


/**
 * A portfolio of projects.  This class provides the grouping of projects into a portfolio.
 */
public class ProjectPortfolioBean extends ProjectPortfolio implements java.io.Serializable {

    /** Contains XML formatting information and utilities specific to this object. **/
    protected XMLFormatter m_formatter;


    /**
     * Creates a new ProjectPortfolioBean.
     */
    public ProjectPortfolioBean() {
        super();
        m_formatter = new XMLFormatter();
    }


    /**
     * Get an HTML option list for a select tag with the project specified by the projectID selected.
     * The value part is a portfolio entry id.  The display part is the display name of
     * the portfolio entry.
     * @param projectID the id of the project to select
     * @return the HTML option list
     */
    public String getHtmlOptionList(String projectID) {
        StringBuffer options = new StringBuffer();

        for (int i = 0; i < this.size(); i++) {
        	IPortfolioEntry portfolioEntry = (IPortfolioEntry) this.get(i);
            if ((projectID != null) && ((IPortfolioEntry) this.get(i)).getID().equals(projectID))
                options.append("<option value=\"" + ((IPortfolioEntry) this.get(i)).getID() + "\" selected>" + HTMLUtils.escape(portfolioEntry.getName()) + "</option>");
            else
                options.append("<option value=\"" + ((IPortfolioEntry) this.get(i)).getID() + "\">" + HTMLUtils.escape(portfolioEntry.getName()) + "</option>");
        }
        return options.toString();
    }
    
    /**
     * return project list.
     * @return
     */
    public List<PnBusiness> getProjectList() {
    	List<PnBusiness> projectList = new ArrayList<PnBusiness>();
        for (Object entry : this) {
        	IPortfolioEntry portfolioEntry = (IPortfolioEntry) entry;
        	projectList.add(new PnBusiness(Integer.parseInt(portfolioEntry.getID()),HTMLUtils.escape(portfolioEntry.getName())));
        }
        return projectList;
    }

    /**
     * Get an HTML option list for a select tag with the specified Project selected.
     * @param selectedProject the space to select in the HTML option list
     * @return the HTML option list of spaces
     * @see #getHtmlOptionList(String)
     */
    public String getHtmlOptionList(Space selectedProject) {
        return getHtmlOptionList(selectedProject.getID());
    }


    /**
     * Gets the presentation of the component.
     * This method will apply the stylesheet to the XML representation of the component and
     * return the resulting text
     *
     * @return presetation of the component
     */
    public String getPresentation() {
        return m_formatter.getPresentation(getXML());
    }


    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation of the component
     * to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        m_formatter.setStylesheet(styleSheetFileName);
    }


}




