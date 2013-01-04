package net.project.test.acceptance.project.dashboard;

public class LastChangesTest extends ProjectDashboardBase {
	
	public void testStaticData() {
		this.goToProject();
		
		_framework.assertTextPresent("Changes Within 10 Days");
		_framework.assertTextPresent("Blog");
		_framework.assertTextPresent("Wiki");
		_framework.assertTextPresent("Forms");
		_framework.assertTextPresent("Documents");
		_framework.assertTextPresent("Discussions");
	}
	
	public void testForms() {
		this.goToProject();
		String formName = _framework.createNewAssignableForm(this.getProjectName(), true);
		
		this.goToProject();
		_framework.assertTextPresent(formName);
	}
	
	public void testDocuments() {
		this.goToProject();
		
		String prefixName = "TestDocument";
		_framework.assertTextNotPresent(prefixName);
		
		_framework.createNewDocument("TestDocument", this.getProjectName());
		this.goToProject();
		
		_framework.assertTextPresent(prefixName);
	}
	
}
