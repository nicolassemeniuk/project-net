package net.project.test.acceptance.resource;

import java.util.Date;

import net.project.test.acceptance.engine.PnetTestEngine;

public abstract class ResourceManagementBase extends PnetTestEngine {

	private String businessNameOne;

	private String businessNameTwo;

	private String projectNameOne;

	private String projectNameTwo;

	private String projectNameThree;

	private String projectNameFour;

	private String taskNameOne;

	private String taskNameTwo;

	private String taskNameThree;

	private String taskNameFour;

	private String taskNameFive;

	private String taskNameSix;

	private String taskNameSeven;

	private String taskNameEight;

	private String percentAssigned = "100";

	private Date startDate = new Date(2008, 0, 1);

	private Date endDate = new Date(2008, 2, 31);


	@Override
	public void setUp() throws Exception {
		super.setUp();
		setupAssignments();
	}

	@Override
	public void tearDown() throws Exception {
		// delete businesses and project for teardown
		// so that we dont end up with garbage data in tested application
		_framework.deleteBusiness(businessNameOne);
		_framework.deleteBusiness(businessNameTwo);
		super.tearDown();
	}

	public void setupAssignments() throws Exception {

		// Create first business with 2 projects with 2 Tasks each
		businessNameOne = _framework.createNewBusiness("business");

		projectNameOne = _framework.createNewProject("project", startDate, endDate, businessNameOne);

		taskNameOne = _framework.createNewTask("task", projectNameOne, startDate, endDate);
		percentAssigned = "80";
		_framework.assignResource(taskNameOne, projectNameOne, businessNameOne, percentAssigned);
		taskNameTwo = _framework.createNewTask("task", projectNameOne, startDate, endDate);
		percentAssigned = "40";

		_framework.assignResource(taskNameTwo, projectNameOne, businessNameOne, percentAssigned);

		endDate = new Date(2008, 5, 30);
		projectNameTwo = _framework.createNewProject("project", startDate, endDate, businessNameOne);
		taskNameThree = _framework.createNewTask("task", projectNameTwo, startDate, endDate);
		percentAssigned = "60";
		_framework.assignResource(taskNameThree, projectNameTwo, businessNameOne, percentAssigned);
		taskNameFour = _framework.createNewTask("task", projectNameTwo, startDate, endDate);
		percentAssigned = "30";
		_framework.assignResource(taskNameFour, projectNameTwo, businessNameOne, percentAssigned);

		endDate = new Date(2008, 5, 30);
		// Create second business with 2 projects with 2 Tasks each
		businessNameTwo = _framework.createNewBusiness("business");
		projectNameThree = _framework.createNewProject("project", startDate, endDate, businessNameTwo);
		taskNameFive = _framework.createNewTask("task", projectNameThree, startDate, endDate);
		percentAssigned = "100";
		_framework.assignResource(taskNameFive, projectNameThree, businessNameTwo, percentAssigned);
		taskNameSix = _framework.createNewTask("task", projectNameThree, startDate, endDate);
		percentAssigned = "50";
		_framework.assignResource(taskNameSix, projectNameThree, businessNameTwo, percentAssigned);

		endDate = new Date(2008, 5, 30);
		projectNameFour = _framework.createNewProject("project", startDate, endDate, businessNameTwo);
		taskNameSeven = _framework.createNewTask("task", projectNameFour, startDate, endDate);
		percentAssigned = "70";
		_framework.assignResource(taskNameSeven, projectNameFour, businessNameTwo, percentAssigned);
		taskNameEight = _framework.createNewTask("task", projectNameFour, startDate, endDate);
		percentAssigned = "35";
		_framework.assignResource(taskNameEight, projectNameFour, businessNameTwo, percentAssigned);

	}

	public String getBusinessNameOne() {
		return businessNameOne;
	}

	public void setBusinessNameOne(String businessNameOne) {
		this.businessNameOne = businessNameOne;
	}

	public String getBusinessNameTwo() {
		return businessNameTwo;
	}

	public void setBusinessNameTwo(String businessNameTwo) {
		this.businessNameTwo = businessNameTwo;
	}

	public String getProjectNameFour() {
		return projectNameFour;
	}

	public void setProjectNameFour(String projectNameFour) {
		this.projectNameFour = projectNameFour;
	}

	public String getProjectNameOne() {
		return projectNameOne;
	}

	public void setProjectNameOne(String projectNameOne) {
		this.projectNameOne = projectNameOne;
	}

	public String getProjectNameThree() {
		return projectNameThree;
	}

	public void setProjectNameThree(String projectNameThree) {
		this.projectNameThree = projectNameThree;
	}

	public String getProjectNameTwo() {
		return projectNameTwo;
	}

	public void setProjectNameTwo(String projectNameTwo) {
		this.projectNameTwo = projectNameTwo;
	}

	public String getTaskNameEight() {
		return taskNameEight;
	}

	public void setTaskNameEight(String taskNameEight) {
		this.taskNameEight = taskNameEight;
	}

	public String getTaskNameFive() {
		return taskNameFive;
	}

	public void setTaskNameFive(String taskNameFive) {
		this.taskNameFive = taskNameFive;
	}

	public String getTaskNameFour() {
		return taskNameFour;
	}

	public void setTaskNameFour(String taskNameFour) {
		this.taskNameFour = taskNameFour;
	}

	public String getTaskNameOne() {
		return taskNameOne;
	}

	public void setTaskNameOne(String taskNameOne) {
		this.taskNameOne = taskNameOne;
	}

	public String getTaskNameSeven() {
		return taskNameSeven;
	}

	public void setTaskNameSeven(String taskNameSeven) {
		this.taskNameSeven = taskNameSeven;
	}

	public String getTaskNameSix() {
		return taskNameSix;
	}

	public void setTaskNameSix(String taskNameSix) {
		this.taskNameSix = taskNameSix;
	}

	public String getTaskNameThree() {
		return taskNameThree;
	}

	public void setTaskNameThree(String taskNameThree) {
		this.taskNameThree = taskNameThree;
	}

	public String getTaskNameTwo() {
		return taskNameTwo;
	}

	public void setTaskNameTwo(String taskNameTwo) {
		this.taskNameTwo = taskNameTwo;
	}

}
