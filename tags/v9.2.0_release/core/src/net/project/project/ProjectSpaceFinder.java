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

 package net.project.project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.Finder;
import net.project.base.finder.FinderSorter;
import net.project.base.money.InvalidCurrencyException;
import net.project.base.money.InvalidValueException;
import net.project.base.money.Money;
import net.project.base.property.PropertyProvider;
import net.project.business.report.projectstatus.ExportProjectWorkplanData;
import net.project.business.report.projectstatus.ProjectWorkplanData;
import net.project.code.ColorCode;
import net.project.code.ImprovementCode;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.portfolio.view.PersonalPortfolioColumnDefinition;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.WorkplanExporter;
import net.project.security.SessionManager;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * Provides methods for loading ProjectSpace objects from persistence store.
 */
public class ProjectSpaceFinder extends Finder {

    public static final ColumnDefinition ID_COLUMN = new ColumnDefinition("p.project_id", "prm.project.columndefs.id");
    public static final ColumnDefinition NAME_COLUMN = new ColumnDefinition("p.project_name", "prm.project.columndefs.name");
    public static final ColumnDefinition DESCRIPTION_COLUMN = new ColumnDefinition("P.project_desc", "prm.project.columndefs.description");
    public static final ColumnDefinition SPONSER_COLUMN = new ColumnDefinition("p.sponsor_desc", "prm.project.columndefs.sponsor");
    public static final ColumnDefinition DATE_START_COLUMN = new ColumnDefinition("P.start_date", "prm.project.columndefs.datestart");
    public static final ColumnDefinition DATE_FINISH_COLUMN = new ColumnDefinition("P.end_date", "prm.project.columndefs.datefinish");
    public static final ColumnDefinition STATUS_COLUMN = new ColumnDefinition("p.status_code_id", "prm.project.columndefs.status");
    public static final ColumnDefinition COLOR_COLUMN = new ColumnDefinition("p.color_code_id", "prm.project.columndefs.color");
    public static final ColumnDefinition IMPROVEMENT_COLUMN = new ColumnDefinition("p.improvement_code_id", "prm.project.columndefs.improvement");
    
    //
    // Static members
    //

    /**
     * The Base ProjectSpace query that is used for all find operations.
     */
    private static final String PROJECT_SPACE_BASE_QUERY =
        "SELECT "+
        "    DISTINCT p.project_id, p.project_name, p.project_desc, p.percent_complete, "+
        "    p.status_code_id, p.status, p.color_code_id, p.color, p.color_image_url, "+
        "    p.is_subproject, p.record_status, p.start_date, p.end_date, "+
        "    p.project_logo_id, p.default_currency_code, p.percent_calculation_method, "+
        "    p.sponsor_desc, p.improvement_code_id, p.current_status_description, "+
        "    p.financial_status_color_code_id, p.financial_status_imp_code_id, "+
        "    p.budgeted_total_cost_value, p.budgeted_total_cost_cc, "+
        "    p.current_est_total_cost_value, p.current_est_total_cost_cc, "+
        "    p.actual_to_date_cost_value, p.actual_to_date_cost_cc, "+
        "    p.estimated_roi_cost_value, p.estimated_roi_cost_cc, p.cost_center, "+
        "    p.schedule_status_color_code_id, p.schedule_status_imp_code_id, "+
        "    p.resource_status_color_code_id, p.resource_status_imp_code_id, "+
        "    p.priority_code_id, p.risk_rating_code_id, p.visibility_id, shp.plan_id as schedule_plan_id, "+
        "    m.methodology_name, "+
        "    b.business_id AS parent_business_id, "+
        "    b.business_name AS parent_business_name, "+
        "    superProject.project_id As parent_project_id, "+
        "    superProject.project_name As parent_project_name "+
        "FROM "+
        "  pn_space_has_space ss, "+
        "  pn_space_has_space superspace, "+   
        "  pn_project_view p, "+
        "  pn_project_view superProject, "+
        "  pn_space_has_plan shp, "+
        "  pn_business_space bs, "+
        "  pn_business b, "+
        "  pn_methodology_view m, "+
        "  pn_space_has_methodology shm "+
        "WHERE "+
        "  ss.child_space_id(+) = p.project_id "+
        "  and superspace.child_space_id(+) = p.project_id "+
        "  and bs.business_space_id(+) = ss.parent_space_id "+
        "  and superProject.project_id(+) = superspace.parent_space_id "+
        "  and shp.space_id = p.project_id "+
        "  and b.business_id(+) = bs.business_id "+
        "  and ss.relationship_child_to_parent(+) = 'owned_by' "+
        "  and superspace.relationship_child_to_parent(+) = 'subspace' "+
    	"  and shm.space_id(+) = p.project_id " +
    	"  and m.methodology_id(+) = shm.methodology_id ";

    //
    // Instance members
    //

    /**
     * Creates an empty ProjectSpaceFinder.
     */
    public ProjectSpaceFinder() {
        // Do nothing
    }

    //
    // Implementing Finder
    //

    /**
     * Returns the base SQL statement used for all find operations.
     * Includes <code>SELECT</code>, <code>FROM</code> and <code>WHERE</code>
     * sections.  The Where part contains clauses necessary for joining the
     * base tables.
     * @return the base SQL statement, including <code>WHERE</code> clause
     */
    protected String getBaseSQLStatement() {
        return PROJECT_SPACE_BASE_QUERY;
    }

    /**
     * Returns a populated <code>ProjectSpace</code> object from the current
     * row in the specified result set.
     * @param databaseResults the result set from which to populate the object;
     * assumes this points to a current row
     * @return the populated ProjectSpace object
     * @throws SQLException if there is a problem populating the object
     */
    protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException {
        ProjectSpace projectSpace = new ProjectSpace();
        populate(databaseResults, projectSpace);
        return projectSpace;
    }

    // End Finder

    /**
     * Finds ProjectSpaces matching the current filter.
     * @return a List where each element is a <code>ProjectSpace</code>
     * @throws PersistenceException if there is a problem loading the ProjectSpaces
     */
    public final List find() throws PersistenceException {
        return loadFromDB();
    }

    /**
     * Finds <code>ProjectSpace</code>s matching the current filters
     * limiting them to projects in the specified portfolio id.
     * @return a List where each element is a <code>ProjectSpace</code>
     * @throws PersistenceException if there is a problem loading the ProjectSpaces
     * @throws NullPointerException if the portfolioID is null
     */
    public final List findByPortfolioID(String portfolioID) throws PersistenceException {

        if (portfolioID == null) {
            throw new NullPointerException("portfolioID is required");
        }

        // Add additional where clause to limit ProjectSpaces to those
        // for the specified portfolioID
        addWhereClause("p.project_id in (select pv.project_id from pn_portfolio_view pv where pv.portfolio_id = " + portfolioID + ")");
        addOrderByClause(" upper(p.project_name)");
        return loadFromDB();
    }

	/**
	 * Finds <code>ProjectSpace</code>s matching the current filters limiting
	 * them to projects in the specified portfolio id
	 * @param projectMembersOnly, true if the projects with visibility project members only otherwise false. 
	 * @return a List where each element is a <code>ProjectSpace</code>
	 * @throws PersistenceException
	 *             if there is a problem loading the ProjectSpaces
	 * @throws NullPointerException
	 *             if the portfolioID is null
	 */
	public final List findByPortfolioID(String portfolioID, boolean projectMembersOnly) throws PersistenceException {

		if (portfolioID == null) {
			throw new NullPointerException("portfolioID is required");
		}
		
		if(!projectMembersOnly){
			return findByPortfolioID(portfolioID);
		}

		// Add additional where clause to limit ProjectSpaces to those
		// for the specified portfolioID
		addWhereClause(" p.project_id in (" + " select pv.project_id "
					+ " from pn_portfolio_view pv ,pn_project_space shpr "
					+ " where pv.project_id(+) = shpr.project_id and shpr.visibility_id <> 100 "
					+ " and pv.portfolio_id = " + portfolioID + ") ");

		addOrderByClause(" upper(p.project_name)");

		return loadFromDB();
	}

    /**
     * Finds all ProjectSpaces for the specified portfolioID, filtering out any projects dependent on the specified projectID filter.
     * This will create a list of non-dependent projects.
     * @param portfolioID the ID of the portfolio to load
     * @param projectIDFilter the id of the project to filter decendents from
     * @return a List where each element is a <code>ProjectSpace</code>
     * @throws PersistenceException if there is a problem loading the ProjectSpaces
     * @throws NullPointerException if the portfolioID is null
     */
    public final List findNonDependentsByPorfolioID (String portfolioID, String projectIDFilter) throws PersistenceException {

        if (portfolioID == null) {
            throw new NullPointerException("portfolioID is required");
        }

       String cycleExclusionFilterSQL = "(select child_space_id from pn_space_has_space " +
                  "where relationship_parent_to_child = 'superspace' " +
                "start with parent_space_id = " + projectIDFilter +
                " connect by prior child_space_id = parent_space_id)";

        addWhereClause("p.project_id in (select pv.project_id from pn_portfolio_view pv where pv.portfolio_id = " + portfolioID + ")");
        addWhereClause("p.project_id not in " + cycleExclusionFilterSQL);
        addWhereClause("p.project_id != " + projectIDFilter);

        return loadFromDB();
    }
    
    /**
     * Finds <code>ProjectSpace</code>s for the specified ID.
     * @param businessSpaceID the id of the business space to find
     * @return a List where each element is a <code>ProjectSpace</code>.
     * @throws PersistenceException if there is a problem loading the ProjectSpace
     * @throws NullPointerException if the specified businessSpaceID is null
     */
    public final List<ExportProjectWorkplanData> loadByBusinessID(String businessSpaceID) throws PersistenceException {

        if (businessSpaceID == null) {
            throw new NullPointerException("projectSpaceID cannot be null");
        }

        List<ExportProjectWorkplanData> workplanProjectList = new ArrayList<ExportProjectWorkplanData>();
        DBBean db = new DBBean();

        try {
            // Execute the select statement
        	ResultSet result = null;

    		//Find the total projects under selected business.
    		result = fetchMinimalResultSetByBusinessID(db, businessSpaceID);
    		
    		ExportProjectWorkplanData exportProjectWorkplanData = null;
            while (result.next()) {
            	exportProjectWorkplanData = new ExportProjectWorkplanData();
                // Populate a ProjectSpace
                ProjectSpace projectSpace = new ProjectSpace();

                projectSpace.setID(result.getString("project_id"));
                projectSpace.setName(result.getString("project_name"));
                projectSpace.setDescription(result.getString("project_desc"));
                projectSpace.setSponsor(result.getString("sponsor_desc"));
                projectSpace.setPriorityCode(PriorityCode.findByID(result.getString("priority_code_id")));
                projectSpace.setCostCenter(result.getString("cost_center"));
                projectSpace.setStatus(PropertyProvider.get(result.getString("status")));
                projectSpace.setParentBusinessID(result.getString("parent_space_id"));
                projectSpace.setParentBusinessName(result.getString("name"));
                if(result.getBoolean("is_subproject")){
                	projectSpace.setParentBusinessName(getParentBusinessNameOfChildProject(projectSpace.getParentBusinessID()));
                }
                
                // load entries for specified project id
                List<ScheduleEntry> scheduleEntries = WorkplanExporter.findScheduleEntriesBySpaceID(result.getString("project_id"));
                
                exportProjectWorkplanData.projectSpace = projectSpace;
                exportProjectWorkplanData.scheduleEntriesList = scheduleEntries;
                
                projectSpace = null;
                scheduleEntries = null;
                workplanProjectList.add(exportProjectWorkplanData);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ProjectSpaceFinder.class).error("Error loading project space by id: " + sqle);
            throw new PersistenceException("ProjectSpace find by id operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return workplanProjectList;
    }
    
    /**
     * Finds <code>ProjectSpace</code>s for the specified ID.
     * 
     * @param businessSpaceID business space id 
     * @return list of project space and schedule objects
     * @throws PersistenceException
     */
    public final List<ProjectWorkplanData> findByBusinessID(String businessSpaceID) throws PersistenceException {
		 if (businessSpaceID == null) {
	         throw new NullPointerException("projectSpaceID cannot be null");
	     }
    	
		 List<ProjectWorkplanData> projectSpaceList = new ArrayList<ProjectWorkplanData>();
	     DBBean db = new DBBean();
	     
	     try {
	            // Execute the select statement
	        	ResultSet result = null;
        		result = fetchResultSetByBusinessID(db, businessSpaceID);

        		while (result.next()) {
	                // Populate a ProjectSpace
	            	Schedule schedule = new Schedule();
	            	ProjectSpace projectSpace = new ProjectSpace();
	            
            		// Populate project space information
                populate(db.result, projectSpace);
                schedule.setSpace(projectSpace);
	                // Load schedule
                schedule.loadAll();
	                
                //add these to the hybrid object
                ProjectWorkplanData projectWorkplanData = new ProjectWorkplanData();
                projectWorkplanData.schedule = schedule;
                projectWorkplanData.projectSpace = projectSpace;
                
	                schedule = null;
	                projectSpace = null;

                projectSpaceList.add(projectWorkplanData);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ProjectSpaceFinder.class).error("Error loading project space by id: " + sqle);
            throw new PersistenceException("ProjectSpace find by id operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }
        return projectSpaceList;
    }

    /**
     * Finds <code>ProjectSpace</code>s for the specified ID.
     * @param projectSpaceID the id of the project space to find
     * @return a List where each element is a <code>ProjectSpace</code>.
     * @throws PersistenceException if there is a problem loading the ProjectSpace
     * @throws NullPointerException if the specified projectSpaceID is null
     */
    public final List findByID(String projectSpaceID) throws PersistenceException {

        if (projectSpaceID == null) {
            throw new NullPointerException("projectSpaceID cannot be null");
        }

        List projectSpaceList = new ArrayList();
        DBBean db = new DBBean();

        try {
            // Execute the select statement
            ResultSet result = fetchResultSetByID(db, projectSpaceID);

            while (result.next()) {
                // Populate a ProjectSpace
                ProjectSpace projectSpace = new ProjectSpace();
                populate(db.result, projectSpace);
                projectSpaceList.add(projectSpace);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ProjectSpaceFinder.class).error("Error loading project space by id: " + sqle);
            throw new PersistenceException("ProjectSpace find by id operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return projectSpaceList;
    }


    /**
     * Finds and loads a project space for the specified id.
     * Populates the specified project space.
     * @param projectSpaceID the id of the project space to load
     * @param projectSpace the project space to populate
     * @return true if the project space was found; false otherwise
     * @throws PersistenceException if there is a problem loading the ProjectSpace
     * @throws NullPointerException if the specified projectSpaceID or
     * projectSpace is null
     */
    final boolean findByID(String projectSpaceID, ProjectSpace projectSpace) throws PersistenceException {

        if (projectSpaceID == null || projectSpace == null) {
            throw new NullPointerException("projectSpaceID or projectSpace cannot be null");
        }

        boolean isFound = false;
        DBBean db = new DBBean();

        try {

            // Execute the select statement
            ResultSet result = fetchResultSetByID(db, projectSpaceID);

            if (result.next()) {
                // We found a row, so populate ProjectSpace
                populate(result, projectSpace);
                isFound = true;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ProjectSpaceFinder.class).error("Error loading project space by id: " + sqle);
            throw new PersistenceException("ProjectSpace find by id operation failed: " + sqle, sqle);

        } finally {
            db.release();
        }

        return isFound;
    }

    /**
     * Finds <code>ProjectSpace</code>s for the specified collection of IDs
     * irrespective of record status.
     * @param projectSpaceIDCollection the colllection of <code>String</code>
     * ids of the project spaces to find
     * @return a List where each element is a <code>ProjectSpace</code>.
     * The number of entries in the list will match the number of entries in
     * the specified collection, assuming each ID in the collection represents
     * a project ID.  If any IDs are not found, no exception is thrown; rather,
     * the returned list will have fewer entries.
     * @throws PersistenceException if there is a problem loading the ProjectSpaces
     * @throws NullPointerException if the specified projectSpaceIDCollection is null
     * or empty
     */
    public final List findByIDs(Collection projectSpaceIDCollection) throws PersistenceException {
        return findByIDs(projectSpaceIDCollection, null);
    }

    /**
     * Finds <code>ProjectSpace</code>s for the specified collection of IDs for
     * the specified recordStatus.
     * @param projectSpaceIDCollection the colllection of <code>String</code>
     * ids of the project spaces to find
     * @param recordStatus the record status of projects to find; if null,
     * no filter on record status will be applied
     * @return a List where each element is a <code>ProjectSpace</code>.
     * The number of entries in the list will match the number of entries in
     * the specified collection, assuming each ID in the collection represents
     * a project ID and has a matching record status.  If any IDs are not found,
     * no exception is thrown; rather, the returned list will have fewer entries.
     * @throws PersistenceException if there is a problem loading the ProjectSpaces
     * @throws NullPointerException if the specified projectSpaceIDCollection is null
     * or empty
     */
    public final List findByIDs(Collection projectSpaceIDCollection, String recordStatus) throws PersistenceException {

        if (projectSpaceIDCollection == null || projectSpaceIDCollection.isEmpty()) {
            throw new NullPointerException("projectSpaceIDCollection cannot be null or empty");
        }

        // Construct the query
        StringBuffer query = new StringBuffer();
        query.append(getBaseSQLStatement());

        if (recordStatus != null) {
            query.append("and p.record_status = '").append(recordStatus).append("' ");
        }

        query.append("and p.project_id in ( ");
        int counter = 1;
        for (Iterator it = projectSpaceIDCollection.iterator(); it.hasNext(); counter++) {
            query.append("'").append(it.next()).append("' ");
            if (counter < projectSpaceIDCollection.size()) {
                query.append(", ");
            }
        }
        query.append(") ");
        query.append("order by upper(p.project_name) asc");

        List projectSpaceList = new ArrayList();
        DBBean db = new DBBean();

        try {

            // Execute the select statement
            db.prepareStatement(query.toString());
            db.executePrepared();

            while (db.result.next()) {
                projectSpaceList.add(createObjectForResultSetRow(db.result));
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ProjectSpaceFinder.class).error("Error loading project space by id: " + sqle);
            throw new PersistenceException("ProjectSpace find by id operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

            return projectSpaceList;
    }

    /**
     * Finds all <code>ProjectSpace</code>s in the database.
     * @param recordStatus the record status of ProjectSpaces to find
     * @return a list where each element is a <code>ProjectSpace</code>; the
     * list is currently orderd by name, ordering performed by database
     * @throws PersistenceException if there is a problem loading the ProjectSpaces
     * @throws NullPointerException if the record status is null
     */
    public final List findAllForRecordStatus(String recordStatus) throws PersistenceException {

        if (recordStatus == null) {
            throw new NullPointerException("Record status is required");
        }

        addWhereClause("p.record_status = '" + recordStatus + "' ");
        addFinderSorter(new FinderSorter(PersonalPortfolioColumnDefinition.NAME, false));
        return loadFromDB();
    }

    /**
     * Finds all <code>ProjectSpace</code>s in the database based on part
     * of a project name.
     * @param recordStatus the record status of ProjectSpaces to find
     * @param nameFilter part of the project name; A case insensitive
     * search is performed.
     * @return a list where each element is a <code>ProjectSpace</code>; the
     * list is currently orderd by name, ordering performed by database
     * @throws PersistenceException if there is a problem loading the ProjectSpaces
     * @throws NullPointerException if the record status is null
     */
    public final List findAllByName(String recordStatus, String nameFilter) throws PersistenceException {

        if (recordStatus == null || nameFilter == null) {
            throw new NullPointerException("Record status and filter are required");
        }

        // Construct the query
        StringBuffer query = new StringBuffer();
        query.append(getBaseSQLStatement());
        query.append("and p.record_status = ? ");
        query.append("and upper(p.project_name) like UPPER(?) ");
        query.append("order by p.project_name asc ");

        List projectSpaceList = new ArrayList();
        DBBean db = new DBBean();

        String filter = nameFilter;

        if (filter != null) {
            if (filter.length() <= 1) {
                filter = filter + "%";
            } else {
                filter = "%" + filter + "%" ;
            }

        }

        try {
            db.prepareStatement(query.toString());
            int index = 0;
            db.pstmt.setString(++index, recordStatus);
            db.pstmt.setString(++index, filter);
            db.executePrepared();

            while (db.result.next()) {
                projectSpaceList.add(createObjectForResultSetRow(db.result));
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ProjectSpaceFinder.class).error("Error loading all project spaces: " + sqle);
            throw new PersistenceException("ProjectSpace find all for record status operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return projectSpaceList;
    }
    
    /**
     * Executes a select statement to find ProjectSpace for the specified id.
     * @param db the DBBean in which to perform the transaction
     * @param businessSpaceID the id of the business space to load prjects from
     * @return the result set
     * @throws SQLException if there is a problem accessign the database
     */
    private ResultSet fetchResultSetByBusinessID(DBBean db, String businessSpaceID) throws SQLException {

        StringBuffer query = new StringBuffer();
        query.append(getBaseSQLStatement());
        //this gets all the projects and sub projects under a root business and its sub businesses
        query.append("and p.project_id in (");
        query.append("select child_space_id from pn_space_has_space where ");
        query.append("record_status = 'A' and child_space_type = 'project' ");
        query.append("start with parent_space_id = ? or parent_space_id in (");
        query.append("select child_space_id from pn_space_has_space where ");
        query.append("record_status = 'A' and child_space_type = 'business' ");
        query.append("start with parent_space_id = ? connect by parent_space_id = prior child_space_id) ");
        query.append("connect by parent_space_id = prior child_space_id)");

        int index = 0;
        db.prepareStatement(query.toString());
        db.pstmt.setString(++index, businessSpaceID);
        db.pstmt.setString(++index, businessSpaceID);
        db.executePrepared();

        return db.result;
    }


    /**
     * Executes a select statement to find ProjectSpace for the specified id.
     * @param db the DBBean in which to perform the transaction
     * @param projectSpaceID the id of the project space to load
     * @return the result set
     * @throws SQLException if there is a problem accessign the database
     */
    private ResultSet fetchResultSetByID(DBBean db, String projectSpaceID) throws SQLException {

        StringBuffer query = new StringBuffer();
        query.append(getBaseSQLStatement());
        query.append("and p.project_id = ? ");

        int index = 0;
        db.prepareStatement(query.toString());
        db.pstmt.setString(++index, projectSpaceID);
        db.executePrepared();

        return db.result;
    }


    /**
     * Populates the specified project space from a result set row.
     * Assumes result contains a current row.
     * @param result the result set from which to populate
     * @param projectSpace the project space to populate
     * @throws SQLException if there is a problem getting a value from the
     * result set
     */
    private static void populate(java.sql.ResultSet result, ProjectSpace projectSpace) throws SQLException {

        try {
            projectSpace.setID(result.getString("project_id"));
            projectSpace.setName(result.getString("project_name"));
            projectSpace.setDescription(result.getString("project_desc"));
            projectSpace.setPercentComplete(result.getString("percent_complete"));
            projectSpace.setPercentCalculationMethod(result.getString("percent_calculation_method"));
            projectSpace.setStatusID(result.getString("status_code_id"));
            projectSpace.setStatus(PropertyProvider.get(result.getString("status")));
            projectSpace.setColorCode(ColorCode.findByID(result.getString("color_code_id")));
            projectSpace.setColorImgURL(result.getString("color_image_url"));
            projectSpace.setStartDate(result.getTimestamp("start_date"));
            projectSpace.setEndDate(result.getTimestamp("end_date"));
            projectSpace.setProjectLogoID(result.getString("project_logo_id"));
            projectSpace.setRecordStatus(result.getString("record_status"));
            projectSpace.setPlanID(result.getString("schedule_plan_id"));
            projectSpace.setDefaultCurrency(Currency.getInstance(result.getString("default_currency_code")));
            projectSpace.setSponsor(result.getString("sponsor_desc"));
            projectSpace.setImprovementCode(ImprovementCode.findByID(result.getString("improvement_code_id")));
            projectSpace.setCurrentStatusDescription(result.getString("current_status_description"));
            projectSpace.setFinancialStatusColorCode(ColorCode.findByID(result.getString("financial_status_color_code_id")));
            projectSpace.setFinancialStatusImprovementCode(ImprovementCode.findByID(result.getString("financial_status_imp_code_id")));
            projectSpace.setBudgetedTotalCost(makeMoney(result.getString("budgeted_total_cost_value"), result.getString("budgeted_total_cost_cc")));
            projectSpace.setCurrentEstimatedTotalCost(makeMoney(result.getString("current_est_total_cost_value"), result.getString("current_est_total_cost_cc")));
            projectSpace.setActualCostToDate(makeMoney(result.getString("actual_to_date_cost_value"), result.getString("actual_to_date_cost_cc")));
            projectSpace.setEstimatedROI(makeMoney(result.getString("estimated_roi_cost_value"), result.getString("estimated_roi_cost_cc")));
            projectSpace.setCostCenter(result.getString("cost_center"));
            projectSpace.setScheduleStatusColorCode(ColorCode.findByID(result.getString("schedule_status_color_code_id")));
            projectSpace.setScheduleStatusImprovementCode(ImprovementCode.findByID(result.getString("schedule_status_imp_code_id")));
            projectSpace.setResourceStatusColorCode(ColorCode.findByID(result.getString("resource_status_color_code_id")));
            projectSpace.setResourceStatusImprovementCode(ImprovementCode.findByID(result.getString("resource_status_imp_code_id")));
            projectSpace.setPriorityCode(PriorityCode.findByID(result.getString("priority_code_id")));
            projectSpace.setRiskRatingCode(RiskCode.findByID(result.getString("risk_rating_code_id")));
            projectSpace.setVisibility(ProjectVisibility.findByID(result.getString("visibility_id")));
            projectSpace.setTemplateApplied(result.getString("methodology_name"));
        	projectSpace.setParentProjectID(result.getString("parent_project_id"));
			projectSpace.setSuperProjectName(result.getString("parent_project_name"));
            projectSpace.setParentBusinessID(result.getString("parent_business_id"));
            projectSpace.setParentBusinessName(result.getString("parent_business_name"));

            // calculates this project's percent complete based on the calculation method loaded
            projectSpace.calculatePercentComplete();
            // nulling the metadata, so it will be reloaded on the first request (getMetaData call)
            projectSpace.setMetaData(null);
            projectSpace.setLoaded(true);

        } catch (InvalidValueException e) {
            throw (SQLException) new SQLException("Invalid money value").initCause(e);

        } catch (InvalidCurrencyException e) {
            throw (SQLException) new SQLException("Invalid currency value").initCause(e);

        }


    }

    /**
     * Returns a parsed Money value or null.
     * @param value the value
     * @param currencyCode the currency code
     * @return the parsed Money value or null if either the value or currencyCode
     * are null
     * @throws InvalidValueException if the value is present but not valid
     * @throws InvalidCurrencyException if the currencyCode is specified
     * but not valid
     */
    private static Money makeMoney(String value, String currencyCode) throws InvalidValueException, InvalidCurrencyException {

        Money money = null;

        if (value != null && currencyCode != null) {
            money = Money.parse(value, currencyCode, SessionManager.getUser());
        }

        return money;
    }

    /**
     * Executes a select statement to find minimal project space for the specified business id.
     * @param db the DBBean in which to perform the transaction
     * @param businessSpaceID the id of the business space to load prjects from
     * @return the result set
     * @throws SQLException if there is a problem accessign the database
     */
    private ResultSet fetchMinimalResultSetByBusinessID(DBBean db, String businessSpaceID) throws SQLException {

        StringBuffer query = new StringBuffer();
        
        //this gets all the projects and sub projects under a root business and its sub businesses
        query.append("select DISTINCT obn.project_id, obn.project_name, obn.project_desc, obn.sponsor_desc, obn.priority_code_id, obn.cost_center, obn.status, ");
        query.append("onb.name, shs.parent_space_id, obn.is_subproject from pn_space_has_space shs, pn_project_view obn, pn_object_name onb ");
        query.append("where shs.record_status = 'A' and obn.record_status = 'A' and obn.project_id = shs.child_space_id and shs.child_space_type = 'project' ");
        query.append("and onb.object_id = shs.parent_space_id ");
        query.append("start with shs.parent_space_id = ? or shs.parent_space_id in "); 
        query.append("(select child_space_id from pn_space_has_space where record_status = 'A' and child_space_type = 'business' "); 
        query.append("start with parent_space_id = ? connect by parent_space_id = prior child_space_id) "); 
        query.append("connect by shs.parent_space_id = prior shs.child_space_id ORDER BY shs.parent_space_id, obn.project_name ");
        
        int index = 0;
        db.prepareStatement(query.toString());
        db.pstmt.setString(++index, businessSpaceID);
        db.pstmt.setString(++index, businessSpaceID);
        db.executePrepared();

        return db.result;
}
    
    /**
     * Get parent business name by parent space id
     * of child project 
     * 
     * @param parentSpaceId the parent space id of child project
     * @return business name.
     */
    private String getParentBusinessNameOfChildProject(String parentSpaceId){
    	String findBusinessNameSQL = "select b.business_name from pn_space_has_space shs, pn_business b where shs.child_space_id = ? " +
			"and shs.parent_space_id = b.business_id and shs.parent_space_type = 'business'";
    	DBBean db = new DBBean();
    	String businessName = StringUtils.EMPTY;
    	try{
	    	db.prepareStatement(findBusinessNameSQL);
	    	db.pstmt.setString(1, parentSpaceId);
    		db.executePrepared();
    		if(db.result.next()){
    			businessName = db.result.getString("business_name");
    		}
    	}catch (SQLException e) {
			Logger.getLogger(ProjectSpaceFinder.class).error("Error occurred while getting parent business name :" + e.getMessage());
		} finally {
			db.release();
		}
    	
    	return businessName;
    }
}
