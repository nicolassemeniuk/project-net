package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import net.project.hibernate.dao.IPnDiscussionGroupDAO;
import net.project.hibernate.model.PnDiscussionGroup;
import net.project.hibernate.service.impl.PnDiscussionGroupServiceImpl;
import junit.framework.TestCase;

public class PnDiscussionGroupServiceImplTest extends TestCase{
	
	private PnDiscussionGroupServiceImpl pnDiscussionGroupService;
	
	private IPnDiscussionGroupDAO mockDiscussionGroupDAO;
	
	public PnDiscussionGroupServiceImplTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockDiscussionGroupDAO = createMock(IPnDiscussionGroupDAO.class);
		pnDiscussionGroupService = new PnDiscussionGroupServiceImpl();
		pnDiscussionGroupService.setDao(mockDiscussionGroupDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	 /* 
	 * Test method for
     * @see net.project.hibernate.service.PnDiscussionGroupServiceImpl#getDiscussionGroup(Integer)
     */
	public final void testGetDiscussionGroup(){
		Integer discussionGroupId = 1;
		
		PnDiscussionGroup pnDiscussionGroup = new PnDiscussionGroup();
		pnDiscussionGroup.setDiscussionGroupId(1);
		pnDiscussionGroup.setDiscussionGroupName("Test Discussion Group");
		
		expect(mockDiscussionGroupDAO.findByPimaryKey(discussionGroupId)).andReturn(pnDiscussionGroup);
		replay(mockDiscussionGroupDAO);
		
		PnDiscussionGroup discussionGroup = pnDiscussionGroupService.getDiscussionGroup(discussionGroupId);
		assertEquals(1, pnDiscussionGroup.getDiscussionGroupId().intValue());
		
		verify(mockDiscussionGroupDAO);
	}
		
}
	
