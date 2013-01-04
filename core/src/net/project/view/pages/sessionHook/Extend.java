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

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;
import net.project.view.pages.base.BasePage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.util.TextStreamResponse;
import org.json.JSONException;
import org.json.JSONStringer;

/**
 * This page handles the actions related to session timout popup 
 * 1- to return session inactive time
 * 2- to reset the session when user clicks to continue the session 
 *
 */
public class Extend extends BasePage {
    
    private static Logger log = Logger.getLogger(Extend.class);
    
    @Inject
    private RequestGlobals requestGlobals;
    
    // actions 
    private enum sessionTimeoutActions {
        GET_SESSION_INACTIVE_INTERVAL, SESSION_EXPIRED, PUT_SCREEN_RESOLUTION_IN_COOKIE ;
        public static sessionTimeoutActions get( String v ) {
            try {
                return sessionTimeoutActions.valueOf( v.toUpperCase() );
            } catch( Exception ex ) {
                log.error("Unspecified action : " + ex.getMessage());
            }
            return null;
         }
    };
    
    /**
	 * @param action
	 * @return
	 */
    
	Object onActivate(String action) {
        HttpServletRequest request = requestGlobals.getHTTPServletRequest();
        JSONStringer stringer = new JSONStringer();
		if (StringUtils.isNotEmpty(action)) {
             
            // it sets json object with session inactive time interval ,
            // start timer or not according to session value and application title to be displayed on popup
            sessionTimeoutActions timeoutAction = sessionTimeoutActions.get( action ); 
            if (timeoutAction == sessionTimeoutActions.GET_SESSION_INACTIVE_INTERVAL) {
                try {
                    String showActions = request.getParameter("showPopUpOn");
                    if (StringUtils.isNotEmpty(showActions) && showActions.equalsIgnoreCase("beforeSessionExpire")) {
                        stringer.object();
                        int interval = request.getSession(true).getMaxInactiveInterval() / 60;
                        boolean isUserAvailable = SessionManager.getUser() != null && StringUtils.isNotEmpty(SessionManager.getUser().getID());
                        stringer.key("sessionInactiveInterval").value(interval);
                        stringer.key("startTimer").value(isUserAvailable);
                        stringer.key("applicationTitle").value(PropertyProvider.get("prm.global.application.title"));
                        
                        stringer.key("sessionAlertMessage").value(PropertyProvider.get("prm.global.sessiontimeout.sessionexpired.alert.message"));
                        stringer.key("sessionTimedOutMessage").value(PropertyProvider.get("prm.global.sessiontimeout.sessionexpired.timedout.message"));
                        stringer.key("sessionExpiredInMinutes").value(PropertyProvider.get("prm.global.sessiontimeout.currentsessionexpire.confirm.message"));
                        stringer.key("continueCurrentSession").value(PropertyProvider.get("prm.global.sessiontimeout.currentsession.continue.option.message"));
                        stringer.key("logOutCurrentSession").value(PropertyProvider.get("prm.global.sessiontimeout.currentsession.logout.option.message"));
                        stringer.key("sessionExpiredAt").value(PropertyProvider.get("prm.global.sessiontimeout.afterexpiredpopup.expiredat.showtime.message"));
                        stringer.key("newLoginAfterExpired").value(PropertyProvider.get("prm.global.sessiontimeout.afterexpiredpopup.login.message"));
                        stringer.key("stillThereOptionMessage").value(PropertyProvider.get("prm.global.sessiontimeout.areyoustillthere.confirm.message"));
                        stringer.key("sessionExpInMinutesMessage").value(PropertyProvider.get("prm.global.sessiontimeout.currentsessionexpire.minute.confirm.message"));
                        stringer.key("sessionExpTitleMessage").value(PropertyProvider.get("prm.global.sessiontimeout.sessionexpired.title.message"));
                        stringer.key("stayOnThisPageMessage").value(PropertyProvider.get("prm.global.sessiontimeout.stayonthispage.confirm.message"));
                        stringer.endObject();
                        return new TextStreamResponse("text/json", stringer.toString());
                    }
                } catch(JSONException e) {
                    log.error("Error occurred while setting json object : " + e.getMessage());
                    return new TextStreamResponse("text/plain", "false");
                } catch(Exception e) {
                    log.error("Error occurred while getting session interval : " + e.getMessage());
                    return new TextStreamResponse("text/plain", "false");
                }
            }else if (timeoutAction == sessionTimeoutActions.PUT_SCREEN_RESOLUTION_IN_COOKIE){
    			putResolutionInCookie(getParameter("windowWidth"), getParameter("windowHeight"));
    			return new TextStreamResponse("text/plain", "success");
    		}
            // just visit to reset the session
            else if (timeoutAction == sessionTimeoutActions.SESSION_EXPIRED){
                return new TextStreamResponse("text/plain", "true");
			}
		}
		return null;
	}
    
}