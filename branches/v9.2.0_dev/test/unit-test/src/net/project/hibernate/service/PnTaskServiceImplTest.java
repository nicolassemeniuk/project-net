package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.project.hibernate.dao.IPnTaskDAO;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.service.impl.PnTaskServiceImpl;

public class PnTaskServiceImplTest extends TestCase{
	
	private PnTaskServiceImpl taskService;
	
	private IPnTaskDAO mockTaskDAO;
	
	public PnTaskServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockTaskDAO = createMock(IPnTaskDAO.class);
		taskService = new PnTaskServiceImpl();
		taskService.setPnTaskDAO(mockTaskDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnTaskServiceImpl#getTasksByProjectId(Integer)
     */
    public final void testGetTasksByProjectId() {
    	List<PnTask> tasks = new ArrayList<PnTask>();
    	Integer projectId = 972052;
    	expect(mockTaskDAO.getTasksByProjectId(projectId)).andReturn(tasks);
    	replay(mockTaskDAO);
    	tasks = taskService.getTasksByProjectId(projectId);
    	assertEquals(0, tasks.size());
    	verify(mockTaskDAO);
    }
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnTaskServiceImpl#getTaskDetailsById(Integer)
     */
    public final void testGetTaskDetailsById() {
    	PnTask pnTask = new PnTask();
    	Integer taskId = 972140;
    	pnTask.setTaskId(972140);
    	expect(mockTaskDAO.getTaskDetailsById(taskId)).andReturn(pnTask);
    	replay(mockTaskDAO);
    	PnTask task = taskService.getTaskDetailsById(taskId);
    	assertNotNull(task.getTaskId());
    	verify(mockTaskDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnTaskServiceImpl#getProjectMilestones(Integer, boolean)
     */
    public final void testGetProjectMilestones() {
    	List<PnTask> tasks = new ArrayList<PnTask>(); 
    	Integer projectId = 972052;
    	boolean onlyWithoutPhases = true;
    	expect(mockTaskDAO.getProjectMilestones(projectId, onlyWithoutPhases)).andReturn(tasks);
    	replay(mockTaskDAO);
    	tasks = taskService.getProjectMilestones(projectId, onlyWithoutPhases);
    	assertEquals(0, tasks.size());
    	verify(mockTaskDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnTaskServiceImpl#getProjectByTaskId(Integer)
     */
    public final void testGetProjectByTaskId() {
    	Integer projectId = new Integer(972052);
    	Integer taskId = 972140;
    	expect(mockTaskDAO.getProjectByTaskId(taskId)).andReturn(projectId);
    	replay(mockTaskDAO);
    	Integer newProjectId = taskService.getProjectByTaskId(taskId);
    	assertNotNull(newProjectId);
    	verify(mockTaskDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnTaskServiceImpl#getTaskWithRecordStatus(Integer)
     */
    public final void testGetTaskWithRecordStatus() {
    	PnTask pnTask = new PnTask();
    	Integer taskId = 972140;
    	pnTask.setRecordStatus("A");
    	expect(mockTaskDAO.getTaskWithRecordStatus(taskId)).andReturn(pnTask);
    	replay(mockTaskDAO);
    	PnTask task = taskService.getTaskWithRecordStatus(taskId);
    	assertEquals("A", task.getRecordStatus());
    	verify(mockTaskDAO);
    }
}
