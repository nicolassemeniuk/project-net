package net.project.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Interface that define database methods from the CRUD pattern.
 */
public interface IDAO<OBJECT, PK extends Serializable> {
	/**
	 * Create/insert new record into the database.
	 * @param object Bean (domain model object) that contains values to insert.
	 * @return Primary key of the newly created object.
	 */
	public PK create(OBJECT object);
	
	public void saveOrUpdate(OBJECT object);
	
	/**
	 * Return list of objects populated with records from the database.
	 * @return List of domain model objects.
	 */
	public List<OBJECT> findAll();
	
	/**
	 * Find an record identified by primary key and return populated bean.
	 * @param key Primary key of the record to return.
	 * @return Bean populated with data from the record.
	 */
	public OBJECT findByPimaryKey(PK key);
	
	/**
	 * Update an record into the database.
	 * @param object Domain model object with data to update.
	 */
	public void update(OBJECT object);
	
	/**
	 * Delete an record from the database.
	 * @param object Object that represents record to delete.
	 */
	public void delete(OBJECT object);
}
