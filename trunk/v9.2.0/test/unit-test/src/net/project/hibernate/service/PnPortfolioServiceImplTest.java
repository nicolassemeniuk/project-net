package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashSet;
import java.util.Set;

import net.project.hibernate.dao.IPnPortfolioDAO;
import net.project.hibernate.model.PnPortfolio;
import net.project.hibernate.model.PnSpaceHasPortfolio;
import net.project.hibernate.model.PnSpaceHasPortfolioPK;
import net.project.hibernate.service.impl.PnPortfolioServiceImpl;

import junit.framework.TestCase;

public class PnPortfolioServiceImplTest extends TestCase{
	
	private PnPortfolioServiceImpl portfolioService;
	
	private IPnPortfolioDAO mockPortfolioDAO;
	
	public PnPortfolioServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		mockPortfolioDAO = createStrictMock(IPnPortfolioDAO.class);
		portfolioService = new PnPortfolioServiceImpl();
		portfolioService.setPnPortfolioDAO(mockPortfolioDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnPortfolioServiceImpl#getPortfolioForSpace(Integer)
     */
    public final void testGetPortfolioForSpace() {
    	Integer spaceId = 1;
    	
    	PnPortfolio pnPortfolio = new PnPortfolio();
    	pnPortfolio.setPortfolioId(1);
    	PnSpaceHasPortfolio pnSpaceHasPortfolio = new PnSpaceHasPortfolio();
    	PnSpaceHasPortfolioPK comp_id = new PnSpaceHasPortfolioPK();
    	comp_id.setSpaceId(1);
    	comp_id.setPortfolioId(12);
    	pnSpaceHasPortfolio.setComp_id(comp_id);
    	
    	Set<PnSpaceHasPortfolio> pnSpaceHasPortfolios = new HashSet<PnSpaceHasPortfolio>();
    	pnSpaceHasPortfolios.add(pnSpaceHasPortfolio);
    	pnPortfolio.setPnSpaceHasPortfolios(pnSpaceHasPortfolios);
    	
    	expect(mockPortfolioDAO.getPortfolioForSpace(1)).andReturn(pnPortfolio);
    	replay(mockPortfolioDAO);
    	PnPortfolio portfolio = portfolioService.getPortfolioForSpace(spaceId);
    	assertNotNull(portfolio);
    	verify(mockPortfolioDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnPortfolioServiceImpl#getPortfolioForSpace(Integer)
     */
    public final void testGetPortfolioForSpaceWithNoPortfolio() {
    	Integer spaceId = 1;
    	PnPortfolio pnPortfolio = new PnPortfolio();
    	
    	expect(mockPortfolioDAO.getPortfolioForSpace(1)).andReturn(pnPortfolio);
    	replay(mockPortfolioDAO);
    	PnPortfolio portfolio = portfolioService.getPortfolioForSpace(spaceId);
    	assertNotNull(portfolio);
    	verify(mockPortfolioDAO);
    }
}
