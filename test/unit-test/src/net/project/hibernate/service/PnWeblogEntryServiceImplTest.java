package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.BlogTestBase;
import net.project.hibernate.dao.IPnWeblogEntryDAO;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.impl.PnWeblogEntryServiceImpl;
import net.project.space.Space;

import org.junit.Before;
import org.junit.Test;

public class PnWeblogEntryServiceImplTest extends BlogTestBase {

	private PnWeblogEntryServiceImpl service;
	
	private IPnWeblogEntryDAO mockDAO;
	
	List<PnWeblogEntry> blogEntries = new ArrayList<PnWeblogEntry>();
	
	//@Before
	protected void setUp() throws Exception {	
		System.out.println("setUpDao for PnWeblogEntryServiceImplTest");
		mockDAO = createMock(IPnWeblogEntryDAO.class);
		service = new PnWeblogEntryServiceImpl();
		service.setPnWeblogEntryDAO(mockDAO);
		/*
		PnWeblogEntry pnWeblogEntry = getWeblogEntry(1, 11001, Space.PERSONAL_SPACE);
		expect(mockDAO.create(pnWeblogEntry)).andReturn(new Integer(1));
		replay(mockDAO);
		service.save(pnWeblogEntry);
		blogEntries.add(pnWeblogEntry);
		reset(mockDAO);

		pnWeblogEntry = getWeblogEntry(2, 11001, Space.PERSONAL_SPACE);
		expect(mockDAO.create(pnWeblogEntry)).andReturn(new Integer(1));
		replay(mockDAO);
		service.save(pnWeblogEntry);
		blogEntries.add(pnWeblogEntry);
		reset(mockDAO);		*/
	}	
	
	@Test
	public void testSave() {
	/*	// set expected behaviour on dao
		PnWeblogEntry pnWeblogEntry = getWeblogEntry(3, 11001, Space.PERSONAL_SPACE);
		expect(mockDAO.create(pnWeblogEntry)).andReturn(new Integer(1));
		replay(mockDAO);
		
		service.save(pnWeblogEntry);
		blogEntries.add(pnWeblogEntry);
		reset(mockDAO);	
		
		assertEquals(3, blogEntries.size());
		reset(mockDAO);*/
	}
	/*
	@Test
	public void testDelete() {
		// set expected behaviour on dao
		expect(mockDAO.getWeblogEntry(2)).andReturn(blogEntries.get(1));
		replay(mockDAO);
		
		blogEntries.remove(service.getWeblogEntry(2));
		
		assertEquals(1, blogEntries.size());
		reset(mockDAO);
	}

	@Test
	public void testGetWeblogEntry() {
		// set expected behaviour on dao
		expect(mockDAO.getWeblogEntry(1)).andReturn(blogEntries.get(0));
		replay(mockDAO);

		assertEquals("Entry from user_1", service.getWeblogEntry(1).getTitle());
		reset(mockDAO);
		
		//set expected behaviour on dao
		expect(mockDAO.getWeblogEntry(1)).andReturn(blogEntries.get(0));
		replay(mockDAO);
		
		assertEquals("Content for Entry form user_1", service.getWeblogEntry(1).getText());
		reset(mockDAO);
		
		// set expected behaviour on dao
		expect(mockDAO.getWeblogEntry(2)).andReturn(blogEntries.get(1));
		replay(mockDAO);

		assertEquals("Entry from user_2", service.getWeblogEntry(2).getTitle());
		reset(mockDAO);
		
		// set expected behaviour on dao
		expect(mockDAO.getWeblogEntry(2)).andReturn(blogEntries.get(1));
		replay(mockDAO);
		
		assertEquals("Content for Entry form user_2", service.getWeblogEntry(2).getText());
		reset(mockDAO);
	}

	@Test
	public void  testFindAll() {
		// set expected behaviour on dao
		expect(mockDAO.findAll()).andReturn(blogEntries);
		replay(mockDAO);

		assertEquals(2, service.findAll().size());
		reset(mockDAO);
	}	
	*/
}
