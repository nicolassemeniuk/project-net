package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.BlogTestBase;
import net.project.hibernate.dao.IPnWeblogDAO;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnWeblog;
import net.project.hibernate.service.impl.PnWeblogServiceImpl;
import net.project.space.Space;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PnWeblogServiceImplTest extends BlogTestBase {

	private PnWeblogServiceImpl service;

	private IPnWeblogDAO mockDao;

	private List<PnWeblog> weblogs = new ArrayList<PnWeblog>();
	
	private PnPerson person;

	//@Before
	public void setUp() throws Exception {
		System.out.println("setUpDao for PnWeblogServiceImplTest");
		mockDao = createMock(IPnWeblogDAO.class);
		service = new PnWeblogServiceImpl();
		service.setPnWeblogDAO(mockDao);
		/*
		person = new PnPerson();
		person.setDisplayName("Display Name");
		person.setFirstName("FirstName");
		person.setLastName("LastName");
		person.setPersonId(new Integer(1));

		PnWeblog pnWeblog = getWeblog(1, 11001, Space.PERSONAL_SPACE, person);
		expect(mockDao.create(pnWeblog)).andReturn(new Integer(1));
		replay(mockDao);
		service.save(pnWeblog);
		weblogs.add(pnWeblog);
		reset(mockDao);

		pnWeblog = getWeblog(1, 11002, Space.PROJECT_SPACE, person);
		expect(mockDao.create(pnWeblog)).andReturn(new Integer(1));
		replay(mockDao);
		service.save(pnWeblog);
		weblogs.add(pnWeblog);
		reset(mockDao);
		
		person.setPersonId(new Integer(2));

		pnWeblog = getWeblog(2, 11001, Space.PERSONAL_SPACE, person);
		expect(mockDao.create(pnWeblog)).andReturn(new Integer(1));
		replay(mockDao);
		service.save(pnWeblog);
		weblogs.add(pnWeblog);
		reset(mockDao);

		pnWeblog = getWeblog(2, 11002, Space.PROJECT_SPACE, person);
		expect(mockDao.create(pnWeblog)).andReturn(new Integer(1));
		replay(mockDao);
		service.save(pnWeblog);
		weblogs.add(pnWeblog);
		reset(mockDao);*/
	}

	@Test
	public void testSave() {
	/*	// set expected behaviour on dao
		PnWeblog pnWeblog = getWeblog(3, 11003, Space.PROJECT_SPACE, person);
		expect(mockDao.create(pnWeblog)).andReturn(new Integer(1));
		replay(mockDao);
		
		service.save(pnWeblog);
		weblogs.add(pnWeblog);
		reset(mockDao);
		
		assertEquals(5, weblogs.size());
		reset(mockDao);*/
	}
	/*
	@Test
	public void testDelete() {
		// set expected behaviour on dao
		expect(mockDao.getByUserId(3)).andReturn(weblogs.get(3));
		replay(mockDao);
		
		weblogs.remove(service.getByUserId(3));
		
		assertEquals(3, weblogs.size());
		reset(mockDao);
	}

	@Test
	public void testFindAll() {
		// set expected behaviour on dao
		expect(mockDao.findAll()).andReturn(weblogs);
		replay(mockDao);

		assertEquals(4, service.findAll().size());
		reset(mockDao);
	}

	@Test
	public void testGetByUserId() {
		// set expected behaviour on dao
		expect(mockDao.getByUserId(1)).andReturn(weblogs.get(0));
		replay(mockDao);

		assertEquals("Display Name_1", service.getByUserId(1).getName());
		reset(mockDao);

		// set expected behaviour on dao
		expect(mockDao.getByUserId(2)).andReturn(weblogs.get(2));
		replay(mockDao);

		assertEquals("Display Name_2", service.getByUserId(2).getName());
		reset(mockDao);
	}

	@Test
	public void testGetBySpaceId() {
		// set expected behaviour on dao
		expect(mockDao.getBySpaceId(11002)).andReturn(weblogs.get(3));
		replay(mockDao);

		assertEquals("Project Name_11002", service.getBySpaceId(11002).getName());
		reset(mockDao);
	}
	*/
	@After
	public void tearDown() throws Exception {
	}
}
