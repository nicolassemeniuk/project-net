package net.project.business.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.util.NumberFormat;
import net.project.util.ParseString;
import net.project.util.StringUtils;

import org.apache.commons.collections.CollectionUtils;

/**
 *
 */
public class AssignmentList {
	
	private String projectName;
	
	private String taskName;
	
	private String monthlyTotal;
	
	private List<TimeSubmitalWeek> workHourList;
	
	private Double[] dailyTotal;
	
	private String memberName;
	
	private Integer spaceId;
	
	private Integer memberId;
	
	private List<TimeSubmitalWeek> resourceSubTotal;
	
	private String resourceTotal;
	
	private String projectResource;
	
	private List<TimeSubmitalWeek> projectSubTotalList;
	
	private String monthlyProjectTotal;
	
	private Integer objectId;
	
	private String collapsedProjectName;
	
	private String[] summaryFields;
	
	private String assignmentType;
	
	private String CSVPersonName;
	
	private String CSVProjectName;
	
	private List<TimeSubmitalWeek> weekList;
	
	private String memberVisible;
	
	private String projectVisible;
	
	private String assignmentVisible;
	
	private String hiddenResourceVisible;
	
	private String hiddenProjectVisible;
	
	private String contextUrl;
	
	public AssignmentList(String projectName, String taskName, List<TimeSubmitalWeek> workHourList, String monthlyTotal, String memberName, Integer spaceId, Integer memberId, Double[] resourceSubTotal, String resourceTotal, String projectResource, Double[] projectSubTotal, String monthlyProjectTotal, Integer objectId, String collapsedProjectName, String assignmentType, String CSVPersonName, String CSVProjectName, List<TimeSubmitalWeek> weekList, String memberVisible, String projectVisible, String assignmentVisible, String hiddenResourceVisible, String hiddenProjectVisible, String contextUrl){
		this.projectName = projectName;
		this.taskName = taskName;
		this.workHourList = workHourList;
		if(monthlyTotal != null){
			this.monthlyTotal = monthlyTotal.equals("0") ? " " : monthlyTotal;
		} else {
			this.monthlyTotal = monthlyTotal;
		}
		this.memberName = memberName;
		this.spaceId = spaceId;
		this.memberId = memberId;
		this.resourceSubTotal = convertDoubleToString(resourceSubTotal, weekList);
		if(resourceTotal != null){
			this.resourceTotal = resourceTotal.equals("0") ? " " : resourceTotal;
		} else {
			this.resourceTotal = resourceTotal;
		}
		this.projectResource = projectResource;
		this.projectSubTotalList = convertDoubleToString(projectSubTotal, weekList);
		if(monthlyProjectTotal != null){
			this.monthlyProjectTotal = monthlyProjectTotal.equals("0") ? " " : monthlyProjectTotal;
		} else {
			this.monthlyProjectTotal = monthlyProjectTotal;
		}
		this.objectId = objectId;
		this.collapsedProjectName = collapsedProjectName;
		this.assignmentType = assignmentType;
		this.CSVPersonName = CSVPersonName;
		this.CSVProjectName = CSVProjectName;
		this.weekList = weekList;
		this.memberVisible = memberVisible;
		this.projectVisible = projectVisible;
		this.assignmentVisible = assignmentVisible;
		this.hiddenResourceVisible = hiddenResourceVisible;
		this.hiddenProjectVisible = hiddenProjectVisible;
		this.contextUrl = contextUrl;
	}
	
	public AssignmentList(){
		
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @return the workHourList
	 */
	public List<TimeSubmitalWeek> getWorkHourList() {
		return workHourList;
	}

	/**
	 * @return the monthlyTotal
	 */
	public String getMonthlyTotal() {
		return monthlyTotal;
	}

	/**
	 * @return the grantTotal
	 */
	public Double[] getDailyTotal() {
		return dailyTotal;
	}

	/**
	 * @return the memberName
	 */
	public String getMemberName() {
		return memberName;
	}

	/**
	 * @return the memberId
	 */
	public Integer getMemberId() {
		return memberId;
	}

	/**
	 * @return the spaceId
	 */
	public Integer getSpaceId() {
		return spaceId;
	}

	/**
	 * @return the resourceTotal
	 */
	public String getResourceTotal() {
		return resourceTotal;
	}

	/**
	 * @return the projectResource
	 */
	public String getProjectResource() {
		return projectResource;
	}

	/**
	 * Function to take only two decimal digits using number
	 * @param resourceSubTotal
	 * @return string list of resource work capture
	 */
	public static List<TimeSubmitalWeek> convertDoubleToString(Double[] resourceSubTotal, List<TimeSubmitalWeek> weekList){
		List<TimeSubmitalWeek> resourceTotalList = new ArrayList<TimeSubmitalWeek>();
		if(resourceSubTotal != null){
			int index = 0;
			for (TimeSubmitalWeek week : weekList){
				
				TimeSubmitalWeek tempWeek = new TimeSubmitalWeek();
				if(resourceSubTotal[index] != null){
					if(week != null){
						String workHour = NumberFormat.getInstance().formatNumber(resourceSubTotal[index], 0, 2);
						tempWeek.setWork(workHour.equals("0") ? " " : workHour);
						tempWeek.setEndOfweek(false);
						index++;
						resourceTotalList.add(tempWeek);
					} else {
						String workHour = NumberFormat.getInstance().formatNumber(resourceSubTotal[index], 0, 2);
						tempWeek.setWeeklyTotal(workHour.equals("0") ? " " : workHour);
						tempWeek.setEndOfweek(true);
						index++;
						resourceTotalList.add(tempWeek);
					}
				}
			}
			return resourceTotalList;
		} else {
			return null;
		}
	}

	/**
	 * @return the monthlyProjectTotal
	 */
	public String getMonthlyProjectTotal() {
		return monthlyProjectTotal;
	}

	/**
	 * @return the objectId
	 */
	public Integer getObjectId() {
		return objectId;
	}

	/**
	 * @return the collapsedProject
	 */
	public String getCollapsedProjectName() {
		return collapsedProjectName;
	}

	/**
     * Get a comma separated data file for this assignment list.  The CSV String
     * returned is compatable with microsoft excel and other tools that can
     * import CSV files.
     */
    public String getTimeSummaryCSV(List<AssignmentList> assignmentList) {
      
        StringBuffer csv = new StringBuffer(200);
        String[] fieldLabelTokens = getSummaryFieldToken();
        csv.append("\""+ PropertyProvider.get(fieldLabelTokens[0])+"\"");
        for (int index = 1; index < fieldLabelTokens.length; index++) {
            csv.append(",\"" + PropertyProvider.get(fieldLabelTokens[index]) + "\"");
        }
        csv.append("\r\n");

        for(AssignmentList assignment: assignmentList){
        	if(StringUtils.isNotEmpty(assignment.getTaskName())){
	        	List<TimeSubmitalWeek> workHourList = assignment.getWorkHourList();
	        	if(CollectionUtils.isNotEmpty(workHourList)){
		        	for(TimeSubmitalWeek work : workHourList){
		        		if(StringUtils.isNotEmpty(work.getWork()) && !(work.getWork().equals(" "))){
		        			
		    				//Set the CSV column data by comma separated list
		    				csv.append("\"" +formatFieldDataCSV(assignment.getCSVPersonName())+ "\"");
		        			csv.append(",\"" +formatFieldDataCSV(assignment.getCSVProjectName())+"\"");
		        			csv.append(",\"" +formatFieldDataCSV(assignment.getAssignmentType())+"\"");
		        			csv.append(",\"" +formatFieldDataCSV(assignment.getTaskName())+"\"");
		        			csv.append(",\"" +formatFieldDataCSV(new SimpleDateFormat("MMM,dd yyyy").format(work.getDateValue()))+"\"");
		        			csv.append(",\"" +formatFieldDataCSV(work.getWork())+"hr\"");
		        			csv.append("\r\n");
		        		}
		        	}
	        	}	
        	}
        }
        return csv.toString();
    }
    
	/**
	 * @return the summaryFields
	 */
	private static String[] getSummaryFieldToken() {
		String[] totalSummaryField = {"prm.timesummary.exportcsv.personname.label","prm.timesummary.exportcsv.projectname.label","prm.timesummary.exportcsv.assignmenttype.label","prm.timesummary.exportcsv.assignmentname.label","prm.timesummary.exportcsv.captureddate.label","prm.timesummary.exportcsv.workhour.label"};
		return totalSummaryField;
	}

	/**
	 * @return the assignmentType
	 */
	public String getAssignmentType() {
		return assignmentType;
	}
	
	/**
	 * @return the cSVPersonName
	 */
	public String getCSVPersonName() {
		return CSVPersonName;
	}

	/**
	 * @return the cSVProjectName
	 */
	public String getCSVProjectName() {
		return CSVProjectName;
	}

	/**
     * Formats the field data and returns it in a assignment suitable for using
     * in a CSV file.
     * @param fieldData the field data to be formatted and returned.
     * @return a comma separated list representation of the field_data formatted correctly for this type of field.
     */
    private String formatFieldDataCSV(String assignmentData) {
        if (assignmentData != null)
            return (ParseString.escapeDoubleQuotes(assignmentData));
        else
            return "";
    }

	/**
	 * @return the weekList
	 */
	public List<TimeSubmitalWeek> getWeekList() {
		return weekList;
	}

	/**
	 * @return the projectSubTotalList
	 */
	public List<TimeSubmitalWeek> getProjectSubTotalList() {
		return projectSubTotalList;
	}

	/**
	 * @return the resourceSubTotal
	 */
	public List<TimeSubmitalWeek> getResourceSubTotal() {
		return resourceSubTotal;
	}

	/**
	 * @return the assignmentVisible
	 */
	public String getAssignmentVisible() {
		return assignmentVisible;
	}

	/**
	 * @return the memberVisible
	 */
	public String getMemberVisible() {
		return memberVisible;
	}

	/**
	 * @return the projectVisible
	 */
	public String getProjectVisible() {
		return projectVisible;
	}

	/**
	 * @return the hiddenProjectVisible
	 */
	public String getHiddenProjectVisible() {
		return hiddenProjectVisible;
	}

	/**
	 * @return the contextUrl
	 */
	public String getContextUrl() {
		return contextUrl;
	}

	/**
	 * @param contextUrl the contextUrl to set
	 */
	public void setContextUrl(String contextUrl) {
		this.contextUrl = contextUrl;
	}

	/**
	 * @return the hiddenResourceVisible
	 */
	public String getHiddenResourceVisible() {
		return hiddenResourceVisible;
	}
}
