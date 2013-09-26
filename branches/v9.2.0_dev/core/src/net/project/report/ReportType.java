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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 19730 $
|       $Date: 2009-08-12 11:09:22 -0300 (mié, 12 ago 2009) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.report;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.business.report.projectstatus.ProjectPortfolioReport;
import net.project.business.report.projectstatus.ProjectStatusReport;
import net.project.database.DBBean;
import net.project.financial.report.BusinessProjectsFinancialReport;
import net.project.form.report.formitemsummaryreport.FormItemSummaryReport;
import net.project.form.report.formitemtimeseries.FormItemTimeSeriesReport;
import net.project.material.report.ProjectMaterialReport;
import net.project.persistence.PersistenceException;
import net.project.resource.report.businessworkcompleted.BusinessWorkCompletedReport;
import net.project.resource.report.workcompleted.WorkCompletedReport;
import net.project.schedule.report.latetaskreport.LateTaskReport;
import net.project.schedule.report.overallocation.OverallocatedResourcesReport;
import net.project.schedule.report.projectstatusreport.ProjectStatusReports;
import net.project.schedule.report.resourceallocation.ResourceAllocationReport;
import net.project.schedule.report.scheduletasks.ScheduleTasksReport;
import net.project.schedule.report.taskscomingdue.TasksComingDueReport;

/**
 * Identifies a type of report that can be generated from project.net software.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ReportType {
    /** A <code>List</code> containing all types of report available. */
    private static List reportTypes = new LinkedList();
    private static Map reportTypeMap = new HashMap();

    /**
     * Object describing the "Late Task Report" report.
     */
    public static final ReportType LATE_TASK_REPORT = new ReportType("ltr",
        "prm.schedule.report.latetaskreport.name",
        "prm.schedule.report.latetaskreport.description",
        "prm.schedule.report.latetaskreport.xslpath", 
		LateTaskReport.class);
    /**
     * Object describing the "Tasks Coming Due Report" report.
     */
    public static final ReportType TASKS_COMING_DUE_REPORT = new ReportType("tcdr",
        "prm.schedule.report.taskscomingduereport.name",
        "prm.schedule.report.taskscomingduereport.description",
        "prm.schedule.report.taskscomingduereport.xslpath", 
		TasksComingDueReport.class);
    /**
     * Object describing the "Resource Allocation" Report.
     */
    public static final ReportType RESOURCE_ALLOCATION_REPORT = new ReportType("rar",
        "prm.schedule.report.resourceallocationreport.name",
        "prm.schedule.report.resourceallocationreport.description",
        "prm.schedule.report.resourceallocationreport.xslpath", 
		ResourceAllocationReport.class);
    /**
     * Object describing the "Work Completed By User(s)" report.
     */
    public static final ReportType WORK_COMPLETED_REPORT = new ReportType("wcr",
        "prm.resource.report.workcompleted.name",
        "prm.resource.report.workcompleted.description",
        "prm.resource.report.workcompleted.xslpath", 
		WorkCompletedReport.class);
    
    /**
     * Object describing the "Business Work Completed By User(s)" report.
     */
    public static final ReportType BUSINESS_WORK_COMPLETED_REPORT = new ReportType("bwcr",
        "prm.business.report.businessworkcompleted.name",
        "prm.business.report.businessworkcompleted.description",
        "prm.business.report.businessworkcompleted.xslpath", 
		BusinessWorkCompletedReport.class);    
    
    /**
     * Object describing the "Overallocated Resources" report.
     */
    public static final ReportType OVERALLOCATED_RESOURCES_REPORT = new ReportType("oar",
        "prm.schedule.report.overallocationreport.name",
        "prm.schedule.report.overallocationreport.description",
        "prm.schedule.report.overallocationreport.xslpath", 
		OverallocatedResourcesReport.class);
    /**
     * Object describing the "Schedule Tasks" report.
     */
    public static final ReportType SCHEDULE_TASKS_REPORT = new ReportType("str",
        "prm.schedule.report.scheduletasksreport.name",
        "prm.schedule.report.scheduletasksreport.description",
        "prm.schedule.report.scheduletasksreport.xslpath", 
		ScheduleTasksReport.class);
    /**
     * Object describing the "Form Item Summary" report.
     */
    public static final ReportType FORM_ITEM_SUMMARY_REPORT = new ReportType("fisr",
        "prm.form.report.formitemsummaryreport.name",
        "prm.form.report.formitemsummaryreport.description",
        "prm.form.report.formitemsummaryreport.xslpath", 
		FormItemSummaryReport.class) {

        public String getParameterPageURL() {
            return "/report/ChooseForm.jsp?module="+Module.REPORT+"&reportType="+getID();
        }
    };
    /**
     * Object describing the "Form Item Time Series" report.
     */
    public static final ReportType FORM_ITEM_TIME_SERIES = new ReportType("fits",
        "prm.form.report.formitemtimeseries.name",
        "prm.form.report.formitemtimeseries.description",
        "prm.form.report.formitemtimeseries.xslpath", FormItemTimeSeriesReport.class) {

        public String getParameterPageURL() {
            return "/report/ChooseForm.jsp?module="+Module.REPORT+"&reportType="+getID();
        }

        public String getCustomParameterChannelURL() {
            return "/report/include/FITSParameters.jsp";
        }
    };
    /**
     * Object describing the "New User" report.
     */
    public static final ReportType NEW_USER_REPORT = new ReportType("nur",
        "prm.resource.report.newuserreport.name",
        "prm.resource.report.newuserreport.description",
        "prm.resource.report.newuserreport.xslpath",
        net.project.resource.report.newuserreport.NewUserReport.class);

    /**
     * Object describing the "Project Status Report" report.
     */
    public static final ReportType PROJECT_STATUS_REPORT  = new ReportType("psr",
        "prm.business.report.projectstatusreport.name",
        "prm.business.report.projectstatusreport.description",
        "prm.business.report.projectstatusreport.xslpath", 
		ProjectStatusReport.class);
    
    /**
     * Object describing the "Project Portfolio Report" report.
     */
    public static final ReportType PROJECT_PORTFOLIO_REPORT  = new ReportType("ppr",
        "prm.enterprise.report.projectportfolioreport.name",
        "prm.enterprise.report.projectportfolioreport.description",
        "prm.enterprise.report.projectportfolioreport.xslpath", 
		ProjectPortfolioReport.class);

    /**
     * Object describing the "Project and Sub-Project Status Report" report.
     */
    public static final ReportType PROJECT_STATUS_REPORTS  = new ReportType("psrs",
        "prm.project.report.projectstatusreport.name",
        "prm.project.report.projectstatusreport.description",
        "prm.project.report.projectstatusreport.xslpath", 
		ProjectStatusReports.class);
    
    /**
     * Object describing the "Material Report" report.
     */
    public static final ReportType MATERIAL_REPORTS  = new ReportType("mtr",
        "prm.project.report.projectmaterialreport.name",
        "prm.project.report.projectmaterialreport.description",
        "prm.project.report.projectmaterialreport.xslpath", 
		ProjectMaterialReport.class);    
    
    /**
     * Object describing the "Financial Project Report" report.
     */
    public static final ReportType BUSINESS_PROJECTS_FINANCIAL_REPORTS  = new ReportType("bpfr",
        "prm.project.report.businessprojectsfinancialreport.name",
        "prm.project.report.businessprojectsfinancialreport.description",
        "prm.project.report.businessprojectsfinancialreport.xslpath", 
		BusinessProjectsFinancialReport.class);    

    /**
     * Get the report that corresponds to the report id passed in the id parameter.
     *
     * @param id a <code>String</code> value that unique identifies the report.
     * @return a <code>ReportType</code> which corresponds to the id passed in
     * the <code>id</code> parameter.
     */
    public static ReportType getForID(String id) {
        return (ReportType)reportTypeMap.get(id);
    }

    /**
     * Get a list of all possible report types.
     *
     * @return a <code>List</code> value containing all report available on the
     * system.
     */
    public static List getReportTypes() {
        return reportTypes;
    }


    /**
     * Get a list of all possible report types for a given space type.
     *
     * @param spaceType a <code>String</code> defined from ISpaceTypes that
     * specifies a space type.
     * @return a <code>List</code> value containing all report types available
     * for the space type.
     */
    public static List getReportTypes(String spaceType) throws PersistenceException {
        List reportTypes = new LinkedList();
        DBBean db = new DBBean();
        try {
            db.prepareStatement(
                "select " +
                "  s.report_type " +
                "from " +
                "  pn_space_type_has_report_type s, " +
                "  pn_report_sequence r " +
                "where " +
                "  s.report_type = r.report_type " +
                "  and s.space_type = ? " +
                "order by " +
                "  r.sequence"
            );

            db.pstmt.setString(1, spaceType);
            db.executePrepared();

            while (db.result.next()) {
                String reportType = db.result.getString("report_type");
                ReportType reportTypeObj = (ReportType)reportTypeMap.get(reportType);
                //Assertions are only used in development time.
                assert reportTypeObj != null : reportType + " did not have a corresponding report type object";

                reportTypes.add(reportTypeObj);
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to load report types", sqle);
        } finally {
            db.release();
        }

        return reportTypes;
    }

    //--------------------------------------------------------------------------
    //Implementation
    //--------------------------------------------------------------------------
    /**
     * A unique identifier for this ReportType.  This is primarily used when
     * a ReportType has to be transferred through HTTP, which doesn't support
     * java objects.
     */
    private String id;
    /**
     * A class which can be instantiated to create the type of report that this
     * report type points to.
     */
    private Class implementationClass;
    /**
     * A token which can be looked up to find the human-readable name of this
     * report.
     */
    private String nameToken;
    /**
     * A token which contains a full description of this report does.
     */
    private String descriptionToken;
    /**
     * Token which describes the location of an XSL file to produce the HTML
     * version of the report.
     */
    private String xslToken;

    /**
     * Standard private constructor.
     *
     * @param uniqueID a <code>String</code> value that uniquely identifies this
     * report type.
     * @param nameToken a <code>String</code> value that indicates which token
     * should be used to look up the Report name.
     * @param descriptionToken a <code>String</code> value that points to a token
     * which describes the report in a sentence.  This token is used to populate
     * the "Report Description" field on the report list.
     * @param xslToken a <code>String</code> which contains the location of the
     * XSL file to turn an XML report into HTML.  The location is relative to
     * the jsp root.
     * @param implementationClass a <code>Class</code> value that is the class
     * declaration for the report that this report type represents.
     */
    private ReportType(String uniqueID, String nameToken, String descriptionToken,
                       String xslToken, Class implementationClass) {
        this.id = uniqueID;
        this.implementationClass = implementationClass;
        this.nameToken = nameToken;
        this.descriptionToken = descriptionToken;
        this.xslToken = xslToken;

        reportTypes.add(this);
        reportTypeMap.put(uniqueID, this);
    }

    /**
     * Get the string value that uniquely identifies this Report Type.
     *
     * @return a <code>String</code> value that uniquely identifies this report.
     */
    public String getID() {
        return id;
    }

    /**
     * Get the human-readable report name suitable for display.
     *
     * @return a <code>String</code> containing a report name suitable for display.
     */
    public String getName() {
        return PropertyProvider.get(nameToken);
    }

    /**
     * Get a human-readable description for the current report.
     *
     * @return a <code>String</code> value containing a human readable description
     * of the current report.
     */
    public String getDescription() {
        return PropertyProvider.get(descriptionToken);
    }

    /**
     * Get the relative (to the application server web root) xsl file that will
     * properly render this report in html format.
     *
     * @return a <code>String</code> value which contains the location of an xsl
     * file to render this report in html.
     */
    public String getXSLPath() {
        return PropertyProvider.get(xslToken);
    }

    /**
     * Instantiate an instance of the report type that this report type represents.
     *
     * @return a new instance of the report type identified by this <code>ReportType</code>
     * variable.
     * @throws ReportException if an error occurs while instantiating the report.
     */
    public IReport getInstance(ReportScope scope) throws ReportException {
        IReport reportInstance = null;

        try {
            Constructor constructor = implementationClass.getConstructor(new Class[] { ReportScope.class } );
            reportInstance = (IReport)constructor.newInstance(new Object[] { scope });
        } catch (Exception e) {
            throw new ReportException("Unable to create new instance of report.", e);
        }

        return reportInstance;
    }

    /**
     * Get the page that the user will need to visit in order to get to enter
     * parameters to produce this report.
     *
     * @return a <code>String</code> value containing the URL for a parameter
     * page.  The URL doesn't contain the root URL, which should be prepended
     * using <code>SessionManager.getJSPRootURL()</code>.
     */
    public String getParameterPageURL() {
    	//Business time summary report is an independent module, This will open in new page.  
    	if("bwcr".equals(this.id)){
    		return "/business/report/TimeSummary?module="+Module.TIME_SUMMARY_REPORT;
    	}
        return "/report/parameters.jsp?module="+Module.REPORT+"&reportType="+getID();
    }

    /**
     * If this value is set, it is a custom page that should be rendered as a
     * channel.  It contains additional custom parameters that apply to a
     * report.
     *
     * @return a <code>String</code> value containing the location of a page.
     */
    public String getCustomParameterChannelURL() {
        return null;
    }
}
