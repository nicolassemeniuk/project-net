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

 package net.project.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JCC
{

    public static void main(String args[])
        throws FileNotFoundException, IOException
    {
        BufferedReader in = null;
        boolean debug = false;
        int number_lines = 0;
        int number_comments = 0;
        boolean inside_comment = false;
        if(args.length == 1)
            in = new BufferedReader(new FileReader(args[0]));
        else
            in = new BufferedReader(new InputStreamReader(System.in));
        String s;
        while((s = in.readLine()) != null) 
        {
            int pre_trim_length = s.length();
            int trim_length = (new String(s)).trim().length();
            if(debug)
            {
                System.err.println("Line read: " + s);
                System.err.println("\tpre_trim_length: " + pre_trim_length);
                System.err.println("\ttrim_length: " + trim_length);
            }
            if(s.indexOf("/*") != -1 && s.indexOf("*/") != -1)
            {
                if(debug)
                    System.err.println("\t\tcontained comment");
                if(s.trim().length() > 6 && debug)
                {
                    System.err.println("\t\tcontained comment of proper length");
                    number_comments++;
                }
            }
            else
            if(s.indexOf("//") != -1)
            {
                if(debug)
                    System.err.println("\t\tdouble-slash comment");
                if(s.trim().length() > 3)
                {
                    if(debug)
                        System.err.println("\t\tdouble-slash comment of proper length");
                    number_comments++;
                }
            }
            else
            if(s.indexOf("/*") != -1 && s.indexOf("*/") == -1)
            {
                inside_comment = true;
                if(debug)
                    System.err.println("\t\tstarting comment");
                if(s.trim().length() > 3)
                {
                    number_comments++;
                    if(debug)
                        System.err.println("\t\tstarting comment of proper length");
                }
            }
            else
            if(s.indexOf("/*") == -1 && s.indexOf("*/") != -1)
            {
                if(debug)
                    System.err.println("\t\tending a multi-line comment");
                inside_comment = false;
                if(s.trim().length() > 3)
                {
                    if(debug)
                        System.err.println("\t\tending a multi-line comment of proper length");
                    number_comments++;
                }
            }
            else
            if(inside_comment)
            {
                if(debug)
                    System.err.println("\t\tinside multi-line comment");
                if(s.trim().length() > 3)
                {
                    number_comments++;
                    if(debug)
                        System.err.println("\t\tinside multi-line comment of proper length");
                }
            }
            if(s.trim().length() > 3)
            {
                number_lines++;
                if(debug)
                    System.err.println("\t\tline worth counting");
            }
            else
            if(debug)
                System.err.println("\t\tline NOT worth counting");
        }

        System.out.println("Total number of lines of code: " + number_lines);
        System.out.println("Total number of lines of comments: " + number_comments);
        System.out.println("Ratio (comments/code): " + (int)((double)(((float)number_comments / (float)number_lines) * 100F) + 0.5D) + "%");
    }

    public JCC()
    {
    }
}
