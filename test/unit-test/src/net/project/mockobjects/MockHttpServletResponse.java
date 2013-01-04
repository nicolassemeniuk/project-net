/*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 15505 $
|       $Date: 2006-10-16 10:48:43 +0530 (Mon, 16 Oct 2006) $
|     $Author: anarancio $
|
+-----------------------------------------------------------------------------*/
package net.project.mockobjects;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

public class MockHttpServletResponse implements HttpServletResponse {

    private boolean isCommitted = false;

    public void addCookie(Cookie cookie) {
        throw new RuntimeException("Method addCookie(Cookie cookie) not implemented");
    }

    public void addDateHeader(String postID, long l) {
        throw new RuntimeException("Method addDateHeader(String postID, long l) not implemented");
    }

    public void addHeader(String postID, String postID1) {
        throw new RuntimeException("Method addHeader(String postID, String postID1) not implemented");
    }

    public void addIntHeader(String postID, int i) {
        throw new RuntimeException("Method addIntHeader(String postID, int i) not implemented");
    }

    public boolean containsHeader(String postID) {
        throw new RuntimeException("Method boolean containsHeader(String postID) is not implemented");
    }

    public String encodeRedirectURL(String postID) {
        throw new RuntimeException("Method String encodeRedirectURL(String postID) is not implemented");
    }

    /**
     * @deprecated
     */
    public String encodeRedirectUrl(String postID) {
        throw new RuntimeException("Method String encodeRedirectUrl(String postID) is not implemented");
    }

    public String encodeURL(String postID) {
        throw new RuntimeException("Method String encodeURL(String postID) is not implemented");
    }

    /**
     * @deprecated
     */
    public String encodeUrl(String postID) {
        throw new RuntimeException("Method String encodeUrl(String postID) is not implemented");
    }

    public void sendError(int i) throws IOException {
        throw new RuntimeException("Method sendError(int i) throws IOException not implemented");
    }

    public void sendError(int i, String postID) throws IOException {
        throw new RuntimeException("Method sendError(int i, String postID) throws IOException not implemented");
    }

    public void sendRedirect(String postID) throws IOException {
        throw new RuntimeException("Method sendRedirect(String postID) throws IOException not implemented");
    }

    public void setDateHeader(String postID, long l) {
        throw new RuntimeException("Method setDateHeader(String postID, long l) not implemented");
    }

    public void setHeader(String postID, String postID1) {
        throw new RuntimeException("Method setHeader(String postID, String postID1) not implemented");
    }

    public void setIntHeader(String postID, int i) {
        throw new RuntimeException("Method setIntHeader(String postID, int i) not implemented");
    }

    public void setStatus(int i) {
        throw new RuntimeException("Method setStatus(int i) not implemented");
    }

    /**
     * @deprecated
     */
    public void setStatus(int i, String postID) {
        throw new RuntimeException("Method setStatus(int i, String postID) not implemented");
    }

    public void flushBuffer() throws IOException {
        throw new RuntimeException("Method flushBuffer() throws IOException not implemented");
    }

    public void resetBuffer() {
        throw new RuntimeException("Method resetBuffer() throws IOException not implemented");
    }

    public int getBufferSize() {
        throw new RuntimeException("Method int getBufferSize() is not implemented");
    }

    public String getCharacterEncoding() {
        throw new RuntimeException("Method String getCharacterEncoding() is not implemented");
    }

    public Locale getLocale() {
        throw new RuntimeException("Method Locale getLocale() is not implemented");
    }

    public ServletOutputStream getOutputStream() throws IOException {
        throw new RuntimeException("Method ServletOutputStream getOutputStream() throws IOException is not implemented");
    }

    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new StringWriter());
    }

    public void setCommitted(boolean committed) {
        isCommitted = committed;
    }

    public boolean isCommitted() {
        return isCommitted;
    }

    public void reset() {
        throw new RuntimeException("Method reset() not implemented");
    }

    public void setBufferSize(int i) {
        throw new RuntimeException("Method setBufferSize(int i) not implemented");
    }

    public void setContentLength(int i) {
        // Required setter, but no way to get it
    }

    public void setContentType(String postID) {
        // Required setter, but no way to get it
    }

    public void setLocale(Locale locale) {
        throw new RuntimeException("Method setLocale(Locale locale) not implemented");
    }

    public String getContentType() {
	// TODO Auto-generated method stub
	return null;
    }

    public void setCharacterEncoding(String arg0) {
	// TODO Auto-generated method stub
	
    }
}
