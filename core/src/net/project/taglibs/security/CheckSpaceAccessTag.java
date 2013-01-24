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

 package net.project.taglibs.security;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.base.Module;
import net.project.persistence.PersistenceException;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceFactory;

import org.apache.log4j.Logger;

public class CheckSpaceAccessTag extends TagSupport {
    private SecurityProvider securityProvider = new SecurityProvider();
    private String spaceID;
    private String userID;
    private int action = Action.VIEW;

    /**
     * Set the Action value.
     * @param action The new Action value.
     */
    public void setAction(String action) {
        this.action = Action.actionStringToInt(action);
    }

    /**
     * Set the ID of the space into which the user is trying to gain.
     *
     * @param spaceID The space id that we are going to check for access.
     */
    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    /**
     * Indicate the user ID that 
     *
     * @param userID Value to assign to this.userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int doStartTag() throws JspTagException, AuthorizationFailedException {
        verifySpaceAccess(spaceID, action, securityProvider);
        return SKIP_BODY;
    }

    public void release() {
        this.spaceID = null;
        this.userID = null;
        this.action = Action.VIEW;

        super.release();
    }

    /**
     * Verify that the user in question has access to a space.
     *
     * @exception AuthorizationFailedException if the user does not have access
     * to the space.  Normally, throwing exceptions to signal an expected result
     * isn't good practice, but this is a notable exception due to the
     * architecture of our system.  This architecture automatically raises the
     * security dialog when an exception of this type is thrown.
     */
    public static void verifySpaceAccess(String spaceID, int action,
        SecurityProvider securityProvider) throws AuthorizationFailedException {

        Space testSpace;
        
        try {
            //Try to create an instance of a space given it's ID.
            testSpace = SpaceFactory.constructSpaceFromID(spaceID);
        } catch (PersistenceException pe) {
        	Logger.getLogger(CheckSpaceAccessTag.class).debug("Unexpected PersistenceException while trying to look up "+
                "access for space id: " + pe);
            //This is an unexpected exception -- perhaps the space ID couldn't be found
            throw new AuthorizationFailedException("Unable to look up access for space id.");
        }

        securityProvider.setUser(SessionManager.getUser());
        securityProvider.setSpace(testSpace);
        String module = String.valueOf(Module.getModuleForSpaceType(testSpace.getType()));

        //If the user is checking the security for a specific action, make sure it is a
        //valid action
        if (action < 0) {
            throw new AuthorizationFailedException("Invalid action specified when calling VerifySpaceAccess tag");
        }
        
        //Check to see if the user has access to this space.  The method that we are using
        //to do this is a bit ugly, but it is the standard way used throughout the application
        //when you are entering a space.
        if (!securityProvider.isActionAllowed(null, module, action)) {
            throw new AuthorizationFailedException("Access to space denied");
        } else {
            //The user was granted access to the space, no further action is required
        }                                              
    }
}
