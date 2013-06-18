package net.project.hibernate.dao;

import java.util.Date;

import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.model.PnPersonSalaryPK;

public interface IPnPersonSalaryDAO extends IDAO<PnPersonSalary, PnPersonSalaryPK> {
	
	/**
	 * Obtain a person current salary by the person id.
	 * @param personID the id of the person we want to obtain the current salary.
	 * @return a person salary.
	 */
	public PnPersonSalary getCurrentPersonSalaryByPersonId(Integer personID);
	
	public PnPersonSalary getPersonSalaryForDate(Integer personId, Date date);

}
