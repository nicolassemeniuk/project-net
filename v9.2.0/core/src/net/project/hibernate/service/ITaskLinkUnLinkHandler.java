/**
 * 
 */
package net.project.hibernate.service;

import net.project.schedule.Schedule;

public interface ITaskLinkUnLinkHandler {
	
	public String linkTasks(String taskidList, Schedule schedule) ;
	
	public String unlinkTasks(String taskIdString,Schedule schedule) ;

}
