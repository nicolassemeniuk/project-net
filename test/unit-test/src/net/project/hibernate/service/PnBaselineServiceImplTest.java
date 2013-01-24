package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.dao.IPnBaselineDAO;
import net.project.hibernate.model.PnPlanVersion;
import net.project.hibernate.model.PnPlanVersionPK;
import net.project.hibernate.service.impl.PnBaselineServiceImpl;
import junit.framework.TestCase;

public class PnBaselineServiceImplTest extends TestCase{
	
	private PnBaselineServiceImpl pnBaselineService;
	
	private IPnBaselineDAO mockBaselineDAO;
	
	public PnBaselineServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockBaselineDAO = createMock(IPnBaselineDAO.class);
		pnBaselineService = new PnBaselineServiceImpl();
		pnBaselineService.setPnBaselineDAO(mockBaselineDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnBaselineServiceImpl#getCurrentDefaultBaseline(Integer)
     */
    public final void testGetCurrentDefaultBaseline(){
    	Integer objectId = 1;
    	
    	List<Object> list = new ArrayList<Object>();
    	
    	PnPlanVersion pnPlanVersion = new PnPlanVersion();
    	pnPlanVersion.setPlanName("Test Plan");
    	PnPlanVersionPK comp_id = new PnPlanVersionPK();
    	comp_id.setPlanId(100);
    	comp_id.setPlanVersionId(1);
    	pnPlanVersion.setComp_id(comp_id);
    	
    	list.add(pnPlanVersion);
    	
    	expect(mockBaselineDAO.getCurrentDefaultBaseline(objectId)).andReturn(list);
    	replay(mockBaselineDAO);
    	List<Object> baselines = pnBaselineService.getCurrentDefaultBaseline(objectId);
    	assertEquals(1, baselines.size());
    	verify(mockBaselineDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnBaselineServiceImpl#getCurrentDefaultBaseline(Integer)
     */
    public final void testGetCurrentDefaultBaselineWithNoBaselines(){
    	Integer objectId = 1;
    	List<Object> list = new ArrayList<Object>();
    	expect(mockBaselineDAO.getCurrentDefaultBaseline(objectId)).andReturn(list);
    	replay(mockBaselineDAO);
    	List<Object> baselines = pnBaselineService.getCurrentDefaultBaseline(objectId);
    	assertEquals(0, baselines.size());
    	verify(mockBaselineDAO);
    }
}
