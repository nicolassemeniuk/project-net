/**
 * 
 */
package net.project.hibernate.service;

import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.springframework.mock.web.MockHttpServletRequest;

import net.project.application.Application;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.hibernate.service.impl.TaskIndentionHandlerImpl;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.schedule.Schedule;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.base.hibernate.WorkplanTestBase;

public class TaskIndentionHandlerImplTest extends WorkplanTestBase{
	
	private TaskIndentionHandlerImpl taskIndentionService;
	
	private Schedule schedule = null; 
	
	public TaskIndentionHandlerImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Application.login();
		schedule = createSchedule();
		taskIndentionService = new TaskIndentionHandlerImpl();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		schedule = null;
	}

	/*
	 * Test method for 
	 * '@see net.project.hibernate.service.impl.TaskIndentionHandlerImpl.IndentTask(String, Schedule)'
	 */
	public final void testIndentTask() {
		String taskId = "972142"; 
		String entryAbove = "972140"; 
		assertNotNull(taskIndentionService.IndentTask(taskId, entryAbove, schedule));
	}
	
	/*
	 * Test method for 
	 * @see 'net.project.hibernate.service.impl.TaskIndentionHandlerImpl.UnindentTask(String, Schedule)'
	 */
	public final void testUnindentTask() {
		String taskId="972142";
		assertNotNull(taskIndentionService.unIndentTask(taskId, schedule));
	}
}
