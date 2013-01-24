package net.project.hibernate.dao.impl;

import java.io.Serializable;
import java.util.List;

import net.project.hibernate.dao.IDAO;

import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Abstract DAO class that adds support for the Hibernate ORM through the
 * Spring framework.
 */
public abstract class AbstractHibernateDAO<OBJECT, PK extends Serializable> 
	implements IDAO<OBJECT, PK> {
	
	/**
	 * Spring's hibernate template.
	 */
	private HibernateTemplate hibernateTemplate;

	/**
	 * Supported persistent class.
	 */
	private Class clazz;
	
	/**
	 * Default constructor.
	 */
	public AbstractHibernateDAO(Class clazz) {
		this.clazz = clazz;
	}
	
	@SuppressWarnings("unchecked")
	public PK create(OBJECT object) {
		return (PK)hibernateTemplate.save(object);
	}

	public void delete(OBJECT object) {
		hibernateTemplate.delete(object);
	}

	@SuppressWarnings("unchecked")
	public List<OBJECT> findAll() {
		return hibernateTemplate.loadAll(this.clazz);
	}

	@SuppressWarnings("unchecked")
	public OBJECT findByPimaryKey(PK key) {
		return (OBJECT)hibernateTemplate.load(this.clazz, key);
	}

	public void update(OBJECT object) {
		hibernateTemplate.update(object);		
	}
	
	/**
	 * Sets the hibernateTemplate value.
	 * @param hibernateTemplate The hibernateTemplate to set.
	 */
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}	
	
	/**
	 * Gets the hibernate template
	 * @return hibernate template
	 */
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}
}
