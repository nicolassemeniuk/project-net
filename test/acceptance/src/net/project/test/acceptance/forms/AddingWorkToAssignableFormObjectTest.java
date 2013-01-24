package net.project.test.acceptance.forms;

import java.util.ArrayList;
import net.project.test.acceptance.engine.PnetTestEngine;
import net.sourceforge.jwebunit.html.Table;

/**
 * Test case 5.1.3 <br>
 * <b>Add work to assignable form object from central assignments page</b>.<br><br>
 * 
 * Set <b>usersUsername</b> and <b>usersPassword</b> properties to username and password 
 * of user whose credientals you are using to login on application.<br><br>
 * 
 * Function <b>login(String username, String password)</b> should be palaced in <b>PnetTestFramework</b>, 
 * alogn with some other functions from this class.
 * 
 * @author Uros
 *
 */
public class AddingWorkToAssignableFormObjectTest extends PnetTestEngine {

	public static final String ACTION_BAR_BUTTON_UPDATE = "/images/icons/actionbar-update";
	
	//value to select in <b>Assignment Type:</b> select element
	private String assignmetTypeValue = "Form";
	
	//Value of @name attribut in assignment checkbox element
	private String assignmentsCheckboxName = "objectID";
	//XPath to first checkbox in the list presented by the selection criterion
	String assignmentCheckBoxXpath = "//input[1][@name=\"" + assignmentsCheckboxName + "\"]";
	//holds the value of the selected checkbox (the one you select - first in this case)
	String checkboxFormsValue = null;
	
	//Value of @id attribut in assignment Abbreviation Name element
//	private String assignmentsAbbreviationNameLinksId = "href" + checkboxFormsValue;
//	String assignmentsFormAbbreviationXpath = "//a[1][@id=\"" + assignmentsAbbreviationNameLinksId + "\"]";
	
	//Value of @id attribut in assignment workspace name element
	private String assignmentsWorkspaceNameId = null;	
	String assignmentsWorkspaceNameIdXpath = null;		
	
	//Value of @id attribut in assignment Type element
	private String assignmentsTypeId = null;			
	String assignmentsTypeIdXpath = null;				
	
	//Value of @id attribut in assignment Start Time element
	private String assignmentsStartTimeId = null;		
	String assignmentsStartTimeIdXpath = null;			
		
	//Value of @id attribut in assignment End Time (Finish Date) element
	private String assignmentsEndTimeId = null;			
	String assignmentsEndTimeIdXpath = null;			
	
	//Value of @id attribut in assignment Actual Start element
	private String assignmentsActualStartId = null;		
	String assignmentsActualStartIdXpath = null;		
	
	//Value of @id attribut in assignment Percent Complete element
	private String assignmentsPercentCompleteId = null;	
	String assignmentsPercentCompleteIdXpath = null;	
	
	//Value of @id attribut in assignment Work (My Work) element
	private String assignmentsWorkId = null;			
	String assignmentsWorkIdXpath = null;				
	
	//Value of @id attribut in assignment Work Complete (My Work Complete) element
	private String assignmentsWorkCompleteId = null;	
	String assignmentsWorkCompleteIdXpath = null;		
	
	//Value of @id attribut in assignment Work Remaining (My Work Remaining) element
	private String assignmentsWorkRemainingId = null;		
	String assignmentsWorkRemainingIdXpath = null;			
	
	//Abbreviation Name of form object to select 
	private String formsAbbreviationName = null;
	
	//values generated after work was updated in hours
	//used for checking changes made by update in Assignments page
	private String myPercentCompleteValue = null;
	private String myWorkValue = null;
	private String myWorkReportedValue = null;
	private String myWorkRemainingValue = null;
	
	
	public void testMakeExistenentFormAssignable() throws Exception {
		// (2) click Assignments link in navigation pane
		clickOnAssignmentsNavigationPaneLink();
		
		// (3) select assignment type as <b>form</b>
		selectAssignmentTypeAs(assignmetTypeValue);
		//Check for existence of Forms Object assigned to current user
		doesCurrentUserHasFormsAssignedToHim();
	
		// (4) select first object and click capture work icon
		selectFirstAssignmentFormObject();
		
		// (5) enter work entries in hours and update
		updateWorkEntriesInHours();
		comparePresentedWithUpdatedWorkingValues();
	}
		
	////////////////////////////////////////////////////////////////////
	
	/**
	 * Selects assignments link on navigation pane, and check the outcome.
	 */
	public void clickOnAssignmentsNavigationPaneLink() {
		assertLinkPresentWithExactText("Assignments");
		clickLinkWithExactText("Assignments");
		
		assertSelectOptionPresent("filterSpace", "All");
		assertSelectOptionPresent("filterType", "Form");
		
	}
	
	/**
	 * Selects given option in Assignment Type select element.
	 * 
	 * @param assignmetTypeValue value to select in <i>Assignment Type</i> select element.
	 */
	public void selectAssignmentTypeAs(String selectedAssignmetTypeValue) {
		selectOption("filterType", selectedAssignmetTypeValue);
		clickButton("applyFilters");
	}
	
	/**
	 * Move this function in PnetTestFramework.
	 * Clicks on Create tolbar button
	 */
	public void clickCreateToolbarButton() {
		clickLinkWithImage(_framework.TOOLBAR_BUTTON_CREATE);
	}

	/**
	 * Move this function in PnetTestFramework.
	 * Clicks on Update actionBar button
	 */
	public void clickUpdateActionBarButton() {
		clickLinkWithImage(ACTION_BAR_BUTTON_UPDATE);
	}
	
	public void selectFirstAssignmentFormObject() {
		//check on first forms object in Form Assignment table
		checkFirstCheckboxWithName(assignmentsCheckboxName);

		//get value for first checkbox in the list of assignments checkboxes
		checkboxFormsValue = getElementAttributByXPath(assignmentCheckBoxXpath, "value");
		System.out.println("Value of selected checkbox is: " + checkboxFormsValue);
		
		//call this method to remember the abbrevation name for the form you are selecting 
		getFormObjectsAbbreviationName(checkboxFormsValue);
		
		_framework.clickActionCaptureWork();		
		
		//check if you are on right page
		assertTextPresent("Update Assignment");
		assertTextPresent(formsAbbreviationName);
	}
	
	/**
	 * Checks does current user has forms assigned to him.
	 *
	 */
	public void doesCurrentUserHasFormsAssignedToHim() {
		
		//check for presence of assignments forms table
		assertTablePresent("assignmentDetailsTable");
		Table assignmentsTable = getTable("assignmentDetailsTable");
		
		//get the reference to table rows
		ArrayList tableRows = assignmentsTable.getRows();
		System.out.println("\nNumber of assignmentDetailsTable rows is: " + tableRows.size());
		
		//check for existence of assignments intput checkbox,
		//which verifys existence of forms objects assigned to the user
		//assert that there are forms assigned to user
		assertElementPresentByXPath(assignmentCheckBoxXpath);
		
	}
	
	public void assertImageNotPresent(String imageSrc, String imageAlt) {	
		String xpath = "//img[@src=\"" + imageSrc + "\"";
        if (imageAlt!= null) {
            xpath += " and @alt=\"" + imageAlt + "\"";
        }
        xpath += "]";
        assertElementNotPresentByXPath(xpath);
	}
	
	/**
	 * Checks first checkbox in repeating list with given name atribut.
	 * @param checkboxNameXPath
	 */
	public void checkFirstCheckboxWithName(String checkboxName) {
		String xPath = "//input[1][@name=\"" + checkboxName + "\"]";
		clickElementByXPath(xPath);
	}
	
	public void getFormObjectsAbbreviationName(String checkboxFormsValue) {
		//better to make this variable global
		String formsAbbreviationLinkId = "href" + checkboxFormsValue;
		System.out.println("Forms Abbreviation Id atribut: " + formsAbbreviationLinkId);
		
		//gets forms abbreviation name for selected object (which has checkbox checked)
		String formsAbbreviationLinkIdXPaht = "//a[@id=\"" + formsAbbreviationLinkId + "\"]";
		formsAbbreviationName = getElementTextByXPath(formsAbbreviationLinkIdXPaht);
		System.out.println("Forms Abbreviation Name is: " + formsAbbreviationName + "\n");
		
	}
	
	/**
	 * Updates work in hours, check that no errors message was generated.
	 *
	 */
	public void updateWorkEntriesInHours() {
		//prepares XPath for first timer link
		String timerXpath = "//a[1][@id=\"timerLink\"]";
		
		//click on timer link
		assertElementPresentByXPath(timerXpath);
		clickElementByXPath(timerXpath);
		
		//assert that now we have 2 windows (windows are zero based!)
		assertWindowCountEquals(2);
		//go to second window
		gotoWindow(1);
		
		assertFormElementPresent("timeSpan1Start");
		assertFormElementPresent("timeSpan1End");
		
		//set working hour values for work you are reporting
		setTextField("timeSpan1Start", "07:00 AM");
		setTextField("timeSpan1End", "11:00 AM");
		
		_framework.clickSubmitActionbarButton();
		
		assertTextPresent(formsAbbreviationName);
		
		//remembers values generated after update of working hours (for compartion reasons)
		rememberUpdatedDetails();
		
		//click on upate button
		clickUpdateActionBarButton();
		
		//assert that no error occured
		String errorXpath = getElementsXPathWithIdValue("li", "errorOccured");
		assertElementNotPresentByXPath(errorXpath);
		
	}
	
	/**
	 * Compares presented with updated working hour values 
	 * in order to see that presented values are really changed. 
	 *
	 */
	public void comparePresentedWithUpdatedWorkingValues() {
		
		//check that updated object is in the list of presented object assigned to user
		//maybe it isn't (if My Work Remaining is equal to 0 hours)
		
		//update XPaths for working object fileds - so they can reference on right object
		
		//colect values of working object and compare them with updated calculated values 
		comparePresentedWithUpdatedWorkingHourValuesForSelectedObject();
	
	}

	public void rememberUpdatedDetails() {
		String pwcv = getElementsXPathWithIdValue("input", "myPercentComplete");
		myPercentCompleteValue = getElementAttributByXPath( pwcv, "value");
		System.out.println("My percent Complete: " + myPercentCompleteValue);
		
		String wv = getElementsXPathWithNameValue("span", "wk");
		myWorkValue = getElementTextByXPath( wv );
		System.out.println("My work value: " + myWorkValue);
		
		String wrv = getElementsXPathWithNameValue("span", "wkrp");
		myWorkReportedValue = getElementTextByXPath( wrv );
		System.out.println("My work reported value: " + myWorkReportedValue);
		
		String wrmv = getElementsXPathWithNameValue("span", "wkrm");
		myWorkRemainingValue = getElementTextByXPath( wrmv );
		System.out.println("My work remaining value: " + myWorkRemainingValue);
		System.out.println();
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
	 * Update XPaths for working object (selected object), so we can access it's attributes.<br>
	 * 
	 * @param workingObjectCheckboxValue checkbox value for working object (checked checkbox).
	 */
	public void updateXPathsForWorkingObject(String workingObjectCheckboxValue) {
		//Workspace Name @id attribut value
		assignmentsWorkspaceNameId = "workspaceName" + workingObjectCheckboxValue;
		assignmentsWorkspaceNameIdXpath = "//td[@id=\"" + assignmentsWorkspaceNameId+ "\"]";
		
		//Type @id attribut value
		assignmentsTypeId = "objectType" + workingObjectCheckboxValue;
		assignmentsTypeIdXpath = "//td[@id=\"" + assignmentsTypeId+ "\"]";
		
		//Start Date @id attribut value
		assignmentsStartTimeId = "startTime" + workingObjectCheckboxValue;
		assignmentsStartTimeIdXpath = "//td[@id=\"" + assignmentsStartTimeId + "\"]";
		
		//End Date @id attribut value
		assignmentsEndTimeId = "endTime" + workingObjectCheckboxValue;
		assignmentsEndTimeIdXpath = "//td[@id=\"" + assignmentsEndTimeId + "\"]";
		
		//Actual Start @id attribut value
		assignmentsActualStartId = "actualStart" + workingObjectCheckboxValue;
		assignmentsActualStartIdXpath = "//td[@id=\"" + assignmentsActualStartId+  "\"]";
		
		//My % Complete @id attribut value		
		assignmentsPercentCompleteId = "percentComplete" + workingObjectCheckboxValue;
		assignmentsPercentCompleteIdXpath = "//td[@id=\"" + assignmentsPercentCompleteId + "\"]";
		
		//My Work @id attribut value		
		assignmentsWorkId = "work" + workingObjectCheckboxValue;
		assignmentsWorkIdXpath = "//td[@id=\"" + assignmentsWorkId + "\"]";
		
		//My Work Completed @id attribut value
		assignmentsWorkCompleteId = "workComplete" + workingObjectCheckboxValue;
		assignmentsWorkCompleteIdXpath = "//td[@id=\"" + assignmentsWorkCompleteId + "\"]";
		
		//My Work Remaining @id attribut value		
		assignmentsWorkRemainingId = "workRemaining" + workingObjectCheckboxValue;
		assignmentsWorkRemainingIdXpath = "//td[@id=\"" + assignmentsWorkRemainingId + "\"]";
		
	}
	
	/**
	 * Compares <b>presented</b> with <b>updated</b> working hour values for selected object.<br><br>
	 * <b>updateXPathsForWorkingObject(String workingObjectCheckboxValue)</b> is called first in
	 * this method to update XPaths , so that they reference to object just updated. 
	 *
	 */
	public void comparePresentedWithUpdatedWorkingHourValuesForSelectedObject() {
		
		//check is your work finished
		if( myWorkRemainingValue.equals("0 hrs")) {
			//then your work is finished, assert that your work isn't in the list
			//argument is ID of forms abbreviation name
			assertElementNotPresent("href" + checkboxFormsValue);	//better to put argument as class global property
			return;
		}
		
		updateXPathsForWorkingObject(checkboxFormsValue);
		
		//get Presented value for My % Complete 
		String myPercentCompletePresentedValue = getElementTextByXPath( assignmentsPercentCompleteIdXpath );
		System.out.println("My % Complete (Presented): " + myPercentCompletePresentedValue);
		
		assertTrue( myPercentCompletePresentedValue.trim().equals(myPercentCompleteValue) );
		
		//get Presented value for My Work
		String myWorkPresentedValue = getElementTextByXPath( assignmentsWorkIdXpath );
		System.out.println("My Work (Presented): " + myWorkPresentedValue);
		
		assertTrue( myWorkPresentedValue.trim().equals(myWorkValue) );
		
		//get Presented value for My Work Remaining 
		String myWorkRemainingPresentedValue = getElementTextByXPath( assignmentsWorkRemainingIdXpath);
		System.out.println("My Work Remaining (Presented): " + myWorkRemainingPresentedValue);
		
		assertTrue( myWorkRemainingPresentedValue.trim().equals(myWorkRemainingValue) );
		
		//get Presented value for My Work Completed
		String myWorkCompletedPresentedValue = getElementTextByXPath( assignmentsWorkCompleteIdXpath );
		System.out.println("My Work Completed (Presented): " + myWorkCompletedPresentedValue);
		
		assertTrue( myWorkCompletedPresentedValue.trim().equals(myWorkReportedValue) );
		
		
	}
}
