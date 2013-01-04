
package net.project.hibernate.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.mock.web.MockHttpServletRequest;

import net.project.application.Application;
import net.project.database.DBBean;
import net.project.hibernate.service.impl.TaskLinkUnlinkHandlerImpl;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.schedule.Schedule;
import net.project.security.User;
import net.project.space.Space;
import net.project.base.hibernate.WorkplanTestBase;

public class TaskLinkUnlinkHandlerImplTest extends WorkplanTestBase{
	
	private TaskLinkUnlinkHandlerImpl taskLinkUnlinkService;
	
	private Schedule schedule = null;
	
	public TaskLinkUnlinkHandlerImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Application.login();
		schedule = createSchedule();
		taskLinkUnlinkService = new TaskLinkUnlinkHandlerImpl();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		schedule = null;
	}
	
	/*
	 * Test method for 
	 * @see 'net.project.hibernate.service.impl.TaskLinkUnlinkHandlerImpl.linkTasks(String, Schedule)'
	 */
	public final void testLinkTasks(){
		String taskIdList = "972140,972142";
		assertNotNull(taskLinkUnlinkService.linkTasks(taskIdList, schedule));
	}
	
	/*
	 * Test method for 
	 * @see 'net.project.hibernate.service.impl.TaskLinkUnlinkHandlerImpl.unLinkTasks(String, Schedule)'
	 */
	public final void testUnlinkTasks(){
		String taskIdList = "972140,972142";
		assertNotNull(taskLinkUnlinkService.unlinkTasks(taskIdList, schedule));
	}
}
