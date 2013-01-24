package net.project.directory;

import org.springframework.mock.web.MockHttpServletRequest;

import net.project.directory.ManageDirectoryHelper;
import net.project.application.Application;
import net.project.business.BusinessMemberWizard;
import net.project.resource.SpaceInvitationManager;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ManageDirectoryHelperTest extends TestCase{
	
	public ManageDirectoryHelperTest(String s){
		super(s);
	}
	
	 public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ManageDirectoryHelperTest.class);
        return suite;
    }
    
    protected void setUp() throws Exception {
        Application.login();
    }
    
    public void testGetProjectLists(){
    	ManageDirectoryHelper directoryHelper = new ManageDirectoryHelper();
    	String currentSpaceId = "972052";
    	User user = SessionManager.getUser();
    	int action = Action.VIEW;
    	assertNotNull(directoryHelper.getProjectLists(currentSpaceId, user, action));
    }
}
