package net.project.test;

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
			// executes simple query durring server startup and Hibernate
			// mapping files are loadded
			IPnPersonService personService = ServiceFactory.getInstance().getPnPersonService();
			PnPerson person = personService.getPerson(new BigDecimal(1));
			System.out.println("\n\n\nperson:"+person.getFirstName()+"\n\n\n");	
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

	}

}
