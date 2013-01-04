/*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 11467 $
|       $Date: 2003-07-19 03:30:30 +0530 (Sat, 19 Jul 2003) $
|     $Author: matt $
|
+-----------------------------------------------------------------------------*/
package net.project.mockobjects;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Mockobject for the {@link javax.servlet.RequestDispatcher} interface.
 *
 * @author Matthew Flower
 * @since Version 7.6.1
 */
public class MockRequestDispatcher implements RequestDispatcher {
    /**
     * This is the path that was requested by the code, probably from the
     * request.getRequestDispatcher() method.
     */
    private String requestedPath;
    private boolean forwardRequested = false;
    private boolean includeRequested = false;

    /**
     * Constructs a <code>MockRequestDispatcher</code> object, indicating the
     * path that the user has requested.
     *
     * @param requestedPath a <code>String</code> indicating the path that the
     * user has requested.
     */
    MockRequestDispatcher(String requestedPath) {
        this.requestedPath = requestedPath;
    }

    /**
     * Implementation of the forward method of the RequestDispatcher interface.
     * This method won't really try to do any forwarding, though it does keep
     * track that the user requested to do so.
     *
     * @param servletRequest unused
     * @param servletResponse unused
     * @throws ServletException never
     * @throws IOException never
     */
    public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        forwardRequested = true;
    }

    /**
     * Implementation of the include method of the RequestDispatcher interface.
     * This method won't really try to do any forwarding, though it does keep
     * track that the user requested to do so.
     *
     * @param servletRequest unusued
     * @param servletResponse unused
     * @throws ServletException never
     * @throws IOException never
     */
    public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        includeRequested = true;
    }

    /**
     * Get the path that the code requested when creating the dispatcher.  This
     * is post-request analysis.
     *
     * @return a <code>String</code> containing the path that was requested.
     */
    public String getRequestedPath() {
        return requestedPath;
    }

    /**
     * Indicates that the "forward" method was called at some point.
     *
     * @return a <code>boolean</code> indicating whether the forward method was
     * called at some point.
     */
    public boolean isForwardRequested() {
        return forwardRequested;
    }

    /**
     * Indicates whether the "include" method was called at some point.
     *
     * @return a <code>boolean</code> indicating whether the include method was
     * called at some point.
     */
    public boolean isIncludeRequested() {
        return includeRequested;
    }
}
