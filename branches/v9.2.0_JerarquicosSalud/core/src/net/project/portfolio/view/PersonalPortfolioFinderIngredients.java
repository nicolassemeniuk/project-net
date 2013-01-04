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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.PnetRuntimeException;
import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DateFilter;
import net.project.base.finder.DomainComparator;
import net.project.base.finder.DomainOption;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.FilterOperator;
import net.project.base.finder.FinderColumn;
import net.project.base.finder.FinderColumnList;
import net.project.base.finder.FinderFilter;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.FinderGroupingList;
import net.project.base.finder.FinderIngredients;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.FinderSorterList;
import net.project.base.finder.NumberComparator;
import net.project.base.finder.NumberFilter;
import net.project.base.finder.SimpleDomainFilter;
import net.project.base.finder.TextComparator;
import net.project.base.finder.TextFilter;
import net.project.base.money.MoneyFilter;
import net.project.business.BusinessSpace;
import net.project.code.ColorCode;
import net.project.code.ImprovementCode;
import net.project.portfolio.BusinessPortfolio;
import net.project.portfolio.IPortfolioEntry;
import net.project.portfolio.ProjectPortfolio;
import net.project.project.DomainListBean;
import net.project.project.PriorityCode;
import net.project.project.ProjectStatus;
import net.project.project.RiskCode;
import net.project.base.property.PropertyProvider;

/**
 * Provides the ingredients for a Personal Portfolio View.
 * This sets up all the available columns, filters, sorters and groupers.
 */
public class PersonalPortfolioFinderIngredients extends FinderIngredients {

    /**
     * Provides a handle to a FinderFilterList that is a component of the overall
     * FinderFilterList.  This is used for visiting this sub-part of the
     * filter list separately.
     */
    private FinderFilterList projectPropertiesFilterList = null;

    /**
     * Provides a handle to a FinderFilter that is a component of the overall
     * FinderFilterList.  This is used for visiting this sub-part of the
     * filter list separately.
     */
    private FinderFilter selectedProjectsFilter = null;

    /**
     * Provides a handle to a FinderFilter that is a component of the overall
     * FinderFilterList.  This FinderFilter allows Projects to show up because
     * they contains an "everyone" group.
     */
    private FinderFilter everyoneGroupFilter = null;

    private MetaColumnList metaColumnList;

    /**
     * Creates an initialized ingredients class containing all the filters,
     * groupers and sorters.
     */
    PersonalPortfolioFinderIngredients(ProjectPortfolio portfolio) {
        populateColumnList();
        populateFilterList(portfolio);
        populateGroupingList();
        populateSorterList();
        populateMetaColumnList();
    }

    /**
     * Returns the FinderFilterList containing the filters for a Project's
     * Properties.
     * This allows these specific filters to be visited separately.
     *
     * @return the project properties filter list
     */
    public FinderFilterList getProjectPropertiesFilterList() {
        return this.projectPropertiesFilterList;
    }

    /**
     * Returns the FinderFilterList containing a SelectedProjectsFilter.
     * This allows this filter to be visited separately.
     *
     * @return the selected projects filter list
     */
    public FinderFilterList getSelectedProjectsFilterList() {
        FinderFilterList filterList = new FinderFilterList();

        try {
            filterList.add(this.selectedProjectsFilter);

        } catch (DuplicateFilterIDException e) {
            throw new PnetRuntimeException("Unexpected programmer error found while "+
                "constructing the Personal Portfolio View filters: "+ e) {
            };
        }

        return filterList;
    }

    private void populateMetaColumnList() {
        metaColumnList = new MetaColumnList();
        MetaColumn metaColumnName = new MetaColumn("name", false, PropertyProvider.get("prm.project.portfolio.column.projectname.lable"), "general");
        metaColumnName.setInclude(true);
        metaColumnList.addMetaColumn(metaColumnName);
        
        metaColumnList.addMetaColumn(new MetaColumn("ParentBusinessName", false, PropertyProvider.get("prm.project.create.wizard.businessowner"), "general"));
        metaColumnList.addMetaColumn(new MetaColumn("TemplateApplied", false, PropertyProvider.get("prm.project.portfolio.column.templateapplied.lable"), "general"));
        metaColumnList.addMetaColumn(new MetaColumn("description", false, PropertyProvider.get("prm.project.portfolio.column.description.lable"), "general"));
        metaColumnList.addMetaColumn(new MetaColumn("ExternalProjectID", true, PropertyProvider.get("prm.project.create.wizard.meta.projectid"), "general"));
        metaColumnList.addMetaColumn(new MetaColumn("SubprojectOf", false, PropertyProvider.get("prm.project.create.wizard.subprojectof"), "general"));
        metaColumnList.addMetaColumn(new MetaColumn("Sponsor", false, PropertyProvider.get("prm.project.properties.sponsor.label"), "general"));
        metaColumnList.addMetaColumn(new MetaColumn("ProjectManager", true, PropertyProvider.get("prm.project.create.wizard.meta.projectmanager"), "general"));
        metaColumnList.addMetaColumn(new MetaColumn("ProgramManager", true, PropertyProvider.get("prm.project.create.wizard.meta.programmanager"), "general"));
        metaColumnList.addMetaColumn(new MetaColumn("Initiative", true, PropertyProvider.get("prm.project.create.wizard.meta.initiative"), "general"));
        metaColumnList.addMetaColumn(new MetaColumn("FunctionalArea", true, PropertyProvider.get("prm.project.create.wizard.meta.functionalarea"), "general"));
        metaColumnList.addMetaColumn(new MetaColumn("PriorityCode", false, PropertyProvider.get("prm.project.properties.priority.label"), "general"));
        metaColumnList.addMetaColumn(new MetaColumn("RiskRatingCode", false, PropertyProvider.get("prm.project.properties.risk.label"), "general"));
        metaColumnList.addMetaColumn(new MetaColumn("ProjectCharter", true, PropertyProvider.get("prm.project.create.wizard.meta.projectcharter"), "general"));

        metaColumnList.addMetaColumn(new MetaColumn("StartDate", false, PropertyProvider.get("prm.project.create.wizard.startdate"), "status"));
        metaColumnList.addMetaColumn(new MetaColumn("EndDate", false, PropertyProvider.get("prm.project.portfolio.column.finishdate.lable"), "status"));
        metaColumnList.addMetaColumn(new MetaColumn("percent_complete", false, PropertyProvider.get("prm.project.propertiesedit.completion.label"), "completion"));;

        metaColumnList.addMetaColumn(new MetaColumn("CurrentStatusDescription", false, PropertyProvider.get("prm.project.propertiesedit.currentstatusdescription.label"), "status"));
        metaColumnList.addMetaColumn(new MetaColumn("status_code", false, PropertyProvider.get("prm.project.portfolio.column.overallstatus.title"), "status"));

        metaColumnList.addMetaColumn(new MetaColumn("OverallStatus", false, PropertyProvider.get("prm.project.portfolio.column.overallstatus.label"), PropertyProvider.get("prm.project.properties.status.label", "O"), "status"));
        metaColumnList.addMetaColumn(new MetaColumn("FinancialStatus", false, PropertyProvider.get("prm.project.portfolio.column.financialstatus.label"), PropertyProvider.get("prm.project.properties.financialstatus.label", "F"), "status"));
        metaColumnList.addMetaColumn(new MetaColumn("ScheduleStatus", false, PropertyProvider.get("prm.project.portfolio.column.schedulestatus.label"), PropertyProvider.get("prm.project.properties.schedulestatus.label", "S"), "status"));
        metaColumnList.addMetaColumn(new MetaColumn("ResourceStatus", false, PropertyProvider.get("prm.project.portfolio.column.resourcestatus.label"), PropertyProvider.get("prm.project.properties.resourcestatus.label", "R"), "status"));

        metaColumnList.addMetaColumn(new MetaColumn("DefaultCurrencyCode", false, PropertyProvider.get("prm.project.create.wizard.defaultcurrency"), "financial"));
        metaColumnList.addMetaColumn(new MetaColumn("TypeOfExpense", true, PropertyProvider.get("prm.project.create.wizard.meta.typeofexpense"), "financial"));

        metaColumnList.addMetaColumn(new MetaColumn("BudgetedTotalCost/Money/Value", false, PropertyProvider.get("prm.project.properties.budgetedtotalcost.label"), "financial"));
        metaColumnList.addMetaColumn(new MetaColumn("BudgetedTotalCost/Money/Currency", false, PropertyProvider.get("prm.project.portfolio.finder.column.budgetedtotalcostcurrency"), "financial"));
        metaColumnList.addMetaColumn(new MetaColumn("CurrentEstimatedTotalCost/Money/Value", false, PropertyProvider.get("prm.project.properties.currentestimatedtotalcost.label"), "financial"));
        metaColumnList.addMetaColumn(new MetaColumn("CurrentEstimatedTotalCost/Money/Currency", false, PropertyProvider.get("prm.project.properties.currentestimatedtotalcost.currency.label"), "financial"));
        metaColumnList.addMetaColumn(new MetaColumn("ActualCostToDate/Money/Value", false, PropertyProvider.get("prm.project.properties.actualcosttodate.label"), "financial"));
        metaColumnList.addMetaColumn(new MetaColumn("ActualCostToDate/Money/Currency", false, PropertyProvider.get("prm.project.properties.actualcosttodate.currency.label"), "financial"));
        metaColumnList.addMetaColumn(new MetaColumn("EstimatedROI/Money/Value", false, PropertyProvider.get("prm.project.properties.estimatedroi.label"), "financial"));
        metaColumnList.addMetaColumn(new MetaColumn("CostCenter", false, PropertyProvider.get("prm.project.properties.costcenter.label"), "financial"));
    }

    public MetaColumnList getMetaColumnList() {
        return metaColumnList;
    }

    /**
     * Populates the list of columns with all columns that are available for
     * display in a Personal Portfolio View.
     * After calling the finder column list is set.
     */
    private void populateColumnList() {

        FinderColumnList columnList = new FinderColumnList();

        // Name
        columnList.add(
                new FinderColumn(PersonalPortfolioIngredientsID.COLUMN_NAME.getID(),
                        PersonalPortfolioColumnDefinition.NAME));

        // Description
        columnList.add(
                new FinderColumn(PersonalPortfolioIngredientsID.COLUMN_DESCRIPTION.getID(),
                        PersonalPortfolioColumnDefinition.DESCRIPTION));

        // Overall Completion
        columnList.add(
                new FinderColumn(PersonalPortfolioIngredientsID.COLUMN_PERCENT_COMPLETE.getID(),
                        PersonalPortfolioColumnDefinition.PERCENT_COMPLETE));

        // Start Date
        columnList.add(
                new FinderColumn(PersonalPortfolioIngredientsID.COLUMN_START_DATE.getID(),
                        PersonalPortfolioColumnDefinition.START_DATE));

        // End Date
        columnList.add(
                new FinderColumn(PersonalPortfolioIngredientsID.COLUMN_END_DATE.getID(),
                        PersonalPortfolioColumnDefinition.END_DATE));

        // Status
        columnList.add(
                new FinderColumn(PersonalPortfolioIngredientsID.COLUMN_STATUS_CODE.getID(),
                        PersonalPortfolioColumnDefinition.STATUS_CODE));

        setFinderColumnList(columnList);
    }

    /**
     * Populates the list of filters with all filters that are available for
     * Personal Portfolio Views.
     * After calling, the finder filter list is set.
     * @param portfolio the ProjectPortfolio containing projects to use
     * to construct a SelectedProjectsFilter
     */
    private void populateFilterList(ProjectPortfolio portfolio) {


        try {

            //
            // First build the project properties filter list
            //
            FinderFilterList filterList = new FinderFilterList();

            // Project Visibility
            filterList.add(
                    new ProjectVisibilityFilter(PersonalPortfolioIngredientsID.FILTER_PROJECT_VISIBILITY.getID(),
                            "prm.portfolio.project.filter.visibilityfilter.name",
                            portfolio.getID(), portfolio.getUser().getID()));

            // Name
            filterList.add(
                    new TextFilter(PersonalPortfolioIngredientsID.FILTER_NAME.getID(),
                            PersonalPortfolioColumnDefinition.NAME,
                            false
                    ));

            // Project ID
            filterList.add(
                    new MetaTextFilter(PersonalPortfolioIngredientsID.FILTER_META_EXTERNAL_PROJECT_ID.getID(),
                            PersonalPortfolioColumnDefinition.META_EXTERNAL_PROJECT_ID,
                            false
                    ));

            // Description
            filterList.add(
                    new TextFilter(PersonalPortfolioIngredientsID.FILTER_DESCRIPTION.getID(),
                            PersonalPortfolioColumnDefinition.DESCRIPTION,
                            true
                    ));

            // Owning Business
            BusinessPortfolio businessPortfolio = new BusinessPortfolio();
            businessPortfolio.setUser(portfolio.getUser());

            filterList.add(
                    new OwnedBusinessFilter(PersonalPortfolioIngredientsID.FILTER_OWNING_BUSINESS.getID(),
                            PersonalPortfolioColumnDefinition.OWNING_BUSINESS,
                            true, businessPortfolio
                    ));

            // Subproject of
            ArrayList projectNames = new ArrayList();
            for (int i = 0; i < portfolio.size(); i++)
                projectNames.add(((IPortfolioEntry) portfolio.get(i)).getName());
            filterList.add(
                    new MetaSimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_SUBPROJECT_OF.getID(),
                            PersonalPortfolioColumnDefinition.SUBPROJECT_OF,
                            false,
                            (DomainOption[]) makeDomainOptionCollection(projectNames).toArray(new DomainOption[]{})
                    ));

            // Project Sponsor
            filterList.add(
                    new TextFilter(PersonalPortfolioIngredientsID.FILTER_PROJECT_SPONSOR.getID(),
                            PersonalPortfolioColumnDefinition.PROJECT_SPONSOR,
                            true
                    ));

            // Project Manager
            filterList.add(
                    new MetaTextFilter(PersonalPortfolioIngredientsID.FILTER_META_PROJECT_MANAGER.getID(),
                            PersonalPortfolioColumnDefinition.META_PROJECT_MANAGER,
                            false
                    ));

            // Program Manager
            filterList.add(
                    new MetaTextFilter(PersonalPortfolioIngredientsID.FILTER_META_PROGRAM_MANAGER.getID(),
                            PersonalPortfolioColumnDefinition.META_PROGRAM_MANAGER,
                            false
                    ));

            // Initiative
            filterList.add(
                    new MetaTextFilter(PersonalPortfolioIngredientsID.FILTER_META_INITIATIVE.getID(),
                            PersonalPortfolioColumnDefinition.META_INITIATIVE,
                            false
                    ));
            
            // To get project space meta combo properties   
            DomainListBean domainListBean = new DomainListBean();
            
            // Functional Area
            filterList.add(
                    new MetaSimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_META_FUNCTIONAL_AREA.getID(),
                            PersonalPortfolioColumnDefinition.META_FUNCTIONAL_AREA,
                            false,
                            (DomainOption[]) makeDomainOptionCollection(domainListBean.getDomainOptionListForProperty(Integer.valueOf(6), null)).toArray(new DomainOption[]{})
                    ));

            // Priority Code
            filterList.add(
                    new SimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_PRIORITY_CODE.getID(),
                            PersonalPortfolioColumnDefinition.PRIORITY_CODE,
                            true, (DomainOption[]) makeDomainOptionCollection(PriorityCode.getAllPriorityCodes()).toArray(new DomainOption[]{})
                    ));

            // Risk Rating Code
            filterList.add(
                    new SimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_RISK_RATING_CODE.getID(),
                            PersonalPortfolioColumnDefinition.RISK_RATING_CODE,
                            true, (DomainOption[]) makeDomainOptionCollection(RiskCode.getAllRiskCodes()).toArray(new DomainOption[]{})
                    ));

            // Project Charter
            filterList.add(
                    new MetaTextFilter(PersonalPortfolioIngredientsID.FILTER_META_PROJECT_CHARTER.getID(),
                            PersonalPortfolioColumnDefinition.META_PROJECT_CHARTER,
                            false
                    ));

            // Start Date
            filterList.add(
                    new DateFilter(PersonalPortfolioIngredientsID.FILTER_START_DATE.getID(),
                            PersonalPortfolioColumnDefinition.START_DATE,
                            true
                    ));

            // End Date
            filterList.add(
                    new DateFilter(PersonalPortfolioIngredientsID.FILTER_END_DATE.getID(),
                            PersonalPortfolioColumnDefinition.END_DATE,
                            true
                    ));

            // Overall Completion
            // percent complete is calculated after the project is loaded, so we should apply post-load filter
            filterList.add(
                    new PostLoadNumberFilter(PersonalPortfolioIngredientsID.FILTER_PERCENT_COMPLETE.getID(),
                            PersonalPortfolioColumnDefinition.PERCENT_COMPLETE,
                            false
                    ));

            // Status Code
            filterList.add(
                    new SimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_STATUS_CODE.getID(),
                            PersonalPortfolioColumnDefinition.STATUS_CODE,
                            false, (DomainOption[]) makeDomainOptionCollection(ProjectStatus.getAllProjectStatus()).toArray(new DomainOption[]{})
                    ));

            // Financial Status Color Code
            filterList.add(
                    new SimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_FINANCIAL_STATUS_COLOR_CODE.getID(),
                            PersonalPortfolioColumnDefinition.FINANCIAL_STATUS_COLOR_CODE,
                            true, (DomainOption[]) makeDomainOptionCollection(ColorCode.getAllColorCodes()).toArray(new DomainOption[]{})
                    ));

            // Scheduled Status Color Code
            filterList.add(
                    new SimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_SCHEDULE_STATUS_COLOR_CODE.getID(),
                            PersonalPortfolioColumnDefinition.SCHEDULE_STATUS_COLOR_CODE,
                            true, (DomainOption[]) makeDomainOptionCollection(ColorCode.getAllColorCodes()).toArray(new DomainOption[]{})
                    ));

            // Resource Status Color Code
            filterList.add(
                    new SimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_RESOURCE_STATUS_COLOR_CODE.getID(),
                            PersonalPortfolioColumnDefinition.RESOURCE_STATUS_COLOR_CODE,
                            true, (DomainOption[]) makeDomainOptionCollection(ColorCode.getAllColorCodes()).toArray(new DomainOption[]{})
                    ));

            // Current Status Description
            filterList.add(
                    new TextFilter(PersonalPortfolioIngredientsID.FILTER_CURRENT_STATUS_DESCRIPTION.getID(),
                            PersonalPortfolioColumnDefinition.CURRENT_STATUS_DESCRIPTION,
                            true
                    ));

            // Color Code
            filterList.add(
                    new SimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_COLOR_CODE.getID(),
                            PersonalPortfolioColumnDefinition.COLOR_CODE,
                            false, (DomainOption[]) makeDomainOptionCollection(ColorCode.getAllColorCodes()).toArray(new DomainOption[]{})
                    ));

            // Default Currency Code
            filterList.add(
                    new SimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_DEFAULT_CURRENCY_CODE.getID(),
                            PersonalPortfolioColumnDefinition.DEFAULT_CURRENCY_CODE,
                            false, MoneyFilter.makeDomainOptions(net.project.util.Currency.getAllCurrencyCodes()),
                            true
                    ));

            // Type Of Expense
            filterList.add(
                    new MetaSimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_META_TYPE_OF_EXPENSE.getID(),
                            PersonalPortfolioColumnDefinition.META_TYPE_OF_EXPENSE,
                            false,
                            (DomainOption[]) makeDomainOptionCollection(domainListBean.getDomainOptionListForProperty(Integer.valueOf(7), null)).toArray(new DomainOption[]{})
                    ));
            
            // Budgeted Total Cost
            filterList.add(
                    new MoneyFilter(PersonalPortfolioIngredientsID.FILTER_BUDGETED_TOTAL_COST.getID(),
                            PersonalPortfolioColumnDefinition.BUDGETED_TOTAL_COST_VALUE,
                            PersonalPortfolioColumnDefinition.BUDGETED_TOTAL_COST_CURRENCY,
                            net.project.util.Currency.getAllCurrencyCodes(), true
                    ));

            // Current Estimated Total Cost
            filterList.add(
                    new MoneyFilter(PersonalPortfolioIngredientsID.FILTER_CURRENT_ESTIMATED_TOTAL_COST.getID(),
                            PersonalPortfolioColumnDefinition.CURRENT_ESTIMATED_TOTAL_COST_VALUE,
                            PersonalPortfolioColumnDefinition.CURRENT_ESTIMATED_TOTAL_COST_CURRENCY,
                            net.project.util.Currency.getAllCurrencyCodes(), true
                    ));

            // Actual cost to date
            filterList.add(
                    new MoneyFilter(PersonalPortfolioIngredientsID.FILTER_ACTUAL_COST_TO_DATE.getID(),
                            PersonalPortfolioColumnDefinition.ACTUAL_COST_TO_DATE_VALUE,
                            PersonalPortfolioColumnDefinition.ACTUAL_COST_TO_DATE_CURRENCY,
                            net.project.util.Currency.getAllCurrencyCodes(), true
                    ));
            
            // Improvement Code
            filterList.add(
                    new SimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_IMPROVEMENT_CODE.getID(),
                            PersonalPortfolioColumnDefinition.IMPROVEMENT_CODE,
                            false, (DomainOption[]) makeDomainOptionCollection(ImprovementCode.getAllImprovementCodes()).toArray(new DomainOption[]{})
                    ));

            // Financial Status Improvement
            filterList.add(
                    new SimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_FINANCIAL_STATUS_IMPROVEMENT_CODE.getID(),
                            PersonalPortfolioColumnDefinition.FINANCIAL_STATUS_IMPROVEMENT,
                            false, (DomainOption[]) makeDomainOptionCollection(ImprovementCode.getAllImprovementCodes()).toArray(new DomainOption[]{})
                    ));

            // Schedule Status Improvement
            filterList.add(
                    new SimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_SCHEDULE_STATUS_IMPROVEMENT_CODE.getID(),
                            PersonalPortfolioColumnDefinition.SCHEDULE_STATUS_IMPROVEMENT,
                            false, (DomainOption[]) makeDomainOptionCollection(ImprovementCode.getAllImprovementCodes()).toArray(new DomainOption[]{})
                    ));

            // Resource Status Improvement
            filterList.add(
                    new SimpleDomainFilter(PersonalPortfolioIngredientsID.FILTER_RESOURCE_STATUS_IMPROVEMENT_CODE.getID(),
                            PersonalPortfolioColumnDefinition.RESOURCE_STATUS_IMPROVEMENT,
                            false, (DomainOption[]) makeDomainOptionCollection(ImprovementCode.getAllImprovementCodes()).toArray(new DomainOption[]{})
                    ));

            // Estimated ROI
            filterList.add(
                    new MoneyFilter(PersonalPortfolioIngredientsID.FILTER_ESIMATED_ROI.getID(),
                            PersonalPortfolioColumnDefinition.ESTIMATED_ROI_VALUE,
                            PersonalPortfolioColumnDefinition.ESTIMATED_ROI_CURRENCY,
                            net.project.util.Currency.getAllCurrencyCodes(), true
                    ));

            // Cost Center
            filterList.add(
                    new TextFilter(PersonalPortfolioIngredientsID.FILTER_COST_CENTER.getID(),
                            PersonalPortfolioColumnDefinition.COST_CENTER,
                            true
                    ));
            
            this.projectPropertiesFilterList = filterList;

            // Now make the SelectedProjectsFilter
            this.selectedProjectsFilter = new SelectedProjectsFilter(PersonalPortfolioIngredientsID.FILTER_SELECTED_PROJECTS.getID(),
                    PersonalPortfolioColumnDefinition.PROJECT_ID,
                    portfolio
                    );
            this.selectedProjectsFilter.setSelected(true);

            //
            // Now build the entire filter list from the Project Properties
            // Filter List and the Selected Projects filter
            // These are OR'd together
            FinderFilterList allFilters = new FinderFilterList(FilterOperator.OR);
            allFilters.add(this.projectPropertiesFilterList);
            allFilters.add(this.selectedProjectsFilter);
            setFinderFilterList(allFilters);

        } catch (DuplicateFilterIDException e) {
            throw new PnetRuntimeException("Unexpected programmer error found while "+
                "constructing the Personal Portfolio View filters: "+ e, e) {
            };

        }

    }

    /**
     * Constructs a DomainOption collection from collections containing various
     * kinds of objects.
     * @param collection the collection of elements from which to construct
     * DomainOptions
     * @return the collection of DomainOptions
     * @throws IllegalStateException if the element type is not handled
     */
    private Collection makeDomainOptionCollection(Collection collection) {
        List domainOptionList = new ArrayList();

        for (Iterator it = collection.iterator(); it.hasNext(); ) {
            Object nextObject = it.next();
            DomainOption option = null;

            if (nextObject instanceof ColorCode) {
                ColorCode colorCode = (ColorCode) nextObject;
                option = new DomainOption(colorCode.getID(), colorCode.getNameToken());

            } else if (nextObject instanceof ImprovementCode) {
                ImprovementCode improvementCode = (ImprovementCode) nextObject;
                option = new DomainOption(improvementCode.getID(), improvementCode.getNameToken());

            } else if (nextObject instanceof PriorityCode) {
                PriorityCode priorityCode = (PriorityCode) nextObject;
                option = new DomainOption(priorityCode.getID(), priorityCode.getNameToken());

            } else if (nextObject instanceof RiskCode) {
                RiskCode riskCode = (RiskCode) nextObject;
                option = new DomainOption(riskCode.getID(), riskCode.getNameToken());

            } else if (nextObject instanceof ProjectStatus) {
                ProjectStatus status = (ProjectStatus) nextObject;
                option = new DomainOption(status.getID(), status.getNameToken());

            } else if (nextObject instanceof BusinessSpace) {
                BusinessSpace space = (BusinessSpace) nextObject;
                option = new DomainOption(space.getID(), space.getName(), true);

            } else if (nextObject instanceof net.project.util.Currency) {
                BusinessSpace space = (BusinessSpace) nextObject;
                option = new DomainOption(space.getID(), space.getName(), true);

            } else if (nextObject instanceof String) {
                String str = (String) nextObject;
                option = new DomainOption(str, str);

            } else {
                throw new IllegalStateException("Cannot construct DomainObject from element");
            }

            domainOptionList.add(option);
        }

        return Collections.unmodifiableList(domainOptionList);
    }

    /**
     * Create the list of <code>FinderGrouping</code> classes that will be used
     * on the HTML page to allow the user to select task grouping.
     */
    private void populateGroupingList() {
        FinderGroupingList groupingList = new FinderGroupingList();
        setFinderGroupingList(groupingList);
    }

    /**
     * Populate the list of sorters with all sorters that this report supports.
     */
    private void populateSorterList() {

        FinderSorterList sorterList = new FinderSorterList();

        // Define the columns that may be sorted on
        ColumnDefinition[] sortableColumns = (ColumnDefinition[]) PersonalPortfolioColumnDefinition.getSortableColumnDefinitions().toArray(new ColumnDefinition[]{});

        // Define three sorters
        // Each sorter permits the selection of the same columns
        sorterList.add(new FinderSorter(PersonalPortfolioIngredientsID.SORTER_1.getID(), sortableColumns, PersonalPortfolioColumnDefinition.NAME));
        sorterList.add(new FinderSorter(PersonalPortfolioIngredientsID.SORTER_2.getID(), sortableColumns, PersonalPortfolioColumnDefinition.NAME));
        sorterList.add(new FinderSorter(PersonalPortfolioIngredientsID.SORTER_3.getID(), sortableColumns, PersonalPortfolioColumnDefinition.NAME));

        setFinderSorterList(sorterList);

    }

    public static class MetaTextFilter extends TextFilter { // post-load filter
        public MetaTextFilter(String id, ColumnDefinition columnDefinition, boolean isIncludeEmptyOption) {
            super(id, columnDefinition, isIncludeEmptyOption);
        }

        public String getWhereClause() {
            return "";
        }

        /**
         *
         * @param s string to check
         * @return true if s matches the filter, false otherwise
         */
        public boolean matches(String s) {
            if (s == null || "".equals(s)) {
                return isEmptyOptionSelected();
            }
            if (TextComparator.CONTAINS.equals(getComparator())) {
                if (s.indexOf(getValue()) >= 0) return true;
            } else if (TextComparator.EQUALS.equals(getComparator())) {
                if (s.equals(getValue())) return true;
            } else if (TextComparator.NOT_EQUAL.equals(getComparator())) {
                if (!s.equals(getValue())) return true;
            }
            return false;
        }

    }

    public static class MetaSimpleDomainFilter extends SimpleDomainFilter { // post-load filter
        public MetaSimpleDomainFilter(String id, ColumnDefinition columnDefinition, boolean isIncludeEmptyOption, DomainOption[] options) {
            super(id, columnDefinition, isIncludeEmptyOption, options);
        }

        public String getWhereClause() {
            return "";
        }

        /**
         *
         * @param s string to check
         * @return true if s matches the filter, false otherwise
         */
        public boolean matches(String s) {
            if (s == null || "".equals(s)) {
                return isEmptyOptionSelected();
            }
            boolean equals = this.getComparator().getID().equals(DomainComparator.EQUALS.getID());
            for (Object optionObject : getSelectedDomainOptions()) {
                DomainOption domainOption = (DomainOption) optionObject;
                if (s.equals(domainOption.getValue())) return equals;
            }
            return !equals;
        }
    }

    public static class PostLoadNumberFilter extends NumberFilter {
        public PostLoadNumberFilter(String id, ColumnDefinition columnDef, boolean isIncludeEmptyOption) {
            super(id, columnDef, isIncludeEmptyOption);
        }

        public String getWhereClause() {
            return "";
        }

        /**
         *
         * @param s string to check
         * @return true if s matches the filter, false otherwise
         */
        public boolean matches(String s) {
            if (s == null || "".equals(s)) {
                return isEmptyOptionSelected();
            }
            double passedNumber = 0;
            double myNumber = 0;
            try {
                myNumber = getNumber().doubleValue();
                passedNumber = Double.parseDouble(s);
            } catch (Exception ignored) {}
            if (NumberComparator.EQUALS.equals(getComparator())) {
                if (passedNumber == myNumber) return true;
            } else if (NumberComparator.NOT_EQUAL.equals(getComparator())) {
                if (passedNumber != myNumber) return true;
            } else if (NumberComparator.LESS_THAN.equals(getComparator())) {
                if (passedNumber < myNumber) return true;
            } else if (NumberComparator.GREATER_THAN.equals(getComparator())) {
                if (passedNumber > myNumber) return true;
            }
            return false;
        }
    }
    /**
     * To set finder sorter list 
     * @param finderSorterList
     */
    public void setSorterList(FinderSorterList finderSorterList){
    	setFinderSorterList(finderSorterList);
    }
}

