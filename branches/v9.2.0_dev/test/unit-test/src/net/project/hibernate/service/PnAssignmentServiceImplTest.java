package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import net.project.base.hibernate.TestBase;
import net.project.base.ObjectType;
import net.project.hibernate.dao.IPnAssignmentDAO;
import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.PnAssignmentPK;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.service.impl.PnAssignmentServiceImpl;
import net.project.util.DateFormat;

public class PnAssignmentServiceImplTest extends TestBase{

	private PnAssignmentServiceImpl service;
	
	private IPnAssignmentDAO mockAssignmentDao;
	
	private IUtilService mockUtilService;
	
	private IPnPersonAllocationService mockAllocationService;
	
	private IPnPersonService mockPersonService;	

	protected void setUp() throws Exception {
		super.setUp();
		mockAssignmentDao = createMock(IPnAssignmentDAO.class);
		mockPersonService = createMock(IPnPersonService.class);
		mockAllocationService = createStrictMock(IPnPersonAllocationService.class);
		mockUtilService = createStrictMock(IUtilService.class);
		service = new PnAssignmentServiceImpl();
		service.setPnAssignmentDAO(mockAssignmentDao);
		service.setUtilService(mockUtilService);
		service.setPnPersonAllocationService(mockAllocationService);
		service.setPnPersonService(mockPersonService);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/*
	 * Test method for 'net.project.hibernate.service.impl.PnAssignmentServiceImpl.getAssigmentsList(Integer[] personIds, Date startDate, Date endDate)'
	 */
	
	public final void testGetAssigmentsList() {
		System.out.println("Testing getAssigmentsList()");
		Integer[] personIds = new Integer[2];
		personIds[0]= 1;
		personIds[1] = 2;
		Date startDate = createDate(2008, 0, 1);
		Date endDate = createDate(2008, 1, 29);
		List<PnAssignment> list = new ArrayList<PnAssignment>();
		expect(mockAssignmentDao.getAssigmentsList(personIds,  startDate,  endDate)).andReturn(list);
		replay(mockAssignmentDao);
		list =  service.getAssigmentsList(personIds, startDate,  endDate);
		verify(mockAssignmentDao);
	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnAssignmentServiceImpl.getWorkSumByMonthForUsers(Integer[], Integer[], Date, Date)'
	 */
	public final void testGetWorkSumByMonthForUsers() {
		System.out.println("Testing getWorkSumByMonthForUsers() ");
		Integer resourceId = 1;
		Integer businessId = 1;
		Date startDate = createDate(2008, 0, 1);
		Date endDate = createDate(2008, 1, 29);
		DateFormat dateformat = new DateFormat(Locale.getDefault(), TimeZone.getDefault());
		List list = new ArrayList();
		expect(mockAssignmentDao.getResourceAssignmentSummaryByBusiness(resourceId, businessId, startDate,  endDate)).andReturn(list);
		replay(mockAssignmentDao);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DATE, 1);
		startDate = cal.getTime();
		cal.setTime(endDate);
		cal.add(Calendar.DATE, -1);
		endDate = cal.getTime();
		
		service.getWorkSumByMonthForUsers(resourceId, businessId, startDate,  endDate, dateformat);
		verify(mockAssignmentDao);
	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnAssignmentServiceImpl.getResourceAssignmentSummaryByBusiness(Integer, Integer, Date, Date)'
	 */
	
	public final void testGetResourceAssignmentSummaryByBusiness() {
		System.out.println("Testing getResourceAssignmentSummaryByBusiness() ");
		Integer resourceId = 1;
		Integer businessId = 1;
		Integer projectId = 1;
		Date startDate = createDate(2008, 0, 1);
		Date endDate = createDate(2008, 1, 29);
		DateFormat dateformat = new DateFormat(Locale.getDefault(), TimeZone.getDefault());
		List list = new ArrayList();
		expect(mockAssignmentDao.getResourceAssignmentSummaryByBusiness(resourceId, businessId, startDate,  endDate)).andReturn(list);
		replay(mockAssignmentDao);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DATE, 1);
		startDate = cal.getTime();
		cal.setTime(endDate);
		cal.add(Calendar.DATE, -1);
		endDate = cal.getTime();
		
		service.getResourceAssignmentSummaryByBusiness(resourceId, businessId, projectId, startDate, endDate, dateformat);
		verify(mockAssignmentDao);
	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnAssignmentServiceImpl.getAssignmentVsAllocation(Integer, Date, Date)'
	 */
	public final void testGetAssignmentVsAllocation() {
		System.out.println("Testing getAssignmentVsAllocation()");
		Integer resourceId = 1;
		Integer businessId = 1;
		Date startDate = createDate(2008, 0, 1);
		Date endDate = createDate(2008, 1, 29);
		DateFormat dateformat = new DateFormat(Locale.getDefault(), TimeZone.getDefault());
		List list = new ArrayList();
		
		//Assignment
		expect(mockAssignmentDao.getResourceAssignmentSummaryByBusiness(resourceId, businessId, startDate,  endDate)).andReturn(list);
		replay(mockAssignmentDao);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DATE, 1);
		startDate = cal.getTime();
		cal.setTime(endDate);
		cal.add(Calendar.DATE, -1);
		endDate = cal.getTime();
		
		//allocation
		expect(mockAllocationService.getResourceAllocationSumary(resourceId, businessId, startDate, endDate)).andReturn(list);
		replay(mockAllocationService);
		
		service.getAssignmentVsAllocation(resourceId, businessId, startDate, endDate, dateformat);

		verify(mockAssignmentDao);
		verify(mockAllocationService);
	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnAssignmentServiceImpl.getOverAssignedResources(Integer, Date, Date)'
	 */
	public final void testGetOverAssignedResources() {
		List<PnAssignment> list = new ArrayList<PnAssignment>();
		Integer projectId  = 1;
		Integer[] personIds = new Integer[2];
		personIds[0]= 1;
		personIds[1] = 2;
		Date date = new Date();
		expect(mockAssignmentDao.getCurrentAssigmentsListForProject(projectId, personIds, date)).andReturn(list);
		replay(mockAssignmentDao);
		list =  service.getCurrentAssigmentsListForProject(projectId, personIds, date);
		verify(mockAssignmentDao);
	}

	/**
	 * create assignment
	 * 
	 * @param person			- assignment person
	 * @param startDate         - assignment start date 
	 * @param endDate			- assignment end date
	 * @param percentAllocated  - assignment allocation percent
	 * @param work 				- assignment work 
	 * @param taskId			- assignment task id
	 * @param projectId			- assignment project id
	 * @return					- assignment object
	 */
	private PnAssignment createAssginment(PnPerson person, Date startDate, Date endDate, Integer percentAllocated, Integer work,
			Integer taskId, Integer projectId){
		PnAssignment assignment = new PnAssignment(startDate , endDate, BigDecimal.valueOf(percentAllocated), BigDecimal.valueOf(work));
		assignment.setComp_id(new PnAssignmentPK(projectId, person.getPersonId(), taskId));
		assignment.setPnPerson(person);
		return assignment;
	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnAssignmentServiceImpl.getAssignmentsByPersonForProject(Integer, Date, Date)'
	 */	
	public final void atestGetAssignmentsByPersonForProject() {
		// prepare test data
		Integer projectOne = 11;
		Integer taskOne = 21;
		Integer taskTwo = 22;
		Integer taskThree = 23;
		Integer taskFour = 24;
		
		Integer projectTwo = 12;		
		Integer taskFive = 25;
		
		Integer personOne = 1;
		Integer personTwo = 2;
		Integer personThree = 3;
		
		List<Teammate> teammates = new ArrayList<Teammate>();
					
		Teammate teammate = new Teammate(personOne, "user1", "user1", "user1 user1", "user1@user.com", personOne);
		
		PnPerson person = new PnPerson(personOne, "user1", "user1", "user1 user1" );
		PnAssignment assignment = createAssginment(person, createDate(2008,1,1) , createDate(2008,1,3), 100, 24, taskOne, projectOne);
		teammate.getAssignments().add(assignment);
		assignment = createAssginment(person, createDate(2008,1,10), createDate(2008,1,15), 100, 48, taskTwo, projectOne);
		teammate.getAssignments().add(assignment);		
		assignment = createAssginment(person, createDate(2008,1,14), createDate(2008,1,15), 100, 16, taskThree, projectOne);
		teammate.getAssignments().add(assignment);		
		assignment = createAssginment(person, createDate(2008,1,28), createDate(2008,2,2), 100, 48, taskFour, projectOne);					
		teammate.getAssignments().add(assignment);				
		teammates.add(teammate);
						
		teammate = new Teammate(personTwo, "user2", "user2", "user2 user2", "user2@user.com", personTwo);		
		person = new PnPerson(personTwo, "user2", "user2", "user2 user2" );
		assignment = createAssginment(person, createDate(2008,1,1) , createDate(2008,1,3), 100, 24, taskOne, projectOne);		
		teammate.getAssignments().add(assignment);
		assignment = createAssginment(person, createDate(2008,1,28), createDate(2008,2,2), 100, 48, taskFour, projectOne);		
		teammate.getAssignments().add(assignment);
		assignment = createAssginment(person, createDate(2008,1,28), createDate(2008,1,29), 100, 16, taskFour, projectOne);
		teammate.getAssignments().add(assignment);				
		teammates.add(teammate);		
		
		teammate = new Teammate(personThree, "user3", "user3", "user3 user3", "user3@user.com", personThree);		
		person = new PnPerson(personThree, "user3", "user3", "user3 user3" );
		assignment = createAssginment(person, createDate(2008,1,28) , createDate(2008,1,29), 100, 16, taskFive, projectTwo);	
		teammate.getAssignments().add(assignment);				
		teammates.add(teammate);				
		
		expect(mockAssignmentDao.getAssignmentsByPersonForProject(projectOne,createDate(2008,1,15), createDate(2008,1,31))).andReturn(teammates);
		expect(mockAssignmentDao.getTeammatesWithoutAssignments(projectOne,createDate(2008,2,2))).andReturn(teammates);
		replay(mockAssignmentDao); 
		
		List<Teammate> onlineTeammates = new ArrayList<Teammate>();
		Teammate onlineTeammate = new Teammate(personOne, "user1", "user1", "user1 user1", "user1@user.com", personOne);
		onlineTeammates.add(onlineTeammate);
		
		expect(mockPersonService.getOnlineMembers(projectOne)).andReturn(onlineTeammates);
		replay(mockPersonService);
		
		List<Teammate> teammateList = new ArrayList<Teammate>();
		teammateList = service.getAssignmentsByPersonForProject(projectOne,createDate(2008,1,15), createDate(2008,1,31));
		
		assertEquals(teammates.size(), teammateList.size());
		
		Teammate teammateTest = teammateList.get(0);
		assertEquals(1 ,teammateTest.getPersonId().intValue());
		assertTrue(teammateTest.isOnline());
		assertTrue(teammateTest.isOverassigned());
		assertTrue(teammateTest.isHasLateTasks());
		assertTrue(teammateTest.isHasTaskDueThisWeek());
		assertEquals(4, teammateTest.getAssignments().size());
						
		teammateTest = teammateList.get(1);
		assertEquals(2 ,teammateTest.getPersonId().intValue());
		assertFalse(teammateTest.isOnline());		
		assertTrue(teammateTest.isOverassigned());
		assertTrue(teammateTest.isHasLateTasks());
		assertFalse(teammateTest.isHasTaskDueThisWeek());
		assertEquals(3, teammateTest.getAssignments().size());
		
		teammateTest = teammateList.get(2);
		assertEquals(3 ,teammateTest.getPersonId().intValue());
		assertFalse(teammateTest.isOnline());
		assertFalse(teammateTest.isOverassigned());
		assertFalse(teammateTest.isHasLateTasks());
		assertFalse(teammateTest.isHasTaskDueThisWeek());
		assertEquals(0, teammateTest.getAssignments().size());
				
		verify(mockAssignmentDao);
		verify(mockPersonService);
	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnAssignmentServiceImpl.getAssignmentDetailsWithFilters(Integer[],
     *	String, Integer[], Integer, String[], boolean, boolean, boolean, boolean, Date, Date, Integer, Double, String, String, String,
     *	int, int, boolean)'
	 */	
	public final void testGetAssignmentDetailsWithFilters() {
		Integer[] personIds = {497434};
		String assigneeORAssignor = "assignee";
		Integer[] projectIds = {972052};
		Integer businessId = 0;
		String[] assignmentTypes = {ObjectType.TASK, ObjectType.MEETING, ObjectType.FORM_DATA };
		boolean lateAssignment = false;
		boolean comingDueDate = false;
		boolean shouldHaveStart = false;
		boolean InProgress = false;
		Date startDate = createDate(2008,1,10);
		Date endDate = createDate(2008,1,15);
		Integer statusId = null;
		Double percentComplete = .99;
		String PercentCompleteComparator = "lessthan";
		String assignmentName = null;
		String assignmentNameComparator = null;
		int offset = 0;
		int range = 0;
		boolean isOrderByPerson = false;
		
		List<PnAssignment> list = new ArrayList<PnAssignment>();
		expect(mockAssignmentDao.getAssignmentDetailsWithFilters(personIds, assigneeORAssignor, 
					projectIds, businessId, assignmentTypes, lateAssignment, comingDueDate, 
					shouldHaveStart, InProgress, startDate, endDate, statusId, percentComplete, 
					PercentCompleteComparator, assignmentName, assignmentNameComparator, 
					offset, range, isOrderByPerson)).andReturn(list);
		replay(mockAssignmentDao);
		list =  service.getAssignmentDetailsWithFilters(personIds, assigneeORAssignor, 
					projectIds, businessId, assignmentTypes, lateAssignment, comingDueDate, 
					shouldHaveStart, InProgress, startDate, endDate, statusId, percentComplete, 
					PercentCompleteComparator, assignmentName, assignmentNameComparator, 
					offset, range, isOrderByPerson);
		verify(mockAssignmentDao);
	}

}
