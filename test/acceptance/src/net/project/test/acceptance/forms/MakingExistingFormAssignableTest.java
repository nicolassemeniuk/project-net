package net.project.test.acceptance.forms;

import net.project.test.acceptance.engine.PnetTestEngine;

/**
 * Test case 5.1.1 <br>
 * <b>Editing existing form and make it assignable.</b><br><br>
 * 
 * In order to make this test you should edit <b>projectName</b> property to hold 
 * the name of existing project, set <b>notAssignableFormYet</b> property to the 
 * name of form which you want to make assignable, and set property <b>formAbbreviation</b> 
 * to form abbreviation name of previosly defined form.
 * 
 * @author Uros
 *
 */
public class MakingExistingFormAssignableTest extends PnetTestEngine {

	private String projectName;
	//the name of the form
	private String notAssignableFormYetName;
	//forms abbreviation name (used for dinamically generated names of form objects)
	private String formAbbreviation = "tA";
	
	private String _uniquePostFixName;
	
	public void testMakeExistenentFormAssignable() throws Exception {
		
		projectName = _framework.createNewProject();
		_framework.goToPersonal();
		
		_uniquePostFixName = _framework.createNewAssignableForm(projectName, false);
		notAssignableFormYetName = "testForm" + _uniquePostFixName;
		
		// (2) click Projects tab
		_framework.goToProjects();
		
		// (3) select Project that has forms created
		selectProject(projectName);
		
		// (4) click on navigations Forms link/Form Designer link
		clickOnNavigationPaneFormsLinkForProject();
		
		// 5. select modify option for form not assignable yet
		selectingNotAssignableFormAndClickModify(notAssignableFormYetName);
		
		//6. check Include Assignment Fields checkbox and submit
		checkIncludeAssignmentsCheckBox();
		_framework.clickSubmitActionbarButton();
		
		//7. click on modified form in navigation pane
		clickLinkWithExactText(notAssignableFormYetName);
		
		//8. select one form object
		
		//first forms object name(dinamically generated): formsAbbreviation + "-ObjectNumber"
		
		clickLinkWithExactText(formAbbreviation + _uniquePostFixName + "-1");
		displayAssignmentDetails();
		
		//9. fill some work for the form and save
		fillWorkForFormAndSave();
		
	}
	
	public void selectProject(String projectName) {
		assertLinkPresentWithExactText(projectName);
		clickLinkWithExactText(projectName);
		assertTextPresent(projectName);
		assertLinkPresentWithExactText("Forms");
	}

	/**
	 * Clicks on <b>Form Designer</b> link on right top of the page.
	 * 
	 */
	public void clickOnTheFormDesignerLink() {
		clickLinkWithExactText("Form Designer");
		assertTextPresent("Form Designer");
	}

	/**
	 * Select notassignable form with the given name
	 * 
	 * @param notAssignableFormName the name of the form which you want to make assignable
	 */
	public void selectingNotAssignableFormAndClickModify(String notAssignableFormName) { 
		assertLinkPresentWithExactText(notAssignableFormName);
		clickLinkWithExactText(notAssignableFormName);
		assertTextPresent("Include Assignment Fields");
	}
	
	public void checkIncludeAssignmentsCheckBox() {
		assertCheckboxPresent("supportsFormAssignment");
		checkCheckbox("supportsFormAssignment");
	}
	
	/**
	 * Clicks on Navigation pane <b>Forms</b> link from within Project workspace 
	 * and then on <b>FormDesigner</b> link at right top of page.
	 *
	 */
	public void clickOnNavigationPaneFormsLinkForProject() {
		clickOnNavigationPaneFormsLink();
		clickLinkWithExactText("Form Designer");
		assertTextPresent("Form Designer");
	}
	
	public void clickOnNavigationPaneFormsLink() {
		assertLinkPresentWithExactText("Forms");
		clickLinkWithExactText("Forms");
		assertLinkPresentWithExactText("Form Designer");
	}

	/**
	 * Check for presence of Assignment Details (Assigned User, Work, Work % Complete...)
	 */
	public void displayAssignmentDetails() {
		
		assertTextPresent("Assigned User");
		assertTextPresent("Work");
		assertTextPresent("Work Complete");
		assertTextPresent("Work % Complete");
		assertTextPresent("Start Date");
		assertTextPresent("End Date");
		assertTextPresent("Current Assignment");
		
		assertFormPresent("formToCheck");
		setWorkingForm("formToCheck");
		
	}	
	
	/**
	 * Fills in the form details on Assignments page for that selected form. 
	 * Sets working hours... and submits changes.
	 */
	public void fillWorkForFormAndSave() {
		assertElementPresent("work");
		setTextField("work", "100");
		assertSelectOptionPresent("work_units", "hours");
		selectOption("work_units", "hours");
		assertElementPresent("work_percent_complete");
		setTextField("work_percent_complete", "30");
		_framework.clickSubmitActionbarButton();
	}

}
