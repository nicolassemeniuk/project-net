/**
 * 
 */
package net.project.hibernate.service.impl;

import java.text.ParseException;

import org.springframework.stereotype.Service;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.service.IWorkChangeHandler;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskEndpointCalculation;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.util.StringUtils;
import net.project.util.TimeQuantity;

@Service(value="workplanWorkChangeHandler")
public class WorkChangeHandlerImpl implements IWorkChangeHandler {
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IWorkChangeHandler#workChanged(java.lang.String, java.lang.String, net.project.schedule.Schedule)
	 */
	public String workChanged(String taskId, String workAmount, String workUnit, Schedule schedule){
		//Check all parameter is not null 
		if (StringUtils.isEmpty(taskId) || StringUtils.isEmpty(workAmount) || StringUtils.isEmpty(workUnit) || schedule == null) {
			throw new IllegalArgumentException(" parmeters must not be be null");
		}
		
		TimeQuantity work = null;
		try {
			work = TimeQuantity.parse(workAmount, workUnit);
		} catch (ParseException pnetEx) {
			//An error in parsing TimeQuntity from work and workunit.
			return pnetEx.getLocalizedMessage();
		}
		
		//Get the entry for schedule
		ScheduleEntry scheduleEntry = schedule.getEntry(taskId);
		//Work complete must not be more than work.
		if (scheduleEntry.getWorkCompleteTQ().compareTo(work) > 0) {
			return PropertyProvider.get("prm.schedule.taskedit.error.moreworkcomplete.message");
		}
		TimeQuantity oldWork = scheduleEntry.getWorkTQ();
		ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());
		try {
			//Calculate work change.
			calc.workChanged(work);
			//If work was already zero when we started, we need to set the percent
            //complete to make sure it doesn't get modified on the interface
            if (scheduleEntry.getWorkTQ().isZero()) {
                calc.workPercentCompleteChanged(scheduleEntry.getWorkPercentCompleteDecimal());
            } else {
                calc.workCompleteChanged(scheduleEntry.getWorkCompleteTQ());
            }
            new TaskEndpointCalculation().recalculateTaskTimesNoLoad(schedule);
		} catch (RuntimeException e) {
			calc.workChanged(oldWork);
			return e.getLocalizedMessage();
		} catch (PersistenceException e) {
			calc.workChanged(oldWork);
			return e.getLocalizedMessage();
		}
		//check and set if it is milestone 
		if(!scheduleEntry.isMilestone()){
			scheduleEntry.setMilestone(work.isZero() && (schedule.isAutocalculateTaskEndpoints() || (scheduleEntry.getStartTime().equals(scheduleEntry.getEndTime()))));
		}
		//No error in work change handling.
		return StringUtils.EMPTY; 
	}
	
		
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IWorkChangeHandler#WorkCompleteChanged(java.lang.String, net.project.schedule.ScheduleEntry, java.lang.String, net.project.schedule.Schedule, java.lang.String)
	 */
	public String WorkCompleteChanged(String taskId, String workCompleteAmount, String unitSring, Schedule schedule) {
		// Check all parameter is not null
		if (StringUtils.isEmpty(taskId) || StringUtils.isEmpty(workCompleteAmount) || StringUtils.isEmpty(unitSring) || schedule == null) {
			throw new IllegalArgumentException(" parmeters must not be be null");
		}

		TimeQuantity workComplete = null;
		try {
			workComplete = TimeQuantity.parse(workCompleteAmount, unitSring);
		} catch (ParseException pnetEx) {
			// An error in parsing TimeQuntity from work and workunit.
			return pnetEx.getLocalizedMessage();
		}
		ScheduleEntry entry = schedule.getEntry(taskId);
		ScheduleEntryCalculator calculator = new ScheduleEntryCalculator(entry, schedule.getWorkingTimeCalendarProvider());
		if (!entry.getWorkTQ().isZero()) {
			try {
				calculator.workCompleteChanged(workComplete);
			} catch (Exception e) {
				// Error in calculating work complete change.
				return e.getLocalizedMessage();
			}
		}
		//No error in work complete change handling.
		return StringUtils.EMPTY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IWorkChangeHandler#durationChanged(java.lang.String,
	 *      net.project.schedule.ScheduleEntry, java.lang.String, java.lang.String, net.project.schedule.Schedule)
	 */
	public String durationChanged(String taskId, String durationAmount, String durationUnits, Schedule schedule) {
		// Check all parameter is not null
		if (StringUtils.isEmpty(taskId) || StringUtils.isEmpty(durationAmount) || StringUtils.isEmpty(durationUnits) || schedule == null) {
			throw new IllegalArgumentException(" parmeters must not be be null");
		}
		
		TimeQuantity duration = null;
		try {
			duration = TimeQuantity.parse(durationAmount, durationUnits);
		}catch (ParseException e) {
			//Errorn in parsing duration parameter string
			return e.getLocalizedMessage();
		}
		ScheduleEntryCalculator calc = new ScheduleEntryCalculator(schedule.getEntry(taskId), schedule.getWorkingTimeCalendarProvider());
		try {
			calc.durationChanged(duration);
		} catch (RuntimeException e) {
			//Error in calculating duration change.
			return e.getLocalizedMessage();
		}
		//No error in work complete change handling.
		return StringUtils.EMPTY;
	}
}

