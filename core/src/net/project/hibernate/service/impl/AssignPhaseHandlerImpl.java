/**
 * 
 */
package net.project.hibernate.service.impl;

import net.project.hibernate.service.IAssignPhaseHandler;
import net.project.persistence.PersistenceException;
import net.project.schedule.TaskListUtils;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value="assignPhaseHandler")
public class AssignPhaseHandlerImpl implements IAssignPhaseHandler {

	private static Logger log = Logger.getLogger(AssignPhaseHandlerImpl.class);
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IAssignPhaseHandler#assignPhase(java.lang.String, java.lang.String,
	 *      java.lang.String, net.project.schedule.Schedule)
	 */
	public String assignPhase(String taskIdList, String pahseId ) {
		String[] idList = taskIdList.split(",");
		try {
			TaskListUtils.setPhase(idList, pahseId);
		} catch (PersistenceException e) {
			log.error("Error occured while setting phase" + e.getMessage());
			return  e.getLocalizedMessage();
		}
		return "";
	}

}
