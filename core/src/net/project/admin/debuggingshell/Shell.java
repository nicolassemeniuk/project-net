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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18948 $
|       $Date: 2009-02-21 09:39:24 -0200 (sáb, 21 feb 2009) $
|     $Author: ritesh $
|
+-----------------------------------------------------------------------------*/
package net.project.admin.debuggingshell;

import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

import javax.servlet.ServletResponse;
// Shell functionality no longer in use in Pnet admin
//import bsh.EvalError;
//import bsh.Interpreter;

public class Shell {
	/*
    public void executeCommands(String commands, ServletResponse response) throws IOException {
        Interpreter interpreter = new Interpreter();
        //interpreter.setClassLoader(this.getClass().getClassLoader());
        interpreter.setOut(new PrintStream(response.getOutputStream()));

        StringTokenizer tokenizer = new StringTokenizer(commands, "\n");
        while (tokenizer.hasMoreTokens()) {
            String command = tokenizer.nextToken();
            try {
                interpreter.eval(command);
            } catch (EvalError evalError) {
                evalError.printStackTrace(response.getWriter());
                return;
            }
        }
    }
    */
}
