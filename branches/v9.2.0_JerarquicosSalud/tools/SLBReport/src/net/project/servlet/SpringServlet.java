package net.project.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


import net.project.hibernate.LoadHibernate;
import net.project.hibernate.service.ServiceFactory;
import net.project.hibernate.service.impl.ServiceFactoryImpl;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class SpringServlet extends HttpServlet {

	private Logger log = Logger.getLogger(SpringServlet.class);

	@Override
	public void init() throws ServletException {
		try {
			if (log.isDebugEnabled()) {
				log.debug(" Spring bussines context initialization started ");
			}
			ServiceFactory.init(ServiceFactoryImpl.class);
			if (log.isDebugEnabled()) {
				log.debug(" Spring bussines context initialization finished ");
			}
			// initialize Hibernate mappings
			//LoadHibernate loadHibernate = new LoadHibernate();
			//loadHibernate.load();
			// initialize security instances
			//Class.forName("net.project.security.SecurityInstances");
			//SecurityInstances.testPolicy();			
			
		} catch (Exception e) {
			log.error(" Spring bussines context initialization failed " + e.getMessage());
		}
	}

}
