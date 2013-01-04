package net.project.test.acceptance;

import net.project.test.acceptance.engine.PnetTestEngine;

public class ICalendarTest extends PnetTestEngine {

	@Override
	protected void tearDown() throws Exception {
		_framework.getDataCleaner().removeActualObjects();
		_framework.logout();
		super.tearDown();
	}
	
	public void testICalendarExport() {
		_framework.gotoPage("/calendar/Main.jsp?module=70");
		_framework.assertTextPresent("iCalendar Integration");
		_framework.clickLinkWithText("Export");
		_framework.gotoPage("/calendar/Main.jsp?module=70");
		_framework.assertTextPresent("iCalendar Integration");
	}

	public void testSessionInvalidAfterExport() {
		_framework.gotoPage("/calendar/Main.jsp?module=70");
		_framework.assertTextPresent("iCalendar Integration");
		_framework.clickLinkWithText("Export");
		_framework.gotoPage("/calendar/Main.jsp?module=70");
		_framework.assertTextPresent("iCalendar Integration");
	}

	public void testNewMeeting() {
		_framework.createNewMeeting();
	}

	public void testNewEvent() {
		_framework.createNewEvent();
	}

	public void testCalendarDayViews() {
		_framework.gotoPage("/calendar/Main.jsp?module=70");
		_framework.assertTextPresent("iCalendar Integration");

		_framework.clickLinkWithText("Day");
		_framework.assertTextPresent("Name");
		_framework.assertTextPresent("Time");
		_framework.assertTextPresent("Location");
	}

	public void testCalendarWeekViews() {
		_framework.gotoPage("/calendar/Main.jsp?module=70");
		_framework.assertTextPresent("iCalendar Integration");

		_framework.clickLinkWithText("Week");
		_framework.assertTextPresent("Name");
		_framework.assertTextPresent("Time");
		_framework.assertTextPresent("Location");
	}

	public void testCalendarYearViews() {
		_framework.gotoPage("/calendar/Main.jsp?module=70");
		_framework.assertTextPresent("iCalendar Integration");

		_framework.clickLinkWithText("Year");
		_framework.assertTextPresent("Name");
		_framework.assertTextPresent("Time");
		_framework.assertTextPresent("Location");
	}

	public void testCalendarJumptoDateView() {
		_framework.gotoPage("/calendar/Main.jsp?module=70");
		_framework.assertTextPresent("iCalendar Integration");

		_framework.assertTextPresent("Jump to Date");
		_framework.selectOption("month", "February");
		_framework.selectOption("day", "22");
		_framework.selectOption("year", "2008");
		_framework.clickLinkWithText("Jump");
		_framework.assertTextPresent("Name");
		_framework.assertTextPresent("Time");

		_framework.assertTextPresent("iCalendar Integration");
	}

	public void testCalendarMeetingView() {
		String newMeetingName = _framework.createNewMeeting();
		
		_framework.gotoPage("/calendar/Main.jsp?module=70");
		_framework.assertTextPresent("iCalendar Integration");

		_framework.clickLinkWithText(newMeetingName);
		_framework.assertTextPresent("Date:");
		_framework.assertTextPresent(newMeetingName);
	}

	public void testCalendarEventView() {
		String newEventName = _framework.createNewEvent();
		
		_framework.gotoPage("/calendar/Main.jsp?module=70");
		_framework.assertTextPresent("iCalendar Integration");

		_framework.clickLinkWithText(newEventName);
		_framework.assertTextPresent("Event");
		_framework.assertTextPresent(newEventName);
	}
}
