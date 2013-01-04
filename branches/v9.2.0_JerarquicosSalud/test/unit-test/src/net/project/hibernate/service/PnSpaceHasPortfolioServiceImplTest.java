package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import net.project.hibernate.dao.IPnSpaceHasPortfolioDAO;
import net.project.hibernate.model.PnSpaceHasPortfolio;
import net.project.hibernate.model.PnSpaceHasPortfolioPK;
import net.project.hibernate.service.impl.PnSpaceHasPortfolioServiceImpl;
import junit.framework.TestCase;

public class PnSpaceHasPortfolioServiceImplTest extends TestCase{
	
	private PnSpaceHasPortfolioServiceImpl spaceHasPortfolioService;
	
	private IPnSpaceHasPortfolioDAO mockSpaceHasPortfolioDAO;
	
	public PnSpaceHasPortfolioServiceImplTest(){
		super();
	}

	protected void setUp() throws Exception {
		mockSpaceHasPortfolioDAO = createStrictMock(IPnSpaceHasPortfolioDAO.class);
		spaceHasPortfolioService = new PnSpaceHasPortfolioServiceImpl();
		spaceHasPortfolioService.setPnSpaceHasPortfolioDAO(mockSpaceHasPortfolioDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnGroupTypeServiceImpl#getSpaceHasPortfolio(PnSpaceHasPortfolioPK)
     */
    public final void testGetSpaceHasPortfolio() {
    	PnSpaceHasPortfolio pnSpaceHasPortfolio = new PnSpaceHasPortfolio();
    	PnSpaceHasPortfolioPK pnSpaceHasPortfolioPK = new PnSpaceHasPortfolioPK();
    	pnSpaceHasPortfolioPK.setPortfolioId(1);
    	pnSpaceHasPortfolioPK.setSpaceId(1);
    	pnSpaceHasPortfolio.setComp_id(pnSpaceHasPortfolioPK);
    	pnSpaceHasPortfolio.setIsDefault(1);
    	
    	expect(mockSpaceHasPortfolioDAO.findByPimaryKey(pnSpaceHasPortfolioPK)).andReturn(pnSpaceHasPortfolio);
    	replay(mockSpaceHasPortfolioDAO);
    	PnSpaceHasPortfolio spaceHasPortfolio = spaceHasPortfolioService.getSpaceHasPortfolio(pnSpaceHasPortfolioPK);
    	assertEquals(1, spaceHasPortfolio.getComp_id().getPortfolioId().intValue());
    	assertEquals(1, spaceHasPortfolio.getComp_id().getSpaceId().intValue());
    	verify(mockSpaceHasPortfolioDAO);
    }
    
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnGroupTypeServiceImpl#getSpaceHasPortfolio(PnSpaceHasPortfolioPK)
     */
    public final void testGetSpaceHasPortfolioNoPortfolios() {
    	PnSpaceHasPortfolio pnSpaceHasPortfolio = new PnSpaceHasPortfolio();
    	PnSpaceHasPortfolioPK pnSpaceHasPortfolioPK = new PnSpaceHasPortfolioPK();
    	expect(mockSpaceHasPortfolioDAO.findByPimaryKey(pnSpaceHasPortfolioPK)).andReturn(pnSpaceHasPortfolio);
    	replay(mockSpaceHasPortfolioDAO);
    	PnSpaceHasPortfolio spaceHasPortfolio = spaceHasPortfolioService.getSpaceHasPortfolio(pnSpaceHasPortfolioPK);
    	assertEquals(null, spaceHasPortfolio.getComp_id());
    	assertEquals(null, spaceHasPortfolio.getComp_id());
    	verify(mockSpaceHasPortfolioDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnGroupTypeServiceImpl#saveSpaceHasPortfolio(PnSpaceHasPortfolio)
     */
    public final void testSaveSpaceHasPortfolio() {
    	PnSpaceHasPortfolio pnSpaceHasPortfolio = new PnSpaceHasPortfolio();
    	PnSpaceHasPortfolioPK pnSpaceHasPortfolioPK = new PnSpaceHasPortfolioPK();
    	pnSpaceHasPortfolioPK.setPortfolioId(1);
    	pnSpaceHasPortfolioPK.setSpaceId(1);
    	pnSpaceHasPortfolio.setComp_id(pnSpaceHasPortfolioPK);
    	pnSpaceHasPortfolio.setIsDefault(1);
    	
    	expect(mockSpaceHasPortfolioDAO.create(pnSpaceHasPortfolio)).andReturn(pnSpaceHasPortfolioPK);
    	replay(mockSpaceHasPortfolioDAO);
    	PnSpaceHasPortfolioPK spaceHasPortfolioPK = spaceHasPortfolioService.saveSpaceHasPortfolio(pnSpaceHasPortfolio);
    	assertEquals(1, spaceHasPortfolioPK.getPortfolioId().intValue());
    	assertEquals(1, spaceHasPortfolioPK.getSpaceId().intValue());
    	verify(mockSpaceHasPortfolioDAO); 
    }
}
