package net.project.financial.report;

public class ActualCostTypesOverTotalReportSummaryData {

	private int totalProjects = 0;
	private float resourcesTotalActualCostToDate = 0;
	private float materialsTotalActualCostToDate = 0;
	private float discretionalTotalActualCostToDate = 0;

	public int getTotalProjects() {
		return totalProjects;
	}

	public void setTotalProjects(int totalProjects) {
		this.totalProjects = totalProjects;
	}

	public float getResourcesTotalActualCostToDate() {
		return resourcesTotalActualCostToDate;
	}

	public void setResourcesTotalActualCostToDate(float resourcesTotalActualCostToDate) {
		this.resourcesTotalActualCostToDate = resourcesTotalActualCostToDate;
	}

	public float getMaterialsTotalActualCostToDate() {
		return materialsTotalActualCostToDate;
	}

	public void setMaterialsTotalActualCostToDate(float materialsTotalActualCostToDate) {
		this.materialsTotalActualCostToDate = materialsTotalActualCostToDate;
	}

	public float getDiscretionalTotalActualCostToDate() {
		return discretionalTotalActualCostToDate;
	}

	public void setDiscretionalTotalActualCostToDate(float discretionalTotalActualCostToDate) {
		this.discretionalTotalActualCostToDate = discretionalTotalActualCostToDate;
	}

}
