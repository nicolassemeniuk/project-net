package net.project.test;

import net.project.hibernate.service.ServiceFactory;
import net.project.hibernate.service.impl.ServiceFactoryImpl;

public class MainTest {

	public static void main(String[] args) {
		try {
			ServiceFactory.init(ServiceFactoryImpl.class);
			// initialize Hibernate mappings
			LoadHibernate loadHibernate = new LoadHibernate();
			loadHibernate.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
