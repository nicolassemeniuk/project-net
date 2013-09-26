package net.project.schedule.mvc.handler.taskview;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;

public class AbstractTaskFinancialHandler extends AbstractTaskViewHandler {

    public AbstractTaskFinancialHandler(HttpServletRequest request) {
        super(request);
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {

        //First verify the parameters
        if (action == Action.VIEW) {
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW, objectID);
        } else if (action == Action.CREATE) {
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.CREATE, objectID);
        } else {
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY, objectID);
        }
        SessionManager.getSecurityProvider().securityCheck(objectID, String.valueOf(Module.SCHEDULE), Action.MODIFY);
        
    }
}