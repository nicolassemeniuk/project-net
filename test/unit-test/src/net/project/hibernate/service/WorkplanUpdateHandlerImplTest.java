package net.project.hibernate.service;

import net.project.application.Application;
import net.project.hibernate.service.impl.WorkplanUpdateHandlerImpl;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.space.Space;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.base.hibernate.WorkplanTestBase;

public class WorkplanUpdateHandlerImplTest extends WorkplanTestBase{
	
	private WorkplanUpdateHandlerImpl workplanUpdateService;
	
	private Schedule schedule = null;
	
	public WorkplanUpdateHandlerImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Application.login();
		schedule = createSchedule();		
		workplanUpdateService = new WorkplanUpdateHandlerImpl();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		schedule = null;
	}
	
	/*
	 * Test method for 
	 * @see 'net.project.hibernate.service.impl.WorkplanUpdateHandlerImpl.updateTaskValues(Schedule, Schedule)'
	 */
	public final void testUpdateTaskValues(){
		ScheduleEntry scheduleEntry = schedule.getEntry("972142");
		scheduleEntry.setWork(new TimeQuantity(4, TimeQuantityUnit.HOUR));
		Schedule clonedSchedule = (Schedule)schedule.clone();
		assertNotNull(workplanUpdateService.updateTaskValues(schedule, clonedSchedule));
	}
}
