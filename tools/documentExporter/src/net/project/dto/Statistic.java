package net.project.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

public class Statistic implements Serializable {
	
	public Statistic() {
	}
    
	private String userIpAddress;
	
	private Date versionCheckTime;
	
	private String currentVersion;
	
	private Integer numberOfUsers;
	
	private Integer averageUsersPerMonth;
	
	private Integer userLoginToday;
	
	private Integer userLoginLastMonth;
	
	private Integer totalLoginsToday;
	
	private String systemDocumentStorage;
	
	private Integer unregisteredUser;
	
	private Integer activeBusinessCount;
	
	private String newUserTrend;
	
	private Integer newUsersCurrentMonth;
	
	private Integer newUsersThreeMonth;
	
	private Integer averageDailyUserLogin;
	
	private Integer activeProjectCount;
	
	private Integer activeUser;

	private Boolean marked;
	
	private String latitude;
	private String longitude;
	private String area;
	private String city;
	private String country;
	private String organization;
	
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Boolean getMarked() {
		return marked;
	}

	public void setMarked(Boolean marked) {
		this.marked = marked;
	}
	
	public String getUserIpAddress() {
		return userIpAddress;
	}

	public void setUserIpAddress(String userIpAddress) {
		this.userIpAddress = userIpAddress;
	}

	public Date getVersionCheckTime() {
		return versionCheckTime;
	}

	public void setVersionCheckTime(Date versionCheckTime) {
		this.versionCheckTime = versionCheckTime;
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public Integer getAverageUsersPerMonth() {
		return averageUsersPerMonth;
	}

	public void setAverageUsersPerMonth(Integer averageUsersPerMonth) {
		this.averageUsersPerMonth = averageUsersPerMonth;
	}

	public Integer getUserLoginToday() {
		return userLoginToday;
	}

	public void setUserLoginToday(Integer userLoginToday) {
		this.userLoginToday = userLoginToday;
	}

	public Integer getUserLoginLastMonth() {
		return userLoginLastMonth;
	}

	public void setUserLoginLastMonth(Integer userLoginLastMonth) {
		this.userLoginLastMonth = userLoginLastMonth;
	}

	public Integer getTotalLoginsToday() {
		return totalLoginsToday;
	}

	public void setTotalLoginsToday(Integer totalLoginsToday) {
		this.totalLoginsToday = totalLoginsToday;
	}

	public String getSystemDocumentStorage() {
		return systemDocumentStorage;
	}

	public void setSystemDocumentStorage(String systemDocumentStorage) {
		this.systemDocumentStorage = systemDocumentStorage;
	}

	public Integer getUnregisteredUser() {
		return unregisteredUser;
	}

	public void setUnregisteredUser(Integer unregisteredUser) {
		this.unregisteredUser = unregisteredUser;
	}

	public Integer getActiveBusinessCount() {
		return activeBusinessCount;
	}

	public void setActiveBusinessCount(Integer activeBusinessCount) {
		this.activeBusinessCount = activeBusinessCount;
	}

	public String getNewUserTrend() {
		return newUserTrend;
	}

	public void setNewUserTrend(String newUserTrend) {
		this.newUserTrend = newUserTrend;
	}

	public Integer getNewUsersCurrentMonth() {
		return newUsersCurrentMonth;
	}

	public void setNewUsersCurrentMonth(Integer newUsersCurrentMonth) {
		this.newUsersCurrentMonth = newUsersCurrentMonth;
	}

	public Integer getNewUsersThreeMonth() {
		return newUsersThreeMonth;
	}

	public void setNewUsersThreeMonth(Integer newUsersThreeMonth) {
		this.newUsersThreeMonth = newUsersThreeMonth;
	}

	public Integer getAverageDailyUserLogin() {
		return averageDailyUserLogin;
	}

	public void setAverageDailyUserLogin(Integer averageDailyUserLogin) {
		this.averageDailyUserLogin = averageDailyUserLogin;
	}

	public Integer getActiveProjectCount() {
		return activeProjectCount;
	}

	public void setActiveProjectCount(Integer activeProjectCount) {
		this.activeProjectCount = activeProjectCount;
	}

	public Integer getActiveUser() {
		return activeUser;
	}

	public void setActiveUser(Integer activeUser) {
		this.activeUser = activeUser;
	}

	
	
}
