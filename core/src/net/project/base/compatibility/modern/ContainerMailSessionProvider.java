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

 package net.project.base.compatibility.modern;

import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.project.base.PnetRuntimeException;
import net.project.base.compatibility.IMailSessionProvider;

/**
 * Provides a mail session from a JNDI resource configured in the
 * web container.
 * <p>
 * The data source JNDI name is <code>java:comp/env/mail/PnetSession</code>.
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
public class ContainerMailSessionProvider implements IMailSessionProvider {

    private static final String CONTEXT = "java:comp/env";

    /** The JNDI name of the mail session. */
    private static final String SESSION_NAME = "mail/PnetSession";

    /**
     * Returns a mail session configured in the JNDI tree.
     * @return the mail session
     * @throws PnetRuntimeException if there is a problem looking up the JNDI entry
     */
    public Session getSession() {

        Context context = null;
        Session session;

        try {
            context = (Context) new InitialContext().lookup(CONTEXT);
            session = (Session) context.lookup(SESSION_NAME);

        } catch (NamingException e) {
            throw new PnetRuntimeException("Error looking up mail session for name: " + SESSION_NAME, e);

        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException e) {
                    throw new PnetRuntimeException("Error looking up mail session for name: " + SESSION_NAME, e);
                }
            }
        }

        return session;
    }

}
