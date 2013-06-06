package net.project.hibernate.dao;

import net.project.hibernate.model.PnPersonSalary;

public interface IPnPersonSalaryDAO extends IDAO<PnPersonSalary, Integer> {
	
	public PnPersonSalary getPersonSalary(Integer personID);

}
