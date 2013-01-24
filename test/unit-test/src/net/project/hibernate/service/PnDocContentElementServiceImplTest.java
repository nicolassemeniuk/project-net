package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.dao.IPnDocContentElementDAO;
import net.project.hibernate.model.PnDocContentElement;
import net.project.hibernate.service.filters.IPnDocContentElementFilter;
import net.project.hibernate.service.impl.PnDocContentElementServiceImpl;
import junit.framework.TestCase;

public class PnDocContentElementServiceImplTest extends TestCase {

	private PnDocContentElementServiceImpl pnDocContentElementService;
	
	private IPnDocContentElementDAO mockDocContentElementDAO;
	
	public PnDocContentElementServiceImplTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockDocContentElementDAO = createMock(IPnDocContentElementDAO.class);
		pnDocContentElementService = new PnDocContentElementServiceImpl();
		pnDocContentElementService.setDao(mockDocContentElementDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.impl.PnDocContentElementServiceImpl#findByPK(Integer)
     */
	public final void testFindByIdWithNoContentElements() {
		Integer docContentId = 1;
		
		PnDocContentElement pnDocContentElement = new PnDocContentElement();
		
		expect(mockDocContentElementDAO.findByPimaryKey(docContentId)).andReturn(pnDocContentElement);
		replay(mockDocContentElementDAO);
		
		PnDocContentElement docContentElement = pnDocContentElementService.findByPK(docContentId);
		
		assertEquals(null, docContentElement.getDocContentId());
		verify(mockDocContentElementDAO);
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.impl.PnDocContentElementServiceImpl#findByPK(Integer)
     */
	public final void testFindByPK() {
		Integer docContentId = 1;
		
		PnDocContentElement pnDocContentElement = new PnDocContentElement();
		pnDocContentElement.setDocContentId(1);
		
		expect(mockDocContentElementDAO.findByPimaryKey(docContentId)).andReturn(pnDocContentElement);
		replay(mockDocContentElementDAO);
		
		PnDocContentElement docContentElement = pnDocContentElementService.findByPK(docContentId);
		
		assertEquals(1, docContentElement.getDocContentId().intValue());
		verify(mockDocContentElementDAO);
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.impl.PnDocContentElementServiceImpl#findByFilter(IPnDocContentElementFilter)
     */
	public final void testFindByFilter() {
		final Integer docContentId = 1;		
		List<PnDocContentElement> list = new ArrayList<PnDocContentElement>();
		PnDocContentElement pnDocContentElement = new PnDocContentElement();
		pnDocContentElement.setDocContentId(1);
		pnDocContentElement.setDisplaySequence(1);
		
		list.add(pnDocContentElement);
		
		expect(mockDocContentElementDAO.findAll()).andReturn(list);
		replay(mockDocContentElementDAO);
		List<PnDocContentElement> elements = pnDocContentElementService.findByFilter( new IPnDocContentElementFilter(){
			public boolean isSuitable(PnDocContentElement object){
				object.setDocContentId(1);
				return object.getDocContentId().equals(docContentId);
			}
		});
		assertEquals(1, elements.size());
		verify(mockDocContentElementDAO);
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.impl.PnDocContentElementServiceImpl#findByFilter(IPnDocContentElementFilter)
     */
	public final void testFindByFilterWithNoElements() {
		final Integer docContentId = 1;		
		List<PnDocContentElement> list = new ArrayList<PnDocContentElement>();
		expect(mockDocContentElementDAO.findAll()).andReturn(list);
		replay(mockDocContentElementDAO);
		List<PnDocContentElement> elements = pnDocContentElementService.findByFilter( new IPnDocContentElementFilter(){
			public boolean isSuitable(PnDocContentElement object){
				object.setDocContentId(1);
				return object.getDocContentId().equals(docContentId);
			}
		});
		assertEquals(0, elements.size());
		verify(mockDocContentElementDAO);
	}
}
