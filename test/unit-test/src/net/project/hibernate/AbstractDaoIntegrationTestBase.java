package net.project.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class AbstractDaoIntegrationTestBase extends AbstractTransactionalDataSourceSpringContextTests {
//public class AbstractDaoIntegrationTestBase extends AbstractDependencyInjectionSpringContextTests {

	protected SessionFactory sessionFactory;

//	protected SimpleJdbcTemplate jt;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	protected String[] getConfigLocations() {
		 return new String[]{ "classpath:test-bussinessContext.xml"};
	}

/*	@Override
	protected void onSetUpBeforeTransaction() throws Exception {
		jt = new SimpleJdbcTemplate(jdbcTemplate);
	}*/
	

	// Utility method
	protected void flush() {
		sessionFactory.getCurrentSession().flush();
	}
	
	
}
