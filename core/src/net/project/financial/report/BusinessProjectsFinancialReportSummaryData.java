package net.project.financial.report;

public class BusinessProjectsFinancialReportSummaryData {

	private int totalProjects = 0;
	private float totalActualCostToDate = 0;
	private float totalCurrentEstimatedTotalCost = 0;
	private float totalBudgetedCost = 0;

	public int getTotalProjects() {
		return totalProjects;
	}

	public void setTotalProjects(int totalProjects) {
		this.totalProjects = totalProjects;
	}

	public float getTotalActualCostToDate() {
		return totalActualCostToDate;
	}

	public void setTotalActualCostToDate(float totalActualCostToDate) {
		this.totalActualCostToDate = totalActualCostToDate;
	}

	public float getTotalCurrentEstimatedTotalCost() {
		return totalCurrentEstimatedTotalCost;
	}

	public void setTotalCurrentEstimatedTotalCost(float totalCurrentEstimatedTotalCost) {
		this.totalCurrentEstimatedTotalCost = totalCurrentEstimatedTotalCost;
	}

	public float getTotalBudgetedCost() {
		return totalBudgetedCost;
	}

	public void setTotalBudgetedCost(float totalBudgetedCost) {
		this.totalBudgetedCost = totalBudgetedCost;
	}

}
