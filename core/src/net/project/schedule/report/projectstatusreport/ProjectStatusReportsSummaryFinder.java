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

 package net.project.schedule.report.projectstatusreport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.project.base.finder.Finder;
import net.project.base.finder.FinderListener;
import net.project.base.finder.FinderListenerAdapter;
import net.project.base.property.PropertyProvider;
import net.project.code.ColorCode;
import net.project.code.ImprovementCode;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * Class designed to load <code>ProjectStatusReportsSummaryData</code> objects from the database.
 *
 * @author K B Deepak
 * @since Version 1.0
 */
class ProjectStatusReportsSummaryFinder extends Finder {
    /**
     * Private String that contains the basic report.  Be careful about changing
     * this String, it is used by all of the finder methods below, as well as by
     * the Filtering classes that can be found in the same directory as this file.
     */
    private String BASE_SELECT_STATEMENT =
        "SELECT "+
        "    p.project_id, p.project_name, p.project_desc, p.percent_complete, "+
        "    p.status_code_id, p.status, p.color_code_id, p.color, p.color_image_url, "+
        "    p.is_subproject, p.record_status, p.start_date, p.end_date, p.date_modified, "+
        "    p.project_logo_id, p.default_currency_code, p.percent_calculation_method, "+
        "    p.sponsor_desc, p.improvement_code_id, p.current_status_description, p.percent_complete, "+
        "    p.financial_status_color_code_id, p.financial_status_imp_code_id, "+
        "    p.budgeted_total_cost_value, p.budgeted_total_cost_cc, "+
        "    p.current_est_total_cost_value, p.current_est_total_cost_cc, "+
        "    p.actual_to_date_cost_value, p.actual_to_date_cost_cc, "+
        "    p.estimated_roi_cost_value, p.estimated_roi_cost_cc, p.cost_center, "+
        "    p.schedule_status_color_code_id, p.schedule_status_imp_code_id, "+
        "    p.resource_status_color_code_id, p.resource_status_imp_code_id, "+
        "    p.priority_code_id, p.risk_rating_code_id, p.visibility_id, shp.plan_id as schedule_plan_id, "+
        "    b.business_id, "+
        "    b.business_name "+
        "FROM "+
        "  pn_space_has_space ss, "+
        "  pn_business_space bs, "+
        "  pn_business b, "+
        "  pn_project_view p, "+
        "  pn_space_has_plan shp "+
        "WHERE "+
        "  ss.child_space_id(+) = p.project_id "+
        "  and bs.business_space_id(+) = ss.parent_space_id "+
        "  and shp.space_id = p.project_id "+
        "  and b.business_id(+) = bs.business_id "+
        "  and ss.relationship_child_to_parent(+) = 'owned_by' ";

    private FinderListener listener = new FinderListenerAdapter() {
        /**
         * This method is called prior to doing anything in the getSQLStatement()
         * method.  No sorters or filters have been gathered, nor has the
         * <code>getBaseSQLStatement</code> been called.
         *
         * This method was created to allow Finders to add "group by" statements
         * that are required for the sql statement to function properly, although
         * it can be used for much more.
         *
         * @param f a <code>Finder</code> that is about to call its own
         * {@link net.project.base.finder.Finder#getSQLStatement} method.
         */
        public void preConstruct(Finder f) {
        }

        /**
         * This method is called just before running <code>executePrepared</code> on
         * the sql statement constructed by calling <code>getSQLStatement</code>.
         * This allows a user to set parameters that are required to execute a
         * prepared statement.
         *
         * @param db a {@link net.project.database.DBBean} that is just about to
         * call {@link net.project.database.DBBean#executePrepared}.
         * @throws SQLException if an error occurs while modifying the
         * <code>DBBean</code>.
         */
        public void preExecute(DBBean db) throws SQLException {
        }
    };

    /**
     * Standard constructor.
     */
    public ProjectStatusReportsSummaryFinder() {
        super();
        addFinderListener(listener);
    }

    /**
     * Return the basic SQL Statement that we will build our select statement
     * upon.
     *
     * @return a <code>String</code> which contains the base select statement.
     */
    protected String getBaseSQLStatement() {
        return BASE_SELECT_STATEMENT;
    }

    /**
     * Find a LateTaskReportSummaryData object that corresponds to the primary key passed
     * in through the planID variable.
     *
     * @param planID a <code>String</code> containing the primary key of the
     * schedule (plan) that you want to report about.
     * @return a <code>List</code> which should contain 1 or zero
     * {@link net.project.schedule.report.projectstatusreport.ProjectStatusReportsSummaryData} objects.
     * @throws net.project.persistence.PersistenceException if there is an error loading task report
     * information from the database.
     */
    public List findByPlanID(String planID) throws PersistenceException {
        addWhereClause(" p.plan_id = " + planID);
        return loadFromDB();
    }

    /**
     * Find a ProjectStatusReportsSummaryData object that corresponds to the space id passed
     * in the spaceID parameter.
     *
     * @param spaceID a <code>String</code> variable containing the id of a space
     * which contains a schedule.
     * @return a <code>List</code> which should contain 1 or zero
     * {@link net.project.schedule.report.projectstatusreport.ProjectStatusReportsSummaryData} objects.
     * @throws net.project.persistence.PersistenceException if there is an error loading task report
     * information from the database, or if no data is returned from the database.
     */
    public List findBySpaceID(String spaceID) throws PersistenceException {
        addWhereClause(" shp.space_id = " + spaceID);
        return loadFromDB();
    }


    /**
     * Find a ProjectStatusReportSummaryData object that corresponds to the space id passed
     * in the projectID parameter.
     *
     * @param projectID a <code>String</code> variable containing the id of a project
     * which contains a schedule.
     * @return a <code>List</code> which should contain 1 or zero
     * {@link net.project.schedule.report.projectstatusreport.ProjectStatusReportSummaryData} objects.
     * @throws net.project.persistence.PersistenceException if there is an error loading task report
     * information from the database, or if no data is returned from the database.
     */
    public List findByProjectID(String projectID) throws PersistenceException {
        addWhereClause(" p.project_id = " + projectID);
        return loadFromDB();
    }

    /**
     * Find all task report data objects that exist in the database.
     *
     * @return a <code>ProjectStatusReportsSummaryData</code> array of objects which correspond
     * to the SQL Statement that we've constructed.
     * @throws net.project.persistence.PersistenceException if an error occurs while trying to load data
     * into task objects.
     */
    public List findAll() throws PersistenceException {
        return loadFromDB();
    }

    /**
     * Populate a domain object which data specific to the query result.  For
     * example, a task finder would populate a {@link net.project.schedule.Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param databaseResults a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException {
        ProjectStatusReportsSummaryData newObject = new ProjectStatusReportsSummaryData();
        newObject.setProjectLastUpdated(databaseResults.getDate("date_modified"));
        newObject.setProjectBusinessArea(databaseResults.getString("business_name"));
        newObject.setProjectName(databaseResults.getString("project_name"));
        newObject.setProjectNumber(databaseResults.getString("project_id"));
        newObject.setProjectDescription(databaseResults.getString("project_desc"));
        newObject.setProjectStartDate(databaseResults.getDate("start_date"));
        newObject.setProjectFinishDate(databaseResults.getDate("end_date"));
        newObject.setProjectOverallComplete(databaseResults.getString("percent_complete"));
        newObject.setProjectOverallStage(PropertyProvider.get(databaseResults.getString("status")));
        newObject.setColorCode(ColorCode.findByID(databaseResults.getString("color_code_id")));
        newObject.setColorImgURL(databaseResults.getString("color_image_url"));
        newObject.setImprovementCode(ImprovementCode.findByID(databaseResults.getString("improvement_code_id")));
        newObject.setFinancialStatusColorCode(ColorCode.findByID(databaseResults.getString("financial_status_color_code_id")));
        newObject.setFinancialStatusImprovementCode(ImprovementCode.findByID(databaseResults.getString("financial_status_imp_code_id")));
        newObject.setScheduleStatusColorCode(ColorCode.findByID(databaseResults.getString("schedule_status_color_code_id")));
        newObject.setScheduleStatusImprovementCode(ImprovementCode.findByID(databaseResults.getString("schedule_status_imp_code_id")));
        newObject.setResourceStatusColorCode(ColorCode.findByID(databaseResults.getString("resource_status_color_code_id")));
        newObject.setResourceStatusImprovementCode(ImprovementCode.findByID(databaseResults.getString("resource_status_imp_code_id")));
        newObject.setProjectStatusComments(databaseResults.getString("current_status_description"));
        return newObject;
    }
}
