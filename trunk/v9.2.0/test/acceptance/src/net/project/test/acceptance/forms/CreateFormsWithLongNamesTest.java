package net.project.test.acceptance.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.test.acceptance.engine.PnetTestEngine;

public class CreateFormsWithLongNamesTest  extends PnetTestEngine {
	
	public void testCreateFormsWithLongNames() throws Exception {
		// bug-5341
		// Scroll bar is not displayed to view the bottom modules listed 
		// when more forms are created. 
		
		final String newProjectName = _framework.createNewProject("testProjectWithLongNameForms");
		final List<String> newFormsNames = new ArrayList<String>();
		
		//for (int i = 0; i < 3; i++) {
			newFormsNames.add(_framework.createNewAssignableForm(newProjectName, false));	
		//}
		
		for (Iterator iterator = newFormsNames.iterator(); iterator.hasNext();) {
			System.out.println(iterator.next());	
		}
	}
}
