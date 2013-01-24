/**
 * 
 */
package net.project.hibernate.service;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.service.impl.TaskMoveHandlerImpl;
import net.project.schedule.Schedule;
import junit.framework.TestCase;

public class TaskMoveHandlerImplTest extends TestCase {
	
	private TaskMoveHandlerImpl taskMoveHandlerService;
	
	public TaskMoveHandlerImplTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		taskMoveHandlerService = new TaskMoveHandlerImpl();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/*
	 * Test method for 
	 * 'net.project.hibernate.service.impl.TaskMoveHandlerImpl.taskUp(String, Schedule)'
	 */
	public final void testTaskUp() {
		Schedule schedule = new Schedule();
		String taskId = "972140";
		String scheduleID = "972065"; 
		schedule.setID(scheduleID);
		assertNotNull(taskMoveHandlerService.taskUp(taskId, schedule));
	}
	
	
	/*
	 * Test method for 
	 * 'net.project.hibernate.service.impl.TaskMoveHandlerImpl.taskDown(String, Schedule)'
	 */
	public final void testTaskDown() {
		Schedule schedule = new Schedule();
		String taskId = "972140";
		String scheduleID = "972065";
		schedule.setID(scheduleID);
		assertNotNull(taskMoveHandlerService.taskDown(taskId, schedule));
	}
}
