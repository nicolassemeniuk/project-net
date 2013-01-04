package net.project.hibernate.service;


import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import net.project.hibernate.dao.IPnProjectSpaceDAO;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.project_space.ProjectPhase;
import net.project.hibernate.service.impl.PnProjectSpaceServiceImpl;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class PnProjectSpaceServiceImplTest extends TestCase {

	private IPnProjectSpaceDAO mockProjectSpaceDAO;	
	
	private PnProjectSpaceServiceImpl service;
	
	public PnProjectSpaceServiceImplTest() {
		super();
	}

	@BeforeMethod
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setUpDao for PnProjectSpaceServiceImplTest");
		mockProjectSpaceDAO = createStrictMock(IPnProjectSpaceDAO.class);
		service = new PnProjectSpaceServiceImpl();
		service.setPnProjectSpaceDAO(mockProjectSpaceDAO);
	}

	@AfterMethod
	protected void tearDown() throws Exception {

	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnProjectSpaceServiceImpl.getProjectSpace(Integer)'
	 */
	@Test()
	public final void testGetProjectSpace() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnProjectSpaceServiceImpl.getProjectsByUserId(Integer)'
	 */
	@Test()
	public final void testGetProjectsByUserId() {
		System.out.println("Testing getProjectsByUserId() ");
		List<PnProjectSpace> projects = new ArrayList<PnProjectSpace>();
		Integer userId = 1;
		
		// set expected behaviour on dao
		expect(mockProjectSpaceDAO.getProjectsByMemberId(userId)).andReturn(projects);
		replay(mockProjectSpaceDAO);
		
		projects = service.getProjectsByUserId(userId);
		verify(mockProjectSpaceDAO);
	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnProjectSpaceServiceImpl.getProjectsByBusinessId(Integer)'
	 */
	@Test()
	public final void testGetProjectsByBusinessId() {
		System.out.println("Testing getProjectsByBusinessId() ");
		List<PnProjectSpace> projects = new ArrayList<PnProjectSpace>();
		Integer businessId = 1;
		
		// set expected behaviour on dao
		expect(mockProjectSpaceDAO.getProjectsByBusinessId(businessId)).andReturn(projects);
		replay(mockProjectSpaceDAO);
		
		projects = service.getProjectsByBusinessId(businessId);
		verify(mockProjectSpaceDAO);
	}
	
	/*
	 * Test method for 'net.project.hibernate.service.impl.PnProjectSpaceServiceImpl.getAssignedProjectsByResource(Integer, Integer, Date, Date)'
	 */
	@Test()
	public final void testGetAssignedProjectsByResource() {
		System.out.println("Testing getAssignedProjectsByResource() ");
		List<PnProjectSpace> projects = new ArrayList<PnProjectSpace>();
		Integer businessId = 101;
		Integer resourceId = 1;
		Date startDate = new Date();
		Date endDate = new Date();
		
		// set expected behaviour on dao
		expect(mockProjectSpaceDAO.getAssignedProjectsByResource(businessId, resourceId, startDate, endDate)).andReturn(projects);
		replay(mockProjectSpaceDAO);
		
		projects = service.getAssignedProjectsByResource(businessId, resourceId, startDate, endDate);
		verify(mockProjectSpaceDAO);
	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnProjectSpaceServiceImpl.getProjectsByMemberId(Integer)'
	 */
	@Test()
	public final void testGetProjectsByMemberId() {
		Integer memberId = 497434;
		List<PnProjectSpace> projects = new ArrayList<PnProjectSpace>();
		projects.add(new PnProjectSpace(1));
		projects.add(new PnProjectSpace(2));
		expect(mockProjectSpaceDAO.getProjectsByMemberId(memberId)).andReturn(projects);
		replay(mockProjectSpaceDAO);
		
		List<PnProjectSpace> projectsList = service.getProjectsByMemberId(memberId);
		assertEquals(1, projectsList.get(0).getProjectId().intValue());
		assertEquals(2, projectsList.get(1).getProjectId().intValue());
		verify(mockProjectSpaceDAO);
	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnProjectSpaceServiceImpl.getProjectsVisbleByUser(Integer)'
	 */
	@Test()
	public final void testGetProjectsVisbleByUser() {
		Integer memberId = 497434;
		List<PnProjectSpace> projects = new ArrayList<PnProjectSpace>();
		projects.add(new PnProjectSpace(1));
		projects.add(new PnProjectSpace(2));
		expect(mockProjectSpaceDAO.getProjectsVisbleByUser(memberId)).andReturn(projects);
		replay(mockProjectSpaceDAO);
		
		List<PnProjectSpace> projectsList = service.getProjectsVisbleByUser(memberId);
		assertEquals(1, projectsList.get(0).getProjectId().intValue());
		assertEquals(2, projectsList.get(1).getProjectId().intValue());
		verify(mockProjectSpaceDAO);

	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnProjectSpaceServiceImpl.getMemberProjectsNotVisbleByUser(Integer, Integer)'
	 */
	@Test()
	public final void testGetMemberProjectsNotVisbleByUser() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnProjectSpaceServiceImpl.getProjectPhasesAndMilestones(Integer)'
	 */
	@Test()
	public final void testGetProjectPhasesAndMilestones() {
		Integer projectId = 477997;
		List<ProjectPhase> projectphases = new ArrayList<ProjectPhase>();
		projectphases.add(new ProjectPhase());
		expect(mockProjectSpaceDAO.getProjectPhasesAndMilestones(projectId)).andReturn(projectphases);
		replay(mockProjectSpaceDAO);
		
		List<ProjectPhase> projectphasesList = service.getProjectPhasesAndMilestones(projectId);
		assertEquals(projectphases.size(), projectphasesList.size());
		verify(mockProjectSpaceDAO);
	}
	
	/*
	 * Test method for 'net.project.hibernate.service.impl.PnProjectSpaceServiceImpl.getProjectSchedule(Integer)'
	 */
	@Test()
	public final void testGetProjectSchedule() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnProjectSpaceServiceImpl.getProjectChanges(Integer, Integer)'
	 */
	@Test()
	public final void testGetProjectChanges() {
		// TODO Auto-generated method stub

	}

}
