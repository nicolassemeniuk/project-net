package net.project.financial.report;

public class EstimatedCostTypesOverTotalReportSummaryData {

	private int totalProjects = 0;
	private float resourcesTotalCurrentEstimatedTotalCost = 0;
	private float materialsTotalCurrentEstimatedTotalCost = 0;
	private float discretionalTotalCurrentEstimatedTotalCost = 0;

	public int getTotalProjects() {
		return totalProjects;
	}

	public void setTotalProjects(int totalProjects) {
		this.totalProjects = totalProjects;
	}

	public float getResourcesTotalCurrentEstimatedTotalCost() {
		return resourcesTotalCurrentEstimatedTotalCost;
	}

	public void setResourcesTotalCurrentEstimatedTotalCost(float resourcesTotalCurrentEstimatedTotalCost) {
		this.resourcesTotalCurrentEstimatedTotalCost = resourcesTotalCurrentEstimatedTotalCost;
	}

	public float getMaterialsTotalCurrentEstimatedTotalCost() {
		return materialsTotalCurrentEstimatedTotalCost;
	}

	public void setMaterialsTotalCurrentEstimatedTotalCost(float materialsTotalCurrentEstimatedTotalCost) {
		this.materialsTotalCurrentEstimatedTotalCost = materialsTotalCurrentEstimatedTotalCost;
	}

	public float getDiscretionalTotalCurrentEstimatedTotalCost() {
		return discretionalTotalCurrentEstimatedTotalCost;
	}

	public void setDiscretionalTotalCurrentEstimatedTotalCost(float discretionalTotalCurrentEstimatedTotalCost) {
		this.discretionalTotalCurrentEstimatedTotalCost = discretionalTotalCurrentEstimatedTotalCost;
	}

}
