package net.project.hibernate.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.mock.web.MockHttpServletRequest;

import net.project.application.Application;
import net.project.hibernate.service.impl.AddTaskServiceImpl;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.schedule.Schedule;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.TimeQuantityUnit;
import net.project.base.hibernate.WorkplanTestBase;

public class AddTaskServiceImplTest extends WorkplanTestBase{
	private AddTaskServiceImpl addTaskService;
	
	private Schedule schedule = null; 
	
	public AddTaskServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Application.login();
		schedule = createSchedule();
		addTaskService = new AddTaskServiceImpl();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		schedule = null;
	}
	
	/*
	 * Test method for 
	 * @see net.project.hibernate.service.impl.AddTaskServiceImpl.quickAdd(String, String, String, String, String, String, String[], Schedule)
	 */
	public final void testQuickAdd(){
		String taskName = "Test Task";
		String taskDescription = "Testing purpose";
		String work = "8"; 
		String workUnits = "4";
		String startTimeString = "";
		String endTimeString = "";
		String[] selected = null;
		String scheduleEntryID = StringUtils.EMPTY;
		try {
	    	scheduleEntryID = addTaskService.quickAdd(taskName, taskDescription, work, workUnits, startTimeString, endTimeString, selected, schedule);
		}  catch (Exception ex) {
			System.out.println("Error while adding tasks:"+ex.getMessage());
		}
		assertNotNull(scheduleEntryID);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.AddTaskServiceImpl#addASubTaskUnderSelectedTask(String,
	 *      String, String, String, String, String, String,Schedule, String)
	 */
	public final void testAddASubTaskUnderSelectedTask(){
		String taskName = "Child Task";
		String taskDescription = "Testing Sub Task";
		String work = "8";
		String workUnits = "4";
		String startTimeString = ""; 
		String endTimeString = ""; 
		String selectedTaskId = "972140";
		String nodeExpanded = null;
		String scheduleEntryID = StringUtils.EMPTY;
		try {
			scheduleEntryID = addTaskService.quickAddASubTaskUnderSelectedTask(taskName, taskDescription, work, workUnits, startTimeString, endTimeString, selectedTaskId, schedule, nodeExpanded);
		} catch (Exception ex) {
			System.out.println("Error while adding tasks :"+ex.getMessage());
		}
		assertNotNull(scheduleEntryID);	
	}
}
