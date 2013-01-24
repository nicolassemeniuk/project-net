package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.dao.IPnProjectSpaceMetaPropDAO;
import net.project.hibernate.model.PnProjectSpaceMetaProp;
import net.project.hibernate.service.impl.PnProjectSpaceMetaPropServiceImpl;
import junit.framework.TestCase;

public class PnProjectSpaceMetaPropServiceImplTest extends TestCase{
	
	private PnProjectSpaceMetaPropServiceImpl spaceMetaPropService;
	
	private IPnProjectSpaceMetaPropDAO mockProjectSpaceMetaPropDAO;
	
	public PnProjectSpaceMetaPropServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		mockProjectSpaceMetaPropDAO = createStrictMock(IPnProjectSpaceMetaPropDAO.class);
		spaceMetaPropService = new PnProjectSpaceMetaPropServiceImpl();
		spaceMetaPropService.setPnProjectSpaceMetaPropDAO(mockProjectSpaceMetaPropDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnProjectSpaceMetaPropServiceImpl#getProjectSpaceMetaPropByName(String)
     */
    public final void testGetProjectSpaceMetaPropByName() {
    	PnProjectSpaceMetaProp pnProjectSpaceMetaProp = new PnProjectSpaceMetaProp();
    
    	pnProjectSpaceMetaProp.setPropertyId(1);
    	pnProjectSpaceMetaProp.setPropertyName("Test Property");
    	pnProjectSpaceMetaProp.setPropertyType(11);
    	
    	expect(mockProjectSpaceMetaPropDAO.getProjectSpaceMetaPropByName("Test Property")).andReturn(pnProjectSpaceMetaProp);
    	replay(mockProjectSpaceMetaPropDAO);
    	pnProjectSpaceMetaProp = spaceMetaPropService.getProjectSpaceMetaPropByName("Test Property");
    	assertNotNull(pnProjectSpaceMetaProp);
    	verify(mockProjectSpaceMetaPropDAO);
    }
    
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnProjectSpaceMetaPropServiceImpl#getProjectSpaceMetaPropByName(String)
     */
    public final void testGetProjectSpaceMetaPropByNameWithNoValues() {
    	PnProjectSpaceMetaProp pnProjectSpaceMetaProp = new PnProjectSpaceMetaProp();
    	expect(mockProjectSpaceMetaPropDAO.getProjectSpaceMetaPropByName("Test Property")).andReturn(pnProjectSpaceMetaProp);
    	replay(mockProjectSpaceMetaPropDAO);
    	pnProjectSpaceMetaProp = spaceMetaPropService.getProjectSpaceMetaPropByName("Test Property");
    	assertTrue(StringUtils.isEmpty(pnProjectSpaceMetaProp.getPropertyName()));
    	verify(mockProjectSpaceMetaPropDAO);
    }

}
