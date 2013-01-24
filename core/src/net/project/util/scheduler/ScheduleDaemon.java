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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The ScheduleDaemon is a process that reads a schedule file
 * containing one or more schedule entries, and executes the 
 * schedules based on their specified period.
 *
 * @author tim
 * @since 09/2001
 */
public class ScheduleDaemon implements Runnable {

    //
    // Static Members
    //

    /**
     * Indicates the maximum amount of time within in which to have started all
     * tasks, currently <code>60000</code> milliseconds (1 minute).
     */
    private static final long START_ALL_TASKS_WITHIN_TIME = 60000L;

    /**
     * Loads the properties from the specified file into the
     * specified properties object.
     * @param p the properties object into which to load properties
     * @param file the file from which to read properties
     * @throws ScheduleException if there is a problem opening or reading
     * the properties file
     */
    private static void loadProperties(Properties p, File file) throws ScheduleException {
        String filePath = null;

        try {
            filePath = file.getCanonicalPath();

            // Read properties from file
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            p.load(is);

        } catch (FileNotFoundException fnfe) {
            throw new ScheduleException("Error opening properties file " + filePath + ": " + fnfe.getMessage());

        } catch (IOException ioe) {
            throw new ScheduleException("Error reading schedule entries from properties file " + filePath + ": " + ioe.getMessage());

        }
    }


    /**
     * Makes a schedule entry object from a schedule property value.
     * The scheduleEntry is of the form:<code><pre>
     * completeURL,timePeriod
     * </code></pre>
     * @param scheduleEntry the schedule entry to process
     * @return the processed schedule entry
     * @throws InvalidScheduleEntryException if a schedule is not of the
     * correct form
     */
    private static ScheduleEntry makeScheduleEntry(String scheduleEntry) throws InvalidScheduleEntryException {
        String urlString = null;
        String periodString = null;

        // Get URL and period from schedule string
        StringTokenizer tok = new StringTokenizer(scheduleEntry,",");
        if (tok.hasMoreTokens()) {
            urlString = tok.nextToken();
        }
        if (tok.hasMoreTokens()) {
            periodString = tok.nextToken();
        }

        if (urlString == null || periodString == null) {
            throw new InvalidScheduleEntryException("Missing URL or period in schedule entry: " + scheduleEntry);
        }

        return new ScheduleEntry(urlString, periodString);
    }

    //
    // Instance Members
    //

    /** List of schedule entries from schedule file. */
    private final List scheduleEntries = new LinkedList();
    
    /** Timer object used for managing timer tasks. */
    private final Timer timer = new Timer();

    /** Determines whether to be verbose; that is, print messages about tasks. */
    private boolean isVerbose = false;

    /**
     * Creates a new ScheduleDaemon with no scheduled entries.
     */
    public ScheduleDaemon() {
        // Nothing to do
    }


    /**
     * Specifies whether to execute in verbose mode.
     * @param isVerbose true means additional information will be printed about
     * executing tasks; false means the daemon will execute silently, except
     * for errors
     * @see #isVerbose
     */
    public void setVerbose(boolean isVerbose) {
        this.isVerbose = isVerbose;
    }


    /**
     * Indicates whether executing in verbose mode.
     * @return true if in verbose mode; false otherwise
     * @see #setVerbose
     */
    private boolean isVerbose() {
        return this.isVerbose;
    }


    /**
     * Loads the schedule entries from the specified properties
     * file.
     * @param fileName the name of the properties file
     * containing the schedule entries
     * @exception ScheduleException if there is a problem loading or reading
     * the schedule entries
     */
    public void loadScheduleEntries(String fileName) throws ScheduleException {
        // Load the properties into the properties structure
        Properties p = new Properties();
        loadProperties(p, new File(fileName));

        // Build schedule entries
        // by iterating over all properties and translating
        // property value into schedule entry
        this.scheduleEntries.clear();

        // Sort the property names to get tasks in the defined order
        List propertyNames = new LinkedList(p.keySet());
        Collections.sort(propertyNames);

        for (Iterator iterator = propertyNames.iterator(); iterator.hasNext();) {
            String propertyName = (String) iterator.next();

            String schedule = (String) p.get(propertyName);
            try {
                scheduleEntries.add(makeScheduleEntry(schedule));

            } catch (InvalidScheduleEntryException isee) {
                throw new ScheduleException("Invalid schedule entry for property " + propertyName + ": " + isee.getMessage());

            }
        }

    }


    /**
     * Executes this ScheduleDaemon.
     * <br>
     * <b>Preconditions:</b><br>
     * <li><ul>Schedule Entries have been loaded</ul></li>
     * <b>Postconditions:</b><br>
     * <li><ul>Each entry has been scheduled for execution</ul></li>
     */
    public void run() {
        Date initialStartDate = new Date();

        // Always print starting information
        System.out.println(new java.util.Date() + " - ScheduleDaemon starts with " + this.scheduleEntries.size() + " tasks");
        
        // Figure out the minimum delay per task to have all tasks started
        // within the initial window
        long startDelay = START_ALL_TASKS_WITHIN_TIME / this.scheduleEntries.size();

        // Iterate over each schedule entry, creating a URL Task from it
        // Each entry is started a number of milliseconds after the previous
        int entryCount = 0;
        for (Iterator iterator = scheduleEntries.iterator(); iterator.hasNext();) {
            ScheduleEntry nextEntry = (ScheduleEntry) iterator.next();
            startURLTask(nextEntry.getURL(), (initialStartDate.getTime() + (entryCount++ * startDelay)), nextEntry.getPeriod());
        }

    }

    /**
     * Starts a URL Task for the specified url.
     * URL tasks are scheduled at a fixed rate starting at specified time.
     * @param url the URL to access
     * @param startTime the time at which to start execution, in milliseconds
     * @param period the period between executions, in milliseconds
     */
    private void startURLTask(URL url, long startTime, long period) {
        TimerTask task = new URLTask(url, period, isVerbose());
        this.timer.scheduleAtFixedRate(task, new Date(startTime), period);
    }


    //
    // Nested top-level classes
    //


    /**
     * A ScheduleEntry represents the entry from the schedule properties file.
     */
    private static class ScheduleEntry {

        /** URL to navigate to a schedule entry. */
        private final URL url;

        /** The period of a schedule entry. */
        long period = -1;

        /**
         * Creates a new ScheduleEntry with the specified url
         * and period.
         * @param urlString the URL as a string
         * @param period of the schedule entry
         * @throws InvalidScheduleEntryException if the URL or period are
         * invalid
         */
        private ScheduleEntry(String urlString, String period) throws InvalidScheduleEntryException {
            try {
                this.url = new URL(urlString);
                this.period = Long.parseLong(period) * 1000L;

            } catch (IOException ioe) {
                throw new InvalidScheduleEntryException("Invalid URL: " + ioe.getMessage());

            } catch (NumberFormatException nfe) {
                throw new InvalidScheduleEntryException("Invalid period: " + nfe.getMessage());

            }
        }

        /**
         * Returns the URL for this schedule entry.
         * @return the url
         */
        private URL getURL() {
            return this.url;
        }

        /**
         * Returns the period for this schedule entry.
         * @return the period between executions, in milliseconds
         */
        private long getPeriod() {
            return this.period;
        }
    }


    /**
     * Indicates a schedule entry is not of the correct form.
     */
    private static class InvalidScheduleEntryException extends Exception {

        /**
         * Creates an exception with the specified message.
         * @param message the exception message
         */
        InvalidScheduleEntryException(String message) {
            super(message);
        }
    }

}
