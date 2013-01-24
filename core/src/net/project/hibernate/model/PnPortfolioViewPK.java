/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
 */
package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class PnPortfolioViewPK implements Serializable {

	private BigDecimal portfolioId;

	private String portfolioName;

	private BigDecimal projectId;

	private String projectName;

	private String projectDesc;

	private Integer percentComplete;

	private BigDecimal statusCodeId;

	private String status;

	private BigDecimal colorCodeId;

	private String color;

	private String colorImageUrl;

	private Integer isSubproject;

	private BigDecimal projectOwner;

	public PnPortfolioViewPK() {
	}

	public PnPortfolioViewPK(BigDecimal portfolioId, String portfolioName, BigDecimal projectId, String projectName,
			String projectDesc, Integer percentComplete, BigDecimal statusCodeId, String status,
			BigDecimal colorCodeId, String color, String colorImageUrl, Integer isSubproject, BigDecimal projectOwner) {
		this.portfolioId = portfolioId;
		this.portfolioName = portfolioName;
		this.projectId = projectId;
		this.projectName = projectName;
		this.projectDesc = projectDesc;
		this.percentComplete = percentComplete;
		this.statusCodeId = statusCodeId;
		this.status = status;
		this.colorCodeId = colorCodeId;
		this.color = color;
		this.colorImageUrl = colorImageUrl;
		this.isSubproject = isSubproject;
		this.projectOwner = projectOwner;
	}

	@Column(name = "PORTFOLIO_ID", nullable = false, length = 20)
	public BigDecimal getPortfolioId() {
		return this.portfolioId;
	}

	public void setPortfolioId(BigDecimal portfolioId) {
		this.portfolioId = portfolioId;
	}

	@Column(name = "PORTFOLIO_NAME", nullable = false, length = 80)
	public String getPortfolioName() {
		return this.portfolioName;
	}

	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}

	@Column(name = "PROJECT_ID", nullable = false, length = 20)
	public BigDecimal getProjectId() {
		return this.projectId;
	}

	public void setProjectId(BigDecimal projectId) {
		this.projectId = projectId;
	}

	@Column(name = "PROJECT_NAME", nullable = false, length = 80)
	public String getProjectName() {
		return this.projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Column(name = "PROJECT_DESC", nullable = false, length = 1000)
	public String getProjectDesc() {
		return this.projectDesc;
	}

	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}

	@Column(name = "PERCENT_COMPLETE", nullable = false, length = 3)
	public Integer getPercentComplete() {
		return this.percentComplete;
	}

	public void setPercentComplete(Integer percentComplete) {
		this.percentComplete = percentComplete;
	}

	@Column(name = "STATUS_CODE_ID", nullable = false, length = 20)
	public BigDecimal getStatusCodeId() {
		return this.statusCodeId;
	}

	public void setStatusCodeId(BigDecimal statusCodeId) {
		this.statusCodeId = statusCodeId;
	}

	@Column(name = "STATUS", nullable = false, length = 80)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "COLOR_CODE_ID", nullable = false, length = 20)
	public BigDecimal getColorCodeId() {
		return this.colorCodeId;
	}

	public void setColorCodeId(BigDecimal colorCodeId) {
		this.colorCodeId = colorCodeId;
	}

	@Column(name = "COLOR", nullable = false, length = 80)
	public String getColor() {
		return this.color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Column(name = "COLOR_IMAGE_URL", nullable = false, length = 240)
	public String getColorImageUrl() {
		return this.colorImageUrl;
	}

	public void setColorImageUrl(String colorImageUrl) {
		this.colorImageUrl = colorImageUrl;
	}

	@Column(name = "IS_SUBPROJECT", nullable = false, length = 1)
	public Integer getIsSubproject() {
		return this.isSubproject;
	}

	public void setIsSubproject(Integer isSubproject) {
		this.isSubproject = isSubproject;
	}

	@Column(name = "PROJECT_OWNER", nullable = false, length = 20)
	public BigDecimal getProjectOwner() {
		return this.projectOwner;
	}

	public void setProjectOwner(BigDecimal projectOwner) {
		this.projectOwner = projectOwner;
	}

	public String toString() {
		return new ToStringBuilder(this).append("portfolioId", getPortfolioId()).append("portfolioName",
				getPortfolioName()).append("projectId", getProjectId()).append("projectName", getProjectName()).append(
				"projectDesc", getProjectDesc()).append("percentComplete", getPercentComplete()).append("statusCodeId",
				getStatusCodeId()).append("status", getStatus()).append("colorCodeId", getColorCodeId()).append(
				"color", getColor()).append("colorImageUrl", getColorImageUrl()).append("isSubproject",
				getIsSubproject()).append("projectOwner", getProjectOwner()).toString();
	}

}
