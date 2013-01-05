package net.project.hibernate.service;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;
import net.project.hibernate.dao.IPnPersonDAO;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.service.impl.PnPersonServiceImpl;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PnPersonServiceImplTest extends TestCase{

    /**
     * The logger
     */
   // private final Logger log = Logger.getLogger(PnPersonServiceImplTest.class);	
	     
    private PnPersonServiceImpl service;
    
    private IPnPersonDAO mockDao;
    
	public PnPersonServiceImplTest() {
		super();
	}

	@BeforeMethod
	protected void setUp() throws Exception {
		//log.debug("setUpDao for PnPersonServiceImplTest");
		System.out.println("setUpDao for PnPersonServiceImplTest");
		mockDao = createStrictMock(IPnPersonDAO.class);
		service = new PnPersonServiceImpl();
		service.setPnPersonDAO(mockDao);
	}

	@AfterMethod
	protected void tearDown() throws Exception {

	}	
	
	
	/*
	 * Test method for 'net.project.hibernate.service.impl.PnPersonServiceImpl.getOnlineMembers(Integer)'
	 */
	@Test()
	public void testGetOnlineMembers() {
		//log.debug("testing GetOnlineMembers");
		System.out.println("testing GetOnlineMembers");		
		Calendar accessCalendar = GregorianCalendar.getInstance();

		List<Teammate> teammates = new ArrayList<Teammate>();
		Teammate teammamte = new Teammate();
		// last acces time is current time
		teammamte.setPersonId(1);
		teammamte.setLastAccessTime(accessCalendar.getTime());
		teammates.add(teammamte);
		
        //last acces time is before 10min
		accessCalendar.add(Calendar.MINUTE, -10);
		teammamte = new Teammate();		
		teammamte.setPersonId(2);
		teammamte.setLastAccessTime(accessCalendar.getTime());
		teammates.add(teammamte);
		
	    //last acces time is before 40min
		accessCalendar.add(Calendar.MINUTE, -30);
		teammamte = new Teammate();		
		teammamte.setPersonId(3);
		teammamte.setLastAccessTime(accessCalendar.getTime());
		teammates.add(teammamte);				
		
        //last acces time is before 2DAYS
		accessCalendar.add(Calendar.DATE, -2);
		teammamte = new Teammate();		
		teammamte.setPersonId(4);
		teammamte.setLastAccessTime(accessCalendar.getTime());
		teammates.add(teammamte);	
		
		Integer spaceId = 1;
		// set expected behaviour on dao
		expect(mockDao.getOnlineMembers(spaceId)).andReturn(teammates);
		replay(mockDao);		
		
		List<Teammate> onlineTeammates = service.getOnlineMembers(spaceId);
		//check if size of result list is corect
		//assertEquals(2, onlineTeammates.size());
		//chcdck if list contains correct objects
		//assertEquals(1, onlineTeammates.get(0).getPersonId().intValue());
		//assertEquals(2, onlineTeammates.get(1).getPersonId().intValue());
		verify(mockDao);
	}

	/*
	 * Test method for 'net.project.hibernate.service.impl.PnPersonServiceImpl.getOnlineMembers(Integer)'
	 */
	@Test()
	public void testGetOnlineMembersWithEmptyList() {
		//log.debug("testing GetOnlineMembers");
		System.out.println("testing GetOnlineMembers with empty list");		
		List<Teammate> teammates = new ArrayList<Teammate>();
		Integer spaceId = 1;
		// set expected behaviour on dao
		expect(mockDao.getOnlineMembers(spaceId)).andReturn(teammates);
		replay(mockDao);		
		
		List<Teammate> onlineTeammates = service.getOnlineMembers(spaceId);
       //check if size of result list is corect
		assertEquals(0, onlineTeammates.size());		
		verify(mockDao);		
	}
	
	/*
	 * Test method for 'net.project.hibernate.service.impl.PnPersonServiceImpl.getPersonsByBusinessId(Integer)'
	 */
	@Test
	public void testGetPersonsByBusinessId(){
		System.out.println("Testing getPersonsByBusinessId ");
		List<PnPerson> personList = new ArrayList<PnPerson>();
		personList.add(new PnPerson(1));
		personList.add(new PnPerson(2));
		Integer businessId = 1;
		
		// set expected behaviour on dao
		expect(mockDao.getPersonsByBusinessId(businessId)).andReturn(personList);
		replay(mockDao);
		
		List<PnPerson> personList2 = service.getPersonsByBusinessId(businessId);
		assertEquals(1, personList2.get(0).getPersonId().intValue());
		assertEquals(2, personList2.get(1).getPersonId().intValue());
		verify(mockDao);
	}
	
	/*
	 * Test method for 'net.project.hibernate.service.impl.PnPersonServiceImpl.getAllPersonsIds(Integer)'
	 */
	@Test
	public void testGetAllPersonsIds(){
		List<PnPerson> personList = new ArrayList<PnPerson>();
		personList.add(new PnPerson(1));
		personList.add(new PnPerson(2));
		Integer personId = 497434;
		
		// set expected behaviour on dao
		expect(mockDao.getAllPersonsIds(personId)).andReturn(personList);
		replay(mockDao);
		
		List<PnPerson> personList2 = service.getAllPersonsIds(personId);
		assertEquals(1, personList2.get(0).getPersonId().intValue());
		assertEquals(2, personList2.get(1).getPersonId().intValue());
		verify(mockDao);			
	}
	
	/*
	 * Test method for 'net.project.hibernate.service.impl.PnPersonServiceImpl.getPersonsByProjectId(Integer)'
	 */
	@Test
	public void testGetPersonsByProjectId(){
		List<PnPerson> personList = new ArrayList<PnPerson>();
		personList.add(new PnPerson(1));
		personList.add(new PnPerson(2));
		Integer projectId = 477997;
		
		// set expected behaviour on dao
		expect(mockDao.getPersonsByProjectId(projectId)).andReturn(personList);
		replay(mockDao);
		
		List<PnPerson> personList2 = service.getPersonsByProjectId(projectId);
		assertEquals(1, personList2.get(0).getPersonId().intValue());
		assertEquals(2, personList2.get(1).getPersonId().intValue());
		verify(mockDao);			
	}
}