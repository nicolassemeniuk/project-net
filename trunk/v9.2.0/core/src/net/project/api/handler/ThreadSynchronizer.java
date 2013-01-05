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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.api.handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.mvc.IView;
import net.project.util.Validator;

import org.apache.log4j.Logger;

/**
 * This handler makes all callers wait until a certain predetermined number of
 * callers have called this method.
 *
 * @author Matthew Flower
 * @since Version 7.6.4
 */
public class ThreadSynchronizer implements IGatewayHandler {
    private Logger logger = Logger.getLogger(ThreadSynchronizer.class);
    private static final int MAXIMUM_WAITERS = 200;
    private static int waiters = 0;
    private static Object synchro = new Object();

    private void acquireLock(HttpServletRequest request) throws InterruptedException {
        synchronized(synchro) {
            waiters++;

            if (waiters >= MAXIMUM_WAITERS || !Validator.isBlankOrNull(request.getParameter("go"))) {
                waiters = 0;
                logger.debug("Releasing lock");
                synchro.notifyAll();
            } else {
                logger.debug(waiters + " waiting for synchro lock");
                synchro.wait();
            }
        }
    }

    public IView getView() {
        return new IView() {
            public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                String content = "ok";
                response.setContentType("text/plain");
                response.setContentLength(content.length());

                // Now write the response
                Writer writer = new BufferedWriter(response.getWriter());
                writer.write(content);
                writer.flush();
            }
        };
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Map model = new HashMap();

        try {
            acquireLock(request);
        } catch (InterruptedException e) {
            throw new RuntimeException("Unexpected InterruptedExcpetion when trying to wait for synchronization lock");
        }

        return model;
    }
}
