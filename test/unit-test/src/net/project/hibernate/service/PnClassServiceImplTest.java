package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.dao.IPnClassDAO;
import net.project.hibernate.model.PnClass;
import net.project.hibernate.service.impl.PnClassServiceImpl;
import junit.framework.TestCase;

public class PnClassServiceImplTest extends TestCase{
	
	private PnClassServiceImpl pnClassService;
	
	private IPnClassDAO mockClassDAO; 
	
	public PnClassServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockClassDAO = createMock(IPnClassDAO.class);
		pnClassService = new PnClassServiceImpl();
		pnClassService.setPnClassDAO(mockClassDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnClassServiceImpl#getFormNamesForSpace(String)
     */
    public final void testGetFormNamesForSpace(){
    	String spaceId = "1";
    	List<PnClass> list = new ArrayList<PnClass>();
    	
    	PnClass pnClass = new PnClass();
    	pnClass.setClassId(1);
    	pnClass.setOwnerSpaceId(1);
    	pnClass.setClassName("Test Form");
    	
    	list.add(pnClass);
    	expect(mockClassDAO.getFormNamesForSpace(spaceId)).andReturn(list);
    	replay(mockClassDAO);
    	List<PnClass> forms = pnClassService.getFormNamesForSpace(spaceId);
    	assertEquals(1, list.size());
    	verify(mockClassDAO);
    }
    
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnClassServiceImpl#getFormNamesForSpace(String)
     */
    public final void testGetFormNamesForSpaceWithNoForms(){
    	String spaceId = "1";
    	List<PnClass> list = new ArrayList<PnClass>();
    	expect(mockClassDAO.getFormNamesForSpace(spaceId)).andReturn(list);
    	replay(mockClassDAO);
    	List<PnClass> forms = pnClassService.getFormNamesForSpace(spaceId);
    	assertEquals(0, list.size());
    	verify(mockClassDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnClassServiceImpl#getFormWithRecordStatus(Integer)
     */
    public final void testGetFormWithRecordStatus(){
    	Integer classId = 1;
    	
    	PnClass pnClass = new PnClass();
    	pnClass.setClassId(1);
    	pnClass.setOwnerSpaceId(1);
    	pnClass.setClassName("Test Form");
    	pnClass.setRecordStatus("A");
    	
    	expect(mockClassDAO.getFormWithRecordStatus(classId)).andReturn(pnClass);
    	replay(mockClassDAO);
    	PnClass class1 = pnClassService.getFormWithRecordStatus(classId);
    	assertEquals("A", class1.getRecordStatus());
    	assertEquals(1, class1.getOwnerSpaceId().intValue());
    	verify(mockClassDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnClassServiceImpl#getFormWithRecordStatus(Integer)
     */
    public final void testGetFormWithRecordStatusWithNoRecordsFound(){
    	Integer classId = 1;
    	PnClass pnClass = new PnClass();
    	expect(mockClassDAO.getFormWithRecordStatus(classId)).andReturn(pnClass);
    	replay(mockClassDAO);
    	PnClass class1 = pnClassService.getFormWithRecordStatus(classId);
    	assertTrue(StringUtils.isEmpty(class1.getRecordStatus()));
    	assertEquals(null, class1.getOwnerSpaceId());
    	verify(mockClassDAO);
    }

}
