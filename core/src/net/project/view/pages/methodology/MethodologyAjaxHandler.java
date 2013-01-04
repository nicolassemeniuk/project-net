/**
 * 
 */
package net.project.view.pages.methodology;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.methodology.MethodologyProvider;
import net.project.methodology.MethodologySpace;
import net.project.persistence.PersistenceException;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * Class for handling Ajax requests for Methodology.
 * @author Uros Lates
 */
public class MethodologyAjaxHandler extends BasePage {
	
    @Inject
    private Request request;
    
    private static Logger log = Logger.getLogger(MethodologyAjaxHandler.class);
    
    Object onActivate(String action) {
		if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("save")) {
			return saveMethodology();
		} else {
			return new TextStreamResponse("text/plain", "");
		}
    }
    
    /**
	 * Method to save methodology
	 * 
	 * @return url
	 */
	private Object saveMethodology() {
		String moduleId = request.getParameter("moduleId");
		String action = request.getParameter("action");
		String theAction = request.getParameter("theAction");
		String refLink = request.getParameter("refLink");
		String selectedModules = request.getParameter("selectedModules"); // TODO: refactor request.getParameterValues('selectedModules') - return sting of checked chkboxes
		String[] modules = selectedModules.split(",");
		String name = request.getParameter("name") != null ? request.getParameter("name").trim() : null;
		JSONObject result = new JSONObject();
		
		try{
			User user = (User) SessionManager.getUser();  
			MethodologySpace methodologySpace = new MethodologySpace();

			// TODO: verify access: net.project.security.AccessVerifier.verifyAccess(moduleId, action, null);

			// prepare the methodology object
			methodologySpace.setUser(user);
			methodologySpace.setCreatedBy(user.getDisplayName());
			methodologySpace.setCreatedByID(user.getID());
			methodologySpace.setName(name);
			methodologySpace.setDescription (request.getParameter("description"));
			methodologySpace.setUseScenario (request.getParameter("useScenario"));
			methodologySpace.setParentSpaceID (request.getParameter("parentSpaceID"));
			methodologySpace.setGlobal(request.getParameter("isGlobal"));
			methodologySpace.setSelectedModulesStringArray(modules);
			methodologySpace.setBasedOnSpaceID(user.getCurrentSpace().getID());

			// check if methodology name already exists
			if(methodologySpace.isNameExist()){
				String errorMsg = PropertyProvider.get("prm.template.create.namealreadyexist.error.message");
				result.put("status", 0);				// FAIL indicator
				result.put("message", errorMsg);
			} else {
				methodologySpace.store();
				
				// 3 CreateTemplateProcessing.jsp
				MethodologyProvider methodologyProvider = request.getAttribute("methodologyProvider") != null ?
						(MethodologyProvider) request.getAttribute("methodologyProvider") : new MethodologyProvider();

				methodologyProvider.setUser(user);
				methodologyProvider.clearErrors();
				try {
					methodologyProvider.createTemplate(user.getCurrentSpace().getID(), methodologySpace);
				} catch (PnetException e) {
				    result.put("status", 0);			// FAIL indicator
				    result.put("hasErrors", 1);			// HAS ERRORS indicator - errors occurred during creation
				    result.put("title", PropertyProvider.get("prm.template.create.fail.title"));
				    result.put("message", PropertyProvider.get("prm.template.create.fail.message", new Object[]{name, methodologySpace.getParentSpaceName()}));
				    log.debug("Exception occured during Methodology saving: " + e.getMessage(), e);
				 
				    return new TextStreamResponse("text/plain", result.toString());
				}
				
				if (methodologyProvider.hasErrors()) {
					// methodology is created with errors
					result.put("status", 1);			// OK indicator - Methodology Space is created
					result.put("hasErrors", 1);			// HAS ERRORS indicator - errors occurred during creation
					result.put("errorsHeader", PropertyProvider.get("prm.template.create.error.successwitherrors"));
					result.put("message", PropertyProvider.get("prm.template.create.success.message", new Object[]{name, methodologySpace.getParentSpaceName()}));
					result.put("errors", methodologyProvider.getAllErrorMessages());
					log.debug("Errors while saving Methodology: " + methodologyProvider.getAllErrorMessages());
				} else {
				    methodologySpace.clear();
				    methodologySpace.load();
				    result.put("status", 1);			// OK indicator
				    result.put("hasErrors", 0);			// HAS ERRORS indicator - no errors occurred during creation
				    result.put("message", PropertyProvider.get("prm.template.create.success.message", new Object[]{name, methodologySpace.getParentSpaceName()}));
				}
				
			}
			
		} catch (AuthorizationFailedException e) {
			log.error("Authorization Error occured while handling methodology: " + e.getMessage());
			result.put("status", 0);				// FAIL indicator
			result.put("message", "You are not authorized to preform this action!");
			result.put("errorCause", e.getMessage());
			result.put("stackTrace", stack2string(e));
			e.printStackTrace();
		} catch (PersistenceException e) {
			log.error("Database related error occured while saving methodology: \n" + e.getMessage());
			result.put("status", 0);				// FAIL indicator
			result.put("message", "Internal, database related, server error occured during methodology creation!");
			result.put("errorCause", e.getMessage());
			result.put("stackTrace", stack2string(e));
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Error occured while saving methodology: \n" + e.getMessage());
			result.put("status", 0);				// FAIL indicator
			result.put("message", "Internal server error occured while saving methodology!");
			result.put("errorCause", e.getMessage());
			result.put("stackTrace", stack2string(e));
			e.printStackTrace();
		}

		return new TextStreamResponse("text/plain", result.toString());
	}
        
	private String stack2string(Exception e) {
		StringWriter sw = null;
		try {
			sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
		} catch(Exception ex) {
			log.error("Error in stack2string method!");
			ex.printStackTrace();
		}
		return sw == null ? null : sw.toString();
	}

}
