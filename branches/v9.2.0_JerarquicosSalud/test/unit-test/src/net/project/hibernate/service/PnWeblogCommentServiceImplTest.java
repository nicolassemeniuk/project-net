package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.BlogTestBase;
import net.project.hibernate.dao.IPnWeblogCommentDAO;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.service.impl.PnWeblogCommentServiceImpl;
import net.project.space.Space;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PnWeblogCommentServiceImplTest extends BlogTestBase {

	private PnWeblogCommentServiceImpl service;

	private IPnWeblogCommentDAO mockDAO;

	List<PnWeblogComment> blogComments = new ArrayList<PnWeblogComment>();

	//@Before
	public void setUp() throws Exception {
		System.out.println("setUpDao for PnWeblogCommentServiceImplTest");
		mockDAO = createMock(IPnWeblogCommentDAO.class);
		service = new PnWeblogCommentServiceImpl();
		service.setPnWeblogCommentDAO(mockDAO);
		/*
		PnWeblogComment pnWeblogComment = getWeblogComment(1, 11001, Space.PERSONAL_SPACE);
		expect(mockDAO.create(pnWeblogComment)).andReturn(new Integer(1));
		replay(mockDAO);
		service.save(pnWeblogComment);
		blogComments.add(pnWeblogComment);
		reset(mockDAO);
		
		pnWeblogComment = getWeblogComment(2, 11002, Space.PROJECT_SPACE);
		expect(mockDAO.create(pnWeblogComment)).andReturn(new Integer(1));
		replay(mockDAO);
		service.save(pnWeblogComment);
		blogComments.add(pnWeblogComment);
		reset(mockDAO);*/
	}
	
	@Test
	public void testSave(){
	/*	PnWeblogComment pnWeblogComment = getWeblogComment(3, 11002, Space.PROJECT_SPACE);
		expect(mockDAO.create(pnWeblogComment)).andReturn(new Integer(1));
		replay(mockDAO);
		
		service.save(pnWeblogComment);
		blogComments.add(pnWeblogComment);
		reset(mockDAO);
		
		assertEquals(3, blogComments.size());
		reset(mockDAO);*/
	}
	/*
	@Test
	public void testDelete(){
		// set expected behaviour on dao
		expect(mockDAO.findByPimaryKey(2)).andReturn(blogComments.get(1));
		replay(mockDAO);
		
		blogComments.remove(service.get(2));
		
		assertEquals(1, blogComments.size());
		reset(mockDAO);
	}

	@Test
	public void testGet() {
		// set expected behaviour on dao
		expect(mockDAO.findByPimaryKey(1)).andReturn(blogComments.get(0));
		replay(mockDAO);

		assertEquals("Comment contents from user_1", service.get(1).getContent());
		reset(mockDAO);
		
		// set expected behaviour on dao
		expect(mockDAO.findByPimaryKey(2)).andReturn(blogComments.get(1));
		replay(mockDAO);

		assertEquals("Comment contents from user_2", service.get(2).getContent());
		reset(mockDAO);
	}
	
	@Test
	public void testFindAll() {
		// set expected behaviour on dao
		expect(mockDAO.findAll()).andReturn(blogComments);
		replay(mockDAO);

		assertEquals(2, service.findAll().size());
		reset(mockDAO);		
	}
	*/
	@After
	public void tearDown() throws Exception {
	}

}
