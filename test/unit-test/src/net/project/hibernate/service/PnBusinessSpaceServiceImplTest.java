package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import net.project.hibernate.dao.IPnBusinessSpaceDAO;
import net.project.hibernate.model.PnBusiness;
import net.project.hibernate.model.PnBusinessSpace;
import net.project.hibernate.service.impl.PnBusinessSpaceServiceImpl;
import junit.framework.TestCase;

public class PnBusinessSpaceServiceImplTest extends TestCase{
	
	private PnBusinessSpaceServiceImpl businessSpaceService;
	
	private IPnBusinessSpaceDAO mockBusinessSpaceDAO;
	
	public PnBusinessSpaceServiceImplTest(){
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		mockBusinessSpaceDAO = createMock(IPnBusinessSpaceDAO.class);
		businessSpaceService = new PnBusinessSpaceServiceImpl();
		businessSpaceService.setPnBusinessSpaceDAO(mockBusinessSpaceDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnBusinessSpaceServiceImpl#getBusinessByProjectId(Integer)
     */
    public final void testGetBusinessByProjectId(){
    	Integer projectId = 1;
    	
    	PnBusiness pnBusiness = new PnBusiness();
    	pnBusiness.setBusinessId(1);
    	pnBusiness.setBusinessName("Test Business");
    	pnBusiness.setRecordStatus("A");
    	pnBusiness.setIsMaster(0);
    	
    	expect(mockBusinessSpaceDAO.getBusinessByProjectId(projectId)).andReturn(pnBusiness);
    	replay(mockBusinessSpaceDAO);
    	PnBusiness business = businessSpaceService.getBusinessByProjectId(projectId);
    	assertNotNull(business);
    	assertEquals("Test Business", business.getBusinessName());
    	assertEquals(1, business.getBusinessId().intValue());
    	verify(mockBusinessSpaceDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnBusinessSpaceServiceImpl#getBusinessByProjectId(Integer)
     */
    public final void testGetBusinessByProjectIdWithNoBusinesses(){
    	Integer projectId = 1;
    	PnBusiness pnBusiness = new PnBusiness();
    	expect(mockBusinessSpaceDAO.getBusinessByProjectId(projectId)).andReturn(pnBusiness);
    	replay(mockBusinessSpaceDAO);
    	PnBusiness business = businessSpaceService.getBusinessByProjectId(projectId);
    	assertEquals(null, business.getBusinessId());
    	verify(mockBusinessSpaceDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnBusinessSpaceServiceImpl#getBusinessSpaceById(Integer)
     */
    public final void testGetBusinessSpaceById(){
    	Integer businessId = 1;
    	
    	PnBusinessSpace pnBusinessSpace = new PnBusinessSpace();
    	PnBusiness pnBusiness = new PnBusiness();
    	pnBusiness.setBusinessId(1);
    	pnBusiness.setBusinessName("Test Business");
    	pnBusiness.setRecordStatus("A");
    	pnBusiness.setIsMaster(0);
    	pnBusinessSpace.setBusinessSpaceId(1);
    	pnBusinessSpace.setPnBusiness(pnBusiness);
    	
    	expect(mockBusinessSpaceDAO.getBusinessSpaceById(businessId)).andReturn(pnBusinessSpace);
    	replay(mockBusinessSpaceDAO);
    	PnBusinessSpace businessSpace = businessSpaceService.getBusinessSpaceById(businessId);
    	assertEquals(1, businessSpace.getBusinessSpaceId().intValue());
    	assertEquals(1, businessSpace.getPnBusiness().getBusinessId().intValue());
    	verify(mockBusinessSpaceDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnBusinessSpaceServiceImpl#getBusinessSpaceById(Integer)
     */
    public final void testGetBusinessSpaceByIdWithNoSpaces(){
    	Integer businessId = 1;
    	PnBusinessSpace pnBusinessSpace = new PnBusinessSpace();
    	expect(mockBusinessSpaceDAO.getBusinessSpaceById(businessId)).andReturn(pnBusinessSpace);
    	replay(mockBusinessSpaceDAO);
    	PnBusinessSpace businessSpace = businessSpaceService.getBusinessSpaceById(businessId);
    	assertEquals(null, businessSpace.getBusinessSpaceId());
    	assertEquals(null, businessSpace.getPnBusiness());
    	verify(mockBusinessSpaceDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnBusinessSpaceServiceImpl#getBusinessSpace(Integer)
     */
    public final void testGetBusinessSpace(){
    	Integer objectId = 1;
    	
    	PnBusinessSpace pnBusinessSpace = new PnBusinessSpace();
    	PnBusiness pnBusiness = new PnBusiness();
    	pnBusiness.setBusinessId(1);
    	pnBusiness.setBusinessName("Test Business");
    	pnBusiness.setRecordStatus("A");
    	pnBusiness.setIsMaster(0);
    	pnBusinessSpace.setBusinessSpaceId(1);
    	pnBusinessSpace.setPnBusiness(pnBusiness);
    	
    	expect(mockBusinessSpaceDAO.findByPimaryKey(objectId)).andReturn(pnBusinessSpace);
    	replay(mockBusinessSpaceDAO);
    	PnBusinessSpace businessSpace = businessSpaceService.getBusinessSpace(objectId);
    	assertEquals(1, businessSpace.getBusinessSpaceId().intValue());
    	verify(mockBusinessSpaceDAO);
    }
}
