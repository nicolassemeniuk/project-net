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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18405 $
|       $Date: 2008-11-23 19:38:47 -0200 (dom, 23 nov 2008) $
|     $Author: puno $
|
+----------------------------------------------------------------------*/
package net.project.security;

import net.project.base.property.PropertyProvider;

import org.apache.log4j.Logger;

/**
 * This class is responsible for checking that the security parameters that a 
 * JSP page expects are the same parameters that were passed to the page.  The
 * parameters that are being checked are module, action, and id, which are the
 * basis of Project.Net security.
 *
 * @author Matthew Flower
 * @since 7.4
 */
public class AccessVerifier {
    /**
     * DEFAULT_ERROR_VALUE is the value assigned to the module, action, and 
     * object_id parameters by the verifyaccess tag library if these variables
     * have not been set.
     */
    public static final int DEFAULT_ERROR_VALUE = -1;

    public static final Logger logger = Logger.getLogger(AccessVerifier.class);

    /**
     * Check that specified parameters are the same as the parameters that were
     * used the last time that SecurityProvider was called to verify access to 
     * a JSP page.    
     *
     * Note that this method assumes that the action is Action.VIEW and objectID
     * is null.  Action will be checked against the last security parameters even
     * though you have not provided it as a parameter.  
     * 
     * @param module a <code>String</code> value that indicates the module that
     * we want to compare to the security provider.  Valid module constants can 
     * be found by looking at {@link net.project.base.Module}.
     * @throws AuthorizationFailedException when the parameters provided to this
     * method are not the same as the last parameters provided to the security
     * system.
     */
    public static void verifyAccess(String module) throws AuthorizationFailedException {
        verifyAccess(Integer.parseInt(module), Action.VIEW);
    }
    
    /**
     * Check that specified parameters are the same as the parameters that were
     * used the last time that SecurityProvider was called to verify access to 
     * a JSP page.    
     * 
     * @param module a <code>String</code> value that indicates the module that
     * we want to compare to the security provider.  Valid module constants can 
     * be found by looking at {@link net.project.base.Module}.
     * @param action a <code>String</code> value that indicates the action that
     * we want to compare to the security provider.  Valid action constants can
     * be found by looking at the statics in net.project.security.Action.
     * @throws AuthorizationFailedException when the parameters provided to this
     * method are not the same as the last parameters provided to the security
     * system.
     */
    public static void verifyAccess(String module, String action) throws AuthorizationFailedException  {
        verifyAccess(module, action, null);
    }

    /**
     * Check that specified parameters are the same as the parameters that were
     * used the last time that SecurityProvider was called to verify access to 
     * a JSP page.    
     * 
     * @param module a <code>int</code> value that indicates the module that
     * we want to compare to the security provider.  Valid module constants can 
     * be found by looking at {@link net.project.base.Module}.
     * @param action a <code>int</code> value that indicates the action that
     * we want to compare to the security provider.  Valid action constants can
     * be found by looking at the statics in net.project.security.Action.
     * @throws AuthorizationFailedException when the parameters provided to this
     * method are not the same as the last parameters provided to the security
     * system.
     */
    public static void verifyAccess(int module, int action) throws AuthorizationFailedException  {
        verifyAccess(module, action, null);
    }
    
    /**
     * Check that specified parameters are the same as the parameters that were
     * used the last time that SecurityProvider was called to verify access to 
     * a JSP page.    
     * 
     * @param module a <code>String</code> value that indicates the module that
     * we want to compare to the security provider.  Valid module constants can 
     * be found by looking at {@link net.project.base.Module}.
     * @param action a <code>String</code> value that indicates the action that
     * we want to compare to the security provider.  Valid action constants can
     * be found by looking at the statics in net.project.security.Action.
     * @param id a <code>String</code> value that indicates the object id that 
     * was passed to the security provider the last time it was called.  A 
     * <code>null</code> value indicates that a parameter was not passed.
     * @throws AuthorizationFailedException when the parameters provided to this
     * method are not the same as the last parameters provided to the security
     * system.
     */
    public static void verifyAccess(String module, String action, String id) throws AuthorizationFailedException {
        verifyAccess(Integer.parseInt(module), Integer.parseInt(action), id);
    }
    
    /**
     * Check that specified parameters are the same as the parameters that were
     * used the last time that SecurityProvider was called to verify access to 
     * a JSP page.    
     * 
     * @param module a <code>int</code> value that indicates the module that
     * we want to compare to the security provider.  Valid module constants can 
     * be found by looking at {@link net.project.base.Module}.
     * @param action a <code>int</code> value that indicates the action that
     * we want to compare to the security provider.  Valid action constants can
     * be found by looking at the statics in net.project.security.Action.
     * @param id a <code>String</code> value that indicates the object id that 
     * was passed to the security provider the last time it was called.  A 
     * <code>null</code> value indicates that a parameter was not passed.
     * @throws AuthorizationFailedException when the parameters provided to this
     * method are not the same as the last parameters provided to the security
     * system.
     */
    public static void verifyAccess(int module, int action, String id) throws AuthorizationFailedException {
    	verifyAccess(module, action, id, null);
    }    
    
    /**
     * Check that specified parameters are the same as the parameters that were
     * used the last time that SecurityProvider was called to verify access to 
     * a JSP page.    
     * 
     * @param module a <code>int</code> value that indicates the module that
     * we want to compare to the security provider.  Valid module constants can 
     * be found by looking at {@link net.project.base.Module}.
     * @param action a <code>int</code> value that indicates the action that
     * we want to compare to the security provider.  Valid action constants can
     * be found by looking at the statics in net.project.security.Action.
     * @param id a <code>String</code> value that indicates the object id that 
     * was passed to the security provider the last time it was called.  A 
     * <code>null</code> value indicates that a parameter was not passed.     
     * @param securityProvider a SecurityProvider to use for the verification. If a 
     * <code>null</code> value is passed, the SessionManager will be used to 
     * get the SecurityProvider from the user's session.
     * @throws AuthorizationFailedException when the parameters provided to this
     * method are not the same as the last parameters provided to the security
     * system.
     */
    public static void verifyAccess(int module, int action, String id, SecurityProvider securityProvider) throws AuthorizationFailedException {
        //First, get the security parameters that were originally used when the 
        //page was called.
    	if (securityProvider == null) {
    		securityProvider = SessionManager.getSecurityProvider();
    	}
        int checkedModuleID = securityProvider.getCheckedModuleID();
        int checkedActionID = securityProvider.getCheckedActionID();
        String checkedObjectID = securityProvider.getCheckedObjectID();
        
        //This keeps track of whether the user has access to the page.        
        boolean isAllowed = true;
        
        //Check to make sure that all of the parameters that were passed during 
        //the last request are the same as the parameters being indicated on the
        //page.  We do this to make sure that the user doesn't pass credentials
        //that they know will work so they can bypass security.   
        if (id != null) {
			if ((checkedModuleID != module) || (module == DEFAULT_ERROR_VALUE) || (checkedActionID != action) || (action == DEFAULT_ERROR_VALUE) || (!checkedObjectID.equals(id))) {
				isAllowed = false;
			}
		} else {
			if ((checkedModuleID != module) || (module == DEFAULT_ERROR_VALUE) || (checkedActionID != action) || (action == DEFAULT_ERROR_VALUE)) {
				isAllowed = false;
			}
		}
        // If any of the above checks fail, throw an exception. We throw an
        //exception instead of returning a boolean because this way we can 
        //automatically trigger the JSP error handling system.

        if (!isAllowed) {
            logger.debug("\nChecked Module ID: " + checkedModuleID);
            logger.debug("Module ID: " + module);
            logger.debug("Checked Action ID: " + checkedActionID);
            logger.debug("Action ID: " + action);
            logger.debug("Checked Object ID: " + checkedObjectID);
            logger.debug("Object ID: " + id+"\n");
            throw new AuthorizationFailedException(PropertyProvider.get("prm.security.main.authorizationfailed.message"));
        }
    }    
}
