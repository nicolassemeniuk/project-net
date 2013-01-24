package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import net.project.hibernate.dao.IPnSpaceHasGroupDAO;
import net.project.hibernate.model.PnGroup;
import net.project.hibernate.model.PnSpaceHasGroup;
import net.project.hibernate.model.PnSpaceHasGroupPK;
import net.project.hibernate.service.impl.PnSpaceHasGroupServiceImpl;
import junit.framework.TestCase;


public class PnSpaceHasGroupServiceImplTest extends TestCase{
	
	private PnSpaceHasGroupServiceImpl spaceHasGroupService;
	
	private IPnSpaceHasGroupDAO mockSpaceHasGroupDAO;
	
	public PnSpaceHasGroupServiceImplTest(){
		super();
	}
	
	@BeforeMethod
	protected void setUp() throws Exception {
		super.setUp();
		mockSpaceHasGroupDAO = createMock(IPnSpaceHasGroupDAO.class);
		spaceHasGroupService = new PnSpaceHasGroupServiceImpl();
		spaceHasGroupService.setPnSpaceHasGroupDAO(mockSpaceHasGroupDAO);
	}
	
	@AfterMethod
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnSpaceHasGroupServiceImpl#getSpaceHasGroup(PnSpaceHasGroupPK)
     */
    public final void testGetSpaceHasGroup(){
    	PnSpaceHasGroupPK spaceHasGroupPK = new PnSpaceHasGroupPK();
    	spaceHasGroupPK.setSpaceId(972052);
    	spaceHasGroupPK.setGroupId(972056);
    	PnSpaceHasGroup spaceHasGroup = new PnSpaceHasGroup();
    	spaceHasGroup.setComp_id(spaceHasGroupPK);
    	spaceHasGroup.setIsOwner(1);
    	expect(mockSpaceHasGroupDAO.findByPimaryKey(spaceHasGroupPK)).andReturn(spaceHasGroup);
    	replay(mockSpaceHasGroupDAO);
    	PnSpaceHasGroup pnSpaceHasGroup = spaceHasGroupService.getSpaceHasGroup(spaceHasGroupPK);
    	assertNotNull(pnSpaceHasGroup.getComp_id().getSpaceId());
    	verify(mockSpaceHasGroupDAO);
    }
    
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnSpaceHasGroupServiceImpl#saveSpaceHasGroup(PnSpaceHasGroup)
     */
    public final void testSaveSpaceHasGroup(){
    	PnSpaceHasGroupPK spaceHasGroupPK = new PnSpaceHasGroupPK();
    	PnSpaceHasGroupPK pnSpaceHasGroupPK = new PnSpaceHasGroupPK();
    	PnSpaceHasGroup spaceHasGroup = new PnSpaceHasGroup();
    	
    	spaceHasGroupPK.setGroupId(972056);
    	spaceHasGroupPK.setSpaceId(972052);
    	spaceHasGroup.setComp_id(spaceHasGroupPK);
    	spaceHasGroup.setIsOwner(1);
    	
    	expect(mockSpaceHasGroupDAO.create(spaceHasGroup)).andReturn(pnSpaceHasGroupPK);
    	replay(mockSpaceHasGroupDAO);
    	PnSpaceHasGroupPK hasGroupPK = mockSpaceHasGroupDAO.create(spaceHasGroup);
    	assertEquals(null, hasGroupPK.getGroupId());
    	verify(mockSpaceHasGroupDAO);
    }
    
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnSpaceHasGroupServiceImpl#getAll(PnSpaceHasGroup)
     */
    public final void testGetAll(){
    	List<PnSpaceHasGroup> list = new ArrayList<PnSpaceHasGroup>();
    	
    	PnSpaceHasGroup spaceHasGroup = new PnSpaceHasGroup();
    	PnGroup pnGroup = new PnGroup();
    	pnGroup.setGroupName("Test Group");
    	spaceHasGroup.setIsOwner(1);
    	spaceHasGroup.setPnGroup(pnGroup);
    	list.add(spaceHasGroup);
    	
    	expect(mockSpaceHasGroupDAO.findAll()).andReturn(list);
    	replay(mockSpaceHasGroupDAO);
    	list = spaceHasGroupService.getAll();
    	assertNotSame(0, list.size());
    	verify(mockSpaceHasGroupDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnSpaceHasGroupServiceImpl#getByFilter(PnSpaceHasGroup)
     */
    public final void testGetByFilter(){
    	List<PnSpaceHasGroup> list = new ArrayList<PnSpaceHasGroup>();
    	expect(mockSpaceHasGroupDAO.findAll()).andReturn(list);
    	replay(mockSpaceHasGroupDAO);
    	list = mockSpaceHasGroupDAO.findAll();
    	assertEquals(0, list.size());
    	verify(mockSpaceHasGroupDAO);
    }
}
