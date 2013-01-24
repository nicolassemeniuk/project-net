package net.project.hibernate;

import java.math.BigDecimal;

import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.ServiceFactory;

import org.apache.log4j.Logger;

public class LoadHibernate {

	private Logger log = Logger.getLogger(LoadHibernate.class);
	
	public LoadHibernate() {
	}

	public void load() {
		try {
			// executes simple query during server startup and Hibernate mapping files are loadded 
			IPnPersonService personService = ServiceFactory.getInstance().getPnPersonService();
			PnPerson person = personService.getPerson(new BigDecimal(1));

		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

}
