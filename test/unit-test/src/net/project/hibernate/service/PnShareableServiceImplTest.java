package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import net.project.hibernate.dao.IPnShareableDAO;
import net.project.hibernate.model.PnShareable;
import net.project.hibernate.service.impl.PnShareableServiceImpl;
import junit.framework.TestCase;

public class PnShareableServiceImplTest extends TestCase{
	
	private PnShareableServiceImpl shareableService;
	
	private IPnShareableDAO mockPnShareableDAO;
	
	public PnShareableServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockPnShareableDAO = createMock(IPnShareableDAO.class);
		shareableService = new PnShareableServiceImpl();
		shareableService.setPnShareableDAO(mockPnShareableDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnShareableServiceImpl#getShareable(Integer)
     */
    public final void testGetShareable(){
    	Integer objectId = 1;
    	
    	PnShareable pnShareable = new PnShareable();
    	pnShareable.setObjectId(1);
    	pnShareable.setContainerId(10);
    	pnShareable.setAllowableActions(1);
    	pnShareable.setSpaceId(1);
    	
    	expect(mockPnShareableDAO.findByPimaryKey(objectId)).andReturn(pnShareable);
    	replay(mockPnShareableDAO);
    	PnShareable shareable = shareableService.getShareable(objectId);
    	assertEquals(1, shareable.getSpaceId().intValue());
    	assertEquals(1, shareable.getAllowableActions().intValue());
    	verify(mockPnShareableDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnShareableServiceImpl#getShareable(Integer)
     */
    public final void testGetShareableWithNoSharables(){
    	Integer objectId = 1;
    	PnShareable pnShareable = new PnShareable();
    	expect(mockPnShareableDAO.findByPimaryKey(objectId)).andReturn(pnShareable);
    	replay(mockPnShareableDAO);
    	PnShareable shareable = shareableService.getShareable(objectId);
    	assertEquals(null, shareable.getSpaceId());
    	verify(mockPnShareableDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnShareableServiceImpl#saveShareable(PnShareable)
     */
    public final void testSaveShareable(){
    	Integer shareableId = 1;
    	PnShareable pnShareable = new PnShareable();
    	pnShareable.setObjectId(1);
    	pnShareable.setContainerId(10);
    	pnShareable.setAllowableActions(1);
    	pnShareable.setSpaceId(1);
    	expect(mockPnShareableDAO.create(pnShareable)).andReturn(shareableId);
    	replay(mockPnShareableDAO);
    	Integer pnShareableId = shareableService.saveShareable(pnShareable);
    	assertEquals(1, pnShareableId.intValue());
    	verify(mockPnShareableDAO);
    }
}
