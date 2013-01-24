/**
 * 
 */
package net.project.hibernate.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.service.ITaskLinkUnLinkHandler;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskDependency;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value="taskLinkUnlinkHandler")
public class TaskLinkUnlinkHandlerImpl implements ITaskLinkUnLinkHandler {
	

	private static Logger log  = Logger.getLogger(TaskLinkUnlinkHandlerImpl.class);

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ITaskLinkUnLinkHandler#linkTasks(java.lang.String, net.project.schedule.Schedule)
	 */
	public String linkTasks(String taskidList, Schedule schedule)  {
		String errorMessage="";
		ErrorReporter errorReporter=null;
		String idList[] = taskidList.split(",");
		// Check to make sure there aren't any shared tasks
		Map tasks = schedule.getEntryMap();
		List sharedTasksNames = new ArrayList();
		for (int i = 1; i < idList.length; i++) {
			String taskID = idList[i];
			ScheduleEntry task = (ScheduleEntry) tasks.get(taskID);
			if (task.isFromShare()/* && task.isShareReadOnly() */) {
				sharedTasksNames.add(task.getNameMaxLength40());
			}
		}
		if (!sharedTasksNames.isEmpty()) {
			errorMessage = PropertyProvider.get("prm.schedule.main.linktasks.sharereadonly.error");
		} else {
			if (idList.length > 0) {
				try{
					errorReporter = TaskDependency.linkTasks(idList, schedule);
					if(errorReporter.errorsFound()){
						errorMessage="<ul>";
						Collection errorList=errorReporter.getErrorDescriptions();
						Iterator it=errorList.iterator();
						while(it.hasNext()){
							ErrorDescription errorDescription = (ErrorDescription)it.next();
							errorMessage+=	" <li>"+errorDescription.getErrorText()+"</li><br/>";			
						}
						errorMessage+="</ul>";
					}else{
						errorMessage="";
					}
				}catch(Exception e){
					log.error("error occured while linking task"+e.getMessage());
				}
			}
		}
		return  errorMessage ;
		
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ITaskLinkUnLinkHandler#unlinkTasks(java.lang.String, net.project.schedule.Schedule)
	 */
	public String unlinkTasks(String taskIdString, Schedule schedule)  {
		String[] idList = taskIdString.split(",");
		if (idList.length > 0) {
			try {
				TaskDependency.unlinkTasks(idList, schedule);
			} catch (PersistenceException e) {
				log.error("error occured while unlinking task"+e.getMessage());
				return e.getLocalizedMessage();
			}
		}
		return "";
	}

		

	
	


}
