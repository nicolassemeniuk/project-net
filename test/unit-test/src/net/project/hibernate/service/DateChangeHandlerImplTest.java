package net.project.hibernate.service;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import net.project.application.Application;
import net.project.hibernate.service.impl.DateChangeHandlerImpl;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.schedule.Schedule;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.base.hibernate.WorkplanTestBase;

public class DateChangeHandlerImplTest extends WorkplanTestBase{
	private DateChangeHandlerImpl dateChangeService;
	
	private Schedule schedule = null;
	
	public DateChangeHandlerImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Application.login();
		schedule = createSchedule();
		dateChangeService = new DateChangeHandlerImpl();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		schedule = null;
	}
	
	/*
	 * Test method for 
	 * @see 'net.project.hibernate.service.impl.DateChangeHandlerImpl.changeEndDate(String, String, Schedule)'
	 */
	public final void testChangeEndDate(){
		String taskId = "972142";
		String newDateTochange = SessionManager.getUser().getDateFormatter().formatDate(new Date());
		//assertTrue(StringUtils.isEmpty(dateChangeService.changeEndDate(taskId, newDateTochange, schedule)));
	}
	
	/*
	 * Test method for 
	 * @see 'net.project.hibernate.service.impl.DateChangeHandlerImpl.changeStartDate(String, String, Schedule)'
	 */
	public final void testChangeStartDate(){
		String taskId = "972142";
		String newDateTochange = SessionManager.getUser().getDateFormatter().formatDate(new Date());
		//assertTrue(StringUtils.isEmpty(dateChangeService.changeStartDate(taskId, newDateTochange, schedule)));
	}
}
