package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.dao.IPnDocContainerListViewDAO;
import net.project.hibernate.model.PnDocContainerListView;
import net.project.hibernate.model.PnSpaceHasDocSpace;
import net.project.hibernate.model.PnSpaceHasDocSpacePK;
import net.project.hibernate.service.impl.PnDocContainerListViewServiceImpl;
import junit.framework.TestCase;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class PnDocContainerListViewServiceImplTest extends TestCase{
	
	private PnDocContainerListViewServiceImpl containerListViewService;
	
	private IPnDocContainerListViewDAO mockDocContainerListViewDAO;
	
	public PnDocContainerListViewServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockDocContainerListViewDAO = createMock(IPnDocContainerListViewDAO.class);
		containerListViewService = new PnDocContainerListViewServiceImpl();
		containerListViewService.setDocContainerListViewDAO(mockDocContainerListViewDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	 /* 
	 * Test method for
     * @see net.project.hibernate.service.PnDocContainerListViewServiceImpl#getAllContainersDocument(String)
     */
	public final void testGetAllContainersDocument(){
		String spaceID = "477997";
		
		List<PnDocContainerListView> list = new ArrayList<PnDocContainerListView>();
		PnDocContainerListView pnDocContainerListView = new PnDocContainerListView();
		pnDocContainerListView.setAuthor("test author");
		PnSpaceHasDocSpace pnSpaceHasDocSpace = new PnSpaceHasDocSpace();
		PnSpaceHasDocSpacePK comp_id = new PnSpaceHasDocSpacePK();
		comp_id.setDocSpaceId(1);
		comp_id.setSpaceId(477997);
		pnSpaceHasDocSpace.setComp_id(comp_id);
		
		list.add(pnDocContainerListView);
		
		expect(mockDocContainerListViewDAO.getAllContainersDocument(spaceID)).andReturn(list);
		replay(mockDocContainerListViewDAO);
		List<PnDocContainerListView> docuements = containerListViewService.getAllContainersDocument(spaceID);
		assertNotNull(docuements);
		verify(mockDocContainerListViewDAO);
	}
	
	 /* 
	 * Test method for
     * @see net.project.hibernate.service.PnDocContainerListViewServiceImpl#getAllContainersDocument(String)
     */
	public final void testGetAllContainersDocumentWithNoDocuments(){
		String spaceID = "477997";
		
		List<PnDocContainerListView> list = new ArrayList<PnDocContainerListView>();
		PnDocContainerListView pnDocContainerListView = new PnDocContainerListView();
		PnSpaceHasDocSpace pnSpaceHasDocSpace = new PnSpaceHasDocSpace();
		PnSpaceHasDocSpacePK comp_id = new PnSpaceHasDocSpacePK();
		comp_id.setSpaceId(477997);
		pnSpaceHasDocSpace.setComp_id(comp_id);
		
		expect(mockDocContainerListViewDAO.getAllContainersDocument(spaceID)).andReturn(list);
		replay(mockDocContainerListViewDAO);
		List<PnDocContainerListView> docuements = containerListViewService.getAllContainersDocument(spaceID);
		assertEquals(0, docuements.size());
		verify(mockDocContainerListViewDAO);
	}
}
