package net.project.test.acceptance.forms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.project.test.acceptance.engine.PnetTestEngine;

/**
 * <b>Test Case 5.1.4</b> Change assignments for assignable form object.<br><br>
 * 
 * Prerequisites for this test:
 *  <ul>
 *  	<li> put full name of user to which this form is assigned by default in property <b>assignedToUser</b>
 *  	<li> put full name of user to which this form should be reassigned in property <b>assigneToUser</b>
 *  	<li>put email address of user to which this form should be reassigned in property <b>emailOfUserToAssigneProjectTo</b>
 *  </ul>
 * 
 * This test makes new project, and assignable form. Then it takes that form which was primarly
 * assigned to user stored in <b>assignedToUser</b> property.<br>
 * After you run this test once it will assigne form list element to new user (one specified in <b>assigneToUser</b> property), 
 * so if you think to run it few more times, first change values of properties <i>assigneTo</i> and 
 * <i>assignedTo</i> so the test would succeed (because there is a possibility that element you have just 
 * reassinged may be first in the form list again, and you'll then try to assigne that element to user
 * you just assigned that element previossly - FAIL).<br><br>
 * 
 * Change properties <b>newWork</b> and <b>newWorkingUnits</b> to values that you would like to assigning to new user 
 * when assigning him form list element. (how much working units and work will he be spending while working with new assigned form). 
 * Set <b>projectName</b> property to project name whose form you are reassigning, and <b>formName</b> property to name of 
 * the form you will like to reassign to new user.<br><br>
 * @author uros
 *
 */
public class ChangeAssignmentsForAssignableFormObjectTest extends PnetTestEngine {

//////////////////////////////////////////////////////////////////////////////
	
	//project name to select (whose from object assignment we will change)
	private String projectName = null;
	//form object name which we want to reassigne
	private String formName = null;
	
	//variables to hold new Working details for user we are reasigning form to
	String newWork = "100";
	String newWorkingUnits = "hours";
	//user to whome this form is currently assigned, in form "Name Lastname"
	private String assignedToUser = "Application Administrator";
	//user to whome this form will be assigned, in form "Name Lastname"
	private String assigneToUser = "test1 test1";
	//email of the user to which you want to assigne the form (used for searching user in project participants list)
	private String emailOfUserToAssigneProjectTo = "test1@ics.com";
	
//////////////////////////////////////////////////////////////////////////////

	//first forms list element value
	private String firstFormListElementValue = null;
	//first forms list element name
	private String firstFormsListElementName = null;
	
	//the @name and @id attribut of select element with users to which this form can be assigned
	private String usersSelectElementNameAttribut = "assignedUser";
	private String usersSelectElementIdAttribut = "assignedUserId";

	//data for Assignment History part (work of previously assigned user, his name, working units)
	private String historyWork = null;
	private String historyWorkingUnits = null;
	private String historyWorkComplete = null;
	private String historyPercWorkComplete = null;
	private String historyStartDate = null;
	private String historyEndDate = null;
	
	//data necesarry for adding new user to project if it isn't added already
	private String[] usersNames = null;
	private String nameOfUser = null;
	private String lastnameOfUser = null;
	
	public void testChangeAssignmentsForAssignableForm() {

		projectName = _framework.createNewProject();
		_framework.goToPersonal();
		
		formName = "testForm" + _framework.createNewAssignableForm(projectName, true);
		
		initUsersNameAndLastname();
		isUserAddedToProject(nameOfUser, lastnameOfUser, emailOfUserToAssigneProjectTo, projectName);	//, "Lates", emailOfUserToAssigneProjectTo, projectName);
		
		_framework.goToPersonal();
		
		//(2) navigate to assignment form to change, and click edit
		selectFormObjectToReassign();
		checkCurrentAssignmentDetails();
		
		//(3) select new user to whome you'll assigne the form
		assigneFormObjectToNewUser(assigneToUser);
		
		//(4) enter new work for this assignment and save
		enterNewWorkAndSave();
		
		//(5) click form list object again to verify changes
		clickLinkWithExactText(firstFormsListElementName);
		
		//verify correctness of details in history part 		
		checkHistoryDetails();
		
		//verify correctness of details for new user (is form list object assigned to new user)
		checkCorrectnessNewDetails();
		
	}
	
	public void selectFormObjectToReassign() {
		selectProjectWithName(projectName);
		
		//select form to reassign
		assertLinkPresentWithExactText(formName);
		clickLinkWithExactText(formName);
		
		//checks does selected form has list elements - call clickLinkWithExactText(formName) first
		doesFormHasElements();
		
		firstFormListElementValue = getFirstFormsListElementValue();
		checkFirstCheckboxInList();
		
		//gets first forms list element name
		firstFormsListElementName = getFirstFormsListElementName();	
		
		clickModifyFormObjectButton();
	}
		
	/**
	 * Click on project with the given name.
	 * @param projectName
	 */
	public void selectProjectWithName(String projectName) {
		_framework.goToProjects();
		
		assertLinkPresentWithExactText(projectName);
		clickLinkWithExactText(projectName);
		
		assertTextPresent(projectName);
		assertLinkPresentWithExactText("Forms");
		
		System.out.println("Selected project with name: " + projectName);
	}
	
	/**
	 * Returns XPath for type of HTML element you give it with given id attribut value.<br>
	 * 
	 * @param typeOfElement	the type of HTML element (span, a, tr...)
	 * @param idAttributValue value for id attribut of that element
	 * @return String
	 */
	public String getElementsXPathWithIdValue(String typeOfElement, String idAttributValue) {
		String xPath = "//" + typeOfElement + "[@id=\"" + idAttributValue + "\"]";
		return xPath;
	}
		
	/**
	 * Returns XPath for type of HTML element you give him with given name attribut value.<br>
	 * 
	 * @param typeOfElement	the type of HTML element (span, a, tr...)
	 * @param nameAttributValue value for name attribut of that element
	 * @return String
	 */
	public String getElementsXPathWithNameValue(String typeOfElement, String nameAttributValue) {
		String xPath = "//" + typeOfElement + "[@name=\"" + nameAttributValue + "\"]";
		return xPath;
	}

	/**
	 * Asserts that forms list has any elements
	 */
	public void doesFormHasElements() {
		assertElementPresentByXPath( getElementsXPathWithIdValue("tr", "tableContentHighlight") );
	}
	
	/**
	 * Gets forms first list element value.
	 */
	public String getFirstFormsListElementValue() {
		String firstCheckboxXpath = getElementsXPathWithNameValue("input[1]", "selected");
		String listElementsValue = getElementAttributByXPath(firstCheckboxXpath, "value");
		System.out.println("\nValue of the first element in the forms list is :" + listElementsValue + "\n");
		return listElementsValue;
	}
	
	/**
	 * Checks first element in list of elements for the given form, if there
	 * is any.
	 *
	 */
	public void checkFirstCheckboxInList() {
		doesFormHasElements();
		clickElementByXPath( getElementsXPathWithNameValue("input[1]", "selected") );
	}

	/**
	 * Clicks on Modify actionbar icon, and rememberes first form list element name
	 * so it can be referenced by the name on the next page.
	 */
	public void clickModifyFormObjectButton() {
		
		_framework.clickActionModify();
		
		assertTextPresent(firstFormsListElementName);
		assertTextPresent(formName);
		assertTextPresent("Assignment History");
		assertTextPresent("Current Assignment");
	}
	
	/**
	 * Returning the name for first form list element.
	 * @return name of first forms list element.
	 */
	public String getFirstFormsListElementName() {
		String name = getElementTextByXPath( getElementsXPathWithIdValue("a[1]", "listElement"));
		System.out.println("First forms list element name is: " + name);
		return name;
	}
	
	/**
	 * Checks for presence of assignemnt details for form list element.
	 */
	public void checkCurrentAssignmentDetails() {
		//asserts that select list with users is present (by it's id attribut)
		assertElementPresent(usersSelectElementIdAttribut);
	
		//option for user to whome this form is assigned, is present in select element, and selected
		assertSelectOptionPresent(usersSelectElementNameAttribut, assignedToUser);
		assertSelectedOptionEquals(usersSelectElementNameAttribut, assignedToUser);

	}
	
	/**
	 * 
	 * @param newUserName the full name of user (in format "Name Lastname")
	 * @return <b>value</b> attribut for new user.
	 */
	public void assigneFormObjectToNewUser(String newUserName) {
		
		//option for user to whome we will assigne this form is present in select element
		assertSelectOptionPresent(usersSelectElementNameAttribut, assigneToUser);
		//select that option (new user to assigne form object to)
		selectOption(usersSelectElementNameAttribut, assigneToUser);
		System.out.println("Form is now assigned to user: " + assigneToUser + "\n");
		
	}
	
	public void enterNewWorkAndSave() {

		//assert that elements are present
		assertElementPresent("work");
		assertElementPresent("work_units");
		
		//collect and remember values for elements that belonged to previous user
		collectWorkingValues();
		
		//cast newWork to int so you can check if it is bigger value than initial work for that form elment
		int newWorkVal = Integer.parseInt(newWork);
		int historyWorkVal = Integer.parseInt( historyWork.substring(0,(historyWork.length()-4)) );			//4 for length of substring ' hrs'
		
		//changing workingUnit values to hours if they are given in "days" or "weeks" (necesary for testing javascript popUp window)
		if( newWorkingUnits.equals("days") ) {
			newWorkVal = newWorkVal * 8;		//because there are 8 working hours per day
			newWork = Integer.toString(newWorkVal);
			//now that we changed days in hours we set new working units to hours
			newWorkingUnits = "hours";
			System.out.println("...Calculating working_units - chagning days to hours!");
		} else if ( newWorkingUnits.equals("weeks") ) {
			newWorkVal = newWorkVal * 8*5;		//because there are 8*5 working hours per week (5 working days)
			newWork = Integer.toString(newWorkVal);
			//now that we changed weeks in hours we set new working units to hours
			newWorkingUnits = "hours";
			System.out.println("...Calculating working_units - chagning weeks to hours!");
		}
		
		//check if you've tried to enter new work value less than work completed value was
		//in that case javascript popUp window appears with warrning		
		if( newWorkVal < historyWorkVal ) {
			System.out.println("\n***Testing javascript popUp Window and making new Work value bigger***\n");
			//javascript popUp window apeared with warning that task has more work completed than work
			getDialog().clickButtonWithText("OK");
			System.out.println("Javascript PopUp dialog is now closed!");
			
			//then make new work value bigger than workCompleted
			newWorkVal = historyWorkVal + 10;	//new work value is for 10 hours bigger
			newWork = Integer.toString(newWorkVal);
			System.out.println("New bigger Work value is set to " + newWork + "\n");
			
		}
			
		System.out.println("\n***Setting new values for work***\n");
		
		//set new values for elements
		setTextField("work", newWork);
		selectOption("work_units", newWorkingUnits);
		
		System.out.println("Work: " + newWork + " " + newWorkingUnits + "\n");
		
		//click submit button to save changes
		_framework.clickSubmitActionbarButton();
		
		//asserts that there is link with given form list object name we reassigned
		assertLinkPresentWithExactText(firstFormsListElementName);
	}
	
	public void collectWorkingValues() {
		historyWork = getElementAttributByXPath( getElementsXPathWithNameValue("input", "work"), "value");
		historyWork = historyWork.equals("")? "0" : historyWork;
		System.out.println("Previous user work was: " + historyWork);

		historyWorkingUnits = getElementTextByXPath( "//select[@name=\"work_units\"]/option[@selected]");
		System.out.println("Working units are: " + historyWorkingUnits);
		
		historyWorkComplete = getElementTextByXPath( getElementsXPathWithIdValue("td", "workComplete")).trim();
		historyWorkComplete = historyWorkComplete.equals("")? "0 hrs" : historyWorkComplete;
		System.out.println("Previous user work complete was: " + historyWorkComplete);
		
		historyPercWorkComplete = getElementAttributByXPath( getElementsXPathWithIdValue("input", "work_percent_complete"), "value").trim();
		historyPercWorkComplete = historyPercWorkComplete.equals("")? "0%" : historyPercWorkComplete; 
		System.out.println("Previous user work % complete was: " + historyPercWorkComplete);		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date currentDate = new Date();
		try {
			currentDate = dateFormat.parse(currentDate.toString());
		} catch (ParseException e) {
			System.out.println("Error while parsing date.");
		}
		
		historyStartDate = getElementTextByXPath( getElementsXPathWithIdValue("td", "startDate")).trim();
		historyStartDate = historyStartDate.equals("") ? currentDate.toString(): historyStartDate;
		System.out.println("Previous users startDate was: " + historyStartDate);
		
		historyEndDate = getElementTextByXPath( getElementsXPathWithIdValue("td", "endDate")).trim();
		historyEndDate = historyEndDate.equals("") ? currentDate.toString(): historyEndDate;
		System.out.println("Previous users endDate was: " + historyEndDate);
		
		historyWork = historyWorkComplete;
		
		//if user had percent complete set to 0%, make it stay that way, othervise set it to 100%
		if( !historyPercWorkComplete.equals("0%") ) {
			historyPercWorkComplete = "100%";		//previous user has finished working with form
		}
		
	}
	
	/**
	 * Checks history details concerning selected form.
	 * And their equivalence with ex users details.
	 */
	public void checkHistoryDetails() {
		assertTextPresent("Assignment History");
		
		//check equivalence of history details and details presented before editing assignments
		assertTrue( getElementTextByXPath( getElementsXPathWithIdValue("td", "exUsersFullName")).trim().equals(assignedToUser) );
		assertTrue( getElementTextByXPath( getElementsXPathWithIdValue("td", "exWorkComplete")).trim().equals(historyWorkComplete) );
		assertTrue( getElementTextByXPath( getElementsXPathWithIdValue("td", "exWorkPercentComplete")).trim().equals(historyPercWorkComplete) );
		assertTrue( getElementTextByXPath( getElementsXPathWithIdValue("td", "exStartDate")).trim().equals(historyStartDate) );
		//assertTrue( getElementTextByXPath( getElementsXPathWithIdValue("td", "exEndDate")).trim().equals(historyEndDate) );
	}
	
	/**
	 * Checks new details in form list object, entered in process of reassigning 
	 * forms list object to new user (assigneTo user).
	 */
	public void checkCorrectnessNewDetails() {
		assertSelectedOptionEquals(usersSelectElementNameAttribut, assigneToUser);
		//assert that current user work is correct
		assertTrue( newWork.equals( getElementAttributByXPath( getElementsXPathWithNameValue("input", "work"), "value" ) ) );
	}
	
	/**
	 * Checks if user is added to the project. If it is, do nothing, if it's not, add it. 
	 * 
	 * @param userName
	 * @param userLastname
	 * @param userEmail
	 * @param project
	 */
	private void isUserAddedToProject(String userName, String userLastname, String userEmail, String project) {
		
		_framework.goToProjects();
		assertLinkPresentWithExactText(project);
		clickLinkWithExactText(project);
		assertTextPresent(project);
		assertLinkPresentWithExactText("Setup");
		clickLinkWithExactText("Setup");
		
		assertLinkPresentWithExactText("People and Roles");
		clickLinkWithExactText("People and Roles");
		assertTextPresent("Participants");
		
		boolean isUserAdded = getPageSource().contains(userEmail)? true:false ;
		if( !isUserAdded) {
			//add user to the list
			addUserToProject(userName, userLastname, userEmail, project);
			System.out.println("\n*** User " + userName + " " + userLastname + " added to project " + project + ". ***\n");
		} else {
			//user is already added to project list
			System.out.println("\n*** User " + userName + " " + userLastname + " already exists in projects " + project + " list. ***\n");
		}

	}
	
	/**
	 * Adds user for the first time to the specific project passed to this method.
	 * 
	 * @param user Name to add to project (e.g. Homer)
	 * @param user Lastname to add to project (e.g. Simpson)
	 * @param user email address (e.g. homer@gmail.com)
	 * @param project to which you want to add new user to
	 */
	public void addUserToProject(String userName, String userLastname, String userEmail, String project) {
		
		assertTextPresent("Participants");
		assertLinkPresentWithText("Invite Participant");
		clickLinkWithText("Invite Participant");
		
		assertTextPresent("Invite By Email Address");
		setWorkingForm("memberadd");
		assertFormElementPresent("inviteeFirstName");
		setFormElement("inviteeFirstName", userName);
		assertFormElementPresent("inviteeLastName");
		setFormElement("inviteeLastName", userLastname);
		assertFormElementPresent("inviteeEmail");
		setFormElement("inviteeEmail", userEmail);
		assertLinkPresentWithText("Add to Invitee List");
		clickLinkWithText("Add to Invitee List");
		_framework.clickNextActionbarButton();
		assertTextPresent("Assign Role");
		assertLinkPresentWithText("Invite");
		clickLinkWithText("Invite");
		
		
	}
	
	/**
	 * Initialize variables necessary for adding new user to project
	 *
	 */
	private void initUsersNameAndLastname() {
		usersNames = assigneToUser.split(" ");
		nameOfUser = usersNames[0];
		lastnameOfUser = usersNames[1];
	}
	
}
