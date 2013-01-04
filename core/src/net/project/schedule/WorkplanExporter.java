package net.project.schedule;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.project.NoSuchPropertyException;
import net.project.project.ProjectSpace;
import net.project.schedule.ScheduleColumn;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryFactory;
import net.project.schedule.TaskType;
import net.project.util.HTMLUtils;
import net.project.util.NumberFormat;
import net.project.util.ParseString;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Exports workplan for specified schedule and project space
 * into CSV or Excel format
 */
public class WorkplanExporter {
	
	/**
	 * Creates comma separated values string of specified schedule 
	 * and project space
	 * 
	 * @param schedule schedule that needs to exported in CSV format
	 * @param projectSpace project space to set
	 * @return comma separated values string for the schedule.
	 */
	public static String createWorkplanCSV(List<ScheduleEntry> scheduleEntriesList, ProjectSpace projectSpace){
	
		StringBuffer csvWorkplan = new StringBuffer();
		
		// Create task list
		if(CollectionUtils.isNotEmpty(scheduleEntriesList)) {
			try {
				for(ScheduleEntry scheduleEntry : scheduleEntriesList){
					// Business ID and Business name
					csvWorkplan.append("\"" + projectSpace.getParentBusinessID() + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getParentBusinessName()) + "\"");
					
					csvWorkplan.append(",\"" + projectSpace.getID() + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getName()) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getDescription() != null ? projectSpace.getDescription() : StringUtils.EMPTY) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getMetaData().getProperty("ProjectManager")) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getMetaData().getProperty("ProgramManager")) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getMetaData().getProperty("ExternalProjectID")) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getSponsor() != null ? projectSpace.getSponsor() : StringUtils.EMPTY) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getMetaData().getProperty("Initiative")) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getMetaData().getProperty("FunctionalArea")) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getPriorityCode() != null ? projectSpace.getPriorityCode().getName() : StringUtils.EMPTY) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getMetaData().getProperty("ProjectCharter")) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getMetaData().getProperty("TypeOfExpense")) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getCostCenter() != null ? projectSpace.getCostCenter() : StringUtils.EMPTY) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(projectSpace.getStatus() != null ? projectSpace.getStatus() : StringUtils.EMPTY) + "\"");
					
					// Task Id and task information
					csvWorkplan.append(",\"" + scheduleEntry.getID() + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.getName()) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.isCriticalPath() ? "Y" : "N") + "\"");
					if(scheduleEntry.dateCreated != null){
						csvWorkplan.append(",\"" + formatFieldDataCSV(new SimpleDateFormat("MM/dd/yyyy").format(scheduleEntry.dateCreated)) + "\"");
					} else {
						csvWorkplan.append(",\"" + formatFieldDataCSV("") + "\"");
					}
					csvWorkplan.append(",\"" + formatFieldDataCSV(new SimpleDateFormat("MM/dd/yyyy").format(scheduleEntry.getStartTime())) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(new SimpleDateFormat("MM/dd/yyyy").format(scheduleEntry.getEndTime())) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.getDuration()) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.getWorkPercentComplete()) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.getWork()) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV("") + "\"");
					if(scheduleEntry.getActualStartTime() != null) {
						csvWorkplan.append(",\"" + formatFieldDataCSV(new SimpleDateFormat("MM/dd/yyyy").format(scheduleEntry.getActualStartTime())) + "\"");
					} else {
						csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.getActualStartDateString()) + "\"");
					}
					if(scheduleEntry.getActualEndTime() != null){
						csvWorkplan.append(",\"" + formatFieldDataCSV(new SimpleDateFormat("MM/dd/yyyy").format(scheduleEntry.getActualEndTime())) + "\"");
					} else {
						csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.getActualEndDateString()) + "\"");
					}
					csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.getBaselineDurationString()) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV("") + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV("") + "\"");
					if(scheduleEntry.getBaselineStart() != null){
						csvWorkplan.append(",\"" + formatFieldDataCSV(new SimpleDateFormat("MM/dd/yyyy").format(scheduleEntry.getBaselineStart())) + "\"");
					} else {
						csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.getBaselineStartDateString()) + "\"");
					}
					if(scheduleEntry.getBaselineEnd() != null){
						csvWorkplan.append(",\"" + formatFieldDataCSV(new SimpleDateFormat("MM/dd/yyyy").format(scheduleEntry.getBaselineEnd())) + "\"");
					} else {
						csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.getBaselineEndDateString()) + "\"");
					}
					csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.isMilestone() ? "Y" : "N") + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.getWorkComplete()) + "\"");
					csvWorkplan.append(",\"" + formatFieldDataCSV(scheduleEntry.getPriorityString()) + "\"");
					
					csvWorkplan.append("\r\n");
				}
			}catch (NoSuchPropertyException e) {
				Logger.getLogger(WorkplanExporter.class).error("project space propery does not exist...."+e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				Logger.getLogger(WorkplanExporter.class).error("WorkplanExporter.createWorkplanCSV() failed...."+e.getMessage());
			}
		}
		return csvWorkplan.toString();
	}
	
	/**
     * Formats the field data and returns it in a assignment suitable for using
     * in a CSV file.
     * @param fieldData the field data to be formatted and returned.
     * @return a comma separated list representation of the field_data formatted correctly for this type of field.
     */
    private static String formatFieldDataCSV(String assignmentData) {
        if (assignmentData != null)
            return (ParseString.escapeDoubleQuotes(assignmentData));
        else
            return "";
    }
    
    /**
     *  Create Excel sheet for specified schedule and project 
     *  in given excel workbook
     *   
     * @param schedule
     * @param projectSpace
     * @param wb
     */
    public static void createExcelSheet(List<ScheduleEntry> scheduleEntriesList, ProjectSpace projectSpace, HSSFWorkbook workBook){
    	HSSFSheet sheet = null;
    	// Give sheet name
    	try{
    		sheet = workBook.createSheet(projectSpace.getID());
    	} catch (Exception e) {
    		sheet = workBook.createSheet();
		}
		
		// Top Heading row 
		HSSFRow rowHeader = sheet.createRow((short) 0);
		HSSFCellStyle cellStyle = workBook.createCellStyle();
		
		HSSFCell cellHeader = rowHeader.createCell((short) 5);
		cellHeader.setCellValue(new HSSFRichTextString(PropertyProvider.get("Project :") + projectSpace.getName()));
		
		// Top Column heading row
		HSSFRow columnHeaderRow = sheet.createRow((short) 3);
		
		// Get all the tokens and create header
		String[] exportToolTokens = getExportToolTokens();
		for(int colIndex = 0; colIndex < exportToolTokens.length; colIndex++){
			HSSFCell headerColumns = columnHeaderRow.createCell((short) colIndex);
			headerColumns.setCellValue(new HSSFRichTextString(PropertyProvider.get(exportToolTokens[colIndex])));
		}

		int rowIndex = 3;
		for(ScheduleEntry scheduleEntry : scheduleEntriesList){
			
			HSSFRow columnvaluesRow = sheet.createRow((short) ++rowIndex);
			
			// Business Id
			HSSFCell businessIdValue = columnvaluesRow.createCell((short)0);
			businessIdValue.setCellValue(Double.valueOf(projectSpace.getParentBusinessID()));
			businessIdValue = null;
			
			// Business Name
			HSSFCell businessNameValue = columnvaluesRow.createCell((short)1);
			businessNameValue.setCellValue(new HSSFRichTextString(projectSpace.getParentBusinessName()));
			businessIdValue = null;
			
			// Project ID
			HSSFCell projectIdValue = columnvaluesRow.createCell((short)2);
			projectIdValue.setCellValue(Double.valueOf(projectSpace.getID()));
			projectIdValue = null;
			
			// Project Name
			HSSFCell projectNameValue = columnvaluesRow.createCell((short)3);
			projectNameValue.setCellValue(new HSSFRichTextString(projectSpace.getName()));
			
			// Project Description
			HSSFCell projectDescValue = columnvaluesRow.createCell((short)4);
			projectDescValue.setCellValue(new HSSFRichTextString(projectSpace.getDescription()));
			
			try {
				// Project Manager value
				HSSFCell projectManagerValue = columnvaluesRow.createCell((short)5);
				projectManagerValue.setCellValue(new HSSFRichTextString(projectSpace.getMetaData().getProperty("ProjectManager")));
				
				// Programe Manager value
				HSSFCell programeManagerValue = columnvaluesRow.createCell((short)6);
				programeManagerValue.setCellValue(new HSSFRichTextString(projectSpace.getMetaData().getProperty("ProgramManager")));
				
				// External Project ID value
				HSSFCell externalProjectIDValue = columnvaluesRow.createCell((short)7);
				externalProjectIDValue.setCellValue(new HSSFRichTextString(projectSpace.getMetaData().getProperty("ExternalProjectID")));
				
				// Project Sponsor value
				HSSFCell projectSponsorValue = columnvaluesRow.createCell((short)8);
				projectSponsorValue.setCellValue(new HSSFRichTextString(
						projectSpace.getSponsor() != null ? projectSpace.getSponsor() : StringUtils.EMPTY));
				
				// Project Initiative value
				HSSFCell projectInitiativeValue = columnvaluesRow.createCell((short)9);
				projectInitiativeValue.setCellValue(new HSSFRichTextString(projectSpace.getMetaData().getProperty("Initiative")));
				
				// Functional Area value
				HSSFCell functionalAreaValue = columnvaluesRow.createCell((short)10);
				functionalAreaValue.setCellValue(new HSSFRichTextString(projectSpace.getMetaData().getProperty("FunctionalArea")));
				
				// Priority Code value
				HSSFCell priorityCodeValue = columnvaluesRow.createCell((short)11);
				priorityCodeValue.setCellValue(new HSSFRichTextString(
						projectSpace.getPriorityCode() != null ? projectSpace.getPriorityCode().getName() : StringUtils.EMPTY));
				
				// Project Charter value
				HSSFCell projectCharterValue = columnvaluesRow.createCell((short)12);
				projectCharterValue.setCellValue(new HSSFRichTextString(projectSpace.getMetaData().getProperty("ProjectCharter")));
				
				// Type of Expense value
				HSSFCell typeOfExpenseValue = columnvaluesRow.createCell((short)13);
				typeOfExpenseValue.setCellValue(new HSSFRichTextString(projectSpace.getMetaData().getProperty("TypeOfExpense")));
				
				// Cost Center value
				HSSFCell costCenterValue = columnvaluesRow.createCell((short)14);
				costCenterValue.setCellValue(new HSSFRichTextString(projectSpace.getCostCenter() != null ? projectSpace
						.getCostCenter() : StringUtils.EMPTY));
				
				// Project Status Value
				HSSFCell projectStatusValue = columnvaluesRow.createCell((short)15);
				projectStatusValue.setCellValue(new HSSFRichTextString(projectSpace.getStatus() != null ? projectSpace
						.getStatus() : StringUtils.EMPTY));
				
			} catch (NoSuchPropertyException e) {
				Logger.getLogger(WorkplanExporter.class).error("No such property " + e.getMessage());
			}
			
			
			
			// Task ID
			HSSFCell taskIdValue = columnvaluesRow.createCell((short)16);
			if(scheduleEntry.getID() != null){
				taskIdValue.setCellValue(Double.valueOf(scheduleEntry.getID()));
			} else {
				taskIdValue.setCellValue(new HSSFRichTextString(""));
			}
			taskIdValue = null;
			
			// task name
			HSSFCell taskName = columnvaluesRow.createCell((short)17);
			if(scheduleEntry.getName() != null){
				taskName.setCellValue(new HSSFRichTextString(scheduleEntry.getName()));
			} else {
				taskName.setCellValue(new HSSFRichTextString(""));
			}
			taskName = null;
			
			// Is Critical Path Task
			HSSFCell criticalPath = columnvaluesRow.createCell((short)18);
			if(scheduleEntry.isCriticalPath()){
				criticalPath.setCellValue(new HSSFRichTextString("Y"));
			} else {
				criticalPath.setCellValue(new HSSFRichTextString("N"));
			}
			criticalPath = null;
			
			// Created Date
			HSSFCell createdDate = columnvaluesRow.createCell((short)19);
			if(scheduleEntry.dateCreated != null){
				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
				createdDate.setCellValue(scheduleEntry.dateCreated);
				createdDate.setCellStyle(cellStyle);
			} else {
				createdDate.setCellValue(new HSSFRichTextString(""));
			}
			createdDate = null;
			
			// start date
			HSSFCell startDate = columnvaluesRow.createCell((short)20);
			if(scheduleEntry.getStartTime() != null){
				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
				startDate.setCellValue(scheduleEntry.getStartTime());
				startDate.setCellStyle(cellStyle);
			} else {
				startDate.setCellValue(new HSSFRichTextString(""));
			}
			startDate = null;
			
			// Finish date
			HSSFCell finishDate = columnvaluesRow.createCell((short)21);
			if(scheduleEntry.getEndTime() != null){
				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
				finishDate.setCellValue(scheduleEntry.getEndTime());
				finishDate.setCellStyle(cellStyle);
			} else {
				finishDate.setCellValue(new HSSFRichTextString(""));
			}
			finishDate = null;
			
			// duration 
			HSSFCell duration = columnvaluesRow.createCell((short)22);
			if(scheduleEntry.getDuration() != null){
				try {
					duration.setCellValue(NumberFormat.getInstance().parseNumber(scheduleEntry.getDuration()).doubleValue());
				} catch (ParseException e) {
					Logger.getLogger(WorkplanExporter.class).error("Parsing error in work :"+e.getMessage());
				}
			} else {
				duration.setCellValue(new HSSFRichTextString(""));
			}
			duration = null;
			
			// Work % Complete
			HSSFCell workPercentComplete = columnvaluesRow.createCell((short)23);
			if(scheduleEntry.getWorkPercentComplete() != null){
				workPercentComplete.setCellValue(Double.valueOf((scheduleEntry.getWorkPercentCompleteDouble())));
			} else {
				workPercentComplete.setCellValue(new HSSFRichTextString(""));
			}
			workPercentComplete = null;
			
			// work
			HSSFCell work = columnvaluesRow.createCell((short)24);
			if(scheduleEntry.getWork() != null){
				try {
					work.setCellValue(NumberFormat.getInstance().parseNumber(scheduleEntry.getWork()).doubleValue());
				} catch (ParseException e) {
					Logger.getLogger(WorkplanExporter.class).error("Parsing error in work :"+e.getMessage());
				}
			} else {
				work.setCellValue(new HSSFRichTextString(""));
			}
			work = null;
			
			// Estimated Cost
			HSSFCell estimatedCost = columnvaluesRow.createCell((short)25);
			estimatedCost.setCellValue(new HSSFRichTextString(""));
			estimatedCost = null;
			
			// actual start date
			HSSFCell actualStartDate = columnvaluesRow.createCell((short)26);
			if(StringUtils.isNotEmpty(scheduleEntry.getActualStartDateStringMedium())){
				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
				actualStartDate.setCellValue(scheduleEntry.getActualStartTime());
				actualStartDate.setCellStyle(cellStyle);
			} else {
				actualStartDate.setCellValue(new HSSFRichTextString(""));
			}
			actualStartDate = null;
			
			// actual finish date
			HSSFCell actualFinishDate = columnvaluesRow.createCell((short)27);
			if(StringUtils.isNotEmpty(scheduleEntry.getActualEndDateStringMedium())){
				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
				actualFinishDate.setCellStyle(cellStyle);
				actualFinishDate.setCellValue(scheduleEntry.getActualEndTime());
			} else {
				actualFinishDate.setCellValue(new HSSFRichTextString(""));
			}
			actualFinishDate = null;
			
			// Baseline Duration
			HSSFCell baselineDuration = columnvaluesRow.createCell((short)28);
			if(StringUtils.isNotEmpty(scheduleEntry.getBaselineDurationString())){
				baselineDuration.setCellValue(new HSSFRichTextString(scheduleEntry.getBaselineDurationString()));
			} else {
				baselineDuration.setCellValue(new HSSFRichTextString(""));
			}
			baselineDuration = null;
			
			// Actual Cost
			HSSFCell actualCost = columnvaluesRow.createCell((short)29);
			actualCost.setCellValue(new HSSFRichTextString(""));
			actualCost = null;
			
			// Remaining Duration 
			HSSFCell remainingDuration = columnvaluesRow.createCell((short)30);
			remainingDuration.setCellValue(new HSSFRichTextString(""));
			remainingDuration = null;
			
			// baseline start date
			HSSFCell baselineStartDate = columnvaluesRow.createCell((short)31);
			if(scheduleEntry.getBaselineStart() != null){
				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
				baselineStartDate.setCellStyle(cellStyle);
				baselineStartDate.setCellValue(scheduleEntry.getBaselineStart());
			} else {
				baselineStartDate.setCellValue(new HSSFRichTextString(""));
			}
			baselineStartDate = null;
			
			// baseline finish date
			HSSFCell baselineFinishDate = columnvaluesRow.createCell((short)32);
			if(scheduleEntry.getBaselineEnd() != null){
				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
				baselineFinishDate.setCellStyle(cellStyle);
				baselineFinishDate.setCellValue(scheduleEntry.getBaselineEnd());
			} else {
				baselineFinishDate.setCellValue(new HSSFRichTextString(""));
			}
			baselineFinishDate = null;
			
			// Milestone column
			HSSFCell milestoneValue = columnvaluesRow.createCell((short)33);
			if(scheduleEntry.isMilestone()){
				milestoneValue.setCellValue(new HSSFRichTextString("Y"));
			} else {
				milestoneValue.setCellValue(new HSSFRichTextString("N"));
			}
			milestoneValue = null;
			
			// Work complete
			HSSFCell workComplete = columnvaluesRow.createCell((short)34);
			if(scheduleEntry.getWorkComplete() != null){
				try {
					workComplete.setCellValue(NumberFormat.getInstance().parseNumber(scheduleEntry.getWorkComplete()).doubleValue());
				} catch (ParseException e) {
					Logger.getLogger(WorkplanExporter.class).error("Error occurred in work complete parsing : "+e.getMessage());
				}
			} else {
				workComplete.setCellValue(new HSSFRichTextString(""));
			}
			workComplete = null;
			
			// priority
			HSSFCell priority = columnvaluesRow.createCell((short)35);
			if(scheduleEntry.getPriorityString() != null){
				priority.setCellValue(new HSSFRichTextString(scheduleEntry.getPriorityString()));
			} else {
				priority.setCellValue(new HSSFRichTextString());
			}
			priority = null;
		}
    }
    
    /**
     * Method to remove phase, baseline work, priority, calculation type, work variance,
     * baseline duration, duration variance, status notifiers, resources, WBS columns.
     * 
     * @param scheduleColumn
     * @return true / false depending on the column. 
     */
    public static boolean isValidColumn(ScheduleColumn scheduleColumn){
    	return (!scheduleColumn.getHeader().equals(ScheduleColumn.PHASE.getHeader()) && !scheduleColumn.getHeader().equals(ScheduleColumn.PRIORITY.getHeader())
    			&& !scheduleColumn.getHeader().equals(ScheduleColumn.BASELINE_WORK.getHeader()) && !scheduleColumn.getHeader().equals(ScheduleColumn.CALCULATION_TYPE.getHeader())
    			&& !scheduleColumn.getHeader().equals(ScheduleColumn.WORK_VARIANCE.getHeader()) && !scheduleColumn.getHeader().equals(ScheduleColumn.BASELINE_DURATION.getHeader())
    			&& !scheduleColumn.getHeader().equals(ScheduleColumn.DURATION_VARIANCE.getHeader()) && !scheduleColumn.getHeader().equals(ScheduleColumn.STATUS_NOTIFIERS.getHeader())
    			&& !scheduleColumn.getHeader().equals(ScheduleColumn.RESOURCES.getHeader()) && !scheduleColumn.getHeader().equals(ScheduleColumn.DEPENDENCIES.getHeader())
    			&& !scheduleColumn.getHeader().equals(ScheduleColumn.WBS.getHeader()));
    }
    
    /**
     * Method to find the schedule entries 
     * by space ID 
     * @param spaceID,  the id of project to be passed 
     * 
     * @return list of minimal schedule entries
     */
    public static List<ScheduleEntry> findScheduleEntriesBySpaceID(String spaceID){
    	List<ScheduleEntry> scheduleEntries = null;
    	String baseSQL = "select " 
			+ "t.task_name, t.task_type, t.priority,  t.duration, t.duration_units, " 
			+ "t.work, t.work_units, t.work_complete,   t.work_complete_units, t.date_start, t.date_finish, t.task_id, "   
			+ "t.work_percent_complete, t.ignore_times_for_dates, t.actual_start, t.actual_finish, t.percent_complete,  t.is_milestone, "
			+ "t.critical_path, t.seq, t.date_modified, ctv.work as baseline_work, ctv.work_units as baseline_work_units, ctv.baseline_id,  " 
			+ "ctv.duration as baseline_duration, ctv.duration_units as baseline_duration_units, "
			+ "ctv.date_start as baseline_start, ctv.date_finish as baseline_finish "
		+ "from "  
			+"pn_space_has_plan shp, pn_plan p, pn_plan_has_task pht, "  
			+"pn_task t, pn_current_task_version ctv " 
		
		+"where "   
			+"shp.plan_id = p.plan_id  and shp.plan_id = pht.plan_id and p.plan_id = pht.plan_id and pht.task_id = t.task_id " 
			+"and t.task_id = ctv.task_id(+)  and shp.space_id = ? and t.record_status = 'A' " 
			+"order by t.seq"; 
    	
    	DBBean db = new DBBean();
    	
    	try{
    		db.prepareStatement(baseSQL);
    		db.pstmt.setString(1, spaceID);
    		db.executePrepared();
    		scheduleEntries = populateEntries(db.result);
    	} catch (SQLException e) {
			Logger.getLogger(WorkplanExporter.class).error("WorkplanExporter.findScheduleEntriesBySpaceID() failed.."+e.getMessage());
		} finally {
			db.release();
		}
    	return scheduleEntries;
    }
    
    /**
     * Method to populate minimal task entries for exporting the 
     * workplan in Excel and CSV format
     * 
     * @param resultSet
     * @return list of tasks
     * @throws SQLException
     */
    public static List<ScheduleEntry> populateEntries(ResultSet resultSet) throws SQLException{
       	List<ScheduleEntry> scheduleEntries = new ArrayList<ScheduleEntry>();
       	ScheduleEntry scheduleEntry = null;
       	while(resultSet.next()){
       		scheduleEntry = ScheduleEntryFactory.createFromType(TaskType.getForID(resultSet.getString("task_type")));
       		scheduleEntry.setID(resultSet.getString("task_id"));
       		scheduleEntry.setName(resultSet.getString("task_name"));
       		scheduleEntry.setPriority(resultSet.getString("priority"));
       		
       		scheduleEntry.setWork(new TimeQuantity(resultSet.getDouble("work"), TimeQuantityUnit.getForID(resultSet.getInt("work_units"))));
       		scheduleEntry.setWorkComplete(new TimeQuantity(resultSet.getDouble("work_complete"), TimeQuantityUnit.getForID(resultSet.getInt("work_complete_units"))));
	        if (scheduleEntry.getWorkTQ().isZero()) {
	            BigDecimal workPercentComplete = resultSet.getBigDecimal("work_percent_complete").divide(new BigDecimal(100), 5, BigDecimal.ROUND_HALF_UP);
	            if (workPercentComplete.scale() < 5) {
	                workPercentComplete = workPercentComplete.setScale(5);
	            }
	            scheduleEntry.setWorkPercentComplete(workPercentComplete);
	        }
	
	        scheduleEntry.setStartTimeD(DatabaseUtils.makeDate(resultSet.getTimestamp("date_start")));
	        scheduleEntry.setEndTimeD(DatabaseUtils.makeDate(resultSet.getTimestamp("date_finish")));
	        scheduleEntry.setIgnoreTimePortionOfDate(resultSet.getBoolean("ignore_times_for_dates"));
	        
	        // populate Duration
	        TimeQuantityUnit durationUnits = TimeQuantityUnit.getForID(resultSet.getString("duration_units"));
	        scheduleEntry.setDuration(new TimeQuantity(new BigDecimal(resultSet.getString("duration")), durationUnits));

	        scheduleEntry.setActualStartTimeD(DatabaseUtils.makeDate(resultSet.getTimestamp("actual_start")));
	        scheduleEntry.setActualEndTimeD(DatabaseUtils.makeDate(resultSet.getTimestamp("actual_finish")));
	       
	        scheduleEntry.setCriticalPath(resultSet.getBoolean("critical_path"));
	        scheduleEntry.setSequenceNumber(resultSet.getInt("seq"));
	        scheduleEntry.setMilestone(resultSet.getBoolean("is_milestone"));
	        scheduleEntry.dateCreated = DatabaseUtils.makeDate(resultSet.getTimestamp("date_modified"));
	        
	        if (resultSet.getString("percent_complete") != null && scheduleEntry.getWorkTQ().getAmount().signum() == 0) {
	        	scheduleEntry.setPercentCompleteInt(resultSet.getInt("percent_complete"));
	        }
	        
	        scheduleEntry.setBaselineStart(DatabaseUtils.makeDate(resultSet.getTimestamp("baseline_start")));
	        scheduleEntry.setBaselineEnd(DatabaseUtils.makeDate(resultSet.getTimestamp("baseline_finish")));
	        if (resultSet.getString("baseline_work") != null) {
	        	scheduleEntry.setBaselineWork(new TimeQuantity(resultSet.getDouble("baseline_work"), TimeQuantityUnit.getForID(resultSet.getInt("baseline_work_units"))));
	        }
	        if (resultSet.getString("baseline_duration") != null) {
	            // Construct the baseline duration as a TimeQuantity
	            TimeQuantityUnit basseLineDurationUnits = TimeQuantityUnit.getForID(resultSet.getString("baseline_duration_units"));
	            scheduleEntry.setBaselineDuration(new TimeQuantity(new BigDecimal(resultSet.getString("baseline_duration")), basseLineDurationUnits));
	        }
	        scheduleEntries.add(scheduleEntry);
       	}
       	return scheduleEntries;
    }
    
    /**
     * Method to get the token names for the export tool
     * 
     * @return array of tokens used for task export from business.
     */
    public static String[] getExportToolTokens() {

		String[] totalExportToolTokens = {
				"prm.schedule.task.export.business.id",
				"prm.schedule.task.export.business.name.custom",
				"prm.schedule.task.export.project.id.maxim.custom",
				"prm.schedule.task.export.project.name.maxim.custom",
				"prm.schedule.task.export.project.description.maxim.custom",
				"prm.schedule.task.export.project.maxim.part.number.maxim.custom",
				"prm.schedule.task.export.project.fab.process.maxim.custom",
				"prm.schedule.task.export.project.die.type.family.maxim.custom",
				"prm.schedule.task.export.project.product.line.maxim.custom",
				"prm.schedule.task.export.project.package.maxim.custom",
				"prm.schedule.task.export.project.project.type.maxim.custom",
				"prm.schedule.task.export.project.pass.maxim.custom",
				"prm.schedule.task.export.project.code.fab.maxim.custom",
				"prm.schedule.task.export.project.intro.quarter.maxim.custom",
				"prm.schedule.task.export.project.die.rev.maxim.custom",
				"prm.schedule.task.export.project.overall.status.maxim.custom",
				"prm.schedule.task.export.csv.taskid",
				"prm.schedule.task.export.csv.name.custom",
				"prm.schedule.task.export.csv.critical",
				"prm.schedule.task.export.csv.datecreated",
				"prm.schedule.task.export.csv.datestart",
				"prm.schedule.task.export.csv.datefinish",
				"prm.schedule.task.export.csv.duration",
				"prm.schedule.task.export.csv.percentcomplete",
				"prm.schedule.task.export.csv.work",
				"prm.schedule.export.csv.currentestimatedtotalcost",
				"prm.schedule.export.csv.actualstartdate",
				"prm.schedule.export.csv.actualfinish",
				"prm.schedule.export.csv.duration",
				"prm.schedule.export.csv.actualcost",
				"prm.schedule.export.csv.remainingduration",
				"prm.schedule.export.csv.baseline.start.date",
				"prm.schedule.export.csv.baseline.end.date",
				"Milestone",
				"Work Complete",
				"Priority" };
		return totalExportToolTokens;

	}
}
