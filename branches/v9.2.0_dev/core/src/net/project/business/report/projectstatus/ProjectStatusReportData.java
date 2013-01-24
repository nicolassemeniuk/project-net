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

/*----------------------------------------------------------------------+
 |
 |    $RCSfile$
 |   $Revision: 15475 $
 |       $Date: 2006-12-10 14:25:36 +0530 (Sun, 10 Dec 2006) $
 |     $Author: sjmittal $
 |
 +----------------------------------------------------------------------*/
package net.project.business.report.projectstatus;

import java.sql.SQLException;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.EmptyFinderFilter;
import net.project.base.finder.FinderGrouping;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.RadioButtonFilter;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceFinder;
import net.project.report.SummaryDetailReportData;

/**
 * Controller object which coordinates querying the database to find the data
 * needed to produce the report.
 * 
 * @author Sachin Mittal
 * @since Version 1.0
 */
public class ProjectStatusReportData extends SummaryDetailReportData {

	/** Token for the label of the "Default Grouping" grouper. */
    private static final String DEFAULT_GROUPING = PropertyProvider.get("prm.business.report.projectstatusreport.grouping.name");
    
    /** Token for the label of the "Root business". */
    private static final String ROOT_BUSINESS = PropertyProvider.get("prm.business.report.projectstatusreport.grouping.noparent.name"); 

    /**
     * Token pointing to: "Unexpected programmer error found while constructing
     * the list of report filters."
     */
    private String FILTER_LIST_CONSTRUCTION_ERROR = "prm.report.errors.filterlistcreationerror.message";


	/**
	 * Standard constructor.
	 */
	public ProjectStatusReportData() {
		populateFinderFilterList();
		populateFinderSorterList();
		populateFinderGroupingList();
	}


	/**
     * Create the list of <code>FinderGrouping</code> classes that will be used
     * on the HTML page to allow the user to select sub-business grouping.
	 */
	protected void populateFinderGroupingList() {
		
		FinderGrouping groupByParent = new FinderGrouping("10", DEFAULT_GROUPING, true) {
			public Object getGroupingValue(Object currentObject) throws PersistenceException {
				if(currentObject == null)
					return null;
				
				String toReturn = ((ProjectWorkplanData) currentObject).projectSpace.getParentProjectID();
				
				if(toReturn == null || toReturn.trim().equals(""))
					toReturn = ((ProjectWorkplanData) currentObject).projectSpace.getParentBusinessID();
				
				return toReturn;
			}
			
			public String getGroupName(Object currentObject) throws PersistenceException {
				ProjectWorkplanData currentProject = (ProjectWorkplanData)currentObject;
				if(currentObject == null)
					return ROOT_BUSINESS;
				String toReturn;
				
				//get the parent business or project
				//which ever is applicable
				//first check for parent project
				toReturn = currentProject.projectSpace.getParentProjectName();
				if(toReturn == null || toReturn.trim().equals("")) {
					//then check for the perent business
					toReturn = currentProject.projectSpace.getParentBusinessName();
				}
				
				if(toReturn == null || toReturn.trim().equals(""))
					return ROOT_BUSINESS;
				
				return toReturn;
			}
		};
    
	
		groupingList.add(groupByParent);
	}

	/**
	 * Populate the list of sorters with all sorters that this report supports.
	 */
	protected void populateFinderSorterList() {

		FinderSorter fs = new FinderSorter(String.valueOf(10),
				new ColumnDefinition[] { ProjectSpaceFinder.SPONSER_COLUMN,
						ProjectSpaceFinder.NAME_COLUMN,
						ProjectSpaceFinder.ID_COLUMN,
						ProjectSpaceFinder.DATE_START_COLUMN,
						ProjectSpaceFinder.DATE_FINISH_COLUMN,
						ProjectSpaceFinder.STATUS_COLUMN,
						ProjectSpaceFinder.COLOR_COLUMN,
						ProjectSpaceFinder.IMPROVEMENT_COLUMN,
						ProjectSpaceFinder.DESCRIPTION_COLUMN },
				ProjectSpaceFinder.NAME_COLUMN);
		sorterList.add(fs);
	}

	/**
	 * Populate the list of filters with all filters that are available for this
	 * report.
	 */
	protected void populateFinderFilterList() {
		try {
			RadioButtonFilter rbf = new RadioButtonFilter("10");
            EmptyFinderFilter eff = new EmptyFinderFilter("20", "prm.business.report.projectstatusreport.showallprojetcs.name");
			eff.setSelected(true);
			
			rbf.add(eff);
			filterList.add(rbf);
		} catch (DuplicateFilterIDException e) {
            throw new RuntimeException(
                PropertyProvider.get(FILTER_LIST_CONSTRUCTION_ERROR), e);
		}
	}

	/**
	 * Populate this report data object with data from the database.
	 *
	 * @throws PersistenceException if there is a difficulty loading the data from the database.
	 */
	public void load() throws PersistenceException, SQLException {
        if (!isLoaded) {
        	ProjectSpaceFinder psf = new ProjectSpaceFinder();
        	psf.addFinderFilterList(filterList);
        	psf.addFinderSorterList(sorterList);
        	
        	// Load the data required to create the details section
        	detailedData = psf.findByBusinessID(getSpaceID());
        	
            isLoaded = true;
        }
	}

	/**
	 * Clear out any data stored in this object and reset.
	 */
	public void clear() {
        isLoaded = false;
        detailedData = null;
	}

}
