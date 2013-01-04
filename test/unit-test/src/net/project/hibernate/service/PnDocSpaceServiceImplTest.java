package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import java.util.Date;
import junit.framework.TestCase;

import net.project.hibernate.dao.IPnDocSpaceDAO;
import net.project.hibernate.model.PnDocSpace;
import net.project.hibernate.service.impl.PnDocSpaceServiceImpl;
import net.project.base.hibernate.TestBase;

public class PnDocSpaceServiceImplTest extends TestCase {

	private PnDocSpaceServiceImpl pnDocSpaceService;
	
	private IPnDocSpaceDAO mockDocSpaceDAO;
	
	private TestBase date;
	
	public PnDocSpaceServiceImplTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		date = new TestBase();
		mockDocSpaceDAO = createMock(IPnDocSpaceDAO.class);
		pnDocSpaceService = new PnDocSpaceServiceImpl();
		pnDocSpaceService.setPnDocSpaceDAO(mockDocSpaceDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.PnDocSpaceServiceImpl#getDocSpace(Integer)
	 */
	public final void testGetDocSpace() {
		Integer docSpaceId = 1;
		Date crc = date.createDate(2009,9,5); 
		String recordStatus = "A";
		PnDocSpace pnDocSpace = new PnDocSpace(docSpaceId,crc,recordStatus);
		
		expect(mockDocSpaceDAO.findByPimaryKey(1)).andReturn(pnDocSpace);
		replay(mockDocSpaceDAO);
		PnDocSpace docSpace = pnDocSpaceService.getDocSpace(docSpaceId);
		assertEquals(1, docSpace.getDocSpaceId().intValue());
		verify(mockDocSpaceDAO);
    }
	
	/* Test method for
	 * @see net.project.hibernate.service.PnDocSpaceServiceImpl#getDocSpace(Integer)
	 */
	public final void testGetPnDocSpaceWithNoDocSpace() {
		Integer docSpaceId = 1;
		PnDocSpace pnDocSpace = new PnDocSpace();
		
		expect(mockDocSpaceDAO.findByPimaryKey(1)).andReturn(pnDocSpace);
		replay(mockDocSpaceDAO);
		PnDocSpace docSpace = pnDocSpaceService.getDocSpace(docSpaceId);
		assertEquals(null, docSpace.getDocSpaceId());
		verify(mockDocSpaceDAO);
    }
}
