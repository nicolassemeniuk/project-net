package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.dao.IPnDocVersionHasContentDAO;
import net.project.hibernate.model.PnDocVersionHasContent;
import net.project.hibernate.service.filters.IPnDocVersionHasContentFilter;
import net.project.hibernate.service.impl.PnDocVersionHasContentServiceImpl;
import junit.framework.TestCase;

public class PnDocVersionHasContentServiceImplTest extends TestCase{
	
	private PnDocVersionHasContentServiceImpl versionHasContentService;
	
	private IPnDocVersionHasContentDAO mockVersionHasContentDAO;
	
	public PnDocVersionHasContentServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockVersionHasContentDAO = createMock(IPnDocVersionHasContentDAO.class);
		versionHasContentService = new PnDocVersionHasContentServiceImpl();
		versionHasContentService.setDao(mockVersionHasContentDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.impl.PnDocVersionHasContentServiceImpl#findByFilter(IPnDocVersionHasContentFilter)
     */
	public final void testFindByFilter(){
		List<PnDocVersionHasContent> list = new ArrayList<PnDocVersionHasContent>();
		PnDocVersionHasContent pnDocVersionHasContent = new PnDocVersionHasContent();
		pnDocVersionHasContent.setDocId(1);
		
		list.add(pnDocVersionHasContent);
		
		expect(mockVersionHasContentDAO.findAll()).andReturn(list);
		replay(mockVersionHasContentDAO);
		
		List<PnDocVersionHasContent> contents = versionHasContentService.findByFilter(new IPnDocVersionHasContentFilter(){
			public boolean isSuitable(PnDocVersionHasContent object){
				object.setDocId(1);
				return object.getDocId().equals(1);
			}
		});
		assertEquals(1, contents.size());
		verify(mockVersionHasContentDAO);
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.impl.PnDocVersionHasContentServiceImpl#findByFilter(IPnDocVersionHasContentFilter)
     */
	public final void testFindByFilterWithNoContents(){
		List<PnDocVersionHasContent> list = new ArrayList<PnDocVersionHasContent>();
		PnDocVersionHasContent pnDocVersionHasContent = new PnDocVersionHasContent();
		expect(mockVersionHasContentDAO.findAll()).andReturn(list);
		replay(mockVersionHasContentDAO);
		
		List<PnDocVersionHasContent> contents = versionHasContentService.findByFilter(new IPnDocVersionHasContentFilter(){
			public boolean isSuitable(PnDocVersionHasContent object){
				object.setDocId(1);
				return object.getDocId().equals(1);
			}
		});
		assertEquals(0, contents.size());
		verify(mockVersionHasContentDAO);
	}
}
