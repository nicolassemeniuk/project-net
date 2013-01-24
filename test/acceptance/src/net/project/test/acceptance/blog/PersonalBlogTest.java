package net.project.test.acceptance.blog;

import net.project.test.acceptance.engine.PnetTestEngine;

public class PersonalBlogTest extends PnetTestEngine {
	
	public void testPersonalBlogTest() throws Exception{
		_framework.goToPersonal();
		_framework.clickLinkWithExactText("My Blog");
		_framework.assertTextPresent("Email");
		_framework.assertTextNotPresent("Team");
		_framework.assertTextPresent("New Entry");
		_framework.clickLinkWithExactText("New Entry");
		
		//_framework.assertTextPresent("Subject :");
		//_framework.assertFormElementPresent("title");
		//_framework.setTextField("title", "First Title");
		//_framework.assertFormElementPresent("content");
		//_framework.setTextArea("content", "First Entry Content");
		//_framework.clickLinkWithExactText("Submit");
		//_framework.assertTextPresent("First Title");
		//_framework.assertTextPresent("First Entry Content");
	}
}
