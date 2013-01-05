package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnTaskDAO;
import net.project.hibernate.model.PnTask;

public class PnTaskDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnTaskDAO pnTaskDAO;
	
	public PnTaskDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnTaskDAO.getTasksByProjectId(Integer)
	 */
	public void testGetTasksByProjectId() throws Exception {
		try	{
			Integer projectId = 972052;
			List<PnTask> tasks = pnTaskDAO.getTasksByProjectId(projectId);
			assertNotNull(tasks);
			assertTrue(tasks.size() > 0);
		}catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnTaskDAO.getTaskDetailsById(Integer)
	 */
	public void testGetTaskDetailsById() throws Exception {
		try {
			Integer taskId = 972142;
			PnTask pnTask = pnTaskDAO.getTaskDetailsById(taskId);
			assertNotNull(pnTask);
			assertTrue(StringUtils.isNotEmpty(pnTask.getTaskName()));
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnTaskDAO.getProjectMilestones(Integer, boolean)
	 */
	public void testGetProjectMilestonesWithoutPhases() throws Exception {
		try {
			Integer projectId = 972052;
			boolean onlyWithoutPhases = false;
			List<PnTask> tasks = pnTaskDAO.getProjectMilestones(projectId, onlyWithoutPhases);
			assertNotNull(tasks);
			assertTrue(tasks.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnTaskDAO.getProjectMilestones(Integer, boolean)
	 */
	public void testGetProjectMilestonesWithPhases() throws Exception {
		try {
			Integer projectId = 972052;
			boolean onlyWithoutPhases = true;
			List<PnTask> tasks = pnTaskDAO.getProjectMilestones(projectId, onlyWithoutPhases);
			assertNotNull(tasks);
			assertTrue(tasks.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnTaskDAO.getProjectByTaskId(Integer)
	 */
	public void testGetProjectByTaskId() throws Exception {
		try {
			Integer taskId = 972142;
			Integer projectId = pnTaskDAO.getProjectByTaskId(taskId);
			assertNotNull(projectId);
			assertEquals(972052, projectId.intValue());
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnTaskDAO.getTaskWithRecordStatus(Integer)
	 */
	public void testGetTaskWithRecordStatus() throws Exception {
		Integer taskId = 972142;
		try {
			PnTask pnTask = pnTaskDAO.getTaskWithRecordStatus(taskId);
			assertNotNull(pnTask);
			assertEquals("A", pnTask.getRecordStatus());
		} catch (Exception pnetEx) {
			assertTrue(false);
		} 
	}
}	
