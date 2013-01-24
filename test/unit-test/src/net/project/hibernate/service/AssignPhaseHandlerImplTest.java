package net.project.hibernate.service;

import net.project.hibernate.service.impl.AssignPhaseHandlerImpl;
import junit.framework.TestCase;

public class AssignPhaseHandlerImplTest extends TestCase{
	
	private AssignPhaseHandlerImpl assignPhaseService;
	
	public AssignPhaseHandlerImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		assignPhaseService = new AssignPhaseHandlerImpl();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/*
	 * Test method for 
	 * 'net.project.hibernate.service.impl.AssignPhaseHandlerImpl.assignPhase(String, Schedule)'
	 */
	public final void testAssignPhase() {
		String taskIdList = "972140";
		String phaseId = "2043513";
		assertNotNull(assignPhaseService.assignPhase(taskIdList, phaseId));
	}
}
