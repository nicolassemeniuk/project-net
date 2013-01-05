package net.project.test.acceptance.blog;

import net.project.test.acceptance.engine.PnetTestEngine;

public class BlogBase extends PnetTestEngine{

	private String projectName;
	
	private String businessName;
	
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setupBlog();
	}
	
	@Override
	public void tearDown() throws Exception {
		// delete businesses and project for teardown
		// so that we dont end up with garbage data in tested application
		_framework.deleteBusiness(businessName);
		super.tearDown();
	}
	
	public void setupBlog(){
		
		businessName = _framework.createNewBusiness("Blog");
		projectName = _framework.createNewProject("Blog", businessName);
		
		_framework.goToPersonal();
		_framework.clickLinkWithExactText(projectName);
		_framework.clickLinkWithExactText("Blog");
		_framework.assertTextPresent(projectName);
		_framework.assertTextPresent("New Entry");
		//_framework.clickLinkWithExactText("New Entry");
		//_framework.assertTextPresent("Subject :");
		//_framework.assertFormElementPresent("title");
		//_framework.setTextField("title", "First Title");
		//_framework.assertFormElementPresent("content");
		//_framework.setTextArea("content", "First Entry Content");
		//_framework.clickLinkWithExactText("Submit");
		//_framework.assertTextPresent("First Title");
		//_framework.assertTextPresent("First Entry Content");
	}
	
	public void goToProjectBlog(){
		_framework.goToPersonal();
		_framework.clickLinkWithExactText(projectName);
		_framework.clickLinkWithExactText("Blog");
	}
	
	public String getProjectName(){
		return projectName;
	}
	
	public void setProjectName(String strVal){
		projectName = strVal;
	}
}
