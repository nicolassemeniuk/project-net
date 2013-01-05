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

import net.project.hibernate.dao.IPnGroupDAO;
import net.project.hibernate.model.PnGroup;
import net.project.hibernate.service.impl.PnGroupServiceImpl;
import net.project.security.group.GroupTypeID;
import junit.framework.TestCase;

public class PnGroupServiceImplTest extends TestCase {
	
	private PnGroupServiceImpl pnGroupService;
	
	private IPnGroupDAO mockGroupDAO;
	
	public PnGroupServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		mockGroupDAO = createStrictMock(IPnGroupDAO.class);
		pnGroupService = new PnGroupServiceImpl();
		pnGroupService.setPnGroupDAO(mockGroupDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnGroupServiceImpl#getGroupForSpaceAndGroupType(Integer,Integer)
     */
    public final void testGetGroupForSpaceAndGroupType() {
    	List<PnGroup> list = new ArrayList<PnGroup>();
    	
    	PnGroup pnGroup = new PnGroup();
    	pnGroup.setGroupName("Test Group");
    	pnGroup.setGroupId(1);
    	pnGroup.setGroupDesc("Test Group Description");
    	list.add(pnGroup);
    	
    	Integer spaceId = 972052;
    	Integer groupTypeId = Integer.valueOf(GroupTypeID.POWER_USER.getID());
    	expect(mockGroupDAO.getGroupForSpaceAndGroupType(spaceId, groupTypeId)).andReturn(list);
    	replay(mockGroupDAO);
    	List<PnGroup> expected = pnGroupService.getGroupForSpaceAndGroupType(spaceId, groupTypeId);
    	assertEquals(1, expected.size());
    	verify(mockGroupDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnGroupServiceImpl#getGroupForSpaceAndGroupType(Integer,Integer)
     */
    public final void testGetGroupForSpaceAndGroupTypeWithEmptyList() {
    	List<PnGroup> list = new ArrayList<PnGroup>();
    	Integer spaceId = 972052;
    	Integer groupTypeId = Integer.valueOf(GroupTypeID.POWER_USER.getID());
    	expect(mockGroupDAO.getGroupForSpaceAndGroupType(spaceId, groupTypeId)).andReturn(list);
    	replay(mockGroupDAO);
    	List<PnGroup> expected = pnGroupService.getGroupForSpaceAndGroupType(spaceId, groupTypeId);
    	assertEquals(0, expected.size());
    	verify(mockGroupDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnGroupServiceImpl#getPrincipalGroupForSpaceAndPerson(Integer,Integer)
     */
    public final void testGetPrincipalGroupForSpaceAndPerson() {
    	PnGroup pnGroup = new PnGroup();
    	Integer spaceId = 972052;
    	Integer personId = 497434;
    	pnGroup.setGroupName("Test Group");
    	pnGroup.setGroupId(1);
    	expect(mockGroupDAO.getPrincipalGroupForSpaceAndPerson(spaceId, personId)).andReturn(pnGroup);
    	replay(mockGroupDAO);
    	PnGroup group = pnGroupService.getPrincipalGroupForSpaceAndPerson(spaceId, personId);
    	assertEquals("Test Group", group.getGroupName());
    	assertEquals(1, group.getGroupId().intValue());
    	verify(mockGroupDAO);
    }
}
