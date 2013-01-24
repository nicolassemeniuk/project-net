package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import net.project.hibernate.dao.IPnDocProviderHasDocSpaceDAO;
import net.project.hibernate.model.PnDocProvider;
import net.project.hibernate.model.PnDocProviderHasDocSpace;
import net.project.hibernate.model.PnDocProviderHasDocSpacePK;
import net.project.hibernate.model.PnDocSpace;
import net.project.hibernate.service.impl.PnDocProviderHasDocSpaceServiceImpl;
import net.project.base.hibernate.TestBase;

public class PnDocProviderHasDocSpaceServiceImplTest extends TestCase {

	private PnDocProviderHasDocSpaceServiceImpl pnHasDocSpaceService;
	
	private IPnDocProviderHasDocSpaceDAO mockDocProviderHasDocSpaceDAO;
	
	private TestBase testBase;
	
	public PnDocProviderHasDocSpaceServiceImplTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockDocProviderHasDocSpaceDAO = createMock(IPnDocProviderHasDocSpaceDAO.class);
		pnHasDocSpaceService= new PnDocProviderHasDocSpaceServiceImpl();
		pnHasDocSpaceService.setPnDocProviderHasDocSpaceDAO(mockDocProviderHasDocSpaceDAO);
	}
	
	protected void  tearDown() throws Exception {
		super.tearDown();
	}
	
	 /* 
	 * Test method for
     * @see net.project.hibernate.service.PnDocProviderHasDocSpaceServiceImpl#getDocProviderHasDocSpace(PnDocProviderHasDocSpacePK)
     */
 	public final void testGetDocProviderHasDocSpace(){
		Integer docProviderId = 1;
		Integer docSpaceId = 2;
		
		PnDocProviderHasDocSpacePK comp_id = new PnDocProviderHasDocSpacePK();
		comp_id.setDocProviderId(docProviderId);
		comp_id.setDocSpaceId(docSpaceId);
		
		PnDocProviderHasDocSpace pnDocProviderHasDocSpace = new PnDocProviderHasDocSpace();
		
		pnDocProviderHasDocSpace.setComp_id(comp_id);
		
		expect(mockDocProviderHasDocSpaceDAO.findByPimaryKey(comp_id)).andReturn(pnDocProviderHasDocSpace);
		replay(mockDocProviderHasDocSpaceDAO);
		
		PnDocProviderHasDocSpace docProviderHasDocSpace= pnHasDocSpaceService.getDocProviderHasDocSpace(comp_id);
		
		assertEquals(1, docProviderHasDocSpace.getComp_id().getDocProviderId().intValue());
		assertEquals(2, docProviderHasDocSpace.getComp_id().getDocSpaceId().intValue());
		verify(mockDocProviderHasDocSpaceDAO);
	}
	
	 /* 
	 * Test method for
     * @see net.project.hibernate.service.PnDocProviderHasDocSpaceServiceImpl#getDocProviderHasDocSpace(PnDocProviderHasDocSpacePK)
     */
 	public final void testGetDocProviderHasDocSpaceWithNoDocs(){
		
		PnDocProviderHasDocSpacePK comp_id = new PnDocProviderHasDocSpacePK();
		PnDocProviderHasDocSpace pnDocProviderHasDocSpace = new PnDocProviderHasDocSpace();
		
		pnDocProviderHasDocSpace.setComp_id(comp_id);
		
		expect(mockDocProviderHasDocSpaceDAO.findByPimaryKey(comp_id)).andReturn(pnDocProviderHasDocSpace);
		replay(mockDocProviderHasDocSpaceDAO);
		
		PnDocProviderHasDocSpace docProviderHasDocSpace = pnHasDocSpaceService.getDocProviderHasDocSpace(comp_id);
		
		assertEquals(null, docProviderHasDocSpace.getComp_id().getDocProviderId());
		assertEquals(null, docProviderHasDocSpace.getComp_id().getDocSpaceId());
		verify(mockDocProviderHasDocSpaceDAO);
	}
 	
	 /* 
	 * Test method for
     * @see net.project.hibernate.service.PnDocProviderHasDocSpaceServiceImpl#saveDocProviderHasDocSpace(PnDocProviderHasDocSpace)
     */
 	public final void testSaveDocProviderHasDocSpace(){
 		testBase = new TestBase();
		Integer docProviderId = 1;
		Date docProviderCrc = testBase.createDate(2008, 0, 1);
		String docProviderRecordStatus = "Active";
		int isDefault = 199;
		PnDocProvider pnDocProvider = new PnDocProvider(docProviderId,isDefault,docProviderCrc,docProviderRecordStatus);
		
		Integer docSpaceId = 1;
		Date docSpaceCrc = testBase.createDate(2008, 0, 1);
		String docSpaceRecordStatus = "Active";
		PnDocSpace pnDocSpace = new PnDocSpace(docSpaceId,docSpaceCrc,docSpaceRecordStatus);
	
		PnDocProviderHasDocSpacePK comp_id= new PnDocProviderHasDocSpacePK();
		PnDocProviderHasDocSpace pnDocProviderHasDocSpace = new PnDocProviderHasDocSpace();
				
		pnDocProviderHasDocSpace.setComp_id(comp_id);
		pnDocProviderHasDocSpace.setPnDocProvider(pnDocProvider);
		pnDocProviderHasDocSpace.setPnDocSpace(pnDocSpace);
		
		expect(mockDocProviderHasDocSpaceDAO.create(pnDocProviderHasDocSpace)).andReturn(comp_id);
		replay(mockDocProviderHasDocSpaceDAO);
		
		PnDocProviderHasDocSpacePK docProviderHasDocSpacePK = mockDocProviderHasDocSpaceDAO.create(pnDocProviderHasDocSpace);
		
		assertEquals(docProviderHasDocSpacePK, pnDocProviderHasDocSpace.getComp_id());
		verify(mockDocProviderHasDocSpaceDAO);
	}
}
