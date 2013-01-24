package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnUserDAO;

public class PnUserDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnUserDAO pnUserDAO;
	
	public PnUserDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnUserDAO.isOnline(Integer)
	 */
	public void testIsOnline(){
		Integer userId = 497434;
		try {
			Boolean isUserOnline = pnUserDAO.isOnline(userId);
			assertNotNull(isUserOnline);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnUserDAO.getUsersCount()
	 */
	public void testGetUsersCount(){
		try {
			Integer numberOfUsers = pnUserDAO.getUsersCount();
			assertNotNull(numberOfUsers);
			assertTrue(numberOfUsers.intValue() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
