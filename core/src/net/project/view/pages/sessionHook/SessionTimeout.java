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

package net.project.view.pages.sessionHook;
import java.text.MessageFormat;
import javax.servlet.http.HttpServletRequest;
import net.project.security.SessionManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

/**
 * This page handles the actions related to session timout popup 
 * 
 * It returns the popup of 'before session expiration' or 'after session expired' as a alert to user  
 *
 */
public class SessionTimeout {
	
    private static Logger log = Logger.getLogger(SessionTimeout.class);
    
    private boolean isSessionExpired;
	
    private String startSessionTime;
	
    @Inject
    private RequestGlobals requestGlobals;
    
    private String jspRootURL;
    
    private String applicationTitle;
    
    private String sessionInactiveTime;
	
    private String sessionAlertMessage;	
    
    private String sessionTimedOutMessage;	
    
    private String sessionExpiredTitle;
    
    private String sessionExpiredInMinutes;
    
    private String continueCurrentSession;
    
    private String logOutCurrentSession;
    
    private String sessionExpiredAt;
    
    private String newLoginAfterExpired;
    
    private String stillThereOptionMessage;
    
    private String sessionExpInMinutesMessage;
    
    private String sessionExpTitleMessage;
    
    private String stayOnThisPageMessage;

    // actions 
    private enum sessionTimeoutActions {
        GET_SESSION_TIMEOUT_POPUP;
        public static sessionTimeoutActions get( String v ) {
            try {
                return sessionTimeoutActions.valueOf( v.toUpperCase() );
            } catch( Exception ex ) {
                log.error("Unspecified action : " + ex.getMessage());
            }
            return null;
         }
    };
   
    public void FormattedTokens() {
        sessionAlertMessage = MessageFormat.format(sessionAlertMessage, new Object[] { applicationTitle, sessionInactiveTime });
        sessionTimedOutMessage = MessageFormat.format(sessionTimedOutMessage, new Object[] { applicationTitle, sessionInactiveTime });
        sessionExpiredInMinutes = MessageFormat.format(sessionExpiredInMinutes, new Object[] { applicationTitle });
        continueCurrentSession = MessageFormat.format(continueCurrentSession, new Object[] { applicationTitle });
        logOutCurrentSession = MessageFormat.format(logOutCurrentSession, new Object[] { applicationTitle });
        sessionExpiredAt = MessageFormat.format(sessionExpiredAt, new Object[] { applicationTitle });
        newLoginAfterExpired = MessageFormat.format(newLoginAfterExpired, new Object[] { applicationTitle });
    }
    
    void onActivate() {
        jspRootURL = SessionManager.getJSPRootURL();
    }
    
	/**
	 * @param action
	 * @return
	 */
	Object onActivate(String action) {
        HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		if (StringUtils.isNotEmpty(action)) {
            //it sets initial data to display on popup
            try {
                sessionTimeoutActions timeoutAction = sessionTimeoutActions.get( action );
                if (timeoutAction == sessionTimeoutActions.GET_SESSION_TIMEOUT_POPUP) {
        			String showPopUpOn = request.getParameter("showPopUpOn");
        			startSessionTime = request.getParameter("startSessionTime");
                    sessionInactiveTime = request.getParameter("sessionInactiveTime");
                    sessionAlertMessage = request.getParameter("sessionAlertMessage");
                    sessionTimedOutMessage = request.getParameter("sessionTimedOutMessage");
                    sessionExpiredInMinutes = request.getParameter("sessionExpiredInMinutes");
                    continueCurrentSession = request.getParameter("continueCurrentSession");
                    logOutCurrentSession = request.getParameter("logOutCurrentSession");
                    sessionExpiredAt = request.getParameter("sessionExpiredAt");
                    newLoginAfterExpired = request.getParameter("newLoginAfterExpired");
                    stillThereOptionMessage = request.getParameter("stillThereOptionMessage");
                    sessionExpInMinutesMessage = request.getParameter("sessionExpInMinutesMessage");
                    sessionExpTitleMessage = request.getParameter("sessionExpTitleMessage");
                    stayOnThisPageMessage = request.getParameter("stayOnThisPageMessage");
                    if(StringUtils.isNotEmpty(sessionInactiveTime)){
                        try {
                            sessionInactiveTime = (Integer.parseInt(sessionInactiveTime) / 60) > 0 ? (Integer.parseInt(sessionInactiveTime) / 60) + " hours" : sessionInactiveTime + " minutes";
                        } catch(Exception e) {
                            log.error("Error occurred while parsing session time: " + sessionInactiveTime);
                        }
                    }
                    applicationTitle = StringUtils.isEmpty(request.getParameter("applicationTitle")) ? "Project.net" : request.getParameter("applicationTitle");
                    
        			if (StringUtils.isNotEmpty(showPopUpOn) && showPopUpOn.equalsIgnoreCase("beforeSessionExpire")) {
                        isSessionExpired = true;
        			} else {
                        isSessionExpired = false;
        			}
        		}
                FormattedTokens();
            }catch(Exception e) {
                log.error("Error occurred while generating session timeout popup: " + e.getMessage());
            }
		}
		return null;
	}
    
    /**
     * @return the startSessionTime
     */
    public String getStartSessionTime() {
        return startSessionTime;
    }

    /**
     * @return the isSessionExpired
     */
    public boolean isSessionExpired() {
        return isSessionExpired;
    }
    
    /**
     * @return the applicationTitle
     */
    public String getApplicationTitle() {
        return applicationTitle;
    }
    
    /**
     * @return the jspRootURL
     */
    public String getJspRootURL() {
        return jspRootURL;
    }

    /**
     * @return the sessionInactiveTime
     */
    public String getSessionInactiveTime() {
        return sessionInactiveTime;
    }

	/**
	 * @return the sessionAlertMessage
	 */
	public String getSessionAlertMessage() {
		return sessionAlertMessage;
	}

	/**
	 * @return the sessionExpiredTitle
	 */
	public String getSessionExpiredTitle() {
		return sessionExpiredTitle;
	}

	/**
	 * @return the sessionTimedOutMessage
	 */
	public String getSessionTimedOutMessage() {
		return sessionTimedOutMessage;
	}

	/**
	 * @return the sessionExpiredInMinutes
	 */
	public String getSessionExpiredInMinutes() {
		return sessionExpiredInMinutes;
	}

	/**
	 * @return the continueCurrentSession
	 */
	public String getContinueCurrentSession() {
		return continueCurrentSession;
	}

	/**
	 * @return the logOutCurrentSession
	 */
	public String getLogOutCurrentSession() {
		return logOutCurrentSession;
	}

	/**
	 * @return the sessionExpiredAt
	 */
	public String getSessionExpiredAt() {
		return sessionExpiredAt;
	}

	/**
	 * @return the newLoginAfterExpired
	 */
	public String getNewLoginAfterExpired() {
		return newLoginAfterExpired;
	}

	/**
	 * @return the sessionExpInMinutesMessage
	 */
	public String getSessionExpInMinutesMessage() {
		return sessionExpInMinutesMessage;
	}

	/**
	 * @return the sessionExpTitleMessage
	 */
	public String getSessionExpTitleMessage() {
		return sessionExpTitleMessage;
	}

	/**
	 * @return the stayOnThisPageMessage
	 */
	public String getStayOnThisPageMessage() {
		return stayOnThisPageMessage;
	}

	/**
	 * @return the stillThereOptionMessage
	 */
	public String getStillThereOptionMessage() {
		return stillThereOptionMessage;
	}


}
