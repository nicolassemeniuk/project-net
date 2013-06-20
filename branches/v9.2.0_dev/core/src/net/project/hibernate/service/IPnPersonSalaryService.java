package net.project.hibernate.service;

import java.util.Date;

import net.project.hibernate.model.PnPersonSalary;
import net.project.resource.PersonSalaryBean;
import net.project.resource.PnPersonSalaryList;

public interface IPnPersonSalaryService {

	/**
	 * Save a new persons salary on the database.
	 * 
	 * @param personSalary
	 *            a person salary. Must have a User for the creation of the
	 *            Object, an id of the person, cost by hour and a start date for
	 *            the salary.
	 * @return the Id of the new person salary created.
	 */
	public Integer savePersonSalary(PersonSalaryBean personSalary);

	/**
	 * Update a person salary on the database.
	 * 
	 * @param personSalary
	 *            a person salary. This class must have a cost by hour set and
	 *            the id of the person salary object.
	 */
	public void updatePersonSalary(PersonSalaryBean personSalary);

	/**
	 * Obtain the current salary for a person. This means that the salary
	 * returned is the one valid for the current date. The future salaries in
	 * the salary history are not taking in count.
	 * 
	 * @param personID
	 *            the id of the person.
	 * @return a person current salary.
	 */
	public PnPersonSalary getCurrentPersonSalaryByPersonId(Integer personId);

	/**
	 * Obtain a person salary for a certain date.
	 * 
	 * @param personId
	 *            the id of the person.
	 * @param date
	 *            the date from which we want to obtain the salary.
	 * @return the person salary for the period the date is in.
	 */
	public PnPersonSalary getPersonSalaryForDate(Integer personId, Date date);

	/**
	 * Returns the historic list of salaries for a person.
	 * 
	 * @param personId
	 *            the id of the person.
	 * @return a list of all the salaries.
	 */
	public PnPersonSalaryList getPersonSalaries(String personId);

}
