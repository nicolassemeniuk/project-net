
package net.project.hibernate.service;

import net.project.application.Application;
import net.project.hibernate.service.impl.WorkChangeHandlerImpl;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.schedule.Schedule;
import net.project.space.Space;
import net.project.base.hibernate.WorkplanTestBase;

public class WorkChangeHandlerImplTest extends WorkplanTestBase{
	
	private WorkChangeHandlerImpl workChangeService;
	
	private Schedule schedule = null; 
	
	public WorkChangeHandlerImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Application.login();
		schedule = createSchedule();
		workChangeService = new WorkChangeHandlerImpl();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		schedule = null;
	}
	
	/*
	 * Test method for 
	 * @see 'net.project.hibernate.service.impl.WorkChangeHandlerImpl.workChanged(String, String, String, Schedule)'
	 */
	public final void testWorkChanged(){
		String taskId = "972140";
		String workAmount = "10";
		String workUnit = "4";
		assertNotNull(workChangeService.workChanged(taskId, workAmount, workUnit, schedule));
	}
	
	/*
	 * Test method for 
	 * @see 'net.project.hibernate.service.impl.WorkChangeHandlerImpl.WorkCompleteChanged(String, String, String, Schedule)'
	 */
	public final void testWorkCompleteChanged(){
		String taskId = "972140";
		String workCompleteAmount = "5";
		String unitSring = "4";
		assertNotNull(workChangeService.WorkCompleteChanged(taskId, workCompleteAmount, unitSring, schedule));
	}
	
	/*
	 * Test method for 
	 * @see 'net.project.hibernate.service.impl.WorkChangeHandlerImpl.durationChanged(String, String, String, Schedule)'
	 */
	public final void testDurationChanged(){
		String taskId = "972140";
		String durationAmount = "2";
		String durationUnits = "8";
		assertNotNull(workChangeService.durationChanged(taskId, durationAmount, durationUnits, schedule));
	}
}
