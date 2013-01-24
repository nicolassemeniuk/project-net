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

 package net.project.startup;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class SessionAccessEvents implements HttpSessionListener {
    private static int activeSessions = 0;
    private Logger logger = Logger.getLogger(SessionAccessEvents.class);

    private synchronized void changeSessionCount(int change) {
        activeSessions += change;
    }

    public void sessionCreated(HttpSessionEvent event) {
        changeSessionCount(1);
        logger.debug("New Session.  Total Sessions: " + activeSessions);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        changeSessionCount(-1);
        logger.debug("Deleted Session.  Total Sessions: " + activeSessions);
    }
}
