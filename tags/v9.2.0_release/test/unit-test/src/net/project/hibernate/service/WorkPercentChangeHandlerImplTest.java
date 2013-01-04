package net.project.hibernate.service;

import net.project.base.hibernate.WorkplanTestBase;
import net.project.application.Application;
import net.project.hibernate.service.impl.WorkPercentChangeHandlerImpl;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.space.Space;

public class WorkPercentChangeHandlerImplTest extends WorkplanTestBase{

	private WorkPercentChangeHandlerImpl workPercentChangeService;
	
	private Schedule schedule = null;
	
	public WorkPercentChangeHandlerImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Application.login();
		schedule = createSchedule();
		workPercentChangeService = new WorkPercentChangeHandlerImpl();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		schedule = null;
	}
	
	/*
	 * Test method for 
	 * @see 'net.project.hibernate.service.impl.WorkPercentChangeHandlerImpl.workPercentChanged(String,
	 *  ScheduleEntry, String, Schedule)'
	 */
	public final void testWorkPercentChanged(){
		String taskId = "972142";
		ScheduleEntry scheduleEntry = schedule.getEntry(taskId);
		String newPercentAmount = "20";
		assertNotNull(workPercentChangeService.workPercentChanged(taskId, scheduleEntry, newPercentAmount, schedule));
	}
}
