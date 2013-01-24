package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashSet;
import java.util.Set;

import net.project.hibernate.dao.IPnGroupTypeDAO;
import net.project.hibernate.model.PnGroup;
import net.project.hibernate.model.PnGroupType;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.service.impl.PnGroupTypeServiceImpl;
import junit.framework.TestCase;

public class PnGroupTypeServiceImplTest extends TestCase{
	
	private PnGroupTypeServiceImpl groupTypeService;
	
	private IPnGroupTypeDAO mockGroupTypeDAO;
	
	public PnGroupTypeServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		mockGroupTypeDAO = createStrictMock(IPnGroupTypeDAO.class);
		groupTypeService = new PnGroupTypeServiceImpl();
		groupTypeService.setGroupTypeDAO(mockGroupTypeDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnGroupTypeServiceImpl#getByObjectId(Integer)
     */
    public final void testGetByObjectId() {
    	Integer objectId = 1;
    	
    	PnGroupType pnGroupType = new PnGroupType();
    	pnGroupType.setClassName("Test Group");
    	pnGroupType.setGroupTypeId(1);
    	PnGroup group = new PnGroup();
    	group.setGroupId(1);
    	PnObject pnObject = new PnObject();
    	pnObject.setObjectId(1);
    	group.setPnObject(pnObject);
    	
    	Set<PnGroup> pnGroups = new HashSet<PnGroup>();
    	pnGroups.add(group);
    	pnGroupType.setPnGroups(pnGroups);
    	
    	expect(mockGroupTypeDAO.findByPimaryKey(objectId)).andReturn(pnGroupType);
    	replay(mockGroupTypeDAO);
    	PnGroupType groupType = groupTypeService.getByObjectId(objectId);
    	assertNotNull(groupType);
    	assertEquals(1, groupType.getPnGroups().size());
    	verify(mockGroupTypeDAO);
    }
    
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnGroupTypeServiceImpl#getByObjectId(Integer)
     */
    public final void testGetByObjectIdWithNoGroups() {
    	Integer objectId = 1;
    	PnGroupType pnGroupType = new PnGroupType();
    	expect(mockGroupTypeDAO.findByPimaryKey(objectId)).andReturn(pnGroupType);
    	replay(mockGroupTypeDAO);
    	PnGroupType groupType = groupTypeService.getByObjectId(objectId);
    	assertEquals(null, groupType.getGroupTypeId());
    	verify(mockGroupTypeDAO);
    }
}
