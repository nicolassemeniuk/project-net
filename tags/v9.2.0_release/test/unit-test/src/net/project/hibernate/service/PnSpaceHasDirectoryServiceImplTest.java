package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import net.project.hibernate.dao.IPnSpaceHasDirectoryDAO;
import net.project.hibernate.model.PnDirectory;
import net.project.hibernate.model.PnSpaceHasDirectory;
import net.project.hibernate.model.PnSpaceHasDirectoryPK;
import net.project.hibernate.service.impl.PnSpaceHasDirectoryServiceImpl;
import junit.framework.TestCase;

public class PnSpaceHasDirectoryServiceImplTest extends TestCase{
	
	private PnSpaceHasDirectoryServiceImpl spaceHasDirectoryService;
	
	private IPnSpaceHasDirectoryDAO mockSpaceHasDirectoryDAO;
	
	public PnSpaceHasDirectoryServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockSpaceHasDirectoryDAO = createMock(IPnSpaceHasDirectoryDAO.class);
		spaceHasDirectoryService = new PnSpaceHasDirectoryServiceImpl();
		spaceHasDirectoryService.setPnSpaceHasDirectoryDAO(mockSpaceHasDirectoryDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnNewsServiceImpl#saveSpaceHasDirectory(PnSpaceHasDirectory)
     */
    public final void testSaveSpaceHasDirectory(){
    	PnSpaceHasDirectory pnSpaceHasDirectory = new PnSpaceHasDirectory();
    	
    	PnSpaceHasDirectoryPK comp_id = new PnSpaceHasDirectoryPK();
    	comp_id.setDirectoryId(100);
    	comp_id.setSpaceId(1);
    	pnSpaceHasDirectory.setComp_id(comp_id);
    	PnDirectory pnDirectory = new PnDirectory();
    	pnDirectory.setDirectoryId(100);
    	pnDirectory.setBindUsername("testuser");
    	pnDirectory.setBindPassword("test1234");
    	pnSpaceHasDirectory.setPnDirectory(pnDirectory);
    	
    	expect(mockSpaceHasDirectoryDAO.create(pnSpaceHasDirectory)).andReturn(comp_id);
    	replay(mockSpaceHasDirectoryDAO);
    	PnSpaceHasDirectoryPK spaceHasDirectoryPK = spaceHasDirectoryService.saveSpaceHasDirectory(pnSpaceHasDirectory);
    	assertEquals(100, spaceHasDirectoryPK.getDirectoryId().intValue());
    	assertEquals(1, spaceHasDirectoryPK.getSpaceId().intValue());
    	verify(mockSpaceHasDirectoryDAO);
    }
}
