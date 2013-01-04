package net.project.test.acceptance.forms;

import net.project.test.acceptance.engine.PnetTestEngine;

/**
 * Test case 5.1.0 <br> 
 * <b>Create new assignable form in Project.net.</b><br><br>
 * 
 * In this test property <b>newProjectName</b> holds generated project name, 
 * property <b>createdFormName</b> holds name of created form, property <b>newFormAbbreviationName</b> holds 
 * the name of abbreviation for new form, and properties <b>formsFiledlabel</b> and <b>testListName</b> hold 
 * label and list names for that created form so you can access them if you nead them.<br>
 * @author Uros
 *
 */
public class AssignableFormsTest extends PnetTestEngine {

	/**
	 * Test for creating new Assignable Form in Project.net
	 * @throws Exception
	 */
	public void testCreateNewAssignableForm() throws Exception {
		_framework.createNewAssignableForm(null, true);
	}
	
}
