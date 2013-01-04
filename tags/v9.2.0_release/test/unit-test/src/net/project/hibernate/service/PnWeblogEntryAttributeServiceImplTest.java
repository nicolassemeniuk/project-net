package net.project.hibernate.service;


import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.BlogTestBase;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.dao.IPnWeblogEntryAttributeDAO;
import net.project.hibernate.model.PnWeblogEntryAttribute;
import net.project.hibernate.service.impl.PnWeblogEntryAttributeServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PnWeblogEntryAttributeServiceImplTest extends BlogTestBase {
	
	private PnWeblogEntryAttributeServiceImpl service;

	private IPnWeblogEntryAttributeDAO mockDAO;

	List<PnWeblogEntryAttribute> blogEntryAttributes = new ArrayList<PnWeblogEntryAttribute>();

	@Before
	public void setUp() throws Exception {
		System.out.println("setUpDao for PnWeblogEntryAttributeServiceImplTest");
		mockDAO = createMock(IPnWeblogEntryAttributeDAO.class);
		service = new PnWeblogEntryAttributeServiceImpl();
		service.setPnWeblogEntryAttributeDAO(mockDAO);

		PnWeblogEntryAttribute pnWeblogEntryAttribute  = getWeblogEntryAttribute("spaceId", "11001");
		expect(mockDAO.create(pnWeblogEntryAttribute)).andReturn(new Integer(1));
		replay(mockDAO);
		service.save(pnWeblogEntryAttribute);
		blogEntryAttributes.add(pnWeblogEntryAttribute);
		reset(mockDAO);

		pnWeblogEntryAttribute = getWeblogEntryAttribute("taskId", "22001");
		expect(mockDAO.create(pnWeblogEntryAttribute)).andReturn(new Integer(1));
		replay(mockDAO);
		service.save(pnWeblogEntryAttribute);
		blogEntryAttributes.add(pnWeblogEntryAttribute);
		reset(mockDAO);			
	}
	
	@Test
	public void testSave(){
		// set expected behaviour on dao
		PnWeblogEntryAttribute pnWeblogEntryAttribute  = getWeblogEntryAttribute("spaceId", "11001");
		expect(mockDAO.create(pnWeblogEntryAttribute)).andReturn(new Integer(1));
		replay(mockDAO);
		
		service.save(pnWeblogEntryAttribute);
		blogEntryAttributes.add(pnWeblogEntryAttribute);
		reset(mockDAO);
		
		assertEquals(3, blogEntryAttributes.size());
		reset(mockDAO);
	}
	
	@Test
	public void testDelete(){
		//	set expected behaviour on dao
		expect(mockDAO.getWeblogEntryAtribute(1, "taskId")).andReturn(blogEntryAttributes.get(1));
		replay(mockDAO);
		
		blogEntryAttributes.remove(service.getWeblogEntryAtribute(1, "taskId"));
		
		assertEquals(1, blogEntryAttributes.size());
		reset(mockDAO);
	}
	
	@Test
	public void testGetWeblogEntryAttribute() {
		// set expected behaviour on dao
		expect(mockDAO.getWeblogEntryAtribute(1, "spaceId")).andReturn(blogEntryAttributes.get(0));
		replay(mockDAO);

		assertEquals("spaceId", service.getWeblogEntryAtribute(1, "spaceId").getName());
		reset(mockDAO);
		
		// set expected behaviour on dao
		expect(mockDAO.getWeblogEntryAtribute(1, "spaceId")).andReturn(blogEntryAttributes.get(0));
		replay(mockDAO);

		assertEquals("11001", service.getWeblogEntryAtribute(1, "spaceId").getValue());
		reset(mockDAO);
		
		// set expected behaviour on dao
		expect(mockDAO.getWeblogEntryAtribute(1, "taskId")).andReturn(blogEntryAttributes.get(1));
		replay(mockDAO);

		assertEquals("taskId", service.getWeblogEntryAtribute(1, "taskId").getName());
		reset(mockDAO);	
		
		// set expected behaviour on dao
		expect(mockDAO.getWeblogEntryAtribute(1, "taskId")).andReturn(blogEntryAttributes.get(1));
		replay(mockDAO);

		assertEquals("22001", service.getWeblogEntryAtribute(1, "taskId").getValue());
		reset(mockDAO);	
	}
	
	@Test
	public void testFindAll() {
		// set expected behaviour on dao
		expect(mockDAO.findAll()).andReturn(blogEntryAttributes);
		replay(mockDAO);

		assertEquals(2, service.findAll().size());
		reset(mockDAO);
	}
	
	@After
	public void tearDown() throws Exception {
	}

}
