package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import net.project.hibernate.dao.IPnDocContainerDAO;
import net.project.hibernate.dao.IPnDocTypeDAO;
import net.project.hibernate.model.PnDocContainer;
import net.project.hibernate.model.PnDocContainerHasObject;
import net.project.hibernate.model.PnDocProviderHasDocSpacePK;
import net.project.hibernate.service.filters.IPnDocContainerFilter;
import net.project.hibernate.service.impl.PnDocContainerServiceImpl;
import net.project.hibernate.service.impl.PnDocTypeServiceImpl;
import net.project.base.hibernate.TestBase;

public class PnDocContainerServiceImplTest extends TestCase{

	private PnDocContainerServiceImpl pnDocContainerService;
	
	private IPnDocContainerDAO mockPnDocContainerDAO;
	
	private TestBase date;
	
	public PnDocContainerServiceImplTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		date = new TestBase();
		mockPnDocContainerDAO = createMock(IPnDocContainerDAO.class);
		pnDocContainerService = new PnDocContainerServiceImpl();
		pnDocContainerService.setPnDocContainerDAO(mockPnDocContainerDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.IPnDocContainerService#getDocContainer(Integer)
	 */
	public final void testGetDocContainer() {
		Integer docContainerId = 1;
		Date crc = date.createDate(2009,9,5); 
		int isHidden = 0;
		String recordStatus = "Active";
		PnDocContainer pnDocContainer = new PnDocContainer(docContainerId,isHidden,crc,recordStatus);
		
		expect(mockPnDocContainerDAO.findByPimaryKey(docContainerId)).andReturn(pnDocContainer);
		replay(mockPnDocContainerDAO);
		
		PnDocContainer docContainer = pnDocContainerService.getDocContainer(docContainerId);
		
		assertEquals(1, docContainer.getDocContainerId().intValue());
		verify(mockPnDocContainerDAO);
    }
	
	/* Test method for
	 * @see net.project.hibernate.service.IPnDocContainerService#getDocContainer(Integer)
	 * With No Doc Container values
	 */
	public final void testGetDocContainerWithNoDocContainer() {
		Integer docContainerId = 1;
		PnDocContainer pnDocContainer = new PnDocContainer();
		
		expect(mockPnDocContainerDAO.findByPimaryKey(docContainerId)).andReturn(pnDocContainer);
		replay(mockPnDocContainerDAO);
		
		PnDocContainer docContainer = pnDocContainerService.getDocContainer(docContainerId);
		
		assertEquals(null, docContainer.getDocContainerId());
		verify(mockPnDocContainerDAO);
    }
	
	/* Test method for
	 * @see net.project.hibernate.service.IPnDocContainerService#getDocContainerWithRecordStatus(Integer)
	 */
	public final void testGetDocContainerWithRecordStatus() {
		Integer docContainerId = 1;
		Date crc = date.createDate(2009,9,5); 
		int isHidden = 0;
		String recordStatus = "A";
		
		PnDocContainer pnDocContainer = new PnDocContainer(docContainerId,isHidden,crc,recordStatus);
		
		expect(mockPnDocContainerDAO.getDocContainerWithRecordStatus(docContainerId)).andReturn(pnDocContainer);
		replay(mockPnDocContainerDAO);
		PnDocContainer docContainer = pnDocContainerService.getDocContainerWithRecordStatus(docContainerId);
		assertEquals(1, docContainer.getDocContainerId().intValue());
		verify(mockPnDocContainerDAO);
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.IPnDocContainerService#getDocContainerWithIsHidden(Integer)
	 */
	public final void testGetDocContainerWithIsHidden() {
		Integer docContainerId = 1;
		Date crc = date.createDate(2009,9,5); 
		int isHidden = 0;
		String recordStatus = "Active";
		
		PnDocContainer pnDocContainer = new PnDocContainer(docContainerId,isHidden,crc,recordStatus);
		
		expect(mockPnDocContainerDAO.getDocContainerWithIsHidden(docContainerId)).andReturn(pnDocContainer);
		replay(mockPnDocContainerDAO);
		PnDocContainer docContainer = pnDocContainerService.getDocContainerWithIsHidden(docContainerId);
		assertEquals(1, docContainer.getDocContainerId().intValue());
		verify(mockPnDocContainerDAO);
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.IPnDocContainerService#findByFilter(IPnDocContainerFilter)
	 */
	public final void testFindByFilter() {
		List<PnDocContainer> list = new ArrayList<PnDocContainer>();
		final Integer fromContainerId = 1;
		PnDocContainer pnDocContainer = new PnDocContainer();
		pnDocContainer.setDocContainerId(1);
		pnDocContainer.setContainerDescription("Test container");
		
		list.add(pnDocContainer);
		
		expect(mockPnDocContainerDAO.findAll()).andReturn(list);
		replay(mockPnDocContainerDAO);
		List<PnDocContainer> containers = pnDocContainerService.findByFilter(new IPnDocContainerFilter() {
            public boolean isSuitable(PnDocContainer object) {
            	object.setDocContainerId(1);
            	object.setRecordStatus("A");
                return ((object.getDocContainerId().equals(fromContainerId)) && (object.getRecordStatus().toLowerCase().equals("a")));
            }
        });
		assertEquals(1, containers.size());
		verify(mockPnDocContainerDAO);
	}
}
