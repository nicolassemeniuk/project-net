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
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.util.scheduler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.util.TimerTask;

/**
 * A URLTask is a TimerTask that invokes an URL.
 *
 * @author tim
 * @since 09/2001
 */
public class URLTask extends TimerTask {

    //
    // Static Members
    //


    /**
     * The task execution counter. Used for tracing the number of tasks
     * executed.
     */
    private static int taskExecutionCounter = 0;

    /**
     * Indicates whether response code means OK.
     * @param responseCode the response code
     * @return true if the response code is OK; false otherwise
     * @see java.net.HttpURLConnection for response code constants
     */
    private static boolean isResponseOk(int responseCode) {
        boolean isOk;

        if (responseCode == -1) {
            // Invalid response
            isOk = false;

        } else if (responseCode >= 200 & responseCode < 300) {
            // Valid response
            isOk = true;

        } else if (responseCode >= 300 & responseCode < 400) {
            // We don't handle redirects
            isOk = false;
            System.err.println(new java.util.Date() + " - ERROR URLTask; Redirection response code " + responseCode + " not handled");

        } else if (responseCode >= 400 & responseCode < 500) {
            // Client error
            isOk = false;

        } else if (responseCode >= 500 & responseCode < 600) {
            // Server error
            isOk = false;

        } else {
            // Unhandled response code
            isOk = false;
        }

        return isOk;
    }

    //
    // Instance Members
    //

    /**
     * The URL to invoke when this task is executed.
     */
    private final URL url;
    
    /**
     * The period between executions of this task.
     */
    private final long period;
    
    /**
     * The maximum length of time, in milliseconds, that may elapse between executions
     * before the task is skipped.
     */
    private final long maxTardiness;

    /**
     * Determines whether to be verbose; that is, print messages about this task.
     */
    private final boolean isVerbose;


    /**
     * Creates a new URLTask for the specified URL.
     * This specifies a maximum tardiness of half the period.
     * @param url the url to invoke when this task is executed
     * @param period the time, in milliseconds, between invocations
     * @param isVerbose true means this task will print information when it
     * executed; false means the task will execute silently, except for errors
     */
    public URLTask(URL url, long period, boolean isVerbose) {
        this.url = url;
        this.period = period;
        this.maxTardiness = this.period / 2;
        this.isVerbose = isVerbose;
    }


    /**
     * Indicates whether executing in verbose mode.
     * @return true if in verbose mode; false otherwise
     */
    private boolean isVerbose() {
        return this.isVerbose;
    }

    /**
     * Returns the display name of this task.
     * This is the servlet name of the task.
     * @return the name of this task
     */
    private String getDisplayName() {
        int lastSlash;
        String urlDisplay = this.url.toExternalForm();
        
        lastSlash = urlDisplay.lastIndexOf("/");
        if (lastSlash == -1) {
            lastSlash = urlDisplay.lastIndexOf("\\");
        }

        if (lastSlash == -1) {
            return urlDisplay;
        } else {
            return urlDisplay.substring(lastSlash + 1);
        }
    }


    /**
     * Returns the maximum tardiness for this task.
     * @return the maximum tardiness, in milliseconds
     */
    private long getMaxTardiness() {
        return this.maxTardiness;
    }

    /**
     * Execute this task.
     * This executes the URL for the task.
     */
    public void run() {
        HttpURLConnection conn = null;
        int responseCode;
        String responseMessage;

        // Check to see if task should be executed, based on its maximum tardiness
        if (doExecuteTask()) {

            try {
                long startTime;
                
                if (isVerbose()) {
                    System.out.println(new java.util.Date() + " - URLTask " + taskExecutionCounter++ + "; Executing task: " + getDisplayName());
                }

                // Make connection
                startTime = System.currentTimeMillis();
                conn = (HttpURLConnection) this.url.openConnection();

                // Handle response
                // Unfortunately, in the event of an app server error, Bluestone returns
                // a valid HTML page with an OK response
                // The response will be in error only if the web server does not respond
                responseCode = conn.getResponseCode();
                responseMessage = conn.getResponseMessage();
                if (!isResponseOk(responseCode)) {
                    System.err.println(new java.util.Date() + " - ERROR URLTask; Server response: " + responseCode + " - " + responseMessage + " after " + (System.currentTimeMillis() - startTime) + " ms");
                }

            } catch (NoRouteToHostException ne) {
                // Cannot locate host
                System.err.println(new java.util.Date() + " - ERROR URLTask; Unable to connect; check your network connection: " + ne.toString());
            
            } catch (ConnectException ce) {
                // Cannot connect to server (unknown server or web server is not running)
                System.err.println(new java.util.Date() + " - ERROR URLTask; Unable to connect; check the web server is started: " + ce.toString());

            } catch (FileNotFoundException fnfe) {
                // Cannot find file (possibly web server is paused)
                System.err.println(new java.util.Date() + " - ERROR URLTask; Cannot find the file specified; please check the URL: " + fnfe.toString());
            
            } catch (IOException ioe) {
                // Some other error (e.g. SocketException or IOException)
                System.err.println(new java.util.Date() + " - ERROR URLTask; An error occurred: " + ioe.toString());

            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

        } //end if

    }


    /**
     * Indicates whether to execute this task, based on
     * its maximum tardiness.
     * @return true if the task should be executed; that is, the current
     * time is no greater than the scheduled execution time plus the max tardiness
     */
    private boolean doExecuteTask() {

        if (System.currentTimeMillis() - scheduledExecutionTime() >= getMaxTardiness()) {
            // Too late; skip this execution.
            return false;
        }

        return true;
    }


}
