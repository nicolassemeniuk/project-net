package net.project.test.acceptance.schedule;

import net.project.test.acceptance.engine.PnetTestEngine;

public class TaskActionsTest extends PnetTestEngine {

    public void testCreateEditTaskNamePage() {
        String uniqueTaskName = _framework.createNewTask();

        checkCheckbox("selected");
        _framework.clickActionModify();
        assertFormPresent("taskEdit");
        setWorkingForm("taskEdit");
        assertTextPresent("Name:");
        assertFormElementPresent("name");
        setTextField("name", uniqueTaskName + "x");

        _framework.clickSubmitActionbarButton();
                
        clickLinkWithExactText("workplan");
        assertTextPresent(uniqueTaskName+ "x");
    }
    
    public void testDeleteTask(String taskName) {

    }

}
