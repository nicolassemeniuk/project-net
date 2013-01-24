/**
 * 
 */
package net.project.hibernate.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.service.IWorkPercentChangeHandler;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskEndpointCalculation;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value="workplanWorkPercentChangeHandler")
public class WorkPercentChangeHandlerImpl implements IWorkPercentChangeHandler {
	
	private Logger log = Logger.getLogger(WorkPercentChangeHandlerImpl.class);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IWorkPercentChangeHandler#workPercentChanged(java.lang.String,
	 *      java.lang.String, net.project.schedule.Schedule)
	 */
	public String workPercentChanged(String taskId, ScheduleEntry entry, String newPercentAmount, Schedule clonedSchedule) {
		ScheduleEntry scheduleEntry = null;
		if (taskId != null)
			scheduleEntry = clonedSchedule.getEntry(taskId);
		else
			scheduleEntry = entry;
		
		try {
			TimeQuantity work = TimeQuantity.parse(scheduleEntry.getWork(), scheduleEntry.getWorkTQ().getUnits().getUniqueID()+"");
			TimeQuantity workComplete = TimeQuantity.parse(scheduleEntry.getWorkComplete(), scheduleEntry.getWorkCompleteTQ().getUnits().getUniqueID()+"");
			BigDecimal percentComplete = null;
			String percentCompleteParam = newPercentAmount;
			NumberFormat nf = NumberFormat.getInstance();
			if (!percentCompleteParam.endsWith("%")) {
				percentCompleteParam = percentCompleteParam + "%";
			}
			percentComplete = new BigDecimal(nf.parsePercent(percentCompleteParam).toString());
			if (workComplete.compareTo(work) > 0) {
				log.error(PropertyProvider.get("prm.schedule.taskedit.error.toomuchworkcomplete.message"));
				return PropertyProvider.get("prm.schedule.taskedit.error.toomuchworkcomplete.message");
			}
			ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, clonedSchedule
					.getWorkingTimeCalendarProvider());
			calc.workPercentCompleteChanged(percentComplete);
			new TaskEndpointCalculation().recalculateTaskTimesNoLoad(clonedSchedule);
			
		} catch (ParseException pnetEx) {
			log.error(" Excpetion performing the action."+pnetEx.getMessage());
		} catch (PersistenceException pnetEx) {
			log.error(" Excpetion performing the action."+pnetEx.getMessage());
		}
		return "";
	}
}
