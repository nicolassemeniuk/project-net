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

package net.project.project;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Iterator;

import javax.servlet.ServletContext;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.base.money.Money;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.code.ColorCode;
import net.project.code.ImprovementCode;
import net.project.database.DBBean;
import net.project.events.EventType;
import net.project.form.FormManager;
import net.project.hibernate.service.ServiceFactory;
import net.project.methodology.MethodologyProvider;
import net.project.notification.EventCodes;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.portfolio.IPortfolioEntry;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.security.group.GroupProvider;
import net.project.security.group.GroupProvider.PermissionSelection;
import net.project.space.ISpaceTypes;
import net.project.space.PersonalSpace;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.space.SpaceManager;
import net.project.space.SpaceRelationship;
import net.project.space.SpaceTypes;
import net.project.util.Conversion;
import net.project.util.HTMLUtils;
import net.project.util.StringUtils;
import net.project.util.TemplateFormatter;
import net.project.xml.XMLUtils;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import org.apache.log4j.Logger;

/**
 * Provides methods used in presenting a project as a virtual space.
 * ProjectSpace differs from Project in that Project contains the properties of
 * the project and ProjectSpace contains Space-specific methods and properties.
 */
public class ProjectSpace extends Space implements IPortfolioEntry, IJDBCPersistence, IXMLPersistence, Serializable, Comparable {

	/**
	 * The super project for this ProjectSpace. May be null if this project is
	 * not a "sub-project".
	 */
	private String parentProjectID = null;

	/**
	 * The business owner for this ProjectSpace. May be null if this project is
	 * not owned by a business.
	 */
	private String parentBusinessID = null;

	/**
	 * Denormalized parent business name derived from the parent business id
	 * This is set when loaded in from the database; anytime it is null it is
	 * dynamically looked-up from the parentBusinessID.
	 */
	private String parentBusinessName = null;

	/**
	 * The id of the template (methodology) from which this ProjectSpace was
	 * created.
	 */
	private String methodologyID = null;

	/**
	 * The percentage of this project complete.
	 */
	private String percentComplete = null;

	/**
	 * Represents the mechanism through which project completion is
	 * "calculated". The current options are: "manual" -- the project manager
	 * enters and maintains this value "schedule" -- project completion is based
	 * on schedule work percent complete
	 */
	private PercentCalculationMethod percentCalculationMethod = null;

	/**
	 * The id of this ProjectSpace's logo.
	 */
	private String projectLogoID = null;

	/**
	 * The color code of this project.
	 */
	private ColorCode colorCode = null;

	/**
	 * The HTTP URL to the image file representing the current color.
	 */
	private String colorImgURL = null;

	/**
	 * The status id of the current status.
	 */
	private String statusID = null;

	/**
	 * Denormalized status name.
	 */
	private String status = null;

	/**
	 * The start date for this project space.
	 */
	private java.util.Date startDate = null;

	/**
	 * The end date for this project space.
	 */
	private java.util.Date endDate = null;

	/**
	 * The default currency selection for this project space.
	 */
	private Currency defaultCurrency = null;

	/**
	 * The Project Sponsor.
	 */
	private String sponsor = null;

	/**
	 * Indicates this project's overall improvement.
	 */
	private ImprovementCode improvementCode = null;

	/**
	 * The description of the current status.
	 */
	private String currentStatusDescription = null;

	/**
	 * The color of the financial status.
	 */
	private ColorCode financialStatusColorCode = null;

	/**
	 * The improvement of the financial status.
	 */
	private ImprovementCode financialStatusImprovementCode = null;

	/**
	 * Budgeted total cost.
	 */
	private Money budgetedTotalCost = null;

	/**
	 * Current estimated total cost.
	 */
	private Money currentEstimatedTotalCost = null;

	/**
	 * Actual cost to date.
	 */
	private Money actualCostToDate = null;
	
	/**
	 * Discretional cost
	 */
	private Money discretionalCost = null;

	/**
	 * Estimated return on investment.
	 */
	private Money estimatedROI = null;

	/**
	 * Cost center value.
	 */
	private String costCenter = null;

	/**
	 * Color of schedule status.
	 */
	private ColorCode scheduleStatusColorCode = null;

	/**
	 * Improvement of schedule status.
	 */
	private ImprovementCode scheduleStatusImprovementCode = null;

	/**
	 * Color of resource status.
	 */
	private ColorCode resourceStatusColorCode = null;

	/**
	 * Improvement of resource status.
	 */
	private ImprovementCode resourceStatusImprovementCode = null;

	/**
	 * Priority.
	 */
	private PriorityCode priorityCode = null;

	/**
	 * Risk rating.
	 */
	private RiskCode riskRatingCode = null;

	/**
	 * The ProjectVisibility of this project. This defines who may see the
	 * project in a personal portfolio.
	 */
	private ProjectVisibility visibility = null;

	/**
	 * The PlanID of the default workplan created with a project. Note this
	 * value is never loaded or updated except on project create
	 */
	private String workplanID = null;

	/**
	 * The ID of the spaceAdministrator Role for this space Note this value is
	 * never loaded or updated except on project create
	 */
	private String spaceAdminRoleID = null;

	/**
	 * The current user context.
	 */
	private User user = null;

	/**
	 * The Project serial number.
	 */
	public String serial = null;

	/**
	 * Flag indicating whether to update workspace relationships on project
	 * create
	 */
	private boolean updateWorkspaceRelationshipsOnCreate = true;

	/**
	 * Flag indicating whether to copy system forms on project create
	 */
	private boolean copyFormsOnCreate = true;

	/**
	 * Flag indicating whether to reload the space after project create
	 */
	private boolean reloadSpaceOnCreate = true;

	private String schedulePlanID;

	/**
	 * Stores the project's additional (meta) properties.
	 */
	private MetaData metaData;

	private boolean parentChanged;

	private String templateApplied;

	private String superProjectName;

	//
	// Deprecated Properties
	//

	// this stuff doesn't belong here but...
	// will be replaced by forms in the future.
	// decided not to generalize into a SpacePropertySheet class yet --phil

	//
	// End Deprecated properties
	//

	/**
	 * Construct an empty ProjectSpace.
	 */
	public ProjectSpace() {
		this((String) null);
	}

	/**
	 * Construct a Project to be restored from persistence. No load is
	 * performed.
	 * 
	 * @param projectID
	 *            the id of the project
	 */
	public ProjectSpace(String projectID) {
		super(projectID);
		this.spaceType = SpaceTypes.PROJECT;
		setType(ISpaceTypes.PROJECT_SPACE);
	}

	/**
	 * Creates a ProjectSpace as a copy of the specified project space. This is
	 * a shallow clonse. Mutable properties of the source are simply passed by
	 * reference.
	 * 
	 * @param source
	 *            the projectSpace from which to copy the properties
	 */
	protected ProjectSpace(ProjectSpace source) {
		super(source);

		this.projectLogoID = source.projectLogoID;
		this.isLoaded = source.isLoaded();
		this.parentProjectID = source.parentProjectID;
		this.parentBusinessID = source.parentBusinessID;
		this.methodologyID = source.methodologyID;
		this.name = source.name;
		this.description = source.description;
		setColorCode(source.getColorCode());
		this.colorImgURL = source.colorImgURL;
		this.statusID = source.statusID;
		this.status = source.status;
		this.serial = source.serial;
		this.startDate = source.startDate;
		this.endDate = source.endDate;
		this.percentComplete = source.percentComplete;
		this.user = source.user;
		setPlanID(source.getPlanID());
		setDefaultCurrencyCode(source.getDefaultCurrencyCode());
		setSponsor(source.getSponsor());
		setImprovementCode(source.getImprovementCode());
		setCurrentStatusDescription(source.getCurrentStatusDescription());
		setFinancialStatusColorCode(source.getFinancialStatusColorCode());
		setFinancialStatusImprovementCode(source.getFinancialStatusImprovementCode());
		setBudgetedTotalCost(source.getBudgetedTotalCost());
		setCurrentEstimatedTotalCost(source.currentEstimatedTotalCost);
		setActualCostToDate(source.getActualCostToDate());
		setDiscretionalCost(source.getDiscretionalCost());
		setEstimatedROI(source.getEstimatedROI());
		setCostCenter(source.getCostCenter());
		setScheduleStatusColorCode(source.getScheduleStatusColorCode());
		setScheduleStatusImprovementCode(source.getScheduleStatusImprovementCode());
		setResourceStatusColorCode(source.getResourceStatusColorCode());
		setResourceStatusImprovementCode(source.getResourceStatusImprovementCode());
		setPriorityCode(source.getPriorityCode());
		setRiskRatingCode(source.getRiskRatingCode());
		setVisibility(source.getVisibility());
		setTemplateApplied(source.getTemplateApplied());
		setSuperProjectName(source.getSuperProjectName());
	}

	/**
	 * Specifies the id of this project's parent project.
	 * 
	 * @param parentProjectID
	 *            the id of the parent project.
	 * @see #getParentProjectID
	 */
	public void setParentProjectID(String parentProjectID) {
		this.parentProjectID = parentProjectID;
	}

	/**
	 * Returns the parent project id.
	 * 
	 * @return the id of the parent project
	 * @see #setParentProjectID
	 * @see #getParentProjectName
	 */
	public String getParentProjectID() {
		return this.parentProjectID;
	}

	/**
	 * Returs the ID of this projects workplan. Assumes on plan per space.
	 * 
	 * @return the id of the project workplan.
	 */
	public String getPlanID() {
		return this.schedulePlanID;
	}

	/**
	 * Set's the ID of the project workplan
	 * 
	 * @param planID
	 */
	protected void setPlanID(String planID) {
		this.schedulePlanID = planID;
	}

	/**
	 * Returns the name of the project for this specified id. Note: This method
	 * always requires a roundtrip to the database.
	 * 
	 * @param projectID
	 *            the id of the project for which to get the name
	 * @return the name of the project with specified id or null if the
	 *         specified id is null
	 * @deprecated as of 7.4; use {@link #getParentProjectName()} instead. This
	 *             method is never used outside of this class and is
	 *             unnecessary.
	 */
	public String getParentProjectName(String projectID) {
		return (DomainListBean.getProjectName(projectID));
	}

	/**
	 * Returns the name of this project's parent project.
	 * 
	 * @return the name of this project's parent project or null if the current
	 *         parent project id is null
	 * @see #setParentProjectID
	 */
	public String getParentProjectName() {
		return DomainListBean.getProjectName(this.parentProjectID);
	}

	/**
	 * Specifies this id of this project's parent business.
	 * 
	 * @param parentBusinessID
	 *            the parent business id
	 * @see #getParentBusinessID
	 */
	public void setParentBusinessID(String parentBusinessID) {
		this.parentBusinessID = parentBusinessID;
	}

	/**
	 * Returns the id of this project's parent business.
	 * 
	 * @return the parent business id
	 * @see #setParentBusinessID
	 */
	public String getParentBusinessID() {
		return this.parentBusinessID;
	}

	/**
	 * Sets the parent business name. This is a denormalized value, assumed to
	 * be the name of the business specified by the parentBusinessID.
	 * 
	 * @param parentBusinessName
	 *            the name of the parent business
	 * @see #getParentBusinessName
	 */
	public void setParentBusinessName(String parentBusinessName) {
		this.parentBusinessName = parentBusinessName;
	}

	/**
	 * Returns the name of the business for the specified id.
	 * 
	 * @param businessID
	 *            the id of the business for which to get the name
	 * @return the name of the business with specified id or null if the
	 *         specified id is null
	 * @deprecated as of 7.4; use {@link #getParentBusinessName()} instead. This
	 *             method is never used outside of this class and is
	 *             unnecessary.
	 */
	public String getParentBusinessName(String businessID) {
		return (DomainListBean.getBusinessName(businessID));
	}

	/**
	 * Returns the name of this project's parent business. Returns the parent
	 * business name of the project's parent business at the time of loading
	 * this project or looks up the business name from the business.
	 * <p>
	 * Note: If the parent business ID has changed since this project was loaded
	 * (and it previously had a parent business) then this method will continue
	 * to return the cached name of the loaded parent business id, not the newly
	 * set parent business id.
	 * </p>
	 * 
	 * @return the name of this project's parent business or null if the project
	 *         had no parent business id during loading or the current parent
	 *         business project id is null
	 */
	public String getParentBusinessName() {
		return this.parentBusinessName != null ? this.parentBusinessName : DomainListBean.getBusinessName(this.parentBusinessID);
	}

	/**
	 * Sets the visibility of this ProjectSpace.
	 * 
	 * @param visibility
	 *            the id of the visibility
	 * @deprecated as of 7.4; no replacement This property is never used.
	 *             Calling this method has no effect.
	 */
	public void setVisibilityID(String visibility) {
		// Do nothing
	}

	/**
	 * Returns the visibility id of this ProjectSpace.
	 * 
	 * @return the visibility id
	 * @deprecated as of 7.4; no replacement This property is never used. This
	 *             method always returns the empty string.
	 */
	public String getVisibilityID() {
		return "";
	}

	/**
	 * Returns the display name for the specified visiblity id.
	 * 
	 * @return the visibility display name
	 * @deprecated as of 7.4; no replacement This property is never used. This
	 *             method always returns the empty string.
	 */
	public String getVisibilityName(String visibilityID) {
		return "";
	}

	/**
	 * Returns the display name for this ProjectSpace's visiblity.
	 * 
	 * @return the visibility display name
	 * @deprecated as of 7.4; no replacement This property is never used. This
	 *             method always returns the empty string.
	 */
	public String getVisibilityName() {
		return "";
	}

	/**
	 * Specifies the id of the methodology from which this project was created.
	 * 
	 * @param methodologyID
	 *            the id of the methodology
	 * @see #getMethodologyID
	 */
	public void setMethodologyID(String methodologyID) {
		this.methodologyID = methodologyID;
	}

	/**
	 * Returns the id of the methodology from which this project was created.
	 * 
	 * @return the id of the methodology
	 * @see #setMethodologyID
	 */
	public String getMethodologyID() {
		return this.methodologyID;
	}

	/**
	 * Returns the name of the methodology for the specified id.
	 * 
	 * @param methodologyID
	 *            the id of the methodology for which to get the name
	 * @return the name of the methodology or null if the specified id is null
	 *         or there is no methodology with the specified id
	 * @deprecated as of 7.4; Use {@link #getMethodologyName()} instead. For
	 *             getting the id of an arbitrary methodology use
	 *             {@link MethodologyProvider#getMethodologyName} instead.
	 */
	public String getMethodologyName(String methodologyID) {
		return (MethodologyProvider.getMethodologyName(methodologyID));
	}

	/**
	 * Returns the name of the methodology from which this project was created.
	 * 
	 * @return the methodology name or null if the current methodology id is
	 *         null
	 * @see #setMethodologyID
	 */
	public String getMethodologyName() {
		return MethodologyProvider.getMethodologyName(this.methodologyID);
	}

	/**
	 * Set's the method for which completion percentage will be calculated for
	 * this workspace.
	 */
	public void setPercentCalculationMethod(String methodID) {
		this.percentCalculationMethod = PercentCalculationMethod.getForID(methodID);
	}

	/**
	 * Returns the completion method for this project space
	 * 
	 * @return
	 */
	public PercentCalculationMethod getPercentCalculationMethod() {
		return this.percentCalculationMethod;
	}

	/**
	 * Specifies the percent complete of this project.
	 * 
	 * @param percentComplete
	 *            the percent complete value
	 * @see #getPercentComplete
	 */
	public void setPercentComplete(String percentComplete) {
		this.percentComplete = percentComplete;
	}

	/**
	 * Returns the percent complete value of this project.
	 * 
	 * @return the percent complete
	 * @see #setPercentComplete
	 */
	public String getPercentComplete() {
		// now returning "0" if the %complete is null due to that possibility
		// occuring with schedule auto-calc.
		return (this.percentComplete != null) ? this.percentComplete : "0";
	}

	/**
	 * Specifies the id of this project's logo.
	 * 
	 * @param projectLogoID
	 *            the id of the project logo
	 * @see #getProjectLogoID
	 */
	public void setProjectLogoID(String projectLogoID) {
		this.projectLogoID = projectLogoID;
	}

	/**
	 * Returns the id of this project's logo.
	 * 
	 * @return the id of the project logo
	 * @see #setProjectLogoID
	 */
	public String getProjectLogoID() {
		return this.projectLogoID;
	}

	/**
	 * Sets a flag (default true) indicating whether to update workspace
	 * relationships on create.
	 * 
	 * @param updateRelationships
	 *            flag indicating whether to update relationships
	 * @see #updateWorkspaceRelationshipsOnCreate
	 */
	public void setUpdateWorkspaceRelationshipsOnCreate(boolean updateRelationships) {
		this.updateWorkspaceRelationshipsOnCreate = updateRelationships;
	}

	/**
	 * Sets a flag (default true) indicating whether to copy system forms on
	 * project create. Setting this value to true will cause any active forms in
	 * the Application Space to be copied to the project being created
	 * 
	 * @param copyForms
	 *            flag indicating whether to copy the default forms
	 * @see #copyFormsOnCreate
	 */
	public void setCopyFormsOnCreate(boolean copyForms) {
		this.copyFormsOnCreate = copyForms;
	}

	/**
	 * Sets a flag (default true) indicating whether to reload the new workspace
	 * on project create. Setting this value to true will cause the workspace to
	 * be reloaded during the create process.
	 * 
	 * @param reloadSpace
	 *            indicates whether to allow the reloading of the space
	 * @see #reloadSpaceOnCreate
	 */
	public void setReloadSpaceOnCreate(boolean reloadSpace) {
		this.reloadSpaceOnCreate = reloadSpace;
	}

	/**
	 * Sets the plan ID of the default project workplan -- ONLY on project
	 * create. Note this value is never updated, loaded or stored except on
	 * project create. The functionality was added due to the requirement for
	 * the workplan ID when creating a project using the API (primarily for data
	 * population)
	 * 
	 * @param planID
	 *            the ID of the workplan
	 * @see #workplanID
	 */
	private void setWorkplanID(String planID) {
		this.workplanID = planID;
	}

	/**
	 * Returns the plan ID of the default project workplan. IMPORTANT: this
	 * value is never pouplated, loaded or stored except on project create. The
	 * functionality was added due to the requirement for the workplan ID when
	 * creating a project using the API (primarily for data population)
	 * 
	 * @return workplanID
	 * @see #workplanID
	 */
	public String getWorkplanID() {
		return this.workplanID;
	}

	/**
	 * Sets the ID for the space administrator role -- only set on project
	 * create Note this value is never updated, loaded or stored except on
	 * project create.
	 * 
	 * @param roleID
	 *            the ID of the admin role
	 * @see #spaceAdminRoleID
	 */
	private void setSpaceAdminRoleID(String roleID) {
		this.spaceAdminRoleID = roleID;
	}

	/**
	 * Gets the ID for the space administrator role -- only set on project
	 * create Note this value is never updated, loaded or stored except on
	 * project create.
	 * 
	 * @return the ID of the admin role
	 * @see #spaceAdminRoleID
	 */
	public String getSpaceAdminRoleID() {
		return this.spaceAdminRoleID;
	}

	/**
	 * Sets the color code id for this project space.
	 * 
	 * @param colorCodeID
	 *            the id of the color
	 * @see #getColorCodeID
	 * @deprecated as of 7.4; use {@link #setColorCode} instead
	 */
	public void setColorCodeID(String colorCodeID) {
		ColorCode foundColorCode = ColorCode.findByID(colorCodeID);
		if (foundColorCode == ColorCode.EMPTY) {
			this.colorCode = null;
		} else {
			this.colorCode = foundColorCode;
		}
	}

	/**
	 * Returns this ProjectSpace's color code id
	 * 
	 * @return the id of the color selected for this project space
	 * @see #setColorCodeID
	 * @deprecated as of 7.4; use {@link #getColorCode} instead
	 */
	public String getColorCodeID() {
		return (getColorCode() == null ? null : getColorCode().getID());
	}

	/**
	 * Sets the color code of this project.
	 * 
	 * @param colorCode
	 *            the color code
	 * @see #getColorCode
	 */
	public void setColorCode(ColorCode colorCode) {
		if (colorCode == ColorCode.EMPTY) {
			this.colorCode = null;
		} else {
			this.colorCode = colorCode;
		}
	}

	/**
	 * Returns this project's color code.
	 * 
	 * @return the color code or null if none has been set
	 * @see #setColorCode
	 */
	public ColorCode getColorCode() {
		return this.colorCode;
	}

	/**
	 * Returns the display name of the color code for the specified color code
	 * id.
	 * 
	 * @param colorCode
	 *            the id of the color code for which to get the name
	 * @return the display name of the color code or null if the color code is
	 *         not valid
	 * @deprecated as of 7.4; no replacement. This method does not cache the
	 *             color code name. See {@link DomainListBean#getColorCodeName}
	 *             if it is necessary to find the display name for an arbitrary
	 *             color code or use {@link #getColorCodeName} to get this
	 *             project's color code name
	 */
	public String getColorCodeName(String colorCode) {
		return (DomainListBean.getColorCodeName(colorCode));
	}

	/**
	 * Returns the display name of this project space's color code.
	 * 
	 * @return the display name of the color code or null if no color code has
	 *         been set
	 */
	public String getColorCodeName() {
		return (getColorCode() == null ? null : getColorCode().getName());
	}

	/**
	 * Sets the HTML image URL of the image file that represents this project
	 * space's color.
	 * 
	 * @param colorImgURL
	 *            the HTML image URL
	 * @see #getColorImgURL
	 */
	public void setColorImgURL(String colorImgURL) {
		this.colorImgURL = colorImgURL;
	}

	/**
	 * Returns the HTML image URL of the image file that represents this project
	 * space's color.
	 * 
	 * @return the HTML image URL
	 * @see #setColorImgURL
	 */
	public String getColorImgURL() {
		return this.colorImgURL;
	}

	/**
	 * Sets this project's status id
	 * 
	 * @param statusID
	 *            the project's status id
	 * @see #getStatusID
	 */
	public void setStatusID(String statusID) {
		this.statusID = statusID;
	}

	/**
	 * Returns this project's status id.
	 * 
	 * @return the status id
	 * @see #setStatusID
	 */
	public String getStatusID() {
		return this.statusID;
	}

	/**
	 * Specifies the status display name corresponding to this project's status
	 * id. No check is made to ensure that these are consistent.
	 * 
	 * @param status
	 *            the status display name
	 * @see #getStatus
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Retuns this project's status display name.
	 * 
	 * @return the display name of the project's status
	 * @see #setStatus
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * Returns the display name of the specified status id. This is equivalent
	 * to calling <code>ProjectStatus.findByID(statusID).getName()</code>.
	 * 
	 * @param statusID
	 *            the status id for which to get the display name
	 * @return the display name
	 * @deprecated as of 7.4; use {@link #getStatus} to get this project's
	 *             status display name.
	 */
	public String getStatusName(String statusID) {
		return ProjectStatus.findByID(statusID).getName();
	}

	/**
	 * Returns the display name of this project space's status.
	 * 
	 * @return the status display name
	 * @deprecated as of 7.4; use {@link #getStatus} instead.
	 */
	public String getStatusName() {
		return getStatus();
	}

	/**
	 * Sets the start date of this project from a string value. This method
	 * parses the dateString creating a date object and then sets the startDate
	 * of this project.
	 * 
	 * @param startDateString
	 *            the startDate as a String to set to.
	 * @see #setStartDate
	 */
	public void setStartDateString(String startDateString) throws net.project.util.InvalidDateException {

		java.util.Date startDate = null;

		if (!"".equals(startDateString)) {
			startDate = net.project.security.SessionManager.getUser().getDateFormatter().parseDateString(startDateString);

		}

		setStartDate(startDate);
	}

	/**
	 * Returns a formatted start date. The start date is formatted based on the
	 * user's date format.
	 * 
	 * @return the formatted start date
	 */
	public String getStartDateString() {
		return Conversion.dateToString(this.startDate);
	}

	/**
	 * Sets the startDate of this project.
	 * 
	 * @param startDate
	 *            the start Date to which to set to.
	 */
	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Returns the start date of this project space.
	 * 
	 * @return the start date
	 */
	public java.util.Date getStartDate() {
		return this.startDate;
	}

	/**
	 * Sets the end date of this project. This method parses the endDateString
	 * creating a date object and then sets the startDate of this project.
	 * 
	 * @param endDateString
	 *            the endDate as a String to set to.
	 * @see #setEndDate
	 */
	public void setEndDateString(String endDateString) throws net.project.util.InvalidDateException {
		java.util.Date endDate = null;

		if (!"".equals(endDateString)) {
			endDate = net.project.security.SessionManager.getUser().getDateFormatter().parseDateString(endDateString);

		}

		setEndDate(endDate);
	}

	/**
	 * Returns a formatted end date. The end date is formatted based on the
	 * user's date format.
	 * 
	 * @return the formatted end date
	 */
	public String getEndDateString() {
		return Conversion.dateToString(this.endDate);
	}

	/**
	 * Sets the endDate of this project.
	 * 
	 * @param endDate
	 *            the end Date to set to.
	 */
	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Returns this project space's end date.
	 * 
	 * @return the end date
	 */
	public java.util.Date getEndDate() {
		return this.endDate;
	}

	/**
	 * Sets whether this project is a sub-project
	 * 
	 * @param isSubproject
	 *            true if this is a sub-project; false otherwise
	 * @see #isSubproject
	 * @deprecated As of 7.4, no replacement. A subproject is defined as a
	 *             project with a parent project. This method will be removed in
	 *             a future release.
	 */
	public void setIsSubproject(boolean isSubproject) {
		// this.isSubproject = isSubproject;
		// Do nothing; the isSubproject property was assigned
		// but never accessed. It has been removed.
	}

	/**
	 * Indicates whether this project is a sub-project. A sub-project is defined
	 * as a project with a parent project.
	 * 
	 * @return true if this project is a sub-project; false otherwise
	 * @see #getParentProjectID
	 */
	public boolean isSubproject() {
		return (getParentProjectID() != null);
	}

	/**
	 * Sets the default currency for this project. This is assumed to be the
	 * currency for all money values entered.
	 * 
	 * @param defaultCurrency
	 *            the currency
	 * @see #getDefaultCurrency
	 */
	public void setDefaultCurrency(Currency defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	/**
	 * Returns the default currency for this project.
	 * <p>
	 * If this Project does not yet have a currency, the system default currency
	 * is returned. If there is no valid system default currency then null is
	 * returned.
	 * </p>
	 * 
	 * @return the default currency or null if none is available
	 * @see #setDefaultCurrency
	 */
	public Currency getDefaultCurrency() {
		if (this.defaultCurrency == null) {
			this.defaultCurrency = net.project.util.Currency.getSystemDefaultCurrency();
		}
		return defaultCurrency;
	}

	/**
	 * Convenience method to set the default currency for the specified currency
	 * code. Equivalent to calling
	 * <code>setDefaultCurrency(java.util.Currency.getInstance(currencyCode)</code>
	 * 
	 * @param currencyCode
	 *            the currency code to set the currency from; sets the currency
	 *            to null if currencyCode is null
	 * @see #getDefaultCurrencyCode
	 */
	public void setDefaultCurrencyCode(String currencyCode) {
		if (currencyCode == null) {
			setDefaultCurrency(null);
		} else {
			setDefaultCurrency(Currency.getInstance(currencyCode));
		}
	}

	/**
	 * Convenience method to return the code for this project space's default
	 * currency.
	 * 
	 * @return the code for the default currency or <code>null</code> if the
	 *         default currency is null
	 * @see #setDefaultCurrencyCode
	 */
	public String getDefaultCurrencyCode() {
		String currencyCode = null;
		if (getDefaultCurrency() != null) {
			currencyCode = getDefaultCurrency().getCurrencyCode();
		}
		return currencyCode;
	}

	/**
	 * Sets the sponsor information.
	 * 
	 * @param sponsor
	 *            the sponsor information
	 * @see #getSponsor
	 */
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	/**
	 * Returns the sponsor information.
	 * 
	 * @return the sponsor information
	 * @see #setSponsor
	 */
	public String getSponsor() {
		return sponsor;
	}

	/**
	 * Specifies the improvement code for this ProjectSpace.
	 * 
	 * @param improvementCode
	 *            the improvement code
	 * @see #getImprovementCode
	 */
	public void setImprovementCode(ImprovementCode improvementCode) {
		if (improvementCode == ImprovementCode.EMPTY) {
			this.improvementCode = null;
		} else {
			this.improvementCode = improvementCode;
		}
	}

	/**
	 * Returns the improvement code for this ProjectSpace.
	 * 
	 * @return the improvement code
	 * @see #setImprovementCode
	 */
	public ImprovementCode getImprovementCode() {
		return this.improvementCode;
	}

	/**
	 * Specifies the description of the current status of this project.
	 * 
	 * @param currentStatusDescription
	 *            the current status description
	 * @see #getCurrentStatusDescription
	 */
	public void setCurrentStatusDescription(String currentStatusDescription) {
		this.currentStatusDescription = currentStatusDescription;
	}

	/**
	 * Returns the description of the current status of this projec.t
	 * 
	 * @return the current status description
	 * @see #setCurrentStatusDescription
	 */
	public String getCurrentStatusDescription() {
		return this.currentStatusDescription;
	}

	/**
	 * Specifies the color code for the financial status of this project.
	 * 
	 * @param financialStatusColorCode
	 *            the color code
	 * @see #getFinancialStatusColorCode
	 */
	public void setFinancialStatusColorCode(ColorCode financialStatusColorCode) {
		if (financialStatusColorCode == ColorCode.EMPTY) {
			this.financialStatusColorCode = null;
		} else {
			this.financialStatusColorCode = financialStatusColorCode;
		}
	}

	/**
	 * Returns the color code for the financial status of this project.
	 * 
	 * @return the color code
	 * @see #setFinancialStatusColorCode
	 */
	public ColorCode getFinancialStatusColorCode() {
		return this.financialStatusColorCode;
	}

	/**
	 * Specifies the improvement code for the financial status of this project.
	 * 
	 * @param financialStatusImprovementCode
	 *            the improvement code
	 * @see #getFinancialStatusImprovementCode
	 */
	public void setFinancialStatusImprovementCode(ImprovementCode financialStatusImprovementCode) {
		if (financialStatusImprovementCode == ImprovementCode.EMPTY) {
			this.financialStatusImprovementCode = null;
		} else {
			this.financialStatusImprovementCode = financialStatusImprovementCode;
		}
	}

	/**
	 * Returns the improvement code for the financial status of this project.
	 * 
	 * @return the improvement code
	 * @see #setFinancialStatusImprovementCode
	 */
	public ImprovementCode getFinancialStatusImprovementCode() {
		return this.financialStatusImprovementCode;
	}

	/**
	 * Specifies the budgeted total cost for this project.
	 * 
	 * @param budgetedTotalCost
	 *            the budgeted total cost
	 * @see #getBudgetedTotalCost
	 */
	public void setBudgetedTotalCost(Money budgetedTotalCost) {
		if (budgetedTotalCost == Money.EMPTY) {
			this.budgetedTotalCost = null;
		} else {
			this.budgetedTotalCost = budgetedTotalCost;
		}
	}

	/**
	 * Returns the budgeted total cost for this project.
	 * 
	 * @return the budgeted total cost
	 * @see #setBudgetedTotalCost
	 */
	public Money getBudgetedTotalCost() {
		return this.budgetedTotalCost;
	}

	/**
	 * Specifies the currented estimated total cost of this project.
	 * 
	 * @param currentEstimatedTotalCost
	 *            the current estimated total cost
	 * @see #getCurrentEstimatedTotalCost
	 */
	public void setCurrentEstimatedTotalCost(Money currentEstimatedTotalCost) {
		if (currentEstimatedTotalCost == Money.EMPTY) {
			this.currentEstimatedTotalCost = null;
		} else {
			this.currentEstimatedTotalCost = currentEstimatedTotalCost;
		}
	}

	/**
	 * Returns the current estimated total cost of this project.
	 * 
	 * @return the current estimated total cost
	 * @see #setCurrentEstimatedTotalCost
	 */
	public Money getCurrentEstimatedTotalCost() {
		try {
			if (getMetaData().getProperty("CostCalculationMethod").equals("manual"))
				return this.currentEstimatedTotalCost;
			else {
				Float cost = ServiceFactory.getInstance().getProjectFinancialService().calculateEstimatedTotalCost(spaceID);
				return new Money(String.valueOf(cost), getDefaultCurrency());
			}

		} catch (NoSuchPropertyException e) {
			return new Money();
		}
	}
	
	/**
	 * Obtain the material estimated total cost for the project. This can only be
	 * obtained if the project financial calculation method is set to automatic.
	 * Will return a new money instance (0.00) otherwise.
	 * 
	 * @return the materials estimated total cost.
	 */
	
	public Money getMaterialCurrentEstimatedTotalCost(){
		try {
			if (getMetaData().getProperty("CostCalculationMethod").equals("manual"))
				return new Money();
			else {
				Float cost = ServiceFactory.getInstance().getProjectFinancialService().calculateMaterialCurrentEstimatedTotalCost(spaceID);
				return new Money(String.valueOf(cost), getDefaultCurrency());
			}

		} catch (NoSuchPropertyException e) {
			return new Money();
		}
	}
	
	/**
	 * Obtain the resources estimated total cost for the project. This can only
	 * be obtained if the project financial calculation method is set to
	 * automatic. Will return a new money instance (0.00) otherwise.
	 * 
	 * @return the resources estimated total cost.
	 */
	
	public Money getResourcesCurrentEstimatedTotalCost(){
		try {
			if (getMetaData().getProperty("CostCalculationMethod").equals("manual"))
				return new Money();
			else {
				Float cost = ServiceFactory.getInstance().getProjectFinancialService().calculatResourcesCurrentEstimatedTotalCost(spaceID);
				return new Money(String.valueOf(cost), getDefaultCurrency());
			}

		} catch (NoSuchPropertyException e) {
			return new Money();
		}
	}

	/**
	 * Specifies the actual cost to date for this project.
	 * 
	 * @param actualCostToDate
	 *            the actual cost to date
	 * @see #getActualCostToDate
	 */
	public void setActualCostToDate(Money actualCostToDate) {
		if (actualCostToDate == Money.EMPTY) {
			this.actualCostToDate = null;
		} else {
			this.actualCostToDate = actualCostToDate;
		}
	}

	/**
	 * Returns the actual cost to date for this project.
	 * 
	 * @return the actual cost to date
	 * @see #setActualCostToDate
	 */
	public Money getActualCostToDate() {
		try {

			if (getMetaData().getProperty("CostCalculationMethod").equals("manual"))
				return this.actualCostToDate;
			else {
				Float cost = ServiceFactory.getInstance().getProjectFinancialService().calculateActualCostToDate(spaceID);
				return new Money(String.valueOf(cost), getDefaultCurrency());
			}

		} catch (NoSuchPropertyException e) {
			return new Money();
		}		
	}

	/**
	 * Obtain the material actual cost to date for the project. This can only be
	 * obtained if the project financial calculation method is set to automatic.
	 * Will return a new money instance (0.00) otherwise.
	 * 
	 * @return the materials actual cost.
	 */
	public Money getMaterialTotalActualCost(){
		try {
			if (getMetaData().getProperty("CostCalculationMethod").equals("manual"))
				return new Money();
			else {
				Float cost = ServiceFactory.getInstance().getProjectFinancialService().calculateMaterialActualTotalCostToDate(spaceID);
				return new Money(String.valueOf(cost), getDefaultCurrency());
			}

		} catch (NoSuchPropertyException e) {
			return new Money();
		}
	}
	
	
	/**
	 * Obtain the resources actual cost to date for the project. This can only
	 * be obtained if the project financial calculation method is set to
	 * automatic. Will return a new money instance (0.00) otherwise.
	 * 
	 * @return the resources actual cost.
	 */
	public Money getResourcesTotalActualCost(){
		try {
			if (getMetaData().getProperty("CostCalculationMethod").equals("manual"))
				return new Money();
			else {
				Float cost = ServiceFactory.getInstance().getProjectFinancialService().calculateResourcesActualTotalCostToDate(spaceID);
				return new Money(String.valueOf(cost), getDefaultCurrency());
			}

		} catch (NoSuchPropertyException e) {
			return new Money();
		}
	}

	/**
	 * Specifies the estimated return on investment for this project.
	 * 
	 * @param estimatedROI
	 *            the estimated return on investment
	 * @see #getEstimatedROI
	 */
	public void setEstimatedROI(Money estimatedROI) {
		if (estimatedROI == Money.EMPTY) {
			this.estimatedROI = null;
		} else {
			this.estimatedROI = estimatedROI;
		}
	}

	/**
	 * Returns the estimated return on investment for this project.
	 * 
	 * @return the estimated return on investment
	 * @see #setEstimatedROI
	 */
	public Money getEstimatedROI() {
		return this.estimatedROI;
	}
	
	/**
	 * Returns the discretional cost for this project. This value is only returned in case the project costs
	 * calculation method is automatic.
	 * @return the project discretional costs.
	 */
	public Money getDiscretionalCost(){
		try {
			if (getMetaData().getProperty("CostCalculationMethod").equals("manual"))
				return new Money();
			else {
				String cost = getMetaData().getProperty("ProjectDiscretionalCost");
				return new Money(cost, getDefaultCurrency());
			}

		} catch (NoSuchPropertyException e) {
			return new Money();
		}	
	}
	
	/**
	 * Sets the discretional cost for this project.
	 * @param discretionalCost the discretional cost.
	 */
	public void setDiscretionalCost(Money discretionalCost){
		if(discretionalCost == Money.EMPTY){
			this.discretionalCost = null;
		} else {
			this.discretionalCost = discretionalCost;
		}
	}
	

	

	/**
	 * Specifies a cost center.
	 * 
	 * @param costCenter
	 *            the cost center
	 * @see #getCostCenter
	 */
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * Returns the current cost center.
	 * 
	 * @return the cost center
	 * @see #setCostCenter
	 */
	public String getCostCenter() {
		return this.costCenter;
	}

	/**
	 * Specifies the color code for the schedule status.
	 * 
	 * @param scheduleStatusColorCode
	 *            the color code
	 * @see #getScheduleStatusColorCode
	 */
	public void setScheduleStatusColorCode(ColorCode scheduleStatusColorCode) {
		if (scheduleStatusColorCode == ColorCode.EMPTY) {
			this.scheduleStatusColorCode = null;
		} else {
			this.scheduleStatusColorCode = scheduleStatusColorCode;
		}
	}

	/**
	 * Returns the color code for the schedule status.
	 * 
	 * @return the color code
	 * @see #setScheduleStatusColorCode
	 */
	public ColorCode getScheduleStatusColorCode() {
		return this.scheduleStatusColorCode;
	}

	/**
	 * Specifies the improvement code for the schedule status.
	 * 
	 * @param scheduleStatusImprovementCode
	 *            the improvement code
	 * @see #getScheduleStatusImprovementCode
	 */
	public void setScheduleStatusImprovementCode(ImprovementCode scheduleStatusImprovementCode) {
		if (scheduleStatusImprovementCode == ImprovementCode.EMPTY) {
			this.scheduleStatusImprovementCode = null;
		} else {
			this.scheduleStatusImprovementCode = scheduleStatusImprovementCode;
		}
	}

	/**
	 * Returns the improvement code for the schedule status.
	 * 
	 * @return the improvement code
	 * @see #setScheduleStatusImprovementCode
	 */
	public ImprovementCode getScheduleStatusImprovementCode() {
		return this.scheduleStatusImprovementCode;
	}

	/**
	 * Specifies the color code for the resource status.
	 * 
	 * @param resourceStatusColorCode
	 *            the color code
	 * @see #getResourceStatusColorCode
	 */
	public void setResourceStatusColorCode(ColorCode resourceStatusColorCode) {
		if (resourceStatusColorCode == ColorCode.EMPTY) {
			this.resourceStatusColorCode = null;
		} else {
			this.resourceStatusColorCode = resourceStatusColorCode;
		}
	}

	/**
	 * Returns the color code for the resource status.
	 * 
	 * @return the color code
	 * @see #setResourceStatusColorCode
	 */
	public ColorCode getResourceStatusColorCode() {
		return this.resourceStatusColorCode;
	}

	/**
	 * Specifies the improvement code for the resource status.
	 * 
	 * @param resourceStatusImprovementCode
	 *            the improvement code
	 * @see #getResourceStatusImprovementCode
	 */
	public void setResourceStatusImprovementCode(ImprovementCode resourceStatusImprovementCode) {
		if (resourceStatusImprovementCode == ImprovementCode.EMPTY) {
			this.resourceStatusImprovementCode = null;
		} else {
			this.resourceStatusImprovementCode = resourceStatusImprovementCode;
		}
	}

	/**
	 * Returns the improvement code for the resource status.
	 * 
	 * @return the improvement code
	 * @see #setResourceStatusImprovementCode
	 */
	public ImprovementCode getResourceStatusImprovementCode() {
		return this.resourceStatusImprovementCode;
	}

	/**
	 * Specifies the priority of this project.
	 * 
	 * @param priorityCode
	 *            the priority
	 * @see #getPriorityCode
	 */
	public void setPriorityCode(PriorityCode priorityCode) {
		if (priorityCode == PriorityCode.EMPTY) {
			this.priorityCode = null;
		} else {
			this.priorityCode = priorityCode;
		}
	}

	/**
	 * Returns the priority of this project.
	 * 
	 * @return the priority
	 * @see #setPriorityCode
	 */
	public PriorityCode getPriorityCode() {
		return this.priorityCode;
	}

	/**
	 * Specifies the risk rating of this project.
	 * 
	 * @param riskRatingCode
	 *            the risk rating
	 * @see #getRiskRatingCode
	 */
	public void setRiskRatingCode(RiskCode riskRatingCode) {
		if (riskRatingCode == RiskCode.EMPTY) {
			this.riskRatingCode = null;
		} else {
			this.riskRatingCode = riskRatingCode;
		}
	}

	/**
	 * Returns the risk rating of this project.
	 * 
	 * @return the risk rating
	 * @see #setRiskRatingCode
	 */
	public RiskCode getRiskRatingCode() {
		return this.riskRatingCode;
	}

	/**
	 * Specifies the visibility of this project. The visibility defines who may
	 * view this project in a portfolio. See the {@link ProjectVisibility} class
	 * documentation for details.
	 * 
	 * @param visibility
	 *            the visibility for this project
	 * @see #getVisibility
	 */
	public void setVisibility(ProjectVisibility visibility) {
		this.visibility = visibility;
	}

	/**
	 * Returns the visibility of this project.
	 * 
	 * @return the visibility
	 * @see #setVisibility
	 */
	public ProjectVisibility getVisibility() {
		return this.visibility;
	}

	/**
	 * Specifies the serial used to create this project.
	 * 
	 * @param serial
	 *            the serial
	 * @see #getSerial
	 */
	public void setSerial(String serial) {
		this.serial = serial;
	}

	/**
	 * Returns the serial used to create this project.
	 * 
	 * @return the serial
	 * @see #setSerial
	 */
	public String getSerial() {
		return this.serial;
	}

	/**
	 * Specifies the current user context.
	 * 
	 * @param user
	 *            the current user
	 * @see #getUser
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Returns the current user context.
	 * 
	 * @return the current user
	 * @see #setUser
	 */
	public User getUser() {
		return this.user;
	}

	public String getParentSpaceID() {
		return getParentBusinessID();
	}

	/**
	 * 
	 * @param uccID
	 * @return a <code>String</code> value which should not be used.
	 * @deprecated as of 7.4; no replacement. This property is never used.
	 */
	public String getUseClassificationCodeName(String uccID) {
		return (DomainListBean.getUseClassificationCodeName(uccID));
	}

	/**
	 * 
	 * @param sccID
	 * @return a <code>String</code> value which should not be used.
	 * @deprecated as of 7.4; no replacement. This property is never used.
	 */
	public String getSiteConditionCodeName(String sccID) {
		return DomainListBean.getSiteConditionCodeName(sccID);
	}

	/**
	 * 
	 * @param servicesCode
	 * @return a <code>String</code> value which should not be used.
	 * @deprecated as of 7.4; no replacement. This property is never used.
	 */
	public String getServicesCodeName(String servicesCode) {
		return DomainListBean.getServicesCodeName(servicesCode);
	}

	/**
	 * 
	 * @param projectTypeID
	 * @return a <code>String</code> value which should not be used.
	 * @deprecated as of 7.4; no replacement. This property is never used.
	 */
	public String getProjectTypeName(String projectTypeID) {
		return DomainListBean.getProjectTypeName(projectTypeID);
	}

	public MetaData getMetaData() {
		if (metaData == null) {
			metaData = MetaData.getMetaData(spaceID);
		}
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	//
	// Implementing ILinkableObject
	//

	/**
	 * Get the type of the object as registered in the database. (pn_object
	 * table).
	 * 
	 * @return the type string for the object as defined in the database.
	 */
	public String getObjectType() {
		return "project";
	}

	/**
	 * Get the name of this project for display when linked.
	 * 
	 * @return the name of this ProjectSpace.
	 */
	public String getObjectName() {
		return this.name;
	}

	//
	// End ILinkableObject
	//

	public boolean isParentChanged() {
		return parentChanged;
	}

	public void setParentChanged(boolean parentChanged) {
		this.parentChanged = parentChanged;
	}

	/**
	 * Loads the the project from the database.
	 * 
	 * @throws PersistenceException
	 *             if there is a problem loading.
	 * @throws IllegalStateException
	 *             the id of the project to load has not been set
	 */
	public void load() throws PersistenceException {

		setLoaded(false);

		// Load this project space
		new ProjectSpaceFinder().findByID(getID(), this);

		// sjmittal: this piece of code and more is now moved to the
		// ProjectSpaceFinder populate method
		// load the super project space.
		// Space parentSpace = SpaceManager.getSuperProject(this);
		// if (parentSpace != null) {
		// setParentProjectID(parentSpace.getID());
		// } else {
		// setParentProjectID(null);
		// }

	}

	protected void calculatePercentComplete() throws SQLException {

		if (getPercentCalculationMethod().equals(PercentCalculationMethod.SCHEDULE)) {
			this.percentComplete = getScheduleCompletionPercentage();

		} else {
			// do nothing because we already have the percent complete
			// loaded
		}
	}

	private String getScheduleCompletionPercentage() throws SQLException {
		DBBean db = new DBBean();
		String completion = null;
		double completionNumber = 0;

		try {

			db.prepareCall("begin ? := SCHEDULE.GET_SCHEDULE_PERCENT_COMPLETE(?); end;");

			db.cstmt.registerOutParameter(1, java.sql.Types.NUMERIC);
			db.cstmt.setString(2, getPlanID());

			db.executeCallable();

			completionNumber = db.cstmt.getDouble(1);
			completion = Double.toString(completionNumber);
		} finally {
			db.release();
		}

		return completion;
	}

	/**
	 * Stores this project space. If {@link #isLoaded} is true the project space
	 * is updated, otherwise a new project space is created.
	 * 
	 * @throws PersistenceException
	 *             if there is a problem storing
	 */
	public void store() throws PersistenceException {

		DBBean db = new DBBean();

		try {
			store(db);
			db.commit();
		} catch (SQLException sql) {
			throw new PersistenceException("ProjectSpace.store() threw an SQLE: " + sql, sql);
		} finally {
			db.release();
		}
	}

	/**
	 * Stores this project space. Does not explicitly commit store transaction
	 * If {@link #isLoaded} is true the project space is updated, otherwise a
	 * new project space is created.
	 * 
	 * @throws PersistenceException
	 *             if there is a problem storing
	 */
	public void store(DBBean db) throws PersistenceException {

		if (!isLoaded) {
			create(db);
		} else {
			update(db);
		}
	}

	/**
	 * Private helper to create a new project workspace. Creates only the
	 * workspace infrastructure and does not copy forms or establish personal or
	 * business space ownership or parent/child project relationships with
	 * 
	 * @throws PersistenceException
	 *             if there is a problem creating the project space
	 */
	private void createProjectSpace() throws PersistenceException {
		DBBean db = new DBBean();

		try {
			createProjectSpace(db);
			db.commit();
		} catch (SQLException sql) {
			throw new PersistenceException("ProjectSpace.createProjectSpace() threw an SQLE: " + sql, sql);
		} finally {
			db.release();
		}
	}

	/**
	 * Private helper to create a new project workspace. Creates only the
	 * workspace infrastructure and does not copy forms or establish personal or
	 * business space ownership or parent/child project relationships. Note
	 * method does *not* commit transaction
	 * 
	 * @param db
	 *            DBBean for database access
	 * @throws PersistenceException
	 *             if there is a problem creating the project space
	 */
	private void createProjectSpace(DBBean db) throws PersistenceException {

		Timestamp startDateTS = null;
		Timestamp endDateTS = null;

		try {

			db.setAutoCommit(false);

			if (getStartDate() != null) {
				startDateTS = new Timestamp(this.startDate.getTime());
			}

			if (getEndDate() != null) {
				endDateTS = new Timestamp(this.endDate.getTime());
			}

			int projectIDIndex = 0;
			int planIDIndex = 0;
			int spaceAdminRoleIDIndex = 0;
			int index = 0;
			db.prepareCall("{call PROJECT.CREATE_PROJECT (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?)}");

			db.cstmt.setString(++index, SessionManager.getUser().getID());
			db.cstmt.setString(++index, this.parentProjectID);
			db.cstmt.setString(++index, this.parentBusinessID);
			// Old "visibility" setting was poorly defined. It seems to be
			// either "Personal Space" or nothing else. It has been
			// deprecated.
			db.cstmt.setNull(++index, java.sql.Types.NUMERIC);
			db.cstmt.setString(++index, this.name);
			db.cstmt.setString(++index, this.description);
			db.cstmt.setString(++index, this.statusID);
			if (getColorCode() == null) {
				db.cstmt.setNull(++index, java.sql.Types.VARCHAR);
			} else {
				db.cstmt.setString(++index, getColorCode().getID());
			}
			db.cstmt.setString(++index, getPercentCalculationMethod().getID());
			db.cstmt.setString(++index, this.percentComplete);
			db.cstmt.setTimestamp(++index, startDateTS);
			db.cstmt.setTimestamp(++index, endDateTS);
			db.cstmt.setString(++index, this.serial);
			db.cstmt.setString(++index, this.projectLogoID);

			// Add common parameters
			// A number of other parameters are common to update and create
			// but they are in different orders
			index = bindCommonParameters(db.cstmt, index);

			db.cstmt.setBoolean(++index, PropertyProvider.getBoolean("prm.schedule.autocalculate.onbydefault.flag", true));
			db.cstmt.setString(++index, this.name + " " + PropertyProvider.get("prm.schedule.default.planname", name));
			db.cstmt.setBoolean(++index, PropertyProvider.getBoolean("prm.schedule.default.alwaysshare", false));
			db.cstmt.registerOutParameter((projectIDIndex = ++index), java.sql.Types.VARCHAR);
			db.cstmt.registerOutParameter((planIDIndex = ++index), java.sql.Types.VARCHAR);
			db.cstmt.registerOutParameter((spaceAdminRoleIDIndex = ++index), java.sql.Types.VARCHAR);

			// Call the project create stored proceedure and create the new
			// workspace
			db.executeCallable();
			// set the spaceID returned for new ProjectSpace
			setID(db.cstmt.getString(projectIDIndex));
			// for the purpose of API-based project creation, we need the id
			// of
			// the workplan that is created
			// by the stored proceedure.
			setWorkplanID(db.cstmt.getString(planIDIndex));

			setSpaceAdminRoleID(db.cstmt.getString(spaceAdminRoleIDIndex));

		} catch (SQLException sqle) {
			Logger.getLogger(ProjectSpace.class).error("ProjectSpace.createProjectSpace() threw an SQL exception: " + sqle);
			throw new PersistenceException("ProjectSpace.createProjectSpace() threw an SQL exception: " + sqle, sqle);
		}
	}

	/**
	 * Helper to update the project and owning workspace relationships of this
	 * space at create/update The method is called by create() and update() but
	 * does not attempt to clear existing relationships
	 */
	private void updateWorkspaceRelationships() {

		// If a parent (owning) project is available, establish this workspace
		// as a child of the parent
		if ((this.parentProjectID != null) && !this.parentProjectID.equals("")) {
			ProjectSpace parentSpace = new ProjectSpace();
			parentSpace.setID(this.getParentProjectID());
			SpaceManager.addSuperProjectRelationship(parentSpace, this);
		}
		// Business Owner space relationship
		// Associate this project workspace with a business owner if available
		// If there is no business owner, ownership must be set to the creator's
		// personal space
		if ((this.parentBusinessID != null) && !this.parentBusinessID.equals("")) {
			BusinessSpace ownerSpace = new BusinessSpace();
			ownerSpace.setID(this.getParentBusinessID());
			SpaceManager.addOwnerRelationship(ownerSpace, this);
		} else {
			PersonalSpace ownerSpace = new PersonalSpace();
			ownerSpace.setID(SessionManager.getUser().getID());
			SpaceManager.addOwnerRelationship(ownerSpace, this);
		}
	}

	/**
	 * Creates a new project space.
	 * 
	 * @throws PersistenceException
	 *             if there is a problem creating the project space
	 */
	public void create() throws PersistenceException {

		DBBean db = new DBBean();

		try {
			create(db);
			db.commit();
		} catch (SQLException sql) {
			throw new PersistenceException("ProjectSpace.create(): threw and SQLE: " + sql, sql);
		} finally {
			db.release();
		}
	}

	/**
	 * Creates a new project space. Does not explicity commit store transaction.
	 * 
	 * @param db
	 *            DBBean for database access
	 * @throws PersistenceException
	 *             if there is a problem creating the project space
	 */
	public void create(DBBean db) throws PersistenceException {

		// first create the project space. If the workspace is created
		// successfully the new ID will be set
		createProjectSpace(db);

		// if the flag has been set to allow the update of default workspace
		// relationships
		// establish relationships between the newly created space and other
		// workspaces
		if (this.updateWorkspaceRelationshipsOnCreate) {
			updateWorkspaceRelationships();

			if ((this.parentBusinessID != null) && !this.parentBusinessID.equals("")) {
				BusinessSpace ownerSpace = new BusinessSpace();
				ownerSpace.setID(this.getParentBusinessID());
				try {
					FormManager.addSharedForms(db, ownerSpace, this, true);
				} catch (SQLException sqle) {
					// TODO Auto-generated catch block
					throw new PersistenceException("ProjectSpace.create(): threw an SQLE: " + sqle, sqle);
				}

				// Inheriting all business owned roles to project when project
				// visibility is OWNING_BUSINESS_PARTICIPANTS
				if (getVisibility().equals(ProjectVisibility.OWNING_BUSINESS_PARTICIPANTS)) {
					inheritOwningBusinessRoles();
				}
			}
		}

		// Finally, if the flag has been set to allow the copy of forms on
		// create
		// copy any system default forms. Any active forms defined in the
		// application space forms
		// module will be copied.
		if (this.copyFormsOnCreate) {
			try {
				copyDefaultForms();
			} catch (PersistenceException pe) {
				// We can ignore the error if the forms aren't copied
				Logger.getLogger(ProjectSpace.class).warn("ProjectSpace.create() threw persistence exception while copying default forms: " + pe);
			}
		}

		getMetaData().store(spaceID);

		// If the flag has been set to allow the reload of the workspace on
		// create
		// load the space based on the internal ID
		if (this.reloadSpaceOnCreate) {
			load();
		}

		// publishing event to asynchronous queue
		try {
			net.project.events.ProjectEvent projectEvent = (net.project.events.ProjectEvent) EventFactory.getEvent(ObjectType.PROJECT, EventType.NEW);
			projectEvent.setObjectID(spaceID);
			projectEvent.setSpaceID(spaceID);
			projectEvent.setObjectType(ObjectType.PROJECT);
			projectEvent.setName(this.name);
			projectEvent.setObjectRecordStatus("A");
			projectEvent.publish();
		} catch (EventException e) {
			Logger.getLogger(ProjectSpace.class).error("Project.add() :: Project create Event Publishing Failed!", e);
		}
	}

	/**
	 * Updates an existing ProjectSpace.
	 * 
	 * @throws PersistenceException
	 *             if there is a problem updating the project space
	 */
	private void update() throws PersistenceException {

		DBBean db = new DBBean();

		try {
			update(db);
			db.commit();
		} catch (SQLException sqle) {
			throw new PersistenceException("ProjectSpace.update(): threw an SQLE: " + sqle, sqle);
		} finally {
			db.release();
		}
	}

	/**
	 * Updates an existing ProjectSpace. Does not explicity commit store
	 * transaction.
	 * 
	 * @param db
	 *            DBBean for database access
	 * @throws PersistenceException
	 *             if there is a problem updating the project space
	 */
	private void update(DBBean db) throws PersistenceException {

		Timestamp startDateTS = null;
		Timestamp endDateTS = null;

		if (this.startDate != null) {
			startDateTS = new Timestamp(this.startDate.getTime());
		}

		if (this.endDate != null) {
			endDateTS = new Timestamp(this.endDate.getTime());
		}

		try {
			int index = 0;
			db.prepareCall("{call PROJECT.UPDATE_PROPERTIES (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?)}");
			db.cstmt.setString(++index, getID());
			db.cstmt.setString(++index, SessionManager.getUser().getID());
			db.cstmt.setString(++index, this.parentBusinessID);
			// Old "visibility" setting was poorly defined. It seems to be
			// either "Personal Space" or nothing else. It has been
			// deprecated.
			db.cstmt.setNull(++index, java.sql.Types.NUMERIC);
			db.cstmt.setString(++index, this.name);
			db.cstmt.setString(++index, this.description);
			db.cstmt.setString(++index, this.statusID);
			if (getColorCode() == null) {
				db.cstmt.setNull(++index, java.sql.Types.VARCHAR);
			} else {
				db.cstmt.setString(++index, getColorCode().getID());
			}
			// in the event calculation method = schedule, the current (as
			// of
			// last load) schedule completion
			// % will be saved as the percentComplete
			db.cstmt.setString(++index, getPercentCalculationMethod().getID());

			// This convertions between Strings and floats is to not send a
			// value with decimals to the database
			double percentCompleteValueWithDecimals = Double.parseDouble(this.percentComplete);
			int percentCompleteValue = (int) percentCompleteValueWithDecimals;
			db.cstmt.setString(++index, String.valueOf(percentCompleteValue));
			//
			db.cstmt.setTimestamp(++index, startDateTS);
			db.cstmt.setTimestamp(++index, endDateTS);
			db.cstmt.setString(++index, this.projectLogoID);

			// Add common parameters
			// A number of other parameters are common to update and create
			// but they are in different orders
			index = bindCommonParameters(db.cstmt, index);

			db.cstmt.setString(++index, this.serial);

			db.executeCallable();

			// Update the relationships between this workspace any other
			// owning
			// projects and businesses			

			// First remove any existing project, personal or business
			// workspace
			// owners
			SpaceManager.removeSuperProjectRelationships(this);
			SpaceManager.removeOwnerRelationships(this, ISpaceTypes.BUSINESS_SPACE);
			SpaceManager.removeOwnerRelationships(this, ISpaceTypes.PERSONAL_SPACE);

			// Finally, update the workspace relationships.
			updateWorkspaceRelationships();

			if ((this.parentBusinessID != null) && !this.parentBusinessID.equals("")) {
				if (isParentChanged()) {
					BusinessSpace ownerSpace = new BusinessSpace();
					ownerSpace.setID(this.getParentBusinessID());
					try {
						FormManager.updateSharedForms(db, ownerSpace, this);
					} catch (SQLException sqle) {
						// TODO Auto-generated catch block
						throw new PersistenceException("ProjectSpace.update(): threw an SQLE: " + sqle, sqle);
					}
				}

				// Inheriting all business owned roles to project when project
				// visibility is OWNING_BUSINESS_PARTICIPANTS
				if (getVisibility().equals(ProjectVisibility.OWNING_BUSINESS_PARTICIPANTS)) {
					inheritOwningBusinessRoles();
				}
			} else {
				if (isParentChanged()) {
					FormManager.updateSharedForms(db, null, this);
				}
			}

			getMetaData().store(spaceID);
			
			// Create project edit notification
			net.project.project.ProjectEvent event = new net.project.project.ProjectEvent();
			event.setSpaceID(spaceID);
			event.setTargetObjectID(spaceID);
			event.setTargetObjectType(EventCodes.MODIFY_PROJECT);
			event.setTargetObjectXML(this.getMailXMLBody());
			event.setEventType(EventCodes.MODIFY_PROJECT);
			event.setUser(SessionManager.getUser());
			event.setDescription(PropertyProvider.get("prm.notification.type.projectedited.description") + ": \"" + this.getName() + "\"");
			event.store();

			// publishing event to asynchronous queue
			try {
				net.project.events.ProjectEvent projectEvent = (net.project.events.ProjectEvent) EventFactory.getEvent(ObjectType.PROJECT, EventType.EDITED);
				projectEvent.setObjectID(spaceID);
				projectEvent.setSpaceID(spaceID);
				projectEvent.setObjectType(ObjectType.PROJECT);
				projectEvent.setName(this.name);
				projectEvent.setObjectRecordStatus("A");
				projectEvent.publish();
			} catch (EventException e) {
				Logger.getLogger(ProjectSpace.class).error("Project.update() :: Project update Event Publishing Failed!", e);
			}

		} catch (PersistenceException pnetEx) {
			Logger.getLogger(ProjectSpace.class).error("Error occurred while creating notification ProjectSpace.update() : " + pnetEx);
		} catch (SQLException sqle) {
			Logger.getLogger(ProjectSpace.class).error("ProjectSpace.update() threw an SQL exception: " + sqle);
			throw new PersistenceException("Unable to update project space.", sqle);
		}
	}

	/**
	 * Binds common parameters for update / create.
	 * 
	 * @param stmt
	 *            the PreparedStatement to which to bind paramters
	 * @param index
	 *            the current index position which will be incremented to find
	 *            the position for the next parameter to be bound
	 * @return the updated index position set to the last position to which a
	 *         parameter was bound
	 * @throws SQLException
	 */
	private int bindCommonParameters(java.sql.PreparedStatement stmt, int index) throws SQLException {
		stmt.setString(++index, getDefaultCurrency().getCurrencyCode());
		stmt.setString(++index, getSponsor());
		if (getImprovementCode() == null) {
			stmt.setNull(++index, java.sql.Types.VARCHAR);
		} else {
			stmt.setString(++index, getImprovementCode().getID());
		}
		stmt.setString(++index, getCurrentStatusDescription());
		if (getFinancialStatusColorCode() == null) {
			stmt.setNull(++index, java.sql.Types.VARCHAR);
		} else {
			stmt.setString(++index, getFinancialStatusColorCode().getID());
		}
		if (getFinancialStatusImprovementCode() == null) {
			// Default is NO_CHANGE
			stmt.setString(++index, ImprovementCode.NO_CHANGE.getID());
		} else {
			stmt.setString(++index, getFinancialStatusImprovementCode().getID());
		}
		if (budgetedTotalCost == null) {
			stmt.setNull(++index, java.sql.Types.NUMERIC);
			stmt.setNull(++index, java.sql.Types.VARCHAR);
		} else {
			stmt.setDouble(++index, budgetedTotalCost.getValue().doubleValue());
			stmt.setString(++index, getBudgetedTotalCost().getCurrency().getCurrencyCode());
		}
		if (currentEstimatedTotalCost == null) {
			stmt.setNull(++index, java.sql.Types.NUMERIC);
			stmt.setNull(++index, java.sql.Types.VARCHAR);
		} else {
			stmt.setDouble(++index, currentEstimatedTotalCost.getValue().doubleValue());
			stmt.setString(++index, getCurrentEstimatedTotalCost().getCurrency().getCurrencyCode());
		}
		if (actualCostToDate == null) {
			stmt.setNull(++index, java.sql.Types.NUMERIC);
			stmt.setNull(++index, java.sql.Types.VARCHAR);
		} else {
			stmt.setDouble(++index, actualCostToDate.getValue().doubleValue());
			stmt.setString(++index, getActualCostToDate().getCurrency().getCurrencyCode());
		}
		if (estimatedROI == null) {
			stmt.setNull(++index, java.sql.Types.NUMERIC);
			stmt.setNull(++index, java.sql.Types.VARCHAR);
		} else {
			stmt.setDouble(++index, estimatedROI.getValue().doubleValue());
			stmt.setString(++index, getEstimatedROI().getCurrency().getCurrencyCode());
		}
		stmt.setString(++index, getCostCenter());
		if (getScheduleStatusColorCode() == null) {
			stmt.setNull(++index, java.sql.Types.VARCHAR);
		} else {
			stmt.setString(++index, getScheduleStatusColorCode().getID());
		}
		if (getScheduleStatusImprovementCode() == null) {
			// Default is NO_CHANGE
			stmt.setString(++index, ImprovementCode.NO_CHANGE.getID());
		} else {
			stmt.setString(++index, getScheduleStatusImprovementCode().getID());
		}
		if (getResourceStatusColorCode() == null) {
			stmt.setNull(++index, java.sql.Types.VARCHAR);
		} else {
			stmt.setString(++index, getResourceStatusColorCode().getID());
		}
		if (getResourceStatusImprovementCode() == null) {
			// Default is NO_CHANGE
			stmt.setString(++index, ImprovementCode.NO_CHANGE.getID());
		} else {
			stmt.setString(++index, getResourceStatusImprovementCode().getID());
		}
		if (getPriorityCode() == null) {
			stmt.setNull(++index, java.sql.Types.VARCHAR);
		} else {
			stmt.setString(++index, getPriorityCode().getID());
		}
		if (getRiskRatingCode() == null) {
			stmt.setNull(++index, java.sql.Types.VARCHAR);
		} else {
			stmt.setString(++index, getRiskRatingCode().getID());
		}
		if (getVisibility() == null) {
			stmt.setNull(++index, java.sql.Types.VARCHAR);
		} else {
			stmt.setString(++index, getVisibility().getID());
		}

		return index;
	}

	/**
	 * Removes this project from the database and disassociates it from any
	 * child projects. This does NOT remove any participants from the project.
	 * 
	 * @throws PersistenceException
	 *             if there is a problem removing the project space
	 */
	public void remove() throws PersistenceException {
		DBBean db = new DBBean();
		try {
			int index = 0;
			db.prepareCall("{call PROJECT.REMOVE (?,?)}");
			db.cstmt.setString(++index, getID());
			db.cstmt.setString(++index, SessionManager.getUser().getID());
			db.executeCallable();

			// Remove parent relationship
			SpaceManager.removeParentRelationships(this, SpaceRelationship.SUBSPACE, "project");

			// Now remove all child sub-projects relationships
			// This ensures that child projects no longer have a parent
			SpaceManager.removeChildRelationships(this, SpaceRelationship.SUBSPACE, "project");
			SpaceManager.removeSharedRelationships(getID());

			getMetaData().delete();

			// Create project delete notification
			net.project.project.ProjectEvent event = new net.project.project.ProjectEvent();
			event.setSpaceID(spaceID);
			event.setTargetObjectID(spaceID);
			event.setTargetObjectType(EventCodes.REMOVE_PROJECT);
			event.setTargetObjectXML(this.getMailXMLBody());
			event.setEventType(EventCodes.REMOVE_PROJECT);
			event.setUser(SessionManager.getUser());
			event.setDescription(PropertyProvider.get("prm.notification.type.projectdeleted.description") + ": \"" + this.getName() + "\"");

			event.store();

		} catch (SQLException sqle) {
			Logger.getLogger(ProjectSpace.class).error("ProjectWizard.remove() threw an SQL exception: " + sqle);
			throw new PersistenceException("Project remove operation failed: " + sqle, sqle);

		} catch (PersistenceException pnetEx) {
			Logger.getLogger(ProjectSpace.class).error("Error occurred while creating notification ProjectSpace.remove() : ", pnetEx);
		} finally {
			db.release();

		}

	}

	/**
	 * Activates the project in database.
	 * 
	 * @throws PersistenceException
	 *             if there is a problem activating the project
	 */
	public void activate() throws PersistenceException {
		DBBean db = new DBBean();
		try {
			int index = 0;
			db.prepareCall("{call PROJECT.ACTIVATE (?,?)}");
			db.cstmt.setString(++index, getID());
			db.cstmt.setString(++index, SessionManager.getUser().getID());
			db.executeCallable();

		} catch (SQLException sqle) {
			Logger.getLogger(ProjectSpace.class).error("ProjectWizard.activate() threw an SQL exception: " + sqle);
			throw new PersistenceException("Activate Project operation failed: " + sqle, sqle);

		} finally {
			db.release();

		}

	}

	//
	// Implementing IXMLPersistence
	//

	/**
	 * Get an XML representation of the Project without xml tag.
	 * 
	 * @return an XML representation of this object without the xml tag.
	 */
	public String getXMLBody() {
		return getXMLDocument().getXMLBodyString();
	}

	/**
	 * Get an XML representation of the ProjectSpace.
	 * 
	 * @return an XML representation of this object.
	 */
	public String getXML() {
		return getXMLDocument().getXMLString();
	}

	/**
	 * Returns this ProjectSpace as an XMLDocument.
	 * 
	 * @return the xml document
	 */
	protected XMLDocument getXMLDocument() {

		net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();

		try {
			doc.startElement("ProjectSpace");
			addAttributes(doc);
			doc.endElement();

		} catch (XMLDocumentException e) {
			// Do nothing

		}

		return doc;
	}

	/**
	 * Adds all XML attributes to the specified document.
	 * 
	 * @param doc
	 *            the document to which to add attributes; it is assumed that
	 *            the document contains an open element.
	 * @throws XMLDocumentException
	 *             if there is a problem adding attributes
	 */
	protected void addAttributes(XMLDocument doc) throws XMLDocumentException {
		doc.addElement("project_id", getID());
		doc.addElement("name", getName());
		doc.addElement("description", getDescription());
		doc.addElement("status_code", getStatus());
		doc.addElement("color_code", (getColorCode() != null ? getColorCode().getName() : ""));
		doc.addElement("color_code_image_url", (getColorCode() != null ? getColorCode().getImageURL() : ""));
		doc.addElement("StartDate", getStartDate());
		doc.addElement("EndDate", getEndDate());
		doc.addElement("is_subproject", new Boolean(isSubproject()));
		doc.addElement("percent_complete", getPercentComplete());
		doc.addElement("DefaultCurrencyCode", getDefaultCurrency().getCurrencyCode());
		doc.addElement("ParentBusinessID", getParentBusinessID());
		doc.addElement("ParentBusinessName", getParentBusinessName());
		doc.addElement("SubprojectOf", isSubproject() ? getParentProjectName() : "");

		doc.addElement("Sponsor", getSponsor());
		doc.startElement("OverallStatus");
		addStatusXML(doc, getImprovementCode(), getColorCode());
		doc.endElement();
		doc.startElement("FinancialStatus");
		addStatusXML(doc, getFinancialStatusImprovementCode(), getFinancialStatusColorCode());
		doc.endElement();
		doc.startElement("ScheduleStatus");
		addStatusXML(doc, getScheduleStatusImprovementCode(), getScheduleStatusColorCode());
		doc.endElement();
		doc.startElement("ResourceStatus");
		addStatusXML(doc, getResourceStatusImprovementCode(), getResourceStatusColorCode());
		doc.endElement();

		doc.startElement("BudgetedTotalCost");
		if (getBudgetedTotalCost() != null) {
			doc.addElement(getBudgetedTotalCost().getXMLDocument());
		}
		doc.endElement();
		doc.startElement("CurrentEstimatedTotalCost");
		if (getCurrentEstimatedTotalCost() != null) {
			doc.addElement(getCurrentEstimatedTotalCost().getXMLDocument());
		}
		doc.endElement();
		doc.startElement("ActualCostToDate");
		if (getActualCostToDate() != null) {
			doc.addElement(getActualCostToDate().getXMLDocument());
		}
		doc.endElement();
		doc.startElement("EstimatedROI");
		if (getEstimatedROI() != null) {
			doc.addElement(getEstimatedROI().getXMLDocument());
		}
		doc.endElement();

		doc.addElement("CurrentStatusDescription", getCurrentStatusDescription());
		doc.addElement("CostCenter", getCostCenter());

		doc.startElement("PriorityCode");
		if (getPriorityCode() != null) {
			doc.addElement(getPriorityCode().getXMLDocument());
		}
		doc.endElement();
		doc.startElement("RiskRatingCode");
		if (getRiskRatingCode() != null) {
			doc.addElement(getRiskRatingCode().getXMLDocument());
		}
		doc.endElement();

		doc.startElement("Meta");
		try {
			doc.addElement("ExternalProjectID", getMetaData().getProperty("ExternalProjectID"));
			doc.addElement("ProjectManager", getMetaData().getProperty("ProjectManager"));
			doc.addElement("ProgramManager", getMetaData().getProperty("ProgramManager"));
			doc.addElement("Initiative", getMetaData().getProperty("Initiative"));
			doc.addElement("ProjectCharter", getMetaData().getProperty("ProjectCharter"));
			doc.addElement("FunctionalArea", getMetaData().getProperty("FunctionalArea"));
			doc.addElement("TypeOfExpense", getMetaData().getProperty("TypeOfExpense"));
		} catch (NoSuchPropertyException e) {
			e.printStackTrace();
		}
		doc.endElement();
	}

	/**
	 * Adds Status XML for the specified improvement and color. If either value
	 * is null no modifications are made to the XML document.
	 * 
	 * @param doc
	 *            the document to add XML to
	 * @param improvementCode
	 *            the improvement code
	 * @param colorCode
	 *            the color code
	 * @throws XMLDocumentException
	 *             if there is a problem adding XML properties
	 */
	private void addStatusXML(XMLDocument doc, ImprovementCode improvementCode, ColorCode colorCode) throws XMLDocumentException {

		if (improvementCode != null && colorCode != null) {
			doc.addElement(improvementCode.getXMLDocument());
			doc.addElement(colorCode.getXMLDocument());
			doc.startElement("ImageURL");
			doc.addValue(improvementCode.getImageURL(colorCode));
			doc.endElement();

		}

	}

	/**
	 * Get the HTTP URL to the project space.
	 * 
	 * @deprecated as of 7.4; no replacement. This value is currently hard-coded
	 *             to <code>/project/Dashboard</code>; the URL should only be
	 *             constructed at the view level.
	 */
	public String getURL() {
		return SessionManager.getAppURL() + "/project/Dashboard?id=" + getID();
	}

	/**
	 * Copy all the "default" forms from the current configuration to this
	 * ProjectSpace.
	 */
	public void copyDefaultForms() throws PersistenceException {
		// copy default forms just after project is created
		// after creation of project ID should not be null, otherwise it is
		// exception condition
		if (getID() == null)
			throw new PersistenceException("ProjectSpace.copyDefaultForms failed because project is not loaded");

		DBBean db = new DBBean();
		try {
			db.prepareCall("begin FORMS.COPY_ALL (?,?,?,?); end;");

			db.cstmt.setString(1, this.user.getCurrentConfigurationID());
			db.cstmt.setString(2, getID());
			db.cstmt.setString(3, this.user.getID());
			db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);

			db.executeCallable();

			// Activate the copied forms
			net.project.form.FormMenu formMenu = new net.project.form.FormMenu();
			formMenu.setSpaceID(getID());
			formMenu.setUser(getUser());
			formMenu.setDisplayPending(true);
			formMenu.setDisplaySystemForms(true);
			formMenu.load();
			formMenu.activateAll();
		} catch (SQLException sqle) {
			throw new PersistenceException("ProjectSpace.copyDefaultForms() threw an SQLException: " + sqle, sqle);
		} catch (net.project.base.PnetException pe) {
			throw new PersistenceException("Project.copyDefaultForms() threw a forms exception: " + pe, pe);
		} finally {
			db.release();
		}

	}

	/**
	 * Returns the encoded version of the serial number for this ProjectSpace.
	 * 
	 * @return the encoded serial number
	 * @exception PersistenceException
	 * @deprecated As of 7.4; no replacement This property is never set.
	 */
	public String getEncodedSerial() throws PersistenceException {

		String EncodedSerial = null;

		DBBean db = new DBBean();
		try {
			db.prepareCall("{?=call PROJECT.ENCODE_SERIAL_NUMBER(?)}");

			db.cstmt.registerOutParameter(1, java.sql.Types.VARCHAR);
			db.cstmt.setString(2, getSerial());
			db.executeCallable();
			EncodedSerial = db.cstmt.getString(1);
		} catch (SQLException sqle) {
			Logger.getLogger(ProjectSpace.class).debug("ProjectSpace:getEncodedSerial() " + getSerial() + ", unable to execute stored procedure: " + sqle);
			throw new PersistenceException("Serial Number Encoded Operation Failed!", sqle);
		} finally {
			db.release();
		}
		return EncodedSerial;
	}

	/**
	 * Get the BusinessSpace that owns this ProjectSpace. The BusinessSpace is
	 * loaded from persistence before being returned.
	 * 
	 * @return the BusinessSpace that owns this ProjectSpace.
	 */
	public BusinessSpace getBusinessSpace() {
		BusinessSpace business = null;
		String query = "select parent_space_id as business_id from pn_space_has_space where parent_space_id= " + getID()
				+ " and relationship_parent_to_child = 'owns'";

		DBBean db = new DBBean();
		try {
			db.executeQuery(query);

			if (db.result.next()) {
				business = new BusinessSpace();
				business.setID(db.result.getString("parent_space_id"));
				business.load();
			} // end if
		} // end try
		catch (PersistenceException pe) {
			business = null;
		} catch (SQLException sqle) {
			Logger.getLogger(ProjectSpace.class).error("ProjectSpace.getBusinessSpace() threw an SQL Exception: " + sqle);
		} // end catch
		finally {
			db.release();
		}
		return business;

	} // end getBusinessSpace()

	/**
	 * Remove the logo associated with the project from the database.
	 */
	public void removeLogo() throws PersistenceException {
		String qstrRemoveLogo = "update pn_project_space set project_logo_id = NULL where project_id = " + getID();

		DBBean db = new DBBean();
		try {
			db.executeQuery(qstrRemoveLogo);
		} catch (SQLException sqle) {
			Logger.getLogger(ProjectSpace.class).error("ProjectSpace.removeLogo () threw an SQL exception: " + sqle);
			throw new PersistenceException("ProjectSpace.removeLogo () threw an SQL exception: " + sqle, sqle);
		} // end catch
		finally {
			db.release();
		} // end finally
	}

	/**
	 * Save the logo image for this project to the database; must call
	 * setProjectLogoID() first.
	 */
	public void addLogoToProject() throws PersistenceException {
		addLogoToProject(this.projectLogoID);
	}

	/**
	 * Add a specified logo image to this project and save to database.
	 */
	public void addLogoToProject(String logoID) throws PersistenceException {
		this.projectLogoID = logoID;

		DBBean db = new DBBean();
		try {
			db.prepareCall("begin PROJECT.ADD_LOGO_TO_PROJECT(?,?); end;");
			db.cstmt.setString(1, getID());
			db.cstmt.setString(2, getProjectLogoID());
			db.executeCallable();
		} // end try
		catch (SQLException sqle) {
			Logger.getLogger(ProjectSpace.class).error("ProjectCreateWizard.addLogoToProject () threw an SQL exception: " + sqle);
		} // end catch
		finally {
			db.release();
		} // end finally

	} // end addLogoToProject

	/**
	 * Inheriting all roles from parent business
	 * 
	 * @throws PersistenceException
	 */
	private void inheritOwningBusinessRoles() throws PersistenceException {

		GroupCollection groups = new GroupCollection();
		// Load the groups for that space
		groups.setSpace(SpaceFactory.constructSpaceFromID(this.parentBusinessID));
		groups.loadOwned();
		groups.removePrincipalGroups();

		// Generating group id collection of business owned groups
		Collection groupIdCollection = new ArrayList();
		Iterator it = groups.iterator();
		while (it.hasNext()) {
			Group group = (Group) it.next();
			groupIdCollection.add(this.parentBusinessID + "-" + group.getID());
		}

		GroupProvider groupProvider = new GroupProvider();
		groupProvider.inheritGroupsFromSpace(this, groupIdCollection, PermissionSelection.DEFAULT);
	}

	/**
	 * Clears out all project properties.
	 */
	public void clear() {

		super.clear();
		this.projectLogoID = null;
		this.colorImgURL = null;
		this.status = null;
		this.statusID = null;
		this.isLoaded = false;
		reset();
	}

	/**
	 * Resets project properties that are filled in during a wizard. To clear
	 * entire object, use {@link #clear()}
	 */
	public void reset() {

		this.parentProjectID = null;
		this.parentBusinessID = null;
		this.methodologyID = null;
		this.name = null;
		this.description = null;
		setColorCode(null);
		this.statusID = null;
		this.status = null;
		this.serial = null;
		this.startDate = null;
		this.endDate = null;
		this.percentComplete = null;
		this.user = null;
		setDefaultCurrencyCode(null);
		setPlanID(null);
		setSponsor(null);
		setImprovementCode(null);
		setCurrentStatusDescription(null);
		setFinancialStatusColorCode(null);
		setFinancialStatusImprovementCode(null);
		setBudgetedTotalCost(null);
		setCurrentEstimatedTotalCost(null);
		setActualCostToDate(null);
		setEstimatedROI(null);
		setCostCenter(null);
		setScheduleStatusColorCode(null);
		setScheduleStatusImprovementCode(null);
		setResourceStatusColorCode(null);
		setResourceStatusImprovementCode(null);
		setPriorityCode(null);
		setRiskRatingCode(null);
		setVisibility(null);
		setUpdateWorkspaceRelationshipsOnCreate(true);
		setCopyFormsOnCreate(true);
		setReloadSpaceOnCreate(true);
		setMetaData(null);
	}

	/**
	 * details of this project object in html format. By usnig a template
	 * project-detail.tml
	 * 
	 * @param servletContext
	 * @return <code>String</code>HTML of detaill for this object.
	 */
	public String getDetails(ServletContext servletContext) {
		return new TemplateFormatter(servletContext, "/details/template/project-detail.tml").transForm(this);
	}

	/**
	 * This method returns the project name and url as XML text for
	 * notification.
	 * 
	 * @return XML representation
	 */
	public String getMailXMLBody() {
		StringBuffer sb = new StringBuffer();
		sb.append("<Project>\n");
		sb.append("<ProjectName>" + XMLUtils.escape(this.getName()) + "</ProjectName>\n");
		sb.append("<ProjectUrl>" + URLFactory.makeURL(spaceID, ObjectType.PROJECT) + "</ProjectUrl>\n");
		sb.append("</Project>");
		return sb.toString();
	}

	/**
	 * @return the templateApplied
	 */
	public String getTemplateApplied() {
		return templateApplied;
	}

	/**
	 * @param templateApplied
	 *            the templateApplied to set
	 */
	public void setTemplateApplied(String templateApplied) {
		this.templateApplied = templateApplied;
	}

	/**
	 * To get Project details html presentation to be displayed in left side
	 * tool bar
	 * 
	 * @return string
	 */
	public String getProjectSpaceDetails() {
		String detailsHtmlString = StringUtils.EMPTY;
		String subProjectName = StringUtils.EMPTY;
		String parentProjectURL = StringUtils.EMPTY;

		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		try {
			if (this.isSubproject()) {
				subProjectName = this.getParentProjectName();
				parentProjectURL = SessionManager.getJSPRootURL() + "/project/Dashboard?id=" + this.getParentProjectID();
			}
			detailsHtmlString += "<br><div id=\"leftSpaceName\" style=\"margin-top: 15px;\" class=\"project-title\">" + HTMLUtils.escape(this.getName())
					+ "</div><br clear=\"both\"/> ";

			if (StringUtils.isNotEmpty(this.getMetaData().getProperty("ProjectManager"))) {
				detailsHtmlString += "<div class=\"project-description\"><strong>"
						+ PropertyProvider.get("prm.project.portfolio.finder.column.meta.projectmanager") + "</strong><br /> "
						+ HTMLUtils.escape(this.getMetaData().getProperty("ProjectManager")) + "</div>";
			}
			if (StringUtils.isNotEmpty(this.getStatus())) {
				detailsHtmlString += "<div class=\"project-description\"><strong>" + PropertyProvider.get("prm.project.dashboard.status.label") + "</strong>"
						+ this.getStatus() + "</div>";
			}
			if (StringUtils.isNotEmpty(this.getPercentComplete())) {
				detailsHtmlString += "<div class=\"project-description\"><strong>" + PropertyProvider.get("prm.project.dashboard.completion.label")
						+ "</strong>" + nf.format(Double.parseDouble(this.getPercentComplete())) + "%</div>";
			}
			if (StringUtils.isNotEmpty(this.getDescription())) {
				String projectDescription = this.getDescription().length() > 100 ? this.getDescription().substring(0, 100) + ".." : this.getDescription();
				detailsHtmlString += "<div class=\"project-description\"><strong>" + PropertyProvider.get("prm.project.dashboard.description.label")
						+ "<br /></strong>" + HTMLUtils.escape(projectDescription) + "</div>";
			}
			if (StringUtils.isNotEmpty(parentProjectURL) && StringUtils.isNotEmpty(subProjectName)) {
				detailsHtmlString += "<div class=\"project-description\"><strong> " + PropertyProvider.get("prm.project.dashboard.subprojectof.label")
						+ "</strong><br/><a class=\"project\" href=\"" + parentProjectURL + "\">" + HTMLUtils.escape(subProjectName) + "</a></div>";
			}
		} catch (Exception e) {
			Logger.getLogger(ProjectSpace.class).error("Error occured while getting project space details :" + e.getMessage());
		}
		return detailsHtmlString;
	}

	/**
	 * Compare the ProjectSpace object by project name
	 * 
	 * @param obj
	 *            the ProjectSpace object
	 * @return int
	 */
	public int compareTo(Object obj) {
		return this.name.compareToIgnoreCase(((ProjectSpace) obj).name);
	}

	/**
	 * To get the parent project name of this project
	 * 
	 * @return
	 */
	public String getSuperProjectName() {
		return superProjectName;
	}

	/**
	 * To set the parent project name of this project
	 * 
	 * @param superProjectName
	 */
	public void setSuperProjectName(String superProjectName) {
		this.superProjectName = superProjectName;
	}
}
