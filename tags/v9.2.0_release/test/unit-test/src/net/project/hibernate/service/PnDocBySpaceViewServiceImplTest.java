package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.dao.IPnDocBySpaceViewDAO;
import net.project.hibernate.model.PnDocBySpaceView;
import net.project.hibernate.service.filters.IPnDocBySpaceViewFilter;
import net.project.hibernate.service.impl.PnDocBySpaceViewServiceImpl;
import junit.framework.TestCase;

public class PnDocBySpaceViewServiceImplTest extends TestCase{
	
	private PnDocBySpaceViewServiceImpl pnDocBySpaceViewService;
	
	private IPnDocBySpaceViewDAO mockDocBySpaceViewDAO;	
	
	public PnDocBySpaceViewServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockDocBySpaceViewDAO = createMock(IPnDocBySpaceViewDAO.class);
		pnDocBySpaceViewService = new PnDocBySpaceViewServiceImpl();
		pnDocBySpaceViewService.setPnDocBySpaceViewDAO(mockDocBySpaceViewDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.PnDocBySpaceViewServiceImpl#findByFilter(IPnDocBySpaceViewFilter)
	 */
	public final void testFindByFilter(){
		final Integer docSpaceId = 12;
		List<PnDocBySpaceView> list = new ArrayList<PnDocBySpaceView>();
		PnDocBySpaceView pnDocBySpaceView = new PnDocBySpaceView();
		pnDocBySpaceView.setDoc_id(1);
		pnDocBySpaceView.setDocSpaceId(12);
		list.add(pnDocBySpaceView);
		expect(mockDocBySpaceViewDAO.findAll()).andReturn(list);
		replay(mockDocBySpaceViewDAO);
		List<PnDocBySpaceView> docSpaces = pnDocBySpaceViewService.findByFilter(new IPnDocBySpaceViewFilter(){
			public boolean isSuitable(PnDocBySpaceView object){
				object.setDocSpaceId(12);
				return object.getDocSpaceId().equals(docSpaceId);
			}
		});
		assertEquals(1, docSpaces.size());
		verify(mockDocBySpaceViewDAO);
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.PnDocBySpaceViewServiceImpl#findByFilter(IPnDocBySpaceViewFilter)
	 */
	public final void testFindByFilterWithNoDocs(){
		final Integer docSpaceId = 12;
		List<PnDocBySpaceView> list = new ArrayList<PnDocBySpaceView>();
		expect(mockDocBySpaceViewDAO.findAll()).andReturn(list);
		replay(mockDocBySpaceViewDAO);
		List<PnDocBySpaceView> docSpaces = pnDocBySpaceViewService.findByFilter(new IPnDocBySpaceViewFilter(){
			public boolean isSuitable(PnDocBySpaceView object){
				object.setDocSpaceId(12);
				return object.getDocSpaceId().equals(docSpaceId);
			}
		});
		assertEquals(0, docSpaces.size());
		verify(mockDocBySpaceViewDAO);
	}
}
