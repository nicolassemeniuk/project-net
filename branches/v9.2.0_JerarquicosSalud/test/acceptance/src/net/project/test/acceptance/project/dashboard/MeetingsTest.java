package net.project.test.acceptance.project.dashboard;

public class MeetingsTest extends ProjectDashboardBase {
	
	public void testMeeting() {
		String meetingPrefix = "TestMeeting";
		
		this.goToProject();
		// check header
		_framework.assertTextPresent("Upcoming Meetings");
		
		// check that the meeting is not present
		_framework.assertTextNotPresent(meetingPrefix);
		
		// create a meeting
		String meetingName = _framework.createNewMeeting(meetingPrefix);
		
		this.goToProject();
		
		// check if the meeting is present
		_framework.assertTextPresent(meetingName);
	}

}
