/**
 * 
 */
package net.project.hibernate.service.impl;

import java.text.ParseException;

import net.project.hibernate.service.IDurationChangeHandler;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskEndpointCalculation;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.util.StringUtils;
import net.project.util.TimeQuantity;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value="durationChangeHandler")
public class DurationChangeHandlerImpl implements IDurationChangeHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IDurationChangeHandler#durationChanged(java.lang.String, java.lang.String,
	 *      net.project.schedule.Schedule)
	 */
	public String durationChanged(String taskId, ScheduleEntry entry, String durationAmount, String durationUnits,
			Schedule schedule) {
		// Check parameters are not null
		if (StringUtils.isEmpty(taskId) || StringUtils.isEmpty(durationAmount) || StringUtils.isEmpty(durationUnits) || schedule == null) {
			throw new IllegalArgumentException("parmeters must not be be null");
		}

		ScheduleEntry scheduleEntry = schedule.getEntry(taskId);
		TimeQuantity duration = null;
		
		//Parese duration parameter string 
		try {
			duration = TimeQuantity.parse(durationAmount, durationUnits);
		} catch (ParseException e) {
			Logger.getLogger(DurationChangeHandlerImpl.class).error("Error occured wihile parsing Duration" + e.getMessage());
			return e.getLocalizedMessage();
		}
		
		//handle duration change.
		ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());
		try {
			calc.durationChanged(duration);
			new TaskEndpointCalculation().recalculateTaskTimesNoLoad(schedule);
		} catch (Exception e) {
			return e.getLocalizedMessage();
		}
		//Duration change handled successfully.
		return StringUtils.EMPTY;
	}

}
