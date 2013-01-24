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
|   $Revision: 20910 $
|       $Date: 2010-06-03 10:45:19 -0300 (jue, 03 jun 2010) $
|     $Author: ritesh $
|
+----------------------------------------------------------------------*/
package net.project.portfolio.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.finder.ColumnDefinition;

/**
 * Provides the enumeration of available columns.
 * These columns are used when constructing filters and sorters.
 * Each column as a unique ID, a SQL column and a token that provides the display
 * name for the column
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class PersonalPortfolioColumnDefinition extends ColumnDefinition {

    /**
     * All columns.
     */
    private static final List columnList = new ArrayList();

    public static final ColumnDefinition NAME = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_NAME.getID(), "upper(p.project_name)", "prm.project.portfolio.finder.column.name", true);
    public static final ColumnDefinition DESCRIPTION = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_DESCRIPTION.getID(), "p.project_desc", "prm.project.portfolio.finder.column.description", true);
    public static final ColumnDefinition META_EXTERNAL_PROJECT_ID = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_META_EXTERNAL_PROJECT_ID.getID(), "upper(p.project_name)", "prm.project.portfolio.finder.column.meta.externalprojectid", true);
    public static final ColumnDefinition OWNING_BUSINESS = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_OWNING_BUSINESS.getID(), "upper(b.business_name)", "prm.project.portfolio.finder.column.owningbusiness", true);
    public static final ColumnDefinition SUBPROJECT_OF = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_SUBPROJECT_OF.getID(), "upper(p.project_name)", "prm.project.portfolio.finder.column.subprojectof", false);
    public static final ColumnDefinition PROJECT_SPONSOR = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_PROJECT_SPONSOR.getID(), "p.sponsor_desc", "prm.project.portfolio.finder.column.projectsponsor", true);
    public static final ColumnDefinition META_PROJECT_MANAGER = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_META_PROJECT_MANAGER.getID(), "upper(p.project_name)", "prm.project.portfolio.finder.column.meta.projectmanager", true);
    public static final ColumnDefinition META_PROGRAM_MANAGER = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_META_PROGRAM_MANAGER.getID(), "upper(p.project_name)", "prm.project.portfolio.finder.column.meta.programmanager", true);
    public static final ColumnDefinition META_INITIATIVE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_META_INITIATIVE.getID(), "upper(p.project_name)", "prm.project.portfolio.finder.column.meta.initiative", true);
    public static final ColumnDefinition META_FUNCTIONAL_AREA = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_META_FUNCTIONAL_AREA.getID(), "upper(p.project_name)", "prm.project.portfolio.finder.column.meta.functionalarea", true);
    public static final ColumnDefinition PRIORITY_CODE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_PRIORITY_CODE.getID(), "p.priority_code_id", "prm.project.portfolio.finder.column.prioritycode", true);
    public static final ColumnDefinition RISK_RATING_CODE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_RISK_RATING_CODE.getID(), "p.risk_rating_code_id", "prm.project.portfolio.finder.column.riskratingcode", true);
    public static final ColumnDefinition META_PROJECT_CHARTER = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_META_PROJECT_CHARTER.getID(), "upper(p.project_name)", "prm.project.portfolio.finder.column.meta.projectcharter", true);
    public static final ColumnDefinition START_DATE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_START_DATE.getID(), "p.start_date", "prm.project.portfolio.finder.column.startdate", true);
    public static final ColumnDefinition END_DATE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_END_DATE.getID(), "p.end_date", "prm.project.portfolio.finder.column.enddate", true);
    public static final ColumnDefinition PERCENT_COMPLETE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_PERCENT_COMPLETE.getID(), "p.percent_complete", "prm.project.portfolio.finder.column.percentcomplete", true);
    public static final ColumnDefinition STATUS_CODE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_STATUS_CODE.getID(), "p.status_code_id", "prm.project.portfolio.finder.column.statuscode", true);
    public static final ColumnDefinition COLOR_CODE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_COLOR_CODE.getID(), "p.color_code_id", "prm.project.portfolio.finder.column.colorcode", true);
    public static final ColumnDefinition IMPROVEMENT_CODE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_IMPROVEMENT_CODE.getID(), "p.improvement_code_id", "prm.project.portfolio.finder.column.improvementcode", true);
    public static final ColumnDefinition FINANCIAL_STATUS_COLOR_CODE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_FINANCIAL_STATUS_COLOR_CODE.getID(), "p.financial_status_color_code_id", "prm.project.portfolio.finder.column.financialstatuscolorcode", true);
    public static final ColumnDefinition FINANCIAL_STATUS_IMPROVEMENT = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_FINANCIAL_STATUS_IMPROVEMENT.getID(), "p.financial_status_imp_code_id", "prm.project.portfolio.finder.column.financialstatusimprovementcode", true);
    public static final ColumnDefinition SCHEDULE_STATUS_COLOR_CODE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_SCHEDULE_STATUS_COLOR_CODE.getID(), "p.schedule_status_color_code_id", "prm.project.portfolio.finder.column.schedulestatuscolorcode", true);
    public static final ColumnDefinition SCHEDULE_STATUS_IMPROVEMENT = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_SCHEDULE_STATUS_IMPROVEMENT.getID(), "p.schedule_status_imp_code_id", "prm.project.portfolio.finder.column.schedulestatusimprovementcode", true);
    public static final ColumnDefinition RESOURCE_STATUS_COLOR_CODE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_RESOURCE_STATUS_COLOR_CODE.getID(), "p.resource_status_color_code_id", "prm.project.portfolio.finder.column.resourcestatuscolorcode", true);
    public static final ColumnDefinition RESOURCE_STATUS_IMPROVEMENT = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_RESOURCE_STATUS_IMPROVEMENT.getID(), "p.resource_status_imp_code_id", "prm.project.portfolio.finder.column.resourcestatusimprovement", true);
    public static final ColumnDefinition CURRENT_STATUS_DESCRIPTION = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_CURRENT_STATUS_DESCRIPTION.getID(), "p.current_status_description", "prm.project.portfolio.finder.column.currentstatusdescription", true);
    public static final ColumnDefinition ESTIMATED_ROI_VALUE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_ESIMATED_ROI_VALUE.getID(), "p.estimated_roi_cost_value", "prm.project.portfolio.finder.column.estimatedroicostvalue", true);
    public static final ColumnDefinition ESTIMATED_ROI_CURRENCY = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_ESIMATED_ROI_CURRENCY.getID(), "p.estimated_roi_cost_cc", "prm.project.portfolio.finder.column.estimatedroicostcurrency", true);
    public static final ColumnDefinition COST_CENTER = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_COST_CENTER.getID(), "p.cost_center", "prm.project.portfolio.finder.column.costcenter", true);
    public static final ColumnDefinition PROJECT_ID = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_PROJECT_ID.getID(), "p.project_id", "prm.project.portfolio.finder.column.projectid", false);
    public static final ColumnDefinition INCLUDES_EVERYONE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_INCLUDES_EVERYONE.getID(), "p.includes_everyone", "prm.project.portfolio.finder.column.includeseveryone", false);
    public static final ColumnDefinition DEFAULT_CURRENCY_CODE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_DEFAULT_CURRENCY_CODE.getID(), "p.default_currency_code", "prm.project.portfolio.finder.column.defaultcurrencycode", true);
    public static final ColumnDefinition META_TYPE_OF_EXPENSE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_META_TYPE_OF_EXPENSE.getID(), "upper(p.project_name)", "prm.project.portfolio.finder.column.meta.typeofexpense", true);
    public static final ColumnDefinition BUDGETED_TOTAL_COST_VALUE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_BUDGETED_TOTAL_COST_VALUE.getID(), "p.budgeted_total_cost_value", "prm.project.portfolio.finder.column.budgetedtotalcostvalue", true);
    public static final ColumnDefinition BUDGETED_TOTAL_COST_CURRENCY = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_BUDGETED_TOTAL_COST_CURRENCY.getID(), "p.budgeted_total_cost_cc", "prm.project.portfolio.finder.column.budgetedtotalcostcurrency", true);
    public static final ColumnDefinition CURRENT_ESTIMATED_TOTAL_COST_VALUE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_CURRENT_ESTIMATED_TOTAL_COST_VALUE.getID(), "p.current_est_total_cost_value", "prm.project.portfolio.finder.column.currentestimatedtotalcostvalue", true);
    public static final ColumnDefinition CURRENT_ESTIMATED_TOTAL_COST_CURRENCY = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_CURRENT_ESTIMATED_TOTAL_COST_CURRENCY.getID(), "p.current_est_total_cost_cc", "prm.project.portfolio.finder.column.currentestimatedtotalcostcurrency", true);
    public static final ColumnDefinition ACTUAL_COST_TO_DATE_VALUE = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_ACTUAL_COST_TO_DATE_VALUE.getID(), "p.actual_to_date_cost_value", "prm.project.portfolio.finder.column.actualcosttodatevalue", true);
    public static final ColumnDefinition ACTUAL_COST_TO_DATE_CURRENCY = new PersonalPortfolioColumnDefinition(PersonalPortfolioIngredientsID.COLUMN_ACTUAL_COST_TO_DATE_CURRENCY.getID(), "p.actual_to_date_cost_cc", "prm.project.portfolio.finder.column.actualcosttodatecurrency", true);

    /**
     * Returns all Personal Portfolio Column Definitions.
     * @return the list where each element is a <code>ColumnDefinition</code>
     */
    public static List getAllColumnDefinitions() {
        return Collections.unmodifiableList(columnList);
    }

    /**
     * Returns all sortable Personal Portfolio Column Definitions.
     * These are the columns which may be presented for sorting.
     * @return the list where each element is a <code>ColumnDefinition</code>
     */
    static List getSortableColumnDefinitions() {
        List sortableList = new ArrayList();

        for (Iterator it = getAllColumnDefinitions().iterator(); it.hasNext(); ) {
            PersonalPortfolioColumnDefinition nextColumnDefinition = (PersonalPortfolioColumnDefinition) it.next();

            if (nextColumnDefinition.isSortable) {
                sortableList.add(nextColumnDefinition);
            }
        }

        return sortableList;
    }

    /**
     * Indicates whether this column is available for sorting.
     */
    private final boolean isSortable;

    /**
     * Creates a new column definition and adds it to the list of all column definitions.
     * @param id the unique ID of this column, used for persistence
     * @param columnName the name of the SQL column
     * @param nameToken the token providing the display name of the column
     */
    private PersonalPortfolioColumnDefinition(String id, String columnName, String nameToken, boolean isSortable) {
        super(id, columnName, nameToken);
        columnList.add(this);
        this.isSortable = isSortable;
    }

}

