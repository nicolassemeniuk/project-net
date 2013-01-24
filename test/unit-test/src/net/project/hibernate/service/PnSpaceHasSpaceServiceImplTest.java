package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import net.project.hibernate.dao.IPnSpaceHasSpaceDAO;
import net.project.hibernate.model.PnSpaceHasSpace;
import net.project.hibernate.service.filters.IPnSpaceHasGroupServiceFilter;
import net.project.hibernate.service.filters.IPnSpaceHasSpaceFilter;
import net.project.hibernate.service.impl.PnSpaceHasSpaceServiceImpl;
import net.project.space.ISpaceTypes;

public class PnSpaceHasSpaceServiceImplTest extends TestCase{
	
	private PnSpaceHasSpaceServiceImpl spaceHasSpaceService;
	
	private IPnSpaceHasSpaceDAO mockSpaceHasSpaceDAO;
	
	public PnSpaceHasSpaceServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockSpaceHasSpaceDAO = createMock(IPnSpaceHasSpaceDAO.class);
		spaceHasSpaceService = new PnSpaceHasSpaceServiceImpl();
		spaceHasSpaceService.setPnSpaceHasSpaceDAO(mockSpaceHasSpaceDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.IPnSpaceHasSpaceService#findByFilter(IPnSpaceHasSpaceFilter)
     */
    public final void testFindByFilter() {
    	List<PnSpaceHasSpace> list = new ArrayList<PnSpaceHasSpace>();
    	
    	PnSpaceHasSpace pnSpaceHasSpace = new PnSpaceHasSpace();
    	pnSpaceHasSpace.setChildSpaceType(ISpaceTypes.BUSINESS_SPACE);
    	pnSpaceHasSpace.setParentSpaceType(ISpaceTypes.PERSONAL_SPACE);
    	list.add(pnSpaceHasSpace);
    	
    	expect(mockSpaceHasSpaceDAO.findAll()).andReturn(list);
    	replay(mockSpaceHasSpaceDAO);
    	list = mockSpaceHasSpaceDAO.findAll();
    	verify(mockSpaceHasSpaceDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.IPnSpaceHasSpaceService#findByFilter(IPnSpaceHasSpaceFilter)
     */
    public final void testFindByFilterWithEmptyList() {
    	List<PnSpaceHasSpace> list = new ArrayList<PnSpaceHasSpace>();
    	expect(mockSpaceHasSpaceDAO.findAll()).andReturn(list);
    	replay(mockSpaceHasSpaceDAO);
    	list = mockSpaceHasSpaceDAO.findAll();
    	assertEquals(0, list.size());
    	verify(mockSpaceHasSpaceDAO);
    }
}
