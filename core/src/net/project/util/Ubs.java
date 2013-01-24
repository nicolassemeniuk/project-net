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

 /*
 *
 * Copyright 2001 Project.net, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Project.net, Inc.  
 * 
 */
package net.project.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Provides management facilities for UBS instances.
 * For usage information execute: <code><pre>
 *     java Ubs
 * </pre></code>
 * @author Tim Morrow 11/30/2001
 */
public class Ubs {

    /** Shutdown command. */
    private static String SHUTDOWN = "SHUTDOWN";

    /** Ping command. */
    private static String PING = "PING";

    /**
     * Executes Ubs command specified on command line.
     * Exits with the following status:<br>
     * <li>0 - Command completed successfully</li>
     * <li>1 - Command sent, unexpected response</li>
     * <li>2 - Unknown command sent</li>
     * <li>-1 - Error sending command</li>
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int exitStatus = -1;
        String command = null;
        
        // Default host
        String hostname = "localhost";

        // Default port
        int port = 20000;

        // Get input parameters
        for (int i = 0; i < args.length; i++) {
            
            switch (i) {
            case 0:
                // First parameter is the command
                command = args[i];
                break;
            
            case 1:
                // Second parameter is the hostname
                hostname = args[i];
                break;
            
            case 2:
                // Third parameter is the port
                port = new Integer(args[i]).intValue();
                break;
            }
        }

        // Check usage
        // Command is mandatory
        if (command == null) {
            printUsage();
        
        } else {
            // Got all required parameters
            // Now execute the command

            command = command.toUpperCase();

            InputStream in = null;
            OutputStream out = null;
            Socket socket = null;

            try {
                // Open socket to UBS port on hostname
                socket = new Socket(InetAddress.getByName(hostname), port);
                in = socket.getInputStream();
                out = socket.getOutputStream();

                // Read the expected response from the UBS
                // Throws an IOException if something is awry
                readHeaders(in);

                // Now execute the appropriate command
                
                if (command.equals(SHUTDOWN)) {
                    // Shutdown the UBS

                    if (shutdown(in, out)) {
                        // Shutdown command sent.  It may be sometime before
                        // the shutdown completes
                        System.out.println("Shutting down");
                        exitStatus = 0;
                    
                    } else {
                        // Shutdown command sent, but unexpected response received
                        System.out.println("Sent shutdown command, but received unexpected response");
                        exitStatus = 1;
                    }

                } else if (command.equals(PING)) {
                    // Ping the UBS

                    if (ping(in, out)) {
                        // Ping command sent, expected response received
                        System.out.println("Alive");
                        exitStatus = 0;
                    
                    } else {
                        // Ping command sent, but unexpected response received
                        System.out.println("Not responding");
                        exitStatus = 1;
                    
                    }
                
                } else {
                    // Some other command, just send it
                    sendCommand(command, out);
                    exitStatus = 2;
                
                }

            } catch (ConnectException ce) {
                System.out.println("Connection refused to " + hostname + ":" + port);
            
            } catch (IOException ioe) {
                System.out.println("Error talking to UBS " + hostname + ":" + port + " - " + ioe);
            
            } catch (Exception e) {
                e.printStackTrace();
            
            } finally {
                // Cleanup resources
                try {
                    close(in, out, socket);
                
                } catch (IOException ioe) {
                    System.out.println("Error closing connections to " + hostname + ":" + port);
                
                }
            
            }

        }

        System.exit(exitStatus);
    }


    /**
     * Consumes and validates the headers read from the UBS socket when first
     * connected.
     * @param in the stream from which to read the headers
     * @throws IOException if there is a problem reading the headers, or an
     * invalid response is received
     */
    private static void readHeaders(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String nextLine = null;

        // Sapphire_Protocol=
        nextLine = reader.readLine();
        if (nextLine.indexOf("Sapphire_Protocol") < 0) {
            throw new IOException("Unexpected response from host");
        }

        // Send_Java_Config=
        nextLine = reader.readLine();
        if (nextLine.indexOf("Send_java_Config") > 0) {
            // There is request to send config
            // This means there is one more blank line to read
            reader.readLine();
        
        } else {
            // Just read blank line
            // (Config was read by app server from runtime dir)

        }


    }


    /**
     * Closes the resources specified.  Ignores any exceptions until all resources
     * have been closed.
     * @param in the InputStream to close
     * @param out the OutputStream to close
     * @param socket the Socket to close
     * @throws IOException the first IOException that occurred
     */
    private static void close(InputStream in, OutputStream out, Socket socket) 
            throws IOException {

        IOException firstIOException = null;

        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ioe) {
            firstIOException = ioe;
        }

        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException ioe) {
            if (firstIOException != null) {
                firstIOException = ioe;
            }
        }
        
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ioe) {
            if (firstIOException != null) {
                firstIOException = ioe;
            }
        }

        // Throw the first exception we came across
        if (firstIOException != null) {
            throw firstIOException;
        }

    }

    /**
     * Executes shutdown command.
     * @param in the stream from which to read the response
     * @param out the stream to write the command to
     * @throws IOException if there is a problem sending the shutdown command
     */
    private static boolean shutdown(InputStream in, OutputStream out) throws IOException {
        String reply = null;
        boolean isShuttingDown = false;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        
        // Execute shutdown
        sendCommand("SHUTDOWN\n", out);
        
        // Check for appropriate response
        reply = reader.readLine();
        if (reply != null && reply.equals("Goodbye")) {
            isShuttingDown = true;
        } else {
            isShuttingDown = false;
        }

        return isShuttingDown;
    }

    /**
     * Executes ping command.
     * @param in the stream from which to read the response
     * @param out the stream to write the command to
     * @throws IOException if there is a problem sending the ping command
     */
    private static boolean ping(InputStream in, OutputStream out) throws IOException {
        String reply = null;
        boolean isAlive = false;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        
        // Execute ping
        sendCommand("PING\n\n", out);
        
        // Check for appropriate response
        reply = reader.readLine();
        if (reply != null && reply.equals("Hello")) {
            isAlive = true;
        } else {
            isAlive = false;
        }

        return isAlive;
    }


    /**
     * Sends the named command to the output stream specified.
     * @param command the command to send
     * @param out the stream to which to send the command
     */
    private static void sendCommand(String command, OutputStream out) {
        PrintWriter writer = new PrintWriter(out);
        writer.print(command);
        writer.flush();
    }


    /**
     * Prints the usage of this class to standard output.
     */
    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("    java Ubs command [hostname [port]]");
        System.out.println("");
        System.out.println("    command        - one of:");
        System.out.println("                     shutdown - Shuts down app server");
        System.out.println("                     ping     - Indicates whether app server is alive");
        System.out.println("");
        System.out.println("    hostname       - host on which application server is running");
        System.out.println("                     Default is localhost");
        System.out.println("");
        System.out.println("    port           - port on which application server is running");
        System.out.println("                     Default is 20000");
        System.out.println("");
    }

}
