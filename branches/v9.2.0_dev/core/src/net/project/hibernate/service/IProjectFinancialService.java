package net.project.hibernate.service;

import net.project.base.money.Money;

public interface IProjectFinancialService {
	
	/**
	 * Obtain the calculated actual cost to date from a certain project. This means
	 * to sum all the completed task hour cost plus the materials on those tasks.
	 * @param spaceID the space from which we want to obtain the actual cost to date.
	 * @return a Money value representing the cost.
	 */
	
	public Money calculateActualCostToDate(String spaceID);
	
	/**
	 * Obtain the calculated estimated cost from a certain project. This means to sum all of the tasks and
	 * their hours in the project and all of the materials cost too. 
	 * @param spaceID the space from which we want to obtain the actual cost to date.
	 * @return a Float value representing the cost.
	 */
	public Float calculateEstimatedTotalCost(String spaceID);

}
