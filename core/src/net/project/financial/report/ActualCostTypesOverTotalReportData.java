package net.project.financial.report;

import java.sql.SQLException;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.EmptyFinderFilter;
import net.project.base.finder.EmptyFinderGrouping;
import net.project.base.finder.FinderGrouping;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.RadioButtonFilter;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnSpaceHasSpace;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.project.ProjectSpaceFinder;
import net.project.report.SummaryDetailReportData;

public class ActualCostTypesOverTotalReportData extends SummaryDetailReportData {

	/** Token for the label of the "Default Grouping" grouper. */
	private String DEFAULT_GROUPING = PropertyProvider.get("prm.schedule.report.latetaskreport.grouping.default.name");

	/** Token for the label of the "All Business Projects" filter. */
	private String ALL_BUSINESS_PROJECTS = PropertyProvider.get("prm.financial.report.actualcosttypesovertotalreport.showallprojects.name");

	/**
	 * Variable to contain all of the data that will be used to construct the
	 * summary section of the Actual Cost Types Over Total Report.
	 */
	private ActualCostTypesOverTotalReportSummaryData summaryData;

	/**
	 * Token pointing to: "Unexpected programmer error found while constructing
	 * the list of report filters."
	 */
	private String FILTER_LIST_CONSTRUCTION_ERROR = "prm.report.errors.filterlistcreationerror.message";

	/**
	 * Standard constructor.
	 */
	public ActualCostTypesOverTotalReportData() {
		populateFinderFilterList();
		populateFinderSorterList();
		populateFinderGroupingList();
	}

	/**
	 * Create the list of <code>FinderGrouping</code> classes that will be used
	 * on the HTML page to allow the user to select task grouping.
	 */
	private void populateFinderGroupingList() {
		FinderGrouping defaultGrouper = new EmptyFinderGrouping("10", DEFAULT_GROUPING, true);
		groupingList.add(defaultGrouper);
	}

	/**
	 * Populate the list of sorters with all sorters that this report supports.
	 */
	private void populateFinderSorterList() {
		for (int i = 1; i < 5; i++) {
			FinderSorter fs = new FinderSorter(String.valueOf(i * 10), new ColumnDefinition[] { ProjectSpaceFinder.NAME_COLUMN,
					ProjectSpaceFinder.STATUS_COLUMN, ProjectSpaceFinder.DATE_START_COLUMN, ProjectSpaceFinder.DATE_FINISH_COLUMN },
					ProjectSpaceFinder.NAME_COLUMN);
			sorterList.add(fs);
		}
	}

	/**
	 * Populate the list of filters with all filters that are available for this
	 * report.
	 */
	private void populateFinderFilterList() {
		try {
			RadioButtonFilter rbf = new RadioButtonFilter("10");
			EmptyFinderFilter eff = new EmptyFinderFilter("20", ALL_BUSINESS_PROJECTS);
			eff.setSelected(true);
			rbf.add(eff);
			filterList.add(rbf);
		} catch (DuplicateFilterIDException e) {
			throw new RuntimeException(PropertyProvider.get(FILTER_LIST_CONSTRUCTION_ERROR), e);
		}
	}

	@Override
	public void load() throws PersistenceException, SQLException {
		ProjectSpaceFinder pf = new ProjectSpaceFinder();
		pf.addFinderFilter(getFilterList());
		pf.addFinderSorterList(getSorterList());

		// The spaceId is from the financial space, we have to obtain the
		// business spaceId.
		PnSpaceHasSpace relationship = ServiceFactory.getInstance().getPnSpaceHasSpaceService().getBusinessRelatedSpace(getSpaceID());

		// Load the projects for the business.
		detailedData = pf.findProjectSpacesByBusinessID(String.valueOf(relationship.getComp_id().getParentSpaceId()));
		summaryData = calculateTotalSummary();

	}

	public ActualCostTypesOverTotalReportSummaryData calculateTotalSummary() {
		ActualCostTypesOverTotalReportSummaryData summary = new ActualCostTypesOverTotalReportSummaryData();
		summary.setTotalProjects(getDetailedData().size());

		float materialTotalActualCostToDate = 0;
		float resourcesTotalActualCostToDate = 0;
		float discretionalTotalActualCostToDate = 0;

		for (Object project : getDetailedData()) {
			ProjectSpace projectSpace = (ProjectSpace) project;
			materialTotalActualCostToDate = materialTotalActualCostToDate + projectSpace.getMaterialsActualCostToDate().getValue().floatValue();
			resourcesTotalActualCostToDate = resourcesTotalActualCostToDate + projectSpace.getResourcesActualCostToDate().getValue().floatValue();
			discretionalTotalActualCostToDate = discretionalTotalActualCostToDate + projectSpace.getDiscretionalActualCostToDate().getValue().floatValue();
		}
		summary.setMaterialsTotalActualCostToDate(materialTotalActualCostToDate);
		summary.setResourcesTotalActualCostToDate(resourcesTotalActualCostToDate);
		summary.setDiscretionalTotalActualCostToDate(discretionalTotalActualCostToDate);
		return summary;
	}

	@Override
	public void clear() {
		this.summaryData = null;
		this.detailedData = null;

	}

	public ActualCostTypesOverTotalReportSummaryData getSummaryData() {
		return this.summaryData;
	}
}
