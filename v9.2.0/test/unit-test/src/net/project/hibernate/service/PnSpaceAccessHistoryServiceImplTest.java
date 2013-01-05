package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Date;

import net.project.hibernate.dao.IPnSpaceAccessHistoryDAO;
import net.project.hibernate.model.PnSpaceAccessHistory;
import net.project.hibernate.model.PnSpaceAccessHistoryPK;
import net.project.hibernate.service.impl.PnSpaceAccessHistoryServiceImpl;
import junit.framework.TestCase;

public class PnSpaceAccessHistoryServiceImplTest extends TestCase{
	
	private PnSpaceAccessHistoryServiceImpl spaceAccessHistoryService;
	
	private IPnSpaceAccessHistoryDAO mockSpaceAccessHistoryDAO;
	
	public PnSpaceAccessHistoryServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockSpaceAccessHistoryDAO = createMock(IPnSpaceAccessHistoryDAO.class);
		spaceAccessHistoryService = new PnSpaceAccessHistoryServiceImpl();
		spaceAccessHistoryService.setPnSpaceAccessHistoryDAO(mockSpaceAccessHistoryDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnSpaceAccessHistoryServiceImpl#getSpaceHistory(Integer, Integer)
     */
    public final void testGetDefaultDirectory(){
    	Date date = new Date();
    	Integer userId = 1;
    	Integer spaceId = 1;
    	expect(mockSpaceAccessHistoryDAO.getSpaceHistory(userId, spaceId)).andReturn(date);
    	replay(mockSpaceAccessHistoryDAO);
    	Date spaceHistoryDate = spaceAccessHistoryService.getSpaceHistory(userId, spaceId);
    	assertEquals(date, spaceHistoryDate);
    	verify(mockSpaceAccessHistoryDAO);
   }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnSpaceAccessHistoryServiceImpl#getBySpaceAndUserId(PnSpaceAccessHistoryPK)
     */
    public final void testGetBySpaceAndUserId(){
    	PnSpaceAccessHistory pnSpaceAccessHistory = new PnSpaceAccessHistory();
    	PnSpaceAccessHistoryPK spaceAccessHistoryPK = new PnSpaceAccessHistoryPK();
    	spaceAccessHistoryPK.setPersonId(1);
    	spaceAccessHistoryPK.setSpaceId(1);
    	pnSpaceAccessHistory.setComp_id(spaceAccessHistoryPK);
    	pnSpaceAccessHistory.setAccessDate(new Date());
    	
    	expect(mockSpaceAccessHistoryDAO.findByPimaryKey(spaceAccessHistoryPK)).andReturn(pnSpaceAccessHistory);
    	replay(mockSpaceAccessHistoryDAO);
    	PnSpaceAccessHistory spaceAccessHistory = spaceAccessHistoryService.getBySpaceAndUserId(spaceAccessHistoryPK);
    	assertEquals(1, spaceAccessHistory.getComp_id().getSpaceId().intValue());
    	assertEquals(1, spaceAccessHistory.getComp_id().getPersonId().intValue());
    	verify(mockSpaceAccessHistoryDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnSpaceAccessHistoryServiceImpl#getBySpaceAndUserId(PnSpaceAccessHistoryPK)
     */
    public final void testGetBySpaceAndUserIdWithNoHistory(){
    	PnSpaceAccessHistory pnSpaceAccessHistory = new PnSpaceAccessHistory();
    	PnSpaceAccessHistoryPK spaceAccessHistoryPK = new PnSpaceAccessHistoryPK();
    	expect(mockSpaceAccessHistoryDAO.findByPimaryKey(spaceAccessHistoryPK)).andReturn(pnSpaceAccessHistory);
    	replay(mockSpaceAccessHistoryDAO);
    	PnSpaceAccessHistory spaceAccessHistory = spaceAccessHistoryService.getBySpaceAndUserId(spaceAccessHistoryPK);
    	assertEquals(null, spaceAccessHistory.getComp_id());
    	verify(mockSpaceAccessHistoryDAO);
    }

}
