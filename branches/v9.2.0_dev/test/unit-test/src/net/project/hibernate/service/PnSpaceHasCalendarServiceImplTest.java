package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import net.project.hibernate.dao.IPnSpaceHasCalendarDAO;
import net.project.hibernate.model.PnCalendar;
import net.project.hibernate.model.PnSpaceHasCalendar;
import net.project.hibernate.model.PnSpaceHasCalendarPK;
import net.project.hibernate.service.impl.PnSpaceHasCalendarServiceImpl;
import junit.framework.TestCase;

public class PnSpaceHasCalendarServiceImplTest extends TestCase{
	
	private PnSpaceHasCalendarServiceImpl spaceHasCalendarService;
	
	private IPnSpaceHasCalendarDAO mockSpaceHasCalendarDAO;
	
	public PnSpaceHasCalendarServiceImplTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockSpaceHasCalendarDAO = createMock(IPnSpaceHasCalendarDAO.class);
		spaceHasCalendarService = new PnSpaceHasCalendarServiceImpl();
		spaceHasCalendarService.setPnSpaceHasCalendarDAO(mockSpaceHasCalendarDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
    /* 
	 * Test method for
     * @see net.project.hibernate.service.IPnSpaceHasCalendarService#getSpaceHasCalendar(PnSpaceHasCalendarPK)
     */
    public final void testGetSpaceHasCalendar(){
    	PnSpaceHasCalendar pnSpaceHasCalendar = new PnSpaceHasCalendar();
    	PnSpaceHasCalendarPK comp_id = new PnSpaceHasCalendarPK();
    	comp_id.setCalendarId(972064);
    	pnSpaceHasCalendar.setComp_id(comp_id);
    	expect(mockSpaceHasCalendarDAO.findByPimaryKey(comp_id)).andReturn(pnSpaceHasCalendar);
    	replay(mockSpaceHasCalendarDAO);
    	pnSpaceHasCalendar = mockSpaceHasCalendarDAO.findByPimaryKey(comp_id);
    	verify(mockSpaceHasCalendarDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.IPnSpaceHasCalendarService#getSpaceHasCalendar(PnSpaceHasCalendarPK)
     */
    public final void testGetSpaceHasCalendarWithEmptyList(){
    	PnSpaceHasCalendar pnSpaceHasCalendar = new PnSpaceHasCalendar();
    	PnSpaceHasCalendarPK comp_id = new PnSpaceHasCalendarPK();
    	comp_id.setCalendarId(972064);
    	expect(mockSpaceHasCalendarDAO.findByPimaryKey(comp_id)).andReturn(pnSpaceHasCalendar);
    	replay(mockSpaceHasCalendarDAO);
    	pnSpaceHasCalendar = mockSpaceHasCalendarDAO.findByPimaryKey(comp_id);
    	assertEquals(null, pnSpaceHasCalendar.getComp_id());
    	verify(mockSpaceHasCalendarDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.IPnSpaceHasCalendarService#saveSpaceHasCalendar(PnSpaceHasCalendar)
     */
    public final void testSaveSpaceHasCalendar(){
    	PnSpaceHasCalendar spaceHasCalendar = new PnSpaceHasCalendar();
    	PnSpaceHasCalendarPK comp_id = new PnSpaceHasCalendarPK();
    	PnSpaceHasCalendarPK spaceHasCalendarPK = new PnSpaceHasCalendarPK();
    	comp_id.setCalendarId(972064);
    	comp_id.setSpaceId(972052);
    	spaceHasCalendarPK.setCalendarId(972064);
    	spaceHasCalendarPK.setSpaceId(972052);
    	expect(mockSpaceHasCalendarDAO.create(spaceHasCalendar)).andReturn(spaceHasCalendarPK);
    	replay(mockSpaceHasCalendarDAO);
    	spaceHasCalendarPK = mockSpaceHasCalendarDAO.create(spaceHasCalendar);
    	verify(mockSpaceHasCalendarDAO);
    }
}
