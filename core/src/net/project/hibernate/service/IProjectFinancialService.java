package net.project.hibernate.service;

public interface IProjectFinancialService {

	/**
	 * Obtain the calculated actual cost to date from a certain project. This
	 * means to sum all the completed task hour cost plus the materials on those
	 * tasks. A task is considered completed based on a certain percent of
	 * completion. This is a property that is set on the database
	 * (prm.global.taskcompletedpercentage).
	 * 
	 * @param spaceID
	 *            the space from which we want to obtain the actual cost to
	 *            date.
	 * @return a Money value representing the cost.
	 */

	public Float calculateActualCostToDate(String spaceID);

	/**
	 * Obtain the calculated estimated cost from a certain project. This means
	 * to sum all of the tasks and their hours in the project and all of the
	 * materials cost too.
	 * 
	 * @param spaceID
	 *            the space from which we want to obtain the actual cost to
	 *            date.
	 * @return a Float value representing the cost.
	 */
	public Float calculateEstimatedTotalCost(String spaceID);

	/**
	 * Obtain the budgeted total cost for the project.
	 * 
	 * @param spaceID
	 *            the space from which we want to obtain the budgeted total
	 *            cost.
	 * @return a float value representing the cost.
	 */
	public Float getBudgetedCost(String spaceID);

	/**
	 * Obtain the discretional cost for the project.
	 * 
	 * @param spaceID
	 *            the space from which we want to obtain the discretional cost.
	 * @return a float value representing the cost.
	 */
	public Float getDiscretionalCost(String spaceID);

}
