package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import net.project.hibernate.dao.IPnDocRepositoryBaseDAO;
import net.project.hibernate.model.PnDocRepositoryBase;
import net.project.hibernate.service.impl.PnDocRepositoryBaseServiceImpl;

public class PnDocRepositoryBaseServiceImplTest extends TestCase {
	
	private PnDocRepositoryBaseServiceImpl pnDocRepositoryBaseService;
	
	private IPnDocRepositoryBaseDAO mockDocRepositoryBaseDAO;
	
	
	public PnDocRepositoryBaseServiceImplTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockDocRepositoryBaseDAO = createMock(IPnDocRepositoryBaseDAO.class);
		pnDocRepositoryBaseService = new PnDocRepositoryBaseServiceImpl();
		pnDocRepositoryBaseService.setDao(mockDocRepositoryBaseDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.impl.PnDiscussionGroupServiceImpl#findById(Integer)
     */
	public final void testFindById(){
		Integer repositoryId = 1;
		
		PnDocRepositoryBase pnDocRepositoryBase = new PnDocRepositoryBase();
		pnDocRepositoryBase.setRepositoryId(1);
		
		expect(mockDocRepositoryBaseDAO.findByPimaryKey(repositoryId)).andReturn(pnDocRepositoryBase);
		replay(mockDocRepositoryBaseDAO);
		PnDocRepositoryBase docRepositoryBase = pnDocRepositoryBaseService.findByPK(repositoryId);
		assertEquals(1, docRepositoryBase.getRepositoryId().intValue());
		verify(mockDocRepositoryBaseDAO);		
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.impl.PnDiscussionGroupServiceImpl#findById(Integer)
     */
	public final void testFindByIdWithNoRepositoryBase(){
		Integer repositoryId = 1;
		
		PnDocRepositoryBase pnDocRepositoryBase = new PnDocRepositoryBase();
		
		expect(mockDocRepositoryBaseDAO.findByPimaryKey(repositoryId)).andReturn(pnDocRepositoryBase);
		replay(mockDocRepositoryBaseDAO);
		PnDocRepositoryBase docRepositoryBase = pnDocRepositoryBaseService.findByPK(repositoryId);
		assertEquals(null, docRepositoryBase.getRepositoryId());
		verify(mockDocRepositoryBaseDAO);		
	}
}
