package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import junit.framework.TestCase;

import net.project.hibernate.dao.IPnDocTypeDAO;
import net.project.hibernate.model.PnDocType;
import net.project.hibernate.service.impl.PnDocTypeServiceImpl;

public class PnDocTypeServiceImplTest extends TestCase{

	private PnDocTypeServiceImpl pnDocTypeService;
	
	private IPnDocTypeDAO mockDocTypeDAO;
	
	public PnDocTypeServiceImplTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockDocTypeDAO = createMock(IPnDocTypeDAO.class);
		pnDocTypeService = new PnDocTypeServiceImpl();
		pnDocTypeService.setDao(mockDocTypeDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* Test method for
     * @see net.project.hibernate.service.impl.PnDiscussionGroupServiceImpl#findById(Integer)
     */
	public final void testFindById(){
		Integer docTypeId = 1;
		
		PnDocType pnDocType = new PnDocType();
		pnDocType.setDocTypeId(1);
		pnDocType.setTypeName("test Name");
		
		expect(mockDocTypeDAO.findByPimaryKey(docTypeId)).andReturn(pnDocType);
		replay(mockDocTypeDAO);
		
		PnDocType docType = pnDocTypeService.findById(docTypeId);
		assertEquals(1, pnDocType.getDocTypeId().intValue());
		
		verify(mockDocTypeDAO);		
	}
}
