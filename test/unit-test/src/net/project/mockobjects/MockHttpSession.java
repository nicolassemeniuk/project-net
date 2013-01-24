/*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 13838 $
|       $Date: 2005-02-01 23:21:40 +0530 (Tue, 01 Feb 2005) $
|     $Author: matt $
|
+-----------------------------------------------------------------------------*/
package net.project.mockobjects;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MockHttpSession implements HttpSession {
    private HashMap attributes = new HashMap();

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Enumeration getAttributeNames() {
        throw new RuntimeException("Method Enumeration getAttributeNames() is not implemented");
    }

    public long getCreationTime() {
        throw new RuntimeException("Method long getCreationTime() is not implemented");
    }

    public String getId() {
        throw new RuntimeException("Method String getId() is not implemented");
    }

    public long getLastAccessedTime() {
        throw new RuntimeException("Method long getLastAccessedTime() is not implemented");
    }

    public ServletContext getServletContext() {
        throw new RuntimeException("Method ServletContext getServletContext() is not implemented");
    }

    public int getMaxInactiveInterval() {
        throw new RuntimeException("Method int getMaxInactiveInterval() is not implemented");
    }

    /**
     * @deprecated
     */
    public HttpSessionContext getSessionContext() {
        throw new RuntimeException("Method HttpSessionContext getSessionContext() is not implemented");
    }

    /**
     * @deprecated
     */                          
    public Object getValue(String postID) {
        throw new RuntimeException("Method Object getValue(String postID) is not implemented");
    }

    /**
     * @deprecated
     */
    public String[] getValueNames() {
        throw new RuntimeException("Method String[] getValueNames() is not implemented");
    }

    public void invalidate() {
        throw new RuntimeException("Method invalidate() not implemented");
    }

    public boolean isNew() {
        throw new RuntimeException("Method boolean isNew() is not implemented");
    }

    /**
     * @deprecated
     */
    public void putValue(String postID, Object o) {
        throw new RuntimeException("Method putValue(String postID, Object o) not implemented");
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    /**
     * @deprecated
     */
    public void removeValue(String postID) {
        throw new RuntimeException("Method removeValue(String postID) not implemented");
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public void setMaxInactiveInterval(int i) {
        throw new RuntimeException("Method setMaxInactiveInterval(int i) not implemented");
    }

    public Map getAttributes() {
        return attributes;
    }
}
