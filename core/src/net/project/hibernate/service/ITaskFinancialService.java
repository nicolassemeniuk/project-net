package net.project.hibernate.service;


public interface ITaskFinancialService {

	/**
	 * Calculate the Actual Cost to Date for a task. This value is obtained as
	 * the sum between the actual material cost to date and the actual resources
	 * cost to date for the task.
	 * 
	 * @param spaceId
	 *            the id of the project space the tasks belongs to.
	 * @param objectId
	 *            the id of the task.
	 * @return the actual task cost to date in float value.
	 */
	public Float calculateActualCostToDateForTask(String spaceId, String objectId);

	/**
	 * Calculate the Estimated Current Cost a task. This value is obtained as
	 * the sum between the estimated material cost and the estimated resources
	 * cost for the task.
	 * 
	 * @param spaceId
	 *            the id of the project space the tasks belongs to.
	 * @param objectId
	 *            the id of the task.
	 * @return the estimated task cost in float value.
	 */
	public Float calculateEstimatedTotalCostForTask(String spaceId, String objectId);

	/**
	 * Calculate the actual cost to date of human resources for a task. This
	 * means obtaining all the resources active assignments and work amounts
	 * performed for the task and multiply them for each resource cost by hour.
	 * 
	 * @param spaceId
	 *            the id of the project space the tasks belongs to.
	 * @param objectId
	 *            the id of the task from which to obtain the cost.
	 * @return the actual cost to date for the resources in float value.
	 */
	public float calculateResourcesActualCostToDateForTask(String spaceId, String objectId);

	/**
	 * Calculate the actual cost to date of materials for a task. This means
	 * obtaining all the materials of the task's project currently assigned.
	 * 
	 * @param spaceId
	 *            the id of the project space the tasks belongs to.
	 * @param objectId
	 *            the id of the task from which to obtain the cost.
	 * @return the actual cost to date for the materials in float value.
	 */
	public float calculateMaterialActualCostToDateForTask(String spaceId, String objectId);
	
	/**
	 * Calculate the estimated resources total cost to date for a task. This means
	 * obtaining all the resources assignments total work and multiply them by the cost by
	 * hour of the resource.
	 * 
	 * @param spaceId
	 *            the id of the project space the tasks belongs to.
	 * @param objectId
	 *            the id of the task from which to obtain the cost.
	 * @return the estimated total cost for the resources in float value.
	 */
	public float calculateResourceEstimatedTotalCostForTask(String spaceId, String objectId);

	/**
	 * Calculate the estimated material total cost to date for a task. This means
	 * obtaining all the material assigned costs and multiply them by the percentage of the material
	 * corresponding for this task.
	 * 
	 * @param spaceId
	 *            the id of the project space the tasks belongs to.
	 * @param objectId
	 *            the id of the task from which to obtain the cost.
	 * @return the estimated total cost for the materials in float value.
	 */	
	public float calculateMaterialEstimatedTotalCostForTask(String spaceId, String objectId);



}
