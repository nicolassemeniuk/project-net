package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.dao.IPnAssignmentWorkDAO;
import net.project.hibernate.model.PnAssignmentWork;
import net.project.hibernate.service.impl.PnAssignmentWorkServiceImpl;
import net.project.base.hibernate.TestBase;

public class PnAssignmentWorkServiceImplTest extends TestBase{
	
	private PnAssignmentWorkServiceImpl pnAssignmentWorkService;
	
	private IPnAssignmentWorkDAO mockAssignmentWorkDAO;
	
	public PnAssignmentWorkServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockAssignmentWorkDAO = createMock(IPnAssignmentWorkDAO.class);
		pnAssignmentWorkService = new PnAssignmentWorkServiceImpl();
		pnAssignmentWorkService.setPnAssignmentWorkDAO(mockAssignmentWorkDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnAssignmentWorkServiceImpl#getTotalWorkByDate(Integer, Date, Date, Integer)
     */
    public final void testGetTotalWorkByDate(){
    	Integer[] personIds = {11};
    	Date startDate = createDate(2008, 1, 29); 
    	Date endDate = createDate(2008, 2, 29); 
    	Integer spaceId = Integer.valueOf(1);
    	
    	List<PnAssignmentWork> list = new ArrayList<PnAssignmentWork>();
    	
    	PnAssignmentWork pnAssignmentWork = new PnAssignmentWork();
    	pnAssignmentWork.setObjectId(1);
    	pnAssignmentWork.setPersonId(1);
    	pnAssignmentWork.setAssignmentWorkId(12);
    	pnAssignmentWork.setObjectName("Test Assignment");
    	
    	list.add(pnAssignmentWork);
    	
    	expect(mockAssignmentWorkDAO.getTotalWorkByDate(personIds, startDate, endDate, spaceId)).andReturn(list);
    	replay(mockAssignmentWorkDAO);
    	List<PnAssignmentWork> assignmentWorks = pnAssignmentWorkService.getTotalWorkByDate(personIds, startDate, endDate, spaceId);
    	assertEquals(1, assignmentWorks.size());
    	verify(mockAssignmentWorkDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnAssignmentWorkServiceImpl#getTotalWorkByDate(Integer, Date, Date, Integer)
     */
    public final void testGetTotalWorkByDateWithNoAssignments(){
    	Integer[] personIds = {11};
    	Date startDate = createDate(2008, 1, 29); 
    	Date endDate = createDate(2008, 2, 29); 
    	Integer spaceId = Integer.valueOf(1);
    	
    	List<PnAssignmentWork> list = new ArrayList<PnAssignmentWork>();
    	expect(mockAssignmentWorkDAO.getTotalWorkByDate(personIds, startDate, endDate, spaceId)).andReturn(list);
    	replay(mockAssignmentWorkDAO);
    	List<PnAssignmentWork> assignmentWorks = pnAssignmentWorkService.getTotalWorkByDate(personIds, startDate, endDate, spaceId);
    	assertEquals(0, assignmentWorks.size());
    	verify(mockAssignmentWorkDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnAssignmentWorkServiceImpl#getWorkDetailsById(Integer)
     */
    public final void testGetWorkDetailsById(){
    	Integer assignmentWorkId = Integer.valueOf(1);
    	
    	PnAssignmentWork pnAssignmentWork = new PnAssignmentWork();
    	pnAssignmentWork.setObjectId(2);
    	pnAssignmentWork.setPersonId(1);
    	pnAssignmentWork.setAssignmentWorkId(11);
    	pnAssignmentWork.setObjectName("Test Work Assignment");
    	
    	expect(mockAssignmentWorkDAO.getWorkDetailsById(assignmentWorkId)).andReturn(pnAssignmentWork);
    	replay(mockAssignmentWorkDAO);
    	PnAssignmentWork assignmentWork = pnAssignmentWorkService.getWorkDetailsById(assignmentWorkId);
    	assertEquals(2, assignmentWork.getObjectId().intValue());
    	assertTrue(StringUtils.isNotEmpty(assignmentWork.getObjectName()));
    	verify(mockAssignmentWorkDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnAssignmentWorkServiceImpl#getWorkDetailsById(Integer)
     */
    public final void testGetWorkDetailsByIdWithoutWork(){
    	Integer assignmentWorkId = Integer.valueOf(1);
    	PnAssignmentWork pnAssignmentWork = new PnAssignmentWork();
    	expect(mockAssignmentWorkDAO.getWorkDetailsById(assignmentWorkId)).andReturn(pnAssignmentWork);
    	replay(mockAssignmentWorkDAO);
    	PnAssignmentWork assignmentWork = pnAssignmentWorkService.getWorkDetailsById(assignmentWorkId);
    	assertEquals(null, assignmentWork.getObjectId());
    	assertTrue(StringUtils.isEmpty(assignmentWork.getObjectName()));
    	verify(mockAssignmentWorkDAO);
    }
}
