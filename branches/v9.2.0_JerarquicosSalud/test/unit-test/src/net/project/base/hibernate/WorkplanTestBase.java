package net.project.base.hibernate;

import net.project.hibernate.service.impl.AddTaskServiceImpl;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.schedule.Schedule;
import net.project.space.Space;
import junit.framework.TestCase;

public class WorkplanTestBase extends TestCase {
	
	private Schedule schedule;
	
	public WorkplanTestBase(){
		super();
	}
	
	/**
	 * Create schedule 
	 * @return schedule of "junit test project"
	 */
	public static Schedule createSchedule(){
		Schedule schedule = new Schedule();
		Space projectSpace = new ProjectSpaceBean();
		projectSpace.setID("972052");
		schedule.setSpace(projectSpace);
		try{
			schedule.loadAll();
		}catch (PersistenceException pnetEx) {
			System.out.println("Error occured while loading schedule.."+pnetEx.getMessage());
		}
		return schedule;
	}
}
