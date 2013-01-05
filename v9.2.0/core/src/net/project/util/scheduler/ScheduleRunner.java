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

/**
 * The ScheduleRunner is used for starting the ScheduleDaemon.
 * It accepts a command line parameter which is the full path to the
 * schedule configuration file.<br>
 * Usage: <pre><code>
 *    java ScheduleRunner schedule.properties
 * </code></pre>
 *
 * @author tim
 * @since 09/2001
 */
public class ScheduleRunner {

    /**
     * Prevent constructor from being used.
     */
    private ScheduleRunner() {
        // Not needed
    }

    /**
     * Executes the ScheduleDaemon.
     * Exepects the first argument to be the full path to the properties file.
     * @param args the parameters to this class from the command line
     */
    public static void main(String[] args) {
        ScheduleDaemon d;

        try {
            
            // Handle command line parameters
            String propertiesFileName = null;
            boolean isVerbose = false;

            for (int i = 0; i < args.length; i++) {
                String argument = args[i];

                if ("-verbose".equals(argument)) {
                    isVerbose = true;
                
                } else {
                    propertiesFileName = args[i];
                
                }
            }

            // Check Mandatory Parameters

            if (propertiesFileName == null) {
                // Properties file is mandatory
                printUsage();

            } else {
                // Instantiate the ScheduleDaemon and load the entries
                // given by the property file name
                d = new ScheduleDaemon();
                d.setVerbose(isVerbose);
                d.loadScheduleEntries(propertiesFileName);

                Thread t = new Thread(d);
                t.start();
                t.join();

            }
       
        } catch (ScheduleException se) {
            System.err.println(se.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        
        }

    }


    /**
     * Prints the usage of this ScheduleRunner to <code>System.out</code>.
     */
    private static void printUsage() {
        System.out.println();
        System.out.println("Usage: SchedulerRunner [-verbose] propertiesFileName");
        System.out.println("    -verbose            -  print additional information about executing tasks");
        System.out.println("    propertiesFileName  -  the name of the properties file containing schedule");
        System.out.println("                           information");
    }

}
