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
package net.project.hibernate.util;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DBTransactionManager {

	private final Logger log = Logger.getLogger(getClass());

	private static Configuration configuration;

	private static SessionFactory sessionFactory;

	private static final ThreadLocal threadSession = new ThreadLocal();

	private static final ThreadLocal threadTransaction = new ThreadLocal();

	// Create the initial SessionFactory from the default configuration files
	static {
		try {
			configuration = new Configuration();
			sessionFactory = configuration.configure("/hibernate.cfg.xml").buildSessionFactory();
		} catch (Throwable ex) {
			 //log.error("Building SessionFactory failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Returns the SessionFactory used for this static class.
	 * 
	 * @return SessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Retrieves the current Session local to the thread. <p/> If no Session is
	 * open, opens a new Session for the running thread.
	 * 
	 * @return Session
	 */
	public static Session getSession() {
		// With CMT, this should return getSessionFactory().getCurrentSession()
		// and do nothing else
		Session s = (Session) threadSession.get();

		//check for the session state also before we get the new session.
		//If it is not open then open a new session. 
		if (s == null || !s.isOpen()) {
			 //log.debug("******Opening new Session for this thread.");
			s = getSessionFactory().openSession();
			threadSession.set(s);
		} else {
			// log.debug("******Session already exist for this thread.");
		}
		return s;
	}

	/**
	 * Closes the Session local to the thread.
	 */
	public static void closeSession(Session s) {
		// Would be written as a no-op in an EJB container with CMT
		s = (Session) threadSession.get();
		threadSession.set(null);
		// log.debug("******Threadsession set to null");
		if (s != null && s.isOpen()) {
			// log.debug("******Closing Session of this thread.");
			s.close();
		}
	}

	/**
	 * Start a new database transaction.
	 */
	public static Transaction beginTransaction(Session s) {
		// Would be written as a no-op in an EJB container with CMT
		Transaction tx = null;
		try {
			tx = (Transaction) threadTransaction.get();
			if (tx == null) {
				// log.debug("Starting new database transaction in this
				// thread.");
				tx = s.beginTransaction();
				threadTransaction.set(tx);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tx;
	}

	/**
	 * Commit the database transaction.
	 */
	public static void commitTransaction(Transaction tx) {
		// Would be written as a no-op in an EJB container with CMT
		// Transaction tx = (Transaction) threadTransaction.get();
		try {
			if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
				// log.debug("Committing database transaction of this thread.");
				tx.commit();
			}
			threadTransaction.set(null);
		} catch (HibernateException ex) {
			rollbackTransaction();
			throw ex;
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Rollback the database transaction.
	 */
	public static void rollbackTransaction() {
		// Would be written as a no-op in an EJB container with CMT (maybe
		// setRollBackOnly...)
		Transaction tx = (Transaction) threadTransaction.get();
		try {
			threadTransaction.set(null);
			if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
				// log.debug("Tyring to rollback database transaction of this
				// thread.");
				tx.rollback();
			}
		} catch (Throwable t) {
			// log.error("Rollback Transaction failed:" + t.getMessage());
		}
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	public static void setConfiguration(Configuration configuration) {
		DBTransactionManager.configuration = configuration;
	}
}
