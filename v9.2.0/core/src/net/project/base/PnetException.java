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

 package net.project.base;

import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Provides the base class for all Project.net Exceptions.
 * <p/>
 * Example: <pre><code>
 *     throw new PnetException("Some message");
 * <p/>
 *     ...
 * <p/>
 *    } catch (SQLException sqle) {
 *        throw new PnetException("Some message", sqle);
 *    }
 * </pre></code>
 * <p/>
 * All exceptions are logged; this class acts as a wrapper to the logging system - log messages appear to come from the
 * constructing class (typically a subclass).  All exceptions are logged with <code>DEBUG</code> priority and include a
 * stack trace.  If there was a causing exception, that is passed to the logger; otherwise this exception is passed.
 */
public class PnetException extends Exception {

    /** Logging category. */
    private static final Logger logger = Logger.getLogger(PnetException.class);

    private String name = null;
    private String severity = null;
    private int reasonCode = -1;
    private String displayError = null;
    private String userMessage = null;

    /** The message to override the one passed to the constructor. */
    private String overrideMessage = null;

    //
    // Constructors
    //


    /**
     * Creates a new PnetException with no message, logging the exception.
     */
    public PnetException() {
        super();
        init();
        log();
    }


    /**
     * Creates a new PnetException with the specified message, logging the exception.
     * @param message the detailed message
     */
    public PnetException(String message) {
        super(message);
        init();
        log();
    }

    /**
     * Creates a new PnetException with the specified message and specifying the cause of original exception. Logs the
     * exception.
     * @param message the detailed message
     * @param cause the cause exception
     */
    public PnetException(String message, Throwable cause) {
        super(message, cause);
        init();
        log();
    }

    /**
     * Creates a new PnetException specifying the cause of the original exception. Logs the exception.
     * @param cause the cause exception
     */
    public PnetException(Throwable cause) {
        super(cause);
        init();
        log();
    }


    //
    // Methods
    //

    /**
     * Initializes the exception.
     */
    private void init() {
    }

    /**
     * Logs the specified message overriding the exception name.
     * @param name the name to add to the message
     * @param message the message to log
     */
    protected void log(String name, String message) {
        logEvent(name + ": " + message);
    }

    /**
     * Logs the current message with low severity.
     */
    public void log() {
        logEvent(getLogMessage());
    }

    /**
     * Logs the specified message.
     * <p/>
     * Logs to <code>ApplicationLogger</code>. Also logs to log4j logger with <code>DEBUG</code> priority and includes
     * the stack trace of the causing exception (if there is one) or this exception. </p>
     * @param message the message to logEvent
     */
    private void logEvent(String message) {
        // Log to log4j logger; PnetException will act as a wrapper class;
        // the message will appear to come from the constructing class
        // (likely to be the sub-class of PnetException)
        // The FQN method will do this for us
        // Note: We'll also default the severity; in the past the severity was
        // never properly defined; it could be almost any value
        // We'll make the serverity DEBUG
        logger.log(PnetException.class.getName(), Priority.DEBUG, message, (getCause() != null ? getCause() : this));
    }

    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the message for this exception. This object maintains the message and returns it from "getMessage()".
     * @param message the message
     * @deprecated As of Gecko Update 4; No replacement Use the appropriate constructor instead.
     */
    public void setMessage(String message) {
        this.overrideMessage = message;
    }

    /**
     * Returns this exception's message.
     * @return the overridden message in this exception, if set by {@link #setMessage}; otherwise, returns the message
     *         passed in the constructor.
     */
    public String getMessage() {
        if (this.overrideMessage != null) {
            return this.overrideMessage;
        } else {
            return super.getMessage();
        }
    }


    /**
     * Set a user class message.  If this exception bubbles up, we may want to display a more general error message
     * <p/>
     * MDB 06/21/00
     */
    public void setUserMessage(String message) {
        this.userMessage = message;
    }

    public String getUserMessage() {
        return userMessage;
    }


    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setReasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
    }

    public void setDisplayError(String error) {
        this.displayError = error;
    }

    public String getName() {
        return this.name;
    }

    public String getSeverity() {
        return this.severity;
    }

    public int getReasonCode() {
        return this.reasonCode;
    }

    public String getDisplayError() {
        return this.displayError;
    }


    /**
     * Get the XML representation of this Exception
     */
    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append("<exception>\n");

        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>\n");
        xml.append("<message>" + XMLUtils.escape(getMessage()) + "</message>\n");
        xml.append("<severity>" + XMLUtils.escape(getSeverity()) + "</severity>\n");
        xml.append("<reason_code>" + XMLUtils.escape(String.valueOf(getReasonCode())) + "</reason_code>\n");
        xml.append("<stack_trace>" + XMLUtils.escape("STACK_TRACE_HERE") + "</stack_trace>\n");

        xml.append("</exception>\n\n");

        return xml.toString();

    }

    private String getLogMessage() {
        return getClass().getName() + ": " + getLocalizedMessage();
    }

    /**
     * Formats the deepest causing exception's message.
     * <p/>
     * For example, imagine this scenario: <pre><code>
     *    DeliveryException: "Error occurred sending notification"
     *      |--> Caused by EmailSendException: "Error occurred sending email"
     *             |--> Caused by "SendMailException: "Error 500 - Invalid email address"
     * </code></pre>
     * In this case DeliveryException.formatOriginalCauseMessage() would return
     * <code>Error 500 - Invalid email address</code>
     * @return the message, or empty string if this exception has no causing throwable
     */
    public String formatOriginalCauseMessage() {
        Throwable originalCause = null;
        Throwable t = this;
        while (t.getCause() != null) {
            originalCause = t.getCause();
            t = originalCause;
        }

        return (originalCause == null ? "" : originalCause.getMessage());
    }
}
