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
|   $Revision: 19405 $
|       $Date: 2009-06-22 13:26:55 -0300 (lun, 22 jun 2009) $
|     $Author: dpatil $
|
|
+----------------------------------------------------------------------*/
package net.project.space;

import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;

/**
 * Responsible for constructing URL's that are important for a space.
 * For example, this class can construct the base URL in which all JSP's
 * are housed for a space.
 *
 * @author Matthew Flower (11/15/2001)
 * @since Gecko
 */
public class SpaceURLFactory {
    /**
     * This method finds the root directory for a space given only its space type.
     *
     * TODO:
     * Eventually, this method will break unless we do a bit more work.  The reason
     * is that I am using space.getType() to construct the directory of the space.
     * There are parts of our application for which this isn't true even now.  When
     * we aren't in the middle of a release and the database can be modified more
     * freely, we'll have to clean this up a bit and have the locations come out of
     * somewhere such as the database.
     *
     * @param space an instantiated space that we will use to determine what url
     * we are going to construct.
     * @return a base URL for the space, containing the JSPRootURL, a slash, the
     * directory of the space, and another slash.
     */
    public static String constructSpaceURL(Space space) {
        StringBuffer url = new StringBuffer();

        url.append(SessionManager.getJSPRootURL());
        url.append("/").append(space.getType()).append("/");

        return url.toString();
    }

    /**
     * Find the root directory for a space given only the id of that space.  
     * This method is necessary because it is possible to see other space's objects
     * from the personal space.  We need to be able to get to the other space's
     * main page so we can change space to view the task or document.
     *
     * @param id a <code>String</code> value
     * @return a <code>String</code> value
     */
    public static String constructSpaceURL(String id) throws PersistenceException {
        return constructSpaceURL(SpaceFactory.constructSpaceFromID(id));
    }
    
    /**
     * Constructs the URL which points on to the Main page for individual space 
     * @param space a <code>Space</code> value
     * @return a <code>String</code> value
     */
    public static String constructSpaceURLForMainPage(Space space,int module,int action) {
        StringBuffer url = new StringBuffer();

        url.append(SessionManager.getJSPRootURL());
        if(space.getType().equals(SpaceTypes.PERSONAL_SPACE)) {
            url.append("/").append("personal").append("/Main.jsp");
            url.append("?module=").append(module).append("&action=").append(action);
        } else if(space.getType().equals(SpaceTypes.APPLICATION_SPACE)) {
            url.append("/").append("admin").append("/Main.jsp");
            url.append("?module=").append(module).append("&action=").append(action);
        } else if(space.getType().equals(SpaceTypes.PROJECT_SPACE)) {
        	url.append("/").append(space.getType()).append("/Dashboard?id="+space.getID());
            url.append("&module=").append(module).append("&action=").append(action);
        } else {
            url.append("/").append(space.getType()).append("/Main.jsp?id="+space.getID());
            url.append("&module=").append(module).append("&action=").append(action);
        }
        return url.toString();
    }


    /**
     * Constructs the URL which points on to the Main page for individual space 
     * @param space a <code>Space</code> value
     * @return a <code>String</code> value
     */
    public static String constructSpaceURLForMainPage(Space space) {
        StringBuffer url = new StringBuffer();

        url.append(SessionManager.getJSPRootURL());
        if(space.getType().equals(SpaceTypes.PERSONAL_SPACE)) {
            url.append("/").append("personal").append("/Main.jsp");
        } else if(space.getType().equals(SpaceTypes.APPLICATION_SPACE)) {
            url.append("/").append("admin").append("/Main.jsp?id="+space.getID()+"&module="+net.project.base.Module.APPLICATION_SPACE);
        } else {
            url.append("/").append(space.getType()).append("/Main.jsp?id="+space.getID());
        }
        return url.toString();
    }

    /**
     * Constructs the URL which points on to the Main page for individual space ID 
     * @param spaceID   <code>String</code> value
     * @return a <code>String</code> value
     */
    public static String constructSpaceURLForMainPage(String  spaceID) throws PersistenceException {
        Space space = SpaceFactory.constructSpaceFromID(spaceID);
        return constructSpaceURLForMainPage(space);    
    }
    
    public static String constructForwardFromSpaceURL( String page){
    	if(page.indexOf("?") == -1)
    		page += "?redirectedFromSpace=true";
    	else
    		page += "&redirectedFromSpace=true";
    	return page;
    }
}
