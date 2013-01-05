package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.mock.web.MockHttpServletRequest;

import net.project.application.Application;
import net.project.hibernate.service.impl.WorkplanFilterHandlerImpl;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.schedule.Schedule;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.base.hibernate.WorkplanTestBase;

public class WorkplanFilteraHandlerImplTest extends WorkplanTestBase{
	
	private WorkplanFilterHandlerImpl workplanFilterService;
	
	private Schedule schedule = null;
	
	public WorkplanFilteraHandlerImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Application.login();
		schedule = createSchedule();
		workplanFilterService = new WorkplanFilterHandlerImpl();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		schedule = null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.WorkplanFilterHandlerImpl#applyFilter(HttpServletRequest,
	 * 	Schedule, boolean, boolean, boolean, boolean,boolean,boolean,boolean,boolean,String,String,
	 *	String,String,String,String,String,String,String,String,String)
	 */
	public final void testApplyFilter(){
		MockHttpServletRequest request = new MockHttpServletRequest();
		boolean showAllTasks = false;
		boolean showLateTasks = false;
		boolean showComingDue = false;
		boolean showUnassigned = true;
		boolean showAssignedToUser = false;
		boolean showOnCriticalPath = false;
		boolean showShouldHaveStarted = true;
		boolean showStartedAfterPlannedStart = false;
		String showByAssignedToUser = StringUtils.EMPTY;
		String phaseID = StringUtils.EMPTY;
		String taskName = StringUtils.EMPTY;
		String taskNameComparator = StringUtils.EMPTY;
		String workPercentComplete = StringUtils.EMPTY;
		String workPercentCompleteComparator = StringUtils.EMPTY;
		String taskType = StringUtils.EMPTY;
		String startDateFilterStart = StringUtils.EMPTY;
		String startDateFilterEnd = StringUtils.EMPTY;
		String endDateFilterStart = StringUtils.EMPTY;
		String endDateFilterEnd = StringUtils.EMPTY;
		workplanFilterService.applyFilter(request, schedule, showAllTasks, showLateTasks, showComingDue,
						showUnassigned, showAssignedToUser, showOnCriticalPath, showShouldHaveStarted,
						showStartedAfterPlannedStart, showByAssignedToUser, phaseID, taskName, taskNameComparator,
						workPercentComplete, workPercentCompleteComparator, taskType, startDateFilterStart, startDateFilterEnd,
						endDateFilterStart, endDateFilterEnd);
		assertEquals("4", SessionManager.getUser().getCurrentSpace().getID());
		
	}
}
