/**
 * 
 */
package net.project.view.pages.base;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpaceBean;
import net.project.hibernate.service.IBusinessSpaceService;
import net.project.hibernate.service.IPnActivityLogService;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.IPnAssignmentWorkService;
import net.project.hibernate.service.IPnBusinessHasViewService;
import net.project.hibernate.service.IPnBusinessSpaceService;
import net.project.hibernate.service.IPnChargeCodeService;
import net.project.hibernate.service.IPnObjectHasChargeCodeService;
import net.project.hibernate.service.IPnObjectNameService;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.IPnSpaceHasSpaceService;
import net.project.hibernate.service.IPnSpaceViewContextService;
import net.project.hibernate.service.IUtilService;
import net.project.hibernate.service.ServiceFactory;
import net.project.project.ProjectSpaceBean;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.PersonalSpaceBean;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.util.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Context;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

/**
 *
 */
public class BasePage {
	
	@Inject
    public static Logger logger;
	
    @Persist(PersistenceConstants.FLASH)
    private String message;
    
    @Inject
    private RequestGlobals globals;
        
    @Inject
    private Request request;
    
    @Inject
    private Response response;
 
    @Inject
    private Context context;
    
	@Inject
	private Cookies cookies;
    /**
	 * @return projectVersion
	 */
	public String getProjectVersion(){
		return StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
	}
	
    /**
     * @return JSPRootURL
     */
    public String getJSPRootURL(){
		return SessionManager.getJSPRootURL();
	}

    // TODO: Direct access to servlet API should be avoided whenever possible
    public HttpServletRequest getHttpServletRequest() {
    	return globals.getHTTPServletRequest();
    }
    
    // TODO: Direct access to servlet API should be avoided whenever possible
    public HttpServletResponse getHttpServletResponse() {
    	return globals.getHTTPServletResponse();
    }

    // TODO: Direct access to servlet API should be avoided whenever possible
    public ServletContext getServletContext() {
        return getHttpServletRequest().getSession().getServletContext();
    }
    
    // TODO: Direct access to servlet API should be avoided whenever possible
    public HttpSession getSession() {
        return getHttpServletRequest().getSession();
    }
    
    //  TODO: Direct access to servlet API should be avoided whenever possible
    public Object getSessionAttribute(String attribute) {
        return getSession().getAttribute(attribute);
    }
    
    public Object getRequestAttribute(String attribute) {
        return request.getAttribute(attribute);
    }
    
    //  TODO: Direct access to servlet API should be avoided whenever possible
    public void setSessionAttribute(String attribute, Object value) {
        getSession().setAttribute(attribute, value);
    }
    
    public void setRequestAttribute(String attribute, Object value) {
        request.setAttribute(attribute, value);
    }
    
    
    // Read token value 
    public String getText(String key) {
        return PropertyProvider.get(key);
    }
    
    public String getText(String key, Object arg) {
        if (arg == null) {
            return getText(key);
        }

        if (arg instanceof String) {
        	return PropertyProvider.get(key, (String)arg);
        } else if (arg instanceof Object[]) {
            return PropertyProvider.get(key, (Object[]) arg);
        } else {
            logger.error("argument '" + arg + "' not String or Object[]");
            return "";
        }
    }
    
    /**
     * @return User object
     */
    public User getUser(){
    	return SessionManager.getUser();
    }

    /**
	 * @return the request
	 */
	public Request getRequest() {
		return request;
	}
	

	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
	}

    
    /**
	 * @return the applicationTitle
	 */
	public String getApplicationTitle() {
		return getText("prm.global.application.title");
	}
    
    public IPnActivityLogService getPnActivityLogService(){
    	return ServiceFactory.getInstance().getPnActivityLogService();
    }
    
    public IPnPersonService getPnPersonService(){
    	return ServiceFactory.getInstance().getPnPersonService();
    }
    
    public IUtilService getUtilService(){
    	return ServiceFactory.getInstance().getUtilService();
    }

	public IPnProjectSpaceService getPnProjectSpaceService(){
		return ServiceFactory.getInstance().getPnProjectSpaceService();
	}
	
	public IBusinessSpaceService getBusinessSpaceService(){
		return ServiceFactory.getInstance().getBusinessSpaceService();
	}
	
	public IPnObjectNameService getPnObjectNameService(){
		return ServiceFactory.getInstance().getPnObjectNameService();
	}
    
	public IPnAssignmentWorkService getPnAssignmentWorkService(){
		return ServiceFactory.getInstance().getPnAssignmentWorkService();
	}
	
	public IPnAssignmentService getPnAssignmentService(){
		return ServiceFactory.getInstance().getPnAssignmentService();
	}
	
	public IPnSpaceViewContextService getPnSpaceViewContextService(){
		return ServiceFactory.getInstance().getPnSpaceViewContextService();
	}
	
	public IPnBusinessHasViewService getPnBusinessHasViewService(){
		return ServiceFactory.getInstance().getPnBusinessHasViewService();
	}
	
	public IPnChargeCodeService getPnChargeCodeService(){
		return ServiceFactory.getInstance().getPnChargeCodeService();
	}

	public IPnSpaceHasSpaceService getPnSpaceHasSpaceService(){
		return ServiceFactory.getInstance().getPnSpaceHasSpaceService();
	}

	public IPnObjectHasChargeCodeService getPnObjectHasChargeCodeService(){
		return ServiceFactory.getInstance().getPnObjectHasChargeCodeService();
	}

	public IPnBusinessSpaceService getPnBusinessSpaceService(){
		return ServiceFactory.getInstance().getPnBusinessSpaceService();
	}

	public String getParameter(String parameter) {
        return request.getParameter(parameter);
    }
	
	/**
	 * forward to login page if session has expired 
	 */
	public Object checkForUser(){
		URL url = null;
		try {
			if (SessionManager.getUser() == null || SessionManager.getUser().getID() == null) {
	        	url =  new URL(SessionManager.getAppURL() + "/Login.jsp");
			}
	    } catch (MalformedURLException pnetEx) {
			logger.error("Error occurred while handling security : " + pnetEx.getMessage());
		}
	    return url;
	}
	
	/**
	 * @return response object
	 */
	public Response getResponse() {
		return response;
	}
	
	/**
	 * @param parameter
	 * @return request parameter
	 */
	public String getRequestParameter(String parameter) {
        return getHttpServletRequest().getParameter(parameter);
    }
	
    /**
	 * Check for access allowed for specified module and action in specified space 
	 * @param spaceId space identifier
	 * @return true or false
	 */
	public boolean isAccessAllowed(String spaceId, int module , int action ) {
        boolean accessAllowed = false;
        if (StringUtils.isNotEmpty(spaceId)) {            
            SecurityProvider securityProvider = SessionManager.getSecurityProvider();
            // Security Check: Is user allowed access to requested module?
            securityProvider.setSpace(getUser().getCurrentSpace());
            if (securityProvider.isActionAllowed(null, Integer.toString(module), action)) {
                accessAllowed = true;
            }
        }
        return accessAllowed;
    }
    
	/**
	 * checking access for perticular user
	 * @param spaceId
	 * @param module
	 * @param action
	 * @return
	 */
    public Object checkAccess(String spaceId, int module, int action){
    	URL url = null;
    	if(!isAccessAllowed(spaceId, module, action)) {
     		 getRequest().setAttribute("exception", new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.main.authorizationfailed.message"), getUser().getCurrentSpace()));
     	    	try {
					url = new URL(SessionManager.getAppURL() + "/AccessDenied.jsp");
				} catch (MalformedURLException pnetEx) {
					logger.error("Error occurred while handling security : " + pnetEx.getMessage());
				}
    		
    	}
		return url;
    }
    
    /**
	 * Check for access allowed for specified module and action in specified space for specified user.
	 * @param spaceId space identifier
	 * @return true or false
	 */
    public boolean isAccessAllowed(Space space, int module, int action, User user){
        SecurityProvider checkSecurityProvider = new SecurityProvider();
        checkSecurityProvider.setUser(user);
        checkSecurityProvider.setSpace(space);
        return checkSecurityProvider.isActionAllowed(null, module, action);
    }
    
    
    /**
     * current screen height form session
     * @return
     */
    protected int getWindowHeight(){
    	return StringUtils.isNumeric(cookies.readCookieValue("h")) ? Integer.valueOf(cookies.readCookieValue("h")) : 0;
    }
    
    /**
     * current screen width from session. 
     * @return screen width
     */
    protected int getWindowWidth(){
    	return StringUtils.isNumeric(cookies.readCookieValue("w"))? Integer.valueOf(cookies.readCookieValue("w")) : 0;
    }
    
    /**
     * set current screen width and height in session.
     * will be used to render page with perfect resolution calculation.
     * @param width
     * @param height
     */
    protected void putResolutionInCookie(String width, String height){
    	cookies.writeCookieValue("w", width);
    	cookies.writeCookieValue("h", height);
    }
    
    public void checkForSpace(String spaceType){
    	if(!getUser().getCurrentSpace().isTypeOf(spaceType)) {
    		try {
	    		if(spaceType.equalsIgnoreCase(SpaceTypes.PROJECT_SPACE)){
					getUser().setCurrentSpace((ProjectSpaceBean)getSession().getAttribute("projectSpace"));
	    		}else if(spaceType.equalsIgnoreCase(SpaceTypes.PERSONAL_SPACE)){
					getUser().setCurrentSpace((PersonalSpaceBean)getSession().getAttribute("personalSpace"));
	    		}else if(spaceType.equalsIgnoreCase(SpaceTypes.BUSINESS_SPACE)){
	    			getUser().setCurrentSpace((BusinessSpaceBean)getSession().getAttribute("businessSpace"));
	    		}
    		} catch (PnetException pnetEx) {
    			logger.error("Error occurred while setting space : " + pnetEx.getMessage());
			}
    	}
    }
    
    /**
     * @return true if http connection is secure
     */
    public boolean isSecureConnection(){
    	return SessionManager.getSiteScheme().toLowerCase().contains("https");
    }
    
}
