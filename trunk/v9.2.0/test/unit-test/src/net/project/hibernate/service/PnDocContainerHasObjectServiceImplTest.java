package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.dao.IPnDocContainerHasObjectDAO;
import net.project.hibernate.model.PnDocContainer;
import net.project.hibernate.model.PnDocContainerHasObject;
import net.project.hibernate.service.filters.IPnDocContainerHasObjectFilter;
import net.project.hibernate.service.impl.PnDocContainerHasObjectServiceImpl;
import junit.framework.TestCase;

public class PnDocContainerHasObjectServiceImplTest extends TestCase{
	
	private PnDocContainerHasObjectServiceImpl containerHasObjectService;
	
	private IPnDocContainerHasObjectDAO mockContainerHasObjectDAO;
	
	public PnDocContainerHasObjectServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockContainerHasObjectDAO = createMock(IPnDocContainerHasObjectDAO.class);
		containerHasObjectService = new PnDocContainerHasObjectServiceImpl();
		containerHasObjectService.setDao(mockContainerHasObjectDAO);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/*
	 * Test method for 
	 * 'net.project.hibernate.service.impl.PnDocContainerHasObjectServiceImpl.findByFilter(IPnDocContainerHasObjectFilter)'
	 */
	public void testFindByFilter(){
		final Integer docContainerId = 1;
		List<PnDocContainerHasObject> list = new ArrayList<PnDocContainerHasObject>();
		PnDocContainerHasObject pnDocContainerHasObject = new PnDocContainerHasObject();
		PnDocContainer pnDocContainer = new PnDocContainer();
		pnDocContainer.setDocContainerId(1);
		pnDocContainer.setContainerName("Test Container");
		pnDocContainerHasObject.setPnDocContainer(pnDocContainer);
		list.add(pnDocContainerHasObject);
		expect(mockContainerHasObjectDAO.findAll()).andReturn(list);
		replay(mockContainerHasObjectDAO);
		List<PnDocContainerHasObject> objects = containerHasObjectService.findByFilter(new IPnDocContainerHasObjectFilter(){
			public boolean isSuitable(PnDocContainerHasObject object){
				PnDocContainer docContainer = new PnDocContainer();
				docContainer.setDocContainerId(1);
				object.setPnDocContainer(docContainer);
				return object.getPnDocContainer().getDocContainerId().equals(docContainerId);
			}
		});
		assertEquals(1, objects.size());
		verify(mockContainerHasObjectDAO);
	}
	
	/*
	 * Test method for 
	 * 'net.project.hibernate.service.impl.PnDocContainerHasObjectServiceImpl.findByFilter(IPnDocContainerHasObjectFilter)'
	 */
	public void testFindByFilterWithNoContainers(){
		final Integer docContainerId = 1;
		List<PnDocContainerHasObject> list = new ArrayList<PnDocContainerHasObject>();
		expect(mockContainerHasObjectDAO.findAll()).andReturn(list);
		replay(mockContainerHasObjectDAO);
		List<PnDocContainerHasObject> objects = containerHasObjectService.findByFilter(new IPnDocContainerHasObjectFilter(){
			public boolean isSuitable(PnDocContainerHasObject object){
				PnDocContainer docContainer = new PnDocContainer();
				docContainer.setDocContainerId(1);
				object.setPnDocContainer(docContainer);
				return object.getPnDocContainer().getDocContainerId().equals(docContainerId);
			}
		});
		assertEquals(0, objects.size());
		verify(mockContainerHasObjectDAO);
	}
}
