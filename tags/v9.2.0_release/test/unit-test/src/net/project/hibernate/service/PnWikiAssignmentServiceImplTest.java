package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import net.project.hibernate.dao.IPnWikiAssignmentDAO;
import net.project.hibernate.model.PnWikiAssignment;
import net.project.hibernate.service.impl.PnWikiAssignmentServiceImpl;
import junit.framework.TestCase;

public class PnWikiAssignmentServiceImplTest extends TestCase {
	
	private PnWikiAssignmentServiceImpl wikiAssignmentServiceImpl;
	
	private IPnWikiAssignmentDAO pnWikiAssignmentDAO;
	
	public PnWikiAssignmentServiceImplTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		pnWikiAssignmentDAO = createMock(IPnWikiAssignmentDAO.class);
		wikiAssignmentServiceImpl = new PnWikiAssignmentServiceImpl();
		wikiAssignmentServiceImpl.setPnWikiAssignmentDAO(pnWikiAssignmentDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/*
	 * Test method for 
	 * @see 'net.project.hibernate.service.impl.PnWikiAssignmentServiceImpl.getWikiAssignmentByObjectId(Integer)'
	 */
	public final void testGetWikiAssignmentByObjectId() {
		Integer objectId = 972140;
		PnWikiAssignment wikiAssignment = new PnWikiAssignment();		
		expect(pnWikiAssignmentDAO.getWikiAssignmentByObjectId(objectId)).andReturn(wikiAssignment);
		replay(pnWikiAssignmentDAO);
		wikiAssignment =  wikiAssignmentServiceImpl.getWikiAssignmentByObjectId(objectId);
		verify(pnWikiAssignmentDAO);
	}
}
