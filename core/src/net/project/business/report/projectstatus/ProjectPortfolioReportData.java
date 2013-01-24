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
 |       $Date: 2007-01-18 14:25:36 +0530 (Thu, 18 Jan 2007) $
 |     $Author: sjmittal $
 |
 +----------------------------------------------------------------------*/
package net.project.business.report.projectstatus;

import java.sql.SQLException;
import java.util.Collection;

import net.project.base.PnetRuntimeException;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceFinder;
import net.project.resource.filters.assignments.SpaceFilter;
import net.project.space.ISpaceTypes;

/**
 * Controller object which coordinates querying the database to find the data
 * needed to produce the report.
 * 
 * @author Sachin Mittal
 * @since Version 1.0
 */
public class ProjectPortfolioReportData extends ProjectStatusReportData {
	
    private static final String WORKSPACE_NAME_FILTER = PropertyProvider.get("prm.enterprise.report.projectportfolioreport.workspacesfilter.name");
    
    /**
     * Token pointing to: "Unexpected programmer error found while constructing
     * the list of report filters."
     */
    private String FILTER_LIST_CONSTRUCTION_ERROR = "prm.report.errors.filterlistcreationerror.message";
	
	/**
	 * Standard constructor.
	 */
	public ProjectPortfolioReportData() {
		super();
	}

	/**
	 * Populate the list of filters with all filters that are available for this
	 * report.
	 */
	protected void populateFinderFilterList() {
		try {
            SpaceFilter spaceFilter = new SpaceFilter("10", WORKSPACE_NAME_FILTER, null, false);
            try {
                spaceFilter.loadSpaces(new String[] { ISpaceTypes.BUSINESS_SPACE});
            } catch (PersistenceException e) {
                throw new PnetRuntimeException(e);
            }
            
            spaceFilter.setSelected(true);
			filterList.add(spaceFilter);
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
        	
        	Collection selectSpaces = ((SpaceFilter) filterList.get("10")).getSelectedSpaces();
        	String firstSelectedBusinessSpaceId = getSpaceID();
        	if(selectSpaces != null && selectSpaces.iterator().hasNext())
        		firstSelectedBusinessSpaceId = (String) selectSpaces.iterator().next();
        	
        	psf.addFinderSorterList(sorterList);
        	
        	// Load the data required to create the details section
        	detailedData = psf.findByBusinessID(firstSelectedBusinessSpaceId);
        	
            isLoaded = true;
        }
	}
	
}
