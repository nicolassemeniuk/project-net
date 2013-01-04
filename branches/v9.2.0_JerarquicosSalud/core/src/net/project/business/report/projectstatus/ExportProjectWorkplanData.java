package net.project.business.report.projectstatus;

import java.util.List;

import net.project.project.ProjectSpace;
import net.project.schedule.ScheduleEntry;

/**
 * Class for storing project and workplan data 
 * for exporting
 *
 */
public class ExportProjectWorkplanData {
	
	public ProjectSpace projectSpace;
	
	public List<ScheduleEntry> scheduleEntriesList;
}
