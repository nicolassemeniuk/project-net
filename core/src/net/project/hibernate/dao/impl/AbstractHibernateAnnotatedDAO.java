package net.project.hibernate.dao.impl;

import java.io.Serializable;
import java.util.List;

import net.project.hibernate.dao.IDAO;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Abstract DAO class that adds support for the Hibernate ORM through the
 * Spring framework.
 */
@Repository
@Transactional
public abstract class AbstractHibernateAnnotatedDAO<OBJECT, PK extends Serializable> 
	implements IDAO<OBJECT, PK> {
	
	/**
	 * Spring's hibernate template.
	 */
	@Autowired
	private HibernateTemplate hibernateTemplate;

	/**
	 * Supported persistent class.
	 */
	@SuppressWarnings("unchecked")
	private Class clazz;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("unchecked")
	public AbstractHibernateAnnotatedDAO(Class clazz) {
		this.clazz = clazz;
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public PK create(OBJECT object) {
		return (PK)hibernateTemplate.save(object);
	}

	@Transactional
	public void delete(OBJECT object) {
		hibernateTemplate.delete(object);
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<OBJECT> findAll() {
		return hibernateTemplate.loadAll(this.clazz);
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public OBJECT findByPimaryKey(PK key) {
		OBJECT o = (OBJECT)hibernateTemplate.load(this.clazz, key);
		if(o != null){
			hibernateTemplate.initialize(o);
		}
		return o;
	}
	
	@Transactional
	public void createOrUpdate(OBJECT object) {
		try {
			hibernateTemplate.saveOrUpdate(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public void update(OBJECT object) {
		hibernateTemplate.update(object);		
	}
	
	/**
	 * Initializes the lazy instance of the entity
	 * @param entity
	 * @return initialized entity
	 */
	@Transactional
	public void initializeEntity(Object entity){
		Hibernate.initialize(entity);
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
