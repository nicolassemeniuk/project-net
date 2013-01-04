/**
 * 
 */
package net.project.hibernate.service.impl;

import java.util.Collection;
import java.util.Iterator;

import net.project.hibernate.service.ITaskIndentionHandler;
import net.project.schedule.Schedule;
import net.project.schedule.TaskListUtils;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service(value="workplanTaskIndentionHandler")
public class TaskIndentionHandlerImpl implements ITaskIndentionHandler {
	
	private static Logger log = Logger.getLogger(TaskIndentionHandlerImpl.class);

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ITaskIndentionHandler#unIndentTask(java.lang.String)
	 */
	public String unIndentTask(String taskId ,  Schedule clonedSchedule) {
		String idList[] = taskId.split(",");
		try {
			TaskListUtils.unindentTasks(idList, clonedSchedule);
		} catch (Exception e) {
			log.error("Error occured while unindent task " + e.getMessage());
			return e.getLocalizedMessage();
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ITaskIndentionHandler#IndentTask(java.lang.String, java.lang.String, java.lang.String, net.project.schedule.Schedule)
	 */
	public String IndentTask(String taskId, String entryAbove, Schedule clonedSchedule) {
		String errorMessage = "";
		String idList[] = taskId.split(",");
		try {
			ErrorReporter errorReporter = TaskListUtils.indentTasks(idList, entryAbove, clonedSchedule);
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
		} catch (Exception e) {
			log.error("Error occured while indenting task " + e.getMessage());
		}
		
		return  errorMessage ;
	}

}
