package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import net.project.hibernate.dao.IPnDocActionLookupDAO;
import net.project.hibernate.model.PnDocActionLookup;
import net.project.hibernate.service.impl.PnDocActionLookupServiceImpl;

public class PnDocActionLookupServiceImplTest extends TestCase {

	private PnDocActionLookupServiceImpl pnActionLookupService;
	
	private IPnDocActionLookupDAO mockActionLookupDAO;
	
	public PnDocActionLookupServiceImplTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockActionLookupDAO = createMock(IPnDocActionLookupDAO.class);
		pnActionLookupService = new PnDocActionLookupServiceImpl();
		pnActionLookupService.setDao(mockActionLookupDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.impl.PnDocActionLookupServiceImpl#getById(String)
     */
 	public final void testGetById() {
		String action = "1";
		String recordStatus = "Active";
		PnDocActionLookup pnDocActionLookup = new PnDocActionLookup("1",recordStatus);
		
		expect(mockActionLookupDAO.findByPimaryKey(action)).andReturn(pnDocActionLookup);
		replay(mockActionLookupDAO);
		
		PnDocActionLookup pnActionLookup = pnActionLookupService.getById(action);
		
		assertEquals("1", pnActionLookup.getAction());
		verify(mockActionLookupDAO);
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.impl.PnDocActionLookupServiceImpl#getById(String)
     */
	public final void testGetByIdWithNoActionLookup() {
		String action = "1";
		String recordStatus = "Active";
		PnDocActionLookup pnDocActionLookup = new PnDocActionLookup();
		
		expect(mockActionLookupDAO.findByPimaryKey(action)).andReturn(pnDocActionLookup);
		replay(mockActionLookupDAO);
		
		PnDocActionLookup pnActionLookup = pnActionLookupService.getById(action);
		
		assertEquals(null, pnActionLookup.getAction());
		verify(mockActionLookupDAO);
	}	
}
