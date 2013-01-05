package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.dao.IPnDirectoryDAO;
import net.project.hibernate.model.PnDirectory;
import net.project.hibernate.service.impl.PnDirectoryServiceImpl;
import junit.framework.TestCase;

public class PnDirectoryServiceImplTest extends TestCase{
	
	private PnDirectoryServiceImpl pnDirectoryService;
	
	private IPnDirectoryDAO mockDirectoryDAO;
	
	public PnDirectoryServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockDirectoryDAO = createMock(IPnDirectoryDAO.class);
		pnDirectoryService = new PnDirectoryServiceImpl();
		pnDirectoryService.setPnDirectoryDAO(mockDirectoryDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnDirectoryServiceImpl#getDefaultDirectory(String)
     */
    public final void testGetDefaultDirectory(){
    	List<PnDirectory> list = new ArrayList<PnDirectory>();
    	
    	PnDirectory pnDirectory = new PnDirectory();
    	pnDirectory.setBindUsername("testuser");
    	pnDirectory.setDirectoryId(100);
    	pnDirectory.setDirectoryName("Test Directory");
    	list.add(pnDirectory);
    	
    	expect(mockDirectoryDAO.getDefaultDirectory()).andReturn(list);
    	replay(mockDirectoryDAO);
    	List<PnDirectory> directories = pnDirectoryService.getDefaultDirectory();
    	assertEquals(1, list.size());
    	verify(mockDirectoryDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnDirectoryServiceImpl#getDefaultDirectory(String)
     */
    public final void testGetDefaultDirectoryWithNoDirectories(){
    	List<PnDirectory> list = new ArrayList<PnDirectory>();
    	expect(mockDirectoryDAO.getDefaultDirectory()).andReturn(list);
    	replay(mockDirectoryDAO);
    	List<PnDirectory> directories = pnDirectoryService.getDefaultDirectory();
    	assertEquals(0, list.size());
    	verify(mockDirectoryDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnDirectoryServiceImpl#getDirectory(Integer)
     */
    public final void testGetDirectory(){
    	Integer directoryId = 1;
    	
    	PnDirectory pnDirectory = new PnDirectory();
    	pnDirectory.setBindUsername("testuser");
    	pnDirectory.setDirectoryId(1);
    	pnDirectory.setDirectoryName("Test Directory");
    	
    	expect(mockDirectoryDAO.findByPimaryKey(directoryId)).andReturn(pnDirectory);
    	replay(mockDirectoryDAO);
    	PnDirectory directory = pnDirectoryService.getDirectory(directoryId);
    	assertEquals(1, pnDirectory.getDirectoryId().intValue());
    	verify(mockDirectoryDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnDirectoryServiceImpl#getDirectory(Integer)
     */
    public final void testGetDirectoryWithNoDirectories(){
    	Integer directoryId = 1;
    	PnDirectory pnDirectory = new PnDirectory();
    	expect(mockDirectoryDAO.findByPimaryKey(directoryId)).andReturn(pnDirectory);
    	replay(mockDirectoryDAO);
    	PnDirectory directory = pnDirectoryService.getDirectory(directoryId);
    	assertEquals(null, pnDirectory.getDirectoryId());
    	verify(mockDirectoryDAO);
    }
}
