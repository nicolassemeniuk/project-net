package net.project.test.acceptance.framework;


public class PnetObject {

	public enum PnetObjectType {
		PROJECT, BUSINESS, TASK, MEETING, EVENT, DOCUMENT, WORKFLOW
	}

	private PnetObjectType _type;
	private String _name;

	public PnetObject(String name) {
		_name = name;
	}

	public PnetObjectType getType() {
		return _type;
	}

	public void setType(PnetObjectType type) {
		_type = type;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}
}

class PnetBusinessObject extends PnetObject {
	public PnetBusinessObject(String name) {
		super(name);
		setType(PnetObjectType.BUSINESS);
	}	
}

class PnetProjectObject extends PnetObject {
	private String _businessName = null;

	public PnetProjectObject(String businessName, String name) {
		super(name);
		setType(PnetObjectType.PROJECT);
		_businessName = businessName;
	}

	public String getBusinessName() {
		return _businessName;
	}
}

class PnetWorkflowObject extends PnetObject {
	private String _projectName = null;

	public PnetWorkflowObject(String projectName, String name) {
		super(name);
		setType(PnetObjectType.WORKFLOW);
		_projectName = projectName;
	}

	public String getProjectName() {
		return _projectName;
	}
}

class PnetTaskObject extends PnetObject {
	private String _projectName = null;

	public PnetTaskObject(String projectName, String name) {
		super(name);
		setType(PnetObjectType.TASK);
		_projectName = projectName;
	}

	public String getProjectName() {
		return _projectName;
	}
}

class PnetMeetingObject extends PnetObject {
	//private String _projectName = null;

	public PnetMeetingObject(/*String projectName, */String name) {
		super(name);
		setType(PnetObjectType.MEETING);
		//_projectName = projectName;
	}

	/*public String getProjectName() {
		return _projectName;
	}*/
}

class PnetEventObject extends PnetObject {
	//private String _projectName = null;

	public PnetEventObject(/*String projectName, */String name) {
		super(name);
		setType(PnetObjectType.EVENT);
		//_projectName = projectName;
	}

	/*public String getProjectName() {
		return _projectName;
	}*/
}

