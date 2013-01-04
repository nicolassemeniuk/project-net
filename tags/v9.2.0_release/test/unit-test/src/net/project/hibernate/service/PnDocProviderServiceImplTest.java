package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import net.project.hibernate.dao.IPnDocProviderDAO;
import net.project.hibernate.model.PnDiscussionGroup;
import net.project.hibernate.model.PnDocProvider;
import net.project.hibernate.service.impl.PnDocProviderServiceImpl;
import java.util.ArrayList;
import java.util.List;

public class PnDocProviderServiceImplTest extends TestCase {

	private PnDocProviderServiceImpl pnDocProviderService;
	
	private IPnDocProviderDAO mockDocProviderDAO;
	
	public PnDocProviderServiceImplTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		mockDocProviderDAO = createMock(IPnDocProviderDAO.class);
		pnDocProviderService = new PnDocProviderServiceImpl();
		pnDocProviderService.setPnDocProviderDAO(mockDocProviderDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	 /* 
	 * Test method for
     * @see net.project.hibernate.service.PnDocProviderServiceImpl#getDocProvider(Integer)
     */
 	public final void testGetDocProvider(){
		Integer docProviderId = 1;
		
		PnDocProvider pnDocProvider = new PnDocProvider();
		pnDocProvider.setDocProviderId(1);
		pnDocProvider.setDocProviderName("Test Document Provider");
		
		expect(mockDocProviderDAO.findByPimaryKey(docProviderId)).andReturn(pnDocProvider);
		replay(mockDocProviderDAO);
		PnDocProvider docProvider = pnDocProviderService.getDocProvider(docProviderId);
		assertEquals(1, docProvider.getDocProviderId().intValue());
		verify(mockDocProviderDAO);
	}
	
	 /* 
	 * Test method for
     * @see net.project.hibernate.service.PnDocProviderServiceImpl#getDocProvider(Integer)
     */
 	public final void testGetDocProviderWithNoDocProvider(){
		Integer docProviderId = 1;
		
		PnDocProvider pnDocProvider = new PnDocProvider();
		
		expect(mockDocProviderDAO.findByPimaryKey(docProviderId)).andReturn(pnDocProvider);
		replay(mockDocProviderDAO);
		PnDocProvider docProviderd= pnDocProviderService.getDocProvider(docProviderId);
		assertEquals(null, docProviderd.getDocProviderId());
		verify(mockDocProviderDAO);
	}
}
