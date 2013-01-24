package net.project.test.acceptance.framework;

import java.util.Stack;


public class DataCleaner {
	
	private static final DataCleaner singletonInstance = new DataCleaner();
	private static PnetAcceptanceTestFramework _framework;

	/*public DataCleaner() {
	}*/
	
	public static final DataCleaner getInstance(PnetAcceptanceTestFramework framework) {
		_framework = framework;
		return singletonInstance;
    }

	private final Stack<PnetObject> _stack = new Stack<PnetObject>();

	public void registerCreatedObject(PnetObject pnetObject) {
		_stack.push(pnetObject);
	}

	public void removeActualObjects() {
		while (!_stack.isEmpty()) {
			removeObject(_stack.pop());
		}
	}

	private void removeObject(PnetObject pnetObject) {
		switch (pnetObject.getType()) {
			case PROJECT : {
				PnetProjectObject projectObject = (PnetProjectObject) pnetObject;
				_framework.deleteProject(projectObject.getName(), projectObject.getBusinessName());
			}; 
			break;
			
			case BUSINESS : {
				PnetBusinessObject businessObject = (PnetBusinessObject) pnetObject;
				_framework.deleteBusiness(businessObject.getName());
			};
			break;
			
			case TASK : {
				PnetTaskObject taskObject = (PnetTaskObject) pnetObject;
				_framework.deleteTask(taskObject.getName(), taskObject.getProjectName());
			};
			break;
			
			case MEETING : {
				PnetMeetingObject meetingObject = (PnetMeetingObject) pnetObject;
				_framework.deleteMeeting(meetingObject.getName()/*, meetingObject.getProjectName()*/);
			};
			break;
			
			case EVENT : {
				PnetEventObject eventObject = (PnetEventObject) pnetObject;
				_framework.deleteEvent(eventObject.getName()/*, eventObject.getProjectName()*/);
			};
			break;
			
			case WORKFLOW : {
				PnetWorkflowObject workflowObject = (PnetWorkflowObject) pnetObject;
				_framework.deleteWorkflow(workflowObject.getName(), workflowObject.getProjectName());
			};
			break;
			
			case DOCUMENT : {};
			/*case BUSINESS : _framework.deleteBusiness(pnetObject.getName());
			case TASK : _framework.deleteTask(taskName, projectName, businessName);*/
		}
	}
}
