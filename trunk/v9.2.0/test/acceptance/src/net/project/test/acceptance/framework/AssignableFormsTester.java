package net.project.test.acceptance.framework;

import net.project.test.acceptance.engine.PnetTestEngine;

/**
 * Test case 5.1.0 <br>
 * <b>Create new assignable form in Project.net.</b><br>
 * <br>
 * 
 * In this test property <b>newProjectName</b> holds generated project name,
 * property <b>createdFormName</b> holds name of created form, property
 * <b>newFormAbbreviationName</b> holds the name of abbreviation for new form,
 * and properties <b>formsFiledlabel</b> and <b>testListName</b> hold label
 * and list names for that created form so you can access them if you nead them.<br>
 * <br>
 * <br>
 * 
 * @author Uros
 * 
 */
public class AssignableFormsTester extends PnetTestEngine {

	private String _uniqueNamePostFix = super.createUniqueValue("");
	
	public AssignableFormsTester(
			PnetAcceptanceTestFramework inializedPnetAcceptanceTestFramework) {
		_framework = inializedPnetAcceptanceTestFramework;
		tester = _framework.getTester();
	}

	protected String createNewAssignableForm(String existentProjectName,
			boolean assignable) {
		// 2. click on the projects tab
		_framework.goToProjects();

		// 3 navigate to project
		String newProjectName = (existentProjectName != null && !existentProjectName
				.equals("")) ? existentProjectName : _framework
				.createNewProject();
		System.out.println("New project name: " + newProjectName);
		clickOnProject(newProjectName);

		// 4. click on the forms link in the navigation pane
		clickOnNavigationPaneFormsLink();

		// 5. click on the Form Desiger link
		clickOnTheFormDesignerLink();

		// 6. click on new icon on top toolbar
		createNewForm();

		// 7. check this checkbox and complete form create process
		// name of form created
		final String createdFormName = createNewAssignmentFormCompletition(assignable);
		System.out.println(" Name of created form is:" + createdFormName);

		// 8. click on the new Form
		clickLinkWithExactText(createdFormName);
		assertTextPresent("No Items Found");

		// 9. click on new icon on top toolbar
		_framework.clickActionCreate();
		
		if (assignable) {
			displayAssignmentDetails();
			// 10. fill some work for the form, add rest of fields for form and save
			fillWorkForFormAddFieldsAndSave();
		}
		else {
			_framework.clickSubmitActionbarButton();
		}
		
		return _uniqueNamePostFix;
	}

	/**
	 * Helper method which clicks on project with the given name from within the
	 * Projects workspace
	 * 
	 * @param projectName
	 */
	public void clickOnProject(String projectName) {
		assertLinkPresentWithExactText(projectName);
		clickLinkWithExactText(projectName);
		assertTextPresent(projectName);
	}

	/**
	 * Clicks on <b>Forms</b> link in navigation pane. Supposes that you are on
	 * the selected projects page
	 * 
	 */
	private void clickOnNavigationPaneFormsLink() {
		assertLinkPresentWithExactText("Forms");
		clickLinkWithExactText("Forms");
		assertLinkPresentWithExactText("Form Designer");
	}

	/**
	 * Clicks on <b>Form Designer</b> link on right top of the page.
	 * 
	 */
	private void clickOnTheFormDesignerLink() {
		clickLinkWithExactText("Form Designer");
		assertTextPresent("Form Designer");
		assertTextPresent("No forms have been created for this workspace");
	}

	/**
	 * Clicks on the <b>Create</b> application bar link, and tests the outcome
	 * (presence of <i>Include Assignment Fields</i> check box).
	 * 
	 */
	private void createNewForm() {
		_framework.clickActionNew();
		assertTextPresent("Definition");
		assertTextPresent("Include Assignment Fields");
		assertCheckboxPresent("supportsFormAssignment");
	}

	/**
	 * Finishes the job of the createNewForm() method. Creates new assignment
	 * form.
	 * 
	 */
	private String createNewAssignmentFormCompletition(boolean assignable) {
		// give the unique name to the form
		String formAbbreviation = "tA";
		String uniqueFormName = createUniqueValue("testForm");
		final String newFormAbbreviationName = createUniqueValue(formAbbreviation);

		assertFormElementPresent("name");
		setTextField("name", uniqueFormName);

		// fill in Form Abbreviation
		assertFormElementPresent("abbreviation");
		setTextField("abbreviation", newFormAbbreviationName);

		// check the Display in Tools Menu:
		assertTextPresent("Display in Tools Menu");
		assertCheckboxPresent("displayedInToolsMenu");
		checkCheckbox("displayedInToolsMenu");

		// check the Include Assignment Fields
		if (assignable) {
			checkCheckbox("supportsFormAssignment");
		}

		_framework.clickSubmitActionbarButton();

		assertTextPresent("Defined Fields");

		// function for processing the rest of the forms creation process
		// (lists, activate...)
		finishFormCreationProcess(uniqueFormName);
		return uniqueFormName;
	}

	/**
	 * We are on the Fields tab
	 * 
	 */
	private void finishFormCreationProcess(String formName) {
		String selectElementName = "ElementID";
		// click on the Create toolbar button
		_framework.clickActionCreate();

		assertTextPresent("Add Field");
		assertSelectOptionPresent(selectElementName, "Text Field");
		selectOption(selectElementName, "Text Field");

		// Form Labes filling
		assertFormElementPresent("field_label");
		final String formsFieldLabel = createUniqueValue("testLabel");
		setTextField("field_label", formsFieldLabel);

		// Form Row:
		assertFormElementPresent("row_num");
		setTextField("row_num", "1");

		// Form Length:
		assertFormElementPresent("size");
		setTextField("size", "5");

		// Form Max Length:
		assertFormElementPresent("data_column_size");
		setTextField("data_column_size", "15");

		// Submit
		_framework.clickSubmitActionbarButton();

		// checks for existence of new field with created Label
		assertLinkPresentWithExactText(formsFieldLabel);

		// creating a Lists
		assertLinkPresentWithExactText("Lists");
		clickLinkWithExactText("Lists");

		// create a new List
		_framework.clickActionCreate();

		// check if we are on create new List page
		assertTextPresent("Add Form List");

		// check for existance and set the working form
		assertFormPresent("listForm");
		setWorkingForm("listForm");
		// fill in the List Name:

		final String testListName = createUniqueValue("testList");

		System.out.println("The name of the List: " + testListName);

		assertFormElementPresent("name");
		setTextField("name", testListName);

		// check one field if present (previosly created field)
		// the code is added on JSP to create id of checkbox
		assertElementPresent(formsFieldLabel);
		// check checkbox with id
		checkCheckboxWithId(formsFieldLabel);

		_framework.clickSubmitActionbarButton();

		// click on the Activate link
		assertLinkPresentWithExactText("Activate");
		clickLinkWithExactText("Activate");

		// change form status to Activated on Activate tab
		assertTextPresent("Change form status:");
		assertSelectOptionPresent("formStatus", "Activated");
		selectOption("formStatus", "Activated");
		_framework.clickSubmitActionbarButton();

		// Name for created form should be under Forms navigation pane
		assertLinkPresentWithExactText(formName);

	}

	private void displayAssignmentDetails() {

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

	private void fillWorkForFormAddFieldsAndSave() {
		assertElementPresent("work");
		setTextField("work", "100");
		assertSelectOptionPresent("work_units", "hours");
		selectOption("work_units", "hours");
		assertElementPresent("work_percent_complete");
		setTextField("work_percent_complete", "30");
		_framework.clickSubmitActionbarButton();
	}
	
	@Override
	public String createUniqueValue(String valuePrefix) {
		return valuePrefix + _uniqueNamePostFix;
		
	}

}
