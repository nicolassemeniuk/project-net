package net.project.test.acceptance.resource;
import net.project.test.acceptance.engine.PnetTestEngine;

public class ResourceManagementTest extends PnetTestEngine
{

	public void testGotoResourceManagementPage() throws Exception {
		_framework.gotoResourceManagementPage();
	}

	public void testAllResourceManagementLinks() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.assertTextPresent("Summary - Cross Project Resource Assignment Summary");

		_framework.clickLinkWithText("Reserve by Resource");
		_framework.assertTextPresent("Reserve By Resource (Entry By Person)");

		_framework.clickLinkWithText("Reserve by Project");
		_framework.assertTextPresent("Reserve By Project (Entry By Project)");

		_framework.clickLinkWithText("View by resource");
		_framework.assertTextPresent("View By Resource - Reservation Summary");

		_framework.clickLinkWithText("Reserved vs. Assigned");
		_framework.assertTextPresent("Reserved vs. Assigned (By Resource)");

		_framework.clickLinkWithText("Tasks Assigned");
		_framework.assertTextPresent("Tasks Assigned - Cross Project Assignment Details");

		_framework.clickLinkWithText("Project Summary");
		_framework.assertTextPresent("Project Summary - Single Project Resource Assignments");

	}

}
