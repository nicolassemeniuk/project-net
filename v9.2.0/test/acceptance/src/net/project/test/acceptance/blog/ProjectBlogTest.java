package net.project.test.acceptance.blog;


public class ProjectBlogTest extends BlogBase{
		
	public void testProjectBlog() throws Exception {
		 goToProjectBlog();		
		_framework.assertLinkPresentWithExactText(getProjectName());
		_framework.assertLinkPresentWithExactText("New Entry");
		_framework.assertTextPresent("Email");
		_framework.assertTextPresent("Team");
		_framework.assertTextPresent("Archives");		
	}	
}
