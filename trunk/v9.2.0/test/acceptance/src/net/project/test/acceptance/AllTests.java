package net.project.test.acceptance;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.project.test.acceptance.forms.AddingWorkToAssignableFormObjectTest;
import net.project.test.acceptance.forms.AssignableFormsTest;
import net.project.test.acceptance.forms.ChangeAssignmentsForAssignableFormObjectTest;
import net.project.test.acceptance.forms.CreateFormsWithLongNamesTest;
import net.project.test.acceptance.resource.ProjectSummaryTest;
import net.project.test.acceptance.resource.ReservebyProjectTest;
import net.project.test.acceptance.resource.ReservebyResourceTest;
import net.project.test.acceptance.resource.ReservedvsAssignedTest;
import net.project.test.acceptance.resource.ResourceManagementTest;
import net.project.test.acceptance.resource.TasksAssignedTest;
import net.project.test.acceptance.resource.ViewSummaryTest;
import net.project.test.acceptance.resource.ViewbyResourceTest;
import net.project.test.acceptance.schedule.TaskActionsTest;
import net.project.test.acceptance.wiki.BasicWikiFeaturesTest;

public class AllTests {

	private AllTests() {
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("net.project.test.acceptance");
		
		// login test
		suite.addTestSuite(LoginTest.class);
		
		// personal home
		suite.addTestSuite(PersonalHomeTest.class);
		
		// ICalendar Test Cases Suite
		suite.addTestSuite(ICalendarTest.class);

		// assignable forms:
		suite.addTestSuite(AddingWorkToAssignableFormObjectTest.class);
		suite.addTestSuite(AssignableFormsTest.class);
		suite.addTestSuite(ChangeAssignmentsForAssignableFormObjectTest.class);
		suite.addTestSuite(MakingExistingFormAssignableTest.class);
		suite.addTestSuite(CreateFormsWithLongNamesTest.class);
		
		// RM Test Cases Suite
		suite.addTestSuite(ResourceManagementTest.class);
		suite.addTestSuite(ReservebyResourceTest.class);
		suite.addTestSuite(ReservebyProjectTest.class);
		suite.addTestSuite(ViewbyResourceTest.class);
		suite.addTestSuite(ReservedvsAssignedTest.class);
		suite.addTestSuite(TasksAssignedTest.class);
		suite.addTestSuite(ViewSummaryTest.class);
		suite.addTestSuite(ProjectSummaryTest.class);
		
		/*// Blog Test Cases Suite
		suite.addTestSuite(ProjectBlogTest.class);
		suite.addTestSuite(PersonalBlogTest.class);*/
		
		// Wiki Test Cases Suite
		suite.addTestSuite(BasicWikiFeaturesTest.class);		
		
        // Schedule Test Cases Suite
        suite.addTestSuite(TaskActionsTest.class);
		
		return suite;
	}
}
