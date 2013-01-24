/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.soa.handler;

import javax.xml.namespace.QName;

import net.project.business.BusinessSpaceBean;
import net.project.project.ProjectSpaceBean;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.security.login.LoginManager;
import net.project.security.login.LoginStatusCode;
import net.project.space.PersonalSpaceBean;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;

/**
 * Handler for project.net security over services layer.
 * 
 * 
 * @author Avinash Bhamare
 * @since 8.3.0
 */
public class SecurityHandler implements org.apache.cxf.interceptor.Interceptor<SoapMessage> {

	public final static String PNET_NS = "http://www.project.net/api";
    public final static String USERNAME = "j_username";
    public final static String PASSWORD = "j_password";
    public final static String DOMAIN = "j_domain";
    public final static String SPACEID = "j_spaceid";
    public final static String SPACETYPE = "j_spacetype";
    
    public final static String GUEST_USERNAME = "guest";
    public final static String GUEST_PASSWORD = "guest";
    public final static String GUEST_DOMAIN = "1000"; // global
    public final static String GUEST_SPACETYPE = "personal"; 
    
    LoginManager loginManager = null; 
    
    public SecurityHandler() 
    {
        super();
    }

	protected LoginStatusCode authenticateUser(String username, String password, String userDomain){
		// Pass to login manager and complete the login process
        loginManager.setLicenseProperties(net.project.license.system.LicenseProperties.getInstance());
        loginManager.createLoginContext(username, password, userDomain);
        LoginStatusCode statusCode = null;
        try{
        	statusCode = loginManager.completeLogin();
        }catch(net.project.base.PnetException pp){
        	//Error found during completing the Login Process. Show the error.
        	statusCode = LoginStatusCode.AUTHENTICATION_ERROR;
        }
        return statusCode;
	}
	
	protected String getHeaderParaValue(String para, SoapMessage message, String defaultValue){
		Object paraValue = null;
        Header el = message.getHeader(new QName(PNET_NS, para ));
        if (el != null ){ 
        	paraValue = el.getObject(); 
        	if (paraValue == null)
	        {
        		paraValue = defaultValue;
	        }
        } 
        return (String)paraValue;
	}
	
	public void handleFault(SoapMessage arg0) {
		// do nothing
		
	}
	
	public void handleMessage(SoapMessage message) throws Fault {

		String username, password, domain, spaceId, spaceType;
		User user = null;
        // Element header = context.getInMessage().getHeader();
        // if (header == null) return;
        username = getHeaderParaValue(USERNAME, message, GUEST_USERNAME);
        password = getHeaderParaValue(PASSWORD, message, GUEST_PASSWORD);
        domain = getHeaderParaValue(DOMAIN, message, GUEST_DOMAIN);
        spaceType = getHeaderParaValue(SPACETYPE, message, GUEST_SPACETYPE);
        spaceId = getHeaderParaValue(SPACEID, message, null);
		loginManager = new LoginManager();
		try{
			user = SessionManager.getUser();
		}catch(Exception ignore){}
		if(null != user) {
			user.setAuthenticated(false);
			user.clear();
		}
		try{ 
	        if(authenticateUser(username, password, domain).equals(LoginStatusCode.SUCCESS)){
	        	user = loginManager.getAuthenticatedUser();
	    		if(null == spaceType || "personal".equalsIgnoreCase(spaceType)){
	    			// Create personal space		
	        		PersonalSpaceBean personalSpace = new PersonalSpaceBean();
	                personalSpace.setID(user.getID());
	                personalSpace.load();
	                user.setCurrentSpace (personalSpace);
	    		}else if("business".equalsIgnoreCase(spaceType)){
	    			// Create business space		
	        		BusinessSpaceBean businessSpace = new BusinessSpaceBean();
	        		businessSpace.setID(spaceId);
	        		businessSpace.load();
	                user.setCurrentSpace (businessSpace);
	    		}else if("project".equalsIgnoreCase(spaceType)){
	    			// Create business space		
	        		ProjectSpaceBean projectSpace = new ProjectSpaceBean();
	        		projectSpace.setID(spaceId);
	        		projectSpace.load();
	                user.setCurrentSpace (projectSpace);
	    		}
	    		SessionManager.setUser(user);
	        }else{
	        	throw new Exception("Invalid system access!");
	        }
		}catch( Exception ex){
			throw new Fault(ex);
		}
		
	}
}
