package net.project.directory;

import java.io.Serializable;
import java.util.ArrayList;

import net.project.business.BusinessDirectorySearchResults;
import net.project.resource.Invitee;

import org.apache.commons.collections.CollectionUtils;

public class ProjectListWrapper implements Serializable {
	
	private String projectName;
	
	private String projectId;
	
	private boolean isParticipant;
	
	private String roleOptionList;
	
	private String participantList;
	
	private int participantListCount;
	
	private boolean isSpaceNameBusiness;
	
	private ArrayList<Invitee> inviteesList;
	
	private ArrayList<GroupWrapper> userRoles;
	
	private Integer cntforprojectAndSubbusinesses;
	
	private boolean isTeamMember;
	
	private String manageTabWidth;
	
	private ArrayList<BusinessDirectorySearchResults.BusinessDirectorySearchResult> projectMemberLists;
	
	/**
	 * @return the shortProjectName
	 */
	public String getShortProjectName() {
		if (projectName.length() > 30) {
			return projectName.substring(0, 30) + "..";
		} else {
			return projectName;
		}
	}
	
	public ProjectListWrapper(){
	}
	
	public ProjectListWrapper(String projectId,String projectName){
		this.projectId = projectId;
		this.projectName = projectName;
	}

	/**
	 * @return the isParticipant
	 */
	public boolean getParticipant() {
		return isParticipant;
	}

	/**
	 * @param isParticipant the isParticipant to set
	 */
	public void setParticipant(boolean isParticipant) {
		this.isParticipant = isParticipant;
	}

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the roleOptionList
	 */
	public String getRoleOptionList() {
		return roleOptionList;
	}

	/**
	 * @param roleOptionList the roleOptionList to set
	 */
	public void setRoleOptionList(String roleOptionList) {
		this.roleOptionList = roleOptionList;
	}

	/**
	 * @return the participantList
	 */
	public String getParticipantList() {
		return participantList;
	}

	/**
	 * @param participantList the participantList to set
	 */
	public void setParticipantList(String participantList) {
		this.participantList = participantList;
	}

	/**
	 * @return the isSpaceNameBusiness
	 */
	public boolean isSpaceNameBusiness() {
		return isSpaceNameBusiness;
	}

	/**
	 * @param isSpaceNameBusiness the isSpaceNameBusiness to set
	 */
	public void setSpaceNameBusiness(boolean isSpaceNameBusiness) {
		this.isSpaceNameBusiness = isSpaceNameBusiness;
	}
	
	/**
	 * @return the cntforprojectAndSubbusinesses
	 */
	public Integer getCntforprojectAndSubbusinesses() {
		return cntforprojectAndSubbusinesses;
	}

	/**
	 * @param cntforprojectAndSubbusinesses the cntforprojectAndSubbusinesses to set
	 */
	public void setCntforprojectAndSubbusinesses(Integer cntforprojectAndSubbusinesses) {
		this.cntforprojectAndSubbusinesses = cntforprojectAndSubbusinesses;
	}

	/**
	 * @return the inviteesList
	 */
	public ArrayList<Invitee> getInviteesList() {
		return inviteesList;
	}

	/**
	 * @param inviteesList the inviteesList to set
	 */
	public void setInviteesList(Invitee invitee) {
		if(CollectionUtils.isEmpty(inviteesList)) {
			inviteesList = new ArrayList<Invitee>();
		}
		if(inviteesList.indexOf(invitee) == -1){
			inviteesList.add(invitee);
		}
	}

	/**
	 * @return the userRoles
	 */
	public ArrayList<GroupWrapper> getUserRoles() {
		return userRoles;
	}

	/**
	 * @param userRoles the userRoles to set
	 */
	public void setUserRoles(ArrayList<GroupWrapper> userRoles) {
		this.userRoles = userRoles;
	}

	/**
	 * @return the isTeamMember
	 */
	public boolean getTeamMember() {
		return isTeamMember;
	}

	/**
	 * @param isTeamMember the isTeamMember to set
	 */
	public void setTeamMember(boolean isTeamMember) {
		this.isTeamMember = isTeamMember;
	}

	/**
	 * @return the participantListCount
	 */
	public int getParticipantListCount() {
		return participantListCount;
	}

	/**
	 * @param participantListCount the participantListCount to set
	 */
	public void setParticipantListCount(int participantListCount) {
		this.participantListCount = participantListCount;
	}
	
	/**
	 * @return the manageTabWidth
	 */
	public String getManageTabWidth() {
		return manageTabWidth;
	}

	/**
	 * @param manageTabWidth the manageTabWidth to set
	 */
	public void setManageTabWidth(String manageTabWidth) {
		this.manageTabWidth = manageTabWidth;
	}
	
	/**
	 * @return the projectMemberLists
	 */
	public ArrayList<BusinessDirectorySearchResults.BusinessDirectorySearchResult> getProjectMemberLists() {
		return projectMemberLists;
	}

	/**
	 * @param projectMemberLists the projectMemberLists to set
	 */
	public void setProjectMemberLists(ArrayList<BusinessDirectorySearchResults.BusinessDirectorySearchResult> projectMemberLists) {
		this.projectMemberLists = projectMemberLists;
	}
	
}
