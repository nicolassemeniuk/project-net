package net.project.schedule.mvc.handler.taskview;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.project.ProjectSpace;
import net.project.schedule.MaterialAssignmentsHelper;
import net.project.schedule.ScheduleEntry;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.User;
import net.project.util.Validator;

public class TaskFinancialHandler extends AbstractTaskFinancialHandler {

	public TaskFinancialHandler(HttpServletRequest request) {
		super(request);
		setViewName("/schedule/TaskViewFinancial.jsp");
	}

    /**
     * Ensure that the requester has proper rights to access this object.  For
     * objects that aren't excluded from security checks, this will just
     * consist of verifying that the parameters that were used to access this
     * page were correct (that is, that the requester didn't try to "spoof it"
     * by using a module and action they have permission to.)
     *
     * @param module the <code>int</code> value representing the module that
     * was passed to security.
     * @param action the <code>int</code> value that was passed through security
     * for the action.
     * @param objectID the <code>String</code> value for objectID that was
     * passed through security.
     * @param request the entire request that was submitted to the schedule
     * controller.
     * @throws net.project.security.AuthorizationFailedException if the user didn't have the proper
     * credentials to view this page, or if they tried to spoof security.
     * @throws net.project.base.PnetException if any other error occurred.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        //According to the original documentation for the TaskView.jsp page,
        //TaskView.jsp can be reached with either view or modify permissions.  I
        //haven't figured out where that is occuring yet -- I'll just have to
        //take TaskView.jsp's word on it for now.
        if (action == Action.VIEW) {
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW, objectID);
        } else {
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY, objectID);
        }

        if (Validator.isBlankOrNull(objectID)) {
            throw new AuthorizationFailedException("task id parameter missing");
        }
    }
}
