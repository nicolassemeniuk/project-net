package net.project.view.pages.timesheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.calendar.CalendarBean;
import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.gui.history.History;
import net.project.gui.history.PageLevel;
import net.project.hibernate.model.PnBusinessSpaceView;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnProjectSpace;
import net.project.personal.CalendarTimeSheetBean;
import net.project.resource.AssignmentWorkCaptureHelper;
import net.project.resource.ResourceWorkingTimeCalendarProvider;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;
import net.project.util.NumberFormat;
import net.project.util.StringUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.view.pages.base.BasePage;
import net.project.view.pages.resource.management.GenericSelectModel;

import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Session;


public class CalendarTimeSheet extends BasePage {
	
	@Inject
	private PropertyAccess access;
	
	@Persist
	@Property
	private String jspRootUrl;
	
	@Property
	private List<CalendarTimeSheetBean> dayHeader;
	
	@Property
	private CalendarTimeSheetBean timeSheetBean;
	
	private CalendarBean calendarBean;
	
	@Property
	private ArrayList weeks;
	
	@Property
	private String[] weekDates;
	
	@Property
	private String URL;
	
	@Property
	private Long dateForAssignment;
	
	private Map summaryDateValues = new HashMap(); 
	 
	private static Logger log = Logger.getLogger(CalendarTimeSheet.class);
	
	@Persist
	private User user;
	
	@Property
	private String dateOfWeek;
	
	@Property
	private String monthName;
	
	@Property
	private String previousMonth;
	
	@Property
	private String nextMonth;
	
	@Property
	private Integer calendarModule;
	
	@Property
	private TimeQuantity weekTotal; 
	
	@Property
	private TimeQuantity monthTotal;
	
	@Property
	private  PnBusinessSpaceView business;
	
	@Property
	private  PnProjectSpace project;
	
	@Persist
	@Property
	private  PnPerson person;
	
	@Persist
	private List<PnBusinessSpaceView> userBusinessList;
	
	@Persist
	private List<PnProjectSpace> userProejctList;
	
	@Persist
	private List<PnPerson> personsInSpace;
	
	@Inject
	@Property
	private Block personBlock;
	
	@Inject
	@Property
	private Block calendarBlock;
	
	@Property
	private String dateToday;
	
	@Property
	private String dayToday;
	
	@Property
	private String monthTotalString;
	
	@Component
	private Form timeSheetForm;
	
	@Property
	private String userName;
	
	@Property
	private String timesheetLeftHeading;
	
	// CONSTANTS
	public static final String HEADER_FORMAT = "MMMM yyyy";
	
	private WorkingTimeCalendarDefinition  workingTimeCalendarDefinition;

	private ResourceWorkingTimeCalendarProvider calendarProvider;
	
	@Property
	private String currentMonthName;
	
	private boolean isAccessDeniedForOtherUser = false;
	
	/**
	 * Setting up values before page render
	 */
	Object onActivate(){
    	Object isValidAccess;
    	isValidAccess = checkForUser();
    	if(isValidAccess != null){
    		return isValidAccess;
    	}
		jspRootUrl = SessionManager.getJSPRootURL();	
		timesheetLeftHeading = PropertyProvider.get("prm.personal.nav.timesheet");
		user = SessionManager.getUser();
		weeks = new ArrayList();
		if(!weeks.isEmpty()){
			weeks.clear();
		}
		initializeFilters();
		setPageTitle();
		getWeekHeader();
		if(person != null){
			getMonthView(person.getPersonId().toString());
		} else {
			getMonthView(user.getID());
		}
		return null;
	}
	
	/** This method returns total list of the day header
	 * (e.g Sun,Mon) according to the user's locale
	 *   
	 * @return List of type CalendarTimeSheetBean
	 * this type has the property to set the day of week 
	 */
	private List<CalendarTimeSheetBean> getWeekHeader(){
		dayHeader = new ArrayList<CalendarTimeSheetBean>();
		if(user != null){
		    PnCalendar weekCal = new PnCalendar(user);
		    weekCal.set(Calendar.DAY_OF_WEEK, weekCal.getFirstDayOfWeek());
		    String dayPattern = "EEE";
		    // Iterate over 7 days, displaying the name of the day
		    for (int i = 0; i < 7; i++) {
		    	CalendarTimeSheetBean calendarTimeSheetBean = new CalendarTimeSheetBean();
		    	calendarTimeSheetBean.setDayOfWeek(SessionManager.getUser().getDateFormatter().formatDate(weekCal.getTime(), dayPattern));
		    	weekCal.roll(Calendar.DAY_OF_WEEK, 1);
		    	dayHeader.add(calendarTimeSheetBean);
		   }
		}
	    return dayHeader;
	}

	/**
	 * This method creates a month view along with the data of total 
	 * work captured for each day
	 */
	@SuppressWarnings("unchecked")
	private void getMonthView(String userId) {
		weeks = new ArrayList();
		if(!weeks.isEmpty()){
			weeks.clear();			
		}
		PnCalendar tempCal = new PnCalendar(SessionManager.getUser());
		PnCalendar workCalendar = null;
		if(!userId.equals(SessionManager.getUser().getID())) {
			isAccessDeniedForOtherUser = true;
			user = new User(userId);
			workCalendar = new PnCalendar(user);
			userName = user.getDisplayName();
		} else {
			workCalendar = new PnCalendar(SessionManager.getUser());
			userName = SessionManager.getUser().getDisplayName();
			user = SessionManager.getUser();
		}
		
		calendarBean = new CalendarBean();
		AssignmentWorkCaptureHelper workCaptureHelper = new AssignmentWorkCaptureHelper();
		
		NumberFormat numberFormat = NumberFormat.getInstance();
		// Navigation for Months
		String dateOfNewMonth =  getRequest().getParameter("DisplayDate");
		Date convertedDate = null;
		if(StringUtils.isNotEmpty(dateOfNewMonth)){
			try {
				convertedDate = new SimpleDateFormat("MMddyyyy").parse(dateOfNewMonth);
				calendarBean.setTime(convertedDate);
			} catch (ParseException e) {
				log.error("Error while parsing the date of the month : "+e.getMessage());
			}
		} else {
			convertedDate = PnCalendar.currentTime();
		}
		
		try {//Find Non working days for the user
			calendarProvider = (ResourceWorkingTimeCalendarProvider) ResourceWorkingTimeCalendarProvider
					.make(user);
			this.workingTimeCalendarDefinition = calendarProvider.getForResourceID(userId);
		} catch (Exception e) {
			log.error("Error occured while inintializie wroking time for person..." + e.getMessage());
		}
		// Populate the month
		Calendar cal = Calendar.getInstance();
		int iMonth = calendarBean.get(Calendar.MONTH);
		int iYear = calendarBean.get(Calendar.YEAR);
		tempCal.setTime(calendarBean.endOfMonth(iMonth, iYear));
		workCalendar.setTime(calendarBean.endOfMonth(iMonth, iYear));
		int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		tempCal.setTime(calendarBean.startOfMonth(iMonth, iYear));
		workCalendar.setTime(calendarBean.startOfMonth(iMonth, iYear));
		int firstDayOfMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		int firstDayOfWeek = cal.getFirstDayOfWeek();
		int day = firstDayOfMonth;
		
		// Dates of starting and ending of current month
		Date startDate = tempCal.startOfMonth(convertedDate);
		Date endDate = tempCal.endOfMonth(convertedDate);
	
		// Getting Month Name and create Url for navigating months
		currentMonthName = calendarBean.formatDateAs(new Date(),HEADER_FORMAT);
		monthName = calendarBean.formatDateAs(convertedDate, HEADER_FORMAT);
		previousMonth = calendarBean.formatDateAs(calendarBean.getPrevMonth(), "MMddyyyy");
		nextMonth = calendarBean.formatDateAs(calendarBean.getNextMonth(), "MMddyyyy");
		
		// Get all the work captured for each day in map.
		if(summaryDateValues != null)
			summaryDateValues.clear();
		
		summaryDateValues.putAll(workCaptureHelper.getTotalWorkHoursForMonth(startDate, endDate, StringUtils.getIntegerArrayOfCSNString(userId), false));

		// Initialize object for month total 
		monthTotal  = new TimeQuantity(0, TimeQuantityUnit.HOUR);
		while (day <= lastDayOfMonth) {
			String[] week = new String[8];
			
			// Build the week array, assigning the day of month to
			// appropriate index position
			boolean isEndOfWeek = false;
			
			// Navigate to the next Week
			Date startDateOfWeek = tempCal.startOfWeek(tempCal.getTime());
			
			// Initalize object for week Total
			weekTotal  = new TimeQuantity(0, TimeQuantityUnit.HOUR);
			DateFormat dateFormat = SessionManager.getUser().getDateFormatter();
			while (day <= lastDayOfMonth && !isEndOfWeek) {
				// Navigate to the Next Day
				// Calculate empty position that are without dates
				int indexPos = tempCal.get(Calendar.DAY_OF_WEEK)- firstDayOfWeek;
				if (indexPos < 0) {
					indexPos = 7 + indexPos;
				}
			
				// get the total work capture for a day from summaryDateValues Map.
				TimeQuantity oldWorkForDay = (TimeQuantity) summaryDateValues.get(workCalendar.getTime());
				oldWorkForDay = (oldWorkForDay == null ? new TimeQuantity(0, TimeQuantityUnit.HOUR) : oldWorkForDay);
				
				String newDate = dateFormat.formatDate(Calendar.getInstance().getTime(), "MMM dd");
				String todayDate = dateFormat.formatDate(tempCal.getTime(), "MMM dd");
				
				// Create URL for each day's work hour captured 
				URL = SessionManager.getJSPRootURL() +"/servlet/AssignmentController/CurrentAssignments/Update?module="
						+Module.PERSONAL_SPACE + "&action=" + Action.MODIFY + "&isFromTimeSheet=true&startDate="+ startDateOfWeek.getTime()
						+"&personId="+userId+"&isAccessForOtherUser="+isAccessDeniedForOtherUser
						+"&isEditMode="+PropertyProvider.getBoolean("prm.resource.timesheet.editmode.isenabled")+"&assignmentStatus=currentassignment";
				String workForDay = numberFormat.formatNumber(oldWorkForDay.getAmount().doubleValue(),0,2);
				
				if(this.workingTimeCalendarDefinition != null && workingTimeCalendarDefinition.isWorkingDay(tempCal)){
					if(todayDate.equals(newDate)){
						//Initialize the day and total work capture in the week days array for printing
						week[indexPos] = "<table border=\"0\" width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\">" +
		                                  "<tr><td valign=\"top\" style=\"border-top:none;\"><div class=\"date-title-personal\">"+ Integer.toString(day) +"</div><div class=\"today-personal\" style=\"padding-top:30px\">"+ 
		                                  "</div><div class=\"meeting-event\" style=\"height:25px;text-align:center;\"><a href=" + URL + " id=work_"+day+">"+ workForDay + "</a></div></td></tr>" +
										  "</table>";
					} else {
						//Initialize the day and total work capture in the week days array for printing
						week[indexPos] = "<table border=\"0\" width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\">" +
		                                  "<tr><td valign=\"top\" style=\"border-top:none;\"><div class=\"date-title-personal\">" + Integer.toString(day) +
		                                  "</div></td></tr><tr class=\"summaryWork\"><td style=\"padding-top:28px;\"><div class=\"meeting-event\"style=\"height:25px;text-align:center;\"><a href=" + URL + " id=work_"+day+">"+ workForDay + "</div></a>" +
										  "</td></tr></table>";
					}
				} else {
					if(todayDate.equals(newDate)){
						//Initialize the day and total work capture in the week days array for printing
						week[indexPos] = "<table border=\"0\" width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\" class=\"timesheet-nonworkingDay-today\">" +
		                                  "<tr><td valign=\"top\" style=\"border-top:none;\"><div class=\"date-title-personal\">"+ Integer.toString(day) +"</div><div class=\"today-personal\" style=\"padding-top:30px\">"+ 
		                                  "</div><div class=\"meeting-event\" style=\"height:25px;text-align:center;\"><a href=" + URL + " id=work_"+day+">"+ workForDay + "</a></div></td></tr>" +
										  "</table>";
					} else {
						//Initialize the day and total work capture in the week days array for printing
						week[indexPos] = "<table border=\"0\" width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\" class=\"timesheet-nonworkingDay\">" +
		                                  "<tr><td valign=\"top\" style=\"border-top:none;\"><div class=\"date-title-personal\">" + Integer.toString(day) +
		                                  "</div></td></tr><tr class=\"summaryWork\"><td style=\"padding-top:28px;\"><div class=\"meeting-event\"style=\"height:25px;text-align:center;\"><a href=" + URL + " id=work_"+day+">"+ workForDay + "</div></a>" +
										  "</td></tr></table>";
					}
				}
				//Add daily hours for week total work captured
				weekTotal = weekTotal.add(oldWorkForDay);
			
				HTMLUtils.escape(week[indexPos]);
				tempCal.roll(Calendar.DAY_OF_MONTH, 1);
				workCalendar.roll(Calendar.DAY_OF_MONTH, 1);
				day++;
				// If we populated the last index element
				// Then its time for a new week
				if (indexPos == 6) {
					isEndOfWeek = true;
				}
			}
			//add weekly hours for month total
			monthTotal = monthTotal.add(weekTotal);
			monthTotalString = numberFormat.formatNumber(monthTotal.getAmount().doubleValue(),0,2);
			week[7] = "<table border=\"0\" height=\"100%\" width=\"100%\" style=\"background:#fef5c7\"><tr class=\"week-Total\"><td>"+
						numberFormat.formatNumber(weekTotal.getAmount().doubleValue(),0,2) + "</td></tr></table>";
			// Save all the weeks in one arraylist
			weeks.add(week);
		}
		dateForAssignment = tempCal.startOfWeek().getTime();
	}
	
	/**
	 * @param pageTitle the pageTitle to set
	 */
	public void setPageTitle() {
		History history = null; // new History();
		Session session = getRequest().getSession(true);

		history = (History) session.getAttribute("historyTagHistoryObject");
		if (history != null) {
			PageLevel pageLevel = new PageLevel(0);
			pageLevel.setActive(false);
			pageLevel.setDisplay("Time Sheet");
			pageLevel.setJspPage(SessionManager.getJSPRootURL() + "/personal/assignments/My?module=" + Module.PERSONAL_SPACE);
			pageLevel.setShow(true);
			history.setLevel(pageLevel);

			session.setAttribute("historyTagHistoryObject", history);
			session.setAttribute("user", SessionManager.getUser());
		}
	}

	public GenericSelectModel<PnBusinessSpaceView> getBusinessModel() {
		return new GenericSelectModel<PnBusinessSpaceView>(userBusinessList, PnBusinessSpaceView.class, "businessName", "businessId", access);
	}
	
	public GenericSelectModel<PnProjectSpace> getProjectModel() {
		return new GenericSelectModel<PnProjectSpace>(userProejctList, PnProjectSpace.class, "projectName", "projectId", access);
	}
	
	public GenericSelectModel<PnPerson> getPersonModel() {
		return new GenericSelectModel<PnPerson>(personsInSpace, PnPerson.class, "displayName", "personId", access);
	}
	
	private void initializeFilters(){
		DateFormat dateFormat = SessionManager.getUser().getDateFormatter();
		dateToday = dateFormat.formatDate(new Date(), "dd");
		dateToday = Integer.valueOf(dateToday) < 10 ? Integer.valueOf(dateToday).toString() : dateToday;
		dayToday = dateFormat.formatDate(new Date(), "EEEEE");
		// initializing dropdown list.
		userBusinessList = getBusinessSpaceService().findByUser(user, "A");
		userProejctList = getPnProjectSpaceService().getProjectsByUserId(Integer.valueOf(user.getID()));

		if (userBusinessList.get(0).getBusinessId() != null) {
			personsInSpace = getPnPersonService().getPersonsByBusinessId(userBusinessList.get(0).getBusinessId());
			business = userBusinessList.get(0);
			project = null;
		} else {
			personsInSpace = new ArrayList<PnPerson>();
			personsInSpace.add(new PnPerson(user));
		}
		// default selection in person drop down list
		if(person == null) {
			person = new PnPerson(user);
		}
	}
	
	Block onActionFromRefreshCalendarZone(String personId){
		if (weeks != null && !weeks.isEmpty()) {
			weeks.clear();
		}
		getWeekHeader();
		getMonthView(personId);
		return calendarBlock;
	}
	
	Block onSubmitFromTimeSheetForm() {
		if (project != null) {
			personsInSpace = getPnPersonService().getPersonsByProjectId(project.getProjectId());
			business = null;
		} else if (business != null){
			personsInSpace = getPnPersonService().getPersonsByBusinessId(business.getBusinessId());
			project = null;
		}
		if (person != null && !personsInSpace.contains(person)) {
			person = new PnPerson(user);
		}
		return personBlock;
	}
}
