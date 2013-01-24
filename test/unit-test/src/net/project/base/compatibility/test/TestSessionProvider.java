/*----------------------------------------------------------------------+
|                                                                       
|     $RCSfile$
|    $Revision: 13629 $
|        $Date: 2004-11-06 03:18:46 +0530 (Sat, 06 Nov 2004) $
|      $Author: matt $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.compatibility.test;

import javax.servlet.http.HttpSession;

import net.project.base.compatibility.ISessionProvider;


import java.util.HashMap;

/**
 * Provides access to the session using a <code>ThreadLocal</code>.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public class TestSessionProvider implements ISessionProvider {

    /**
     * ThreadLocal to maintain the current thread's session object.
     */
    private ThreadLocal localSession = new ThreadLocal();

    public TestSessionProvider() {
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
