package net.project.test.acceptance;

import net.project.test.acceptance.engine.PnetTestEngine;

// Vladimir Malykhin

public class LoginTest extends PnetTestEngine {
	
	/*public static void main(String[] args){
		junit.textui.TestRunner.run(new TestSuite(LoginTest.class));
	}*/

	
	public void testLogin() {
		_framework.login(LOGIN_NAME, PASSWORD);
		assertTitleEquals("Project.net");
	}
}
