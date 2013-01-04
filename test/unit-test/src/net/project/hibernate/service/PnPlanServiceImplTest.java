package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.dao.IPnPlanDAO;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnPlan;
import net.project.hibernate.service.impl.PnPlanServiceImpl;
import junit.framework.TestCase;

public class PnPlanServiceImplTest extends TestCase{
	
	private PnPlanServiceImpl pnPlanService;
	
	private IPnPlanDAO mockPlanDAO;
	
	public PnPlanServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockPlanDAO = createMock(IPnPlanDAO.class);
		pnPlanService = new PnPlanServiceImpl();
		pnPlanService.setPnPlanDAO(mockPlanDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnPlanServiceImpl#savePlan(PnPlan)
     */
    public final void testSavePlan(){
    	Integer planId = Integer.valueOf(1);
    	PnPlan pnPlan = new PnPlan();
    	
    	pnPlan.setPlanId(1);
    	pnPlan.setPlanName("Test Plan");
    	PnObject pnObject = new PnObject();
    	pnObject.setObjectId(1);
    	pnPlan.setPnObject(pnObject);
    	
    	expect(mockPlanDAO.create(pnPlan)).andReturn(planId);
    	replay(mockPlanDAO);
    	Integer newPlanId = pnPlanService.savePlan(pnPlan);
    	assertEquals(1, newPlanId.intValue());
    	verify(mockPlanDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnPlanServiceImpl#getPlan(Integer)
     */
    public final void testGetPlan(){
    	Integer planId = Integer.valueOf(1);
    	PnPlan pnPlan = new PnPlan();
    	
    	pnPlan.setPlanId(1);
    	pnPlan.setPlanName("Test Plan");
    	PnObject pnObject = new PnObject();
    	pnObject.setObjectId(1);
    	pnPlan.setPnObject(pnObject);
    	
    	expect(mockPlanDAO.findByPimaryKey(planId)).andReturn(pnPlan);
    	replay(mockPlanDAO);
    	PnPlan plan = pnPlanService.getPlan(planId);
    	assertEquals(1, plan.getPlanId().intValue());
    	assertTrue(StringUtils.isNotEmpty(pnPlan.getPlanName()));
    	verify(mockPlanDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnPlanServiceImpl#getPlan(Integer)
     */
    public final void testGetPlanWithNoPlans(){
    	Integer planId = Integer.valueOf(1);
    	PnPlan pnPlan = new PnPlan();
    	expect(mockPlanDAO.findByPimaryKey(planId)).andReturn(pnPlan);
    	replay(mockPlanDAO);
    	PnPlan plan = pnPlanService.getPlan(planId);
    	assertEquals(null, plan.getPlanId());
    	assertTrue(StringUtils.isEmpty(pnPlan.getPlanName()));
    	verify(mockPlanDAO);
    }
}
