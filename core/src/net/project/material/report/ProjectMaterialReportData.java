package net.project.material.report;

import java.sql.SQLException;
import java.util.List;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.EmptyFinderFilter;
import net.project.base.finder.EmptyFinderGrouping;
import net.project.base.finder.FinderGrouping;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.RadioButtonFilter;
import net.project.base.property.PropertyProvider;
import net.project.material.MaterialBean;
import net.project.material.MaterialFinder;
import net.project.persistence.PersistenceException;
import net.project.report.SummaryDetailReportData;
import net.project.schedule.report.TasksAssignedToMeFilter;
import net.project.schedule.report.UserFilter;
import net.project.security.SessionManager;

public class ProjectMaterialReportData extends SummaryDetailReportData {
	
    /** Token for the label of the "Default Grouping" grouper. */
    private String DEFAULT_GROUPING = PropertyProvider.get("prm.schedule.report.latetaskreport.grouping.default.name");
    
    /** Token for the label of the "All Materials" filter. */
    private String ALL_MATERIALS = PropertyProvider.get("prm.schedule.report.projectmaterialreport.showallmaterials.name");
    

	/**
	 * Variable to contain all of the data that will be used to construct the
	 * summary section of the Project Materials Report.
	 */
	private ProjectMaterialReportSummaryData summaryData;
	
    /**
     * Token pointing to: "Unexpected programmer error found while constructing
     * the list of report filters."
     */
    private String FILTER_LIST_CONSTRUCTION_ERROR = "prm.report.errors.filterlistcreationerror.message";
	
	
    /**
     * Standard constructor.
     */
    public ProjectMaterialReportData() {
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
        FinderGrouping typeGrouper = new MaterialTypeGrouping("20", false);
        groupingList.add(defaultGrouper);
        groupingList.add(typeGrouper);
    }
    
    /**
     * Populate the list of sorters with all sorters that this report supports.
     */
    private void populateFinderSorterList() {
        for (int i = 1; i < 4; i++) {
            FinderSorter fs = new FinderSorter(String.valueOf(i * 10),
                new ColumnDefinition[]{MaterialFinder.NAME_COLUMN, MaterialFinder.TYPE_NAME_COLUMN,
            						   MaterialFinder.COST_COLUMN},
            	MaterialFinder.NAME_COLUMN);
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
            EmptyFinderFilter eff = new EmptyFinderFilter("20", ALL_MATERIALS);
            eff.setSelected(true);
            rbf.add(eff);
            rbf.add(new MaterialConsumableFilter("30"));
            rbf.add(new MaterialNotConsumableFilter("40"));
            filterList.add(rbf);
        } catch (DuplicateFilterIDException e) {
            throw new RuntimeException(
                PropertyProvider.get(FILTER_LIST_CONSTRUCTION_ERROR), e);
        }
    }

	@Override
	public void load() throws PersistenceException, SQLException {
		MaterialFinder mf = new MaterialFinder();
		mf.addFinderFilter(getFilterList());
		mf.addFinderSorterList(getSorterList());
//        List grouping = this.getGroupingList().getSelectedGroupings();
//        FinderGrouping selectedGroup = null;
//        if(null!=grouping && grouping.size()>0){
//        	selectedGroup = (FinderGrouping) grouping.get(0);
//        }
        detailedData = mf.findBySpaceId(getSpaceID());
        summaryData = calculateTotalSummary();			

	}
	
	public ProjectMaterialReportSummaryData calculateTotalSummary(){
		ProjectMaterialReportSummaryData summary = new ProjectMaterialReportSummaryData();
		summary.setTotalMaterials(getDetailedData().size());
		float totalCost=0;
		for(Object material : getDetailedData()){
			totalCost += Float.valueOf(((MaterialBean) material).getCost());
		}
		summary.setTotalCost(totalCost);
		return summary;		
	}

	@Override
	public void clear() {
		this.summaryData = null;
		this.detailedData = null;

	}

	public ProjectMaterialReportSummaryData getSummaryData() {
		return this.summaryData;
	}

}
