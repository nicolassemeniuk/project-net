package net.project.hibernate.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnPortfolioDAO;
import net.project.hibernate.model.PnPortfolio;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.test.AssertThrows;

import net.project.form.CreateDateField;

public class PnPortfolioDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnPortfolioDAO pnPortfolioDAO;
	
	public PnPortfolioDAOImplTest() {
		setPopulateProtectedVariables(true);
	}	 
	
	/* Test method for
	 * @see net.project.hibernate.dao.IPnPortfolioDAO#getPortfolioForSpace(Integer)
	 */
	public void testGetPortfolioForSpace() {
		Integer spaceId = 412323;
		try {
			PnPortfolio pnPortfolio = pnPortfolioDAO.getPortfolioForSpace(spaceId);
			assertNotNull(pnPortfolio);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
}
