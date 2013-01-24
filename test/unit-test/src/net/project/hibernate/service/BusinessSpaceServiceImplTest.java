package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import net.project.application.Application;
import net.project.hibernate.dao.IBusinessSpaceDAO;
import net.project.hibernate.model.PnBusinessSpaceView;
import net.project.hibernate.service.impl.BusinessSpaceServiceImpl;
import net.project.security.SessionManager;
import net.project.security.User;
import junit.framework.TestCase;

public class BusinessSpaceServiceImplTest extends TestCase{
	
	private BusinessSpaceServiceImpl businessSpaceService;
	
	private IBusinessSpaceDAO mockBusinessSpaceDAO;
	
	public BusinessSpaceServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Application.login();
		mockBusinessSpaceDAO = createMock(IBusinessSpaceDAO.class);
		businessSpaceService = new BusinessSpaceServiceImpl();
		businessSpaceService.setBusinessSpaceDAO(mockBusinessSpaceDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.BusinessSpaceServiceImpl#findByUser(User, String)
     */
    public final void testFindByUser(){
    	User user = SessionManager.getUser();
    	String recordStatus = "A";
    	
    	List<PnBusinessSpaceView> list = new ArrayList<PnBusinessSpaceView>();
    	
    	PnBusinessSpaceView pnBusinessSpaceView = new PnBusinessSpaceView();
    	pnBusinessSpaceView.setBusinessId(12);
    	pnBusinessSpaceView.setBusinessName("Test Business");
    	pnBusinessSpaceView.setBusinessSpaceId(123);
    	pnBusinessSpaceView.setRecordStatus("A");
    	
    	list.add(pnBusinessSpaceView);
    	
    	expect(mockBusinessSpaceDAO.findByUser(user, recordStatus)).andReturn(list);
    	replay(mockBusinessSpaceDAO);
    	List<PnBusinessSpaceView> businesses = businessSpaceService.findByUser(user, recordStatus);
    	assertEquals(1, businesses.size());
    	verify(mockBusinessSpaceDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.BusinessSpaceServiceImpl#findByUser(User, String)
     */
    public final void testFindByUserWithNoBusinesses(){
    	User user = SessionManager.getUser();
    	String recordStatus = "A";
    	
    	List<PnBusinessSpaceView> list = new ArrayList<PnBusinessSpaceView>();
    	expect(mockBusinessSpaceDAO.findByUser(user, recordStatus)).andReturn(list);
    	replay(mockBusinessSpaceDAO);
    	List<PnBusinessSpaceView> businesses = businessSpaceService.findByUser(user, recordStatus);
    	assertEquals(0, businesses.size());
    	verify(mockBusinessSpaceDAO);
    }
}
