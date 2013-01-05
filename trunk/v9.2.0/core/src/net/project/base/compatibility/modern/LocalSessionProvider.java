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
|     $RCSfile$
|    $Revision: 19579 $
|        $Date: 2009-07-27 06:45:23 -0300 (lun, 27 jul 2009) $
|      $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.base.compatibility.modern;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import net.project.base.compatibility.ISessionProvider;

/**
 * Provides access to the session using a <code>ThreadLocal</code>.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public class LocalSessionProvider implements ISessionProvider {

    /**
     * ThreadLocal to maintain the current thread's session object.
     */
    private ThreadLocal localSession = new ThreadLocal();

    public LocalSessionProvider() {
		super();
		localSession.set(new HashMap());
	}

	/**
     * Sets the local session for the current thread.
     * Uses a ThreadLocal, so session values are maintaines on a per-thread basis.
     * @param session the current thread's session
     */
    synchronized public void setLocalSession(Object session) {
        localSession.set(session);
    }
    
    public Object getAttribute(String name) {

        Object attribute = null;
        Object session = localSession.get();
        if (session == null)
        	return null;
        
		if ( session instanceof HttpSession ) 
			attribute = ((HttpSession)session).getAttribute(name);
		else {
			attribute = ((HashMap)session).get(name);
		}
        return attribute;
    }

    public void setAttribute(String name, Object value) {
    	Object session = localSession.get();

        if (session == null) {
        	setLocalSession(new HashMap());
        	session = localSession.get();
        }
        
    	if(session instanceof HttpSession)
    		((HttpSession)session).setAttribute(name, value);
    	else
    		((HashMap)session).put(name, value);
    }
}