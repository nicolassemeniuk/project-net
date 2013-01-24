package net.project.hibernate.service;

import net.project.base.hibernate.WorkplanTestBase;
import net.project.hibernate.service.impl.DurationChangeHandlerImpl;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.util.TimeQuantityUnit;
import net.project.application.Application;

public class DurationChangeHandlerImplTest extends WorkplanTestBase{
	
	private DurationChangeHandlerImpl durationChangeHandler;
	
	private Schedule schedule = null; 
	
	public DurationChangeHandlerImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Application.login();
		schedule = createSchedule();
		durationChangeHandler = new DurationChangeHandlerImpl();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		schedule = null;
	}
	
	/*
	 * Test method for 
	 * @see 'net.project.hibernate.service.impl.WorkChangeHandlerImpl.durationChanged(String, ScheduleEntry, String, String,
			Schedule)'
	 */
	public final void testDurationChanged(){
		String taskId = "972140";
		ScheduleEntry entry = schedule.getEntry(taskId);
		String durationAmount = "4";
		String durationUnits = String.valueOf(TimeQuantityUnit.DAY.getUniqueID());
		assertNotNull(durationChangeHandler.durationChanged(taskId, entry, durationAmount, durationUnits, schedule));
	}
}
