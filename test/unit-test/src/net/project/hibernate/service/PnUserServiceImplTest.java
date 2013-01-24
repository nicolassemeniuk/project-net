package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import net.project.hibernate.dao.IPnUserDAO;
import net.project.hibernate.service.impl.PnUserServiceImpl;
import junit.framework.TestCase;

public class PnUserServiceImplTest extends TestCase{
	
	private PnUserServiceImpl userService;
	
	private IPnUserDAO mockUserDAO;
	
	public PnUserServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockUserDAO = createMock(IPnUserDAO.class);
		userService = new PnUserServiceImpl();
		userService.setPnUserDAO(mockUserDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnUserServiceImpl#isOnline(Integer)
     */
    public final void testIsOnline() {
    	Boolean isUserOnline = new Boolean(true);
    	Integer userId = 497434;
    	expect(mockUserDAO.isOnline(userId)).andReturn(isUserOnline);
    	replay(mockUserDAO);
    	Boolean userOnline = userService.isOnline(userId);
    	assertTrue(userOnline);
    	verify(mockUserDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnUserServiceImpl#getUsersCount()
     */
    public final void testGetUsersCount() {
    	Integer count = new Integer(1);
    	expect(mockUserDAO.getUsersCount()).andReturn(count);
    	replay(mockUserDAO);
    	Integer usersCount = userService.getUsersCount();
    	assertEquals(1, usersCount.intValue());
    	verify(mockUserDAO);
    }
}
