/**
 * 
 */
package net.project.util;

import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;
import net.project.security.User;

/**
 * @author 
 *
 */
public class TokenHelper {

	
	/**
	 * Gets tokens valeus using property provider.
	 * This class is specially desinged for freemarker templates.
	 * freemarker needs object to get values 
	 *   by using this class instance all tokens value will be available in freemarker templates.
	 * @param key
	 * @return String
	 */
	public String get(String key){
		return PropertyProvider.get(key);
	}
	
	/**
	 * Get context value from SessionManager jspRootUrl.
	 * @return String
	 */
	public String getContext(){
		return SessionManager.getJSPRootURL();
	}
	
	/**
	 * Get login user from value from SessionManager getUser.
	 * @return User
	 */
	public User getUser(){
		return SessionManager.getUser();
	}
}
